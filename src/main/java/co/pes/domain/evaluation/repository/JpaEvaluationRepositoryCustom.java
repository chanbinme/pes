package co.pes.domain.evaluation.repository;

import co.pes.domain.evaluation.model.TaskEvaluation;
import java.util.List;

public interface JpaEvaluationRepositoryCustom {

    List<TaskEvaluation> getTaskEvaluationInfoListByTeamIdList(String year, List<Long> teamIdList);

    List<TaskEvaluation> getTaskEvaluationInfoListByTeamId(String year, Long chargeTeamId);

}
