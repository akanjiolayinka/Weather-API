package com.weatherapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapi.exception.WeatherApiException;
import com.weatherapi.model.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final RedisTemplate<String, WeatherResponse> redisTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${weather.api.url}")
    private String weatherApiUrl;
    
    @Value("${weather.api.key}")
    private String weatherApiKey;
    
    @Value("${weather.cache.ttl}")
    private long cacheTtlSeconds;

    public WeatherService(RedisTemplate<String, WeatherResponse> redisTemplate,
                         ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    /**
     * Get weather data for a city. Checks cache first, then fetches from API if needed.
     * 
     * @param city The city name or location
     * @return WeatherResponse containing weather data
     */
    public WeatherResponse getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new WeatherApiException("City parameter is required", 400);
        }

        String cacheKey = getCacheKey(city);
        
        // Try to get from cache first
        WeatherResponse cachedWeather = getFromCache(cacheKey);
        if (cachedWeather != null) {
            log.info("Cache hit for city: {}", city);
            cachedWeather.setSource("cache");
            return cachedWeather;
        }

        // Cache miss - fetch from API
        log.info("Cache miss for city: {}. Fetching from API...", city);
        WeatherResponse weatherResponse = fetchFromApi(city);
        
        // Save to cache
        saveToCache(cacheKey, weatherResponse);
        
        weatherResponse.setSource("api");
        weatherResponse.setCachedAt(System.currentTimeMillis());
        
        return weatherResponse;
    }

    /**
     * Fetch weather data from Visual Crossing API
     */
    private WeatherResponse fetchFromApi(String city) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(weatherApiUrl)
                    .pathSegment(city)
                    .queryParam("key", weatherApiKey)
                    .queryParam("unitGroup", "metric")
                    .queryParam("include", "current")
                    .toUriString();

            log.debug("Calling weather API: {}", url.replaceAll(weatherApiKey, "***"));
            
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            
            if (response == null) {
                throw new WeatherApiException("No data received from weather API", 500);
            }
            
            return response;
            
        } catch (HttpClientErrorException.NotFound e) {
            log.error("City not found: {}", city);
            throw new WeatherApiException("City not found: " + city, 404);
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Invalid API key");
            throw new WeatherApiException("Weather API authentication failed", 401);
        } catch (HttpClientErrorException e) {
            log.error("Weather API client error: {}", e.getMessage());
            throw new WeatherApiException("Invalid request to weather API: " + e.getMessage(), e.getStatusCode().value());
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage(), e);
            throw new WeatherApiException("Failed to fetch weather data: " + e.getMessage(), e);
        }
    }

    /**
     * Get weather data from Redis cache
     */
    private WeatherResponse getFromCache(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Error reading from cache: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Save weather data to Redis cache with expiration
     */
    private void saveToCache(String key, WeatherResponse data) {
        try {
            redisTemplate.opsForValue().set(key, data, cacheTtlSeconds, TimeUnit.SECONDS);
            log.info("Cached weather data for key: {} (TTL: {} seconds)", key, cacheTtlSeconds);
        } catch (Exception e) {
            log.warn("Error saving to cache: {}", e.getMessage());
            // Don't fail the request if caching fails
        }
    }

    /**
     * Generate cache key from city name
     */
    private String getCacheKey(String city) {
        return "weather:" + city.toLowerCase().trim().replaceAll("\\s+", "_");
    }

    /**
     * Clear cache for a specific city
     */
    public void clearCache(String city) {
        String cacheKey = getCacheKey(city);
        try {
            redisTemplate.delete(cacheKey);
            log.info("Cleared cache for city: {}", city);
        } catch (Exception e) {
            log.warn("Error clearing cache: {}", e.getMessage());
        }
    }

    /**
     * Clear all weather cache
     */
    public void clearAllCache() {
        try {
            var keys = redisTemplate.keys("weather:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared {} cache entries", keys.size());
            }
        } catch (Exception e) {
            log.warn("Error clearing all cache: {}", e.getMessage());
        }
    }
}
