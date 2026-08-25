package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.hhtnotification.HhtNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface HhtNotificationRepository extends JpaRepository<HhtNotification,Long>, JpaSpecificationExecutor<HhtNotification> {


    Optional<HhtNotification> findByCompanyIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeviceIdAndUserIdAndTokenIdAndDeletionIndicator(
             String companyId, String plantId, String warehouseId, String languageId, String deviceId, String userId, String tokenId, Long deletionIndicator);
}


