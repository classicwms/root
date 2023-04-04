package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.uomid.AddUomId;
import com.tekclover.wms.api.idmaster.model.uomid.UomId;
import com.tekclover.wms.api.idmaster.model.uomid.UpdateUomId;
import com.tekclover.wms.api.idmaster.repository.UomIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UomIdService extends BaseService {
	
	@Autowired
	private UomIdRepository uomIdRepository;
	
	/**
	 * getUomIds
	 * @return
	 */
	public List<UomId> getUomIds () {
		List<UomId> uomIdList =  uomIdRepository.findAll();
		uomIdList = uomIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return uomIdList;
	}
	
	/**
	 * getUomId
	 * @param uomId
	 * @return
	 */
	public UomId getUomId (String uomId) {
		Optional<UomId> dbUomId = 
				uomIdRepository.findByCompanyCodeIdAndUomIdAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								uomId,
								getLanguageId(),
								0L
								);
		if (dbUomId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"uomId - " + uomId +
						" doesn't exist.");
			
		} 
		return dbUomId.get();
	}
	
//	/**
//	 * 
//	 * @param searchUomId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<UomId> findUomId(SearchUomId searchUomId) 
//			throws ParseException {
//		UomIdSpecification spec = new UomIdSpecification(searchUomId);
//		List<UomId> results = uomIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createUomId
	 * @param newUomId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UomId createUomId (AddUomId newUomId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		UomId dbUomId = new UomId();
		log.info("newUomId : " + newUomId);
		BeanUtils.copyProperties(newUomId, dbUomId, CommonUtils.getNullPropertyNames(newUomId));
		dbUomId.setCompanyCodeId(getCompanyCode());
		dbUomId.setDeletionIndicator(0L);
		dbUomId.setCreatedBy(loginUserID);
		dbUomId.setUpdatedBy(loginUserID);
		dbUomId.setCreatedOn(new Date());
		dbUomId.setUpdatedOn(new Date());
		return uomIdRepository.save(dbUomId);
	}
	
	/**
	 * updateUomId
	 * @param loginUserId 
	 * @param uomId
	 * @param updateUomId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UomId updateUomId (String uomId, String loginUserID, 
			UpdateUomId updateUomId) 
			throws IllegalAccessException, InvocationTargetException {
		UomId dbUomId = getUomId(uomId);
		BeanUtils.copyProperties(updateUomId, dbUomId, CommonUtils.getNullPropertyNames(updateUomId));
		dbUomId.setUpdatedBy(loginUserID);
		dbUomId.setUpdatedOn(new Date());
		return uomIdRepository.save(dbUomId);
	}
	
	/**
	 * deleteUomId
	 * @param loginUserID 
	 * @param uomId
	 */
	public void deleteUomId (String uomId, String loginUserID) {
		UomId dbUomId = getUomId(uomId);
		if ( uomId != null) {
			dbUomId.setDeletionIndicator(1L);
			dbUomId.setUpdatedBy(loginUserID);
			uomIdRepository.save(dbUomId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + uomId);
		}
	}
}
