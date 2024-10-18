package co.pes.common.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        // Request를 복제하여 저장
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
        log.debug("Original Request Wrapper: {}", wrapper.getRequest().getClass().getSuperclass().getName());

        String requestURI = request.getRequestURI();
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.debug("::: [" + requestURI + "] Start :::");

        logRequestParamters(wrapper);
        logRequestBody(wrapper);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        log.debug("::: [" + requestURI + "] End :::");
        log.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    private static void logRequestParamters(ContentCachingRequestWrapper wrapper) {
        log.debug("Parameter {");
        try {
            Map<String, String[]> parameterMap = wrapper.getParameterMap();

            if (!parameterMap.isEmpty()) {
                Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();

                for (Entry<String, String[]> entry : entrySet) {
                    String paramName = entry.getKey();
                    String[] paramValues = entry.getValue();

                    StringBuilder paramValueString = new StringBuilder();
                    for (String paramValue : paramValues) {
                        paramValueString.append(paramValue).append(", ");
                    }

                    if (paramValueString.length() > 0) {
                        paramValueString.setLength(paramValueString.length() - 2);
                    }

                    log.debug("{} : {}", paramName, paramValueString);
                }
            }
        } catch (Exception e) {
            log.error("" + e.fillInStackTrace());
        } finally {
            log.debug("}");
        }
    }

    private void logRequestBody(ContentCachingRequestWrapper wrapper) throws IOException {
        Map<String, Object> requestBody = null;

        if (hasRequestBody(wrapper)) {
            requestBody = getRequestBody(wrapper);
        }

        log.debug("body {");
        try {
            if (requestBody != null && !requestBody.isEmpty()) {
                Set<Entry<String, Object>> entrySet = requestBody.entrySet();

                for (Entry<String, Object> entry : entrySet) {
                    String name = entry.getKey();
                    String value = (String) entry.getValue();

                    StringBuilder valueString = new StringBuilder();

                    if (isNotPassword(name)) {
                        valueString.append(value);
                        log.debug("{} : {}", name, valueString);
                    }
                }
            }
        } catch (Exception e) {
            log.error("" + e.fillInStackTrace());
        } finally {
            log.debug("}");
        }
    }

    private static boolean isNotPassword(String name) {
        return !name.toLowerCase().contains("password") && !name.toLowerCase().contains("pwd");
    }

    private boolean hasRequestBody(ContentCachingRequestWrapper wrapper) throws IOException {
        return wrapper.getInputStream().available() > 0;
    }

    private Map<String, Object> getRequestBody(ContentCachingRequestWrapper wrapper) {
        byte[] buf = wrapper.getContentAsByteArray();
        String requestBodyString = "";

        if (buf.length > 0) {
            requestBodyString = new String(buf, StandardCharsets.UTF_8);
        }

        // JSON 문자열을 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(requestBodyString, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            log.error("Failed to parse JSON request body", e);
            return Collections.emptyMap();
        }
    }
}