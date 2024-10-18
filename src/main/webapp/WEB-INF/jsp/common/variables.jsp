<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="isOfficer" value="${userInfo.positionGb eq 1}" scope="request"/>
<c:set var="isCeo" value="${userInfo.positionGb eq 0}" scope="request"/>