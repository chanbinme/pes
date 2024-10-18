package co.pes.domain.total.model;

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
public class Total {

    private Long evaluationTotalId;
    private String year;
    private String positionGb;
    private String name;
    private Long teamId;        // 팀 ID
    private String teamTitle;
    private double totalPoint;
    private String insUser;
    private String ranking;
    private String note;
    private LocalDateTime insDate;
    private String insIp;
    private String modUser;
    private LocalDateTime modDate;
    private String modIp;
    private String officerId;

    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }

    public void changeTotalPoint(double totalPoint) {
        this.totalPoint = totalPoint;
    }
}
