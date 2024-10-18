package co.pes.domain.admin.model;

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
public class OfficerEvaluationPeriod {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String insUser;
    private String insIp;
}
