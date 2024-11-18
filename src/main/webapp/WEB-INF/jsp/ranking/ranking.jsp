<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <title>인사평가 - 랭킹</title>
    <link rel="stylesheet" href="/css/style.css?v=22">
    <link rel="stylesheet" href="/css/jstree_style.css?v=21"/>
    <script src="/js/jquery-3.7.1.min.js"></script>
    <script src="/js/jstree.js"></script>
    <script src="/js/ui.js"></script>
    <style>
        .hidden {
            display: none;
        }
    </style>
</head>
<body>
<header class="header">
    <h2 class="header__title">랭킹</h2>
    <%@ include file="/WEB-INF/jsp/include/top.jsp" %>
</header>
<div class="container">
    <div class="ranking">
        <div class="content__wrap">
            <div class="select-year__box">
                <select id="select01" class="select-year__item"></select>
            </div>
            <div class="tree__wrap">
                <div id="jstree" class="js-tree"></div>
            </div>
        </div>
        <div class="table__wrap">
            <div class="table__info">
                <%--                <p class="table__text">등급이 자동계산 되지 않을 경우, 등급을 직접 선택해야 합니다.</p>--%>
                <div class="table__btn-wrap">
                    <button type="button" class="btn table__btn" id="excelDownloadBtn">
                        <span class="table__btn-text">엑셀 다운로드</span>
                    </button>
                    <button type="button" class="btn table__btn" id="btn--popup"
                            data-popup="preview">
                        <span class="table__btn-text">랭킹 결과</span>
                    </button>
                </div>
            </div>
            <div class="table-cotent">
                <table class="table">
                    <caption>팀명/부문명, 성명, 직책, 최종점수, 자동 계산 등급, 기존 등급, 등급선택, 메모 내용의 표</caption>
                    <colgroup>
                        <col style="width:25%">
                        <col style="width:8%">
                        <col style="width:8%">
                        <col style="width:8%">
                        <col style="width:8%">
                        <col style="width:8%">
                        <col style="width:8%">
                        <col>
                    </colgroup>
                    <thead>
                    <tr>
                        <th scope="col">팀명/부문명</th>
                        <th scope="col">성명</th>
                        <th scope="col">직책</th>
                        <th scope="col">최종점수</th>
                        <th scope="col">
                            <span class="rank__text">자동 계산<br>등급</span>
                            <div class="tooltip__box" data-tooltipBox="tooltip01">
                                <button type="button" class="btn btn__tooltip"><span class="blind">툴팁 보기</span>
                                </button>
                                <div class="tooltip" data-tooltip="tooltip01">
                                    <ul class="tooltip__list">
                                        <li class="tooltip__item">S : 125 이상</li>
                                        <li class="tooltip__item">A : 112 ~ 124</li>
                                        <li class="tooltip__item">B : 79 ~ 111</li>
                                        <li class="tooltip__item">C : 66 ~ 78</li>
                                        <li class="tooltip__item">D : 66 미만</li>
                                    </ul>
                                </div>
                            </div>
                        </th>
                        <th scope="col">기존등급</th>
                        <th scope="col">등급<br>선택</th>
                        <th scope="col">메모</th>
                    </tr>
                    </thead>
                    <tbody class="resultTbody">
                    </tbody>
                </table>
            </div>
            <div class="btn__wrap">
                <button class="btn btn--type01 btn--outline" id="btn--confirm" type="button">
                    <span class="btn__text">저장</span>
                </button>
                <button class="btn btn--type01" id="btn--fix" type="button">
                    <span class="btn__text">연도 마감</span>
                </button>
            </div>
        </div>
    </div>
</div>
<div class="popup__wrap popup--full popup-preview" id="preview">
    <div class="popup">
        <div class="popup-content">
            <div class="popup-header">
                <strong class="popup-header__title">랭킹 결과</strong>
            </div>
            <div class="container">
                <div class="ranking">
                    <div class="content__wrap">
                        <div class="tree__wrap">
                            <div id="jstree_02" class="js-tree"></div>
                        </div>
                    </div>
                    <div class="table__wrap">
                        <div class="table__info">
                            <strong class="table__title"><c:out value="${selectedYear}"/> Ranking</strong>
                        </div>
                        <div class="table-cotent">
                            <table class="table">
                                <caption>팀명/부문명, 성명, 직책, 최종점수, 등급, 메모 내용의 표</caption>
                                <colgroup>
                                    <col style="width:26%">
                                    <col style="width:11%">
                                    <col style="width:11%">
                                    <col style="width:11%">
                                    <col style="width:11%">
                                    <col>
                                </colgroup>
                                <thead>
                                <tr>
                                    <th scope="col">팀명/부문명</th>
                                    <th scope="col">성명</th>
                                    <th scope="col">직책</th>
                                    <th scope="col">최종점수</th>
                                    <th scope="col">
                                        <button type="button" class="btn btn__filter"
                                                id="btn--filter">
                                            <span class="btn__text">등급</span>
                                        </button>
                                        <div class="filter-box">
                                            <ul class="filter-box__list">
                                                <li class="filter-box__item">
                                                    <input type="checkbox" id="filter-check01"
                                                           class="filter-box__checkbox"
                                                           name="filter-checkbox" value="S">
                                                    <label for="filter-check01">
                                                        <span class="filter-box__text">S</span>
                                                    </label>
                                                </li>
                                                <li class="filter-box__item">
                                                    <input type="checkbox" id="filter-check02"
                                                           class="filter-box__checkbox"
                                                           name="filter-checkbox" value="A">
                                                    <label for="filter-check02">
                                                        <span class="filter-box__text">A</span>
                                                    </label>
                                                </li>
                                                <li class="filter-box__item">
                                                    <input type="checkbox" id="filter-check03"
                                                           class="filter-box__checkbox"
                                                           name="filter-checkbox" value="B">
                                                    <label for="filter-check03">
                                                        <span class="filter-box__text">B</span>
                                                    </label>
                                                </li>
                                                <li class="filter-box__item">
                                                    <input type="checkbox" id="filter-check04"
                                                           class="filter-box__checkbox"
                                                           name="filter-checkbox" value="C">
                                                    <label for="filter-check04">
                                                        <span class="filter-box__text">C</span>
                                                    </label>
                                                </li>
                                                <li class="filter-box__item">
                                                    <input type="checkbox" id="filter-check05"
                                                           class="filter-box__checkbox"
                                                           name="filter-checkbox" value="D">
                                                    <label for="filter-check05">
                                                        <span class="filter-box__text">D</span>
                                                    </label>
                                                </li>
                                                <li class="filter-box__item">
                                                    <input type="checkbox" id="filter-check06"
                                                           class="filter-box__checkbox"
                                                           name="filter-checkbox" value="-">
                                                    <label for="filter-check06">
                                                        <span class="filter-box__text">등급없음</span>
                                                    </label>
                                                </li>
                                            </ul>
                                        </div>
                                    </th>
                                    <th scope="col">메모</th>
                                </tr>
                                </thead>
                                <tbody class="resultTbody2">
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="popup-btn__wrap">
            <button class="popup-btn__close" type="button" id="btn--close">
                <span class="popup-btn__text blind">팝업 닫기</span>
            </button>
        </div>
    </div>
</div>
<script>
    function getOrganizationChartJson() {
        $.ajax({
            type: 'GET',
            url: '/am/organizationchart',
            dataType: 'JSON',
            success: function (data) {
                const organizations = [];
                $.each(data, function (idx, item) {
                    organizations[idx] = {
                        id: item.id,
                        parent: item.parent,
                        text: "<span class='js-tree__text'>" + item.text + "</span>"
                    }
                });
                $('#jstree').jstree({
                    "plugins": ["wholerow", "checkbox"],
                    core: {
                        data: organizations,
                        "dblclick_toggle": false
                    }
                })
                .bind('loaded.jstree', function (event, data) {
                    // 트리 로딩 완료 이벤트
                    const topLevelNode = $("#jstree").jstree("get_node", "#").children[0];
                    $('#jstree').jstree("open_node", topLevelNode);
                })
                .bind('select_node.jstree', function (event, data) {
                    // 노드 선택 이벤트
                });
            },
            error: function (e) {
                alert("[" + e.status + "]\n" + e.message)
            },
        })
    }

    function getOrganizationChartJson2() {
        $.ajax({
            type: 'GET',
            url: '/am/organizationchart',
            dataType: 'JSON',
            success: function (data) {
                const organizations = [];
                $.each(data, function (idx, item) {
                    organizations[idx] = {
                        id: item.id,
                        parent: item.parent,
                        text: "<span class='js-tree__text'>" + item.text + "</span>"
                    }
                });
                $('#jstree_02').jstree({
                    "plugins": ["wholerow", "checkbox"],
                    "dblclick_toggle": false,
                    core: {
                        data: organizations,
                        "dblclick_toggle": false
                    }
                })
                .bind('loaded.jstree', function (event, data) {
                    // 트리 로딩 완료 이벤트
                    $('#jstree_02').jstree('open_node', '#22');
                    $('#jstree_02').jstree('open_node', '#23');
                    $('#jstree_02').jstree('open_node', '#24');
                    $('#jstree_02').jstree('open_node', '#25');
                })
                .bind('select_node.jstree', function (event, data) {
                    // 노드 선택 이벤트
                });
            },
            error: function (e) {
                alert("[" + e.status + "]\n" + e.message)
            },
        })
    }

    function displayTotalList(totalList) {
        const resultTbody = $('.resultTbody');
        resultTbody.empty();

        if (totalList.length > 0) {
            $.each(totalList, function (index, totalInfo) {
                const teamTitle = totalInfo.teamTitle;
                const name = totalInfo.name;
                const position = totalInfo.position;
                const totalPoint = totalInfo.totalPoint;
                const dbRank = totalInfo.ranking;
                const rank = totalInfo.newRanking;
                const note = totalInfo.note;
                const year = totalInfo.year;
                const positionGb = totalInfo.positionGb;
                const teamId = totalInfo.teamId;
                const divisionTitle = totalInfo.divisionTitle;
                const evaluationTotalId  = totalInfo.evaluationTotalId;

                const tr = $('<tr></tr>');
                let teamTitleTd;
                if (divisionTitle !== null && divisionTitle !== '') {
                    teamTitleTd = $('<td><input type="text" class="table-input team-title" title="팀명/부문명" readonly value="'
                        + teamTitle + '/' + divisionTitle + '"></td>');
                } else {
                    teamTitleTd = $('<td><input type="text" class="table-input team-title" title="팀명/부문명" readonly value="'
                        + teamTitle + '"></td>');
                }
                const nameTd = $(
                    '<td><input type="text" class="table-input name" title="성명" readonly value="'
                    + name + '"></td>');
                const positionTd = $(
                    '<td><input type="text" class="table-input position" title="직책" readOnly value="'
                    + position + '"></td>');
                const totalPointTd = $(
                    '<td class="total"><input type="text" class="table-input total-point" title="최종점수" readOnly value="'
                    + totalPoint + '"></td>');
                const rankTd = $(
                    '<td><input type="text" class="table-input ranking" title="자동 계산 등급" readOnly value="'
                    + rank + '"></td>');
                const dbRankTd = $(
                    '<td><input type="text" class="table-input" title="기존등급" readOnly value="'
                    + dbRank + '"></td>');
                const selectRankTd = $(
                    '<td><select class="table-select select-ranking" title="등급 선택">' +
                    '<option value="">선택</option>' +
                    '<option value="S">S</option>' +
                    '<option value="A">A</option>' +
                    '<option value="B">B</option>' +
                    '<option value="C">C</option>' +
                    '<option value="D">D</option>' +
                    '</select></td>');
                const noteTd = $(
                    '<td><textarea class="note" title="메모">' + note + '</textarea></td>');
                const yearInput = $('<input class="year" type="hidden" ' + 'value="' +
                    year + '">');
                const positionGbInput = $('<input class="position-gb" type="hidden" value="' +
                    positionGb + '">');
                const teamIdInput = $('<input class="team-id" type="hidden" ' + 'value="' +
                    teamId + '">');
                const evaluationTotalIdInput = $('<input class="evaluation-total-id" type="hidden" ' + 'value="' +
                    evaluationTotalId + '">');

                tr.append(teamTitleTd);
                tr.append(nameTd);
                tr.append(positionTd);
                tr.append(totalPointTd);
                tr.append(rankTd);
                tr.append(dbRankTd);
                tr.append(selectRankTd);
                tr.append(noteTd);
                tr.append(yearInput);
                tr.append(positionGbInput);
                tr.append(teamIdInput);
                tr.append(evaluationTotalIdInput);
                resultTbody.append(tr);
            });
        }
    }

    function displayTotalListPreview(totalList) {
        const resultTbody2 = $('.resultTbody2');
        resultTbody2.empty();

        if (totalList.length > 0) {
            $.each(totalList, function (index, totalInfo) {
                const teamTitle = totalInfo.teamTitle;
                const name = totalInfo.name;
                const position = totalInfo.position;
                const totalPoint = totalInfo.totalPoint;
                const dbRank = totalInfo.ranking;
                const note = totalInfo.note;
                const year = totalInfo.year;
                const positionGb = totalInfo.positionGb;
                const teamId = totalInfo.teamId;
                const divisionTitle = totalInfo.divisionTitle;

                const tr = $('<tr></tr>');
                let teamTitleTd;
                if (divisionTitle !== null && divisionTitle !== '') {
                    teamTitleTd = $('<td><input type="text" class="table-input team-title" title="팀명/부문명" readonly value="'
                        + teamTitle + '/' + divisionTitle + '"></td>');
                } else {
                    teamTitleTd = $('<td><input type="text" class="table-input team-title" title="팀명/부문명" readonly value="'
                        + teamTitle + '"></td>');
                }
                const nameTd = $(
                    '<td><input type="text" class="table-input name" title="성명" readonly value="'
                    + name + '"></td>');
                const positionTd = $(
                    '<td><input type="text" class="table-input position" title="직책" readOnly value="'
                    + position + '"></td>');
                const totalPointTd = $(
                    '<td class="total"><input type="text" class="table-input total-point" title="최종점수" readOnly value="'
                    + totalPoint + '"></td>');
                const dbRankTd = $(
                    '<td><input type="text" class="table-input rank" title="기존등급" readOnly value="'
                    + dbRank + '"></td>');
                const noteTd = $(
                    '<td><textarea readonly class="note" title="메모">' + note + '</textarea></td>');
                const yearInput = $('<input class="year" type="hidden" ' + 'value="' +
                    year + '">');
                const positionGbInput = $('<input class="position-gb" type="hidden" ' + 'value="' +
                    positionGb + '">');
                const teamIdInput = $('<input class="team-id" type="hidden" ' + 'value="' +
                    teamId + '">');

                tr.append(teamTitleTd);
                tr.append(nameTd);
                tr.append(positionTd);
                tr.append(totalPointTd);
                tr.append(dbRankTd);
                tr.append(noteTd);
                tr.append(yearInput);
                tr.append(positionGbInput);
                tr.append(teamIdInput);
                resultTbody2.append(tr);
            });
        }
    }

    function getTotalRankingList() {
        const totalRankingList = [];

        $('.resultTbody').find('tr').each(function () {
            const evaluationTotalId = $(this).find('.evaluation-total-id').val();
            const year = $(this).find('.year').val();
            const positionGb = $(this).find('.position-gb').val();
            const name = $(this).find('.name').val();
            const totalPoint = $(this).find('.total-point').val();
            const position = $(this).find('.position').val();
            const note = $(this).find('.note').val();
            const teamId = $(this).find('.team-id').val();
            const teamTitle = $(this).find('.team-title').val().split('/')[0];
            let ranking;
            if ($(this).find('.select-ranking option:selected').val()) {
                ranking = $(this).find('.select-ranking option:selected').val();
            } else {
                ranking = $(this).find('.ranking').val();
            }

            const totalRankingInfo = {
                evaluationTotalId: evaluationTotalId,
                year: year,
                teamId: teamId,
                teamTitle: teamTitle,
                positionGb: positionGb,
                position: position,
                name: name,
                totalPoint: totalPoint,
                ranking: ranking,
                note: note
            }

            totalRankingList.push(totalRankingInfo);
        });

        return totalRankingList;
    }

    function saveTotalRanking(totalRankingList) {
        $.ajax({
            type: 'POST',
            url: '/am/totals/ranking',
            data: JSON.stringify(totalRankingList),
            contentType: 'application/json',
            success: function (data) {
                requestFindTotalRanking();
                alert(data);
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

    function requestFindTotalRanking() {
        let orgInfoList = [];
        const checkedOrgList = $('#jstree').jstree(true).get_checked(true);

        $.each(checkedOrgList, function (index, org) {

            if (org.children.length === 0) {
                const orgTitle = org.text.replace('<span class=\'js-tree__text\'>', '').replace(
                    '</span>', '');
                const orgId = org.id;
                const orgInfo = {
                    teamId: orgId,
                    teamTitle: orgTitle
                }
                orgInfoList.push(orgInfo);
            }
        });

        if (orgInfoList.length === 0) {
            $('.resultTbody').empty();
            return;
        }

        const year = $('#select01').val();

        $.ajax({
            type: 'POST',
            url: '/am/totals/' + year,
            data: JSON.stringify(orgInfoList),
            contentType: 'application/json',
            dataType: 'JSON',
            success: function (data) {
                displayTotalList(data);
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

    function requestFindTotalRankingPreview() {
        let orgInfoList = [];
        const checkedOrgList = $('#jstree_02').jstree(true).get_checked(true);

        $.each(checkedOrgList, function (index, org) {

            if (org.children.length === 0) {
                const orgTitle = org.text.replace('<span class=\'js-tree__text\'>', '').replace(
                    '</span>', '');
                const orgId = org.id;
                const orgInfo = {
                    teamId: orgId,
                    teamTitle: orgTitle
                }
                orgInfoList.push(orgInfo);
            }
        });

        if (orgInfoList.length === 0) {
            $('.resultTbody2').empty();
            return;
        }

        const year = $('#select01').val();

        $.ajax({
            type: 'POST',
            url: '/am/totals/preview/' + year,
            data: JSON.stringify(orgInfoList),
            contentType: 'application/json',
            dataType: 'JSON',
            success: function (data) {
                displayTotalListPreview(data);
                totalRankingListFiltering();
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

    function endYear(year) {
        $.ajax({
            type: 'POST',
            url: '/am/totals/finish',
            data: {
                year: year
            },
            dataType: 'text',
            success: function (data) {
                requestFindTotalRanking();
                alert(data);
                location.reload();
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

    <!-- 모두 평가되었는지 확인 -->
    function checkAllEvaluationsComplete(year) {
        let result;
        $.ajax({
            type: 'GET',
            url: '/am/totals/check',
            data: {
                year: year
            },
            dataType: 'JSON',
            async: false,
            success: function (data) {
                result = data;
            },
            error: function (e) {
                if (e.status === 403) {
                    location.href = "/am/manager/login";
                }
                const error = JSON.parse(e.responseText);
                alert("[" + error.status + "]\n" + error.message);
            },
        })

        return result;
    }

    function totalRankingListFiltering() {
        let checkedRanks = [];

        $('input[type=checkbox][name=filter-checkbox]').each(function () {
            if ($(this).prop('checked')) {
                checkedRanks.push($(this).val());
            } else {
                const index = checkedRanks.indexOf($(this).val());
                if (index !== -1) {
                    checkedRanks.splice(index, 1);
                }
            }
        });

        if (checkedRanks.length > 0) {
            $('.resultTbody2').find('tr').each(function () {
                const rank = $(this).find('.rank').val();

                if (!checkedRanks.includes(rank) && !$(this).hasClass('hidden')) {
                    $(this).addClass('hidden');
                } else if (checkedRanks.includes(rank) && $(this).hasClass('hidden')) {
                    $(this).removeClass('hidden');
                }
            });
        } else if (checkedRanks.length === 0) {
            $('.resultTbody2').find('tr').each(function () {
                if ($(this).hasClass('hidden')) {
                    $(this).removeClass('hidden');
                }
            });
        }
    }

    function displayYear() {
        const select01 = $('#select01');
        let year;
        let option;
        <c:forEach var="item" items="${yearList}">
        year = "<c:out value="${item}"/>";
        if (year === "${selectedYear}") {
            option = $('<option value="' + year + '" selected>' + year + '년' + '</option>');
        } else {
            option = $('<option value="' + year + '">' + year + '년' + '</option>');
        }
        select01.append(option);
        </c:forEach>
    }

    <!-- 조직도 변경 조직 선택 시 이벤트 -->
    $(document).on("click", "#jstree .jstree-anchor", function () {
        requestFindTotalRanking();
    });

    <!-- 미리보기 조직도 변경 조직 선택 시 이벤트 -->
    $(document).on("click", "#jstree_02 .jstree-anchor", function () {
        requestFindTotalRankingPreview();
    });

    $(document).on("click", "#excelDownloadBtn", function () {
        let orgInfoList = [];
        const checkedOrgList = $('#jstree').jstree(true).get_checked(true);

        $.each(checkedOrgList, function (index, org) {

            if (org.children.length === 0) {
                const orgTitle = org.text.replace('<span class=\'js-tree__text\'>', '').replace(
                    '</span>', '');
                const orgId = org.id;
                const orgInfo = {
                    teamId: orgId,
                    teamTitle: orgTitle
                }
                orgInfoList.push(orgInfo);
            }
        });

        if (orgInfoList.length === 0) {
            alert("랭킹 정보를 다운로드 할 조직을 선택해주세요.");
            return;
        }

        if ($('.resultTbody').find('tr').length === 0) {
            alert("다운로드할 데이터가 없습니다.");
            return;
        }

        const year = $('#select01').val();

        $.ajax({
            type: 'POST',
            url: '/am/totals/excel-download/' + year,
            data: JSON.stringify(orgInfoList),
            contentType: 'application/json',
            xhrFields: {
                responseType: 'blob'
            },
            success: function (data, status) {
                // 다운로드 처리
                const blob = new Blob([data], {type: 'application/vnd.ms-excel'});
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                let date = new Date();
                const utc = date.getTime() + (date.getTimezoneOffset() * 60 * 1000);
                const koreaTimeDiff = 9 * 60 * 60 * 1000;
                date = new Date(utc + koreaTimeDiff);
                a.download = 'ranking_excel_' + date.getFullYear() + '-' + date.getMonth() + '-' + date.getDay() + '.xlsx';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
            },
            error: function (e) {
                if (e.status === 403) {
                    location.href = "/am/manager/login";
                }
                const error = JSON.parse(e.responseText);
                alert("[" + error.status + "]\n" + error.message);
            },
        })
    });

    $(document).ready(function () {
        getOrganizationChartJson();
        getOrganizationChartJson2();
        displayYear();

        <!-- 저장 -->
        $('#btn--confirm').on('click', function () {
            const totalRankingList = getTotalRankingList();
            saveTotalRanking(totalRankingList);
        });

        <!-- 연도 마감 -->
        $('#btn--fix').on('click', function () {
            if (confirm("연도 마감 시, 해당 연도 평가 결과는 수정할 수 없습니다.\n마감하시겠습니까?")) {
                const selectedYear = "${selectedYear}";
                if (checkAllEvaluationsComplete(selectedYear)) {
                    endYear(selectedYear);
                } else {
                    alert("평가 받지 못한 피평가자가 존재합니다.");
                }
            }
        });

        <!-- 미리보기 -->
        $('#btn--popup').on('click', function () {
            $('.popup__wrap').addClass('is-active');
        });

        $('#btn--close').on('click', function () {
            $('.popup__wrap').removeClass('is-active');
            $('#jstree_02').jstree('deselect_all');
            $('.resultTbody2').empty();
        });

        $('#btn--filter').on('click', function () {
            if ($('.filter-box').hasClass('is-active')) {
                $('.filter-box').removeClass('is-active');
            } else {
                $('.filter-box').addClass('is-active');
            }
        });

        $('input[type=checkbox][name=filter-checkbox]').change(function () {
            totalRankingListFiltering();
        });

        $("#select01").on("change", function () {
            location.href = '/am/totals/ranking?selectedYear=' + $(this).val();
        });
    });
</script>
</body>
</html>