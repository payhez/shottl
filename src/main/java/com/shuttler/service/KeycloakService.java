package com.shuttler.service;

import com.shuttler.exception.KeycloakException;
import com.shuttler.model.Manager;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.admin.realm}")
    private String realm;

    public KeycloakService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public Mono<Void> createUserOnKeycloak(final Manager savedManager, final String password) {
        try {
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(StringUtils.isNotEmpty(savedManager.getEmail()) ? savedManager.getEmail() : savedManager.getPhoneNumber());
            user.setFirstName(savedManager.getFirstName());
            user.setLastName(savedManager.getSurname());
            user.setEmail(savedManager.getEmail());
            Response response = keycloak.realm(realm).users().create(user);
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
                keycloak.realm(realm).users().get(userId).resetPassword(passwordCred);
            } catch (NotFoundException e) {
                log.error("User ({} {})  creation failed!",savedManager.getEmail(), savedManager.getPhoneNumber(), e);
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
