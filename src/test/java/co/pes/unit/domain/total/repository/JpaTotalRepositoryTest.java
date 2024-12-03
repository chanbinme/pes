package co.pes.unit.domain.total.repository;

import static co.pes.utils.TestUtils.createDummyEvaluationTotalEntityList;
import static org.assertj.core.api.Assertions.assertThat;

import co.pes.common.config.QueryDslConfig;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.total.entity.EvaluationTotalEntity;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.repository.JpaTotalRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JpaTotalRepositoryTest {

    @Autowired
    private JpaTotalRepository totalRepository;

    @Test
    @DisplayName("팀 ID 리스트로 해당 팀의 총점을 조회한다.")
    void getTotalByTeamIdList() {
        // given
        String year = "2024";
        List<EvaluationTotalEntity> totalList = createDummyEvaluationTotalEntityList(Arrays.asList(17L, 24L, 25L));
        List<EvaluationTotalEntity> evaluationTotalEntityList = totalRepository.saveAll(totalList);
        List<Long> chargeTeamIds = evaluationTotalEntityList.stream()
            .map(EvaluationTotalEntity::getOrganization).mapToLong(OrganizationEntity::getId).boxed().collect(Collectors.toList());
        List<Long> expectedTeamIdList = Arrays.asList(17L, 24L, 25L);

        // when
        List<TotalRanking> actual = totalRepository.getTotalByTeamIdList(year, chargeTeamIds);

        // then
        List<Long> actualTeamIdList = actual.stream().mapToLong(TotalRanking::getTeamId).boxed().collect(Collectors.toList());
        assertThat(actualTeamIdList).containsAll(expectedTeamIdList);
    }

    @Test
    @DisplayName("팀 ID 리스트로 해당 팀이 소속된 부서의 총점을 조회한다.")
    void getOfficerTotalByTeamIdList() {
        // given
        String year = "2024";
        List<EvaluationTotalEntity> totalList = createDummyEvaluationTotalEntityList(Arrays.asList(1L, 2L, 7L, 17L, 24L, 25L));
        totalRepository.saveAll(totalList);
        List<Long> chargeTeamIds = Arrays.asList(17L, 24L, 25L);
        List<Long> expectedTeamIdList = Arrays.asList(1L, 2L, 7L);  // 17, 24, 25 팀이 소속된 부서 ID

        // when
        List<TotalRanking> actual = totalRepository.getOfficerTotalByTeamIdList(year, chargeTeamIds);

        // then
        List<Long> actualTeamIdList = actual.stream().mapToLong(TotalRanking::getTeamId).boxed().collect(Collectors.toList());
        assertThat(actualTeamIdList).containsAll(expectedTeamIdList);
    }
}
