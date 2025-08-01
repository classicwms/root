package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic.v2.PeriodicLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface PeriodicLineV2Repository extends JpaRepository<PeriodicLineV2, Long>,
        JpaSpecificationExecutor<PeriodicLineV2>, StreamableJpaSpecificationRepository<PeriodicLineV2> {

    //	SELECT ITM_CODE FROM tblperiodicline
//	WHERE WH_ID = 110 AND ITM_CODE IN :itemCode AND STATUS_ID <> 70
    @Query(value = "SELECT ITM_CODE FROM tblperiodicline \r\n"
            + "WHERE WH_ID = 110 AND ITM_CODE IN :itemCode \r\n"
            + "AND STATUS_ID <> 70 AND IS_DELETED = 0", nativeQuery = true)
    public List<String> findByStatusIdNotIn70(@Param(value = "itemCode") Set<String> itemCode);

    PeriodicLineV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String cycleCountNo,
            String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);

    List<PeriodicLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String cycleCountNo, Long deletionIndicator);

    @Query(value = "SELECT * from tblperiodicline WHERE " +
            "C_ID = :companyCode AND PLANT_ID = :plantId AND LANG_ID = :languageId " +
            "AND WH_ID = :warehouseId AND CC_NO = :cycleCountNo AND IS_DELETED = 0", nativeQuery = true)
    List<PeriodicLineV2> getPeriodicLines(@Param("companyCode") String companyCode,
                                          @Param("plantId") String plantId,
                                          @Param("languageId") String languageId,
                                          @Param("warehouseId") String warehouseId,
                                          @Param("cycleCountNo") String cycleCountNo);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PeriodicLineV2 pl \r\n"
            + " SET pl.cycleCounterId = :cycleCounterId, pl.cycleCounterName = :cycleCounterName, pl.statusId = :statusId, pl.statusDescription = :statusDescription,  \r\n"
            + " pl.countedBy = :countedBy, pl.countedOn = :countedOn "
            + " WHERE pl.companyCode = :companyCode AND pl.plantId = :plantId AND pl.languageId = :languageId AND pl.warehouseId = :warehouseId AND \r\n "
            + " pl.cycleCountNo = :cycleCountNo AND pl.storageBin in :storageBin AND pl.itemCode = :itemCode AND pl.packBarcodes = :packBarcodes"
            + " AND deletionIndicator = 0")
    public void updateHHTUser(
            @Param("cycleCounterId") String cycleCounterId,
            @Param("cycleCounterName") String cycleCounterName,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("countedBy") String countedBy,
            @Param("countedOn") Date countedOn,
            @Param("companyCode") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("cycleCountNo") String cycleCountNo,
            @Param("storageBin") String storageBin,
            @Param("itemCode") String itemCode,
            @Param("packBarcodes") String packBarcodes);

//    @Modifying
//    @Query(value = "UPDATE tblperiodicline  SET COUNTER_ID = :cycleCounterId, COUNTER_NM = :cycleCounterName, " +
//            "STATUS_ID = :statusId, STATUS_TEXT = :statusDescription, CC_CNT_BY = :countedBy, CC_CNT_ON = :countedOn " +
//            "WHERE C_ID = :companyCode AND ant_id = :antId AND lang_id = :languageId AND wh_id = :warehouseId " +
//            "AND CC_NO = :cycleCountNo AND ST_BIN = :storageBin AND ITM_CODE = :itemCode AND PACK_BARCODE = :packBarcodes " +
//            "AND IS_DELETED = 0", nativeQuery = true)
//    public void updateHHTUserV6(@Param("cycleCounterId") String cycleCounterId,
//                                @Param("cycleCounterName") String cycleCounterName,
//                                @Param("statusId") Long statusId,
//                                @Param("statusDescription") String statusDescription,
//                                @Param("countedBy") String countedBy,
//                                @Param("countedOn") Date countedOn,
//                                @Param("companyCode") String companyCode,
//                                @Param("plantId") String plantId,
//                                @Param("languageId") String languageId,
//                                @Param("warehouseId") String warehouseId,
//                                @Param("cycleCountNo") String cycleCountNo,
//                                @Param("storageBin") String storageBin,
//                                @Param("itemCode") String itemCode,
//                                @Param("packBarcodes") String packBarcodes);

    List<PeriodicLineV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStatusIdInAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String cycleCountNo, List<Long> statusId, Long deletionIndicator);

    PeriodicLineV2 findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndManufacturerNameAndPackBarcodesAndBarcodeIdAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String cycleCountNo, String storageBin,
            String itemCode, String manufacturerName, String packBarcodes, String barcodeId, Long deletionIndicator);

    @Query(value = "SELECT * from tblperiodicline WHERE C_ID = :companyCode " +
            "AND PLANT_ID = :plantId AND lang_id = :languageId AND wh_id = :warehouseId " +
            "AND cc_no = :cycleCountNo AND st_bin = :storageBin AND itm_code = :itemCode " +
            "AND MFR_NAME = :manufacturerName AND pack_barcode = :packBarcodes " +
            "AND PARTNER_ITEM_BARCODE = :barcodeId AND IS_DELETED = 0", nativeQuery = true)
    PeriodicLineV2 getPeriodicLine(@Param("companyCode") String companyCode,
                                   @Param("plantId") String plantId,
                                   @Param("languageId") String languageId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("cycleCountNo") String cycleCountNo,
                                   @Param("storageBin") String storageBin,
                                   @Param("itemCode") String itemCode,
                                   @Param("manufacturerName") String manufacturerName,
                                   @Param("packBarcodes") String packBarcodes,
                                   @Param("barcodeId") String barcodeId);
}