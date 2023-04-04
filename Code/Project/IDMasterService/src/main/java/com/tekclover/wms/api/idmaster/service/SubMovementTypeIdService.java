package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.AddSubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.SubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.UpdateSubMovementTypeId;
import com.tekclover.wms.api.idmaster.repository.SubMovementTypeIdRepository;
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
public class SubMovementTypeIdService extends BaseService {
	
	@Autowired
	private SubMovementTypeIdRepository subMovementTypeIdRepository;
	
	/**
	 * getSubMovementTypeIds
	 * @return
	 */
	public List<SubMovementTypeId> getSubMovementTypeIds () {
		List<SubMovementTypeId> subMovementTypeIdList =  subMovementTypeIdRepository.findAll();
		subMovementTypeIdList = subMovementTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return subMovementTypeIdList;
	}
	
	/**
	 * getSubMovementTypeId
	 * @param subMovementTypeId
	 * @return
	 */
	public SubMovementTypeId getSubMovementTypeId (String warehouseId, String movementTypeId, String subMovementTypeId) {
		Optional<SubMovementTypeId> dbSubMovementTypeId = 
				subMovementTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubMovementTypeIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								movementTypeId,
								subMovementTypeId,
								getLanguageId(),
								0L
								);
		if (dbSubMovementTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"movementTypeId - " + movementTypeId +
						"subMovementTypeId - " + subMovementTypeId +
						" doesn't exist.");
			
		} 
		return dbSubMovementTypeId.get();
	}
	
	/**
	 * createSubMovementTypeId
	 * @param newSubMovementTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubMovementTypeId createSubMovementTypeId (AddSubMovementTypeId newSubMovementTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		SubMovementTypeId dbSubMovementTypeId = new SubMovementTypeId();
		log.info("newSubMovementTypeId : " + newSubMovementTypeId);
		BeanUtils.copyProperties(newSubMovementTypeId, dbSubMovementTypeId, CommonUtils.getNullPropertyNames(newSubMovementTypeId));
		dbSubMovementTypeId.setCompanyCodeId(getCompanyCode());
		dbSubMovementTypeId.setPlantId(getPlantId());
		dbSubMovementTypeId.setDeletionIndicator(0L);
		dbSubMovementTypeId.setCreatedBy(loginUserID);
		dbSubMovementTypeId.setUpdatedBy(loginUserID);
		dbSubMovementTypeId.setCreatedOn(new Date());
		dbSubMovementTypeId.setUpdatedOn(new Date());
		return subMovementTypeIdRepository.save(dbSubMovementTypeId);
	}
	
	/**
	 * updateSubMovementTypeId
	 * @param loginUserID
	 * @param subMovementTypeId
	 * @param updateSubMovementTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubMovementTypeId updateSubMovementTypeId (String warehouseId,String movementTypeId, String subMovementTypeId, String loginUserID,
			UpdateSubMovementTypeId updateSubMovementTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		SubMovementTypeId dbSubMovementTypeId = getSubMovementTypeId( warehouseId,movementTypeId, subMovementTypeId);
		BeanUtils.copyProperties(updateSubMovementTypeId, dbSubMovementTypeId, CommonUtils.getNullPropertyNames(updateSubMovementTypeId));
		dbSubMovementTypeId.setUpdatedBy(loginUserID);
		dbSubMovementTypeId.setUpdatedOn(new Date());
		return subMovementTypeIdRepository.save(dbSubMovementTypeId);
	}
	
	/**
	 * deleteSubMovementTypeId
	 * @param loginUserID 
	 * @param subMovementTypeId
	 */
	public void deleteSubMovementTypeId (String warehouseId,String movementTypeId, String subMovementTypeId, String loginUserID) {
		SubMovementTypeId dbSubMovementTypeId = getSubMovementTypeId( warehouseId, movementTypeId, subMovementTypeId);
		if ( dbSubMovementTypeId != null) {
			dbSubMovementTypeId.setDeletionIndicator(1L);
			dbSubMovementTypeId.setUpdatedBy(loginUserID);
			subMovementTypeIdRepository.save(dbSubMovementTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + subMovementTypeId);
		}
	}
}
