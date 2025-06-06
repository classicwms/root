package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ProformaInvoiceLineRepository extends JpaRepository<ProformaInvoiceLine,Long>, JpaSpecificationExecutor<ProformaInvoiceLine> {
    Optional<ProformaInvoiceLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(String companyCode, String plantId, String warehouseId, String proformaBillNo, String partnerCode, Long lineNumber, String languageId, Long deletionIndicator);
}
