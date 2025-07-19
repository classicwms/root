package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.InboundLine;

@Repository
@Transactional
public interface InboundLineRepository extends JpaRepository<InboundLine, Long>,
        JpaSpecificationExecutor<InboundLine>, StreamableJpaSpecificationRepository<InboundLine> {

    String UPGRADE_SKIPLOCKED = "-2";

    /**
     *
     */
    public List<InboundLine> findAll();

    /**
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @param deletionIndicator
     * @return
     */
    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = UPGRADE_SKIPLOCKED)})
    public Optional<InboundLineV2>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
            String languageId,
            String companyCode,
            String plantId,
            String warehouseId,
            String refDocNumber,
            String preInboundNo,
            Long lineNo,
            String itemCode,
            Long deletionIndicator);

    public List<InboundLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, Long deletionIndicator);


    @Query(value = "select \n" +
            "* \n" +
            "from \n" +
            "tblinboundline \n" +
            "where \n" +
            "c_id IN (:companyCode) and \n" +
            "lang_id IN (:languageId) and \n" +
            "plant_id IN(:plantId) and \n" +
            "wh_id IN (:warehouseId) and \n" +
            "ib_line_no IN (:lineNo) and \n" +
            "itm_code IN (:itemCode) and \n" +
            "pre_ib_no IN (:preInboundNo) and \n" +
            "ref_doc_no IN (:refDocNumber) and \n" +
            "is_deleted = 0", nativeQuery = true)
    InboundLineV2 getInboundLineV2(
            @Param(value = "warehouseId") String warehouseId,
            @Param(value = "lineNo") Long lineNo,
            @Param(value = "preInboundNo") String preInboundNo,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "companyCode") String companyCode,
            @Param(value = "plantId") String plantId,
            @Param(value = "languageId") String languageId,
            @Param(value = "refDocNumber") String refDocNumber
    );


    // For Sending confirmation API
    public List<InboundLine> findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndReferenceField1AndStatusIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String refDocNumber,
            String preInboundNo, String referenceField1, Long statusId, Long deletionIndicator);

    public List<InboundLine> findByRefDocNumberAndDeletionIndicator(String refDocNumber, long l);

    @Query(value = "select il.wh_id as warehouseId, il.itm_code as itemCode, 'InBound' as documentType ,il.ref_doc_no as documentNumber, il.partner_code as partnerCode, "
            + " (COALESCE(il.accept_qty,0) + COALESCE(il.damage_qty,0)) as movementQty, im.text as itemText ,im.mfr_part as manufacturerSKU from tblinboundline il "
            + " join tblimbasicdata1 im on il.itm_code = im.itm_code WHERE il.ITM_CODE in (:itemCode) AND im.WH_ID in (:warehouseId) AND il.WH_ID in (:warehouseId) AND il.status_id in (:statusId) "
            + " AND (il.accept_qty is not null OR il.damage_qty is not null)",
            nativeQuery = true)
    public List<StockMovementReportImpl> findInboundLineForStockMovement(@Param("itemCode") List<String> itemCode,
                                                                         @Param("warehouseId") List<String> warehouseId,
                                                                         @Param("statusId") List<Long> statusId);

    @Query(value = "Select top 1 PA_CNF_ON from tblputawayline where ref_doc_no = :refDocNo and itm_code = :itemCode order by PA_CNF_ON DESC",
            nativeQuery = true)
    public Date findDateFromPutawayLine(@Param("refDocNo") String refDocNo, @Param("itemCode") String itemCode);

    public List<InboundLine> findByRefDocNumberAndWarehouseIdAndDeletionIndicator(String refDocNumber,
                                                                                  String warehouseId, long l);

    long countByWarehouseIdAndConfirmedOnBetweenAndStatusIdAndReferenceField1IsNull(
            String warehouseId, Date fromDate, Date toDate, Long statusId);

    @Query(value = "select (COALESCE(il.accept_qty,0) + COALESCE(il.damage_qty,0)) as quantity \n" +
            "from tblinboundline il where il.itm_code = :itemCode and il.IB_LINE_NO = :lineNo and il.ref_doc_no = :refDocNo and \n" +
            "il.PRE_IB_NO = :preInboundNo and il.wh_id = :warehouseId and il.IS_DELETED = 0 ", nativeQuery = true)
    public Double getQuantityByRefDocNoAndPreInboundNoAndLineNoAndItemCode(@Param("itemCode") String itemCode,
                                                                           @Param("refDocNo") String refDocNo,
                                                                           @Param("preInboundNo") String preInboundNo,
                                                                           @Param("lineNo") Long lineNo,
                                                                           @Param("warehouseId") String warehouseId);

    /**
     *
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     * @param confirmedBy
     * @param confirmedOn
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE InboundLine ib SET ib.statusId = :statusId, ib.confirmedBy = :confirmedBy, ib.confirmedOn = :confirmedOn WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber")
    void updateInboundLineStatus(@Param("warehouseId") String warehouseId,
                                 @Param("refDocNumber") String refDocNumber,
                                 @Param("statusId") Long statusId,
                                 @Param("confirmedBy") String confirmedBy,
                                 @Param("confirmedOn") Date confirmedOn);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE InboundLine ib SET ib.statusId = :statusId, ib.confirmedBy = :confirmedBy, ib.confirmedOn = :confirmedOn, ib.statusDescription = :statusDescription \n" +
            "WHERE ib.warehouseId = :warehouseId AND ib.refDocNumber = :refDocNumber and ib.companyCode = :companyCode and ib.plantId = :plantId and ib.languageId = :languageId")
    void updateInboundLineStatus(@Param("warehouseId") String warehouseId,
                                 @Param("companyCode") String companyCode,
                                 @Param("plantId") String plantId,
                                 @Param("languageId") String languageId,
                                 @Param("refDocNumber") String refDocNumber,
                                 @Param("statusId") Long statusId,
                                 @Param("statusDescription") String statusDescription,
                                 @Param("confirmedBy") String confirmedBy,
                                 @Param("confirmedOn") Date confirmedOn);
}

