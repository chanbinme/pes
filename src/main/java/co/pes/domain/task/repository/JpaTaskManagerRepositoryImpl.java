package co.pes.domain.task.repository;

import co.pes.domain.task.entity.QTaskEntity;
import co.pes.domain.task.entity.QTaskOrganizationMappingEntity;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.QProject;
import co.pes.domain.task.model.QTasks;
import co.pes.domain.task.model.Tasks;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaTaskManagerRepositoryImpl implements JpaTaskManagerRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Project> searchProjectTitleByYear(String year) {
        QTaskEntity t = QTaskEntity.taskEntity;

        return query
            .select(new QProject(t.projectTitle)).distinct()
            .from(t)
            .where(t.year.eq(year))
            .fetch();
    }

    @Override
    public List<Tasks> searchTasksByYearAndProjectTitle(String year, String projectTitle) {
        QTaskEntity t = QTaskEntity.taskEntity;

        return query
            .select(new QTasks(
                t.id,
                t.year,
                t.taskTitle,
                t.taskState,
                t.taskProgress
            ))
            .from(t)
            .where(t.year.eq(year), t.projectTitle.eq(projectTitle))
            .fetch();
    }

    @Override
    public List<Long> searchChargeTeamIdsByTaskId(Long id) {
        QTaskOrganizationMappingEntity tom = QTaskOrganizationMappingEntity.taskOrganizationMappingEntity;

        return query
            .select(tom.organization.id)
            .from(tom)
            .where(tom.task.id.eq(id))
            .fetch();
    }

    @Override
    public List<Long> searchTeamIdByTaskId(Long taskId) {
        QTaskOrganizationMappingEntity tom = QTaskOrganizationMappingEntity.taskOrganizationMappingEntity;
        return query
            .selectDistinct(tom.organization.id)
            .from(tom)
            .where(tom.task.id.eq(taskId))
            .fetch();
    }
}
