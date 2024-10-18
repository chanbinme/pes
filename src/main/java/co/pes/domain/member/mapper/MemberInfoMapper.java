package co.pes.domain.member.mapper;

import co.pes.domain.member.controller.dto.MemberInfoModifyRequestDto;
import co.pes.domain.member.controller.dto.MemberJoinRequestDto;
import co.pes.domain.member.service.dto.MemberInfoModifyDto;
import co.pes.domain.member.service.dto.MemberJoinDto;
import co.pes.common.utils.SHA512Utils;

import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Component;

@Component
public class MemberInfoMapper {

    public MemberJoinDto requestDtoToMemberJoinDto(MemberJoinRequestDto requestDto, String userIp)
        throws NoSuchAlgorithmException {
        return MemberJoinDto.builder()
            .id(requestDto.getId())
            .password(SHA512Utils.encrypt(requestDto.getPassword()))
            .name(requestDto.getName())
            .build();
    }

    public MemberInfoModifyDto requestDtoToMemberInfoModifyDto(
            MemberInfoModifyRequestDto requestDto, String userId, String userIp)
        throws NoSuchAlgorithmException {
        return MemberInfoModifyDto.builder()
            .id(userId)
            .password(SHA512Utils.encrypt(requestDto.getUserPassword()))
            .build();
    }
}
