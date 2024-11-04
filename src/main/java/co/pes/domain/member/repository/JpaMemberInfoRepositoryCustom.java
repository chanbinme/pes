package co.pes.domain.member.repository;

import co.pes.domain.member.model.Users;
import java.util.Optional;

public interface JpaMemberInfoRepositoryCustom {
    Optional<Users> searchUserById(String userId);
}
