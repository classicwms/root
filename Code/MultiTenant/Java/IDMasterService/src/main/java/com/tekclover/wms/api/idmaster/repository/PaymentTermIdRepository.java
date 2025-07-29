package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.threepl.paymenttermid.PaymentTermId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Repository
@Transactional
public interface PaymentTermIdRepository extends JpaRepository<PaymentTermId,Long>, JpaSpecificationExecutor<PaymentTermId> {
    Optional<PaymentTermId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPaymentTermIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long paymentTermId, String languageId, Long deletionIndicator);
}
