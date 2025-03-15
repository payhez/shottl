package com.moush.controller.api.v1.manager;

import com.moush.controller.request.ManagerSignUpRequest;
import com.moush.model.Manager;
import com.moush.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping
public class ManagerSignUpController {

    @Autowired
    ManagerService managerService;

    @PostMapping("/signup")
    Mono<ResponseEntity<String>> signUpManager(@RequestBody ManagerSignUpRequest request) {
        Manager manager = Manager.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .signUpDate(new Date())
                .build();

        return managerService.addManager(manager, request.getPassword())
                .thenReturn(ResponseEntity.ok("Manager saved successfully."))
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode()).body(e.getReason()))
                );
    }
}
