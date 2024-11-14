package co.pes.domain.total.model;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OfficerTeamInfo {
    private Long teamId;
    private String teamTitle;

    @QueryProjection
    public OfficerTeamInfo(Long teamId, String teamTitle) {
        this.teamId = teamId;
        this.teamTitle = teamTitle;
    }
}
