package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.cyclecount.perpetual.v2.PerpetualLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface PerpetualLineV2Repository extends JpaRepository<PerpetualLineV2, Long>,
        JpaSpecificationExecutor<PerpetualLineV2>, StreamableJpaSpecificationRepository<PerpetualLineV2> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PerpetualLineV2 pl \r\n"
            + " SET pl.cycleCounterId = :cycleCounterId, pl.cycleCounterName = :cycleCounterName, pl.statusId = :statusId, pl.statusDescription = :statusDescription,  \r\n"
            + " pl.countedBy = :countedBy, pl.countedOn = :countedOn "
            + " WHERE pl.companyCodeId = :companyCodeId AND pl.plantId = :plantId AND pl.languageId = :languageId AND pl.warehouseId = :warehouseId AND \r\n "
            + " pl.cycleCountNo = :cycleCountNo AND pl.storageBin in :storageBin AND pl.itemCode = :itemCode AND pl.packBarcodes = :packBarcodes"
            + " AND deletionIndicator = 0")
    public void updateHHTUser(
            @Param("cycleCounterId") String cycleCounterId,
            @Param("cycleCounterName") String cycleCounterName,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("countedBy") String countedBy,
            @Param("countedOn") Date countedOn,
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("cycleCountNo") String cycleCountNo,
            @Param("storageBin") String storageBin,
            @Param("itemCode") String itemCode,
            @Param("packBarcodes") String packBarcodes);

    PerpetualLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String cycleCountNo, String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);

    PerpetualLineV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndManufacturerNameAndPackBarcodesAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo,
            String storageBin, String itemCode, String manufacturerName, String packBarcodes, Long deletionIndicator);

    List<PerpetualLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, Long deletionIndicator);

    List<PerpetualLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndCycleCounterIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, List<String> cycleCounterId, Long deletionIndicator);

    List<PerpetualLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);

    List<PerpetualLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, Long statusId, long l);

    List<PerpetualLineV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndCycleCountNoAndStatusIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String cycleCountNo, List<Long> statusId, Long deletionIndicator);

    //StockAdjustment
    @Query(value = "SELECT top 1 * FROM tblperpetualline \r\n"
            + " WHERE ITM_CODE = :itemCode and MFR_NAME = :manufacturerName and \n"
            + "C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId and \n"
            + "VAR_QTY != 0 and VAR_QTY is not null and \n"
            + "IS_DELETED = 0 order by cc_cnt_on desc", nativeQuery = true)
    public PerpetualLineV2 findPerpetualLineByItemCode(@Param(value = "itemCode") String itemCode,
                                                             @Param(value = "companyCodeId") String companyCodeId,
                                                             @Param(value = "plantId") String plantId,
                                                             @Param(value = "languageId") String languageId,
                                                             @Param(value = "warehouseId") String warehouseId,
                                                             @Param(value = "manufacturerName") String manufacturerName);
}