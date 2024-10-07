package city.averagetemperatures.application.service;

import city.averagetemperatures.application.calculation.AverageTemperatureCalculation;
import city.averagetemperatures.application.dao.DataReaderDao;
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
    private DataReaderDao dataReaderDao;

    public TemperatureService(DataReaderDao dataReaderDao) {
        this.dataReaderDao = dataReaderDao;
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
        if (this.isExternalDataSourceReadingNecessary(cityName)) {
            cityAverageTemperatureDTOS = this.processData(cityName);
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
     * If city temperature data is not cached, it needs to be read from external data source.
     * If external data source modification date is newer than cached data, it will be refreshed from external one
     */
    private boolean isExternalDataSourceReadingNecessary(String cityName) {
        boolean reading = false;
        boolean externalDataSourceAvailable = this.dataReaderDao.externalDataSourceAvailable();
        boolean cityDataCached = this.isCityAverageTemperatureCached(cityName);
        if (!externalDataSourceAvailable && !cityDataCached) {
            throw new CityNotFoundException("city not found: " + cityName);
        }
        if (externalDataSourceAvailable && !cityDataCached) {
            return true;
        }
        if (!externalDataSourceAvailable && cityDataCached) {
            return false;
        }
        LocalDateTime externalDataLastModifiedTime = this.dataReaderDao.getExternalDataLastModifiedTime();
        Optional<CityCachingDate> cityCachingDateOptional = this.cityCachingDateRepository.findById(cityName);
        if (cityCachingDateOptional.isPresent()) {
            LocalDateTime cachingDate = cityCachingDateOptional.get().getCachingDate();
            if (externalDataLastModifiedTime.isAfter(cachingDate)) {
                return true;
            }
        }
        return reading;
    }

    private boolean isCityAverageTemperatureCached(String cityName) {
        return this.cityAverageTemperatureRepository.existsCityAverageTemperatureByCityName(cityName);
    }

    /**
     * Method reads data from data source, calculates average year values, prepares list for response
    */
    private List<CityAverageTemperatureDTO> processData(String cityName) {
        List<CityAverageTemperatureDTO> cityAverageTemperatureDTOS = new ArrayList<>();
        AverageTemperatureCalculation averageTemperatureCalculation = this.dataReaderDao.readCityData(cityName);
        cityAverageTemperatureDTOS = averageTemperatureCalculation.calculateAverageTemperature();
        if (!cityAverageTemperatureDTOS.isEmpty()) {
            this.deleteAllCityAverageTemperatures(cityName);
            for (CityAverageTemperatureDTO cityAverageTemperatureDTO : cityAverageTemperatureDTOS) {
                this.saveCityAverageTemperature(new CityAverageTemperature(cityAverageTemperatureDTO.getCityName(),
                   Short.parseShort(cityAverageTemperatureDTO.getYear()), cityAverageTemperatureDTO.getAverageTemperature()));
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
