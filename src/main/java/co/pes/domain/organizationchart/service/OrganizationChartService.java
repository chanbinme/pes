package co.pes.domain.organizationchart.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.member.model.Users;
import co.pes.domain.organizationchart.model.OrganizationChart;
import co.pes.domain.organizationchart.repository.OrganizationChartRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author cbkim
 * @PackageName: co.pes.organizationchart.service
 * @FileName : OrganizationChartService.java
 * @Date : 2023. 11. 30.
 * @프로그램 설명 : 조직도 관련 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationChartService {

    private final OrganizationChartRepository organizationChartRepository;

    /**
     * 조직도 정보를 조회합니다. 평가 페이지에서 조회하는 경우, 사용자의 팀 정보만 조직도에 표시합니다.
     *
     * @param isEvaluationPage 평가 페이지에서의 요청인지 여부
     * @param user           요청하는 사용자
     * @return 조직도 정보
     */
    public List<OrganizationChart> findOrganizationChartInfo(Boolean isEvaluationPage, Users user) {
        if (!user.isAdminOrCeo() && !user.isOfficer()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
        if (Boolean.TRUE.equals(isEvaluationPage) && user.isOfficer()) {
                return this.findOrganizationChartInfo(user);
            }
        return organizationChartRepository.findOrganizationChartInfo();
    }

    /**
     * 사용자의 팀 정보를 기반으로 조직도 정보를 조회합니다.
     *
     * @param user 요청하는 사용자
     * @return 조직도 정보
     */
    public List <OrganizationChart> findOrganizationChartInfo(Users user) {
        String id = user.getId();
        List<Long> teamIdList = organizationChartRepository.getChargeTeamIdByUserId(id);
        List<OrganizationChart> organizationChart = new ArrayList<>();

        if (teamIdList != null) {
            organizationChart = organizationChartRepository.findOrganizationChartInfoByTeamId(teamIdList);
        }

        return organizationChart;
    }
}
