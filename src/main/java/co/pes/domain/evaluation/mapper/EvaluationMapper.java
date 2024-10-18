package co.pes.domain.evaluation.mapper;

import co.pes.domain.evaluation.model.JobEvaluation;
import co.pes.domain.evaluation.controller.dto.JobEvaluationRequestDto;
import co.pes.domain.member.model.Users;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EvaluationMapper {

    private JobEvaluation dtoToJobEvaluation(JobEvaluationRequestDto dto, String userName, String userIp) {
        return JobEvaluation.builder()
            .taskId(dto.getTaskId())
            .chargeTeamId(dto.getChargeTeamId())
            .weight(dto.getWeight())
            .officerPoint(dto.getOfficerPoint())
            .ceoPoint(dto.getCeoPoint())
            .jobGb(dto.getJobGb())
            .levelOfficer(dto.getLevelOfficer())
            .levelCeo(dto.getLevelCeo())
            .condOfficer(dto.getCondOfficer())
            .condCeo(dto.getCondCeo())
            .totalPoint(dto.getTotalPoint())
            .note(dto.getNote())
            .state(dto.getState())
            .insUser(userName)
            .insDate(LocalDateTime.now())
            .insIp(userIp)
            .modUser(userName)
            .modDate(LocalDateTime.now())
            .modIp(userIp)
            .build();
    }

    public List<JobEvaluation> dtoListToJobEvaluationList(List<JobEvaluationRequestDto> dtoList, Users user,
        String userIp) {
        List<JobEvaluation> jobEvaluationList = new ArrayList<>();

        if (!dtoList.isEmpty()) {
            String userName = user.getName();
            for (JobEvaluationRequestDto dto : dtoList) {
                jobEvaluationList.add(dtoToJobEvaluation(dto, userName, userIp));
            }
        }

        return jobEvaluationList;
    }
}
