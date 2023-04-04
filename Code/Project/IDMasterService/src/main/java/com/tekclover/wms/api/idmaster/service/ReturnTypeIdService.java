package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.returntypeid.AddReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.ReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.UpdateReturnTypeId;
import com.tekclover.wms.api.idmaster.repository.ReturnTypeIdRepository;
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
public class ReturnTypeIdService extends BaseService {
	
	@Autowired
	private ReturnTypeIdRepository returnTypeIdRepository;
	
	/**
	 * getReturnTypeIds
	 * @return
	 */
	public List<ReturnTypeId> getReturnTypeIds () {
		List<ReturnTypeId> returnTypeIdList =  returnTypeIdRepository.findAll();
		returnTypeIdList = returnTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return returnTypeIdList;
	}
	
	/**
	 * getReturnTypeId
	 * @param returnTypeId
	 * @return
	 */
	public ReturnTypeId getReturnTypeId (String warehouseId, String returnTypeId) {
		Optional<ReturnTypeId> dbReturnTypeId = 
				returnTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndReturnTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								returnTypeId,
								getLanguageId(),
								0L
								);
		if (dbReturnTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"returnTypeId - " + returnTypeId +
						" doesn't exist.");
			
		} 
		return dbReturnTypeId.get();
	}
	
	/**
	 * createReturnTypeId
	 * @param newReturnTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ReturnTypeId createReturnTypeId (AddReturnTypeId newReturnTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ReturnTypeId dbReturnTypeId = new ReturnTypeId();
		log.info("newReturnTypeId : " + newReturnTypeId);
		BeanUtils.copyProperties(newReturnTypeId, dbReturnTypeId, CommonUtils.getNullPropertyNames(newReturnTypeId));
		dbReturnTypeId.setCompanyCodeId(getCompanyCode());
		dbReturnTypeId.setPlantId(getPlantId());
		dbReturnTypeId.setDeletionIndicator(0L);
		dbReturnTypeId.setCreatedBy(loginUserID);
		dbReturnTypeId.setUpdatedBy(loginUserID);
		dbReturnTypeId.setCreatedOn(new Date());
		dbReturnTypeId.setUpdatedOn(new Date());
		return returnTypeIdRepository.save(dbReturnTypeId);
	}
	
	/**
	 * updateReturnTypeId
	 * @param loginUserID
	 * @param returnTypeId
	 * @param updateReturnTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ReturnTypeId updateReturnTypeId (String warehouseId, String returnTypeId, String loginUserID,
			UpdateReturnTypeId updateReturnTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		ReturnTypeId dbReturnTypeId = getReturnTypeId( warehouseId, returnTypeId);
		BeanUtils.copyProperties(updateReturnTypeId, dbReturnTypeId, CommonUtils.getNullPropertyNames(updateReturnTypeId));
		dbReturnTypeId.setUpdatedBy(loginUserID);
		dbReturnTypeId.setUpdatedOn(new Date());
		return returnTypeIdRepository.save(dbReturnTypeId);
	}
	
	/**
	 * deleteReturnTypeId
	 * @param loginUserID 
	 * @param returnTypeId
	 */
	public void deleteReturnTypeId (String warehouseId, String returnTypeId, String loginUserID) {
		ReturnTypeId dbReturnTypeId = getReturnTypeId( warehouseId, returnTypeId);
		if ( dbReturnTypeId != null) {
			dbReturnTypeId.setDeletionIndicator(1L);
			dbReturnTypeId.setUpdatedBy(loginUserID);
			returnTypeIdRepository.save(dbReturnTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + returnTypeId);
		}
	}
}
