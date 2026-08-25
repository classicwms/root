package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.outbound.v2.PickListHeader;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public interface PickListHeaderRepository extends JpaRepository<PickListHeader, Long>,
        JpaSpecificationExecutor<PickListHeader>,
        StreamableJpaSpecificationRepository<PickListHeader> {




}