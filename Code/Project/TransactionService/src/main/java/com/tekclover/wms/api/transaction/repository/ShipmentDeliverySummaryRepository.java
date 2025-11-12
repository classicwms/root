package com.tekclover.wms.api.transaction.repository;


import com.tekclover.wms.api.transaction.model.report.ShipmentDeliverySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ShipmentDeliverySummaryRepository extends JpaRepository<ShipmentDeliverySummary, Long> {


    @Query(value = "select * from tblshipmentdeliverysummary where reference_id = :referenceId ", nativeQuery = true)
    List<ShipmentDeliverySummary> findAllShipment(@Param("referenceId") Long referenceId);

    @Modifying
    @Transactional
    @Query(value = "delete tblshipmentdeliverysummary where reference_id = :referenceId", nativeQuery = true)
    int deleteShipmentDeliveryReport(@Param("referenceId") Long referenceId);
}
