package co.pes.common.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
public class BaseEntity {

    // 최초 등록자 사번
    @Column(length = 50, updatable = false)
    private String insUser;

    // 최초 등록일
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime insDate;

    // 최초 등록자 IP
    @Column(length = 15, updatable = false)
    private String insIp;

    // 최종 수정자 사번
    @Column(length = 50, insertable = false)
    private String modUser;

    // 최종 수정일
    @Column(insertable = false)
    @UpdateTimestamp
    private LocalDateTime modDate;

    // 최종 수정자 IP
    @Column(length = 15, insertable = false)
    private String modIp;

    public void updateModIp(String modIp) {
        this.modIp = modIp;
    }
}
