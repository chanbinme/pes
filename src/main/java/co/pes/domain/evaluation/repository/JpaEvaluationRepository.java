package co.pes.domain.evaluation.repository;

import co.pes.domain.evaluation.entity.TaskEvaluationEntity;
import co.pes.domain.evaluation.entity.TaskEvaluationEntityId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaEvaluationRepository extends JpaRepository<TaskEvaluationEntity, TaskEvaluationEntityId>, JpaEvaluationRepositoryCustom {

    boolean existsByIdTaskId(Long taskId);

    boolean existsByIdTaskIdIn(List<Long> taskIdList);
}
