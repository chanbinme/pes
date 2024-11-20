package co.pes.domain.member.repository;

import static co.pes.domain.member.entity.QUsersEntity.usersEntity;

import co.pes.domain.member.model.QUsers;
import co.pes.domain.member.model.Users;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaMemberInfoRepositoryImpl implements JpaMemberInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Users> searchUserById(String userId) {
        return Optional.ofNullable(queryFactory
            .select(new QUsers(
                usersEntity.id,
                usersEntity.name,
                usersEntity.position,
                usersEntity.positionGb,
                usersEntity.insUser,
                usersEntity.insDate,
                usersEntity.insIp,
                usersEntity.modUser,
                usersEntity.modDate,
                usersEntity.modIp))
                .from(usersEntity)
            .where(usersEntity.id.eq(userId))
            .fetchOne());
    }
}
