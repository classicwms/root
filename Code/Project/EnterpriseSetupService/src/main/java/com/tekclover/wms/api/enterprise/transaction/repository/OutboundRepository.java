package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
//public interface OutboundRepository extends PagingAndSortingRepository<OutboundHeader, Long>,
public interface OutboundRepository extends JpaRepository<OutboundHeader, Long>,
        JpaSpecificationExecutor<OutboundHeader>, StreamableJpaSpecificationRepository<OutboundHeader> {

}