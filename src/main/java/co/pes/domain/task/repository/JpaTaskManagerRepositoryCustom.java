package co.pes.domain.task.repository;

import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import java.util.List;

public interface JpaTaskManagerRepositoryCustom {
    List<Project> searchProjectTitleByYear(String year);

    List<Tasks> searchTasksByYearAndProjectTitle(String year, String projectTitle);

    List<Long> searchChargeTeamIdsByTaskId(Long id);

    List<Long> searchTeamIdByTaskId(Long taskId);
}
