package co.pes.unit.domain.task.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import co.pes.domain.evaluation.repository.JpaEvaluationRepository;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.repository.JpaTaskManagerRepository;
import co.pes.domain.task.repository.JpaTaskOrganizationMappingRepository;
import co.pes.domain.task.service.JpaTaskManagerServiceImpl;
import co.pes.domain.total.service.TotalService;
import co.pes.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaTaskManagerServiceImplTest {

    @InjectMocks
    private JpaTaskManagerServiceImpl jpaTaskManagerServiceImpl;
    @Mock
    private JpaTaskManagerRepository taskManagerRepository;
    @Mock
    private JpaEvaluationRepository evaluationRepository;
    @Mock
    private JpaOrganizationRepository organizationRepository;
    @Mock
    private JpaTaskOrganizationMappingRepository taskOrganizationMappingRepository;
    @Mock
    private TotalService totalService;

    @Test
    void getProjects() {
        // given
        String year = "2024";
        List<Project> expectedProjectList = TestUtils.createDummyProjectList();

        given(taskManagerRepository.searchProjectTitleByYear(year)).willReturn(expectedProjectList);

        // when
        List<Project> actualProjectList = jpaTaskManagerServiceImpl.getProjects(year);

        // then
        assertThat(expectedProjectList).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(actualProjectList);
    }

    @Test
    void getTasks() {
    }

    @Test
    void postMapping() {
    }

    @Test
    void deleteMappingInfo() {
    }

    @Test
    void deleteTasks() {
    }

    @Test
    void resetMapping() {
    }
}