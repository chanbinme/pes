<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
    <title>인사평가 - 매핑</title>
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
    <h2 class="header__title">업무-팀 매핑</h2>
    <%@ include file="/WEB-INF/jsp/include/top.jsp" %>
</header>
<div class="container">
    <div class="team-mapping">
        <div class="select__wrap">
            <strong class="select__title">년도별 프로젝트</strong>
            <div class="select-year__box">
                <select id="select01" class="select-year__item" name="year"></select>
            </div>
            <div class="resultContainer"></div>
        </div>
        <div class="table__wrap">
            <div class="table__info">
                <strong class="table__title"></strong>
                <div class="table__box">
                    <div class="table__btn-wrap">
                        <button type="button" class="btn table__btn" id="btn--delete">
                            <span class="table__btn-text">업무 삭제</span>
                        </button>
                        <button type="button" class="btn table__btn" id="btn--clear">
                            <span class="table__btn-text">매핑 초기화</span>
                        </button>
                    </div>
                    <div class="table__list">
                        <strong class="table__state-title">매핑 상태 :</strong>
                        <div class="input-item__group">
                            <div class="input-item">
                                <input type="radio" id="radio01" class="input-item__radio" name="radio" value="0" checked>
                                <label for="radio01"><span>전체</span></label>
                            </div>
                            <div class="input-item">
                                <input type="radio" id="radio02" class="input-item__radio" name="radio" value="1">
                                <label for="radio02"><span>대기</span></label>
                            </div>
                            <div class="input-item">
                                <input type="radio" id="radio03" class="input-item__radio" name="radio" value="2">
                                <label for="radio03"><span>완료</span></label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="table-cotent">
                <table class="table">
                    <caption>선택, 상태, 업무명, 진척도, 담당팀 내용의 표</caption>
                    <colgroup>
                        <col style="width:6%;">
                        <col style="width:6%;">
                        <col style="width:57%;">
                        <col style="width:9%;">
                        <col style="width:13%;">
                    </colgroup>
                    <thead>
                    <tr>
                        <th>
                            <div class="input-item">
                                <input type="checkbox" id="check-all" class="input-item__checkbox" name="checkboxAll">
                                <label for="check-all"><span class="blind">전체 선택</span></label>
                            </div>
                        </th>
                        <th scope="col">상태</th>
                        <th scope="col">업무명</th>
                        <th scope="col">진척도</th>
                        <th scope="col" class="team">담당 팀</th>
                    </tr>
                    </thead>
                    <tbody class="resultTbody">
                        <input type='hidden' class='new-charge-team-id' value=''>
                        <input type='hidden' class='new-charge-team-title' value=''>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="content__wrap">
            <div class="tree__wrap">
                <div id="jstree" class="js-tree"></div>
            </div>
            <div class="btn__wrap">
                <button class="btn btn--type01" id="btn--confirm" type="button">
                    <span class="btn__text">저장</span>
                </button>
            </div>
        </div>
    </div>
</div>
</body>
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
                        data: organizations
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
                if (e.status === 403) {
                    location.href = "/am/manager/login";
                }
                const error = JSON.parse(e.responseText);
                alert("[" + error.status + "]\n" + error.message);
            },
        })
    }

    <!-- 프로젝트 리스트 조회 -->
    function getProjectList(year) {
        $.ajax({
            type: 'GET',
            url: '/am/tasks/projects',
            data: {
                year: year
            },
            success: function (data) {
                displayProjectList(data);
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

    <!-- 프로젝트 리스트 노출 -->
    function displayProjectList(projectList) {
        const resultContainer = $('.resultContainer');
        resultContainer.empty(); // 기존 내용 비우기

        if (projectList.length > 0) {
            const selectBox = $("<div class='select__box'></div>");
            const selectList = $("<ul class='select__list'></ul>");

            $.each(projectList, function (index, projectInfo) {
                const listItem = $("<li class='select__item'></li>");
                const button = $("<button type='button' class='select__btn'></button>");

                button.append("<span class='select__text' id='projectTitle'>" + projectInfo.projectTitle + "</span>");

                button.on("click", function () {
                    $('.select__item').each(function () {
                        if ($(this).hasClass('is-active')) {
                            $(this).removeClass('is-active')
                        }
                    });
                    $(this).parent().addClass('is-active');
                    getTaskList(projectInfo);
                });

                listItem.append(button);
                selectList.append(listItem);
            });

            selectBox.append(selectList);
            resultContainer.append(selectBox);
        }
    }

    <!-- 업무 리스트 노출 -->
    function displayTaskList(taskList) {
        console.log(taskList);
        const resultTbody = $('.resultTbody');
        resultTbody.empty();
        const newChargeTeamId = $("<input type='hidden' class='new-charge-team-id' value=''>");
        const newChargeTeamTitle = $("<input type='hidden' class='new-charge-team-title' value=''>");
        resultTbody.append(newChargeTeamId);
        resultTbody.append(newChargeTeamTitle);

        if (taskList.length > 0) {
            $.each(taskList, function (index, taskInfo) {
                <!-- 담당 팀명 -->
                let chargeTeamTitles;
                if (taskInfo.chargeTeamTitles) {
                    chargeTeamTitles = taskInfo.chargeTeamTitles.join('<br>');
                } else {
                    chargeTeamTitles = '-';
                }

                <!-- 담당 팀 ID -->
                let chargeTeamIds;
                if (taskInfo.chargeTeamIds) {
                    chargeTeamIds = taskInfo.chargeTeamIds.join(',');
                } else {
                    chargeTeamIds = '';
                }

                <!-- 진행 중인 업무인지 체크 -->
                let trClass = '';
                if (taskInfo.topJob) {
                    trClass = ' sub';
                }

                let tr;
                switch (taskInfo.taskState) {
                    case "진행":
                        trClass = 'ing' + trClass;
                        break;
                    case "보류":
                        trClass = 'hold' + trClass;
                        break;
                    case "완료":
                        trClass = 'complete' + trClass;
                        break;
                    case "요청":
                        trClass = 'request' + trClass;
                        break;
                    case "피드백":
                        trClass = 'feedback' + trClass;
                        break;
                }

                tr = $("<tr class='" + trClass + "'></tr>");

                <!-- 업무 리스트 노출 -->
                const checkTd = $("<td></td>");
                const checkDiv = $("<div class='input-item'></div>");
                const checkBox = $("<input type='checkbox' id='" + "check" + index + "' class='input-item__checkbox' name='checkbox'>");
                const checkLabel = $("<label for='" + "check" + index + "'></label>");
                const checkSpan = $("<span class='blind'>선택</span>");
                const taskStateTd = $("<td class='state'><span class='state__text'>" + taskInfo.taskState + "</span></td>");
                let taskTitleTd;
                if (taskInfo.topJob) {
                    taskTitleTd = $("<td class='task-title sub__text'>" + taskInfo.taskTitle + "</td>");
                } else {
                    taskTitleTd = $("<td class='task-title'>" + taskInfo.taskTitle + "</td>");
                }
                const taskProgressTd = $("<td>" + taskInfo.taskProgress + "%" + "</td>");
                const chargeTeamTitleTd = $("<td class='charge-team-title'>" + chargeTeamTitles + "</td>");
                const chargeTeamIdsInput = $("<input type='hidden' class='current-charge-team-id' value='" + chargeTeamIds + "'>");
                const yearInput = $("<input type='hidden' class='year' value='" + taskInfo.year + "'>");
                const idInput = $('<input type="hidden" class="taskId" value="' + taskInfo.id + '">')

                checkLabel.append(checkSpan);
                checkDiv.append(checkBox);
                checkDiv.append(checkLabel);
                checkTd.append(checkDiv);
                tr.append(checkTd);
                tr.append(taskStateTd);
                tr.append(taskTitleTd);
                tr.append(taskProgressTd);
                tr.append(chargeTeamTitleTd);
                tr.append(chargeTeamIdsInput);
                tr.append(yearInput);
                tr.append(idInput)
                resultTbody.append(tr);
            });
        }
    }

    <!-- 업무 리스트 조회 -->
    function getTaskList(projectInfo) {
        const projectTitle = projectInfo.projectTitle;
        $('.table__title').text(projectTitle);
        let selectedYear = $('#select01').val();
        $.ajax({
            type: 'GET',
            url: '/am/tasks',
            data: {
                year: selectedYear,
                projectTitle: projectTitle
            },
            success: function (data) {
                displayTaskList(data);
                clearMatchingState();
                clearCheckbox();
                checkBox();
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

    function clearResultContainer() {
        $('.resultContainer').empty();
    }

    <!-- 담당 팀 매핑 -->
    function addChargeTeam(orgTitle, orgId) {
        const chargeTeamIds = $('.new-charge-team-id');
        let chargeTeamIdsValue;
        const chargeTeamTitles = $('.new-charge-team-title');
        let chargeTeamTitlesValue;

        if (chargeTeamIds.val()) {
            let chargeTeamIdArr = chargeTeamIds.val().split(',');
            if (!chargeTeamIdArr.includes(orgId)) {
                chargeTeamIdArr.push(orgId);
            }
            chargeTeamIdsValue = chargeTeamIdArr.join(',');
        } else {
            chargeTeamIdsValue = orgId;
        }
        chargeTeamIds.val(chargeTeamIdsValue);

        if (chargeTeamTitles.val()) {
            let chargeTeamTitlesArr = chargeTeamTitles.val().split(',');
            if (!chargeTeamTitlesArr.includes(orgTitle)) {
                chargeTeamTitlesArr.push(orgTitle);
            }
            chargeTeamTitlesValue = chargeTeamTitlesArr.join(',');
        } else {
            chargeTeamTitlesValue = orgTitle;
        }
        chargeTeamTitles.val(chargeTeamTitlesValue);
    }

    function removeChargeTeam(orgTitle, orgId) {
        const chargeTeamIds = $('.new-charge-team-id');
        let chargeTeamIdsValue;
        const chargeTeamTitles = $('.new-charge-team-title');
        let chargeTeamTitlesValue;

        if (chargeTeamIds.val()) {
            let chargeTeamIdArr = chargeTeamIds.val().split(',');
            if (chargeTeamIdArr.includes(orgId)) {
                chargeTeamIdArr = chargeTeamIdArr.filter((element) => element !== orgId);
            }
            chargeTeamIdsValue = chargeTeamIdArr.join(',');
        }
        chargeTeamIds.val(chargeTeamIdsValue);

        if (chargeTeamTitles.val()) {
            let chargeTeamTitlesArr = chargeTeamTitles.val().split(',');
            if (chargeTeamTitlesArr.includes(orgTitle)) {
                chargeTeamTitlesArr = chargeTeamTitlesArr.filter((element) => element !== orgTitle);
            }
            chargeTeamTitlesValue = chargeTeamTitlesArr.join(',');
        }
        chargeTeamTitles.val(chargeTeamTitlesValue);
    }

    function clearCheckbox() {
        $('.new-charge-team-id').val('');
        $('.new-charge-team-title').val('');
        $('#jstree').jstree(true).deselect_all();
        $('input[name="checkboxAll"]:checked').prop('checked', false);
        $('input[name="checkbox"]:checked').prop('checked', false);
        $('.table tbody tr').each(function () {
            if ($(this).hasClass('checked')) {
                $(this).removeClass('checked');
            }
        });
    }

    <!-- 업무 - 팀 매핑 -->
    function mapping() {
        let mappingDataList = [];
        const chargeTeamIds = $('.new-charge-team-id').val().split(',');
        const checkedJobCount = $('input[name="checkbox"]:checked').length;

        if (checkedJobCount === 0) {
            alert("선택된 업무가 없습니다.");
            return;
        }

        if (!$('.new-charge-team-id').val()) {
            alert("변경된 내용이 없습니다.");
            return;
        }

        $('.table tbody tr').each(function () {
            if ($(this).find('input[name=checkbox]').prop('checked') && chargeTeamIds.length > 0) {
                const taskId = $(this).find('.taskId').val();

                $.each(chargeTeamIds, function (idx, chargeTeamId) {
                    const mappingData = {
                        chargeTeamId: chargeTeamId,
                        taskId : taskId,
                    }
                    mappingDataList.push(mappingData);
                });
            }
        });

        $.ajax({
            type: 'POST',
            url: '/am/tasks/mappings',
            data: JSON.stringify(mappingDataList),
            contentType: 'application/json',
            success: function (data) {
                alert(data);
                $('.table tbody tr').each(function () {
                    if ($(this).find('input[name=checkbox]').prop('checked')) {
                        if ($('.new-charge-team-title').val()) {
                            $(this).find('.charge-team-title').html(
                                $('.new-charge-team-title').val().replaceAll(',', '<br>'));
                            $(this).find('.current-charge-team-id').val(
                                $('.new-charge-team-id').val());
                        } else {
                            $(this).find('.charge-team-title').text('-');
                        }
                    }
                });
                clearCheckbox();
            },
            error: function (e) {
                if (e.status === 403) {
                    location.href = "/am/manager/login";
                }
                const error = JSON.parse(e.responseText);
                alert("[" + error.status + "]\n" + error.message);
            }
        });
    }

    <!-- 기존 매핑 정보 삭제 -->
    function clearMappingInfo() {
        let clearMappingDataList = [];

        const checkedJobCount = $('input[name="checkbox"]:checked').length;

        if (checkedJobCount === 0) {
            alert("선택된 업무가 없습니다.");
            return;
        }

        $('.table tbody tr').each(function () {
            if ($(this).find('input[name=checkbox]').prop('checked') && $(this).find('.current-charge-team-id').val()) {
                const chargeTeamIds = $(this).find('.current-charge-team-id').val().split(',');
                const taskId = $(this).find('.taskId').val();

                $.each(chargeTeamIds, function (idx, chargeTeamId) {
                    const mappingData = {
                        chargeTeamId: chargeTeamId,
                        taskId : taskId,
                    }
                    clearMappingDataList.push(mappingData);
                });
            }
        });

        $.ajax({
            type: 'DELETE',
            url: '/am/tasks/mappings',
            data: JSON.stringify(clearMappingDataList),
            contentType: 'application/json',
            success: function (data) {
                alert(data);
                $('.table tbody tr').each(function () {
                    if ($(this).find('input[name=checkbox]').prop('checked') && $(this).find(
                        '.current-charge-team-id').val()) {
                        $(this).find('.charge-team-title').text('-');
                        $(this).find('.current-charge-team-id').val('');
                    }
                });
                clearCheckbox();
                $('#radio01').prop('checked', true);
            },
            error: function (e) {
                if (e.status === 403) {
                    location.href = "/am/manager/login";
                }
                const error = JSON.parse(e.responseText);
                alert("[" + error.status + "]\n" + error.message);
            }
        });
    }

    <!-- 업무 매칭 상태 기반 필터링 -->
    function jobListFiltering(filterType) {
        if (filterType === "0") {   <!-- 전체 -->
            $('.table tbody tr').each(function () {
                if ($(this).hasClass('hidden')) {
                    $(this).removeClass('hidden');
                }
            });
        } else if (filterType === "1") {    <!-- 대기 -->
            $('.table tbody tr').each(function () {
                if ($(this).hasClass('hidden')) {
                    $(this).removeClass('hidden');
                }
                if ($(this).find('.charge-team-title').text() !== "-") {
                    $(this).addClass('hidden');
                }
            });
        } else if (filterType === "2") {    <!-- 완료 -->
            $('.table tbody tr').each(function () {
                if ($(this).hasClass('hidden')) {
                    $(this).removeClass('hidden');
                }
                if ($(this).find('.charge-team-title').text() === "-") {
                    $(this).addClass('hidden');
                }
            });
        }
        clearCheckbox();
    }

    function clearMatchingState() {
        $('input[name="radio"]:checked').prop('checked', false);
        $('#radio01').prop('checked', true);
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

    function deleteJobInfo() {
        const checkedJobCount = $('input[name="checkbox"]:checked').length;
        let deleteJobInfoList = [];
        let mappingYn = 'N';

        if (checkedJobCount === 0) {
            alert("선택된 업무가 없습니다.");
            return;
        }

        $('.table tbody tr').each(function () {
            if ($(this).find('input[name=checkbox]').prop('checked')) {
                if ($(this).find('.current-charge-team-id').val()) {
                    mappingYn = 'Y';
                } else {
                    const taskId = $(this).find('.taskId').val();
                    const jobInfo = {
                        taskId : taskId,
                    }
                    deleteJobInfoList.push(jobInfo);
                }
            }
        });

        if (mappingYn === 'Y') {
            alert('매핑된 업무가 있습니다.\n해당 업무의 매핑 초기화 후 업무 삭제 가능합니다.');
            return;
        }

        $.ajax({
            type: 'DELETE',
            url: '/am/tasks/' + $('#select01').val(),
            data: JSON.stringify(deleteJobInfoList),
            contentType: 'application/json',
            success: function (data) {
                alert(data);
                $('.table tbody tr').each(function () {
                    if ($(this).find('input[name=checkbox]').prop('checked')) {
                        $(this).remove();
                    }
                });
                clearCheckbox();
                $('#radio01').prop('checked', true);
            },
            error: function (e) {
                if (e.status === 403) {
                    location.href = "/am/manager/login";
                }
                const error = JSON.parse(e.responseText);
                alert("[" + error.status + "]\n" + error.message);
            }
        });
    }

    $(document).on("click", ".jstree-anchor", function () {
        <!-- 가장 Depth가 낮은 조직 클릭 시 이벤트 발생 -->
        const orgTitle = $(this).text();
        const orgId = $(this).attr('id').replace('_anchor', '');
        if ($(this).parent().hasClass('jstree-leaf') && $(this).hasClass('jstree-clicked')) {
            addChargeTeam(orgTitle, orgId);
        } else if ($(this).parent().hasClass('jstree-leaf')) {
            removeChargeTeam(orgTitle, orgId);
        }
    });

    $(document).ready(function () {
        getOrganizationChartJson();
        displayYear();
        getProjectList($('#select01').val());

        $("#select01").on("change", function () {
            if ($(this).val() !== "") {
                getProjectList($(this).val());
            } else {
                clearResultContainer();
            }
        });

        $("#btn--confirm").on("click", function () {
            mapping();
        });

        $("#btn--clear").on("click", function () {
            if (confirm("초기화 시, 선택한 담당팀 모두 삭제됩니다.\n삭제하고 초기화 하시겠습니까?")) {
                clearMappingInfo();
            }
        });

        $(".input-item__radio").on("change", function () {
            const radio = $('input[name="radio"]:checked');
            const filterType = radio.val();
            jobListFiltering(filterType);
        });

        $("#btn--delete").on("click", function () {
            if (confirm("삭제 시, 선택한 업무는 모두 삭제됩니다.\n선택한 업무를 모두 삭제하시겠습니까?")) {
                deleteJobInfo();
            }
        });
    });
</script>
</html>