package co.pes.domain.evaluation.entity;

import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.task.entity.TaskEntity;
import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class TaskEvaluationEntityId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_team_id")
    private OrganizationEntity organization;

}
