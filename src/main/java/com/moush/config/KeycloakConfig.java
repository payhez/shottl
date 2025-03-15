package com.moush.config;

import lombok.Data;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix="keycloak.admin")
public class KeycloakConfig {

    private String serverUrl;
    private String realm;
    private String adminRealm;
    private String adminUser;
    private String adminPassword;
    private String adminClientId;
    private String userClientId;
    private String serverClientId;

    @Bean
    public Keycloak setUpKeycloakRealm() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .username(adminUser)
                .password(adminPassword)
                .clientId(adminClientId)
                .build();
    }
}
