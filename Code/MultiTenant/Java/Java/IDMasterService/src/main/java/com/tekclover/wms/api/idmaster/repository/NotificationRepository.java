package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface NotificationRepository extends JpaRepository<Notification, String>, JpaSpecificationExecutor<Notification> {

    Optional<Notification> findByLanguageIdAndCompanyIdAndNotificationIdAndDeletionIndicator(
            String languageId, String company, String notificationId, Long deletionIndicator);

    boolean existsByLanguageIdAndCompanyIdAndNotificationIdAndDeletionIndicator(
            String languageId, String company, String notificationId, Long deletionIndicator);


    // Get Description
    @Query(value = "Select \n" +
            "CONCAT (tl.LANG_ID, ' - ', tl.LANG_TEXT) As langDesc, \n" +
            "CONCAT (tc.C_ID, ' - ', tc.C_TEXT) As companyDesc \n" +
            "From tbllanguageid tl \n" +
            "Join tblcompanyid tc on tl.LANG_ID = tc.LANG_ID \n" +
            "Where \n" +
            "tl.LANG_ID IN (:languageId) and \n" +
            "tc.C_ID IN (:companyId) and \n" +
            "tl.IS_DELETED = 0 and \n" +
            "tc.IS_DELETED = 0", nativeQuery = true)
    IKeyValuePair getDescription(@Param(value = "languageId") String languageId,
                                 @Param(value = "companyId") String companyId);


    // Get All Non-Deleted Notifications
    @Query(value = "SELECT * FROM tblnotification\n" +
            "WHERE is_deleted=0", nativeQuery = true)
    List<Notification> getAllNonDeletedNotifications();


}
