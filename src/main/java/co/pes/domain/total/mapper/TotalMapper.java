package co.pes.domain.total.mapper;

import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.member.entity.OrganizationEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.entity.EvaluationTotalEntity;
import co.pes.domain.total.model.Total;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TotalMapper {

    public Total dtoToTeamTotal(TotalRequestDto totalRequestDto, Users user,
        String userIp) {
        return Total.builder()
            .name(totalRequestDto.getTeamName())
            .teamId(totalRequestDto.getTeamId())
            .teamTitle(totalRequestDto.getTeamTitle())
            .year(totalRequestDto.getYear())
            .positionGb("0")
            .totalPoint(totalRequestDto.getTotalPoint())
            .insUser(user.getName())
            .insDate(LocalDateTime.now())
            .insIp(userIp)
            .modUser(user.getName())
            .modDate(LocalDateTime.now())
            .modIp(userIp)
            .build();
    }

    public EvaluationTotalEntity dtoToTeamEvaluationTotalEntity(TotalRequestDto totalRequestDto, Users user,
        String userIp, OrganizationEntity organization) {
        return EvaluationTotalEntity.builder()
            .teamTitle(totalRequestDto.getTeamTitle())
            .organization(organization)
            .year(totalRequestDto.getYear())
            .totalPoint(totalRequestDto.getTotalPoint())
            .insUser(user.getName())
            .insIp(userIp)
            .modUser(user.getName())
            .modIp(userIp)
            .build();
    }

    public Total dtoToOfficerTotal(TotalRequestDto totalRequestDto, Users user, String userIp,
        Long teamId, String teamTitle) {
        return Total.builder()
            .name(totalRequestDto.getOfficerName())
            .year(totalRequestDto.getYear())
            .teamId(teamId)
            .teamTitle(teamTitle)
            .note(totalRequestDto.getNote())
            .positionGb("1")    // Officer
            .insUser(user.getName())
            .insDate(LocalDateTime.now())
            .insIp(userIp)
            .modUser(user.getName())
            .modDate(LocalDateTime.now())
            .modIp(userIp)
            .build();
    }

    public List<Total> postDtoListToTotalList(
        List<PostTotalRankingRequestDto> dtoList, Users user, String userIp) {
        List<Total> totalRankingList = new ArrayList<>();

        if (!dtoList.isEmpty()) {
            String userName = user.getName();
            for (PostTotalRankingRequestDto dto : dtoList) {
                totalRankingList.add(postDtoToTotal(dto, userName, userIp));
            }
        }

        return totalRankingList;
    }

    private Total postDtoToTotal(PostTotalRankingRequestDto dto, String userName,
        String userIp) {
        return Total.builder()
            .evaluationTotalId(dto.getEvaluationTotalId())
            .year(dto.getYear())
            .positionGb(dto.getPositionGb())
            .teamId(dto.getTeamId())
            .teamTitle(dto.getTeamTitle())
            .name(dto.getName())
            .ranking(dto.getRanking())
            .note(dto.getNote())
            .totalPoint(dto.getTotalPoint())
            .insUser(userName)
            .insDate(LocalDateTime.now())
            .insIp(userIp)
            .modUser(userName)
            .modDate(LocalDateTime.now())
            .modIp(userIp)
            .build();
    }

    public EvaluationTotalEntity postDtoToEvaluationTotalEntity(PostTotalRankingRequestDto dto, OrganizationEntity organization, String userName, String userIp) {
        return EvaluationTotalEntity.builder()
            .id(dto.getEvaluationTotalId())
            .organization(organization)
            .year(dto.getYear())
            .teamTitle(dto.getTeamTitle())
            .ranking(dto.getRanking())
            .note(dto.getNote())
            .totalPoint(dto.getTotalPoint())
            .insUser(userName)
            .insIp(userIp)
            .modUser(userName)
            .modIp(userIp)
            .build();
    }

    public EvaluationTotalEntity dtoToOfficerEvaluationTotalEntity(TotalRequestDto totalRequestDto,
        Users user, String userIp, OrganizationEntity organization) {
        return EvaluationTotalEntity.builder()
            .year(totalRequestDto.getYear())
            .organization(organization)
            .teamTitle(organization.getTitle())
            .note(totalRequestDto.getNote())
            .insUser(user.getName())
            .insDate(LocalDateTime.now())
            .insIp(userIp)
            .modUser(user.getName())
            .modDate(LocalDateTime.now())
            .modIp(userIp)
            .build();
    }
}
