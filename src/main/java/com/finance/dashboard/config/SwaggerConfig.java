package com.finance.dashboard.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name           = "bearerAuth",
        type           = SecuritySchemeType.HTTP,
        scheme         = "bearer",
        bearerFormat   = "JWT",
        description    = "Enter your JWT token here. Example: Bearer eyJhbGci..."
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Dashboard Backend API")
                        .description("""
                                Backend API for Finance Dashboard System.
                                
                                Roles:
                                - VIEWER  → Read financial records only
                                - ANALYST → Read records + access dashboard analytics
                                - ADMIN   → Full access (users + records + dashboard)
                                
                                Authentication:
                                1. Register via POST /api/v1/auth/register
                                2. Login via POST /api/v1/auth/login
                                3. Copy the token from response
                                4. Click Authorize button and paste: Bearer <token>
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Mahesh Yadav")
                                .email("mahi234xp@gmail.com")
                                .url("https://github.com/mahesh-ryadav"))
                        .license(new License()
                                .name("MIT License")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ));
    }
}