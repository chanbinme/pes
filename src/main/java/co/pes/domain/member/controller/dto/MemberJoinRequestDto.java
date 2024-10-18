package co.pes.domain.member.controller.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberJoinRequestDto {

    // 사번
    @NotBlank
    @Size(min = 6, max = 20)
    private String id;

    // 비밀번호
    @NotBlank
    @Pattern(regexp = "^[A-Za-z\\d@$!%*#?&]{10,}$")
    private String password;

    // 이름
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    // 직급
    private String rank;

    // 직책 구분
    private String positionGb;
}
