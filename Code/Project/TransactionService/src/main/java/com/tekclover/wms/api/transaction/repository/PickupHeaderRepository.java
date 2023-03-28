package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;

@Repository
@Transactional
public interface PickupHeaderRepository
		extends JpaRepository<PickupHeader, Long>, JpaSpecificationExecutor<PickupHeader> {

	@QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.fetchSize", value = "500"))
	public List<PickupHeader> findAll();

	public Optional<PickupHeader> findByPickupNumber(String pickupNumber);

	public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			Long lineNumber, String itemCode, Long deletionIndicator);

	public List<PickupHeader> findByWarehouseIdAndStatusIdAndOutboundOrderTypeIdInAndDeletionIndicator(
			String warehouseId, Long statusId, List<Long> outboundOrderTypeId, Long deletionIndicator);

	public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			Long deletionIndicator);

	public List<PickupHeader> findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			Long lineNumber, String itemCode, Long deletionIndicator);

	public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, String proposedStorageBin, String proposedPackCode, Long deletionIndicator);
}