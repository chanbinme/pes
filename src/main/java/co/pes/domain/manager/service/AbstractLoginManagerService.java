package co.pes.domain.manager.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.manager.controller.dto.LoginRequestDto;
import co.pes.domain.manager.mapper.LoginManagerMapper;
import co.pes.domain.manager.service.dto.LoginDto;
import co.pes.domain.member.model.Users;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public abstract class AbstractLoginManagerService implements LoginManagerService {

        protected final LoginManagerMapper loginManagerMapper;

        @Override
        public Users login(HttpSession session, LoginRequestDto requestDto) throws Exception {
            LoginDto loginDto = loginManagerMapper.requestDtoToLoginDto(requestDto);
            if (this.isSuccessLogin(loginDto)) {
                return this.onLoginSuccess(session, loginDto);
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_ID_OR_PASSWORD);
            }
        }

        protected abstract boolean isSuccessLogin(LoginDto loginDto);

        protected abstract Users onLoginSuccess(HttpSession session, LoginDto loginDto);
}
