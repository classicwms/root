package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.batchserial.AddBatchSerial;
import com.tekclover.wms.api.enterprise.model.batchserial.BatchSerial;
import com.tekclover.wms.api.enterprise.model.batchserial.SearchBatchSerial;
import com.tekclover.wms.api.enterprise.model.batchserial.UpdateBatchSerial;
import com.tekclover.wms.api.enterprise.repository.BatchSerialRepository;
import com.tekclover.wms.api.enterprise.repository.specification.BatchSerialSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BatchSerialService extends BaseService {
	
	@Autowired
	private BatchSerialRepository batchSerialRepository;
	
	/**
	 * getBatchSerials
	 * @return
	 */
	public List<BatchSerial> getBatchSerials () {
		List<BatchSerial> batchserialList = batchSerialRepository.findAll();
		log.info("batchserialList : " + batchserialList);
		batchserialList = batchserialList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return batchserialList;
	}
	
	/**
	 * getBatchSerial
	 * @param storageMethod
	 * @return
	 */
	public BatchSerial getBatchSerial (String storageMethod) {
		BatchSerial batchserial = batchSerialRepository.findByStorageMethod(storageMethod).orElse(null);
		if (batchserial != null && batchserial.getDeletionIndicator() != null && batchserial.getDeletionIndicator() == 0) {
			return batchserial;
		} else {
			throw new BadRequestException("The given BatchSerial ID : " + storageMethod + " doesn't exist.");
		}
	}
	
	/**
	 * findBatchSerial
	 * @param searchBatchSerial
	 * @return
	 * @throws ParseException
	 */
	public List<BatchSerial> findBatchSerial(SearchBatchSerial searchBatchSerial) throws Exception {
		if (searchBatchSerial.getStartCreatedOn() != null && searchBatchSerial.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchBatchSerial.getStartCreatedOn(), searchBatchSerial.getEndCreatedOn());
			searchBatchSerial.setStartCreatedOn(dates[0]);
			searchBatchSerial.setEndCreatedOn(dates[1]);
		}
		
		BatchSerialSpecification spec = new BatchSerialSpecification(searchBatchSerial);
		List<BatchSerial> results = batchSerialRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createBatchSerial
	 * @param newBatchSerial
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BatchSerial createBatchSerial (AddBatchSerial newBatchSerial, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BatchSerial dbBatchSerial = new BatchSerial();
		BeanUtils.copyProperties(newBatchSerial, dbBatchSerial, CommonUtils.getNullPropertyNames(newBatchSerial));
		
		dbBatchSerial.setCompanyId(getCompanyCode());
		dbBatchSerial.setLanguageId(getLanguageId());
		dbBatchSerial.setPlantId(getPlantId());
		
		dbBatchSerial.setDeletionIndicator(0L);
		dbBatchSerial.setCreatedBy(loginUserID);
		dbBatchSerial.setUpdatedBy(loginUserID);
		dbBatchSerial.setCreatedOn(new Date());
		dbBatchSerial.setUpdatedOn(new Date());
		return batchSerialRepository.save(dbBatchSerial);
	}
	
	/**
	 * updateBatchSerial
	 * @param batchserialCode
	 * @param updateBatchSerial
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BatchSerial updateBatchSerial (String storageMethod, UpdateBatchSerial updateBatchSerial, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		BatchSerial dbBatchSerial = getBatchSerial(storageMethod);
		BeanUtils.copyProperties(updateBatchSerial, dbBatchSerial, CommonUtils.getNullPropertyNames(updateBatchSerial));
		dbBatchSerial.setUpdatedBy(loginUserID);
		dbBatchSerial.setUpdatedOn(new Date());
		return batchSerialRepository.save(dbBatchSerial);
	}
	
	/**
	 * deleteBatchSerial
	 * @param batchserialCode
	 */
	public void deleteBatchSerial (String storageMethod, String loginUserID) {
		BatchSerial batchSerial = getBatchSerial(storageMethod);
		if ( batchSerial != null) {
			batchSerial.setDeletionIndicator (1L);
			batchSerial.setUpdatedBy(loginUserID);
			batchSerial.setUpdatedOn(new Date());
			batchSerialRepository.save(batchSerial);
		} else {
			throw new EntityNotFoundException(String.valueOf(storageMethod));
		}
	}
}
