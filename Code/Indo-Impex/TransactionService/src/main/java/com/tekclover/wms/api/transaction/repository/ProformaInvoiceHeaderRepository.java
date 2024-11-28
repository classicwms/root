package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.threepl.proformainvoiceheader.ProformaInvoiceHeader;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ProformaInvoiceHeaderRepository extends JpaRepository<ProformaInvoiceHeader, String>,
        JpaSpecificationExecutor<ProformaInvoiceHeader>, StreamableJpaSpecificationRepository<ProformaInvoiceHeader> {

    Optional<ProformaInvoiceHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLanguageIdAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId,
            Long proformaBillNo, String partnerCode, String languageId, Long deletionIndicator);

    @Query(value = "select MAX(PROFORMA_BILL_NO) as proformaNo from tblproformainvoiceheader where is_deleted = 0", nativeQuery = true)
    public Long getProforma();
}
