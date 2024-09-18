package city.averagetemperatures.application.controller;

import city.averagetemperatures.application.dto.CityAverageTemperatureDTO;
import city.averagetemperatures.application.service.TemperatureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CityTemperatureController {

    private TemperatureService temperatureService;

    public CityTemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @GetMapping("/avgtemperatures/{cityName}")
    public List<CityAverageTemperatureDTO> getCityTemperatures(@PathVariable String cityName) {
        return this.temperatureService.findAverageTemperaturesByCity(cityName);
    }

}
