package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.orders.model.inbound.InboundLineInterim;

@Repository
@Transactional
public interface InboundLineInterimRepository extends JpaRepository<InboundLineInterim,Long>,
        JpaSpecificationExecutor<InboundLineInterim>, StreamableJpaSpecificationRepository<InboundLineInterim> {
	
}

