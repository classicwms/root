package com.tekclover.wms.api.transaction.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrderLine;

@Repository
@Transactional
public interface OutboundOrderLinesRepository extends JpaRepository<OutboundOrderLine,Long> {
	
	
}