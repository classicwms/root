package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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


    void deleteByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, Long deletionIndicator);

}