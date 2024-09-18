package city.averagetemperatures.application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class CityCachingDate {

    @Id
    private String cityName;
    private LocalDateTime cachingDate;

    public CityCachingDate() {
    }

    public CityCachingDate(String cityName, LocalDateTime cachingDate) {
        this.cityName = cityName;
        this.cachingDate = cachingDate;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public LocalDateTime getCachingDate() {
        return cachingDate;
    }

    public void setCachingDate(LocalDateTime cachingDate) {
        this.cachingDate = cachingDate;
    }

    @Override
    public String toString() {
        return "CityCachingDate{" +
                "cityName='" + cityName + '\'' +
                ", cachingDate=" + cachingDate +
                '}';
    }
}
