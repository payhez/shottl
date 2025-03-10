package com.shuttler.controller.api.v1.organisation;

import com.shuttler.controller.request.AddOrganisationRequest;
import com.shuttler.model.Organisation;
import com.shuttler.service.OrganisationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@Slf4j
@RequestMapping
public class AddOrganizationController {

    @Autowired
    private OrganisationService organisationService;

    @PostMapping("/add")
    Mono<ResponseEntity<String>> addOrganisation(@RequestBody AddOrganisationRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {

        Organisation organisation =
                Organisation.builder()
                        .organisationName(request.getName())
                        .address(request.getAddress())
                        .geoLocation(request.getGeoLocation())
                        .organisationType(request.getOrganisationType())
                        .totalNumberOfPassengers(request.getTotalNumberOfPassengers())
                        .activeNumberOfPassengers(0)
                        .managers(Collections.singletonList(jwt.getSubject()))
                        .build();

        return organisationService.addOrganisation(organisation)
                .thenReturn(ResponseEntity.ok("Organisation saved successfully."))
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode()).body(e.getReason()))
                );
    }
}
