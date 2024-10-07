package city.averagetemperatures.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class CityAverageTemperature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String cityName;
    private short year;
    private double averageTemperature;

    public CityAverageTemperature() {
    }

    public CityAverageTemperature(String cityName, short year, double averageTemperature) {
        this.cityName = cityName;
        this.year = year;
        this.averageTemperature = averageTemperature;
    }

    public long getId() {
       return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public short getYear() {
        return year;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    @Override
    public String toString() {
        return "CityAverageTemperature{" +
                "cityName='" + cityName + '\'' +
                ", year=" + year +
                ", averageTemperature=" + averageTemperature +
                '}';
    }
}
