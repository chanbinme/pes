package co.pes.domain.admin.mapper;

import co.pes.domain.admin.controller.dto.OfficerEvaluationPeriodDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public OfficerEvaluationPeriod dtoToOfficerEvaluationPeriod(
            OfficerEvaluationPeriodDto officerEvaluationPeriodDto, Users user, String userIp) {
        return OfficerEvaluationPeriod.builder()
            .startDate(officerEvaluationPeriodDto.getStartDate())
            .endDate(officerEvaluationPeriodDto.getEndDate())
            .insUser(user.getName())
            .insIp(userIp)
            .build();
    }
}
