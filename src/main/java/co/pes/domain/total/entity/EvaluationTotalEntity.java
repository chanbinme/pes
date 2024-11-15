package co.pes.domain.total.entity;

import co.pes.common.entity.BaseEntity;
import co.pes.domain.member.entity.OrganizationEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"organization"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "EVALUATION_TOTAL")
public class EvaluationTotalEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private OrganizationEntity organization;

    @Column(length = 4)
    @Size(min = 4, max = 4)
    private String year;

    @Column(precision = 10, scale = 2)
    private double totalPoint;

    @Column(length = 1)
    private String ranking;

    @Column(length = 50)
    private String teamTitle;

    @Column(length = 500)
    private String note;

    public void changeTotalPoint(double officerTotalPoint) {
        this.totalPoint = officerTotalPoint;
    }
}
