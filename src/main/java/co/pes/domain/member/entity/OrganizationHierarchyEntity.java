package co.pes.domain.member.entity;

import co.pes.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
import lombok.experimental.SuperBuilder;

@Getter
@Builder
@EqualsAndHashCode
@ToString(exclude = {"ancestorOrganization", "descendantOrganization"})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "ORGANIZATION_HIERARCHY")
public class OrganizationHierarchyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ancestor_organization_id")
    private OrganizationEntity ancestorOrganization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "descendant_organization_id")
    private OrganizationEntity descendantOrganization;

    @Column(nullable = false)
    private int depth;

    private int sort;
}
