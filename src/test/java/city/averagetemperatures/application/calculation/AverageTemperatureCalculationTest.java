package city.averagetemperatures.application.calculation;

import city.averagetemperatures.application.dto.CityAverageTemperatureDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AverageTemperatureCalculationTest {

    @Test
    void checkCalculatedAverageTemperature() {
        AverageTemperatureCalculation averageTemperatureCalculation = new AverageTemperatureCalculation("Wrocław");
        averageTemperatureCalculation.setFieldDelimiter(";");
        List<String> cityTemperatures = List.of("Wrocław;2023-06-10 11:35:54.217;29.98",
                "Wrocław;2023-06-11 09:00:00.268;36.87");
        for(String cityTemperature : cityTemperatures) {
            averageTemperatureCalculation.mergeTemperatureData(cityTemperature);
        }
        List<CityAverageTemperatureDTO> cityAverageTemperatureDTOS = averageTemperatureCalculation.calculateAverageTemperature();
        Assertions.assertEquals(33.4, cityAverageTemperatureDTOS.get(0).getAverageTemperature());
    }
}