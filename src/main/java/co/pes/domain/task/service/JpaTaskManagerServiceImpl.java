package co.pes.domain.task.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.entity.TaskEvaluationEntityId;
import co.pes.domain.evaluation.repository.JpaEvaluationRepository;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.controller.dto.TaskRequestDto;
import co.pes.domain.task.entity.TaskEntity;
import co.pes.domain.task.entity.TaskOrganizationMappingEntity;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.repository.JpaTaskManagerRepository;
import co.pes.domain.task.repository.JpaTaskOrganizationMappingRepository;
import co.pes.domain.total.service.TotalService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Slf4j
@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JpaTaskManagerServiceImpl extends AbstractTaskManagerService {

    private final JpaTaskManagerRepository taskManagerRepository;
    private final JpaEvaluationRepository evaluationRepository;
    private final JpaOrganizationRepository organizationRepository;
    private final JpaTaskOrganizationMappingRepository taskOrganizationMappingRepository;
    private final TotalService totalService;

    @Override
    public List<Project> getProjects(String year) {
        return taskManagerRepository.searchProjectTitleByYear(year);
    }

    @Override
    public List<Tasks> getTasks(String year, String projectTitle) {
        List<Tasks> taskList = taskManagerRepository.searchTasksByYearAndProjectTitle(year, projectTitle);

        for (Tasks task : taskList) {
            List<Long> chargeTeamIds = taskManagerRepository.searchChargeTeamIdsByTaskId(task.getId());
            if (!CollectionUtils.isEmpty(chargeTeamIds)) {
                task.addChargeTeamIds(chargeTeamIds);
                List<String> chargeTeamTitles = organizationRepository.searchChargeTeamTitlesByTeamIds(chargeTeamIds);
                task.addChargeTeamTitles(chargeTeamTitles);
            }
        }

        return taskList;
    }

    @Override
    @Transactional
    public void postMapping(List<MappingDto> mappingDtos, Users user, String userIp) {
        List<TaskOrganizationMappingEntity> mappingEntityList = this.mappingDtoListToMappingEntityList(mappingDtos, user, userIp);

        // 해당 업무의 매핑 정보가 이미 존재한다면 초기화
        for (TaskOrganizationMappingEntity mappingEntity : mappingEntityList) {
            this.resetMapping(mappingEntity);
        }

        // 매핑하려는 팀이 이미 최종 평가된 팀이라면 매핑 불가
        List<Long> chargeTeamIdList = mappingEntityList.stream()
            .map(TaskOrganizationMappingEntity::getOrganization).mapToLong(OrganizationEntity::getId).boxed().collect(Collectors.toList());
        for (Long chargeTeamId : chargeTeamIdList) {
            if (totalService.existsTotal(chargeTeamId)) {
                log.info("postMapping exception occur chargeTeamId : {}", chargeTeamId);
                throw new BusinessLogicException(ExceptionCode.INVALID_MAPPING);
            }
        }
        taskOrganizationMappingRepository.saveAll(mappingEntityList);
    }

    @Override
    @Transactional
    public void deleteMappingInfo(List<MappingDto> mappingDtos) {
        List<TaskOrganizationMappingEntity> mappingEntityList = this.mappingDtoListToMappingEntityList(mappingDtos, null, null);
        List<Long> mappedTaskIdList = mappingEntityList.stream().map(TaskOrganizationMappingEntity::getTask)
            .mapToLong(TaskEntity::getId).boxed().collect(Collectors.toList());

        // 평가 최종 제출된 업무는 평가 삭제 및 매핑 초기화 불가
        if (evaluationRepository.containsFinalSaveEvaluation(mappedTaskIdList)) {
            throw new BusinessLogicException(ExceptionCode.FINAL_SAVE_EVALUATION);
        }

        // 평가중인 업무 평가 정보 삭제
        List<TaskEvaluationEntityId> taskEvaluationEntityIdList = new ArrayList<>();
        for (TaskOrganizationMappingEntity mappingEntity : mappingEntityList) {
            TaskEvaluationEntityId taskEvaluationEntityId = TaskEvaluationEntityId.builder()
                .taskId(mappingEntity.getTask().getId())
                .chargeTeamId(mappingEntity.getOrganization().getId())
                .build();
            taskEvaluationEntityIdList.add(taskEvaluationEntityId);
        }
        evaluationRepository.removeAllByIdList(taskEvaluationEntityIdList);

        for (TaskOrganizationMappingEntity mappingEntity : mappingEntityList) {
            // 해당 업무의 매핑 정보가 이미 존재한다면 초기화
            this.resetMapping(mappingEntity);
        }
    }

    @Override
    @Transactional
    public void deleteTasks(List<TaskRequestDto> taskRequestDtos) {
        List<Long> taskIdList = taskRequestDtos.stream().mapToLong(TaskRequestDto::getTaskId).boxed().collect(Collectors.toList());
        if (evaluationRepository.existsByIdTaskIdIn(taskIdList)) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS_MAPPING);
        }
        taskManagerRepository.removeAllByIdList(taskIdList);
    }

    /**
     * 기존 매핑 정보 초기화합니다. 최종 평가 완료된 팀이라면 초기화 불가합니다.
     *
     * @param TaskOrganizationMappingEntity 매핑 정보
     */
    protected void resetMapping(TaskOrganizationMappingEntity TaskOrganizationMappingEntity) {
        // 해당 업무에 이미 매핑된 팀이 최종 평가된 팀이라면 초기화 불가
        Long mappingTaskId = TaskOrganizationMappingEntity.getTask().getId();
        List<Long> mappedTeamIdList = taskManagerRepository.searchTeamIdByTaskId(mappingTaskId);
        if (!CollectionUtils.isEmpty(mappedTeamIdList)) {
            for (Long mappedTeamId : mappedTeamIdList) {
                if (totalService.existsTotal(mappedTeamId)) {
                    log.info("resetMapping exception occur mapping : {}", mappedTeamId);
                    throw new BusinessLogicException(ExceptionCode.INVALID_MAPPING);
                }
            }
            taskOrganizationMappingRepository.deleteByTaskId(mappingTaskId);
        }
    }

    private List<TaskOrganizationMappingEntity> mappingDtoListToMappingEntityList(List<MappingDto> mappingDtos, Users user, String userIp) {
        List<TaskOrganizationMappingEntity> taskOrganizationMappingEntityList = new ArrayList<>();
        for (MappingDto mappingDto : mappingDtos) {
            TaskEntity task = taskManagerRepository.getReferenceById(mappingDto.getTaskId());
            OrganizationEntity organization = organizationRepository.getReferenceById(mappingDto.getChargeTeamId());
            taskOrganizationMappingEntityList.add(TaskOrganizationMappingEntity.builder()
                .task(task)
                .organization(organization)
                .insIp(userIp)
                .insUser(user != null ? user.getName() : null)
                .build());
        }
        return taskOrganizationMappingEntityList;
    }
}
