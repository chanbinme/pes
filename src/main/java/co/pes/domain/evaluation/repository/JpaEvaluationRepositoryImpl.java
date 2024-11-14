package co.pes.domain.evaluation.repository;

import co.pes.domain.evaluation.entity.QTaskEvaluationEntity;
import co.pes.domain.evaluation.model.QTaskEvaluation;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.member.entity.QOrganizationHierarchyEntity;
import co.pes.domain.member.entity.QOrganizationLeadEntity;
import co.pes.domain.member.entity.QUsersEntity;
import co.pes.domain.task.entity.QTaskEntity;
import co.pes.domain.task.entity.QTaskOrganizationMappingEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaEvaluationRepositoryImpl implements JpaEvaluationRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<TaskEvaluation> getTaskEvaluationInfoListByTeamIdList(String year, List<Long> teamIdList) {
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;
        QOrganizationLeadEntity ol2 = new QOrganizationLeadEntity("ol2");
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        QUsersEntity u = QUsersEntity.usersEntity;
        QUsersEntity u2 = new QUsersEntity("u2");
        QTaskOrganizationMappingEntity tom = QTaskOrganizationMappingEntity.taskOrganizationMappingEntity;
        QTaskEntity t = QTaskEntity.taskEntity;
        QTaskEvaluationEntity te = QTaskEvaluationEntity.taskEvaluationEntity;

        return query.select(new QTaskEvaluation(
                t.id.as("taskId"),
                u.name.as("chargeTeam"),
                u2.name.as("chargeOfficer"),
                oh.descendantOrganization.id.as("chargeTeamId"),
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
            ))
            .from(ol)
            .innerJoin(u)
            .on(ol.user.id.eq(u.id))
            .innerJoin(oh)
            .on(oh.descendantOrganization.id.eq(ol.organization.id))
            .innerJoin(ol2)
            .on(oh.ancestorOrganization.id.eq(ol2.organization.id))
            .innerJoin(u2)
            .on(ol2.user.id.eq(u2.id))
            .innerJoin(tom)
            .on(tom.organization.id.eq(oh.descendantOrganization.id))
            .innerJoin(t)
            .on(t.id.eq(tom.task.id))
            .leftJoin(te)
            .on(tom.task.id.eq(te.task.id))
            .where(t.year.eq(year)
                .and(oh.descendantOrganization.id.in(teamIdList)))
            .orderBy(oh.descendantOrganization.id.asc(), t.id.asc())
            .fetch();
    }

    @Override
    public List<TaskEvaluation> getTaskEvaluationInfoListByTeamId(String year, Long teamId) {
        QOrganizationLeadEntity ol = QOrganizationLeadEntity.organizationLeadEntity;
        QOrganizationLeadEntity ol2 = new QOrganizationLeadEntity("ol2");
        QOrganizationHierarchyEntity oh = QOrganizationHierarchyEntity.organizationHierarchyEntity;
        QUsersEntity u = QUsersEntity.usersEntity;
        QUsersEntity u2 = new QUsersEntity("u2");
        QTaskOrganizationMappingEntity tom = QTaskOrganizationMappingEntity.taskOrganizationMappingEntity;
        QTaskEntity t = QTaskEntity.taskEntity;
        QTaskEvaluationEntity te = QTaskEvaluationEntity.taskEvaluationEntity;

        return query.select(new QTaskEvaluation(
                t.id.as("taskId"),
                u.name.as("chargeTeam"),
                u2.name.as("chargeOfficer"),
                oh.descendantOrganization.id.as("chargeTeamId"),
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
            ))
            .from(ol)
            .innerJoin(u)
            .on(ol.user.id.eq(u.id))
            .innerJoin(oh)
            .on(oh.descendantOrganization.id.eq(ol.organization.id))
            .innerJoin(ol2)
            .on(oh.ancestorOrganization.id.eq(ol2.organization.id))
            .innerJoin(u2)
            .on(ol2.user.id.eq(u2.id))
            .innerJoin(tom)
            .on(tom.organization.id.eq(oh.descendantOrganization.id))
            .innerJoin(t)
            .on(t.id.eq(tom.task.id))
            .leftJoin(te)
            .on(tom.task.id.eq(te.task.id))
            .where(t.year.eq(year)
                .and(oh.descendantOrganization.id.eq(teamId))
                .and(tom.organization.id.eq(teamId)))
            .orderBy(tom.organization.id.asc(), t.id.asc())
            .fetch();
    }
}
