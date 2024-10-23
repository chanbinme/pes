package co.pes.unit.domain.total.controller;

import static co.pes.utils.TestUtils.readJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import co.pes.common.SessionsUser;
import co.pes.common.config.WebMvcConfig;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.controller.TotalController;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.service.TotalService;
import co.pes.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(controllers = TotalController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = WebMvcConfig.class))
class TotalControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TotalService totalService;

    private MockedStatic<SessionsUser> sessionUser;

    private final String BASE_URL = "/am/totals";

    @BeforeEach
    void beforeEach() {
        sessionUser = Mockito.mockStatic(SessionsUser.class);
    }

    @AfterEach
    void afterEach() {
        sessionUser.close();
    }

    @Test
    @DisplayName("Officer은 평가 결과 페이지로 이동할 수 없다.")
    void getRankingPage() throws Exception {
        // given
        String selectedYear = "2024";
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyOfficer());

        // when & then
        mockMvc.perform(get(BASE_URL + "/ranking")
            .param("selectedYear", selectedYear))
            .andExpect(status().is(ExceptionCode.ACCESS_DENIED.getStatus()))
            .andExpect(result -> assertInstanceOf(BusinessLogicException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(ExceptionCode.ACCESS_DENIED.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @DisplayName("이미 평가 마감된 연도는 수정 불가능한 랭킹 페이지로 이동된다.")
    void getRankingPage2() throws Exception {
        // given
        String selectedYear = "2024";
        List<String> evaluationYearList = Arrays.asList("2024", "2023", "2022");
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyCeo());
        given(totalService.getEvaluationYearList()).willReturn(evaluationYearList);
        given(totalService.checkEndedYear(Mockito.anyString())).willReturn(true);

        // when & then
        mockMvc.perform(get(BASE_URL + "/ranking")
            .param("selectedYear", selectedYear))
            .andExpect(status().isOk())
            .andExpect(view().name("/ranking/ranking-result"))
            .andExpect(model().attribute("yearList", evaluationYearList))
            .andExpect(model().attribute("selectedYear", selectedYear));
    }

    @Test
    @DisplayName("마감되지 않은 연도는 랭킹 페이지로 이동된다.")
    void getRankingPage3() throws Exception {
        // given
        String selectedYear = "2024";
        List<String> evaluationYearList = Arrays.asList("2024", "2023", "2022");
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyCeo());
        given(totalService.getEvaluationYearList()).willReturn(evaluationYearList);
        given(totalService.checkEndedYear(Mockito.anyString())).willReturn(false);

        // when & then
        mockMvc.perform(get(BASE_URL + "/ranking")
                .param("selectedYear", selectedYear))
            .andExpect(status().isOk())
            .andExpect(view().name("/ranking/ranking"))
            .andExpect(model().attribute("yearList", evaluationYearList))
            .andExpect(model().attribute("selectedYear", selectedYear));
    }

    @Test
    @DisplayName("selectedYear 파라미터가 전달되지 않았을 때 가장 최근의 연도로 설정된다.")
    void getRankingPage4() throws Exception {
        // given
        List<String> evaluationYearList = Arrays.asList("2024", "2023", "2022");
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyCeo());
        given(totalService.getEvaluationYearList()).willReturn(evaluationYearList);
        given(totalService.checkEndedYear(Mockito.anyString())).willReturn(false);

        // when & then
        mockMvc.perform(get(BASE_URL + "/ranking"))
            .andExpect(status().isOk())
            .andExpect(view().name("/ranking/ranking"))
            .andExpect(model().attribute("yearList", evaluationYearList))
            .andExpect(model().attribute("selectedYear", evaluationYearList.get(0)));
    }

    @Test
    @DisplayName("평가 결과를 조회한다.")
    void getRankingTotalList() throws Exception {
        // given
        String year = "2024";
        List<TotalRanking> totalRankingList = TestUtils.createDummyTotalRankingList();
        given(totalService.findTotalListAndCalculateRanking(Mockito.anyString(), Mockito.anyList())).willReturn(totalRankingList);

        // when
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/{year}", year)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readJson("/total/get-total-ranking-request-dto.json")))
            .andExpect(status().isOk()).andReturn();

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        List<TotalRanking> response = objectMapper.readerForListOf(TotalRanking.class)
            .readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertEquals(response.size(), totalRankingList.size());
        assertThat(response).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(totalRankingList);
    }

    @Test
    @DisplayName("미리보기를 위한 평가 결과를 조회한다.")
    void getRankingTotalListForPreview() throws Exception {
        // given
        String year = "2024";
        List<TotalRanking> totalRankingList = TestUtils.createDummyTotalRankingList();
        given(totalService.findTotalListForPreview(Mockito.anyString(), Mockito.anyList())).willReturn(totalRankingList);

        // when
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/preview/{year}", year)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readJson("/total/get-total-ranking-request-dto.json")))
            .andExpect(status().isOk()).andReturn();

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        List<TotalRanking> response = objectMapper.readerForListOf(TotalRanking.class)
            .readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertEquals(response.size(), totalRankingList.size());
        assertThat(response).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(totalRankingList);
    }

    @Test
    @DisplayName("총 평가 결과를 저장한다.")
    void postRankingTotalList() throws Exception {
        // given
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyCeo());
        doNothing().when(totalService).saveTotalRankingList(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString());

        // when & then
        mockMvc.perform(post(BASE_URL + "/ranking")
            .contentType(MediaType.APPLICATION_JSON)
            .content(readJson("/total/post-total-ranking-request-dto.json")))
            .andExpect(status().isOk())
            .andExpect(content().string("저장되었습니다."));
        Mockito.verify(totalService, Mockito.times(1))
            .saveTotalRankingList(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString());
    }

    @Test
    @DisplayName("주어진 연도의 평가 종료 처리 (연도 마감)")
    void endYear() throws Exception {
        // given
        String year = "2024";
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyCeo());
        doNothing().when(totalService).endYear(Mockito.anyString(), Mockito.any(Users.class), Mockito.anyString());

        // when & then
        mockMvc.perform(post(BASE_URL + "/finish")
            .param("year", year))
            .andExpect(status().isOk())
            .andExpect(content().string("마감되었습니다."));
        Mockito.verify(totalService, Mockito.times(1))
            .endYear(Mockito.anyString(), Mockito.any(Users.class), Mockito.anyString());
    }

    @Test
    @DisplayName("주어진 연도의 평가 종료 처리 (연도 마감) - CEO가 아닌 사용자는 마감할 수 없다.")
    void endYear2() throws Exception {
        // given
        String year = "2024";
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyOfficer());

        // when & then
        mockMvc.perform(post(BASE_URL + "/finish")
            .param("year", year))
            .andExpect(status().is(ExceptionCode.ACCESS_DENIED.getStatus()))
            .andExpect(result -> assertInstanceOf(BusinessLogicException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(ExceptionCode.ACCESS_DENIED.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @DisplayName("연도 마감 취소")
    void cancelEndYear() throws Exception {
        // given
        String year = "2024";
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyCeo());
        doNothing().when(totalService).cancelEndYear(Mockito.anyString());

        // when & then
        mockMvc.perform(delete(BASE_URL + "/finish")
            .param("year", year))
            .andExpect(status().isOk())
            .andExpect(content().string("마감 취소되었습니다."));
        Mockito.verify(totalService, Mockito.times(1)).cancelEndYear(Mockito.anyString());
    }

    @Test
    @DisplayName("연도 마감 취소 - CEO가 아닌 사용자는 마감 취소할 수 없다.")
    void cancelEndYear2() throws Exception {
        // given
        String year = "2024";
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(TestUtils.createDummyOfficer());

        // when & then
        mockMvc.perform(delete(BASE_URL + "/finish")
            .param("year", year))
            .andExpect(status().is(ExceptionCode.ACCESS_DENIED.getStatus()))
            .andExpect(result -> assertInstanceOf(BusinessLogicException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(ExceptionCode.ACCESS_DENIED.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @DisplayName("모든 평가가 완료되었는지 확인한다.")
    void checkAllEvaluationsComplete() throws Exception {
        // given
        String year = "2024";
        given(totalService.checkAllEvaluationsComplete(Mockito.anyString())).willReturn(true);

        // when & then
        mockMvc.perform(get(BASE_URL + "/check")
            .param("year", year))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }
}