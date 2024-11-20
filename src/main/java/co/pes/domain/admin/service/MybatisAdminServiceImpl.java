package co.pes.domain.admin.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.admin.controller.dto.OfficerEvaluationPeriodDto;
import co.pes.domain.admin.mapper.AdminMapper;
import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import co.pes.domain.admin.repository.AdminRepository;
import co.pes.domain.member.model.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author cbkim
 * @PackageName : co.pes.admin.service
 * @FileName : AdminService.java
 * @Date : 2024. 1. 2.
 * @프로그램 설명 : 관리 페이지의 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MybatisAdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final AdminRepository adminRepository;

    /**
     * 임원 평가 기간을 설정합니다.
     *
     * @param officerEvaluationPeriodDto 임원 평가 기간 DTO
     * @param user 사용자
     * @param userIp 사용자의 IP 주소
     */
    @Transactional
    public void postOfficerEvaluationPeriod(OfficerEvaluationPeriodDto officerEvaluationPeriodDto,
                                            Users user, String userIp) {
        OfficerEvaluationPeriod officerEvaluationPeriod =
            adminMapper.dtoToOfficerEvaluationPeriod(officerEvaluationPeriodDto, user, userIp);
        adminRepository.postOfficerEvaluationPeriod(officerEvaluationPeriod);
    }

    /**
     * 임원 평가 기간을 조회합니다.
     *
     * @return 임원 평가 기간
     * @throws BusinessLogicException 임원 평가 기간을 찾을 수 없는 경우
     */
    public OfficerEvaluationPeriod getOfficerEvaluationPeriod() {
        OfficerEvaluationPeriod officerEvaluationPeriod = adminRepository.getOfficerEvaluationPeriod();
        if (officerEvaluationPeriod == null) {
            throw new BusinessLogicException(ExceptionCode.OFFICER_EVALUATION_PERIOD_NOT_FOUND);
        }

        return officerEvaluationPeriod;
    }
}
