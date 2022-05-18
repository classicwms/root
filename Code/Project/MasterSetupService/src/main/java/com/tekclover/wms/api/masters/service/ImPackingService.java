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
import com.tekclover.wms.api.masters.model.impacking.AddImPacking;
import com.tekclover.wms.api.masters.model.impacking.ImPacking;
import com.tekclover.wms.api.masters.model.impacking.UpdateImPacking;
import com.tekclover.wms.api.masters.repository.ImPackingRepository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImPackingService {
	
	@Autowired
	private ImPackingRepository impackingRepository;
	
	/**
	 * getImPackings
	 * @return
	 */
	public List<ImPacking> getImPackings () {
		List<ImPacking> impackingList = impackingRepository.findAll();
		log.info("impackingList : " + impackingList);
		impackingList = impackingList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return impackingList;
	}
	
	/**
	 * getImPacking
	 * @param packingMaterialNo
	 * @return
	 */
	public ImPacking getImPacking (String packingMaterialNo) {
		ImPacking impacking = impackingRepository.findByPackingMaterialNo(packingMaterialNo).orElse(null);
		if (impacking != null && impacking.getDeletionIndicator() != null && impacking.getDeletionIndicator() == 0) {
			return impacking;
		} else {
			throw new BadRequestException("The given ImPacking ID : " + packingMaterialNo + " doesn't exist.");
		}
	}
	
	/**
	 * findImPacking
	 * @param searchImPacking
	 * @return
	 * @throws ParseException
	 */
//	public List<ImPacking> findImPacking(SearchImPacking searchImPacking) throws ParseException {
//		if (searchImPacking.getStartCreatedOn() != null && searchImPacking.getEndCreatedOn() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImPacking.getStartCreatedOn(), searchImPacking.getEndCreatedOn());
//			searchImPacking.setStartCreatedOn(dates[0]);
//			searchImPacking.setEndCreatedOn(dates[1]);
//		}
//		
//		ImPackingSpecification spec = new ImPackingSpecification(searchImPacking);
//		List<ImPacking> results = impackingRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createImPacking
	 * @param newImPacking
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImPacking createImPacking (AddImPacking newImPacking, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImPacking dbImPacking = new ImPacking();
		BeanUtils.copyProperties(newImPacking, dbImPacking, CommonUtils.getNullPropertyNames(newImPacking));
		dbImPacking.setDeletionIndicator(0L);
		dbImPacking.setCreatedBy(loginUserID);
		dbImPacking.setUpdatedBy(loginUserID);
		dbImPacking.setCreatedOn(new Date());
		dbImPacking.setUpdatedOn(new Date());
		return impackingRepository.save(dbImPacking);
	}
	
	/**
	 * updateImPacking
	 * @param impacking
	 * @param updateImPacking
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImPacking updateImPacking (String packingMaterialNo, UpdateImPacking updateImPacking, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImPacking dbImPacking = getImPacking(packingMaterialNo);
		BeanUtils.copyProperties(updateImPacking, dbImPacking, CommonUtils.getNullPropertyNames(updateImPacking));
		dbImPacking.setUpdatedBy(loginUserID);
		dbImPacking.setUpdatedOn(new Date());
		return impackingRepository.save(dbImPacking);
	}
	
	/**
	 * deleteImPacking
	 * @param impacking
	 */
	public void deleteImPacking (String packingMaterialNo, String loginUserID) {
		ImPacking impacking = getImPacking(packingMaterialNo);
		if ( impacking != null) {
			impacking.setDeletionIndicator (1L);
			impacking.setUpdatedBy(loginUserID);
			impacking.setUpdatedOn(new Date());
			impackingRepository.save(impacking);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + packingMaterialNo);
		}
	}
}
