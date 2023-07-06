package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.auditlog.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{
	public AuditLog findByAuditLogNumber(String auditLogNumber);

	@Query(value ="select max(AUD_LOG_NO)+1 \n"+
			" from tbltransactionauditlog ",nativeQuery = true)
	public String getAuditLogNumber();
}