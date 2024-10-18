package co.pes.domain.evaluation.controller.dto;

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
public class TotalRequestDto {

    private Long teamId;        // 팀 ID
    private String year;
    private String teamTitle;   // 팀 이름
    private String teamName;    // Manager 이름
    private String officerName;     // Officer 이름
    private double totalPoint;
    private String note;

}
