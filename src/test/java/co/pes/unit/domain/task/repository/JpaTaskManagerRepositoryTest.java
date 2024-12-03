package co.pes.unit.domain.task.repository;

import static co.pes.utils.TestUtils.createDummyNewTaskEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.pes.common.config.QueryDslConfig;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.task.entity.TaskEntity;
import co.pes.domain.task.entity.TaskOrganizationMappingEntity;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.repository.JpaTaskManagerRepository;
import co.pes.domain.task.repository.JpaTaskOrganizationMappingRepository;
import co.pes.utils.TestUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaTaskManagerRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JpaTaskManagerRepository taskManagerRepository;

    @Autowired
    private JpaTaskOrganizationMappingRepository taskOrganizationMappingRepository;

    @ParameterizedTest
    @ValueSource(strings = {"2024", "2023"})
    @DisplayName("특정 연도의 프로젝트 타이틀 목록을 조회합니다.")
    void getProjectListByYear(String year) {
        // when
        List<Project> projectListByYear = taskManagerRepository.searchProjectTitleByYear(year);

        // then
        assertEquals(year.equals("2024") ? 18 : 1, projectListByYear.size());
    }

    @Test
    @DisplayName("특정 연도와 프로젝트 타이틀에 해당하는 Task 목록을 조회합니다.")
    void searchTasksByYearAndProjectTitle() {
        // given
        String expectedYear = "2024";
        String projectTitle = "Test Project";
        List<TaskEntity> dummyTaskEntityList = TestUtils.createDummyTaskEntityList(projectTitle);
        List<TaskEntity> expected = taskManagerRepository.saveAll(dummyTaskEntityList);

        // when
        List<Tasks> actual = taskManagerRepository.searchTasksByYearAndProjectTitle(expectedYear, projectTitle);

        // then
        assertAll(
            () -> actual.forEach(tasks -> assertEquals(expectedYear, tasks.getYear())),
            () -> assertEquals(expected.size(), actual.size())
        );
    }

    @Test
    @DisplayName("특정 Task를 담당하는 Team ID 목록을 조회합니다.")
    void searchChargeTeamIdsByTaskId() {
        // given
        taskOrganizationMappingRepository.deleteAll();
        Random random = new Random();
        long taskId = (long) random.nextInt(100) + 1;
        List<TaskOrganizationMappingEntity> dummyMappingEntityList = TestUtils.createDummyMappingEntityList(taskId);
        taskOrganizationMappingRepository.saveAll(dummyMappingEntityList);

        // when
        List<Long> actualChargeTeamIdList = taskManagerRepository.searchChargeTeamIdsByTaskId(taskId);

        // then
        List<Long> expectedChargeTeamIdList = dummyMappingEntityList.stream()
            .map(TaskOrganizationMappingEntity::getOrganization)
            .map(OrganizationEntity::getId).collect(Collectors.toList());
        assertThat(actualChargeTeamIdList).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(expectedChargeTeamIdList);
    }

    @Test
    @DisplayName("특정 Task를 담당하는 Team ID를 조회합니다.")
    void searchTeamIdByTaskId() {
        // given
        taskOrganizationMappingRepository.deleteAllInBatch();
        Random random = new Random();
        long taskId = (long) random.nextInt(100) + 1;
        List<TaskOrganizationMappingEntity> dummyMappingEntityList = TestUtils.createDummyMappingEntityList(taskId);
        taskOrganizationMappingRepository.saveAll(dummyMappingEntityList);

        // when
        List<Long> actualChargeTeamIdList = taskManagerRepository.searchTeamIdByTaskId(taskId);

        // then
        List<Long> expectedChargeTeamIdList = dummyMappingEntityList.stream()
            .map(TaskOrganizationMappingEntity::getOrganization)
            .map(OrganizationEntity::getId).sorted().collect(Collectors.toList());
        assertThat(actualChargeTeamIdList).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(expectedChargeTeamIdList);
    }

    @Test
    @DisplayName("특정 Task ID 목록을 삭제합니다.")
    void removeAllByIdList() {
        // given
        Random random = new Random();
        long taskId1 = (long) random.nextInt(100) + 1;
        long taskId2 = (long) random.nextInt(100) + 1;
        //TODO: Repository에 의존하지 않고 데이터 초기화할 수 있도록 리팩토링 필요
        taskOrganizationMappingRepository.deleteAllInBatch();
        boolean isExistingTask1 = taskManagerRepository.findById(taskId1).isPresent();
        boolean isExistingTask2 = taskManagerRepository.findById(taskId2).isPresent();
        em.clear();

        // when
        taskManagerRepository.removeAllByIdList(Arrays.asList(taskId1, taskId2));

        // then
        boolean isDeletedTask1 = taskManagerRepository.findById(taskId1).isPresent();
        boolean isDeletedTask2 = taskManagerRepository.findById(taskId2).isPresent();
        assertAll(
            () -> assertTrue(isExistingTask1),
            () -> assertTrue(isExistingTask2),
            () -> assertFalse(isDeletedTask1),
            () -> assertFalse(isDeletedTask2)
        );
    }
    
    @Test
    @DisplayName("특정 Task ID로 Task를 조회합니다.")
    void findById() {
        // given
        TaskEntity dummyTaskEntity = createDummyNewTaskEntity();
        TaskEntity expected = taskManagerRepository.save(dummyTaskEntity);

        // when
        TaskEntity actual = taskManagerRepository.findById(expected.getId()).orElse(null);
        
        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}