package com.shuttler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public abstract class BaseTest {

    @Autowired
    protected MongoTemplate mongoTemplate;

    @AfterEach
    public void clearDatabase() {
        mongoTemplate.getDb().drop();
    }
}
