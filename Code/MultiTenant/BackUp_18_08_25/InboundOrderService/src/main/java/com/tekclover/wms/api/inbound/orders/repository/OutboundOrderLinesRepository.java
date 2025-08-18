package com.tekclover.wms.api.inbound.orders.repository;

import javax.transaction.Transactional;

import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.OutboundOrderLine;

@Repository
@Transactional
public interface OutboundOrderLinesRepository extends JpaRepository<OutboundOrderLine, Long>,
        StreamableJpaSpecificationRepository<OutboundOrderLine> {


}