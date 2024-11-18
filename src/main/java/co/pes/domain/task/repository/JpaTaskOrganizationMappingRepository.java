package co.pes.domain.task.repository;

import co.pes.domain.task.entity.TaskOrganizationMappingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTaskOrganizationMappingRepository extends JpaRepository<TaskOrganizationMappingEntity, Long> {
    List<TaskOrganizationMappingEntity> findAllByTaskId(Long taskId);
}
