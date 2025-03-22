package com.shottl;

import org.springframework.boot.SpringApplication;

public class TestShottlApplication {
	public static void main(String[] args) {
		SpringApplication.from(ShottlApplication::main).with(TestcontainersConfiguration.class).run(args);
	}
}
