package co.pes.unit.domain.evaluation.repository;

import static co.pes.utils.TestUtils.createDummyTaskEvaluationEntity;
import static co.pes.utils.TestUtils.createDummyTaskEvaluationEntityList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.pes.common.config.QueryDslConfig;
import co.pes.domain.evaluation.entity.TaskEvaluationEntity;
import co.pes.domain.evaluation.entity.TaskEvaluationEntityId;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.JpaEvaluationRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaEvaluationRepositoryTest {

    @Autowired
    private JpaEvaluationRepository evaluationRepository;

    @Test
    @DisplayName("팀 아이디로 평가 정보 목록 조회")
    void searchTaskEvaluationInfoListByTeamId() {
        // given
        String year = "2024";
        Long chargeTeamId = (long) (new Random().nextInt(25) + 1);

        // when
        List<TaskEvaluation> actual = evaluationRepository.searchTaskEvaluationInfoListByTeamId(year, chargeTeamId);

        // then
        assertAll(
            () -> assertNotNull(actual),
            () -> actual.forEach(e -> assertEquals(chargeTeamId, e.getChargeTeamId()))
        );
    }

    @Test
    @DisplayName("팀 아이디 리스트로 평가 정보 목록 조회")
    void searchTaskEvaluationInfoListByTeamIdList() {
        // given
        Random random = new Random();
        long chargeTeamId1 = (long) random.nextInt(25) + 1;
        long chargeTeamId2 = (long) random.nextInt(25) + 1;
        long chargeTeamId3 = (long) random.nextInt(25) + 1;
        List<Long> chargeTeamIdList = Arrays.asList(chargeTeamId1, chargeTeamId2, chargeTeamId3);
        String year = "2024";

        // when
        List<TaskEvaluation> actual = evaluationRepository.searchTaskEvaluationInfoListByTeamIdList(year, chargeTeamIdList);

        // then
        assertAll(
            () -> assertNotNull(actual),
            () -> actual.forEach(e -> {
                long actualChargeTeamId = e.getChargeTeamId();
                assertThat(chargeTeamIdList).contains(actualChargeTeamId);
            })
        );
    }

    @ParameterizedTest
    @DisplayName("최종 저장된 평가 정보가 있는지 확인한다.")
    @CsvSource({"S, false", "F, true"})
    void containsFinalSaveEvaluation(String state,  boolean expected) {
        // given
        TaskEvaluationEntity dummyTaskEvaluationEntity = createDummyTaskEvaluationEntity();
        dummyTaskEvaluationEntity.changeState(state);
        evaluationRepository.save(dummyTaskEvaluationEntity);

        // when
        boolean actual = evaluationRepository.containsFinalSaveEvaluation(Arrays.asList(dummyTaskEvaluationEntity.getId().getTaskId()));

        // then
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("평가 정보 ID 목록으로 평가 정보 목록을 삭제한다.")
    void removeAllByIdList() {
        // given
        List<TaskEvaluationEntity> expected = evaluationRepository.saveAll(createDummyTaskEvaluationEntityList());
        List<TaskEvaluationEntityId> taskEvaluationEntityIdList = expected.stream()
            .map(TaskEvaluationEntity::getId).collect(Collectors.toList());

        // when
        evaluationRepository.removeAllByIdList(taskEvaluationEntityIdList);
        
        // then
        List<TaskEvaluationEntity> actual = evaluationRepository.findAllById(taskEvaluationEntityIdList);
        assertAll(
            () -> assertThat(actual).isEmpty(),
            () -> assertNotEquals(expected.size(), actual.size())
        );
    }

    @Test
    @DisplayName("업무 ID로 평가 정보가 존재하는지 확인한다.")
    void existsByIdTaskId() {
        // given
        TaskEvaluationEntity taskEvaluationEntity = evaluationRepository.save(createDummyTaskEvaluationEntity());

        // when
        boolean successActual = evaluationRepository.existsByIdTaskId(taskEvaluationEntity.getId().getTaskId());
        boolean fasleActual = evaluationRepository.existsByIdTaskId(0L);

        // then
        assertAll(
            () -> assertTrue(successActual),
            () -> assertFalse(fasleActual)
        );
    }

    @Test
    @DisplayName("업무 ID 목록으로 평가 정보가 존재하는지 확인한다.")
    void existsByIdTaskIdIn() {
        // given
        List<TaskEvaluationEntity> expected = evaluationRepository.saveAll(createDummyTaskEvaluationEntityList());
        List<Long> taskIdList = expected.stream()
            .map(taskEvaluationEntity -> taskEvaluationEntity.getId().getTaskId()).collect(Collectors.toList());
        // when
        boolean successActual = evaluationRepository.existsByIdTaskIdIn(taskIdList);
        boolean fasleActual = evaluationRepository.existsByIdTaskIdIn(Arrays.asList(0L));

        // then
        assertAll(
            () -> assertTrue(successActual),
            () -> assertFalse(fasleActual)
        );
    }
}