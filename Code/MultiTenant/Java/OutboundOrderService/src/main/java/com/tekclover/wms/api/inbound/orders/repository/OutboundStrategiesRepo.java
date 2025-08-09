package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.inbound.strategies.OutboundStrategies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface OutboundStrategiesRepo extends JpaRepository<OutboundStrategies, Long> {

    @Query(value = "SELECT * FROM tbloutboundstrategies WHERE IS_DELETED = 0 AND " +
            "(COALESCE(:companyId, null) IS NULL OR (C_ID IN (:companyId))) AND " +
            "(COALESCE(:plantId, null) IS NULL OR (PLANT_ID IN (:plantId))) AND " +
            "(COALESCE(:warehouseId, null) IS NULL OR (WH_ID IN (:warehouseId))) AND " +
            "(COALESCE(:outboundStrategiesId, null) IS NULL OR (OUT_STRATEGIES_ID IN (:outboundStrategiesId))) AND " +
            "(COALESCE(:inventoryId, null) IS NULL OR (INV_ID IN (:inventoryId))) AND " +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) AND " +
            "(COALESCE(:palletCode, null) IS NULL OR (PAL_CODE IN (:palletCode))) AND " +
            "(COALESCE(:refDocNo, null) IS NULL OR (REF_ORD_NO IN (:refDocNo))) AND " +
            "(COALESCE(:sequenceNo, null) IS NULL OR (SEQ_NO IN (:sequenceNo))) ", nativeQuery = true)
    public List<OutboundStrategies> findOutboundStrategies(@Param("companyId") String companyId,
                                                           @Param("plantId") String plantId,
                                                           @Param("warehouseId") String warehouseId,
                                                           @Param("outboundStrategiesId") Long outboundStrategiesId,
                                                           @Param("inventoryId") Long inventoryId,
                                                           @Param("itemCode") String itemCode,
                                                           @Param("palletCode") String palletCode,
                                                           @Param("refDocNo") String refDocNo,
                                                           @Param("sequenceNo") Long sequenceNo);

    public OutboundStrategies findTopByCompanyCodeIdAndLanguageIdAndPlantIdAndInventoryIdAndItemCodeAndBarcodeIdAndDeletionIndicatorOrderByOutboundStrategiesIdDesc(
            String companyId, String languageId, String plantId, Long inventoryId, String itemCode, String barcodeId, Long deletionIndicator);



}
