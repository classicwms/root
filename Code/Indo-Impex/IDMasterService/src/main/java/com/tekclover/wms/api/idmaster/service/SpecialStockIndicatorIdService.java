package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.AddSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.FindSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.SpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.specialstockindicatorid.UpdateSpecialStockIndicatorId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.StockTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.SpecialStockIndicatorIdSpecification;
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
public class SpecialStockIndicatorIdService {

	@Autowired
	private StockTypeIdService stockTypeIdService;

	@Autowired
	private StockTypeIdRepository stockTypeIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private SpecialStockIndicatorIdRepository specialStockIndicatorIdRepository;

	/**
	 * getSpecialStockIndicatorIds
	 * @return
	 */
	public List<SpecialStockIndicatorId> getSpecialStockIndicatorIds () {
		List<SpecialStockIndicatorId> specialStockIndicatorIdList =  specialStockIndicatorIdRepository.findAll();
		specialStockIndicatorIdList = specialStockIndicatorIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<SpecialStockIndicatorId> newSpecialStockIndicatorId=new ArrayList<>();
		for(SpecialStockIndicatorId dbSpecialStockIndicatorId:specialStockIndicatorIdList) {
			if (dbSpecialStockIndicatorId.getCompanyIdAndDescription() != null&&dbSpecialStockIndicatorId.getPlantIdAndDescription()!=null&&dbSpecialStockIndicatorId.getWarehouseIdAndDescription()!=null&&dbSpecialStockIndicatorId.getStockTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbSpecialStockIndicatorId.getCompanyCodeId(), dbSpecialStockIndicatorId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbSpecialStockIndicatorId.getPlantId(), dbSpecialStockIndicatorId.getLanguageId(), dbSpecialStockIndicatorId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbSpecialStockIndicatorId.getWarehouseId(), dbSpecialStockIndicatorId.getLanguageId(), dbSpecialStockIndicatorId.getCompanyCodeId(), dbSpecialStockIndicatorId.getPlantId());
				IKeyValuePair iKeyValuePair3 = stockTypeIdRepository.getStockTypeIdAndDescription(dbSpecialStockIndicatorId.getStockTypeId(), dbSpecialStockIndicatorId.getLanguageId(), dbSpecialStockIndicatorId.getCompanyCodeId(), dbSpecialStockIndicatorId.getPlantId(), dbSpecialStockIndicatorId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbSpecialStockIndicatorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbSpecialStockIndicatorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbSpecialStockIndicatorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbSpecialStockIndicatorId.setStockTypeIdAndDescription(iKeyValuePair3.getStockTypeId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newSpecialStockIndicatorId.add(dbSpecialStockIndicatorId);
		}
		return newSpecialStockIndicatorId;
	}

	/**
	 * getSpecialStockIndicatorId
	 * @param specialStockIndicatorId
	 * @return
	 */
	public SpecialStockIndicatorId getSpecialStockIndicatorId (String warehouseId,String stockTypeId, String specialStockIndicatorId,String companyCodeId,String languageId,String plantId) {
		Optional<SpecialStockIndicatorId> dbSpecialStockIndicatorId =
				specialStockIndicatorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStockTypeIdAndSpecialStockIndicatorIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						stockTypeId,
						specialStockIndicatorId,
						languageId,
						0L
				);
		if (dbSpecialStockIndicatorId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"stockTypeId - " + stockTypeId +
					"specialStockIndicatorId - " + specialStockIndicatorId +
					" doesn't exist.");

		}
		SpecialStockIndicatorId newSpecialStockIndicatorId = new SpecialStockIndicatorId();
		BeanUtils.copyProperties(dbSpecialStockIndicatorId.get(),newSpecialStockIndicatorId, CommonUtils.getNullPropertyNames(dbSpecialStockIndicatorId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= stockTypeIdRepository.getStockTypeIdAndDescription(stockTypeId,languageId,companyCodeId,plantId,warehouseId);
		if(iKeyValuePair!=null) {
			newSpecialStockIndicatorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newSpecialStockIndicatorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newSpecialStockIndicatorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newSpecialStockIndicatorId.setStockTypeIdAndDescription(iKeyValuePair3.getStockTypeId() + "-" + iKeyValuePair3.getDescription());
		}
		return newSpecialStockIndicatorId;
	}

	/**
	 * createSpecialStockIndicatorId
	 * @param newSpecialStockIndicatorId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SpecialStockIndicatorId createSpecialStockIndicatorId (AddSpecialStockIndicatorId newSpecialStockIndicatorId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SpecialStockIndicatorId dbSpecialStockIndicatorId = new SpecialStockIndicatorId();
		Optional<SpecialStockIndicatorId> duplicateSpecialStockIndicatorId = specialStockIndicatorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndStockTypeIdAndSpecialStockIndicatorIdAndLanguageIdAndDeletionIndicator(newSpecialStockIndicatorId.getCompanyCodeId(), newSpecialStockIndicatorId.getPlantId(), newSpecialStockIndicatorId.getWarehouseId(), newSpecialStockIndicatorId.getStockTypeId(), newSpecialStockIndicatorId.getSpecialStockIndicatorId(), newSpecialStockIndicatorId.getLanguageId(), 0L);
		if (!duplicateSpecialStockIndicatorId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			StockTypeId dbStockTypeId=stockTypeIdService.getStockTypeId(newSpecialStockIndicatorId.getWarehouseId(),newSpecialStockIndicatorId.getStockTypeId(), newSpecialStockIndicatorId.getCompanyCodeId(),newSpecialStockIndicatorId.getLanguageId(), newSpecialStockIndicatorId.getPlantId());
			log.info("newSpecialStockIndicatorId : " + newSpecialStockIndicatorId);
			BeanUtils.copyProperties(newSpecialStockIndicatorId, dbSpecialStockIndicatorId, CommonUtils.getNullPropertyNames(newSpecialStockIndicatorId));
			dbSpecialStockIndicatorId.setDeletionIndicator(0L);
			dbSpecialStockIndicatorId.setCompanyIdAndDescription(dbStockTypeId.getCompanyIdAndDescription());
			dbSpecialStockIndicatorId.setPlantIdAndDescription(dbStockTypeId.getPlantIdAndDescription());
			dbSpecialStockIndicatorId.setWarehouseIdAndDescription(dbStockTypeId.getWarehouseIdAndDescription());
			dbSpecialStockIndicatorId.setStockTypeIdAndDescription(dbStockTypeId.getStockTypeId()+"-"+dbStockTypeId.getStockTypeText());
			dbSpecialStockIndicatorId.setCreatedBy(loginUserID);
			dbSpecialStockIndicatorId.setUpdatedBy(loginUserID);
			dbSpecialStockIndicatorId.setCreatedOn(new Date());
			dbSpecialStockIndicatorId.setUpdatedOn(new Date());
			return specialStockIndicatorIdRepository.save(dbSpecialStockIndicatorId);
		}
	}

	/**
	 * updateSpecialStockIndicatorId
	 * @param loginUserID
	 * @param specialStockIndicatorId
	 * @param updateSpecialStockIndicatorId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SpecialStockIndicatorId updateSpecialStockIndicatorId (String warehouseId, String stockTypeId, String specialStockIndicatorId,String companyCodeId,String languageId,String plantId,String loginUserID,
																  UpdateSpecialStockIndicatorId updateSpecialStockIndicatorId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SpecialStockIndicatorId dbSpecialStockIndicatorId = getSpecialStockIndicatorId( warehouseId,stockTypeId,specialStockIndicatorId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateSpecialStockIndicatorId, dbSpecialStockIndicatorId, CommonUtils.getNullPropertyNames(updateSpecialStockIndicatorId));
		dbSpecialStockIndicatorId.setUpdatedBy(loginUserID);
		dbSpecialStockIndicatorId.setUpdatedOn(new Date());
		return specialStockIndicatorIdRepository.save(dbSpecialStockIndicatorId);
	}

	/**
	 * deleteSpecialStockIndicatorId
	 * @param loginUserID
	 * @param specialStockIndicatorId
	 */
	public void deleteSpecialStockIndicatorId (String warehouseId, String stockTypeId, String specialStockIndicatorId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		SpecialStockIndicatorId dbSpecialStockIndicatorId = getSpecialStockIndicatorId(warehouseId, stockTypeId,specialStockIndicatorId,companyCodeId,languageId,plantId);
		if ( dbSpecialStockIndicatorId != null) {
			dbSpecialStockIndicatorId.setDeletionIndicator(1L);
			dbSpecialStockIndicatorId.setUpdatedBy(loginUserID);
			specialStockIndicatorIdRepository.save(dbSpecialStockIndicatorId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + specialStockIndicatorId);
		}
	}

	//Find SpecialStockIndicatorId
	public List<SpecialStockIndicatorId> findSpecialStockIndicatorId(FindSpecialStockIndicatorId findSpecialStockIndicatorId) throws ParseException {

		SpecialStockIndicatorIdSpecification spec = new SpecialStockIndicatorIdSpecification(findSpecialStockIndicatorId);
		List<SpecialStockIndicatorId> results = specialStockIndicatorIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<SpecialStockIndicatorId> newSpecialStockIndicatorId=new ArrayList<>();
		for(SpecialStockIndicatorId dbSpecialStockIndicatorId:results) {
			if (dbSpecialStockIndicatorId.getCompanyIdAndDescription() != null&&dbSpecialStockIndicatorId.getPlantIdAndDescription()!=null&&dbSpecialStockIndicatorId.getWarehouseIdAndDescription()!=null&&dbSpecialStockIndicatorId.getStockTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbSpecialStockIndicatorId.getCompanyCodeId(), dbSpecialStockIndicatorId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbSpecialStockIndicatorId.getPlantId(), dbSpecialStockIndicatorId.getLanguageId(), dbSpecialStockIndicatorId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbSpecialStockIndicatorId.getWarehouseId(), dbSpecialStockIndicatorId.getLanguageId(), dbSpecialStockIndicatorId.getCompanyCodeId(), dbSpecialStockIndicatorId.getPlantId());
				IKeyValuePair iKeyValuePair3= stockTypeIdRepository.getStockTypeIdAndDescription(dbSpecialStockIndicatorId.getStockTypeId(), dbSpecialStockIndicatorId.getLanguageId(), dbSpecialStockIndicatorId.getCompanyCodeId(), dbSpecialStockIndicatorId.getPlantId(), dbSpecialStockIndicatorId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbSpecialStockIndicatorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbSpecialStockIndicatorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbSpecialStockIndicatorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbSpecialStockIndicatorId.setStockTypeIdAndDescription(iKeyValuePair3.getStockTypeId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newSpecialStockIndicatorId.add(dbSpecialStockIndicatorId);
		}
		return newSpecialStockIndicatorId;
	}
}
