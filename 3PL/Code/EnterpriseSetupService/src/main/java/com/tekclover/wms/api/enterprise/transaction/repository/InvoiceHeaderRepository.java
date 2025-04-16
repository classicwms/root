package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.threepl.invoiceheader.InvoiceHeader;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeader, String>,
        JpaSpecificationExecutor<InvoiceHeader>, StreamableJpaSpecificationRepository<InvoiceHeader> {
    Optional<InvoiceHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInvoiceNumberAndPartnerCodeAndLanguageIdAndDeletionIndicator(String companyCode, String plantId, String warehouseId, String invoiceNumber, String partnerCode, String languageId, Long deletionIndicator);
}