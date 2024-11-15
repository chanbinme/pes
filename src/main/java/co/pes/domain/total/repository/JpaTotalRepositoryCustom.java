package co.pes.domain.total.repository;

import co.pes.domain.total.model.TotalRanking;
import java.util.List;

public interface JpaTotalRepositoryCustom {
    List<TotalRanking> getTotalByTeamIdList(String year, List<Long> teamIdList);

    List<TotalRanking> getOfficerTotalByTeamIdList(String year, List<Long> teamIdList);

    Double sumSubTeamTotalPoint(List<Long> teamIdList, String year);

    boolean checkAllEvaluationsComplete(String year);

    List<String> getEvaluationYearList();
}
