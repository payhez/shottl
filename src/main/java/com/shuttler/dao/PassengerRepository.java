package com.shuttler.dao;

import com.shuttler.model.Passenger;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends ReactiveMongoRepository<Passenger, String> {
}
