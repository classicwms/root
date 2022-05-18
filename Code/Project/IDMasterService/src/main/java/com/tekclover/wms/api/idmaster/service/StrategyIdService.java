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
import com.tekclover.wms.api.idmaster.model.strategyid.AddStrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.StrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.UpdateStrategyId;
import com.tekclover.wms.api.idmaster.repository.StrategyIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StrategyIdService extends BaseService {
	
	@Autowired
	private StrategyIdRepository strategyIdRepository;
	
	/**
	 * getStrategyIds
	 * @return
	 */
	public List<StrategyId> getStrategyIds () {
		List<StrategyId> strategyIdList =  strategyIdRepository.findAll();
		strategyIdList = strategyIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return strategyIdList;
	}
	
	/**
	 * getStrategyId
	 * @param strategyNo
	 * @return
	 */
	public StrategyId getStrategyId (String warehouseId, Long strategyTypeId, String strategyNo) {
		Optional<StrategyId> dbStrategyId = 
				strategyIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndStrategyNoAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								strategyTypeId,
								strategyNo,
								getLanguageId(),
								0L
								);
		if (dbStrategyId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"strategyTypeId - " + strategyTypeId +
						"strategyNo - " + strategyNo +
						" doesn't exist.");
			
		} 
		return dbStrategyId.get();
	}
	
//	/**
//	 * 
//	 * @param searchStrategyId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<StrategyId> findStrategyId(SearchStrategyId searchStrategyId) 
//			throws ParseException {
//		StrategyIdSpecification spec = new StrategyIdSpecification(searchStrategyId);
//		List<StrategyId> results = strategyIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createStrategyId
	 * @param newStrategyId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StrategyId createStrategyId (AddStrategyId newStrategyId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		StrategyId dbStrategyId = new StrategyId();
		log.info("newStrategyId : " + newStrategyId);
		BeanUtils.copyProperties(newStrategyId, dbStrategyId, CommonUtils.getNullPropertyNames(newStrategyId));
		dbStrategyId.setCompanyCodeId(getCompanyCode());
		dbStrategyId.setPlantId(getPlantId());
		dbStrategyId.setDeletionIndicator(0L);
		dbStrategyId.setCreatedBy(loginUserID);
		dbStrategyId.setUpdatedBy(loginUserID);
		dbStrategyId.setCreatedOn(new Date());
		dbStrategyId.setUpdatedOn(new Date());
		return strategyIdRepository.save(dbStrategyId);
	}
	
	/**
	 * updateStrategyId
	 * @param loginUserId 
	 * @param strategyNo
	 * @param updateStrategyId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StrategyId updateStrategyId ( String warehouseId, Long strategyTypeId, String strategyNo, String loginUserID, 
			UpdateStrategyId updateStrategyId) 
			throws IllegalAccessException, InvocationTargetException {
		StrategyId dbStrategyId = getStrategyId(warehouseId, strategyTypeId, strategyNo);
		BeanUtils.copyProperties(updateStrategyId, dbStrategyId, CommonUtils.getNullPropertyNames(updateStrategyId));
		dbStrategyId.setUpdatedBy(loginUserID);
		dbStrategyId.setUpdatedOn(new Date());
		return strategyIdRepository.save(dbStrategyId);
	}
	
	/**
	 * deleteStrategyId
	 * @param loginUserID 
	 * @param strategyNo
	 */
	public void deleteStrategyId ( String warehouseId, Long strategyTypeId, String strategyNo, String loginUserID) {
		StrategyId dbStrategyId = getStrategyId(warehouseId, strategyTypeId, strategyNo);
		if ( dbStrategyId != null) {
			dbStrategyId.setDeletionIndicator(1L);
			dbStrategyId.setUpdatedBy(loginUserID);
			strategyIdRepository.save(dbStrategyId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + strategyNo);
		}
	}
}
