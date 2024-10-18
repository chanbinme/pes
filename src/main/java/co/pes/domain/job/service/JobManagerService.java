package co.pes.domain.job.service;

import co.pes.domain.evaluation.model.JobEvaluation;
import co.pes.domain.job.model.Tasks;
import co.pes.domain.job.model.Project;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.service.TotalService;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.repository.EvaluationRepository;
import co.pes.domain.job.controller.dto.JobRequestDto;
import co.pes.domain.job.controller.dto.MappingDto;
import co.pes.domain.job.mapper.JobInfoMapper;
import co.pes.domain.job.model.Mapping;
import co.pes.domain.job.repository.JobManagerRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


/**
 * @author cbkim
 * @PackageName: co.pes.job.service
 * @FileName : JobManagerService.java
 * @Date : 2023. 12. 5.
 * @프로그램 설명 : 평가할 직무를 관리하는 로직을 처리합니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JobManagerService {

    private final JobManagerRepository jobManagerRepository;
    private final EvaluationRepository evaluationRepository;
    private final TotalService totalService;
    private final JobInfoMapper jobInfoMapper;

    /**
     * 특정 연도의 프로젝트 목록을 조회합니다.
     */
    /**
     * 특정 연도의 프로젝트 목록을 조회합니다.
     *
     * @param year 평가 연도
     * @return
     */
    public List<Project> getProjects(String year) {
        return jobManagerRepository.getProjectListByYear(year);
    }

    /**
     * 특정 연도의 프로젝트에 포함된 업무 목록을 조회합니다.
     *
     * @param year 평가 연도
     * @param projectTitle 프로젝트 제목
     * @return
     */
    public List<Tasks> getTasks(String year, String projectTitle) {
        List<Tasks> taskList = jobManagerRepository.getTaskList(year, projectTitle);

        for (Tasks task : taskList) {
            List<Long> chargeTeamIds = jobManagerRepository.findChargeTeamIds(task);
            if (!chargeTeamIds.isEmpty()) {
                task.addChargeTeamIds(chargeTeamIds);
                List<String> chargeTeamTitles = jobManagerRepository.findChargeTeamTitles(chargeTeamIds);
                task.addChargeTeamTitles(chargeTeamTitles);
            }
        }

        return taskList;
    }

    /**
     * 업무 - 팀 매핑 정보를 저장합니다.
     *
     * @param mappingDtos 매핑 정보 DTO
     * @param user 매핑 저장을 요청한 사용자
     * @param userIp 사용자의 IP 주소
     */
    @Transactional
    public void postMapping(List<MappingDto> mappingDtos, Users user, String userIp) {
        List<Mapping> mappingInfoList = jobInfoMapper.mappingDtoListToMappingList(mappingDtos, user, userIp);

        for (Mapping mappingInfo : mappingInfoList) {
            // 해당 업무의 매핑 정보가 이미 존재한다면 초기화
            this.resetMapping(mappingInfo);
        }

        for (Mapping mappingInfo : mappingInfoList) {
            Long chargeTeamId = mappingInfo.getChargeTeamId();
            if (chargeTeamId != null) {
                this.findAndDesignateChargePerson(mappingInfo, chargeTeamId);

                if (totalService.existsTotal(mappingInfo)) {
                    log.info("postMapping exception occur mapping : {}", mappingInfo);
                    throw new BusinessLogicException(ExceptionCode.INVALID_MAPPING);
                }

                int postMappingInfoCount = jobManagerRepository.postMappingInfo(mappingInfo);
                if (postMappingInfoCount < 1) {
                    log.info("매핑 정보가 저장되지 않았습니다. {}", mappingInfo);
                }
            }
        }
    }

    /**
     * 업무 - 팀 매핑 정보를 삭제합니다.
     *
     * @param mappingDtos 매핑 정보 DTO
     */
    @Transactional
    public void deleteMappingInfo(List<MappingDto> mappingDtos) {
        List<Mapping> mappingInfoList = jobInfoMapper.mappingDtoListToMappingList(mappingDtos);

        for (Mapping mappingInfo : mappingInfoList) {
            Long chargeTeamId = mappingInfo.getChargeTeamId();
            String teamLeaderName = jobManagerRepository.findTeamLeaderNameByChargeTeamId(chargeTeamId);   // Manager 이름 조회
            String officerName = jobManagerRepository.findOfficerNameByChargeTeamId(chargeTeamId);    // Officer 이름 조회

            JobEvaluation jobEvaluation = JobEvaluation.builder()
                .taskId(mappingInfo.getTaskId())
                .chargeTeam(teamLeaderName)
                .chargeOfficer(officerName)
                .build();

            String evaluationState = evaluationRepository.findEvaluationState(jobEvaluation);

            if (StringUtils.hasText(evaluationState)) {
                if (evaluationState.equals("F")) {      // 평가 최종 제출된 업무는 평가 삭제 및 매핑 초기화 불가
                    throw new BusinessLogicException(ExceptionCode.FINAL_SAVE_EVALUATION);
                } else if (evaluationState.equals("N")){    // 평가중인 업무는 삭제 가능
                    evaluationRepository.deleteJobEvaluation(jobEvaluation);
                }
            }

            // 해당 업무의 매핑 정보가 이미 존재한다면 초기화
            this.resetMapping(mappingInfo);
        }
    }

    /**
     * 업무 정보를 삭제합니다.
     *
     * @param jobRequestDtos 업무 정보 DTO
     */
    @Transactional
    public void deleteJobs(List<JobRequestDto> jobRequestDtos) {
        if (jobManagerRepository.countMappingInfo(jobRequestDtos) > 0) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS_MAPPING);
        }
        jobManagerRepository.deleteJobs(jobRequestDtos);
    }

    /**
     * 업무 담당 Manager, Officer를 조회하여 지정합니다.
     *
     * @param mappingInfo 매핑 정보
     * @param chargeTeamId 담당 팀 ID
     */
    private void findAndDesignateChargePerson(Mapping mappingInfo, Long chargeTeamId) {
        String chargeTeam = jobManagerRepository.findChargeTeam(chargeTeamId);
        String chargeOfficer = jobManagerRepository.findChargeOfficer(chargeTeamId);
        mappingInfo.designateChargePerson(chargeTeam, chargeOfficer);
    }

    /**
     * 기존 매핑 정보 초기화합니다. 최종 평가 완료된 팀이라면 초기화 불가합니다.
     *
     * @param mappingInfo 매핑 정보
     */
    private void resetMapping(Mapping mappingInfo) {
        List<Mapping> findMappingInfoList = jobManagerRepository.findMappingInfo(mappingInfo);
        if (!findMappingInfoList.isEmpty()) {
            for (Mapping findMappingInfo : findMappingInfoList) {
                if (totalService.existsTotal(findMappingInfo)) {
                    log.info("resetMapping exception occur mapping : {}",
                        findMappingInfo.toString());
                    throw new BusinessLogicException(ExceptionCode.INVALID_MAPPING);
                }
            }
            jobManagerRepository.resetMappingInfo(mappingInfo);
        }
    }
}
