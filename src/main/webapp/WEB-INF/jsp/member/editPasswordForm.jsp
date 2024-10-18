<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <title>인사평가 - 계정 - 비밀번호 변경</title>
    <link rel="stylesheet" href="/css/style.css?v=22">
    <link rel="stylesheet" href="/css/jstree_style.css?v=21"/>
    <script src="/js/jquery-3.7.1.min.js"></script>
    <script src="/js/jstree.js"></script>
    <script src="/js/ui.js"></script>
</head>
<body>
<header class="header">
    <h2 class="header__title">비밀번호 변경</h2>
    <%@ include file="/WEB-INF/jsp/include/top.jsp" %>
</header>
<div class="container account-type">
    <div class="account">
        <div class="table__wrap">
            <div class="table__item">
                <div class="table__info">
                    <strong class="table__title">비밀번호 변경</strong>
                </div>
                <div class="table-cotent">
                    <table class="table table--calendar">
                        <caption>현재 비밀번호 입력 표</caption>
                        <colgroup>
                            <col style="width:30%">
                            <col>
                        </colgroup>
                        <tbody>
                        <tr>
                            <th scope="row">현재 비밀번호</th>
                            <td>
                                <div class="input-box">
                                    <input id="currentPassword" type="password" class="table-input" title="현재 비밀번호">
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">새 비밀번호</th>
                            <td>
                                <div class="input-box" id="newPasswordInput">
                                    <input id="newPassword" type="password" class="table-input" title="새 비밀번호">
                                    <span class="password-alert" id="newPasswordHelpBlock"></span>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th scope="row">새 비밀번호 확인</th>
                            <td>
                                <div class="input-box" id="newPasswordConfirmInput">
                                    <input id="newPasswordConfirm" type="password" class="table-input" title="새 비밀번호 확인">
                                    <span class="password-alert" id="newPasswordConfirmHelpBlock"></span>
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="btn__wrap" id="edit">
                <button class="btn btn--type01" type="button">
                    <span class="btn__text">확인</span>
                </button>
            </div>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {

        $("#edit").on("click", function () {
            edit();
        });

        function edit() {
            const pwdPattern = /^[A-Za-z\d@$!%*#?&]{8,}$/;

            if ($("#newPassword").val() && $("#newPassword").val() != $("#newPasswordConfirm").val()) {
                $("#newPasswordConfirmInput").addClass("alert");
                $("#newPasswordConfirmHelpBlock").text('비밀번호가 일치하지 않습니다.');
                $("#newPassword").focus();
                return false;
            }
            if ($("#newPassword").val() && !pwdPattern.test($("#newPassword").val())) {
                $("#newPasswordInput").addClass("alert");
                $("#newPasswordHelpBlock").text('8자리 이상 영문, 숫자 및 특수문자만 사용 가능합니다.');
                $("#newPassword").focus();
                return false;
            }
            if (!$("#currentPassword").val()) {
                alert("현재 비밀번호를 입력해주세요.");
                $("#currentPassword").focus();
                return false;
            }
            if (!$("#newPassword").val()) {
                alert("새 비밀번호를 입력해주세요.");
                $("#newPassword").focus();
                return false;
            }
            if (!$("#newPasswordConfirm").val()) {
                alert("새 비밀번호 확인을 입력해주세요.");
                $("#newPasswordConfirm").focus();
                return false;
            }

            const param = {
                "currentPassword": $("#currentPassword").val(),
                "newPassword" : $("#newPassword").val()
            };

            $.ajax({
                type: 'PATCH',
                data: JSON.stringify(param),
                dataType: 'text',
                contentType: 'application/json; utf-8',
                url: "/am/member/password",
                success: function (data) {
                    alert(data);
                    location.href = "/am/jobs-evaluation";
                },
                error: function (e) {
                    if (e.status === 403) {
                        location.href = "/am/manager/login";
                    }
                    const error = JSON.parse(e.responseText);
                    alert("[" + error.status + "]\n" + error.message);
                },
            })
        }

        $("#newPassword").on("input", function () {
            validateNewPassword();
        });

        function validateNewPassword() {
            const pattern = /^[A-Za-z\d@$!%*#?&]{8,}$/;
            const newPassword = $("#newPassword").val();

            if (!pattern.test(newPassword)) {
                $("#newPasswordInput").addClass("alert");
                $("#newPasswordHelpBlock").text('8자리 이상 영문, 숫자 및 특수문자만 사용 가능합니다.');
            } else {
                $("#newPasswordInput").removeClass("alert");
                $("#newPasswordHelpBlock").text('');
            }
        }

        $("#newPasswordConfirm").on("input", function () {
            newPasswordConfirm();
        });

        function newPasswordConfirm() {
            const newPasswordConfirm = $("#newPasswordConfirm").val();
            const newPassword = $("#newPassword").val();

            if (newPasswordConfirm === newPassword) {
                $("#newPasswordConfirmInput").removeClass("alert");
                $("#newPasswordConfirmHelpBlock").text('');
            } else {
                $("#newPasswordConfirmInput").addClass("alert");
                $("#newPasswordConfirmHelpBlock").text('불일치');
            }
        }
    });
</script>
</body>
</html>