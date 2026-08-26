package com.lar.customer_onboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan("com.lar")
public class CustomerOnboardingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerOnboardingApplication.class, args);
	}

}
