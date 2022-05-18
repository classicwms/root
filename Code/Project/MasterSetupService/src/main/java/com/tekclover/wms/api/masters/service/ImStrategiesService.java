package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.imstrategies.AddImStrategies;
import com.tekclover.wms.api.masters.model.imstrategies.ImStrategies;
import com.tekclover.wms.api.masters.model.imstrategies.UpdateImStrategies;
import com.tekclover.wms.api.masters.repository.ImStrategiesRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImStrategiesService {
	
	@Autowired
	private ImStrategiesRepository imstrategiesRepository;
	
	/**
	 * getImStrategiess
	 * @return
	 */
	public List<ImStrategies> getImStrategiess () {
		List<ImStrategies> imstrategiesList = imstrategiesRepository.findAll();
		log.info("imstrategiesList : " + imstrategiesList);
		imstrategiesList = imstrategiesList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return imstrategiesList;
	}
	
	/**
	 * getImStrategies
	 * @param strategeyTypeId
	 * @return
	 */
	public ImStrategies getImStrategies (String strategeyTypeId) {
		ImStrategies imstrategies = imstrategiesRepository.findByStrategeyTypeId(strategeyTypeId).orElse(null);
		if (imstrategies != null && imstrategies.getDeletionIndicator() != null && imstrategies.getDeletionIndicator() == 0) {
			return imstrategies;
		} else {
			throw new BadRequestException("The given ImStrategies ID : " + strategeyTypeId + " doesn't exist.");
		}
	}
	
	/**
	 * findImStrategies
	 * @param searchImStrategies
	 * @return
	 * @throws ParseException
	 */
//	public List<ImStrategies> findImStrategies(SearchImStrategies searchImStrategies) throws ParseException {
//		if (searchImStrategies.getStartCreatedOn() != null && searchImStrategies.getEndCreatedOn() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImStrategies.getStartCreatedOn(), searchImStrategies.getEndCreatedOn());
//			searchImStrategies.setStartCreatedOn(dates[0]);
//			searchImStrategies.setEndCreatedOn(dates[1]);
//		}
//		
//		ImStrategiesSpecification spec = new ImStrategiesSpecification(searchImStrategies);
//		List<ImStrategies> results = imstrategiesRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createImStrategies
	 * @param newImStrategies
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImStrategies createImStrategies (AddImStrategies newImStrategies, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImStrategies dbImStrategies = new ImStrategies();
		BeanUtils.copyProperties(newImStrategies, dbImStrategies, CommonUtils.getNullPropertyNames(newImStrategies));
		dbImStrategies.setDeletionIndicator(0L);
		dbImStrategies.setCreatedBy(loginUserID);
		dbImStrategies.setUpdatedBy(loginUserID);
		dbImStrategies.setCreatedOn(new Date());
		dbImStrategies.setUpdatedOn(new Date());
		return imstrategiesRepository.save(dbImStrategies);
	}
	
	/**
	 * updateImStrategies
	 * @param imstrategies
	 * @param updateImStrategies
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImStrategies updateImStrategies (String strategeyTypeId, UpdateImStrategies updateImStrategies, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImStrategies dbImStrategies = getImStrategies(strategeyTypeId);
		BeanUtils.copyProperties(updateImStrategies, dbImStrategies, CommonUtils.getNullPropertyNames(updateImStrategies));
		dbImStrategies.setUpdatedBy(loginUserID);
		dbImStrategies.setUpdatedOn(new Date());
		return imstrategiesRepository.save(dbImStrategies);
	}
	
	/**
	 * deleteImStrategies
	 * @param imstrategies
	 */
	public void deleteImStrategies (String strategeyTypeId, String loginUserID) {
		ImStrategies imstrategies = getImStrategies(strategeyTypeId);
		if ( imstrategies != null) {
			imstrategies.setDeletionIndicator (1L);
			imstrategies.setUpdatedBy(loginUserID);
			imstrategies.setUpdatedOn(new Date());
			imstrategiesRepository.save(imstrategies);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + strategeyTypeId);
		}
	}
}
