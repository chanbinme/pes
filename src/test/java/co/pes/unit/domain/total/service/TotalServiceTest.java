package co.pes.unit.domain.total.service;

import static co.pes.utils.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.MemberInfoRepository;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.mapper.TotalMapper;
import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.Total;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.repository.TotalRepository;
import co.pes.domain.total.service.TotalService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TotalServiceTest {

    @InjectMocks
    private TotalService totalService;

    @Mock
    private TotalRepository totalRepository;
    @Mock
    private MemberInfoRepository memberInfoRepository;
    @Mock
    private TotalMapper totalMapper;

    @Test
    @DisplayName("팀장 및 부문장의 총 평가 결과 저장")
    void saveTotal() {
        // given
        TotalRequestDto totalRequestDto = createDummyTotalRequestDto();
        Users user = createDummyCeo();
        String userIp = "userIp";
        Total teamTotal = createDummyTeamTotal();
        Total officerTotal = createDummyOfficerTotal();
        OfficerTeamInfo officerTeamInfo = createDummyOfficerTeamInfo();
        String officerId = "officerId";
        int teamCount = 10;
        double sumTeamTotalPoint = 100.0;

        given(totalMapper.dtoToTeamTotal(Mockito.any(TotalRequestDto.class), Mockito.any(Users.class), Mockito.anyString())).willReturn(teamTotal);
        given(totalRepository.countTotal(Mockito.any(Total.class))).willReturn(0, 0);
        doNothing().when(totalRepository).saveTotal(Mockito.any(Total.class));
        given(totalRepository.findOfficerTeamInfoByTeamId(Mockito.anyLong())).willReturn(officerTeamInfo);
        given(totalMapper.dtoToOfficerTotal(Mockito.any(TotalRequestDto.class), Mockito.any(Users.class)
            , Mockito.anyString(), Mockito.anyLong(), Mockito.anyString())).willReturn(officerTotal);
        given(memberInfoRepository.findIdByNameAndPositionGb(Mockito.anyString(), Mockito.anyString())).willReturn(officerId);
        given(totalRepository.countMappingTeamByTeamId(Mockito.anyLong())).willReturn(teamCount);
        given(totalRepository.sumTeamTotalPoint(Mockito.any(Total.class))).willReturn(sumTeamTotalPoint);

        // when
        totalService.saveTotal(totalRequestDto, user, userIp);

        // then
        assertAll(
            () -> Mockito.verify(totalRepository, Mockito.times(2)).saveTotal(Mockito.any(Total.class)),
            () -> Mockito.verify(totalRepository, Mockito.never()).updateTotal(Mockito.any(Total.class)),
            () -> assertEquals(Math.round((sumTeamTotalPoint / teamCount) * 10) / 10.0, officerTotal.getTotalPoint())
        );
    }

    @Test
    @DisplayName("이미 저장된 평가 결과가 있으면 기존 팀장 및 부문장의 총 평가 결과 업데이트")
    void saveTotal2() {
        // given
        TotalRequestDto totalRequestDto = createDummyTotalRequestDto();
        Users user = createDummyCeo();
        String userIp = "userIp";
        Total teamTotal = createDummyTeamTotal();
        Total officerTotal = createDummyOfficerTotal();
        OfficerTeamInfo officerTeamInfo = createDummyOfficerTeamInfo();
        String officerId = "officerId";
        int teamCount = 10;
        double sumTeamTotalPoint = 100.0;

        given(totalMapper.dtoToTeamTotal(Mockito.any(TotalRequestDto.class), Mockito.any(Users.class), Mockito.anyString())).willReturn(teamTotal);
        given(totalRepository.countTotal(Mockito.any(Total.class))).willReturn(1, 1);
        doNothing().when(totalRepository).updateTotal(Mockito.any(Total.class));
        given(totalRepository.findOfficerTeamInfoByTeamId(Mockito.anyLong())).willReturn(officerTeamInfo);
        given(totalMapper.dtoToOfficerTotal(Mockito.any(TotalRequestDto.class), Mockito.any(Users.class)
            , Mockito.anyString(), Mockito.anyLong(), Mockito.anyString())).willReturn(officerTotal);
        given(memberInfoRepository.findIdByNameAndPositionGb(Mockito.anyString(), Mockito.anyString())).willReturn(officerId);
        given(totalRepository.countMappingTeamByTeamId(Mockito.anyLong())).willReturn(teamCount);
        given(totalRepository.sumTeamTotalPoint(Mockito.any(Total.class))).willReturn(sumTeamTotalPoint);

        // when
        totalService.saveTotal(totalRequestDto, user, userIp);

        // then
        assertAll(
            () -> Mockito.verify(totalRepository, Mockito.never()).saveTotal(Mockito.any(Total.class)),
            () -> Mockito.verify(totalRepository, Mockito.times(2)).updateTotal(Mockito.any(Total.class))
        );
    }

    @Test
    @DisplayName("회사인 경우 평가 결과 저장하지 않음")
    void saveTotal3() {
        // given
        TotalRequestDto totalRequestDto = createDummyTotalRequestDto();
        Users user = createDummyCeo();
        String userIp = "userIp";
        Total teamTotal = createDummyTeamTotal();
        Total ceoTotal = createDummyCeoTotal();
        OfficerTeamInfo officerTeamInfo = createDummyOfficerTeamInfo();

        given(totalMapper.dtoToTeamTotal(Mockito.any(TotalRequestDto.class), Mockito.any(Users.class), Mockito.anyString())).willReturn(teamTotal);
        given(totalRepository.countTotal(Mockito.any(Total.class))).willReturn(0);
        doNothing().when(totalRepository).saveTotal(Mockito.any(Total.class));
        given(totalRepository.findOfficerTeamInfoByTeamId(Mockito.anyLong())).willReturn(officerTeamInfo);
        given(totalMapper.dtoToOfficerTotal(Mockito.any(TotalRequestDto.class), Mockito.any(Users.class)
            , Mockito.anyString(), Mockito.anyLong(), Mockito.anyString())).willReturn(ceoTotal);

        // when
        totalService.saveTotal(totalRequestDto, user, userIp);

        // then
        assertAll(
            () -> Mockito.verify(totalRepository, Mockito.times(1)).saveTotal(Mockito.any(Total.class)),
            () -> Mockito.verify(memberInfoRepository, Mockito.never()).findIdByNameAndPositionGb(Mockito.anyString(), Mockito.anyString()),
            () -> Mockito.verify(totalRepository, Mockito.never()).countMappingTeamByTeamId(Mockito.anyLong()),
            () -> Mockito.verify(totalRepository, Mockito.never()).sumTeamTotalPoint(Mockito.any(Total.class))
        );
    }

    @Test
    @DisplayName("총 평가 결과를 기반으로 등급을 계산 후 등급 내림차순으로 정렬.")
    void findTotalListAndCalculateRanking() {
        // given
        List<TotalRanking> teamTotalRankingList = createDummyTeamTotalRankingList();
        List<TotalRanking> officerTotalRankingList = createDummyOfficerTotalRankingList();
        given(totalRepository.getTotalByTeamIdList(Mockito.anyString(), Mockito.anyList())).willReturn(teamTotalRankingList);
        given(totalRepository.getOfficerTotalByTeamIdList(Mockito.anyString(), Mockito.anyList())).willReturn(officerTotalRankingList);

        // when
        List<TotalRanking> actual = totalService.findTotalListAndCalculateRanking("2024", createTotalRankingRequestDtoList());

        // then
        assertAll(
                () -> assertEquals("S", actual.get(0).getNewRanking()),
                () -> assertEquals("A", actual.get(1).getNewRanking()),
                () -> assertEquals("B", actual.get(2).getNewRanking()),
                () -> assertEquals("C", actual.get(3).getNewRanking()),
                () -> assertEquals("D", actual.get(4).getNewRanking())
        );
    }

    @Test
    @DisplayName("미리보기 화면에 노출시킬 총 평가 결과를 등급순으로 정렬 조회")
    void findTotalListForPreview() {
        // given
        List<TotalRanking> teamTotalRankingList = createDummyTeamTotalRankingListForPreview();
        List<TotalRanking> officerTotalRankingList = createDummyOfficerTotalRankingListForPreview();
        given(totalRepository.getTotalByTeamIdList(Mockito.anyString(), Mockito.anyList())).willReturn(teamTotalRankingList);
        given(totalRepository.getOfficerTotalByTeamIdList(Mockito.anyString(), Mockito.anyList())).willReturn(officerTotalRankingList);

        // when
        List<TotalRanking> actual = totalService.findTotalListForPreview("2024", createTotalRankingRequestDtoList());

        // then
        assertAll(
                () -> assertEquals("S", actual.get(0).getRanking()),
                () -> assertEquals("A", actual.get(1).getRanking()),
                () -> assertEquals("B", actual.get(2).getRanking()),
                () -> assertEquals("C", actual.get(3).getRanking()),
                () -> assertEquals("D", actual.get(4).getRanking())
        );
    }

    @Test
    @DisplayName("이미 마감된 연도의 평가는 저장할 수 없습니다.")
    void saveTotalRankingList() {
        // given
        Users user = createDummyCeo();
        String userIp = "userIp";
        given(totalRepository.countEndYear(Mockito.anyString())).willReturn(1);
        List<PostTotalRankingRequestDto> postTotalRankingRequestDtoList = createDummyPostTotalRankingRequestDtoList();

        // when & then
        assertThatThrownBy(() -> totalService.saveTotalRankingList(postTotalRankingRequestDtoList, user, userIp))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.FINISHED_EVALUATION.getMessage());
    }

    @Test
    @DisplayName("기존의 평가 결과가 없으면 예외를 발생시킵니다.")
    void saveTotalRankingList2() {
        // given
        Users user = createDummyCeo();
        String userIp = "userIp";
        List<PostTotalRankingRequestDto> postTotalRankingRequestDtoList = createDummyPostTotalRankingRequestDtoList();
        given(totalRepository.countEndYear(Mockito.anyString())).willReturn(0);
        given(totalMapper.postDtoListToTotalList(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString())).willReturn(createDummyTotalList());
        given(totalRepository.countTotal(Mockito.any(Total.class))).willReturn(0);

        // when & then
        assertThatThrownBy(() -> totalService.saveTotalRankingList(postTotalRankingRequestDtoList, user, userIp))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.TOTAL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("총 평가 결과를 저장합니다.")
    void saveTotalRankingList3() {
        // given
        Users user = createDummyCeo();
        String userIp = "userIp";
        List<PostTotalRankingRequestDto> postTotalRankingRequestDtoList = createDummyPostTotalRankingRequestDtoList();
        List<Total> totalList = createDummyTotalList();
        given(totalRepository.countEndYear(Mockito.anyString())).willReturn(0);
        given(totalMapper.postDtoListToTotalList(Mockito.anyList(), Mockito.any(Users.class), Mockito.anyString())).willReturn(totalList);
        given(totalRepository.countTotal(Mockito.any(Total.class))).willReturn(1, 1, 1);

        // when
        totalService.saveTotalRankingList(postTotalRankingRequestDtoList, user, userIp);

        // then
        Mockito.verify(totalRepository, Mockito.times(totalList.size())).updateTotalRanking(Mockito.any(Total.class));
    }

    @Test
    @DisplayName("이미 마감된 연도인 경우 연도 마감을 요청할 수 없습니다.")
    void endYear() {
        // given
        String year = "2024";
        Users user = createDummyCeo();
        String userIp = "userIp";
        given(totalRepository.countEndYear(Mockito.anyString())).willReturn(1);

        // when & then
        assertThatThrownBy(() -> totalService.endYear(year, user, userIp))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessage(ExceptionCode.FINISHED_EVALUATION.getMessage());
    }

    @Test
    @DisplayName("주어진 연도의 평가 종료 처리 (연도 마감)")
    void endYear2() {
        // given
        String year = "2024";
        Users user = createDummyCeo();
        String userIp = "userIp";
        given(totalRepository.countEndYear(Mockito.anyString())).willReturn(0);

        // when
        totalService.endYear(year, user, userIp);

        // then
        Mockito.verify(totalRepository, Mockito.times(1)).postEndYear(Mockito.any());
    }

    @Test
    @DisplayName("주어진 연도의 평가 종료 취소 처리 (연도 마감 취소)")
    void cancelEndYear() {
        // given
        String year = "2024";
        given(totalRepository.countEndYear(Mockito.anyString())).willReturn(1);

        // when
        totalService.cancelEndYear(year);

        // then
        Mockito.verify(totalRepository, Mockito.times(1)).deleteEndYear(Mockito.anyString());
    }

    @Test
    @DisplayName("마감된 연도가 아니면 연도 마감 취소를 요청할 수 없습니다.")
    void cancelEndYear2() {
        // given
        String year = "2024";
        given(totalRepository.countEndYear(Mockito.anyString())).willReturn(0);

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
        given(totalRepository.countEndYear(Mockito.anyString())).willReturn(1);

        // when
        boolean actual = totalService.checkEndedYear(year);

        // then
        assertTrue(actual);
    }

    @Test
    @DisplayName("모든 평가가 완료되었는지 확인합니다. 모든 평가가 완료되었으면 true를 반환합니다.")
    void checkAllEvaluationsComplete() {
        // given
        String year = "2024";
        given(totalRepository.checkAllEvaluationsComplete(Mockito.anyString())).willReturn(0);

        // when
        boolean actual = totalService.checkAllEvaluationsComplete(year);

        // then
        assertTrue(actual);
    }
}
