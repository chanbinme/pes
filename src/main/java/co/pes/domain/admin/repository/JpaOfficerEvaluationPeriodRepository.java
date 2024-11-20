package co.pes.domain.admin.repository;

import co.pes.domain.admin.entity.OfficerEvaluationPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOfficerEvaluationPeriodRepository extends JpaRepository<OfficerEvaluationPeriodEntity, Long>, JpaOfficerEvaluationPeriodRepositoryCustom {

}
