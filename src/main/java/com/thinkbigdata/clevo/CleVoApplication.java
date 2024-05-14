package com.thinkbigdata.clevo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CleVoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CleVoApplication.class, args);
	}

}
