package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.exceptionlog.ExceptionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ExceptionLogRepository extends JpaRepository<ExceptionLog, String>, JpaSpecificationExecutor<ExceptionLog> {

}
