package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.aisleid.*;
import com.tekclover.wms.api.idmaster.repository.AisleIdRepository;
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
public class AisleIdService extends BaseService {
	
	@Autowired
	private AisleIdRepository aisleIdRepository;
	
	/**
	 * getAisleIds
	 * @return
	 */
	public List<AisleId> getAisleIds () {
		List<AisleId> aisleIdList =  aisleIdRepository.findAll();
		aisleIdList = aisleIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return aisleIdList;
	}
	
	/**
	 * getAisleId
	 * @param aisleId
	 * @return
	 */
	public AisleId getAisleId (String warehouseId, String aisleId, String floorId, String storageSectionId) {
		Optional<AisleId> dbAisleId = 
				aisleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								aisleId,
								floorId,
								storageSectionId,
								getLanguageId(),
								0L
								);
		if (dbAisleId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"aisleId - " + aisleId +
						"floorId - " + floorId +
						"storageSectionId - " + storageSectionId +
						" doesn't exist.");
			
		} 
		return dbAisleId.get();
	}
	
	/**
	 * createAisleId
	 * @param newAisleId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AisleId createAisleId (AddAisleId newAisleId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		AisleId dbAisleId = new AisleId();
		log.info("newAisleId : " + newAisleId);
		BeanUtils.copyProperties(newAisleId, dbAisleId, CommonUtils.getNullPropertyNames(newAisleId));
		dbAisleId.setCompanyCodeId(getCompanyCode());
		dbAisleId.setPlantId(getPlantId());
		dbAisleId.setDeletionIndicator(0L);
		dbAisleId.setCreatedBy(loginUserID);
		dbAisleId.setUpdatedBy(loginUserID);
		dbAisleId.setCreatedOn(new Date());
		dbAisleId.setUpdatedOn(new Date());
		return aisleIdRepository.save(dbAisleId);
	}
	
	/**
	 * updateAisleId
	 * @param loginUserID
	 * @param aisleId
	 * @param updateAisleId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AisleId updateAisleId (String warehouseId, String aisleId,String floorId, String storageSectionId, String loginUserID,
			UpdateAisleId updateAisleId) 
			throws IllegalAccessException, InvocationTargetException {
		AisleId dbAisleId = getAisleId(warehouseId, aisleId, floorId, storageSectionId);
		BeanUtils.copyProperties(updateAisleId, dbAisleId, CommonUtils.getNullPropertyNames(updateAisleId));
		dbAisleId.setUpdatedBy(loginUserID);
		dbAisleId.setUpdatedOn(new Date());
		return aisleIdRepository.save(dbAisleId);
	}
	
	/**
	 * deleteAisleId
	 * @param loginUserID 
	 * @param aisleId
	 */
	public void deleteAisleId (String warehouseId, String aisleId,String floorId, String storageSectionId, String loginUserID) {
		AisleId dbAisleId = getAisleId(warehouseId, aisleId, floorId, storageSectionId);
		if ( dbAisleId != null) {
			dbAisleId.setDeletionIndicator(1L);
			dbAisleId.setUpdatedBy(loginUserID);
			aisleIdRepository.save(dbAisleId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + aisleId);
		}
	}
}
