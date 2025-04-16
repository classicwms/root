package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.threepl.invoiceline.InvoiceLine;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long>,
        JpaSpecificationExecutor<InvoiceLine>, StreamableJpaSpecificationRepository<InvoiceLine> {
    Optional<InvoiceLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(String companyCode, String plantId, String warehouseId, String invoiceNumber, String partnerCode, Long lineNumber, String languageId, Long deletionIndicator);
}