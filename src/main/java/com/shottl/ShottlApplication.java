package com.shottl;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Shottl API", version= "1.0.0"))
public class ShottlApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShottlApplication.class, args);
	}
}
