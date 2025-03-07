package com.shuttler;

import com.shuttler.config.KeycloakConfig;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterEach;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public abstract class BaseTest {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected KeycloakConfig keycloakConfig;

    @Autowired
    protected KeycloakContainer keycloakContainer;

    protected Keycloak keycloak;

    @PostConstruct
    private void initKeycloak() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakContainer.getAuthServerUrl())
                    .realm(keycloakConfig.getAdminRealm())
                    .clientId(keycloakConfig.getClientId())
                    .username(keycloakConfig.getAdminUser())
                    .password(keycloakConfig.getAdminPassword())
                    .build();
        }
    }


    @AfterEach
    public void clearDatabase() {
        mongoTemplate.getDb().drop();
    }

    @AfterEach
    public void clearKeycloakUsers() {
        keycloak.realm(keycloakConfig.getRealm()).users().list()
                .forEach(user -> keycloak.realm(keycloakConfig.getRealm())
                        .users().get(user.getId()).remove());
    }
}
