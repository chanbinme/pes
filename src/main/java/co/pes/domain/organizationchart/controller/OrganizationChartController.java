package co.pes.domain.organizationchart.controller;

import co.pes.common.SessionsUser;
import co.pes.domain.member.model.Users;
import co.pes.domain.organizationchart.model.OrganizationChart;
import co.pes.domain.organizationchart.service.OrganizationChartService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cbkim
 * @PackageName: co.pes.organizationchart.controller
 * @FileName : LoginManagerController.java
 * @Date : 2023. 9. 5.
 * @프로그램 설명 : 로그인을 관리하는 Controller Class
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrganizationChartController {

    private final OrganizationChartService organizationChartService;

    @GetMapping("/am/organizationchart")
    public List<OrganizationChart> getOrganizationChart(HttpServletRequest request,
        @RequestParam(value = "isEvaluationPage", required = false) Boolean isEvaluationPage) {
        Users user = SessionsUser.getSessionUser(request.getSession());

        return organizationChartService.findOrganizationChartInfo(isEvaluationPage, user);
    }
}
