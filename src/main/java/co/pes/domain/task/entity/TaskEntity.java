package co.pes.domain.task.entity;

import static javax.persistence.GenerationType.IDENTITY;

import co.pes.common.entity.BaseEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "TASK")
public class TaskEntity extends BaseEntity {

    // 아이디
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // 년도
    @Column(length = 4, nullable = false)
    @Size(min = 4, max = 4)
    private String year;

    // 프로젝트명
    @Column(length = 50)
    @Size(min = 1, max = 50)
    private String projectTitle;

    // 업무명
    @Column(length = 300, nullable = false)
    @Size(min = 1, max = 300)
    private String taskTitle;

    // 업무상태
    @Column(length = 10)
    @Size(min = 1, max = 10)
    private String taskState;

    // 진척도
    @Column(length = 10)
    @NotNull
    private int taskProgress;
}
