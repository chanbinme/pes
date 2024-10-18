package co.pes.domain.manager.service;

import co.pes.domain.manager.service.dto.LoginDto;
import co.pes.common.SessionsUser;
import co.pes.domain.manager.mapper.LoginManagerMapper;
import co.pes.domain.manager.repository.LoginManagerRepository;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.manager.controller.dto.LoginRequestDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.MemberInfoRepository;

import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginManagerService {

    private final LoginManagerRepository loginManagerRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final LoginManagerMapper loginManagerMapper;

    /**
     * 로그인
     *
     * @param session 세션
     * @param requestDto 로그인 요청 DTO
     * @return 로그인 성공 시 사용자 정보
     * @throws Exception 로그인 실패 시 예외 발생
     */
    public Users login(HttpSession session, LoginRequestDto requestDto) throws Exception {
        LoginDto loginDto = loginManagerMapper.requestDtoToLoginDto(requestDto);
        if (this.isSuccessLogin(loginDto)) {
            return this.onLoginSuccess(session, loginDto);
        } else {
            throw new BusinessLogicException(ExceptionCode.INVALID_ID_OR_PASSWORD);
        }
    }

    /**
     * 로그인 성공 여부 검증
     *
     * @param loginDto 로그인 DTO
     * @return 로그인 성공 여부
     */
    private boolean isSuccessLogin(LoginDto loginDto) {
        return loginManagerRepository.login(loginDto) == 1;
    }

    /**
     * 로그인 성공 시 처리
     *
     * @param session 세션
     * @param loginDto 로그인 DTO
     * @return 사용자 정보
     */
    private Users onLoginSuccess(HttpSession session, LoginDto loginDto) {
        Users users = memberInfoRepository.findById(loginDto.getId());
        users.successLogin();
        SessionsUser.setSessionUser(session, users);

        return users;
    }
}
