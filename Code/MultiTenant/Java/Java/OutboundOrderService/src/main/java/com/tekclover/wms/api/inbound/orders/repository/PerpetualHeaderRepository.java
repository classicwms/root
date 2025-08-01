package com.tekclover.wms.api.inbound.orders.repository;

import java.util.Optional;

import com.tekclover.wms.api.inbound.orders.model.cyclecount.perpetual.PerpetualHeader;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PerpetualHeaderRepository extends JpaRepository<PerpetualHeader, Long>,
        JpaSpecificationExecutor<PerpetualHeader>, StreamableJpaSpecificationRepository<PerpetualHeader> {

    public Optional<PerpetualHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, Long cycleCountTypeId, String cycleCountNo,
            Long movementTypeId, Long subMovementTypeId, long l);

    public PerpetualHeader findByCycleCountNo(String cycleCountNo);
}