package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.dto.IInventory;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface InventoryV2Repository extends PagingAndSortingRepository<InventoryV2, Long>,
        JpaSpecificationExecutor<InventoryV2>, StreamableJpaSpecificationRepository<InventoryV2> {


    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId,
            String warehouseId, String referenceDocumentNo, String itemCode, Long deletionIndicator);

    Optional<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String packBarcodes, String itemCode, String storageBin, String manufacturerCode, Long deletionIndicator);

    Optional<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String packBarcodes, String itemCode, String manufacturerCode, Long deletionIndicator);


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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n"

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

    //OrderManagement
    @Query(value = "\n" +
            "SELECT * FROM (\n" +
            "    SELECT \n" +
            "           SUM(INV_QTY) inventoryQuantity,\n" +
            "           ALLOC_QTY allocatedQuantity,\n" +
            "           BARCODE_ID barcodeId,\n" +
            "           REF_DOC_NO referenceDocumentNo,\n" +
            "           INV_ID inventoryId\n" +
            "    FROM tblinventory\n" +
            "    WHERE \n" +
            "        itm_code IN (:itemCode) AND\n" +
            "        wh_id IN (:warehouseId) AND\n" +
            "        bin_cl_id IN (:binClassId) AND\n" +
            "        stck_typ_id IN (:stockTypeId) AND\n" +
            "        plant_id IN (:plantId) AND\n" +
            "        lang_id IN (:languageId) AND\n" +
            "        c_id IN (:companyCodeId) AND\n" +
            "        is_deleted = 0 AND\n" +
            "        inv_id IN (\n" +
            "            SELECT max(inv_id)\n" +
            "            FROM tblinventory\n" +
            "            WHERE \n" +
            "                itm_code in (:itemCode) AND\n" +
            "                wh_id in (:warehouseId) AND\n" +
            "                bin_cl_id in (:binClassId) AND\n" +
            "                plant_id in (:plantId) AND\n" +
            "                lang_id in (:languageId) AND\n" +
            "                c_id in (:companyCodeId) AND\n" +
            "                is_deleted = 0\n" +
            "            GROUP BY itm_code, barcode_id, pack_barcode, st_bin, plant_id, wh_id, c_id, lang_id\n" +
            "        )\n" +
            "    group by itm_code, st_bin,\n" +
            "            ALLOC_QTY,\n" +
            "            BARCODE_ID,REF_DOC_NO,\n" +
            "            INV_ID \n" +
            ") x \n" +
            "WHERE inventoryQuantity > 0 "
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForOrderManagementV7(  // For Knowell
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "binClassId") Long binClassId,
            @Param(value = "stockTypeId") Long stockTypeId);

    @Query(value = "SELECT max(INV_ID) inventoryId into #inv FROM TBLINVENTORY WHERE is_deleted = 0 GROUP BY ITM_CODE,MFR_NAME,ST_BIN,PLANT_ID,WH_ID,C_ID,LANG_ID \r\n"
            + "SELECT LEVEL_ID AS levelId, SUM(INV_QTY) AS inventoryQty FROM tblinventory \r\n"
            + "WHERE WH_ID = :warehouseId and ITM_CODE = :itemCode AND BIN_CL_ID = :binClassId AND STCK_TYP_ID = :stockTypeId \r\n"
            + "AND C_ID = :companyCodeId and PLANT_ID = :plantId AND LANG_ID = :languageId \r\n"
            + "AND MFR_NAME = :manufacturerName AND IS_DELETED = 0 \r\n"
            + "AND INV_ID in (select inventoryId from #inv) \r\n"
            + "GROUP BY LEVEL_ID, ITM_CODE, MFR_NAME, PLANT_ID, WH_ID, C_ID, LANG_ID \r\n"
            + "HAVING SUM(INV_QTY) > 0 \r\n"
            + "ORDER BY LEVEL_ID, SUM(INV_QTY)", nativeQuery = true)
    public List<IInventory> findInventoryGroupByLevelId(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "manufacturerName") String manufacturerName,
            @Param(value = "stockTypeId") Long stockTypeId,
            @Param(value = "binClassId") Long binClassId);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String itemCode, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String storageBin, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndStorageBinAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String storageBin, String manufacturerName, Long deletionIndicator);


    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n" +
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


    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n" +
            "WHERE \n" +
            "itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT * from ( \n"
            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "BIN_CL_ID binClassId, \n"
            + "sum(INV_QTY) inventoryQuantity, \n"
            + "sum(ALLOC_QTY) allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "sum(REF_FIELD_4) referenceField4, \n"
            + "LEVEL_ID levelId, \n"
            + "BARCODE_ID barcodeId, \n"
            + "MFR_NAME manufacturerName, \n"
            + "BAG_SIZE bagSize, \n"
            + "ALT_UOM alternateUom \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "stck_typ_id in (:stockTypeId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (ALT_UOM IN (:alternateUom))) and\n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "and inv_id in (select inventoryId from #inv) \n" +
            "group by ITM_CODE, BARCODE_ID, MFR_NAME, PACK_BARCODE, STR_NO, ALT_UOM, INV_UOM, BAG_SIZE, \n" +
            "ST_BIN, BIN_CL_ID, LEVEL_ID, PLANT_ID, WH_ID, LANG_ID, C_ID \n" +
            ") x where inventoryQuantity > 0 ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryForOrderManagementV4(@Param("companyCodeId") String companyCodeId,
                                                                  @Param("plantId") String plantId,
                                                                  @Param("languageId") String languageId,
                                                                  @Param("warehouseId") String warehouseId,
                                                                  @Param("itemCode") String itemCode,
                                                                  @Param("manufacturerName") String manufacturerName,
                                                                  @Param("binClassId") Long binClassId,
                                                                  @Param("stockTypeId") Long stockTypeId,
                                                                  @Param("alternateUom") String alternateUom);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n" +
            "WHERE \n" +
            "itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT * from ( \n"
            + "SELECT LANG_ID languageId, \n"
            + "C_ID companyCodeId, \n"
            + "PLANT_ID plantId, \n"
            + "WH_ID warehouseId, \n"
            + "PACK_BARCODE packBarcodes, \n"
            + "ITM_CODE itemCode, \n"
            + "STR_NO batchSerialNumber, \n"
            + "ST_BIN storageBin, \n"
            + "BIN_CL_ID binClassId, \n"
            + "sum(INV_QTY) inventoryQuantity, \n"
            + "sum(ALLOC_QTY) allocatedQuantity, \n"
            + "INV_UOM inventoryUom, \n"
            + "sum(REF_FIELD_4) referenceField4, \n"
            + "LEVEL_ID levelId, \n"
            + "BARCODE_ID barcodeId, \n"
            + "MFR_NAME manufacturerName, \n"
            + "BAG_SIZE bagSize, \n"
            + "ALT_UOM alternateUom \n"
            + "from tblinventory \n"
            + "WHERE itm_code in (:itemCode) and \n" +
            "wh_id in (:warehouseId) and \n" +
            "bin_cl_id in (:binClassId) and \n" +
            "stck_typ_id in (:stockTypeId) and \n" +
            "plant_id in (:plantId) and \n" +
            "lang_id in (:languageId) and \n" +
            "mfr_name in (:manufacturerName) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (ALT_UOM IN (:alternateUom))) and\n" +
            "c_id in (:companyCodeId) and is_deleted = 0 \n" +
            "and inv_id in (select inventoryId from #inv) \n" +
            "group by ITM_CODE, BARCODE_ID, MFR_NAME, PACK_BARCODE, STR_NO, ALT_UOM, INV_UOM, BAG_SIZE, \n" +
            "ST_BIN, BIN_CL_ID, LEVEL_ID, PLANT_ID, WH_ID, LANG_ID, C_ID \n" +
            ") x where inventoryQuantity > 0 ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryForOrderManagementV7(@Param("companyCodeId") String companyCodeId,
                                                                  @Param("plantId") String plantId,
                                                                  @Param("languageId") String languageId,
                                                                  @Param("warehouseId") String warehouseId,
                                                                  @Param("itemCode") String itemCode,
                                                                  @Param("manufacturerName") String manufacturerName,
                                                                  @Param("binClassId") Long binClassId,
                                                                  @Param("stockTypeId") Long stockTypeId,
                                                                  @Param("alternateUom") String alternateUom);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +
            "iv.ALT_UOM alternateUom, \n" +
            "iv.NO_BAGS noBags, \n" +
            "iv.BAG_SIZE bagSize, \n" +
            "iv.MRP mrp, \n" +
            "iv.ITM_GRP itemGroup, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.UTD_ON, iv.BARCODE_ID, iv.INV_QTY \n", nativeQuery = true)
    public List<IInventoryImpl> getOMLInventoryV4OrderByCreatedOn(@Param("companyCodeId") String companyCodeId,
                                                                  @Param("plantId") String plantId,
                                                                  @Param("languageId") String languageId,
                                                                  @Param("warehouseId") String warehouseId,
                                                                  @Param("barcodeId") String barcodeId,
                                                                  @Param("batchSerialNumber") String batchSerialNumber,
                                                                  @Param("itemCode") String itemCode,
                                                                  @Param("manufacturerName") String manufacturerName,
                                                                  @Param("packBarcodes") String packBarcodes,
                                                                  @Param("binClassId") Long binClassId,
                                                                  @Param("stockTypeId") Long stockTypeId,
                                                                  @Param("alternateUom") String alternateUom);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +
            "iv.ALT_UOM alternateUom, \n" +
            "iv.NO_BAGS noBags, \n" +
            "iv.BAG_SIZE bagSize, \n" +
            "iv.MRP mrp, \n" +
            "iv.ITM_GRP itemGroup, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.EXP_DATE, iv.BARCODE_ID, iv.INV_QTY \n", nativeQuery = true)
    public List<IInventoryImpl> getOMLInventoryV4OrderByExpiryDate(@Param("companyCodeId") String companyCodeId,
                                                                   @Param("plantId") String plantId,
                                                                   @Param("languageId") String languageId,
                                                                   @Param("warehouseId") String warehouseId,
                                                                   @Param("barcodeId") String barcodeId,
                                                                   @Param("batchSerialNumber") String batchSerialNumber,
                                                                   @Param("itemCode") String itemCode,
                                                                   @Param("manufacturerName") String manufacturerName,
                                                                   @Param("packBarcodes") String packBarcodes,
                                                                   @Param("binClassId") Long binClassId,
                                                                   @Param("stockTypeId") Long stockTypeId,
                                                                   @Param("alternateUom") String alternateUom);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +
            "iv.ALT_UOM alternateUom, \n" +
            "iv.NO_BAGS noBags, \n" +
            "iv.BAG_SIZE bagSize, \n" +
            "iv.MRP mrp, \n" +
            "iv.ITM_GRP itemGroup, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.LEVEL_ID, iv.BARCODE_ID, iv.INV_QTY \n", nativeQuery = true)
    public List<IInventoryImpl> getOMLInventoryV4OrderByLevelId(@Param("companyCodeId") String companyCodeId,
                                                                @Param("plantId") String plantId,
                                                                @Param("languageId") String languageId,
                                                                @Param("warehouseId") String warehouseId,
                                                                @Param("barcodeId") String barcodeId,
                                                                @Param("batchSerialNumber") String batchSerialNumber,
                                                                @Param("itemCode") String itemCode,
                                                                @Param("manufacturerName") String manufacturerName,
                                                                @Param("packBarcodes") String packBarcodes,
                                                                @Param("binClassId") Long binClassId,
                                                                @Param("stockTypeId") Long stockTypeId,
                                                                @Param("alternateUom") String alternateUom);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT SUM(INV_QTY) AS inventoryQty \r\n"
            + "FROM tblinventory WHERE WH_ID = :warehouseId and ITM_CODE = :itemCode \r\n"
            + "AND C_ID = :companyCodeId and PLANT_ID = :plantId AND LANG_ID = :languageId \r\n"
            + "AND BIN_CL_ID = :binClassId AND STCK_TYP_ID = :stockTypeId AND \r\n"
            + "(COALESCE(:alternateUom, null) IS NULL OR (ALT_UOM IN (:alternateUom))) \n"
            + "AND MFR_NAME = :manufacturerName AND IS_DELETED = 0 AND STR_NO IS NOT NULL \r\n"
            + "GROUP BY ITM_CODE, MFR_NAME \r\n"
            + "HAVING SUM(INV_QTY) > 0 \r\n"
            + "ORDER BY SUM(INV_QTY)", nativeQuery = true)
    public List<IInventory> findInventoryByBatchV4(@Param("companyCodeId") String companyCodeId,
                                                   @Param("plantId") String plantId,
                                                   @Param("languageId") String languageId,
                                                   @Param("warehouseId") String warehouseId,
                                                   @Param("binClassId") Long binClassId,
                                                   @Param("stockTypeId") Long stockTypeId,
                                                   @Param("itemCode") String itemCode,
                                                   @Param("manufacturerName") String manufacturerName,
                                                   @Param("alternateUom") String alternateUom);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +
            "iv.ALT_UOM alternateUom, \n" +
            "iv.NO_BAGS noBags, \n" +
            "iv.BAG_SIZE bagSize, \n" +
            "iv.MRP mrp, \n" +
            "iv.ITM_GRP itemGroup, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.STR_NO, iv.BARCODE_ID, iv.INV_QTY \n", nativeQuery = true)
    public List<IInventoryImpl> getOMLInventoryV4OrderByBatch(@Param("companyCodeId") String companyCodeId,
                                                              @Param("plantId") String plantId,
                                                              @Param("languageId") String languageId,
                                                              @Param("warehouseId") String warehouseId,
                                                              @Param("barcodeId") String barcodeId,
                                                              @Param("batchSerialNumber") String batchSerialNumber,
                                                              @Param("itemCode") String itemCode,
                                                              @Param("manufacturerName") String manufacturerName,
                                                              @Param("packBarcodes") String packBarcodes,
                                                              @Param("binClassId") Long binClassId,
                                                              @Param("stockTypeId") Long stockTypeId,
                                                              @Param("alternateUom") String alternateUom);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT LEVEL_ID AS levelId, SUM(INV_QTY) AS inventoryQty FROM tblinventory \r\n"
            + "WHERE WH_ID = :warehouseId and ITM_CODE = :itemCode AND BIN_CL_ID = :binClassId AND STCK_TYP_ID = :stockTypeId \r\n"
            + "AND C_ID = :companyCodeId and PLANT_ID = :plantId AND LANG_ID = :languageId \r\n"
            + "AND MFR_NAME = :manufacturerName AND IS_DELETED = 0 AND \r\n"
            + "(COALESCE(:alternateUom, null) IS NULL OR (ALT_UOM IN (:alternateUom))) \n"
            + "AND INV_ID in (select inventoryId from #inv) \r\n"
            + "GROUP BY LEVEL_ID, ITM_CODE, MFR_NAME, PLANT_ID, WH_ID, C_ID, LANG_ID \r\n"
            + "HAVING SUM(INV_QTY) > 0 \r\n"
            + "ORDER BY LEVEL_ID, SUM(INV_QTY)", nativeQuery = true)
    public List<IInventory> findInventoryGroupByLevelIdV4(@Param("companyCodeId") String companyCodeId,
                                                          @Param("plantId") String plantId,
                                                          @Param("languageId") String languageId,
                                                          @Param("warehouseId") String warehouseId,
                                                          @Param("itemCode") String itemCode,
                                                          @Param("manufacturerName") String manufacturerName,
                                                          @Param("stockTypeId") Long stockTypeId,
                                                          @Param("binClassId") Long binClassId,
                                                          @Param("alternateUom") String alternateUom);

    @Query(value = "SELECT TOP 1 iv.INV_QTY " +
            "FROM tblinventory iv " +
            "WHERE iv.is_deleted = 0 " +
            "AND iv.LOOSE_PACK = 0 " +
            "AND iv.INV_QTY = :orderQty " +
            "AND (:companyCodeId IS NULL OR iv.c_id = :companyCodeId) " +
            "AND (:plantId IS NULL OR iv.plant_id = :plantId) " +
            "AND (:languageId IS NULL OR iv.lang_id = :languageId) " +
            "AND (:warehouseId IS NULL OR iv.wh_id = :warehouseId) " +
            "AND (:itemCode IS NULL OR iv.ITM_CODE = :itemCode) " +
            "AND (:binClassId IS NULL OR iv.BIN_CL_ID = :binClassId) " +
            "ORDER BY iv.LOOSE_PACK DESC",  // No need for LIMIT in SQL Server
            nativeQuery = true)
    Double getInvCaseQty2(@Param("companyCodeId") String companyCodeId,
                          @Param("plantId") String plantId,
                          @Param("languageId") String languageId,
                          @Param("warehouseId") String warehouseId,
                          @Param("itemCode") String itemCode,
                          @Param("binClassId") Long binClassId,
                          @Param("orderQty") Double orderQty);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +
            "iv.ALT_UOM alternateUom, \n" +
            "iv.NO_BAGS noBags, \n" +
            "iv.BAG_SIZE bagSize, \n" +
            "iv.MRP mrp, \n" +
            "iv.ITM_GRP itemGroup, \n" +
            "iv.LOOSE_PACK loosePack, \n " +
            "iv.CASE_QTY caseQty, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:levelId, null) IS NULL OR (iv.LEVEL_ID IN (:levelId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.LOOSE_PACK desc \n", nativeQuery = true)
    public List<IInventoryImpl> getOMLInventoryLevelIdV6(@Param("companyCodeId") String companyCodeId,
                                                         @Param("plantId") String plantId,
                                                         @Param("languageId") String languageId,
                                                         @Param("warehouseId") String warehouseId,
                                                         @Param("barcodeId") String barcodeId,
                                                         @Param("batchSerialNumber") String batchSerialNumber,
                                                         @Param("itemCode") String itemCode,
                                                         @Param("manufacturerName") String manufacturerName,
                                                         @Param("packBarcodes") String packBarcodes,
                                                         @Param("binClassId") Long binClassId,
                                                         @Param("stockTypeId") Long stockTypeId,
                                                         @Param("alternateUom") String alternateUom,
                                                         @Param("levelId") Long levelId);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +
            "iv.ALT_UOM alternateUom, \n" +
            "iv.NO_BAGS noBags, \n" +
            "iv.BAG_SIZE bagSize, \n" +
            "iv.MRP mrp, \n" +
            "iv.ITM_GRP itemGroup, \n" +
            "iv.LOOSE_PACK loosePack, \n " +
            "iv.CASE_QTY caseQty, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:levelId, null) IS NULL OR (iv.LEVEL_ID IN (:levelId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.LOOSE_PACK, INV_QTY \n", nativeQuery = true)
    public List<IInventoryImpl> getOMLInventoryLevelAsscIdV6(@Param("companyCodeId") String companyCodeId,
                                                             @Param("plantId") String plantId,
                                                             @Param("languageId") String languageId,
                                                             @Param("warehouseId") String warehouseId,
                                                             @Param("barcodeId") String barcodeId,
                                                             @Param("batchSerialNumber") String batchSerialNumber,
                                                             @Param("itemCode") String itemCode,
                                                             @Param("manufacturerName") String manufacturerName,
                                                             @Param("packBarcodes") String packBarcodes,
                                                             @Param("binClassId") Long binClassId,
                                                             @Param("stockTypeId") Long stockTypeId,
                                                             @Param("alternateUom") String alternateUom,
                                                             @Param("levelId") Long levelId);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +
            "iv.ALT_UOM alternateUom, \n" +
            "iv.NO_BAGS noBags, \n" +
            "iv.BAG_SIZE bagSize, \n" +
            "iv.MRP mrp, \n" +
            "iv.ITM_GRP itemGroup, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.BARCODE_ID \n", nativeQuery = true)
    public IInventoryImpl getOMLInventoryV4(@Param("companyCodeId") String companyCodeId,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("barcodeId") String barcodeId,
                                            @Param("batchSerialNumber") String batchSerialNumber,
                                            @Param("itemCode") String itemCode,
                                            @Param("manufacturerName") String manufacturerName,
                                            @Param("packBarcodes") String packBarcodes,
                                            @Param("storageBin") String storageBin,
                                            @Param("binClassId") Long binClassId,
                                            @Param("stockTypeId") Long stockTypeId,
                                            @Param("alternateUom") String alternateUom);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n" +
            "WHERE \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "is_deleted = 0 \n" +
            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +
            "iv.ALT_UOM alternateUom, \n" +
            "iv.NO_BAGS noBags, \n" +
            "iv.BAG_SIZE bagSize, \n" +
            "iv.MRP mrp, \n" +
            "iv.ITM_GRP itemGroup, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0  and iv.stck_typ_id = 1 and (iv.INV_QTY > 0) order by iv.BARCODE_ID \n", nativeQuery = true)
    public IInventoryImpl getInventoryV4(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("barcodeId") String barcodeId,
                                         @Param("batchSerialNumber") String batchSerialNumber,
                                         @Param("itemCode") String itemCode,
                                         @Param("manufacturerName") String manufacturerName,
                                         @Param("packBarcodes") String packBarcodes,
                                         @Param("storageBin") String storageBin,
                                         @Param("alternateUom") String alternateUom);

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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n"

            + "SELECT SUM(INV_QTY) AS inventoryQty FROM tblinventory \r\n"
            + "WHERE WH_ID = :warehouseId and ITM_CODE = :itemCode AND BIN_CL_ID = :binClassId AND STCK_TYP_ID = :stockTypeId \r\n"
            + "AND C_ID = :companyCodeId and PLANT_ID = :plantId AND LANG_ID = :languageId \r\n"
            + "AND MFR_NAME = :manufacturerName AND IS_DELETED = 0 \r\n"
            + "AND BARCODE_ID = :barcodeId \r\n"
            + "AND INV_ID in (select inventoryId from #inv) \r\n"
            + "GROUP BY ITM_CODE, MFR_NAME, PLANT_ID, WH_ID, C_ID, LANG_ID \r\n"
            + "HAVING SUM(INV_QTY) > 0 \r\n"
            + "ORDER BY SUM(INV_QTY)", nativeQuery = true)
    public List<IInventory> findInventoryGroupByLevelIdV5(@Param("companyCodeId") String companyCodeId,
                                                          @Param("plantId") String plantId,
                                                          @Param("languageId") String languageId,
                                                          @Param("warehouseId") String warehouseId,
                                                          @Param("itemCode") String itemCode,
                                                          @Param("manufacturerName") String manufacturerName,
                                                          @Param("stockTypeId") Long stockTypeId,
                                                          @Param("binClassId") Long binClassId,
                                                          @Param("barcodeId") String barcodeId);

    //oml - order by [level_id]
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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
            "iv.REF_ORD_NO referenceOrderNo,\n" +
            "iv.STR_MTD storageMethod,\n" +
            "iv.BIN_CL_ID binClassId,\n" +
            "iv.TEXT description,\n" +
            "iv.INV_QTY inventoryQuantity,\n" +
            "iv.ALLOC_QTY allocatedQuantity,\n" +
            "iv.INV_UOM inventoryUom,\n" +
            "iv.MFR_DATE manufacturerDate,\n" +
            "iv.EXP_DATE expiryDate,\n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +

            "iv.PALLET_ID palletId, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.BARCODE_ID, iv.INV_QTY \n", nativeQuery = true)
    public List<IInventoryImpl> getOMLInventoryLevelIdV5(@Param("companyCodeId") String companyCodeId,
                                                         @Param("plantId") String plantId,
                                                         @Param("languageId") String languageId,
                                                         @Param("warehouseId") String warehouseId,
                                                         @Param("barcodeId") String barcodeId,
                                                         @Param("batchSerialNumber") String batchSerialNumber,
                                                         @Param("itemCode") String itemCode,
                                                         @Param("manufacturerName") String manufacturerName,
                                                         @Param("packBarcodes") String packBarcodes,
                                                         @Param("binClassId") Long binClassId,
                                                         @Param("stockTypeId") Long stockTypeId);


    //OrderByLevelId
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
            "group by itm_code,barcode_id,mfr_name,pack_barcode,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.ST_SEC_ID storageSectionId, \n" +
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
            "iv.ITM_TYP_ID itemType,\n" +
            "iv.ITM_TYP_TXT itemTypeDescription,\n" +
            "iv.PARTNER_CODE partnerCode,\n" +
            "iv.BATCH_DATE batchDate,\n" +
            "iv.MATERIAL_NO materialNo, \n" +
            "iv.PRICE_SEGMENT priceSegment, \n" +
            "iv.ARTICLE_NO articleNo, \n" +
            "iv.GENDER gender, \n" +
            "iv.COLOR color, \n" +
            "iv.SIZE size, \n" +
            "iv.NO_PAIRS noPairs, \n" +

            "iv.PALLET_ID palletId, \n" +
            "iv.STATUS_TEXT statusDescription\n" +
            "from tblinventory iv\n" +
            "where \n" +
            "iv.inv_id in (select inventoryId from #inv) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (iv.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (iv.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (iv.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (iv.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:batchSerialNumber, null) IS NULL OR (iv.STR_NO IN (:batchSerialNumber))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) order by iv.LEVEL_ID, iv.BARCODE_ID, iv.INV_QTY, iv.ST_SEC_ID, iv.REF_FIELD_10 \n", nativeQuery = true)
    public List<IInventoryImpl> getOMLInventoryOrderByLevelIdV5(@Param("companyCodeId") String companyCodeId,
                                                                @Param("plantId") String plantId,
                                                                @Param("languageId") String languageId,
                                                                @Param("warehouseId") String warehouseId,
                                                                @Param("barcodeId") String barcodeId,
                                                                @Param("batchSerialNumber") String batchSerialNumber,
                                                                @Param("itemCode") String itemCode,
                                                                @Param("manufacturerName") String manufacturerName,
                                                                @Param("packBarcodes") String packBarcodes,
                                                                @Param("binClassId") Long binClassId,
                                                                @Param("stockTypeId") Long stockTypeId);

    @Query(value = "SELECT EXP_DATE AS expiryDate, INV_QTY AS inventoryQty, ALLOC_QTY allocatedQty, TEXT AS description, " +
            "PARTNER_CODE AS partnerCode, INV_ID AS inventoryId, ITM_CODE AS itemCode, MFR_NAME AS manufacturerName, ST_BIN storageBin " +
            "FROM tblinventory " +
            "WHERE WH_ID = :warehouseId AND ITM_CODE = :itemCode AND BIN_CL_ID = :binClassId " +
            "AND STCK_TYP_ID = :stockTypeId AND C_ID = :companyCodeId AND PLANT_ID = :plantId " +
            "AND LANG_ID = :languageId AND MFR_NAME = :manufacturerName AND IS_DELETED = 0 AND INV_QTY > 0 " +
            "AND INV_ID IN (SELECT MAX(INV_ID) FROM tblinventory WHERE IS_DELETED = 0 " +
            "GROUP BY ITM_CODE, EXP_DATE, MFR_NAME, PACK_BARCODE, ST_BIN, PLANT_ID, WH_ID, C_ID, LANG_ID) " +
            "ORDER BY EXP_DATE ", nativeQuery = true)
    List<IInventory> findInventoryGroupIdExpiryV5(@Param("companyCodeId") String companyCodeId,
                                                  @Param("plantId") String plantId,
                                                  @Param("languageId") String languageId,
                                                  @Param("warehouseId") String warehouseId,
                                                  @Param("itemCode") String itemCode,
                                                  @Param("manufacturerName") String manufacturerName,
                                                  @Param("stockTypeId") Long stockTypeId,
                                                  @Param("binClassId") Long binClassId);

    @Query(value = "SELECT EXP_DATE AS expiryDate, INV_QTY AS inventoryQty, ALLOC_QTY allocatedQty, TEXT AS description, " +
            "PARTNER_CODE AS partnerCode, INV_ID AS inventoryId, ITM_CODE AS itemCode, MFR_NAME AS manufacturerName, ST_BIN storageBin " +
            "FROM tblinventory " +
            "WHERE WH_ID = :warehouseId AND ITM_CODE = :itemCode AND BIN_CL_ID = :binClassId " +
            "AND STCK_TYP_ID = :stockTypeId AND C_ID = :companyCodeId AND PLANT_ID = :plantId " +
            "AND LANG_ID = :languageId AND MFR_NAME = :manufacturerName AND IS_DELETED = 0 AND INV_QTY > 0 " +
            "AND INV_ID IN (SELECT MAX(INV_ID) FROM tblinventory WHERE IS_DELETED = 0 and REMAINING_SELF_LIFE_PERCENTAGE >70 " +
            "GROUP BY ITM_CODE, EXP_DATE, MFR_NAME, PACK_BARCODE, ST_BIN, PLANT_ID, WH_ID, C_ID, LANG_ID) " +
            "ORDER BY EXP_DATE ", nativeQuery = true)
    List<IInventory> findInventoryForRemainingDaysV5(@Param("companyCodeId") String companyCodeId,
                                                     @Param("plantId") String plantId,
                                                     @Param("languageId") String languageId,
                                                     @Param("warehouseId") String warehouseId,
                                                     @Param("itemCode") String itemCode,
                                                     @Param("manufacturerName") String manufacturerName,
                                                     @Param("stockTypeId") Long stockTypeId,
                                                     @Param("binClassId") Long binClassId);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code, exp_date, mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
//            "(COALESCE(:expiryDate, null) IS NULL OR (iv.exp_date IN (:expiryDate))) and\n" +
            "(COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR (iv.exp_date between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and COALESCE(CONVERT(VARCHAR(255), :toDate), null))) and \n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.exp_date ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOmlGroupByExpiryDateV5(@Param("companyCodeId") String companyCodeId,
                                                                    @Param("plantId") String plantId,
                                                                    @Param("languageId") String languageId,
                                                                    @Param("warehouseId") String warehouseId,
                                                                    @Param("itemCode") String itemCode,
                                                                    @Param("manufacturerName") String manufacturerName,
                                                                    @Param("stockTypeId") Long stockTypeId,
                                                                    @Param("binClassId") Long binClassId,
                                                                    @Param("fromDate") Date fromDate,
                                                                    @Param("toDate") Date toDate);

    @Query(value = "select max(inv_id) inventoryId into #inv from tblinventory \n"
            + "WHERE is_deleted = 0 group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.MFR_DATE manufacturerDate,\n" +
            "iv.EXP_DATE expiryDate,\n" +
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
            "(COALESCE(:barcodeId, null) IS NULL OR (iv.BARCODE_ID IN (:barcodeId))) and\n" +
            "iv.is_deleted = 0 and (iv.INV_QTY > 0) ORDER BY iv.BARCODE_ID ", nativeQuery = true)
    public List<IInventoryImpl> findInventoryOmlGroupByBarcodeIdV5(@Param("companyCodeId") String companyCodeId,
                                                                   @Param("languageId") String languageId,
                                                                   @Param("plantId") String plantId,
                                                                   @Param("warehouseId") String warehouseId,
                                                                   @Param("manufacturerName") String manufacturerName,
                                                                   @Param("itemCode") String itemCode,
                                                                   @Param("storageBin") String storageBin,
                                                                   @Param("stockTypeId") Long stockTypeId,
                                                                   @Param("binClassId") Long binClassId,
                                                                   @Param("barcodeId") String barcodeId);


    @Query(value = "SELECT partner_typ AS businessPartnerType FROM tblbusinesspartner " +
            "WHERE C_ID = :companyCodeId and PLANT_ID = :plantId and LANG_ID = :languageId and WH_ID = :warehouseId " +
            "AND partner_code = :partnerCode AND IS_DELETED = 0", nativeQuery = true)
    public Long findPartnerType(@Param(value = "companyCodeId") String companyCodeId,
                                @Param(value = "plantId") String plantId,
                                @Param(value = "languageId") String languageId,
                                @Param(value = "warehouseId") String warehouseId,
                                @Param(value = "partnerCode") String partnerCode);

}