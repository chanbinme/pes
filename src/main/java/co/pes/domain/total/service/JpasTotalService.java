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
public class JpasTotalService extends AbstractTotalService {

    private final TotalRepository totalRepository;
    private final TotalMapper totalMapper;
    private final MybatisMemberInfoRepository mybatisMemberInfoRepository;

    @Override
    public void saveTotalRankingList(
        List<PostTotalRankingRequestDto> postTotalRankingRequestDtoList, Users user, String userIp) {

    }

    @Override
    public void endYear(String year, Users user, String userIp) {

    }

    @Override
    public void cancelEndYear(String year) {

    }

    @Override
    public boolean checkEndedYear(String year) {
        return false;
    }

    @Override
    protected List<TotalRanking> getTotalList(String year,
        List<TotalRankingRequestDto> totalRankingRequestDtoList) {
        return Collections.emptyList();
    }

    @Override
    protected void saveOrUpdateTeamTotal(TotalRequestDto totalRequestDto, Users user,
        String userIp) {

    }

    @Override
    protected void saveOrUpdateOfficerTotal(TotalRequestDto totalRequestDto, Users user,
        String userIp) {

    }

    @Override
    protected boolean existsTotal(Total total) {
        return false;
    }

    @Override
    public boolean existsTotal(Mapping mapping) {
        return false;
    }

    @Override
    public boolean checkAllEvaluationsComplete(String year) {
        return false;
    }

    @Override
    public List<String> getEvaluationYearList() {
        return Collections.emptyList();
    }
}
