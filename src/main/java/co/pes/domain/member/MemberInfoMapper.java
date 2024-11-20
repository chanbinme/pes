package co.pes.domain.member;

import co.pes.domain.member.entity.UsersEntity;
import co.pes.domain.member.model.Users;
import org.springframework.stereotype.Component;

@Component
public class MemberInfoMapper {

    public Users covertToUsers(UsersEntity usersEntity) {
        return Users.builder()
            .id(usersEntity.getId())
            .name(usersEntity.getName())
            .position(usersEntity.getPosition())
            .positionGb(usersEntity.getPositionGb())
            .insUser(usersEntity.getInsUser())
            .insDate(usersEntity.getInsDate())
            .insIp(usersEntity.getInsIp())
            .modUser(usersEntity.getModUser())
            .modDate(usersEntity.getModDate())
            .modIp(usersEntity.getModIp())
            .build();
    }
}
