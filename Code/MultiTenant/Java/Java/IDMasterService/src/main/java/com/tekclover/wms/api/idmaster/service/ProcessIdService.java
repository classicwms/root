package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.controltypeid.ControlTypeId;
import com.tekclover.wms.api.idmaster.model.processid.AddProcessId;
import com.tekclover.wms.api.idmaster.model.processid.FindProcessId;
import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
import com.tekclover.wms.api.idmaster.model.processid.UpdateProcessId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.ProcessIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.ProcessIdSpecification;
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
public class ProcessIdService{

	@Autowired
	private WarehouseService warehouseService;

	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private ProcessIdRepository processIdRepository;

	/**
	 * getProcessIds
	 * @return
	 */
	public List<ProcessId> getProcessIds () {
		List<ProcessId> processIdList =  processIdRepository.findAll();
		processIdList = processIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ProcessId> newProcessId=new ArrayList<>();
		for(ProcessId dbProcessId:processIdList) {
			if (dbProcessId.getCompanyIdAndDescription() != null&&dbProcessId.getPlantIdAndDescription()!=null&&dbProcessId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbProcessId.getCompanyCodeId(), dbProcessId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbProcessId.getPlantId(), dbProcessId.getLanguageId(), dbProcessId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbProcessId.getWarehouseId(), dbProcessId.getLanguageId(), dbProcessId.getCompanyCodeId(), dbProcessId.getPlantId());
				if (iKeyValuePair != null) {
					dbProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newProcessId.add(dbProcessId);
		}
		return newProcessId;

	}

	/**
	 * getProcessId
	 * @param processId
	 * @return
	 */
	public ProcessId getProcessId (String warehouseId,String processId,String companyCodeId,String languageId,String plantId) {
		Optional<ProcessId> dbProcessId =
				processIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						processId,
						languageId,
						0L
				);
		if (dbProcessId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"processId - " + processId +
					" doesn't exist.");

		}
		ProcessId newProcessId = new ProcessId();
		BeanUtils.copyProperties(dbProcessId.get(),newProcessId, CommonUtils.getNullPropertyNames(dbProcessId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newProcessId;

	}

	/**
	 * createProcessId
	 * @param newProcessId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ProcessId createProcessId (AddProcessId newProcessId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ProcessId dbProcessId = new ProcessId();
		Optional<ProcessId>duplicateProcessId=processIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndLanguageIdAndDeletionIndicator(newProcessId.getCompanyCodeId(), newProcessId.getPlantId(), newProcessId.getWarehouseId(), newProcessId.getProcessId(), newProcessId.getLanguageId(), 0L);
		if(!duplicateProcessId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newProcessId.getWarehouseId(), newProcessId.getCompanyCodeId(), newProcessId.getPlantId(), newProcessId.getLanguageId());
			log.info("newProcessId : " + newProcessId);
			BeanUtils.copyProperties(newProcessId, dbProcessId, CommonUtils.getNullPropertyNames(newProcessId));
			dbProcessId.setDeletionIndicator(0L);
			dbProcessId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbProcessId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbProcessId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbProcessId.setCreatedBy(loginUserID);
			dbProcessId.setUpdatedBy(loginUserID);
			dbProcessId.setCreatedOn(new Date());
			dbProcessId.setUpdatedOn(new Date());
			return processIdRepository.save(dbProcessId);
		}
	}

	/**
	 * updateProcessId
	 * @param loginUserID
	 * @param processId
	 * @param updateProcessId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ProcessId updateProcessId (String warehouseId,String processId,String companyCodeId,String languageId,String plantId,String loginUserID,
									  UpdateProcessId updateProcessId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ProcessId dbProcessId = getProcessId(warehouseId,processId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateProcessId, dbProcessId, CommonUtils.getNullPropertyNames(updateProcessId));
		dbProcessId.setUpdatedBy(loginUserID);
		dbProcessId.setUpdatedOn(new Date());
		return processIdRepository.save(dbProcessId);
	}

	/**
	 * deleteProcessId
	 * @param loginUserID
	 * @param processId
	 */
	public void deleteProcessId (String warehouseId, String processId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		ProcessId dbProcessId = getProcessId(warehouseId,processId,companyCodeId,languageId,plantId);
		if ( dbProcessId != null) {
			dbProcessId.setDeletionIndicator(1L);
			dbProcessId.setUpdatedBy(loginUserID);
			processIdRepository.save(dbProcessId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + processId);
		}
	}

	//Find ProcessId
	public List<ProcessId> findProcessId(FindProcessId findProcessId) throws ParseException {

		ProcessIdSpecification spec = new ProcessIdSpecification(findProcessId);
		List<ProcessId> results = processIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ProcessId> newProcessId=new ArrayList<>();
		for(ProcessId dbProcessId:results) {
			if (dbProcessId.getCompanyIdAndDescription() != null&&dbProcessId.getPlantIdAndDescription()!=null&&dbProcessId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbProcessId.getCompanyCodeId(),dbProcessId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbProcessId.getPlantId(), dbProcessId.getLanguageId(), dbProcessId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbProcessId.getWarehouseId(), dbProcessId.getLanguageId(), dbProcessId.getCompanyCodeId(), dbProcessId.getPlantId());
				if (iKeyValuePair != null) {
					dbProcessId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbProcessId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbProcessId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newProcessId.add(dbProcessId);
		}
		return newProcessId;
	}
}
