package com.hulton.hotels.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

/**
 * The whole app against MySQL in Testcontainers, with the Flyway schema and sample data
 * (4 hotels, 96 rooms, 5 guests; see db/sample). Each test runs in a transaction that is
 * rolled back, so tests can book rooms without affecting each other. Photos are not
 * uploaded, as there is no MinIO.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(properties = "hulton.images.seed-samples=false")
@Import(TestcontainersConfiguration.class)
@Transactional
public @interface IntegrationTest {

}
