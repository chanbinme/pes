package co.pes.domain.organizationchart.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.organizationchart.model.OrganizationChart;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JpaOrganizationChartServiceImpl implements OrganizationChartService {

    private final JpaOrganizationRepository organizationRepository;

    public List<OrganizationChart> findOrganizationChartInfo(Boolean isEvaluationPage, Users user) {
        if (!user.isAdminOrCeo() && !user.isOfficer()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
        if (Boolean.TRUE.equals(isEvaluationPage) && user.isOfficer()) {
                return this.findOrganizationChartInfo(user);
            }
        return organizationRepository.searchOrganizationChartInfo();
    }

    public List <OrganizationChart> findOrganizationChartInfo(Users user) {
        String userId = user.getId();
        List<Long> teamIdList = organizationRepository.searchChargeTeamIdsByUserId(userId);
        List<OrganizationChart> organizationChart = new ArrayList<>();

        if (teamIdList != null && !teamIdList.isEmpty()) {
            organizationChart = organizationRepository.searchOrganizationChartInfoByTeamId(teamIdList);
        }

        return organizationChart;
    }
}
