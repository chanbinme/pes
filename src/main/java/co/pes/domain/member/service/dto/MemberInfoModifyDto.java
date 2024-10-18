package co.pes.domain.member.service.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberInfoModifyDto {

    // 아이디
    private String id;

    // 이름
    private String name;

    // 핸드폰 번호
    private String password;

    // 직급
    private String rank;

    // 직책 구분 (0: CEO / 1: Officer / 2: Manager)
    private String positionGb;

    // 최종 수정자 사번
    private String modUser;

    // 최종 수정일
    private LocalDateTime modDate;

    // 최종 수정자 IP
    private String modIp;
}
