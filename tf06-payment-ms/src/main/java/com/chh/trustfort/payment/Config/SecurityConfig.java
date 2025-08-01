package com.chh.trustfort.payment.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("🔐 SecurityConfig applied...");
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/paymentService/webhook/flutterwave",
                        "/trustfort/api/v1/paymentService/flutterwave-redirect",
                        "/trustfort/api/v1/paymentService/wallet/internal/check-balance",
                        "/v3/api-docs/**",           // Swagger/OpenAPI JSON
                        "/swagger-ui/**",           // Swagger UI assets
                        "/swagger-ui.html",         // Swagger UI HTML
                        "/swagger-resources/**",    // Swagger internal
                        "/webjars/**"
                ).permitAll()
                .anyRequest().permitAll(); // 🚨 Allows all requests without authentication
    }

}

