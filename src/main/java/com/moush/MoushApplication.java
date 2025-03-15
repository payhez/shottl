package com.moush;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Moush API", version= "1.0.0"))
public class MoushApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoushApplication.class, args);
	}
}
