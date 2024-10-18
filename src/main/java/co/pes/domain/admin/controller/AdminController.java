package co.pes.domain.admin.controller;

import co.pes.domain.admin.controller.dto.OfficerEvaluationPeriodDto;
import co.pes.domain.member.model.Users;
import co.pes.common.SessionsUser;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import co.pes.domain.admin.service.AdminService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author cbkim
 * @PackageName: co.pes.admin.controller
 * @FileName : AdminController.java
 * @Date : 2024. 1. 2.
 * @프로그램 설명 : 관리 기능을 관리하는 Controller Class
 */
@Slf4j
@RestController
@RequestMapping("/am/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 관리 페이지로 이동합니다.
     */
    @GetMapping
    public ModelAndView viewAdminPage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        HttpSession session = request.getSession();
        Users user = SessionsUser.getSessionUser(session);
        mv.addObject("userInfo", user);
        if (user.isAdminOrCeo()) {
            mv.setViewName("/admin/admin");
        } else {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }

        return mv;
    }

    /**
     * 평가 기간을 설정합니다.
     *
     * @param officerEvaluationPeriodDto 평가 기간 설정 DTO
     * @return 저장 성공 시 메시지
     */
    @PostMapping("/officer-evaluation-period")
    public String postOfficerEvaluationPeriod(HttpServletRequest request,
        @RequestBody OfficerEvaluationPeriodDto officerEvaluationPeriodDto) {
        HttpSession session = request.getSession();
        Users user = SessionsUser.getSessionUser(session);
        String userIp = request.getRemoteAddr();
        if (user.isAdminOrCeo()) {
            adminService.postOfficerEvaluationPeriod(officerEvaluationPeriodDto, user, userIp);
        } else {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }

        return "저장되었습니다.";
    }

    /**
     * 평가 기간을 조회합니다.
     *
     * @return 평가 기간
     */
    @GetMapping("/officer-evaluation-period")
    public OfficerEvaluationPeriod getOfficerEvaluationPeriod(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Users user = SessionsUser.getSessionUser(session);
        if (user.isAdminOrCeo()) {
            return adminService.getOfficerEvaluationPeriod();
        } else {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
    }
}
