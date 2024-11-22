package co.pes.unit.domain.evaluation.service;

import static co.pes.utils.TestUtils.createDummyCeo;
import static co.pes.utils.TestUtils.createDummyFinalEvaluationRequestDto;
import static co.pes.utils.TestUtils.createDummyOfficer;
import static co.pes.utils.TestUtils.createDummyTaskEntity;
import static co.pes.utils.TestUtils.createDummyTaskEvaluationList;
import static co.pes.utils.TestUtils.createDummyTaskEvaluationRequestDtoList;
import static co.pes.utils.TestUtils.createFailDummyEvaluationPeriod;
import static co.pes.utils.TestUtils.createSuccessDummyEvaluationPeriod;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import co.pes.domain.admin.service.AdminService;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.entity.TaskEvaluationEntity;
import co.pes.domain.evaluation.mapper.EvaluationMapper;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.JpaEvaluationRepository;
import co.pes.domain.evaluation.service.JpaEvaluationServiceImpl;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.task.entity.TaskEntity;
import co.pes.domain.task.repository.JpaTaskManagerRepository;
import co.pes.domain.total.service.TotalService;
import co.pes.utils.TestUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        List<TaskEvaluation> expectedTaskEvaluationList = createDummyTaskEvaluationList();
        boolean expectedExistsTotal = true;

        given(totalService.existsByYearAndOrganizationId(Mockito.anyString(), Mockito.anyLong())).willReturn(expectedExistsTotal);
        given(organaizationRepository.getIdListByUserId(Mockito.anyString())).willReturn(checkTeamIdList);
        given(organaizationRepository.getIdListByAncestorOrgId(Mockito.anyLong(), Mockito.isNull())).willReturn(descendantOrgIdList);
        given(organaizationRepository.existsDescendantOrgByAncestorOrgId(Mockito.anyLong())).willReturn(true);
        given(evaluationRepository.searchTaskEvaluationInfoListByTeamIdList(Mockito.anyString(), Mockito.anyList())).willReturn(expectedTaskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(
            year, chargeTeamId, user);

        // then
        assertAll(
            () -> verify(organaizationRepository,
                    Mockito.times(descendantOrgIdList.size() + 1))
                .existsDescendantOrgByAncestorOrgId(Mockito.anyLong()),
            () -> verify(organaizationRepository,
                    Mockito.times(descendantOrgIdList.size() + 1))
                .getIdListByAncestorOrgId(Mockito.anyLong(), Mockito.isNull()),
            () -> assertEquals(expectedTaskEvaluationList.size(),
                taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertEquals(expectedExistsTotal, taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(expectedTaskEvaluationList).usingRecursiveFieldByFieldElementComparator()
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
        List<TaskEvaluation> expectedTaskEvaluationList = createDummyTaskEvaluationList();
        boolean expectedExistsTotal = true;

        given(totalService.existsByYearAndOrganizationId(Mockito.anyString(), Mockito.anyLong())).willReturn(expectedExistsTotal);
        given(organaizationRepository.getIdListByUserId(Mockito.anyString())).willReturn(checkTeamIdList);
        given(organaizationRepository.existsDescendantOrgByAncestorOrgId(Mockito.anyLong())).willReturn(false);
        given(evaluationRepository.searchTaskEvaluationInfoListByTeamId(Mockito.anyString(), Mockito.anyLong())).willReturn(expectedTaskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(year, chargeTeamId, user);

        // then
        assertAll(
            () -> verify(organaizationRepository, Mockito.times(1)).existsDescendantOrgByAncestorOrgId(Mockito.anyLong()),
            () -> verify(organaizationRepository, Mockito.never()).getIdListByAncestorOrgId(Mockito.anyLong(), Mockito.isNull()),
            () -> assertEquals(expectedTaskEvaluationList.size(), taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertEquals(expectedExistsTotal, taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(expectedTaskEvaluationList).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(taskEvaluationResponseDto.getTaskEvaluationList())
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
        List<TaskEvaluation> expectedTaskEvaluationList = createDummyTaskEvaluationList();
        boolean expectedExistsTotal = false;

        given(totalService.existsByYearAndOrganizationId(Mockito.anyString(), Mockito.anyLong())).willReturn(expectedExistsTotal);
        given(organaizationRepository.getIdListByUserId(Mockito.anyString())).willReturn(checkTeamIdList);
        given(organaizationRepository.getIdListByAncestorOrgId(Mockito.anyLong(), Mockito.isNull())).willReturn(descendantOrgIdList);
        given(organaizationRepository.getIdListByAncestorOrgId(Mockito.anyLong(), Mockito.anyList())).willReturn(descendantOrgIdList);
        given(organaizationRepository.existsDescendantOrgByAncestorOrgId(Mockito.anyLong())).willReturn(true);
        given(evaluationRepository.searchTaskEvaluationInfoListByTeamIdList(Mockito.anyString(), Mockito.anyList())).willReturn(expectedTaskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(year, chargeTeamId, user);

        // then
        assertAll(
            () -> verify(organaizationRepository, Mockito.times(descendantOrgIdList.size() + 1)).existsDescendantOrgByAncestorOrgId(Mockito.anyLong()),
            () -> verify(organaizationRepository, Mockito.times(1)).getIdListByAncestorOrgId(Mockito.anyLong(), Mockito.isNull()),
            () -> verify(organaizationRepository, Mockito.times(descendantOrgIdList.size())).getIdListByAncestorOrgId(Mockito.anyLong(), Mockito.anyList()),
            () -> assertEquals(expectedTaskEvaluationList.size(), taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertEquals(expectedExistsTotal, taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(expectedTaskEvaluationList).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(taskEvaluationResponseDto.getTaskEvaluationList())
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
        List<TaskEvaluation> expectedTaskEvaluationList = createDummyTaskEvaluationList();
        boolean expectedExistsTotal = false;

        given(totalService.existsByYearAndOrganizationId(Mockito.anyString(), Mockito.anyLong())).willReturn(expectedExistsTotal);
        given(organaizationRepository.getIdListByUserId(Mockito.anyString())).willReturn(checkTeamIdList);
        given(organaizationRepository.existsDescendantOrgByAncestorOrgId(Mockito.anyLong())).willReturn(false);
        given(evaluationRepository.searchTaskEvaluationInfoListByTeamId(Mockito.anyString(), Mockito.anyLong())).willReturn(expectedTaskEvaluationList);

        // when
        TaskEvaluationResponseDto taskEvaluationResponseDto = evaluationService.getEvaluationInfo(year, chargeTeamId, user);

        // then
        assertAll(
            () -> verify(organaizationRepository, Mockito.times(1)).existsDescendantOrgByAncestorOrgId(Mockito.anyLong()),
            () -> assertEquals(expectedTaskEvaluationList.size(), taskEvaluationResponseDto.getTaskEvaluationList().size()),
            () -> assertEquals(expectedExistsTotal, taskEvaluationResponseDto.isExistsTotal()),
            () -> assertThat(expectedTaskEvaluationList).usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(taskEvaluationResponseDto.getTaskEvaluationList())
        );
    }

    @Test
    @DisplayName("평가 정보를 임시 저장합니다. (이미 저장된 평가 정보가 없는 경우 생성, 있는 경우 수정)")
    void saveTaskEvaluationList() {
        // given
        TaskEntity dummyTaskEntity = createDummyTaskEntity();
        OrganizationEntity dummyOrganizationEntity = TestUtils.createDummyOrganizationEntity();
        List<TaskEvaluationRequestDto> dummyTaskEvaluationRequestDtoList = createDummyTaskEvaluationRequestDtoList();
        TaskEvaluationEntity dummyTaskEvaluationEntity = TestUtils.createDummyTaskEvaluationEntity();

        given(taskManagerRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(dummyTaskEntity));
        given(organaizationRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(dummyOrganizationEntity));
        given(evaluationMapper.dtoToTaskEvaluationEntity(Mockito.any(TaskEvaluationRequestDto.class),
                Mockito.any(TaskEntity.class), Mockito.any(OrganizationEntity.class),
                Mockito.anyString(), Mockito.anyString())).willReturn(dummyTaskEvaluationEntity);
        given(evaluationRepository.saveAll(Mockito.anyList())).willReturn(null);

        // when
        evaluationService.saveTaskEvaluationList(dummyTaskEvaluationRequestDtoList, createDummyOfficer(), "2024");

        // then
        ArgumentCaptor<List<TaskEvaluationEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(evaluationRepository).saveAll(captor.capture());
        List<TaskEvaluationEntity> captoredEntities = captor.getValue();
        int expectedCallCount = dummyTaskEvaluationRequestDtoList.size();
        assertAll(
            () -> verify(taskManagerRepository, Mockito.times(expectedCallCount)).findById(Mockito.anyLong()),
            () -> verify(organaizationRepository, Mockito.times(expectedCallCount)).findById(Mockito.anyLong()),
            () -> verify(evaluationRepository, Mockito.times(1)).saveAll(Mockito.anyList()),
            () -> captoredEntities.forEach(entity -> assertEquals("N", entity.getState()))
        );
    }

    @Test
    @DisplayName("최종 평가 목록을 저장하고 총 평가 결과를 업데이트합니다.")
    void finalSaveTaskEvaluationList() {
        // given
        TaskEntity dummyTaskEntity = createDummyTaskEntity();
        OrganizationEntity dummyOrganizationEntity = TestUtils.createDummyOrganizationEntity();
        List<TaskEvaluationRequestDto> dummyTaskEvaluationRequestDtoList = createDummyTaskEvaluationRequestDtoList();
        TaskEvaluationEntity dummyTaskEvaluationEntity = TestUtils.createDummyTaskEvaluationEntity();

        given(taskManagerRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(dummyTaskEntity));
        given(organaizationRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(dummyOrganizationEntity));
        given(evaluationMapper.dtoToTaskEvaluationEntity(Mockito.any(TaskEvaluationRequestDto.class),
            Mockito.any(TaskEntity.class), Mockito.any(OrganizationEntity.class),
            Mockito.anyString(), Mockito.anyString())).willReturn(dummyTaskEvaluationEntity);
        given(evaluationRepository.saveAll(Mockito.anyList())).willReturn(null);

        // when
        evaluationService.finalSaveTaskEvaluationList(createDummyFinalEvaluationRequestDto(), createDummyOfficer(), "2024");

        // then
        ArgumentCaptor<List<TaskEvaluationEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(evaluationRepository).saveAll(captor.capture());
        List<TaskEvaluationEntity> captoredEntities = captor.getValue();
        int expectedCallCount = dummyTaskEvaluationRequestDtoList.size();
        assertAll(
            () -> verify(taskManagerRepository, Mockito.times(expectedCallCount)).findById(Mockito.anyLong()),
            () -> verify(organaizationRepository, Mockito.times(expectedCallCount)).findById(Mockito.anyLong()),
            () -> verify(evaluationRepository, Mockito.times(1)).saveAll(Mockito.anyList()),
            () -> captoredEntities.forEach(entity -> assertEquals("F", entity.getState()))
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