package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;

@Repository
@Transactional
public interface OrderManagementLineRepository extends JpaRepository<OrderManagementLine,Long>, JpaSpecificationExecutor<OrderManagementLine> {
	
	public List<OrderManagementLine> findAll();
	
	public Optional<OrderManagementLine> findByRefDocNumber(String refDocNumber);
	
	public List<OrderManagementLine> findByPreOutboundNoAndLineNumberAndItemCodeAndDeletionIndicator(String preOutboundNo,
			Long lineNumber, String itemCode, Long deletionIndicator);

	public OrderManagementLine findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
			String languageId, String companyCodeId, String plantId, String warehouseId, String preOutboundNo,
			String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String proposedStorageBin, String proposedPackBarCode, Long deletionIndicator);

	public OrderManagementLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, String proposedStorageBin, String proposedPackBarCode, Long deletionIndicator);

	public List<OrderManagementLine> findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, Long deletionIndicator);

	public List<OrderManagementLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, Long deletionIndicator);

	public List<OrderManagementLine> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, String proposedStorageBin, String proposedPackBarCode, Long deletionIndicator);
	
	public List<OrderManagementLine> findByWarehouseIdAndStatusIdIn(String warehouseId, List<Long> statusIds);
}