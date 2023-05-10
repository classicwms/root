package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
//public interface OutboundRepository extends PagingAndSortingRepository<OutboundHeader, Long>,
public interface OutboundRepository extends JpaRepository<OutboundHeader, Long>,
        JpaSpecificationExecutor<OutboundHeader>, StreamableJpaSpecificationRepository<OutboundHeader> {

}
