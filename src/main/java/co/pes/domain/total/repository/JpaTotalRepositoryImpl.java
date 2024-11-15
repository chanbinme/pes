package co.pes.domain.total.repository;


import co.pes.domain.member.entity.QOrganizationEntity;
import co.pes.domain.member.entity.QOrganizationHierarchyEntity;
import co.pes.domain.member.entity.QOrganizationLeadEntity;
import co.pes.domain.member.entity.QUsersEntity;
import co.pes.domain.task.entity.QTaskEntity;
import co.pes.domain.total.entity.QEvaluationTotalEntity;
import co.pes.domain.total.model.QTotalRanking;
import co.pes.domain.total.model.TotalRanking;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaTotalRepositoryImpl implements JpaTotalRepositoryCustom{

    private final JPAQueryFactory query;

    @Override
    public List<TotalRanking> getTotalByTeamIdList(String year, List<Long> teamIdList) {
        QEvaluationTotalEntity et = QEvaluationTotalEntity.evaluationTotalEntity;
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        QOrganizationEntity o = QOrganizationEntity.organizationEntity;
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;
        QUsersEntity u = QUsersEntity.usersEntity;

        return query.select(new QTotalRanking(
            et.id.as("evaluationTotalId"),
            et.year,
            et.organization.id.as("teamId"),
            et.teamTitle,
            o.title.as("divisionTitle"),
            new CaseBuilder()
                .when(u.positionGb.eq("2")).then("Manager")
                .when(u.positionGb.eq("1")).then("Officer")
                .when(u.positionGb.eq("0")).then("CEO")
                .otherwise("ERROR")
                .as("position"),
            u.name,
            et.totalPoint,
            et.ranking.coalesce("-").as("ranking"),
            et.note.coalesce("").as("note")))
            .from(et)
            .innerJoin(oh)
            .on(et.organization.id.eq(oh.descendantOrganization.id))
            .innerJoin(o)
            .on(oh.ancestorOrganization.id.eq(o.id))
            .innerJoin(ol)
            .on(et.organization.id.eq(ol.organization.id))
            .innerJoin(u)
            .on(ol.user.id.eq(u.id))
            .where(et.year.eq(year)
                .and(et.organization.id.in(teamIdList)))
            .fetch();
    }

    @Override
    public List<TotalRanking> getOfficerTotalByTeamIdList(String year, List<Long> teamIdList) {
        QEvaluationTotalEntity et = QEvaluationTotalEntity.evaluationTotalEntity;
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;
        QUsersEntity u = QUsersEntity.usersEntity;

        return query.select(new QTotalRanking(
                et.id.as("evaluationTotalId"),
                et.year,
                et.organization.id.as("teamId"),
                et.teamTitle,
                Expressions.as(Expressions.constant(""), "divisionTitle"),
                new CaseBuilder()
                    .when(u.positionGb.eq("2")).then("Manager")
                    .when(u.positionGb.eq("1")).then("Officer")
                    .when(u.positionGb.eq("0")).then("CEO")
                    .otherwise("ERROR")
                    .as("position"),
                u.name,
                et.totalPoint,
                et.ranking.coalesce("-").as("ranking"),
                et.note.coalesce("").as("note"))).distinct()
            .from(et)
            .innerJoin(oh)
            .on(et.organization.id.eq(oh.ancestorOrganization.id))
            .innerJoin(ol)
            .on(et.organization.id.eq(ol.organization.id))
            .innerJoin(u)
            .on(ol.user.id.eq(u.id))
            .where(et.year.eq(year)
                .and(oh.descendantOrganization.id.in(teamIdList)))
            .fetch();
    }

    @Override
    public Double sumSubTeamTotalPoint(List<Long> teamIdList, String year) {
        QEvaluationTotalEntity et = QEvaluationTotalEntity.evaluationTotalEntity;
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;

        return query.select(et.totalPoint.sum())
            .from(et)
            .innerJoin(oh)
            .on(et.organization.id.eq(oh.descendantOrganization.id))
            .where(et.year.eq(year)
                .and(et.organization.id.in(teamIdList)))
            .fetchFirst();
    }

    @Override
    public boolean checkAllEvaluationsComplete(String year) {
        QEvaluationTotalEntity et = QEvaluationTotalEntity.evaluationTotalEntity;

        return query.select(et.id)
            .from(et)
            .where(et.year.eq(year)
                .and(et.ranking.isNull()))
            .fetchFirst() == null;
    }

    @Override
    public List<String> getEvaluationYearList() {
        QTaskEntity t = QTaskEntity.taskEntity;
        return query.select(t.year).distinct()
            .from(t)
            .orderBy(t.year.desc())
            .fetch();
    }
}
