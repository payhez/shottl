package com.shuttler;

import org.springframework.boot.SpringApplication;

public class TestShuttlerApplication {

	public static void main(String[] args) {
		SpringApplication.from(ShuttlerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
