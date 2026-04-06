package com.finance.dashboard.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtUserVerifier jwtUserVerifier;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Read Authorization header
        final String authHeader = request.getHeader(SecurityConstants.HEADER_STRING);

        // Step 2: If no Bearer token then skip filter
        if (authHeader == null || !authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract token
        final String token = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());

        try {
            // Step 4: Parse & validate token once
            JwtUtil.ParsedToken parsed = jwtUtil.parseAndValidate(token).orElse(null);
            if (parsed == null) {
                filterChain.doFilter(request, response);
                return;
            }

            final String email = parsed.email();
            final String role = parsed.role();

            // Step 5: Only process if email exists & not yet authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 6: Optional DB check (cached) for inactive/deleted users and role changes
                if (!jwtUserVerifier.isUserAllowed(email, role)) {
                    SecurityContextHolder.clearContext();
                    filterChain.doFilter(request, response);
                    return;
                }

                // Step 7: Create authentication from JWT claims (mostly stateless)
                List<SimpleGrantedAuthority> authorities = (role == null || role.isBlank())
                        ? List.of()
                        : List.of(new SimpleGrantedAuthority("ROLE_" + role));

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                org.springframework.security.core.userdetails.User
                                        .withUsername(email)
                                        .password("")
                                        .authorities(authorities)
                                        .accountExpired(false)
                                        .accountLocked(false)
                                        .credentialsExpired(false)
                                        .disabled(false)
                                        .build(),
                                null,
                                authorities
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Step 8: Set in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // If token invalid/expired then do not set authentication
            // GlobalExceptionHandler won't catch filter exceptions so we let Spring Security handle the 401 response
            SecurityContextHolder.clearContext();
        }

        // Step 10: Continue filter chain
        filterChain.doFilter(request, response);
    }
}