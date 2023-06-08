package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.bom.AddBomLine;
import com.tekclover.wms.api.masters.model.bom.BomLine;
import com.tekclover.wms.api.masters.model.bom.UpdateBomLine;
import com.tekclover.wms.api.masters.repository.BomLineRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BomLineService extends BaseService {
	
	@Autowired
	private BomLineRepository bomLineRepository;
	
	/**
	 * getBomLines
	 * @return
	 */
	public List<BomLine> getBomLines () {
		List<BomLine> bomLineList =  bomLineRepository.findAll();
		bomLineList = bomLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return bomLineList;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param bomNumber
	 * @return
	 */
	public List<BomLine> getBomLine (String warehouseId, Long bomNumber) {
		List<BomLine> bomLine = bomLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId,
						bomNumber,
						0L);
		return bomLine;
	}
	
	/**
	 * getBomLine
	 * @param bomNumber
	 * @return
	 */
	public BomLine getBomLine (String warehouseId, Long bomNumber, String childItemCode) {
		Optional<BomLine> bomLine = 
				bomLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndChildItemCodeAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId,
						bomNumber,
						childItemCode,
						0L);
		if (bomLine.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",bomNumber: " + bomNumber + 
					",childItemCode: " + childItemCode +
					" doesn't exist.");
		} 
		return bomLine.get();
	}
	
	/**
	 * createBomLine
	 * @param newBomLine
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BomLine createBomLine (AddBomLine newBomLine, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BomLine dbBomLine = new BomLine();
		log.info("newBomLine : " + newBomLine);
		BeanUtils.copyProperties(newBomLine, dbBomLine, CommonUtils.getNullPropertyNames(newBomLine));
		dbBomLine.setDeletionIndicator(0L);
		dbBomLine.setCreatedBy(loginUserID);
		dbBomLine.setUpdatedBy(loginUserID);
		dbBomLine.setCreatedOn(new Date());
		dbBomLine.setUpdatedOn(new Date());
		return bomLineRepository.save(dbBomLine);
	}
	
	/**
	 * updateBomLine
	 * @param loginUserId 
	 * @param bomNumber
	 * @param updateBomLine
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BomLine updateBomLine (String warehouseId, Long bomNumber, String childItemCode, 
			String loginUserID, UpdateBomLine updateBomLine) 
			throws IllegalAccessException, InvocationTargetException {
		BomLine dbBomLine = getBomLine(warehouseId, bomNumber, childItemCode);
		BeanUtils.copyProperties(updateBomLine, dbBomLine, CommonUtils.getNullPropertyNames(updateBomLine));
		dbBomLine.setUpdatedBy(loginUserID);
		dbBomLine.setUpdatedOn(new Date());
		return bomLineRepository.save(dbBomLine);
	}
	
	/**
	 * deleteBomLine
	 * @param loginUserID 
	 * @param bomNumber
	 */
	public void deleteBomLine (String warehouseId, Long bomNumber, String childItemCode, String loginUserID) {
		BomLine bomLine = getBomLine(warehouseId, bomNumber, childItemCode);
		if ( bomLine != null) {
			bomLine.setDeletionIndicator(1L);
			bomLine.setUpdatedBy(loginUserID);
			bomLineRepository.save(bomLine);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + bomNumber);
		}
	}
}
