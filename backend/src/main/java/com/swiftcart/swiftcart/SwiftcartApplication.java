package com.swiftcart.swiftcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SwiftcartApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiftcartApplication.class, args);
	}

}
