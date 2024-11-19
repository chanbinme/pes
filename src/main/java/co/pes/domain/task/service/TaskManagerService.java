package co.pes.domain.task.service;

import co.pes.domain.member.model.Users;
import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.controller.dto.TaskRequestDto;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author cbkim
 * @PackageName: co.pes.task.service
 * @FileName : TaskManagerService.java
 * @Date : 2023. 12. 5.
 * @프로그램 설명 : 평가할 직무를 관리하는 로직을 처리합니다.
 */
public interface TaskManagerService {

    /**
     * 특정 연도의 프로젝트 목록을 조회합니다.
     *
     * @param year 평가 연도
     * @return
     */
    List<Project> getProjects(String year);

    /**
     * 특정 연도의 프로젝트에 포함된 업무 목록을 조회합니다.
     *
     * @param year 평가 연도
     * @param projectTitle 프로젝트 제목
     * @return
     */
    List<Tasks> getTasks(String year, String projectTitle);

    /**
     * 업무 - 팀 매핑 정보를 저장합니다.
     *
     * @param mappingDtos 매핑 정보 DTO
     * @param user 매핑 저장을 요청한 사용자
     * @param userIp 사용자의 IP 주소
     */
    void postMapping(List<MappingDto> mappingDtos, Users user, String userIp);

    /**
     * 업무 - 팀 매핑 정보를 삭제합니다.
     *
     * @param mappingDtos 매핑 정보 DTO
     */
    void deleteMappingInfo(List<MappingDto> mappingDtos);

    /**
     * 업무 정보를 삭제합니다.
     *
     * @param taskRequestDtos 업무 정보 DTO
     */
    void deleteTasks(List<TaskRequestDto> taskRequestDtos);
}
