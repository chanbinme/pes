package co.pes.domain.total.repository;

import co.pes.domain.total.entity.EvaluationTotalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTotalRepository extends JpaRepository<EvaluationTotalEntity, Long>, JpaTotalRepositoryCustom {
    boolean existsByYearAndOrganizationId(String year, Long organizationId);
}
