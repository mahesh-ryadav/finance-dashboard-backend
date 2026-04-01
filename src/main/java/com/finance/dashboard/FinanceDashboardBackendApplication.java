package com.finance.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // it enables createdAt / updatedAt auto fill
public class FinanceDashboardBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceDashboardBackendApplication.class, args);
	}

}
