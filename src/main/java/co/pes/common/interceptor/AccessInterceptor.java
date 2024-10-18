package co.pes.common.interceptor;

import co.pes.domain.member.model.Users;
import co.pes.common.SessionsUser;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        Users user = SessionsUser.getSessionUser(request.getSession());
        if (user.isAdminOrCeo()) {
            return true;
        }
        throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
    }
}
