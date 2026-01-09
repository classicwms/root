package com.tekclover.wms.api.transaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;

@Repository
@Transactional
public interface OrderManagementHeaderV2Repository extends JpaRepository<OrderManagementHeaderV2, Long>,
        JpaSpecificationExecutor<OrderManagementHeaderV2>,
        StreamableJpaSpecificationRepository<OrderManagementHeaderV2> {


    OrderManagementHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId,
            String preOutboundNo, String refDocNumber, String partnerCode, Long deletionIndicator);

    OrderManagementHeaderV2 findByPreOutboundNo(String preOutboundNo);

    Optional<OrderManagementHeaderV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId,
            String preOutboundNo, String refDocNumber, String partnerCode, Long deletionIndicator);

    OrderManagementHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber, Long deletionIndicator);

    OrderManagementHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, Long deletionIndicator);

    OrderManagementHeaderV2 findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OrderManagementHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo")
    public void updateOrderManagementHeaderStatusV2(@Param("companyCodeId") String companyCodeId,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("warehouseId") String warehouseId,
                                                    @Param("refDocNumber") String refDocNumber,
                                                    @Param("preOutboundNo") String preOutboundNo,
                                                    @Param("statusId") Long statusId,
                                                    @Param("statusDescription") String statusDescription);

    //======================================================Walkaroo-V3===================================================================

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OrderManagementHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.preOutboundNo = :preOutboundNo")
    void updateOrderManagementHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                             @Param("plantId") String plantId,
                                             @Param("languageId") String languageId,
                                             @Param("warehouseId") String warehouseId,
                                             @Param("preOutboundNo") String preOutboundNo,
                                             @Param("statusId") Long statusId,
                                             @Param("statusDescription") String statusDescription);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update OrderManagementHeaderV2 ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.refDocNumber = :refDocNumber AND ob.preOutboundNo = :preOutboundNo")
    public void updateOrderManagementHeaderStatusV3(@Param("companyCodeId") String companyCodeId,
                                                    @Param("plantId") String plantId,
                                                    @Param("languageId") String languageId,
                                                    @Param("warehouseId") String warehouseId,
                                                    @Param("refDocNumber") String refDocNumber,
                                                    @Param("preOutboundNo") String preOutboundNo,
                                                    @Param("statusId") Long statusId,
                                                    @Param("statusDescription") String statusDescription);
    
    //============================PGIReversal================================================================
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE tblordermangementheader SET STATUS_ID = :statusId, REF_FIELD_10 = :statusDescription, STATUS_TEXT = :statusDescription \n" +
            "WHERE LANG_ID = :languageId AND C_ID = :companyCodeId AND \n" +
            "PLANT_ID = :plantId AND WH_ID = :warehouseId AND REF_DOC_NO = :refDocNumber", nativeQuery = true)
    void updateOrderManagementHeaderStatusForReversalV3(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("refDocNumber") String refDocNumber,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);
    
    @Modifying
    @Query(value = "UPDATE tblordermangementheader SET is_deleted = 1 where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber AND pre_ob_no = :preOutboundNo " +
            "AND is_deleted = 0", nativeQuery = true)
    void deleteOrderManagementHeader (@Param("companyCodeId") String companyCodeId,
                                   @Param("plantId") String plantId,
                                   @Param("warehouseId") String warehouseId,
                                   @Param("refDocNumber") String refDocNumber,
                                   @Param("preOutboundNo") String preOutboundNo);

    @Modifying
    @Query(value = "DELETE tblordermangementheader where c_id = :companyCodeId " +
            "AND plant_id = :plantId AND wh_id = :warehouseId AND ref_doc_no = :refDocNumber " +
            "AND is_deleted = 0", nativeQuery = true)
    void deleteOrderManagementHeaderV2(@Param("companyCodeId") String companyCodeId,
                                       @Param("plantId") String plantId,
                                       @Param("warehouseId") String warehouseId,
                                       @Param("refDocNumber") String refDocNumber);
}