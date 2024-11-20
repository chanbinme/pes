package co.pes.domain.member.service;

import co.pes.domain.member.model.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public abstract class AbstractMemberInfoService implements MemberInfoService {

    /**
     * 회원정보 조회 및 존재 여부 검증
     *
     * @param userId 사용자 ID
     * @return 회원정보
     */
    protected abstract Users findMemberAndCheckMemberExists(String userId);

    protected abstract boolean verifyPassword(String userId, String encryptedCurrentPassword);
}
