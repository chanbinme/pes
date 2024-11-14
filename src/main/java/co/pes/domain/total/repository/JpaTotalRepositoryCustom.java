package co.pes.domain.total.repository;

import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.TotalRanking;
import java.util.List;
import java.util.Optional;

public interface JpaTotalRepositoryCustom {
    List<TotalRanking> getTotalByTeamIdList(String year, List<Long> teamIdList);
    List<TotalRanking> getOfficerTotalByTeamIdList(String year, List<Long> teamIdList);
    Optional<OfficerTeamInfo> findOfficerTeamInfoByTeamId(Long teamId);
}
