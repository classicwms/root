package com.tekclover.wms.api.transaction.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;

@Repository
@Transactional
public interface PerpetualLineRepository extends JpaRepository<PerpetualLine,Long>, JpaSpecificationExecutor<PerpetualLine> {
	
	public PerpetualLine 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String cycleCountNo, 
				String storageBin, String itemCode, String packBarcodes, Long deletionIndicator);
	
	/*
	 * dbPerpetualLine.setCycleCounterId(assignHHTUser.getCycleCounterId());
			dbPerpetualLine.setCycleCounterName(assignHHTUser.getCycleCounterName());
			dbPerpetualLine.setStatusId(72L);
			dbPerpetualLine.setCountedBy(loginUserID);
			dbPerpetualLine.setCountedOn(new Date());
	 */
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