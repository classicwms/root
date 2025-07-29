package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.notification.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long>, JpaSpecificationExecutor<NotificationMessage> {

    List<NotificationMessage> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndDeletionIndicator(
            String languageId, String companyId, String plantId, String warehouseId, String processId, Long deletionIndicator);

    boolean existsByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndDeletionIndicator(
            String languageId, String companyId, String plantId, String warehouseId, String processId, Long deletionIndicator);


}