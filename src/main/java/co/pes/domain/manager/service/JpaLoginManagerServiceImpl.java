package co.pes.domain.manager.service;

import co.pes.common.SessionsUser;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.manager.mapper.LoginManagerMapper;
import co.pes.domain.manager.repository.JpaLoginManagerRepository;
import co.pes.domain.manager.service.dto.LoginDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaMemberInfoRepository;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Primary
@Transactional(readOnly = true)
public class JpaLoginManagerServiceImpl extends AbstractLoginManagerService {

    private final JpaLoginManagerRepository jpaLoginManagerRepository;
    private final JpaMemberInfoRepository jpaMemberInfoRepository;

    public JpaLoginManagerServiceImpl(LoginManagerMapper loginManagerMapper,
        JpaLoginManagerRepository jpaLoginManagerRepository, JpaMemberInfoRepository jpaMemberInfoRepository) {
        super(loginManagerMapper);
        this.jpaLoginManagerRepository = jpaLoginManagerRepository;
        this.jpaMemberInfoRepository = jpaMemberInfoRepository;
    }

    /**
     * 로그인 성공 여부 검증
     *
     * @param loginDto 로그인 DTO
     * @return 로그인 성공 여부
     */
    protected boolean isSuccessLogin(LoginDto loginDto) {
        return jpaLoginManagerRepository.existsByIdAndPassword(loginDto.getId(), loginDto.getPassword());
    }

    /**
     * 로그인 성공 시 처리
     *
     * @param session  세션
     * @param loginDto 로그인 DTO
     * @return 사용자 정보
     */
    protected Users onLoginSuccess(HttpSession session, LoginDto loginDto) {
        Users users = jpaMemberInfoRepository.searchUserById(loginDto.getId())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        users.successLogin();
        SessionsUser.setSessionUser(session, users);

        return users;
    }
}
