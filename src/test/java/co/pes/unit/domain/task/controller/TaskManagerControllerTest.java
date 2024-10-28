package co.pes.unit.domain.task.controller;

import static co.pes.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import co.pes.common.SessionsUser;
import co.pes.common.config.WebMvcConfig;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.member.model.Users;
import co.pes.domain.task.controller.TaskManagerController;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.service.TaskManagerService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author cbkim
 * @PackageName: co.pes.unit.domain.task
 * @FileName : TaskManagerControllerTest.java
 * @Date : 2024. 10. 21.
 * @프로그램 설명 : 업무 관리 컨트롤러 단위 테스트
 */
@WebMvcTest(controllers = TaskManagerController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = WebMvcConfig.class))
class TaskManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskManagerService taskManagerService;

    @MockBean
    private TotalService totalService;

    private MockedStatic<SessionsUser> sessionUser;
    private final String BASE_URL = "/am/tasks";

    @BeforeEach
    void beforeEach() {
        sessionUser = Mockito.mockStatic(SessionsUser.class);
    }

    @AfterEach
    void afterEach() {
        sessionUser.close();
    }

    @Test
    @DisplayName("Officer는 업무 관리 페이지로 이동할 수 없다.")
    void getTaskListFail() throws Exception {
        // given
        Users officer = createDummyOfficer();
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(officer);

        // when & then
        mockMvc.perform(get("/am/tasks-manager")
                .session(new MockHttpSession()))
            .andExpect(status().is(ExceptionCode.ACCESS_DENIED.getStatus()))
            .andExpect(result -> assertInstanceOf(BusinessLogicException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(ExceptionCode.ACCESS_DENIED.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andDo(print());
    }

    @Test
    @DisplayName("CEO는 업무 관리 페이지로 이동할 수 있다.")
    void getTaskListSuccess() throws Exception {
        // given
        Users ceo = createDummyCeo();
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(ceo);
        List<String> evaluationYearList = Arrays.asList("2024", "2023", "2022");
        given(totalService.getEvaluationYearList()).willReturn(evaluationYearList);

        // when
        mockMvc.perform(get("/am/tasks-manager")
                .session(new MockHttpSession()))
            .andExpect(status().isOk())
            .andExpect(view().name("/task/taskInfoList"))
            .andExpect(model().attribute("yearList", evaluationYearList))
            .andExpect(model().attribute("selectedYear", evaluationYearList.get(0)))
            .andExpect(model().attribute("userInfo", ceo))
            .andDo(print());
    }

    @Test
    @DisplayName("특정 연도의 프로젝트 목록을 조회한다.")
    void getProjects() throws Exception {
        // given
        String year = "2024";
        List<Project> projectList = createDummyProjectList();
        given(taskManagerService.getProjects(year)).willReturn(projectList);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "/projects")
                .param("year", year))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<Project> response = objectMapper.readerForListOf(Project.class).readValue(contentAsString);
        assertEquals(response.size(), projectList.size());
        assertEquals(response.get(0).getProjectTitle(), projectList.get(0).getProjectTitle());
    }

    @Test
    @DisplayName("특정 연도의 프로젝트에 포함된 업무 목록을 조회한다.")
    void getTasks() throws Exception {
        // given
        String year = "2024";
        String projectTitle = "프로젝트1";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("year", year);
        params.add("projectTitle", projectTitle);
        List<Tasks> tasksList = TestUtils.createDummyTasksList();
        given(taskManagerService.getTasks(Mockito.anyString(), Mockito.anyString())).willReturn(tasksList);

        // when & then
        MvcResult mvcResult = mockMvc.perform(get(BASE_URL)
                .params(params))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String contentAsString = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<Tasks> response = objectMapper.readerForListOf(Tasks.class).readValue(contentAsString);
        assertEquals(response.size(), tasksList.size());
        assertEquals(response.get(0).getTaskTitle(), tasksList.get(0).getTaskTitle());
    }

    @Test
    @DisplayName("Officer은 업무 정보를 삭제할 수 없다.")
    void deleteTasksFail1() throws Exception {
        // given
        String year = "2024";
        Users officer = createDummyOfficer();
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(officer);

        // when & then
        mockMvc.perform(delete(BASE_URL + "/{year}", year)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readJson("/task/delete-task-request-dto.json")))
            .andExpect(status().is(ExceptionCode.ACCESS_DENIED.getStatus()))
            .andExpect(result -> assertInstanceOf(BusinessLogicException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(ExceptionCode.ACCESS_DENIED.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andDo(print());
    }

    @Test
    @DisplayName("이미 평가 마감된 연도의 업무 정보는 삭제할 수 없다.")
    void deleteTasksFail2() throws Exception {
        // given
        String year = "2024";
        Users officer = createDummyCeo();
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(officer);
        given(totalService.checkEndedYear(Mockito.anyString())).willReturn(true);

        // when & then
        mockMvc.perform(delete(BASE_URL + "/{year}", year)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readJson("/task/delete-task-request-dto.json")))
            .andExpect(status().is(ExceptionCode.FINISHED_EVALUATION.getStatus()))
            .andExpect(result -> assertInstanceOf(BusinessLogicException.class, result.getResolvedException()))
            .andExpect(result -> assertEquals(ExceptionCode.FINISHED_EVALUATION.getMessage(), Objects.requireNonNull(result.getResolvedException()).getMessage()))
            .andDo(print());
    }

    @Test
    @DisplayName("업무 정보를 삭제한다.")
    void deleteTasksSuccess() throws Exception {
        // given
        String year = "2024";
        Users officer = createDummyCeo();
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(officer);
        given(totalService.checkEndedYear(Mockito.anyString())).willReturn(false);
        doNothing().when(taskManagerService).deleteTasks(Mockito.anyList());

        // when & then
        mockMvc.perform(delete(BASE_URL + "/{year}", year)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readJson("/task/delete-task-request-dto.json")))
            .andExpect(status().isOk())
            .andExpect(content().string("삭제되었습니다."))
            .andDo(print());
    }

    @Test
    @DisplayName("저장하려는 데이터가 없는 경우 업무 정보 생성 안함")
    void taskMappingEmpty() throws Exception {
        // when & then
        mockMvc.perform(post(BASE_URL + "/mappings")
                .content(readJson("/task/empty.json"))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("변경된 내용이 없습니다."))
            .andDo(print());
        Mockito.verify(taskManagerService, Mockito.never())
            .postMapping(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString());
    }

    @Test
    @DisplayName("저장하려는 데이터가 없는 경우 업무 정보 생성 안함")
    void taskMapping() throws Exception {
        // given
        sessionUser.when(() -> SessionsUser.getSessionUser(Mockito.any(MockHttpSession.class))).thenReturn(createDummyCeo());

        // when & then
        mockMvc.perform(post(BASE_URL + "/mappings")
                .content(readJson("/task/post-or-delete-mapping-request-dto.json"))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("저장되었습니다."))
            .andDo(print());
        Mockito.verify(taskManagerService, Mockito.times(1))
            .postMapping(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString());
    }

    @Test
    @DisplayName("삭제하려는 매핑 정보가 없는 경우 삭제 안함")
    void deleteMappingInfoEmpty() throws Exception {
        // when & then
        mockMvc.perform(delete(BASE_URL + "/mappings")
                .content(readJson("/task/empty.json"))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("초기화되었습니다."))
            .andDo(print());
        Mockito.verify(taskManagerService, Mockito.never())
            .deleteMappingInfo(Mockito.anyList());
    }

    @Test
    @DisplayName("삭제하려는 매핑 정보가 없는 경우 삭제 안함")
    void deleteMappingInfo() throws Exception {
        // when & then
        mockMvc.perform(delete(BASE_URL + "/mappings")
                .content(readJson("/task/post-or-delete-mapping-request-dto.json"))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("초기화되었습니다."))
            .andDo(print());
        Mockito.verify(taskManagerService, Mockito.times(1))
            .deleteMappingInfo(Mockito.anyList());
    }
}