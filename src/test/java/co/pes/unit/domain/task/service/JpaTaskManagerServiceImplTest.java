package co.pes.unit.domain.task.service;

import static co.pes.utils.TestUtils.createDummyCeo;
import static co.pes.utils.TestUtils.createDummyMappedTeamIdList;
import static co.pes.utils.TestUtils.createDummyMappingDtoList;
import static co.pes.utils.TestUtils.createDummyOrganizationEntity;
import static co.pes.utils.TestUtils.createDummyProjectList;
import static co.pes.utils.TestUtils.createDummyTaskEntity;
import static co.pes.utils.TestUtils.createDummyTasksList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.repository.JpaEvaluationRepository;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.controller.dto.TaskRequestDto;
import co.pes.domain.task.entity.TaskEntity;
import co.pes.domain.task.entity.TaskOrganizationMappingEntity;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.repository.JpaTaskManagerRepository;
import co.pes.domain.task.repository.JpaTaskOrganizationMappingRepository;
import co.pes.domain.task.service.JpaTaskManagerServiceImpl;
import co.pes.domain.total.service.TotalService;
import co.pes.utils.TestUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        List<Project> expectedProjectList = createDummyProjectList();

        given(taskManagerRepository.searchProjectTitleByYear(year)).willReturn(expectedProjectList);

        // when
        List<Project> actualProjectList = jpaTaskManagerServiceImpl.getProjects(year);

        // then
        assertThat(expectedProjectList).usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(actualProjectList);
    }

    @Test
    @DisplayName("특정 연도의 프로젝트에 포함된 업무 목록을 조회합니다.")
    void getTasks() {
        // given
        String year = "2024";
        String projectTitle = "project";
        List<Long> chargeTeamIds = Arrays.asList(1L, 2L, 3L);
        List<String> chargeTeamTitles = Arrays.asList("team1", "team2", "team3");
        List<Tasks> expected = createDummyTasksList();
        given(taskManagerRepository.searchTasksByYearAndProjectTitle(Mockito.anyString(), Mockito.anyString())).willReturn(expected);
        given(taskManagerRepository.searchChargeTeamIdsByTaskId(Mockito.anyLong())).willReturn(chargeTeamIds, null, null);
        given(organizationRepository.searchChargeTeamTitlesByTeamIds(Mockito.anyList())).willReturn(chargeTeamTitles);

        // when
        List<Tasks> actual = jpaTaskManagerServiceImpl.getTasks(year, projectTitle);

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).containsExactlyElementsOf(expected),
            () -> assertThat(actual.get(0).getChargeTeamIds()).containsExactlyElementsOf(chargeTeamIds),
            () -> assertThat(actual.get(0).getChargeTeamTitles()).containsExactlyElementsOf(chargeTeamTitles),
            () -> assertThat(actual.get(1).getChargeTeamIds()).isNull(),
            () -> assertThat(actual.get(1).getChargeTeamTitles()).isNull()
        );
    }

    @Test
    @DisplayName("매핑 하려는 업무를 이미 매핑하고 있는 팀이 최종 평가된 경우 매핑 초기화 할 수 없습니다. (예외 발생)")
    void postMapping() {
        // given
        List<MappingDto> mappingDtoList = createDummyMappingDtoList();
        Users user = createDummyCeo();
        String userIp = "userIp";

        given(taskManagerRepository.getReferenceById(Mockito.anyLong())).willReturn(createDummyTaskEntity());
        given(organizationRepository.getReferenceById(Mockito.anyLong())).willReturn(createDummyOrganizationEntity());
        given(taskManagerRepository.searchTeamIdByTaskId(Mockito.anyLong())).willReturn(createDummyMappedTeamIdList());
        given(totalService.existsTotal(Mockito.anyLong())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jpaTaskManagerServiceImpl.postMapping(mappingDtoList, user, userIp))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.INVALID_MAPPING.getMessage());
    }

    @Test
    @DisplayName("최종 평가 완료된 팀은 업무 매핑을 할 수 없습니다. (예외 발생)")
    void postMapping2() {
        // given
        List<MappingDto> mappingDtoList = Collections.singletonList(createDummyMappingDtoList().get(0));
        Users user = createDummyCeo();
        String userIp = "userIp";
        TaskEntity dummyTaskEntity = createDummyTaskEntity();
        OrganizationEntity dummyOrganizationEntity = createDummyOrganizationEntity();
        List<Long> dummyMappedTeamIdList = Collections.singletonList(createDummyMappedTeamIdList().get(0));

        given(taskManagerRepository.getReferenceById(Mockito.anyLong())).willReturn(dummyTaskEntity);
        given(organizationRepository.getReferenceById(Mockito.anyLong())).willReturn(dummyOrganizationEntity);
        given(taskManagerRepository.searchTeamIdByTaskId(Mockito.anyLong())).willReturn(dummyMappedTeamIdList);
        given(totalService.existsTotal(Mockito.anyLong())).willReturn(false, true);
        doNothing().when(taskOrganizationMappingRepository).deleteByTaskId(Mockito.anyLong());

        // when & then
        assertThatThrownBy(() -> jpaTaskManagerServiceImpl.postMapping(mappingDtoList, user, userIp))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.INVALID_MAPPING.getMessage());
    }

    @Test
    @DisplayName("팀과 업무 매핑 정보를 저장합니다.")
    void postMapping3() {
        // given
        List<MappingDto> mappingDtoList = createDummyMappingDtoList();
        Users user = createDummyCeo();
        String userIp = "userIp";
        TaskEntity dummyTaskEntity = createDummyTaskEntity();
        OrganizationEntity dummyOrganizationEntity = createDummyOrganizationEntity();

        given(taskManagerRepository.getReferenceById(Mockito.anyLong())).willReturn(dummyTaskEntity);
        given(organizationRepository.getReferenceById(Mockito.anyLong())).willReturn(dummyOrganizationEntity);
        given(taskManagerRepository.searchTeamIdByTaskId(Mockito.anyLong())).willReturn(createDummyMappedTeamIdList());
        given(totalService.existsTotal(Mockito.anyLong())).willReturn(false);
        doNothing().when(taskOrganizationMappingRepository).deleteByTaskId(Mockito.anyLong());

        // when
        jpaTaskManagerServiceImpl.postMapping(mappingDtoList, user, userIp);

        // then
        ArgumentCaptor<List<TaskOrganizationMappingEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(taskOrganizationMappingRepository).saveAll(captor.capture());
        TaskOrganizationMappingEntity actualTaskOrganizationMappingEntity= captor.getValue().get(0);
        assertAll(
            () -> Mockito.verify(taskOrganizationMappingRepository, Mockito.times(1)).saveAll(Mockito.anyList()),
            () -> assertThat(dummyTaskEntity).usingRecursiveComparison().isEqualTo(actualTaskOrganizationMappingEntity.getTask()),
            () -> assertThat(dummyOrganizationEntity).usingRecursiveComparison().isEqualTo(actualTaskOrganizationMappingEntity.getOrganization())
        );
    }

    @Test
    @DisplayName("최종 평가 완료된 팀의 업무 매핑 정보를 삭제할 수 없습니다. (예외 발생)")
    void deleteMappingInfo() {
        // given
        List<MappingDto> mappingDtoList = createDummyMappingDtoList();
        TaskEntity dummyTaskEntity = createDummyTaskEntity();
        OrganizationEntity dummyOrganizationEntity = createDummyOrganizationEntity();

        given(taskManagerRepository.getReferenceById(Mockito.anyLong())).willReturn(dummyTaskEntity);
        given(organizationRepository.getReferenceById(Mockito.anyLong())).willReturn(dummyOrganizationEntity);
        given(evaluationRepository.containsFinalSaveEvaluation(Mockito.anyList())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jpaTaskManagerServiceImpl.deleteMappingInfo(mappingDtoList))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.FINAL_SAVE_EVALUATION.getMessage());
    }

    @Test
    @DisplayName("업무 - 팀 매핑 정보를 삭제합니다.")
    void deleteMappingInfo2() {
        // given
        List<MappingDto> mappingDtoList = createDummyMappingDtoList();
        TaskEntity dummyTaskEntity = createDummyTaskEntity();
        OrganizationEntity dummyOrganizationEntity = createDummyOrganizationEntity();

        given(taskManagerRepository.getReferenceById(Mockito.anyLong())).willReturn(dummyTaskEntity);
        given(organizationRepository.getReferenceById(Mockito.anyLong())).willReturn(dummyOrganizationEntity);
        given(evaluationRepository.containsFinalSaveEvaluation(Mockito.anyList())).willReturn(false);
        given(taskManagerRepository.searchTeamIdByTaskId(Mockito.anyLong())).willReturn(createDummyMappedTeamIdList());
        given(totalService.existsTotal(Mockito.anyLong())).willReturn(false);

        // when
        jpaTaskManagerServiceImpl.deleteMappingInfo(mappingDtoList);

        // then
        assertAll(
            () -> Mockito.verify(evaluationRepository, Mockito.times(1)).removeAllByIdList(Mockito.anyList()),
            () -> Mockito.verify(taskOrganizationMappingRepository, Mockito.times(mappingDtoList.size())).deleteByTaskId(Mockito.anyLong())
        );
    }

    @Test
    @DisplayName("이미 매핑된 업무는 삭제할 수 없습니다.")
    void deleteTasks() {
        // given
        List<TaskRequestDto> taskRequestDtoList = TestUtils.createDummyTaskRequestDtoList();
        given(evaluationRepository.existsByIdTaskIdIn(Mockito.anyList())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jpaTaskManagerServiceImpl.deleteTasks(taskRequestDtoList))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.ALREADY_EXISTS_MAPPING.getMessage());
    }

    @Test
    @DisplayName("업무 정보를 삭제합니다.")
    void deleteTasks2() {
        // given
        List<TaskRequestDto> taskRequestDtoList = TestUtils.createDummyTaskRequestDtoList();
        given(evaluationRepository.existsByIdTaskIdIn(Mockito.anyList())).willReturn(false);

        // when
        jpaTaskManagerServiceImpl.deleteTasks(taskRequestDtoList);

        // then
        Mockito.verify(taskManagerRepository, Mockito.times(1)).removeAllByIdList(Mockito.anyList());
    }
}