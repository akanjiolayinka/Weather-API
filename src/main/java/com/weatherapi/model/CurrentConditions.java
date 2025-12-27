package com.weatherapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentConditions implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String datetime;
    private Double temp;
    
    @JsonProperty("feelslike")
    private Double feelsLike;
    
    private Double humidity;
    private Double precip;
    
    @JsonProperty("precipprob")
    private Double precipProb;
    
    private Double windspeed;
    
    @JsonProperty("winddir")
    private Double windDir;
    
    private Double pressure;
    private Double cloudcover;
    private Double visibility;
    
    @JsonProperty("uvindex")
    private Double uvIndex;
    
    private String conditions;
    private String icon;
    private String sunrise;
    private String sunset;
}
