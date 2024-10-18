package co.pes.common.interceptor;

import co.pes.common.SessionsUser;
import co.pes.domain.member.model.Users;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI();
        String loginURI = "/am/manager/login";
        String loginProcURI = "/am/manager/loginProc";

        if (requestURI.equals(loginURI) || requestURI.equals(loginProcURI)) {
            return true;
        }

        if (!SessionsUser.isLoginUser(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendRedirect(loginURI);

            return false;
        }

        Users user = SessionsUser.getSessionUser(session);
        if (!user.isOfficer() && !user.isAdminOrCeo()) {
            SessionsUser.removeSessionUser(session);
            response.sendRedirect(loginURI);

            return false;
        }

        return true;
    }
}
