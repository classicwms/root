package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface InboundOrderLinesV2Repository extends JpaRepository<InboundOrderLinesV2,Long>,
        StreamableJpaSpecificationRepository<InboundOrderLinesV2>, JpaSpecificationExecutor<InboundOrderLinesV2> {


    List<InboundOrderLinesV2> findByOrderId(String orderId);

    @Query(value = "SELECT * FROM tbliborderlines WHERE order_id = :orderId ", nativeQuery = true)
    public List<InboundOrderLinesV2> getOrderLines(@Param("orderId") String orderId);

    @Query(value = "SELECT * FROM tbliborderlines WHERE order_id = :orderId and  inbound_order_type_id = :inboundOrderTypeId ", nativeQuery = true)
    public List<InboundOrderLinesV2> getOrderLinesByOrderTypeId(@Param("orderId") String orderId,
                                                                @Param("inboundOrderTypeId") Long inboundOrderTypeId);
    boolean existsByBarcodeIdAndOrderId(String barcodeId, String refDocNumber);


    @Query(value = "SELECT b.barcode_id FROM tbliborderlines2 b WHERE b.barcode_id IN :barcodeIds", nativeQuery = true)
    List<String> findAllByBarcodeIdIn(@Param("barcodeIds") List<String> barcodeIds);

    InboundOrderLinesV2 findTopByOrderIdAndItemCode(String refDocNumber, String itemCode);

    @Query(value = "select \n"
            + "tc.c_text AS companyDesc,\n"
            + "tp.plant_text AS plantDesc,\n"
            + "tw.wh_text AS warehouseDesc \n"
            + "from tblcompanyid tc \n"
            + "join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id \n"
            + "join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id = tc.lang_id and tw.plant_id = tp.plant_id \n"
            + "where \n"
            + "tc.c_id IN (:companyCodeId) and \n"
            + "tp.plant_id IN (:plantId) and \n"
            + "tw.wh_id IN (:warehouseId) \n", nativeQuery = true)
    IKeyValuePair getV2Description(@Param(value = "companyCodeId") String companyCodeId,
                                   @Param(value = "plantId") String plantId,
                                   @Param(value = "warehouseId") String warehouseId);
}