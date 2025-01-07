package com.shuttler.dao;

import com.shuttler.model.Organisation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganisationRepository extends ReactiveMongoRepository<Organisation, String> {
    Optional<Organisation> findByInvitationCode(String invitationCode);
}
