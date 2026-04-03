package com.finance.dashboard.security;

public class SecurityConstants {
    private SecurityConstants() {}

    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    public static final String TOKEN_PREFIX   = "Bearer ";
    public static final String HEADER_STRING  = "Authorization";
    public static final String CLAIM_ROLE     = "role";
    public static final String CLAIM_USER_ID  = "userId";
}
