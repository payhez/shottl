package com.shuttler.controller;

import com.shuttler.controller.request.ManagerSignUpRequest;
import com.shuttler.model.Manager;
import com.shuttler.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class ManagerSignUpController {

    @Inject
    ManagerService managerService;

    @PostMapping("/manager")
    ResponseEntity<?> signUpOrganisationManager(@RequestBody ManagerSignUpRequest request) {
        Manager manager = Manager.builder()
                                .firstName(request.getFirstName())
                                .middleName(request.getMiddleName())
                                .surname(request.getLastName())
                                .build();

        managerService.addManager(manager);
        return null;
    }
}
