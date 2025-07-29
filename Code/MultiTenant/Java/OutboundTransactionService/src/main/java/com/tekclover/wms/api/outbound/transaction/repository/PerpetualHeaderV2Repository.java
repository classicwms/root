package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.v2.PerpetualHeaderV2;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PerpetualHeaderV2Repository extends JpaRepository<PerpetualHeaderV2, Long>,
        JpaSpecificationExecutor<PerpetualHeaderV2>, StreamableJpaSpecificationRepository<PerpetualHeaderV2> {

    PerpetualHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNo(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo);

    Optional<PerpetualHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            Long cycleCountTypeId, String cycleCountNo, Long movementTypeId, Long subMovementTypeId, Long deletionIndicator);

    Optional<PerpetualHeaderV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, Long deletionIndicator);
}