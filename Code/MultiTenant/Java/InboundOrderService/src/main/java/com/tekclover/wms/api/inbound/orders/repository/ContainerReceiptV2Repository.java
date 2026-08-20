package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.inbound.containerreceipt.v2.ContainerReceiptV2;
import com.tekclover.wms.api.inbound.orders.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ContainerReceiptV2Repository extends JpaRepository<ContainerReceiptV2,Long>,
		JpaSpecificationExecutor<ContainerReceiptV2>, StreamableJpaSpecificationRepository<ContainerReceiptV2> {


    Optional<ContainerReceiptV2> findByContainerReceiptNoAndDeletionIndicator(String containerReceiptNo, long deletionIndicator);

    Optional<ContainerReceiptV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndContainerReceiptNoAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, String containerReceiptNo, long deletionIndicator);

    Optional<ContainerReceiptV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndContainerReceiptNoAndDeletionIndicator(
            String companyCode, String plantId, String languageId, String warehouseId, String containerReceiptNo, long deletionIndicator);

    @Query(value = "select * from tblcontainerreceipt where c_id = :companyCodeId and lang_id = :languageId and plant_id = :plantId and wh_id = :warehouseId and INV_NO = :invoiceNo and \n" +
            "is_deleted=0", nativeQuery = true)
    public ContainerReceiptV2 getContainerReceipt(@Param(value = "companyCodeId") String companyCodeId,
                                                  @Param(value = "languageId") String languageId,
                                                  @Param(value = "plantId") String plantId,
                                                  @Param(value = "warehouseId") String warehouseId,
                                                  @Param(value = "invoiceNo") String invoiceNo);
}