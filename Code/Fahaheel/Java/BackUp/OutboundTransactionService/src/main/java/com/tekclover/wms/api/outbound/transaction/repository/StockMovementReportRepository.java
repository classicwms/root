package com.tekclover.wms.api.outbound.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.outbound.transaction.model.report.StockMovementReport;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface StockMovementReportRepository extends JpaRepository<StockMovementReport, Long>,
        JpaSpecificationExecutor<StockMovementReport>,
        StreamableJpaSpecificationRepository<StockMovementReport> {

}