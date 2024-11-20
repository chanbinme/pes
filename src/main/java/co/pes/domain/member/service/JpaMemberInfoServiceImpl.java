package co.pes.domain.member.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.common.utils.SHA512Utils;
import co.pes.domain.member.MemberInfoMapper;
import co.pes.domain.member.controller.dto.PasswordModifyRequestDto;
import co.pes.domain.member.entity.UsersEntity;
import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.JpaMemberInfoRepository;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JpaMemberInfoServiceImpl extends AbstractMemberInfoService {

    private final JpaMemberInfoRepository memberInfoRepository;
    private final MemberInfoMapper memberInfoMapper;

    public Users findMember(String userId) {
        return this.findMemberAndCheckMemberExists(userId);
    }

    protected Users findMemberAndCheckMemberExists(String userId) {
        UsersEntity usersEntity = memberInfoRepository.findById(userId).orElseThrow(() -> {
            log.debug("MemberInfoService.findMember exception occur userId: {}", userId);
            return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        });
        return memberInfoMapper.covertToUsers(usersEntity);
    }

    @Transactional
    public void editPassword(PasswordModifyRequestDto passwordModifyRequestDto, String userId, String userIp) throws NoSuchAlgorithmException {
        String newPassword = passwordModifyRequestDto.getNewPassword();
        String encryptedCurrentPassword = SHA512Utils.encrypt(passwordModifyRequestDto.getCurrentPassword());

        if (this.verifyPassword(userId, encryptedCurrentPassword)) {
            String encryptedNewPassword = SHA512Utils.encrypt(newPassword);
            memberInfoRepository.findById(userId).ifPresent(usersEntity -> {
                usersEntity.changePassword(encryptedNewPassword, userIp);
                memberInfoRepository.save(usersEntity);
            });
        } else {
            throw new BusinessLogicException(ExceptionCode.NOT_MATCHED_PASSWORD);
        }
    }

    protected boolean verifyPassword(String userId, String encryptedCurrentPassword) {
        return memberInfoRepository.existsByIdAndPassword(userId, encryptedCurrentPassword);
    }
}
