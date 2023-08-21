package com.tekclover.wms.api.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.InboundLineInterim;

@Repository
@Transactional
public interface InboundLineInterimRepository extends JpaRepository<InboundLineInterim,Long>, JpaSpecificationExecutor<InboundLineInterim> {
	
}

