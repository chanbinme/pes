package co.pes.domain.total.repository;

import co.pes.domain.total.entity.EndYearEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEndYearRepository extends JpaRepository<EndYearEntity, String> {
    boolean existsByYear(String year);

    void deleteByYear(String year);
}
