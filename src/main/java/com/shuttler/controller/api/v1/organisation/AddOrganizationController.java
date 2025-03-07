package com.shuttler.controller.api.v1.organisation;

import com.shuttler.controller.request.AddOrganisationRequest;
import com.shuttler.model.Organisation;
import com.shuttler.service.OrganisationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping
public class AddOrganizationController {

    @Autowired
    OrganisationService organisationService;

    @PostMapping("/add")
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
}
