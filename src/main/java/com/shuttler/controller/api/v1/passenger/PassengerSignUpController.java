package com.shuttler.controller.api.v1.passenger;

import com.shuttler.controller.request.PassengerSignUpRequest;
import com.shuttler.model.Passenger;
import com.shuttler.service.OrganisationService;
import com.shuttler.service.PassengerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping
public class PassengerSignUpController {

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private OrganisationService organisationService;

    @PostMapping("/signup")
    ResponseEntity<?> signUpPassenger(@RequestBody PassengerSignUpRequest request) {

        ResponseEntity<?> responseEntity;

        organisationService.validateInvitationCode(request.getInvitationCode());
        Passenger passenger = Passenger.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .surname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .signUpDate(new Date())
                .build();

        //if (ObjectUtils.isEmpty(passengerService.addPassanger(manager))) {
        //    return new ResponseEntity<>("Manager could not be added.", HttpStatus.INTERNAL_SERVER_ERROR);
        //}
        return new ResponseEntity<>("Passenger added.", HttpStatus.ACCEPTED);
    }
}
