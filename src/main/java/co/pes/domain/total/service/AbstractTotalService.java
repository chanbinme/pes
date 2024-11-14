package co.pes.domain.total.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.MybatisMemberInfoRepository;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.controller.dto.TotalRankingRequestDto;
import co.pes.domain.total.mapper.TotalMapper;
import co.pes.domain.total.model.EndYear;
import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.Total;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.repository.TotalRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author cbkim
 * @PackageName: co.pes.evaluation.service
 * @FileName : TotalService.java
 * @Date : 2023. 12. 20.
 * @프로그램 설명 : 총 평가 결과를 관리합니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public abstract class AbstractTotalService implements TotalService {

    @Override
    @Transactional
    public void saveTotal(TotalRequestDto totalRequestDto, Users user, String userIp) {
        this.saveOrUpdateTeamTotal(totalRequestDto, user, userIp);
        this.saveOrUpdateOfficerTotal(totalRequestDto, user, userIp);
    }

    @Override
    public List<TotalRanking> findTotalListAndCalculateRanking(String year, List<TotalRankingRequestDto> totalRankingRequestDtoList) {
        List<TotalRanking> totalList = this.getTotalList(year, totalRankingRequestDtoList);
        return this.evaluationRank(totalList);
    }

    @Override
    public List<TotalRanking> findTotalListForPreview(String year, List<TotalRankingRequestDto> totalRankingRequestDtoList) {
        List<TotalRanking> totalList = this.getTotalList(year, totalRankingRequestDtoList);
        totalList.sort(TotalRanking.totalRankingComparator);
        return totalList;
    }

    protected abstract List<TotalRanking> getTotalList(String year, List<TotalRankingRequestDto> totalRankingRequestDtoList);

    /**
     * 총 평가 결과를 기반으로 등급을 계산합니다.
     * (S: 125 이상, A: 112-124, B: 79-111, C: 66-78, D: 0-65)
     *
     * @param totalRankingList 총 평가 결과 목록
     * @return 등급이 부여된 총 평가 결과 목록
     */
    protected List<TotalRanking> evaluationRank(List<TotalRanking> totalRankingList) {
        List<Double> totalPoints = new ArrayList<>();

        for (TotalRanking totalRanking : totalRankingList) {
            totalPoints.add(totalRanking.getTotalPoint());
        }

        List<String> ranks = this.calculateRanks(totalPoints);

        for (int i = 0; i < totalRankingList.size(); i++) {
            totalRankingList.get(i).updateNewRanking(ranks.get(i));
        }
        totalRankingList.sort(Collections.reverseOrder());

        return totalRankingList;
    }

    // Manager 평가 결과 저장
    protected abstract void saveOrUpdateTeamTotal(TotalRequestDto totalRequestDto, Users user, String userIp);

    // Officer 평가 결과 저장
    protected abstract void saveOrUpdateOfficerTotal(TotalRequestDto totalRequestDto, Users user, String userIp);

    protected abstract boolean existsTotal(Total total);

    private List<String> calculateRanks(List<Double> totalPoints) {
        List<String> grades = new ArrayList<>();
        String grade = "";
        for (Double totalPoint : totalPoints) {
            // S: 125 이상, A: 112-124, B: 79-111, C: 66-78, D: 0-65
            if (totalPoint >= 125) {
                grade = "S";
            } else if (totalPoint >= 112) {
                grade = "A";
            } else if (totalPoint >= 79) {
                grade = "B";
            } else if (totalPoint >= 66) {
                grade = "C";
            } else {
                grade = "D";
            }
            grades.add(grade);
        }
        return grades;
    }
}
