package com.tekclover.wms.api.outbound.transaction.repository;

import javax.transaction.Transactional;

import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.OutboundOrderLine;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface OutboundOrderLinesRepository extends JpaRepository<OutboundOrderLine, Long>,
        StreamableJpaSpecificationRepository<OutboundOrderLine> {


}