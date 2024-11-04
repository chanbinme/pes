package co.pes.domain.member.repository;

import co.pes.domain.member.model.Users;
import co.pes.domain.member.service.dto.MemberInfoModifyDto;
import co.pes.domain.member.service.dto.MemberJoinDto;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MybatisMemberInfoRepository {

    void memberJoin(@Param("memberInfo") MemberJoinDto memberJoinDto);

    Users findById(String userId);

    void editMemberInfo(@Param("memberInfo") MemberInfoModifyDto memberInfoModifyDto);

    void softDeleteById(@Param("userId") String userId, @Param("userIp") String userIp);

    List<Users> findAll(@Param("startNum") int startNum, @Param("endNum") int endNum);

    int findAllCount();

    String findIdByNameAndPositionGb(@Param("name")String name, @Param("positionGb") String positionGb);

    void editPassword(@Param("password") String password, @Param("userId") String userId, @Param("userIp") String userIp);

    int verifyPassword(@Param("userId") String userId, @Param("encryptedCurrentPassword") String encryptedCurrentPassword);
}
