package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.cyclecount.periodic.PeriodicLine;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface PeriodicLineRepository extends JpaRepository<PeriodicLine,Long>,
		JpaSpecificationExecutor<PeriodicLine>, StreamableJpaSpecificationRepository<PeriodicLine> {
	
	public List<PeriodicLine> findAll();
	
	public PeriodicLine findByWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
			String warehouseId, String cycleCountNo, String storageBin, String itemCode, String packBarcodes, long l);

	public List<PeriodicLine> findByCycleCountNoAndDeletionIndicator(String cycleCountNo, long l);
	
//	SELECT ITM_CODE FROM tblperiodicline 
//	WHERE WH_ID = 110 AND ITM_CODE IN :itemCode AND STATUS_ID <> 70
	@Query(value = "SELECT ITM_CODE FROM tblperiodicline \r\n"
			+ "WHERE WH_ID = 110 AND ITM_CODE IN :itemCode \r\n"
			+ "AND STATUS_ID <> 70 AND IS_DELETED = 0", nativeQuery = true)
	public List<String> findByStatusIdNotIn70(@Param(value = "itemCode") Set<String> itemCode);
}