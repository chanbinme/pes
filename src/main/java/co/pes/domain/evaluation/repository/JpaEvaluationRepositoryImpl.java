package co.pes.domain.evaluation.repository;

import co.pes.domain.evaluation.entity.QTaskEvaluationEntity;
import co.pes.domain.evaluation.model.QTaskEvaluation;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.member.entity.QOrganizationHierarchyEntity;
import co.pes.domain.member.entity.QOrganizationLeadEntity;
import co.pes.domain.member.entity.QUsersEntity;
import co.pes.domain.task.entity.QTaskEntity;
import co.pes.domain.task.entity.QTaskOrganizationMappingEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.SQLTemplates;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaEvaluationRepositoryImpl implements JpaEvaluationRepositoryCustom {

    private final SQLTemplates sqlTemplates;
    private final EntityManager em;

    @Override
    public List<TaskEvaluation> getTaskEvaluationInfoListByTeamIdList(String year, List<Long> teamIdList) {
        JPASQLQuery<Object> sqlQuery = new JPASQLQuery<>(em, sqlTemplates);

        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;
        QOrganizationLeadEntity ol2 = QOrganizationLeadEntity.organizationLeadEntity;
        QUsersEntity u = QUsersEntity.usersEntity;
        QUsersEntity u2 = QUsersEntity.usersEntity;
        QTaskOrganizationMappingEntity tom = QTaskOrganizationMappingEntity.taskOrganizationMappingEntity;
        QTaskEntity t = QTaskEntity.taskEntity;
        QTaskEvaluationEntity te = QTaskEvaluationEntity.taskEvaluationEntity;
        StringPath subQueryPath = Expressions.stringPath("sub_query");

        JPQLQuery<Tuple> subquery = JPAExpressions.select(
            oh.descendantOrganization.id.as("chargeTeamId")
                , u.name.as("chargeTeam"),
                u2.name.as("chargeOfficer"))
            .from(ol)
            .innerJoin(u)
            .on(ol.user.id.eq(u.id))
            .innerJoin(oh)
            .on(ol.organization.id.eq(oh.descendantOrganization.id))
            .innerJoin(ol2)
            .on(oh.ancestorOrganization.id.eq(ol2.organization.id))
            .innerJoin(u2)
            .on(ol2.user.id.eq(u2.id))
            .where(oh.ancestorOrganization.id.in(teamIdList));

        return sqlQuery.select(
                new QTaskEvaluation(
                    te.id.task.id.as("taskId"),
                    Expressions.stringPath(subQueryPath, "chargeTeam"),
                    Expressions.stringPath(subQueryPath, "chargeOfficer"),
                    Expressions.numberPath(Long.class, subQueryPath, "chargeTeamId"),
                    t.projectTitle,
                    t.taskTitle,
                    t.taskState,
                    t.taskProgress,
                    te.weight.coalesce(0.0).as("weight"),
                    te.officerPoint.coalesce(0).as("officerPoint"),
                    te.ceoPoint.coalesce(0).as("ceoPoint"),
                    te.taskGb.coalesce("").as("taskGb"),
                    te.levelOfficer.coalesce("").as("levelOfficer"),
                    te.levelCeo.coalesce("").as("levelCeo"),
                    te.condOfficer.coalesce("").as("condOfficer"),
                    te.condCeo.coalesce("").as("condCeo"),
                    te.totalPoint.coalesce(0.0).as("totalPoint"),
                    te.note.coalesce("").as("note"),
                    te.state.coalesce("").as("state")
                )
            ).from(subquery, subQueryPath)
            .innerJoin(tom)
            .on(Expressions.numberPath(Long.class, subQueryPath, "chargeTeamId")
                .eq(tom.organization.id))
            .innerJoin(t)
            .on(tom.task.id.eq(t.id))
            .leftJoin(te)
            .on(tom.task.id.eq(te.id.task.id))
            .where(t.year.eq(year))
            .orderBy(Expressions.numberPath(Long.class, subQueryPath, "chargeTeamId").asc(), t.id.asc())
            .fetch();
    }

    @Override
    public List<TaskEvaluation> getTaskEvaluationInfoListByTeamId(String year, Long teamId) {
        JPASQLQuery<Object> sqlQuery = new JPASQLQuery<>(em, sqlTemplates);

        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;
        QOrganizationLeadEntity ol2 = QOrganizationLeadEntity.organizationLeadEntity;
        QUsersEntity u = QUsersEntity.usersEntity;
        QUsersEntity u2 = QUsersEntity.usersEntity;
        QTaskOrganizationMappingEntity tom = QTaskOrganizationMappingEntity.taskOrganizationMappingEntity;
        QTaskEntity t = QTaskEntity.taskEntity;
        QTaskEvaluationEntity te = QTaskEvaluationEntity.taskEvaluationEntity;
        StringPath subQueryPath = Expressions.stringPath("sub_query");
        NumberPath<Long> taskId = Expressions.numberPath(Long.class, te, "task.id");
        NumberPath<Long> organizationId = Expressions.numberPath(Long.class, te, "organization.id");


        JPQLQuery<Tuple> subquery = JPAExpressions.select(
                oh.descendantOrganization.id.as("chargeTeamId")
                , u.name.as("chargeTeam"),
                u2.name.as("chargeOfficer"))
            .from(ol)
            .innerJoin(u)
            .on(ol.user.id.eq(u.id))
            .innerJoin(oh)
            .on(ol.organization.id.eq(oh.descendantOrganization.id))
            .innerJoin(ol2)
            .on(oh.ancestorOrganization.id.eq(ol2.organization.id))
            .innerJoin(u2)
            .on(ol2.user.id.eq(u2.id))
            .where(oh.ancestorOrganization.id.eq(teamId));

        return sqlQuery.select(
                new QTaskEvaluation(
                    te.id.task.id.as("taskId"),
                    Expressions.stringPath(subQueryPath, "chargeTeam"),
                    Expressions.stringPath(subQueryPath, "chargeOfficer"),
                    Expressions.numberPath(Long.class, subQueryPath, "chargeTeamId"),
                    t.projectTitle,
                    t.taskTitle,
                    t.taskState,
                    t.taskProgress,
                    te.weight.coalesce(0.0).as("weight"),
                    te.officerPoint.coalesce(0).as("officerPoint"),
                    te.ceoPoint.coalesce(0).as("ceoPoint"),
                    te.taskGb.coalesce("").as("taskGb"),
                    te.levelOfficer.coalesce("").as("levelOfficer"),
                    te.levelCeo.coalesce("").as("levelCeo"),
                    te.condOfficer.coalesce("").as("condOfficer"),
                    te.condCeo.coalesce("").as("condCeo"),
                    te.totalPoint.coalesce(0.0).as("totalPoint"),
                    te.note.coalesce("").as("note"),
                    te.state.coalesce("").as("state")
                )
            )
            .from(tom)
            .innerJoin(t)
            .on(tom.task.id.eq(t.id))
            .leftJoin(te)
            .on(tom.task.id.eq(taskId).and(tom.organization.id.eq(organizationId)))
            .innerJoin(subquery, subQueryPath)
            .on((tom.organization.id).eq(Expressions.numberPath(Long.class, subQueryPath, "chargeTeamId")))
            .where(t.year.eq(year).and(tom.organization.id.eq(teamId)))
            .orderBy(Expressions.numberPath(Long.class, subQueryPath, "chargeTeamId").asc(), t.id.asc())
            .fetch();
    }
}
