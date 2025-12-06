package com.shottl.service;

import com.shottl.dao.OrganisationRepository;
import com.shottl.dao.PassengerRepository;
import com.shottl.model.Organisation;
import com.shottl.model.Passenger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static com.shottl.utils.UserUtils.hasAtLeastOneCommunicationChannel;

@Service
@Slf4j
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final OrganisationRepository organisationRepository;
    private final KeycloakService keycloakService;
    private final ServiceHelper serviceHelper;

    public Mono<Passenger> addPassenger(final Passenger passenger,
                                        final Organisation organisation,
                                        final String password) {

        if (!hasAtLeastOneCommunicationChannel(passenger)) {
            log.warn("No communication channel is provided for passenger: {} {}!",
                    passenger.getFirstName(), passenger.getLastName());
            return Mono.error(
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "No communication channel is provided!")
            );
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
