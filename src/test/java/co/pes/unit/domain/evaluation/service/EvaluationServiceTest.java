package co.pes.unit.domain.evaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import co.pes.domain.admin.service.AdminService;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
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
        Users user = TestUtils.createDummyCeo();
        List<Long> checkTeamIdList = Arrays.asList(1L, 2L, 3L);
        List<Long> descendantOrgIdList = Arrays.asList(4L, 5L, 6L);
        List<TaskEvaluation> taskEvaluationList = TestUtils.createDummyTaskEvaluationList();

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
        Users user = TestUtils.createDummyCeo();
        List<Long> checkTeamIdList = Arrays.asList(1L, 2L, 3L);
        List<TaskEvaluation> taskEvaluationList = TestUtils.createDummyTaskEvaluationList();
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
        Users user = TestUtils.createDummyOfficer();
        List<Long> checkTeamIdList = Arrays.asList(1L, 2L, 3L);
        List<Long> descendantOrgIdList = Arrays.asList(4L, 5L, 6L);
        List<TaskEvaluation> taskEvaluationList = TestUtils.createDummyTaskEvaluationList();
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
    void saveTaskEvaluationList() {
    }

    @Test
    void finalSaveTaskEvaluationList() {
    }

    @Test
    void checkOfficerEvaluationPeriod() {
    }
}