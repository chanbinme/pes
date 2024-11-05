package co.pes.domain.member.repository;

import java.util.List;

public interface JpaOrganizationRepositoryCustom {
    List<Long> getIdListByUserId(String userId);

    boolean existsDescendantOrgByAncestorOrgId(Long ancestorOrgId);
}
