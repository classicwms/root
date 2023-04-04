package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.processsequenceid.AddProcessSequenceId;
import com.tekclover.wms.api.idmaster.model.processsequenceid.ProcessSequenceId;
import com.tekclover.wms.api.idmaster.model.processsequenceid.UpdateProcessSequenceId;
import com.tekclover.wms.api.idmaster.repository.ProcessSequenceIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessSequenceIdService extends BaseService {
	
	@Autowired
	private ProcessSequenceIdRepository processSequenceIdRepository;
	
	/**
	 * getProcessSequenceIds
	 * @return
	 */
	public List<ProcessSequenceId> getProcessSequenceIds () {
		List<ProcessSequenceId> processSequenceIdList =  processSequenceIdRepository.findAll();
		processSequenceIdList = processSequenceIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return processSequenceIdList;
	}
	
	/**
	 * getProcessSequenceId
	 * @param processId
	 * @return
	 */
	public ProcessSequenceId getProcessSequenceId (String warehouseId, Long processId, Long subLevelId, String processDescription, String subProcessDescription) {
		Optional<ProcessSequenceId> dbProcessSequenceId = 
				processSequenceIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndSubLevelIdAndLanguageIdAndProcessDescriptionAndSubProcessDescriptionAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								processId,
								subLevelId,
								getLanguageId(),
								processDescription,
								subProcessDescription,
								0L
								);
		if (dbProcessSequenceId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"processId - " + processId +
						"subLevelId - " + subLevelId +
						"processDescription - " + processDescription +
						"subProcessDescription - " + subProcessDescription +
						" doesn't exist.");
			
		} 
		return dbProcessSequenceId.get();
	}
	
//	/**
//	 * 
//	 * @param searchProcessSequenceId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<ProcessSequenceId> findProcessSequenceId(SearchProcessSequenceId searchProcessSequenceId) 
//			throws ParseException {
//		ProcessSequenceIdSpecification spec = new ProcessSequenceIdSpecification(searchProcessSequenceId);
//		List<ProcessSequenceId> results = processSequenceIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createProcessSequenceId
	 * @param newProcessSequenceId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ProcessSequenceId createProcessSequenceId (AddProcessSequenceId newProcessSequenceId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ProcessSequenceId dbProcessSequenceId = new ProcessSequenceId();
		log.info("newProcessSequenceId : " + newProcessSequenceId);
		BeanUtils.copyProperties(newProcessSequenceId, dbProcessSequenceId, CommonUtils.getNullPropertyNames(newProcessSequenceId));
		dbProcessSequenceId.setCompanyCodeId(getCompanyCode());
		dbProcessSequenceId.setPlantId(getPlantId());
		dbProcessSequenceId.setDeletionIndicator(0L);
		dbProcessSequenceId.setCreatedBy(loginUserID);
		dbProcessSequenceId.setUpdatedBy(loginUserID);
		dbProcessSequenceId.setCreatedOn(new Date());
		dbProcessSequenceId.setUpdatedOn(new Date());
		return processSequenceIdRepository.save(dbProcessSequenceId);
	}
	
	/**
	 * updateProcessSequenceId
	 * @param loginUserId 
	 * @param processId
	 * @param updateProcessSequenceId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ProcessSequenceId updateProcessSequenceId (String warehouseId, Long processId, Long subLevelId, String processDescription, String subProcessDescription, String loginUserID, 
			UpdateProcessSequenceId updateProcessSequenceId) 
			throws IllegalAccessException, InvocationTargetException {
		ProcessSequenceId dbProcessSequenceId = getProcessSequenceId(warehouseId, processId, subLevelId, processDescription, subProcessDescription);
		BeanUtils.copyProperties(updateProcessSequenceId, dbProcessSequenceId, CommonUtils.getNullPropertyNames(updateProcessSequenceId));
		dbProcessSequenceId.setUpdatedBy(loginUserID);
		dbProcessSequenceId.setUpdatedOn(new Date());
		return processSequenceIdRepository.save(dbProcessSequenceId);
	}
	
	/**
	 * deleteProcessSequenceId
	 * @param loginUserID 
	 * @param processId
	 */
	public void deleteProcessSequenceId (String warehouseId, Long processId, Long subLevelId, String processDescription, String subProcessDescription, String loginUserID) {
		ProcessSequenceId dbProcessSequenceId = getProcessSequenceId(warehouseId, processId, subLevelId, processDescription, subProcessDescription);
		if ( dbProcessSequenceId != null) {
			dbProcessSequenceId.setDeletionIndicator(1L);
			dbProcessSequenceId.setUpdatedBy(loginUserID);
			processSequenceIdRepository.save(dbProcessSequenceId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + processId);
		}
	}
}
