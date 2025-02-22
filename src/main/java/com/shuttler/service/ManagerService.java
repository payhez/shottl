package com.shuttler.service;

import com.shuttler.dao.ManagerRepository;
import com.shuttler.model.Manager;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ManagerService {

    @Autowired
    ManagerRepository managerRepository;

    @Value("${keycloak.admin.serverUrl}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.adminRealm}")
    private String adminRealm;

    @Value("${keycloak.admin.adminUser}")
    private String adminUser;

    @Value("${keycloak.admin.adminPassword}")
    private String adminPassword;

    @Value("${keycloak.admin.clientId}")
    private String clientId;


    public Mono<ResponseEntity<Object>> addManager(final Manager manager, final String password) {
        if (!hasAtLeastOneCommunicationChannel(manager)) {
            log.error("No communication channel is provided for manager: {} {}!", manager.getFirstName(), manager.getSurname());
            return Mono.just(new ResponseEntity<>("No communication channel is provided!", HttpStatus.BAD_REQUEST));
        }

        return managerRepository.save(manager)
                .map(savedManager -> createUserOnKeycloak(manager, password))
                .onErrorResume(ex -> {
                    log.error("Manager could not be added due to: ", ex);
                    if (ex instanceof DuplicateKeyException) {
                        return Mono.just(new ResponseEntity<>("The communication channel already exists!", HttpStatus.CONFLICT));
                    } else {
                        return Mono.just(new ResponseEntity<>("Internal Server Error!", HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }

    private void deleteManager(final Manager manager) {
        managerRepository.delete(manager)
                .doOnSuccess(unused ->
                        log.debug("The manager({}, {}) deleted successfully!", manager.getEmail(), manager.getPhoneNumber()))
                .doOnError(ex -> {
                    log.error("Manager could not be deleted due to: ", ex);
                }).subscribe();
    }

    private void disableManager(final Manager manager) {
        if (BooleanUtils.isTrue(manager.getActive())) {
            manager.setActive(false);
        } else {
            return;
        }
        managerRepository.save(manager)
                .doOnSuccess(unused ->
                        log.debug("The manager({}, {}) disabled successfully!", manager.getEmail(), manager.getPhoneNumber()))
                .doOnError(ex -> {
                    log.error("Manager could not be disabled due to: ", ex);
                }).subscribe();
    }

    private boolean hasAtLeastOneCommunicationChannel(final Manager manager) {
        return BooleanUtils.isFalse(StringUtils.isBlank(manager.getEmail()) && StringUtils.isBlank(manager.getPhoneNumber()));
    }

    private ResponseEntity<Object> createUserOnKeycloak(final Manager savedManager, final String password) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm(adminRealm) // usually admin realm
                    .username(adminUser)
                    .password(adminPassword)
                    .clientId(clientId)
                    .build();

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
                deleteManager(savedManager);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Signup failed while creating user: " + response.getStatus());
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
                deleteManager(savedManager);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("User have not been created successfully!");
            } catch (BadRequestException e) {
                log.warn("Password does not fit to the requirements for user ({} {}) ",
                        savedManager.getEmail(), savedManager.getPhoneNumber(), e);
                deleteManager(savedManager);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Password does not fit to the requirements!");
            }
            return new ResponseEntity<>(savedManager, HttpStatus.ACCEPTED);

        } catch (Exception e) {
            log.error("User ({} {}) keycloak signup failed due to: {} !",
                    savedManager.getEmail(), savedManager.getPhoneNumber(), e.getMessage());
            deleteManager(savedManager);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Signup failed: " + e.getMessage());
        }
    }
 }
