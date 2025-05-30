package com.dentagenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DentagendaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentagendaApplication.class, args);
	}

}
