package com.tekclover.wms.api.transaction.repository;


import com.tekclover.wms.api.transaction.model.report.MetricsSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MetricsSummaryRepository extends JpaRepository<MetricsSummary, Long> {


    @Modifying
    @Transactional
    @Query(value = "delete tblmatricssummary where reference_id = :referenceId", nativeQuery = true)
    int deleteMatricsSummary(@Param("referenceId") Long referenceId);

}
