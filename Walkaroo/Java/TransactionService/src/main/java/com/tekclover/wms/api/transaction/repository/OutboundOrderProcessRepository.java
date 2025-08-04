package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.v3.OutboundOrderProcessV4;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OutboundOrderProcessRepository extends JpaRepository<OutboundOrderProcessV4, Long>, JpaSpecificationExecutor<OutboundOrderProcessV4>,
        StreamableJpaSpecificationRepository<OutboundOrderProcessV4> {

}