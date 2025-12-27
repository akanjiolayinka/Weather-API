package com.weatherapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String resolvedAddress;
    private String address;
    private String timezone;
    private Double latitude;
    private Double longitude;
    
    @JsonProperty("days")
    private List<DayWeather> days;
    
    private CurrentConditions currentConditions;
    
    // Metadata
    private String source;
    private Long cachedAt;
    
    public WeatherResponse() {}
    
    // Getters and Setters
    public String getResolvedAddress() { return resolvedAddress; }
    public void setResolvedAddress(String resolvedAddress) { this.resolvedAddress = resolvedAddress; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public List<DayWeather> getDays() { return days; }
    public void setDays(List<DayWeather> days) { this.days = days; }
    
    public CurrentConditions getCurrentConditions() { return currentConditions; }
    public void setCurrentConditions(CurrentConditions currentConditions) { this.currentConditions = currentConditions; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public Long getCachedAt() { return cachedAt; }
    public void setCachedAt(Long cachedAt) { this.cachedAt = cachedAt; }
}
