package co.pes.domain.job.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MappingDto {

    private Long chargeTeamId;    // 담당 팀 ID
    private Long taskId;    // 업무 ID
}
