package co.pes.unit.domain.evaluation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.EvaluationRepository;
import co.pes.domain.total.model.Total;
import co.pes.domain.total.repository.TotalRepository;
import co.pes.utils.TestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
class EvaluationRepositoryTest {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private TotalRepository totalRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("평가 정보 목록 조회")
    void getTaskEvaluationInfoList() {
        // given
        List<TaskEvaluation> taskEvaluationList = TestUtils.createDummyTaskEvaluationList();
        String year = "2024";
        Long chargeTeamId = taskEvaluationList.get(0).getChargeTeamId();
        taskEvaluationList.forEach(evaluationRepository::saveTaskEvaluation);

        // when
        List<TaskEvaluation> actual = evaluationRepository.getTaskEvaluationInfoList(year, chargeTeamId);

        // then
        assertAll(
            () -> assertNotNull(actual),
            () -> assertEquals(taskEvaluationList.size(), actual.size()),
            () -> assertThat(actual).usingRecursiveComparison()
                .ignoringFields("insUser", "insDate", "insIp", "modUser", "modDate", "modIp", "taskGb")
                .isEqualTo(taskEvaluationList)
        );
    }

    @Test
    @DisplayName("평가 정보 저장")
    void saveTaskEvaluation() {
        // given
        TaskEvaluation taskEvaluation = TestUtils.createDummyTaskEvaluation();
        String year = "2024";

        // when
        evaluationRepository.saveTaskEvaluation(taskEvaluation);

        // then
        TaskEvaluation actual = evaluationRepository.getTaskEvaluationInfoList(year, taskEvaluation.getChargeTeamId()).get(0);
        assertThat(actual).usingRecursiveComparison()
            .ignoringFields("insUser", "insDate", "insIp", "modUser", "modDate", "modIp", "taskGb")
            .isEqualTo(taskEvaluation);
    }

    @Test
    @DisplayName("평가 정보 수정")
    void updateTaskEvaluation() {
        // given
        TaskEvaluation taskEvaluation = TestUtils.createDummyTaskEvaluation();
        String year = "2024";
        evaluationRepository.saveTaskEvaluation(taskEvaluation);
        TaskEvaluation findTaskEvaluation = evaluationRepository.getTaskEvaluationInfoList(year,
            taskEvaluation.getChargeTeamId()).get(0);
        findTaskEvaluation.changeState("F");

        // when
        evaluationRepository.updateTaskEvaluation(findTaskEvaluation);

        // then
        TaskEvaluation actual = evaluationRepository.getTaskEvaluationInfoList(year, taskEvaluation.getChargeTeamId()).get(0);
        assertAll(
            () -> assertNotNull(actual),
            () -> assertThat(actual).usingRecursiveComparison()
                .ignoringFields("state", "insUser", "insDate", "insIp", "modUser", "modDate", "modIp", "taskGb")
                .isEqualTo(taskEvaluation),
            () -> assertNotEquals(taskEvaluation.getState(), actual.getState()),
            () -> assertEquals(findTaskEvaluation.getState(), actual.getState())
        );
    }

    @Test
    @DisplayName("평가 정보 카운트")
    void countTaskEvaluation() {
        // given
        TaskEvaluation taskEvaluation = TestUtils.createDummyTaskEvaluation();
        evaluationRepository.saveTaskEvaluation(taskEvaluation);

        // when
        int actual = evaluationRepository.countTaskEvaluation(taskEvaluation.getTaskId());

        // then
        assertEquals(1, actual);
    }

    @Test
    @DisplayName("평가 상태 조회")
    void findEvaluationState() {
        // given
        TaskEvaluation taskEvaluation = TestUtils.createDummyTaskEvaluation();
        evaluationRepository.saveTaskEvaluation(taskEvaluation);

        // when
        String actual = evaluationRepository.findEvaluationState(taskEvaluation);

        // then
        assertEquals(taskEvaluation.getState(), actual);
    }

    @Test
    @DisplayName("평가 정보 삭제")
    void deleteTaskEvaluation() {
        // given
        TaskEvaluation taskEvaluation = TestUtils.createDummyTaskEvaluation();
        evaluationRepository.saveTaskEvaluation(taskEvaluation);

        // when
        evaluationRepository.deleteTaskEvaluation(taskEvaluation);

        // then
        int actual = evaluationRepository.countTaskEvaluation(taskEvaluation.getTaskId());
        assertEquals(0, actual);
    }

    @Test
    @DisplayName("최종 평가 처리된 정보는 삭제되지 않음")
    void deleteTaskEvaluation2() {
        // given
        TaskEvaluation taskEvaluation = TestUtils.createDummyTaskEvaluation();
        taskEvaluation.changeState("F");
        evaluationRepository.saveTaskEvaluation(taskEvaluation);

        // when
        evaluationRepository.deleteTaskEvaluation(taskEvaluation);

        // then
        int actual = evaluationRepository.countTaskEvaluation(taskEvaluation.getTaskId());
        assertEquals(1, actual);
    }

    @Test
    @DisplayName("팀 ID로 하위 조직 수 조회")
    void countDescendantOrgByTeamId() {
        // given
        // when
        int actual = evaluationRepository.countDescendantOrgByTeamId(1L);   // 1L = 판매부. 하위 조직 4개 있음

        // then
        assertEquals(4, actual);
    }

    @Test
    @DisplayName("팀 ID 목록으로 평가 정보 조회")
    void getTaskEvaluationInfoListByTeamIdList() {
        // given
        String year = "2024";
        List<Long> chargeTeamIds = Arrays.asList(17L);

        // when
        List<TaskEvaluation> actual = evaluationRepository.getTaskEvaluationInfoListByTeamIdList(year, chargeTeamIds);

        // then
        int excpectedSize = 7;
        assertAll(
            () -> assertNotNull(actual),
            () -> assertEquals(excpectedSize, actual.size()),
            () -> actual.forEach(taskEvaluation -> assertEquals(chargeTeamIds.get(0), taskEvaluation.getChargeTeamId()))
        );
    }

    @Test
    @DisplayName("팀 ID로 하위 조직 ID 목록 조회")
    void getDescendantOrgIdList() {
        // given
        Long chargeTeamId = 1L; // 판매부

        // when
        List<Long> actual = evaluationRepository.getDescendantOrgIdList(chargeTeamId);

        // then
        List<Long> expected = Arrays.asList(11L, 17L, 18L, 23L);  // 판매부 하위 팀 ID 목록
        assertAll(
            () -> assertNotNull(actual),
            () -> assertEquals(expected.size(), actual.size()),
            () -> assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
        );
    }

    @Test
    @DisplayName("평가 정보 총 개수 조회")
    void countTotal() {
        // given
        String year = "2024";
        Total teamTotal = TestUtils.createDummyTeamTotal();
        totalRepository.saveTotal(teamTotal);

        // when
        int actual = evaluationRepository.countTotal(year, teamTotal.getTeamId());

        // then
        assertEquals(1, actual);
    }

    @Test
    @DisplayName("사용자 ID로 담당 팀과 하위 팀 ID 목록 조회")
    void getTeamListByUserId() {
        // given
        String userId = "chb314";   // CEO ID

        // when
        List<Long> actual = evaluationRepository.getTeamListByUserId(userId);

        // then
        List<Long> expected = new ArrayList<>();  // 모든 팀 ID 목록
        for (long i = 1L; i <= 26L; i++) expected.add(i);
        assertAll(
            () -> assertNotNull(actual),
            () -> assertEquals(expected.size(), actual.size()),
            () -> assertThat(actual).containsExactlyInAnyOrderElementsOf(expected)
        );
    }

    @Test
    @DisplayName("하위 팀 중 체크된 팀의 ID 목록만 조회")
    void getLastDescendantOrgIdList() {
        // given
        Long chargeTeamId = 1L; // 판매부
        List<Long> checkTeamIdList = Arrays.asList(18L, 23L);

        // when
        List<Long> actual = evaluationRepository.getLastDescendantOrgIdList(chargeTeamId, checkTeamIdList);

        // then
        List<Long> expected = Arrays.asList(11L, 17L, 18L, 23L);  // 판매부 하위 팀 ID 목록
        assertAll(
            () -> assertNotNull(actual),
            () -> assertNotEquals(expected.size(), actual.size()),
            () -> assertThat(actual).containsExactlyInAnyOrderElementsOf(checkTeamIdList)
        );
    }
}