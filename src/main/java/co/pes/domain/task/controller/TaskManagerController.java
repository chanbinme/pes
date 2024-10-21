package co.pes.domain.task.controller;

import co.pes.domain.task.controller.dto.TaskRequestDto;
import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.service.TaskManagerService;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.service.TotalService;
import co.pes.common.SessionsUser;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author cbkim
 * @PackageName: co.pes.task.controller
 * @FileName : TaskManagerController.java
 * @Date : 2023. 11. 30.
 * @프로그램 설명 : 업무를 관리하는 Controller Class
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TaskManagerController {

    private final TaskManagerService taskManagerService;
    private final TotalService totalService;

    /**
     * 업무 관리 페이지로 이동합니다.
     */
    @GetMapping("/am/tasks-manager")
    public ModelAndView getTaskList(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        Users user = SessionsUser.getSessionUser(request.getSession());

        if (user.isAdminOrCeo()) {
            List<String> evaluationYearList = totalService.getEvaluationYearList();
            String recentEvaluationYear = String.valueOf(evaluationYearList.get(0));
            mv.addObject("yearList", evaluationYearList);
            mv.addObject("selectedYear", recentEvaluationYear);
            mv.addObject("userInfo", user);
            mv.setViewName("/task/taskInfoList");

            return mv;
        }

        throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
    }

    /**
     * 특정 연도의 프로젝트 목록을 조회합니다.
     *
     * @param year 평가 연도
     * @return 프로젝트 목록
     */
    @GetMapping("/am/tasks/projects")
    @ResponseBody
    public List<Project> getProjects(@RequestParam("year") String year) {
        return taskManagerService.getProjects(year);
    }

    /**
     * 특정 연도의 프로젝트에 포함된 업무 목록을 조회합니다.
     *
     * @param year 평가 연도
     * @param projectTitle 프로젝트 제목
     * @return 업무 목록
     */
    @GetMapping("/am/tasks")
    @ResponseBody
    public List<Tasks> getTasks(@RequestParam String year,
                                @RequestParam String projectTitle) {
        return taskManagerService.getTasks(year, projectTitle);
    }

    /**
     * 특정 프로젝트의 업무 리스트 정보 삭제
     *
     * @param year 평가 연도
     * @param taskRequestDtos 삭제할 업무 리스트
     * @return 삭제 결과
     */
    @DeleteMapping("/am/tasks/{year}")
    @ResponseBody
    public String deleteTasks(HttpServletRequest request,
        @PathVariable("year") String year,
        @RequestBody List<TaskRequestDto> taskRequestDtos) {
        Users user = SessionsUser.getSessionUser(request.getSession());
        if (user.isAdminOrCeo()) {
            if (totalService.checkEndedYear(year)) {
                throw new BusinessLogicException(ExceptionCode.FINISHED_EVALUATION);
            }
            taskManagerService.deleteTasks(taskRequestDtos);
        } else {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }

        return "삭제되었습니다.";
    }

    /**
     * 업무 - 팀 매핑 정보를 저장합니다.
     *
     * @param request 요청 객체
     * @param mappingDtos 업무 - 팀 매핑 정보
     * @return 저장 결과
     */
    @PostMapping("/am/tasks/mappings")
    public String taskMapping(HttpServletRequest request,
                            @RequestBody List<MappingDto> mappingDtos) {
        if (!mappingDtos.isEmpty()) {
            Users user = SessionsUser.getSessionUser(request.getSession());
            String userIp = request.getRemoteAddr();
            taskManagerService.postMapping(mappingDtos, user, userIp);
        } else {
            return "변경된 내용이 없습니다.";
        }
        return "저장되었습니다.";
    }

    /**
     * 업무 - 팀 매핑 정보를 삭제합니다.
     *
     * @param mappingDtos 매핑 정보 DTO
     * @return 삭제 결과
     */
    @DeleteMapping("/am/tasks/mappings")
    public String deleteMappingInfo(@RequestBody List<MappingDto> mappingDtos) {
        if (!mappingDtos.isEmpty()) {
            taskManagerService.deleteMappingInfo(mappingDtos);
        }

        return "초기화되었습니다.";
    }
}
