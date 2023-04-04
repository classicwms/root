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
import com.tekclover.wms.api.idmaster.model.statusid.AddStatusId;
import com.tekclover.wms.api.idmaster.model.statusid.StatusId;
import com.tekclover.wms.api.idmaster.model.statusid.UpdateStatusId;
import com.tekclover.wms.api.idmaster.repository.StatusIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StatusIdService extends BaseService {
	
	@Autowired
	private StatusIdRepository statusIdRepository;
	
	/**
	 * getStatusIds
	 * @return
	 */
	public List<StatusId> getStatusIds () {
		List<StatusId> statusIdList =  statusIdRepository.findAll();
		statusIdList = statusIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return statusIdList;
	}
	
	/**
	 * getStatusId
	 * @param statusId
	 * @return
	 */
	public StatusId getStatusId ( String warehouseId, Long statusId) {
		Optional<StatusId> dbStatusId = 
				statusIdRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
								getLanguageId(),
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								statusId,
								0L
								);
		if (dbStatusId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"statusId - " + statusId +
						" doesn't exist.");
			
		} 
		return dbStatusId.get();
	}
	
//	/**
//	 * 
//	 * @param searchStatusId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<StatusId> findStatusId(SearchStatusId searchStatusId) 
//			throws ParseException {
//		StatusIdSpecification spec = new StatusIdSpecification(searchStatusId);
//		List<StatusId> results = statusIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createStatusId
	 * @param newStatusId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StatusId createStatusId (AddStatusId newStatusId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StatusId dbStatusId = new StatusId();
		log.info("newStatusId : " + newStatusId);
		BeanUtils.copyProperties(newStatusId, dbStatusId, CommonUtils.getNullPropertyNames(newStatusId));
		dbStatusId.setCompanyCodeId(getCompanyCode());
		dbStatusId.setPlantId(getPlantId());
		dbStatusId.setDeletionIndicator(0L);
		dbStatusId.setCreatedBy(loginUserID);
		dbStatusId.setUpdatedBy(loginUserID);
		dbStatusId.setCreatedOn(new Date());
		dbStatusId.setUpdatedOn(new Date());
		return statusIdRepository.save(dbStatusId);
	}
	
	/**
	 * updateStatusId
	 * @param loginUserId 
	 * @param statusId
	 * @param updateStatusId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StatusId updateStatusId ( String warehouseId, Long statusId, String loginUserID, 
			UpdateStatusId updateStatusId) 
			throws IllegalAccessException, InvocationTargetException {
		StatusId dbStatusId = getStatusId(warehouseId, statusId);
		BeanUtils.copyProperties(updateStatusId, dbStatusId, CommonUtils.getNullPropertyNames(updateStatusId));
		dbStatusId.setUpdatedBy(loginUserID);
		dbStatusId.setUpdatedOn(new Date());
		return statusIdRepository.save(dbStatusId);
	}
	
	/**
	 * deleteStatusId
	 * @param loginUserID 
	 * @param statusId
	 */
	public void deleteStatusId ( String warehouseId, Long statusId, String loginUserID) {
		StatusId dbStatusId = getStatusId(warehouseId, statusId);
		if ( dbStatusId != null) {
			dbStatusId.setDeletionIndicator(1L);
			dbStatusId.setUpdatedBy(loginUserID);
			statusIdRepository.save(dbStatusId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + statusId);
		}
	}
}
