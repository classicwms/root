package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.OutboundOrderLine;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface OutboundOrderLinesRepository extends JpaRepository<OutboundOrderLine, Long>,
        StreamableJpaSpecificationRepository<OutboundOrderLine> {


}