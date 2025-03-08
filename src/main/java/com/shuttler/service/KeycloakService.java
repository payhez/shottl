package com.shuttler.service;

import com.shuttler.config.KeycloakConfig;
import com.shuttler.exception.KeycloakException;
import com.shuttler.model.Manager;
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
    // TODO create another model called User.. Bcz User and Person are different concepts
    // TODO make this method generic(not only for manager) --> createUserOnKeycloak
    public Mono<Void> createUserOnKeycloak(final Manager savedManager, final String password) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setId(savedManager.getId());
            user.setUsername(StringUtils.isNotEmpty(savedManager.getEmail()) ? savedManager.getEmail() : savedManager.getPhoneNumber());
            user.setFirstName(savedManager.getFirstName());
            user.setLastName(savedManager.getSurname());
            user.setEmail(savedManager.getEmail());
            Response response = keycloak.realm(keycloakConfig.getRealm()).users().create(user);
            if (response.getStatus() != HttpStatus.CREATED.value()) {
                log.error("User ({} {}) keycloak signup failed on user creation due to: {}!",
                        savedManager.getEmail(), savedManager.getPhoneNumber(), response.getStatus());
                throw new KeycloakException(response.getStatusInfo().getReasonPhrase(), response.getStatus());
            }

            String userId = CreatedResponseUtil.getCreatedId(response);

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
                        .roles().get("MANAGER").toRepresentation();

                userResource.roles()
                        .clientLevel(userClient.getId()).add(Collections.singletonList(userClientRole));
            } catch (NotFoundException e) {
                log.error("User ({} {})  creation failed!", savedManager.getEmail(), savedManager.getPhoneNumber(), e);
                throw new KeycloakException("User creation failed!", HttpStatus.INTERNAL_SERVER_ERROR.value());
            } catch (BadRequestException e) {
                log.warn("Password does not fit to the requirements for user ({} {}) ",
                        savedManager.getEmail(), savedManager.getPhoneNumber(), e);
                throw new KeycloakException("Password does not fit to the requirements!", HttpStatus.BAD_REQUEST.value());
            }
            return Mono.empty();
        } catch (Exception e) {
            if (e instanceof KeycloakException kce) {
                throw kce;
            }
            log.error("User ({} {}) keycloak signup failed due to: {} !",
                    savedManager.getEmail(), savedManager.getPhoneNumber(), e.getMessage());
            throw new KeycloakException(
                    "Signup failed due to: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }
}
