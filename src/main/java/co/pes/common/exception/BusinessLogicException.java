package co.pes.common.exception;

import lombok.Getter;

/**
 * @author cbkim
 * @PackageName: co.pes.common.exception
 * @FileName : BusinessLogicException.java
 * @Date : 2023. 9. 11.
 * @프로그램 설명 : ExceptionCode를 받아 예외 정보를 제공
 */
@Getter
public class BusinessLogicException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public BusinessLogicException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
