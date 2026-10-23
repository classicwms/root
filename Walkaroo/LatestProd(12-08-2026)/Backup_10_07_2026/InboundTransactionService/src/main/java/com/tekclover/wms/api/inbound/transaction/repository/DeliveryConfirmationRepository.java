package com.tekclover.wms.api.inbound.transaction.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.inbound.transaction.model.deliveryconfirmation.DeliveryConfirmation;
import com.tekclover.wms.api.inbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface DeliveryConfirmationRepository extends JpaRepository<DeliveryConfirmation, String>,
        JpaSpecificationExecutor<DeliveryConfirmation>,
        StreamableJpaSpecificationRepository<DeliveryConfirmation> {

    @Query(value = "SELECT * FROM tbldeliveryconfirmation WHERE OUTBOUND = :outbound" +
            "AND SKU_CODE = :skuCode AND HU_SERIAL_NUMBER = :huSerialNo ", nativeQuery = true)
    List<DeliveryConfirmation> getDeliveryConfirmation(@Param("outbound") String outbound,
                                         @Param("skuCode") String skuCode,
                                         @Param("huSerialNo") String huSerialNo);


    @Query(value = "SELECT outbound FROM tbldeliveryconfirmation WHERE OUTBOUND in (:outbound) and process_status_id <> 100", nativeQuery = true)
    List<String> validateDeliveryConfirmation(@Param("outbound") List<String> outbound);

    @Query(value = "SELECT ref_doc_no FROM tbloutboundheader WHERE is_deleted = 0 and ref_doc_no in (:outbound)", nativeQuery = true)
    List<String> validateDeliveryOrders(@Param("outbound") List<String> outbound);
    
    @Query(value = "SELECT OUTBOUND FROM tbldeliveryconfirmation WHERE OUTBOUND in (:outbound)", nativeQuery = true)
    List<String> validateDeliveryConfirmationOrders(@Param("outbound") List<String> outbound);

    @Query(value = "SELECT ref_doc_no \n" +
            "FROM tbloutboundheader \n" +
            "WHERE is_deleted = 0 and ref_doc_no IN (:outbound)\n" +
            "  AND NOT EXISTS (\n" +
            "      SELECT 1 \n" +
            "      FROM tbldeliveryconfirmation \n" +
            "      WHERE OUTBOUND IN (:outbound));", nativeQuery = true)
    List<String> validateDeliveryOrderNumber(@Param("outbound") List<String> outbound);

    List<DeliveryConfirmation> findByProcessedStatusIdOrderByOrderReceivedOn(Long processStatusId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbldeliveryconfirmation set process_status_id = :processStatusId where DELIVERY_ID in :deliveryId ", nativeQuery = true)
    void updateBatchExecuted(@Param("deliveryId") List<Long> deliveryId,
                             @Param("processStatusId") Long processStatusId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbldeliveryconfirmation set process_status_id = :processStatusId, order_processed_on = :orderProcessedOn  where DELIVERY_ID in :deliveryId and process_status_id <> 100 ", nativeQuery = true)
    void updateProcessStatusId(@Param("deliveryId") List<Long> deliveryId,
                               @Param("processStatusId") Long processStatusId,
                               @Param("orderProcessedOn") Date orderProcessedOn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbldeliveryconfirmation set process_status_id = :processStatusId, order_processed_on = :orderProcessedOn, remark = :remark where DELIVERY_ID in :deliveryId and process_status_id <> 100 ", nativeQuery = true)
    void updateFailedProcessStatusId(@Param("deliveryId") List<Long> deliveryId,
                                     @Param("processStatusId") Long processStatusId,
                                     @Param("remark") String remark,
                                     @Param("orderProcessedOn") Date orderProcessedOn);

    @Query(value = "SELECT * from tbldeliveryconfirmation " +
            "WHERE HU_SERIAL_NO in (:barcodeId) and " +
            "(COALESCE(:itemCode, null) IS NULL OR (SKU_CODE IN (:itemCode)))", nativeQuery = true)
    List<DeliveryConfirmation> findDLVValues(@Param("itemCode") String itemCode,
                                    @Param("barcodeId") String barcodeId);


    @Query(value = "SELECT * FROM tbldeliveryconfirmation " +
        "WHERE HU_SERIAL_NO IN (:barcodeIds) " +
        "AND (:itemCode IS NULL OR SKU_CODE IN (:itemCode)) ", nativeQuery = true)
    List<DeliveryConfirmation> findDLVBulk(
            @Param("itemCode") List<String> itemCode,
            @Param("barcodeIds") List<String> barcodeIds);
}
