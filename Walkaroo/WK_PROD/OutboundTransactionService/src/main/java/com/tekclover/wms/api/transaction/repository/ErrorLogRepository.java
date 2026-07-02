package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.errorlog.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ErrorLogRepository extends JpaRepository<ErrorLog, String>, JpaSpecificationExecutor<ErrorLog> {

}
