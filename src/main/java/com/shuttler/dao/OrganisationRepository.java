package com.shuttler.dao;

import com.shuttler.model.Organisation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OrganisationRepository extends ReactiveMongoRepository<Organisation, String> {
    Mono<Organisation> findByInvitationCode(String invitationCode);
    Mono<Organisation> findByOrganisationName(String organisationName);

    @Update("{ '$inc' : { 'activeNumberOfPassengers' : 1 } }")
    Mono<Void> findAndIncrementActiveNumberOfPassengersById(String id);
}
