package co.pes.domain.member.service.dto;

import co.pes.common.pagination.Paging;
import co.pes.domain.member.model.Users;
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
public class MemberInfoListPaginationDto {

    private List<Users> usersList;
    private Paging paging;
}
