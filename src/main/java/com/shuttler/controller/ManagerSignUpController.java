package com.shuttler.controller;

import com.shuttler.controller.request.ManagerSignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManagerSignUpController {



    @PostMapping("/manager/signup")
    ResponseEntity<?> signUpOrganisationManager(@RequestBody ManagerSignUpRequest request) {



    }
}
