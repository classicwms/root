package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.dto.GroupedStagingLineProjection;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface StagingLineV2Repository extends JpaRepository<StagingLineEntityV2, Long>,
        JpaSpecificationExecutor<StagingLineEntityV2>, StreamableJpaSpecificationRepository<StagingLineEntityV2> {

    Optional<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndItemCodeAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String stagingNo, String palletCode, String caseCode,
            Long lineNo, String itemCode, String manufacturerCode, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String refDocNumber, String preInboundNo,
            Long lineNo, String itemCode, String manufacturerCode, Long deletionIndicator);

    //    long getStagingLineCountByStatusId(String companyCode, String plantId, String warehouseId,
//                                       String preInboundNo, String refDocNumber, String manufacturerCode);
    @Query(value = "SELECT COUNT(*) FROM tblstagingline WHERE LANG_ID ='EN' \n" +
            "AND C_ID = :companyId AND PLANT_ID = :plantId AND WH_ID = :warehouseId \r\n" +
            "AND PRE_IB_NO = :preInboundNo AND REF_DOC_NO = :refDocNumber AND MFR_CODE = :manufacturerCode AND\n" +
            "STATUS_ID IN (14, 17) AND IS_DELETED = 0", nativeQuery = true)
    public long getStagingLineCountByStatusId(
            @Param("companyId") String companyId,
            @Param("plantId") String plantId,
            @Param("warehouseId") String warehouseId,
            @Param("manufacturerCode") String manufacturerCode,
            @Param("preInboundNo") String preInboundNo,
            @Param("refDocNumber") String refDocNumber);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndCaseCodeAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String refDocNumber, String preInboundNo,
            Long lineNo, String itemCode, String caseCode, String manufacturerCode, Long deletionIndicator);

    @Query(value = "select (COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0)) from tblinventory i \n" +
            "WHERE i.ITM_CODE in (:itemCode) AND i.WH_ID in (:warehouseId) AND \n" +
            "i.LANG_ID in (:languageId) AND i.plant_id in (:plantId) AND i.c_id in (:companyCodeId)", nativeQuery = true)
    public Double getTotalQuantity(
            @Param(value = "itemCode") String itemCode,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "languageId") String languageId,
            @Param(value = "plantId") String plantId,
            @Param(value = "companyCodeId") String companyCodeId);

    //Almailem
    @Query(value = "select sum(totalQuantity) from (select (COALESCE(i.inv_qty,0) + COALESCE(i.alloc_qty,0)) totalQuantity from tblinventory i \n" +
            "WHERE i.ITM_CODE in (:itemCode) AND i.WH_ID in (:warehouseId) AND \n" +
            "i.LANG_ID in (:languageId) AND i.plant_id in (:plantId) AND i.c_id in (:companyCodeId)) x", nativeQuery = true)
    public Double getTotalQuantityNew(
            @Param(value = "itemCode") String itemCode,
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "languageId") String languageId,
            @Param(value = "plantId") String plantId,
            @Param(value = "companyCodeId") String companyCodeId);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndLineNoAndItemCodeAndCaseCodeAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String warehouseId, String preInboundNo, String refDocNumber,
            String stagingNo, Long lineNo, String itemCode, String caseCode,
            String manufacturerCode, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndLineNoAndItemCodeAndStatusIdInAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId,
            String preInboundNo, Long lineNo, String itemCode,
            List<Long> statusIds, String manufacturerCode, Long deletionIndicator);

    //Company Description
    @Query(value = "select c_text from tblcompanyid tc \n" +
            "WHERE tc.c_id in (:companyCodeId) and \n" +
            "tc.is_deleted = 0", nativeQuery = true)
    public String getCompanyDescription(@Param(value = "companyCodeId") String companyCodeId);

    //Plant Description
    @Query(value = "select plant_text from tblplantid tp \n" +
            "WHERE tp.c_id in (:companyCodeId) and tp.plant_id in (:plantId) and \n" +
            "tp.is_deleted = 0", nativeQuery = true)
    public String getPlantDescription(@Param(value = "companyCodeId") String companyCodeId,
                                      @Param(value = "plantId") String plantId);

    //Warehouse Description
    @Query(value = "select wh_text from tblwarehouseid wh \n" +
            "WHERE wh.c_id in (:companyCodeId) and wh.plant_id in (:plantId) and wh.wh_id in (:warehouseId) and \n" +
            "wh.is_deleted = 0", nativeQuery = true)
    public String getWarehouseDescription(@Param(value = "companyCodeId") String companyCodeId,
                                          @Param(value = "plantId") String plantId,
                                          @Param(value = "warehouseId") String warehouseId);

    //Partner_item_barcode
    @Query(value = "select string_agg(partner_itm_bar,', ') from tblimpartner ip \n" +
            "WHERE ip.itm_code in (:itemCode) and \n" +
            "ip.is_deleted = 0", nativeQuery = true)
    public String getPartnerItemBarcode(@Param(value = "itemCode") String itemCode);

    //Partner_item_barcode - almailem
    @Query(value = "select string_agg(partner_itm_bar,', ') from (select distinct partner_itm_bar from tblimpartner ip \n" +
            "WHERE ip.itm_code in (:itemCode) and \n" +
            "ip.c_id in (:companyCode) and \n" +
            "ip.plant_id in (:plantId) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.lang_id in (:languageId) and \n" +
            "ip.mfr_name in (:manufactureName) and \n" +
            "ip.is_deleted = 0) x", nativeQuery = true)
    public String getItemBarcode(@Param(value = "itemCode") String itemCode,
                                        @Param(value = "companyCode") String companyCode,
                                        @Param(value = "plantId") String plantId,
                                        @Param(value = "warehouseId") String warehouseId,
                                        @Param(value = "manufactureName") String manufactureName,
                                        @Param(value = "languageId") String languageId);

    //Partner_item_barcode - almailem
    @Query(value = "select partner_itm_bar from tblimpartner ip \n" +
            "WHERE ip.itm_code in (:itemCode) and \n" +
            "ip.c_id in (:companyCode) and \n" +
            "ip.plant_id in (:plantId) and \n" +
            "ip.wh_id in (:warehouseId) and \n" +
            "ip.lang_id in (:languageId) and \n" +
            "ip.mfr_name in (:manufactureName) and \n" +
            "ip.is_deleted = 0 order by ctd_on desc", nativeQuery = true)
    public List<String> getPartnerItemBarcode(@Param(value = "itemCode") String itemCode,
                                              @Param(value = "companyCode") String companyCode,
                                              @Param(value = "plantId") String plantId,
                                              @Param(value = "warehouseId") String warehouseId,
                                              @Param(value = "manufactureName") String manufactureName,
                                              @Param(value = "languageId") String languageId);

    //Partner_item_barcode - almailem - interim only
    @Query(value = "select string_agg(barcode,', ') from (select distinct barcode from tblinterimbarcodeid ip \n" +
            "WHERE ip.itm_code in (:itemCode) and \n" +
            "ip.ref_field_1 in (:manufactureCode) ) x", nativeQuery = true)
    public String getPartnerItemBarcode(@Param(value = "itemCode") String itemCode,
                                        @Param(value = "manufactureCode") String manufactureCode);


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

    //Status Description
    @Query(value = "select status_text \n" +
            "from tblstatusid \n" +
            "where \n" +
            "status_id IN (:statusId) and \n" +
            "lang_id IN (:languageId) and \n" +
            "is_deleted=0", nativeQuery = true)
    public String getStatusDescription(@Param(value = "statusId") Long statusId,
                                       @Param(value = "languageId") String languageId);

    @Query(value = "select tc.stck_typ_text stockTypeDescription from \n" +
            "tblstocktypeid tc\n" +
            "where\n" +
            "tc.stck_typ_id IN (:stockTypeId) and \n" +
            "tc.c_id IN (:companyCodeId) and \n" +
            "tc.lang_id IN (:languageId) and \n" +
            "tc.plant_id IN(:plantId) and \n" +
            "tc.wh_id IN (:warehouseId) and \n" +
            "tc.is_deleted=0", nativeQuery = true)
    public String getStockTypeDescription(@Param(value = "companyCodeId") String companyCodeId,
                                          @Param(value = "plantId") String plantId,
                                          @Param(value = "languageId") String languageId,
                                          @Param(value = "warehouseId") String warehouseId,
                                          @Param(value = "stockTypeId") Long stockTypeId);

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

    @Query(value = "select top 1 tc.itm_type_id itemType,tc.itm_typ itemTypeDescription from tblitemtypeid tc \n" +
            "join tblimbasicdata1 tbd on tbd.c_id=tc.c_id and tbd.plant_id=tc.plant_id and \n" +
            "tbd.lang_id=tc.lang_id and tbd.wh_id=tc.wh_id and tbd.itm_typ_id=tc.itm_type_id \n" +
            "where\n" +
            "tbd.itm_code IN (:itemCode) and \n" +
            "tbd.c_id IN (:companyCodeId) and \n" +
            "tbd.lang_id IN (:languageId) and \n" +
            "tbd.plant_id IN(:plantId) and \n" +
            "tbd.wh_id IN (:warehouseId) and \n" +
            "tbd.is_deleted=0", nativeQuery = true)
    public IKeyValuePair getItemTypeAndDescription(@Param(value = "companyCodeId") String companyCodeId,
                                                   @Param(value = "plantId") String plantId,
                                                   @Param(value = "languageId") String languageId,
                                                   @Param(value = "warehouseId") String warehouseId,
                                                   @Param(value = "itemCode") String itemCode);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String refDocNumber, String preInboundNo, Long lineNo, String itemCode, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String refDocNumber, String preInboundNo, Long lineNo, String itemCode, String caseCode, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndPreInboundNoAndLineNoAndItemCodeAndStatusIdInAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String preInboundNo,
            Long lineNo, String itemCode, List<Long> statusIds, Long deletionIndicator);

    Optional<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, String stagingNo, String palletCode,
            String caseCode, Long lineNo, String itemCode, Long deletionIndicator);

    Optional<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndPalletCodeAndCaseCodeAndLineNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, String stagingNo, String palletCode,
            String caseCode, Long lineNo, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndStagingNoAndLineNoAndItemCodeAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String preInboundNo, String refDocNumber, String stagingNo, Long lineNo,
            String itemCode, String caseCode, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndStagingNoAndCaseCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String stagingNo, String caseCode, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndStagingNoAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String stagingNo, String refDocNumber, String preInboundNo, Long deletionIndicator);

    //HHt User by order type id
    @Query(value = "select ht.usr_id \n" +
            "from tblhhtuser ht join tblordertypeid ot on  ot.usr_id = ht.usr_id \n" +
            "where \n" +
            "ht.c_id IN (:companyCodeId) and \n" +
            "ht.lang_id IN (:languageId) and \n" +
            "ht.plant_id IN (:plantId) and \n" +
            "ht.wh_id IN (:warehouseId) and \n" +
            "ot.ob_ord_typ_id IN (:inboundOrderTypeId) and \n" +
            "ht.is_deleted=0", nativeQuery = true)
    public List<String> getHhtUserByOrderType(@Param(value = "companyCodeId") String companyCodeId,
                                              @Param(value = "languageId") String languageId,
                                              @Param(value = "plantId") String plantId,
                                              @Param(value = "warehouseId") String warehouseId,
                                              @Param(value = "inboundOrderTypeId") Long inboundOrderTypeId);

    //HHt User
    @Query(value = "select ht.usr_id \n" +
            "from tblhhtuser ht \n" +
            "where \n" +
            "ht.c_id IN (:companyCodeId) and \n" +
            "ht.lang_id IN (:languageId) and \n" +
            "ht.plant_id IN (:plantId) and \n" +
            "ht.wh_id IN (:warehouseId) and \n" +
            "ht.is_deleted=0", nativeQuery = true)
    public List<String> getHhtUser(@Param(value = "companyCodeId") String companyCodeId,
                                   @Param(value = "languageId") String languageId,
                                   @Param(value = "plantId") String plantId,
                                   @Param(value = "warehouseId") String warehouseId);

    Optional<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndManufacturerCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preInboundNo,
            String refDocNumber, Long lineNo, String itemCode, String manufacturerCode, Long deletionIndicator);

    Optional<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preInboundNo,
            String refDocNumber, Long lineNo, String itemCode, String manufacturerName, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    List<StagingLineEntityV2> findByLanguageIdInAndCompanyCodeInAndPlantIdInAndWarehouseIdInAndRefDocNumberInAndPreInboundNoInAndDeletionIndicator(
            List<String> languageId,List<String> companyCode, List<String> plantId, List<String> warehouseId, List<String> refDocNumber, List<String> preInboundNo,Long deletionIndicator);



    StagingLineEntityV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, Long lineNo, String itemCode, String manufacturerName, Long deletionIndicator);

    StagingLineEntityV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndCaseCodeAndPalletCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String itemCode, String manufacturerName, String caseCode, String palletCode, Long deletionIndicator);
    StagingLineEntityV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndCaseCodeAndPalletCodeAndLineNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String itemCode, String manufacturerName, String caseCode, String palletCode, Long lineNo, Long deletionIndicator);

    List<StagingLineEntityV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    List<StagingLineEntityV2> findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

    List<StagingLineEntityV2> findAllByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            String itemCode, String manufacturerName, Long deletionIndicator);

    @Transactional
    @Procedure(procedureName = "staging_line_update_proc")
    public void updateStagingLineUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("itmCode") String itmCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("lineNumber") Long lineNumber,
            @Param("updatedOn") Date updatedOn,
            @Param("recAcceptQty") Double recAcceptQty,
            @Param("recDamageQty") Double recDamageQty
    );

    @Transactional
    @Procedure(procedureName = "staging_line_update_new_proc")
    public void updateStagingLineUpdateNewProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("updatedOn") Date updatedOn
    );

    @Transactional
    @Procedure(procedureName = "stagingline_status_update_ib_cnf_proc")
    public void updateStagingLineStatusUpdateInboundConfirmProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );

    @Transactional
    @Procedure(procedureName = "stagingline_inv_qty_update_proc")
    public void updateStagingLineInvQtyUpdateProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo
    );

    //update barcode - stagingline - barcode is null
    @Query(value = "select * from tblstagingline i \n" +
            "WHERE i.ITM_CODE in (:itemCode) AND i.MFR_NAME in (:manufacturerName) AND i.WH_ID in (:warehouseId) AND \n" +
            "i.LANG_ID in (:languageId) AND i.PLANT_ID in (:plantId) AND i.C_ID in (:companyCodeId) AND \n" +
            "i.STATUS_ID <> 17 AND i.PARTNER_ITEM_BARCODE IS NULL AND i.IS_DELETED = 0", nativeQuery = true)
    public List<StagingLineEntityV2> getStagingLineList(@Param(value = "itemCode") String itemCode,
                                                        @Param(value = "manufacturerName") String manufacturerName,
                                                        @Param(value = "companyCodeId") String companyCodeId,
                                                        @Param(value = "plantId") String plantId,
                                                        @Param(value = "languageId") String languageId,
                                                        @Param(value = "warehouseId") String warehouseId);

    @Transactional
    @Procedure(procedureName = "stgl_stgh_ibl_status_update_proc")
    public void updateStagingLineHeaderInboundLineStatusUpdateProc(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("statusId") Long statusId,
            @Param("statusDescription") String statusDescription,
            @Param("updatedBy") String updatedBy,
            @Param("updatedOn") Date updatedOn
    );
    
    @Query(value = "select ib_ord_typ from tblinboundordertypeid i \n" +
            "WHERE i.WH_ID in (:warehouseId) AND \n" +
            "i.LANG_ID in (:languageId) AND i.PLANT_ID in (:plantId) AND i.C_ID in (:companyCodeId) AND \n" +
            "i.IB_ORD_TYP_ID in (:orderTypeId) AND i.IS_DELETED = 0", nativeQuery = true)
    public String getInboundOrderTypeDescription(@Param(value = "orderTypeId") Long orderTypeId,
                                                 @Param(value = "companyCodeId") String companyCodeId,
                                                 @Param(value = "plantId") String plantId,
                                                 @Param(value = "languageId") String languageId,
                                                 @Param(value = "warehouseId") String warehouseId);

    @Query(value = "select ob_ord_typ from tbloutboundordertypeid i \n" +
            "WHERE i.WH_ID in (:warehouseId) AND \n" +
            "i.LANG_ID in (:languageId) AND i.PLANT_ID in (:plantId) AND i.C_ID in (:companyCodeId) AND \n" +
            "i.OB_ORD_TYP_ID in (:orderTypeId) AND i.IS_DELETED = 0", nativeQuery = true)
    public String getOutboundOrderTypeDescription(@Param(value = "orderTypeId") Long orderTypeId,
                                                  @Param(value = "companyCodeId") String companyCodeId,
                                                  @Param(value = "plantId") String plantId,
                                                  @Param(value = "languageId") String languageId,
                                                  @Param(value = "warehouseId") String warehouseId);

    //===============================================Walkaroo-v3========================================================
    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentProductionOrderNoAndDeletionIndicatorOrderByReferenceField5(
            String languageId, String companyCode, String plantId, String warehouseId, String parentProductionOrderNo, Long deletionIndicator);

    @Query(value = "select top 1 ht.usr_id from tblhhtuser ht \n" +
            "where \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ht.is_deleted = 0 ", nativeQuery = true)
    public String getHHTUser(@Param("companyCodeId") String companyCodeId,
                             @Param("plantId") String plantId,
                             @Param("languageId") String languageId,
                             @Param("warehouseId") String warehouseId);

    @Query(value = "select top 1 ht.CAS_NO noOfCase, ht.PL_TOL plusTolerance, ht.MS_TOL minusTolerance from tblwarehouse ht \n" +
            "where \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ht.is_deleted = 0 ", nativeQuery = true)
    public IKeyValuePair getNoOfCaseAndTolerance(@Param("companyCodeId") String companyCodeId,
                                                 @Param("plantId") String plantId,
                                                 @Param("languageId") String languageId,
                                                 @Param("warehouseId") String warehouseId);

    @Query(value = "select top 1 UOM_TEXT from tbluomid ht \n" +
            "where \n" +
            "ht.c_id in (:companyCodeId) and \n" +
            "ht.plant_id in (:plantId) and \n" +
            "ht.lang_id in (:languageId) and \n" +
            "ht.wh_id in (:warehouseId) and \n" +
            "ht.is_deleted = 0 ", nativeQuery = true)
    public String getUomId(@Param("companyCodeId") String companyCodeId,
                           @Param("plantId") String plantId,
                           @Param("languageId") String languageId,
                           @Param("warehouseId") String warehouseId);

    StagingLineEntityV2 findByBarcodeIdAndDeletionIndicator(String barcodeId, Long deletionIndicator);

    StagingLineEntityV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndStagingNoAndCaseCodeAndPalletCodeAndLineNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, String itemCode, String stagingNo, String caseCode,
            String palletCode, Long lineNo, Long deletionIndicator);

    List<StagingLineEntityV2> findByRefDocNumberAndBarcodeIdAndDeletionIndicator(String refDocNumber, String huSerialNo, long l);


    StagingLineEntityV2 findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndLineNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, String itemCode, Long lineNo, Long deletionIndicator
    );

    @Modifying
    @Query(value = "UPDATE tblstagingline set status_id = :statusId, status_text = :statusText where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ib_no = :preInboundNo " +
            "AND itm_code = :itemCode AND ib_line_no = :lineNo and is_deleted = 0", nativeQuery = true)
    void updateStagingLinePGI(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("preInboundNo") String preInbound,
                              @Param("itemCode") String itemCode,
                              @Param("lineNo") Long lineNo,
                              @Param("statusId") Long statusId,
                              @Param("statusText") String statusText);

    @Modifying
    @Query(value = "UPDATE tblstagingline set status_id = :statusId, status_text = :statusText where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND itm_code = :itemCode AND ib_line_no = :lineNo and is_deleted = 0", nativeQuery = true)
    void updateStagingLine(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("warehouseId") String warehouseId,
                              @Param("refDocNumber") String refDocNumber,
                              @Param("itemCode") String itemCode,
                              @Param("lineNo") Long lineNo,
                              @Param("statusId") Long statusId,
                              @Param("statusText") String statusText);

    @Query(value = "SELECT status_text from tblstatusid where status_id = :statusId " +
            "AND is_deleted = 0", nativeQuery = true)
    String getReversalDesc(@Param("statusId") Long statusId);


    @Query(value = "SELECT * from tblstagingline where ref_doc_no = :refDocNumber " +
            "AND PARTNER_ITEM_BARCODE = :barcodeId and is_deleted = 0", nativeQuery = true)
    StagingLineEntityV2 findRefDocNo(String refDocNumber, String barcodeId);

    @Query(value = "SELECT COUNT(*) FROM tblstagingline WHERE ref_doc_no = :refDocNo AND status_id = :statusId", nativeQuery = true)
    long countByRefDocNoAndStatusId(@Param("refDocNo") String refDocNo, @Param("statusId") Long statusId);

    @Query(value = "SELECT COUNT(*) FROM tblstagingline WHERE ref_doc_no = :refDocNo", nativeQuery = true)
    long countByRefDocNo(@Param("refDocNo") String refDocNo);

    @Query(value = "SELECT CASE WHEN EXISTS (" +
            "SELECT 1 FROM tblputawayheader " +
            "WHERE ref_doc_no = :refDocNumber AND status_id IN (24, 20) AND is_deleted = 0" +
            ") THEN 1 ELSE 0 END", nativeQuery = true)
    int existsByRefDocNumberAndStatusId(@Param("refDocNumber") String refDocNumber);

    @Query(
            value = "SELECT \n" +
                    "    ref_doc_no AS refDocNumber,\n" +
                    "    COUNT(CASE WHEN partner_item_barcode IS NOT NULL THEN 1 END) AS totalCount,\n" +
                    "    COUNT(CASE WHEN partner_item_barcode IS NOT NULL AND status_id = 101 THEN 1 END) AS totalBarcodeStatusCount,\n" +
                    "    COUNT(CASE WHEN vehicle_no IS NOT NULL THEN 1 END) AS totalVehicleHuNumber,\n" +
                    "    COUNT(CASE WHEN vehicle_no IS NOT NULL AND status_id = 101 THEN 1 END) AS totalVehicleScan,\n" +
                    "    MAX(vehicle_no) AS vehicleNumber,\n" +
                    "    MAX(st_ctd_on) AS createdOn,\n" +
                    "    CASE \n" +
                    "       WHEN COUNT(CASE WHEN partner_item_barcode IS NOT NULL THEN 1 END)\n" +
                    "         = COUNT(CASE WHEN partner_item_barcode IS NOT NULL AND status_id = 101 THEN 1 END)\n" +
                    "       THEN 1 ELSE 0 \n" +
                    "    END AS putAwayFlag \n" +
                    "FROM \n" +
                    "    tblstagingline sl1 \n" +
                    "WHERE \n" +
                    "    status_id != 17 AND status_id != 24 \n" +
                    "    AND vehicle_no IS NOT NULL\n" +
                    "    AND (:vehicleNo IS NULL OR vehicle_no IN (:vehicleNo)) \n" +
                    "GROUP BY \n" +
                    "    ref_doc_no;\n",
            nativeQuery = true
    )
    List<GroupedStagingLineProjection> findGroupedStagingLinesNative(@Param("vehicleNo") List<String> vehicleNo);

    @Query(value = "SELECT \n" +
            "    vehicle_no AS refDocNumber,\n" +
            "    COUNT(CASE WHEN partner_item_barcode IS NOT NULL THEN 1 END) AS totalCount,\n" +
            "    COUNT(CASE WHEN partner_item_barcode IS NOT NULL AND status_id = 101 THEN 1 END) AS totalBarcodeStatusCount,\n" +
            "    COUNT(CASE WHEN vehicle_no IS NOT NULL THEN 1 END) AS totalVehicleHuNumber,\n" +
            "    COUNT(CASE WHEN vehicle_no IS NOT NULL AND (status_id = 101 OR status_id = 24) THEN 1 END) AS totalVehicleScan,\n" +
            "    MAX(vehicle_no) AS vehicleNumber,\n" +
            "    MAX(st_ctd_on) AS createdOn,\n" +
            "    CASE \n" +
            "       WHEN COUNT(CASE WHEN partner_item_barcode IS NOT NULL THEN 1 END)\n" +
            "         = COUNT(CASE WHEN partner_item_barcode IS NOT NULL AND status_id = 101 THEN 1 END)\n" +
            "       THEN 1 ELSE 0 \n" +
            "    END AS putAwayFlag \n" +
            "FROM \n" +
            "    tblstagingline sl1 \n" +
            "WHERE \n" +
            "    status_id != 17 AND status_id != 24 \n" +
            "    AND vehicle_no IS NOT NULL \n" +
            "    AND (COALESCE(CONVERT(VARCHAR(225), :startCreatedOn), NULL) IS NULL OR " +
            "    (st_ctd_on BETWEEN COALESCE(CONVERT(VARCHAR(225), :startCreatedOn), NULL) " +
            "    AND COALESCE(CONVERT(VARCHAR(225), :endCreatedOn), NULL))) \n" +
            // "    AND st_ctd_on BETWEEN :startCreatedOn AND :endCreatedOn \n" +
            "GROUP BY \n" +
            "    vehicle_no; \n",
            nativeQuery = true
    )
    List<GroupedStagingLineProjection> findGroupedStagingLinesVehicleWise(@Param("startCreatedOn") Date startCreatedOn,
                                                                          @Param("endCreatedOn") Date endCreatedOn);

    //-------------------SAP-DATA-UPDATE-----------------------------------------------------------------------
    @Modifying
    @Query(value = "UPDATE tblstagingline \r\n"
    		+ "SET sap_document_no = :sapDocumentNo, sap_message = :sapMmessage, \r\n"
    		+ "sap_type = :sapType, material_doc_date = :matDocDate \r\n"
    		+ "WHERE ref_doc_no = :refDocNumber AND is_deleted = 0", nativeQuery = true)
    void updateStagingLine_SAP(@Param("refDocNumber") String refDocNumber,
                              @Param("sapDocumentNo") String sapDocumentNo,
                              @Param("sapMmessage") String sapMmessage,
                              @Param("sapType") String sapType,
                              @Param("matDocDate") Date matDocDate);

    List<StagingLineEntityV2> findByRefDocNumberAndDeletionIndicator(String refDocNo, Long deletionIndicator);
}
