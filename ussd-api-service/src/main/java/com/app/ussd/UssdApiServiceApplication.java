package com.app.ussd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UssdApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UssdApiServiceApplication.class, args);
		
		
	}

}
