package co.pes.domain.task.service;

import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;
import co.pes.domain.task.entity.TaskOrganizationMappingEntity;
import co.pes.domain.task.model.Mapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
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
    protected void findAndDesignateChargePerson(Mapping mappingInfo, Long chargeTeamId) {
        throw new BusinessLogicException(ExceptionCode.NOT_IMPLEMENTED);
    }
}
