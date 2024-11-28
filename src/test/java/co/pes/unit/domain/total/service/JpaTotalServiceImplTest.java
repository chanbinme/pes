package co.pes.unit.domain.total.service;

import static co.pes.utils.TestUtils.createDummyCeo;
import static co.pes.utils.TestUtils.createDummyEvaluationTotalEntity;
import static co.pes.utils.TestUtils.createDummyOrganizationEntity;
import static co.pes.utils.TestUtils.createDummyPostTotalRankingRequestDtoList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.entity.EvaluationTotalEntity;
import co.pes.domain.total.mapper.TotalMapper;
import co.pes.domain.total.repository.JpaEndYearRepository;
import co.pes.domain.total.repository.JpaTotalRepository;
import co.pes.domain.total.service.JpaTotalServiceImpl;
import java.util.Arrays;
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
class JpaTotalServiceImplTest {

    @InjectMocks
    private JpaTotalServiceImpl totalService;

    @Mock
    private JpaTotalRepository totalRepository;
    @Mock
    private JpaEndYearRepository endYearRepository;
    @Mock
    private JpaOrganizationRepository organizationRepository;
    @Mock
    private TotalMapper totalMapper;

    @Test
    @DisplayName("이미 마감된 연도인 경우총 평가 결과를 저장할 수 없습니다.")
    void saveTotalRankingList1() {
        // given
        Users user = createDummyCeo();
        String userIp = "userIp";
        List<PostTotalRankingRequestDto> dummyPostTotalRankingRequestDtoList = createDummyPostTotalRankingRequestDtoList();
        given(endYearRepository.existsByYear(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> totalService.saveTotalRankingList(dummyPostTotalRankingRequestDtoList, user, userIp))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.FINISHED_EVALUATION.getMessage());
    }

    @Test
    @DisplayName("선택된 팀들의 총 평가 결과를 저장합니다.")
    void saveTotalRankingList2() {
        // given
        Users user = createDummyCeo();
        String userIp = "userIp";
        List<PostTotalRankingRequestDto> dummyPostTotalRankingRequestDtoList = createDummyPostTotalRankingRequestDtoList();
        given(endYearRepository.existsByYear(anyString())).willReturn(false);
        given(organizationRepository.getReferenceById(anyLong())).willReturn(createDummyOrganizationEntity());
        given(totalMapper.postDtoToEvaluationTotalEntity(
            any(PostTotalRankingRequestDto.class), any(OrganizationEntity.class), anyString(), anyString())).willReturn(createDummyEvaluationTotalEntity());

        // when
        totalService.saveTotalRankingList(dummyPostTotalRankingRequestDtoList, user, userIp);

        // then
        ArgumentCaptor<List<EvaluationTotalEntity>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(totalRepository).saveAll(captor.capture());
        int actualSize = captor.getValue().size();
        int expectedSize = dummyPostTotalRankingRequestDtoList.size();
        assertAll(
            () -> verify(totalRepository, Mockito.times(1)).saveAll(Mockito.anyList()),
            () -> assertEquals(expectedSize, actualSize)
        );
    }

    @Test
    @DisplayName("주어진 연도의 평가 종료 처리 (연도 마감)")
    void endYear1() {
        // given
        given(endYearRepository.existsByYear(anyString())).willReturn(false);

        // when
        totalService.endYear("2024", createDummyCeo(), "userIp");

        // then
        verify(endYearRepository, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("이미 마감된 연도인 경우 평가 종료 처리를 할 수 없습니다.")
    void endYear2() {
        // given
        given(endYearRepository.existsByYear(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> totalService.endYear("2024", createDummyCeo(), "userIp"))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.FINISHED_EVALUATION.getMessage());
    }

    @Test
    @DisplayName("주어진 연도의 평가 종료 취소 처리 (연도 마감 취소)")
    void cancelEndYear1() {
        // given
        String year = "2024";
        given(endYearRepository.existsByYear(anyString())).willReturn(true);

        // when
        totalService.cancelEndYear(year);

        // then
        Mockito.verify(endYearRepository, Mockito.times(1)).deleteByYear(Mockito.anyString());
    }

    @Test
    @DisplayName("마감된 연도가 아니면 연도 마감 취소를 요청할 수 없습니다.")
    void cancelEndYear2() {
        // given
        String year = "2024";
        given(endYearRepository.existsByYear(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> totalService.cancelEndYear(year))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.NOT_FINISHED_EVALUATION.getMessage());
    }

    @Test
    @DisplayName("마감된 연도인지 체크합니다. 마감된 연도이면 true를 반환합니다.")
    void checkEndedYear() {
        // given
        String year = "2024";
        given(endYearRepository.existsByYear(Mockito.anyString())).willReturn(true);

        // when
        boolean actual = totalService.checkEndedYear(year);

        // then
        assertTrue(actual);
    }

    @Test
    @DisplayName("총 평가 결과가 존재하는지 확인합니다.")
    void existsTotal() {
        // given
        Long chargeTeamId = 1L;
        given(totalRepository.existsByOrganizationId(Mockito.anyLong())).willReturn(true);

        // when
        boolean actual = totalService.existsTotal(chargeTeamId);

        // then
        assertTrue(actual);
    }

    @Test
    @DisplayName("주어진 연도의 모든 평가가 완료되었는지 확인합니다.")
    void checkAllEvaluationsComplete() throws Exception {
        // given
        String year = "2024";
        given(totalRepository.checkAllEvaluationsComplete(Mockito.anyString())).willReturn(true);

        // when
        boolean actual = totalService.checkAllEvaluationsComplete(year);

        // then
        assertTrue(actual);
    }

    @Test
    @DisplayName("평가 연도 목록을 조회합니다.")
    void getEvaluationYearList() throws Exception {
        // given
        List<String> expectedYearList = Arrays.asList("2021", "2022", "2023", "2024");
        given(totalRepository.getEvaluationYearList()).willReturn(expectedYearList);

        // when
        List<String> actualYearList = totalService.getEvaluationYearList();

        // then
        assertEquals(expectedYearList, actualYearList);
    }

    @Test
    @DisplayName("주어진 연도와 팀 ID로 총 평가 결과가 존재하는지 확인합니다.")
    void existsByYearAndOrganizationId() throws Exception {
        // given
        String year = "2024";
        Long chargeTeamId = 1L;
        given(totalRepository.existsByYearAndOrganizationId(Mockito.anyString(), Mockito.anyLong())).willReturn(true);

        // when
        boolean actual = totalService.existsByYearAndOrganizationId(year, chargeTeamId);

        // then
        assertTrue(actual);
    }
}