package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.dateformatid.AddDateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.DateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.UpdateDateFormatId;
import com.tekclover.wms.api.idmaster.repository.DateFormatIdRepository;
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
public class DateFormatIdService extends BaseService {
	
	@Autowired
	private DateFormatIdRepository dateFormatIdRepository;
	
	/**
	 * getDateFormatIds
	 * @return
	 */
	public List<DateFormatId> getDateFormatIds () {
		List<DateFormatId> dateFormatIdList =  dateFormatIdRepository.findAll();
		dateFormatIdList = dateFormatIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return dateFormatIdList;
	}
	
	/**
	 * getDateFormatId
	 * @param dateFormatId
	 * @return
	 */
	public DateFormatId getDateFormatId (String warehouseId, String dateFormatId) {
		Optional<DateFormatId> dbDateFormatId = 
				dateFormatIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDateFormatIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								dateFormatId,
								getLanguageId(),
								0L
								);
		if (dbDateFormatId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"dateFormatId - " + dateFormatId +
						" doesn't exist.");
			
		} 
		return dbDateFormatId.get();
	}
	
	/**
	 * createDateFormatId
	 * @param newDateFormatId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DateFormatId createDateFormatId (AddDateFormatId newDateFormatId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		DateFormatId dbDateFormatId = new DateFormatId();
		log.info("newDateFormatId : " + newDateFormatId);
		BeanUtils.copyProperties(newDateFormatId, dbDateFormatId, CommonUtils.getNullPropertyNames(newDateFormatId));
		dbDateFormatId.setCompanyCodeId(getCompanyCode());
		dbDateFormatId.setPlantId(getPlantId());
		dbDateFormatId.setDeletionIndicator(0L);
		dbDateFormatId.setCreatedBy(loginUserID);
		dbDateFormatId.setUpdatedBy(loginUserID);
		dbDateFormatId.setCreatedOn(new Date());
		dbDateFormatId.setUpdatedOn(new Date());
		return dateFormatIdRepository.save(dbDateFormatId);
	}
	
	/**
	 * updateDateFormatId
	 * @param loginUserID
	 * @param dateFormatId
	 * @param updateDateFormatId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DateFormatId updateDateFormatId (String warehouseId, String dateFormatId, String loginUserID,
			UpdateDateFormatId updateDateFormatId) 
			throws IllegalAccessException, InvocationTargetException {
		DateFormatId dbDateFormatId = getDateFormatId( warehouseId, dateFormatId);
		BeanUtils.copyProperties(updateDateFormatId, dbDateFormatId, CommonUtils.getNullPropertyNames(updateDateFormatId));
		dbDateFormatId.setUpdatedBy(loginUserID);
		dbDateFormatId.setUpdatedOn(new Date());
		return dateFormatIdRepository.save(dbDateFormatId);
	}
	
	/**
	 * deleteDateFormatId
	 * @param loginUserID 
	 * @param dateFormatId
	 */
	public void deleteDateFormatId (String warehouseId, String dateFormatId, String loginUserID) {
		DateFormatId dbDateFormatId = getDateFormatId( warehouseId, dateFormatId);
		if ( dbDateFormatId != null) {
			dbDateFormatId.setDeletionIndicator(1L);
			dbDateFormatId.setUpdatedBy(loginUserID);
			dateFormatIdRepository.save(dbDateFormatId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + dateFormatId);
		}
	}
}
