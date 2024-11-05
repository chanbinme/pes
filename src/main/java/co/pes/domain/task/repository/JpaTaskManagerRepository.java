package co.pes.domain.task.repository;

import co.pes.domain.task.entity.TaskEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTaskManagerRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findById(Long id);
}
