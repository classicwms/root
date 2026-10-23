package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderProcessV4;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface InboundOrderProcessRepository extends JpaRepository<InboundOrderProcessV4, Long>, JpaSpecificationExecutor<InboundOrderProcessV4>,
        StreamableJpaSpecificationRepository<InboundOrderProcessV4> {

}