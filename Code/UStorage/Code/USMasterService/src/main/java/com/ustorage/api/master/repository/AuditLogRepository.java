package com.ustorage.api.master.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.auditlog.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{
	public AuditLog findByAuditLogNumber(String auditLogNumber);
}