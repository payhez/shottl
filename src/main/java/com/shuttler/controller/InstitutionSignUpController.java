package com.shuttler.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstitutionSignUpController {

    @PostMapping("/signup")
    ResponseEntity<?> signUpInstitution(@RequestBody institutionRequest) {

        return null;
    }
}
