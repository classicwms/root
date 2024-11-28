package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceline.ProformaInvoiceLine;
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
public interface ProformaInvoiceLineRepository extends JpaRepository<ProformaInvoiceLine, Long>,
        JpaSpecificationExecutor<ProformaInvoiceLine>, StreamableJpaSpecificationRepository<ProformaInvoiceLine> {
    Optional<ProformaInvoiceLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLineNumberAndLanguageIdAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId,
            Long proformaBillNo, String partnerCode, Long lineNumber, String languageId, Long deletionIndicator);

    List<ProformaInvoiceLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long proformaBillNo, String partnerCode, String languageId, Long deletionIndicator);


    @Query(value = "select MAX(PROFORMA_BILL_NO) as proformaNo from tblproformainvoiceline where is_deleted = 0", nativeQuery = true)
    public Long getProformaNo();

}
