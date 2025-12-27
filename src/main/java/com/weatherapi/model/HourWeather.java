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
public class HourWeather implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String datetime;
    private Double temp;
    private Double humidity;
    private Double precip;
    
    @JsonProperty("precipprob")
    private Double precipProb;
    
    private Double windspeed;
    private Double pressure;
    private Double cloudcover;
    private Double visibility;
    
    @JsonProperty("uvindex")
    private Double uvIndex;
    
    private String conditions;
    private String icon;
}
