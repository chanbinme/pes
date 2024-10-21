<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <title>인사평가 - 관리</title>
    <link rel="stylesheet" href="/css/style.css?v=22">
    <link rel="stylesheet" href="/css/jstree_style.css?v=21"/>
    <script src="../js/jquery-3.7.1.min.js"></script>
    <script src="../js/jstree.js"></script>
    <script src="../js/ui.js"></script>
</head>
<body>
<header class="header">
    <h2 class="header__title">관리</h2>
    <%@ include file="/WEB-INF/jsp/include/top.jsp" %>
</header>
<div class="container">
    <div class="setting">
        <div class="table__wrap">
            <div class="table__item">
                <div class="table__info">
                    <strong class="table__title">임원 평가 기간 설정</strong>
                </div>
                <div class="table-cotent">
                    <table class="table table--calendar">
                        <caption>평가기간 내용의 표</caption>
                        <colgroup>
                            <col style="width:10%">
                            <col>
                        </colgroup>
                        <tbody>
                        <tr>
                            <th scope="row">평가 기간</th>
                            <td class="input-item__wrap">
                                <div class="input-item__group">
                                    <div class="input-item">
                                        <input type="date" id="start-date" class="input-item__date" title="평가 기간 시작 날짜" placeholder="시작 날짜를 선택 해주세요." required>
                                    </div>
                                    <div class="input-item">
                                        <input type="date" id="end-date" class="input-item__date" title="평가 기간 마감 날짜" placeholder="마감 날짜를 선택 해주세요." required>
                                    </div>
                                </div>
                                <div class="btn__wrap">
                                    <button class="btn btn--type01" id="btn-period-confirm" type="button">
                                        <span class="btn__text">저장</span>
                                    </button>
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    function getOfficerEvaluationPeriod() {
        $.ajax({
            type: 'GET',
            url: '/am/admin/officer-evaluation-period',
            dataType: 'JSON',
            success: function (data) {
                displayOfficerEvaluationPeriod(data);
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

    function displayOfficerEvaluationPeriod(officerEvaluationPeriodInfo) {
        const startDate = officerEvaluationPeriodInfo.startDate.split('T')[0];
        const endDate = officerEvaluationPeriodInfo.endDate.split('T')[0];
        $('#start-date').val(startDate);
        $('#end-date').val(endDate);
    }

    function saveOfficerEvaluationPeriod() {
        const startDate = $('#start-date').val();
        const endDate = $('#end-date').val();
        const data = {
            startDate: new Date(startDate),
            endDate: new Date(endDate)
        };

        $.ajax({
            type: 'POST',
            url: '/am/admin/officer-evaluation-period',
            data: JSON.stringify(data),
            contentType: 'application/json',
            dataType: 'text',
            success: function (data) {
                alert(data);
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

    $(document).ready(function () {
        getOfficerEvaluationPeriod();

        $('#btn-period-confirm').on('click', function () {
            saveOfficerEvaluationPeriod();
        });
    });
</script>
</body>
