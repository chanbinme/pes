package co.pes.domain.admin.repository;

import co.pes.domain.admin.entity.OfficerEvaluationPeriodEntity;
import co.pes.domain.admin.entity.QOfficerEvaluationPeriodEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaOfficerEvaluationPeriodRepositoryImpl implements JpaOfficerEvaluationPeriodRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Optional<OfficerEvaluationPeriodEntity> findByRecentPeriod() {
        QOfficerEvaluationPeriodEntity oep = QOfficerEvaluationPeriodEntity.officerEvaluationPeriodEntity;
        return Optional.ofNullable(query
            .select(oep)
            .from(oep)
            .orderBy(oep.id.desc())
            .fetchFirst());
    }
}
