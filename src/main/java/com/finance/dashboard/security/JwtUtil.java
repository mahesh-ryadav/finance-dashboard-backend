package com.finance.dashboard.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // method to generate token
    public String generateToken(org.springframework.security.core.userdetails.UserDetails userDetails, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_ROLE,    role);
        claims.put(SecurityConstants.CLAIM_USER_ID, userId);
        return buildToken(claims, userDetails.getUsername());
    }

    // build token internally
    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Optional<ParsedToken> parseAndValidate(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date exp = claims.getExpiration();
            if (exp == null || exp.before(new Date())) {
                return Optional.empty();
            }

            String email = claims.getSubject();
            if (email == null || email.isBlank()) {
                return Optional.empty();
            }

            String role = claims.get(SecurityConstants.CLAIM_ROLE, String.class);
            Long userId = claims.get(SecurityConstants.CLAIM_USER_ID, Long.class);
            return Optional.of(new ParsedToken(email, role, userId));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // ── build signing key from secret ─────────────────────────────────
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public record ParsedToken(String email, String role, Long userId) {}
}