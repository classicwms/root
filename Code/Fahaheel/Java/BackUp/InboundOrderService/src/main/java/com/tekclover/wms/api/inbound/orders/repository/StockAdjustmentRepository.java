package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.cyclecount.stockadjustment.StockAdjustment;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, String>,
        JpaSpecificationExecutor<StockAdjustment>, StreamableJpaSpecificationRepository<StockAdjustment> {


    StockAdjustment findByLanguageIdAndCompanyCodeAndBranchCodeAndWarehouseIdAndItemCodeAndManufacturerNameAndStorageBinAndStockAdjustmentIdAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
            String manufacturerName, String storageBin, Long stockAdjustmentId, Long deletionIndicator);

    List<StockAdjustment> findByLanguageIdAndCompanyCodeAndBranchCodeAndWarehouseIdAndItemCodeAndManufacturerNameAndStorageBinAndStockAdjustmentKeyAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String itemCode, String manufacturerName, String storageBin, Long stockAdjustmentKey, Long deletionIndicator);

    List<StockAdjustment> findByLanguageIdAndCompanyCodeAndBranchCodeAndWarehouseIdAndItemCodeAndManufacturerNameAndStockAdjustmentKeyAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String itemCode, String manufacturerName, Long stockAdjustmentKey, Long deletionIndicator);

    StockAdjustment findByLanguageIdAndCompanyCodeAndBranchCodeAndWarehouseIdAndItemCodeAndManufacturerNameAndStockAdjustmentKeyAndStockAdjustmentIdAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
            String manufacturerName, Long stockAdjustmentKey, Long stockAdjustmentId, Long deletionIndicator);
}