package co.pes.domain.evaluation.service;

import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import co.pes.domain.admin.service.AdminService;
import co.pes.domain.evaluation.controller.dto.FinalEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.member.model.Users;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public abstract class AbstractEvaluationService implements EvaluationService {

    private final AdminService adminService;

    public abstract TaskEvaluationResponseDto getEvaluationInfo(String year, Long chargeTeamId, Users user);

    /**
     * 사용자의 역할과 관리하는 팀을 기반으로 하위 팀을 포함한 팀 ID 목록을 구성합니다.
     *
     * @param chargeTeamId    담당 팀 ID
     * @param user            요청하는 사용자
     * @param checkTeamIdList 사용자가 관리하는 팀 ID 목록
     * @return 팀 ID 목록
     */
    protected abstract List<Long> getTeamIdList(Long chargeTeamId, Users user, List<Long> checkTeamIdList);

    protected abstract void addDescendantTeams(List<Long> teamIdList, Long descendantOrgId, Users user, List<Long> checkTeamIdList);

    protected abstract boolean hasDescendant(Long chargeTeamId);

    public abstract void finalSaveTaskEvaluationList(FinalEvaluationRequestDto finalEvaluationRequestDto, Users user, String userIp);

    protected abstract boolean existsTaskEvaluation(TaskEvaluation taskEvaluation);

    @Override
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
