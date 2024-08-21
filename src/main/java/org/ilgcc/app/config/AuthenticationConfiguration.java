package org.ilgcc.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * This configuration class will allow anonymous auth, since IL CCAP doesn't have logins and
 * it will remove Spring Boot Security's error about using the default password
 */
@Configuration
public class AuthenticationConfiguration {
    @Bean
    public AuthenticationManager noopAuthenticationManager() {
        return authentication -> {
            throw new AuthenticationServiceException("Authentication is disabled");
        };
    }
}
