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

    List<StagingLineEntityV2> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber, String preInboundNo, Long deletionIndicator);

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
    @Procedure(procedureName = "alm_staging_line_update_proc_v2")
    public void updateStagingLineUpdateNewProc(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("refDocNumber") String refDocNumber,
            @Param("preInboundNo") String preInboundNo,
            @Param("lineNumber") Long lineNumber,
            @Param("itmCode") String itmCode,
            @Param("mfrName") String mfrName,
            @Param("updatedOn") Date updatedOn
    );


    @Modifying
    @Query(value =
            "UPDATE IBL SET IBL.REC_ACCEPT_QTY = X.ACCEPTQTY, IBL.REC_DAMAGE_QTY = X.DAMAGEQTY, IBL.ST_UTD_ON = :updatedOn, IBL.ST_CNF_ON = :updatedOn " +
                    "FROM tblstagingline IBL " +
                    "INNER JOIN (SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_IB_NO, IB_LINE_NO, ITM_CODE, MFR_NAME, " +
                    "SUM(ACCEPT_QTY) AS ACCEPTQTY, SUM(DAMAGE_QTY) AS DAMAGEQTY FROM tblgrline " +
                    "WHERE IS_DELETED = 0 AND C_ID = :companyCodeId " +
                    "AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber " +
                    "AND PRE_IB_NO = :preInboundNo AND IB_LINE_NO = :lineNumber AND ITM_CODE = :itmCode AND MFR_NAME = :mfrName " +
                    "GROUP BY ITM_CODE, MFR_NAME, IB_LINE_NO, REF_DOC_NO, PRE_IB_NO, WH_ID, PLANT_ID, C_ID, LANG_ID) X " +
                    "ON IBL.C_ID = X.C_ID " +
                    "AND IBL.PLANT_ID = X.PLANT_ID " +
                    "AND IBL.LANG_ID = X.LANG_ID " +
                    "AND IBL.WH_ID = X.WH_ID " +
                    "AND IBL.REF_DOC_NO = X.REF_DOC_NO " +
                    "AND IBL.PRE_IB_NO = X.PRE_IB_NO " +
                    "AND IBL.ITM_CODE = X.ITM_CODE " +
                    "AND IBL.MFR_NAME = X.MFR_NAME " +
                    "AND IBL.IB_LINE_NO = X.IB_LINE_NO " +
                    "AND IBL.IS_DELETED = 0",
            nativeQuery = true)
    void updateAcceptAndDamageQty(@Param("updatedOn") Date updatedOn,
                                  @Param("companyCodeId") String companyCodeId,
                                  @Param("plantId") String plantId,
                                  @Param("languageId") String languageId,
                                  @Param("warehouseId") String warehouseId,
                                  @Param("refDocNumber") String refDocNumber,
                                  @Param("preInboundNo") String preInboundNo,
                                  @Param("lineNumber") Long lineNumber,
                                  @Param("itmCode") String itmCode,
                                  @Param("mfrName") String mfrName);


    @Modifying
    @Query(value = "UPDATE IBL SET IBL.STATUS_ID = X.STATUS_ID, IBL.STATUS_TEXT = X.STATUS_TEXT " +
            "FROM tblstagingline IBL INNER JOIN ( " +
            "SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_IB_NO, IB_LINE_NO, ITM_CODE, MFR_NAME, STATUS_ID, STATUS_TEXT FROM tblgrline " +
            "WHERE IS_DELETED = 0 " +
            "AND C_ID = :companyCodeId AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId " +
            "AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo AND STATUS_ID <> 24 " +
            "AND IB_LINE_NO = :lineNumber AND ITM_CODE = :itmCode " +
            "AND MFR_NAME = :mfrName AND GR_CTD_ON IN (SELECT MAX(GR_CTD_ON) FROM TBLGRLINE " +
            "GROUP BY ITM_CODE, MFR_NAME, IB_LINE_NO, REF_DOC_NO, PRE_IB_NO )) X " +
            "ON IBL.C_ID = X.C_ID AND IBL.PLANT_ID = X.PLANT_ID AND IBL.LANG_ID = X.LANG_ID " +
            "AND IBL.WH_ID = X.WH_ID AND IBL.REF_DOC_NO = X.REF_DOC_NO " +
            "AND IBL.PRE_IB_NO = X.PRE_IB_NO AND IBL.ITM_CODE = X.ITM_CODE " +
            "AND IBL.MFR_NAME = X.MFR_NAME AND IBL.IB_LINE_NO = X.IB_LINE_NO " +
            "AND IBL.IS_DELETED = 0",
            nativeQuery = true)
    void updateStaingLineStatus(@Param("companyCodeId") String companyCodeId,
                                @Param("plantId") String plantId,
                                @Param("languageId") String languageId,
                                @Param("warehouseId") String warehouseId,
                                @Param("refDocNumber") String refDocNumber,
                                @Param("preInboundNo") String preInboundNo,
                                @Param("lineNumber") Long lineNumber,
                                @Param("itmCode") String itmCode,
                                @Param("mfrName") String mfrName);


    @Modifying
    @Query(value = "UPDATE STGL SET STGL.STATUS_ID = :statusId2, STGL.STATUS_TEXT = :statusDescription2, STGL.ST_CNF_BY = :updatedBy, STGL.ST_CNF_ON = :updatedOn \n" +
            "FROM tblstagingline STGL INNER JOIN (SELECT C_ID, PLANT_ID, LANG_ID, WH_ID, REF_DOC_NO, PRE_IB_NO, IB_LINE_NO, ITM_CODE, MFR_NAME FROM tblinboundline \n" +
            "WHERE IS_DELETED = 0 AND REF_FIELD_2 = 'TRUE' AND STATUS_ID = 24 AND C_ID = :companyCodeId \n" +
            "AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND PRE_IB_NO = :preInboundNo) X \n" +
            "ON STGL.C_ID = X.C_ID AND STGL.PLANT_ID = X.PLANT_ID AND STGL.LANG_ID = X.LANG_ID AND STGL.WH_ID = X.WH_ID AND STGL.REF_DOC_NO = X.REF_DOC_NO \n" +
            "AND STGL.PRE_IB_NO = X.PRE_IB_NO AND STGL.ITM_CODE = X.ITM_CODE AND STGL.MFR_NAME = X.MFR_NAME AND STGL.IB_LINE_NO = X.IB_LINE_NO \n" +
            "AND STGL.IS_DELETED = 0 ", nativeQuery = true)
    void updateStagingLine(@Param("companyCodeId") String companyCodeId,
                          @Param("plantId") String plantId,
                          @Param("languageId") String languageId,
                          @Param("warehouseId") String warehouseId,
                          @Param("refDocNumber") String refDocNumber,
                          @Param("preInboundNo") String preInboundNo,
                          @Param("statusId2") Long statusId2,
                          @Param("statusDescription2") String statusDescription2,
                          @Param("updatedBy") String updatedBy,
                          @Param("updatedOn") Date updatedOn);
}