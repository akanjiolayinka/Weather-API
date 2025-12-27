package com.weatherapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WeatherApiApplication {

    public static void main(String[] args) {
        // Load .env file if it exists
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            
            dotenv.entries().forEach(entry -> 
                System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
            System.out.println("No .env file found, using system environment variables");
        }
        
        SpringApplication.run(WeatherApiApplication.class, args);
    }
}
