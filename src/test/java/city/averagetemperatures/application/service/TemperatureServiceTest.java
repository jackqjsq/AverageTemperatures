package city.averagetemperatures.application.service;

import city.averagetemperatures.application.calculation.AverageTemperatureCalculation;
import city.averagetemperatures.application.calculation.AverageTemperatureCalculationImpl;
import city.averagetemperatures.application.calculation.AverageTemperatureCalculationTest;
import city.averagetemperatures.application.dao.DataReaderDao;
import city.averagetemperatures.application.entity.CityCachingDate;
import city.averagetemperatures.application.exception.CityNotFoundException;
import city.averagetemperatures.application.repository.CityAverageTemperatureRepository;
import city.averagetemperatures.application.repository.CityCachingDateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class TemperatureServiceTest {

    @Mock
    private DataReaderDao dataReaderDaoMock;

    @Mock
    private CityAverageTemperatureRepository cityAverageTemperatureRepositoryMock;

    @Mock
    private CityCachingDateRepository cityCachingDateRepositoryMock;

    @InjectMocks
    private TemperatureService temperatureService;

    @Test
    void shouldThrowCityNotFoundExceptionWhenNeitherExternalDataNorCache() {
        when(dataReaderDaoMock.externalDataSourceAvailable()).thenReturn(false);
        when(cityAverageTemperatureRepositoryMock.existsCityAverageTemperatureByCityName(anyString()))
                .thenReturn(false);
        assertThrows(CityNotFoundException.class, () -> {
            temperatureService.findAverageTemperaturesByCity("City");
        });
    }

    @Test
    void shouldBeEqualSizeOfExternalDataWhenExternalDataPresentAndNoCache() {
        String cityName = "testCity";
        when(dataReaderDaoMock.externalDataSourceAvailable()).thenReturn(true);
        when(cityAverageTemperatureRepositoryMock.existsCityAverageTemperatureByCityName(anyString()))
                .thenReturn(false);
        when(cityCachingDateRepositoryMock.save(any(CityCachingDate.class))).thenReturn(new CityCachingDate(cityName, LocalDateTime.now()));
        List<String> cityTemperatures = AverageTemperatureCalculationTest.getDataSet(cityName);
        AverageTemperatureCalculation averageTemperatureCalculation = AverageTemperatureCalculationTest.prepareCalculationData(cityName, cityTemperatures);
        when(dataReaderDaoMock.readCityData(cityName)).thenReturn(averageTemperatureCalculation);
        assertEquals(1, temperatureService.findAverageTemperaturesByCity(cityName).size());
    }


}