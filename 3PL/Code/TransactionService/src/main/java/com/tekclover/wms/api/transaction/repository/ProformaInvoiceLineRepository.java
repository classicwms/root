package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ProformaInvoiceLineRepository extends JpaRepository<ProformaInvoiceLine, Long>,
        JpaSpecificationExecutor<ProformaInvoiceLine>, StreamableJpaSpecificationRepository<ProformaInvoiceLine> {
    Optional<ProformaInvoiceLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId,
            String proformaBillNo, String partnerCode, Long lineNumber, String languageId, Long deletionIndicator);

    @Modifying
    @Query(value = "UPDATE tblproformainvoiceline SET IS_DELETED = 1 WHERE c_id IN (:companyCodeId) AND lang_id IN (:languageId) AND plant_id IN (:plantId) and " +
            "PROFORMA_BILL_NO IN (:proformaBillNo) And wh_id IN (:warehouseId) and PARTNER_CODE IN (:partnerCode) and LINE_NO IN (:lineNumber) AND IS_DELETED = 0", nativeQuery = true)
    void deleteProformaInvoice(@Param(value = "proformaBillNo") String proformaBillNo,
                               @Param(value = "languageId") String languageId,
                               @Param(value = "companyCodeId") String companyCodeId,
                               @Param(value = "plantId") String plantId,
                               @Param(value = "warehouseId") String warehouseId,
                               @Param(value = "partnerCode") String partnerCode,
                               @Param(value = "lineNumber") Long lineNumber);

    @Modifying
    @Query(value = "Update tblgrline set PROFORMA_INVOICE_NO = :invoiceNo where c_id IN (:companyCodeId) And lang_id IN (:languageId) And plant_id IN (:plantId) and " +
            " wh_id IN (:warehouseId) AND partner_code = :partnerCode and is_deleted = 0 " +
            "AND (:startCreatedOn IS NULL OR gr_ctd_on >= :startCreatedOn)" +
            "And (:endCreatedOn IS NULL OR gr_ctd_on <= :endCreatedOn) ", nativeQuery = true)
    void updateGr(@Param(value = "companyCodeId") String companyCodeId,
                                       @Param(value = "languageId") String languageId,
                                       @Param(value = "plantId") String plantId,
                                       @Param(value = "warehouseId") String warehouseId,
                                       @Param(value = "partnerCode") String partnerCode,
                                       @Param(value = "startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
                                       @Param(value = "endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn,
                                       @Param(value = "invoiceNo") String invoiceNo);



    @Modifying
    @Query(value = "Update tblpackingline set PROFORMA_INVOICE_NO = :invoiceNo where c_id IN (:companyCodeId) And lang_id IN (:languageId) And plant_id IN (:plantId) and " +
            " wh_id IN (:warehouseId) AND partner_code = :partnerCode and is_deleted = 0 " +
            "AND (:startCreatedOn IS NULL OR PACK_CNF_ON >= :startCreatedOn)" +
            "And (:endCreatedOn IS NULL OR PACK_CNF_ON <= :endCreatedOn) ", nativeQuery = true)
    void updatePackingLine(@Param(value = "companyCodeId") String companyCodeId,
                                       @Param(value = "languageId") String languageId,
                                       @Param(value = "plantId") String plantId,
                                       @Param(value = "warehouseId") String warehouseId,
                                       @Param(value = "partnerCode") String partnerCode,
                                       @Param(value = "startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
                                       @Param(value = "endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn,
                                       @Param(value = "invoiceNo") String invoiceNo);
    @Modifying
    @Query(value = "Update tblpickupline set PROFORMA_INVOICE_NO = :invoiceNo where c_id IN (:companyCodeId) And lang_id IN (:languageId) And plant_id IN (:plantId) and " +
            " wh_id IN (:warehouseId) AND partner_code = :partnerCode and is_deleted = 0 " +
            "AND (:startCreatedOn IS NULL OR pick_ctd_on >= :startCreatedOn)" +
            "And (:endCreatedOn IS NULL OR pick_ctd_on <= :endCreatedOn) ", nativeQuery = true)
    void updatePickingLine(@Param(value = "companyCodeId") String companyCodeId,
                                            @Param(value = "languageId") String languageId,
                                            @Param(value = "plantId") String plantId,
                                            @Param(value = "warehouseId") String warehouseId,
                                            @Param(value = "partnerCode") String partnerCode,
                                            @Param(value = "startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
                                            @Param(value = "endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn,
                                            @Param(value = "invoiceNo") String invoiceNo);

    @Modifying
    @Query(value = "Update tblstockmovement set PROFORMA_INVOICE_NO = :invoiceNo where c_id IN (:companyCodeId) And lang_id IN (:languageId) And plant_id IN (:plantId) and " +
            " wh_id IN (:warehouseId) AND partner_code = :partnerCode and is_deleted = 0 " +
            "AND (:startCreatedOn IS NULL OR CTD_ON >= :startCreatedOn)" +
            "And (:endCreatedOn IS NULL OR CTD_ON <= :endCreatedOn) ", nativeQuery = true)
    void updateStockmovement(@Param(value = "companyCodeId") String companyCodeId,
                                            @Param(value = "languageId") String languageId,
                                            @Param(value = "plantId") String plantId,
                                            @Param(value = "warehouseId") String warehouseId,
                                            @Param(value = "partnerCode") String partnerCode,
                                            @Param(value = "startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
                                            @Param(value = "endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn,
                                            @Param(value = "invoiceNo") String invoiceNo);

    @Modifying
    @Query(value = "Update tblputawayline set PROFORMA_INVOICE_NO = :invoiceNo where c_id IN (:companyCodeId) And lang_id IN (:languageId) And plant_id IN (:plantId) and " +
            " wh_id IN (:warehouseId) AND partner_code = :partnerCode and is_deleted = 0 " +
            "AND (:startCreatedOn IS NULL OR PA_CTD_ON >= :startCreatedOn)" +
            "And (:endCreatedOn IS NULL OR PA_CTD_ON <= :endCreatedOn) ", nativeQuery = true)
    void updatePutAway(@Param(value = "companyCodeId") String companyCodeId,
                                            @Param(value = "languageId") String languageId,
                                            @Param(value = "plantId") String plantId,
                                            @Param(value = "warehouseId") String warehouseId,
                                            @Param(value = "partnerCode") String partnerCode,
                                            @Param(value = "startCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date startCreatedOn,
                                            @Param(value = "endCreatedOn") @Temporal(TemporalType.TIMESTAMP) Date endCreatedOn,
                                            @Param(value = "invoiceNo") String invoiceNo);
}
