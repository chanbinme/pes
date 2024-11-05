package co.pes.domain.member.repository;

import co.pes.domain.member.entity.OrganizationEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrganizationRepository extends JpaRepository<OrganizationEntity, Long>, JpaOrganizationRepositoryCustom {
    Optional<OrganizationEntity> findById(Long id);
}
