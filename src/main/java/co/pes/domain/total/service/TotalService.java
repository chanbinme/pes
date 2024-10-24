package co.pes.domain.total.service;

import co.pes.domain.member.repository.MemberInfoRepository;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.model.EndYear;
import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.controller.dto.TotalRankingRequestDto;
import co.pes.domain.total.mapper.TotalMapper;
import co.pes.domain.total.model.Total;
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
@RequiredArgsConstructor
public class TotalService {

    private final TotalRepository totalRepository;
    private final TotalMapper totalMapper;
    private final MemberInfoRepository memberInfoRepository;

    @Transactional
    public void saveTotal(TotalRequestDto totalRequestDto, Users user, String userIp) {
        this.saveOrUpdateTeamTotal(totalRequestDto, user, userIp);
        this.saveOrUpdateOfficerTotal(totalRequestDto, user, userIp);
    }

    public List<TotalRanking> findTotalListAndCalculateRanking(String year, List<TotalRankingRequestDto> totalRankingRequestDtoList) {
        List<TotalRanking> totalList = this.getTotalList(year, totalRankingRequestDtoList);
        return this.evaluationRank(totalList);
    }

    public List<TotalRanking> findTotalListForPreview(String year, List<TotalRankingRequestDto> totalRankingRequestDtoList) {
        List<TotalRanking> totalList = this.getTotalList(year, totalRankingRequestDtoList);
        totalList.sort(TotalRanking.totalRankingComparator);
        return totalList;
    }

    /**
     * 총 평가 결과를 저장합니다.
     *
     * @param postTotalRankingRequestDtoList 총 평가 결과 목록
     * @param user 총 평가 결과를 저장한 사용자
     * @param userIp 사용자의 IP 주소
     */
    @Transactional
    public void saveTotalRankingList(List<PostTotalRankingRequestDto> postTotalRankingRequestDtoList,
        Users user, String userIp) {
        if (this.checkEndedYear(postTotalRankingRequestDtoList.get(0).getYear())) {
            throw new BusinessLogicException(ExceptionCode.FINISHED_EVALUATION);
        }

        List<Total> totalList = totalMapper.postDtoListToTotalList(
            postTotalRankingRequestDtoList, user, userIp);

        for (Total total : totalList) {
            if (this.existsTotal(total)) {
                totalRepository.updateTotalRanking(total);
            } else {
                log.info("saveTotalRankingList exception occur name : {}, positionGb : {}, ranking : {}",
                    total.getName(), total.getPositionGb(), total.getRanking());
                throw new BusinessLogicException(ExceptionCode.TOTAL_NOT_FOUND);
            }
        }
    }

    /**
     * 주어진 연도의 평가 종료 처리 (연도 마감)
     *
     * @param year 평가 연도
     * @param user 연도 마감을 요청한 사용자
     * @param userIp 사용자의 IP 주소
     */
    @Transactional
    public void endYear(String year, Users user, String userIp) {
        EndYear endYear = EndYear.builder()
            .year(year)
            .insUser(user.getName())
            .insDate(LocalDateTime.now())
            .insIp(userIp)
            .build();

        if (this.checkEndedYear(year)) {
            throw new BusinessLogicException(ExceptionCode.FINISHED_EVALUATION);
        }
        totalRepository.postEndYear(endYear);
    }

    /**
     * 주어진 연도의 평가 종료 취소 처리 (연도 마감 취소)
     *
     * @param year 평가 연도
     */
    @Transactional
    public void cancelEndYear(String year) {
        if (this.checkEndedYear(year)) {
            totalRepository.deleteEndYear(year);
        } else {
            throw new BusinessLogicException(ExceptionCode.NOT_FINISHED_EVALUATION);
        }
    }

    public boolean checkEndedYear(String year) {
        return totalRepository.countEndYear(year) > 0;
    }

    private List<TotalRanking> getTotalList(
        String year, List<TotalRankingRequestDto> totalRankingRequestDtoList) {
        List<TotalRanking> totalList = new ArrayList<>();
        List<Long> teamIdList = totalRankingRequestDtoList.stream()
            .map(TotalRankingRequestDto::getTeamId).collect(Collectors.toList());
        List<TotalRanking> totalRankingList  = totalRepository.getTotalByTeamIdList(year, teamIdList);

        if (totalRankingList != null) {
            totalList.addAll(totalRankingList);
        }

        List<TotalRanking> officerTotalList = totalRepository.getOfficerTotalByTeamIdList(year, teamIdList);
        if (officerTotalList != null) {
            totalList.addAll(officerTotalList);
        }

        return totalList;
    }

    /**
     * 총 평가 결과를 기반으로 등급을 계산합니다.
     * (S: 125 이상, A: 112-124, B: 79-111, C: 66-78, D: 0-65)
     *
     * @param totalRankingList
     * @return
     */
    private List<TotalRanking> evaluationRank(List<TotalRanking> totalRankingList) {
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
    private void saveOrUpdateTeamTotal(TotalRequestDto totalRequestDto, Users user, String userIp) {
        Total teamTotal = totalMapper.dtoToTeamTotal(totalRequestDto, user, userIp);

        if (this.existsTotal(teamTotal)) {
            totalRepository.updateTotal(teamTotal);
        } else {
            totalRepository.saveTotal(teamTotal);
        }
    }

    // Officer 평가 결과 저장
    private void saveOrUpdateOfficerTotal(TotalRequestDto totalRequestDto, Users user, String userIp) {
        OfficerTeamInfo officerTeamInfo = totalRepository.findOfficerTeamInfoByTeamId(totalRequestDto.getTeamId());
        Total officerTotal = totalMapper.dtoToOfficerTotal(totalRequestDto, user, userIp, officerTeamInfo.getTeamId(), officerTeamInfo.getTeamTitle());
        String officerId = "";

        if (officerTotal.getTeamId() != 26) {   // 본부는 Total 저장되지 않음
            officerId = memberInfoRepository.findIdByNameAndPositionGb(officerTotal.getName(), officerTotal.getPositionGb());
            officerTotal.setOfficerId(officerId);
            int teamCount = totalRepository.countMappingTeamByTeamId(officerTotal.getTeamId());    // Officer이 관리하는 매핑 팀 수
            double sumTeamTotalPoint = totalRepository.sumTeamTotalPoint(officerTotal);    // Officer이 관리하는 팀들의 최종 점수 합계

            double officerTotalPoint = (Math.round((sumTeamTotalPoint / teamCount) * 10) / 10.0);
            officerTotal.changeTotalPoint(officerTotalPoint);

            if (this.existsTotal(officerTotal)) {
                totalRepository.updateTotal(officerTotal);
            } else {
                totalRepository.saveTotal(officerTotal);
            }
        }
    }

    private boolean existsTotal(Total total) {
        int result = totalRepository.countTotal(total);
        return result > 0;
    }

    public boolean existsTotal(Mapping mapping) {
        int result = totalRepository.countTotalByMapping(mapping);
        return result > 0;
    }

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

    public boolean checkAllEvaluationsComplete(String year) {
        return totalRepository.checkAllEvaluationsComplete(year) == 0;
    }

    public List<String> getEvaluationYearList() {
        return totalRepository.getEvaluationYearList();
    }
}
