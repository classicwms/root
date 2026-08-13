package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.threepl.billingfrequencyid.BillingFrequencyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Repository
@Transactional
public interface BillingFrequencyIdRepository extends JpaRepository<BillingFrequencyId,Long>, JpaSpecificationExecutor<BillingFrequencyId> {
    Optional<BillingFrequencyId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillFrequencyIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long billFrequencyId, String languageId,  Long deletionIndicator);
}
