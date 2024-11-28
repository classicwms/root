package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.model.warehousetypeid.WarehouseTypeId;
import com.tekclover.wms.api.idmaster.model.workcenterid.*;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.WorkCenterIdSpecification;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jdbc.Work;
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
public class WorkCenterIdService  {
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
	private WorkCenterIdRepository workCenterIdRepository;

	/**
	 * getWorkCenterIds
	 * @return
	 */
	public List<WorkCenterId> getWorkCenterIds () {
		List<WorkCenterId> workCenterIdList =  workCenterIdRepository.findAll();
		workCenterIdList = workCenterIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<WorkCenterId> newWorkCenterId=new ArrayList<>();
		for(WorkCenterId dbWorkCenterId:workCenterIdList) {
			if (dbWorkCenterId.getCompanyIdAndDescription() != null&&dbWorkCenterId.getPlantIdAndDescription()!=null&&dbWorkCenterId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbWorkCenterId.getCompanyCodeId(), dbWorkCenterId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbWorkCenterId.getPlantId(), dbWorkCenterId.getLanguageId(), dbWorkCenterId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbWorkCenterId.getWarehouseId(), dbWorkCenterId.getLanguageId(), dbWorkCenterId.getCompanyCodeId(), dbWorkCenterId.getPlantId());
				if (iKeyValuePair != null) {
					dbWorkCenterId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbWorkCenterId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbWorkCenterId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newWorkCenterId.add(dbWorkCenterId);
		}
		return newWorkCenterId;
	}

	/**
	 * getWorkCenterId
	 * @param workCenterId
	 * @return
	 */
	public WorkCenterId getWorkCenterId (String warehouseId,String workCenterId,String companyCodeId,String languageId,String plantId) {
		Optional<WorkCenterId> dbWorkCenterId =
				workCenterIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndWorkCenterIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						workCenterId,
						languageId,
						0L
				);
		if (dbWorkCenterId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"workCenterId - " + workCenterId +
					" doesn't exist.");

		}
		WorkCenterId newWorkCenterId = new WorkCenterId();
		BeanUtils.copyProperties(dbWorkCenterId.get(),newWorkCenterId, CommonUtils.getNullPropertyNames(dbWorkCenterId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newWorkCenterId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if (iKeyValuePair1 != null) {
			newWorkCenterId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newWorkCenterId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newWorkCenterId;
	}

	/**
	 * createWorkCenterId
	 * @param newWorkCenterId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkCenterId createWorkCenterId (AddWorkCenterId newWorkCenterId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		WorkCenterId dbWorkCenterId = new WorkCenterId();
		Optional<WorkCenterId> duplicateWorkCenterId = workCenterIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndWorkCenterIdAndLanguageIdAndDeletionIndicator(newWorkCenterId.getCompanyCodeId(), newWorkCenterId.getPlantId(), newWorkCenterId.getWarehouseId(), newWorkCenterId.getWorkCenterId(), newWorkCenterId.getLanguageId(), 0L);
		if (!duplicateWorkCenterId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newWorkCenterId.getWarehouseId(), newWorkCenterId.getCompanyCodeId(), newWorkCenterId.getPlantId(), newWorkCenterId.getLanguageId());
			log.info("newWorkCenterId : " + newWorkCenterId);
			BeanUtils.copyProperties(newWorkCenterId, dbWorkCenterId, CommonUtils.getNullPropertyNames(newWorkCenterId));
			dbWorkCenterId.setDeletionIndicator(0L);
			dbWorkCenterId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbWorkCenterId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbWorkCenterId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbWorkCenterId.setCreatedBy(loginUserID);
			dbWorkCenterId.setUpdatedBy(loginUserID);
			dbWorkCenterId.setCreatedOn(new Date());
			dbWorkCenterId.setUpdatedOn(new Date());
			return workCenterIdRepository.save(dbWorkCenterId);
		}
	}

	/**
	 * updateWorkCenterId
	 * @param loginUserID
	 * @param workCenterId
	 * @param updateWorkCenterId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public WorkCenterId updateWorkCenterId (String warehouseId, String workCenterId,String companyCodeId,String languageId,String plantId,String loginUserID,
											UpdateWorkCenterId updateWorkCenterId)
			throws IllegalAccessException, InvocationTargetException {
		WorkCenterId dbWorkCenterId = getWorkCenterId(warehouseId,workCenterId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateWorkCenterId, dbWorkCenterId, CommonUtils.getNullPropertyNames(updateWorkCenterId));
		dbWorkCenterId.setUpdatedBy(loginUserID);
		dbWorkCenterId.setUpdatedOn(new Date());
		return workCenterIdRepository.save(dbWorkCenterId);
	}

	/**
	 * deleteWorkCenterId
	 * @param loginUserID
	 * @param workCenterId
	 */
	public void deleteWorkCenterId (String warehouseId, String workCenterId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		WorkCenterId dbWorkCenterId = getWorkCenterId(warehouseId,workCenterId,companyCodeId,languageId,plantId);
		if ( dbWorkCenterId != null) {
			dbWorkCenterId.setDeletionIndicator(1L);
			dbWorkCenterId.setUpdatedBy(loginUserID);
			workCenterIdRepository.save(dbWorkCenterId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + workCenterId);
		}
	}

	//Find WorkCenterId
	public List<WorkCenterId> findWorkCenterId(FindWorkCenterId findWorkCenterId) throws ParseException {

		WorkCenterIdSpecification spec = new WorkCenterIdSpecification(findWorkCenterId);
		List<WorkCenterId> results = workCenterIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<WorkCenterId> newWorkCenterId=new ArrayList<>();
		for(WorkCenterId dbWorkCenterId:results) {
			if (dbWorkCenterId.getCompanyIdAndDescription() != null&&dbWorkCenterId.getPlantIdAndDescription()!=null&&dbWorkCenterId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbWorkCenterId.getCompanyCodeId(), dbWorkCenterId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbWorkCenterId.getPlantId(), dbWorkCenterId.getLanguageId(), dbWorkCenterId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbWorkCenterId.getWarehouseId(), dbWorkCenterId.getLanguageId(), dbWorkCenterId.getCompanyCodeId(), dbWorkCenterId.getPlantId());
				if (iKeyValuePair != null) {
					dbWorkCenterId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbWorkCenterId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbWorkCenterId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newWorkCenterId.add(dbWorkCenterId);
		}
		return newWorkCenterId;
	}
}
