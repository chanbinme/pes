package co.pes.domain.evaluation.controller.dto;

import co.pes.domain.evaluation.model.TaskEvaluation;

import java.util.List;
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
public class TaskEvaluationResponseDto {

    private boolean existsTotal;
    private List<TaskEvaluation> taskEvaluationList;

}