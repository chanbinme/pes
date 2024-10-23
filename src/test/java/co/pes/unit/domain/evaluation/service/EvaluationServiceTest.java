package co.pes.unit.domain.evaluation.service;

import static co.pes.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import co.pes.domain.admin.service.AdminService;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.evaluation.mapper.EvaluationMapper;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.EvaluationRepository;
import co.pes.domain.evaluation.service.EvaluationService;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.service.TotalService;
import co.pes.utils.TestUtils;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @InjectMocks
    private EvaluationService evaluationService;

    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private EvaluationMapper evaluationMapper;
    @Mock
    private TotalService totalService;
    @Mock
    private AdminService adminService;

    @Test
    @DisplayName("CEO는 조회하려는 조직의 하위 모든 팀 평가 정보를 조회할 수 있습니다. (하위 팀이 있는 경우)")
    void getEvaluationInfo() {
        // given
        String year = "2024";
        Long chargeTeamId = 1L;
        Users user = createDummyCeo();
        List<Long> checkTeamIdList = Arrays.asList(1L, 2L, 3L);
        List<Long> descendantOrgIdList = Arrays.asList(4L, 5L, 6L);
        List<TaskEvaluation> taskEvaluationList = createDummyTaskEvaluationList();

        given(evaluationRepository.countTotal(Mockito.anyString(), Mockito.anyLong())).willReturn(1);
        given(evaluationRepository.getTeamListByUserId(Mockito.anyString())).willReturn(checkTeamIdList);
        given(evaluationRepository.countDescendantOrgByTeamId(Mockito.anyLong())).willReturn(1, 0, 0, 0);
        given(evaluationRepository.getDescendantOrgIdList(Mockito.anyLong())).willReturn(descendantOrgIdList);
        given(evaluationRepository.getTaskEvaluationInfoListByTeamIdList(Mockito.anyString(), Mockito.anyList())).willReturn(taskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(year, chargeTeamId, user);

        // then
        assertAll(
            () -> Mockito.verify(evaluationRepository, Mockito.times(4)).countDescendantOrgByTeamId(Mockito.anyLong()),
            () -> Mockito.verify(evaluationRepository, Mockito.times(1)).getDescendantOrgIdList(Mockito.anyLong()),
            () -> assertEquals(taskEvaluationList.size(), taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertTrue(taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(taskEvaluationList).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(taskEvaluationResponseDto.getTaskEvaluationList())
        );
    }

    @Test
    @DisplayName("CEO는 조회하려는 조직의 하위 모든 팀 평가 정보를 조회할 수 있습니다. (하위 팀이 없는 경우)")
    void getEvaluationInfo2() {
        // given
        String year = "2024";
        Long chargeTeamId = 1L;
        Users user = createDummyCeo();
        List<Long> checkTeamIdList = Arrays.asList(1L, 2L, 3L);
        List<TaskEvaluation> taskEvaluationList = createDummyTaskEvaluationList();
        given(evaluationRepository.countTotal(Mockito.anyString(), Mockito.anyLong())).willReturn(0);
        given(evaluationRepository.getTeamListByUserId(Mockito.anyString())).willReturn(checkTeamIdList);
        given(evaluationRepository.countDescendantOrgByTeamId(Mockito.anyLong())).willReturn(0);
        given(evaluationRepository.getTaskEvaluationInfoList(Mockito.anyString(), Mockito.anyLong())).willReturn(taskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(year, chargeTeamId, user);

        // then
        assertAll(
            () -> Mockito.verify(evaluationRepository, Mockito.times(1)).countDescendantOrgByTeamId(Mockito.anyLong()),
            () -> Mockito.verify(evaluationRepository, Mockito.never()).getDescendantOrgIdList(Mockito.anyLong()),
            () -> assertEquals(taskEvaluationList.size(), taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertFalse(taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(taskEvaluationList.get(0)).usingRecursiveComparison().isEqualTo(taskEvaluationResponseDto.getTaskEvaluationList().get(0))
        );
    }

    @Test
    @DisplayName("Officer는 조회하려는 조직에서 자신이 담당하고 있는 팀의 평가 정보만 조회할 수 있습니다. (하위 팀이 있는 경우)")
    void getEvaluationInfo3() {
        // given
        String year = "2024";
        Long chargeTeamId = 1L;
        Users user = createDummyOfficer();
        List<Long> checkTeamIdList = Arrays.asList(1L, 2L, 3L);
        List<Long> descendantOrgIdList = Arrays.asList(4L, 5L, 6L);
        List<TaskEvaluation> taskEvaluationList = createDummyTaskEvaluationList();
        given(evaluationRepository.countTotal(Mockito.anyString(), Mockito.anyLong())).willReturn(1);
        given(evaluationRepository.getTeamListByUserId(Mockito.anyString())).willReturn(checkTeamIdList);
        given(evaluationRepository.getDescendantOrgIdList(Mockito.anyLong())).willReturn(descendantOrgIdList);
        given(evaluationRepository.countDescendantOrgByTeamId(Mockito.anyLong())).willReturn(1, 0, 0, 0);
        given(evaluationRepository.getTaskEvaluationInfoListByTeamIdList(Mockito.anyString(), Mockito.anyList())).willReturn(taskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(year, chargeTeamId, user);

        // then
        assertAll(
            () -> Mockito.verify(evaluationRepository, Mockito.times(4)).countDescendantOrgByTeamId(Mockito.anyLong()),
            () -> Mockito.verify(evaluationRepository, Mockito.times(1)).getDescendantOrgIdList(Mockito.anyLong()),
            () -> assertEquals(taskEvaluationList.size(), taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertTrue(taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(taskEvaluationList.get(0)).usingRecursiveComparison().isEqualTo(taskEvaluationResponseDto.getTaskEvaluationList().get(0))
        );
    }

    @Test
    @DisplayName("Officer는 조회하려는 조직에서 자신이 담당하고 있는 팀의 평가 정보만 조회할 수 있습니다. (하위 팀이 없는 경우)")
    void getEvaluationInfo4() {
        // given
        String year = "2024";
        Long chargeTeamId = 1L;
        Users user = createDummyOfficer();
        List<Long> checkTeamIdList = Arrays.asList(1L, 2L, 3L);
        List<TaskEvaluation> taskEvaluationList = createDummyTaskEvaluationList();
        given(evaluationRepository.countTotal(Mockito.anyString(), Mockito.anyLong())).willReturn(1);
        given(evaluationRepository.getTeamListByUserId(Mockito.anyString())).willReturn(checkTeamIdList);
        given(evaluationRepository.countDescendantOrgByTeamId(Mockito.anyLong())).willReturn(0);
        given(evaluationRepository.getTaskEvaluationInfoList(Mockito.anyString(), Mockito.anyLong())).willReturn(taskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(year, chargeTeamId, user);

        // then
        assertAll(
            () -> Mockito.verify(evaluationRepository, Mockito.times(1)).countDescendantOrgByTeamId(Mockito.anyLong()),
            () -> Mockito.verify(evaluationRepository, Mockito.never()).getDescendantOrgIdList(Mockito.anyLong()),
            () -> assertEquals(taskEvaluationList.size(), taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertTrue(taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(taskEvaluationList.get(0)).usingRecursiveComparison().isEqualTo(taskEvaluationResponseDto.getTaskEvaluationList().get(0))
        );
    }

    @Test
    @DisplayName("평가 정보를 임시 저장합니다. (이미 저장된 평가 정보가 없는 경우 생성, 있는 경우 수정)")
    void saveTaskEvaluationList() {
        // given
        List<TaskEvaluation> taskEvaluationList = createDummyTaskEvaluationList();
        given(evaluationMapper.dtoListToTaskEvaluationList(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString()))
            .willReturn(taskEvaluationList);
        given(evaluationRepository.countTaskEvaluation(Mockito.any(TaskEvaluation.class))).willReturn(0, 1, 0, 0);  // insert : 3, update : 1
        doNothing().when(evaluationRepository).saveTaskEvaluation(Mockito.any(TaskEvaluation.class));
        doNothing().when(evaluationRepository).updateTaskEvaluation(Mockito.any(TaskEvaluation.class));

        // when
        evaluationService.saveTaskEvaluationList(createDummyTaskEvaluationRequestDtoList(), createDummyOfficer(), "2024");

        // then
        assertAll(
            () -> Mockito.verify(evaluationRepository, Mockito.times(4)).countTaskEvaluation(Mockito.any(TaskEvaluation.class)),
            () -> Mockito.verify(evaluationRepository, Mockito.times(3)).saveTaskEvaluation(Mockito.any(TaskEvaluation.class)),
            () -> Mockito.verify(evaluationRepository, Mockito.times(1)).updateTaskEvaluation(Mockito.any(TaskEvaluation.class)),
            () -> taskEvaluationList.forEach(taskEvaluation -> assertEquals("N", taskEvaluation.getState()))
        );
    }

    @Test
    @DisplayName("최종 평가 목록을 저장하고 총 평가 결과를 업데이트합니다.")
    void finalSaveTaskEvaluationList() {
        // given
        List<TaskEvaluation> taskEvaluationList = createDummyTaskEvaluationList();
        given(evaluationMapper.dtoListToTaskEvaluationList(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString())).willReturn(taskEvaluationList);
        given(evaluationRepository.countTaskEvaluation(Mockito.any(TaskEvaluation.class))).willReturn(0, 1, 1, 0);  // insert : 2, update : 2
        doNothing().when(evaluationRepository).saveTaskEvaluation(Mockito.any(TaskEvaluation.class));
        doNothing().when(evaluationRepository).updateTaskEvaluation(Mockito.any(TaskEvaluation.class));
        doNothing().when(totalService).saveTotal(Mockito.any(TotalRequestDto.class), Mockito.any(Users.class), Mockito.anyString());

        // when
        evaluationService.finalSaveTaskEvaluationList(createDummyFinalEvaluationRequestDto(), createDummyOfficer(), "2024");

        // then
        assertAll(
            () -> Mockito.verify(evaluationRepository, Mockito.times(4)).countTaskEvaluation(Mockito.any(TaskEvaluation.class)),
            () -> Mockito.verify(evaluationRepository, Mockito.times(2)).saveTaskEvaluation(Mockito.any(TaskEvaluation.class)),
            () -> Mockito.verify(evaluationRepository, Mockito.times(2)).updateTaskEvaluation(Mockito.any(TaskEvaluation.class)),
            () -> Mockito.verify(totalService, Mockito.times(1)).saveTotal(Mockito.any(TotalRequestDto.class), Mockito.any(Users.class), Mockito.anyString()),
            () -> taskEvaluationList.forEach(taskEvaluation -> assertEquals("F", taskEvaluation.getState()))
        );
    }

    @Test
    @DisplayName("오늘이 임원 평가 기간에 속하는지 확인합니다. (임원 평가 기간인 경우)")
    void checkOfficerEvaluationPeriod() {
        // given
        given(adminService.getOfficerEvaluationPeriod()).willReturn(createSuccessDummyEvaluationPeriod());

        // when
        String result = evaluationService.checkOfficerEvaluationPeriod();

        // then
        assertEquals("임원 평가 기간입니다.", result);
    }

    @Test
    @DisplayName("오늘이 임원 평가 기간에 속하는지 확인합니다. (임원 평가 기간이 아닌 경우)")
    void checkOfficerEvaluationPeriod2() {
        // given
        OfficerEvaluationPeriod evaluationPeriod = createFailDummyEvaluationPeriod();

        given(adminService.getOfficerEvaluationPeriod()).willReturn(evaluationPeriod);

        // when
        String result = evaluationService.checkOfficerEvaluationPeriod();

        // then
        String expectedResult =
            "임원 평가 기간이 아닙니다.\\n평가 기간: "
                + evaluationPeriod.getStartDate().toLocalDate() + " ~ "
                + evaluationPeriod.getEndDate().toLocalDate();
        assertEquals(expectedResult, result);
    }
}