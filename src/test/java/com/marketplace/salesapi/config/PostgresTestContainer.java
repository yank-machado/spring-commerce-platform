package com.marketplace.salesapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import jakarta.annotation.PreDestroy;

@TestConfiguration
public class PostgresTestContainer {
    
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);
    
    static {
        postgres.start();
    }
    
    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return postgres;
    }
    
    @PreDestroy
    public void stopContainer() {
        if (postgres.isRunning()) {
            postgres.stop();
        }
    }
    
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgres.getJdbcUrl(),
                "spring.datasource.username=" + postgres.getUsername(),
                "spring.datasource.password=" + postgres.getPassword(),
                "spring.jpa.hibernate.ddl-auto=create-drop"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
} 