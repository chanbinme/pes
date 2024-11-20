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

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MybatisMemberInfoServiceImpl extends AbstractMemberInfoService {

    private final MybatisMemberInfoRepository mybatisMemberInfoRepository;

    /**
     * 회원정보 조회
     *
     * @param userId 사용자 ID
     * @return 회원정보
     */
    public Users findMember(String userId) {
        return this.findMemberAndCheckMemberExists(userId);
    }

    /**
     * 회원정보 조회 및 존재 여부 검증
     *
     * @param userId 사용자 ID
     * @return 회원정보
     */
    protected Users findMemberAndCheckMemberExists(String userId) {
        return Optional.ofNullable(mybatisMemberInfoRepository.findById(userId))
            .orElseThrow(() -> {
                log.debug("MemberInfoService.findMember exception occur userId: {}", userId);
                return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        });
    }

    /**
     * 비밀번호 변경
     *
     * @param passwordModifyRequestDto 비밀번호 변경 요청 DTO
     * @param userId 사용자 ID
     * @param userIp 사용자 IP
     * @throws BusinessLogicException 비밀번호 인증 실패 시 예외 발생
     */
    @Transactional
    public void editPassword(PasswordModifyRequestDto passwordModifyRequestDto, String userId, String userIp) throws NoSuchAlgorithmException {
        String newPassword = passwordModifyRequestDto.getNewPassword();
        String encryptedCurrentPassword = SHA512Utils.encrypt(passwordModifyRequestDto.getCurrentPassword());

        if (this.verifyPassword(userId, encryptedCurrentPassword)) {
            String encryptedPassword = SHA512Utils.encrypt(newPassword);
            mybatisMemberInfoRepository.editPassword(encryptedPassword, userId, userIp);
        } else {
            throw new BusinessLogicException(ExceptionCode.NOT_MATCHED_PASSWORD);
        }
    }

    protected boolean verifyPassword(String userId, String encryptedCurrentPassword) {
        return mybatisMemberInfoRepository.verifyPassword(userId, encryptedCurrentPassword) == 1;
    }
}
