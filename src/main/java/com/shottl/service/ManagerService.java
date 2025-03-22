package com.shottl.service;

import com.shottl.dao.ManagerRepository;
import com.shottl.model.Manager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.shottl.utils.UserUtils.hasAtLeastOneCommunicationChannel;

@Service
@Slf4j
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private ServiceHelper serviceHelper;

    public Mono<Manager> addManager(final Manager manager, final String password) {
        if (!hasAtLeastOneCommunicationChannel(manager)) {
            log.warn("No communication channel is provided for manager: {} {}!", manager.getFirstName(), manager.getLastName());
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
                }).doOnError(e -> serviceHelper.handleUserSignupError(e));
    }

    public Mono<Void> deleteManager(final Manager manager) {
        return managerRepository.delete(manager)
                .doOnSuccess(unused ->
                    log.debug("The manager({}, {}) deleted successfully!", manager.getEmail(), manager.getPhoneNumber()))
                .doOnError(ex -> log.error("Manager could not be deleted due to: ", ex));
    }

    public void disableManager(final Manager manager) {
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
 }
