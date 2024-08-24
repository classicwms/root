//package com.tekclover.wms.api.transaction.repository;
//
//import com.tekclover.wms.api.transaction.model.inbound.containerreceipt.v2.ContainerReceiptV2;
//import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Repository
//@Transactional
//public interface ContainerReceiptV2Repository extends JpaRepository<ContainerReceiptV2,Long>,
//		JpaSpecificationExecutor<ContainerReceiptV2>, StreamableJpaSpecificationRepository<ContainerReceiptV2> {
//
//
//    Optional<ContainerReceiptV2> findByContainerReceiptNoAndDeletionIndicator(String containerReceiptNo, long deletionIndicator);
//
//    Optional<ContainerReceiptV2> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndContainerReceiptNoAndDeletionIndicator(
//            String languageId, String companyCode, String plantId, String warehouseId, String preInboundNo, String refDocNumber, String containerReceiptNo, long deletionIndicator);
//
//    Optional<ContainerReceiptV2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndContainerReceiptNoAndDeletionIndicator(
//            String companyCode, String plantId, String languageId, String warehouseId, String containerReceiptNo, long deletionIndicator);
//}