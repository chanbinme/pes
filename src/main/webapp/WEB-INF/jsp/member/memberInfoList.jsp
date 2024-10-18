<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>회원 리스트 관리</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65"
          crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <script src="https://code.jquery.com/jquery-3.4.1.js"></script>
</head>
<body>
<div class="container">
    <div class="input-form-background row">
        <div class="input-form input-form01 col-md-12 mx-auto">
            <h4 class="mb-3 main-title">회원 리스트 관리</h4>
            <div class="row">
                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                    <button class="btn btn-primary me-md-2" id="goForm" type="button">등록</button>
                    <button class="btn btn-primary" id="goDelete" type="button">삭제</button>
                </div>
                <div class="mb-3"></div>
            </div>
            <form id="listFrm">
                <table class="table table-striped">
                    <thead class="table-dark">
                    <tr>
                        <th scope="col"><label>
                            <input type="checkbox" class="checkbox checkAll" name="allCheck"/>
                        </label></th>
                        <th scope="col">NO</th>
                        <th scope="col">아이디</th>
                        <th scope="col">이름</th>
                        <th scope="col">등록일</th>
                    </tr>
                    </thead>
                    <tbody class="table-group-divider">
                    <c:choose>
                        <c:when test="${not empty memberInfoList}">
                            <c:forEach var="memberInfo" items="${memberInfoList}" varStatus="i">
                                <tr>
                                    <td><input type="checkbox" class="checkbox checkRow"
                                               id="USER_ID_{memberInfo.userId}" name="USER_IDs"
                                               value="${memberInfo.id}"></td>
                                    <td><c:out value="${memberInfo.rowNum}"/></td>
                                    <td><a href="/am/member/${memberInfo.id}"><c:out
                                            value="${memberInfo.id}"/></a></td>
                                    <td><c:out value="${memberInfo.name}"/></td>
                                    <td><tf:formatDateTime value="${memberInfo.insDate}"
                                                           pattern="yyyy-MM-dd"/></td>
                                </tr>
                            </c:forEach>
                        </c:when>
                    </c:choose>
                    </tbody>
                </table>
            </form>
            <form id="frm" name="frm">
                <div class="page" style="float:right;">
                    총 <strong class="em"><c:out value="${paging.totalRecordCount}"/> </strong>건,
                    <c:out value="${paging.pageNum}"/> of <c:out value="${paging.totalPageCount}"/>
                    page
                </div>
                <div id="sort" style="float:left;">
                    <select class="form-select" name="pageSize" aria-label="Default select example">
                        <option value="3" <c:if test="${paging.pageSize eq 3}">selected</c:if>>
                            3개씩 보기
                        </option>
                        <option value="5" <c:if test="${paging.pageSize eq 5}">selected</c:if>>
                            5개씩 보기
                        </option>
                        <option value="10" <c:if test="${paging.pageSize eq 10}">selected</c:if>>
                            10개씩 보기
                        </option>
                        <option value="20" <c:if test="${paging.pageSize eq 20}">selected</c:if>>
                            20개씩 보기
                        </option>
                        <option value="30" <c:if test="${paging.pageSize eq 30}">selected</c:if>>
                            30개씩 보기
                        </option>
                    </select>
                </div>
            </form>
            <nav aria-label="pagination" style="clear: both">
                <ul class="pagination justify-content-center">
                    <li class="page-item">
                        <a class="page-link" href="${pageContext.request.contextPath}/am/member?pageSize=${paging.pageSize}"
                           aria-label="Previous">
                            <span aria-hidden="true">&laquo;</span>
                        </a>
                    </li>
                    <c:forEach var="i" begin="1" end="${paging.totalPageCount}">
                        <li class="page-item <c:if test="${i eq paging.pageNum}">active</c:if>"><a
                                class="page-link"
                                href="${pageContext.request.contextPath}/am/member?pageNum=${i}&pageSize=${paging.pageSize}">${i}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item">
                        <a class="page-link"
                           href="${pageContext.request.contextPath}/am/member?pageNum=${paging.totalPageCount}&pageSize=${paging.pageSize}"
                           aria-label="Next">
                            <span aria-hidden="true">&raquo;</span>
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/include/top.jsp" %>
</div>
<script>
    $(document).ready(function () {
        // 등록 버튼 클릭 이벤트
        $("#goForm").on("click", function () {
            location.href = "/am/member/joinForm"
        });

        $("#goDelete").on("click", function () {
            goDelete();
        });

        $("#accountMenuButton").on("click", function () {
            if ($("#gnbDepth").hasClass("is-active")) {
                $("#gnbDepth").removeClass("is-active");
            } else {
                $("#gnbDepth").addClass("is-active");
            }
        });

        function goDelete() {

            const arr = [];

            $("input[name='USER_IDs']:checked").each(function () {
                const userId = $(this).val();
                arr.push(userId);
            });

            if (arr.length === 0) {
                alert("선택된 회원이 없습니다.");
                return;
            }

            $.ajax({
                type: 'PATCH',
                data: JSON.stringify(arr),
                dataType: 'text',
                contentType: "application/json; utf-8",
                url: "/am/member/delete",
                success: function (data) {
                    alert(data);
                    location.reload();
                },
                error: function (e) {
                    alert("[" + e.status + "]\n" + e.message);
                    if (e.status === 403) {
                        location.href = "/am/manager/login";
                    }
                },
            });
        }

        $(".checkAll").change(function () {
            allCheck();
        });

        function allCheck() {
            if ($('.checkAll').is(':checked')) {
                $('.checkRow').prop('checked', true);
            } else {
                $('.checkRow').prop('checked', false);
            }
        }
    });

    // pageSize select change 이벤트
    $("select[name='pageSize']").on("change", function () {
        goPage();
    });

    function goPage() {
        $("#frm").attr({"action": "/am/member", "target": "_self", "method": "GET"}).submit();
    }
</script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
</body>
</html>