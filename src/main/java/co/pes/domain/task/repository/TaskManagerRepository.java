package co.pes.domain.task.repository;

import co.pes.domain.task.model.Tasks;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.controller.dto.TaskRequestDto;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TaskManagerRepository {

    List<Project> getProjectListByYear(@Param("year") String year);

    List<Tasks> getTaskList(@Param("year") String year, @Param("projectTitle") String projectTitle);

    int postMappingInfo(@Param("mappingInfo") Mapping mappingInfo);

    String findChargeTeam(@Param("teamId") Long teamId);

    String findChargeOfficer(@Param("teamId") Long teamId);

    List<Long> findChargeTeamIds(@Param("task") Tasks task);

    List<String> findChargeTeamTitles(List<Long> chargeTeamIds);

    void resetMappingInfo(@Param("mappingInfo") Mapping mappingInfo);

    List<Mapping> findMappingInfo(@Param("mappingInfo") Mapping mappingInfo);

    String findTeamLeaderNameByChargeTeamId(Long chargeTeamId);

    String findOfficerNameByChargeTeamId(Long chargeTeamId);

    void deleteTasks(List<TaskRequestDto> taskRequestDtos);

    int countMappingInfo(List<TaskRequestDto> taskRequestDtos);
}
