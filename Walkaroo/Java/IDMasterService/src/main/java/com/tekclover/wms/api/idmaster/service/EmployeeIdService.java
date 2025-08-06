package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.employeeid.*;
import com.tekclover.wms.api.idmaster.model.strategyid.StrategyId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.EmployeeIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.EmployeeIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
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
public class EmployeeIdService  {

	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private EmployeeIdRepository employeeIdRepository;


	/**
	 * getEmployeeIds
	 * @return
	 */
	public List<EmployeeId> getEmployeeIds () {
		List<EmployeeId> employeeIdList =  employeeIdRepository.findAll();
		employeeIdList = employeeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<EmployeeId> newEmployeeId=new ArrayList<>();
		for(EmployeeId dbEmployeeId:employeeIdList) {
			if (dbEmployeeId.getCompanyIdAndDescription() != null&&dbEmployeeId.getPlantIdAndDescription()!=null&&dbEmployeeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbEmployeeId.getCompanyCodeId(), dbEmployeeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbEmployeeId.getPlantId(), dbEmployeeId.getLanguageId(), dbEmployeeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbEmployeeId.getWarehouseId(), dbEmployeeId.getLanguageId(), dbEmployeeId.getCompanyCodeId(), dbEmployeeId.getPlantId());
				if (iKeyValuePair != null) {
					dbEmployeeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbEmployeeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbEmployeeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newEmployeeId.add(dbEmployeeId);
		}
		return newEmployeeId;
	}

	/**
	 * getEmployeeId
	 * @param employeeId
	 * @return
	 */
	public EmployeeId getEmployeeId (String warehouseId,String employeeId,String companyCodeId,String languageId,String plantId) {
		Optional<EmployeeId> dbEmployeeId =
				employeeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndEmployeeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						employeeId,
						languageId,
						0L
				);
		if (dbEmployeeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"employeeId - " + employeeId +
					" doesn't exist.");

		}
		EmployeeId newEmployeeId = new EmployeeId();
		BeanUtils.copyProperties(dbEmployeeId.get(),newEmployeeId, CommonUtils.getNullPropertyNames(dbEmployeeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newEmployeeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newEmployeeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newEmployeeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newEmployeeId;
	}

	/**
	 * createEmployeeId
	 * @param newEmployeeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public EmployeeId createEmployeeId (AddEmployeeId newEmployeeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		EmployeeId dbEmployeeId = new EmployeeId();
		Optional<EmployeeId> duplicateEmployeeId = employeeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndEmployeeIdAndLanguageIdAndDeletionIndicator(newEmployeeId.getCompanyCodeId(), newEmployeeId.getPlantId(), newEmployeeId.getWarehouseId(), newEmployeeId.getEmployeeId(), newEmployeeId.getLanguageId(), 0L);
		if (!duplicateEmployeeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newEmployeeId.getWarehouseId(), newEmployeeId.getCompanyCodeId(), newEmployeeId.getPlantId(), newEmployeeId.getLanguageId());
			log.info("newEmployeeId : " + newEmployeeId);
			BeanUtils.copyProperties(newEmployeeId, dbEmployeeId, CommonUtils.getNullPropertyNames(newEmployeeId));
			dbEmployeeId.setDeletionIndicator(0L);
			dbEmployeeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbEmployeeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbEmployeeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbEmployeeId.setCreatedBy(loginUserID);
			dbEmployeeId.setUpdatedBy(loginUserID);
			dbEmployeeId.setCreatedOn(new Date());
			dbEmployeeId.setUpdatedOn(new Date());
			return employeeIdRepository.save(dbEmployeeId);
		}
	}

	/**
	 * updateEmployeeId
	 * @param loginUserID
	 * @param employeeId
	 * @param updateEmployeeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public EmployeeId updateEmployeeId (String warehouseId, String employeeId,String companyCodeId,String languageId,String plantId,String loginUserID,
										UpdateEmployeeId updateEmployeeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		EmployeeId dbEmployeeId = getEmployeeId( warehouseId,employeeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateEmployeeId, dbEmployeeId, CommonUtils.getNullPropertyNames(updateEmployeeId));
		dbEmployeeId.setUpdatedBy(loginUserID);
		dbEmployeeId.setUpdatedOn(new Date());
		return employeeIdRepository.save(dbEmployeeId);
	}

	/**
	 * deleteEmployeeId
	 * @param loginUserID
	 * @param employeeId
	 */
	public void deleteEmployeeId (String warehouseId, String employeeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		EmployeeId dbEmployeeId = getEmployeeId( warehouseId,employeeId,companyCodeId,languageId,plantId);
		if ( dbEmployeeId != null) {
			dbEmployeeId.setDeletionIndicator(1L);
			dbEmployeeId.setUpdatedBy(loginUserID);
			employeeIdRepository.save(dbEmployeeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + employeeId);
		}
	}

	//Find EmployeeId
	public List<EmployeeId> findEmployeeId(FindEmployeeId findEmployeeId) throws ParseException {

		EmployeeIdSpecification spec = new EmployeeIdSpecification(findEmployeeId);
		List<EmployeeId> results = employeeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<EmployeeId> newEmployeeId=new ArrayList<>();
		for(EmployeeId dbEmployeeId:results) {
			if (dbEmployeeId.getCompanyIdAndDescription() != null&&dbEmployeeId.getPlantIdAndDescription()!=null&&dbEmployeeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbEmployeeId.getCompanyCodeId(), dbEmployeeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbEmployeeId.getPlantId(),dbEmployeeId.getLanguageId(), dbEmployeeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbEmployeeId.getWarehouseId(),dbEmployeeId.getLanguageId(), dbEmployeeId.getCompanyCodeId(), dbEmployeeId.getPlantId());
				if (iKeyValuePair != null) {
					dbEmployeeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbEmployeeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbEmployeeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newEmployeeId.add(dbEmployeeId);
		}
		return newEmployeeId;
	}
}
