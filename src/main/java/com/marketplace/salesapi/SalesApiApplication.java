package com.marketplace.salesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "Marketplace Sales API",
    version = "1.0",
    description = "API RESTful para um marketplace de vendas adaptável a qualquer nicho"
))
public class SalesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesApiApplication.class, args);
    }

}