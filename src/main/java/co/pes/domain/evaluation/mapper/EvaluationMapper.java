package co.pes.domain.evaluation.mapper;

import co.pes.domain.evaluation.controller.dto.TaskEvaluationRequestDto;
import co.pes.domain.evaluation.entity.TaskEvaluationEntity;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.task.entity.TaskEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EvaluationMapper {

    private TaskEvaluation dtoToTaskEvaluation(TaskEvaluationRequestDto dto, String userName, String userIp) {
        return TaskEvaluation.builder()
            .taskId(dto.getTaskId())
            .chargeTeamId(dto.getChargeTeamId())
            .weight(dto.getWeight())
            .officerPoint(dto.getOfficerPoint())
            .ceoPoint(dto.getCeoPoint())
            .taskGb(dto.getTaskGb())
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

    public List<TaskEvaluation> dtoListToTaskEvaluationList(List<TaskEvaluationRequestDto> dtoList, Users user,
        String userIp) {
        List<TaskEvaluation> taskEvaluationList = new ArrayList<>();

        if (!dtoList.isEmpty()) {
            String userName = user.getName();
            for (TaskEvaluationRequestDto dto : dtoList) {
                taskEvaluationList.add(dtoToTaskEvaluation(dto, userName, userIp));
            }
        }

        return taskEvaluationList;
    }

    public TaskEvaluationEntity dtoToTaskEvaluationEntity(TaskEvaluationRequestDto dto, TaskEntity task, OrganizationEntity organization, String userName, String userIp) {
        return TaskEvaluationEntity.builder()
            .task(task)
            .organization(organization)
            .weight(dto.getWeight())
            .officerPoint(dto.getOfficerPoint())
            .ceoPoint(dto.getCeoPoint())
            .levelOfficer(dto.getLevelOfficer())
            .levelCeo(dto.getLevelCeo())
            .condOfficer(dto.getCondOfficer())
            .condCeo(dto.getCondCeo())
            .totalPoint(dto.getTotalPoint())
            .note(dto.getNote())
            .state(dto.getState())
            .insUser(userName)
            .insIp(userIp)
            .modUser(userName)
            .modIp(userIp)
            .build();
    }
}
