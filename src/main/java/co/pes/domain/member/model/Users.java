package co.pes.domain.member.model;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Users {

    // 아이디
    private String id;

    // 이름
    private String name;

    // 핸드폰 번호
    private String password;

    // 직급
    private String position;

    // 직책 구분 (0: CEO / 1: Officer / 2: Manager)
    private String positionGb;

    // 최초 등록자 사번
    private String insUser;

    // 최초 등록일
    private LocalDateTime insDate;

    // 최초 등록자 IP
    private String insIp;

    // 최종 수정자 사번
    private String modUser;

    // 최종 수정일
    private LocalDateTime modDate;

    // 최종 수정자 IP
    private String modIp;

    // 로그인 성공 여부
    @Builder.Default
    private String userLoginYn = "N";

    private int rowNum;

    public void successLogin() {
        this.userLoginYn = "Y";
    }

    public boolean isAdminOrCeo() {
        return this.positionGb.equals("0");
    }

    public boolean isOfficer() {
        return this.positionGb.equals("1");
    }
}
