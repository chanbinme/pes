package co.pes.domain.organizationchart.model;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrganizationChart {

    private String id;
    private String parent;
    private String text;
    private String icon;

    @QueryProjection
    public OrganizationChart(String id, String parent, String text) {
        this.id = id;
        this.parent = parent;
        this.text = text;
    }
}
