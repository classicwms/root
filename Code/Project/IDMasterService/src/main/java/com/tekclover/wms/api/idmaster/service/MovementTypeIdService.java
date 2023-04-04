package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.movementtypeid.AddMovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import com.tekclover.wms.api.idmaster.model.movementtypeid.UpdateMovementTypeId;
import com.tekclover.wms.api.idmaster.repository.MovementTypeIdRepository;
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
public class MovementTypeIdService extends BaseService {
	
	@Autowired
	private MovementTypeIdRepository movementTypeIdRepository;
	
	/**
	 * getMovementTypeIds
	 * @return
	 */
	public List<MovementTypeId> getMovementTypeIds () {
		List<MovementTypeId> movementTypeIdList =  movementTypeIdRepository.findAll();
		movementTypeIdList = movementTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return movementTypeIdList;
	}
	
	/**
	 * getMovementTypeId
	 * @param movementTypeId
	 * @return
	 */
	public MovementTypeId getMovementTypeId (String warehouseId, String movementTypeId) {
		Optional<MovementTypeId> dbMovementTypeId = 
				movementTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								movementTypeId,
								getLanguageId(),
								0L
								);
		if (dbMovementTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"movementTypeId - " + movementTypeId +
						" doesn't exist.");
			
		} 
		return dbMovementTypeId.get();
	}
	
	/**
	 * createMovementTypeId
	 * @param newMovementTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public MovementTypeId createMovementTypeId (AddMovementTypeId newMovementTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		MovementTypeId dbMovementTypeId = new MovementTypeId();
		log.info("newMovementTypeId : " + newMovementTypeId);
		BeanUtils.copyProperties(newMovementTypeId, dbMovementTypeId, CommonUtils.getNullPropertyNames(newMovementTypeId));
		dbMovementTypeId.setCompanyCodeId(getCompanyCode());
		dbMovementTypeId.setPlantId(getPlantId());
		dbMovementTypeId.setDeletionIndicator(0L);
		dbMovementTypeId.setCreatedBy(loginUserID);
		dbMovementTypeId.setUpdatedBy(loginUserID);
		dbMovementTypeId.setCreatedOn(new Date());
		dbMovementTypeId.setUpdatedOn(new Date());
		return movementTypeIdRepository.save(dbMovementTypeId);
	}
	
	/**
	 * updateMovementTypeId
	 * @param loginUserID
	 * @param movementTypeId
	 * @param updateMovementTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public MovementTypeId updateMovementTypeId (String warehouseId, String movementTypeId, String loginUserID,
			UpdateMovementTypeId updateMovementTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		MovementTypeId dbMovementTypeId = getMovementTypeId( warehouseId, movementTypeId);
		BeanUtils.copyProperties(updateMovementTypeId, dbMovementTypeId, CommonUtils.getNullPropertyNames(updateMovementTypeId));
		dbMovementTypeId.setUpdatedBy(loginUserID);
		dbMovementTypeId.setUpdatedOn(new Date());
		return movementTypeIdRepository.save(dbMovementTypeId);
	}
	
	/**
	 * deleteMovementTypeId
	 * @param loginUserID 
	 * @param movementTypeId
	 */
	public void deleteMovementTypeId (String warehouseId, String movementTypeId, String loginUserID) {
		MovementTypeId dbMovementTypeId = getMovementTypeId( warehouseId, movementTypeId);
		if ( dbMovementTypeId != null) {
			dbMovementTypeId.setDeletionIndicator(1L);
			dbMovementTypeId.setUpdatedBy(loginUserID);
			movementTypeIdRepository.save(dbMovementTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + movementTypeId);
		}
	}
}
