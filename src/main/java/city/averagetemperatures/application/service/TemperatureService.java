package city.averagetemperatures.application.service;

import city.averagetemperatures.application.calculation.AverageTemperatureCalculation;
import city.averagetemperatures.application.dao.CSVDataReaderDao;
import city.averagetemperatures.application.dto.CityAverageTemperatureDTO;
import city.averagetemperatures.application.entity.CityAverageTemperature;
import city.averagetemperatures.application.entity.CityCachingDate;
import city.averagetemperatures.application.exception.CityNotFoundException;
import city.averagetemperatures.application.repository.CityAverageTemperatureRepository;
import city.averagetemperatures.application.repository.CityCachingDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TemperatureService {

    private CityCachingDateRepository cityCachingDateRepository;
    private CityAverageTemperatureRepository cityAverageTemperatureRepository;
    private CSVDataReaderDao csvDataReaderDao;

    public TemperatureService(CSVDataReaderDao csvDataReaderDao) {
        this.csvDataReaderDao = csvDataReaderDao;
    }

    public void saveCityCachingDate(CityCachingDate cityCachingDate) {
        this.cityCachingDateRepository.save(cityCachingDate);
    }

    public void saveCityAverageTemperature(CityAverageTemperature cityAverageTemperature) {
        this.cityAverageTemperatureRepository.save(cityAverageTemperature);
    }

    public void deleteAllCityAverageTemperatures(String cityName) {
        this.cityAverageTemperatureRepository.deleteAllByCityName(cityName);
    }

    /**
     * Method reads csv file or populates response with cached data
     */
    public List<CityAverageTemperatureDTO> findAverageTemperaturesByCity(String cityName) {
        List<CityAverageTemperatureDTO> cityAverageTemperatureDTOS = new ArrayList<>();
        if (this.isFileReadingNecessary(cityName)) {
            cityAverageTemperatureDTOS = this.processFileData(cityName);
        } else {
            List<CityAverageTemperature> cityAverageTemperatures = this.cityAverageTemperatureRepository.findAllByCityName(cityName);
            for (CityAverageTemperature cityAverageTemperature : cityAverageTemperatures)  {
                cityAverageTemperatureDTOS.add(new CityAverageTemperatureDTO(cityAverageTemperature.getCityName(),
                    String.valueOf(cityAverageTemperature.getYear()), cityAverageTemperature.getAverageTemperature()));
            }
        }
        if (cityAverageTemperatureDTOS.isEmpty()) {
            throw new CityNotFoundException("city not found: " + cityName);
        }
        return cityAverageTemperatureDTOS;
    }

    /**
     * If city temperature data is not cached, it needs to be read from csv file.
     * If csv file modification date is newer than cached data, it will be refreshed from file
     */
    private boolean isFileReadingNecessary(String cityName) {
        boolean reading = false;
        if (!this.isCityAverageTemperatureCached(cityName)) {
            return true;
        }
        LocalDateTime fileLastModifiedTime = this.csvDataReaderDao.getFileLastModifiedTime();
        Optional<CityCachingDate> cityCachingDateOptinal = this.cityCachingDateRepository.findById(cityName);
        if (cityCachingDateOptinal.isPresent()) {
            LocalDateTime cachingDate = cityCachingDateOptinal.get().getCachingDate();
            if (fileLastModifiedTime.isAfter(cachingDate)) {
                return true;
            }
        }
        return reading;
    }

    private boolean isCityAverageTemperatureCached(String cityName) {
        return this.cityAverageTemperatureRepository.existsCityAverageTemperatureByCityName(cityName);
    }

    /**
     * Method reads data from csv file, calculates average year values, prepares list for response
    */
    private List<CityAverageTemperatureDTO> processFileData(String cityName) {
        List<CityAverageTemperatureDTO> cityAverageTemperatureDTOS = new ArrayList<>();
        AverageTemperatureCalculation averageTemperatureCalculation = this.csvDataReaderDao.readCityData(cityName);
        cityAverageTemperatureDTOS = averageTemperatureCalculation.calculateAverageTemperature();
        if (!cityAverageTemperatureDTOS.isEmpty()) {
            this.deleteAllCityAverageTemperatures(cityName);
            for (CityAverageTemperatureDTO cityAverageTemperatureDTO : cityAverageTemperatureDTOS) {
                this.saveCityAverageTemperature(new CityAverageTemperature(cityAverageTemperatureDTO.getCityName(),
                   Integer.parseInt(cityAverageTemperatureDTO.getYear()), cityAverageTemperatureDTO.getAverageTemperature()));
            }
            this.saveCityCachingDate(new CityCachingDate(cityName, LocalDateTime.now()));
        }
        return cityAverageTemperatureDTOS;
    }

    @Autowired
    public void setCityCachingDateRepository(CityCachingDateRepository cityCachingDateRepository) {
        this.cityCachingDateRepository = cityCachingDateRepository;
    }

    @Autowired
    public void setCityAverageTemperatureRepository(CityAverageTemperatureRepository cityAverageTemperatureRepository) {
        this.cityAverageTemperatureRepository = cityAverageTemperatureRepository;
    }
}
