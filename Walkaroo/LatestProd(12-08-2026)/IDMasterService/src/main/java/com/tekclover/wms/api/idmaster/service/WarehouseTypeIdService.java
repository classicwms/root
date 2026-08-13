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
import com.tekclover.wms.api.idmaster.model.returntypeid.ReturnTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.FindWarehouseTypeId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.WarehouseTypeIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.AddWarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.UpdateWarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.WarehouseTypeId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WarehouseTypeIdService {
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
	private WarehouseTypeIdRepository warehouseTypeIdRepository;

	/**
	 * getWarehouseTypeIds
	 * @return
	 */
	public List<WarehouseTypeId> getWarehouseTypeIds () {
		List<WarehouseTypeId> warehouseTypeIdList =  warehouseTypeIdRepository.findAll();
		warehouseTypeIdList = warehouseTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<WarehouseTypeId> newWarehouseTypeId=new ArrayList<>();
		for(WarehouseTypeId dbWarehouseTypeId:warehouseTypeIdList) {
			if (dbWarehouseTypeId.getCompanyIdAndDescription() != null&&dbWarehouseTypeId.getPlantIdAndDescription()!=null&&dbWarehouseTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbWarehouseTypeId.getCompanyCodeId(), dbWarehouseTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbWarehouseTypeId.getPlantId(), dbWarehouseTypeId.getLanguageId(), dbWarehouseTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbWarehouseTypeId.getWarehouseId(), dbWarehouseTypeId.getLanguageId(), dbWarehouseTypeId.getCompanyCodeId(), dbWarehouseTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbWarehouseTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbWarehouseTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbWarehouseTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newWarehouseTypeId.add(dbWarehouseTypeId);
		}
		return newWarehouseTypeId;
	}

	/**
	 * getWarehouseTypeId
	 * @param warehouseTypeId
	 * @return
	 */
	public WarehouseTypeId getWarehouseTypeId (String warehouseId,Long warehouseTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<WarehouseTypeId> dbWarehouseTypeId =
				warehouseTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndWarehouseTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						warehouseTypeId,
						languageId,
						0L
				);
		if (dbWarehouseTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"warehouseTypeId - " + warehouseTypeId +
					" doesn't exist.");

		}
		WarehouseTypeId newWarehouseTypeId = new WarehouseTypeId();
		BeanUtils.copyProperties(dbWarehouseTypeId.get(),newWarehouseTypeId, CommonUtils.getNullPropertyNames(dbWarehouseTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newWarehouseTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newWarehouseTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newWarehouseTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newWarehouseTypeId;
	}

//	/**
//	 * 
//	 * @param searchWarehouseTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<WarehouseTypeId> findWarehouseTypeId(SearchWarehouseTypeId searchWarehouseTypeId) 
//			throws ParseException {
//		WarehouseTypeIdSpecification spec = new WarehouseTypeIdSpecification(searchWarehouseTypeId);
//		List<WarehouseTypeId> results = warehouseTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createWarehouseTypeId
	 * @param newWarehouseTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WarehouseTypeId createWarehouseTypeId (AddWarehouseTypeId newWarehouseTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		WarehouseTypeId dbWarehouseTypeId = new WarehouseTypeId();
		Optional<WarehouseTypeId> duplicateWarehouseTypeId = warehouseTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndWarehouseTypeIdAndLanguageIdAndDeletionIndicator(newWarehouseTypeId.getCompanyCodeId(), newWarehouseTypeId.getPlantId(), newWarehouseTypeId.getWarehouseId(), newWarehouseTypeId.getWarehouseTypeId(), newWarehouseTypeId.getLanguageId(), 0L);
		if (!duplicateWarehouseTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newWarehouseTypeId.getWarehouseId(), newWarehouseTypeId.getCompanyCodeId(), newWarehouseTypeId.getPlantId(), newWarehouseTypeId.getLanguageId());
			log.info("newWarehouseTypeId : " + newWarehouseTypeId);
			BeanUtils.copyProperties(newWarehouseTypeId, dbWarehouseTypeId, CommonUtils.getNullPropertyNames(newWarehouseTypeId));
			dbWarehouseTypeId.setDeletionIndicator(0L);
			dbWarehouseTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbWarehouseTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbWarehouseTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbWarehouseTypeId.setCreatedBy(loginUserID);
			dbWarehouseTypeId.setUpdatedBy(loginUserID);
			dbWarehouseTypeId.setCreatedOn(new Date());
			dbWarehouseTypeId.setUpdatedOn(new Date());
			return warehouseTypeIdRepository.save(dbWarehouseTypeId);
		}
	}

	/**
	 * updateWarehouseTypeId
	 * @param loginUserID
	 * @param warehouseTypeId
	 * @param updateWarehouseTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WarehouseTypeId updateWarehouseTypeId (String warehouseId, Long warehouseTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
												  UpdateWarehouseTypeId updateWarehouseTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		WarehouseTypeId dbWarehouseTypeId = getWarehouseTypeId(warehouseId,warehouseTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateWarehouseTypeId, dbWarehouseTypeId, CommonUtils.getNullPropertyNames(updateWarehouseTypeId));
		dbWarehouseTypeId.setUpdatedBy(loginUserID);
		dbWarehouseTypeId.setUpdatedOn(new Date());
		return warehouseTypeIdRepository.save(dbWarehouseTypeId);
	}

	/**
	 * deleteWarehouseTypeId
	 * @param loginUserID
	 * @param warehouseTypeId
	 */
	public void deleteWarehouseTypeId (String warehouseId, Long warehouseTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		WarehouseTypeId dbWarehouseTypeId = getWarehouseTypeId(warehouseId,warehouseTypeId,companyCodeId,languageId,plantId);
		if ( dbWarehouseTypeId != null) {
			dbWarehouseTypeId.setDeletionIndicator(1L);
			dbWarehouseTypeId.setUpdatedBy(loginUserID);
			warehouseTypeIdRepository.save(dbWarehouseTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + warehouseTypeId);
		}
	}

	//Find WarehouseTypeId
	public List<WarehouseTypeId> findWarehouseTypeId(FindWarehouseTypeId findWarehouseTypeId) throws ParseException {

		WarehouseTypeIdSpecification spec = new WarehouseTypeIdSpecification(findWarehouseTypeId);
		List<WarehouseTypeId> results = warehouseTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<WarehouseTypeId> newWarehouseTypeId=new ArrayList<>();
		for(WarehouseTypeId dbWarehouseTypeId:results) {
			if (dbWarehouseTypeId.getCompanyIdAndDescription() != null&&dbWarehouseTypeId.getPlantIdAndDescription()!=null&&dbWarehouseTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbWarehouseTypeId.getCompanyCodeId(), dbWarehouseTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbWarehouseTypeId.getPlantId(), dbWarehouseTypeId.getLanguageId(), dbWarehouseTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbWarehouseTypeId.getWarehouseId(), dbWarehouseTypeId.getLanguageId(), dbWarehouseTypeId.getCompanyCodeId(), dbWarehouseTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbWarehouseTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbWarehouseTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbWarehouseTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newWarehouseTypeId.add(dbWarehouseTypeId);
		}
		return newWarehouseTypeId;
	}
}
