package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.AddDecimalNotationId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.DecimalNotationId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.UpdateDecimalNotationId;
import com.tekclover.wms.api.idmaster.repository.DecimalNotationIdRepository;
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
public class DecimalNotationIdService extends BaseService {
	
	@Autowired
	private DecimalNotationIdRepository decimalNotationIdRepository;
	
	/**
	 * getDecimalNotationIds
	 * @return
	 */
	public List<DecimalNotationId> getDecimalNotationIds () {
		List<DecimalNotationId> decimalNotationIdList =  decimalNotationIdRepository.findAll();
		decimalNotationIdList = decimalNotationIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return decimalNotationIdList;
	}
	
	/**
	 * getDecimalNotationId
	 * @param decimalNotationId
	 * @return
	 */
	public DecimalNotationId getDecimalNotationId (String warehouseId, String decimalNotationId) {
		Optional<DecimalNotationId> dbDecimalNotationId = 
				decimalNotationIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDecimalNotationIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								decimalNotationId,
								getLanguageId(),
								0L
								);
		if (dbDecimalNotationId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"decimalNotationId - " + decimalNotationId +
						" doesn't exist.");
			
		} 
		return dbDecimalNotationId.get();
	}
	
	/**
	 * createDecimalNotationId
	 * @param newDecimalNotationId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DecimalNotationId createDecimalNotationId (AddDecimalNotationId newDecimalNotationId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		DecimalNotationId dbDecimalNotationId = new DecimalNotationId();
		log.info("newDecimalNotationId : " + newDecimalNotationId);
		BeanUtils.copyProperties(newDecimalNotationId, dbDecimalNotationId, CommonUtils.getNullPropertyNames(newDecimalNotationId));
		dbDecimalNotationId.setCompanyCodeId(getCompanyCode());
		dbDecimalNotationId.setPlantId(getPlantId());
		dbDecimalNotationId.setDeletionIndicator(0L);
		dbDecimalNotationId.setCreatedBy(loginUserID);
		dbDecimalNotationId.setUpdatedBy(loginUserID);
		dbDecimalNotationId.setCreatedOn(new Date());
		dbDecimalNotationId.setUpdatedOn(new Date());
		return decimalNotationIdRepository.save(dbDecimalNotationId);
	}
	
	/**
	 * updateDecimalNotationId
	 * @param loginUserID
	 * @param decimalNotationId
	 * @param updateDecimalNotationId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DecimalNotationId updateDecimalNotationId (String warehouseId, String decimalNotationId, String loginUserID,
			UpdateDecimalNotationId updateDecimalNotationId) 
			throws IllegalAccessException, InvocationTargetException {
		DecimalNotationId dbDecimalNotationId = getDecimalNotationId( warehouseId, decimalNotationId);
		BeanUtils.copyProperties(updateDecimalNotationId, dbDecimalNotationId, CommonUtils.getNullPropertyNames(updateDecimalNotationId));
		dbDecimalNotationId.setUpdatedBy(loginUserID);
		dbDecimalNotationId.setUpdatedOn(new Date());
		return decimalNotationIdRepository.save(dbDecimalNotationId);
	}
	
	/**
	 * deleteDecimalNotationId
	 * @param loginUserID 
	 * @param decimalNotationId
	 */
	public void deleteDecimalNotationId (String warehouseId, String decimalNotationId, String loginUserID) {
		DecimalNotationId dbDecimalNotationId = getDecimalNotationId( warehouseId, decimalNotationId);
		if ( dbDecimalNotationId != null) {
			dbDecimalNotationId.setDeletionIndicator(1L);
			dbDecimalNotationId.setUpdatedBy(loginUserID);
			decimalNotationIdRepository.save(dbDecimalNotationId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + decimalNotationId);
		}
	}
}
