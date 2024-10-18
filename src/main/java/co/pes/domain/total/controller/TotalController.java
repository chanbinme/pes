package co.pes.domain.total.controller;

import co.pes.common.SessionsUser;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.common.utils.ExcelUtil;
import co.pes.domain.member.model.Users;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.controller.dto.TotalRankingRequestDto;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.service.TotalService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author cbkim
 * @PackageName: co.pes.total.controller
 * @FileName : TotalController.java
 * @Date : 2023. 12. 21.
 * @프로그램 설명 : 평가 결과를 관리하는 Controller Class
 */
@Slf4j
@RestController
@RequestMapping("/am/totals")
@RequiredArgsConstructor
public class TotalController {

    private final TotalService totalService;

    /**
     * 평가 결과 페이지로 이동합니다.
     */
    @GetMapping("/ranking")
    public ModelAndView getRankingPage(HttpServletRequest request,
        @RequestParam(required = false, value = "selectedYear") String selectedYear) {
        ModelAndView mv = new ModelAndView();
        Users user = SessionsUser.getSessionUser(request.getSession());
        if (user.isAdminOrCeo()) {
            List<String> evaluationYearList = totalService.getEvaluationYearList();
            mv.addObject("yearList", evaluationYearList);
            mv.addObject("userInfo", user);
            if (selectedYear == null || selectedYear.isEmpty()) {
                selectedYear = String.valueOf(evaluationYearList.get(0));
            }
            mv.addObject("selectedYear", selectedYear);

            if (totalService.checkEndedYear(selectedYear)) {
                mv.setViewName("/ranking/ranking-result");
            } else {
                mv.setViewName("/ranking/ranking");
            }

            return mv;
        }

        throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
    }

    /**
     * 평가 결과를 조회합니다.
     *
     * @param year 평가 연도
     * @param totalRankingRequestDtoList 평가 결과 조회 요청 DTO 목록
     * @return 평가 결과 목록
     */
    @PostMapping("/{year}")
    public List<TotalRanking> getRankingTotalList(
        @PathVariable("year") String year,
        @RequestBody List<TotalRankingRequestDto> totalRankingRequestDtoList) {

        return totalService.findTotalListAndCalculateRanking(year, totalRankingRequestDtoList);
    }

    /**
     * 미리보기를 위한 평가 결과를 조회합니다.
     *
     * @param year 평가 연도
     * @param totalRankingRequestDtoList 평가 결과 조회 요청 DTO 목록
     * @return 평가 결과 목록
     */
    @PostMapping("/preview/{year}")
    public List<TotalRanking> getRankingTotalListForPreview(
        @PathVariable("year") String year,
        @RequestBody List<TotalRankingRequestDto> totalRankingRequestDtoList) {

        return totalService.findTotalListForPreview(year, totalRankingRequestDtoList);
    }

    /**
     * 총 평가 결과를 저장합니다.
     *
     * @param request 요청 객체
     * @param postTotalRankingRequestDtoList 총 평가 결과 목록
     * @return 저장 결과
     */
    @PostMapping("/ranking")
    public String postRankingTotalList(HttpServletRequest request,
        @RequestBody List<PostTotalRankingRequestDto> postTotalRankingRequestDtoList) {
        Users user = SessionsUser.getSessionUser(request.getSession());
        String userIp = request.getRemoteAddr();
        totalService.saveTotalRankingList(postTotalRankingRequestDtoList, user, userIp);

        return "저장되었습니다.";
    }

    /**
     * 주어진 연도의 평가 종료 처리 (연도 마감)
     *
     * @param request 요청 객체
     * @param year 평가 연도
     * @return 마감 결과
     */
    @PostMapping("/finish")
    public String endYear(HttpServletRequest request,
        @RequestParam(value = "year") String year) {
        Users user = SessionsUser.getSessionUser(request.getSession());
        String userIp = request.getRemoteAddr();
        if (user.isAdminOrCeo()) {
            totalService.endYear(year, user, userIp);
        } else {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }

        return "마감되었습니다.";
    }

    /**
     * 주어진 연도의 평가 마감 취소 처리
     *
     * @param request 요청 객체
     * @param year 평가 연도
     * @return 마감 취소 결과
     */
    @DeleteMapping("/finish")
    public String cancelEndYear(HttpServletRequest request,
        @RequestParam(value = "year") String year) {
        Users user = SessionsUser.getSessionUser(request.getSession());
        if (user.isAdminOrCeo()) {
            totalService.cancelEndYear(year);
        } else {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }

        return "마감 취소되었습니다.";
    }

    /**
     * 모든 평가가 완료되었는지 확인합니다.
     *
     * @param year 평가 연도
     * @return 평가 완료 여부
     */
    @GetMapping("/check")
    public boolean checkAllEvaluationsComplete(@RequestParam(value = "year") String year) {
        return totalService.checkAllEvaluationsComplete(year);
    }

    /**
     * 엑셀 다운로드
     *
     * @param year 평가 연도
     * @param totalRankingRequestDtoList 평가 결과 조회 요청 DTO 목록
     * @param response 응답 객체
     * @return 엑셀 파일
     */
    @PostMapping("/excel-download/{year}")
    public ResponseEntity<byte[]> excelDownloadRankingInfo(@PathVariable("year") String year,
                                        @RequestBody List<TotalRankingRequestDto> totalRankingRequestDtoList,
                                        HttpServletResponse response) {
        List<TotalRanking> totalRankingList = totalService.findTotalListForPreview(year, totalRankingRequestDtoList);
        byte[] data = ExcelUtil.excelDownload(response, totalRankingList);

        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
