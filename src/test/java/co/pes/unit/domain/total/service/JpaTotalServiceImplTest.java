package co.pes.unit.domain.total.service;

import static co.pes.utils.TestUtils.createDummyCeo;
import static co.pes.utils.TestUtils.createDummyEvaluationTotalEntity;
import static co.pes.utils.TestUtils.createDummyOrganizationEntity;
import static co.pes.utils.TestUtils.createDummyPostTotalRankingRequestDtoList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        // when

        // then
    }
}