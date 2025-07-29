package com.tekclover.wms.api.outbound.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.PickupLine;

@Repository
@Transactional
public interface PickupLineRepository extends JpaRepository<PickupLine, Long>,
        JpaSpecificationExecutor<PickupLine>, StreamableJpaSpecificationRepository<PickupLine> {

    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "1000"))
    public List<PickupLine> findAll();

    public Optional<PickupLine> findByActualHeNo(String actualHeNo);

    public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, Long deletionIndicator);

    public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndActualHeNoAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String pickupNumber, String itemCode, String pickedStorageBin, String pickedPackCode, String actualHeNo,
            long l);

    public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, Long deletionIndicator);

    public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndPickedPackCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, String pickedPackCode, Long deletionIndicator);

    public PickupLine findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndActualHeNoAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String pickupNumber, String itemCode, String actualHeNo, String pickedStorageBin, String pickedPackCode, Long deletionIndicator);

    @Query(value = "SELECT SUM(PICK_CNF_QTY) FROM tblpickupline WHERE WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber AND\r\n"
            + "PRE_OB_NO = :preOutboundNo AND OB_LINE_NO = :obLineNumber AND ITM_CODE = :itemCode AND IS_DELETED = 0 \r\n"
            + "GROUP BY REF_DOC_NO", nativeQuery = true)
    public Double getPickupLineCount(@Param("warehouseId") String warehouseId,
                                     @Param("refDocNumber") String refDocNumber,
                                     @Param("preOutboundNo") String preOutboundNo,
                                     @Param("obLineNumber") Long obLineNumber,
                                     @Param("itemCode") String itemCode);

    public List<PickupLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers,
            List<String> itemCodes, long l);

    @Query(value = "SELECT COUNT(OB_LINE_NO) AS lineCount FROM tblpickupline \r\n"
            + "	WHERE WH_ID = :warehouseId AND REF_DOC_NO IN :refDocNumber AND PRE_OB_NO = :preOutboundNo\r\n"
            + "	AND STATUS_ID=50 AND IS_DELETED = 0 GROUP BY REF_DOC_NO", nativeQuery = true)
    public Double getCountByWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
            @Param("warehouseId") String warehouseId,
            @Param("preOutboundNo") String preOutboundNo,
            @Param("refDocNumber") List<String> refDocNumber);

    public List<PickupLine> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoInAndRefDocNumberInAndPartnerCodeAndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, List<String> preOutboundNo,
            List<String> refDocNumber, String partnerCode, Long statusId, long l);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = "SELECT SUM(PU_QTY) AS SUM_PUQTY_VALUE FROM tblpickupline \r\n"
            + " WHERE ITM_CODE IN :itemCode AND STATUS_ID = 50 AND IS_DELETED = 0 \r\n"
            + " AND PICK_CTD_ON BETWEEN :dateFrom AND :dateTo GROUP BY ITM_CODE", nativeQuery = true)
    public Double findSumOfPickupLineQty(@Param(value = "itemCode") List<String> itemCode,
                                         @Param(value = "dateFrom") Date dateFrom,
                                         @Param(value = "dateTo") Date dateTo);

    @Query(value = "select level_id as levelId from tblhhtuser where usr_id in (:userId) and " +
            "c_id in (:companyCode) and plant_id in (:plantId) and lang_id in (:languageId) and " +
            " wh_id in (:warehouseId) and is_deleted = 0 ", nativeQuery = true)
    public String getLevelId(@Param("companyCode") String companyCode,
                             @Param("plantId") String plantId,
                             @Param("languageId") String languageId,
                             @Param("warehouseId") String warehouseId,
                             @Param(value = "userId") String userId);

    @Query(value = "SELECT level_id AS levelId FROM tblhhtuser WHERE "
            + "(:userId IS NULL OR usr_id IN (:userId)) AND "
            + "(:companyCode IS NULL OR c_id IN (:companyCode)) AND "
            + "(:plantId IS NULL OR plant_id IN (:plantId)) AND "
            + "(:languageId IS NULL OR lang_id IN (:languageId)) AND "
            + "(:warehouseId IS NULL OR wh_id IN (:warehouseId)) AND "
            + "is_deleted = 0", nativeQuery = true)
    public String getLevelIdForUserId(@Param("companyCode") List<String> companyCode,
                                      @Param("plantId") List<String> plantId,
                                      @Param("languageId") List<String> languageId,
                                      @Param("warehouseId") List<String> warehouseId,
                                      @Param("userId") List<String> userId);

    //Status Description
    @Query(value = "select status_text \n" +
            "from tblstatusid \n" +
            "where \n" +
            "status_id IN (:statusId) and \n" +
            "lang_id IN (:languageId) and \n" +
            "is_deleted=0", nativeQuery = true)
    public String getStatusDescription(@Param(value = "statusId") Long statusId,
                                       @Param(value = "languageId") String languageId);

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

    @Query(value = "select top 1 partner_nm from tblbusinesspartner \n" +
            "WHERE partner_typ = 2 and partner_code = :partnerCode AND WH_ID in (:warehouseId) AND \n" +
            "LANG_ID in (:languageId) AND PLANT_ID in (:plantId) AND C_ID in (:companyCodeId) AND \n" +
            "IS_DELETED = 0", nativeQuery = true)
    public String getCustomerName(@Param("companyCodeId") String companyCodeId,
                                  @Param("plantId") String plantId,
                                  @Param("languageId") String languageId,
                                  @Param("warehouseId") String warehouseId,
                                  @Param("partnerCode") String partnerCode);

    @Query(value = "SELECT COUNT(*) from tblpickupline WHERE C_ID = :companyCodeId \n " +
            "AND PLANT_ID = :plantId AND LANG_ID = :languageId AND WH_ID = :warehouseId \n " +
            "AND REF_DOC_NO = :refDocNo AND ITM_CODE = :itemCode AND IS_DELETED = 0", nativeQuery = true)
    public Long getPickupLineCountForBags(@Param("companyCodeId") String companyCodeId,
                                     @Param("plantId") String plantId,
                                     @Param("languageId") String languageId,
                                     @Param("warehouseId") String warehouseId,
                                     @Param("refDocNo") String refDocNo,
                                     @Param("itemCode") String itemCode);


}