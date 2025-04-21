package com.marketplace.salesapi;

import com.marketplace.salesapi.config.PostgresTestContainer;
import com.marketplace.salesapi.config.TestSecurityConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(
    classes = SalesApiApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.main.allow-bean-definition-overriding=true"}
)
@ContextConfiguration(
    classes = {SalesApiApplication.class, PostgresTestContainer.class},
    initializers = PostgresTestContainer.Initializer.class
)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@Sql(scripts = "classpath:cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class BaseIntegrationTest {
} 