package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.dto.StorageBin;
import com.tekclover.wms.api.inbound.orders.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
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
public interface StorageBinRepository extends JpaRepository<StorageBin, Long>,
        JpaSpecificationExecutor<StorageBin>, StreamableJpaSpecificationRepository<StorageBin> {

    public List<StorageBin> findByStorageBinInAndStorageSectionIdInAndPutawayBlockAndPickingBlockAndDeletionIndicatorOrderByStorageBinDesc(
            List<String> storageBin, List<String> storageSectionId, Integer putawayBlock, Integer pickingBlock, Long deletionIndicator);

    @Query(value = "SELECT ST_SEC_ID FROM tblstoragebin \r\n"
            + " WHERE ST_BIN = :storageBin", nativeQuery = true)
    public String findByStorageBin(@Param(value = "storageBin") String storageBin);


    public Optional<StorageBin> findByStorageBinAndDeletionIndicator(String storageBin, Long delFlag);

    public StorageBin findByWarehouseIdAndStorageBinAndDeletionIndicator(String warehouseId, String storageBin, long l);

    long countByWarehouseIdAndStatusIdAndDeletionIndicator(String warehouseId, Long statusId, Long deletionIndicator);

    long countByWarehouseIdAndStatusIdNotAndDeletionIndicator(String warehouseId, Long statusId, Long deletionIndicator);

    StorageBin findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId,
            String storageBin, Long deletionIndicator);

    @Query(value = "select * from tblstoragebin sb \n" +
            "WHERE sb.is_deleted = 0 and (case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbmPerQty and sb.st_bin in (select st_bin from tblinventory ip where ip.itm_code in (:itemCode) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.bin_cl_id in (:binClassId) and \n" +
            "ip.is_deleted = 0) order by remain_vol", nativeQuery = true)
    public List<StorageBinV2> getStorageBinList(@Param(value = "itemCode") String itemCode,
                                                @Param(value = "warehouseId") String warehouseId,
                                                @Param(value = "cbmPerQty") String cbmPerQty,
                                                @Param(value = "binClassId") Long binClassId);

    //companyCode Filter
    @Query(value = "select * from tblstoragebin sb \n" +
            "WHERE sb.is_deleted = 0 and sb.c_id = :companyCode and (case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbmPerQty and sb.st_bin in (select st_bin from tblinventory ip where ip.itm_code in (:itemCode) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.bin_cl_id in (:binClassId) and \n" +
            "ip.c_id in (:companyCode) and \n" +
            "ip.is_deleted = 0) order by remain_vol", nativeQuery = true)
    public List<StorageBinV2> getStorageBinListByCompany(@Param(value = "itemCode") String itemCode,
                                                         @Param(value = "warehouseId") String warehouseId,
                                                         @Param(value = "companyCode") String companyCode,
                                                         @Param(value = "cbmPerQty") String cbmPerQty,
                                                         @Param(value = "binClassId") Long binClassId);

    //companyCode and Branch Filter
    @Query(value = "select * from tblstoragebin sb \n" +
            "WHERE sb.is_deleted = 0 and sb.c_id = :companyCode and sb.plant_id = :branchCode and sb.lang_id = :languageId and sb.wh_id = :warehouseId and \n" +
            "(case when ISNUMERIC(remain_vol)=1 then CAST(remain_vol AS NUMERIC) else 0 end) > :cbmPerQty and \n" +
            "sb.st_bin in (select st_bin from tblinventory ip where ip.itm_code in (:itemCode) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.bin_cl_id in (:binClassId) and \n" +
            "ip.mfr_name in (:manufacturerName) and \n" +
            "ip.c_id in (:companyCode) and \n" +
            "ip.lang_id in (:languageId) and \n" +
            "ip.plant_id in (:branchCode) and \n" +
            "ip.is_deleted = 0) order by remain_vol", nativeQuery = true)
    public List<StorageBinV2> getStorageBinListByCompanyBranch(@Param(value = "itemCode") String itemCode,
                                                               @Param(value = "warehouseId") String warehouseId,
                                                               @Param(value = "companyCode") String companyCode,
                                                               @Param(value = "branchCode") String branchCode,
                                                               @Param(value = "languageId") String languageId,
                                                               @Param(value = "cbmPerQty") String cbmPerQty,
                                                               @Param(value = "binClassId") Long binClassId,
                                                               @Param(value = "manufacturerName") String manufacturerName
                                                               );

    //Non CBM companyCode and Branch Filter
    @Query(value = "select * from tblstoragebin sb \n" +
            "WHERE sb.is_deleted = 0 and sb.c_id = :companyCode and sb.plant_id = :branchCode and sb.lang_id = :languageId and sb.wh_id = :warehouseId and \n" +
            "sb.st_bin in (select st_bin from tblinventory ip where ip.itm_code in (:itemCode) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.bin_cl_id in (:binClassId) and \n" +
            "ip.c_id in (:companyCode) and \n" +
            "ip.mfr_name in (:manufacturerName) and \n" +
            "ip.lang_id in (:languageId) and \n" +
            "ip.plant_id in (:branchCode) and \n" +
            "ip.is_deleted = 0)", nativeQuery = true)
    public List<StorageBinV2> getStorageBinListByCompanyBranchNonCBM(@Param(value = "itemCode") String itemCode,
                                                                     @Param(value = "warehouseId") String warehouseId,
                                                                     @Param(value = "companyCode") String companyCode,
                                                                     @Param(value = "branchCode") String branchCode,
                                                                     @Param(value = "languageId") String languageId,
                                                                     @Param(value = "binClassId") Long binClassId,
                                                                     @Param(value = "manufacturerName") String manufacturerName);

    @Query(value = "select * from tblstoragebin sb \n" +
            "WHERE \n" +
            "sb.c_id in (:companyCode) and \n" +
            "sb.plant_id in (:plantId) and \n" +
            "sb.lang_id in (:languageId) and \n" +
            "sb.wh_id in (:warehouseId) and \n" +
            "sb.st_bin in (:storageBin) and \n" +
            "sb.is_deleted = 0 order by remain_vol", nativeQuery = true)
    public StorageBinV2 getStorageBin(@Param(value = "companyCode") String companyCode,
                                      @Param(value = "plantId") String plantId,
                                      @Param(value = "languageId") String languageId,
                                      @Param(value = "warehouseId") String warehouseId,
                                      @Param(value = "storageBin") String storageBin);

    @Query(value = "select * from tblstoragebin sb \n" +
            "WHERE \n" +
            "sb.c_id in (:companyCode) and \n" +
            "sb.plant_id in (:plantId) and \n" +
            "sb.lang_id in (:languageId) and \n" +
            "sb.wh_id in (:warehouseId) and \n" +
            "sb.st_bin in (:storageBin) and \n" +
            "sb.bin_cl_id in (:binClassId) and \n" +
            "sb.is_deleted = 0 order by remain_vol", nativeQuery = true)
    public StorageBinV2 getStorageBinByBinClassId(@Param(value = "companyCode") String companyCode,
                                                  @Param(value = "plantId") String plantId,
                                                  @Param(value = "languageId") String languageId,
                                                  @Param(value = "warehouseId") String warehouseId,
                                                  @Param(value = "binClassId") Long binClassId,
                                                  @Param(value = "storageBin") String storageBin);
}


