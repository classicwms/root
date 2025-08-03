package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.levelid.FindLevelId;
import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.LevelIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
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
public class LevelIdService {

	@Autowired
	private LevelIdRepository levelIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getLevelIds
	 * @return
	 */
	public List<LevelId> getLevelIds () {
		List<LevelId> levelIdList =  levelIdRepository.findAll();
		levelIdList = levelIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<LevelId> newLevelId=new ArrayList<>();
		for(LevelId dbLevelId:levelIdList) {
			if (dbLevelId.getCompanyIdAndDescription() != null&&dbLevelId.getPlantIdAndDescription()!=null&&dbLevelId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbLevelId.getCompanyCodeId(), dbLevelId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbLevelId.getPlantId(), dbLevelId.getLanguageId(), dbLevelId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbLevelId.getWarehouseId(), dbLevelId.getLanguageId(), dbLevelId.getCompanyCodeId(), dbLevelId.getPlantId());
				if (iKeyValuePair != null) {
					dbLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newLevelId.add(dbLevelId);
		}
		return newLevelId;
	}

	/**
	 * getLevelId
	 * @param levelId
	 * @return
	 */
	public LevelId getLevelId (String warehouseId, Long levelId,String companyCodeId,String  languageId,String plantId) {
		Optional<LevelId> dbLevelId =
				levelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLevelIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						levelId,
						languageId,
						0L
				);
		if (dbLevelId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"levelId - " + levelId +
					" doesn't exist.");

		}
		LevelId newLevelId = new LevelId();
		BeanUtils.copyProperties(dbLevelId.get(),newLevelId, CommonUtils.getNullPropertyNames(dbLevelId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
			return newLevelId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		LevelId dbLevelId = new LevelId();
		Optional<LevelId> duplicateLevelId = levelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLevelIdAndLanguageIdAndDeletionIndicator(newLevelId.getCompanyCodeId(),
				newLevelId.getPlantId(), newLevelId.getWarehouseId(), newLevelId.getLevelId(), newLevelId.getLanguageId(), 0L);
		if (!duplicateLevelId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newLevelId.getWarehouseId(), newLevelId.getCompanyCodeId(), newLevelId.getPlantId(), newLevelId.getLanguageId());
			log.info("newLevelId : " + newLevelId);
			BeanUtils.copyProperties(newLevelId, dbLevelId, CommonUtils.getNullPropertyNames(newLevelId));
			dbLevelId.setDeletionIndicator(0L);
			dbLevelId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbLevelId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbLevelId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbLevelId.setCreatedBy(loginUserID);
			dbLevelId.setUpdatedBy(loginUserID);
			dbLevelId.setCreatedOn(new Date());
			dbLevelId.setUpdatedOn(new Date());
			return levelIdRepository.save(dbLevelId);
		}
	}

	/**
	 * updateLevelId
	 * @param loginUserID
	 * @param levelId
	 * @param updateLevelId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public LevelId updateLevelId (String warehouseId, Long levelId,String companyCodeId,String languageId,String plantId,String loginUserID,
								  UpdateLevelId updateLevelId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		LevelId dbLevelId = getLevelId(warehouseId, levelId,companyCodeId,languageId,plantId);
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
	public void deleteLevelId (String warehouseId, Long levelId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		LevelId dbLevelId = getLevelId(warehouseId, levelId, companyCodeId, languageId, plantId);
		if (dbLevelId != null) {
			dbLevelId.setDeletionIndicator(1L);
			dbLevelId.setUpdatedBy(loginUserID);
			levelIdRepository.save(dbLevelId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + levelId);
		}
	}

	//Find LevelId
	public List<LevelId> findLevelId (FindLevelId findLevelId) throws ParseException {

		LevelIdSpecification spec = new LevelIdSpecification(findLevelId);
		List<LevelId> results = levelIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<LevelId> newLevelId=new ArrayList<>();
		for(LevelId dbLevelId:results) {
			if (dbLevelId.getCompanyIdAndDescription() != null&&dbLevelId.getPlantIdAndDescription()!=null&&dbLevelId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbLevelId.getCompanyCodeId(),dbLevelId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbLevelId.getPlantId(),dbLevelId.getLanguageId(), dbLevelId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbLevelId.getWarehouseId(), dbLevelId.getLanguageId(), dbLevelId.getCompanyCodeId(), dbLevelId.getPlantId());
				if (iKeyValuePair != null) {
					dbLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newLevelId.add(dbLevelId);
		}
		return newLevelId;
	}

}
