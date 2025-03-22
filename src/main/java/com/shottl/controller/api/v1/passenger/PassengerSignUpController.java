package com.shottl.controller.api.v1.passenger;

import com.shottl.controller.request.PassengerSignUpRequest;
import com.shottl.model.Passenger;
import com.shottl.service.OrganisationService;
import com.shottl.service.PassengerService;
import com.shottl.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class PassengerSignUpController {

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private OrganisationService organisationService;

    @PostMapping("/signup")
    Mono<ResponseEntity<String>> signUpPassenger(@RequestBody PassengerSignUpRequest request) {

        if (!UserUtils.isUUID(request.getInvitationCode())) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(StringUtils.isBlank(request.getInvitationCode()) ? "Invitation code required" : "Invalid code."));
        }

        return organisationService.validateInvitationCode(request.getInvitationCode()).
                flatMap(organisation -> {
                    Passenger passenger = Passenger.builder()
                            .firstName(request.getFirstName())
                            .middleName(request.getMiddleName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .phoneNumber(request.getPhoneNumber())
                            .signUpDate(new Date())
                            .organisationId(organisation.getId())
                            .build();

                    return passengerService.addPassenger(passenger, organisation, request.getPassword());
                })
                .thenReturn(ResponseEntity.ok("Passenger saved successfully."))
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode()).body(e.getReason()))
                );
    }
}
