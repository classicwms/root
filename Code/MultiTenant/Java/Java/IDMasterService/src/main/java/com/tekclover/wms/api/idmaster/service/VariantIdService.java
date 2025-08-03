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
import com.tekclover.wms.api.idmaster.model.itemtypeid.ItemTypeId;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.varientid.FindVariantId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.VariantIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.varientid.AddVariantId;
import com.tekclover.wms.api.idmaster.model.varientid.UpdateVariantId;
import com.tekclover.wms.api.idmaster.model.varientid.VariantId;
import com.tekclover.wms.api.idmaster.repository.VariantIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VariantIdService{
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private VariantIdRepository variantIdRepository;

	/**
	 * getVariantIds
	 * @return
	 */
	public List<VariantId> getVariantIds () {
		List<VariantId> variantIdList =  variantIdRepository.findAll();
		variantIdList = variantIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<VariantId> newVariantId=new ArrayList<>();
		for(VariantId dbVariantId:variantIdList) {
			if (dbVariantId.getCompanyIdAndDescription() != null&&dbVariantId.getPlantIdAndDescription()!=null&&dbVariantId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbVariantId.getCompanyCodeId(), dbVariantId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbVariantId.getPlantId(), dbVariantId.getLanguageId(), dbVariantId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbVariantId.getWarehouseId(), dbVariantId.getLanguageId(), dbVariantId.getCompanyCodeId(), dbVariantId.getPlantId());
				if (iKeyValuePair != null) {
					dbVariantId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbVariantId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbVariantId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newVariantId.add(dbVariantId);
		}
		return newVariantId;
	}

	/**
	 * getVariantId
	 * @param variantCode
	 * @return
	 */
	public VariantId getVariantId ( String warehouseId, String variantCode, String variantType,String variantSubCode,String companyCodeId,String plantId,String languageId) {
		Optional<VariantId> dbVariantId =
				variantIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndVariantCodeAndVariantTypeAndVariantSubCodeAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						variantCode,
						variantType,
						variantSubCode,
						languageId,
						0L
				);
		if (dbVariantId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"variantCode - " + variantCode +
					"variantType - " + variantType +
					"variantSubCode - " + variantSubCode +
					" doesn't exist.");

		}
		VariantId newvariantId = new VariantId();
		BeanUtils.copyProperties(dbVariantId.get(),newvariantId, CommonUtils.getNullPropertyNames(dbVariantId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newvariantId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newvariantId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newvariantId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newvariantId;
	}

//	/**
//	 * 
//	 * @param searchVariantId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<VariantId> findVariantId(SearchVariantId searchVariantId) 
//			throws ParseException {
//		VariantIdSpecification spec = new VariantIdSpecification(searchVariantId);
//		List<VariantId> results = variantIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createVariantId
	 * @param newVariantId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public VariantId createVariantId (AddVariantId newVariantId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		VariantId dbVariantId = new VariantId();
		Optional<VariantId> duplicateVariantId = variantIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndVariantCodeAndVariantTypeAndVariantSubCodeAndLanguageIdAndDeletionIndicator(newVariantId.getCompanyCodeId(), newVariantId.getPlantId(), newVariantId.getWarehouseId(), newVariantId.getVariantCode(),
				newVariantId.getVariantType(), newVariantId.getVariantSubCode(), newVariantId.getLanguageId(), 0L);
		if (!duplicateVariantId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newVariantId.getWarehouseId(), newVariantId.getCompanyCodeId(), newVariantId.getPlantId(), newVariantId.getLanguageId());
			log.info("newVariantId : " + newVariantId);
			BeanUtils.copyProperties(newVariantId, dbVariantId, CommonUtils.getNullPropertyNames(newVariantId));
			dbVariantId.setDeletionIndicator(0L);
			dbVariantId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbVariantId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbVariantId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbVariantId.setCreatedBy(loginUserID);
			dbVariantId.setUpdatedBy(loginUserID);
			dbVariantId.setCreatedOn(new Date());
			dbVariantId.setUpdatedOn(new Date());
			return variantIdRepository.save(dbVariantId);
		}
	}

	/**
	 * updateVariantId
	 * @param loginUserID
	 * @param variantCode
	 * @param updateVariantId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public VariantId updateVariantId (String warehouseId, String variantCode, String variantType, String variantSubCode,String companyCodeId,String plantId,String languageId, String loginUserID,
									  UpdateVariantId updateVariantId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		VariantId dbVariantId = getVariantId(warehouseId, variantCode, variantType, variantSubCode,companyCodeId,plantId,languageId);
		BeanUtils.copyProperties(updateVariantId, dbVariantId, CommonUtils.getNullPropertyNames(updateVariantId));
		dbVariantId.setUpdatedBy(loginUserID);
		dbVariantId.setUpdatedOn(new Date());
		return variantIdRepository.save(dbVariantId);
	}

	/**
	 * deleteVariantId
	 * @param loginUserID
	 * @param variantCode
	 */
	public void deleteVariantId ( String warehouseId, String variantCode, String variantType, String variantSubCode, String companyCodeId,String plantId,String languageId,
								  String loginUserID) {
		VariantId dbVariantId = getVariantId(warehouseId, variantCode, variantType, variantSubCode,companyCodeId,plantId,languageId);
		if ( dbVariantId != null) {
			dbVariantId.setDeletionIndicator(1L);
			dbVariantId.setUpdatedBy(loginUserID);
			variantIdRepository.save(dbVariantId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + variantCode);
		}
	}
	//Find VariantId

	public List<VariantId> findVariantId(FindVariantId findVariantId) throws ParseException {

		VariantIdSpecification spec = new VariantIdSpecification(findVariantId);
		List<VariantId> results = variantIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<VariantId> newVariantId=new ArrayList<>();
		for(VariantId dbVariantId:results) {
			if (dbVariantId.getCompanyIdAndDescription() != null&&dbVariantId.getPlantIdAndDescription()!=null&&dbVariantId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbVariantId.getCompanyCodeId(),dbVariantId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbVariantId.getPlantId(), dbVariantId.getLanguageId(), dbVariantId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbVariantId.getWarehouseId(), dbVariantId.getLanguageId(), dbVariantId.getCompanyCodeId(), dbVariantId.getPlantId());
				if (iKeyValuePair != null) {
					dbVariantId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbVariantId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbVariantId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newVariantId.add(dbVariantId);
		}
		return newVariantId;
	}
}
