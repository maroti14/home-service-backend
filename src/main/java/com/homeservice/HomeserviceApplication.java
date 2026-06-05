package com.homeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HomeserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeserviceApplication.class, args);
	}

}
