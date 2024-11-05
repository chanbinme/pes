package co.pes.domain.evaluation.repository;

import co.pes.domain.evaluation.entity.TaskEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEvaluationRepository extends JpaRepository<TaskEvaluationEntity, Long>, JpaEvaluationRepositoryCustom {

    boolean existsByIdTaskId(Long taskId);
}
