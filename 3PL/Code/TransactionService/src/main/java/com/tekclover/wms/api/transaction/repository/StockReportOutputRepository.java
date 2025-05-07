package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.report.StockReportOutput;
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
public interface StockReportOutputRepository extends JpaRepository<StockReportOutput, Long>,
        JpaSpecificationExecutor<StockReportOutput>,
        StreamableJpaSpecificationRepository<StockReportOutput> {

    @Transactional
    @Procedure(procedureName = "sp_stock_report")
    void updateSpStockReport(
            @Param("companyCodeId") String companyCodeId,
            @Param("plantId") String plantId,
            @Param("languageId") String languageId,
            @Param("warehouseId") String warehouseId,
            @Param("itemCode") String itemCode,
            @Param("manufacturerName") String manufacturerName,
            @Param("itemText") String itemText,
            @Param("stockTypeText") String stockTypeText,
            @Param("partnerCode") String partnerCode
    );
}