package city.averagetemperatures.application.dao;


import city.averagetemperatures.application.calculation.AverageTemperatureCalculation;
import city.averagetemperatures.application.calculation.AverageTemperatureCalculationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Repository
public class CSVDataReaderDaoImpl implements DataReaderDao {

    private static final Logger log = LoggerFactory.getLogger(CSVDataReaderDaoImpl.class);
    @Value("${csv.file.name}")
    private String fileName;
    @Value("${csv.field.delimiter}")
    private String fieldDelimiter;

    public CSVDataReaderDaoImpl() {

    }

    @Override
    public AverageTemperatureCalculation readCityData(String pCityName) {
        Path path = this.getCsvFilePath();
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            AverageTemperatureCalculation averageTemperatureCalculation = new AverageTemperatureCalculationImpl(pCityName);
            averageTemperatureCalculation.setFieldDelimiter(fieldDelimiter);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith(pCityName)) {
                    averageTemperatureCalculation.mergeTemperatureData(line);
                }
            }
            return averageTemperatureCalculation;
        } catch (IOException e) {
            logFileReadingError();
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public LocalDateTime getExternalDataLastModifiedTime() {
        Path path = this.getCsvFilePath();
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            return LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            logFileReadingError();
            throw new UncheckedIOException(e);
        }
    }



    @Override
    public boolean externalDataSourceAvailable() {
        boolean exists = Files.exists(this.getCsvFilePath());
        if (!exists) {
            this.logFileReadingError();
        }
        return exists;
    }

    private void logFileReadingError() {
        if (log.isErrorEnabled()) {
            log.error("{}: CSV File reading error", Thread.currentThread().getName());
        }
    }

    private Path getCsvFilePath() {
        return Path.of(Paths.get("").toAbsolutePath().toString(), "\\", fileName);
    }
}
