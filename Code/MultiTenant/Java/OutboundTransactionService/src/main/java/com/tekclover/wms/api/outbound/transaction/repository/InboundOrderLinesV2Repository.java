package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
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

    InboundOrderLinesV2 findTopByOrderIdAndItemCode(String refDocNumber, String itemCode);

//    boolean existsByBarcodeIdAndOrderId(String barcodeId, String refDocNumber);


    @Query(value = "SELECT b.barcode_id FROM tbliborderlines2 b WHERE b.barcode_id IN :barcodeIds", nativeQuery = true)
    List<String> findAllByBarcodeIdIn(@Param("barcodeIds") List<String> barcodeIds);

//    List<InboundOrderLinesV2> findByOrderIdAndInboundOrderHeaderId(String orderId,Long inboundOrderHeaderId);

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

    void deleteByOrderIdAndItemCode(String orderId, String itemCode);

    @Query(value = "SELECT * FROM tbliborderlines "+
            "where item_code = :itemCode and order_id = :refDocNumber and line_reference in (:lineNo) " , nativeQuery = true)
    InboundOrderLinesV2 findByItemCodeAndOrderIdAndLineReference(@Param(value = "itemCode") String itemCode,
                                                                 @Param(value = "refDocNumber") String refDocNumber,
                                                                 @Param(value = "lineNo") List<Long> lineNo);



    @Query(value = "SELECT * FROM tbliborderlines "+
            "WHERE item_code = :itemCode and order_id = :refDocNumber and " +
            "line_reference in (:lineNo) and inbound_order_type_id = :inboundOrderTypeId " , nativeQuery = true)
    InboundOrderLinesV2 getItemCodeAndOrderIdAndLineReferenceAndInboundOrderTypeId( @Param(value = "itemCode") String itemCode,
                                                                 @Param(value = "refDocNumber") String refDocNumber,
                                                                 @Param(value = "lineNo") List<Long> lineNo,
                                                                 @Param(value = "inboundOrderTypeId") Long inboundOrderTypeId);

    @Query(value = "select coalesce(max(line_reference), 0) + 1 from tbliborderlines2 where company_code = :companyCodeId " +
            " and branch_code = :plantId  and order_id = :refDocNumber ",nativeQuery = true)
    public Long getLineNoV10(@Param("companyCodeId") String companyCodeId,
                             @Param("plantId") String plantId,
                             @Param("refDocNumber") String refDocNumber);
}