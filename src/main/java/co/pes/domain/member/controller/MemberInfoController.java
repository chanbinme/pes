package co.pes.domain.member.controller;

import co.pes.domain.member.controller.dto.MemberInfoDeleteRequestDto;
import co.pes.domain.member.controller.dto.MemberInfoModifyRequestDto;
import co.pes.domain.member.controller.dto.MemberJoinRequestDto;
import co.pes.domain.member.service.dto.MemberInfoListPaginationDto;
import co.pes.common.SessionsUser;
import co.pes.domain.member.controller.dto.PasswordModifyRequestDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.service.MemberInfoService;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author cbkim
 * @PackageName: co.pes.member.controller
 * @FileName : MemberInfoController.java
 * @Date : 2023. 9. 5.
 * @프로그램 설명 : 회원을 처리하는 Controller Class
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberInfoController {

    private final MemberInfoService memberInfoService;

    /**
     * 회원정보 조회
     *
     * @param userId 회원 ID
     * @return 회원정보
     */
    @GetMapping("/am/member/{userId}")
    public ModelAndView findMember(@PathVariable("userId") String userId) {
        ModelAndView mv = new ModelAndView();
        Users users = memberInfoService.findMember(userId);
        mv.addObject("memberInfo", users);
        mv.setViewName("/member/editMemberInfo");

        return mv;
    }

    /**
     * 비밀번호 수정
     *
     * @param passwordDto 비밀번호 수정 요청 DTO
     * @return 비밀번호 수정 성공 시 메시지
     * @throws Exception 비밀번호 수정 실패 시 예외 발생
     */
    @PatchMapping("/am/member/password")
    public String editPassword(HttpServletRequest request,
        @RequestBody PasswordModifyRequestDto passwordDto) throws Exception {
        Users user = SessionsUser.getSessionUser(request.getSession());
        String userIp = request.getRemoteAddr();
        memberInfoService.editPassword(passwordDto, user.getId(), userIp);

        return "수정되었습니다.";
    }

    /**
     * 비밀번호 수정 화면
     *
     * @return 비밀번호 수정 화면
     */
    @GetMapping("/am/member/edit-password")
    public ModelAndView editPasswordForm(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        Users user = SessionsUser.getSessionUser(request.getSession());

        mv.addObject("userInfo", user);
        mv.setViewName("/member/editPasswordForm");

        return mv;
    }
}
