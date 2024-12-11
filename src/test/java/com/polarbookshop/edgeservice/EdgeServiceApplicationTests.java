package com.polarbookshop.edgeservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers //activation du démarrage auto et nettoyage des containers de tests
class EdgeServiceApplicationTests {

	public static final int REDIS_PORT = 6379;

	//définition du container des test Redis
	@Container
	static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
			.withExposedPorts(REDIS_PORT);

	//redéfinition pour les tests de la configuration Redis définie dans application.yml
	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", ()-> redis.getHost()); // paramètre fonction de type Supplier
		registry.add("spring.redis.port", ()-> redis.getMappedPort(REDIS_PORT));
	}
	@Test
	void verifyThatSpringContextLoads() {
	}

}
