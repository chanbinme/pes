package co.pes.domain.member.service;

import co.pes.domain.member.controller.dto.MemberInfoDeleteRequestDto;
import co.pes.domain.member.controller.dto.MemberInfoModifyRequestDto;
import co.pes.domain.member.controller.dto.MemberJoinRequestDto;
import co.pes.domain.member.controller.dto.PasswordModifyRequestDto;
import co.pes.domain.member.repository.MemberInfoRepository;
import co.pes.domain.member.service.dto.MemberInfoListPaginationDto;
import co.pes.domain.member.service.dto.MemberInfoModifyDto;
import co.pes.domain.member.service.dto.MemberJoinDto;
import co.pes.common.utils.SHA512Utils;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.common.pagination.Paging;
import co.pes.domain.member.mapper.MemberInfoMapper;
import co.pes.domain.member.model.Users;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInfoService {

    private final MemberInfoRepository memberInfoRepository;
    private final MemberInfoMapper memberInfoMapper;

    /**
     * 회원가입
     *
     * @param requestDto 회원가입 요청 DTO
     * @param userIp 사용자 IP
     * @throws Exception 회원가입 실패 시 예외 발생
     */
    @Transactional
    public void join(MemberJoinRequestDto requestDto, String userIp)
        throws Exception {
        MemberJoinDto memberJoinDto =
            memberInfoMapper.requestDtoToMemberJoinDto(requestDto, userIp);
        memberInfoRepository.memberJoin(memberJoinDto);
    }

    /**
     * 중복 회원 확인
     *
     * @param userId 사용자 ID
     * @return 중복 회원 여부
     */
    public boolean checkDuplicatedMember(String userId) {
        Optional<Users> member =
            Optional.ofNullable(memberInfoRepository.findById(userId));
        if (member.isPresent()) {
            log.debug("MemberInfoService.checkDuplicatedMember exception occur userId: {}", userId);
            log.info("이미 존재하는 회원입니다.");

            return true;
        }

        return false;
    }

    /**
     * 회원정보 수정
     *
     * @param requestDto 회원정보 수정 요청 DTO
     * @param userId 사용자 ID
     * @param userIp 사용자 IP
     * @throws NoSuchAlgorithmException
     */
    @Transactional
    public void editMemberInfo(MemberInfoModifyRequestDto requestDto, String userId, String userIp)
        throws NoSuchAlgorithmException {
        this.findMemberAndCheckMemberExists(userId);
        MemberInfoModifyDto memberInfoModifyDto =
            memberInfoMapper.requestDtoToMemberInfoModifyDto(requestDto, userId, userIp);
        memberInfoRepository.editMemberInfo(memberInfoModifyDto);
    }

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
     * 회원정보 논리적 삭제
     *
     * @param requestDtos 회원정보 삭제 요청 DTO
     * @param userIp 사용자 IP
     */
    @Transactional
    public void softDeleteMember(List<MemberInfoDeleteRequestDto> requestDtos, String userIp) {
        for (MemberInfoDeleteRequestDto requestDto : requestDtos) {
            String userId = requestDto.getId();
            this.findMemberAndCheckMemberExists(userId);
            memberInfoRepository.softDeleteById(userId, userIp);
        }
    }

    /**
     * 회원정보 목록 조회
     *
     * @param pageNum 페이지 번호
     * @param pageSize 페이지 크기
     * @return 회원정보 목록
     */
    public MemberInfoListPaginationDto findAll(int pageNum, int pageSize) {
        int totalRecordCount = memberInfoRepository.findAllCount();
        Paging paging = Paging.builder()
            .totalRecordSize(totalRecordCount)
            .pageNum(pageNum)
            .pageSize(pageSize)
            .build();
        List<Users> usersList = memberInfoRepository.findAll(paging.getStartNum(), paging.getEndNum());

        return MemberInfoListPaginationDto.builder()
            .usersList(usersList)
            .paging(paging)
            .build();
    }

    /**
     * 회원정보 조회 및 존재 여부 검증
     *
     * @param userId 사용자 ID
     * @return 회원정보
     */
    private Users findMemberAndCheckMemberExists(String userId) {
        return Optional.ofNullable(memberInfoRepository.findById(userId))
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
    public void editPassword(PasswordModifyRequestDto passwordModifyRequestDto, String userId, String userIp)
        throws NoSuchAlgorithmException {
        String newPassword = passwordModifyRequestDto.getNewPassword();
        String encryptedCurrentPassword = SHA512Utils.encrypt(passwordModifyRequestDto.getCurrentPassword());

        if (this.verifyPassword(userId, encryptedCurrentPassword)) {
            String encryptedPassword = SHA512Utils.encrypt(newPassword);
            memberInfoRepository.editPassword(encryptedPassword, userId, userIp);
        } else {
            throw new BusinessLogicException(ExceptionCode.NOT_MATCHED_PASSWORD);
        }
    }

    private boolean verifyPassword(String userId, String encryptedCurrentPassword) {
        return memberInfoRepository.verifyPassword(userId, encryptedCurrentPassword) == 1;
    }
}
