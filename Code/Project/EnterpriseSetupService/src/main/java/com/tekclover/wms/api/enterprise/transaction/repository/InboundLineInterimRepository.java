package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.InboundLineInterim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface InboundLineInterimRepository extends JpaRepository<InboundLineInterim,Long>, JpaSpecificationExecutor<InboundLineInterim> {
	
}