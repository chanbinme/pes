package co.pes.domain.admin.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.domain.admin.controller.dto.OfficerEvaluationPeriodDto;
import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import co.pes.domain.member.model.Users;

/**
 * @author cbkim
 * @PackageName : co.pes.admin.service
 * @FileName : AdminService.java
 * @Date : 2024. 1. 2.
 * @프로그램 설명 : 관리 페이지의 비즈니스 로직을 처리합니다.
 */
public interface AdminService {

    /**
     * 임원 평가 기간을 설정합니다.
     *
     * @param officerEvaluationPeriodDto 임원 평가 기간 DTO
     * @param user 사용자
     * @param userIp 사용자의 IP 주소
     */
    void postOfficerEvaluationPeriod(OfficerEvaluationPeriodDto officerEvaluationPeriodDto, Users user, String userIp);

    /**
     * 임원 평가 기간을 조회합니다.
     *
     * @return 임원 평가 기간
     * @throws BusinessLogicException 임원 평가 기간을 찾을 수 없는 경우
     */
    OfficerEvaluationPeriod getOfficerEvaluationPeriod();
}
