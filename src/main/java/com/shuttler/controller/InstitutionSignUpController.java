package com.shuttler.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
public class InstitutionSignUpController {

    @PostMapping("")
    ResponseEntity<?> signUpInstitution(@RequestBody Institution institutionRequest) {

        return null;
    }
}
