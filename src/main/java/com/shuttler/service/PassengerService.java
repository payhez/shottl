package com.shuttler.service;

import com.shuttler.dao.OrganisationRepository;
import com.shuttler.dao.PassengerRepository;
import com.shuttler.model.Organisation;
import com.shuttler.model.Passenger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.shuttler.utils.UserUtils.hasAtLeastOneCommunicationChannel;

@Service
@Slf4j
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private ServiceHelper serviceHelper;

    public Mono<Passenger> addPassenger(final Passenger passenger,
                                        final Organisation organisation,
                                        final String password) {

        if (!hasAtLeastOneCommunicationChannel(passenger)) {
            log.warn("No communication channel is provided for passenger: {} {}!", passenger.getFirstName(), passenger.getLastName());
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No communication channel is provided!"));
        }

        return keycloakService.createUserOnKeycloak(passenger, password, "PASSENGER")
                .flatMap(userId -> {
                    passenger.setId(userId);
                    return passengerRepository.save(passenger)
                            .onErrorMap(e -> {
                                keycloakService.deleteUser(userId);
                                return e;
                            });
                })
                .doOnSuccess(s ->
                        organisationRepository
                                .findAndIncrementActiveNumberOfPassengersById(organisation.getId()).subscribe())
                .doOnError(e -> serviceHelper.handleUserSignupError(e));
    }
}
