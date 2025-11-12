package com.tekclover.wms.api.transaction.repository;


import com.tekclover.wms.api.transaction.model.report.SummaryMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SummaryMetricsRepository extends JpaRepository<SummaryMetrics, Long> {


    @Query(value = "select * from tblsummarymetrics where reference_id = :referenceId", nativeQuery = true)
    List<SummaryMetrics> findAllSummary(@Param("referenceId") Long referenceId);

    @Modifying
    @Transactional
    @Query(value = "delete tblsummarymetrics where reference_id = :referenceId", nativeQuery = true)
    int deleteSummaryMetrics(@Param("referenceId") Long referenceId);
}
