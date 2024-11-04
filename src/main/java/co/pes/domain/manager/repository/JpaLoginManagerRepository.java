package co.pes.domain.manager.repository;

import co.pes.domain.member.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLoginManagerRepository extends JpaRepository<UsersEntity, String> {

    boolean existsByIdAndPassword(String id, String password);
}
