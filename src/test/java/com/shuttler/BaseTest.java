package com.shuttler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
public class BaseTest {

    private static MongoDBContainer mongoDBContainer;
    @Autowired
    protected MongoTemplate mongoTemplate;

    @BeforeAll
    static void startMongoDBContainer() {
        if (mongoDBContainer == null) {
            mongoDBContainer = new MongoDBContainer("mongo:latest");
            mongoDBContainer.start();
            System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
        }
    }

    @AfterEach
    public void clearDatabase() {
        mongoTemplate.getDb().drop();
    }

}
