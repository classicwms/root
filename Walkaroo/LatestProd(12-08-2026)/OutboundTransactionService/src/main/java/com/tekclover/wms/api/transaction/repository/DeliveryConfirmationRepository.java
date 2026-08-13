package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.tekclover.wms.api.transaction.model.dto.DeliveryConfirmationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.deliveryconfirmation.DeliveryConfirmation;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbldeliveryconfirmation set cust_code = :storageBin, process_status_id = :processStatusId, order_processed_on = :orderProcessedOn  where DELIVERY_ID = :deliveryId ", nativeQuery = true)
    void updateProcessStatusId(@Param("deliveryId") Long deliveryId,
                               @Param("processStatusId") Long processStatusId,
                               @Param("orderProcessedOn") Date orderProcessedOn,
                               @Param("storageBin") String storageBin);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbldeliveryconfirmation set process_status_id = :processStatusId, order_processed_on = :orderProcessedOn  where DELIVERY_ID = :deliveryId and process_status_id <> 100 ", nativeQuery = true)
    void updateProcessStatusIdDLV(@Param("deliveryId") Long deliveryId,
                               @Param("processStatusId") Long processStatusId,
                               @Param("orderProcessedOn") Date orderProcessedOn);
    DeliveryConfirmation findTopByProcessedStatusIdOrderByOrderReceivedOn(Long processStatusId);

    @Query(value = "select * from tbldeliveryconfirmation where outbound = (select top 1 outbound from tbldeliveryconfirmation \n" +
            "where process_status_id = :statusId group by outbound, order_received_on order by order_received_on) ", nativeQuery = true)
    List<DeliveryConfirmation> findTop1DeliveryConfirmationList(@Param("statusId") Long statusId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tbldeliveryconfirmation set process_status_id = :processStatusId, order_processed_on = :orderProcessedOn  " +
            "where outbound = :outbound and process_status_id <> 100 ", nativeQuery = true)
    void updateProcessStatusIdForDLC(@Param("outbound") String outbound,
                                     @Param("processStatusId") Long processStatusId,
                                     @Param("orderProcessedOn") Date orderProcessedOn);

    @Query(value =
            "SELECT " +
                    "    ol.C_TEXT              AS companyDesc, " +
                    "    ol.PLANT_TEXT          AS plantDesc, " +
                    "    ol.WH_TEXT             AS warehouseDesc, " +
                    "    ol.CUSTOMER_ID         AS customerCode, " +
                    "    ol.CUSTOMER_NAME       AS customerName, " +
                    "    dc.HU_SERIAL_NO        AS huSerialNo, " +
                    "    dc.MATERIAL            AS material, " +
                    "    dc.OUTBOUND            AS outboundNo, " +
                    "    dc.SKU_CODE            AS skuCode, " +
                    "    dc.PIK_QTY             AS pickQty, " +
                    "    ol.SHIP_TO_PARTY       AS shipToParty, " +
                    "    ol.SHIP_TO_CODE        AS shipToCode, " +
                    "    dc.ORDER_PROCESSED_ON  AS orderProcessedOn " +
                    "FROM tbldeliveryconfirmation dc " +
                    "INNER JOIN tbloutboundline ol " +
                    "    ON dc.OUTBOUND = ol.REF_DOC_NO " +
                    "   AND dc.SKU_CODE = ol.ITM_CODE " +
                    "   AND dc.MATERIAL = ol.MATERIAL_NO " +
                    "WHERE " +
                    "(COALESCE(:companyCodeId, null) IS NULL OR (dc.C_ID IN (:companyCodeId))) \n" +
                    "AND (COALESCE(:plantId, null) IS NULL OR (dc.PLANT_ID IN (:plantId))) \n" +
                    "AND (COALESCE(:warehouseId, null) IS NULL OR (dc.WH_ID IN (:warehouseId))) \n" +
                    "AND (COALESCE(:refDocNo, NULL) IS NULL OR (dc.OUTBOUND IN (:refDocNo))) \n " +
                    "AND (COALESCE(:customerCode, NULL) IS NULL OR (ol.CUSTOMER_ID IN (:customerCode))) \n" +
                    "AND (COALESCE(:itemCode, NULL) IS NULL OR (dc.SKU_CODE IN (:itemCode))) \n" +
                    "AND (COALESCE(CONVERT(VARCHAR(255), :fromDate), null) IS NULL OR " +
                    "(dc.ORDER_PROCESSED_ON between COALESCE(CONVERT(VARCHAR(255), :fromDate), null) and " +
                    "COALESCE(CONVERT(VARCHAR(255), :toDate), null)))",
            nativeQuery = true)
    List<DeliveryConfirmationDto> getDeliveryConfirmationReport(
            @Param("companyCodeId") List<String> companyCodeId,
            @Param("plantId") List<String> plantId,
            @Param("warehouseId") List<String> warehouseId,
            @Param("refDocNo") List<String> refDocNo,
            @Param("customerCode") List<String> customerCode,
            @Param("itemCode") List<String> itemCode,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

}
