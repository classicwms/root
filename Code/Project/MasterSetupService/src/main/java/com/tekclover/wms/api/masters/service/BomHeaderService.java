package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.bom.AddBomHeader;
import com.tekclover.wms.api.masters.model.bom.AddBomLine;
import com.tekclover.wms.api.masters.model.bom.BomHeader;
import com.tekclover.wms.api.masters.model.bom.BomLine;
import com.tekclover.wms.api.masters.model.bom.SearchBomHeader;
import com.tekclover.wms.api.masters.model.bom.UpdateBomHeader;
import com.tekclover.wms.api.masters.model.bom.UpdateBomLine;
import com.tekclover.wms.api.masters.repository.BomHeaderRepository;
import com.tekclover.wms.api.masters.repository.specification.BomHeaderSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BomHeaderService extends BaseService {
	
	@Autowired
	private BomHeaderRepository bomHeaderRepository;
	
	@Autowired
	private BomLineService bomLineService;
	
	/**
	 * getBomHeaders
	 * @return
	 */
	public List<AddBomHeader> getBomHeaders () {
		List<BomHeader> bomHeaderList =  bomHeaderRepository.findAll();
		bomHeaderList = bomHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		
		List<AddBomHeader> addBomHeaders = new ArrayList<>();
		for (BomHeader bomHeader : bomHeaderList) {
			List<BomLine> bomLines = bomLineService.getBomLine (bomHeader.getWarehouseId(), bomHeader.getBomNumber());
			
			List<AddBomLine> addBomLines = new ArrayList<>();
			for (BomLine bomLine : bomLines) {
				AddBomLine addBomLine = new AddBomLine();
				BeanUtils.copyProperties(bomLine, addBomLine, CommonUtils.getNullPropertyNames(bomLine));
				addBomLines.add(addBomLine);
			}
			
			AddBomHeader addBomHeader = new AddBomHeader();
			BeanUtils.copyProperties(bomHeader, addBomHeader, CommonUtils.getNullPropertyNames(bomHeader));
			addBomHeader.setBomLines(addBomLines);
			addBomHeaders.add(addBomHeader);
		}
		
		return addBomHeaders;
	}
	
	/**
	 * getBomHeader
	 * @param parentItemCode
	 * @return
	 */
	public AddBomHeader getBomHeader (String warehouseId, String parentItemCode) {
		Optional<BomHeader> optBomHeader = 
				bomHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentItemCodeAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId,
						parentItemCode,
						0L);
		if (optBomHeader.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",parentItemCode: " + parentItemCode + 
					" doesn't exist.");
		} 
		
		BomHeader bomHeader = optBomHeader.get();
		List<BomLine> bomLines = bomLineService.getBomLine (bomHeader.getWarehouseId(), bomHeader.getBomNumber());
		
		List<AddBomLine> addBomLines = new ArrayList<>();
		for (BomLine bomLine : bomLines) {
			AddBomLine addBomLine = new AddBomLine();
			BeanUtils.copyProperties(bomLine, addBomLine, CommonUtils.getNullPropertyNames(bomLine));
			addBomLines.add(addBomLine);
		}
		
		AddBomHeader addBomHeader = new AddBomHeader();
		BeanUtils.copyProperties(bomHeader, addBomHeader, CommonUtils.getNullPropertyNames(bomHeader));
		addBomHeader.setBomLines(addBomLines);
		return addBomHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param parentItemCode
	 * @return
	 */
	public BomHeader getBomHeaderByEntity (String warehouseId, String parentItemCode) {
		Optional<BomHeader> optBomHeader = 
				bomHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentItemCodeAndDeletionIndicator(
						getLanguageId(),
						getCompanyCode(),
						getPlantId(),
						warehouseId,
						parentItemCode,
						0L);
		if (optBomHeader.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId + 
					",parentItemCode: " + parentItemCode + 
					" doesn't exist.");
		} 
		
		return optBomHeader.get();
	}
	
	/**
	 * 
	 * @param searchBomHeader
	 * @return
	 * @throws ParseException
	 */
	public List<AddBomHeader> findBomHeader(SearchBomHeader searchBomHeader) throws Exception {
		if (searchBomHeader.getStartCreatedOn() != null && searchBomHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchBomHeader.getStartCreatedOn(), searchBomHeader.getEndCreatedOn());
			searchBomHeader.setStartCreatedOn(dates[0]);
			searchBomHeader.setEndCreatedOn(dates[1]);
		}
		if (searchBomHeader.getStartUpdatedOn() != null && searchBomHeader.getStartUpdatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchBomHeader.getStartUpdatedOn(), searchBomHeader.getEndUpdatedOn());
			searchBomHeader.setStartUpdatedOn(dates[0]);
			searchBomHeader.setEndUpdatedOn(dates[1]);
		}
		BomHeaderSpecification spec = new BomHeaderSpecification(searchBomHeader);
		List<BomHeader> bomHeaderSearchResults = bomHeaderRepository.findAll(spec);
		log.info("Search results: " + bomHeaderSearchResults);
		
		/* pulling out child records */
		List<AddBomHeader> addBomHeaders = new ArrayList<>();
		for (BomHeader bomHeader : bomHeaderSearchResults) {
			List<BomLine> bomLines = bomLineService.getBomLine (bomHeader.getWarehouseId(), bomHeader.getBomNumber());
			
			List<AddBomLine> addBomLines = new ArrayList<>();
			for (BomLine bomLine : bomLines) {
				AddBomLine addBomLine = new AddBomLine();
				BeanUtils.copyProperties(bomLine, addBomLine, CommonUtils.getNullPropertyNames(bomLine));
				addBomLines.add(addBomLine);
			}
			
			AddBomHeader addBomHeader = new AddBomHeader();
			BeanUtils.copyProperties(bomHeader, addBomHeader, CommonUtils.getNullPropertyNames(bomHeader));
			addBomHeader.setBomLines(addBomLines);
			addBomHeaders.add(addBomHeader);
		}
		return addBomHeaders;
	}
	
	/**
	 * createBomHeader
	 * @param newBomHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AddBomHeader createBomHeader (AddBomHeader newBomHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		/*
		 * Creating BOM Number
		 * ------------------------
		 * On Clicking save, Pass the selected WH_ID, NUM_RAN_OBJ=90,FISCALYEAR= CURRENT CALENDAR YEAR in NUMBERRANGE table 
		 * and fetch NUM_RAN_CURRENT values and add +1 and insert this no as BOM_NO in BOMHEADER and BOMLINE tables
		 */
		Long NUM_RAN_CODE = 90L;
		String BOMNUMBER = getNextRangeNumber(NUM_RAN_CODE, newBomHeader.getWarehouseId());
		
		Optional<BomHeader> bomHeader = 
				bomHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentItemCodeAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), newBomHeader.getWarehouseId(),
						newBomHeader.getParentItemCode(), 0L);
		if (!bomHeader.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + newBomHeader.getWarehouseId() + 
					",parentItemCode: " + newBomHeader.getParentItemCode() + 
					" already existing.");
		}
		
		BomHeader dbBomHeader = new BomHeader();
		log.info("newBomHeader : " + newBomHeader);
		
		List<AddBomLine> createdBomLines = new ArrayList<>();
		for (AddBomLine newBomLine : newBomHeader.getBomLines()) {
			newBomLine.setBomNumber(Long.valueOf(BOMNUMBER));
			BomLine bomLine = bomLineService.createBomLine(newBomLine, loginUserID);
			
			AddBomLine createdBomLine = new AddBomLine();
			BeanUtils.copyProperties(bomLine, createdBomLine, CommonUtils.getNullPropertyNames(bomLine));
			createdBomLines.add(createdBomLine);
		}
		
		BeanUtils.copyProperties(newBomHeader, dbBomHeader, CommonUtils.getNullPropertyNames(newBomHeader));
		dbBomHeader.setBomNumber(Long.valueOf(BOMNUMBER));
		dbBomHeader.setDeletionIndicator(0L);
		dbBomHeader.setCreatedBy(loginUserID);
		dbBomHeader.setUpdatedBy(loginUserID);
		dbBomHeader.setCreatedOn(new Date());
		dbBomHeader.setUpdatedOn(new Date());
		dbBomHeader = bomHeaderRepository.save(dbBomHeader);
		 
		AddBomHeader createdBomHeader = new AddBomHeader();
		BeanUtils.copyProperties(dbBomHeader, createdBomHeader, CommonUtils.getNullPropertyNames(dbBomHeader));
		createdBomHeader.setBomLines(createdBomLines);
		
		return createdBomHeader;
	}
	
	/**
	 * updateBomHeader
	 * @param loginUserId 
	 * @param parentItemCode
	 * @param updateBomHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UpdateBomHeader updateBomHeader (String warehouseId, String parentItemCode, 
			String loginUserID, UpdateBomHeader updateBomHeader) 
			throws IllegalAccessException, InvocationTargetException {
		BomHeader dbBomHeader = getBomHeaderByEntity (warehouseId, parentItemCode);
		BeanUtils.copyProperties(updateBomHeader, dbBomHeader, CommonUtils.getNullPropertyNames(updateBomHeader));
		dbBomHeader.setUpdatedBy(loginUserID);
		dbBomHeader.setUpdatedOn(new Date());
		dbBomHeader = bomHeaderRepository.save(dbBomHeader);
		
		List<UpdateBomLine> updateBomLines = new ArrayList<>();
		for (UpdateBomLine updateBomLine : updateBomHeader.getBomLines()) {
			BomLine bomLine = bomLineService.updateBomLine(warehouseId, updateBomLine.getBomNumber(), 
					updateBomLine.getChildItemCode(), loginUserID, updateBomLine);
			
			UpdateBomLine updatedBomLine = new UpdateBomLine ();
			BeanUtils.copyProperties(bomLine, updatedBomLine, CommonUtils.getNullPropertyNames(bomLine));
			updateBomLines.add(updatedBomLine);
		}
		
		UpdateBomHeader updatedBomHeader = new UpdateBomHeader ();
		BeanUtils.copyProperties(dbBomHeader, updatedBomHeader, CommonUtils.getNullPropertyNames(dbBomHeader));
		updatedBomHeader.setBomLines(updateBomLines);
		return updatedBomHeader;
	}
	
	/**
	 * deleteBomHeader
	 * @param loginUserID 
	 * @param parentItemCode
	 */
	public void deleteBomHeader (String warehouseId, String parentItemCode, String loginUserID) {
		BomHeader bomHeader = getBomHeaderByEntity(warehouseId, parentItemCode);
		if ( bomHeader != null) {
			bomHeader.setDeletionIndicator(1L);
			bomHeader.setUpdatedBy(loginUserID);
			bomHeaderRepository.save(bomHeader);
			List<BomLine> bomLines = bomLineService.getBomLine(warehouseId, bomHeader.getBomNumber());
			
			for (BomLine bomLine : bomLines) {
				bomLineService.deleteBomLine(warehouseId, bomHeader.getBomNumber(), 
						bomLine.getChildItemCode(), loginUserID);
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + parentItemCode);
		}
	}
}
