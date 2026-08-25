package com.tekclover.wms.api.inbound.orders.repository;

import javax.transaction.Transactional;

import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.InboundOrderLines;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface InboundOrderLinesRepository extends JpaRepository<InboundOrderLines,Long>,
        StreamableJpaSpecificationRepository<InboundOrderLines> {
	
	
}