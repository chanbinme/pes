package co.pes.domain.task.model;

import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.member.model.Users;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapping {

    private Long chargeTeamId;   // 담당 팀 ID
    private Long taskId;    // 업무 ID
    private String chargeTeam;  // 담당 Manager
    private String chargeOfficer;   // 담당 임원
    private String insUser;
    private LocalDateTime insDate;
    private String insIp;

    public void designateChargePerson(String chargeTeam, String chargeOfficer) {
        this.chargeTeam = chargeTeam;
        this.chargeOfficer = chargeOfficer;
    }

    @Builder
    public Mapping(MappingDto mappingDto, Users user, String userIp) {
        this.chargeTeamId = mappingDto.getChargeTeamId();
        this.taskId = mappingDto.getTaskId();
        this.insUser = user.getId();
        this.insDate = LocalDateTime.now();
        this.insIp = user.getInsIp();
    }
}
