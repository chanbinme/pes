package co.pes.unit.domain.total.repository;

import static co.pes.utils.TestUtils.createDummyEndYear;
import static co.pes.utils.TestUtils.createDummyMappingList;
import static co.pes.utils.TestUtils.createDummyOfficerTotal;
import static co.pes.utils.TestUtils.createDummyTeamTotal;
import static co.pes.utils.TestUtils.createDummyTotalList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.pes.domain.task.model.Mapping;
import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.Total;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.repository.TotalRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TotalRepositoryTest {

    @Autowired
    private TotalRepository totalRepository;

    @Autowired
    private SqlSession sqlSession;

    private final String TOTAL_QUERY_NAME_SPACE = "co.pes.domain.total.repository.TotalRepository.";

    @Test
    @DisplayName("특정 연도와 팀 아이디로 총점 데이터가 존재하는지 확인합니다.")
    void countTotal() {
        // given
        List<Total> totalList = createDummyTotalList(); // 팀 아이디가 다른 더미 데이터 3개 생성
        totalList.forEach(total -> totalRepository.saveTotal(total));

        // when
        int actual = totalRepository.countTotal(totalList.get(0));

        // then
        assertEquals(1, actual);
    }

    @Test
    @DisplayName("총점 데이터를 저장합니다.")
    void saveTotal() {
        // given
        Total total = createDummyOfficerTotal();

        // when
        totalRepository.saveTotal(total);

        // then
        List<Total> actual = sqlSession.selectList(TOTAL_QUERY_NAME_SPACE + "getTotalByTeamIdAndYear", total);
        assertAll(
            () -> assertEquals(1, actual.size()),
            () -> assertEquals(total.getYear(), actual.get(0).getYear()),
            () -> assertEquals(total.getTeamId(), actual.get(0).getTeamId()),
            () -> assertEquals(total.getTeamTitle(), actual.get(0).getTeamTitle()),
            () -> assertEquals(total.getTotalPoint(), actual.get(0).getTotalPoint())
        );
    }

    @Test
    @DisplayName("총점 데이터를 수정합니다.")
    void updateTotal() {
        // given
        Total total = createDummyOfficerTotal();
        totalRepository.saveTotal(total);
        double beforeTotalPoint = total.getTotalPoint();
        total.changeTotalPoint(100.0);

        // when
        totalRepository.updateTotal(total);

        // then
        List<Total> actual = sqlSession.selectList(TOTAL_QUERY_NAME_SPACE + "getTotalByTeamIdAndYear", total);
        assertAll(
            () -> assertEquals(1, actual.size()),
            () -> assertEquals(total.getTotalPoint(), actual.get(0).getTotalPoint()),
            () -> assertNotEquals(beforeTotalPoint, actual.get(0).getTotalPoint())
        );
    }

    @Test
    @DisplayName("특정 팀 아이디로 하위 팀의 총점을 합산합니다.")
    void sumTeamTotalPoint() {
        // given
        List<Total> teamTotalList = createDummyTotalList(); // 부서가 같은 하위 팀 더미 데이터 3개 생성
        teamTotalList.forEach(total -> totalRepository.saveTotal(total));
        Total officerTotal = createDummyOfficerTotal();

        // when
        double actual = totalRepository.sumTeamTotalPoint(officerTotal);

        // then
        double expected = teamTotalList.stream()
            .mapToDouble(Total::getTotalPoint)
            .sum();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("특정 연도와 팀 아이디 목록으로 총점 데이터를 조회합니다. 해당 팀의 부서 정보도 함께 조회합니다.")
    void getTotalByTeamIdList() {
        // given
        List<Total> teamTotalList = createDummyTotalList(); // 부서가 같은 하위 팀 더미 데이터 3개 생성
        teamTotalList.forEach(total -> totalRepository.saveTotal(total));
        List<Long> teamIdList = teamTotalList.stream().mapToLong(Total::getTeamId).boxed().collect(Collectors.toList());

        // when
        List<TotalRanking> actual = totalRepository.getTotalByTeamIdList(teamTotalList.get(0).getYear(), teamIdList);

        // then
        Total officerTotal = createDummyOfficerTotal(); // 부서 정보
        assertAll(
            () -> assertEquals(teamIdList.size(), actual.size()),
            () -> {
                Total expected0 = teamTotalList.get(0);
                TotalRanking actual0 = actual.stream().filter(totalRanking -> totalRanking.getTeamId()
                    .equals(expected0.getTeamId())).findFirst().orElse(null);
                assert actual0 != null;
                assertEquals(expected0.getTeamId(), actual0.getTeamId());
                assertEquals(expected0.getTeamTitle(), actual0.getTeamTitle());
                assertEquals(expected0.getTotalPoint(), actual0.getTotalPoint());
                assertEquals("Manager", actual0.getPosition());
                assertEquals(officerTotal.getTeamTitle(), actual0.getDivisionTitle());
            },
            () -> {
                Total expected1 = teamTotalList.get(1);
                TotalRanking actual1 = actual.stream().filter(totalRanking -> totalRanking.getTeamId()
                    .equals(expected1.getTeamId())).findFirst().orElse(null);
                assert actual1 != null;
                assertEquals(expected1.getTeamId(), actual1.getTeamId());
                assertEquals(expected1.getTeamTitle(), actual1.getTeamTitle());
                assertEquals(expected1.getTotalPoint(), actual1.getTotalPoint());
                assertEquals("Manager", actual1.getPosition());
                assertEquals(officerTotal.getTeamTitle(), actual1.getDivisionTitle());
            }
        );
    }

    @Test
    @DisplayName("특정 연도와 팀 아이디 목록으로 팀의 부서 총점 데이터를 조회합니다.")
    void getOfficerTotalByTeamIdList() {
        // given
        Total officerTotal = createDummyOfficerTotal(); // 부서 정보
        totalRepository.saveTotal(officerTotal);
        List<Long> teamIdList = createDummyTotalList()
            .stream().mapToLong(Total::getTeamId).boxed().collect(Collectors.toList()); // 부서 하위 팀 아이디 리스트

        // when
        List<TotalRanking> actual = totalRepository.getOfficerTotalByTeamIdList(officerTotal.getYear(), teamIdList);

        // then
        assertAll(
            () -> assertEquals(1, actual.size()),
            () -> assertEquals(officerTotal.getTeamId(), actual.get(0).getTeamId()),
            () -> assertEquals(officerTotal.getTeamTitle(), actual.get(0).getTeamTitle()),
            () -> assertEquals(officerTotal.getTotalPoint(), actual.get(0).getTotalPoint()),
            () -> assertEquals("Officer", actual.get(0).getPosition())
        );
    }

    @Test
    @DisplayName("총점 데이터의 랭크를 수정합니다.")
    void updateTotalRanking() {
        // given
        Total total = createDummyOfficerTotal();
        totalRepository.saveTotal(total);
        String beforeRanking = total.getRanking();
        total.changeRanking("S");

        // when
        totalRepository.updateTotalRanking(total);

        // then
        List<Total> actual = sqlSession.selectList(TOTAL_QUERY_NAME_SPACE + "getTotalByTeamIdAndYear", total);
        assertAll(
            () -> assertEquals(1, actual.size()),
            () -> assertEquals(total.getRanking(), actual.get(0).getRanking()),
            () -> assertNotEquals(beforeRanking, actual.get(0).getRanking())
        );
    }

    @Test
    @DisplayName("특정 연도의 종료 여부를 확인합니다.")
    void countEndYear() {
        // given
        String successYear = "2024";
        String failYear = "2224";
        totalRepository.postEndYear(createDummyEndYear(successYear));

        // when
        int successActual = totalRepository.countEndYear(successYear);
        int failActual = totalRepository.countEndYear(failYear);

        // then
        assertEquals(1, successActual);
        assertEquals(0, failActual);
    }

    @Test
    @DisplayName("특정 연도의 랭크가 없는 팀의 수를 확인합니다.")
    void checkAllEvaluationsComplete() {
        // given
        List<Total> totalList = createDummyTotalList(); // 더미 데이터 3개 생성
        totalList.forEach(total -> totalRepository.saveTotal(total));
        totalList.get(0).changeRanking("S");
        totalRepository.updateTotalRanking(totalList.get(0));

        // when
        int actual = totalRepository.checkAllEvaluationsComplete(totalList.get(0).getYear());

        // then
        long expected = totalList.stream().filter(total -> total.getRanking() == null).count();
        assertAll(
            () -> assertNotEquals(totalList.size(), actual),
            () -> assertEquals(expected, actual)
        );
    }

    @Test
    @DisplayName("특정 팀 아이디로 부서 정보를 조회합니다.")
    void findOfficerTeamInfoByTeamId() {
        // given
        Total teamTotal = createDummyTeamTotal();   // 부서의 하위 팀

        // when
        OfficerTeamInfo actual = totalRepository.findOfficerTeamInfoByTeamId(teamTotal.getTeamId());

        // then
        Total officerTotal = createDummyOfficerTotal(); // 부서
        assertAll(
            () -> assertEquals(officerTotal.getTeamId(), actual.getTeamId()),
            () -> assertEquals(officerTotal.getTeamTitle(), actual.getTeamTitle())
        );
    }

    @Test
    @DisplayName("팀 아이디로 총점 데이터가 있는지 확인합니다.")
    void countTotalByMapping() {
        // given
        Total total = createDummyTeamTotal();
        totalRepository.saveTotal(total);
        Mapping successMapping = createDummyMappingList().get(0);
        Mapping failMapping = createDummyMappingList().get(1);

        // when
        int successActual = totalRepository.countTotalByMapping(successMapping);
        int failActual = totalRepository.countTotalByMapping(failMapping);

        // then
        assertEquals(1, successActual);
        assertEquals(0, failActual);
    }

    @Test
    @DisplayName("평가 연도 목록을 조회합니다. 가장 최근 평가 연도가 먼저 조회됩니다.")
    void getEvaluationYearList() {
        // when
        List<String> actual = totalRepository.getEvaluationYearList();

        // then
        assertAll(
            () -> assertNotNull(actual),
            () -> assertTrue(Integer.parseInt(actual.get(0)) > Integer.parseInt(actual.get(1))),
            () -> assertThat(actual).contains("2024", "2023")
        );
    }
}
