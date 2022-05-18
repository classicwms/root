package com.tekclover.wms.api.masters.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.auditlog.AuditLog;

@Repository
@Transactional
public interface AuditLogRepository extends JpaRepository<AuditLog,Long>, JpaSpecificationExecutor<AuditLog> {

	AuditLog findByAuditFileNumber(String auditFileNumber);
}