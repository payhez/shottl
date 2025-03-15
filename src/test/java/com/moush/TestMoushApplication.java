package com.moush;

import org.springframework.boot.SpringApplication;

public class TestMoushApplication {
	public static void main(String[] args) {
		SpringApplication.from(MoushApplication::main).with(TestcontainersConfiguration.class).run(args);
	}
}
