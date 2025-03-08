package com.shuttler.service;

import com.shuttler.dao.OrganisationRepository;
import com.shuttler.model.Organisation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OrganisationService {

    @Autowired
    private OrganisationRepository organisationRepository;

    public Mono<Organisation> addOrganisation(final Organisation organisation) {
        String invitationCode = UUID.randomUUID().toString();
        organisation.setInvitationCode(invitationCode);
        return organisationRepository.save(organisation).onErrorResume(ex -> {
            if (ex instanceof DuplicateKeyException) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The organisation already exists!");
            }
            log.error("Organisation({}) could no be added!", organisation.getOrganisationName(), ex);
            return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Organisation could no be added!"));
        });
    }

    public Optional<ResponseEntity<String>> validateInvitationCode(final String invitationCode) {
        return organisationRepository.findByInvitationCode(invitationCode)
                .map(organisation -> {
                            if (organisation.getTotalNumberOfPassengers() <= organisation.getActiveNumberOfPassengers() ) {
                                return new ResponseEntity<>("No more passengers allowed.", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
                            }
                            return null;
                        })
                .or(() -> Optional.of(new ResponseEntity<>("Invitation code is wrong!", HttpStatus.NOT_ACCEPTABLE)));
    }
}
