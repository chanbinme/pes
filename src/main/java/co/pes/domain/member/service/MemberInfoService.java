package co.pes.domain.member.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.common.utils.SHA512Utils;
import co.pes.domain.member.controller.dto.PasswordModifyRequestDto;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.MybatisMemberInfoRepository;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface MemberInfoService {

    /**
     * 회원정보 조회
     *
     * @param userId 사용자 ID
     * @return 회원정보
     */
    Users findMember(String userId);

    /**
     * 비밀번호 변경
     *
     * @param passwordModifyRequestDto 비밀번호 변경 요청 DTO
     * @param userId 사용자 ID
     * @param userIp 사용자 IP
     * @throws BusinessLogicException 비밀번호 인증 실패 시 예외 발생
     */
    void editPassword(PasswordModifyRequestDto passwordModifyRequestDto, String userId, String userIp)
        throws NoSuchAlgorithmException;
}
