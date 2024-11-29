package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.OrderManagementLineV2;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface OrderManagementLineV2Repository extends JpaRepository<OrderManagementLineV2, Long>,
        JpaSpecificationExecutor<OrderManagementLineV2>, StreamableJpaSpecificationRepository<OrderManagementLineV2> {

}