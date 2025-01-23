package com.shuttler.service;

import com.shuttler.dao.ManagerRepository;
import com.shuttler.model.Manager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ManagerService {

    @Autowired
    ManagerRepository managerRepository;

    public Mono<?> addManager(final Manager manager) {
        checkAtLeastOneCommunicationChannel(manager);
        return managerRepository.save(manager).onErrorResume(ex -> {
            log.error("Manager could not be added due to", ex);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error!");
        });
    }

    private void checkAtLeastOneCommunicationChannel(final Manager manager) {
        if (StringUtils.isBlank(manager.getEmail()) && StringUtils.isBlank(manager.getPhoneNumber())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "No communication channel is provided!");
        }
    }
}
