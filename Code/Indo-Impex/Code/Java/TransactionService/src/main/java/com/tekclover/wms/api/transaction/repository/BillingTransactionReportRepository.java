package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.report.BillingTransactionReport;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public interface BillingTransactionReportRepository extends JpaRepository<BillingTransactionReport, Long>,
        JpaSpecificationExecutor<BillingTransactionReport>, StreamableJpaSpecificationRepository<BillingTransactionReport> {

    @Transactional
    @Procedure(procedureName = "btr")
    public void billingTransactionReport(
            @Param("companyCodeId") String companyCode,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo
    );
}