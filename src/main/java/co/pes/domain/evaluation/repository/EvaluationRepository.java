package co.pes.domain.evaluation.repository;

import co.pes.domain.evaluation.model.TaskEvaluation;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EvaluationRepository {

    List<TaskEvaluation> getTaskEvaluationInfoList(@Param("year") String year, @Param("chargeTeamId") Long chargeTeamId);

    void saveTaskEvaluation(TaskEvaluation taskEvaluation);

    void updateTaskEvaluation(TaskEvaluation taskEvaluation);

    int countTaskEvaluation(TaskEvaluation taskEvaluation);

    String findEvaluationState(TaskEvaluation taskEvaluation);

    void deleteTaskEvaluation(TaskEvaluation taskEvaluation);

    int countDescendantOrgByTeamId(@Param("chargeTeamId") Long chargeTeamId);

    List<TaskEvaluation> getTaskEvaluationInfoListByTeamIdList(@Param("year") String year, @Param("teamIdList") List<Long> teamIdList);

    List<Long> getDescendantOrgIdList(@Param("chargeTeamId") Long chargeTeamId);

    int countTotal(@Param("year") String year, @Param("chargeTeamId") Long chargeTeamId);

    List<Long> getTeamListByUserId(@Param("userId") String id);

    List<Long> getLastDescendantOrgIdList(@Param("chargeTeamId") Long chargeTeamId, @Param("checkTeamIdList") List<Long> checkTeamIdList);
}
