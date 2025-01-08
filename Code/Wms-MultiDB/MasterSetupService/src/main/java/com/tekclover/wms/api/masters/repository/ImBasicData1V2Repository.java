package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.dto.ImBasicData1V2Impl;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImBasicData1V2Repository extends JpaRepository<ImBasicData1V2, Long>,
        JpaSpecificationExecutor<ImBasicData1V2>,
        StreamableJpaSpecificationRepository<ImBasicData1V2> {

    @Query(value = "select \n"
            + "tw.lang_id AS languageId, \n"
            + "tw.wh_id AS warehouseId \n"
            + "from tblwarehouseid tw \n"
            + "where \n"
            + "tw.c_id IN (:companyCodeId) and \n"
            + "tw.plant_id IN (:plantId) and is_deleted = 0", nativeQuery = true)
    IKeyValuePair getImBasicDataV2Description(@Param(value = "companyCodeId") String companyCodeId,
                                              @Param(value = "plantId") String plantId);

    //Description
    @Query(value = "select tc.c_text companyDesc,\n" +
            "tp.plant_text plantDesc,\n" +
            "tw.wh_text warehouseDesc from \n" +
            "tblcompanyid tc\n" +
            "join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id\n" +
            "join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id=tc.lang_id and tw.plant_id = tp.plant_id\n" +
            "where\n" +
            "tc.c_id IN (:companyCodeId) and \n" +
            "tc.lang_id IN (:languageId) and \n" +
            "tp.plant_id IN(:plantId) and \n" +
            "tw.wh_id IN (:warehouseId) and \n" +
            "tc.is_deleted=0 and \n" +
            "tp.is_deleted=0 and \n" +
            "tw.is_deleted=0", nativeQuery = true)
    public IKeyValuePair getDescription(@Param(value = "companyCodeId") String companyCodeId,
                                        @Param(value = "languageId") String languageId,
                                        @Param(value = "plantId") String plantId,
                                        @Param(value = "warehouseId") String warehouseId);

    Optional<ImBasicData1V2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);

    Optional<ImBasicData1V2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String itemCode, String manufacturerName, String languageId, Long deletionIndicator);

    Optional<ImBasicData1V2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String itemCode, String uomId, String manufacturerPartNo, String languageId, Long deletionIndicator);

    //Description
    @Query(value = "select \n" +
            "ib.UOM_ID uomId, \n" +
            "ib.LANG_ID languageId, \n" +
            "ib.C_ID companyCodeId, \n" +
            "ib.PLANT_ID plantId, \n" +
            "ib.WH_ID warehouseId, \n" +
            "ib.ITM_CODE itemCode, \n" +
            "ib.MFR_PART manufacturerPartNo, \n" +
            "ib.TEXT description, \n" +
            "ib.MODEL model, \n" +
            "ib.SPEC_01 specifications1, \n" +
            "ib.SPEC_02 specifications2, \n" +
            "ib.EAN_UPC_NO eanUpcNo, \n" +
            "ib.HSN_CODE hsnCode, \n" +
            "ib.ITM_TYP_ID itemType, \n" +
            "ib.ITM_GRP_ID itemGroup, \n" +
            "ib.SUB_ITM_GRP_ID subItemGroup, \n" +
            "ib.ST_SEC_ID storageSectionId, \n" +
            "ib.TOT_STK totalStock, \n" +
            "ib.MIN_STK minimumStock, \n" +
            "ib.MAX_STK maximumStock, \n" +
            "ib.RE_ORD_LVL reorderLevel, \n" +
            "ib.CAP_CHK capacityCheck, \n" +
            "ib.REP_QTY replenishmentQty, \n" +
            "ib.SAFTY_STCK safetyStock, \n" +
            "ib.CAP_UNIT capacityUnit, \n" +
            "ib.CAP_UOM capacityUom, \n" +
            "ib.QUANTITY quantity, \n" +
            "ib.WEIGHT weight, \n" +
            "ib.STATUS_ID statusId, \n" +
            "ib.SHELF_LIFE_IND shelfLifeIndicator, \n" +
            "ib.REF_FIELD_1 referenceField1, \n" +
            "ib.REF_FIELD_2 referenceField2, \n" +
            "ib.REF_FIELD_3 referenceField3, \n" +
            "ib.REF_FIELD_4 referenceField4, \n" +
            "ib.REF_FIELD_5 referenceField5, \n" +
            "ib.REF_FIELD_6 referenceField6, \n" +
            "ib.REF_FIELD_7 referenceField7, \n" +
            "ib.REF_FIELD_8 referenceField8, \n" +
            "ib.REF_FIELD_9 referenceField9, \n" +
            "ib.REF_FIELD_10 referenceField10, \n" +
            "ib.IS_DELETED deletionIndicator, \n" +
            "ib.CTD_BY createdBy, \n" +
            "ib.CTD_ON createdOn, \n" +
            "ib.UTD_BY updatedBy, \n" +
            "ib.UTD_ON updatedOn, \n" +
            "ib.LENGTH length, \n" +
            "ib.WIDTH width, \n" +
            "ib.HEIGHT height, \n" +
            "ib.DIM_UOM dimensionUom, \n" +
            "ib.VOLUME volume, \n" +
            "ib.BATCH_QUANTITY batchQuantity, \n" +
            "ib.MOQ moq, \n" +
            "ib.MANUFACTURER_NAME manufacturerName, \n" +
            "ib.MANUFACTURER_FULL_NAME manufacturerFullName, \n" +
            "ib.MANUFACTURER_CODE manufacturerCode, \n" +
            "ib.BRAND brand, \n" +
            "ib.SUPPLIER_PART_NUMBER supplierPartNumber, \n" +
            "ib.REMARKS remarks, \n" +
            "ib.IS_NEW isNew, \n" +
            "ib.IS_UPDATE isUpdate, \n" +
            "ib.IS_COMPLETED isCompleted, \n" +
            "ib.C_TEXT companyDescription, \n" +
            "ib.PLANT_TEXT plantDescription, \n" +
            "ib.WH_TEXT warehouseDescription, \n" +
            "ib.MIDDLEWARE_ID middlewareId, \n" +
            "ib.MIDDLEWARE_TABLE middlewareTable, \n" +
            "concat(it.ITM_TYPE_ID,' - ',it.ITM_TYP) itemTypeDescription, \n" +
            "concat(ig.ITM_GRP_ID,' - ',ig.IMT_GRP) itemGroupDescription \n" +
            "from tblimbasicdata1 ib\n" +
            "join tblitemtypeid it on ib.c_id = it.c_id and ib.lang_id = it.lang_id and \n" +
            "ib.plant_id = it.plant_id and ib.wh_id = it.wh_id and ib.ITM_TYP_ID = it.ITM_TYPE_ID \n" +
            "join tblitemgroupid ig on ib.c_id = ig.c_id and ib.lang_id = ig.lang_id and \n" +
            "ib.plant_id = ig.plant_id and ib.wh_id = ig.wh_id and ib.itm_typ_id = ig.itm_type_id and ib.ITM_GRP_ID = ig.ITM_GRP_ID\n" +
            "where\n" +
            "(COALESCE(:companyCodeId, null) IS NULL OR (ib.c_id IN (:companyCodeId))) and \n" +
            "(COALESCE(:plantId, null) IS NULL OR (ib.plant_id IN (:plantId))) and \n" +
            "(COALESCE(:languageId, null) IS NULL OR (ib.lang_id IN (:languageId))) and \n" +
            "(COALESCE(:warehouseId, null) IS NULL OR (ib.wh_id IN (:warehouseId))) and \n" +
            "(COALESCE(:itemCode, null) IS NULL OR (ib.itm_code IN (:itemCode))) and \n" +
            "(COALESCE(:description, null) IS NULL OR (ib.text IN (:description))) and \n" +
            "(COALESCE(:itemType, null) IS NULL OR (ib.itm_typ_id IN (:itemType))) and \n" +
            "(COALESCE(:itemGroup, null) IS NULL OR (ib.ITM_GRP_ID IN (:itemGroup))) and \n" +
            "(COALESCE(:subItemGroup, null) IS NULL OR (ib.SUB_ITM_GRP_ID IN (:subItemGroup))) and \n" +
            "(COALESCE(:manufacturerPartNo, null) IS NULL OR (ib.MFR_PART IN (:manufacturerPartNo))) and \n" +
            "(COALESCE(:createdBy, null) IS NULL OR (ib.CTD_BY IN (:createdBy))) and \n" +
            "(COALESCE(:updatedBy, null) IS NULL OR (ib.UTD_BY IN (:updatedBy))) and \n" +
            "(COALESCE(CONVERT(VARCHAR(255), :startCreatedOn), null) IS NULL OR (ib.CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startCreatedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endCreatedOn), null))) and\n" +
            "(COALESCE(CONVERT(VARCHAR(255), :startUpdatedOn), null) IS NULL OR (ib.UTD_ON between COALESCE(CONVERT(VARCHAR(255), :startUpdatedOn), null) and COALESCE(CONVERT(VARCHAR(255), :endUpdatedOn), null))) and\n" +
            "ib.is_deleted=0 ", nativeQuery = true)
    public List<ImBasicData1V2Impl> findImbasicData1(@Param(value = "companyCodeId") List<String> companyCodeId,
                                                     @Param(value = "plantId") List<String> plantId,
                                                     @Param(value = "languageId") List<String> languageId,
                                                     @Param(value = "warehouseId") List<String> warehouseId,
                                                     @Param(value = "itemCode") List<String> itemCode,
                                                     @Param(value = "description") List<String> description,
                                                     @Param(value = "itemType") List<Long> itemType,
                                                     @Param(value = "itemGroup") List<Long> itemGroup,
                                                     @Param(value = "subItemGroup") List<Long> subItemGroup,
                                                     @Param(value = "manufacturerPartNo") List<String> manufacturerPartNo,
                                                     @Param(value = "createdBy") List<String> createdBy,
                                                     @Param(value = "updatedBy") List<String> updatedBy,
                                                     @Param(value = "startCreatedOn") Date startCreatedOn,
                                                     @Param(value = "endCreatedOn") Date endCreatedOn,
                                                     @Param(value = "startUpdatedOn") Date startUpdatedOn,
                                                     @Param(value = "endUpdatedOn") Date endUpdatedOn);

    Optional<ImBasicData1V2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, Long deletionIndicator);

    //========================================================Impex============================================================

    @Query(value = "select tc.itm_typ itemTypeDescription from \n" +
            "tblitemtypeid tc\n" +
            "where\n" +
            "tc.itm_type_id IN (:itemTypeId) and \n" +
            "tc.c_id IN (:companyCodeId) and \n" +
            "tc.lang_id IN (:languageId) and \n" +
            "tc.plant_id IN(:plantId) and \n" +
            "tc.wh_id IN (:warehouseId) and \n" +
            "tc.is_deleted=0", nativeQuery = true)
    public String getItemTypeDescription(@Param(value = "companyCodeId") String companyCodeId,
                                         @Param(value = "plantId") String plantId,
                                         @Param(value = "languageId") String languageId,
                                         @Param(value = "warehouseId") String warehouseId,
                                         @Param(value = "itemTypeId") Long itemTypeId);

    @Query(value = "select tc.imt_grp from \n" +
            "tblitemgroupid tc\n" +
            "where\n" +
            "tc.itm_grp_id IN (:itemGroupId) and \n" +
            "tc.c_id IN (:companyCodeId) and \n" +
            "tc.lang_id IN (:languageId) and \n" +
            "tc.plant_id IN(:plantId) and \n" +
            "tc.wh_id IN (:warehouseId) and \n" +
            "tc.is_deleted=0", nativeQuery = true)
    public String getItemGroupDescription(@Param(value = "companyCodeId") String companyCodeId,
                                          @Param(value = "plantId") String plantId,
                                          @Param(value = "languageId") String languageId,
                                          @Param(value = "warehouseId") String warehouseId,
                                          @Param(value = "itemGroupId") Long itemGroupId);

    @Transactional
    @Procedure(procedureName = "imbasicdata_description_update_proc")
    void updateImbasicData1DescriptionProcedure();

}