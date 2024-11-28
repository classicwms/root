package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.threepl.invoiceheader.InvoiceHeader;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeader, String>,
        JpaSpecificationExecutor<InvoiceHeader>, StreamableJpaSpecificationRepository<InvoiceHeader> {
    Optional<InvoiceHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLanguageIdAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, Long invoiceNumber, String partnerCode, String languageId, Long deletionIndicator);

    @Query(value = "select MAX(INVOICE_NO) as invoiceId from tblinvoiceheader WHERE IS_DELETED = 0", nativeQuery = true)
    public Long getInvoiceId();
}
