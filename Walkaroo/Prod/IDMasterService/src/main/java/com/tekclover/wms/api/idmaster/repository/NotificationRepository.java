package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.websocketnotification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    List<Notification> findByUserIdOrUserIdIsNull(String userId);

    @Modifying
    @Query("update Notification n set n.status = true where n.userId = :userId")
    void updateStatus(@Param("userId") String userId);
    
    @Modifying
    @Query("update Notification n set n.status = true where n.userId = :userId and n.id = :id")
    void updateStatusById(@Param("userId") String userId, @Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tblwebsocketnotification set status = 1 where notification_id = :id", nativeQuery = true)
    void updateStatusByIdV3(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tblwebsocketnotification set status = 1", nativeQuery = true)
    void updateStatusV3();

    @Modifying
    @Query("update Notification n set n.newCreated = false where n.storageBin = :storageBin")
    void updateStorageBin(@Param("storageBin") String storageBin);

    @Modifying
    @Query("update Notification n set n.newCreated = false")
    void updateStorageBin();
}
