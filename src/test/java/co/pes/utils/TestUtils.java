package co.pes.utils;

import co.pes.domain.admin.model.OfficerEvaluationPeriod;
import co.pes.domain.evaluation.controller.dto.FinalEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationRequestDto;
import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.manager.controller.dto.LoginRequestDto;
import co.pes.domain.manager.service.dto.LoginDto;
import co.pes.domain.member.model.Users;

import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.controller.dto.TaskRequestDto;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.task.model.Project;
import co.pes.domain.task.model.Tasks;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.controller.dto.TotalRankingRequestDto;
import co.pes.domain.total.model.EndYear;
import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.Total;
import co.pes.domain.total.model.TotalRanking;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cbkim
 * @PackageName: co.pes.utils
 * @FileName : TestUtils.java
 * @Date : 2024. 10. 18.
 * @프로그램 설명 : 테스트 유틸리티 클래스
 */
public class TestUtils {

    /**
     * JSON 파일을 읽어서 문자열로 반환합니다.
     *
     * @param path JSON 파일 경로
     * @return JSON 파일 문자열
     * @throws IOException
     */
    public static String readJson(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/json/" + path)));
    }

    public static Users createDummyOfficer() {
        return Users.builder()
                .id("chb314")
                .name("김찬빈")
                .positionGb("1")
                .build();
    }

    public static Users createDummyCeo() {
        return Users.builder()
                .id("chb314")
                .name("김찬빈")
                .positionGb("0")
                .build();
    }

    public static TaskEvaluationResponseDto createDummyTaskEvaluationResponseDto() {
        List<TaskEvaluation> taskEvaluationList = Arrays.asList(
            TaskEvaluation.builder()
                .taskId(67L)
                .chargeTeam("강동우")
                .chargeOfficer("임성민")
                .chargeTeamId(20L)
                .projectTitle("사내 업무 효율화 프로젝트")
                .taskTitle("오피스 공간 리디자인")
                .taskState("완료")
                .taskProgress(100)
                .responsibility(null)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .insUser(null)
                .insDate(null)
                .insIp(null)
                .modUser(null)
                .modDate(null)
                .modIp(null)
                .build(),
            TaskEvaluation.builder()
                .taskId(68L)
                .chargeTeam("강동우")
                .chargeOfficer("임성민")
                .chargeTeamId(20L)
                .projectTitle("사내 팀빌딩 이벤트 계획")
                .taskTitle("친환경 패키징 솔루션 개발")
                .taskState("진행")
                .taskProgress(29)
                .responsibility(null)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .insUser(null)
                .insDate(null)
                .insIp(null)
                .modUser(null)
                .modDate(null)
                .modIp(null)
                .build(),
            TaskEvaluation.builder()
                .taskId(69L)
                .chargeTeam("강동우")
                .chargeOfficer("임성민")
                .chargeTeamId(20L)
                .projectTitle("사내 멘토링 프로그램 구축")
                .taskTitle("가상 현실(VR) 팀빌딩 경험 설계")
                .taskState("완료")
                .taskProgress(100)
                .responsibility(null)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .insUser(null)
                .insDate(null)
                .insIp(null)
                .modUser(null)
                .modDate(null)
                .modIp(null)
                .build(),
            TaskEvaluation.builder()
                .taskId(70L)
                .chargeTeam("강동우")
                .chargeOfficer("임성민")
                .chargeTeamId(20L)
                .projectTitle("사내 문화 확산 프로젝트")
                .taskTitle("스마트홈 음성 제어 시스템 테스트")
                .taskState("완료")
                .taskProgress(100)
                .responsibility(null)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .insUser(null)
                .insDate(null)
                .insIp(null)
                .modUser(null)
                .modDate(null)
                .modIp(null)
                .build()
        );

        return TaskEvaluationResponseDto.builder()
                .existsTotal(false)
                .taskEvaluationList(taskEvaluationList)
                .build();
    }

    public static List<Project> createDummyProjectList() {
        return Arrays.asList(
            createDummyProject(),
            createDummyProject(),
            createDummyProject()
        );
    }

    public static Project createDummyProject() {
        int id = (int) (Math.random() * 100);
        return Project.builder()
            .projectTitle("projectTitle" + id).build();
    }

    public static List<Tasks> createDummyTasksList() {
        return Arrays.asList(
            createDummyTasks(),
            createDummyTasks(),
            createDummyTasks()
        );
    }

    public static Tasks createDummyTasks() {
        int id = (int) (Math.random() * 100);
        return Tasks.builder()
            .id((long) id)
            .year("2024")
            .projectTitle("Project Title " + id)
            .taskTitle("Task Title " + id)
            .taskState("In Progress")
            .taskProgress((int) (Math.random() * 100))
            .build();
    }

    public static Tasks createDummyTasks(Long taskId) {
        return Tasks.builder()
            .id(taskId)
            .year("2024")
            .projectTitle("Project Title " + taskId)
            .taskTitle("Task Title " + taskId)
            .taskState("In Progress")
            .taskProgress((int) (Math.random() * 100))
            .build();
    }

    public static List<Tasks> createDummyTaskList(List<Long> taskIdList) {
        return taskIdList.stream().map(TestUtils::createDummyTasks).collect(Collectors.toList());
    }

    public static List<TotalRanking> createDummyTotalRankingList() {
        return Arrays.asList(
            TotalRanking.builder()
                .evaluationTotalId(3L)
                .year("2024")
                .teamId(16L)
                .teamTitle("품질관리팀")
                .divisionTitle("개발부")
                .name("김찬빈")
                .totalPoint(18.7)
                .ranking("-")
                .newRanking("D")
                .build(),
            TotalRanking.builder()
                .evaluationTotalId(2L)
                .year("2024")
                .teamId(3L)
                .teamTitle("개발부")
                .name("정준호")
                .totalPoint(13.9)
                .ranking("-")
                .newRanking("D")
                .build(),
            TotalRanking.builder()
                .evaluationTotalId(1L)
                .year("2024")
                .teamId(10L)
                .teamTitle("개발팀")
                .divisionTitle("개발부")
                .name("이승우")
                .totalPoint(9.1)
                .ranking("-")
                .newRanking("D")
                .build()
        );
    }

    public static LoginDto createDummyLoginDto() {
        return LoginDto.builder()
            .id("chb314")
            .password("b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86")
            .build();
    }

    public static LoginRequestDto createDummyLoginRequestDto() {
        return LoginRequestDto.builder()
            .id("chb314")
            .password("b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86")
            .build();
    }

    public static List<TaskEvaluation> createDummyTaskEvaluationList() {
        return Arrays.asList(
            TaskEvaluation.builder()
                .taskId(67L)
                .chargeTeam("강동우")
                .chargeOfficer("임성민")
                .chargeTeamId(20L)
                .projectTitle("사내 업무 효율화 프로젝트")
                .taskTitle("오피스 공간 리디자인")
                .taskState("완료")
                .taskProgress(100)
                .responsibility(null)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .insUser(null)
                .insDate(null)
                .insIp(null)
                .modUser(null)
                .modDate(null)
                .modIp(null)
                .build(),
            TaskEvaluation.builder()
                .taskId(68L)
                .chargeTeam("강동우")
                .chargeOfficer("임성민")
                .chargeTeamId(20L)
                .projectTitle("사내 팀빌딩 이벤트 계획")
                .taskTitle("친환경 패키징 솔루션 개발")
                .taskState("진행")
                .taskProgress(29)
                .responsibility(null)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .insUser(null)
                .insDate(null)
                .insIp(null)
                .modUser(null)
                .modDate(null)
                .modIp(null)
                .build(),
            TaskEvaluation.builder()
                .taskId(69L)
                .chargeTeam("강동우")
                .chargeOfficer("임성민")
                .chargeTeamId(20L)
                .projectTitle("사내 멘토링 프로그램 구축")
                .taskTitle("가상 현실(VR) 팀빌딩 경험 설계")
                .taskState("완료")
                .taskProgress(100)
                .responsibility(null)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .insUser(null)
                .insDate(null)
                .insIp(null)
                .modUser(null)
                .modDate(null)
                .modIp(null)
                .build(),
            TaskEvaluation.builder()
                .taskId(70L)
                .chargeTeam("강동우")
                .chargeOfficer("임성민")
                .chargeTeamId(20L)
                .projectTitle("사내 문화 확산 프로젝트")
                .taskTitle("스마트홈 음성 제어 시스템 테스트")
                .taskState("완료")
                .taskProgress(100)
                .responsibility(null)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .insUser(null)
                .insDate(null)
                .insIp(null)
                .modUser(null)
                .modDate(null)
                .modIp(null)
                .build()
        );
    }

    public static List<TaskEvaluationRequestDto> createDummyTaskEvaluationRequestDtoList() {

        return Arrays.asList(
            TaskEvaluationRequestDto.builder()
                .taskId(67L)
                .chargeTeamId(20L)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .build(),
            TaskEvaluationRequestDto.builder()
                .taskId(68L)
                .chargeTeamId(20L)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .build(),
            TaskEvaluationRequestDto.builder()
                .taskId(69L)
                .chargeTeamId(20L)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .build(),
            TaskEvaluationRequestDto.builder()
                .taskId(70L)
                .chargeTeamId(20L)
                .weight(0.0)
                .officerPoint(0)
                .ceoPoint(0)
                .taskGb("")
                .levelOfficer("")
                .levelCeo("")
                .condOfficer("")
                .condCeo("")
                .totalPoint(0.0)
                .note("")
                .state("")
                .build()
        );
    }

    public static TotalRequestDto createDummyTotalRequestDto() {
        return TotalRequestDto.builder()
            .teamId(10L)
            .year("2024")
            .teamTitle("개발팀")
            .teamName("임성민")
            .officerName("김찬빈")
            .note("")
            .totalPoint(78.9)
            .build();
    }

    public static FinalEvaluationRequestDto createDummyFinalEvaluationRequestDto() {
        return FinalEvaluationRequestDto.builder()
                .taskEvaluationRequestDtoList(createDummyTaskEvaluationRequestDtoList())
                .totalRequestDto(createDummyTotalRequestDto())
                .build();
    }

    public static OfficerEvaluationPeriod createFailDummyEvaluationPeriod() {
        return OfficerEvaluationPeriod.builder()
            .startDate(LocalDateTime.now().minusDays(2))
            .endDate(LocalDateTime.now().minusDays(1))
            .build();
    }

    public static OfficerEvaluationPeriod createSuccessDummyEvaluationPeriod() {
        return OfficerEvaluationPeriod.builder()
            .startDate(LocalDateTime.now().minusDays(2))
            .endDate(LocalDateTime.now().plusDays(2))
            .build();
    }

    public static List<MappingDto> createDummyMappingDtoList() {
        return Arrays.asList(
            MappingDto.builder()
                .chargeTeamId(1L)
                .taskId(1L)
                .build(),
            MappingDto.builder()
                .chargeTeamId(2L)
                .taskId(2L)
                .build(),
            MappingDto.builder()
                .chargeTeamId(3L)
                .taskId(3L)
                .build()
        );
    }

    public static List<Mapping> createDummyMappingList() {
        return Arrays.asList(
            Mapping.builder()
                .mappingDto(
                    MappingDto.builder()
                    .chargeTeamId(11L)
                    .taskId(1L)
                    .build())
                .user(createDummyCeo())
                .userIp("userIp")
                .build(),
            Mapping.builder()
                .mappingDto(
                    MappingDto.builder()
                    .chargeTeamId(2L)
                    .taskId(2L)
                    .build())
                .user(createDummyCeo())
                .userIp("userIp")
                .build(),
            Mapping.builder()
                .mappingDto(
                    MappingDto.builder()
                    .chargeTeamId(3L)
                    .taskId(3L)
                    .build())
                .user(createDummyCeo())
                .userIp("userIp")
                .build()
        );
    }

    public static List<TaskRequestDto> createDummyTaskRequestDtoList() {
        return Arrays.asList(
            TaskRequestDto.builder()
                .taskId(1L)
                .build(),
            TaskRequestDto.builder()
                .taskId(2L)
                .build(),
            TaskRequestDto.builder()
                .taskId(3L)
                .build()
        );
    }

    public static Total createDummyTeamTotal() {
        return Total.builder()
            .evaluationTotalId(1L)
            .year("2024")
            .teamId(11L)
            .teamTitle("영업팀")
            .name("박상현")
            .totalPoint(127.3)
            .positionGb("1")
            .build();
    }

    public static Total createDummyOfficerTotal() {
        return Total.builder()
            .evaluationTotalId(1L)
            .year("2024")
            .teamId(1L)
            .teamTitle("판매부")
            .name("김찬빈")
            .totalPoint(150.5)
            .positionGb("1")
            .ranking("-")
            .build();
    }

    public static Total createDummyCeoTotal() {
        return Total.builder()
            .evaluationTotalId(1L)
            .year("2024")
            .teamId(26L)
            .teamTitle("기술부")
            .name("김찬빈")
            .totalPoint(78.1)
            .positionGb("1")
            .ranking("-")
            .build();
    }

    public static OfficerTeamInfo createDummyOfficerTeamInfo() {
        return OfficerTeamInfo.builder()
            .teamId(1L)
            .teamTitle("기술부")
            .build();
    }

    public static List<TotalRanking> createDummyTeamTotalRankingList() {
        return Arrays.asList(
            TotalRanking.builder()
                .evaluationTotalId(3L)
                .year("2024")
                .teamId(16L)
                .teamTitle("품질관리팀")
                .divisionTitle("개발부")
                .name("김찬빈")
                .totalPoint(60)
                .ranking("-")
                .build(),
            TotalRanking.builder()
                .evaluationTotalId(1L)
                .year("2024")
                .teamId(10L)
                .teamTitle("개발팀")
                .divisionTitle("개발부")
                .name("이승우")
                .totalPoint(70)
                .ranking("-")
                .build(),
            TotalRanking.builder()
                .evaluationTotalId(2L)
                .year("2024")
                .teamId(3L)
                .teamTitle("개발부")
                .name("정준호")
                .totalPoint(99)
                .ranking("-")
                .build(),
            TotalRanking.builder()
                .evaluationTotalId(4L)
                .year("2024")
                .teamId(20L)
                .teamTitle("기술부")
                .name("강동우")
                .totalPoint(113.9)
                .ranking("-")
                .build()
        );
    }

    public static List<TotalRanking> createDummyOfficerTotalRankingList() {
        return Arrays.asList(
            TotalRanking.builder()
                .evaluationTotalId(1L)
                .year("2024")
                .teamId(1L)
                .teamTitle("기술부")
                .name("박상현")
                .totalPoint(127.3)
                .ranking("-")
                .build()
        );
    }

    public static List<TotalRankingRequestDto> createTotalRankingRequestDtoList() {
        return Arrays.asList(
            TotalRankingRequestDto.builder()
                .teamId(1L)
                .teamTitle("기술부")
                .build(),
            TotalRankingRequestDto.builder()
                .teamId(2L)
                .teamTitle("개발부")
                .build(),
            TotalRankingRequestDto.builder()
                .teamId(3L)
                .teamTitle("품질관리팀")
                .build()
        );
    }

    public static List<TotalRanking> createDummyTeamTotalRankingListForPreview() {
        return Arrays.asList(
            TotalRanking.builder()
                .evaluationTotalId(3L)
                .year("2024")
                .teamId(16L)
                .teamTitle("품질관리팀")
                .divisionTitle("개발부")
                .name("김찬빈")
                .totalPoint(70)
                .ranking("C")
                .build(),
            TotalRanking.builder()
                .evaluationTotalId(1L)
                .year("2024")
                .teamId(10L)
                .teamTitle("개발팀")
                .divisionTitle("개발부")
                .name("이승우")
                .totalPoint(60)
                .ranking("D")
                .build(),
            TotalRanking.builder()
                .evaluationTotalId(2L)
                .year("2024")
                .teamId(3L)
                .teamTitle("개발부")
                .name("정준호")
                .totalPoint(127.3)
                .ranking("S")
                .build(),
            TotalRanking.builder()
                .evaluationTotalId(4L)
                .year("2024")
                .teamId(20L)
                .teamTitle("기술부")
                .name("강동우")
                .totalPoint(113.9)
                .ranking("A")
                .build()
        );
    }

    public static List<TotalRanking> createDummyOfficerTotalRankingListForPreview() {
        return Arrays.asList(
            TotalRanking.builder()
                .evaluationTotalId(1L)
                .year("2024")
                .teamId(1L)
                .teamTitle("기술부")
                .name("박상현")
                .totalPoint(99)
                .ranking("B")
                .build()
        );
    }

    public static List<PostTotalRankingRequestDto> createDummyPostTotalRankingRequestDtoList() {
        return Arrays.asList(
            PostTotalRankingRequestDto.builder()
                .teamId(1L)
                .teamTitle("기술부")
                .totalPoint(127.3)
                .year("2024")
                .build(),
            PostTotalRankingRequestDto.builder()
                .teamId(2L)
                .teamTitle("개발부")
                .totalPoint(70)
                .year("2024")
                .build(),
            PostTotalRankingRequestDto.builder()
                .teamId(3L)
                .teamTitle("품질관리팀")
                .totalPoint(60)
                .year("2024")
                .build()
        );
    }

    public static List<Total> createDummyTotalList() {
        return Arrays.asList(
            Total.builder()
                .evaluationTotalId(1L)
                .year("2024")
                .teamId(11L)
                .teamTitle("영업팀")
                .name("박상현")
                .totalPoint(127.3)
                .positionGb("1")
                .build(),
            Total.builder()
                .evaluationTotalId(2L)
                .year("2024")
                .teamId(17L)
                .teamTitle("구매팀")
                .name("이승우")
                .totalPoint(70)
                .positionGb("2")
                .build(),
            Total.builder()
                .evaluationTotalId(3L)
                .year("2024")
                .teamId(18L)
                .teamTitle("물류팀")
                .name("김찬빈")
                .totalPoint(60)
                .positionGb("2")
                .build()
        );
    }

    public static LoginDto createDummyFailLoginDto() {
        return LoginDto.builder()
            .id("chb314")
            .password("failPassword")
            .build();
    }

    public static TaskEvaluation createDummyTaskEvaluation() {
        return TaskEvaluation.builder()
            .taskId(67L)
            .chargeTeam("강동우")
            .chargeOfficer("임성민")
            .chargeTeamId(20L)
            .projectTitle("사내 업무 효율화 프로젝트")
            .taskTitle("오피스 공간 리디자인")
            .taskState("완료")
            .taskProgress(100)
            .responsibility(null)
            .weight(0.0)
            .officerPoint(0)
            .ceoPoint(0)
            .taskGb("")
            .levelOfficer("")
            .levelCeo("")
            .condOfficer("")
            .condCeo("")
            .totalPoint(0.0)
            .note("")
            .state("N")
            .insUser(null)
            .insDate(null)
            .insIp(null)
            .modUser(null)
            .modDate(null)
            .modIp(null)
            .build();
    }

    public static EndYear createDummyEndYear(String year) {
        return EndYear.builder()
            .year(year)
            .insUser("chb314")
            .insDate(LocalDateTime.now())
            .insIp("userIp")
            .build();
    }
}
