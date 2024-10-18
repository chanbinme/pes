package co.pes.domain.manager.controller;

import co.pes.common.SessionsUser;
import co.pes.domain.manager.service.LoginManagerService;
import co.pes.domain.member.model.Users;
import co.pes.domain.manager.controller.dto.LoginRequestDto;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author cbkim
 * @PackageName: co.pes.manager.controller
 * @FileName : LoginManagerController.java
 * @Date : 2023. 9. 5.
 * @프로그램 설명 : 로그인을 관리하는 Controller Class
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginManagerController {

    private final LoginManagerService loginManagerService;

    /**
     * 로그인 페이지로 이동
     */
    @GetMapping("/am/manager/login")
    public ModelAndView login(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/manager/loginForm");

        if (SessionsUser.isLoginUser(request.getSession())) {
            // 메인화면으로 리다이렉트 시켜주기
            mv.addObject("returnUrl", "/am/jobs-evaluation");
            mv.setViewName("/common/result");

            return mv;
        }

        return mv;
    }

    /**
     * 로그인 검증 메서드
     *
     * @param request HttpServletRequest
     * @param requestDto LoginRequestDto
     * @return 환영 메시지
     * @throws Exception 예외 발생 시
     */
    @PostMapping("/am/manager/loginProc")
    public String loginProc(HttpServletRequest request,
                              @RequestBody LoginRequestDto requestDto)
        throws Exception {
        HttpSession session = request.getSession();
        Users users = loginManagerService.login(session, requestDto);

        return users.getName() + "님 환영합니다.";
    }

    /**
     * 로그아웃
     */
    @GetMapping("/am/manager/logout")
    public ModelAndView logout(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        HttpSession session = request.getSession();

        if (SessionsUser.isLoginUser(session)) {
            SessionsUser.removeSessionUser(session);
        }

        mv.addObject("returnUrl", "/am/manager/login");
        mv.setViewName("/common/result");

        return mv;
    }
}
