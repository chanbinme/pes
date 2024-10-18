<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/variables.jsp"%>
<nav class="gnb">
    <ul class="gnb__list">
        <c:if test="${isCeo}">
            <li class="gnb__item">
                <a href="${pageContext.request.contextPath}/am/jobs-manager" class="gnb__link">팀 매핑</a>
            </li>
        </c:if>
        <li class="gnb__item">
            <a href="${pageContext.request.contextPath}/am/jobs-evaluation" class="gnb__link">팀 평가</a>
        </li>
        <c:if test="${isCeo}">
            <li class="gnb__item">
                <a href="${pageContext.request.contextPath}/am/totals/ranking" class="gnb__link">랭킹</a>
            </li>
        </c:if>
        <c:if test="${isCeo}">
            <li class="gnb__item">
                <a href="${pageContext.request.contextPath}/am/admin" class="gnb__link">관리</a>
            </li>
        </c:if>
        <li class="gnb__item">
            <a href="#none" class="gnb__link gnb__link--account" data-link="account" id="accountMenuButton">
                <span class="blind">계정</span>
            </a>
            <div class="gnb__depth" data-depth="account">
                <ul class="gnb__depth--list">
                    <li class="gnb__depth--item">
                        <a href="${pageContext.request.contextPath}/am/member/edit-password" class="gnb__depth-link gnb__depth-link--password">비밀번호 변경</a>
                    </li>
                    <li class="gnb__depth--item">
                        <a href="${pageContext.request.contextPath}/am/manager/logout" class="gnb__depth-link gnb__depth-link--logout">로그아웃</a>
                    </li>
                </ul>
            </div>
        </li>
    </ul>
</nav>