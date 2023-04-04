package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.AddHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.HandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.UpdateHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.repository.HandlingEquipmentIdRepository;
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
public class HandlingEquipmentIdService extends BaseService {
	
	@Autowired
	private HandlingEquipmentIdRepository handlingEquipmentIdRepository;
	
	/**
	 * getHandlingEquipmentIds
	 * @return
	 */
	public List<HandlingEquipmentId> getHandlingEquipmentIds () {
		List<HandlingEquipmentId> handlingEquipmentIdList =  handlingEquipmentIdRepository.findAll();
		handlingEquipmentIdList = handlingEquipmentIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return handlingEquipmentIdList;
	}
	
	/**
	 * getHandlingEquipmentId
	 * @param handlingEquipmentId
	 * @return
	 */
	public HandlingEquipmentId getHandlingEquipmentId (String warehouseId,String handlingEquipmentId, String handlingEquipmentNumber) {
		Optional<HandlingEquipmentId> dbHandlingEquipmentId =
				handlingEquipmentIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingEquipmentIdAndHandlingEquipmentNumberAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								handlingEquipmentId,
								handlingEquipmentNumber,
								getLanguageId(),
								0L
								);
		if (dbHandlingEquipmentId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"handlingEquipmentId - " + handlingEquipmentId +
						"handlingEquipmentNumber - "+ handlingEquipmentNumber +
						" doesn't exist.");
			
		} 
		return dbHandlingEquipmentId.get();
	}
	
	/**
	 * createHandlingEquipmentId
	 * @param newHandlingEquipmentId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingEquipmentId createHandlingEquipmentId (AddHandlingEquipmentId newHandlingEquipmentId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		HandlingEquipmentId dbHandlingEquipmentId = new HandlingEquipmentId();
		log.info("newHandlingEquipmentId : " + newHandlingEquipmentId);
		BeanUtils.copyProperties(newHandlingEquipmentId, dbHandlingEquipmentId, CommonUtils.getNullPropertyNames(newHandlingEquipmentId));
		dbHandlingEquipmentId.setCompanyCodeId(getCompanyCode());
		dbHandlingEquipmentId.setPlantId(getPlantId());
		dbHandlingEquipmentId.setDeletionIndicator(0L);
		dbHandlingEquipmentId.setCreatedBy(loginUserID);
		dbHandlingEquipmentId.setUpdatedBy(loginUserID);
		dbHandlingEquipmentId.setCreatedOn(new Date());
		dbHandlingEquipmentId.setUpdatedOn(new Date());
		return handlingEquipmentIdRepository.save(dbHandlingEquipmentId);
	}
	
	/**
	 * updateHandlingEquipmentId
	 * @param loginUserID
	 * @param handlingEquipmentId
	 * @param updateHandlingEquipmentId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingEquipmentId updateHandlingEquipmentId (String warehouseId, String handlingEquipmentId,String handlingEquipmentNumber, String loginUserID,
			UpdateHandlingEquipmentId updateHandlingEquipmentId) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingEquipmentId dbHandlingEquipmentId = getHandlingEquipmentId( warehouseId, handlingEquipmentId,handlingEquipmentNumber);
		BeanUtils.copyProperties(updateHandlingEquipmentId, dbHandlingEquipmentId, CommonUtils.getNullPropertyNames(updateHandlingEquipmentId));
		dbHandlingEquipmentId.setUpdatedBy(loginUserID);
		dbHandlingEquipmentId.setUpdatedOn(new Date());
		return handlingEquipmentIdRepository.save(dbHandlingEquipmentId);
	}
	
	/**
	 * deleteHandlingEquipmentId
	 * @param loginUserID 
	 * @param handlingEquipmentId
	 */
	public void deleteHandlingEquipmentId (String warehouseId, String handlingEquipmentId,String handlingEquipmentNumber, String loginUserID) {
		HandlingEquipmentId dbHandlingEquipmentId = getHandlingEquipmentId( warehouseId, handlingEquipmentId, handlingEquipmentNumber);
		if ( dbHandlingEquipmentId != null) {
			dbHandlingEquipmentId.setDeletionIndicator(1L);
			dbHandlingEquipmentId.setUpdatedBy(loginUserID);
			handlingEquipmentIdRepository.save(dbHandlingEquipmentId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + handlingEquipmentId);
		}
	}
}
