package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.cyclecount.perpetual.PerpetualHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PerpetualHeaderRepository extends JpaRepository<PerpetualHeader,Long>,
	JpaSpecificationExecutor<PerpetualHeader> {
	
	public Optional<PerpetualHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
			String companyCode, String plantId, String warehouseId, Long cycleCountTypeId, String cycleCountNo,
			Long movementTypeId, Long subMovementTypeId, long l);
	
	public PerpetualHeader findByCycleCountNo(String cycleCountNo);
}