package city.averagetemperatures.application.repository;

import city.averagetemperatures.application.entity.CityAverageTemperature;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityAverageTemperatureRepository extends JpaRepository<CityAverageTemperature, Long> {

    @Transactional
    void deleteAllByCityName(String cityName);

    @Transactional
    boolean existsCityAverageTemperatureByCityName(String cityName);

    @Transactional
    List<CityAverageTemperature> findAllByCityName(String cityName);
}
