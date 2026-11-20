package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.refdoctypeid.RefDocTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.AddReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.FindReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.ReturnTypeId;
import com.tekclover.wms.api.idmaster.model.returntypeid.UpdateReturnTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.ReturnTypeIdSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReturnTypeIdService {

	@Autowired
	private ReturnTypeIdRepository returnTypeIdRepository;

	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getReturnTypeIds
	 * @return
	 */
	public List<ReturnTypeId> getReturnTypeIds () {
		List<ReturnTypeId> returnTypeIdList =  returnTypeIdRepository.findAll();
		returnTypeIdList = returnTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ReturnTypeId> newReturnTypeId=new ArrayList<>();
		for(ReturnTypeId dbReturnTypeId:returnTypeIdList) {
			if (dbReturnTypeId.getCompanyIdAndDescription() != null&&dbReturnTypeId.getPlantIdAndDescription()!=null&&dbReturnTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbReturnTypeId.getCompanyCodeId(), dbReturnTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbReturnTypeId.getPlantId(), dbReturnTypeId.getLanguageId(), dbReturnTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbReturnTypeId.getWarehouseId(), dbReturnTypeId.getLanguageId(), dbReturnTypeId.getCompanyCodeId(), dbReturnTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbReturnTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbReturnTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbReturnTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newReturnTypeId.add(dbReturnTypeId);
		}
		return newReturnTypeId;
	}

	/**
	 * getReturnTypeId
	 * @param returnTypeId
	 * @return
	 */
	public ReturnTypeId getReturnTypeId (String warehouseId, String returnTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<ReturnTypeId> dbReturnTypeId =
				returnTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndReturnTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						returnTypeId,
						languageId,
						0L
				);
		if (dbReturnTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"returnTypeId - " + returnTypeId +
					" doesn't exist.");

		}
		ReturnTypeId newReturnTypeId = new ReturnTypeId();
		BeanUtils.copyProperties(dbReturnTypeId.get(),newReturnTypeId, CommonUtils.getNullPropertyNames(dbReturnTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newReturnTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newReturnTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newReturnTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newReturnTypeId;
	}

	/**
	 * createReturnTypeId
	 * @param newReturnTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ReturnTypeId createReturnTypeId (AddReturnTypeId newReturnTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ReturnTypeId dbReturnTypeId = new ReturnTypeId();
		Optional<ReturnTypeId> duplicateReturnTypeId = returnTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndReturnTypeIdAndLanguageIdAndDeletionIndicator(newReturnTypeId.getCompanyCodeId(), newReturnTypeId.getPlantId(), newReturnTypeId.getWarehouseId(), newReturnTypeId.getReturnTypeId(), newReturnTypeId.getLanguageId(), 0L);
		if (!duplicateReturnTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newReturnTypeId.getWarehouseId(), newReturnTypeId.getCompanyCodeId(), newReturnTypeId.getPlantId(), newReturnTypeId.getLanguageId());
			log.info("newReturnTypeId : " + newReturnTypeId);
			BeanUtils.copyProperties(newReturnTypeId, dbReturnTypeId, CommonUtils.getNullPropertyNames(newReturnTypeId));
			dbReturnTypeId.setDeletionIndicator(0L);
			dbReturnTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbReturnTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbReturnTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbReturnTypeId.setCreatedBy(loginUserID);
			dbReturnTypeId.setUpdatedBy(loginUserID);
			dbReturnTypeId.setCreatedOn(DateUtils.getCurrentKWTDateTime());
			dbReturnTypeId.setUpdatedOn(DateUtils.getCurrentKWTDateTime());
			return returnTypeIdRepository.save(dbReturnTypeId);
		}
	}

	/**
	 * updateReturnTypeId
	 * @param loginUserID
	 * @param returnTypeId
	 * @param updateReturnTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ReturnTypeId updateReturnTypeId (String warehouseId, String returnTypeId,String companyCodeId,String languageId,String plantId, String loginUserID,
											UpdateReturnTypeId updateReturnTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ReturnTypeId dbReturnTypeId = getReturnTypeId( warehouseId, returnTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateReturnTypeId, dbReturnTypeId, CommonUtils.getNullPropertyNames(updateReturnTypeId));
		dbReturnTypeId.setUpdatedBy(loginUserID);
		dbReturnTypeId.setUpdatedOn(DateUtils.getCurrentKWTDateTime());
		return returnTypeIdRepository.save(dbReturnTypeId);
	}

	/**
	 * deleteReturnTypeId
	 * @param loginUserID
	 * @param returnTypeId
	 */
	public void deleteReturnTypeId (String warehouseId, String returnTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		ReturnTypeId dbReturnTypeId = getReturnTypeId( warehouseId,returnTypeId,companyCodeId,languageId,plantId);
		if ( dbReturnTypeId != null) {
			dbReturnTypeId.setDeletionIndicator(1L);
			dbReturnTypeId.setUpdatedBy(loginUserID);
			returnTypeIdRepository.save(dbReturnTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + returnTypeId);
		}
	}
	//Find ReturnTypeId

	public List<ReturnTypeId> findReturnTypeId(FindReturnTypeId findReturnTypeId) throws ParseException {

		ReturnTypeIdSpecification spec = new ReturnTypeIdSpecification(findReturnTypeId);
		List<ReturnTypeId> results = returnTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ReturnTypeId> newReturnTypeId=new ArrayList<>();
		for(ReturnTypeId dbReturnTypeId:results) {
			if (dbReturnTypeId.getCompanyIdAndDescription() != null&&dbReturnTypeId.getPlantIdAndDescription()!=null&&dbReturnTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbReturnTypeId.getCompanyCodeId(), dbReturnTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbReturnTypeId.getPlantId(), dbReturnTypeId.getLanguageId(), dbReturnTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbReturnTypeId.getWarehouseId(), dbReturnTypeId.getLanguageId(), dbReturnTypeId.getCompanyCodeId(), dbReturnTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbReturnTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbReturnTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbReturnTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newReturnTypeId.add(dbReturnTypeId);
		}
		return newReturnTypeId;
	}
}
