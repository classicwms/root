package com.tekclover.wms.api.transaction.repository;


import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.threepl.stockmovement.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
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

    List<StockMovement> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceOrderNoAndDeletionIndicator(
            String companyCodeId, String languageId, String plantId, String warehouseId, String itemCode, String referenceOrderNo, Long deletionIndicator);

    @Query(value = "select SUM(TOTAL_TPL_CBM) totalCbm, sum(RATE) totalRate, min(currency) currency from tblstockmovement where c_id = :companyId " +
            "AND plant_id = :plantId and wh_id = :warehouseId " +
            "AND partner_code = :partnerCode And (PROFORMA_INVOICE_NO is null or PROFORMA_INVOICE_NO =0) and is_deleted = 0 and BIN_CL_ID = 1 " +
            "AND (:startCreatedOn IS NULL OR CTD_ON >= :startCreatedOn) " +
            "AND (:endCreatedOn IS NULL OR CTD_ON <= :endCreatedOn)",
            nativeQuery = true)
    IKeyValuePair getCbm(@Param("companyId") String companyId,
                         @Param("plantId") String plantId,
                         @Param("warehouseId") String warehouseId,
                         @Param("partnerCode") String partnerCode,
                         @Param("startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
                         @Param("endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn);
}
