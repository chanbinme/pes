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
import org.springframework.context.annotation.Primary;
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
public class MybatisTotalService extends AbstractTotalService {

    private final TotalRepository totalRepository;
    private final TotalMapper totalMapper;
    private final MybatisMemberInfoRepository mybatisMemberInfoRepository;

    @Override
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

    @Override
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

    @Override
    @Transactional
    public void cancelEndYear(String year) {
        if (this.checkEndedYear(year)) {
            totalRepository.deleteEndYear(year);
        } else {
            throw new BusinessLogicException(ExceptionCode.NOT_FINISHED_EVALUATION);
        }
    }

    @Override
    public boolean checkEndedYear(String year) {
        return totalRepository.countEndYear(year) > 0;
    }

    @Override
    protected List<TotalRanking> getTotalList(
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

    // Manager 평가 결과 저장
    @Override
    protected void saveOrUpdateTeamTotal(TotalRequestDto totalRequestDto, Users user, String userIp) {
        Total teamTotal = totalMapper.dtoToTeamTotal(totalRequestDto, user, userIp);

        if (this.existsTotal(teamTotal)) {
            totalRepository.updateTotal(teamTotal);
        } else {
            totalRepository.saveTotal(teamTotal);
        }
    }

    // Officer 평가 결과 저장
    @Override
    protected void saveOrUpdateOfficerTotal(TotalRequestDto totalRequestDto, Users user,
        String userIp) {
        OfficerTeamInfo officerTeamInfo = totalRepository.findOfficerTeamInfoByTeamId(totalRequestDto.getTeamId());
        Total officerTotal = totalMapper.dtoToOfficerTotal(totalRequestDto, user, userIp, officerTeamInfo.getTeamId(), officerTeamInfo.getTeamTitle());
        String officerId = "";

        if (officerTotal.getTeamId() != 26) {   // 본부는 Total 저장되지 않음
            officerId = mybatisMemberInfoRepository.findIdByNameAndPositionGb(officerTotal.getName(), officerTotal.getPositionGb());
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

    protected boolean existsTotal(Total total) {
        int result = totalRepository.countTotal(total);
        return result > 0;
    }

    @Override
    public boolean existsTotal(Mapping mapping) {
        int result = totalRepository.countTotalByMapping(mapping);
        return result > 0;
    }

    @Override
    public boolean checkAllEvaluationsComplete(String year) {
        return totalRepository.checkAllEvaluationsComplete(year) == 0;
    }

    @Override
    public List<String> getEvaluationYearList() {
        return totalRepository.getEvaluationYearList();
    }
}
