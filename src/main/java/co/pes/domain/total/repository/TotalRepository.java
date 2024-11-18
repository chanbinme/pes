package co.pes.domain.total.repository;

import co.pes.domain.task.model.Mapping;
import co.pes.domain.total.model.EndYear;
import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.model.Total;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TotalRepository {

    int countTotal(Total total);

    void saveTotal(Total total);

    void updateTotal(Total total);

    int countMappingTeamByTeamId(@Param(value = "teamId") Long teamId);

    double sumTeamTotalPoint(Total officerTotal);

    List<TotalRanking> getTotalByTeamIdList(@Param("year") String year, @Param(value = "teamIdList") List<Long> teamIdList);

    List<TotalRanking> getOfficerTotalByTeamIdList(@Param("year") String year, @Param(value = "teamIdList") List<Long> teamIdList);

    void updateTotalRanking(Total total);

    int countEndYear(@Param(value = "year") String year);

    void postEndYear(EndYear endYear);

    int checkAllEvaluationsComplete(@Param(value = "year") String year);

    OfficerTeamInfo findOfficerTeamInfoByTeamId(@Param("teamId") Long teamId);

    int countTotalByMapping(@Param("chargeTeamId") Long chargeTeamId);

    List<String> getEvaluationYearList();

    void deleteEndYear(@Param(value = "year") String year);
}
