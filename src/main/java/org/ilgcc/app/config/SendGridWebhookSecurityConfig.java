package org.ilgcc.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SendGridWebhookSecurityConfig {

    @Bean
    @ConditionalOnBean(name = "securityFilterChain")
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/sendgrid-webhook"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/sendgrid-webhook").permitAll().anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }
}
