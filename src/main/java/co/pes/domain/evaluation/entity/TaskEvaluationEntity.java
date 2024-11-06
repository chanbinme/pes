package co.pes.domain.evaluation.entity;

import co.pes.common.entity.BaseEntity;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.task.entity.TaskEntity;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@Entity(name = "TASK_EVALUATION")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskEvaluationEntity extends BaseEntity {

    @EmbeddedId
    private TaskEvaluationEntityId id;

    @MapsId("taskId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @MapsId("chargeTeamId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_team_id")
    private OrganizationEntity organization;

    @Column(precision = 10, scale = 2)
    private double weight;  // 가중치

    @Column(precision = 10, scale = 2)
    private int officerPoint; // Officer 평가 점수

    @Column(precision = 10, scale = 2)
    private int ceoPoint;    // CEO 평가 점수

    @Column(length = 10)
    private String taskGb;  // 업무 구분 (0: 일반 / 1: 중요)

    @Column(length = 10)
    private String levelOfficer; // Officer 평가 등급

    @Column(length = 10)
    private String levelCeo;   // CEO 평가 등급

    @Column(length = 10)
    private String condOfficer;  // Officer 평가 기여도

    @Column(length = 10)
    private String condCeo; // CEO 평가 기여도

    @Column(precision = 10, scale = 2)
    private double totalPoint; // 총점

    @Column(length = 500)
    private String note; // 비고

    @Column(length = 1)
    @ColumnDefault("N")
    private String state; // 상태 (N: 임시저장 / F: 최종제출)

    public void changeState(String state) {
        this.state = state;
    }
}
