package co.pes.domain.task.model;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tasks {

    private Long id;
    private String year;    // 년도
    private String projectTitle;    // 프로젝트명
    private String taskTitle;    // 업무명
    private String taskState;   // 업무상태
    private int taskProgress;    // 진척도
    private String responsibility;    // 담당자
    private String topTask;      // 상위업무
    private List<Long> chargeTeamIds;   // 담당 팀 ID 리스트
    private List<String> chargeTeamTitles;   // 담당 팀 ID 리스트

    @QueryProjection
    public Tasks(Long id, String year, String taskTitle, String taskState, int taskProgress) {
        this.id = id;
        this.year = year;
        this.taskTitle = taskTitle;
        this.taskState = taskState;
        this.taskProgress = taskProgress;
    }

    public void addChargeTeamIds(List<Long> chargeTeamIds) {
        this.chargeTeamIds = chargeTeamIds;
    }

    public void addChargeTeamTitles(List<String> chargeTeamTitles) {
        this.chargeTeamTitles = chargeTeamTitles;
    }
}
