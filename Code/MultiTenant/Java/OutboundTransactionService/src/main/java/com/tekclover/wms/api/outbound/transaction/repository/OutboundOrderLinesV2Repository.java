package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.warehouse.outbound.v2.OutboundOrderLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface OutboundOrderLinesV2Repository extends JpaRepository<OutboundOrderLineV2, Long>,
        JpaSpecificationExecutor<OutboundOrderLineV2>,
        StreamableJpaSpecificationRepository<OutboundOrderLineV2> {
    List<OutboundOrderLineV2> findAllByOrderIdAndOutboundOrderTypeID(String orderId, Long outboundOrderTypeID);

    void deleteByOrderId(String orderId);

    @Query(value = "SELECT * FROM tbloborderlines2 "+
            "where item_code = :itemCode and order_id = :refDocNumber and line_reference in (:lineNo) " , nativeQuery = true)
    OutboundOrderLineV2 findByItemCodeAndOrderIdAndLineReference(@Param(value = "itemCode") String itemCode,
                                                                 @Param(value = "refDocNumber") String refDocNumber,
                                                                 @Param(value = "lineNo") List<Long> lineNo);

    @Modifying
    @Transactional
    @Query(value = "delete from tbloborderlines2 "+
            "where item_code = :itemCode and order_id = :refDocNumber " , nativeQuery = true)
    int deleteByItemCodeAndOrderId(@Param(value = "itemCode") String itemCode,
                                   @Param(value = "refDocNumber") String refDocNumber);
}