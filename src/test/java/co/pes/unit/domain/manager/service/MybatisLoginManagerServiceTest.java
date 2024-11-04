package co.pes.unit.domain.manager.service;

import static co.pes.utils.TestUtils.createDummyCeo;
import static co.pes.utils.TestUtils.createDummyLoginDto;
import static co.pes.utils.TestUtils.createDummyLoginRequestDto;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.manager.controller.dto.LoginRequestDto;
import co.pes.domain.manager.mapper.LoginManagerMapper;
import co.pes.domain.manager.repository.MybatisLoginManagerRepository;
import co.pes.domain.manager.service.MybatisLoginManagerService;
import co.pes.domain.manager.service.dto.LoginDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.MybatisMemberInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

@ExtendWith(MockitoExtension.class)
class MybatisLoginManagerServiceTest {

    @InjectMocks
    private MybatisLoginManagerService mybatisLoginManagerService;

    @Mock
    private MybatisLoginManagerRepository mybatisLoginManagerRepository;
    @Mock
    private MybatisMemberInfoRepository mybatisMemberInfoRepository;
    @Mock
    private LoginManagerMapper loginManagerMapper;

    @Test
    @DisplayName("로그인 성공 시 로그인 처리하여 사용자 정보 반환")
    void login() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        LoginRequestDto loginRequestDto = createDummyLoginRequestDto();
        LoginDto loginDto = createDummyLoginDto();
        Users user = createDummyCeo();
        given(loginManagerMapper.requestDtoToLoginDto(Mockito.any(LoginRequestDto.class))).willReturn(loginDto);
        given(mybatisLoginManagerRepository.login(Mockito.any(LoginDto.class))).willReturn(1);
        given(mybatisMemberInfoRepository.findById(Mockito.anyString())).willReturn(user);

        // when
        Users loginUser = mybatisLoginManagerService.login(session, loginRequestDto);

        // then
        assertAll(
            () -> assertEquals(user.getId(), loginUser.getId()),
            () -> assertEquals(user.getName(), loginUser.getName()),
            () -> assertEquals(user.getPositionGb(), loginUser.getPositionGb()),
            () -> assertEquals(user.getUserLoginYn(), loginUser.getUserLoginYn()),
            () -> assertEquals("Y", loginUser.getUserLoginYn())
        );
    }

    @Test
    @DisplayName("로그인 실패 - 로그인 시도한 id/password로 회원이 조회되지 않으면 예외 발생")
    void login2() throws Exception {
        // given
        MockHttpSession session = new MockHttpSession();
        LoginRequestDto loginRequestDto = createDummyLoginRequestDto();
        LoginDto loginDto = createDummyLoginDto();
        given(loginManagerMapper.requestDtoToLoginDto(Mockito.any(LoginRequestDto.class))).willReturn(loginDto);
        given(mybatisLoginManagerRepository.login(Mockito.any(LoginDto.class))).willReturn(0);

        // when & then
        assertThatThrownBy(() -> mybatisLoginManagerService.login(session, loginRequestDto))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.INVALID_ID_OR_PASSWORD.getMessage());
    }
}
