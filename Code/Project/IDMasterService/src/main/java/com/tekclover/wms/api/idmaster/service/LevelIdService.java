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
import com.tekclover.wms.api.idmaster.model.levelid.AddLevelId;
import com.tekclover.wms.api.idmaster.model.levelid.LevelId;
import com.tekclover.wms.api.idmaster.model.levelid.UpdateLevelId;
import com.tekclover.wms.api.idmaster.repository.LevelIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LevelIdService extends BaseService {
	
	@Autowired
	private LevelIdRepository levelIdRepository;
	
	/**
	 * getLevelIds
	 * @return
	 */
	public List<LevelId> getLevelIds () {
		List<LevelId> levelIdList =  levelIdRepository.findAll();
		levelIdList = levelIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return levelIdList;
	}
	
	/**
	 * getLevelId
	 * @param levelId
	 * @return
	 */
	public LevelId getLevelId (String warehouseId, Long levelId, String level) {
		Optional<LevelId> dbLevelId = 
				levelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLevelIdAndLanguageIdAndLevelAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								levelId,
								getLanguageId(),
								level,
								0L
								);
		if (dbLevelId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"levelId - " + levelId +
						"level - " + level +
						" doesn't exist.");
			
		} 
		return dbLevelId.get();
	}
	
//	/**
//	 * 
//	 * @param searchLevelId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<LevelId> findLevelId(SearchLevelId searchLevelId) 
//			throws ParseException {
//		LevelIdSpecification spec = new LevelIdSpecification(searchLevelId);
//		List<LevelId> results = levelIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createLevelId
	 * @param newLevelId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public LevelId createLevelId (AddLevelId newLevelId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		LevelId dbLevelId = new LevelId();
		log.info("newLevelId : " + newLevelId);
		BeanUtils.copyProperties(newLevelId, dbLevelId, CommonUtils.getNullPropertyNames(newLevelId));
		dbLevelId.setCompanyCodeId(getCompanyCode());
		dbLevelId.setPlantId(getPlantId());
		dbLevelId.setDeletionIndicator(0L);
		dbLevelId.setCreatedBy(loginUserID);
		dbLevelId.setUpdatedBy(loginUserID);
		dbLevelId.setCreatedOn(new Date());
		dbLevelId.setUpdatedOn(new Date());
		return levelIdRepository.save(dbLevelId);
	}
	
	/**
	 * updateLevelId
	 * @param loginUserId 
	 * @param levelId
	 * @param updateLevelId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public LevelId updateLevelId (String warehouseId, Long levelId, String level, String loginUserID, 
			UpdateLevelId updateLevelId) 
			throws IllegalAccessException, InvocationTargetException {
		LevelId dbLevelId = getLevelId(warehouseId, levelId, level);
		BeanUtils.copyProperties(updateLevelId, dbLevelId, CommonUtils.getNullPropertyNames(updateLevelId));
		dbLevelId.setUpdatedBy(loginUserID);
		dbLevelId.setUpdatedOn(new Date());
		return levelIdRepository.save(dbLevelId);
	}
	
	/**
	 * deleteLevelId
	 * @param loginUserID 
	 * @param levelId
	 */
	public void deleteLevelId (String warehouseId, Long levelId, String level, String loginUserID) {
		LevelId dbLevelId = getLevelId(warehouseId, levelId, level);
		if ( dbLevelId != null) {
			dbLevelId.setDeletionIndicator(1L);
			dbLevelId.setUpdatedBy(loginUserID);
			levelIdRepository.save(dbLevelId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + levelId);
		}
	}
}
