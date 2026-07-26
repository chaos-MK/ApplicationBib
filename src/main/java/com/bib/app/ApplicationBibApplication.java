package com.bib.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.bib.app"})
public class ApplicationBibApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationBibApplication.class, args);
	}
}
