package co.pes.common.exception;

import lombok.Getter;

/**
 * @author cbkim
 * @PackageName: co.pes.common.exception
 * @FileName : ExceptionCode.java
 * @Date : 2023. 9. 11.
 * @프로그램 설명 : 예외 코드 관리
 */
@Getter
public enum ExceptionCode {
    INVALID_ID_OR_PASSWORD(401, "아이디 또는 비밀번호를 확인하세요."),
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    ACCESS_DENIED(401, "접근 권한이 없습니다."),
    TOTAL_NOT_FOUND(404, "평가 결과가 존재하지 않습니다."),
    FINAL_SAVE_EVALUATION(409, "최종 제출된 평가는 수정 또는 삭제할 수 없습니다."),
    FINISHED_EVALUATION(409, "이미 마감되었습니다."),
    ALREADY_EXISTS_TOTAL(409, "이미 최종 제출되었습니다."),
    INVALID_MAPPING(409, "평가 완료된 팀은 매핑할 수 없습니다."),
    OFFICER_EVALUATION_PERIOD_NOT_FOUND(404, "임원 평가 기간이 설정되어 있지 않습니다."),
    INVALID_EVALUATION_PERIOD(409, "임원 평가 기간이 아닙니다."),
    NOT_FINISHED_EVALUATION(409, "평가 마감된 연도가 아닙니다."),
    NOT_MATCHED_PASSWORD(401, "비밀번호가 일치하지 않습니다."),
    ALREADY_EXISTS_MAPPING(409, "이미 매핑된 업무입니다."),
    INVALID_DATA_LIST(409, "데이터 타입이 올바르지 않습니다."),
    NOT_EXISTS_DATA(409, "다운로드할 데이터가 존재하지 않습니다.");

    private final int status;

    private final String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
