package co.pes.domain.evaluation.model;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskEvaluation {

    private Long taskId;    // 업무 ID
    private String chargeTeam;  // 담당 Manager
    private String chargeOfficer;   // 담당 임원
    private Long chargeTeamId;  // 담당 팀 ID
    private String projectTitle;    // 프로젝트명
    private String taskTitle;    // 업무명
    private String taskState;    // 업무 상태
    private int taskProgress;    // 진척도
    private String responsibility;  // 담당자
    private double weight;     // 가중치
    private int officerPoint;   // 담당 임원 점수
    private int ceoPoint;   // 대표 조정 점수
    private String taskGb;   // 업무 구분
    private String levelOfficer;    // 담당 임원 난이도
    private String levelCeo;    // 대표 조정 난이도
    private String condOfficer;     // 담당 임원 기여도
    private String condCeo;     // 대표 조정 기여도
    private double totalPoint;     // 최종 점수
    private String note;    // 피드백
    private String state;   // 평가 상태
    private String insUser;
    private LocalDateTime insDate;
    private String insIp;
    private String modUser;
    private LocalDateTime modDate;
    private String modIp;

    public void changeState(String state) {
        this.state = state;
    }
}
