package com.shottl.dao;

import com.shottl.model.Passenger;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PassengerRepository extends ReactiveMongoRepository<Passenger, String> {

    Flux<Passenger> findByFirstName(String firstName);
}
