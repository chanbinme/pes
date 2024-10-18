package co.pes.common;

import co.pes.domain.member.model.Users;

import javax.servlet.http.HttpSession;

public class SessionsUser {

    /**
     * <pre>
     * 1. MethodName : setSessionUser
     * 2. ClassName  : SessionUser.java
     * 3. Comment    : 관리자 정보를 세션에 저장
     * 4. 작성자       : cbkim
     * 5. 작성일       : 2023. 9. 7.
     * </pre>
     */
    public static void setSessionUser(HttpSession session, Users users) {
        session.setAttribute("admSessionInfo", users);
    }

    /**
     * <pre>
     * 1. MethodName : getSessionUser
     * 2. ClassName  : SessionUser.java
     * 3. Comment    : 관리자 정보를 세션에서 조회
     * 4. 작성자       : cbkim
     * 5. 작성일       : 2023. 9. 7.
     * </pre>
     */
    public static Users getSessionUser(HttpSession session) {
        return (Users) session.getAttribute("admSessionInfo");
    }

    /**
     * <pre>
     * 1. MethodName : isLoginUser
     * 2. ClassName  : SessionUser.java
     * 3. Comment    : 로그인 유무 확인
     * 4. 작성자       : cbkim
     * 5. 작성일       : 2023. 9. 7.
     * </pre>
     */
    public static boolean isLoginUser(HttpSession session) {
        return getSessionUser(session) != null;
    }

    /**
     * <pre>
     * 1. MethodName : removeSessionUser
     * 2. ClassName  : SessionUser.java
     * 3. Comment    : 세션에서 회원 정보 삭제 (로그아웃)
     * 4. 작성자       : cbkim
     * 5. 작성일       : 2023. 10. 4.
     * </pre>
     */
    public static void removeSessionUser(HttpSession session) {
        session.removeAttribute("admSessionInfo");
    }
}
