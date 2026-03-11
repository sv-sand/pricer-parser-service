package ru.svsand.pricer.parserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot application entry point for the Parser Service.
 * Enables Spring's scheduled task execution for periodic price parsing.
 */
@SpringBootApplication
@EnableScheduling
public class Application {

	/**
	 * Launches the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
