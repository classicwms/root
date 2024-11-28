package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.auditlog.AuditLog;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog>,
        StreamableJpaSpecificationRepository<AuditLog> {

    AuditLog findByAuditFileNumberAndDeletionIndicator(String auditFileNumber, Long deletionIndicator);
}