package co.pes.domain.manager.service;

import co.pes.domain.manager.controller.dto.LoginRequestDto;
import co.pes.domain.member.model.Users;
import javax.servlet.http.HttpSession;

public interface LoginManagerService {

    /**
     * 로그인
     *
     * @param session 세션
     * @param requestDto 로그인 요청 DTO
     * @return 로그인 성공 시 사용자 정보
     * @throws Exception 로그인 실패 시 예외 발생
     */
    Users login(HttpSession session, LoginRequestDto requestDto) throws Exception;
}