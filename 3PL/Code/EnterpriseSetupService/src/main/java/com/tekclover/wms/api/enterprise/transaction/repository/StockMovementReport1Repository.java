package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.report.StockMovementReport1;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StockMovementReport1Repository extends JpaRepository<StockMovementReport1, Long>,
        JpaSpecificationExecutor<StockMovementReport1>,
        StreamableJpaSpecificationRepository<StockMovementReport1> {

}