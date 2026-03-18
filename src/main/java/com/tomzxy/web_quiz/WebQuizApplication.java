package com.tomzxy.web_quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebQuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebQuizApplication.class, args);
	}

}
