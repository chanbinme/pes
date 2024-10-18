<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>회원가입</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <script src="https://code.jquery.com/jquery-3.4.1.js"></script>
</head>
<body>
<div class="container">
    <div class="input-form-background row">
        <div class="input-form input-form02 col-md-12 mx-auto">
            <h4 class="mb-3 main-title">회원가입</h4>
            <form class="validation-form novalidate">
                <div class="row">
                    <div class="mb-3">
                        <input type="hidden" id="isDuplicatedMember" value="true" />
                        <label for="userId" class="form-label">아이디*</label>
                        <div class="input-group">
                            <input type="text" class="form-control" id="userId" placeholder="아이디를 입력해주세요.">
                            <button class="btn btn-outline-secondary" type="button" id="check-duplicated-member">중복 확인</button>
                        </div>
                        <div id="idHelpBlock" class="form-text"></div>
                    </div>
                    <div class="mb-3">
                        <label for="userPassword" class="col-sm-2 col-form-label">비밀번호 *</label>
                        <input type="password" class="form-control" id="userPassword" placeholder="비밀번호를 입력해주세요.">
                        <div id="passwordHelpBlock" class="form-text"></div>
                    </div>
                    <div class="mb-3">
                        <label for="userPasswordConfirm" class="col-sm-2 col-form-label">비밀번호 확인 *</label>
                        <input type="password" class="form-control" id="userPasswordConfirm">
                        <div id="passwordHelpBlock2" class="form-text"></div>
                    </div>
                    <div class="mb-3">
                        <label for="userName" class="form-label">이름 *</label>
                        <input type="text" class="form-control" id="userName" placeholder="홍길동">
                        <div id="nameHelpBlock" class="form-text"></div>
                    </div>
                    <div class="mb-3">
                        <label for="userHp" class="form-label">휴대폰 *</label>
                        <input type="text" maxlength="11" oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');"
                               class="form-control" id="userHp" placeholder="01012345678">
                        <div id="hpHelpBlock" class="form-text"></div>
                    </div>
                    <div class="mb-3">
                        <label for="userEmail" class="form-label">이메일 *</label>
                        <input type="text" class="form-control" id="userEmail" placeholder="email@gmail.com">
                        <div id="emailHelpBlock" class="form-text"></div>
                    </div>
                </div>
                <div class="mb-4"></div>
                <div class="d-grid gap-2">
                    <button class="btn btn-primary" id="join" type="button">가입 완료</button>
                </div>
            </form>
        </div>
    </div>
</div>
<%@ include file="/WEB-INF/jsp/include/top.jsp" %>
<script>
    $(document).ready(function () {

        $("#accountMenuButton").on("click", function () {
            if ($("#gnbDepth").hasClass("is-active")) {
                $("#gnbDepth").removeClass("is-active");
            } else {
                $("#gnbDepth").addClass("is-active");
            }
        });

        // 아이디 중복 확인 버튼 클릭 이벤트
        $("#check-duplicated-member").on("click", function () {
            checkDuplicatedMember();
        });

        function checkDuplicatedMember() {
            // 아이디 중복 확인
            if (!$("#userId").val()) {
                alert("아이디를 입력하세요.")
                return false;
            }

            $.ajax({
                type: 'GET',
                dataType: 'text',
                async: false,
                url: "/am/member/checkDuplicatedMember/" + $("#userId").val(),
                success: function (data) {
                    if (data === "true" || data == null) {
                        alert("이미 사용중인 아이디입니다.")
                    }
                    else {
                        alert("사용 가능한 아이디입니다.")
                    }
                    $("#isDuplicatedMember").val(data)
                },
                error: function (e) {
                    alert("[" + e.status + "]\n" + e.message);
                    if (e.status === 403) {
                        location.href = "/am/manager/login";
                    }
                },
            })
        }

        $("#join").on("click", function () {
            join();
        });

        function join() {
            const idPattern = /^[a-zA-Z0-9]{6,20}$/;
            const pwdPattern = /^[A-Za-z\d@$!%*#?&]{10,}$/;
            const namePattern = /^[a-zA-Zㄱ-힣]+$/;
            const hpPattern = /^[0-9]{11}$/;
            const emailPattern = /^[a-z0-9]+@[a-z]+\.[a-z]{2,3}$/;

            if (!$("#userId").val()) {
                $("#idHelpBlock").html('아이디는 필수 정보입니다.').css("color", "red");
                $("#userId").focus();
                return false;
            }
            if (!idPattern.test($("#userId").val())) {
                $("#idHelpBlock").html('아이디는 6~20자의 영문 대/소문자, 숫자만 사용 가능합니다.').css("color", "red");
                $("#userId").focus();
                return false;
            }
            if ($("#isDuplicatedMember").val() == "true") {
                $("#idHelpBlock").html('아이디 중복 확인이 필요합니다.').css("color", "red");
                $("#userId").focus();
                return false;
            }
            if (!$("#userPassword").val()) {
                $("#passwordHelpBlock").html('비밀번호는 필수 정보입니다.').css("color", "red");
                $("#userPassword").focus();
                return false;
            }
            if (!pwdPattern.test($("#userPassword").val())) {
                $("#passwordHelpBlock").html('비밀번호는 10자리 이상 영문, 숫자 및 특수문자만 사용 가능합니다.').css("color", "red");
                $("#userPassword").focus();
                return false;
            }
            if (!$("#userPasswordConfirm").val()) {
                $("#passwordHelpBlock2").html('비밀번호 확인을 입력하세요.').css("color", "red");
                $("#userPasswordConfirm").focus();
                return false;
            }
            if ($("#userPassword").val() != $("#userPasswordConfirm").val()) {
                $("#passwordHelpBlock2").html('비밀번호가 일치하지 않습니다.').css("color", "red");
                $("#userPasswordConfirm").focus();
                return false;
            }
            if (!$("#userName").val()) {
                $("#nameHelpBlock").html('이름은 필수 정보입니다.').css("color", "red");
                $("#userName").focus();
                return false;
            }
            if (!namePattern.test($("#userName").val())) {
                $("#nameHelpBlock").html('이름은 한글, 영문 대/소문자만 사용 가능합니다.').css("color", "red");
                $("#userName").focus();
                return false;
            }
            if (!$("#userHp").val()) {
                $("#hpHelpBlock").html('휴대폰 번호는 필수 정보입니다.').css("color", "red");
                $("#userHp").focus();
                return false;
            }
            if (!hpPattern.test($("#userHp").val())) {
                $("#hpHelpBlock").html('휴대폰 번호는 11자의 숫자만 사용 가능합니다.').css("color", "red");
                $("#userHp").focus();
                return false;
            }
            if (!$("#userEmail").val()) {
                $("#emailHelpBlock").html('이메일은 필수 정보입니다.').css("color", "red");
                $("#userEmail").focus();
                return false;
            }
            if (!emailPattern.test($("#userEmail").val())) {
                $("#emailHelpBlock").html('이메일 형식이 올바르지 않습니다.').css("color", "red");
                $("#userEmail").focus();
                return false;
            }

            const param =
                {
                    "userId" : $("#userId").val(),
                    "userPassword" : $("#userPassword").val(),
                    "userName" : $("#userName").val(),
                    "userHp" : $("#userHp").val(),
                    "userEmail" : $("#userEmail").val()
                };

            $.ajax({
                type: 'POST',
                data: JSON.stringify(param),
                dataType: 'text',
                contentType: 'application/json; utf-8',
                url: "/am/member/join",
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

        $("#userPasswordConfirm").on("input", function () {
            userPasswordConfirm();
        });

        function userPasswordConfirm() {
            const userPasswordConfirm = $("#userPasswordConfirm").val();
            const userPassword = $("#userPassword").val();

            if (userPasswordConfirm === userPassword) {
                $("#passwordHelpBlock2").html('');
                $("#userPasswordConfirm").addClass('is-valid');
            } else {
                $("#passwordHelpBlock2").html('불일치').css("color", "red");
                $("#userPasswordConfirm").removeClass('is-valid');
            }
        }

        $("#userId").on("input", function () {
            validateuserId();

            // 중복 확인 초기화
            $("#isDuplicatedMember").val(true);
        });

        function validateuserId() {
            const pattern = /^[a-zA-Z0-9]{6,20}$/;
            const userId = $("#userId").val();

            if (!pattern.test(userId)) {
                $("#idHelpBlock").html('아이디는 6~20자의 영문 대/소문자, 숫자만 사용 가능합니다.').css("color", "red");
                $("#userId").removeClass('is-valid');
            } else {
                $("#idHelpBlock").html('');
                $("#userId").addClass('is-valid');

            }
        }

        $("#userPassword").on("input", function () {
            validateuserPassword();
        });

        function validateuserPassword() {
            const pattern = /^[A-Za-z\d@$!%*#?&]{10,}$/;
            const userPassword = $("#userPassword").val();

            if (!pattern.test(userPassword)) {
                $("#passwordHelpBlock").html('비밀번호는 10자리 이상 영문, 숫자 및 특수문자만 사용 가능합니다.').css("color", "red");
                $("#userPassword").removeClass('is-valid');
            } else {
                $("#passwordHelpBlock").html('');
                $("#userPassword").addClass('is-valid');
            }
        }

        $("#userName").on("input", function () {
            validateuserName();
        });

        function validateuserName() {
            const pattern = /^[a-zA-Zㄱ-힣]+$/;
            const userName = $("#userName").val();

            if (!pattern.test(userName)) {
                $("#nameHelpBlock").html('이름은 한글, 영문 대/소문자만 사용 가능합니다.').css("color", "red");
                $("#userName").removeClass('is-valid');
            } else {
                $("#nameHelpBlock").html('');
                $("#userName").addClass('is-valid');
            }
        }

        $("#userHp").on("input", function () {
            validateuserHp();
        });

        function validateuserHp() {
            const pattern = /^[0-9]{11}$/;
            const userHp = $("#userHp").val();

            if (!pattern.test(userHp)) {
                $("#hpHelpBlock").html('휴대전화 번호가 정확한지 확인해 주세요.').css("color", "red");
                $("#userHp").removeClass('is-valid');
            } else {
                $("#hpHelpBlock").html('');
                $("#userHp").addClass('is-valid');
            }
        }

        $("#userEmail").on("input", function () {
            const pattern = /^[a-z0-9]+@[a-z]+\.[a-z]{2,3}$/;
            const userEmail = $("#userEmail").val();

            if (!pattern.test(userEmail)) {
                $("#emailHelpBlock").html('이메일 주소가 정확한지 확인해 주세요.').css("color", "red");
                $("#userEmail").removeClass('is-valid');
            } else {
                $("#emailHelpBlock").html('');
                $("#userEmail").addClass('is-valid');
            }
        });
    });
</script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4" crossorigin="anonymous"></script>
</body>
</html>