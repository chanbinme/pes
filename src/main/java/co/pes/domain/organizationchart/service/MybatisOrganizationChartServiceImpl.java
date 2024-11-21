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

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MybatisOrganizationChartServiceImpl implements OrganizationChartService {

    private final OrganizationChartRepository organizationChartRepository;

    public List<OrganizationChart> findOrganizationChartInfo(Boolean isEvaluationPage, Users user) {
        if (!user.isAdminOrCeo() && !user.isOfficer()) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
        if (Boolean.TRUE.equals(isEvaluationPage) && user.isOfficer()) {
                return this.findOrganizationChartInfo(user);
            }
        return organizationChartRepository.findOrganizationChartInfo();
    }

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
