package com.lar.customeronboarding.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customerOnboardingApi() {
        return new OpenAPI().info(new Info()
                .title("Random Bank Customer Onboarding API")
                .description("Remote registration, login and account overview for Random Bank customers")
                .version("v1"));
    }
}