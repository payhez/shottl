package com.shuttler;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:latest";

	//@Bean
	//@ServiceConnection
	//KafkaContainer kafkaContainer() {
	//	return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
	//}

	@Bean
	@ServiceConnection
	public MongoDBContainer mongoDBContainer() {
		return new MongoDBContainer(DockerImageName.parse("mongo:latest"));
	}

	@Bean
	KeycloakContainer keycloak(DynamicPropertyRegistry registry) {
		var keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
				.withEnv("KC_BOOTSTRAP_ADMIN_USERNAME", "admin")
				.withEnv("KC_BOOTSTRAP_ADMIN_PASSWORD", "adminPass")
				.withRealmImportFile("shuttler-realm.json");
		registry.add("keycloak.admin.serverUrl", keycloak::getAuthServerUrl);
		return keycloak;
	}

}
