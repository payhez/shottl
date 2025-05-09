package com.shottl.service;

import com.shottl.config.KeycloakConfig;
import com.shottl.exception.KeycloakException;
import com.shottl.model.User;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Collections;

@Service
@Slf4j
public class KeycloakService {

    private final Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    public KeycloakService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public Mono<String> createUserOnKeycloak(final User savedUser, final String password, final String role) {
        String userId;
        try {
            userId = createUser(savedUser);

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(password);
            try {
                RealmResource realmResource = keycloak.realm(keycloakConfig.getRealm());
                UserResource userResource = realmResource.users().get(userId);
                realmResource.users().get(userId).resetPassword(passwordCred);
                ClientRepresentation userClient = realmResource.clients()
                        .findByClientId(keycloakConfig.getUserClientId()).getFirst();

                RoleRepresentation userClientRole = realmResource.clients().get(userClient.getId())
                        .roles().get(role).toRepresentation();

                userResource.roles()
                        .clientLevel(userClient.getId()).add(Collections.singletonList(userClientRole));
            } catch (NotFoundException e) {
                log.error("User ({} {})  creation failed!", savedUser.getEmail(), savedUser.getPhoneNumber(), e);
                throw new KeycloakException("User creation failed!", HttpStatus.INTERNAL_SERVER_ERROR.value());
            } catch (BadRequestException e) {
                log.warn("Password does not fit to the requirements for user ({} {}) ",
                        savedUser.getEmail(), savedUser.getPhoneNumber(), e);
                throw new KeycloakException("Password does not fit to the requirements!",
                        HttpStatus.BAD_REQUEST.value());
            }
            return Mono.just(userId);
        } catch (Exception e) {
            if (e instanceof KeycloakException kce) {
                return Mono.error(kce);
            }
            log.error("User ({} {}) keycloak signup failed due to: {} !",
                    savedUser.getEmail(), savedUser.getPhoneNumber(), e.getMessage());
            return Mono.error(
                    new KeycloakException(
                        "Signup failed due to: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e
                    )
            );
        }
    }

    public void deleteUser(final String userId) {
        try {
            RealmResource realmResource = keycloak.realm(keycloakConfig.getRealm());
            realmResource.users().get(userId).remove();
        } catch (Exception e) {
            log.error("User could not be removed!", e);
        }
    }

    private String createUser(final User savedUser) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(
                StringUtils.isNotEmpty(savedUser.getEmail()) ? savedUser.getEmail() : savedUser.getPhoneNumber());
        user.setFirstName(savedUser.getFirstName());
        user.setLastName(savedUser.getLastName());
        user.setEmail(savedUser.getEmail());
        Response response = keycloak.realm(keycloakConfig.getRealm()).users().create(user);
        if (response.getStatus() == HttpStatus.CONFLICT.value()) {
            log.warn("User({}) already exists.", user.getUsername());
            throw new KeycloakException(String.format("User(%s) already exists.", user.getUsername()),
                    response.getStatus());
        } else if (response.getStatus() != HttpStatus.CREATED.value()) {
            log.error("User ({} {}) keycloak signup failed on user creation due to: {}!",
                    savedUser.getEmail(), savedUser.getPhoneNumber(), response.getStatus());
            throw new KeycloakException(response.getStatusInfo().getReasonPhrase(), response.getStatus());
        }

        return CreatedResponseUtil.getCreatedId(response);
    }
}
