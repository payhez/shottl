package com.shuttler.controller;

import com.shuttler.controller.request.AddOrganisationRequest;
import com.shuttler.controller.request.ManagerSignUpRequest;
import com.shuttler.controller.request.PassengerSignUpRequest;
import com.shuttler.model.Manager;
import com.shuttler.model.Organisation;
import com.shuttler.model.Passenger;
import com.shuttler.service.ManagerService;
import com.shuttler.service.OrganisationService;
import com.shuttler.service.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController("/signup")
public class SignUpController {

    private static final Logger log = LoggerFactory.getLogger(SignUpController.class);
    @Autowired
    OrganisationService organisationService;

    ManagerService managerService;

    @Autowired
    PassengerService passengerService;

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

    @PostMapping("/manager")
    ResponseEntity<?> signUpManager(@RequestBody ManagerSignUpRequest request) {
        Manager manager = Manager.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .surname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .signUpDate(new Date())
                .build();

        return managerService.addManager(manager)
                .map(savedManager -> new ResponseEntity<>(savedManager, HttpStatus.ACCEPTED))
                .onErrorResume(ex -> {
                    log.error("Manager could not be added due to", ex);
                    return Mono.just(new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR));
                }).block();
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
