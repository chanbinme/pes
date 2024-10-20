package co.pes.unit.domain.evaluation.controller;

import co.pes.common.SessionsUser;
import co.pes.common.config.WebMvcConfig;
import co.pes.domain.evaluation.controller.EvaluationController;
import co.pes.domain.evaluation.controller.dto.FinalEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.JobEvaluationResponseDto;
import co.pes.domain.evaluation.service.EvaluationService;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.service.TotalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static co.pes.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EvaluationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = WebMvcConfig.class))
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationService evaluationService;

    @MockBean
    private TotalService totalService;

    private final String BASE_URL = "/am/jobs-evaluation";

    @Test
    @DisplayName("평가 데이터를 저장한다.")
    void postEvaluationData() throws Exception {
        try (MockedStatic<SessionsUser> sessionUser = Mockito.mockStatic(SessionsUser.class)) {
            // given
            Users user = Users.builder().build();
            sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(user);
            doNothing().when(evaluationService).saveJobEvaluationList(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString());

            // when & then
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(readJson("/evaluation/post-job-evaluation-request-dto-list.json"))
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("저장되었습니다."));
        }
    }

    @Test
    @DisplayName("최종 평가 데이터를 저장한다.")
    void finalPostEvaluationData() throws Exception {
        try (MockedStatic<SessionsUser> sessionUser = Mockito.mockStatic(SessionsUser.class)) {
            // given
            Users user = Users.builder().build();
            sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(user);
            doNothing().when(evaluationService)
                    .finalSaveJobEvaluationList(Mockito.any(FinalEvaluationRequestDto.class), Mockito.any(Users.class), Mockito.anyString());

            // when & then
            mockMvc.perform(post(BASE_URL + "/final")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(readJson("/evaluation/post-final-evaluation-request-dto.json"))
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("최종 제출되었습니다."));
        }
    }

    @Test
    @DisplayName("임원은 임원 평가 기간이 아닌 경우 평가 리스트를 조회할 수 없다.")
    void getJobEvaluationListFail() throws Exception {
        Users user = Users.builder().positionGb("1").build();
        try (MockedStatic<SessionsUser> sessionUser = Mockito.mockStatic(SessionsUser.class)) {
            // given
            sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(user);

            given(evaluationService.checkOfficerEvaluationPeriod()).willReturn("임원 평가 기간이 아닙니다.");

            // when & then
            mockMvc.perform(get(BASE_URL)
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/common/result"))
                    .andExpect(model().attribute("returnUrl", "/am/manager/logout"))
                    .andExpect(model().attribute("message", "임원 평가 기간이 아닙니다."));
        }
    }

    @Test
    @DisplayName("임원은 임원 평가 기간인 경우 평가 리스트를 조회할 수 있다.")
    void getJobEvaluationListSuccess1() throws Exception {
        try (MockedStatic<SessionsUser> sessionUser = Mockito.mockStatic(SessionsUser.class)) {
            // given
            Users user = Users.builder().positionGb("1").build();
            List<String> yearList = List.of("2024", "2023");
            sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(user);
            given(evaluationService.checkOfficerEvaluationPeriod()).willReturn("임원 평가 기간입니다.");
            given(totalService.getEvaluationYearList()).willReturn(yearList);

            // when & then
            mockMvc.perform(get(BASE_URL)
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/evaluation/evaluation"))
                    .andExpect(model().attribute("yearList", yearList))
                    .andExpect(model().attribute("selectedYear", yearList.get(0)))
                    .andExpect(model().attribute("userInfo", user));
            sessionUser.verify(() -> SessionsUser.removeSessionUser(Mockito.any(MockHttpSession.class)), Mockito.never());
            Mockito.verify(evaluationService, Mockito.times(1)).checkOfficerEvaluationPeriod();
        }
    }

    @Test
    @DisplayName("CEO는 임원 평가 기간에 상관없이 평가 리스트를 조회할 수 있다.")
    void getJobEvaluationListSuccess2() throws Exception {
        try (MockedStatic<SessionsUser> sessionUser = Mockito.mockStatic(SessionsUser.class)) {
            // given
            Users user = Users.builder().positionGb("2").build();
            List<String> yearList = List.of("2024", "2023");
            sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(user);
            given(evaluationService.checkOfficerEvaluationPeriod()).willReturn("임원 평가 기간입니다.");
            given(totalService.getEvaluationYearList()).willReturn(yearList);

            // when & then
            mockMvc.perform(get(BASE_URL)
                            .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/evaluation/evaluation"))
                    .andExpect(model().attribute("yearList", yearList))
                    .andExpect(model().attribute("selectedYear", yearList.get(0)))
                    .andExpect(model().attribute("userInfo", user));
            sessionUser.verify(() -> SessionsUser.removeSessionUser(Mockito.any(MockHttpSession.class)), Mockito.never());
            Mockito.verify(evaluationService, Mockito.never()).checkOfficerEvaluationPeriod();
        }
    }

    @Test
    @DisplayName("평가 데이터를 조회한다.")
    void getEvaluationInfo() throws Exception {
        try(MockedStatic<SessionsUser> sessionUser = Mockito.mockStatic(SessionsUser.class)) {
            // given
            String year = "2024";
            String chargeTeamId = "1";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.set("year", year);
            params.set("chargeTeamId", chargeTeamId);
            Users user = createDummyOfficer();
            JobEvaluationResponseDto jobEvaluationResponseDto = createDummyJobEvaluationResponseDto();
            sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(user);
            given(evaluationService.getEvaluationInfo(Mockito.anyString(), Mockito.anyLong(), Mockito.any(Users.class))).willReturn(jobEvaluationResponseDto);

            // when & then
            MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "/jobs")
                    .params(params)
                    .session(new MockHttpSession()))
                    .andExpect(status().isOk())
                    .andReturn();

            String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            JobEvaluationResponseDto response = objectMapper.readValue(contentAsString, JobEvaluationResponseDto.class);
            assertEquals(response.isExistsTotal(), jobEvaluationResponseDto.isExistsTotal());
            assertThat(response.getJobEvaluationList())
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyElementsOf(jobEvaluationResponseDto.getJobEvaluationList());
        }
    }
}
