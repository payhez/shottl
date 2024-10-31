package com.shuttler.service;

import com.shuttler.dao.ManagerRepository;
import com.shuttler.model.Manager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ManagerService {

    @Inject
    ManagerRepository managerRepository;

    public void addManager(final Manager manager) {
        managerRepository.save(manager);
    }

}
