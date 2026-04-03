package com.finance.dashboard.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

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
            // Step 4: Extract email from token
            final String email = jwtUtil.extractEmail(token);

            // Step 5: Only process if email exists & not yet authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 6: Load user from DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Step 7: Validate token
                if (jwtUtil.isTokenValid(token, userDetails)) {

                    //Step 8: Create authentication object
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Step 9: Set in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
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