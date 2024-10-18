<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>로그인</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.js"></script>
    <style>
        .input-form {
            max-width: 680px;
            margin-top: 80px;
            padding: 32px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="input-form-background row">
        <div class="input-form col-md-12 mx-auto">
            <h4 class="mb-3 main-title">로그인</h4>
            <form class="validation-form novalidate" id="frm" name="frm">
                <div class="row">
                    <div class="mb-3">
                        <label for="userId" class="form-label">아이디</label>
                        <input type="text" class="form-control" id="userId" name="userId" placeholder="아이디를 입력해주세요." autocomplete="off">
                    </div>
                    <div class="mb-3">
                        <label for="userPassword" class="form-label">비밀번호</label>
                        <input type="password" class="form-control" id="userPassword" name="userPassword" placeholder="비밀번호를 입력해주세요." autocomplete="off">
                    </div>
                </div>
                <div class="mb-4"></div>
                <div class="d-grid gap-2">
                    <input class="btn btn-primary" id="login" type="button" value="로그인">
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    $(document).ready(function () {
        // 로그인 버튼 클릭 이벤트
        $("#login").on('click', function () {
            login();
        });

        // 로그인 input에서 enter 입력 이벤트
        $(".form-control").on('keyup', function (event) {
            if (event.keyCode === 13) {
                login();
            }
        });

        function login() {
            // 로그인 인증
            const userId = $("#userId").val();
            const userPassword = $("#userPassword").val();

            if (!userId) {
                alert("아이디를 입력하세요.")
                userId.focus();
                return false;
            }
            if (!userPassword) {
                alert("비밀번호를 입력하세요.")
                userPassword.focus();
                return false;
            }

            const param = {"id": userId, "password": userPassword};

            $.ajax({
                type: 'POST',
                data: JSON.stringify(param),
                dataType: 'text',
                contentType: 'application/json; charset=UTF-8',
                url: "/am/manager/loginProc",
                success: function (data) {
                    location.href = "/am/jobs-evaluation";
                },
                error: function (e) {
                    if (e.status === 403) {
                        location.href = "/am/manager/login";
                    }
                    const error = JSON.parse(e.responseText);
                    alert("[" + error.status + "]\n" + error.message);
                },
            });
        }
    });
</script>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4" crossorigin="anonymous"></script>
</body>
</html>