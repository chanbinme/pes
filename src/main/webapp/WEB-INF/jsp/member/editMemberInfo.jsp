<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>회원 정보 수정</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <script src="https://code.jquery.com/jquery-3.4.1.js"></script>
</head>
<body>
<div class="container">
    <div class="input-form-background row">
        <div class="input-form input-form02 col-md-12 mx-auto">
            <h4 class="mb-3 main-title">회원 정보 수정</h4>
            <form class="validation-form novalidate">
                <div class="row">
                    <div class="mb-3">
                        <label for="userId" class="form-label">아이디</label>
                        <div class="input-group">
                            <input type="text" class="form-control" id="userId" value="${memberInfo.userId}" disabled>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="userPassword" class="col-sm-2 col-form-label">비밀번호</label>
                        <input type="password" class="form-control" id="userPassword" placeholder="비밀번호를 입력해주세요.">
                        <div id="passwordHelpBlock" class="form-text"></div>
                    </div>
                    <div class="mb-3">
                        <label for="userPasswordConfirm" class="col-sm-2 col-form-label">비밀번호 확인</label>
                        <input type="password" class="form-control" id="userPasswordConfirm">
                        <div id="passwordHelpBlock2" class="form-text"></div>
                    </div>
                    <div class="mb-3">
                        <label for="userName" class="form-label">이름</label>
                        <input type="text" class="form-control" id="userName" value="${memberInfo.userName}" disabled>
                    </div>
                    <div class="mb-3">
                        <label for="userHp" class="form-label">휴대폰</label>
                        <input type="text" maxlength="11" oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');"
                               class="form-control" id="userHp" placeholder=${memberInfo.userHp}>
                        <div id="hpHelpBlock" class="form-text"></div>
                    </div>
                    <div class="mb-3">
                        <label for="userEmail" class="form-label">이메일</label>
                        <input type="email" class="form-control" id="userEmail" placeholder=${memberInfo.userEmail}>
                        <div id="emailHelpBlock" class="form-text"></div>
                    </div>
                </div>
                <div class="mb-4"></div>
                <div class="d-grid gap-2">
                    <button class="btn btn-primary" id="edit" type="button" disabled>적용</button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {

        $("#edit").on("click", function () {
            edit();
        });

        $("#accountMenuButton").on("click", function () {
            if ($("#gnbDepth").hasClass("is-active")) {
                $("#gnbDepth").removeClass("is-active");
            } else {
                $("#gnbDepth").addClass("is-active");
            }
        });

        function edit() {
            const pwdPattern = /^[A-Za-z\d@$!%*#?&]{10,}$/;
            const hpPattern = /^[0-9]{11}$/;
            const emailPattern = /^[a-z0-9]+@[a-z]+\.[a-z]{2,3}$/;

            if ($("#userPassword").val() && $("#userPassword").val() != $("#userPasswordConfirm").val()) {
                $("#passwordHelpBlock2").html('비밀번호가 일치하지 않습니다.').css("color", "red");
                $("#userPassword").focus();
                return false;
            }
            if ($("#userPassword").val() && !pwdPattern.test($("#userPassword").val())) {
                $("#passwordHelpBlock").html('비밀번호는 10자리 이상 영문, 숫자 및 특수문자만 사용 가능합니다.').css("color", "red");
                $("#userPassword").focus();
                return false;
            }
            if ($("#userHp").val() && !hpPattern.test($("#userHp").val())) {
                $("#hpHelpBlock").html('휴대폰 번호는 11자의 숫자만 사용 가능합니다.').css("color", "red");
                $("#userHp").focus();
                return false;
            }
            if ($("#userEmail").val() && !emailPattern.test($("#userEmail").val())) {
                $("#emailHelpBlock").html('이메일 형식이 올바르지 않습니다.').css("color", "red");
                $("#userEmail").focus();
                return false;
            }
            if (!$("#userPassword").val() && !$("#userPasswordConfirm").val() && !$("#userHp").val()
                && !$("#userEmail").val()) {
                alert("")
            }

            const param =
                {
                    "userPassword": $("#userPassword").val(),
                    "userHp": $("#userHp").val(),
                    "userEmail": $("#userEmail").val(),
                };

            $.ajax({
                type: 'PATCH',
                data: JSON.stringify(param),
                dataType: 'text',
                contentType: 'application/json; utf-8',
                url: "/am/member/" + $("#userId").val(),
                success: function (data) {
                    alert(data);
                    location.href = "/am/member";
                },
                error: function (e) {
                    alert("[" + e.status + "]\n" + e.message);
                    if (e.status === 403) {
                        location.href = "/am/manager/login";
                    }
                },
            })
        }

        $("#userPassword").on("input", function () {
            validateuserPassword();
            buttonConfirm();
        });

        function validateuserPassword() {
            const pattern = /^[A-Za-z\d@$!%*#?&]{10,}$/;
            const userPassword = $("#userPassword").val();

            if (!pattern.test(userPassword)) {
                $("#passwordHelpBlock").html('비밀번호는 10자리 이상 영문, 숫자 및 특수문자만 사용 가능합니다.').css("color", "red");
            } else {
                $("#passwordHelpBlock").html('');
            }
        }

        $("#userPasswordConfirm").on("input", function () {
            userPasswordConfirm();
        });

        function userPasswordConfirm() {
            const userPasswordConfirm = $("#userPasswordConfirm").val();
            const userPassword = $("#userPassword").val();

            if (userPasswordConfirm === userPassword) {
                $("#passwordHelpBlock2").html('');
            } else {
                $("#passwordHelpBlock2").html('불일치').css("color", "red");
            }
        }

        $("#userHp").on("input", function () {
            validateuserHp();
            buttonConfirm();
        });

        function validateuserHp() {
            const pattern = /^[0-9]{11}$/;
            const userHp = $("#userHp").val();

            if (!pattern.test(userHp)) {
                $("#hpHelpBlock").html('휴대전화번호가 정확한지 확인해 주세요.').css("color", "red");
            } else {
                $("#hpHelpBlock").html('');
            }
        }

        $("#userEmail").on("input", function () {
            buttonConfirm();

            const pattern = /^[a-z0-9]+@[a-z]+\.[a-z]{2,3}$/;
            const userEmail = $("#userEmail").val();

            if (!pattern.test(userEmail)) {
                $("#emailHelpBlock").html('이메일 주소가 정확한지 확인해 주세요.').css("color", "red");
            } else {
                $("#emailHelpBlock").html('');
            }
        });

        function buttonConfirm() {
            const userEmail = $("#userEmail").val().trim();
            const userPassword = $("#userPassword").val().trim();
            const userHp = $("#userHp").val().trim();
            const editButton = $("#edit");

            if (userEmail !== '' || userPassword !== '' || userHp !== '') {
                editButton.prop('disabled', false);
            } else {
                editButton.prop('disabled', true);
            }
        }
    });
</script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4" crossorigin="anonymous"></script>
</body>
</html>