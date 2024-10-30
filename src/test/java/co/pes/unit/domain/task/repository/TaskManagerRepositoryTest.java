package co.pes.unit.domain.task.repository;

import static co.pes.utils.TestUtils.createDummyMappingList;
import static co.pes.utils.TestUtils.createDummyTaskRequestDtoList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.controller.dto.TaskRequestDto;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.repository.TaskManagerRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskManagerRepositoryTest {

    @Autowired
    private TaskManagerRepository taskManagerRepository;

    @Autowired
    private SqlSession sqlSession;

    private final String TASK_QUERY_NAME_SPACE = "co.pes.domain.task.repository.TaskManagerRepository.";

    @ParameterizedTest
    @ValueSource(strings = {"2024", "2023"})
    @DisplayName("특정 연도의 프로젝트 타이틀 목록을 조회합니다.")
    void getProjectListByYear(String year) {
        // when
        List<Project> projectListByYear = taskManagerRepository.getProjectListByYear(year);

        // then
        assertEquals(year.equals("2024") ? 18 : 1, projectListByYear.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"사내 건강 캠페인 프로젝트", "빅데이터 분석 플랫폼 구축"})
    @DisplayName("특정 연도와 프로젝트 타이틀에 해당하는 Task 목록을 조회합니다.")
    void getTaskList(String projectTitle) {
        // given
        String year = "2024";

        // when
        List<Tasks> taskList = taskManagerRepository.getTaskList(year, projectTitle);

        // then
        assertAll(
            () -> taskList.forEach(task -> assertEquals(year, taskList.get(0).getYear())),
            () -> assertEquals(projectTitle.equals("사내 건강 캠페인 프로젝트") ? 9 : 7, taskList.size())
        );
    }

    @Test
    @DisplayName("Task와 팀의 Mapping 정보를 등록합니다.")
    void postMappingInfo() {
        // given
        sqlSession.delete(TASK_QUERY_NAME_SPACE + "deleteAllMappingInfo");
        Mapping mapping = createDummyMappingList().get(0);

        // when
        taskManagerRepository.postMappingInfo(mapping);

        // then
        List<Mapping> mappingList = taskManagerRepository.findMappingInfo(mapping);
        assertAll(
            () -> assertEquals(1, mappingList.size()),
            () -> assertEquals(mapping.getChargeTeamId(), mappingList.get(0).getChargeTeamId()),
            () -> assertEquals(mapping.getTaskId(), mappingList.get(0).getTaskId())
        );
    }

    @ParameterizedTest
    @CsvSource({"박승호, 1", "최예린, 2"})
    @DisplayName("팀 아이디에 해당하는 팀 리더 이름을 조회합니다.")
    void findTeamLeaderNameByChargeTeamId(String teamLeaderName, Long chargeTeamId) {
        // when
        String actual = taskManagerRepository.findTeamLeaderNameByChargeTeamId(chargeTeamId);

        // then
        assertEquals(teamLeaderName, actual);
    }

    @ParameterizedTest
    @CsvSource({"김찬빈, 1", "김찬빈, 2"})
    @DisplayName("팀 아이디에 해당하는 팀의 부서 리더 이름을 조회합니다.")
    void findOfficerNameByChargeTeamId() {
        // when
        String actual = taskManagerRepository.findOfficerNameByChargeTeamId(1L);

        // then
        assertEquals("김찬빈", actual);
    }

    @ParameterizedTest
    @CsvSource({"1, 9", "7, 10"})
    @DisplayName("특정 Task의 담당 팀 ID 목록을 조회합니다.")
    void findChargeTeamIds(Long taskId, Long chargeTeamId) {
        // given
        Tasks task = Tasks.builder().id(taskId).build();

        // when
        List<Long> chargeTeamIds = taskManagerRepository.findChargeTeamIds(task);

        // then
        chargeTeamIds.forEach(id -> assertEquals(chargeTeamId, id));
    }

    @Test
    @DisplayName("팀 아이디에 해당하는 팀의 타이틀 목록을 조회합니다.")
    void findChargeTeamTitles() {
        // given
        List<Long> chargeTeamIds = Arrays.asList(1L, 2L);

        // when
        List<String> actual = taskManagerRepository.findChargeTeamTitles(chargeTeamIds);

        // then
        List<String> chargeTeamTitles = Arrays.asList("판매부", "인사부");
        assertThat(actual).containsExactlyElementsOf(chargeTeamTitles);
    }

    @Test
    @DisplayName("Task 아이디에 해당하는 Task와 팀의 Mapping 정보를 삭제합니다.")
    void resetMappingInfo() {
        // given
        Mapping mapping = createDummyMappingList().get(0);
        taskManagerRepository.postMappingInfo(mapping);
        List<Mapping> beforeMappingList = taskManagerRepository.findMappingInfo(mapping);

        // when
        taskManagerRepository.resetMappingInfo(mapping);

        // then
        List<Mapping> actual = taskManagerRepository.findMappingInfo(mapping);
        assertAll(
            () -> assertNotEquals(beforeMappingList.size(), actual.size()),
            () -> assertEquals(0, actual.size())
        );
    }

    @ParameterizedTest
    @CsvSource({"1, 1", "2, 1"})
    @DisplayName("Task 아이디에 해당하는 Task와 팀의 Mapping 정보를 조회합니다.")
    void findMappingInfo(Long taskId, int size) {
        // given
        Mapping mapping = Mapping.builder().mappingDto(MappingDto.builder().taskId(taskId).build()).build();

        // when
        List<Mapping> actual = taskManagerRepository.findMappingInfo(mapping);

        // then
        assertEquals(size, actual.size());
    }

    @Test
    @DisplayName("Task 아이디에 해당하는 Task 정보를 삭제합니다.")
    void deleteTasks() {
        // given
        List<TaskRequestDto> taskRequestDtoList = createDummyTaskRequestDtoList();
        List<Long> taskIdList = taskRequestDtoList.stream()
            .mapToLong(TaskRequestDto::getTaskId).boxed().collect(Collectors.toList());
        int beforeSize = sqlSession.selectOne(TASK_QUERY_NAME_SPACE + "countTaskIdByTaskIdList", taskIdList);
        sqlSession.delete(TASK_QUERY_NAME_SPACE + "deleteAllMappingInfo");

        // when
        taskManagerRepository.deleteTasks(taskRequestDtoList);

        // then
        int actual = sqlSession.selectOne(TASK_QUERY_NAME_SPACE + "countTaskIdByTaskIdList", taskIdList);
        assertAll(
            () -> assertNotEquals(beforeSize, actual),
            () -> assertEquals(0, actual)
        );
    }

    @Test
    @DisplayName("Task 아이디에 해당하는 Task와 팀의 Mapping 정보의 개수를 조회합니다.")
    void countMappingInfo() {
        // given
        List<TaskRequestDto> taskRequestDtoList = createDummyTaskRequestDtoList();

        // when
        int actual = taskManagerRepository.countMappingInfo(taskRequestDtoList);

        // then
        assertEquals(3, actual);
    }
}