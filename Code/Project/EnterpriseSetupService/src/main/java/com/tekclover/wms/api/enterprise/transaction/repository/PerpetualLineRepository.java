package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.enterprise.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface PerpetualLineRepository extends JpaRepository<PerpetualLine,Long>,
		JpaSpecificationExecutor<PerpetualLine> , StreamableJpaSpecificationRepository<PerpetualLine> {
	
	public PerpetualLine 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String cycleCountNo, 
				String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);
	
	public List<PerpetualLine> findByWarehouseIdAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
			String warehouseId, String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE PerpetualLine pl \r\n"
			+ " SET pl.cycleCounterId = :cycleCounterId, pl.cycleCounterName = :cycleCounterName, pl.statusId = :statusId,  \r\n"
			+ " pl.countedBy = :countedBy, pl.countedOn = :countedOn "
			+ " WHERE pl.warehouseId = :warehouseId AND \r\n "
			+ " pl.cycleCountNo = :cycleCountNo AND pl.storageBin in :storageBin AND pl.itemCode = :itemCode AND pl.packBarcodes = :packBarcodes"
			+ " AND deletionIndicator = 0")
	public void updateHHTUser (
			@Param ("cycleCounterId") String cycleCounterId, 
			@Param ("cycleCounterName") String cycleCounterName,
			@Param ("statusId") Long statusId, 
			@Param ("countedBy") String countedBy,
			@Param ("countedOn") Date countedOn,
			@Param ("warehouseId") String warehouseId,
			@Param ("cycleCountNo") String cycleCountNo,
			@Param ("storageBin") String storageBin,
			@Param ("itemCode") String itemCode,
			@Param ("packBarcodes") String packBarcodes);
	
	public List<PerpetualLine> findByCycleCountNoAndDeletionIndicator(String cycleCountNo, Long deletionIndicator);

	public List<PerpetualLine> findByCycleCountNoAndCycleCounterIdInAndDeletionIndicator(String cycleCountNo,
			List<String> cycleCounterId, Long deletionIndicator);
}