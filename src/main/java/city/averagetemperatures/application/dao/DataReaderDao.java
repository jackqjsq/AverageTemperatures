package city.averagetemperatures.application.dao;

import city.averagetemperatures.application.calculation.AverageTemperatureCalculation;

import java.time.LocalDateTime;

public interface DataReaderDao {
    AverageTemperatureCalculation readCityData(String pCityName);

    LocalDateTime getExternalDataLastModifiedTime();

    boolean externalDataSourceAvailable();
}
