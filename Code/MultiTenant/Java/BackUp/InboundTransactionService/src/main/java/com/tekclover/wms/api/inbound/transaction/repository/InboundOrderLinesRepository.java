package com.tekclover.wms.api.inbound.transaction.repository;

import javax.transaction.Transactional;

import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.InboundOrderLines;

@Repository
@Transactional
public interface InboundOrderLinesRepository extends JpaRepository<InboundOrderLines,Long>,
        StreamableJpaSpecificationRepository<InboundOrderLines> {
	
	
}