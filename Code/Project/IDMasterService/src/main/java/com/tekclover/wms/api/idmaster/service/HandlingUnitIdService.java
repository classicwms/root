package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.handlingunitid.AddHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.HandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.UpdateHandlingUnitId;
import com.tekclover.wms.api.idmaster.repository.HandlingUnitIdRepository;
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
public class HandlingUnitIdService extends BaseService {
	
	@Autowired
	private HandlingUnitIdRepository handlingUnitIdRepository;
	
	/**
	 * getHandlingUnitIds
	 * @return
	 */
	public List<HandlingUnitId> getHandlingUnitIds () {
		List<HandlingUnitId> handlingUnitIdList =  handlingUnitIdRepository.findAll();
		handlingUnitIdList = handlingUnitIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return handlingUnitIdList;
	}
	
	/**
	 * getHandlingUnitId
	 * @param handlingUnitId
	 * @return
	 */
	public HandlingUnitId getHandlingUnitId (String warehouseId,String handlingUnitId, String handlingUnitNumber) {
		Optional<HandlingUnitId> dbHandlingUnitId =
				handlingUnitIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitIdAndHandlingUnitNumberAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								handlingUnitId,
								handlingUnitNumber,
								getLanguageId(),
								0L
								);
		if (dbHandlingUnitId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"handlingUnitId - " + handlingUnitId +
						"handlingUnitNumber - "+ handlingUnitNumber +
						" doesn't exist.");
			
		} 
		return dbHandlingUnitId.get();
	}
	
	/**
	 * createHandlingUnitId
	 * @param newHandlingUnitId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingUnitId createHandlingUnitId (AddHandlingUnitId newHandlingUnitId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		HandlingUnitId dbHandlingUnitId = new HandlingUnitId();
		log.info("newHandlingUnitId : " + newHandlingUnitId);
		BeanUtils.copyProperties(newHandlingUnitId, dbHandlingUnitId, CommonUtils.getNullPropertyNames(newHandlingUnitId));
		dbHandlingUnitId.setCompanyCodeId(getCompanyCode());
		dbHandlingUnitId.setPlantId(getPlantId());
		dbHandlingUnitId.setDeletionIndicator(0L);
		dbHandlingUnitId.setCreatedBy(loginUserID);
		dbHandlingUnitId.setUpdatedBy(loginUserID);
		dbHandlingUnitId.setCreatedOn(new Date());
		dbHandlingUnitId.setUpdatedOn(new Date());
		return handlingUnitIdRepository.save(dbHandlingUnitId);
	}
	
	/**
	 * updateHandlingUnitId
	 * @param loginUserID
	 * @param handlingUnitId
	 * @param updateHandlingUnitId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingUnitId updateHandlingUnitId (String warehouseId, String handlingUnitId,String handlingUnitNumber, String loginUserID,
			UpdateHandlingUnitId updateHandlingUnitId) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingUnitId dbHandlingUnitId = getHandlingUnitId( warehouseId, handlingUnitId,handlingUnitNumber);
		BeanUtils.copyProperties(updateHandlingUnitId, dbHandlingUnitId, CommonUtils.getNullPropertyNames(updateHandlingUnitId));
		dbHandlingUnitId.setUpdatedBy(loginUserID);
		dbHandlingUnitId.setUpdatedOn(new Date());
		return handlingUnitIdRepository.save(dbHandlingUnitId);
	}
	
	/**
	 * deleteHandlingUnitId
	 * @param loginUserID 
	 * @param handlingUnitId
	 */
	public void deleteHandlingUnitId (String warehouseId, String handlingUnitId,String handlingUnitNumber, String loginUserID) {
		HandlingUnitId dbHandlingUnitId = getHandlingUnitId( warehouseId, handlingUnitId, handlingUnitNumber);
		if ( dbHandlingUnitId != null) {
			dbHandlingUnitId.setDeletionIndicator(1L);
			dbHandlingUnitId.setUpdatedBy(loginUserID);
			handlingUnitIdRepository.save(dbHandlingUnitId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + handlingUnitId);
		}
	}
}
