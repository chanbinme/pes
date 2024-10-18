package co.pes.domain.organizationchart.repository;

import co.pes.domain.organizationchart.model.OrganizationChart;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrganizationChartRepository {

    List<OrganizationChart> findOrganizationChartInfo();

    List<Long> getChargeTeamIdByUserId(@Param(value = "id") String id);

    List<OrganizationChart> findOrganizationChartInfoByTeamId(@Param(value = "teamIdList") List<Long> teamIdList);
}
