package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.dateformatid.DateFormatId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.AddDecimalNotationId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.DecimalNotationId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.FindDecimalNotationId;
import com.tekclover.wms.api.idmaster.model.decimalnotationid.UpdateDecimalNotationId;
import com.tekclover.wms.api.idmaster.model.doorid.DoorId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.DecimalNotationIdSpecification;
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
public class DecimalNotationIdService  {
	@Autowired
	private LanguageIdRepository languageIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private DecimalNotationIdRepository decimalNotationIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getDecimalNotationIds
	 * @return
	 */
	public List<DecimalNotationId> getDecimalNotationIds () {
		List<DecimalNotationId> decimalNotationIdList =  decimalNotationIdRepository.findAll();
		decimalNotationIdList = decimalNotationIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<DecimalNotationId> newDecimalNotationId=new ArrayList<>();
		for(DecimalNotationId dbDecimalNotationId:decimalNotationIdList) {
			if (dbDecimalNotationId.getCompanyIdAndDescription() != null&&dbDecimalNotationId.getPlantIdAndDescription()!=null&&dbDecimalNotationId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbDecimalNotationId.getCompanyCodeId(), dbDecimalNotationId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbDecimalNotationId.getPlantId(), dbDecimalNotationId.getLanguageId(), dbDecimalNotationId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbDecimalNotationId.getWarehouseId(), dbDecimalNotationId.getLanguageId(), dbDecimalNotationId.getCompanyCodeId(), dbDecimalNotationId.getPlantId());
				if (iKeyValuePair != null) {
					dbDecimalNotationId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbDecimalNotationId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbDecimalNotationId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newDecimalNotationId.add(dbDecimalNotationId);
		}
		return newDecimalNotationId;
	}

	/**
	 * getDecimalNotationId
	 * @param decimalNotationId
	 * @return
	 */
	public DecimalNotationId getDecimalNotationId (String warehouseId, String decimalNotationId,String companyCodeId,String languageId,String plantId) {
		Optional<DecimalNotationId> dbDecimalNotationId =
				decimalNotationIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDecimalNotationIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						decimalNotationId,
						languageId,
						0L
				);
		if (dbDecimalNotationId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"decimalNotationId - " + decimalNotationId +
					" doesn't exist.");

		}
		DecimalNotationId newDecimalNotationId = new DecimalNotationId();
		BeanUtils.copyProperties(dbDecimalNotationId.get(),newDecimalNotationId, CommonUtils.getNullPropertyNames(dbDecimalNotationId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newDecimalNotationId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newDecimalNotationId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newDecimalNotationId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newDecimalNotationId;
	}

	/**
	 * createDecimalNotationId
	 * @param newDecimalNotationId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DecimalNotationId createDecimalNotationId (AddDecimalNotationId newDecimalNotationId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		DecimalNotationId dbDecimalNotationId = new DecimalNotationId();
		Optional<DecimalNotationId> duplicateDecimalNotationId = decimalNotationIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDecimalNotationIdAndLanguageIdAndDeletionIndicator(newDecimalNotationId.getCompanyCodeId(), newDecimalNotationId.getPlantId(), newDecimalNotationId.getWarehouseId(), newDecimalNotationId.getDecimalNotationId(), newDecimalNotationId.getLanguageId(), 0L);
		if (!duplicateDecimalNotationId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newDecimalNotationId.getWarehouseId(), newDecimalNotationId.getCompanyCodeId(), newDecimalNotationId.getPlantId(), newDecimalNotationId.getLanguageId());
			log.info("newDecimalNotationId : " + newDecimalNotationId);
			BeanUtils.copyProperties(newDecimalNotationId, dbDecimalNotationId, CommonUtils.getNullPropertyNames(newDecimalNotationId));
			dbDecimalNotationId.setDeletionIndicator(0L);
			dbDecimalNotationId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbDecimalNotationId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbDecimalNotationId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbDecimalNotationId.setCreatedBy(loginUserID);
			dbDecimalNotationId.setUpdatedBy(loginUserID);
			dbDecimalNotationId.setCreatedOn(new Date());
			dbDecimalNotationId.setUpdatedOn(new Date());
			return decimalNotationIdRepository.save(dbDecimalNotationId);
		}
	}

	/**
	 * updateDecimalNotationId
	 * @param loginUserID
	 * @param decimalNotationId
	 * @param updateDecimalNotationId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DecimalNotationId updateDecimalNotationId (String warehouseId, String decimalNotationId,String companyCodeId,String languageId,String plantId, String loginUserID,
													  UpdateDecimalNotationId updateDecimalNotationId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		DecimalNotationId dbDecimalNotationId = getDecimalNotationId( warehouseId, decimalNotationId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateDecimalNotationId, dbDecimalNotationId, CommonUtils.getNullPropertyNames(updateDecimalNotationId));
		dbDecimalNotationId.setUpdatedBy(loginUserID);
		dbDecimalNotationId.setUpdatedOn(new Date());
		return decimalNotationIdRepository.save(dbDecimalNotationId);
	}

	/**
	 * deleteDecimalNotationId
	 * @param loginUserID
	 * @param decimalNotationId
	 */
	public void deleteDecimalNotationId (String warehouseId, String decimalNotationId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		DecimalNotationId dbDecimalNotationId = getDecimalNotationId( warehouseId, decimalNotationId,companyCodeId,languageId,plantId);
		if ( dbDecimalNotationId != null) {
			dbDecimalNotationId.setDeletionIndicator(1L);
			dbDecimalNotationId.setUpdatedBy(loginUserID);
			decimalNotationIdRepository.save(dbDecimalNotationId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + decimalNotationId);
		}
	}
	//Find DecimalNotationId

	public List<DecimalNotationId> findDecimalNotationId(FindDecimalNotationId findDecimalNotationId) throws ParseException {

		DecimalNotationIdSpecification spec = new DecimalNotationIdSpecification(findDecimalNotationId);
		List<DecimalNotationId> results = decimalNotationIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<DecimalNotationId> newDecimalNotationId=new ArrayList<>();
		for(DecimalNotationId dbDecimalNotationId:results) {
			if (dbDecimalNotationId.getCompanyIdAndDescription() != null&&dbDecimalNotationId.getPlantIdAndDescription()!=null&&dbDecimalNotationId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbDecimalNotationId.getCompanyCodeId(), dbDecimalNotationId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbDecimalNotationId.getPlantId(), dbDecimalNotationId.getLanguageId(), dbDecimalNotationId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbDecimalNotationId.getWarehouseId(), dbDecimalNotationId.getLanguageId(), dbDecimalNotationId.getCompanyCodeId(), dbDecimalNotationId.getPlantId());
				if (iKeyValuePair != null) {
					dbDecimalNotationId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbDecimalNotationId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbDecimalNotationId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newDecimalNotationId.add(dbDecimalNotationId);
		}
		return newDecimalNotationId;
	}
}
