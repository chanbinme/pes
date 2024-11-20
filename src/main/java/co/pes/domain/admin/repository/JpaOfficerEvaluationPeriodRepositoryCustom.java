package co.pes.domain.admin.repository;

import co.pes.domain.admin.entity.OfficerEvaluationPeriodEntity;
import java.util.Optional;

public interface JpaOfficerEvaluationPeriodRepositoryCustom {

    Optional<OfficerEvaluationPeriodEntity> findByRecentPeriod();
}
