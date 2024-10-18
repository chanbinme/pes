package co.pes.domain.evaluation.repository;

import co.pes.domain.evaluation.model.JobEvaluation;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EvaluationRepository {

    List<JobEvaluation> getJobEvaluationInfoList(@Param("year") String year, @Param("chargeTeamId") Long chargeTeamId);

    void saveJobEvaluation(JobEvaluation jobEvaluationList);

    void updateJobEvaluation(JobEvaluation jobEvaluation);

    int countJobEvaluation(JobEvaluation jobEvaluation);

    String findEvaluationState(JobEvaluation jobEvaluation);

    void deleteJobEvaluation(JobEvaluation jobEvaluation);

    int countDescendantOrgByTeamId(@Param("chargeTeamId") Long chargeTeamId);

    List<JobEvaluation> getJobEvaluationInfoListByTeamIdList(@Param("year") String year, @Param("teamIdList") List<Long> teamIdList);

    List<Long> getDescendantOrgIdList(@Param("chargeTeamId") Long chargeTeamId);

    int countTotal(@Param("year") String year, @Param("chargeTeamId") Long chargeTeamId);

    List<Long> getTeamListByUserId(@Param("userId") String id);

    List<JobEvaluation> getJobEvaluationInfoListByCheckTeamIdList(@Param("year") String year, @Param("chargeTeamId") Long chargeTeamId, @Param("checkTeamIdList") List<Long> checkTeamIdList);

    List<Long> getLastDescendantOrgIdList(@Param("chargeTeamId") Long chargeTeamId, @Param("checkTeamIdList") List<Long> checkTeamIdList);
}
