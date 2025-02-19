package com.shuttler.service;

import com.shuttler.dao.ManagerRepository;
import com.shuttler.model.Manager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Mono<ResponseEntity<Object>> addManager(final Manager manager) {
        if (!hasAtLeastOneCommunicationChannel(manager)) {
            log.error("No communication channel is provided for manager: {} {}!", manager.getFirstName(), manager.getSurname());
            return Mono.just(new ResponseEntity<>("No communication channel is provided!", HttpStatus.BAD_REQUEST));
        }

        return managerRepository.save(manager)
                .map(savedManager -> new ResponseEntity<Object>(savedManager, HttpStatus.ACCEPTED))
                .onErrorResume(ex -> {
                    log.error("Manager could not be added due to: ", ex);
                    if (ex instanceof DuplicateKeyException) {
                        return Mono.just(new ResponseEntity<>("The communication channel already exists!", HttpStatus.CONFLICT));
                    } else {
                        return Mono.just(new ResponseEntity<>("Internal Server Error!", HttpStatus.INTERNAL_SERVER_ERROR));
                    }
                });
    }

    private boolean hasAtLeastOneCommunicationChannel(final Manager manager) {
        return BooleanUtils.isFalse(StringUtils.isBlank(manager.getEmail()) && StringUtils.isBlank(manager.getPhoneNumber()));
    }
}
