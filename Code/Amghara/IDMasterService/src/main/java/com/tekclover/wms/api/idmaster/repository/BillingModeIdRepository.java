package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.threepl.billingmodeid.BillingModeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Repository
@Transactional
public interface BillingModeIdRepository extends JpaRepository<BillingModeId,Long>, JpaSpecificationExecutor<BillingModeId> {
    Optional<BillingModeId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillModeIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long billModeId, String languageId, Long deletionIndicator);
}
