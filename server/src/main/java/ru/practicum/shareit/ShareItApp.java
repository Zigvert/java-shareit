package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ShareItApp {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ShareItApp.class);
		ConfigurableApplicationContext context = app.run(args);

		Environment env = context.getEnvironment();
		System.out.println("Datasource URL: " + env.getProperty("spring.datasource.url"));
	}
}