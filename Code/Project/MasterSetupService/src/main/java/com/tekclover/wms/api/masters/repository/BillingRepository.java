package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.threepl.billing.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface BillingRepository extends JpaRepository<Billing,String>, JpaSpecificationExecutor<Billing> {
    Optional<Billing> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndModuleAndLanguageIdAndDeletionIndicator(
            String companyCode, String plantId, String warehouseId, String partnerCode, Long module, String languageId, Long deletionIndicator);

}
