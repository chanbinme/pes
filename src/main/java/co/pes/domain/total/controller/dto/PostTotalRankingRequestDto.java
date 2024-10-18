package co.pes.domain.total.controller.dto;

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
public class PostTotalRankingRequestDto {

    private Long evaluationTotalId;
    private String year;
    private String positionGb;
    private String position;
    private Long teamId;        // 팀 ID
    private String teamTitle;
    private String name;
    private double totalPoint;
    private String ranking;
    private String note;

}
