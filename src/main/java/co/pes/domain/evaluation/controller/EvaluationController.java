package co.pes.domain.evaluation.controller;

import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.common.SessionsUser;
import co.pes.common.utils.ExcelUtil;
import co.pes.domain.evaluation.controller.dto.FinalEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.service.EvaluationService;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.service.TotalService;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author cbkim
 * @PackageName: co.pes.evaluation.controller
 * @FileName : EvaluationController.java
 * @Date : 2023. 12. 7.
 * @프로그램 설명 : 평가 데이터를 관리하는 Controller Class
 */
@Slf4j
@RestController
@RequestMapping("/am/tasks-evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final TotalService totalService;

    /**
     * 평가 데이터를 저장합니다.
     *
     * @param taskEvaluationRequestDtoList 평가 데이터 요청 DTO
     * @return 저장 성공 시 메시지
     */
    @PostMapping
    public String postEvaluationData(HttpServletRequest request,
        @RequestBody List<TaskEvaluationRequestDto> taskEvaluationRequestDtoList) {
        Users user = SessionsUser.getSessionUser(request.getSession());
        String userIp = request.getRemoteAddr();
        evaluationService.saveTaskEvaluationList(taskEvaluationRequestDtoList, user, userIp);
        return "저장되었습니다.";
    }

    /**
     * 최종 평가 데이터를 저장합니다.
     *
     * @param finalEvaluationRequestDto 최종 평가 데이터 요청 DTO
     * @return 저장 성공 시 메시지
     */
    @PostMapping("/final")
    public String finalPostEvaluationData(HttpServletRequest request,
        @RequestBody FinalEvaluationRequestDto finalEvaluationRequestDto) {
        Users user = SessionsUser.getSessionUser(request.getSession());
        String userIp = request.getRemoteAddr();
        evaluationService.finalSaveTaskEvaluationList(finalEvaluationRequestDto, user, userIp);

        return "최종 제출되었습니다.";
    }

    /**
     * 평가 데이터를 조회합니다.
     *
     * @return 평가 데이터
     */
    @GetMapping
    public ModelAndView getTaskEvaluationList(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        Users user = SessionsUser.getSessionUser(request.getSession());

        if (user.isOfficer()) {
            String message = evaluationService.checkOfficerEvaluationPeriod();
            if (!message.equals("임원 평가 기간입니다.")) {
                log.info("임원 평가 기간이 아닙니다. {}", LocalDate.now());
                mv.addObject("message", message);
                mv.addObject("returnUrl", "/am/manager/logout");
                mv.setViewName("/common/result");

                return mv;
            }
        }

        List<String> evaluationYearList = totalService.getEvaluationYearList();
        String recentEvaluationYear = String.valueOf(evaluationYearList.get(0));
        mv.addObject("yearList", evaluationYearList);
        mv.addObject("userInfo", user);
        mv.addObject("selectedYear", recentEvaluationYear);
        mv.setViewName("/evaluation/evaluation");

        return mv;
    }

    /**
     * 평가 데이터를 조회합니다.
     *
     * @param year    평가 연도
     * @param chargeTeamId 담당팀 ID
     * @return 평가 데이터
     */
    @GetMapping("/tasks")
    public TaskEvaluationResponseDto getEvaluationInfo(HttpServletRequest request,
                                    @RequestParam("year") String year,
                                    @RequestParam("chargeTeamId") Long chargeTeamId) {
        Users user = SessionsUser.getSessionUser(request.getSession());
        return evaluationService.getEvaluationInfo(year, chargeTeamId, user);
    }

    /**
     * 엑셀 다운로드
     *
     * @param year    평가 연도
     * @param chargeTeamId 담당팀 ID
     */
    @GetMapping("/excel-download")
    public void excelDownloadEvaluationInfo(HttpServletRequest request,
                                            @RequestParam("year") String year,
                                            @RequestParam("chargeTeamId") Long chargeTeamId,
                                            HttpServletResponse response) {
        Users user = SessionsUser.getSessionUser(request.getSession());
        List<TaskEvaluation> evaluationList = evaluationService.getEvaluationInfo(year, chargeTeamId, user).getTaskEvaluationList();
        ExcelUtil.excelDownload(response, evaluationList);
    }
}
