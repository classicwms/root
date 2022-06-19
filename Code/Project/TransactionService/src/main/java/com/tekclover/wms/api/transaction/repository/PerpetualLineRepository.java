package com.tekclover.wms.api.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;

@Repository
@Transactional
public interface PerpetualLineRepository extends JpaRepository<PerpetualLine,Long>, JpaSpecificationExecutor<PerpetualLine> {
	
	public List<PerpetualLine> findAll();
	
	public PerpetualLine 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String cycleCountNo, 
				String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);
	
	public List<PerpetualLine> findByCycleCountNoAndDeletionIndicator(String cycleCountNo, Long deletionIndicator);

	public List<PerpetualLine> findByCycleCountNoAndCycleCounterIdInAndDeletionIndicator(String cycleCountNo,
			List<String> cycleCounterId, Long deletionIndicator);
}