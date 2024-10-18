<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <title>인사평가 - 랭킹 결과</title>
    <link rel="stylesheet" href="/css/style.css?v=22">
    <link rel="stylesheet" href="/css/jstree_style.css?v=21" />
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
    <div class="ranking ranking__result">
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
                <strong class="table__title"><c:out value="${selectedYear}" /></strong>
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
                            <button type="button" class="btn btn__filter" id="btn--filter">
                                <span class="btn__text">등급</span>
                            </button>
                            <div class="filter-box">
                                <ul class="filter-box__list">
                                    <li class="filter-box__item">
                                        <input type="checkbox" id="filter-check01" class="filter-box__checkbox" name="filter-checkbox" value="S">
                                        <label for="filter-check01">
                                            <span class="filter-box__text">S</span>
                                        </label>
                                    </li>
                                    <li class="filter-box__item">
                                        <input type="checkbox" id="filter-check02" class="filter-box__checkbox" name="filter-checkbox" value="A">
                                        <label for="filter-check02">
                                            <span class="filter-box__text">A</span>
                                        </label>
                                    </li>
                                    <li class="filter-box__item">
                                        <input type="checkbox" id="filter-check03" class="filter-box__checkbox" name="filter-checkbox" value="B">
                                        <label for="filter-check03">
                                            <span class="filter-box__text">B</span>
                                        </label>
                                    </li>
                                    <li class="filter-box__item">
                                        <input type="checkbox" id="filter-check04" class="filter-box__checkbox" name="filter-checkbox" value="C">
                                        <label for="filter-check04">
                                            <span class="filter-box__text">C</span>
                                        </label>
                                    </li>
                                    <li class="filter-box__item">
                                        <input type="checkbox" id="filter-check05" class="filter-box__checkbox" name="filter-checkbox" value="D">
                                        <label for="filter-check05">
                                            <span class="filter-box__text">D</span>
                                        </label>
                                    </li>
                                    <li class="filter-box__item">
                                        <input type="checkbox" id="filter-check06" class="filter-box__checkbox" name="filter-checkbox" value="-">
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
                    <tbody class="resultTbody"></tbody>
                </table>
            </div>
            <div class="btn__wrap">
                <button class="btn btn--type01" type="button" id="btn--cancel">
                    <span class="btn__text">마감 취소</span>
                </button>
            </div>
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
                        id:item.id,
                        parent:item.parent,
                        text:"<span class='js-tree__text'>" + item.text + "</span>"
                    }
                });
                $('#jstree').jstree({
                    "plugins" : [ "wholerow", "checkbox" ],
                    core: {
                        data: organizations,
                        "dblclick_toggle" : false
                    }
                })
                .bind('loaded.jstree', function (event, data) {
                    // 트리 로딩 완료 이벤트
                    $('#jstree').jstree('open_node', '#22');
                    $('#jstree').jstree('open_node', '#23');
                    $('#jstree').jstree('open_node', '#24');
                    $('#jstree').jstree('open_node', '#25');
                })
                .bind('select_node.jstree', function (event, data) {
                    // 노드 선택 이벤트
                });
            },
            error: function (data) {
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
                const orgTitle = org.text.replace('<span class=\'js-tree__text\'>', '').replace('</span>', '');
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
            url: '/am/totals/preview/' + year,
            data: JSON.stringify(orgInfoList),
            contentType: 'application/json',
            dataType: 'JSON',
            success: function (data) {
                displayTotalList(data);
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
                const note = totalInfo.note;
                const year = totalInfo.year;
                const positionGb = totalInfo.positionGb;
                const teamId = totalInfo.teamId;
                const divisionTitle = totalInfo.divisionTitle;

                const tr = $('<tr></tr>');
                let teamTitleTd;
                if (divisionTitle !== null) {
                    teamTitleTd = $('<td><input type="text" class="table-input team-title" title="팀명/부문명" readonly value="'
                        + teamTitle + '/' + divisionTitle + '"></td>');
                } else {
                    teamTitleTd = $('<td><input type="text" class="table-input team-title" title="팀명/부문명" readonly value="'
                        + teamTitle + '"></td>');
                }

                const nameTd = $('<td><input type="text" class="table-input name" title="성명" readonly value="'
                    + name +'"></td>');
                const positionTd = $('<td><input type="text" class="table-input position" title="직책" readOnly value="'
                    + position + '"></td>');
                const totalPointTd = $('<td class="total"><input type="text" class="table-input total-point" title="최종점수" readOnly value="'
                    + totalPoint + '"></td>');
                const dbRankTd = $('<td><input type="text" class="table-input rank" title="기존등급" readOnly value="'
                    + dbRank + '"></td>');
                const noteTd = $('<td><textarea readonly class="note" title="메모">' + note + '</textarea></td>');
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
                resultTbody.append(tr);
            });
        }
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
        console.log(checkedRanks);

        if (checkedRanks.length > 0) {
            $('.resultTbody').find('tr').each(function () {
                const rank = $(this).find('.rank').val();

                if (!checkedRanks.includes(rank) && !$(this).hasClass('hidden')) {
                    $(this).addClass('hidden');
                } else if (checkedRanks.includes(rank) && $(this).hasClass('hidden')) {
                    $(this).removeClass('hidden');
                }
            });
        } else if (checkedRanks.length === 0) {
            $('.resultTbody').find('tr').each(function () {
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

    function cancelEndYear() {
        const year = $('#select01').val();
        $.ajax({
            type: 'DELETE',
            url: '/am/totals/finish',
            data: {
                year: year
            },
            dataType: 'text',
            success: function (data) {
                alert(data);
                location.reload();
            },
            error: function (e) {
                if (e.status === 403) {
                    location.href = "/am/manager/login";
                }
                const error = JSON.parse(e.responseText);
                alert("[" + error.status + "]\n" + error.message);
            }
        })
    }

    <!-- 조직도 변경 조직 선택 시 이벤트 -->
    $(document).on("click", "#jstree .jstree-anchor", function () {
        requestFindTotalRanking();
    });

    $(document).ready(function () {
        getOrganizationChartJson();
        displayYear();

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

        $('#btn--cancel').on('click', function () {
            cancelEndYear();
        });
    });
</script>
</body>
</html>