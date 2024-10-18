package co.pes.domain.manager.mapper;

import co.pes.domain.manager.controller.dto.LoginRequestDto;
import co.pes.domain.manager.service.dto.LoginDto;
import co.pes.common.utils.SHA512Utils;

import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Component;

@Component
public class LoginManagerMapper {

    public LoginDto requestDtoToLoginDto(LoginRequestDto requestDto)
        throws NoSuchAlgorithmException {
        return LoginDto.builder()
            .id(requestDto.getId())
            .password(SHA512Utils.encrypt(requestDto.getPassword()))
            .build();
    }

}
