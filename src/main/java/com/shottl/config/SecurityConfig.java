package com.shottl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String VERSION_ONE_BASE_PREFIX = "/api/v1";

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // TODO Enable csrf
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(authorize  -> {
            authorize.requestMatchers(
                    "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml",
                            VERSION_ONE_BASE_PREFIX + "/manager/signup",
                            VERSION_ONE_BASE_PREFIX + "/passenger/signup"
                    ).permitAll();
            authorize.requestMatchers(HttpMethod.POST,VERSION_ONE_BASE_PREFIX + "/organisation/add")
                    .hasRole("MANAGER");
            authorize.anyRequest().denyAll();

        });
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter()))
        );

        // TODO set up login and logout templates
        /*http.oauth2Login(oauth2 -> oauth2
                .loginPage("/oauth2/authorization/keycloak") // Sets custom login page for OAuth2 with Keycloak
                .defaultSuccessUrl("/menu", true) // Redirects to "/menu" after successful login
        ).logout(logout -> logout
                .logoutSuccessUrl("/") // Redirects to the root URL on successful logout
                .invalidateHttpSession(true) // Invalidates session to clear session data
                .clearAuthentication(true) // Clears authentication details
                .deleteCookies("JSESSIONID") // Deletes the session cookie
        );*/
        return http.build();
    }

    private JwtAuthenticationConverter keycloakJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Set<GrantedAuthority> authorities = new HashSet<>();

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
            }

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null) {
                Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get(keycloakConfig.getUserClientId());
                if (clientRoles != null && clientRoles.containsKey("roles")) {
                    List<String> roles = (List<String>) clientRoles.get("roles");
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                }
            }
            return authorities;
        });
        return converter;
    }
}