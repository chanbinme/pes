package co.pes.unit.domain.task.service;

import static co.pes.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.EvaluationRepository;
import co.pes.domain.member.model.Users;
import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.controller.dto.TaskRequestDto;
import co.pes.domain.task.mapper.TaskInfoMapper;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.repository.TaskManagerRepository;
import co.pes.domain.task.service.TaskManagerService;
import co.pes.domain.total.service.TotalService;
import co.pes.utils.TestUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskManagerServiceTest {

    @InjectMocks
    private TaskManagerService taskManagerService;

    @Mock
    private TaskManagerRepository taskManagerRepository;
    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private TotalService totalService;
    @Mock
    private TaskInfoMapper taskInfoMapper;

    @Test
    @DisplayName("특정 연도의 프로젝트 목록을 조회합니다.")
    void getProjects() {
        // given
        String year = "2024";
        List<Project> expected = createDummyProjectList();
        given(taskManagerRepository.getProjectListByYear(Mockito.anyString())).willReturn(expected);

        // when
        List<Project> actual = taskManagerService.getProjects(year);

        // then
        assertAll(
            () -> assertThat(actual).isNotNull(),
            () -> assertThat(actual).hasSize(expected.size()),
            () -> assertThat(actual).containsExactlyElementsOf(expected)
        );
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
        given(taskManagerRepository.getTaskList(Mockito.anyString(), Mockito.anyString())).willReturn(expected);
        given(taskManagerRepository.findChargeTeamIds(Mockito.any(Tasks.class))).willReturn(chargeTeamIds, null, null);
        given(taskManagerRepository.findChargeTeamTitles(Mockito.anyList())).willReturn(chargeTeamTitles, null, null);

        // when
        List<Tasks> actual = taskManagerService.getTasks(year, projectTitle);

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
        List<Mapping> mappingList = createDummyMappingList();
        Users user = createDummyCeo();
        String userIp = "userIp";
        given(taskInfoMapper.mappingDtoListToMappingList(Mockito.anyList(), Mockito.any(),
            Mockito.anyString())).willReturn(mappingList);
        given(taskManagerRepository.findMappingInfo(Mockito.any(Mapping.class))).willReturn(mappingList);
        given(totalService.existsTotal(Mockito.any(Long.class))).willReturn(false, false, true);

        // when & then
        assertThatThrownBy(() -> taskManagerService.postMapping(mappingDtoList, user, userIp))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.INVALID_MAPPING.getMessage());
    }

    @Test
    @DisplayName("최종 평가 완료된 팀은 업무 매핑을 할 수 없습니다. (예외 발생)")
    void postMapping2() {
        // given
        List<MappingDto> mappingDtoList = createDummyMappingDtoList();
        List<Mapping> mappingList = Collections.singletonList(createDummyMappingList().get(0));
        Users user = createDummyCeo();
        String userIp = "userIp";
        String chargeTeam = "팀장";
        String chargeOfficer = "임원";
        given(taskInfoMapper.mappingDtoListToMappingList(Mockito.anyList(), Mockito.any(),
            Mockito.anyString())).willReturn(mappingList);
        given(taskManagerRepository.findMappingInfo(Mockito.any(Mapping.class))).willReturn(mappingList);
        given(totalService.existsTotal(Mockito.any(Long.class))).willReturn(false, true);
        doNothing().when(taskManagerRepository).resetMappingInfo(Mockito.any(Mapping.class));
        given(taskManagerRepository.findTeamLeaderNameByChargeTeamId(Mockito.anyLong())).willReturn(chargeTeam);
        given(taskManagerRepository.findOfficerNameByChargeTeamId(Mockito.anyLong())).willReturn(chargeOfficer);

        // when & then
        assertThatThrownBy(() -> taskManagerService.postMapping(mappingDtoList, user, userIp))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.INVALID_MAPPING.getMessage());
    }

    @Test
    @DisplayName("팀과 업무 매핑 정보를 저장합니다.")
    void postMapping3() {
        // given
        List<MappingDto> mappingDtoList = createDummyMappingDtoList();
        List<Mapping> mappingList = createDummyMappingList();
        Users user = createDummyCeo();
        String userIp = "userIp";
        String chargeTeam = "팀장";
        String chargeOfficer = "임원";
        given(taskInfoMapper.mappingDtoListToMappingList(Mockito.anyList(), Mockito.any(),
            Mockito.anyString())).willReturn(mappingList);
        given(taskManagerRepository.findMappingInfo(Mockito.any(Mapping.class))).willReturn(mappingList);
        given(totalService.existsTotal(Mockito.any(Long.class))).willReturn(false);
        doNothing().when(taskManagerRepository).resetMappingInfo(Mockito.any(Mapping.class));
        given(taskManagerRepository.findTeamLeaderNameByChargeTeamId(Mockito.anyLong())).willReturn(chargeTeam);
        given(taskManagerRepository.findOfficerNameByChargeTeamId(Mockito.anyLong())).willReturn(chargeOfficer);
        given(taskManagerRepository.postMappingInfo(Mockito.any(Mapping.class))).willReturn(1);

        // when
        taskManagerService.postMapping(mappingDtoList, user, userIp);

        // then
        Mockito.verify(taskManagerRepository, Mockito.times(mappingList.size())).postMappingInfo(Mockito.any(Mapping.class));
        assertEquals(chargeTeam, mappingList.get(0).getChargeTeam());
        assertEquals(chargeOfficer, mappingList.get(0).getChargeOfficer());
    }

    @Test
    @DisplayName("최종 평가 완료된 팀의 업무 매핑 정보를 삭제할 수 없습니다. (예외 발생)")
    void deleteMappingInfo() {
        // given
        List<MappingDto> mappingDtoList = createDummyMappingDtoList();
        List<Mapping> mappingList = createDummyMappingList();
        given(taskInfoMapper.mappingDtoListToMappingList(Mockito.anyList())).willReturn(mappingList);
        given(taskManagerRepository.findTeamLeaderNameByChargeTeamId(Mockito.anyLong())).willReturn("팀장");
        given(taskManagerRepository.findOfficerNameByChargeTeamId(Mockito.anyLong())).willReturn("임원");
        given(evaluationRepository.findEvaluationState(Mockito.any(TaskEvaluation.class))).willReturn("F");

        // when & then
        assertThatThrownBy(() -> taskManagerService.deleteMappingInfo(mappingDtoList))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.FINAL_SAVE_EVALUATION.getMessage());
    }

    @Test
    @DisplayName("업무 - 팀 매핑 정보를 삭제합니다.")
    void deleteMappingInfo2() {
        // given
        List<MappingDto> mappingDtoList = createDummyMappingDtoList();
        List<Mapping> mappingList = createDummyMappingList();
        given(taskInfoMapper.mappingDtoListToMappingList(Mockito.anyList())).willReturn(mappingList);
        given(taskManagerRepository.findTeamLeaderNameByChargeTeamId(Mockito.anyLong())).willReturn("팀장");
        given(taskManagerRepository.findOfficerNameByChargeTeamId(Mockito.anyLong())).willReturn("임원");
        given(evaluationRepository.findEvaluationState(Mockito.any(TaskEvaluation.class))).willReturn("N");
        doNothing().when(evaluationRepository).deleteTaskEvaluation(Mockito.any(TaskEvaluation.class));
        given(taskManagerRepository.findMappingInfo(Mockito.any(Mapping.class))).willReturn(mappingList);
        given(totalService.existsTotal(Mockito.any(Long.class))).willReturn(false);
        doNothing().when(taskManagerRepository).resetMappingInfo(Mockito.any(Mapping.class));

        // when
        taskManagerService.deleteMappingInfo(mappingDtoList);

        // then
        assertAll(
            () -> Mockito.verify(evaluationRepository, Mockito.times(mappingList.size())).deleteTaskEvaluation(Mockito.any(TaskEvaluation.class)),
            () -> Mockito.verify(taskManagerRepository, Mockito.times(mappingList.size())).resetMappingInfo(Mockito.any(Mapping.class))
        );
    }

    @Test
    @DisplayName("이미 매핑된 업무는 삭제할 수 없습니다.")
    void deleteTasks() {
        // given
        List<TaskRequestDto> taskRequestDtoList = TestUtils.createDummyTaskRequestDtoList();
        given(taskManagerRepository.countMappingInfo(Mockito.anyList())).willReturn(1);

        // when & then
        assertThatThrownBy(() -> taskManagerService.deleteTasks(taskRequestDtoList))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.ALREADY_EXISTS_MAPPING.getMessage());
    }

    @Test
    @DisplayName("업무 정보를 삭제합니다.")
    void deleteTasks2() {
        // given
        List<TaskRequestDto> taskRequestDtoList = TestUtils.createDummyTaskRequestDtoList();
        given(taskManagerRepository.countMappingInfo(Mockito.anyList())).willReturn(0);
        doNothing().when(taskManagerRepository).deleteTasks(Mockito.anyList());

        // when
        taskManagerService.deleteTasks(taskRequestDtoList);

        // then
        Mockito.verify(taskManagerRepository, Mockito.times(1)).deleteTasks(Mockito.anyList());
    }
}
