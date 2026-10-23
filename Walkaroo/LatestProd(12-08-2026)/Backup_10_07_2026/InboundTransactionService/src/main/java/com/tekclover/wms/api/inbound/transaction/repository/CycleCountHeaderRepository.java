package com.tekclover.wms.api.inbound.transaction.repository;


import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.CycleCountHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CycleCountHeaderRepository extends JpaRepository<CycleCountHeader,String> {
    List<CycleCountHeader> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);
    List<CycleCountHeader> findTopByProcessedStatusIdAndStockCountTypeOrderByOrderReceivedOn(long l, String stockCountType);

    CycleCountHeader findByCycleCountNo(String cycleCountNo);
}
