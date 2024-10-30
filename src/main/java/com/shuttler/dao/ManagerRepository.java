package com.shuttler.dao;


import com.shuttler.model.Manager;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ManagerRepository extends MongoRepository<Manager, String> {
}
