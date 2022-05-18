package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;

@Repository
@Transactional
public interface PickupLineRepository extends JpaRepository<PickupLine,Long>, JpaSpecificationExecutor<PickupLine> {
	
	public List<PickupLine> findAll();
	
	public Optional<PickupLine> findByActualHeNo(String actualHeNo);

	public PickupLine findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
			String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
			String itemCode, Long deletionIndicator);
}