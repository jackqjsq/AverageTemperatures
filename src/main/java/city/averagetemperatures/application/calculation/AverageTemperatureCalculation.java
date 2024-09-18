package city.averagetemperatures.application.calculation;

import city.averagetemperatures.application.dto.CityAverageTemperatureDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AverageTemperatureCalculation {

    private Map<Integer, Double> temperatureSumPerYear = new TreeMap<>();
    private Map<Integer, Integer> counterPerYear = new TreeMap<>();
    private List<CityAverageTemperatureDTO> cityAverageTemperatureDTOS = new ArrayList<>();

    private String fieldDelimiter;
    private String cityName;

    public AverageTemperatureCalculation(String cityName) {
        this.cityName = cityName;
    }

    public void mergeTemperatureData(String fileLine) {
        String[] fields = fileLine.split(fieldDelimiter);
        String cityName = fields[0];
        LocalDateTime logDate = LocalDateTime.parse(fields[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        double temperature = Double.parseDouble(fields[2]);
        int year = logDate.getYear();
        this.temperatureSumPerYear.merge(year, temperature, (v1, v2) -> v1 + temperature);
        this.counterPerYear.merge(year, 1, (v1, v2) -> v1 + 1);
    }

    public List<CityAverageTemperatureDTO> calculateAverageTemperature() {
        for (Integer year : this.temperatureSumPerYear.keySet()) {
            double temperatureSum = this.temperatureSumPerYear.get(year);
            int counter = this.counterPerYear.get(year);
            double truncated = Math.floor((temperatureSum/counter)* 10) / 10;
            this.cityAverageTemperatureDTOS.add(new CityAverageTemperatureDTO(this.cityName, year, truncated));
        }
        return this.cityAverageTemperatureDTOS;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }
}
