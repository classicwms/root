package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.controlprocessid.ControlProcessId;
import com.tekclover.wms.api.idmaster.model.controltypeid.*;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.ControlTypeIdSpecification;
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
public class ControlTypeIdService{
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
	private ControlTypeIdRepository controlTypeIdRepository;

	/**
	 * getControlTypeIds
	 * @return
	 */
	public List<ControlTypeId> getControlTypeIds () {
		List<ControlTypeId> controlTypeIdList =  controlTypeIdRepository.findAll();
		controlTypeIdList = controlTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ControlTypeId> newControlTypeId=new ArrayList<>();
		for(ControlTypeId dbControlTypeId:controlTypeIdList) {
			if (dbControlTypeId.getCompanyIdAndDescription() != null&&dbControlTypeId.getPlantIdAndDescription()!=null&&dbControlTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbControlTypeId.getCompanyCodeId(), dbControlTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbControlTypeId.getPlantId(), dbControlTypeId.getLanguageId(), dbControlTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbControlTypeId.getWarehouseId(), dbControlTypeId.getLanguageId(), dbControlTypeId.getCompanyCodeId(), dbControlTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbControlTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbControlTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbControlTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newControlTypeId.add(dbControlTypeId);
		}
		return newControlTypeId;
	}

	/**
	 * getControlTypeId
	 * @param controlTypeId
	 * @return
	 */
	public ControlTypeId getControlTypeId (String warehouseId, String controlTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<ControlTypeId> dbControlTypeId =
				controlTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndControlTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						controlTypeId,
						languageId,
						0L
				);
		if (dbControlTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"controlTypeId - " + controlTypeId +
					" doesn't exist.");

		}
		ControlTypeId newControlTypeId = new ControlTypeId();
		BeanUtils.copyProperties(dbControlTypeId.get(),newControlTypeId, CommonUtils.getNullPropertyNames(dbControlTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newControlTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newControlTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newControlTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newControlTypeId;
	}

	/**
	 * createControlTypeId
	 * @param newControlTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ControlTypeId createControlTypeId (AddControlTypeId newControlTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ControlTypeId dbControlTypeId = new ControlTypeId();
		Optional<ControlTypeId> duplicateControlTypeId = controlTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndControlTypeIdAndLanguageIdAndDeletionIndicator(newControlTypeId.getCompanyCodeId(), newControlTypeId.getPlantId(), newControlTypeId.getWarehouseId(),
				newControlTypeId.getControlTypeId(), newControlTypeId.getLanguageId(), 0L);
		if (!duplicateControlTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newControlTypeId.getWarehouseId(), newControlTypeId.getCompanyCodeId(), newControlTypeId.getPlantId(), newControlTypeId.getLanguageId());
			log.info("newControlTypeId : " + newControlTypeId);
			BeanUtils.copyProperties(newControlTypeId, dbControlTypeId, CommonUtils.getNullPropertyNames(newControlTypeId));
			dbControlTypeId.setDeletionIndicator(0L);
			dbControlTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbControlTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbControlTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbControlTypeId.setCreatedBy(loginUserID);
			dbControlTypeId.setUpdatedBy(loginUserID);
			dbControlTypeId.setCreatedOn(new Date());
			dbControlTypeId.setUpdatedOn(new Date());
			return controlTypeIdRepository.save(dbControlTypeId);
		}
	}

	/**
	 * updateControlTypeId
	 * @param loginUserID
	 * @param controlTypeId
	 * @param updateControlTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ControlTypeId updateControlTypeId (String warehouseId, String controlTypeId,String companyCodeId,String languageId,String plantId, String loginUserID,
											  UpdateControlTypeId updateControlTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ControlTypeId dbControlTypeId = getControlTypeId( warehouseId, controlTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateControlTypeId, dbControlTypeId, CommonUtils.getNullPropertyNames(updateControlTypeId));
		dbControlTypeId.setUpdatedBy(loginUserID);
		dbControlTypeId.setUpdatedOn(new Date());
		return controlTypeIdRepository.save(dbControlTypeId);
	}

	/**
	 * deleteControlTypeId
	 * @param loginUserID
	 * @param controlTypeId
	 */
	public void deleteControlTypeId (String warehouseId, String controlTypeId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		ControlTypeId dbControlTypeId = getControlTypeId( warehouseId, controlTypeId,companyCodeId,languageId,plantId);
		if ( dbControlTypeId != null) {
			dbControlTypeId.setDeletionIndicator(1L);
			dbControlTypeId.setUpdatedBy(loginUserID);
			controlTypeIdRepository.save(dbControlTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + controlTypeId);
		}
	}
	//Find ControlTypeId

	public List<ControlTypeId> findControlTypeId(FindControlTypeId findControlTypeId) throws ParseException {

		ControlTypeIdSpecification spec = new ControlTypeIdSpecification(findControlTypeId);
		List<ControlTypeId> results = controlTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ControlTypeId> newControlTypeId=new ArrayList<>();
		for(ControlTypeId dbControlTypeId:results) {
			if (dbControlTypeId.getCompanyIdAndDescription() != null&&dbControlTypeId.getPlantIdAndDescription()!=null&&dbControlTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbControlTypeId.getCompanyCodeId(), dbControlTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbControlTypeId.getPlantId(), dbControlTypeId.getLanguageId(), dbControlTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbControlTypeId.getWarehouseId(), dbControlTypeId.getLanguageId(), dbControlTypeId.getCompanyCodeId(), dbControlTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbControlTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbControlTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbControlTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newControlTypeId.add(dbControlTypeId);
		}
		return newControlTypeId;
	}
}
