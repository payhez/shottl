package com.shuttler.controller;

import com.shuttler.controller.request.AddInstitutionRequest;
import com.shuttler.controller.request.ManagerSignUpRequest;
import com.shuttler.model.Institution;
import com.shuttler.model.Manager;
import com.shuttler.service.InstitutionService;
import com.shuttler.service.ManagerService;
import com.shuttler.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/signup")
public class SignUpController {

    @Autowired
    InstitutionService institutionService;

    @Autowired
    ManagerService managerService;

    @Autowired
    PassengerService passengerService;

    @PostMapping("/institution")
    ResponseEntity<?> addInstitution(@RequestBody AddInstitutionRequest request) {
        Institution institution =
                Institution.builder()
                        .institutionName(request.getName())
                        .address(request.getAddress())
                        .geoLocation(request.getGeoLocation())
                        .institutionType(request.getInstitutionType())
                        .totalNumberOfPassengers(request.getTotalNumberOfPassengers())
                        .build();
        try {
            return new ResponseEntity<>(institutionService.addInstitution(institution), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Institution could not be added.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/manager")
    ResponseEntity<?> signUpManager(@RequestBody ManagerSignUpRequest request) {
        Manager manager = Manager.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .surname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();

        if (ObjectUtils.isEmpty(managerService.addManager(manager))) {
            return new ResponseEntity<>("Manager could not be added.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Manager added.", HttpStatus.ACCEPTED);
    }
}
