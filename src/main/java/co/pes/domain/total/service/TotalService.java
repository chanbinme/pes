package co.pes.domain.total.service;

import co.pes.domain.member.model.Users;
import co.pes.domain.member.repository.MybatisMemberInfoRepository;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.evaluation.controller.dto.TotalRequestDto;
import co.pes.domain.task.model.Mapping;
import co.pes.domain.total.controller.dto.PostTotalRankingRequestDto;
import co.pes.domain.total.model.EndYear;
import co.pes.domain.total.model.OfficerTeamInfo;
import co.pes.domain.total.model.TotalRanking;
import co.pes.domain.total.controller.dto.TotalRankingRequestDto;
import co.pes.domain.total.mapper.TotalMapper;
import co.pes.domain.total.model.Total;
import co.pes.domain.total.repository.TotalRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author cbkim
 * @PackageName: co.pes.evaluation.service
 * @FileName : TotalService.java
 * @Date : 2023. 12. 20.
 * @프로그램 설명 : 총 평가 결과를 관리합니다.
 */
public interface TotalService {

    void saveTotal(TotalRequestDto totalRequestDto, Users user, String userIp);

    List<TotalRanking> findTotalListAndCalculateRanking(String year, List<TotalRankingRequestDto> totalRankingRequestDtoList);

    List<TotalRanking> findTotalListForPreview(String year, List<TotalRankingRequestDto> totalRankingRequestDtoList);

    /**
     * 총 평가 결과를 저장합니다.
     *
     * @param postTotalRankingRequestDtoList 총 평가 결과 목록
     * @param user 총 평가 결과를 저장한 사용자
     * @param userIp 사용자의 IP 주소
     */
    void saveTotalRankingList(List<PostTotalRankingRequestDto> postTotalRankingRequestDtoList, Users user, String userIp);

    /**
     * 주어진 연도의 평가 종료 처리 (연도 마감)
     *
     * @param year 평가 연도
     * @param user 연도 마감을 요청한 사용자
     * @param userIp 사용자의 IP 주소
     */
    void endYear(String year, Users user, String userIp);

    /**
     * 주어진 연도의 평가 종료 취소 처리 (연도 마감 취소)
     *
     * @param year 평가 연도
     */
    void cancelEndYear(String year);

    boolean checkEndedYear(String year);

    boolean existsTotal(Mapping mapping);

    boolean checkAllEvaluationsComplete(String year);

    List<String> getEvaluationYearList();

    default boolean existsByYearAndOrganizationId(String year, Long chargeTeamId) {
        throw new BusinessLogicException(ExceptionCode.NOT_IMPLEMENTED);
    }
}