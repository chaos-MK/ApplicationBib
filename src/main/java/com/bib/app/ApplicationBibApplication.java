package com.bib.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication(scanBasePackages = {"com.bib.app"})
public class ApplicationBibApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationBibApplication.class, args);
	}
}
