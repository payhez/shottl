package com.shuttler.controller;

import com.shuttler.controller.request.AddOrganisationRequest;
import com.shuttler.controller.request.ManagerSignUpRequest;
import com.shuttler.model.Manager;
import com.shuttler.model.Organisation;
import com.shuttler.service.ManagerService;
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
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@Slf4j
@RequestMapping("/signup")
public class SignUpController {

    @Autowired
    OrganisationService organisationService;

    @Autowired
    ManagerService managerService;

    @Autowired
    PassengerService passengerService;

    @PostMapping("/manager")
    Mono<? extends ResponseEntity<?>> signUpManager(@RequestBody ManagerSignUpRequest request) {
        Manager manager = Manager.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .surname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .signUpDate(new Date())
                .build();

        return managerService.addManager(manager)
                .map(result -> {
                    if (result instanceof Manager savedManager) {
                        return new ResponseEntity<>(savedManager, HttpStatus.ACCEPTED);
                    } else if (result instanceof HttpClientErrorException httpClientErrorException) {
                        return new ResponseEntity<>(httpClientErrorException.getResponseBodyAsString(),
                                httpClientErrorException.getStatusCode());
                    }
                    return new ResponseEntity<>("Internal Server error!", HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    @PostMapping("/organisation")
    ResponseEntity<?> addOrganisation(@RequestBody AddOrganisationRequest request) {
        Organisation organisation =
                Organisation.builder()
                        .organisationName(request.getName())
                        .address(request.getAddress())
                        .geoLocation(request.getGeoLocation())
                        .organisationType(request.getOrganisationType())
                        .totalNumberOfPassengers(request.getTotalNumberOfPassengers())
                        .build();
        try {
            return new ResponseEntity<>(organisationService.addOrganisation(organisation).block(), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>("Organisation could not be added.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /*@PostMapping("/passenger")
    ResponseEntity<?> signUpPassenger(@RequestBody PassengerSignUpRequest request) {

        ResponseEntity<?> responseEntity;

        organisationService.validateInvitationCode(request.getInvitationCode());
        Passenger manager = Passenger.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .surname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .signUpDate(new Date())
                .build();

        if (ObjectUtils.isEmpty(passengerService.addPassanger(manager))) {
            return new ResponseEntity<>("Manager could not be added.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Passenger added.", HttpStatus.ACCEPTED);
    }*/
}
