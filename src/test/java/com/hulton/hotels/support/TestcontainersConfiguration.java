package com.hulton.hotels.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

/**
 * A throwaway MySQL for integration tests, matching the mysql service in
 * docker-compose.yml. The app's datasource is pointed at it automatically, and
 * Flyway creates the schema and sample data in it on startup.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	MySQLContainer mysql() {
		// Same flag as docker-compose.yml: the schema has foreign keys to part of a composite key.
		return new MySQLContainer("mysql:8.4").withCommand("--restrict-fk-on-non-standard-key=OFF");
	}

}
