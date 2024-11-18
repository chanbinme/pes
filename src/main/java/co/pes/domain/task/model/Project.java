package co.pes.domain.task.model;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Project {

    private String projectTitle;   // 프로젝트명

    @QueryProjection
    public Project(String projectTitle) {
        this.projectTitle = projectTitle;
    }
}
