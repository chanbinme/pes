package co.pes.utils;

import co.pes.domain.evaluation.controller.dto.TaskEvaluationResponseDto;
import co.pes.domain.evaluation.model.TaskEvaluation;
import co.pes.domain.member.model.Users;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

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
}
