package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.storagebin.v2.StorageBinV2;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StorageBinV2Repository extends JpaRepository<StorageBinV2, Long>,
        JpaSpecificationExecutor<StorageBinV2>,
        StreamableJpaSpecificationRepository<StorageBinV2> {

    List<StorageBinV2> findByWarehouseIdAndStorageBinInAndBinClassIdAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
            String warehouseId, List<String> storageBin, Long binClassId, int i, int j, long l);

    List<StorageBinV2> findByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId, Long statusId, long l);

    Optional<StorageBinV2> findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
            String storageBin, String companyCodeId, String plantId, String warehouseId, String languageId, long l);

    StorageBinV2 findByWarehouseIdAndStorageBinAndDeletionIndicator(String warehouseId, String storageBin, long l);

    @Query(value = "SELECT * FROM tblstoragebin WHERE st_bin = :storageBin and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and is_deleted = 0 ", nativeQuery = true)
    public StorageBinV2 getStorageBin(@Param("storageBin") String storageBin,
                                      @Param("companyCode") String companyCode,
                                      @Param("plantId") String plantId,
                                      @Param("languageId") String languageId,
                                      @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and is_deleted = 0 ", nativeQuery = true)
    public StorageBinV2 getStorageBinByBinClassId(@Param("binclassId") Long binclassId,
                                                  @Param("companyCode") String companyCode,
                                                  @Param("plantId") String plantId,
                                                  @Param("languageId") String languageId,
                                                  @Param("warehouseId") String warehouseId);

    Optional<StorageBinV2> findByBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
            String binClassId, String companyCodeId, String plantId, String warehouseId, String languageId, long l);

    Optional<StorageBinV2> findTopByBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
            Long binClassId, String companyCodeId, String plantId, String warehouseId, String languageId, long l);

    List<StorageBinV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long statusId, Long deletionIndicator);

    List<StorageBinV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinInAndBinClassIdAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, List<String> storageBin, Long binClassId, int i, int j, Long deletionIndicator);

    List<StorageBinV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndBinClassIdAndCapacityCheckAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            Long binClassId, boolean capacityCheck, int i, int j, Long deletionIndicator);

    //pick_block=0 and putaway_block=0 condition removed since it is not requered in Almailem
    //11-03-2024 validation removed as per business requirement - Ticket No. ALM/2024/001
    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and CAP_CHECK = 'TRUE' and \n" +
//            "pick_block = 0 and putaway_block = 0 and status_id = :statusId and \n" +
            "status_id = :statusId and \n" +
            "(case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbm and \n" +
            "is_deleted = 0  order by remain_vol", nativeQuery = true)
    public StorageBinV2 getStorageBinCBM(@Param("binclassId") Long binclassId,
                                         @Param("companyCode") String companyCode,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("cbm") Double cbm,
                                         @Param("statusId") Long statusId,
                                         @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and CAP_CHECK = 'TRUE' and \n" +
//            "pick_block = 0 and putaway_block = 0 and status_id = :statusId and \n" +
            "status_id = :statusId and \n" +
            "(case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbmPerQty and \n" +
            "is_deleted = 0  order by remain_vol", nativeQuery = true)
    public StorageBinV2 getStorageBinCbmPerQty(@Param("binclassId") Long binclassId,
                                               @Param("companyCode") String companyCode,
                                               @Param("plantId") String plantId,
                                               @Param("languageId") String languageId,
                                               @Param("cbmPerQty") Double cbmPerQty,
                                               @Param("statusId") Long statusId,
                                               @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and CAP_CHECK = 'TRUE' and st_bin in (:storageBin) and\n" +
//            "pick_block = 0 and putaway_block = 0 and status_id = :statusId and \n" +
            "status_id = :statusId and \n" +
            "(case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbm and \n" +
            "is_deleted = 0  order by remain_vol", nativeQuery = true)
    public StorageBinV2 getStorageBinLastPickCBM(@Param("binclassId") Long binclassId,
                                                 @Param("companyCode") String companyCode,
                                                 @Param("plantId") String plantId,
                                                 @Param("languageId") String languageId,
                                                 @Param("cbm") Double cbm,
                                                 @Param("statusId") Long statusId,
                                                 @Param("storageBin") List<String> storageBin,
                                                 @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and CAP_CHECK = 'TRUE' and st_bin in (:storageBin) and\n" +
//            "pick_block = 0 and putaway_block = 0 and status_id = :statusId and \n" +
            "status_id = :statusId and \n" +
            "(case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbmPerQty and \n" +
            "is_deleted = 0  order by remain_vol", nativeQuery = true)
    public StorageBinV2 getStorageBinCbmPerQtyLastPick(@Param("binclassId") Long binclassId,
                                                       @Param("companyCode") String companyCode,
                                                       @Param("plantId") String plantId,
                                                       @Param("languageId") String languageId,
                                                       @Param("cbmPerQty") Double cbmPerQty,
                                                       @Param("statusId") Long statusId,
                                                       @Param("storageBin") List<String> storageBin,
                                                       @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin not in (:storageBin) and \n" +
//            "putaway_block = 0 and pick_block = 0 and status_id = :statusId and \n" +
            "status_id = :statusId and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 order by st_bin", nativeQuery = true)
    public StorageBinV2 getStorageBinNonCBM(@Param("binclassId") Long binclassId,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("statusId") Long statusId,
                                            @Param("storageBin") List<String> storageBin,
                                            @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin in (:storageBin) and \n" +
//            "putaway_block = 0 and pick_block = 0 and status_id = :statusId and \n" +
            "status_id = :statusId and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 ", nativeQuery = true)
    public StorageBinV2 getStorageBinNonCBMLastPick(@Param("binclassId") Long binclassId,
                                                    @Param("companyCode") String companyCode,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("statusId") Long statusId,
                                                    @Param("storageBin") List<String> storageBin,
                                                    @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and \n" +
//            "putaway_block = 0 and pick_block = 0 and status_id = :statusId and \n" +
            "status_id = :statusId and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 order by st_bin", nativeQuery = true)
    public StorageBinV2 getStorageBinNonCBM(@Param("binclassId") Long binclassId,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("statusId") Long statusId,
                                            @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin in (:storageBin) and \n" +
//            "putaway_block = 0 and pick_block = 0 and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 order by st_bin", nativeQuery = true)
    public StorageBinV2 getExistingStorageBinNonCBM(@Param("binclassId") Long binclassId,
                                                    @Param("companyCode") String companyCode,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("storageBin") List<String> storageBin,
                                                    @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and \n" +
//            "CAP_CHECK = 'FALSE' and pick_block = 0 and putaway_block = 0 and \n" +
            " is_deleted = 0 order by st_bin", nativeQuery = true)
    public StorageBinV2 getStorageBinNonCBMBinClassId(@Param("binclassId") Long binclassId,
                                                      @Param("companyCode") String companyCode,
                                                      @Param("plantId") String plantId,
                                                      @Param("languageId") String languageId,
                                                      @Param("warehouseId") String warehouseId);

    StorageBinV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin, Long deletionIndicator);
    StorageBinV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin, Long binClassId, Long deletionIndicator);
}


