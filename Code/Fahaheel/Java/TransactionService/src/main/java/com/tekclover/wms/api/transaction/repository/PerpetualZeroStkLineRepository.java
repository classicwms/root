package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.v2.PerpetualZeroStockLine;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PerpetualZeroStkLineRepository extends JpaRepository<PerpetualZeroStockLine, Long>,
        JpaSpecificationExecutor<PerpetualZeroStockLine>, StreamableJpaSpecificationRepository<PerpetualZeroStockLine> {


    List<PerpetualZeroStockLine> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, Long deletionIndicator);

    PerpetualZeroStockLine findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String cycleCountNo, String itemCode, String manufacturerName, Long deletionIndicator);
}