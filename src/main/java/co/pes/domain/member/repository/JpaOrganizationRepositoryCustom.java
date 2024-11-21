package co.pes.domain.member.repository;

import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.organizationchart.model.OrganizationChart;
import java.util.List;
import java.util.Optional;

public interface JpaOrganizationRepositoryCustom {
    List<Long> getIdListByUserId(String userId);

    boolean existsDescendantOrgByAncestorOrgId(Long ancestorOrgId);

    List<Long> getIdListByAncestorOrgId(Long ancestorOrgId, List<Long> checkTeamIdList);

    Optional<OrganizationEntity> searchOfficerTeamByTeamId(Long teamId);

    List<Long> getSubTeamIdList(Long teamId);

    List<String> searchChargeTeamTitlesByTeamIds(List<Long> chargeTeamIds);

    List<OrganizationChart> searchOrganizationChartInfo();

    List<Long> searchChargeTeamIdsByUserId(String userId);

    List<OrganizationChart> searchOrganizationChartInfoByTeamId(List<Long> teamIdList);
}
