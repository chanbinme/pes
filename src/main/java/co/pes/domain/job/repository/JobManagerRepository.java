package co.pes.domain.job.repository;

import co.pes.domain.job.model.Tasks;
import co.pes.domain.job.model.Mapping;
import co.pes.domain.job.model.Project;
import co.pes.domain.job.controller.dto.JobRequestDto;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface JobManagerRepository {

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

    void deleteJobs(List<JobRequestDto> jobRequestDtos);

    int countMappingInfo(List<JobRequestDto> jobRequestDtos);
}
