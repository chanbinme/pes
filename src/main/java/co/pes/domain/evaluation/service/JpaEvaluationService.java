package co.pes.domain.evaluation.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.admin.service.AdminService;
import co.pes.domain.evaluation.controller.dto.FinalEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.entity.TaskEvaluationEntity;
import co.pes.domain.evaluation.mapper.EvaluationMapper;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.evaluation.repository.JpaEvaluationRepository;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaOrganizationRepository;
import co.pes.domain.task.entity.TaskEntity;
import co.pes.domain.task.repository.JpaTaskManagerRepository;
import co.pes.domain.total.service.TotalService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Primary
@Transactional(readOnly = true)
public class JpaEvaluationService extends AbstractEvaluationService {

    private final JpaEvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final JpaTaskManagerRepository taskManagerRepository;
    private final JpaOrganizationRepository organaizationRepository;
    private final TotalService totalService;

    public JpaEvaluationService(JpaEvaluationRepository evaluationRepository, AdminService adminService,  JpaOrganizationRepository organizationRepository,
        EvaluationMapper evaluationMapper, JpaTaskManagerRepository taskManagerRepository, TotalService totalService) {
        super(adminService);
        this.evaluationRepository = evaluationRepository;
        this.evaluationMapper = evaluationMapper;
        this.taskManagerRepository = taskManagerRepository;
        this.organaizationRepository = organizationRepository;
        this.totalService = totalService;
    }

    @Override
    public TaskEvaluationResponseDto getEvaluationInfo(String year, Long chargeTeamId, Users user) {
        // 평가 완료된 팀인지 체크합니다. 평가 완료된 팀이라면 해당 팀의 평가 정보는 수정할 수 없습니다.
        boolean existsTotal = totalService.existsByYearAndOrganizationId(year, chargeTeamId);
        List<TaskEvaluation> taskEvaluationInfoList;
        List<Long> checkTeamIdList = organaizationRepository.getIdListByUserId(user.getId());    // 평가자가 관리하는 Team Id List 가져오기

        if (this.hasDescendant(chargeTeamId)) {
            List<Long> teamIdList = this.getTeamIdList(chargeTeamId, user, checkTeamIdList);
            taskEvaluationInfoList = evaluationRepository.getTaskEvaluationInfoListByTeamIdList(year, teamIdList);
        } else {
            taskEvaluationInfoList = evaluationRepository.getTaskEvaluationInfoListByTeamId(year, chargeTeamId);
        }

        return TaskEvaluationResponseDto.builder()
            .existsTotal(existsTotal)
            .taskEvaluationList(taskEvaluationInfoList)
            .build();
    }

    @Override
    protected List<Long> getTeamIdList(Long chargeTeamId, Users user, List<Long> checkTeamIdList) {
        List<Long> teamIdList = new ArrayList<>();  // 팀 ID 리스트
        List<Long> descendantOrgIdList = organaizationRepository.getIdListByAncestorOrgId(chargeTeamId, null);

        for (Long descendantOrgId : descendantOrgIdList) {
            this.addDescendantTeams(teamIdList, descendantOrgId, user, checkTeamIdList);
        }
        return teamIdList;
    }

    @Override
    protected void addDescendantTeams(List<Long> teamIdList, Long descendantOrgId, Users user, List<Long> checkTeamIdList) {
        if (this.hasDescendant(descendantOrgId)) {
            if (user.isAdminOrCeo()) {
                teamIdList.addAll(organaizationRepository.getIdListByAncestorOrgId(descendantOrgId, null));
            } else {
                teamIdList.addAll(organaizationRepository.getIdListByAncestorOrgId(descendantOrgId, checkTeamIdList));
            }
        } else {
            if (user.isAdminOrCeo()) {
                teamIdList.add(descendantOrgId);
            } else {
                // 평가자가 관리하는 Team만 Add
                if (checkTeamIdList.contains(descendantOrgId)) {
                    teamIdList.add(descendantOrgId);
                }
            }
        }
    }

    @Override
    protected boolean hasDescendant(Long chargeTeamId) {
        return organaizationRepository.existsDescendantOrgByAncestorOrgId(chargeTeamId);
    }

    @Override
    @Transactional
    public void saveTaskEvaluationList(List<TaskEvaluationRequestDto> taskEvaluationRequestDtoList, Users user, String userIp) {
        List<TaskEvaluationEntity>  taskEvaluationEntityList = new ArrayList<>();
        taskEvaluationRequestDtoList.forEach(dto -> {
            TaskEntity task = this.getTaskByTaskId(dto.getTaskId());
            OrganizationEntity organization = this.getOrganizationByOrganizationId(dto.getChargeTeamId());
            TaskEvaluationEntity taskEvaluationEntity = evaluationMapper.dtoToTaskEvaluationEntity(dto, task, organization, user.getName(), userIp);
            taskEvaluationEntity.changeState("N");
            taskEvaluationEntityList.add(taskEvaluationEntity);
        });
        evaluationRepository.saveAll(taskEvaluationEntityList);
    }

    private OrganizationEntity getOrganizationByOrganizationId(Long organizationId) {
        return organaizationRepository.findById(organizationId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORGANIZATION_NOT_FOUND));
    }

    private TaskEntity getTaskByTaskId(Long taskId) {
        return taskManagerRepository.findById(taskId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.TASK_NOT_FOUND));
    }

    @Override
    @Transactional
    public void finalSaveTaskEvaluationList(FinalEvaluationRequestDto finalEvaluationRequestDto, Users user, String userIp) {
        List<TaskEvaluationEntity>  taskEvaluationEntityList = new ArrayList<>();
        finalEvaluationRequestDto.getTaskEvaluationRequestDtoList().forEach(dto -> {
            TaskEntity task = this.getTaskByTaskId(dto.getTaskId());
            OrganizationEntity organization = this.getOrganizationByOrganizationId(dto.getChargeTeamId());
            TaskEvaluationEntity taskEvaluationEntity = evaluationMapper.dtoToTaskEvaluationEntity(dto, task, organization, user.getName(), userIp);
            taskEvaluationEntity.changeState("F");
            taskEvaluationEntityList.add(taskEvaluationEntity);
        });
        evaluationRepository.saveAll(taskEvaluationEntityList);
        totalService.saveTotal(finalEvaluationRequestDto.getTotalRequestDto(), user, userIp);
    }

    @Override
    protected boolean existsByTaskId(Long taskId) {
        return evaluationRepository.existsByIdTaskId(taskId);
    }
}