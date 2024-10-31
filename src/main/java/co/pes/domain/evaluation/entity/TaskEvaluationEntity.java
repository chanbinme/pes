package co.pes.domain.evaluation.entity;

import static javax.persistence.GenerationType.IDENTITY;

import co.pes.common.entity.BaseEntity;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.task.entity.TaskEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"task", "organization"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "TASK_EVALUATION")
public class TaskEvaluationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_team_id")
    private OrganizationEntity organization;

    @Column(precision = 10, scale = 2)
    private double weight;  // 가중치

    @Column(precision = 10, scale = 2)
    private double officerPoint; // Officer 평가 점수

    @Column(precision = 10, scale = 2)
    private double ceoPoint;    // CEO 평가 점수

    @Column(length = 10)
    private String jobGb;  // 업무 구분 (0: 일반 / 1: 중요)

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
}
