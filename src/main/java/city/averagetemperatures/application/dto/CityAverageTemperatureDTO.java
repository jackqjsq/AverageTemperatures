package city.averagetemperatures.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CityAverageTemperatureDTO {

    @JsonIgnore
    private String cityName;
    private String year;
    private double averageTemperature;

    public CityAverageTemperatureDTO() {
    }

    public CityAverageTemperatureDTO(String cityName, String year, double averageTemperature) {
        this.cityName = cityName;
        this.year = year;
        this.averageTemperature = averageTemperature;
    }

    public String getCityName() {
        return cityName;
    }

    public String getYear() {
        return year;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }
}
