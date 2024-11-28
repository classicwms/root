package com.tekclover.wms.api.idmaster.repository;


import com.tekclover.wms.api.idmaster.model.deliverynotification.DeliveryNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface DeliveryNotificationRepository extends JpaRepository<DeliveryNotification, Long> {


    Optional<DeliveryNotification> findByCompanyIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeviceIdAndUserIdAndTokenIdAndDeletionIndicator(
            String companyId, String plantId, String warehouseId, String languageId, String deviceId, String userId, String tokenId, Long deletionIndicator);
}
