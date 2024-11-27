package co.pes.unit.domain.total.service;

import static co.pes.utils.TestUtils.createDummyCeo;
import static co.pes.utils.TestUtils.createDummyPostTotalRankingRequestDtoList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyString;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.mapper.TotalMapper;
import co.pes.domain.total.repository.JpaEndYearRepository;
import co.pes.domain.total.repository.TotalRepository;
import co.pes.domain.total.service.JpaTotalServiceImpl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaTotalServiceImplTest {

    @InjectMocks
    private JpaTotalServiceImpl totalService;

    @Mock
    private TotalRepository totalRepository;
    @Mock
    private JpaEndYearRepository endYearRepository;
    @Mock
    private JpaOrganizationRepository organizationRepository;
    @Mock
    private TotalMapper totalMapper;

    @Test
    @DisplayName("이미 마감된 연도인 경우총 평가 결과를 저장할 수 없습니다.")
    void saveTotalRankingList() {
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
}