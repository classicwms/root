package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.spanid.*;
import com.tekclover.wms.api.idmaster.repository.SpanIdRepository;
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
public class SpanIdService extends BaseService {
	
	@Autowired
	private SpanIdRepository spanIdRepository;
	
	/**
	 * getSpanIds
	 * @return
	 */
	public List<SpanId> getSpanIds () {
		List<SpanId> spanIdList =  spanIdRepository.findAll();
		spanIdList = spanIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return spanIdList;
	}
	
	/**
	 * getSpanId
	 * @param spanId
	 * @return
	 */
	public SpanId getSpanId (String warehouseId, String aisleId, String spanId, String floorId, String storageSectionId) {
		Optional<SpanId> dbSpanId = 
				spanIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								aisleId,
								spanId,
								floorId,
								storageSectionId,
								getLanguageId(),
								0L
								);
		if (dbSpanId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"aisleId - " + aisleId +
						"spanId - " + spanId +
						"floorId - " + floorId +
						"storageSectionId - " + storageSectionId +
						" doesn't exist.");
			
		} 
		return dbSpanId.get();
	}
	
	/**
	 * createSpanId
	 * @param newSpanId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SpanId createSpanId (AddSpanId newSpanId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		SpanId dbSpanId = new SpanId();
		log.info("newSpanId : " + newSpanId);
		BeanUtils.copyProperties(newSpanId, dbSpanId, CommonUtils.getNullPropertyNames(newSpanId));
		dbSpanId.setCompanyCodeId(getCompanyCode());
		dbSpanId.setPlantId(getPlantId());
		dbSpanId.setDeletionIndicator(0L);
		dbSpanId.setCreatedBy(loginUserID);
		dbSpanId.setUpdatedBy(loginUserID);
		dbSpanId.setCreatedOn(new Date());
		dbSpanId.setUpdatedOn(new Date());
		return spanIdRepository.save(dbSpanId);
	}
	
	/**
	 * updateSpanId
	 * @param loginUserID
	 * @param spanId
	 * @param updateSpanId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SpanId updateSpanId (String warehouseId, String aisleId,String spanId,String floorId, String storageSectionId, String loginUserID,
			UpdateSpanId updateSpanId) 
			throws IllegalAccessException, InvocationTargetException {
		SpanId dbSpanId = getSpanId(warehouseId, aisleId, spanId, floorId, storageSectionId);
		BeanUtils.copyProperties(updateSpanId, dbSpanId, CommonUtils.getNullPropertyNames(updateSpanId));
		dbSpanId.setUpdatedBy(loginUserID);
		dbSpanId.setUpdatedOn(new Date());
		return spanIdRepository.save(dbSpanId);
	}
	
	/**
	 * deleteSpanId
	 * @param loginUserID 
	 * @param spanId
	 */
	public void deleteSpanId (String warehouseId,String aisleId, String spanId,String floorId, String storageSectionId, String loginUserID) {
		SpanId dbSpanId = getSpanId(warehouseId, aisleId, spanId, floorId, storageSectionId);
		if ( dbSpanId != null) {
			dbSpanId.setDeletionIndicator(1L);
			dbSpanId.setUpdatedBy(loginUserID);
			spanIdRepository.save(dbSpanId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + spanId);
		}
	}
}
