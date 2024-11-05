package co.pes.domain.evaluation.service;

import co.pes.domain.admin.service.AdminService;
import co.pes.domain.evaluation.controller.dto.FinalEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.mapper.EvaluationMapper;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.EvaluationRepository;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.service.TotalService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MybatisEvaluationService extends AbstractEvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final TotalService totalService;

    public MybatisEvaluationService(EvaluationRepository evaluationRepository,
        AdminService adminService, EvaluationMapper evaluationMapper, TotalService totalService) {
        super(adminService);
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
        this.totalService = totalService;
    }

    @Override
    public TaskEvaluationResponseDto getEvaluationInfo(String year, Long chargeTeamId, Users user) {
        // 평가 완료된 팀인지 체크합니다. 평가 완료된 팀이라면 해당 팀의 평가 정보는 수정할 수 없습니다.
        boolean existsTotal = evaluationRepository.countTotal(year, chargeTeamId) > 0;
        List<TaskEvaluation> taskEvaluationInfoList;
        List<Long> checkTeamIdList = evaluationRepository.getTeamListByUserId(user.getId());    // 평가자가 관리하는 Team Id List 가져오기

        if (this.hasDescendant(chargeTeamId)) {
            List<Long> teamIdList = this.getTeamIdList(chargeTeamId, user, checkTeamIdList);
            taskEvaluationInfoList = evaluationRepository.getTaskEvaluationInfoListByTeamIdList(year, teamIdList);
        } else {
            taskEvaluationInfoList = evaluationRepository.getTaskEvaluationInfoList(year, chargeTeamId);
        }

        return TaskEvaluationResponseDto.builder()
            .existsTotal(existsTotal)
            .taskEvaluationList(taskEvaluationInfoList)
            .build();
    }

    protected List<Long> getTeamIdList(Long chargeTeamId, Users user, List<Long> checkTeamIdList) {
        List<Long> teamIdList = new ArrayList<>();  // 팀 ID 리스트
        List<Long> descendantOrgIdList = evaluationRepository.getDescendantOrgIdList(chargeTeamId);

        for (Long descendantOrgId : descendantOrgIdList) {
            this.addDescendantTeams(teamIdList, descendantOrgId, user, checkTeamIdList);
        }
        return teamIdList;
    }

    protected void addDescendantTeams(List<Long> teamIdList, Long descendantOrgId, Users user,
        List<Long> checkTeamIdList) {
        if (this.hasDescendant(descendantOrgId)) {
            if (user.isAdminOrCeo()) {
                teamIdList.addAll(evaluationRepository.getDescendantOrgIdList(descendantOrgId));
            } else {
                teamIdList.addAll(evaluationRepository.getLastDescendantOrgIdList(descendantOrgId,
                    checkTeamIdList));
            }
        } else {
            if (user.isAdminOrCeo()) {
                teamIdList.add(descendantOrgId);
            } else {
                // 평가자가 관리하는 Team만 Add
                if (checkTeamIdList.contains(descendantOrgId)) {
                    teamIdList.add(descendantOrgId);
                }
            }
        }
    }

    @Override
    protected boolean hasDescendant(Long chargeTeamId) {
        return evaluationRepository.countDescendantOrgByTeamId(chargeTeamId) > 0;
    }

    @Override
    @Transactional
    public void saveTaskEvaluationList(List<TaskEvaluationRequestDto> taskEvaluationRequestDtoList,
        Users user, String userIp) {
        List<TaskEvaluation> taskEvaluationList = evaluationMapper.dtoListToTaskEvaluationList(
            taskEvaluationRequestDtoList, user, userIp);

        for (TaskEvaluation taskEvaluation : taskEvaluationList) {
            taskEvaluation.changeState("N");

            if (this.existsTaskEvaluation(taskEvaluation)) {
                evaluationRepository.updateTaskEvaluation(taskEvaluation);
            } else {
                evaluationRepository.saveTaskEvaluation(taskEvaluation);
            }
        }
    }

    @Override
    @Transactional
    public void finalSaveTaskEvaluationList(FinalEvaluationRequestDto finalEvaluationRequestDto,
        Users user, String userIp) {
        List<TaskEvaluation> taskEvaluationList = evaluationMapper.dtoListToTaskEvaluationList(
            finalEvaluationRequestDto.getTaskEvaluationRequestDtoList(), user, userIp);

        for (TaskEvaluation taskEvaluation : taskEvaluationList) {
            taskEvaluation.changeState("F");

            if (this.existsTaskEvaluation(taskEvaluation)) {
                evaluationRepository.updateTaskEvaluation(taskEvaluation);
            } else {
                evaluationRepository.saveTaskEvaluation(taskEvaluation);
            }
        }
        totalService.saveTotal(finalEvaluationRequestDto.getTotalRequestDto(), user, userIp);
    }

    protected boolean existsTaskEvaluation(TaskEvaluation taskEvaluation) {
        return evaluationRepository.countTaskEvaluation(taskEvaluation) > 0;
    }
}