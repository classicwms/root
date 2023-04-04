package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.AddSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.SpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.UpdateSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.repository.SpecialStockIndicatorIdRepository;
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
public class SpecialStockIndicatorIdService extends BaseService {
	
	@Autowired
	private SpecialStockIndicatorIdRepository specialStockIndicatorIdRepository;
	
	/**
	 * getSpecialStockIndicatorIds
	 * @return
	 */
	public List<SpecialStockIndicatorId> getSpecialStockIndicatorIds () {
		List<SpecialStockIndicatorId> specialStockIndicatorIdList =  specialStockIndicatorIdRepository.findAll();
		specialStockIndicatorIdList = specialStockIndicatorIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return specialStockIndicatorIdList;
	}
	
	/**
	 * getSpecialStockIndicatorId
	 * @param specialStockIndicatorId
	 * @return
	 */
	public SpecialStockIndicatorId getSpecialStockIndicatorId (String warehouseId,String stockTypeId, String specialStockIndicatorId) {
		Optional<SpecialStockIndicatorId> dbSpecialStockIndicatorId =
				specialStockIndicatorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStockTypeIdAndSpecialStockIndicatorIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								stockTypeId,
								specialStockIndicatorId,
								getLanguageId(),
								0L
								);
		if (dbSpecialStockIndicatorId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"stockTypeId - " + stockTypeId +
						"specialStockIndicatorId - " + specialStockIndicatorId +
						" doesn't exist.");
			
		} 
		return dbSpecialStockIndicatorId.get();
	}
	
	/**
	 * createSpecialStockIndicatorId
	 * @param newSpecialStockIndicatorId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SpecialStockIndicatorId createSpecialStockIndicatorId (AddSpecialStockIndicatorId newSpecialStockIndicatorId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		SpecialStockIndicatorId dbSpecialStockIndicatorId = new SpecialStockIndicatorId();
		log.info("newSpecialStockIndicatorId : " + newSpecialStockIndicatorId);
		BeanUtils.copyProperties(newSpecialStockIndicatorId, dbSpecialStockIndicatorId, CommonUtils.getNullPropertyNames(newSpecialStockIndicatorId));
		dbSpecialStockIndicatorId.setCompanyCodeId(getCompanyCode());
		dbSpecialStockIndicatorId.setPlantId(getPlantId());
		dbSpecialStockIndicatorId.setDeletionIndicator(0L);
		dbSpecialStockIndicatorId.setCreatedBy(loginUserID);
		dbSpecialStockIndicatorId.setUpdatedBy(loginUserID);
		dbSpecialStockIndicatorId.setCreatedOn(new Date());
		dbSpecialStockIndicatorId.setUpdatedOn(new Date());
		return specialStockIndicatorIdRepository.save(dbSpecialStockIndicatorId);
	}
	
	/**
	 * updateSpecialStockIndicatorId
	 * @param loginUserID
	 * @param specialStockIndicatorId
	 * @param updateSpecialStockIndicatorId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SpecialStockIndicatorId updateSpecialStockIndicatorId (String warehouseId, String stockTypeId, String specialStockIndicatorId, String loginUserID,
			UpdateSpecialStockIndicatorId updateSpecialStockIndicatorId) 
			throws IllegalAccessException, InvocationTargetException {
		SpecialStockIndicatorId dbSpecialStockIndicatorId = getSpecialStockIndicatorId( warehouseId, stockTypeId,specialStockIndicatorId);
		BeanUtils.copyProperties(updateSpecialStockIndicatorId, dbSpecialStockIndicatorId, CommonUtils.getNullPropertyNames(updateSpecialStockIndicatorId));
		dbSpecialStockIndicatorId.setUpdatedBy(loginUserID);
		dbSpecialStockIndicatorId.setUpdatedOn(new Date());
		return specialStockIndicatorIdRepository.save(dbSpecialStockIndicatorId);
	}
	
	/**
	 * deleteSpecialStockIndicatorId
	 * @param loginUserID 
	 * @param specialStockIndicatorId
	 */
	public void deleteSpecialStockIndicatorId (String warehouseId, String stockTypeId, String specialStockIndicatorId, String loginUserID) {
		SpecialStockIndicatorId dbSpecialStockIndicatorId = getSpecialStockIndicatorId( warehouseId, stockTypeId, specialStockIndicatorId);
		if ( dbSpecialStockIndicatorId != null) {
			dbSpecialStockIndicatorId.setDeletionIndicator(1L);
			dbSpecialStockIndicatorId.setUpdatedBy(loginUserID);
			specialStockIndicatorIdRepository.save(dbSpecialStockIndicatorId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + specialStockIndicatorId);
		}
	}
}
