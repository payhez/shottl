package com.moush;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:latest";

	//@Container
	//static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withExposedPorts(26016);
//
	//@DynamicPropertySource
	//static void containersProperties(DynamicPropertyRegistry registry) {
	//	registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
	//	registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
	//	registry.add("spring.data.mongodb.auto-index-creation", () -> "true");
	//}

	//@Bean
	//@ServiceConnection
	//KafkaContainer kafkaContainer() {
	//	return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
	//}

	@Bean
	@ServiceConnection
	public MongoDBContainer mongoDBContainer(DynamicPropertyRegistry registry) {
		MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
		registry.add("spring.data.mongodb.auto-index-creation", () -> "true");
		return mongoDBContainer;
	}

	@Bean
	KeycloakContainer keycloak(DynamicPropertyRegistry registry) {
		var keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
				.withEnv("KC_BOOTSTRAP_ADMIN_USERNAME", "admin")
				.withEnv("KC_BOOTSTRAP_ADMIN_PASSWORD", "adminPass")
				.withRealmImportFile("../keycloak/moush-realm.json")
				.withReuse(true);
		registry.add("keycloak.admin.serverUrl", keycloak::getAuthServerUrl);
		return keycloak;
	}
}
