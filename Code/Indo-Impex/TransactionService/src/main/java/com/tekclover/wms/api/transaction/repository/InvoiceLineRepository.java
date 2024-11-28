package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.threepl.invoiceline.InvoiceLine;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long>,
        JpaSpecificationExecutor<InvoiceLine>, StreamableJpaSpecificationRepository<InvoiceLine> {
    Optional<InvoiceLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, Long invoiceNumber, String partnerCode, Long lineNumber, String languageId, Long deletionIndicator);

    @Query(value = "select MAX(INVOICE_NO) as invoice from tblinvoiceline where is_deleted = 0", nativeQuery = true)
    public Long getInvoiceNo();

    List<InvoiceLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId,String warehouseId, Long invoiceNumber, String partnerCode, String languageId, Long deletionIndicator);
}
