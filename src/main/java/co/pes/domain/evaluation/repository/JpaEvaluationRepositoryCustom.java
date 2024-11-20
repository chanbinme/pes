package co.pes.domain.evaluation.repository;

import co.pes.domain.evaluation.entity.TaskEvaluationEntityId;
import co.pes.domain.evaluation.model.TaskEvaluation;
import java.util.List;

public interface JpaEvaluationRepositoryCustom {

    List<TaskEvaluation> searchTaskEvaluationInfoListByTeamIdList(String year, List<Long> teamIdList);

    List<TaskEvaluation> searchTaskEvaluationInfoListByTeamId(String year, Long chargeTeamId);

    boolean containsFinalSaveEvaluation(List<Long> mappedTaskIdList);

    void removeAllByIdList(List<TaskEvaluationEntityId> taskEvaluationEntityIdList);
}
