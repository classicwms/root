package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.threepl.paymentmodeid.PaymentModeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Repository
@Transactional
public interface PaymentModeIdRepository extends JpaRepository<PaymentModeId,Long>, JpaSpecificationExecutor<PaymentModeId> {
    Optional<PaymentModeId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPaymentModeIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long paymentModeId, String languageId, Long deletionIndicator);
}
