package co.pes.domain.total.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.controller.dto.TotalRankingRequestDto;
import co.pes.domain.total.entity.EndYearEntity;
import co.pes.domain.total.entity.EvaluationTotalEntity;
import co.pes.domain.total.mapper.TotalMapper;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.repository.JpaEndYearRepository;
import co.pes.domain.total.repository.JpaTotalRepository;
import java.util.ArrayList;
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
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JpaTotalService extends AbstractTotalService {

    private final JpaTotalRepository totalRepository;
    private final TotalMapper totalMapper;
    private final JpaEndYearRepository endYearRepository;
    private final JpaOrganizationRepository organizationRepository;

    @Override
    @Transactional
    public void saveTotalRankingList(List<PostTotalRankingRequestDto> postTotalRankingRequestDtoList, Users user, String userIp) {
        if (this.checkEndedYear(postTotalRankingRequestDtoList.get(0).getYear())) {
            throw new BusinessLogicException(ExceptionCode.FINISHED_EVALUATION);
        }
        List<EvaluationTotalEntity> evaluationTotalEntityList = totalMapper.postDtoListToEvaluationTotalEntityList(postTotalRankingRequestDtoList, user, userIp);
        totalRepository.saveAll(evaluationTotalEntityList);
    }

    @Override
    @Transactional
    public void endYear(String year, Users user, String userIp) {
        EndYearEntity endYear = EndYearEntity.builder()
            .year(year)
            .insUser(user.getName())
            .insIp(userIp)
            .build();

        if (this.checkEndedYear(year)) {
            throw new BusinessLogicException(ExceptionCode.FINISHED_EVALUATION);
        }
        endYearRepository.save(endYear);
    }

    @Override
    @Transactional
    public void cancelEndYear(String year) {
        if (!this.checkEndedYear(year)) {
            throw new BusinessLogicException(ExceptionCode.NOT_FINISHED_EVALUATION);
        }
        endYearRepository.deleteByYear(year);
    }

    @Override
    public boolean checkEndedYear(String year) {
        return endYearRepository.existsByYear(year);
    }

    @Override
    protected List<TotalRanking> getTotalList(String year, List<TotalRankingRequestDto> totalRankingRequestDtoList) {
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

    @Override
    protected void saveOrUpdateTeamTotal(TotalRequestDto totalRequestDto, Users user, String userIp) {
        OrganizationEntity organization = organizationRepository.findById(totalRequestDto.getTeamId())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORGANIZATION_NOT_FOUND));
        EvaluationTotalEntity evaluationTotal = totalMapper.dtoToTeamEvaluationTotalEntity(totalRequestDto, user, userIp, organization);
        totalRepository.save(evaluationTotal);
    }

    @Override
    protected void saveOrUpdateOfficerTotal(TotalRequestDto totalRequestDto, Users user, String userIp) {
        OrganizationEntity officerTeam = organizationRepository.searchOfficerTeamByTeamId(totalRequestDto.getTeamId())
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORGANIZATION_NOT_FOUND));
        EvaluationTotalEntity officerTotal = totalMapper.dtoToOfficerEvaluationTotalEntity(totalRequestDto, user, userIp, officerTeam);

        if (!officerTotal.getOrganization().isTopLevel()) {   // 최고 관리 조직은 Total 저장되지 않음
            List<Long> teamIdList = organizationRepository.getSubTeamIdList(officerTeam.getId());    // Officer이 관리하는 매핑 팀 수
            double sumTeamTotalPoint = totalRepository.sumSubTeamTotalPoint(teamIdList, officerTotal.getYear());    // Officer이 관리하는 팀들의 최종 점수 합계
            int teamCount = teamIdList.size();
            double officerTotalPoint = (Math.round((sumTeamTotalPoint / teamCount) * 10) / 10.0);
            officerTotal.changeTotalPoint(officerTotalPoint);

            totalRepository.save(officerTotal);
        }
    }

    @Override
    public boolean existsTotal(Mapping mapping) {
        return totalRepository.existsByOrganizationId(mapping.getChargeTeamId());
    }

    @Override
    public boolean checkAllEvaluationsComplete(String year) {
        return totalRepository.checkAllEvaluationsComplete(year);
    }

    @Override
    public List<String> getEvaluationYearList() {
        return totalRepository.getEvaluationYearList();
    }

    @Override
    public boolean existsByYearAndOrganizationId(String year, Long chargeTeamId) {
        return totalRepository.existsByYearAndOrganizationId(year, chargeTeamId);
    }
}
