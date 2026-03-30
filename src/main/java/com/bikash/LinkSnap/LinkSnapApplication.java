package com.bikash.LinkSnap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LinkSnapApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkSnapApplication.class, args);
	}

}
