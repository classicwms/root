package com.tekclover.wms.api.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeader;

@Repository
@Transactional
public interface PerpetualHeaderRepository extends JpaRepository<PerpetualHeader,Long>, JpaSpecificationExecutor<PerpetualHeader> {
	
	public List<PerpetualHeader> findAll();
	
	public Optional<PerpetualHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
			String companyCode, String plantId, String warehouseId, Long cycleCountTypeId, String cycleCountNo,
			Long movementTypeId, Long subMovementTypeId, long l);
	
	public PerpetualHeader findByCycleCountNo(String cycleCountNo);
}