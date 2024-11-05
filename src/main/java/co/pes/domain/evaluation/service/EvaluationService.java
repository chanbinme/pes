package co.pes.domain.evaluation.service;

import co.pes.domain.evaluation.controller.dto.FinalEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.member.model.Users;
import java.util.List;

/**
 * @author cbkim
 * @PackageName: co.pes.evaluation.service
 * @FileName : EvaluationService.java
 * @Date : 2023. 12. 7.
 * @프로그램 설명 : 평가 데이터를 관리하는 Service Class
 */
public interface EvaluationService {

    /**
     * 주어진 연도와 팀에 대한 평가 정보를 가져옵니다.
     *
     * @param year         평가 연도
     * @param chargeTeamId 담당 팀 ID
     * @param user         요청하는 사용자
     * @return 평가 정보가 포함된 TaskEvaluationResponseDto
     */
    TaskEvaluationResponseDto getEvaluationInfo(String year, Long chargeTeamId, Users user);

    /**
     * 직무 평가 정보를 임시 저장합니다.
     *
     * @param taskEvaluationRequestDtoList 직무 평가 요청 DTO 목록
     * @param user 평가하는 사용자
     * @param userIp 사용자의 IP 주소
     */
    void saveTaskEvaluationList(List<TaskEvaluationRequestDto> taskEvaluationRequestDtoList, Users user, String userIp);

    /**
     * 최종 직무 평가 목록을 저장하고 총 평가 결과를 업데이트합니다.
     *
     * @param finalEvaluationRequestDto 최종 평가 요청 DTO
     * @param user 평가하는 사용자
     * @param userIp 사용자의 IP 주소
     */
    void finalSaveTaskEvaluationList(FinalEvaluationRequestDto finalEvaluationRequestDto, Users user, String userIp);

    /**
     * 오늘이 임원 평가 기간에 속하는지 확인합니다.
     *
     * @return 임원 평가 기간 여부를 나타내는 메시지
     */
    String checkOfficerEvaluationPeriod();
}