package city.averagetemperatures.application.dao;


import city.averagetemperatures.application.calculation.AverageTemperatureCalculation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Repository
public class CSVDataReaderDao {

    @Value("${csv.file.name}")
    private String fileName;
    @Value("${csv.field.delimiter}")
    private String fieldDelimiter;

    public CSVDataReaderDao() {

    }

    public AverageTemperatureCalculation readCityData(String pCityName) {
        Path path = this.getCsvFilePath();
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String line;
            AverageTemperatureCalculation averageTemperatureCalculation = new AverageTemperatureCalculation(pCityName);
            averageTemperatureCalculation.setFieldDelimiter(fieldDelimiter);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith(pCityName)) {
                    averageTemperatureCalculation.mergeTemperatureData(line);
                }
            }
            return averageTemperatureCalculation;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public LocalDateTime getFileLastModifiedTime() {
        Path path = this.getCsvFilePath();
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            return LocalDateTime.ofInstant(lastModifiedTime.toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getCsvFilePath() {
        return Path.of(Paths.get("").toAbsolutePath().toString(), "\\", fileName);
    }
}
