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
import com.tekclover.wms.api.masters.model.imbasicdata2.AddImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.ImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.UpdateImBasicData2;
import com.tekclover.wms.api.masters.repository.ImBasicData2Repository;
import com.tekclover.wms.api.masters.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImBasicData2Service {
	
	@Autowired
	private ImBasicData2Repository imbasicdata2Repository;
	
	/**
	 * getImBasicData2s
	 * @return
	 */
	public List<ImBasicData2> getImBasicData2s () {
		List<ImBasicData2> imbasicdata2List = imbasicdata2Repository.findAll();
		log.info("imbasicdata2List : " + imbasicdata2List);
		imbasicdata2List = imbasicdata2List.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return imbasicdata2List;
	}
	
	/**
	 * getImBasicData2
	 * @param itemCode
	 * @return
	 */
	public ImBasicData2 getImBasicData2 (String itemCode) {
		ImBasicData2 imbasicdata2 = imbasicdata2Repository.findByItemCode(itemCode).orElse(null);
		if (imbasicdata2 != null && imbasicdata2.getDeletionIndicator() != null && imbasicdata2.getDeletionIndicator() == 0) {
			return imbasicdata2;
		} else {
			throw new BadRequestException("The given ImBasicData2 ID : " + itemCode + " doesn't exist.");
		}
	}
	
	/**
	 * findImBasicData2
	 * @param searchImBasicData2
	 * @return
	 * @throws ParseException
	 */
//	public List<ImBasicData2> findImBasicData2(SearchImBasicData2 searchImBasicData2) throws ParseException {
//		if (searchImBasicData2.getStartCreatedOn() != null && searchImBasicData2.getEndCreatedOn() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData2.getStartCreatedOn(), searchImBasicData2.getEndCreatedOn());
//			searchImBasicData2.setStartCreatedOn(dates[0]);
//			searchImBasicData2.setEndCreatedOn(dates[1]);
//		}
//		
//		ImBasicData2Specification spec = new ImBasicData2Specification(searchImBasicData2);
//		List<ImBasicData2> results = imbasicdata2Repository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createImBasicData2
	 * @param newImBasicData2
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImBasicData2 createImBasicData2 (AddImBasicData2 newImBasicData2, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImBasicData2 dbImBasicData2 = new ImBasicData2();
		BeanUtils.copyProperties(newImBasicData2, dbImBasicData2, CommonUtils.getNullPropertyNames(newImBasicData2));
		dbImBasicData2.setDeletionIndicator(0L);
		dbImBasicData2.setCreatedBy(loginUserID);
		dbImBasicData2.setUpdatedBy(loginUserID);
		dbImBasicData2.setCreatedOn(new Date());
		dbImBasicData2.setUpdatedOn(new Date());
		return imbasicdata2Repository.save(dbImBasicData2);
	}
	
	/**
	 * updateImBasicData2
	 * @param imbasicdata2
	 * @param updateImBasicData2
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImBasicData2 updateImBasicData2 (String itemCode, UpdateImBasicData2 updateImBasicData2, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImBasicData2 dbImBasicData2 = getImBasicData2(itemCode);
		BeanUtils.copyProperties(updateImBasicData2, dbImBasicData2, CommonUtils.getNullPropertyNames(updateImBasicData2));
		dbImBasicData2.setUpdatedBy(loginUserID);
		dbImBasicData2.setUpdatedOn(new Date());
		return imbasicdata2Repository.save(dbImBasicData2);
	}
	
	/**
	 * deleteImBasicData2
	 * @param imbasicdata2
	 */
	public void deleteImBasicData2 (String itemCode, String loginUserID) {
		ImBasicData2 imbasicdata2 = getImBasicData2(itemCode);
		if ( imbasicdata2 != null) {
			imbasicdata2.setDeletionIndicator (1L);
			imbasicdata2.setUpdatedBy(loginUserID);
			imbasicdata2.setUpdatedOn(new Date());
			imbasicdata2Repository.save(imbasicdata2);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + itemCode);
		}
	}
}
