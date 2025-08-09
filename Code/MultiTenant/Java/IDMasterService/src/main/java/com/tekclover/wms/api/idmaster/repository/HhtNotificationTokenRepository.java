package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.hhtnotification.HhtNotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface HhtNotificationTokenRepository extends JpaRepository<HhtNotificationToken,Long>, JpaSpecificationExecutor<HhtNotificationToken> {


}
