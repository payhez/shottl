package com.shuttler.service;

import com.shuttler.dao.ManagerRepository;
import com.shuttler.model.Manager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ManagerService {

    @Autowired
    ManagerRepository managerRepository;

    public Mono<Manager> addManager(final Manager manager) {
        return managerRepository.save(manager);
    }
}
