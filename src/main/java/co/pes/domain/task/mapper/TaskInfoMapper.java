package co.pes.domain.task.mapper;

import co.pes.domain.task.controller.dto.MappingDto;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.member.model.Users;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TaskInfoMapper {

    private Mapping mappingDtoToMapping(MappingDto mappingDto) {
        return Mapping.builder().mappingDto(mappingDto).build();
    }

    private Mapping mappingDtoToMapping(MappingDto mappingDto, Users user, String userIp) {
        return Mapping.builder().mappingDto(mappingDto).user(user).userIp(userIp).build();
    }

    public List<Mapping> mappingDtoListToMappingList(List<MappingDto> mappingDtos) {
        ArrayList<Mapping> mappingInfoList = new ArrayList<>();

        for (MappingDto mappingDto : mappingDtos) {
            Mapping mappingInfo = this.mappingDtoToMapping(mappingDto);
            mappingInfoList.add(mappingInfo);
        }

        return mappingInfoList;
    }

    public List<Mapping> mappingDtoListToMappingList(List<MappingDto> mappingDtos, Users user, String userIp) {
        ArrayList<Mapping> mappingInfoList = new ArrayList<>();

        for (MappingDto mappingDto : mappingDtos) {
            Mapping mappingInfo = this.mappingDtoToMapping(mappingDto, user, userIp);
            mappingInfoList.add(mappingInfo);
        }

        return mappingInfoList;
    }
}
