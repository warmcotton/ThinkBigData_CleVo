package com.thinkbigdata.anna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AnnaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnnaApplication.class, args);
	}

}
