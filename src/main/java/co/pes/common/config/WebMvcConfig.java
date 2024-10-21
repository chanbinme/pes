package co.pes.common.config;

import co.pes.common.interceptor.AccessInterceptor;
import co.pes.common.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final AccessInterceptor accessInterceptor;

    /**
     * 로그인 인증 Interceptor 설정
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
            .order(0)
            .addPathPatterns("/am/tasks/**", "/am/tasks-manager/**", "/am/tasks-evaluation/**", "/am/totals/**", "/am/admin/**", "/am/member/**")
            .excludePathPatterns("/am/manager/login", "/am/manager/loginProc");

        registry.addInterceptor(accessInterceptor)
            .order(1)
            .addPathPatterns("/am/tasks/**", "/am/tasks-manager/**", "/am/totals/**", "/am/admin/**");

    }
}
