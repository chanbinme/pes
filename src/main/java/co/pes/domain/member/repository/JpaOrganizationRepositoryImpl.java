package co.pes.domain.member.repository;

import static co.pes.domain.member.entity.QOrganizationHierarchyEntity.organizationHierarchyEntity;

import co.pes.domain.member.entity.QOrganizationHierarchyEntity;
import co.pes.domain.member.entity.QOrganizationLeadEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
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
        QOrganizationHierarchyEntity oh = organizationHierarchyEntity;
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;

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
        QOrganizationHierarchyEntity oh = organizationHierarchyEntity;
        return query.select(oh)
            .from(oh)
            .where(oh.ancestorOrganization.id.eq(ancestorOrgId))
            .fetchFirst() != null;
    }

    @Override
    public List<Long> getIdListByAncestorOrgId(Long ancestorOrgId, List<Long> checkTeamIdList) {
        QOrganizationHierarchyEntity oh = organizationHierarchyEntity;
        return query.select(oh.descendantOrganization.id)
            .from(oh)
            .where(oh.ancestorOrganization.id.eq(ancestorOrgId)
                .and(inCheckTeamIdList(checkTeamIdList)))
            .fetch();
    }

    private BooleanExpression inCheckTeamIdList(List<Long> checkTeamIdList) {
        return checkTeamIdList == null || checkTeamIdList.isEmpty() ? null : organizationHierarchyEntity.descendantOrganization.id.in(checkTeamIdList);
    }
}
