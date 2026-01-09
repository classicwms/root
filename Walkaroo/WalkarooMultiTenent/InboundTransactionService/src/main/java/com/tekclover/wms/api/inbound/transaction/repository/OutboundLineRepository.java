package com.tekclover.wms.api.inbound.transaction.repository;


import com.tekclover.wms.api.inbound.transaction.model.dto.v2.OutboundLine;
import com.tekclover.wms.api.inbound.transaction.model.report.FastSlowMovingDashboard;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface OutboundLineRepository extends JpaRepository<OutboundLine, Long>,
        JpaSpecificationExecutor<OutboundLine>, StreamableJpaSpecificationRepository<OutboundLine> {

    @Query(value = "select itm_code as itemCode,item_text as itemText, COALESCE(sum(dlv_qty),0) as deliveryQuantity \n" +
            "from tbloutboundline \n" +
            "where dlv_cnf_on between :fromDate and :toDate and wh_id = :warehouseId and dlv_qty is not null and dlv_qty > 0  \n" +
            "group by itm_code,item_text order by sum(dlv_qty) desc ", nativeQuery = true)
    List<FastSlowMovingDashboard.FastSlowMovingDashboardImpl> getFastSlowMovingDashboardData(@Param("warehouseId") String warehouseId,
                                                                                             @Param("fromDate") Date fromDate,
                                                                                             @Param("toDate") Date toDate);

    long countByWarehouseIdAndDeliveryConfirmedOnBetweenAndStatusIdAndDeletionIndicatorAndReferenceField1AndReferenceField2IsNullAndDeliveryQtyIsNotNullAndDeliveryQtyGreaterThan(
            String warehouseId, Date fromDate, Date toDate, Long statusId, Long deletionIndicator, String referenceField1, Double deliveryQty);

    long countByWarehouseIdAndDeliveryConfirmedOnBetweenAndStatusIdAndDeletionIndicatorAndReferenceField2IsNullAndDeliveryQtyIsNotNullAndDeliveryQtyGreaterThan(
            String warehouseId, Date fromDate, Date toDate, Long statusId, Long deletionIndicator, Double deliveryQty);

}
