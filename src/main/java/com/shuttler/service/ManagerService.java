package com.shuttler.service;

import com.shuttler.dao.ManagerRepository;
import com.shuttler.exception.KeycloakException;
import com.shuttler.model.Manager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private KeycloakService keycloakService;

    public Mono<Manager> addManager(final Manager manager, final String password) {
        if (!hasAtLeastOneCommunicationChannel(manager)) {
            log.warn("No communication channel is provided for manager: {} {}!", manager.getFirstName(), manager.getSurname());
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No communication channel is provided!"));
        }

        return keycloakService.createUserOnKeycloak(manager, password, "MANAGER")
                .flatMap(userId -> {
                    manager.setId(userId);
                    return managerRepository.save(manager)
                            .onErrorMap(e -> {
                                keycloakService.deleteUser(userId);
                                return e;
                            });
                }).doOnError(this::handleManagerSignupError);

        /*return managerRepository.save(manager)
                .flatMap(savedManager ->
                        keycloakService.createUserOnKeycloak(savedManager, password, "MANAGER")
                                .onErrorMap(e -> {
                                    deleteManager(savedManager);
                                    return e;
                                })
                ).doOnError(this::handleManagerSignupError);*/
    }

    private Mono<Void> deleteManager(final Manager manager) {
        return managerRepository.delete(manager)
                .doOnSuccess(unused ->
                    log.debug("The manager({}, {}) deleted successfully!", manager.getEmail(), manager.getPhoneNumber()))
                .doOnError(ex -> log.error("Manager could not be deleted due to: ", ex));
    }

    private void handleManagerSignupError(final Throwable ex) {
        log.error("Manager could not be added due to: ", ex);
        if (ex instanceof DuplicateKeyException) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The communication channel already exists!");
        } else if (ex instanceof KeycloakException kce) {
            throw new ResponseStatusException(HttpStatus.valueOf(kce.getHttpStatus()), kce.getMessage());
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error!", ex);
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
 }
