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
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable) // TODO Enable csrf
                .authorizeHttpRequests(authorize  ->
                authorize.requestMatchers(HttpMethod.POST, "/signup/manager").permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
}