package co.pes.domain.member.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberInfoModifyRequestDto {

    // 관리자 비밀번호
    private String userPassword;

    // 관리자 핸드폰 번호
    private String userHp;

    // 관리자 이메일
    private String userEmail;

}
