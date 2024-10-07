package city.averagetemperatures.application.calculation;

import city.averagetemperatures.application.dto.CityAverageTemperatureDTO;

import java.util.List;

public interface AverageTemperatureCalculation {
    void mergeTemperatureData(String dataLine);

    List<CityAverageTemperatureDTO> calculateAverageTemperature();

    void setFieldDelimiter(String fieldDelimiter);
}
