package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.threepl.proformainvoiceheader.ProformaInvoiceHeader;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ProformaInvoiceHeaderRepository extends JpaRepository<ProformaInvoiceHeader, String>,
        JpaSpecificationExecutor<ProformaInvoiceHeader>, StreamableJpaSpecificationRepository<ProformaInvoiceHeader> {

    Optional<ProformaInvoiceHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProformaBillNoAndPartnerCodeAndLanguageIdAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId,
            String proformaBillNo, String partnerCode, String languageId, Long deletionIndicator);

}