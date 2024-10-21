<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <title>인사평가 - 팀 평가</title>
    <link rel="stylesheet" href="/css/style.css?v=22">
    <link rel="stylesheet" href="/css/jstree_style.css?v=21"/>
    <script src="/js/jquery-3.7.1.min.js"></script>
    <script src="/js/jstree.js"></script>
    <script src="/js/ui.js"></script>
</head>
<body>
    <header class="header">
        <h2 class="header__title">팀 평가</h2>
        <%@ include file="/WEB-INF/jsp/include/top.jsp" %>
    </header>
    <div class="container">
        <div class="project-rating">
            <div class="content__wrap is-active">
                <div class="select-year__box">
                    <select id="select01" class="select-year__item"></select>
                </div>
                <div class="tree__wrap">
                    <div id="jstree" class="js-tree"></div>
                    <!-- //js-tree -->
                </div>
                <div class="btn__wrap ">
                    <button class="btn btn--toggle is-active" type="button">
                        <span class="blind">접기</span>
                        <svg aria-hidden="true" focusable="false" class="btn__arrow" role="img"
                             xmlns="http://www.w3.org/2000/svg" viewBox="0 0 192 512"
                             data-testid="angle-left">
                            <path d="M25.1 247.5l117.8-116c4.7-4.7 12.3-4.7 17 0l7.1 7.1c4.7 4.7 4.7 12.3 0 17L64.7 256l102.2 100.4c4.7 4.7 4.7 12.3 0 17l-7.1 7.1c-4.7 4.7-12.3 4.7-17 0L25 264.5c-4.6-4.7-4.6-12.3.1-17z"></path>
                        </svg>
                    </button>
                </div>
            </div>
            <div class="table__wrap">
                <div class="table__info">
                    <strong class="table__title"><c:out value="${selectedYear}" /></strong>
                    <input type="hidden" id="selectedOrgId" name="selectedOrgId" />
                    <div class="table__btn-wrap">
                        <button type="button" class="btn table__btn" id="excelDownloadBtn">
                            <span class="table__btn-text">엑셀 다운로드</span>
                        </button>
                    </div>
                </div>
                <div class="table-cotent">
                    <table class="table">
                        <caption>업무명, 담당팀장, 담당인원, 가중치(%) 점수 - 담당인원, 점수 - 대표조정, 업무구분, 난이도 - 담당임원, 난이도 - 대표조정, 기여도 - 담당임원, 기여도 - 대표조정, 최종점수, 피드백 내용의 표</caption>
                        <colgroup>
                            <col style="width:25%;">
                            <col style="width:5%;">
                            <col style="width:5%;">
                            <col style="width:7%;">
                            <col style="width:6%;">
                            <col style="width:6%;">
                            <col style="width:7%;">
                            <col style="width:6%;">
                            <col style="width:6%;">
                            <col style="width:5%;">
                            <col style="width:5%;">
                            <col style="width:5%;">
                            <col>
                        </colgroup>
                        <thead>
                        <tr>
                            <th scope="col" rowspan="2">업무명</th>
                            <th scope="col" rowspan="2">담당<br>팀장</th>
                            <th scope="col" rowspan="2">담당<br>임원</th>
                            <th scope="col" rowspan="2">가중치<br>(%)</th>
                            <th scope="col" colspan="2">점수</th>
                            <th scope="col" rowspan="2">업무<br>구분</th>
                            <th scope="col" colspan="2">난이도</th>
                            <th scope="col" colspan="2">기여도</th>
                            <th scope="col" rowspan="2">최종<br>점수</th>
                            <th scope="col" rowspan="2">피드백</th>
                        </tr>
                        <tr>
                            <th scope="col">담당<br>임원</th>
                            <th scope="col">대표<br>조정</th>
                            <th scope="col">담당<br>임원</th>
                            <th scope="col">대표<br>조정</th>
                            <th scope="col">담당<br>임원</th>
                            <th scope="col">대표<br>조정</th>
                        </tr>
                        </thead>
                        <tbody class="resultTbody">
                        </tbody>
                    </table>
                </div>
                <div class="btn__wrap" id="btn--save--wrap">
                    <button type="button" class="btn btn--type02 btn--outline" id="btn--confirm">
                        <span class="btn__text">저장</span>
                    </button>
                    <c:if test="${isCeo}">
                        <button type="button" class="btn btn--type01" id="btn--fix">
                            <span class="btn__text">최종제출</span>
                        </button>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    <script>
        function getOrganizationChartJson() {
            $.ajax({
                type: 'GET',
                url: '/am/organizationchart',
                data: {
                    isEvaluationPage: true
                },
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
                        core: {
                            data: organizations
                        }
                    })
                    .bind('loaded.jstree', function (event, data) {
                        // 트리 로딩 완료 이벤트
                        if (${isCeo}) {
                            <!-- 대표이사, 관리자: 부서까지만 조직도 펼침 -->
                            const topLevelNode = $("#jstree").jstree("get_node", "#").children[0];
                            $('#jstree').jstree("open_node", topLevelNode);
                        } else if (${isOfficer}) {
                            <!-- 본부장: 전체 조직도 펼침 -->
                            $('#jstree').jstree('open_all');
                        }
                    })
                    .bind('select_node.jstree', function (event, data) {
                        // 노드 선택 이벤트
                    });
                },
                error: function (e) {
                    alert("[" + e.status + "]\n" + e.message);
                    if (e.status === 403) {
                        location.href = "/am/manager/login";
                    }
                },
            })
        }

        function displayTaskListToEvaluation(data) {
            const taskEvaluationList = data.taskEvaluationList;
            const existsTotal = data.existsTotal;
            const resultTbody = $('.resultTbody');
            resultTbody.empty();
            $('.no-result').remove();
            const selectedOrg = $('#jstree').jstree('get_selected', true);
            const hasDescendantOrg = selectedOrg[0].children.length > 0;    <!-- 부문급 이상 조직은 뷰잉만 -->

            if (taskEvaluationList.length > 0) {

                const weight = (100 / taskEvaluationList.length).toFixed(1);

                $.each(taskEvaluationList, function (index, taskEvaluationInfo) {
                    const tr = $('<tr></tr>');
                    const titleTd = $('<td>' +
                        '<span class="title">' + taskEvaluationInfo.taskTitle + '</span>' +
                        '<span class="sub-title">' + taskEvaluationInfo.projectTitle + ' | ' + taskEvaluationInfo.taskState + '</span>');
                    const chargeTeamTd = $('<td class="charge-team">' + taskEvaluationInfo.chargeTeam + '</td>');
                    const chargeOfficerTd = $('<td class="charge-officer">' + taskEvaluationInfo.chargeOfficer + '</td>');
                    const weightTd =
                        $('<td>' +
                        '<input type="text" class="table-input weight" title="가중치 %" value="' + (hasDescendantOrg || taskEvaluationInfo.state === "N" || taskEvaluationInfo.state === "F" ? taskEvaluationInfo.weight : weight) + '" ' +
                            (hasDescendantOrg || existsTotal ? "disabled " : "") +
                            'oninput="this.value = this.value.replace(/[^0-9.]/g, \'\'); if (parseInt(this.value) > 100) this.value = \'100\';">' +
                        '</td>');
                    const officerPoint =
                        $('<td>' +
                        '<input type="text" class="table-input officer-point" maxlength="3" title="점수 - 담당인원" value="' + taskEvaluationInfo.officerPoint + '" ' +
                            (hasDescendantOrg|| existsTotal  || !${isOfficer} ? "disabled " : "") +
                            'oninput="this.value = this.value.replace(/[^0-9.]/g, \'\'); if (parseInt(this.value) > 100) this.value = \'100\';">' +
                        '</td>');
                    const ceoPoint =
                        $('<td>' +
                        '<input type="text" class="table-input ceo-point"  maxlength="3" title="점수 - 대표조정" value="' + taskEvaluationInfo.ceoPoint + '" ' +
                            (hasDescendantOrg|| existsTotal  || !${isCeo} ? "disabled " : "") +
                            'oninput="this.value = this.value.replace(/[^0-9.]/g, \'\');  if (parseInt(this.value) > 100) this.value = \'100\';">' +
                        '</td>');
                    const taskGbTd =
                        $('<td>' +
                        '<select class="table-select task-gb" ' + (hasDescendantOrg || existsTotal  ? "disabled " : "") + 'title="업무구분">' +
                        '<option value="">선택</option>' +
                        '<option value="B" ' + (taskEvaluationInfo.taskGb === "B" ? "selected" : "") + '>기본루틴</option>' +
                        '<option value="S" ' + (taskEvaluationInfo.taskGb === "S" ? "selected" : "") + '>특별기획</option>' +
                        '</select>' +
                        '</td>');
                    const levelOfficerTd =
                        $('<td>' +
                        '<select class="table-select level-officer" ' + (hasDescendantOrg || existsTotal || !${isOfficer} ? "disabled " : "") +  'title="난이도 - 담당임원">' +
                            '<option value="" hidden>선택</option>' +
                            '<option value="110" ' +
                            (taskEvaluationInfo.taskGb === "S" ? "" : "hidden ") +
                            (taskEvaluationInfo.levelOfficer === "최상" ? "selected" : "") +
                            '>최상</option>' +
                            '<option value="105" ' +
                                (taskEvaluationInfo.taskGb === "S" ? "" : "hidden ") +
                                (taskEvaluationInfo.levelOfficer === "상" ? "selected" : "") +
                            '>상</option>' +
                            '<option value="100" ' +
                                (taskEvaluationInfo.taskGb === "S" ? "" : "hidden ") +
                                (taskEvaluationInfo.levelOfficer === "중상" ? "selected" : "") +
                            '>중상</option>' +
                            '<option value="95" ' +
                                (taskEvaluationInfo.levelOfficer === "중" ? "selected" : "") +
                            '>중</option>' +
                            '<option value="90" ' +
                                (taskEvaluationInfo.taskGb === "B" ? "" : "hidden ") +
                                (taskEvaluationInfo.levelOfficer === "중하" ? "selected" : "") +
                            '>중하</option>' +
                            '<option value="85" ' +
                                (taskEvaluationInfo.taskGb === "B" ? "" : "hidden ") +
                                (taskEvaluationInfo.levelOfficer === "하" ? "selected" : "") +
                            '>하</option>' +
                            '<option value="80" ' +
                            (taskEvaluationInfo.taskGb === "B" ? "" : "hidden ") +
                            (taskEvaluationInfo.levelOfficer === "최하" ? "selected" : "") +
                            '>최하</option>' +
                        '</select>' +
                        '</td>');
                    const levelCeoTd =
                        $('<td>' +
                        '<select class="table-select level-ceo" ' + (hasDescendantOrg || existsTotal || !${isCeo} ? "disabled " : "") +  ' title="난이도 - 대표조정">' +
                            '<option value="" hidden>선택</option>' +
                            '<option value="110" ' +
                            (taskEvaluationInfo.taskGb === "S" ? "" : "hidden ") +
                            (taskEvaluationInfo.levelCeo === "최상" ? "selected" : "") +
                            '>최상</option>' +
                            '<option value="105" ' +
                            (taskEvaluationInfo.taskGb === "S" ? "" : "hidden ") +
                            (taskEvaluationInfo.levelCeo === "상" ? "selected" : "") +
                            '>상</option>' +
                            '<option value="100" ' +
                            (taskEvaluationInfo.taskGb === "S" ? "" : "hidden ") +
                            (taskEvaluationInfo.levelCeo === "중상" ? "selected" : "") +
                            '>중상</option>' +
                            '<option value="95" ' +
                            (taskEvaluationInfo.levelCeo === "중" ? "selected" : "") +
                            '>중</option>' +
                            '<option value="90" ' +
                            (taskEvaluationInfo.taskGb === "B" ? "" : "hidden ") +
                            (taskEvaluationInfo.levelCeo === "중하" ? "selected" : "") +
                            '>중하</option>' +
                            '<option value="85" ' +
                            (taskEvaluationInfo.taskGb === "B" ? "" : "hidden ") +
                            (taskEvaluationInfo.levelCeo === "하" ? "selected" : "") +
                            '>하</option>' +
                            '<option value="80" ' +
                            (taskEvaluationInfo.taskGb === "B" ? "" : "hidden ") +
                            (taskEvaluationInfo.levelCeo === "최하" ? "selected" : "") +
                            '>최하</option>' +
                        '</select>' +
                        '</td>');
                    const condOfficerTd =
                        $('<td>' +
                        '<select class="table-select cond-officer" ' + (hasDescendantOrg || existsTotal || !${isOfficer} ? "disabled " : "") +  ' title="기여도 - 담당임원">' +
                            '<option value="" hidden>선택</option>' +
                            '<option value="120" ' +
                            (taskEvaluationInfo.condOfficer === "A" ? "selected" : "") +
                            '>A</option>' +
                            '<option value="110" ' +
                            (taskEvaluationInfo.condOfficer === "B" ? "selected" : "") +
                            '>B</option>' +
                            '<option value="100" ' +
                            (taskEvaluationInfo.condOfficer === "C" ? "selected" : "") +
                            '>C</option>' +
                            '<option value="90" ' +
                            (taskEvaluationInfo.condOfficer === "D" ? "selected" : "") +
                            '>D</option>' +
                            '<option value="80" ' +
                            (taskEvaluationInfo.condOfficer === "E" ? "selected" : "") +
                            '>E</option>' +
                        '</select>' +
                        '</td>');
                    const condCeoTd =
                        $('<td>' +
                        '<select class="table-select cond-ceo" ' + (hasDescendantOrg || existsTotal || !${isCeo} ? "disabled " : "") +  ' title="기여도 - 대표조정">' +
                            '<option value="" hidden>선택</option>' +
                            '<option value="120" ' +
                            (taskEvaluationInfo.condCeo === "A" ? "selected" : "") +
                            '>A</option>' +
                            '<option value="110" ' +
                            (taskEvaluationInfo.condCeo === "B" ? "selected" : "") +
                            '>B</option>' +
                            '<option value="100" ' +
                            (taskEvaluationInfo.condCeo === "C" ? "selected" : "") +
                            '>C</option>' +
                            '<option value="90" ' +
                            (taskEvaluationInfo.condCeo === "D" ? "selected" : "") +
                            '>D</option>' +
                            '<option value="80" ' +
                            (taskEvaluationInfo.condCeo === "E" ? "selected" : "") +
                            '>E</option>' +
                        '</select>' +
                        '</td>');
                    const totalPointTd =
                        $('<td class="total">' +
                        '<input type="text" class="table-input total-point" title="최종점수" readonly value="' +
                            (taskEvaluationInfo.totalPoint === 0 ? "" : taskEvaluationInfo.totalPoint) + '">' +
                        '</td>');
                    const feedbackTd =
                        $('<td>' +
                        '<textarea class="feedback" ' + (hasDescendantOrg || existsTotal ? "disabled " : "") +  ' title="피드백">' +
                            (taskEvaluationInfo.note === null ? "" : taskEvaluationInfo.note) + '</textarea>' +
                        '</td>');
                    const taskIdInput = $('<input class="task-id" type="hidden" ' + 'value="' +
                        taskEvaluationInfo.taskId + '">');
                    const chargeTeamIdInput = $('<input class="charge-team-id" type="hidden" ' + 'value="' +
                        taskEvaluationInfo.chargeTeamId + '">');
                    const stateInput = $('<input class="state" type="hidden" ' + 'value="' +
                        taskEvaluationInfo.state + '">');

                    tr.append(titleTd);
                    tr.append(chargeTeamTd);
                    tr.append(chargeOfficerTd);
                    tr.append(weightTd);
                    tr.append(officerPoint);
                    tr.append(ceoPoint);
                    tr.append(taskGbTd);
                    tr.append(levelOfficerTd);
                    tr.append(levelCeoTd);
                    tr.append(condOfficerTd);
                    tr.append(condCeoTd);
                    tr.append(totalPointTd);
                    tr.append(feedbackTd);
                    tr.append(taskIdInput);
                    tr.append(chargeTeamIdInput);
                    tr.append(stateInput);
                    resultTbody.append(tr);
                });
                if (!hasDescendantOrg) {
                    const tableTotalTr =
                        $('<tr class="table-total">' +
                            '<td>합계</td>' +
                            '<td></td>' +
                            '<td></td>' +
                            '<td>' +
                            '<input type="text" class="table-input sum-weight" title="가중치 % 합계" readonly value="">' +
                            '</td>' +
                            '<td></td><td></td><td></td><td></td><td></td><td></td><td></td>' +
                            '<td class="total__sum">' +
                            '<input type="text" class="table-input sum-total-point" title="최종점수 합계" readonly value="">' +
                            '</td>' +
                            '<td></td>' +
                            '</tr>');
                    resultTbody.append(tableTotalTr);
                    calculateSumTotalPoint();
                    calculateSumWeight();
                }
            } else {
                const tableContentDiv = $('.table-cotent');

                const noResultDiv = $('<!-- [D] 23.12.18 - 결과값 없을시에 노출 -->' +
                '<div class="no-result">' +
                    '<p class="no-result__text">해당년도-팀에 매핑된 업무가 없습니다.</p>' +
                '</div>' +
                '<!-- //[D] 23.12.18 - 결과값 없을시에 노출 -->');

                tableContentDiv.append(noResultDiv);
            }

            const btnSaveWrap = $('#btn--save--wrap');
            if (hasDescendantOrg) {
                if (!btnSaveWrap.hasClass('hidden')) {
                    btnSaveWrap.addClass('hidden');
                }
            } else {
                if (btnSaveWrap.hasClass('hidden')) {
                    btnSaveWrap.removeClass('hidden');
                }
            }
        }

        function getTaskListToEvaluation(year, orgId) {

            $.ajax({
                type: 'GET',
                url: '/am/tasks-evaluation/tasks',
                data: {
                    year: year,
                    chargeTeamId: orgId
                },
                success: function (data) {
                    displayTaskListToEvaluation(data);
                },
            });
        }

        <!-- 가중치 합계 산정 -->
        function calculateSumWeight() {
            let sumWeight = 0;
            $('.weight').each(function () {
                const weight = $(this).val();
                sumWeight += parseFloat(weight) || 0;
            });

            $('.sum-weight').val(sumWeight.toFixed(1) + "%");
        }

        function calculateSumTotalPoint() {
            let sumTotalPoint = 0;
            $('.total-point').each(function () {
                const totalPoint = $(this).val();
                sumTotalPoint += parseFloat(totalPoint) || 0;
            });

            $('.sum-total-point').val(Math.round(sumTotalPoint * 10) / 10);
        }

        function getLevel(taskGb) {
            const basicLevel = $(
                '<option value="" selected hidden>선택</option>' +
                '<option value="110" hidden>최상</option>' +
                '<option value="105" hidden>상</option>' +
                '<option value="100" hidden>중상</option>' +
                '<option value="95">중</option>' +
                '<option value="90">중하</option>' +
                '<option value="85">하</option>' +
                '<option value="80">최하</option>'
            );

            const specialLevel = $(
                '<option value="" selected hidden>선택</option>' +
                '<option value="110">최상</option>' +
                '<option value="105">상</option>' +
                '<option value="100">중상</option>' +
                '<option value="95">중</option>' +
                '<option value="90" hidden>중하</option>' +
                '<option value="85" hidden>하</option>' +
                '<option value="80" hidden>최하</option>'
            );

            const none = $('<option value="" selected hidden>선택</option>');

            if (taskGb === 'B') {
                return basicLevel;
            } else if (taskGb === 'S') {
                return specialLevel;
            } else {
                return none;
            }
        }

        function getCond(taskGb) {
            const cond = $(
                '<option value="" selected hidden>선택</option>' +
                '<option value="120">A</option>' +
                '<option value="110">B</option>' +
                '<option value="100">C</option>' +
                '<option value="90">D</option>' +
                '<option value="80">E</option>'
            );

            const none = $('<option value="" selected hidden>선택</option>');

            if (taskGb === 'B' || taskGb === 'S') {
                return cond;
            } else {
                return none;
            }
        }

        <!-- 해당 업무의 평가를 완료했는지 체크 -->
        function checkEvaluationCompletion(tr) {
            const weight = $(tr).find('.weight');
            const officerPoint = $(tr).find('.officer-point');
            const ceoPoint = $(tr).find('.ceo-point');
            const taskGb = $(tr).find('.task-gb');
            const levelOfficer = $(tr).find('.level-officer');
            const levelCeo = $(tr).find('.level-ceo');
            const condOfficer = $(tr).find('.cond-officer');
            const condCeo = $(tr).find('.cond-ceo');

            if (!weight.val()) {
                return false;
            } else if (!officerPoint.val() && !ceoPoint.val()) {
                return false;
            } else if (!taskGb.val()) {
                return false;
            } else if (!levelOfficer.val() && !levelCeo.val()) {
                return false;
            } else if (!condOfficer.val() && !condCeo.val()) {
                return false;
            } else {
                return true;
            }
        }

        <!-- 최종 점수 산정 -->
        function calculateTotalPoint(tr) {
            const weight = parseFloat($(tr).find('.weight').val()) / 100;
            const officerPoint = $(tr).find('.officer-point');
            const ceoPoint = $(tr).find('.ceo-point');
            const levelOfficer = $(tr).find('.level-officer');
            const levelCeo = $(tr).find('.level-ceo');
            const condOfficer = $(tr).find('.cond-officer');
            const condCeo = $(tr).find('.cond-ceo');
            let point;
            let level;
            let cond;

            <!-- 대표 조정 점수가 있다면 대표 조정 점수 사용 -->
            if (ceoPoint.val() && parseInt(ceoPoint.val()) > 0) {
                point = parseInt(ceoPoint.val());
            } else {
                point = parseInt(officerPoint.val());
            }

            if (levelCeo.val()) {
                level = parseFloat(levelCeo.val()) / 100;
            } else {
                level = parseFloat(levelOfficer.val()) / 100;
            }

            if (condCeo.val()) {
                cond = parseFloat(condCeo.val()) / 100;
            } else {
                cond = parseFloat(condOfficer.val()) / 100;
            }

            const totalPoint = point * weight * level * cond;
            tr.find('.total-point').val(Math.round(totalPoint * 10) / 10);
        }

        function getTaskEvaluationList() {
            let taskEvaluationList = [];

            $('.resultTbody').find('tr').each(function () {
                if (!$(this).hasClass('table-total')) {
                    const taskId = $(this).find('.task-id').val();
                    const chargeTeam = $(this).find('.charge-team').text();
                    const chargeOfficer = $(this).find('.charge-officer').text();
                    const chargeTeamId = $(this).find('.charge-team-id').val();
                    const subtitle = $(this).find('.sub-title').text();
                    const projectTitle = subtitle.split(' | ').shift();
                    const taskState = subtitle.split(' | ').pop();
                    const taskTitle = $(this).find('.title').text();
                    const weight = $(this).find('.weight').val();
                    const officerPoint = $(this).find('.officer-point').val();
                    const ceoPoint = $(this).find('.ceo-point').val();
                    const taskGb = $(this).find('.task-gb').val();
                    const levelOfficer = $(this).find('.level-officer option:selected').text();
                    const levelCeo = $(this).find('.level-ceo option:selected').text();
                    const condOfficer = $(this).find('.cond-officer option:selected').text();
                    const condCeo = $(this).find('.cond-ceo option:selected').text();
                    const totalPoint = $(this).find('.total-point').val();
                    const note = $(this).find('.feedback').val();
                    const state = $(this).find('.state').val();

                    const taskEvaluationInfo = {
                        taskId: taskId,
                        chargeTeam: chargeTeam,
                        chargeOfficer: chargeOfficer,
                        projectTitle: projectTitle,
                        taskTitle: taskTitle,
                        taskState: taskState,
                        weight: weight,
                        officerPoint: officerPoint,
                        ceoPoint: ceoPoint,
                        taskGb: taskGb,
                        levelOfficer: levelOfficer,
                        levelCeo: levelCeo,
                        condOfficer: condOfficer,
                        condCeo: condCeo,
                        totalPoint: totalPoint,
                        note: note,
                        chargeTeamId: chargeTeamId,
                        state: state
                    }

                    taskEvaluationList.push(taskEvaluationInfo);
                }
            });

            return taskEvaluationList;
        }

        function getTotalInfo() {
            const selectedOrg = $('#jstree').jstree('get_selected', true);
            const teamTitle = selectedOrg[0].text.replace('<span class=\'js-tree__text\'>', '').replace('</span>', '');
            const teamId = selectedOrg[0].id;
            const tr = $('.resultTbody').find('tr');
            const year = $('#select01').val();
            const teamName = tr.find('.charge-team').eq(0).text();
            const officerName = tr.find('.charge-officer').eq(0).text();
            const sumTotalPointVal = $('.resultTbody').find('.total__sum').find('.sum-total-point').val();
            const sumTotalPoint = Math.round(sumTotalPointVal * 10) / 10;

            const totalInfo = {
                year : year,
                teamId : teamId,
                teamTitle : teamTitle,
                teamName : teamName,
                officerName : officerName,
                totalPoint: sumTotalPoint
            }

            return totalInfo;
        }

        function saveTaskEvaluation(taskEvaluationList) {
            $.ajax({
                type: 'POST',
                url: '/am/tasks-evaluation',
                data: JSON.stringify(taskEvaluationList),
                contentType: 'application/json',
                success: function (data) {
                    alert(data);
                },
                error: function (e) {
                    alert("[" + e.status + "]\n" + e.message);
                    if (e.status === 403) {
                        location.href = "/am/manager/login";
                    }
                },
            })
        }

        function fianlSaveTaskEvaluation(finalTaskEvaluationInfo) {
            $.ajax({
                type: 'POST',
                url: '/am/tasks-evaluation/final',
                data: JSON.stringify(finalTaskEvaluationInfo),
                contentType: 'application/json',
                success: function (data) {
                    alert(data);
                },
                error: function (e) {
                    alert("[" + e.status + "]\n" + e.message);
                    if (e.status === 403) {
                        location.href = "/am/manager/login";
                    }
                },
            })
        }

        function checkSumWeight() {
            const sumWeight = parseFloat($('.sum-weight').val().replace('%', ''));
            return sumWeight <= 100;
        }

        function checkSumTotalPoint() {
            return $('.sum-total-point').val() > 0;
        }

        function isFixedEvaluation() {
            let result = false;
            $('.resultTbody').find('tr').each(function () {
                if ($(this).find('.state').val() === "F") {
                    result = true;
                    return result;
                }
            });
            return result;
        }

        function changeTableTitle(year, orgId, orgTitle) {
            $('.table__title').text(year + ' - ' + orgTitle);
            $('#selectedOrgId').val(orgId);
        }

        function clearResultTbody() {
            $('.resultTbody').empty();
            $('.no-result').remove();
        }

        $(document).on("click", ".jstree-anchor", function () {
            <!-- 가장 Depth가 낮은 조직 클릭 시 이벤트 발생 -->
            const orgId = $(this).attr('id').replace('_anchor', '');
            const orgTitle = $(this).text();
            const year = $('#select01').val();

            getTaskListToEvaluation(year, orgId);
            changeTableTitle(year, orgId, orgTitle);
        });

        $(document).on("input", ".weight", function () {
            calculateSumWeight();
        });

        $(document).on("input", ".total-point", function () {
            calculateSumTotalPoint();
        });

        <!-- 업무 구분에 따라 평가 점수 항목 다르게 노출 -->
        $(document).on("change", ".task-gb", function () {
            const taskGb = $(this).val();
            const level = getLevel(taskGb);
            const level2 = getLevel(taskGb);
            const cond = getCond(taskGb);
            const cond2 = getCond(taskGb);

            const levelOfficer = $(this).closest('tr').find('.level-officer');
            const levelCeo = $(this).closest('tr').find('.level-ceo');
            const condOfficer = $(this).closest('tr').find('.cond-officer');
            const condCeo = $(this).closest('tr').find('.cond-ceo');

            levelCeo.html(level);
            levelOfficer.html(level2);
            condCeo.html(cond);
            condOfficer.html(cond2);

            const tr = $(this).closest('tr');
            const totalPoint = tr.find('.total-point');
            totalPoint.val('');
            calculateSumTotalPoint();
        });

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

        <!-- 평가 시 실시간 최종 점수 산정 -->
        $(document).on('input change', 'tr :input', function () {
            const tr = $(this).closest('tr');
            if (checkEvaluationCompletion(tr)) {
                calculateTotalPoint(tr);
                calculateSumTotalPoint();
            }
        });

        $(document).ready(function () {
            getOrganizationChartJson();
            displayYear();

            $('#btn--confirm').on('click', function () {
                const boolean = isFixedEvaluation();
                if (boolean) {
                    alert("이미 최종 제출되었습니다.");
                    return;
                }

                if (!checkSumWeight()) {
                    alert("가중치 총 합은 100을 초과할 수 없습니다.");
                    return;
                }

                const taskEvaluationList = getTaskEvaluationList();
                saveTaskEvaluation(taskEvaluationList);
            });

            $('#btn--fix').on('click', function () {
                if (!checkSumWeight()) {
                    alert("가중치 총 합은 100을 초과할 수 없습니다.");
                    return;
                }

                if (!checkSumTotalPoint()) {
                    alert("최종 점수 총 합은 0일 수 없습니다." +
                        "\n적절한 최종 점수를 입력해주세요.");
                    return;
                }

                const taskEvaluationList = getTaskEvaluationList();
                const totalInfo = getTotalInfo();

                const finalTaskEvaluationInfo = {
                    taskEvaluationRequestDtoList : taskEvaluationList,
                    totalRequestDto : totalInfo
                }

                fianlSaveTaskEvaluation(finalTaskEvaluationInfo);
            });

            $("#select01").on("change", function () {
                $('#jstree').jstree('deselect_all');
                clearResultTbody();
                $('.table__title').text($('#select01').val());
            });

            $("#excelDownloadBtn").on("click", function () {
                const orgId = $('#selectedOrgId').val();
                const year = $('#select01').val();

                if (orgId === '') {
                    alert("평가 정보를 다운로드 할 조직을 선택해주세요.");
                    return;
                }

                if ($('.table-cotent').find('.no-result').length > 0) {
                    alert("다운로드할 데이터가 없습니다.");
                    return;
                }

                window.location.href = "/am/tasks-evaluation/excel-download?year=" + year + "&chargeTeamId=" + orgId;
            });
        });
    </script>
</body>
</html>