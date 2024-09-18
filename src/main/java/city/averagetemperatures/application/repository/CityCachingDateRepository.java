package city.averagetemperatures.application.repository;

import city.averagetemperatures.application.entity.CityCachingDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityCachingDateRepository extends JpaRepository<CityCachingDate, String> {
}
