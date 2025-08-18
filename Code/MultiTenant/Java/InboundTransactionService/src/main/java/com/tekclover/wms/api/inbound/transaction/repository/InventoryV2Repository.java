package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.dto.IInventory;
import com.tekclover.wms.api.inbound.transaction.model.impl.StockReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.report.StorageBinDashBoardImpl;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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


    InventoryV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndBatchSerialNumberAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCode, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String number, Long binClassId, String batchSerialNumber, Long deletionIndicator);

    InventoryV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndBinClassIdAndBarcodeIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCode, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long binClassId, String barcodeId, Long deletionIndicator);

    InventoryV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndBarcodeIdAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCode, String plantId, String languageId, String warehouseId, String itemCode,
            String barcodeId, String manufacturerName, String packbarcode, Long binClassId, Long deletionIndicator);

    InventoryV2 findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndBarcodeIdAndDeletionIndicatorOrderByInventoryIdDesc(
            String companyCodeId, String plantId, String languageId, String warehouseId, String packBarcodes,
            String itemCode, String manufacturerName, String storageBin, Long stockTypeId, Long specialStockIndicatorId, String barcodeId, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndBarcodeIdAndManufacturerNameAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String barcodeId, String manufacturerName, String storageBin, Long deletionIndicator);

    InventoryV2 findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndBarcodeIdAndManufacturerCodeAndStorageBinAndBatchSerialNumberAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String barcodeId, String manufacturerName, String storageBin, String batchSerialNumber, Long deletionIndicator);

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
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and iv.stck_typ_id = 1\n", nativeQuery = true)
    public IInventoryImpl getInboundInventoryV4(@Param("companyCodeId") String companyCodeId,
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
            "group by itm_code,barcode_id,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id \n" +

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
            "iv.STATUS_TEXT statusDescription, \n" +
            "iv.QTY_IN_CASE qtyInCase, \n " +
            "iv.QTY_IN_PIECE qtyInPiece, \n " +
            "iv.QTY_IN_CREATE qtyInCreate \n" +
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
            "iv.is_deleted = 0 and iv.stck_typ_id = 1\n", nativeQuery = true)
    public IInventoryImpl getInboundInventoryV5(@Param("companyCodeId") String companyCodeId,
                                                @Param("plantId") String plantId,
                                                @Param("languageId") String languageId,
                                                @Param("warehouseId") String warehouseId,
                                                @Param("barcodeId") String barcodeId,
                                                @Param("batchSerialNumber") String batchSerialNumber,
                                                @Param("itemCode") String itemCode,
                                                @Param("manufacturerName") String manufacturerName,
                                                @Param("packBarcodes") String packBarcodes,
                                                @Param("storageBin") String storageBin,
                                                @Param("binClassId") Long binClassId);

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
            , nativeQuery = true)
    public List<IInventoryImpl> findInventoryForInventoryMovement(
            @Param(value = "companyCodeId") String companyCodeId,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "binClassId") Long binClassId,
            @Param(value = "manufacturerName") String manufacturerName);


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

    List<InventoryV2>
    findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String packBarcodes,
            String itemCode, String manufacturerName, String storageBin, Long deletionIndicator);

    List<InventoryV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerCodeAndDeletionIndicatorOrderByInventoryIdDesc(
            String languageId, String companyCode, String plantId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);


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
            "(COALESCE(:manufacturerCode, null) IS NULL OR (iv.MFR_CODE IN (:manufacturerCode))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (iv.MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:packBarcodes, null) IS NULL OR (iv.PACK_BARCODE IN (:packBarcodes))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (iv.ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:storageBin, null) IS NULL OR (iv.ST_BIN IN (:storageBin))) and\n" +
            "(COALESCE(:stockTypeId, null) IS NULL OR (iv.STCK_TYP_ID IN (:stockTypeId))) and \n" +
            "(COALESCE(:storageSectionId, null) IS NULL OR (iv.REF_FIELD_10 IN (:storageSectionId))) and \n" +
            "(COALESCE(:levelId, null) IS NULL OR (iv.level_id IN (:levelId))) and \n" +
            "(COALESCE(:specialStockIndicatorId, null) IS NULL OR (iv.SP_ST_IND_ID IN (:specialStockIndicatorId))) and \n" +
            "(COALESCE(:itemType, null) IS NULL OR (iv.ITM_TYP_ID IN (:itemType))) and \n" +
            "(COALESCE(:partnerCode, null) IS NULL OR (iv.PARTNER_CODE IN (:partnerCode))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (iv.BIN_CL_ID IN (:binClassId))) and\n" +
            "(COALESCE(:description, null) IS NULL OR (iv.TEXT IN (:description))) and \n" +
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "(COALESCE(:size, null) IS NULL OR (iv.SIZE IN (:size))) and\n" +
            "iv.is_deleted = 0 and (iv.REF_FIELD_4 > 0)\n", nativeQuery = true)
    public List<IInventoryImpl> findInventoryV4(@Param("companyCodeId") List<String> companyCodeId,
                                                @Param("plantId") List<String> plantId,
                                                @Param("languageId") List<String> languageId,
                                                @Param("warehouseId") List<String> warehouseId,
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
                                                @Param("partnerCode") List<String> partnerCode,
                                                @Param("specialStockIndicatorId") List<Long> specialStockIndicatorId,
                                                @Param("itemType") List<Long> itemType,
                                                @Param("size") List<String> size,
                                                @Param("binClassId") List<Long> binClassId,
                                                @Param("alternateUom") List<String> alternateUom);


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


    @Query(value = "select st_bin storageBin from tblinventory where is_deleted = 0 and \n " +
            "inv_id in (select max(inv_id) inventoryId from tblinventory \n" +
            "where IS_DELETED = 0 group by itm_code,mfr_name,st_bin,plant_id,wh_id,c_id,lang_id) and \n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:manufacturerName, null) IS NULL OR (MFR_NAME IN (:manufacturerName))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ITM_CODE IN (:itemCode))) and \n" +
            "(COALESCE(:barcodeId, null) IS NULL OR (BARCODE_ID IN (:barcodeId))) and \n" +
            "(COALESCE(:binClassId, null) IS NULL OR (BIN_CL_ID IN (:binClassId))) and \n" +
            "is_deleted = 0 and (REF_FIELD_4 > 0) and st_bin <> 'REC-AL-B2' ", nativeQuery = true)
    public List<String> getStorageBinListV5(@Param("companyCodeId") String companyCodeId,
                                            @Param("plantId") String plantId,
                                            @Param("languageId") String languageId,
                                            @Param("warehouseId") String warehouseId,
                                            @Param("itemCode") String itemCode,
                                            @Param("manufacturerName") String manufacturerName,
                                            @Param("binClassId") Long binClassId,
                                            @Param("barcodeId") String barcodeId);


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

    List<InventoryV2> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerNameAndStorageBinAndStockTypeIdAndSpecialStockIndicatorIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String packBarcodes, String itemCode,
            String manufacturerName, String storageBin, Long stockTypeId, Long specialStockIndicatorId, Long deletionIndicator);

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
//            "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +
            "group by itm_code,mfr_name,pack_barcode,stck_typ_id,st_bin,plant_id,wh_id,c_id,lang_id \n" +

            "SELECT \n" +
            "iv.ST_BIN storageBin\n" +
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
            "(COALESCE(:alternateUom, null) IS NULL OR (iv.ALT_UOM IN (:alternateUom))) and\n" +
            "iv.is_deleted = 0 and iv.stck_typ_id = 1 \n", nativeQuery = true)
    public List<String> getPutAwayHeaderCreateInventoryV4(@Param("companyCodeId") String companyCodeId,
                                                          @Param("plantId") String plantId,
                                                          @Param("languageId") String languageId,
                                                          @Param("warehouseId") String warehouseId,
                                                          @Param("barcodeId") String barcodeId,
                                                          @Param("batchSerialNumber") String batchSerialNumber,
                                                          @Param("itemCode") String itemCode,
                                                          @Param("manufacturerName") String manufacturerName,
                                                          @Param("packBarcodes") String packBarcodes,
                                                          @Param("binClassId") Long binClassId,
                                                          @Param("alternateUom") String alternateUom);


    //OutboundInventory
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
            "iv.is_deleted = 0  and iv.stck_typ_id = 1 and (iv.REF_FIELD_4 > 0) order by iv.BARCODE_ID \n", nativeQuery = true)
    public IInventoryImpl getOutboundInventoryV4(@Param("companyCodeId") String companyCodeId,
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

    //OrderByCreatedOn
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

    //OrderByExpiryDate
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

    //===================================Notification's Query======================================//
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
                   "group by itm_code,barcode_id,mfr_name,pack_barcode,alt_uom,bag_size,st_bin,plant_id,wh_id,c_id,lang_id \n" +
//                    "group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id \n" +

                    // inv_qty from tblinventory to temp table
                    "UPDATE TH SET TH.quantity = X.TOT_QTY FROM #stBinDashBoard TH INNER JOIN \n" +
                    "(select c_id, plant_id, lang_id, wh_id, st_bin, ISNULL(sum(REF_FIELD_4),0) TOT_QTY\n" +
                    "from tblinventory \r\n" +
                    "where is_deleted = 0 and ref_field_4 > 0 and \r\n" +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (c_id IN (:companyCodeId))) and\n" +
                    "(COALESCE(:plantId, null) IS NULL OR (plant_id IN (:plantId))) and\n" +
                    "(COALESCE(:languageId, null) IS NULL OR (lang_id IN (:languageId))) and\n" +
                    "(COALESCE(:warehouseId, null) IS NULL OR (wh_id IN (:warehouseId))) and\n" +
                    "inv_id in (select inventoryId from #inv) \r\n" +
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

    //=====================================WMS_FAHAHEEL=====================================//

    //------Added-by--Murugavel--on--10/03---for---InventoryTrans-----------
    @Modifying(clearAutomatically = true)
    @Query(" UPDATE Inventory inv \r\n"
            + " SET inv.inventoryQuantity = :inventoryQuantity, \r\n"
            + " inv.allocatedQuantity = :allocatedQuantity \r\n"
            + " WHERE inv.warehouseId = :warehouseId \r\n"
            + " AND inv.packBarcodes = :packBarcodes \r\n"
            + " AND inv.itemCode = :itemCode \r\n"
            + " AND inv.storageBin = :storageBin AND inv.deletionIndicator = 0")
    void updateInventory(@Param ("warehouseId") String warehouseId,
                         @Param ("packBarcodes") String packBarcodes,
                         @Param ("itemCode") String itemCode,
                         @Param ("storageBin") String storageBin,
                         @Param ("inventoryQuantity") Double inventoryQuantity,
                         @Param ("allocatedQuantity") Double allocatedQuantity);



    @Query(value = "\n" +
            "select * from tblinventory where  c_id = :companyCodeId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and \n" +
            " barcode_id = :barcodeId and itm_code = :itemCode and bin_cl_id = 3 and inv_id in (select max(inv_id) from tblinventory " +
            "where bin_cl_id = 3 and itm_code = :itemCode and is_deleted = 0  and ref_field_4 > 0 " +
            "group by itm_code,barcode_id,mfr_name,pack_barcode,st_bin,plant_id,wh_id,c_id,lang_id) ",nativeQuery = true)
    InventoryV2 findInventoryId(@Param("companyCodeId") String companyCodeId,
                                @Param("plantId") String plantId,
                                @Param("languageId") String languageId,
                                @Param("warehouseId") String warehouseId,
                                @Param ("itemCode") String itemCode,
                                @Param("barcodeId") String barcodeId);



}