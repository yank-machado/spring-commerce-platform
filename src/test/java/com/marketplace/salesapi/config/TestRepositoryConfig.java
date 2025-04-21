package com.marketplace.salesapi.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.marketplace.salesapi")
@EnableJpaRepositories(basePackages = "com.marketplace.salesapi")
public class TestRepositoryConfig {
} 