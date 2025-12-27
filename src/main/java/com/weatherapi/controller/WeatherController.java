package com.weatherapi.controller;

import com.weatherapi.model.WeatherResponse;
import com.weatherapi.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WeatherController {

    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);
    
    private final WeatherService weatherService;
    
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Get weather data for a specific city
     * 
     * GET /api/weather?city=London
     * GET /api/weather?city=New York
     */
    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam String city) {
        log.info("Received weather request for city: {}", city);
        WeatherResponse weather = weatherService.getWeather(city);
        return ResponseEntity.ok(weather);
    }

    /**
     * Get weather data for a specific city (alternative endpoint)
     * 
     * GET /api/weather/London
     * GET /api/weather/New%20York
     */
    @GetMapping("/weather/{city}")
    public ResponseEntity<WeatherResponse> getWeatherByPath(@PathVariable String city) {
        log.info("Received weather request for city: {}", city);
        WeatherResponse weather = weatherService.getWeather(city);
        return ResponseEntity.ok(weather);
    }

    /**
     * Clear cache for a specific city
     * 
     * DELETE /api/cache?city=London
     */
    @DeleteMapping("/cache")
    public ResponseEntity<Map<String, String>> clearCache(@RequestParam String city) {
        log.info("Clearing cache for city: {}", city);
        weatherService.clearCache(city);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache cleared for city: " + city);
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear all weather cache
     * 
     * DELETE /api/cache/all
     */
    @DeleteMapping("/cache/all")
    public ResponseEntity<Map<String, String>> clearAllCache() {
        log.info("Clearing all weather cache");
        weatherService.clearAllCache();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "All weather cache cleared");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     * 
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Weather API");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(response);
    }
}
