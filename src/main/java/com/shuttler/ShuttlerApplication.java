package com.shuttler;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Shuttler API", version= "1.0.0"))
public class ShuttlerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShuttlerApplication.class, args);
	}
}
