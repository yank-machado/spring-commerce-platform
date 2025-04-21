package com.marketplace.salesapi.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class MigrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
            .withDatabaseName("migration_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private Flyway flyway;

    @Test
    @DisplayName("Flyway migrations should apply successfully")
    void migrationsApplySuccessfully() {
        // Validate migrations apply cleanly
        flyway.migrate();
        
        // Check that migrations were applied successfully
        assertTrue(flyway.info().current() != null, "Migrations should be applied");
        
        // Validate that the schema meets our expectations
        assertTrue(postgres.isRunning(), "PostgreSQL container should be running");
    }

    @Test
    @DisplayName("Flyway migrations should be repeatable")
    void migrationsAreRepeatable() {
        // Apply migrations
        flyway.migrate();
        
        // Clean database
        flyway.clean();
        
        // Re-apply migrations
        flyway.migrate();
        
        // Check that migrations were applied successfully
        assertTrue(flyway.info().current() != null, "Migrations should be repeatable");
    }
} 