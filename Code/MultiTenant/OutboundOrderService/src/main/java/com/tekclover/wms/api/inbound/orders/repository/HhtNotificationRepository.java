package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.hhtnotification.HhtNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface HhtNotificationRepository extends JpaRepository<HhtNotification, Long> {

    @Modifying
    @Query(value = "TRUNCATE TABLE tblhhtnotification", nativeQuery = true)
    void truncateTable();
}
