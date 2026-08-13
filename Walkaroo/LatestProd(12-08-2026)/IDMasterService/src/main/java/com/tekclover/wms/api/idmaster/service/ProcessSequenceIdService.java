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
import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
import com.tekclover.wms.api.idmaster.model.processsequenceid.FindProcessSequenceId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.ProcessSequenceIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.processsequenceid.AddProcessSequenceId;
import com.tekclover.wms.api.idmaster.model.processsequenceid.ProcessSequenceId;
import com.tekclover.wms.api.idmaster.model.processsequenceid.UpdateProcessSequenceId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessSequenceIdService {

	@Autowired
	private ProcessSequenceIdRepository processSequenceIdRepository;
	@Autowired
	private ProcessIdRepository processIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private ProcessIdService processIdService;

	/**
	 * getProcessSequenceIds
	 * @return
	 */
	public List<ProcessSequenceId> getProcessSequenceIds () {
		List<ProcessSequenceId> processSequenceIdList =  processSequenceIdRepository.findAll();
		processSequenceIdList = processSequenceIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ProcessSequenceId> newProcessSequenceId=new ArrayList<>();
		for(ProcessSequenceId dbProcessSequenceId:processSequenceIdList) {
			if (dbProcessSequenceId.getCompanyIdAndDescription() != null&&dbProcessSequenceId.getPlantIdAndDescription()!=null&&dbProcessSequenceId.getWarehouseIdAndDescription()!=null&&dbProcessSequenceId.getProcessIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbProcessSequenceId.getCompanyCodeId(), dbProcessSequenceId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbProcessSequenceId.getPlantId(), dbProcessSequenceId.getLanguageId(), dbProcessSequenceId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbProcessSequenceId.getWarehouseId(), dbProcessSequenceId.getLanguageId(), dbProcessSequenceId.getCompanyCodeId(), dbProcessSequenceId.getPlantId());
				IKeyValuePair iKeyValuePair3 = processIdRepository.getProcessIdAndDescription(dbProcessSequenceId.getProcessId(), dbProcessSequenceId.getLanguageId(), dbProcessSequenceId.getCompanyCodeId(), dbProcessSequenceId.getPlantId(), dbProcessSequenceId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbProcessSequenceId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbProcessSequenceId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbProcessSequenceId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbProcessSequenceId.setProcessIdAndDescription(iKeyValuePair3.getProcessId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newProcessSequenceId.add(dbProcessSequenceId);
		}
		return newProcessSequenceId;
	}

	/**
	 * getProcessSequenceId
	 * @param processId
	 * @return
	 */
	public ProcessSequenceId getProcessSequenceId (String warehouseId, String processId, Long processSequenceId,String companyCodeId,String languageId,String planId) {
		Optional<ProcessSequenceId> dbProcessSequenceId =
				processSequenceIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndProcessSequenceIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						planId,
						warehouseId,
						processId,
						processSequenceId,
						languageId,
						0L
				);
		if (dbProcessSequenceId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"processId - " + processId +
					"processSequenceId - " + processSequenceId +
					" doesn't exist.");

		}
		ProcessSequenceId newProcessSequenceId = new ProcessSequenceId();
		BeanUtils.copyProperties(dbProcessSequenceId.get(), newProcessSequenceId, CommonUtils.getNullPropertyNames(dbProcessSequenceId));
		IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(planId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,planId);
		IKeyValuePair iKeyValuePair3 = processIdRepository.getProcessIdAndDescription(processId,languageId,companyCodeId,planId,warehouseId);
		if(iKeyValuePair!=null) {
			newProcessSequenceId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newProcessSequenceId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newProcessSequenceId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newProcessSequenceId.setProcessIdAndDescription(iKeyValuePair3.getProcessId() + "-" + iKeyValuePair3.getDescription());
		}
		return newProcessSequenceId;
	}


//	/**
//	 * 
//	 * @param searchProcessSequenceId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<ProcessSequenceId> findProcessSequenceId(SearchProcessSequenceId searchProcessSequenceId) 
//			throws ParseException {
//		ProcessSequenceIdSpecification spec = new ProcessSequenceIdSpecification(searchProcessSequenceId);
//		List<ProcessSequenceId> results = processSequenceIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createProcessSequenceId
	 * @param newProcessSequenceId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ProcessSequenceId createProcessSequenceId (AddProcessSequenceId newProcessSequenceId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ProcessSequenceId dbProcessSequenceId = new ProcessSequenceId();
		Optional<ProcessSequenceId> duplicateProcessSequenceId = processSequenceIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndProcessSequenceIdAndLanguageIdAndDeletionIndicator(newProcessSequenceId.getCompanyCodeId(), newProcessSequenceId.getPlantId(), newProcessSequenceId.getWarehouseId(), newProcessSequenceId.getProcessId(), newProcessSequenceId.getProcessSequenceId(), newProcessSequenceId.getLanguageId(), 0L);
		if (!duplicateProcessSequenceId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			ProcessId dbProcessId=processIdService.getProcessId(newProcessSequenceId.getWarehouseId(), newProcessSequenceId.getProcessId(), newProcessSequenceId.getCompanyCodeId(), newProcessSequenceId.getLanguageId(), newProcessSequenceId.getPlantId());
			log.info("newProcessSequenceId : " + newProcessSequenceId);
			BeanUtils.copyProperties(newProcessSequenceId, dbProcessSequenceId, CommonUtils.getNullPropertyNames(newProcessSequenceId));
			dbProcessSequenceId.setDeletionIndicator(0L);
			dbProcessSequenceId.setCompanyIdAndDescription(dbProcessId.getCompanyIdAndDescription());
			dbProcessSequenceId.setPlantIdAndDescription(dbProcessId.getPlantIdAndDescription());
			dbProcessSequenceId.setWarehouseIdAndDescription(dbProcessId.getWarehouseIdAndDescription());
			dbProcessSequenceId.setProcessIdAndDescription(dbProcessId.getProcessId()+"-"+dbProcessId.getProcessText());
			dbProcessSequenceId.setCreatedBy(loginUserID);
			dbProcessSequenceId.setUpdatedBy(loginUserID);
			dbProcessSequenceId.setCreatedOn(new Date());
			dbProcessSequenceId.setUpdatedOn(new Date());
			return processSequenceIdRepository.save(dbProcessSequenceId);
		}
	}

	/**
	 * updateProcessSequenceId
	 * @param loginUserID
	 * @param processId
	 * @param updateProcessSequenceId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ProcessSequenceId updateProcessSequenceId (String warehouseId, String processId, Long processSequenceId,String companyCodeId,String languageId,String plantId,String loginUserID,
													  UpdateProcessSequenceId updateProcessSequenceId)
            throws IllegalAccessException, InvocationTargetException {
		ProcessSequenceId dbProcessSequenceId = getProcessSequenceId(warehouseId, processId, processSequenceId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateProcessSequenceId, dbProcessSequenceId, CommonUtils.getNullPropertyNames(updateProcessSequenceId));
		dbProcessSequenceId.setUpdatedBy(loginUserID);
		dbProcessSequenceId.setUpdatedOn(new Date());
		return processSequenceIdRepository.save(dbProcessSequenceId);
	}

	/**
	 * deleteProcessSequenceId
	 * @param loginUserID
	 * @param processId
	 */
	public void deleteProcessSequenceId (String warehouseId, String processId, Long processSequenceId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		ProcessSequenceId dbProcessSequenceId = getProcessSequenceId(warehouseId,processId,processSequenceId,companyCodeId,languageId,plantId);
		if ( dbProcessSequenceId != null) {
			dbProcessSequenceId.setDeletionIndicator(1L);
			dbProcessSequenceId.setUpdatedBy(loginUserID);
			processSequenceIdRepository.save(dbProcessSequenceId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + processId);
		}
	}

	//Find ProcessSequenceId
	public List<ProcessSequenceId> findProcessSequenceId(FindProcessSequenceId findProcessSequenceId) throws ParseException {

		ProcessSequenceIdSpecification spec = new ProcessSequenceIdSpecification(findProcessSequenceId);
		List<ProcessSequenceId> results = processSequenceIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ProcessSequenceId> newProcessSequenceId=new ArrayList<>();
		for(ProcessSequenceId dbProcessSequenceId:results) {
			if (dbProcessSequenceId.getCompanyIdAndDescription() != null&&dbProcessSequenceId.getPlantIdAndDescription()!=null&&dbProcessSequenceId.getWarehouseIdAndDescription()!=null&&dbProcessSequenceId.getProcessIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbProcessSequenceId.getCompanyCodeId(), dbProcessSequenceId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbProcessSequenceId.getPlantId(), dbProcessSequenceId.getLanguageId(), dbProcessSequenceId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbProcessSequenceId.getWarehouseId(), dbProcessSequenceId.getLanguageId(), dbProcessSequenceId.getCompanyCodeId(), dbProcessSequenceId.getPlantId());
				IKeyValuePair iKeyValuePair3= processIdRepository.getProcessIdAndDescription(dbProcessSequenceId.getProcessId(), dbProcessSequenceId.getLanguageId(), dbProcessSequenceId.getCompanyCodeId(), dbProcessSequenceId.getPlantId(), dbProcessSequenceId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbProcessSequenceId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbProcessSequenceId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbProcessSequenceId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbProcessSequenceId.setProcessIdAndDescription(iKeyValuePair3.getProcessId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newProcessSequenceId.add(dbProcessSequenceId);
		}
		return newProcessSequenceId;
	}
}
