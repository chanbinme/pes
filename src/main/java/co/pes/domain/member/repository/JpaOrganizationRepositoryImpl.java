package co.pes.domain.member.repository;

import static co.pes.domain.member.entity.QOrganizationHierarchyEntity.organizationHierarchyEntity;

import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.entity.QOrganizationEntity;
import co.pes.domain.member.entity.QOrganizationHierarchyEntity;
import co.pes.domain.member.entity.QOrganizationLeadEntity;
import co.pes.domain.organizationchart.model.OrganizationChart;
import co.pes.domain.organizationchart.model.QOrganizationChart;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<OrganizationEntity> searchOfficerTeamByTeamId(Long teamId) {
        QOrganizationEntity o = QOrganizationEntity.organizationEntity;
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        return Optional.ofNullable(query
            .select(o)
            .from(o)
            .leftJoin(ol)
            .on(o.id.eq(ol.organization.id))
            .innerJoin(oh)
            .on(o.id.eq(oh.ancestorOrganization.id))
            .where(oh.descendantOrganization.id.eq(teamId)).fetchOne());
    }

    @Override
    public List<Long> getSubTeamIdList(Long teamId) {
        QOrganizationHierarchyEntity oh = organizationHierarchyEntity;
        return query.select(oh.descendantOrganization.id)
            .from(oh)
            .where(oh.ancestorOrganization.id.eq(teamId))
            .fetch();
    }

    @Override
    public List<String> searchChargeTeamTitlesByTeamIds(List<Long> chargeTeamIds) {
        QOrganizationEntity o = QOrganizationEntity.organizationEntity;
        return query
            .select(o.title)
            .from(o)
            .where(o.id.in(chargeTeamIds))
            .fetch();
    }

    @Override
    public List<OrganizationChart> searchOrganizationChartInfo() {
        QOrganizationEntity o = QOrganizationEntity.organizationEntity;
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        return query
            .select(new QOrganizationChart(
                oh.descendantOrganization.id.stringValue(),
                new CaseBuilder()
                    .when(oh.ancestorOrganization.id.eq(oh.descendantOrganization.id)).then("#")
                    .otherwise(oh.ancestorOrganization.id.stringValue()).as("parent"),
                o.title
            ))
            .from(o)
            .leftJoin(oh)
            .on(o.id.eq(oh.descendantOrganization.id))
            .orderBy(o.title.asc())
            .fetch();
    }

    @Override
    public List<Long> searchChargeTeamIdsByUserId(String userId) {
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;
        return query
            .select(ol.organization.id)
            .from(ol)
            .where(ol.user.id.eq(userId))
            .fetch();
    }

    @Override
    public List<OrganizationChart> searchOrganizationChartInfoByTeamId(List<Long> teamIdList) {
        QOrganizationEntity o = QOrganizationEntity.organizationEntity;
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;

        return query
            .select(new QOrganizationChart(
                oh.descendantOrganization.id.stringValue(),
                new CaseBuilder()
                    .when(oh.ancestorOrganization.id.eq(oh.descendantOrganization.id)).then("#")
                    .otherwise(oh.ancestorOrganization.id.stringValue()).as("parent"),
                o.title
            ))
            .from(o)
            .leftJoin(oh)
            .on(o.id.eq(oh.descendantOrganization.id))
            .where(oh.descendantOrganization.id.in(teamIdList)
                .or(oh.ancestorOrganization.id.in(teamIdList))
                .or(oh.ancestorOrganization.id.eq(oh.descendantOrganization.id))
                .or(oh.descendantOrganization.id.in(
                    JPAExpressions
                        .select(oh.ancestorOrganization.id.as("id"))
                        .from(oh)
                        .where(oh.descendantOrganization.id.in(teamIdList)
                )))
            )
            .orderBy(o.title.asc())
            .fetch();
    }

    private BooleanExpression inCheckTeamIdList(List<Long> checkTeamIdList) {
        return checkTeamIdList == null || checkTeamIdList.isEmpty() ? null : organizationHierarchyEntity.descendantOrganization.id.in(checkTeamIdList);
    }
}
