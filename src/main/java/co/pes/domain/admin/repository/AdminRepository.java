package co.pes.domain.admin.repository;

import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AdminRepository {

    void postOfficerEvaluationPeriod(OfficerEvaluationPeriod officerEvaluationPeriod);

    OfficerEvaluationPeriod getOfficerEvaluationPeriod();
}
