package co.pes.domain.member.entity;

import co.pes.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"password"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "USERS")
public class UsersEntity extends BaseEntity {

    // 아이디
    @Id
    @Size(min = 1, max = 50)
    private String id;

    // 이름
    @Column(length = 50, nullable = false)
    @Size(min = 1, max = 50)
    private String name;

    // 핸드폰 번호
    @Column(length = 200, nullable = false)
    @Size(min = 1, max = 200)
    private String password;

    // 직급
    @Column(length = 50)
    @Size(min = 1, max = 50)
    private String position;

    // 직책 구분 (0: CEO / 1: Officer / 2: Manager)
    @Column(length = 1)
    @Size(min = 1, max = 1)
    private String positionGb;

    public boolean isAdminOrCeo() {
        return this.positionGb.equals("0");
    }

    public boolean isOfficer() {
        return this.positionGb.equals("1");
    }
}
