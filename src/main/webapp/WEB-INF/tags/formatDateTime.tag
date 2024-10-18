<!-- LocalDateTime JSP에서 출력하기 위한 태그 파일 -->
<%@ tag body-content="empty" pageEncoding="utf-8" %>
<%@ tag import="java.time.format.DateTimeFormatter" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="value" required="true"
              type="java.time.temporal.TemporalAccessor" %>
<%@ attribute name="pattern" type="java.lang.String" %>
<%
    if (pattern == null) {
        pattern = "yyyy-MM-dd HH:mm:ss";
    }
%>
<%= DateTimeFormatter.ofPattern(pattern).format(value) %>