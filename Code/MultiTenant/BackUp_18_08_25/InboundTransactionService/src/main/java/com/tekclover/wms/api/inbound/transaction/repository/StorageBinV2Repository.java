package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface StorageBinV2Repository extends JpaRepository<StorageBinV2, Long>,
        JpaSpecificationExecutor<StorageBinV2>, StreamableJpaSpecificationRepository<StorageBinV2> {
    StorageBinV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdAndStorageSectionIdNotInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String bin, Long binClassId, List<String> storageSectionIds, long l);

    StorageBinV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String bin, Long binClassId, long l);

    StorageBinV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndStorageSectionIdNotInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String bin, List<String> storageSectionIds, long l);

    StorageBinV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndDeletionIndicator(String companyCodeId, String plantId, String languageId, String warehouseId, String bin, long l);

    Optional<StorageBinV2> findTopByBinClassIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
            Long binClassId, String companyCodeId, String plantId, String warehouseId, String languageId, Long deletionIndicator);

    @Query(value = "SELECT * from tblstoragebin where bin_cl_id = :binClassId " +
            "AND c_id = :companyCodeId AND plant_id = :plantId AND wh_id = :warehouseId " +
            "AND lang_id = :languageId AND IS_DELETED = 0", nativeQuery = true)
    Optional<StorageBinV2> getStorageBin(@Param("binClassId") Long binClassId,
                                         @Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("languageId") String languageId);

    StorageBinV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndBinClassIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long binClassId, Long deletionIndicator);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and \n" +
            " is_deleted = 0 and st_bin <> 'REC-AL-B2' order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinNonCBMBinClassId(@Param("binclassId") Long binclassId,
                                                      @Param("companyCode") String companyCode,
                                                      @Param("plantId") String plantId,
                                                      @Param("languageId") String languageId,
                                                      @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin in (:storageBin) and \n" +
            "status_id = :statusId and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 and st_bin <> 'REC-AL-B2' order by UTD_ON desc", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinNonCBMLastPick(@Param("binclassId") Long binclassId,
                                                    @Param("companyCode") String companyCode,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("statusId") Long statusId,
                                                    @Param("storageBin") List<String> storageBin,
                                                    @Param("warehouseId") String warehouseId);


    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and CAP_CHECK = 'TRUE' and st_bin in (:storageBin) and\n" +
            "status_id = :statusId and \n" +
            "(case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbm and \n" +
            "is_deleted = 0 and st_bin <> 'REC-AL-B2' order by remain_vol", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
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
            "status_id = :statusId and \n" +
            "(case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbmPerQty and \n" +
            "is_deleted = 0 and st_bin <> 'REC-AL-B2' order by remain_vol", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinCbmPerQtyLastPick(@Param("binclassId") Long binclassId,
                                                       @Param("companyCode") String companyCode,
                                                       @Param("plantId") String plantId,
                                                       @Param("languageId") String languageId,
                                                       @Param("cbmPerQty") Double cbmPerQty,
                                                       @Param("statusId") Long statusId,
                                                       @Param("storageBin") List<String> storageBin,
                                                       @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin in (:storageBin) and ST_SEC_ID not in (:storageSectionId) and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 and st_bin <> 'RECEIVINGSTAGING' order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getExistingStorageBinNonCBM(@Param("binclassId") Long binclassId,
                                                    @Param("storageSectionId") List<String> storageSectionId,
                                                    @Param("companyCode") String companyCode,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("storageBin") List<String> storageBin,
                                                    @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin in (:storageBin) and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 and st_bin <> 'REC-AL-B2' order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getExistingStorageBinNonCBM(@Param("binclassId") Long binclassId,
                                                    @Param("companyCode") String companyCode,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("storageBin") List<String> storageBin,
                                                    @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and CAP_CHECK = 'TRUE' and \n" +
            "status_id = :statusId and \n" +
            "(case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbm and \n" +
            "is_deleted = 0 and st_bin <> 'REC-AL-B2' order by remain_vol", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinCBM(@Param("binclassId") Long binclassId,
                                         @Param("companyCode") String companyCode,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("cbm") Double cbm,
                                         @Param("statusId") Long statusId,
                                         @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin not in (:storageBin) and \n" +
            "status_id = :statusId and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 and st_bin <> 'REC-AL-B2' order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinNonCBM(@Param("binclassId") Long binclassId,
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
            "CAP_CHECK = 'FALSE' and is_deleted = 0 and st_bin <> 'REC-AL-B2' order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinNonCBM(@Param("binclassId") Long binclassId,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("statusId") Long statusId,
                                            @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin in (:storageBin) and ST_SEC_ID not in (:storageSectionId) and \n" +
            "status_id = :statusId and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 and st_bin <> 'REC-AL-B2' order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinNonCBMV7(@Param("binclassId") Long binclassId,
                                            @Param("storageSectionId") List<String> storageSectionId,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("statusId") Long statusId,
                                            @Param("storageBin") List<String> storageBin,
                                            @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and st_bin not in (:storageBin) and ST_SEC_ID not in (:storageSectionId) and \n" +
            "status_id = :statusId and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 and st_bin <> 'REC-AL-B2' order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinNonCBM(@Param("binclassId") Long binclassId,
                                            @Param("storageSectionId") List<String> storageSectionId,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("statusId") Long statusId,
                                            @Param("storageBin") List<String> storageBin,
                                            @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE bin_cl_id = :binclassId and c_id = :companyCode and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and ST_SEC_ID not in (:storageSectionIds) and \n" +
            "ST_SEC_ID = :storageSectionId and \n" +
            "CAP_CHECK = 'FALSE' and is_deleted = 0 and st_bin <> 'REC-AL-B2' order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    public StorageBinV2 getStorageBinNonCBM(@Param("binclassId") Long binclassId,
                                            @Param("storageSectionIds") List<String> storageSectionIds,
                                            @Param("companyCode") String companyCode,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("storageSectionId") String storageSectionId,
                                            @Param("warehouseId") String warehouseId);


    Optional<StorageBinV2> findByStorageBinAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
            String storageBin, String companyCodeId, String plantId, String warehouseId, String languageId, Long deletionIndicator);

    @Query(value = "SELECT top 1 * FROM tblstoragebin WHERE c_id = :companyCodeId and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and is_deleted = 0 and st_bin = :storageBin order by st_bin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    Optional<StorageBinV2> getStorageBinV5(@Param("storageBin") String storageBin,
                                        @Param("companyCodeId") String companyCodeId,
                                        @Param("plantId") String plantId,
                                        @Param("languageId") String languageId,
                                        @Param("warehouseId") String warehouseId);

    @Query(value = "SELECT * FROM tblstoragebin WHERE c_id = :companyCodeId and plant_id = :plantId and \n" +
            "wh_id = :warehouseId and lang_id = :languageId and is_deleted = 0 and st_bin = :storageBin", nativeQuery = true)
    //storage-bin excluding direct stock receipt bin
    Optional<StorageBinV2> getStorageBinEmptyCrate(@Param("storageBin") String storageBin,
                                        @Param("companyCodeId") String companyCodeId,
                                        @Param("plantId") String plantId,
                                        @Param("languageId") String languageId,
                                        @Param("warehouseId") String warehouseId);

    boolean existsByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndBinClassIdInAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String storageBin, List<Long> binClassIds, Long deletionIndicator);

    @Modifying
    @Query(value = "UPDATE tblstoragebin set status_id = :statusId WHERE \n " +
            "st_bin = :storageBin AND c_id = :companyCodeId AND plant_id = :plantId \n" +
            "AND wh_id = :warehouseId", nativeQuery = true)
    void updateEmptyBinStatus(@Param("storageBin") String storageBin,
                              @Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("warehouseId") String warehouseId,
                              @Param("statusId") Long statusId);

    @Query(value = "SELECT top 1 * from tblstoragebin WHERE \n " +
            "(st_bin LIKE 'E-%' OR st_bin LIKE 'P-%') AND c_id = :companyCodeId \n" +
            "AND plant_id = :plantId AND wh_id = :warehouseId and status_id = 0 \n" +
            "AND is_deleted = 0", nativeQuery = true)
    StorageBinV2 getEorPBin(@Param("companyCodeId") String companyCodeId,
                            @Param("plantId") String plantId,
                            @Param("warehouseId") String warehouseId);

}
