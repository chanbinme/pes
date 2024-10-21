package co.pes.domain.evaluation.service;

import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import co.pes.domain.admin.service.AdminService;
import co.pes.domain.evaluation.controller.dto.FinalEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.mapper.EvaluationMapper;
import co.pes.domain.evaluation.repository.EvaluationRepository;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.service.TotalService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author cbkim
 * @PackageName: co.pes.evaluation.service
 * @FileName : EvaluationService.java
 * @Date : 2023. 12. 7.
 * @프로그램 설명 : 평가 데이터를 관리하는 Service Class
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final TotalService totalService;
    private final AdminService adminService;

    /**
     * 주어진 연도와 팀에 대한 평가 정보를 가져옵니다.
     *
     * @param year         평가 연도
     * @param chargeTeamId 담당 팀 ID
     * @param user         요청하는 사용자
     * @return 평가 정보가 포함된 TaskEvaluationResponseDto
     */
    public TaskEvaluationResponseDto getEvaluationInfo(String year, Long chargeTeamId, Users user) {
        // 평가 완료된 팀인지 체크합니다. 평가 완료된 팀이라면 해당 팀의 평가 정보는 수정할 수 없습니다.
        boolean existsTotal = evaluationRepository.countTotal(year, chargeTeamId) > 0;
        List<TaskEvaluation> taskEvaluationInfoList;
        List<Long> checkTeamIdList = evaluationRepository.getTeamListByUserId(user.getId());    // 평가자가 관리하는 Team Id List 가져오기

        if (this.hasDescendant(chargeTeamId)) {
            List<Long> teamIdList = this.getTeamIdList(chargeTeamId, user, checkTeamIdList);
            taskEvaluationInfoList = evaluationRepository.getTaskEvaluationInfoListByTeamIdList(year, teamIdList);
        } else if (chargeTeamId == 26) {
            if (user.isAdminOrCeo()) {
                taskEvaluationInfoList = evaluationRepository.getTaskEvaluationInfoList(year, chargeTeamId);
            } else {
                taskEvaluationInfoList =
                    evaluationRepository.getTaskEvaluationInfoListByCheckTeamIdList(year, chargeTeamId, checkTeamIdList);
            }
        } else {
            taskEvaluationInfoList = evaluationRepository.getTaskEvaluationInfoList(year, chargeTeamId);
        }

        return TaskEvaluationResponseDto.builder()
            .existsTotal(existsTotal)
            .taskEvaluationList(taskEvaluationInfoList)
            .build();
    }

    /**
     * 사용자의 역할과 관리하는 팀을 기반으로 하위 팀을 포함한 팀 ID 목록을 구성합니다.
     *
     * @param chargeTeamId    담당 팀 ID
     * @param user            요청하는 사용자
     * @param checkTeamIdList 사용자가 관리하는 팀 ID 목록
     * @return 팀 ID 목록
     */
    private List<Long> getTeamIdList(Long chargeTeamId, Users user, List<Long> checkTeamIdList) {
        List<Long> teamIdList = new ArrayList<>();  // 팀 ID 리스트
        List<Long> descendantOrgIdList = evaluationRepository.getDescendantOrgIdList(chargeTeamId);

        for (Long descendantOrgId : descendantOrgIdList) {
            this.addDescendantTeams(teamIdList, descendantOrgId, user, checkTeamIdList);
        }
        return teamIdList;
    }

    private void addDescendantTeams(List<Long> teamIdList, Long descendantOrgId, Users user, List<Long> checkTeamIdList) {
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

    private boolean hasDescendant(Long chargeTeamId) {
        return evaluationRepository.countDescendantOrgByTeamId(chargeTeamId) > 0;
    }

    /**
     * 직무 평가 정보를 임시 저장합니다.
     *
     * @param taskEvaluationRequestDtoList 직무 평가 요청 DTO 목록
     * @param user 평가하는 사용자
     * @param userIp 사용자의 IP 주소
     */
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

    /**
     * 최종 직무 평가 목록을 저장하고 총 평가 결과를 업데이트합니다.
     *
     * @param finalEvaluationRequestDto 최종 평가 요청 DTO
     * @param user 평가하는 사용자
     * @param userIp 사용자의 IP 주소
     */
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

    private boolean existsTaskEvaluation(TaskEvaluation taskEvaluation) {
        return evaluationRepository.countTaskEvaluation(taskEvaluation) > 0;
    }

    /**
     * 오늘이 임원 평가 기간에 속하는지 확인합니다.
     *
     * @return 임원 평가 기간 여부를 나타내는 메시지
     */
    public String checkOfficerEvaluationPeriod() {
        OfficerEvaluationPeriod officerEvaluationPeriod = adminService.getOfficerEvaluationPeriod();
        LocalDate startDate = officerEvaluationPeriod.getStartDate().toLocalDate();
        LocalDate endDate = officerEvaluationPeriod.getEndDate().toLocalDate();
        LocalDate nowDate = LocalDate.now();

        if (nowDate.isAfter(startDate.minusDays(1)) && nowDate.isBefore(endDate.plusDays(1))) {
            return "임원 평가 기간입니다.";
        } else {
            return "임원 평가 기간이 아닙니다.\\n평가 기간: " + startDate + " ~ " + endDate;
        }
    }
}