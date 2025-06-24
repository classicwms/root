package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.deliveryline.DeliveryLine;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DeliveryLineRepository extends JpaRepository<DeliveryLine, String>,
        JpaSpecificationExecutor<DeliveryLine>, StreamableJpaSpecificationRepository<DeliveryLine> {

    Optional<DeliveryLine> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeliveryNoAndItemCodeAndLineNumberAndInvoiceNumberAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId, Long deliveryNo, String itemCode,
            Long lineNumber, String invoiceNumber, String refDocNumber, Long deletionIndicator);

    List<DeliveryLine> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeliveryNoAndItemCodeAndLineNumberAndInvoiceNumberAndRefDocNumberAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, Long deliveryNo,
            String itemCode, Long lineNumber, String invoiceNumber, String refDocNumber, Long deletionIndicator);




    @Query(value = "select count(*) as count from tbldeliveryline where c_id = :companyCodeId and " +
            "plant_id = :plantId and wh_id = :warehouseId and lang_id = :languageId and driver_nm = :driverId and" +
            " status_id = :statusId and is_deleted = 0 group by ref_doc_no ", nativeQuery = true )
    public List<Long> getNewRecordCount(@Param("companyCodeId") String companyCodeId,
                                        @Param("plantId") String plantId,
                                        @Param("warehouseId") String warehouseId,
                                        @Param("languageId") String languageId,
                                        @Param("driverId") String driverId,
                                        @Param("statusId")Long statusId);


    @Query(value = "select count(*) as count from tbldeliveryline where c_id = :companyCodeId and " +
            "plant_id = :plantId and wh_id = :warehouseId and lang_id = :languageId and driver_nm = :driverId and " +
            " status_id = :statusId and re_delivery = :reDelivery and is_deleted = 0 group by ref_doc_no", nativeQuery = true)
    public List<Long> getReDeliveryLineCount(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("languageId") String languageId,
                                             @Param("driverId") String driverId,
                                             @Param("statusId") Long statusId,
                                             @Param("reDelivery") Boolean reDelivery);


    @Query(value = "select count(*) as count from tbldeliveryline where " +
            "(:companyCodeId is null or c_id = :companyCodeId) and " +
            "(:plantId is null or plant_id = :plantId) and " +
            "(:warehouseId is null or wh_id = :warehouseId) and " +
            "(:languageId is null or lang_id = :languageId) and " +
            "(:driverId is null or driver_nm = :driverId) and " +
            "(status_id = :statusId) and " +
            "is_deleted = 0 group by ref_doc_no", nativeQuery = true)
    public List<Long> getCountOfDeliveryLine(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("languageId") String languageId,
                                       @Param("driverId") String driverId,
                                       @Param("statusId") Long statusId);

    @Query(value = "select count(*) as count from tbldeliveryline where " +
            "(:companyCodeId is null or c_id = :companyCodeId) and " +
            "(:plantId is null or plant_id = :plantId) and " +
            "(:warehouseId is null or wh_id = :warehouseId) and " +
            "(:languageId is null or lang_id = :languageId) and " +
            "(:driverId is null or driver_nm = :driverId) and " +
            "(status_id = :statusId) and " +
            "is_deleted = 0 group by vehicle_id", nativeQuery = true)
    public List<Long> getNewDeliveryLineCount(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("languageId") String languageId,
                                             @Param("driverId") String driverId,
                                             @Param("statusId") Long statusId);


    @Query(value = "select count(*) as count from tbldeliveryline where " +
            "(:companyCodeId is null or c_id = :companyCodeId) and " +
            "(:plantId is null or plant_id = :plantId) and " +
            "(:warehouseId is null or wh_id = :warehouseId) and " +
            "(:languageId is null or lang_id = :languageId) and " +
            "(:driverId is null or driver_nm = :driverId) and " +
            "(status_id = :statusId) and " +
            "(re_delivery = :reDelivery) and " +
            "is_deleted = 0 group by ref_doc_no", nativeQuery = true)
    public List<Long> getCountOfDeliveryLine(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("languageId") String languageId,
                                       @Param("driverId") String driverId,
                                       @Param("statusId") Long statusId,
                                       @Param("reDelivery") Boolean reDelivery);


    List<DeliveryLine> findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);
}
