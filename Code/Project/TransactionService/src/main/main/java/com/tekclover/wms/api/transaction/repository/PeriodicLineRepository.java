package com.tekclover.wms.api.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.cyclecount.periodic.PeriodicLine;

@Repository
@Transactional
public interface PeriodicLineRepository extends JpaRepository<PeriodicLine,Long>, JpaSpecificationExecutor<PeriodicLine> {
	
	public List<PeriodicLine> findAll();
	
	public PeriodicLine findByWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
			String warehouseId, String cycleCountNo, String storageBin, String itemCode, String packBarcodes, long l);

	public List<PeriodicLine> findByCycleCountNoAndDeletionIndicator(String cycleCountNo, long l);
}