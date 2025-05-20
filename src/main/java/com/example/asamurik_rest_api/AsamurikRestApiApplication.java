package com.example.asamurik_rest_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class AsamurikRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsamurikRestApiApplication.class, args);
	}

}
