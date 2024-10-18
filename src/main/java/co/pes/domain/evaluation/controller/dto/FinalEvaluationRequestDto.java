package co.pes.domain.evaluation.controller.dto;

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
public class FinalEvaluationRequestDto {

    private List<JobEvaluationRequestDto> jobEvaluationRequestDtoList;
    private TotalRequestDto totalRequestDto;

}
