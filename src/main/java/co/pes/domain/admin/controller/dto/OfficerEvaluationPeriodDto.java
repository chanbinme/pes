package co.pes.domain.admin.controller.dto;

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
public class OfficerEvaluationPeriodDto {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
