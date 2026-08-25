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
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.uomid.FindUomId;
import com.tekclover.wms.api.idmaster.model.varientid.VariantId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.UomIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.uomid.AddUomId;
import com.tekclover.wms.api.idmaster.model.uomid.UomId;
import com.tekclover.wms.api.idmaster.model.uomid.UpdateUomId;
import com.tekclover.wms.api.idmaster.repository.UomIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UomIdService {

	@Autowired
	private UomIdRepository uomIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getUomIds
	 * @return
	 */
	public List<UomId> getUomIds () {
		List<UomId> uomIdList =  uomIdRepository.findAll();
		uomIdList = uomIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<UomId> newUomId=new ArrayList<>();
		for(UomId dbUomId:uomIdList) {
			if (dbUomId.getCompanyIdAndDescription() != null&&dbUomId.getPlantIdAndDescription()!=null&&dbUomId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbUomId.getCompanyCodeId(), dbUomId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbUomId.getPlantId(), dbUomId.getLanguageId(), dbUomId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbUomId.getWarehouseId(), dbUomId.getLanguageId(), dbUomId.getCompanyCodeId(), dbUomId.getPlantId());
				if (iKeyValuePair != null) {
					dbUomId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbUomId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbUomId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}

			newUomId.add(dbUomId);
		}
		return newUomId;
	}

	/**
	 * getUomId
	 * @param uomId
	 * @return
	 */
	public UomId getUomId (String uomId,String companyCodeId,String warehouseId,String plantId,String languageId) {
		Optional<UomId> dbUomId =
				uomIdRepository.findByCompanyCodeIdAndUomIdAndWarehouseIdAndPlantIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						uomId,
						warehouseId,
						plantId,
						languageId,
						0L
				);
		if (dbUomId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"uomId - " + uomId +
					" doesn't exist.");

		}
		UomId newUomId = new UomId();
		BeanUtils.copyProperties(dbUomId.get(),newUomId, CommonUtils.getNullPropertyNames(dbUomId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newUomId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newUomId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newUomId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newUomId;
	}

//	/**
//	 * 
//	 * @param searchUomId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<UomId> findUomId(SearchUomId searchUomId) 
//			throws ParseException {
//		UomIdSpecification spec = new UomIdSpecification(searchUomId);
//		List<UomId> results = uomIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createUomId
	 * @param newUomId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UomId createUomId (AddUomId newUomId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		UomId dbUomId = new UomId();
		Optional<UomId> duplicateUomId = uomIdRepository.findByCompanyCodeIdAndUomIdAndWarehouseIdAndPlantIdAndLanguageIdAndDeletionIndicator(newUomId.getCompanyCodeId(), newUomId.getUomId(), newUomId.getWarehouseId(), newUomId.getPlantId(), newUomId.getLanguageId(), 0L);
		if (!duplicateUomId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newUomId.getWarehouseId(), newUomId.getCompanyCodeId(), newUomId.getPlantId(), newUomId.getLanguageId());
			log.info("newUomId : " + newUomId);
			BeanUtils.copyProperties(newUomId, dbUomId, CommonUtils.getNullPropertyNames(newUomId));
			dbUomId.setDeletionIndicator(0L);
			dbUomId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbUomId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbUomId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbUomId.setCreatedBy(loginUserID);
			dbUomId.setUpdatedBy(loginUserID);
			dbUomId.setCreatedOn(new Date());
			dbUomId.setUpdatedOn(new Date());
			return uomIdRepository.save(dbUomId);
		}
	}

	/**
	 * updateUomId
	 * @param loginUserID
	 * @param uomId
	 * @param updateUomId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UomId updateUomId (String uomId,String companyCodeId,String warehouseId,String plantId,String languageId,String loginUserID,
							  UpdateUomId updateUomId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		UomId dbUomId = getUomId(uomId,companyCodeId,warehouseId,plantId,languageId);
		BeanUtils.copyProperties(updateUomId, dbUomId, CommonUtils.getNullPropertyNames(updateUomId));
		dbUomId.setUpdatedBy(loginUserID);
		dbUomId.setUpdatedOn(new Date());
		return uomIdRepository.save(dbUomId);
	}

	/**
	 * deleteUomId
	 * @param loginUserID
	 * @param uomId
	 */
	public void deleteUomId (String uomId,String companyCodeId,String warehouseId,String plantId,String languageId, String loginUserID) {
		UomId dbUomId = getUomId(uomId,companyCodeId,warehouseId,plantId,languageId);
		if ( dbUomId != null) {
			dbUomId.setDeletionIndicator(1L);
			dbUomId.setUpdatedBy(loginUserID);
			uomIdRepository.save(dbUomId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + uomId);
		}
	}

	//Find UomId
	public List<UomId> findUomId(FindUomId findUomId) throws ParseException {

		UomIdSpecification spec = new UomIdSpecification(findUomId);
		List<UomId> results = uomIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<UomId> newUomId=new ArrayList<>();
		for(UomId dbUomId:results) {
			if (dbUomId.getCompanyIdAndDescription() != null&&dbUomId.getPlantIdAndDescription()!=null&&dbUomId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbUomId.getCompanyCodeId(), dbUomId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbUomId.getPlantId(), dbUomId.getLanguageId(), dbUomId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbUomId.getWarehouseId(), dbUomId.getLanguageId(), dbUomId.getCompanyCodeId(), dbUomId.getPlantId());
				if (iKeyValuePair != null) {
					dbUomId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbUomId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbUomId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newUomId.add(dbUomId);
		}
		return newUomId;
	}
}
