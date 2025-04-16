package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.threepl.invoiceline.InvoiceLine;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long>,
        JpaSpecificationExecutor<InvoiceLine>, StreamableJpaSpecificationRepository<InvoiceLine> {
    Optional<InvoiceLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(String companyCode, String plantId, String warehouseId, String invoiceNumber, String partnerCode, Long lineNumber, String languageId, Long deletionIndicator);

    @Modifying
    @Query(value = "Update tblinvoiceline set IS_DELETED = 1 where c_id IN (:companyCodeId) AND lang_id IN (:languageId) AND plant_id IN (:plantId) and " +
            "INVOICE_NO IN (:invoiceNumber) And wh_id IN (:warehouseId) and PARTNER_CODE IN (:partnerCode) AND IS_DELETED = 0", nativeQuery = true)
    void deleteInvoiceLine(@Param(value = "invoiceNumber") String invoiceNumber,
                           @Param(value = "languageId") String languageId,
                           @Param(value = "companyCodeId") String companyCodeId,
                           @Param(value = "plantId") String plantId,
                           @Param(value = "warehouseId") String warehouseId,
                           @Param(value = "partnerCode") String partnerCode);

}
