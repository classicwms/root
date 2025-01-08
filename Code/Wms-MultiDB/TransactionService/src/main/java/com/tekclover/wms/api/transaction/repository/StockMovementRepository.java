package com.tekclover.wms.api.transaction.repository;


import com.tekclover.wms.api.transaction.model.threepl.stockmovement.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StockMovementRepository extends JpaRepository<StockMovement, Long>, JpaSpecificationExecutor<StockMovement> {


    List<StockMovement> findByMovementDocNoAndLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
            Long movementDocNo, String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, Long deletionIndicator);

    Optional<StockMovement> findByLanguageIdAndMovementDocNoAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
             String languageId, Long movementDocNo, String companyCodeId, String plantId, String warehouseId, String itemCode, Long deletionIndicator);


    StockMovement findByCompanyCodeIdAndLanguageIdAndMovementDocNoAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
            String companyCodeId, String languageId, Long movementDocNo, String plantId, String warehouseId, String itemCode, Long deletionIndicator);

    List<StockMovement> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, String itemCode, String refDocNumber, Long deletionIndicator);

}
