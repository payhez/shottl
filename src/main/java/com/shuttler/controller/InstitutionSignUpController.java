package com.shuttler.controller;

import com.shuttler.controller.request.AddInstitutionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstitutionSignUpController {

    @PostMapping("/institution")
    ResponseEntity<?> signUpInstitution(@RequestBody AddInstitutionRequest institutionRequest) {
        return null;
    }
}
