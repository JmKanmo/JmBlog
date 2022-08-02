package com.service.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi(@Value("${springdoc.packages-to-scan}") String scanPackage) {
        return GroupedOpenApi.builder()
                .group("freelog.open.api")
                .packagesToScan(scanPackage)
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("freelog.open.api")
                        .description("freelog api documentation")
                        .version("v0.0.1"));
    }
}