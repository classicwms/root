package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.warehouse.cyclecount.CycleCountLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CycleCountLineRepository extends JpaRepository<CycleCountLine,String> {

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblcyclecountline set countedQty = :countedQty, isCompleted = :isCompleted  \r\n"
            + " WHERE cycleCountNo = :cycleCountNo and itemCode = :itemCode and manufacturerName = :manufacturerName", nativeQuery = true)
    public void updatePplLine (
            @Param(value = "countedQty") Double countedQty,
            @Param(value = "isCompleted") Long isCompleted,
            @Param(value = "cycleCountNo") String cycleCountNo,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "manufacturerName") String manufacturerName);

    CycleCountLine findByCycleCountNoAndItemCodeAndManufacturerCode(String cycleCountNo, String itemCode, String manufacturerName);
    CycleCountLine findByCycleCountNoAndItemCodeAndManufacturerNameAndLineOfEachItemCode(String cycleCountNo, String itemCode, String manufacturerName, Long lineOfEachItemCode);
}