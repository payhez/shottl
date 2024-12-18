package com.shuttler.service;

import com.shuttler.dao.ManagerRepository;
import com.shuttler.model.Manager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ManagerService {

    @Autowired
    ManagerRepository managerRepository;

    public Manager addManager(final Manager manager) {
        try {
            return managerRepository.save(manager);
        } catch (Exception e) {
            log.error("Manager could not be added!", e);
            return null;
        }
    }

}
