package com.tekclover.wms.api.inbound.transaction.repository;


import com.tekclover.wms.api.inbound.transaction.model.dashboard.BinOccupancyProjection;
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

//    @Query(value = "with occupaidQty AS(select sum(inv_qty) occupaidQty, plant_id, wh_id, ST_BIN from tblinventory where \n" +
//            "REF_FIELD_4 > 0 and INV_ID in (select max(inv_id) from tblinventory where IS_DELETED = 0 group by ITM_CODE, ST_BIN, BARCODE_ID, WH_ID, PLANT_ID) \n" +
//            "group by ST_BIN, WH_ID, PLANT_ID) \n" +
//            "\n" +
//            "select st.st_bin storageBin, st.CAP_UNIT openingQty, coalesce(occupaidQty.occupaidQty, 0) as occupaidQty, st.WH_ID warehouseId, st.c_id companyCodeId, st.plant_id plantId, \n" +
//            "coalesce((st.CAP_UNIT - occupaidQty.occupaidQty), 0) as balanceQty, coalesce(round((st.CAP_UNIT / occupaidQty.occupaidQty), 2), 0) as occupaidPercentage \n" +
//            "from tblstoragebin st left join occupaidQty on st.ST_BIN = occupaidQty.ST_BIN and st.WH_ID = occupaidQty.WH_ID and st.PLANT_ID = occupaidQty.PLANT_ID", nativeQuery = true)
//    List<BinOccupancyProjection> getBinOccupancyData();

    @Query(value = "\n" +
            "WITH occupaidQty AS (\n" +
            "    SELECT \n" +
            "        plant_id, \n" +
            "        wh_id, \n" +
            "        c_id, \n" +
            "        MAX(plant_text) AS plantText, \n" +
            "        MAX(wh_text) AS warehouseText, \n" +
            "        COUNT(DISTINCT st_bin) AS binCount \n" +
            "    FROM tblinventory \n" +
            "    WHERE ref_field_4 > 0 \n" +
            "    AND inv_id IN (\n" +
            "        SELECT MAX(inv_id) \n" +
            "        FROM tblinventory \n" +
            "        WHERE is_deleted = 0 \n" +
            "        GROUP BY itm_code, st_bin, barcode_id, wh_id, plant_id\n" +
            "    ) and BIN_CL_ID in (1,10) \n" +
            "    GROUP BY wh_id, plant_id, c_id\n" +
            ") \n" +
            "SELECT \n" +
            "    st.plant_id AS plantId,\n" +
            "    st.wh_id AS warehouseId, \n" +
            "    st.c_id AS companyCodeId, \n" +
            "    MAX(occupaidQty.plantText) AS plantText, \n" +
            "    MAX(occupaidQty.warehouseText) AS warehouseText, \n" +
            "    COUNT(st.st_bin) AS openingQty, \n" +
            "    COALESCE(MAX(occupaidQty.binCount), 0) AS occupaidQty, \n" +
            "    COALESCE(COUNT(st.st_bin) - MAX(occupaidQty.binCount), 0) AS balanceQty, \n" +
            "    COALESCE(CAST(ROUND((MAX(occupaidQty.binCount) * 100.0) / COUNT(st.st_bin), 2) AS DECIMAL(10,2)), 0) AS occupaidPercentage \n" +
            "FROM tblstoragebin st \n" +
            "LEFT JOIN occupaidQty \n" +
            "    ON st.wh_id = occupaidQty.wh_id\n" +
            "    AND st.plant_id = occupaidQty.plant_id \n" +
            "    AND st.c_id = occupaidQty.c_id  where st.BIN_CL_ID in (1,10) \n" +
            "GROUP BY st.plant_id, st.wh_id, st.c_id ",nativeQuery = true)
    List<BinOccupancyProjection> getBinOccupancyData();

}
