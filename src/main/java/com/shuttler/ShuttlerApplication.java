package com.shuttler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class ShuttlerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShuttlerApplication.class, args);

	}
}
