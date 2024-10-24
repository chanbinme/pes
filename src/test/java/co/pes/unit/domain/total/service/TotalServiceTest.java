package co.pes.unit.domain.total.service;

import static co.pes.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.MemberInfoRepository;
import co.pes.domain.total.mapper.TotalMapper;
import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.Total;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.repository.TotalRepository;
import co.pes.domain.total.service.TotalService;
import co.pes.utils.TestUtils;
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
    @DisplayName("총 평가 결과를 기반으로 등급을 계산합니다.")
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
            () -> assertEquals(teamTotalRankingList.size(), actual.size()),
            () -> assertEquals(officerTotalRankingList.size(), actual.size())
        );
    }
}
