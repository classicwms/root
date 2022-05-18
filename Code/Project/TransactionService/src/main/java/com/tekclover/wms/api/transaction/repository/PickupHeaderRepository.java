package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;

@Repository
@Transactional
public interface PickupHeaderRepository extends JpaRepository<PickupHeader,Long>, JpaSpecificationExecutor<PickupHeader> {
	
	public List<PickupHeader> findAll();
	
	public Optional<PickupHeader> findByPickupNumber(String pickupNumber);
	
	public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			Long lineNumber, String itemCode, Long deletionIndicator);
	
	public List<PickupHeader> findByWarehouseIdAndStatusIdAndOutboundOrderTypeIdIn (String warehouseId, Long statusId,
			List<Long> outboundOrderTypeId);

	public PickupHeader findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
			Long deletionIndicator);
}