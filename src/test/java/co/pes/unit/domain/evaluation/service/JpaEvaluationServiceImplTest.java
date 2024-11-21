package co.pes.unit.domain.evaluation.service;

import static co.pes.utils.TestUtils.createDummyCeo;
import static co.pes.utils.TestUtils.createDummyTaskEvaluationList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.mapper.EvaluationMapper;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.JpaEvaluationRepository;
import co.pes.domain.evaluation.service.JpaEvaluationServiceImpl;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.task.repository.JpaTaskManagerRepository;
import co.pes.domain.total.service.TotalService;
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
class JpaEvaluationServiceImplTest {

    @InjectMocks
    private JpaEvaluationServiceImpl evaluationService;

    @Mock
    private JpaEvaluationRepository evaluationRepository;
    @Mock
    private EvaluationMapper evaluationMapper;
    @Mock
    private JpaTaskManagerRepository taskManagerRepository;
    @Mock
    private JpaOrganizationRepository organaizationRepository;
    @Mock
    private TotalService totalService;

    @Test
    @DisplayName("CEO는 조회하려는 조직의 하위 모든 팀 평가 정보를 조회할 수 있습니다. (하위 팀이 있는 경우)")
    void getEvaluationInfo() {
        // given
        String year = "2024";
        Long chargeTeamId = 1L;
        Users user = createDummyCeo();
        List<Long> checkTeamIdList = Arrays.asList(1L, 2L, 3L);
        List<Long> descendantOrgIdList = Arrays.asList(4L, 5L, 6L);
        List<TaskEvaluation> expectedTaskEvaluationList = createDummyTaskEvaluationList();
        boolean expectedExistsTotal = true;

        given(totalService.existsByYearAndOrganizationId(Mockito.anyString(),
            Mockito.anyLong())).willReturn(expectedExistsTotal);
        given(organaizationRepository.getIdListByUserId(Mockito.anyString())).willReturn(
            checkTeamIdList);
        given(organaizationRepository.getIdListByAncestorOrgId(Mockito.anyLong(),
            Mockito.isNull())).willReturn(descendantOrgIdList);
        given(organaizationRepository.existsDescendantOrgByAncestorOrgId(
            Mockito.anyLong())).willReturn(true);
        given(evaluationRepository.searchTaskEvaluationInfoListByTeamIdList(Mockito.anyString(),
            Mockito.anyList())).willReturn(expectedTaskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(
            year, chargeTeamId, user);

        // then
        assertAll(
            () -> Mockito.verify(organaizationRepository,
                    Mockito.times(descendantOrgIdList.size() + 1))
                .existsDescendantOrgByAncestorOrgId(Mockito.anyLong()),
            () -> Mockito.verify(organaizationRepository,
                    Mockito.times(descendantOrgIdList.size() + 1))
                .getIdListByAncestorOrgId(Mockito.anyLong(), Mockito.isNull()),
            () -> assertEquals(expectedTaskEvaluationList.size(),
                taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertTrue(taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(
                expectedTaskEvaluationList).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(taskEvaluationResponseDto.getTaskEvaluationList())
        );
    }

    @Test
    @DisplayName("CEO는 조회하려는 조직의 하위 모든 팀 평가 정보를 조회할 수 있습니다. (하위 팀이 없는 경우)")
    void getEvaluationInfo2() {

    }

    @Test
    @DisplayName("Officer는 조회하려는 조직에서 자신이 담당하고 있는 팀의 평가 정보만 조회할 수 있습니다. (하위 팀이 있는 경우)")
    void getEvaluationInfo3() {
    }

    @Test
    @DisplayName("Officer는 조회하려는 조직에서 자신이 담당하고 있는 팀의 평가 정보만 조회할 수 있습니다. (하위 팀이 없는 경우)")
    void getEvaluationInfo4() {
    }

    @Test
    @DisplayName("평가 정보를 임시 저장합니다. (이미 저장된 평가 정보가 없는 경우 생성, 있는 경우 수정)")
    void saveTaskEvaluationList() {
    }

    @Test
    @DisplayName("최종 평가 목록을 저장하고 총 평가 결과를 업데이트합니다.")
    void finalSaveTaskEvaluationList() {
    }

    @Test
    @DisplayName("오늘이 임원 평가 기간에 속하는지 확인합니다. (임원 평가 기간인 경우)")
    void checkOfficerEvaluationPeriod() {
    }

    @Test
    @DisplayName("오늘이 임원 평가 기간에 속하는지 확인합니다. (임원 평가 기간이 아닌 경우)")
    void checkOfficerEvaluationPeriod2() {
    }
}