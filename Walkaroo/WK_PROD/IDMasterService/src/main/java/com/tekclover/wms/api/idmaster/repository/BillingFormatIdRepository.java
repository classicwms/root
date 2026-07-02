package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.threepl.billingformatid.BillingFormatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Repository
@Transactional
public interface BillingFormatIdRepository extends JpaRepository<BillingFormatId,Long>, JpaSpecificationExecutor<BillingFormatId> {
    Optional<BillingFormatId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBillFormatIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long billFormatId, String languageId,Long deletionIndicator);
}
