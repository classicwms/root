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
import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;
import com.tekclover.wms.api.idmaster.model.strategyid.FindStrategyId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.StrategyIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.strategyid.AddStrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.StrategyId;
import com.tekclover.wms.api.idmaster.model.strategyid.UpdateStrategyId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StrategyIdService {
	@Autowired
	private LanguageIdRepository languageIdRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private StrategyIdRepository strategyIdRepository;

	/**
	 * getStrategyIds
	 * @return
	 */
	public List<StrategyId> getStrategyIds () {
		List<StrategyId> strategyIdList =  strategyIdRepository.findAll();
		strategyIdList = strategyIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StrategyId> newStrategyId=new ArrayList<>();
		for(StrategyId dbStrategyId:strategyIdList) {
			if (dbStrategyId.getCompanyIdAndDescription() != null&&dbStrategyId.getPlantIdAndDescription()!=null&&dbStrategyId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbStrategyId.getCompanyCodeId(), dbStrategyId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbStrategyId.getPlantId(), dbStrategyId.getLanguageId(), dbStrategyId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStrategyId.getWarehouseId(), dbStrategyId.getLanguageId(), dbStrategyId.getCompanyCodeId(), dbStrategyId.getPlantId());
				if (iKeyValuePair != null) {
					dbStrategyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStrategyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStrategyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newStrategyId.add(dbStrategyId);
		}
		return newStrategyId;
	}

	/**
	 * getStrategyId
	 * @param strategyNo
	 * @return
	 */
	public StrategyId getStrategyId (String warehouseId,Long strategyTypeId,String strategyNo,String companyCodeId,String languageId,String plantId) {
		Optional<StrategyId> dbStrategyId =
				strategyIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndStrategyNoAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						strategyTypeId,
						strategyNo,
						languageId,
						0L
				);
		if (dbStrategyId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"strategyTypeId - " + strategyTypeId +
					"strategyNo - " + strategyNo +
					" doesn't exist.");

		}
		StrategyId newStrategyId = new StrategyId();
		BeanUtils.copyProperties(dbStrategyId.get(),newStrategyId, CommonUtils.getNullPropertyNames(dbStrategyId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newStrategyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newStrategyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newStrategyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newStrategyId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StrategyId dbStrategyId = new StrategyId();
		Optional<StrategyId>duplicateStrategyId=strategyIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndStrategyNoAndLanguageIdAndDeletionIndicator(newStrategyId.getCompanyCodeId(), newStrategyId.getPlantId(), newStrategyId.getWarehouseId(), newStrategyId.getStrategyTypeId(), newStrategyId.getStrategyNo(), newStrategyId.getLanguageId(), 0L);
		if(!duplicateStrategyId.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newStrategyId.getWarehouseId(), newStrategyId.getCompanyCodeId(), newStrategyId.getPlantId(), newStrategyId.getLanguageId());
			log.info("newStrategyId : " + newStrategyId);
			BeanUtils.copyProperties(newStrategyId, dbStrategyId, CommonUtils.getNullPropertyNames(newStrategyId));
			dbStrategyId.setDeletionIndicator(0L);
			dbStrategyId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbStrategyId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbStrategyId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbStrategyId.setCreatedBy(loginUserID);
			dbStrategyId.setUpdatedBy(loginUserID);
			dbStrategyId.setCreatedOn(new Date());
			dbStrategyId.setUpdatedOn(new Date());
			return strategyIdRepository.save(dbStrategyId);
		}
	}

	/**
	 * updateStrategyId
	 * @param loginUserID
	 * @param strategyNo
	 * @param updateStrategyId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StrategyId updateStrategyId ( String warehouseId, Long strategyTypeId, String strategyNo,
										 String companyCodeId,String languageId,String plantId,String loginUserID,
										 UpdateStrategyId updateStrategyId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StrategyId dbStrategyId = getStrategyId(warehouseId, strategyTypeId,strategyNo,companyCodeId,languageId,plantId);
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
	public void deleteStrategyId ( String warehouseId, Long strategyTypeId, String strategyNo,String companyCodeId,String languageId,String plantId,String loginUserID) {
		StrategyId dbStrategyId = getStrategyId(warehouseId, strategyTypeId,strategyNo,companyCodeId,languageId,plantId);
		if ( dbStrategyId != null) {
			dbStrategyId.setDeletionIndicator(1L);
			dbStrategyId.setUpdatedBy(loginUserID);
			strategyIdRepository.save(dbStrategyId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + strategyNo);
		}
	}

	//Find StrategyId
	public List<StrategyId> findStrategyId(FindStrategyId findStrategyId) throws ParseException {

		StrategyIdSpecification spec = new StrategyIdSpecification(findStrategyId);
		List<StrategyId> results = strategyIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<StrategyId> newStrategyId=new ArrayList<>();
		for(StrategyId dbStrategyId:results) {
			if (dbStrategyId.getCompanyIdAndDescription() != null&&dbStrategyId.getPlantIdAndDescription()!=null&&dbStrategyId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbStrategyId.getCompanyCodeId(), dbStrategyId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbStrategyId.getPlantId(), dbStrategyId.getLanguageId(), dbStrategyId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbStrategyId.getWarehouseId(), dbStrategyId.getLanguageId(), dbStrategyId.getCompanyCodeId(), dbStrategyId.getPlantId());
				if (iKeyValuePair != null) {
					dbStrategyId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStrategyId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbStrategyId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newStrategyId.add(dbStrategyId);
		}
		return newStrategyId;
	}
}
