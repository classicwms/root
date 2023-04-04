package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.processid.AddProcessId;
import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
import com.tekclover.wms.api.idmaster.model.processid.UpdateProcessId;
import com.tekclover.wms.api.idmaster.repository.ProcessIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcessIdService extends BaseService {
	
	@Autowired
	private ProcessIdRepository processIdRepository;
	
	/**
	 * getProcessIds
	 * @return
	 */
	public List<ProcessId> getProcessIds () {
		List<ProcessId> processIdList =  processIdRepository.findAll();
		processIdList = processIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return processIdList;
	}
	
	/**
	 * getProcessId
	 * @param processId
	 * @return
	 */
	public ProcessId getProcessId (String warehouseId, String processId) {
		Optional<ProcessId> dbProcessId = 
				processIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								processId,
								getLanguageId(),
								0L
								);
		if (dbProcessId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"processId - " + processId +
						" doesn't exist.");
			
		} 
		return dbProcessId.get();
	}
	
	/**
	 * createProcessId
	 * @param newProcessId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ProcessId createProcessId (AddProcessId newProcessId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ProcessId dbProcessId = new ProcessId();
		log.info("newProcessId : " + newProcessId);
		BeanUtils.copyProperties(newProcessId, dbProcessId, CommonUtils.getNullPropertyNames(newProcessId));
		dbProcessId.setCompanyCodeId(getCompanyCode());
		dbProcessId.setPlantId(getPlantId());
		dbProcessId.setDeletionIndicator(0L);
		dbProcessId.setCreatedBy(loginUserID);
		dbProcessId.setUpdatedBy(loginUserID);
		dbProcessId.setCreatedOn(new Date());
		dbProcessId.setUpdatedOn(new Date());
		return processIdRepository.save(dbProcessId);
	}
	
	/**
	 * updateProcessId
	 * @param loginUserID
	 * @param processId
	 * @param updateProcessId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ProcessId updateProcessId (String warehouseId, String processId, String loginUserID,
			UpdateProcessId updateProcessId) 
			throws IllegalAccessException, InvocationTargetException {
		ProcessId dbProcessId = getProcessId( warehouseId, processId);
		BeanUtils.copyProperties(updateProcessId, dbProcessId, CommonUtils.getNullPropertyNames(updateProcessId));
		dbProcessId.setUpdatedBy(loginUserID);
		dbProcessId.setUpdatedOn(new Date());
		return processIdRepository.save(dbProcessId);
	}
	
	/**
	 * deleteProcessId
	 * @param loginUserID 
	 * @param processId
	 */
	public void deleteProcessId (String warehouseId, String processId, String loginUserID) {
		ProcessId dbProcessId = getProcessId( warehouseId, processId);
		if ( dbProcessId != null) {
			dbProcessId.setDeletionIndicator(1L);
			dbProcessId.setUpdatedBy(loginUserID);
			processIdRepository.save(dbProcessId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + processId);
		}
	}
}
