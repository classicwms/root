package com.almailem.ams.api.connector.repository;

import com.almailem.ams.api.connector.model.perpetual.PerpetualLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional
public interface PerpetualLineRepository extends JpaRepository<PerpetualLine, String>,
        JpaSpecificationExecutor<PerpetualLine> {


    PerpetualLine findByCycleCountNoAndItemCodeAndManufacturerCode(
            String cycleCountNo, String itemCode, String manufacturerName);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE PERPETUALLINE set CountedQty = :countedQty, IS_COMPLETED = :isCompleted  \r\n"
            + " WHERE CycleCountNo = :cycleCountNo and Itemcode = :itemCode and ManufacturerName = :manufacturerName", nativeQuery = true)
    public void updatePplLine (
            @Param(value = "countedQty") Double countedQty,
            @Param(value = "isCompleted") Long isCompleted,
            @Param(value = "cycleCountNo") String cycleCountNo,
            @Param(value = "itemCode") String itemCode,
            @Param(value = "manufacturerName") String manufacturerName);

    PerpetualLine findByCycleCountNoAndItemCodeAndManufacturerName(String cycleCountNo, String itemCode, String manufacturerName);
}
