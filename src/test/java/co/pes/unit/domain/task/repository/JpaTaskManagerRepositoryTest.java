package co.pes.unit.domain.task.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.pes.common.config.QueryDslConfig;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.repository.JpaTaskManagerRepository;
import co.pes.domain.task.repository.JpaTaskOrganizationMappingRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
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

    @ParameterizedTest
    @ValueSource(strings = {"사내 건강 캠페인 프로젝트", "빅데이터 분석 플랫폼 구축"})
    @DisplayName("특정 연도와 프로젝트 타이틀에 해당하는 Task 목록을 조회합니다.")
    void getTaskList(String projectTitle) {
        // given
        String expectedYear = "2024";

        // when
        List<Tasks> taskList = taskManagerRepository.searchTasksByYearAndProjectTitle(expectedYear, projectTitle);

        // then
        assertAll(
            () -> taskList.forEach(actual -> assertEquals(expectedYear, actual.getYear())),
            () -> assertEquals(projectTitle.equals("사내 건강 캠페인 프로젝트") ? 9 : 7, taskList.size()
        ));
    }
}