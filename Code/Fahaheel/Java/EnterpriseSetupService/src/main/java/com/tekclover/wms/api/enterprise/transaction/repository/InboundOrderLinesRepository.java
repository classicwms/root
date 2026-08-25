package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.warehouse.inbound.InboundOrderLines;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface InboundOrderLinesRepository extends JpaRepository<InboundOrderLines,Long>,
        StreamableJpaSpecificationRepository<InboundOrderLines> {
	
	
}