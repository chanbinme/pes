package co.pes.domain.member.repository;

import co.pes.domain.member.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemberInfoRepository extends JpaRepository<UsersEntity, String>, JpaMemberInfoRepositoryCustom {

    boolean existsByIdAndPassword(String userId, String encryptedCurrentPassword);
}
