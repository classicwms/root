package com.tekclover.wms.api.inbound.transaction.repository;


import com.tekclover.wms.api.inbound.transaction.model.preoutbound.v2.OutboundHeader;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

@Repository
@Transactional
public interface OutboundHeaderRepository extends JpaRepository<OutboundHeader, Long>,
        JpaSpecificationExecutor<OutboundHeader>, StreamableJpaSpecificationRepository<OutboundHeader> {
    String UPGRADE_SKIPLOCKED = "-2";

    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // adds 'FOR UPDATE' statement
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = UPGRADE_SKIPLOCKED)})
    public OutboundHeader findByRefDocNumberAndWarehouseIdAndDeletionIndicator(String refDocNumber, String warehouseId, long l);

}
