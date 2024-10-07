package city.averagetemperatures.application.calculation;

import city.averagetemperatures.application.dto.CityAverageTemperatureDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AverageTemperatureCalculationTest {

    @Test
    void checkAverageTemperatureCalculation() {
        String cityName = "testCity";
        List<String> cityTemperatures = getDataSet(cityName);
        AverageTemperatureCalculation averageTemperatureCalculation = AverageTemperatureCalculationTest.prepareCalculationData(cityName, cityTemperatures);
        List<CityAverageTemperatureDTO> cityAverageTemperatureDTOS = averageTemperatureCalculation.calculateAverageTemperature();
        Assertions.assertEquals(33.4, cityAverageTemperatureDTOS.get(0).getAverageTemperature());
    }

    public static List<String> getDataSet(String cityName) {
        return List.of(cityName + ";2024-08-10 11:35:54.217;29.98",
                cityName + ";2024-08-11 09:00:00.268;36.87");
    }

    public static AverageTemperatureCalculation prepareCalculationData(String cityName, List<String> cityTemperatures) {
        AverageTemperatureCalculation averageTemperatureCalculation = new AverageTemperatureCalculationImpl(cityName);
        averageTemperatureCalculation.setFieldDelimiter(";");
        for(String cityTemperature : cityTemperatures) {
            averageTemperatureCalculation.mergeTemperatureData(cityTemperature);
        }
        return averageTemperatureCalculation;
    }


}