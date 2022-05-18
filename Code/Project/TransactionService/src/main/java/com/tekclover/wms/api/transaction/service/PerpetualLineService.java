package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualLine;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.UpdatePerpetualLine;
import com.tekclover.wms.api.transaction.repository.PerpetualLineRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PerpetualLineService extends BaseService {
	
	@Autowired
	private PerpetualLineRepository perpetualLineRepository;
	
	/**
	 * getPerpetualLines
	 * @return
	 */
	public List<PerpetualLine> getPerpetualLines () {
		List<PerpetualLine> perpetualLineList =  perpetualLineRepository.findAll();
		perpetualLineList = perpetualLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return perpetualLineList;
	}
	
	/**
	 * getPerpetualLine
	 * @param cycleCountNo
	 * @return
	 */
	public PerpetualLine getPerpetualLine (String warehouseId, String cycleCountNo, 
		String storageBin, String itemCode, String packBarcodes) {
		Optional<PerpetualLine> perpetualLine = 
				perpetualLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
						getCompanyCode(), getPlantId(), warehouseId, cycleCountNo, storageBin, itemCode, 
						packBarcodes, 0L);
		
		if (perpetualLine.isEmpty()) {
			throw new BadRequestException("The given PerpetualLine ID - "
					+ " warehouseId: " + warehouseId + ","
					+ "cycleCountNo: " + cycleCountNo + "," 
					+ "storageBin: " + storageBin + ","
					+ "itemCode: " + itemCode + ","
					+ "packBarcodes: " + packBarcodes + ","					
					+ " doesn't exist.");
		} 
		
		return perpetualLine.get();
	}
	
	/**
	 * createPerpetualLine
	 * @param newPerpetualLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualLine createPerpetualLine (AddPerpetualLine newPerpetualLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<PerpetualLine> perpetualline = 
				perpetualLineRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountNoAndStorageBinAndItemCodeAndPackBarcodesAndDeletionIndicator(
						newPerpetualLine.getCompanyCodeId(),
						newPerpetualLine.getPlantId(),
						newPerpetualLine.getWarehouseId(),
						newPerpetualLine.getCycleCountNo(),
						newPerpetualLine.getStorageBin(),
						newPerpetualLine.getItemCode(),
						newPerpetualLine.getPackBarcodes(),
						0L);
		if (!perpetualline.isEmpty()) {
			throw new BadRequestException("Record is getting duplicated with the given values");
		}
		
		PerpetualLine dbPerpetualLine = new PerpetualLine();
		log.info("newPerpetualLine : " + newPerpetualLine);
		BeanUtils.copyProperties(newPerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(newPerpetualLine));
		dbPerpetualLine.setDeletionIndicator(0L);
		dbPerpetualLine.setCreatedBy(loginUserID);
		dbPerpetualLine.setCreatedOn(new Date());
		dbPerpetualLine.setCountedBy(loginUserID);
		dbPerpetualLine.setCountedOn(new Date());
		return perpetualLineRepository.save(dbPerpetualLine);
	}
	
	/**
	 * updatePerpetualLine
	 * @param loginUserId 
	 * @param cycleCountNo
	 * @param updatePerpetualLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualLine updatePerpetualLine (String companyCodeId, String plantId, String warehouseId, 
			String cycleCountNo, String storageBin, String itemCode, String packBarcodes, 
			String loginUserID, UpdatePerpetualLine updatePerpetualLine) 
			throws IllegalAccessException, InvocationTargetException {
		PerpetualLine dbPerpetualLine = getPerpetualLine(warehouseId, cycleCountNo, storageBin, itemCode, 
				packBarcodes);
		BeanUtils.copyProperties(updatePerpetualLine, dbPerpetualLine, CommonUtils.getNullPropertyNames(updatePerpetualLine));
		dbPerpetualLine.setCountedBy(loginUserID);
		dbPerpetualLine.setCountedOn(new Date());
		return perpetualLineRepository.save(dbPerpetualLine);
	}
	
	/**
	 * deletePerpetualLine
	 * @param loginUserID 
	 * @param cycleCountNo
	 */
	public void deletePerpetualLine (String companyCodeId, String plantId, String warehouseId, String cycleCountNo, 
		String storageBin, String itemCode, String packBarcodes, String loginUserID) {
		PerpetualLine perpetualLine = getPerpetualLine(warehouseId, cycleCountNo, storageBin, itemCode, 
				packBarcodes);
		if ( perpetualLine != null) {
			perpetualLine.setDeletionIndicator(1L);
			perpetualLine.setCountedBy(loginUserID);
			perpetualLine.setCountedOn(new Date());
			perpetualLineRepository.save(perpetualLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + cycleCountNo);
		}
	}
}
