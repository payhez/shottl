package com.shuttler.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String VERSION_ONE_BASE_PREFIX = "/api/v1";

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // TODO Enable csrf
                .authorizeHttpRequests(authorize  ->
                authorize.requestMatchers(HttpMethod.POST,
                                VERSION_ONE_BASE_PREFIX + "/manager/signup",
                                VERSION_ONE_BASE_PREFIX + "/passenger/signup",
                                VERSION_ONE_BASE_PREFIX + "/organization/add")
                        .permitAll()
                        .anyRequest()
                        .authenticated());
        return http.build();
    }
}