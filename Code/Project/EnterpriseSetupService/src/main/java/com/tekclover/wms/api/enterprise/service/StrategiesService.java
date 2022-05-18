package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.strategies.AddStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.SearchStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.Strategies;
import com.tekclover.wms.api.enterprise.model.strategies.UpdateStrategies;
import com.tekclover.wms.api.enterprise.repository.StrategiesRepository;
import com.tekclover.wms.api.enterprise.repository.specification.StrategiesSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StrategiesService extends BaseService {
	
	@Autowired
	private StrategiesRepository strategiesRepository;
	
	/**
	 * getStrategiess
	 * @return
	 */
	public List<Strategies> getStrategiess () {
		List<Strategies> strategiesList = strategiesRepository.findAll();
		log.info("strategiesList : " + strategiesList);
		strategiesList = strategiesList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return strategiesList;
	}
	
	/**
	 * getStrategies
	 * @param warehouseId
	 * @param sequenceIndicator
	 * @param strategyTypeId
	 * @param strategyNo
	 * @param priority
	 * @return
	 */
	public Strategies getStrategies (String warehouseId, Long strategyTypeId, Long sequenceIndicator, 
			String strategyNo, Long priority) {
		Optional<Strategies> strategies = 
				strategiesRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndStrategyNoAndPriorityAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, strategyTypeId, sequenceIndicator, strategyNo, priority, 0L);
		if (strategies.isEmpty()) {
			throw new BadRequestException("The given Strategies Id : " + strategyTypeId + " doesn't exist.");
		} 
		return strategies.get();
	}
	
	/**
	 * findStrategies
	 * @param searchStrategies
	 * @return
	 * @throws ParseException
	 */
	public List<Strategies> findStrategies(SearchStrategies searchStrategies) throws Exception {
		if (searchStrategies.getStartCreatedOn() != null && searchStrategies.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchStrategies.getStartCreatedOn(), searchStrategies.getEndCreatedOn());
			searchStrategies.setStartCreatedOn(dates[0]);
			searchStrategies.setEndCreatedOn(dates[1]);
		}
		
		StrategiesSpecification spec = new StrategiesSpecification(searchStrategies);
		List<Strategies> results = strategiesRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createStrategies
	 * @param newStrategies
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Strategies createStrategies (AddStrategies newStrategies, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<Strategies> optStrategies = 
				strategiesRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndStrategyNoAndPriorityAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newStrategies.getWarehouseId(),
						newStrategies.getStrategyTypeId(),
						newStrategies.getSequenceIndicator(),
						newStrategies.getStrategyNo(),
						newStrategies.getPriority(),
						0L);
		if (!optStrategies.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		Strategies dbStrategies = new Strategies();
		BeanUtils.copyProperties(newStrategies, dbStrategies, CommonUtils.getNullPropertyNames(newStrategies));
		dbStrategies.setLanguageId(getLanguageId());
		dbStrategies.setCompanyId(getCompanyCode());
		dbStrategies.setPlantId(getPlantId());
		
		dbStrategies.setDeletionIndicator(0L);
		dbStrategies.setCreatedBy(loginUserID);
		dbStrategies.setUpdatedBy(loginUserID);
		dbStrategies.setCreatedOn(new Date());
		dbStrategies.setUpdatedOn(new Date());
		return strategiesRepository.save(dbStrategies);
	}
	
	/**
	 * updateStrategies
	 * @param strategiesCode
	 * @param updateStrategies
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Strategies updateStrategies (String warehouseId, Long strategyTypeId, Long sequenceIndicator, 
			String strategyNo, Long priority, UpdateStrategies updateStrategies, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Strategies dbStrategies = getStrategies(warehouseId, strategyTypeId, sequenceIndicator, strategyNo, priority);
		BeanUtils.copyProperties(updateStrategies, dbStrategies, CommonUtils.getNullPropertyNames(updateStrategies));
		dbStrategies.setUpdatedBy(loginUserID);
		dbStrategies.setUpdatedOn(new Date());
		return strategiesRepository.save(dbStrategies);
	}
	
	/**
	 * deleteStrategies
	 * @param strategiesCode
	 */
	public void deleteStrategies (String warehouseId, Long strategyTypeId, Long sequenceIndicator, String strategyNo, Long priority, String loginUserID) {
		Strategies strategies = getStrategies(warehouseId, strategyTypeId, sequenceIndicator, strategyNo, priority);
		if ( strategies != null) {
			strategies.setDeletionIndicator (1L);
			strategies.setUpdatedBy(loginUserID);
			strategies.setUpdatedOn(new Date());
			strategiesRepository.save(strategies);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + strategyTypeId);
		}
	}
}
