package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.shelfid.*;
import com.tekclover.wms.api.idmaster.repository.ShelfIdRepository;
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
public class ShelfIdService extends BaseService {
	
	@Autowired
	private ShelfIdRepository shelfIdRepository;
	
	/**
	 * getShelfIds
	 * @return
	 */
	public List<ShelfId> getShelfIds () {
		List<ShelfId> shelfIdList =  shelfIdRepository.findAll();
		shelfIdList = shelfIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return shelfIdList;
	}
	
	/**
	 * getShelfId
	 * @param shelfId
	 * @return
	 */
	public ShelfId getShelfId (String warehouseId, String aisleId, String shelfId,String spanId, String floorId, String storageSectionId) {
		Optional<ShelfId> dbShelfId = 
				shelfIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndShelfIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								aisleId,
								shelfId,
								spanId,
								floorId,
								storageSectionId,
								getLanguageId(),
								0L
								);
		if (dbShelfId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"aisleId - " + aisleId +
						"shelfId - " + shelfId +
						"spanId - " + spanId +
						"floorId - " + floorId +
						"storageSectionId - " + storageSectionId +
						" doesn't exist.");
			
		} 
		return dbShelfId.get();
	}
	
	/**
	 * createShelfId
	 * @param newShelfId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ShelfId createShelfId (AddShelfId newShelfId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		ShelfId dbShelfId = new ShelfId();
		log.info("newShelfId : " + newShelfId);
		BeanUtils.copyProperties(newShelfId, dbShelfId, CommonUtils.getNullPropertyNames(newShelfId));
		dbShelfId.setCompanyCodeId(getCompanyCode());
		dbShelfId.setPlantId(getPlantId());
		dbShelfId.setDeletionIndicator(0L);
		dbShelfId.setCreatedBy(loginUserID);
		dbShelfId.setUpdatedBy(loginUserID);
		dbShelfId.setCreatedOn(new Date());
		dbShelfId.setUpdatedOn(new Date());
		return shelfIdRepository.save(dbShelfId);
	}
	
	/**
	 * updateShelfId
	 * @param loginUserID
	 * @param shelfId
	 * @param updateShelfId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ShelfId updateShelfId (String warehouseId, String aisleId,String shelfId,String spanId,String floorId, String storageSectionId, String loginUserID,
			UpdateShelfId updateShelfId) 
			throws IllegalAccessException, InvocationTargetException {
		ShelfId dbShelfId = getShelfId(warehouseId, aisleId, shelfId,spanId, floorId, storageSectionId);
		BeanUtils.copyProperties(updateShelfId, dbShelfId, CommonUtils.getNullPropertyNames(updateShelfId));
		dbShelfId.setUpdatedBy(loginUserID);
		dbShelfId.setUpdatedOn(new Date());
		return shelfIdRepository.save(dbShelfId);
	}
	
	/**
	 * deleteShelfId
	 * @param loginUserID 
	 * @param shelfId
	 */
	public void deleteShelfId (String warehouseId,String aisleId, String shelfId,String spanId,String floorId, String storageSectionId, String loginUserID) {
		ShelfId dbShelfId = getShelfId(warehouseId, aisleId, shelfId,spanId, floorId, storageSectionId);
		if ( dbShelfId != null) {
			dbShelfId.setDeletionIndicator(1L);
			dbShelfId.setUpdatedBy(loginUserID);
			shelfIdRepository.save(dbShelfId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + shelfId);
		}
	}
}
