package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.controlprocessid.*;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.ControlProcessIdSpecification;
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
public class ControlProcessIdService {
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
	private ControlProcessIdRepository controlProcessIdRepository;

	/**
	 * getControlProcessIds
	 * @return
	 */
	public List<ControlProcessId> getControlProcessIds () {
		List<ControlProcessId> controlProcessIdList =  controlProcessIdRepository.findAll();
		controlProcessIdList = controlProcessIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ControlProcessId> newControlProcessId=new ArrayList<>();
		for(ControlProcessId dbControlProcessId:controlProcessIdList) {
			if (dbControlProcessId.getCompanyIdAndDescription() != null&&dbControlProcessId.getPlantIdAndDescription()!=null&&dbControlProcessId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbControlProcessId.getCompanyCodeId(), dbControlProcessId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbControlProcessId.getPlantId(), dbControlProcessId.getLanguageId(), dbControlProcessId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbControlProcessId.getWarehouseId(), dbControlProcessId.getLanguageId(), dbControlProcessId.getCompanyCodeId(), dbControlProcessId.getPlantId());
				if (iKeyValuePair != null) {
					dbControlProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbControlProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbControlProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newControlProcessId.add(dbControlProcessId);
		}
		return newControlProcessId;
	}

	/**
	 * getControlProcessId
	 * @param controlProcessId
	 * @return
	 */
	public ControlProcessId getControlProcessId (String warehouseId, String controlProcessId,String companyCodeId,String languageId,String plantId) {
		Optional<ControlProcessId> dbControlProcessId =
				controlProcessIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndControlProcessIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						controlProcessId,
						languageId,
						0L
				);
		if (dbControlProcessId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"controlProcessId - " + controlProcessId +
					" doesn't exist.");

		}
		ControlProcessId newControlProcessId = new ControlProcessId();
		BeanUtils.copyProperties(dbControlProcessId.get(),newControlProcessId, CommonUtils.getNullPropertyNames(dbControlProcessId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newControlProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newControlProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newControlProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newControlProcessId;
	}

	/**
	 * createControlProcessId
	 * @param newControlProcessId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ControlProcessId createControlProcessId (AddControlProcessId newControlProcessId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ControlProcessId dbControlProcessId = new ControlProcessId();
		Optional<ControlProcessId> duplicateControlProcessId = controlProcessIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndControlProcessIdAndLanguageIdAndDeletionIndicator(newControlProcessId.getCompanyCodeId(), newControlProcessId.getPlantId(), newControlProcessId.getWarehouseId(), newControlProcessId.getControlProcessId(), newControlProcessId.getLanguageId(), 0L);
		if (!duplicateControlProcessId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newControlProcessId.getWarehouseId(), newControlProcessId.getCompanyCodeId(), newControlProcessId.getPlantId(), newControlProcessId.getLanguageId());
			log.info("newControlProcessId : " + newControlProcessId);
			BeanUtils.copyProperties(newControlProcessId, dbControlProcessId, CommonUtils.getNullPropertyNames(newControlProcessId));
			dbControlProcessId.setDeletionIndicator(0L);
			dbControlProcessId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbControlProcessId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbControlProcessId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbControlProcessId.setCreatedBy(loginUserID);
			dbControlProcessId.setUpdatedBy(loginUserID);
			dbControlProcessId.setCreatedOn(new Date());
			dbControlProcessId.setUpdatedOn(new Date());
			return controlProcessIdRepository.save(dbControlProcessId);
		}
	}

	/**
	 * updateControlProcessId
	 * @param loginUserID
	 * @param controlProcessId
	 * @param updateControlProcessId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ControlProcessId updateControlProcessId (String warehouseId, String controlProcessId,String companyCodeId,String languageId,String plantId,String loginUserID,
													UpdateControlProcessId updateControlProcessId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ControlProcessId dbControlProcessId = getControlProcessId( warehouseId, controlProcessId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateControlProcessId, dbControlProcessId, CommonUtils.getNullPropertyNames(updateControlProcessId));
		dbControlProcessId.setUpdatedBy(loginUserID);
		dbControlProcessId.setUpdatedOn(new Date());
		return controlProcessIdRepository.save(dbControlProcessId);
	}

	/**
	 * deleteControlProcessId
	 * @param loginUserID
	 * @param controlProcessId
	 */
	public void deleteControlProcessId (String warehouseId, String controlProcessId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		ControlProcessId dbControlProcessId = getControlProcessId( warehouseId, controlProcessId,companyCodeId,languageId,plantId);
		if ( dbControlProcessId != null) {
			dbControlProcessId.setDeletionIndicator(1L);
			dbControlProcessId.setUpdatedBy(loginUserID);
			controlProcessIdRepository.save(dbControlProcessId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + controlProcessId);
		}
	}
	//Find ControlProcessId

	public List<ControlProcessId> findControlProcessId(FindControlProcessId findControlProcessId) throws ParseException {

		ControlProcessIdSpecification spec = new ControlProcessIdSpecification(findControlProcessId);
		List<ControlProcessId> results = controlProcessIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ControlProcessId> newControlProcessId=new ArrayList<>();
		for(ControlProcessId dbControlProcessId:results) {
			if (dbControlProcessId.getCompanyIdAndDescription() != null&&dbControlProcessId.getPlantIdAndDescription()!=null&&dbControlProcessId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbControlProcessId.getCompanyCodeId(), dbControlProcessId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbControlProcessId.getPlantId(), dbControlProcessId.getLanguageId(), dbControlProcessId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbControlProcessId.getWarehouseId(),dbControlProcessId.getLanguageId(), dbControlProcessId.getCompanyCodeId(), dbControlProcessId.getPlantId());
				if (iKeyValuePair != null) {
					dbControlProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbControlProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbControlProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newControlProcessId.add(dbControlProcessId);
		}
		return newControlProcessId;
	}

}
