package org.ilgcc.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ILGCCSecurityConfig {

    /**
     * A bean that overrides and replaces FFL's SecurityConfigurationBase's securityFilterChain so the Sendgrid webhook is not
     * blocked by CSRF validation
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    @Primary
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/sendgrid-webhook"))
                .formLogin(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }
}
