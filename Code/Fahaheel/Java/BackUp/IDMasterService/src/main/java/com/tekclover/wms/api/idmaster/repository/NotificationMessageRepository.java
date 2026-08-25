package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.notificationmessage.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long>, JpaSpecificationExecutor<NotificationMessage> {

    List<NotificationMessage> findByNotificationIdAndDeletionIndicator(Long notificationId, Long deletionIndicator);

    NotificationMessage findByNotificationId(Long notificationId);

    List<NotificationMessage> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
            String languageId, String companyId, String plantId, String warehouseId, Long deletionIndicator);


    // Get All Notification Messages
    @Query(value = "SELECT * FROM tblnotificationmessage\n" +
            "WHERE is_deleted = 0", nativeQuery = true)
    List<NotificationMessage> getAllNonDeletedNotificationMessages();


    @Modifying
    @Query("update NotificationMessage n set n.newCreated = false")
    void updateStorageBin();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tblnotificationmessage set status = 1", nativeQuery = true)
    void updateStatusV3();
}