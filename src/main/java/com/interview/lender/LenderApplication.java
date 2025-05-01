package com.interview.lender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(LenderApplication.class, args);
	}
}
