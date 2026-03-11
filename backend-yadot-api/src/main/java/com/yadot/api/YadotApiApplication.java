package com.yadot.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class YadotApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(YadotApiApplication.class, args);
	}

}