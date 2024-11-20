package co.pes.domain.manager.service;

import co.pes.common.SessionsUser;
import co.pes.domain.manager.mapper.LoginManagerMapper;
import co.pes.domain.manager.repository.MybatisLoginManagerRepository;
import co.pes.domain.manager.service.dto.LoginDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.MybatisMemberInfoRepository;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MybatisLoginManagerServiceImpl extends AbstractLoginManagerService {

    private final MybatisLoginManagerRepository mybatisLoginManagerRepository;
    private final MybatisMemberInfoRepository mybatisMemberInfoRepository;

    public MybatisLoginManagerServiceImpl(LoginManagerMapper loginManagerMapper,
        MybatisLoginManagerRepository mybatisLoginManagerRepository, MybatisMemberInfoRepository mybatisMemberInfoRepository) {
        super(loginManagerMapper);
        this.mybatisLoginManagerRepository = mybatisLoginManagerRepository;
        this.mybatisMemberInfoRepository = mybatisMemberInfoRepository;
    }

    /**
     * 로그인 성공 여부 검증
     *
     * @param loginDto 로그인 DTO
     * @return 로그인 성공 여부
     */
    protected boolean isSuccessLogin(LoginDto loginDto) {
        return mybatisLoginManagerRepository.login(loginDto) == 1;
    }

    /**
     * 로그인 성공 시 처리
     *
     * @param session 세션
     * @param loginDto 로그인 DTO
     * @return 사용자 정보
     */
    protected Users onLoginSuccess(HttpSession session, LoginDto loginDto) {
        Users users = mybatisMemberInfoRepository.findById(loginDto.getId());
        users.successLogin();
        SessionsUser.setSessionUser(session, users);

        return users;
    }
}
