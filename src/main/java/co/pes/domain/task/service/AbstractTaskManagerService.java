package co.pes.domain.task.service;

import co.pes.domain.task.model.Mapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public abstract class AbstractTaskManagerService implements TaskManagerService {

    /**
     * 업무 담당 Manager, Officer를 조회하여 지정합니다.
     *
     * @param mappingInfo 매핑 정보
     * @param chargeTeamId 담당 팀 ID
     */
    protected abstract void findAndDesignateChargePerson(Mapping mappingInfo, Long chargeTeamId);

    /**
     * 기존 매핑 정보 초기화합니다. 최종 평가 완료된 팀이라면 초기화 불가합니다.
     *
     * @param mappingInfo 매핑 정보
     */
    protected abstract void resetMapping(Mapping mappingInfo);
}
