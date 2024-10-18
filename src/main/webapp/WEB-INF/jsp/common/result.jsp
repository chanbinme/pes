<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<script src="https://code.jquery.com/jquery-3.4.1.js"></script>
<script>
    const message = "<c:out value="${message}" />";
    if (message && message.length > 0) {
        alert(message);
    }
    location.href = "${returnUrl}";
</script>