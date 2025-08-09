package com.tekclover.wms.api.inbound.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.inbound.orders.model.report.StockMovementReport1;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface StockMovementReport1Repository extends JpaRepository<StockMovementReport1, Long>,
        JpaSpecificationExecutor<StockMovementReport1>,
        StreamableJpaSpecificationRepository<StockMovementReport1> {

}