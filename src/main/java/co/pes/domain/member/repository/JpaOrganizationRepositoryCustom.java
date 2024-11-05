package co.pes.domain.member.repository;

import java.util.List;
import org.springframework.lang.Nullable;

public interface JpaOrganizationRepositoryCustom {
    List<Long> getIdListByUserId(String userId);

    boolean existsDescendantOrgByAncestorOrgId(Long ancestorOrgId);

    List<Long> getIdListByAncestorOrgId(Long ancestorOrgId, List<Long> checkTeamIdList);
}
