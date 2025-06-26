package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.dto.IInventory;
import com.tekclover.wms.api.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.report.CBMBinReport;
import com.tekclover.wms.api.transaction.model.report.OccupancyBinReportResponse;
import com.tekclover.wms.api.transaction.model.report.StorageBinDashBoardImpl;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface InventoryV2Repository extends PagingAndSortingRepository<InventoryV2, Long>,
        JpaSpecificationExecutor<InventoryV2>, StreamableJpaSpecificationRepository<InventoryV2> {


    List<InventoryV2> findByWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(
            String warehouseId, String itemCode, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String companycode, String plantId, String languageId, String warehouseId,
            String itemCode, String manufacturerName, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndStorageBinAndBarcodeIdAndBinClassIdAndThreePLPartnerIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String storageBin, String barcodeId, Long binClassId, String threePLPartnerId, Long deletionIndicator);
    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String referenceDocumentNo, String itemCode, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String packBarcodes, String itemCode, String manufacturerName,
            String storageBin, Long stockTypeId, Long specialStockIndicatorId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String packBarcodes, String itemCode, Long binClassId, Long deletionIndicator);

    Optional<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String packBarcodes, String itemCode, String storageBin, String manufacturerCode, Long deletionIndicator);

    Optional<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String packBarcodes, String itemCode, String manufacturerCode, Long deletionIndicator);

    @Query(value = "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "REF_ORD_NO referenceOrderNo, \n"
            + "STR_MTD storageMethod, \n"
            + "BIN_CL_ID binClassId, \n"
            + "TEXT description, \n"
            + "INV_QTY inventoryQuantity, \n"
            + "ALLOC_QTY allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
            + "COALESCE(INV_QTY,0)+COALESCE(ALLOC_QTY,0) referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "IU_CTD_BY createdBy, \n"
            + "IU_CTD_ON createdOn, \n"
            + "UTD_BY updatedBy, \n"
            + "UTD_ON updatedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "BARCODE_ID barcodeId, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_PER_QTY cbmPerQuantity, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "REF_DOC_NO referenceDocumentNo, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription", nativeQuery = true)
    public IInventory findInventoryForPeriodicRun(
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "storageBin") String storageBin,
            @Param(value = "packbarCode") String packbarCode);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (1,7) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "REF_ORD_NO referenceOrderNo, \n"
            + "STR_MTD storageMethod, \n"
            + "BIN_CL_ID binClassId, \n"
            + "TEXT description, \n"
            + "sum(INV_QTY) inventoryQuantity, \n"
            + "ALLOC_QTY allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
            + "REF_FIELD_4 referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "IU_CTD_BY createdBy, \n"
            + "IU_CTD_ON createdOn, \n"
            + "UTD_BY updatedBy, \n"
            + "UTD_ON updatedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "BARCODE_ID barcodeId, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_PER_QTY cbmPerQuantity, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "REF_DOC_NO referenceDocumentNo, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (1,7) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 and STCK_TYP_ID = 1 \n" +
            "and inv_id in (select inventoryId from #inv) \n" +
            "group by itm_code, mfr_name, st_bin,LANG_ID,C_ID,PLANT_ID,WH_ID,VAR_ID,VAR_SUB_ID,STR_NO,STCK_TYP_ID,\n" +
            "SP_ST_IND_ID,REF_ORD_NO,STR_MTD,BIN_CL_ID,TEXT,ALLOC_QTY,INV_UOM,MFR_DATE,EXP_DATE,REF_FIELD_1,REF_FIELD_2,\n" +
            "REF_FIELD_3,REF_FIELD_4,REF_FIELD_5,REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,\n" +
            "IU_CTD_BY,IU_CTD_ON,UTD_BY,UTD_ON,MFR_CODE,BARCODE_ID,CBM,CBM_UNIT,CBM_PER_QTY,ORIGIN,BRAND,REF_DOC_NO,\n" +
            "C_TEXT,PLANT_TEXT,WH_TEXT,STATUS_TEXT,PAL_CODE,CASE_CODE,PACK_BARCODE "
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForPerpertual(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
//            @Param(value = "binClassId") Long binClassId,
            @Param(value = "manufacturerName") String manufacturerName);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.INV_QTY ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOml(@Param("companyCodeId") String companyCodeId,
                                                 @Param("languageId") String languageId,
                                                 @Param("plantId") String plantId,
                                                 @Param("warehouseId") String warehouseId,
                                                 @Param("manufacturerName") String manufacturerName,
                                                 @Param("itemCode") String itemCode,
                                                 @Param("storageBin") String storageBin,
                                                 @Param("stockTypeId") Long stockTypeId,
                                                 @Param("binClassId") Long binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.LEVEL_ID,iv.INV_QTY ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOmlOrderByLevelId(@Param("companyCodeId") String companyCodeId,
                                                               @Param("languageId") String languageId,
                                                               @Param("plantId") String plantId,
                                                               @Param("warehouseId") String warehouseId,
                                                               @Param("manufacturerName") String manufacturerName,
                                                               @Param("itemCode") String itemCode,
                                                               @Param("storageBin") String storageBin,
                                                               @Param("stockTypeId") Long stockTypeId,
                                                               @Param("binClassId") Long binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.LEVEL_ID levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:levelId, null) IS NULL OR (iv.LEVEL_ID IN (:levelId))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.LEVEL_ID,iv.INV_QTY ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOmlGroupByLevelId(@Param("companyCodeId") String companyCodeId,
                                                               @Param("languageId") String languageId,
                                                               @Param("plantId") String plantId,
                                                               @Param("warehouseId") String warehouseId,
                                                               @Param("manufacturerName") String manufacturerName,
                                                               @Param("itemCode") String itemCode,
                                                               @Param("storageBin") String storageBin,
                                                               @Param("stockTypeId") Long stockTypeId,
                                                               @Param("binClassId") Long binClassId,
                                                               @Param("levelId") Long levelId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.LEVEL_ID,iv.INV_QTY ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOmlGroupByLevelId(@Param("companyCodeId") String companyCodeId,
                                                               @Param("languageId") String languageId,
                                                               @Param("plantId") String plantId,
                                                               @Param("warehouseId") String warehouseId,
                                                               @Param("manufacturerName") String manufacturerName,
                                                               @Param("itemCode") String itemCode,
                                                               @Param("storageBin") String storageBin,
                                                               @Param("stockTypeId") Long stockTypeId,
                                                               @Param("binClassId") Long binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.IU_CTD_ON,iv.INV_QTY ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOmlOrderByCtdOnId(@Param("companyCodeId") String companyCodeId,
                                                               @Param("plantId") String plantId,
                                                               @Param("languageId") String languageId,
                                                               @Param("warehouseId") String warehouseId,
                                                               @Param("itemCode") String itemCode,
                                                               @Param("manufacturerName") String manufacturerName,
                                                               @Param("stockTypeId") Long stockTypeId,
                                                               @Param("binClassId") Long binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.LEVEL_ID,iv.INV_QTY ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOmlOrderByLevelId(@Param("companyCodeId") String companyCodeId,
                                                               @Param("plantId") String plantId,
                                                               @Param("languageId") String languageId,
                                                               @Param("warehouseId") String warehouseId,
                                                               @Param("itemCode") String itemCode,
                                                               @Param("manufacturerName") String manufacturerName,
                                                               @Param("stockTypeId") Long stockTypeId,
                                                               @Param("binClassId") Long binClassId);
    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.LEVEL_ID levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:levelId, null) IS NULL OR (iv.LEVEL_ID IN (:levelId))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.LEVEL_ID,iv.INV_QTY ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOmlGroupByLevelId(@Param("companyCodeId") String companyCodeId,
                                                               @Param("plantId") String plantId,
                                                               @Param("languageId") String languageId,
                                                               @Param("warehouseId") String warehouseId,
                                                               @Param("itemCode") String itemCode,
                                                               @Param("manufacturerName") String manufacturerName,
                                                               @Param("stockTypeId") Long stockTypeId,
                                                               @Param("binClassId") Long binClassId,
                                                               @Param("levelId") Long levelId);

    //StockAdjustment group by packbarcode
    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 and inv_qty > 0 \n" +
            "group by itm_code,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "REF_ORD_NO referenceOrderNo, \n"
            + "STR_MTD storageMethod, \n"
            + "BIN_CL_ID binClassId, \n"
            + "TEXT description, \n"
            + "sum(INV_QTY) inventoryQuantity, \n"
            + "ALLOC_QTY allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
//            + "COALESCE(INV_QTY,0)+COALESCE(ALLOC_QTY,0) referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "IU_CTD_BY createdBy, \n"
            + "IU_CTD_ON createdOn, \n"
            + "UTD_BY updatedBy, \n"
            + "UTD_ON updatedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "BARCODE_ID barcodeId, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_PER_QTY cbmPerQuantity, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "REF_DOC_NO referenceDocumentNo, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "stck_typ_id in (:stockTypeId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "and inv_id in (select inventoryId from #inv) \n" +
            "group by itm_code, mfr_name, st_bin,LANG_ID,C_ID,PLANT_ID,WH_ID,VAR_ID,VAR_SUB_ID,STR_NO,STCK_TYP_ID,\n" +
            "SP_ST_IND_ID,REF_ORD_NO,STR_MTD,BIN_CL_ID,TEXT,ALLOC_QTY,INV_UOM,MFR_DATE,EXP_DATE,REF_FIELD_1,REF_FIELD_2,\n" +
            "REF_FIELD_3,REF_FIELD_5,REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,\n" +
            "IU_CTD_BY,IU_CTD_ON,UTD_BY,UTD_ON,MFR_CODE,BARCODE_ID,CBM,CBM_UNIT,CBM_PER_QTY,ORIGIN,BRAND,REF_DOC_NO,\n" +
            "C_TEXT,PLANT_TEXT,WH_TEXT,STATUS_TEXT,PAL_CODE,CASE_CODE,PACK_BARCODE "
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForStockAdjustmentGroupByPackBarcode(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "binClassId") Long binClassId,
            @Param(value = "stockTypeId") Long stockTypeId,
            @Param(value = "manufacturerName") String manufacturerName);

    //StockAdjustment without groupby packBarCode
    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 and inv_qty > 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n"
            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "REF_ORD_NO referenceOrderNo, \n"
            + "STR_MTD storageMethod, \n"
            + "BIN_CL_ID binClassId, \n"
            + "TEXT description, \n"
            + "sum(INV_QTY) inventoryQuantity, \n"
            + "ALLOC_QTY allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
//            + "COALESCE(INV_QTY,0)+COALESCE(ALLOC_QTY,0) referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "IU_CTD_BY createdBy, \n"
            + "IU_CTD_ON createdOn, \n"
            + "UTD_BY updatedBy, \n"
            + "UTD_ON updatedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "BARCODE_ID barcodeId, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_PER_QTY cbmPerQuantity, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "REF_DOC_NO referenceDocumentNo, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "stck_typ_id in (:stockTypeId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "and inv_id in (select inventoryId from #inv) \n" +
            "group by itm_code, mfr_name, st_bin,LANG_ID,C_ID,PLANT_ID,WH_ID,VAR_ID,VAR_SUB_ID,STR_NO,STCK_TYP_ID,\n" +
            "SP_ST_IND_ID,REF_ORD_NO,STR_MTD,BIN_CL_ID,TEXT,ALLOC_QTY,INV_UOM,MFR_DATE,EXP_DATE,REF_FIELD_1,REF_FIELD_2,\n" +
            "REF_FIELD_3,REF_FIELD_5,REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,\n" +
            "IU_CTD_BY,IU_CTD_ON,UTD_BY,UTD_ON,MFR_CODE,BARCODE_ID,CBM,CBM_UNIT,CBM_PER_QTY,ORIGIN,BRAND,REF_DOC_NO,\n" +
            "C_TEXT,PLANT_TEXT,WH_TEXT,STATUS_TEXT,PAL_CODE,CASE_CODE,PACK_BARCODE "
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForStockAdjustment(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "binClassId") Long binClassId,
            @Param(value = "stockTypeId") Long stockTypeId,
            @Param(value = "manufacturerName") String manufacturerName);

    //InventoryMovement
    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "REF_ORD_NO referenceOrderNo, \n"
            + "STR_MTD storageMethod, \n"
            + "BIN_CL_ID binClassId, \n"
            + "TEXT description, \n"
            + "INV_QTY inventoryQuantity, \n"
            + "ALLOC_QTY allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
            + "REF_FIELD_4 referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "IU_CTD_BY createdBy, \n"
            + "IU_CTD_ON createdOn, \n"
            + "UTD_BY updatedBy, \n"
            + "UTD_ON updatedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "BARCODE_ID barcodeId, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_PER_QTY cbmPerQuantity, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "REF_DOC_NO referenceDocumentNo, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "and inv_id in (select inventoryId from #inv) \n"
//            "group by itm_code, mfr_name, st_bin,LANG_ID,C_ID,PLANT_ID,WH_ID,VAR_ID,VAR_SUB_ID,STR_NO,STCK_TYP_ID,\n" +
//            "SP_ST_IND_ID,REF_ORD_NO,STR_MTD,BIN_CL_ID,TEXT,ALLOC_QTY,INV_UOM,MFR_DATE,EXP_DATE,REF_FIELD_1,REF_FIELD_2,\n" +
//            "REF_FIELD_3,REF_FIELD_5,REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,\n" +
//            "IU_CTD_BY,IU_CTD_ON,UTD_BY,UTD_ON,MFR_CODE,BARCODE_ID,CBM,CBM_UNIT,CBM_PER_QTY,ORIGIN,BRAND,REF_DOC_NO,\n" +
//            "C_TEXT,PLANT_TEXT,WH_TEXT,STATUS_TEXT,PAL_CODE,CASE_CODE,PACK_BARCODE "
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForInventoryMovement(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "binClassId") Long binClassId,
            @Param(value = "manufacturerName") String manufacturerName);

    //InventoryMovement
    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
//            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "group by itm_code,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "REF_ORD_NO referenceOrderNo, \n"
            + "STR_MTD storageMethod, \n"
            + "BIN_CL_ID binClassId, \n"
            + "TEXT description, \n"
            + "INV_QTY inventoryQuantity, \n"
            + "ALLOC_QTY allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
            + "REF_FIELD_4 referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "IU_CTD_BY createdBy, \n"
            + "IU_CTD_ON createdOn, \n"
            + "UTD_BY updatedBy, \n"
            + "UTD_ON updatedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "BARCODE_ID barcodeId, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_PER_QTY cbmPerQuantity, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "REF_DOC_NO referenceDocumentNo, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
//            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "and inv_id in (select inventoryId from #inv) \n"
//            "group by itm_code, mfr_name, st_bin,LANG_ID,C_ID,PLANT_ID,WH_ID,VAR_ID,VAR_SUB_ID,STR_NO,STCK_TYP_ID,\n" +
//            "SP_ST_IND_ID,REF_ORD_NO,STR_MTD,BIN_CL_ID,TEXT,ALLOC_QTY,INV_UOM,MFR_DATE,EXP_DATE,REF_FIELD_1,REF_FIELD_2,\n" +
//            "REF_FIELD_3,REF_FIELD_5,REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,\n" +
//            "IU_CTD_BY,IU_CTD_ON,UTD_BY,UTD_ON,MFR_CODE,BARCODE_ID,CBM,CBM_UNIT,CBM_PER_QTY,ORIGIN,BRAND,REF_DOC_NO,\n" +
//            "C_TEXT,PLANT_TEXT,WH_TEXT,STATUS_TEXT,PAL_CODE,CASE_CODE,PACK_BARCODE "
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForAdditionalBins(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "binClassId") Long binClassId);

    //InventoryForDelete
    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "pack_barcode in (:packBarcodes) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "group by itm_code,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "REF_ORD_NO referenceOrderNo, \n"
            + "STR_MTD storageMethod, \n"
            + "BIN_CL_ID binClassId, \n"
            + "TEXT description, \n"
            + "INV_QTY inventoryQuantity, \n"
            + "ALLOC_QTY allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
            + "REF_FIELD_4 referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "IU_CTD_BY createdBy, \n"
            + "IU_CTD_ON createdOn, \n"
            + "UTD_BY updatedBy, \n"
            + "UTD_ON updatedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "BARCODE_ID barcodeId, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_PER_QTY cbmPerQuantity, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "REF_DOC_NO referenceDocumentNo, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "pack_barcode in (:packBarcodes) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 and bin_cl_id = :binclassId \n" +
            "and inv_id in (select inventoryId from #inv) \n"
//            "group by itm_code, mfr_name, st_bin,LANG_ID,C_ID,PLANT_ID,WH_ID,VAR_ID,VAR_SUB_ID,STR_NO,STCK_TYP_ID,\n" +
//            "SP_ST_IND_ID,REF_ORD_NO,STR_MTD,BIN_CL_ID,TEXT,ALLOC_QTY,INV_UOM,MFR_DATE,EXP_DATE,REF_FIELD_1,REF_FIELD_2,\n" +
//            "REF_FIELD_3,REF_FIELD_5,REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,\n" +
//            "IU_CTD_BY,IU_CTD_ON,UTD_BY,UTD_ON,MFR_CODE,BARCODE_ID,CBM,CBM_UNIT,CBM_PER_QTY,ORIGIN,BRAND,REF_DOC_NO,\n" +
//            "C_TEXT,PLANT_TEXT,WH_TEXT,STATUS_TEXT,PAL_CODE,CASE_CODE,PACK_BARCODE "
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForInventoryDelete(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "packBarcodes") String packBarcodes,
            @Param(value = "binclassId") Long binclassId,
            @Param(value = "manufacturerName") String manufacturerName);

    //OrderManagement
    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n" +
            "WHERE \n" +
            "itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "group by itm_code,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT * from ( \n"
            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PAL_CODE palletCode, \n"
            + "CASE_CODE caseCode, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "VAR_ID variantCode, \n"
            + "VAR_SUB_ID variantSubCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "STCK_TYP_ID stockTypeId, \n"
            + "SP_ST_IND_ID specialStockIndicatorId, \n"
            + "REF_ORD_NO referenceOrderNo, \n"
            + "STR_MTD storageMethod, \n"
            + "BIN_CL_ID binClassId, \n"
            + "TEXT description, \n"
            + "sum(INV_QTY) inventoryQuantity, \n"
            + "ALLOC_QTY allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "MFR_DATE manufacturerDate, \n"
            + "EXP_DATE expiryDate, \n"
            + "REF_FIELD_1 referenceField1, \n"
            + "REF_FIELD_2 referenceField2, \n"
            + "REF_FIELD_3 referenceField3, \n"
//            + "COALESCE(INV_QTY,0)+COALESCE(ALLOC_QTY,0) referenceField4, \n"
            + "REF_FIELD_5 referenceField5, \n"
            + "REF_FIELD_6 referenceField6, \n"
            + "REF_FIELD_7 referenceField7, \n"
            + "REF_FIELD_8 referenceField8, \n"
            + "REF_FIELD_9 referenceField9, \n"
            + "REF_FIELD_10 referenceField10, \n"
            + "LEVEL_ID levelId, \n"
            + "IU_CTD_BY createdBy, \n"
            + "IU_CTD_ON createdOn, \n"
            + "UTD_BY updatedBy, \n"
            + "UTD_ON updatedOn, \n"
            + "MFR_CODE manufacturerCode, \n"
            + "BARCODE_ID barcodeId, \n"
            + "CBM cbm, \n"
            + "CBM_UNIT cbmUnit, \n"
            + "CBM_PER_QTY cbmPerQuantity, \n"
            + "MFR_NAME manufacturerName, \n"
            + "ORIGIN origin, \n"
            + "BRAND brand, \n"
            + "REF_DOC_NO referenceDocumentNo, \n"
            + "C_TEXT companyDescription, \n"
            + "PLANT_TEXT plantDescription, \n"
            + "WH_TEXT warehouseDescription, \n"
            + "STATUS_TEXT statusDescription \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "stck_typ_id in (:stockTypeId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "and inv_id in (select inventoryId from #inv) \n" +
            "group by itm_code, mfr_name, st_bin,LANG_ID,C_ID,PLANT_ID,WH_ID,VAR_ID,VAR_SUB_ID,STR_NO,STCK_TYP_ID,\n" +
            "SP_ST_IND_ID,REF_ORD_NO,STR_MTD,BIN_CL_ID,TEXT,ALLOC_QTY,INV_UOM,MFR_DATE,EXP_DATE,REF_FIELD_1,REF_FIELD_2,\n" +
            "REF_FIELD_3,REF_FIELD_5,REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,\n" +
            "IU_CTD_BY,IU_CTD_ON,UTD_BY,UTD_ON,MFR_CODE,BARCODE_ID,CBM,CBM_UNIT,CBM_PER_QTY,ORIGIN,BRAND,REF_DOC_NO,\n" +
            "C_TEXT,PLANT_TEXT,WH_TEXT,STATUS_TEXT,PAL_CODE,CASE_CODE,PACK_BARCODE,LEVEL_ID \n" +
            ") x where inventoryQuantity > 0 "
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForOrderManagement(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "binClassId") Long binClassId,
            @Param(value = "stockTypeId") Long stockTypeId,
            @Param(value = "manufacturerName") String manufacturerName);

    @Query (value = "SELECT max(INV_ID) inventoryId into #inv FROM TBLINVENTORY WHERE is_deleted = 0 GROUP BY ITM_CODE,MFR_NAME,ST_BIN,PLANT_ID,WH_ID,C_ID,LANG_ID \r\n"
            + "SELECT LEVEL_ID AS levelId, SUM(INV_QTY) AS inventoryQty FROM tblinventory \r\n"
            + "WHERE WH_ID = :warehouseId and ITM_CODE = :itemCode AND BIN_CL_ID = :binClassId AND STCK_TYP_ID = :stockTypeId \r\n"
            + "AND C_ID = :companyCodeId and PLANT_ID = :plantId AND LANG_ID = :languageId \r\n"
            + "AND MFR_NAME = :manufacturerName AND IS_DELETED = 0 \r\n"
            + "AND INV_ID in (select inventoryId from #inv) \r\n"
            + "GROUP BY LEVEL_ID, ITM_CODE, MFR_NAME, PLANT_ID, WH_ID, C_ID, LANG_ID \r\n"
            + "HAVING SUM(INV_QTY) > 0 \r\n"
            + "ORDER BY LEVEL_ID, SUM(INV_QTY)", nativeQuery = true)
    public List<IInventory> findInventoryGroupByLevelId (
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "manufacturerName") String manufacturerName,
            @Param(value = "stockTypeId") Long stockTypeId,
            @Param(value = "binClassId") Long binClassId);

    @Query (value = "SELECT max(INV_ID) inventoryId into #inv FROM TBLINVENTORY WHERE is_deleted = 0 GROUP BY ITM_CODE,MFR_NAME,ST_BIN,PLANT_ID,WH_ID,C_ID,LANG_ID \r\n"
            + "SELECT IU_CTD_ON AS createdOn, ITM_CODE AS itemCode, MFR_NAME AS manufacturerName, SUM(INV_QTY) AS inventoryQty, ST_BIN AS storageBin\r\n"
            + "FROM tblinventory WHERE WH_ID = :warehouseId and ITM_CODE = :itemCode \r\n"
            + "AND C_ID = :companyCodeId and PLANT_ID = :plantId AND LANG_ID = :languageId \r\n"
            + "AND BIN_CL_ID = :binClassId AND STCK_TYP_ID = 1 \r\n"
            + "AND MFR_NAME = :manufacturerName AND IS_DELETED = 0 \r\n"
            + "GROUP BY ITM_CODE, MFR_NAME, IU_CTD_ON, ST_BIN\r\n"
            + "HAVING SUM(INV_QTY) > 0 \r\n"
            + "ORDER BY IU_CTD_ON, SUM(INV_QTY)", nativeQuery = true)
    public List<IInventory> findInventoryGroupByCreatedOn (
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "binClassId") Long binClassId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "manufacturerName") String manufacturerName);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String itemCode, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId,
            String itemCode, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String itemCode, Long deletionIndicator);

    List<InventoryV2>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId,
            String packBarcodes, String itemCode, String storageBin, Long deletionIndicator);

    InventoryV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndPackBarcodesAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId,
            String itemCode, String manufacturerCode, String packBarcodes, Long deletionIndicator);

    //getStorageBin - almailem
    @Query(value = "select * from tblinventory ip \n" +
            "WHERE ip.itm_code in (:itemCode) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.bin_cl_id in (:binClassId) and \n" +
            "ip.is_deleted = 0", nativeQuery = true)
    public List<InventoryV2> getInventoryStorageBinLst(@Param(value = "itemCode") String itemCode,
                                                       @Param(value = "warehouseId") String warehouseId,
                                                       @Param(value = "binClassId") Long binClassId);

    //getStorageBin - almailem
    @Query(value = "select st_bin from tblinventory ip \n" +
            "WHERE ip.itm_code in (:itemCode) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.bin_cl_id in (:binClassId) and \n" +
            "ip.is_deleted = 0", nativeQuery = true)
    public List<String> getInventoryStorageBinList(@Param(value = "itemCode") String itemCode,
                                                   @Param(value = "warehouseId") String warehouseId,
                                                   @Param(value = "binClassId") Long binClassId);


    //getInventory - almailem
    @Query(value = "select * from tblinventory ip \n" +
            "WHERE ip.itm_code in (:itemCode) and \n" +
            "ip.c_id in (:companyCode) and \n" +
            "ip.plant_id in (:plantId) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.lang_id in (:languageId) and \n" +
            "ip.mfr_code in (:manufactureCode) and \n" +
            "ip.pack_barcode in (:packBarcode) and \n" +
            "ip.is_deleted = 0", nativeQuery = true)
    public InventoryV2 getInventory(@Param(value = "itemCode") String itemCode,
                                    @Param(value = "companyCode") String companyCode,
                                    @Param(value = "plantId") String plantId,
                                    @Param(value = "warehouseId") String warehouseId,
                                    @Param(value = "manufactureCode") String manufactureCode,
                                    @Param(value = "packBarcode") String packBarcode,
                                    @Param(value = "languageId") String languageId);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndBinClassIdAndInventoryQuantityGreaterThanAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
            Long stockTypeId, Long binClassId, Double invQty, Long deletionIndicator);

    @Query(value = "SELECT ST_BIN AS storageBin, SUM(INV_QTY) AS inventoryQty FROM tblinventory \r\n"
            + "WHERE C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and \r\n"
            + "WH_ID = :warehouseId and ITM_CODE = :itemCode AND BIN_CL_ID = 1 AND STCK_TYP_ID = 1 \r\n"
            + "AND REF_FIELD_10 IN :storageSecIds AND IS_DELETED = 0 \r\n"
            + "GROUP BY ST_BIN \r\n"
            + "HAVING SUM(INV_QTY) > 0 \r\n"
            + "ORDER BY ST_BIN, SUM(INV_QTY)", nativeQuery = true)
    public List<IInventory> findInventoryGroupByStorageBinV2(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "storageSecIds") List<String> storageSecIds);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndBinClassIdAndStorageBinAndStockTypeIdAndDeletionIndicatorAndInventoryQuantityGreaterThanOrderByInventoryQuantityAscInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String itemCode, Long binClassId, String storageBin, Long stockTypeId, long l, double m);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndBinClassIdAndInventoryQuantityGreaterThan(
            String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
            List<String> storageSectionIds, long l, double m);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndReferenceField10InAndStockTypeIdAndBinClassIdAndInventoryQuantityGreaterThan(
            String languageId, String companyCode, String plantId, String warehouseId, String itemCode,
            List<String> storageSectionIds, Long stockTypeId, long l, double m);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String storageBin, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String itemCode, String packBarcodes, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndBinClassIdAndManufacturerCodeAndInventoryQuantityGreaterThanAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
            Long stockTypeId, Long binClassId, String manufacturerCode, Double invQty, Long deletionIndicator);

    InventoryV2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndReferenceDocumentNoAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
            String pickedPackCode, String refDocNumber, String manufacturerCode, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId, String packBarcodes, String itemCode, Long deletionIndicator);

    List<InventoryV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndBinClassIdAndDeletionIndicator(
            String companycode, String plantId, String languageId, String warehouseId,
            String itemCode, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentNoAndStockTypeIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String referenceDocumentNo, Long stockTypeId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndAndStockTypeIdAndBinClassIdAndManufacturerNameAndInventoryQuantityGreaterThanAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
            Long stockTypeId, Long binClassId, String manufacturerName, Double invQty, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String storageBin, String manufacturerName, Long deletionIndicator);

    InventoryV2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndReferenceDocumentNoAndManufacturerNameAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String itemCode, String pickedPackCode, String refDocNumber, String manufacturerName, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String itemCode, String manufacturerName, Long deletionIndicator);

    Optional<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String packBarcodes, String itemCode, String manufacturerName, String storageBin, Long deletionIndicator);

    List<InventoryV2>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String manufacturerName, String storageBin, Long deletionIndicator);

    @Query(value = "SELECT * FROM tblinventory where c_id = :companyId and plant_id = :plantId and wh_id = :warehouseId and lang_id = :languageId " +
            "and itm_code = :itemCode and pack_barcode = :packBarcode and st_bin = :storageBin and is_Deleted =0 and mfr_code = '3PL' order by inv_id desc", nativeQuery = true)
    public List<InventoryV2> getTargetInventory(@Param("companyId") String companyId,
                                                @Param("plantId") String plantId,
                                                @Param("warehouseId") String warehouseId,
                                                @Param("languageId") String languageId,
                                                @Param("itemCode") String itemCode,
                                                @Param("packBarcode") String barcodeId,
                                                @Param("storageBin") String storageBin);
    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);

//    @Modifying(clearAutomatically = true)
//    @Query("UPDATE tblinventory iv SET iv.STCK_TYP_ID = 1 \n" +
//            "WHERE iv.warehouseId = :warehouseId AND iv.referenceDocumentNo = :referenceDocumentNo and \n" +
//            "iv.companyCode = :companyCode and iv.plantId = :plantId and iv.languageId = :languageId and iv.stockTypeId = :stockTypeId")
//    void updateInventoryStockTypeId(@Param("warehouseId") String warehouseId,
//                                   @Param("companyCode") String companyCode,
//                                   @Param("plantId") String plantId,
//                                   @Param("languageId") String languageId,
//                                   @Param("referenceDocumentNo") String referenceDocumentNo,
//                                   @Param("stockTypeId") Long stockTypeId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:manufacturerCode, null) IS NULL OR (MFR_NAME IN (:manufacturerCode))) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "is_deleted = 0 \n" +
            "group by itm_code,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "select max(inv_id) inventoryId into #invgrp from tblinventory \n" +
            "WHERE \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:manufacturerCode, null) IS NULL OR (MFR_NAME IN (:manufacturerCode))) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "is_deleted = 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

//            "select sum(inv_qty) sumInvQty, sum(alloc_qty) sumAllocQty,sum(inv_qty)+sum(alloc_qty) totalQty, itm_code,mfr_name,st_bin \n" +
//            "into #sumInv from tblinventory where inv_id in (select inventoryId from #inv) \n" +
//            "group by itm_code,mfr_name,st_bin \n" +

            "select *,COALESCE(sumInvQty,0)+COALESCE(sumAllocQty,0) totalQty into #sumInv from (select sum(inv_qty) sumInvQty, sum(alloc_qty) sumAllocQty, itm_code,mfr_name,st_bin \n" +
            "from tblinventory where inv_id in (select inventoryId from #inv) \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) x \n" +

//            "SELECT * from ( \n" +
            "SELECT \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
//            "sum(INV_QTY) inventoryQuantity,\n" +
//            "sum(ALLOC_QTY) allocatedQuantity,\n" +
            "si.sumInvQty inventoryQuantity,\n" +
            "si.sumAllocQty allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "si.totalQty referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "left join #sumInv si on si.itm_code = iv.itm_code and si.mfr_name = iv.mfr_name and si.st_bin = iv.st_bin\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #invgrp) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:referenceDocumentNo, null) IS NULL OR (iv.ref_doc_no IN (:referenceDocumentNo))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:manufacturerCode, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerCode))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:specialStockIndicatorId, null) IS NULL OR (iv.SP_ST_IND_ID IN (:specialStockIndicatorId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:description, null) IS NULL OR (iv.TEXT IN (:description))) and \n" +
            "iv.is_deleted = 0 and (si.totalQty > 0)\n"
//            "group by itm_code, mfr_name, st_bin,LANG_ID,C_ID,PLANT_ID,WH_ID,VAR_ID,VAR_SUB_ID,STR_NO,STCK_TYP_ID,\n" +
//            "SP_ST_IND_ID,REF_ORD_NO,STR_MTD,BIN_CL_ID,TEXT,ALLOC_QTY,INV_UOM,MFR_DATE,EXP_DATE,REF_FIELD_1,REF_FIELD_2,\n" +
//            "REF_FIELD_3,REF_FIELD_4,REF_FIELD_5,REF_FIELD_6,REF_FIELD_7,REF_FIELD_8,REF_FIELD_9,REF_FIELD_10,\n" +
//            "IU_CTD_BY,IU_CTD_ON,UTD_BY,UTD_ON,MFR_CODE,BARCODE_ID,CBM,CBM_UNIT,CBM_PER_QTY,ORIGIN,BRAND,REF_DOC_NO,\n" +
//            "C_TEXT,PLANT_TEXT,WH_TEXT,STATUS_TEXT,PAL_CODE,CASE_CODE,IS_DELETED,LEVEL_ID \n"+
//            ") x where inventoryQuantity > 0"
            , nativeQuery = true)
    public List<IInventoryImpl> findInventory(@Param("companyCodeId") List<String> companyCodeId,
                                              @Param("languageId") List<String> languageId,
                                              @Param("plantId") List<String> plantId,
                                              @Param("warehouseId") List<String> warehouseId,
                                              @Param("referenceDocumentNo") List<String> referenceDocumentNo,
                                              @Param("barcodeId") List<String> barcodeId,
                                              @Param("manufacturerCode") List<String> manufacturerCode,
                                              @Param("packBarcodes") List<String> packBarcodes,
                                              @Param("itemCode") List<String> itemCode,
                                              @Param("storageBin") List<String> storageBin,
                                              @Param("description") String description,
                                              @Param("stockTypeId") List<Long> stockTypeId,
                                              @Param("specialStockIndicatorId") List<Long> specialStockIndicatorId,
                                              @Param("binClassId") List<Long> binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n" +
            "WHERE \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "is_deleted = 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:referenceDocumentNo, null) IS NULL OR (iv.ref_doc_no IN (:referenceDocumentNo))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:manufacturerCode, null) IS NULL OR (iv.MFR_CODE IN (:manufacturerCode))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:storageSectionId, null) IS NULL OR (iv.REF_FIELD_10 IN (:storageSectionId))) and \n" +
            "(COALESCE(:levelId, null) IS NULL OR (iv.level_id IN (:levelId))) and \n" +
            "(COALESCE(:specialStockIndicatorId, null) IS NULL OR (iv.SP_ST_IND_ID IN (:specialStockIndicatorId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:description, null) IS NULL OR (iv.TEXT IN (:description))) and \n" +
            "iv.is_deleted = 0 and (iv.REF_FIELD_4 > 0)\n"
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryNew(@Param("companyCodeId") List<String> companyCodeId,
                                                 @Param("languageId") List<String> languageId,
                                                 @Param("plantId") List<String> plantId,
                                                 @Param("warehouseId") List<String> warehouseId,
                                                 @Param("referenceDocumentNo") List<String> referenceDocumentNo,
                                                 @Param("barcodeId") List<String> barcodeId,
                                                 @Param("manufacturerCode") List<String> manufacturerCode,
                                                 @Param("manufacturerName") List<String> manufacturerName,
                                                 @Param("packBarcodes") List<String> packBarcodes,
                                                 @Param("itemCode") List<String> itemCode,
                                                 @Param("storageBin") List<String> storageBin,
                                                 @Param("description") String description,
                                                 @Param("stockTypeId") List<Long> stockTypeId,
                                                 @Param("storageSectionId") List<String> storageSectionId,
                                                 @Param("levelId") List<String> levelId,
                                                 @Param("specialStockIndicatorId") List<Long> specialStockIndicatorId,
                                                 @Param("binClassId") List<Long> binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:manufacturerCode, null) IS NULL OR (MFR_NAME IN (:manufacturerCode))) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "is_deleted = 0 \n" +
            "group by itm_code,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "select max(inv_id) inventoryId into #invgrp from tblinventory \n" +
            "WHERE \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:manufacturerCode, null) IS NULL OR (MFR_NAME IN (:manufacturerCode))) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "is_deleted = 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "select sum(inv_qty) sumInvQty, sum(alloc_qty) sumAllocQty,itm_code,mfr_name,st_bin \n" +
            "into #sumInv from tblinventory where inv_id in (select inventoryId from #inv) \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +


//            "SELECT * from ( \n" +
            "SELECT \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
//            "sum(INV_QTY) inventoryQuantity,\n" +
//            "sum(ALLOC_QTY) allocatedQuantity,\n" +
            "si.sumInvQty inventoryQuantity,\n" +
            "si.sumAllocQty allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "left join #sumInv si on si.itm_code = iv.itm_code and si.mfr_name = iv.mfr_name and si.st_bin = iv.st_bin\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #invgrp) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerCode, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerCode))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 \n"
            , nativeQuery = true)
    public IInventoryImpl getInventoryforExistingBin(@Param("companyCodeId") String companyCodeId,
                                                     @Param("languageId") String languageId,
                                                     @Param("plantId") String plantId,
                                                     @Param("warehouseId") String warehouseId,
                                                     @Param("manufacturerCode") String manufacturerCode,
                                                     @Param("packBarcodes") String packBarcodes,
                                                     @Param("itemCode") String itemCode,
                                                     @Param("binClassId") Long binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "is_deleted = 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 and (iv.REF_FIELD_4 > 0)\n"
            , nativeQuery = true)
    public List<IInventoryImpl> inventoryForPutAway(@Param("companyCodeId") String companyCodeId,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("warehouseId") String warehouseId,
                                                    @Param("itemCode") String itemCode,
                                                    @Param("manufacturerName") String manufacturerName,
                                                    @Param("binClassId") Long binClassId);
    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "into #inventoryTempTable from tblinventory iv \n" +                //copy to temp table to avoid deadlock
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 and (iv.REF_FIELD_4 > 0) and st_bin <> 'REC-AL-B2' \n" +

            "select * from #inventoryTempTable", nativeQuery = true)
    public List<IInventoryImpl> inventoryForPutAwaytemp(@Param("companyCodeId") String companyCodeId,
                                                        @Param("plantId") String plantId,
                                                        @Param("languageId") String languageId,
                                                        @Param("warehouseId") String warehouseId,
                                                        @Param("itemCode") String itemCode,
                                                        @Param("manufacturerName") String manufacturerName,
                                                        @Param("binClassId") Long binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n" +
            "WHERE is_deleted = 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.INV_ID inventoryId, \n" +
            "iv.LANG_ID languageId, \n" +
            "iv.C_ID companyCodeId,\n" +
            "iv.PLANT_ID plantId,\n" +
            "iv.WH_ID warehouseId,\n" +
            "iv.PAL_CODE palletCode,\n" +
            "iv.CASE_CODE caseCode,\n" +
            "iv.PACK_BARCODE packBarcodes,\n" +
            "iv.ITM_CODE itemCode,\n" +
            "iv.VAR_ID variantCode,\n" +
            "iv.VAR_SUB_ID variantSubCode,\n" +
            "iv.STR_NO batchSerialNumber,\n" +
            "iv.ST_BIN storageBin,\n" +
            "iv.STCK_TYP_ID stockTypeId,\n" +
            "iv.SP_ST_IND_ID specialStockIndicatorId,\n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturer,\n" +
            "iv.EXP_DATE expiry,\n" +
            "iv.IS_DELETED deletionIndicator,\n" +
            "iv.REF_FIELD_1 referenceField1,\n" +
            "iv.REF_FIELD_2 referenceField2,\n" +
            "iv.REF_FIELD_3 referenceField3,\n" +
            "iv.REF_FIELD_4 referenceField4,\n" +
            "iv.REF_FIELD_5 referenceField5,\n" +
            "iv.REF_FIELD_6 referenceField6,\n" +
            "iv.REF_FIELD_7 referenceField7,\n" +
            "iv.REF_FIELD_8 referenceField8,\n" +
            "iv.REF_FIELD_9 referenceField9,\n" +
            "iv.REF_FIELD_10 referenceField10,\n" +
            "iv.IU_CTD_BY createdBy,\n" +
            "iv.IU_CTD_ON createdOn,\n" +
            "FORMAT(iv.IU_CTD_ON,'dd-MM-yyyy hh:mm:ss') sCreatedOn,\n" +
            "iv.UTD_BY updatedBy,\n" +
            "iv.UTD_ON updatedOn,\n" +
            "iv.MFR_CODE manufacturerCode,\n" +
            "iv.BARCODE_ID barcodeId,\n" +
            "iv.CBM cbm,\n" +
            "iv.level_id levelId,\n" +
            "iv.CBM_UNIT cbmUnit,\n" +
            "iv.CBM_PER_QTY cbmPerQuantity,\n" +
            "iv.MFR_NAME manufacturerName,\n" +
            "iv.ORIGIN origin,\n" +
            "iv.BRAND brand,\n" +
            "iv.REF_DOC_NO referenceDocumentNo,\n" +
            "iv.C_TEXT companyDescription,\n" +
            "iv.PLANT_TEXT plantDescription,\n" +
            "iv.WH_TEXT warehouseDescription,\n" +
            "iv.STCK_TYP_TEXT stockTypeDescription,\n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "iv.is_deleted = 0 order by iv.REF_FIELD_4 desc", nativeQuery = true)
    public List<IInventoryImpl> inventoryForStockCount(@Param("companyCodeId") String companyCodeId,
                                                       @Param("plantId") String plantId,
                                                       @Param("languageId") String languageId,
                                                       @Param("warehouseId") String warehouseId,
                                                       @Param("itemCode") String itemCode,
                                                       @Param("manufacturerName") String manufacturerName,
                                                       @Param("binClassId") Long binClassId);


    Page<InventoryV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, Long deletionIndicator, Pageable pageable);

    // SQL Query for getting Inventory
    @Query(value = "SELECT INV_QTY AS inventoryQty, INV_UOM AS inventoryUom FROM tblinventory "
            + "WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND PACK_BARCODE = :packbarCode AND "
            + "ITM_CODE = :itemCode AND MFR_NAME = :manufacturerName AND ST_BIN = :storageBin", nativeQuery = true)
    public IInventory findInventoryForPeriodicRunV2(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "manufacturerName") String manufacturerName,
            @Param(value = "storageBin") String storageBin,
            @Param(value = "packbarCode") String packbarCode);

    @Query(value = "SELECT SUM(INV_QTY) FROM tblinventory \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND MFR_NAME = :manufacturerName AND ITM_CODE = :itemCode AND \r\n"
            + " BIN_CL_ID = 1 \r\n"
            + " GROUP BY ITM_CODE", nativeQuery = true)
    public Double getInventoryQtyCountV2(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "manufacturerName") String manufacturerName,
            @Param(value = "itemCode") String itemCode);

    @Query(value ="select max(inv_id) inventoryId into #inv from tblinventory \n" +
            "WHERE is_deleted = 0 \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT SUM(REF_FIELD_4) FROM tblinventory \r\n"
            + " WHERE C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND MFR_NAME = :manufacturerName AND ITM_CODE = :itemCode AND \r\n"
            + " BIN_CL_ID in (1,7) and inv_id in (select inventoryId from #inv) and IS_DELETED = 0 \r\n"
            + " GROUP BY ITM_CODE, MFR_NAME, PLANT_ID, WH_ID, C_ID, LANG_ID", nativeQuery = true)
    public Double getInventoryQtyCountForInvMmt(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "manufacturerName") String manufacturerName,
            @Param(value = "itemCode") String itemCode);

    InventoryV2 findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String storageBin, Long stockTypeId, Long specialStockIndicatorId, long l);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId,
            String itemCode, String manufacturerName, Long binclassId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndBinClassIdAndStockTypeIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId,
            String itemCode, String manufacturerName, Long binclassId, Long stockTypeId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId,
            String itemCode, String manufacturerName, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String packBarcodes, String itemCode, String manufacturerName, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String manufacturerName, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceField1AndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String packBarcodes, String itemCode, String manufacturerName, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndPackBarcodesAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCode, String plantId, String languageId, String warehouseId, String itemCode,
            String manufacturerName, String number, Long deletionIndicator);

    InventoryV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndPackBarcodesAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCode, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String number, Long deletionIndicator);

    InventoryV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCode, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String number, Long binClassId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String storageBin, String manufacturerName, Long binClassId, Long deletionIndicator);

    InventoryV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCode, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String number, Long binClassId, Long deletionIndicator);

    InventoryV2 findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndBinClassIdAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode,
            String packBarcodes, Long binClassId, String manufacturerName, Long deletionIndicator);

    InventoryV2 findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId, String packBarcodes, String itemCode, String manufacturerName, Long binClassId, Long deletionIndicator);

    @Query(value =
            "select max(inv_id) inventoryId into #inv from tblinventory \n" +
            "WHERE \n" +
            "(COALESCE(:itemCodes, null) IS NULL OR (ITM_CODE IN (:itemCodes))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:companyCodeIds, null) IS NULL OR (c_id IN (:companyCodeIds))) and \n" +
            "(COALESCE(:languageIds, null) IS NULL OR (lang_id IN (:languageIds))) and \n" +
            "(COALESCE(:plantIds, null) IS NULL OR (plant_id IN (:plantIds))) and \n" +
            "(COALESCE(:warehouseIds, null) IS NULL OR (wh_id IN (:warehouseIds))) and \n" +
            "is_deleted = 0 \n" +
            "group by itm_code,mfr_name,plant_id,wh_id,c_id,lang_id \n" +

            "Select itemCode, languageId, companyCodeId, plantId, warehouseId, manufacturerSKU, itemText, \r\n"
            + "companyDescription, plantDescription, warehouseDescription, barcodeId, manufacturerName, \r\n"
            + "onHandQty, damageQty, holdQty, (COALESCE(onHandQty,0) + COALESCE(damageQty,0) + COALESCE(holdQty,0)) as availableQty from \r\n"
            + "(Select i.itm_code as itemCode, i.lang_id as languageId, i.c_id as companyCodeId, i.plant_id as plantId, i.wh_id as warehouseId, \r\n"
            + "i.mfr_name as manufacturerSKU, i.text as itemText, i.c_text as companyDescription, i.plant_text as plantDescription, i.wh_text as warehouseDescription, \r\n"
            + "i.barcode_id as barcodeId, i.mfr_name as manufacturerName, \r\n"
            + "(case \r\n"
            + "WHEN :stockTypeText in ('ALL','ONHAND') THEN (select sum(CASE WHEN inv_qty > 0 THEN inv_qty ELSE 0 END) + sum(COALESCE(alloc_qty,0)) from tblinventory \r\n"
            + "where lang_id IN (:languageIds) and c_id IN (:companyCodeIds) and plant_id IN (:plantIds) and wh_id IN (:warehouseIds)  \r\n"
            + "and itm_code = i.itm_code and mfr_name = i.mfr_name and stck_typ_id = 1 and bin_cl_id = 1 and IS_DELETED = 0 \r\n"
            + "and inv_id in (select inventoryId from #inv)) \r\n"
            + "ELSE 0 \r\n"
            + "END ) as onHandQty,\r\n"
            + "(case \r\n"
            + "WHEN :stockTypeText = 'DAMAGED' THEN (select sum(CASE WHEN inv_qty > 0 THEN inv_qty ELSE 0 END) + sum(COALESCE(alloc_qty,0)) from tblinventory \r\n"
            + "where lang_id IN (:languageIds) and c_id IN (:companyCodeIds) and plant_id IN (:plantIds) and wh_id IN (:warehouseIds)  \r\n"
            + "and itm_code = i.itm_code and mfr_name = i.mfr_name and bin_cl_id = 7 and IS_DELETED = 0 \r\n"
            + "and inv_id in (select inventoryId from #inv)) \r\n"
            + "ELSE 0\r\n"
            + "END ) as damageQty,\r\n"
            + "(case \r\n"
            + "WHEN :stockTypeText = 'HOLD' THEN (select sum(CASE WHEN inv_qty > 0 THEN inv_qty ELSE 0 END) from tblinventory \r\n"
            + "where lang_id IN (:languageIds) and c_id IN (:companyCodeIds) and plant_id IN (:plantIds) and wh_id IN (:warehouseIds)  \r\n"
            + "and itm_code = i.itm_code and stck_typ_id = 7 and IS_DELETED = 0 \r\n"
            + "and inv_id in (select inventoryId from #inv)) \r\n"
            + "ELSE 0\r\n"
            + "END ) as holdQty \r\n"
            + "from tblinventory i \r\n"
            + "where \r\n"
            +"(:itemText IS NULL or (i.text = :itemText)) \r\n"
            + "AND i.lang_id IN (:languageIds) \r\n"
            + "AND i.c_id IN (:companyCodeIds) \r\n"
            + "AND i.plant_id IN (:plantIds) \r\n"
            + "AND i.wh_id IN (:warehouseIds) \r\n"
            + "AND (COALESCE(:itemCodes, null) IS NULL OR (i.itm_code IN (:itemCodes))) \r\n"
            + "AND (COALESCE(:manufacturerName, null) IS NULL OR (i.mfr_name IN (:manufacturerName))) \r\n"
            + "AND i.IS_DELETED = 0 \r\n"
            + "group by i.itm_code, i.mfr_name, i.lang_id, i.c_id, i.plant_id, i.wh_id, i.c_text, i.plant_text, i.wh_text, i.barcode_id, i.text) as X", nativeQuery = true)
    List<StockReportImpl> getAllStockReportNew(
            @Param(value = "languageIds") List<String> languageId,
            @Param(value = "companyCodeIds") List<String> companyCodeId,
            @Param(value = "plantIds") List<String> plantId,
            @Param(value = "warehouseIds") List<String> warehouseId,
            @Param(value = "itemCodes") List<String> itemCode,
            @Param(value = "itemText") String itemText,
            @Param(value = "manufacturerName") List<String> manufacturerName,
            @Param(value = "stockTypeText") String stockTypeText);

    //Stock Report New
//    @Query(value =
//            "create table #stockreport \n" +
//            "(C_ID NVARCHAR(10), \n" +
//            "PLANT_ID NVARCHAR(10), \n" +
//            "LANG_ID NVARCHAR(10), \n" +
//            "WH_ID NVARCHAR(10), \n" +
//            "ITM_CODE NVARCHAR(200), \n" +
//            "MFR_NAME NVARCHAR(100), \n" +
//            "TEXT NVARCHAR(255), \n" +
//            "INV_QTY FLOAT, \n" +
//            "DMG_QTY FLOAT, \n" +
//            "C_TEXT NVARCHAR(50), \n" +
//            "PLANT_TEXT NVARCHAR(50), \n" +
//            "WH_TEXT NVARCHAR(50), \n" +
//            "BAR_CODE NVARCHAR(100), \n" +
//            "PRIMARY KEY (C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_NAME)); \n" +
//
//            //itemCode and Description from imbasicData1 to temp table
//            "INSERT INTO #stockreport(C_ID,PLANT_ID,WH_ID,LANG_ID,ITM_CODE,TEXT,MFR_NAME,C_TEXT,PLANT_TEXT,WH_TEXT) \n" +
//            "SELECT C_ID,PLANT_ID,WH_ID,LANG_ID,ITM_CODE,TEXT,MFR_PART,C_TEXT,PLANT_TEXT,WH_TEXT FROM TBLIMBASICDATA1 \n" +
//            "WHERE \n" +
//            "IS_DELETED = 0 AND \n" +
//            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and\n" +
//            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and\n" +
//            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and\n" +
//            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and\n" +
//            "(COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))) and\n" +
//            "(COALESCE(:manufacturerName, null) IS NULL OR (mfr_part IN (:manufacturerName))) and\n" +
//            "(COALESCE(:itemText, null) IS NULL OR (text IN (:itemText))) \n" +

            // company Description from tblCompanyId to temp table
//            "UPDATE TH SET TH.C_TEXT = X.VALUE FROM #stockreport TH INNER JOIN \n" +
//            "(SELECT C_ID,LANG_ID,C_TEXT VALUE FROM TBLCOMPANYID \n" +
//            "WHERE \n" +
//            "IS_DELETED = 0 AND \n" +
//            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) \n" +
//            ") X ON \n" +
//            "X.C_ID = TH.C_ID AND X.LANG_ID = TH.LANG_ID \n" +

            // plant Description from tblplantId to temp table
//            "UPDATE TH SET TH.PLANT_TEXT = X.VALUE FROM #stockreport TH INNER JOIN \n" +
//            "(SELECT C_ID,PLANT_ID,LANG_ID,PLANT_TEXT VALUE FROM TBLPLANTID \n" +
//            "WHERE \n" +
//            "IS_DELETED = 0 AND \n" +
//            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) \n" +
//            ") X ON \n" +
//            "X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.LANG_ID = TH.LANG_ID \n" +

            // warehouse Description from tblwarehouseId to temp table
//            "UPDATE TH SET TH.WH_TEXT = X.VALUE FROM #stockreport TH INNER JOIN \n" +
//            "(SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,WH_TEXT VALUE FROM TBLWAREHOUSEID \n" +
//            "WHERE \n" +
//            "IS_DELETED = 0 AND \n" +
//            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
//            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) \n" +
//            ") X ON \n" +
//            "X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.WH_ID = TH.WH_ID AND X.LANG_ID = TH.LANG_ID \n" +

            // Barcode from tblimpartner to temp table
//            "UPDATE TH SET TH.BAR_CODE = X.VALUE FROM #stockreport TH INNER JOIN \n" +
//            "(SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_NAME,PARTNER_ITM_BAR VALUE FROM TBLIMPARTNER \n" +
//            "WHERE \n" +
//            "IS_DELETED = 0 AND \n" +
//            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
//            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//            "(COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))) and \n" +
//            "(COALESCE(:manufacturerName, null) IS NULL OR (mfr_name IN (:manufacturerName))) \n" +
//            ") X ON \n" +
//            "X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.WH_ID = TH.WH_ID AND X.LANG_ID = TH.LANG_ID AND \n" +
//            "X.ITM_CODE = TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME \n" +

//            "select max(inv_id) inventoryId into #inv from tblinventory \n" +
//            "WHERE \n" +
//            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
//            "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n" +
//            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
//            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
//            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
//            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
//            "is_deleted = 0 \n" +
//            "group by itm_code,mfr_name \n" +
//
//            // inv_qty from tblinventory to temp table
//            "UPDATE TH SET TH.INV_QTY = X.INV_QTY FROM #stockreport TH INNER JOIN \n" +
//            "(select c_id, plant_id, lang_id, wh_id, itm_code, mfr_name, ref_field_4 INV_QTY \n"+
//            "from tblinventory \r\n"+
//            "where stck_typ_id = 1 and bin_cl_id = 1 and IS_DELETED = 0 and :stockTypeText in ('ALL','ONHAND') \r\n"+
//            "and inv_id in (select inventoryId from #inv) \r\n"+
//            ") X ON \n" +
//            "X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.WH_ID = TH.WH_ID AND X.LANG_ID = TH.LANG_ID AND \n" +
//            "X.ITM_CODE = TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME \n" +
//
//            // dmg_qty from tblinventory to temp table
//            "UPDATE TH SET TH.DMG_QTY = X.DMG_QTY FROM #stockreport TH INNER JOIN \n" +
//            "(select c_id, plant_id, lang_id, wh_id, itm_code, mfr_name, ref_field_4 DMG_QTY \n"+
//            "from tblinventory \r\n"+
//            "where bin_cl_id = 7 and IS_DELETED = 0 and :stockTypeText in ('ALL','DAMAGED')\r\n"+
//            "and inv_id in (select inventoryId from #inv) \r\n"+
//            ") X ON \n" +
//            "X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.WH_ID = TH.WH_ID AND X.LANG_ID = TH.LANG_ID AND \n" +
//            "X.ITM_CODE = TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME \n" +
//
//            "select \n" +
//            "C_ID companyCodeId, \n" +
//            "PLANT_ID plantId, \n" +
//            "LANG_ID languageId, \n" +
//            "WH_ID warehouseId, \n" +
//            "ITM_CODE itemCode, \n" +
//            "MFR_NAME manufacturerName, \n" +
//            "MFR_NAME manufacturerSKU, \n" +
//            "TEXT itemText, \n" +
//            "COALESCE(INV_QTY,0) onHandQty, \n" +
//            "COALESCE(DMG_QTY,0) damageQty, \n" +
//            "COALESCE(INV_QTY,0)+COALESCE(DMG_QTY,0) availableQty, \n" +
//            "C_TEXT companyDescription, \n" +
//            "PLANT_TEXT plantDescription, \n" +
//            "WH_TEXT warehouseDescription, \n" +
//            "BAR_CODE barcodeId \n" +
//            "from  \n" +
//            "#stockreport ", nativeQuery = true)
//    List<StockReportImpl> stockReportNew(
//            @Param(value = "languageId") List<String> languageId,
//            @Param(value = "companyCodeId") List<String> companyCodeId,
//            @Param(value = "plantId") List<String> plantId,
//            @Param(value = "warehouseId") List<String> warehouseId,
//            @Param(value = "itemCode") List<String> itemCode,
//            @Param(value = "itemText") List<String> itemText,
//            @Param(value = "manufacturerName") List<String> manufacturerName,
//            @Param(value = "stockTypeText") String stockTypeText);

    //Stock Report New
    @Query(value =
            "create table #stockreport \n" +
                    "(C_ID NVARCHAR(10), \n" +
                    "PLANT_ID NVARCHAR(10), \n" +
                    "LANG_ID NVARCHAR(10), \n" +
                    "WH_ID NVARCHAR(10), \n" +
                    "ITM_CODE NVARCHAR(200), \n" +
                    "MFR_NAME NVARCHAR(100), \n" +
                    "TEXT NVARCHAR(255), \n" +
                    "INV_QTY FLOAT, \n" +
                    "ALLOC_QTY FLOAT, \n" +
                    "TOT_QTY FLOAT, \n" +
                    "C_TEXT NVARCHAR(50), \n" +
                    "PLANT_TEXT NVARCHAR(50), \n" +
                    "WH_TEXT NVARCHAR(50), \n" +
                    "PRIMARY KEY (C_ID,PLANT_ID,LANG_ID,WH_ID,ITM_CODE,MFR_NAME)); \n" +

                    //itemCode and Description from imbasicData1 to temp table
                    "INSERT INTO #stockreport(C_ID,PLANT_ID,WH_ID,LANG_ID,ITM_CODE,TEXT,MFR_NAME,C_TEXT,PLANT_TEXT,WH_TEXT) \n" +
                    "SELECT C_ID,PLANT_ID,WH_ID,LANG_ID,ITM_CODE,TEXT,MFR_PART,C_TEXT,PLANT_TEXT,WH_TEXT FROM TBLIMBASICDATA1 \n" +
                    "WHERE \n" +
                    "IS_DELETED = 0 AND \n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and\n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and\n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and\n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and\n" +
                    "(COALESCE(:itemCode, null) IS NULL OR (itm_code IN (:itemCode))) and\n" +
                    "(COALESCE(:manufacturerName, null) IS NULL OR (mfr_part IN (:manufacturerName))) and\n" +
                    "(COALESCE(:itemText, null) IS NULL OR (text IN (:itemText))) \n" +

                    "select max(inv_id) inventoryId into #inv from tblinventory WHERE is_deleted = 0 \n" +
                    "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

                    // inv_qty from tblinventory to temp table
                    "UPDATE TH SET TH.INV_QTY = X.INV_QTY,TH.ALLOC_QTY = X.ALLOC_QTY,TH.TOT_QTY = X.REF_FIELD_4 FROM #stockreport TH INNER JOIN \n" +
                    "(select c_id, plant_id, lang_id, wh_id, itm_code, mfr_name, INV_QTY, ALLOC_QTY, REF_FIELD_4 \n"+
                    "from tblinventory \r\n"+
                    "where is_deleted = 0 and \r\n"+
                    "inv_id in (select inventoryId from #inv) \r\n"+
                    ") X ON \n" +
                    "X.C_ID = TH.C_ID AND X.PLANT_ID = TH.PLANT_ID AND X.WH_ID = TH.WH_ID AND X.LANG_ID = TH.LANG_ID AND \n" +
                    "X.ITM_CODE = TH.ITM_CODE AND X.MFR_NAME = TH.MFR_NAME \n" +

                    "select \n" +
                    "C_ID companyCodeId, \n" +
                    "PLANT_ID plantId, \n" +
                    "LANG_ID languageId, \n" +
                    "WH_ID warehouseId, \n" +
                    "ITM_CODE itemCode, \n" +
                    "MFR_NAME manufacturerName, \n" +
                    "MFR_NAME manufacturerSKU, \n" +
                    "TEXT itemText, \n" +
                    "COALESCE(INV_QTY,0) invQty, \n" +
                    "COALESCE(ALLOC_QTY,0) allocQty, \n" +
                    "COALESCE(TOT_QTY,0) totalQty, \n" +
                    "C_TEXT companyDescription, \n" +
                    "PLANT_TEXT plantDescription, \n" +
                    "WH_TEXT warehouseDescription \n" +
                    "from  \n" +
                    "#stockreport ", nativeQuery = true)
    List<StockReportImpl> stockReportNew(
            @Param(value = "languageId") List<String> languageId,
            @Param(value = "companyCodeId") List<String> companyCodeId,
            @Param(value = "plantId") List<String> plantId,
            @Param(value = "warehouseId") List<String> warehouseId,
            @Param(value = "itemCode") List<String> itemCode,
            @Param(value = "itemText") List<String> itemText,
            @Param(value = "manufacturerName") List<String> manufacturerName);

    InventoryV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String packBarcodes,
            String itemCode, String manufacturerName, String storageBin, Long stockTypeId, Long specialStockIndicatorId, Long deletionIndicator);

    List<InventoryV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String packBarcodes,
            String itemCode, String manufacturerName, String storageBin, Long deletionIndicator);

    List<InventoryV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String packBarcodes, String itemCode,
            String manufacturerName, String storageBin, Long stockTypeId, Long specialStockIndicatorId, Long deletionIndicator);

    List<InventoryV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);

    @Query(value = "select count(st_bin) numbersOfBin, count(total_tpl_cbm) numbersOfCBM, tpl_partner_id as partnerId, TPL_PARTNER_TEXT as partnerName from tblinventory where\n" +
            "inv_id in (select max(inv_id) from tblinventory where is_deleted = 0 and ref_field_4 > 0 and bin_cl_id = 1 and (COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode))) and (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId)))" +
            " and (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) \n" +
            "group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) and (COALESCE(:threePLPartnerId, null) IS NULL OR (tpl_partner_id IN (:threePLPartnerId))) ",nativeQuery = true)
    CBMBinReport getNoOfBin(@Param("companyCode") String companyCode,
                            @Param("plantId") String plantId,
                            @Param("warehouseId") String warehouseId,
                            @Param("languageId") String languageId,
                            @Param("threePLPartnerId") String threePLPartnerId);

//    @Query(value = "select count(st_bin) numbersOfBin, count(total_tpl_cbm) numbersOfCBM, tpl_partner_id as partnerId, MAX(TPL_PARTNER_TEXT) as partnerName from tblinventory inv1 join
//(select max(inv_id) as max_inv from tblinventory where is_deleted = 0 and ref_field_4 > 0 and bin_cl_id = 1 and
//(COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode))) and (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId)))
//and (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId)))
//group by st_bin,plant_id,wh_id,c_id,lang_id) as inv2 ON inv1.inv_id = inv2.max_inv where
//(COALESCE(:threePLPartnerId, null) IS NULL OR (inv1.tpl_partner_id IN (:threePLPartnerId)))
//group by inv1.tpl_partner_id ",nativeQuery = true)
//    List<CBMBinReport> getNoOfBinV2(@Param("companyCode") String companyCode,
//                            @Param("plantId") String plantId,
//                            @Param("warehouseId") String warehouseId,
//                            @Param("languageId") String languageId,
//                            @Param("threePLPartnerId") String threePLPartnerId);

//
//    @Query(value = "\n" +
//            "WITH cbm AS (\n" +
//            "    SELECT\n" +
//            "        COUNT(total_tpl_cbm) AS numbersOfCBM,\n" +
//            "        tpl_partner_id AS partnerId\n" +
//            "    FROM tblinventory\n" +
//            "    WHERE (COALESCE(:threePLPartnerId, null) IS NULL OR (tpl_partner_id IN (:threePLPartnerId))) \n" +
//            "      AND is_deleted = 0\n" +
//            "      AND (COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode))) \n" +
//            "      AND (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) \n" +
//            "      AND (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) \n" +
//            "      AND (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) \n" +
//            "    GROUP BY tpl_partner_id\n" +
//            ")\n" +
//            "SELECT\n" +
//            "    COUNT(iv.st_bin) AS numbersOfBin,\n" +
//            "    iv.tpl_partner_id AS partnerId,\n" +
//            "    MAX(iv.tpl_partner_text) AS partnerName,\n" +
//            "    cbm.numbersOfCBM\n" +
//            "FROM tblinventory iv\n" +
//            "JOIN cbm ON iv.tpl_partner_id = cbm.partnerId\n" +
//            "WHERE iv.inv_id IN (\n" +
//            "    SELECT MAX(inv1.inv_id) \n" +
//            "    FROM tblinventory inv1 \n" +
//            "    WHERE inv1.is_deleted = 0 \n" +
//            "      AND inv1.ref_field_4 > 0\n" +
//            "      AND inv1.bin_cl_id = 1\n" +
//            "      AND (COALESCE(:companyCode, null) IS NULL OR (inv1.c_id IN (:companyCode))) \n" +
//            "      AND (COALESCE(:plantId, null) IS NULL OR (inv1.plant_id IN (:plantId))) \n" +
//            "      AND (COALESCE(:warehouseId, null) IS NULL OR (inv1.wh_id IN (:warehouseId))) \n" +
//            "      AND (COALESCE(:languageId, null) IS NULL OR (inv1.lang_id IN (:languageId))) \n" +
//            "      AND (COALESCE(:threePLPartnerId, null) IS NULL OR (inv1.tpl_partner_id IN (:threePLPartnerId))) \n"+
//            "    GROUP BY inv1.st_bin, inv1.plant_id, inv1.wh_id, inv1.c_id, inv1.lang_id\n" +
//            ")\n" +
//            "GROUP BY iv.tpl_partner_id, cbm.numbersOfCBM",nativeQuery = true)
//    List<CBMBinReport> getNoOfBinV2(@Param("companyCode") String companyCode,
//                                    @Param("plantId") String plantId,
//                                    @Param("warehouseId") String warehouseId,
//                                    @Param("languageId") String languageId,
//                                    @Param("threePLPartnerId") String threePLPartnerId);

    @Query(value = "SELECT \n" +
            "        sum(inv_qty) AS numbersOfCBM, \n" +
            "        tpl_partner_id AS partnerId \n" +
            "    FROM tblinventory \n" +
            "    WHERE (COALESCE(:threePLPartnerId, null) IS NULL OR (tpl_partner_id IN (:threePLPartnerId))) AND \n" +
            "     is_deleted = 0 and inv_qty > 0 \n" +
            "      AND (COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode)))  \n" +
            "      AND (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId)))  \n" +
            "      AND (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId)))  \n" +
            "      AND (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId)))  \n" +
            " and inv_id in (select max(inv_id) from tblinventory where is_deleted = 0  group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id)\n" +
            "    GROUP BY tpl_partner_id ",nativeQuery = true)
    List<OccupancyBinReportResponse> getCbm(@Param("companyCode") String companyCode,
                                    @Param("plantId") String plantId,
                                    @Param("warehouseId") String warehouseId,
                                    @Param("languageId") String languageId,
                                    @Param("threePLPartnerId") String threePLPartnerId);

    @Query(value = " SELECT \n" +
            "    COUNT(iv.st_bin) AS numbersOfBin,  \n" +
//            "    iv.tpl_partner_id AS partnerId, \n" +
            "    MAX(iv.tpl_partner_text) AS partnerName\n" +
            "FROM tblinventory iv \n" +
            "WHERE iv.is_deleted = 0  \n" +
            "      AND iv.ref_field_4 > 0  and iv.inv_id in (select max(inv_id) from tblinventory where is_deleted = 0 \n" +
            "  group by tpl_partner_id,st_bin,plant_id,wh_id,c_id,lang_id) and tpl_partner_id =:threePLPartnerId group by tpl_partner_id",nativeQuery = true)
    OccupancyBinReportResponse getNoOfBinV2(@Param("threePLPartnerId") String threePLPartnerId);



    @Query(value = "select count(st_bin) as occupiedBin from tblinventory where\n" +
            " inv_id in (select max(inv_id) from tblinventory where is_deleted = 0 and ref_field_4 > 0 and bin_cl_id = 1 \n" +
            " and (COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode))) and (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) \n" +
            " and (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) \n" +
            " group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) and (COALESCE(:threePLPartnerId, null) IS NULL OR (tpl_partner_id IN (:threePLPartnerId))) ",nativeQuery = true)
    public Long getTotalStorageBin(@Param("companyCode") String companyCode,
                                   @Param("plantId") String plantId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("languageId") String languageId,
                                   @Param("threePLPartnerId") String threePLPartnerId);

//    @Query(value = "select count(inv1.st_bin) as occupiedBin,inv1.tpl_partner_id as partnerId,inv1.tpl_partner_text as partnerName from tblinventory inv1 \n" +
//            " join (select max(inv_id) as max_inv from tblinventory where is_deleted =0 AND ref_field_4 > 0  AND bin_cl_id = 1 AND (COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode))) and (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) \n" +
//            " and (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) group by st_bin, plant_id, wh_id, c_id, lang_id,tpl_partner_id) \n" +
//            " as inv2 ON inv1.inv_id = inv2.max_inv where (COALESCE(:threePLPartnerId, null) IS NULL OR (inv1.tpl_partner_id IN (:threePLPartnerId))) group by inv1.tpl_partner_id,inv1.tpl_partner_text",nativeQuery = true)
//    public List<OccupancyBinReportResponse> getTotalStorageBinV2(@Param("companyCode") String companyCode,
//                                                           @Param("plantId") String plantId,
//                                                           @Param("warehouseId") String warehouseId,
//                                                           @Param("languageId") String languageId,
//                                                           @Param("threePLPartnerId") String threePLPartnerId);

    @Query(value = "SELECT \n" +
            "    COUNT(iv.st_bin) AS occupiedBin , \n" +
            "    iv.tpl_partner_id AS partnerId, \n" +
            "    MAX(iv.tpl_partner_text) AS partnerName\n" +
            "    \n" +
            "FROM tblinventory iv \n" +
            "WHERE iv.is_deleted = 0 and (COALESCE(:companyCode, null) IS NULL OR (c_id IN (:companyCode))) and (COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) \n" +
            " and (COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and (COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) AND iv.ref_field_4 > 0  and iv.inv_id in (select max(inv_id) from tblinventory where is_deleted = 0 \n" +
            "\t  group by tpl_partner_id,st_bin,plant_id,wh_id,c_id,lang_id) and (COALESCE(:threePLPartnerId, null) IS NULL OR (tpl_partner_id IN (:threePLPartnerId))) group by tpl_partner_id",nativeQuery = true)
    public List<OccupancyBinReportResponse> getTotalStorageBinV2(@Param("companyCode") String companyCode,
                                                                 @Param("plantId") String plantId,
                                                                 @Param("warehouseId") String warehouseId,
                                                                 @Param("languageId") String languageId,
                                                                 @Param("threePLPartnerId") String threePLPartnerId);

    //---------
    //dashBoard API
    @Query(value =
            "create table #stBinDashBoard \n" +
                    "(companyCodeId NVARCHAR(10), \n" +
                    "plantId NVARCHAR(10), \n" +
                    "languageId NVARCHAR(10), \n" +
                    "warehouseId NVARCHAR(10), \n" +
                    "storageBin NVARCHAR(25), \n" +
                    "statusId NVARCHAR(5), \n" +
                    "binClassId NVARCHAR(5), \n" +
                    "quantity FLOAT, \n" +
                    "PRIMARY KEY (companyCodeId,plantId,languageId,warehouseId,storageBin)); \n" +

                    //storageBin from tblstoragebin to temp table
                    "INSERT INTO #stBinDashBoard(companyCodeId,plantId,languageId,warehouseId,storageBin,statusId,binClassId) \n" +
                    "SELECT C_ID,PLANT_ID,LANG_ID,WH_ID,ST_BIN,STATUS_ID,BIN_CL_ID FROM tblstoragebin \n" +
                    "WHERE \n" +
                    "IS_DELETED = 0 AND \n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and\n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and\n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and\n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and\n" +
                    "(COALESCE(:storageBin, null) IS NULL OR (st_bin IN (:storageBin))) and\n" +
                    "(COALESCE(:binClassId, null) IS NULL OR (bin_cl_id IN (:binClassId))) \n" +

                    "select max(inv_id) inventoryId into #inv from tblinventory WHERE is_deleted = 0 and \n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and\n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and\n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and\n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) \n" +
                    "group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n" +

                    // inv_qty from tblinventory to temp table
                    "UPDATE TH SET TH.quantity = X.TOT_QTY FROM #stBinDashBoard TH INNER JOIN \n" +
                    "(select c_id, plant_id, lang_id, wh_id, st_bin, ISNULL(sum(REF_FIELD_4),0) TOT_QTY\n"+
                    "from tblinventory \r\n"+
                    "where is_deleted = 0 and \r\n"+
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and\n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and\n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and\n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and\n" +
                    "inv_id in (select inventoryId from #inv) \r\n"+
                    "group by st_bin,plant_id,wh_id,c_id,lang_id) X ON \n" +
                    "X.C_ID = TH.companyCodeId AND X.PLANT_ID = TH.plantId AND X.WH_ID = TH.warehouseId AND X.LANG_ID = TH.languageId AND \n" +
                    "X.ST_BIN = TH.storageBin \n" +

                    "select \n" +
                    "storageBin, \n" +
                    "statusId, \n" +
                    "(case when quantity > 0 then 'Occupied' else 'Empty' end) statusDescription, \n" +
                    "quantity \n" +
                    "from  \n" +
                    "#stBinDashBoard ", nativeQuery = true)
    List<StorageBinDashBoardImpl> getStorageBinDashBoardV3(@Param(value = "companyCodeId") String companyCodeId,
                                                           @Param(value = "plantId") String plantId,
                                                           @Param(value = "languageId") String languageId,
                                                           @Param(value = "warehouseId") String warehouseId,
                                                           @Param(value = "storageBin") List<String> storageBin,
                                                           @Param(value = "binClassId") Long binClassId);


    @Query(value = "select PARTNER_CODE as partnerCode from tblpickupline where c_id = :companyId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and \n" +
            "partner_code = :partnerCode and is_deleted = 0", nativeQuery = true)
    public Double getpartnerCode(@Param("companyId") String companyId,
                               @Param("plantId") String plantId,
                               @Param("languageId") String languageId,
                               @Param("warehouseId") String warehouseId,
                               @Param("partnerCode") String partnerCode);
}