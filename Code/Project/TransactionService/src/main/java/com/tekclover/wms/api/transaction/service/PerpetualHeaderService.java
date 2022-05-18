package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.AddPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.PerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.SearchPerpetualHeader;
import com.tekclover.wms.api.transaction.model.cyclecount.perpetual.UpdatePerpetualHeader;
import com.tekclover.wms.api.transaction.repository.PerpetualHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.PerpetualHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PerpetualHeaderService extends BaseService {
	
	@Autowired
	private PerpetualHeaderRepository perpetualHeaderRepository;
	
	/**
	 * getPerpetualHeaders
	 * @return
	 */
	public List<PerpetualHeader> getPerpetualHeaders () {
		List<PerpetualHeader> perpetualHeaderList =  perpetualHeaderRepository.findAll();
		perpetualHeaderList = perpetualHeaderList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0L)
				.collect(Collectors.toList());
		return perpetualHeaderList;
	}
	
	/**
	 * getPerpetualHeader
	 * @param cycleCountNo
	 * @return
	 */
	public PerpetualHeader getPerpetualHeader (String warehouseId, Long cycleCountTypeId, String cycleCountNo, 
			Long movementTypeId, Long subMovementTypeId) {
		Optional<PerpetualHeader> perpetualHeader = perpetualHeaderRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
				getCompanyCode(), getPlantId(), warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
				subMovementTypeId, 0L);
		
		if (perpetualHeader.isEmpty()) {
			throw new BadRequestException("The given PerpetualHeader ID : " + cycleCountNo 
					+ "cycleCountTypeId: " + cycleCountTypeId + ","
					+ "movementTypeId: " + movementTypeId + ","
					+ "subMovementTypeId: " + subMovementTypeId
					+ " doesn't exist.");
		}
		
		return perpetualHeader.get();
	}
	
	/**
	 * 
	 * @param searchPerpetualHeader
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException 
	 */
	public List<PerpetualHeader> findPerpetualHeader(SearchPerpetualHeader searchPerpetualHeader) 
			throws ParseException, java.text.ParseException {		
		if (searchPerpetualHeader.getStartCreatedOn() != null && searchPerpetualHeader.getStartCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchPerpetualHeader.getStartCreatedOn(), searchPerpetualHeader.getEndCreatedOn());
			searchPerpetualHeader.setStartCreatedOn(dates[0]);
			searchPerpetualHeader.setEndCreatedOn(dates[1]);
		}
		
		PerpetualHeaderSpecification spec = new PerpetualHeaderSpecification(searchPerpetualHeader);
		List<PerpetualHeader> results = perpetualHeaderRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createPerpetualHeader
	 * @param newPerpetualHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualHeader createPerpetualHeader (AddPerpetualHeader newPerpetualHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<PerpetualHeader> perpetualheader = 
				perpetualHeaderRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndCycleCountNoAndMovementTypeIdAndSubMovementTypeIdAndDeletionIndicator(
						newPerpetualHeader.getCompanyCodeId(),
						newPerpetualHeader.getPalntId(),
						newPerpetualHeader.getWarehouseId(),
						newPerpetualHeader.getCycleCountTypeId(),
						newPerpetualHeader.getCycleCountNo(),
						newPerpetualHeader.getMovementTypeId(),
						newPerpetualHeader.getSubMovementTypeId(),
						0L);
		if (!perpetualheader.isEmpty()) {
			throw new BadRequestException("Record is getting duplicated with the given values");
		}
		
		PerpetualHeader dbPerpetualHeader = new PerpetualHeader();
		log.info("newPerpetualHeader : " + newPerpetualHeader);
		BeanUtils.copyProperties(newPerpetualHeader, dbPerpetualHeader, CommonUtils.getNullPropertyNames(newPerpetualHeader));
		dbPerpetualHeader.setDeletionIndicator(0L);
		dbPerpetualHeader.setCreatedBy(loginUserID);
		dbPerpetualHeader.setCountedBy(loginUserID);
		dbPerpetualHeader.setCreatedOn(new Date());
		dbPerpetualHeader.setCountedOn(new Date());
		return perpetualHeaderRepository.save(dbPerpetualHeader);
	}
	
	/**
	 * updatePerpetualHeader
	 * @param loginUserId 
	 * @param cycleCountNo
	 * @param updatePerpetualHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PerpetualHeader updatePerpetualHeader (String companyCodeId, String palntId, String warehouseId, 
		Long cycleCountTypeId, String cycleCountNo, Long movementTypeId, Long subMovementTypeId, 
			String loginUserID, UpdatePerpetualHeader updatePerpetualHeader) 
			throws IllegalAccessException, InvocationTargetException {
		PerpetualHeader dbPerpetualHeader = getPerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
				subMovementTypeId);
		BeanUtils.copyProperties(updatePerpetualHeader, dbPerpetualHeader, CommonUtils.getNullPropertyNames(updatePerpetualHeader));
		dbPerpetualHeader.setCountedBy(loginUserID);
		dbPerpetualHeader.setCountedOn(new Date());
		return perpetualHeaderRepository.save(dbPerpetualHeader);
	}
	
	/**
	 * deletePerpetualHeader
	 * @param loginUserID 
	 * @param cycleCountNo
	 */
	public void deletePerpetualHeader (String companyCodeId, String palntId, String warehouseId, 
		Long cycleCountTypeId, String cycleCountNo, Long movementTypeId, Long subMovementTypeId, String loginUserID) {
		PerpetualHeader dbPerpetualHeader = getPerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
				subMovementTypeId);
		if ( dbPerpetualHeader != null) {
			dbPerpetualHeader.setDeletionIndicator(1L);
			dbPerpetualHeader.setCountedBy(loginUserID);
			dbPerpetualHeader.setCountedOn(new Date());
			perpetualHeaderRepository.save(dbPerpetualHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + cycleCountNo);
		}
	}
}
