package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;

import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;

@Repository
@Transactional
public interface PerpetualLineRepository extends JpaRepository<PerpetualLine, Long>,
        JpaSpecificationExecutor<PerpetualLine>, StreamableJpaSpecificationRepository<PerpetualLine> {

    public PerpetualLine
    findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String cycleCountNo,
            String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);

    public List<PerpetualLine> findByWarehouseIdAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
            String warehouseId, String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PerpetualLine pl \r\n"
            + " SET pl.cycleCounterId = :cycleCounterId, pl.cycleCounterName = :cycleCounterName, pl.statusId = :statusId,  \r\n"
            + " pl.countedBy = :countedBy, pl.countedOn = :countedOn "
            + " WHERE pl.warehouseId = :warehouseId AND \r\n "
            + " pl.cycleCountNo = :cycleCountNo AND pl.storageBin in :storageBin AND pl.itemCode = :itemCode AND pl.packBarcodes = :packBarcodes"
            + " AND deletionIndicator = 0")
    public void updateHHTUser(
            @Param("cycleCounterId") String cycleCounterId,
            @Param("cycleCounterName") String cycleCounterName,
            @Param("statusId") Long statusId,
            @Param("countedBy") String countedBy,
            @Param("countedOn") Date countedOn,
            @Param("warehouseId") String warehouseId,
            @Param("cycleCountNo") String cycleCountNo,
            @Param("storageBin") String storageBin,
            @Param("itemCode") String itemCode,
            @Param("packBarcodes") String packBarcodes);

    public List<PerpetualLine> findByCycleCountNoAndDeletionIndicator(String cycleCountNo, Long deletionIndicator);

    public List<PerpetualLine> findByCycleCountNoAndCycleCounterIdInAndDeletionIndicator(String cycleCountNo,
                                                                                         List<String> cycleCounterId, Long deletionIndicator);

    List<PerpetualLine> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndStatusIdInAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId, List<Long> statusId, Long deletionIndicator);

    List<PerpetualLine> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndCycleCounterIdAndStatusIdInAndDeletionIndicator(
            String companyCode, String languageId, String plantId, String warehouseId,String cycleCounterId, List<Long> statusId, Long deletionIndicator);


    // Count for MobileDashBoard
    @Query(value = "SELECT COUNT(cc_no) AS count FROM (\n"
            + "select distinct cc_no from \n"
            + "tblperpetualline WHERE \n"
            + "(:languageId IS NULL OR LANG_ID = :languageId) AND \n"
            + "(:companyCode IS NULL OR C_ID = :companyCode) AND \n"
            + "(:plantId IS NULL OR PLANT_ID = :plantId) AND \n"
            + "(:warehouseId IS NULL OR WH_ID = :warehouseId) AND \n"
            + "(STATUS_ID IN (:statusId)) AND \n"
            + "(COUNTER_ID IN (:counterId)) AND \n"
            + "IS_DELETED = 0 ) x", nativeQuery = true)
    public Long getPerpetualLineCount(@Param("companyCode") List<String> companyCode,
                                           @Param("plantId") List<String> plantId,
                                           @Param("warehouseId") List<String> warehouseId,
                                           @Param("languageId") List<String> languageId,
                                           @Param("statusId") List<Long> statusId,
                                           @Param("counterId") List<String> counterId);
}