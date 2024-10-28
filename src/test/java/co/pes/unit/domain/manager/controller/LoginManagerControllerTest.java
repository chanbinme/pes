package co.pes.unit.domain.manager.controller;

import static co.pes.utils.TestUtils.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import co.pes.common.SessionsUser;
import co.pes.domain.manager.controller.LoginManagerController;
import co.pes.domain.manager.controller.dto.LoginRequestDto;
import co.pes.domain.manager.service.LoginManagerService;
import co.pes.domain.member.model.Users;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * @author cbkim
 * @PackageName: co.pes.unit.domain.manager.controller
 * @FileName : LoginManagerControllerTest.java
 * @Date : 2024. 10. 18.
 * @프로그램 설명 : 로그인 관리자 컨트롤러 테스트
 */
@WebMvcTest(LoginManagerController.class)
class LoginManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginManagerService loginManagerService;

    private MockedStatic<SessionsUser> sessionUser;

    private final String BASE_URL = "/am/manager";

    @BeforeEach
    void beforeEach() {
        sessionUser = Mockito.mockStatic(SessionsUser.class);
    }

    @AfterEach
    void afterEach() {
        sessionUser.close();
    }

    @Test
    @DisplayName("로그인 페이지로 이동")
    void login1() throws Exception {
        // given
        sessionUser.when(() -> SessionsUser.isLoginUser(Mockito.any(MockHttpSession.class))).thenReturn(false);

        // when & then
        mockMvc.perform(get(BASE_URL + "/login")
                        .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("/manager/loginForm"))
            .andDo(print());
    }

    @Test
    @DisplayName("이미 로그인한 유저라면 평가 페이지로 이동")
    void login2() throws Exception {
        // given
        sessionUser.when(() -> SessionsUser.isLoginUser(Mockito.any(MockHttpSession.class))).thenReturn(true);

        // when & then
        mockMvc.perform(get(BASE_URL + "/login")
                        .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("/common/result"))
                .andExpect(model().attribute("returnUrl", "/am/tasks-evaluation"))
            .andDo(print());
    }

    @Test
    @DisplayName("로그인 검증 메서드")
    void loginProc() throws Exception {
        // given
        Users user = Users.builder().name("김찬빈").build();
        given(loginManagerService.login(
                Mockito.any(MockHttpSession.class), Mockito.any(LoginRequestDto.class))).willReturn(user);

        // when & then
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/loginProc")
                        .session(new MockHttpSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readJson("/manager/login-request-dto.json")))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String returnString = mvcResult.getResponse().getContentAsString();
        assert (returnString).equals(user.getName() + "님 환영합니다.");
    }

    @Test
    @DisplayName("로그아웃 성공 - 세션 제거 및 로그인 페이지로 이동")
    void logoutSuccess() throws Exception {
        // given
        sessionUser.when(() -> SessionsUser.isLoginUser(Mockito.any(MockHttpSession.class))).thenReturn(true);

        // when & then
        mockMvc.perform(get(BASE_URL + "/logout")
                        .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("/common/result"))
                .andExpect(model().attribute("returnUrl", "/am/manager/login"))
            .andDo(print());

        sessionUser.verify(() -> SessionsUser.removeSessionUser(Mockito.any(MockHttpSession.class)));
    }

    @Test
    @DisplayName("로그아웃 실패 - 로그인 페이지로 이동")
    void logoutFail() throws Exception {
        // given
        sessionUser.when(() -> SessionsUser.isLoginUser(Mockito.any(MockHttpSession.class))).thenReturn(false);

        // when & then
        mockMvc.perform(get(BASE_URL + "/logout")
                        .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("/common/result"))
                .andExpect(model().attribute("returnUrl", "/am/manager/login"))
            .andDo(print());

        sessionUser.verify(() -> SessionsUser.removeSessionUser(Mockito.any(MockHttpSession.class)), Mockito.never());
    }
}