package co.pes.domain.task.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.EvaluationRepository;
import co.pes.domain.member.model.Users;
import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.controller.dto.TaskRequestDto;
import co.pes.domain.task.mapper.TaskInfoMapper;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.repository.TaskManagerRepository;
import co.pes.domain.total.service.TotalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MybatisTaskManagerServiceImpl extends AbstractTaskManagerService{

    private final TaskManagerRepository taskManagerRepository;
    private final EvaluationRepository evaluationRepository;
    private final TotalService totalService;
    private final TaskInfoMapper taskInfoMapper;

    @Override
    public List<Project> getProjects(String year) {
        return taskManagerRepository.getProjectListByYear(year);
    }

    @Override
    public List<Tasks> getTasks(String year, String projectTitle) {
        List<Tasks> taskList = taskManagerRepository.getTaskList(year, projectTitle);

        for (Tasks task : taskList) {
            List<Long> chargeTeamIds = taskManagerRepository.findChargeTeamIds(task);
            if (!CollectionUtils.isEmpty(chargeTeamIds)) {
                task.addChargeTeamIds(chargeTeamIds);
                List<String> chargeTeamTitles = taskManagerRepository.findChargeTeamTitles(chargeTeamIds);
                task.addChargeTeamTitles(chargeTeamTitles);
            }
        }

        return taskList;
    }

    @Override
    @Transactional
    public void postMapping(List<MappingDto> mappingDtos, Users user, String userIp) {
        List<Mapping> mappingInfoList = taskInfoMapper.mappingDtoListToMappingList(mappingDtos, user, userIp);

        for (Mapping mappingInfo : mappingInfoList) {
            // 해당 업무의 매핑 정보가 이미 존재한다면 초기화
            this.resetMapping(mappingInfo);
        }

        for (Mapping mappingInfo : mappingInfoList) {
            Long chargeTeamId = mappingInfo.getChargeTeamId();
            if (chargeTeamId != null) {
                this.findAndDesignateChargePerson(mappingInfo, chargeTeamId);

                if (totalService.existsTotal(mappingInfo.getChargeTeamId())) {
                    log.info("postMapping exception occur mapping : {}", mappingInfo);
                    throw new BusinessLogicException(ExceptionCode.INVALID_MAPPING);
                }

                int postMappingInfoCount = taskManagerRepository.postMappingInfo(mappingInfo);
                if (postMappingInfoCount < 1) {
                    log.info("매핑 정보가 저장되지 않았습니다. {}", mappingInfo);
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteMappingInfo(List<MappingDto> mappingDtos) {
        List<Mapping> mappingInfoList = taskInfoMapper.mappingDtoListToMappingList(mappingDtos);

        for (Mapping mappingInfo : mappingInfoList) {
            Long chargeTeamId = mappingInfo.getChargeTeamId();
            String teamLeaderName = taskManagerRepository.findTeamLeaderNameByChargeTeamId(chargeTeamId);   // Manager 이름 조회
            String officerName = taskManagerRepository.findOfficerNameByChargeTeamId(chargeTeamId);    // Officer 이름 조회

            TaskEvaluation taskEvaluation = TaskEvaluation.builder()
                .taskId(mappingInfo.getTaskId())
                .chargeTeam(teamLeaderName)
                .chargeOfficer(officerName)
                .build();

            String evaluationState = evaluationRepository.findEvaluationState(taskEvaluation);

            if (StringUtils.hasText(evaluationState)) {
                if (evaluationState.equals("F")) {      // 평가 최종 제출된 업무는 평가 삭제 및 매핑 초기화 불가
                    throw new BusinessLogicException(ExceptionCode.FINAL_SAVE_EVALUATION);
                } else if (evaluationState.equals("N")){    // 평가중인 업무는 삭제 가능
                    evaluationRepository.deleteTaskEvaluation(taskEvaluation);
                }
            }

            // 해당 업무의 매핑 정보가 이미 존재한다면 초기화
            this.resetMapping(mappingInfo);
        }
    }

    @Override
    @Transactional
    public void deleteTasks(List<TaskRequestDto> taskRequestDtos) {
        if (taskManagerRepository.countMappingInfo(taskRequestDtos) > 0) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS_MAPPING);
        }
        taskManagerRepository.deleteTasks(taskRequestDtos);
    }

    @Override
    protected void findAndDesignateChargePerson(Mapping mappingInfo, Long chargeTeamId) {
        String teamLeaderName = taskManagerRepository.findTeamLeaderNameByChargeTeamId(chargeTeamId);
        String officerName = taskManagerRepository.findOfficerNameByChargeTeamId(chargeTeamId);
        mappingInfo.designateChargePerson(teamLeaderName, officerName);
    }

    protected void resetMapping(Mapping mappingInfo) {
        List<Mapping> findMappingInfoList = taskManagerRepository.findMappingInfo(mappingInfo);
        if (!CollectionUtils.isEmpty(findMappingInfoList)) {
            for (Mapping findMappingInfo : findMappingInfoList) {
                if (totalService.existsTotal(findMappingInfo.getChargeTeamId())) {
                    log.info("resetMapping exception occur mapping : {}",
                        findMappingInfo.toString());
                    throw new BusinessLogicException(ExceptionCode.INVALID_MAPPING);
                }
            }
            taskManagerRepository.resetMappingInfo(mappingInfo);
        }
    }
}
