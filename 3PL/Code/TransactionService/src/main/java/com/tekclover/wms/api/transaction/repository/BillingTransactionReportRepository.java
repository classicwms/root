package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.report.BillingTransactionReport;
import com.tekclover.wms.api.transaction.model.report.BillingTransactionReportImpl;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Transactional
@Repository
public interface BillingTransactionReportRepository extends JpaRepository<BillingTransactionReport, Long>,
        JpaSpecificationExecutor<BillingTransactionReport>, StreamableJpaSpecificationRepository<BillingTransactionReport> {
    @Transactional
    @Query(value = "Select c_id as companyCodeId, lang_id as languageId, plant_id as plantId, wh_id as warehouseId ,\n" +
            "partner_code as partnerCode, tpl_uom as threePLUom, total_tpl_cbm as threePLCbm, ITM_CODE as itemCode, REF_DOC_NO as refDocNumber, REPLACE('GR,CHARGES', ',', ' ') AS description, \n" +
            " Rate as rate, currency as currency, c_text companyDescription, plant_text plantDescription, wh_text warehouseDescription, ORD_QTY transactionQty \n" +
            "from tblgrline  \n" +
            "Where (coalesce(:languageId , null) is null or (lang_id IN (:languageId))) and (coalesce(:companyCodeId, null) is null or (c_id IN (:companyCodeId))) and " +
            "(coalesce(:plantId, null) is null or (plant_id IN (:plantId))) and (coalesce(:warehouseId, null) is null or (wh_id IN (:warehouseId)))" +
            "AND (:startCreatedOn IS NULL OR gr_ctd_on >= :startCreatedOn) " +
            "AND (:endCreatedOn IS NULL OR gr_ctd_on <= :endCreatedOn) " +
            "AND partner_code = :partnerCode AND is_deleted=0 ", nativeQuery = true)
    public List<BillingTransactionReportImpl> getGrLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("partnerCode") String partnerCode,
            @Param("startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
            @Param("endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn);


    @Transactional
    @Query(value = "Select c_id as companyCodeId, lang_id as languageId, plant_id as plantId, wh_id as warehouseId ,\n" +
            "partner_code as partnerCode, tpl_uom as threePLUom, total_tpl_cbm as threePLCbm, ITM_CODE as itemCode, REF_DOC_NO as refDocNumber, REPLACE('PUTAWAY,CHARGES', ',', ' ') AS description, \n" +
            "c_text companyDescription, plant_text plantDescription, wh_text warehouseDescription, Rate as rate, currency as currency, ORD_QTY transactionQty \n" +
            "from tblputawayline  \n" +
            "Where (coalesce(:languageId , null) is null or (lang_id IN (:languageId))) and (coalesce(:companyCodeId, null) is null or (c_id IN (:companyCodeId))) and " +
            "(coalesce(:plantId, null) is null or (plant_id IN (:plantId))) and (coalesce(:warehouseId, null) is null or (wh_id IN (:warehouseId))) " +
            "AND (:startCreatedOn IS NULL OR pa_ctd_on >= :startCreatedOn) " +
            "AND (:endCreatedOn IS NULL OR pa_ctd_on <= :endCreatedOn) " +
            "AND partner_code = :partnerCode AND is_deleted=0 ", nativeQuery = true)
    List<BillingTransactionReportImpl> getPutAwayLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("partnerCode") String partnerCode,
            @Param("startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
            @Param("endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn);

    @Transactional
    @Query(value = "Select c_id as companyCodeId, lang_id as languageId, plant_id as plantId, wh_id as warehouseId, partner_code as partnerCode, " +
            " tpl_uom as threePLUom, tpl_cbm as threePLCbm, Rate as rate, currency as currency, ITM_CODE as itemCode, REF_DOC_NO as refDocNumber, REPLACE('PICKING,CHARGES', ',', ' ') AS description, \n " +
            " c_text companyDescription, plant_text plantDescription, wh_text warehouseDescription, PICK_CNF_QTY transactionQty From tblpickupline  \n" +
            "Where (coalesce(:languageId , null) is null or (lang_id IN (:languageId))) and (coalesce(:companyCodeId, null) is null or (c_id IN (:companyCodeId))) and " +
            "(coalesce(:plantId, null) is null or (plant_id IN (:plantId))) and (coalesce(:warehouseId, null) is null or (wh_id IN (:warehouseId)))" +
            "AND (:startCreatedOn IS NULL OR pick_ctd_on >= :startCreatedOn) " +
            "AND (:endCreatedOn IS NULL OR pick_ctd_on <= :endCreatedOn) " +
            "AND partner_code = :partnerCode AND is_deleted=0 ", nativeQuery = true)
    public List<BillingTransactionReportImpl> getPickupLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("partnerCode") String partnerCode,
            @Param("startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
            @Param("endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn);

    @Transactional
    @Query(value = "Select c_id as companyCodeId, lang_id as languageId, plant_id as plantId, wh_id as warehouseId, partner_code as partnerCode, " +
            " tpl_uom as threePLUom, tpl_cbm as threePLCbm, Rate as rate, currency as currency, ITM_CODE as itemCode, REF_DOC_NO as refDocNumber, REPLACE('PACKING,CHARGES', ',', ' ') AS description, \n " +
            "C_DESC companyDescription, PLANT_DESC plantDescription, WAREHOUSE_DESC warehouseDescription, PICK_CNF_QTY transactionQty from tblpackingline  \n" +
            "Where (coalesce(:languageId , null) is null or (lang_id IN (:languageId))) and (coalesce(:companyCodeId, null) is null or (c_id IN (:companyCodeId))) and " +
            "(coalesce(:plantId, null) is null or (plant_id IN (:plantId))) and (coalesce(:warehouseId, null) is null or (wh_id IN (:warehouseId))) " +
            "AND (:startCreatedOn IS NULL OR pack_cnf_on >= :startCreatedOn) " +
            "AND (:endCreatedOn IS NULL OR pack_cnf_on <= :endCreatedOn) " +
            "AND partner_code = :partnerCode AND is_deleted=0 ", nativeQuery = true)
    public List<BillingTransactionReportImpl> getPackingLine(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("partnerCode") String partnerCode,
            @Param("startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
            @Param("endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn);

    @Transactional
    @Query(value = "Select c_id as companyCodeId, lang_id as languageId, plant_id as plantId, wh_id as warehouseId, partner_code as partnerCode, " +
            " tpl_uom as threePLUom, total_tpl_cbm as threePLCbm, Rate as rate, currency as currency, ITM_CODE as itemCode, REF_ORD_NO as refDocNumber,REPLACE('STORAGE,CHARGES', ',', ' ') AS description, \n " +
            " C_DESC companyDescription, PLANT_DESC plantDescription, WAREHOUSE_DESC warehouseDescription, INV_QTY transactionQty from tblstockmovement  \n" +
            "Where (coalesce(:languageId , null) is null or (lang_id IN (:languageId))) and (coalesce(:companyCodeId, null) is null or (c_id IN (:companyCodeId))) and " +
            "(coalesce(:plantId, null) is null or (plant_id IN (:plantId))) and (coalesce(:warehouseId, null) is null or (wh_id IN (:warehouseId))) " +
            "AND (:startCreatedOn IS NULL OR ctd_on >= :startCreatedOn) " +
            "AND (:endCreatedOn IS NULL OR ctd_on <= :endCreatedOn) " +
            "AND partner_code = :partnerCode AND is_deleted=0 and BIN_CL_ID = 1 ", nativeQuery = true)
    public List<BillingTransactionReportImpl> getStockMovement(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("partnerCode") String partnerCode,
            @Param("startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
            @Param("endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn);
}
