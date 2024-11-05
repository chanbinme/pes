package co.pes.domain.member.repository;

import co.pes.domain.member.entity.QOrganizationHierarchyEntity;
import co.pes.domain.member.entity.QOrgazniationLeadEntity;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaOrganizationRepositoryImpl implements JpaOrganizationRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Long> getIdListByUserId(String userId) {
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        QOrgazniationLeadEntity ol = QOrgazniationLeadEntity.orgazniationLeadEntity;

        JPAQuery<Long> subquery = query.select(ol.organization.id)
            .from(ol)
            .where(ol.user.id.eq(userId));
        List<Long> firstLevelDescendantOrganizationIdList = query.select(oh.descendantOrganization.id)
            .from(oh)
            .where(oh.ancestorOrganization.id.in(subquery))
            .fetch();
        List<Long> allDescendantOrganizationList = query.select(oh.descendantOrganization.id)
            .from(oh)
            .where(oh.ancestorOrganization.id.in(firstLevelDescendantOrganizationIdList))
            .fetch();
        allDescendantOrganizationList.addAll(firstLevelDescendantOrganizationIdList);

        return allDescendantOrganizationList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public boolean existsDescendantOrgByAncestorOrgId(Long ancestorOrgId) {
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        return query.select(oh)
            .from(oh)
            .where(oh.ancestorOrganization.id.eq(ancestorOrgId))
            .fetchFirst() != null;
    }
}
