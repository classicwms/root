package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.notification.notificationuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<notificationuser,Long> {

    notificationuser findByUsername(String username);
}
