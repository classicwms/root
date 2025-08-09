package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import com.tekclover.wms.api.inbound.orders.model.cyclecount.periodic.v2.PeriodicZeroStockLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PeriodicZeroStkLineRepository extends JpaRepository<PeriodicZeroStockLine, Long>,
        JpaSpecificationExecutor<PeriodicZeroStockLine>, StreamableJpaSpecificationRepository<PeriodicZeroStockLine> {


    List<PeriodicZeroStockLine> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, Long deletionIndicator);
}