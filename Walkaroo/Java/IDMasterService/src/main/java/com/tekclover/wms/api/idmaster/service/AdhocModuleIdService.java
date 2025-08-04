package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.adhocmoduleid.*;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.AdhocModuleIdSpecification;
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
public class AdhocModuleIdService {
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private ModuleIdService moduleIdService;
	@Autowired
	private ModuleIdRepository moduleIdRepository;
	@Autowired
	private AdhocModuleIdRepository adhocModuleIdRepository;

	/**
	 * getAdhocModuleIds
	 * @return
	 */
	public List<AdhocModuleId> getAdhocModuleIds () {
		List<AdhocModuleId> adhocModuleIdList =  adhocModuleIdRepository.findAll();
		adhocModuleIdList = adhocModuleIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<AdhocModuleId> newAdhocModuleId=new ArrayList<>();
		for(AdhocModuleId dbAdhocModuleId:adhocModuleIdList) {
			if (dbAdhocModuleId.getCompanyIdAndDescription() != null&&dbAdhocModuleId.getPlantIdAndDescription()!=null&&dbAdhocModuleId.getWarehouseIdAndDescription()!=null&&dbAdhocModuleId.getModuleIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbAdhocModuleId.getCompanyCodeId(), dbAdhocModuleId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbAdhocModuleId.getPlantId(), dbAdhocModuleId.getLanguageId(), dbAdhocModuleId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbAdhocModuleId.getWarehouseId(), dbAdhocModuleId.getLanguageId(), dbAdhocModuleId.getCompanyCodeId(), dbAdhocModuleId.getPlantId());
				IKeyValuePair iKeyValuePair3 = moduleIdRepository.getModuleIdAndDescription(dbAdhocModuleId.getModuleId(), dbAdhocModuleId.getLanguageId(), dbAdhocModuleId.getCompanyCodeId(), dbAdhocModuleId.getPlantId(), dbAdhocModuleId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbAdhocModuleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbAdhocModuleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbAdhocModuleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbAdhocModuleId.setModuleIdAndDescription(iKeyValuePair3.getModuleId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newAdhocModuleId.add(dbAdhocModuleId);
		}
		return newAdhocModuleId;
	}

	/**
	 * getAdhocModuleId
	 * @param adhocModuleId
	 * @return
	 */
	public AdhocModuleId getAdhocModuleId (String warehouseId, String moduleId, String adhocModuleId,String companyCodeId,String languageId,String plantId) {
		Optional<AdhocModuleId> dbAdhocModuleId =
				adhocModuleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndAdhocModuleIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						moduleId,
						adhocModuleId,
						languageId,
						0L
				);
		if (dbAdhocModuleId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"moduleId - " + moduleId +
					"adhocModuleId - " + adhocModuleId +
					" doesn't exist.");

		}
		AdhocModuleId newAdhocModuleId = new AdhocModuleId();
		BeanUtils.copyProperties(dbAdhocModuleId.get(),newAdhocModuleId, CommonUtils.getNullPropertyNames(dbAdhocModuleId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3=moduleIdRepository.getModuleIdAndDescription(moduleId,languageId,companyCodeId,plantId,warehouseId);
		if(iKeyValuePair!=null) {
			newAdhocModuleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newAdhocModuleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newAdhocModuleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newAdhocModuleId.setModuleIdAndDescription(iKeyValuePair3.getModuleId() + "-" + iKeyValuePair3.getDescription());
		}
		return newAdhocModuleId;
	}

	/**
	 * createAdhocModuleId
	 * @param newAdhocModuleId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AdhocModuleId createAdhocModuleId (AddAdhocModuleId newAdhocModuleId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		AdhocModuleId dbAdhocModuleId = new AdhocModuleId();
		Optional<AdhocModuleId> duplicateAdhocModuleId = adhocModuleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndAdhocModuleIdAndLanguageIdAndDeletionIndicator(newAdhocModuleId.getCompanyCodeId(), newAdhocModuleId.getPlantId(), newAdhocModuleId.getWarehouseId(),
				newAdhocModuleId.getModuleId(), newAdhocModuleId.getAdhocModuleId(), newAdhocModuleId.getLanguageId(), 0L);
		if (!duplicateAdhocModuleId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
//			ModuleId dbModuleId=adhocModuleIdRepository.getModuleIdAndDescription(
//														newAdhocModuleId.getModuleId(),
//														newAdhocModuleId.getLanguageId(),
//														newAdhocModuleId.getCompanyCodeId(),
//														newAdhocModuleId.getPlantId(),
//														newAdhocModuleId.getWarehouseId());
			Optional<ModuleId> dbModuleId=moduleIdRepository.
					findTop1ByModuleIdAndLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
														newAdhocModuleId.getModuleId(),
														newAdhocModuleId.getLanguageId(),
														newAdhocModuleId.getCompanyCodeId(),
														newAdhocModuleId.getPlantId(),
														newAdhocModuleId.getWarehouseId(),0L);
					log.info("newAdhocModuleId : " + newAdhocModuleId);
			BeanUtils.copyProperties(newAdhocModuleId, dbAdhocModuleId, CommonUtils.getNullPropertyNames(newAdhocModuleId));
			dbAdhocModuleId.setDeletionIndicator(0L);
			dbAdhocModuleId.setCompanyIdAndDescription(dbModuleId.get().getCompanyIdAndDescription());
			dbAdhocModuleId.setPlantIdAndDescription(dbModuleId.get().getPlantIdAndDescription());
			dbAdhocModuleId.setWarehouseIdAndDescription(dbModuleId.get().getWarehouseIdAndDescription());
			dbAdhocModuleId.setModuleIdAndDescription(dbModuleId.get().getModuleId()+"-"+dbModuleId.get().getModuleDescription());
			dbAdhocModuleId.setCreatedBy(loginUserID);
			dbAdhocModuleId.setUpdatedBy(loginUserID);
			dbAdhocModuleId.setCreatedOn(new Date());
			dbAdhocModuleId.setUpdatedOn(new Date());
			return adhocModuleIdRepository.save(dbAdhocModuleId);
		}
	}

	/**
	 * updateAdhocModuleId
	 * @param loginUserID
	 * @param adhocModuleId
	 * @param updateAdhocModuleId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AdhocModuleId updateAdhocModuleId (String warehouseId, String moduleId, String adhocModuleId,String companyCodeId,String languageId,String plantId,String loginUserID,
											  UpdateAdhocModuleId updateAdhocModuleId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		AdhocModuleId dbAdhocModuleId = getAdhocModuleId(warehouseId, moduleId, adhocModuleId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateAdhocModuleId, dbAdhocModuleId, CommonUtils.getNullPropertyNames(updateAdhocModuleId));
		dbAdhocModuleId.setUpdatedBy(loginUserID);
		dbAdhocModuleId.setUpdatedOn(new Date());
		return adhocModuleIdRepository.save(dbAdhocModuleId);
	}

	/**
	 * deleteAdhocModuleId
	 * @param loginUserID
	 * @param adhocModuleId
	 */
	public void deleteAdhocModuleId (String warehouseId, String moduleId, String adhocModuleId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		AdhocModuleId dbAdhocModuleId = getAdhocModuleId(warehouseId, moduleId, adhocModuleId,companyCodeId,languageId,plantId);
		if ( dbAdhocModuleId != null) {
			dbAdhocModuleId.setDeletionIndicator(1L);
			dbAdhocModuleId.setUpdatedBy(loginUserID);
			adhocModuleIdRepository.save(dbAdhocModuleId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + adhocModuleId);
		}
	}
	//Find AdhocModuleId

	public List<AdhocModuleId> findAdhocModuleId(FindAdhocModuleId findAdhocModuleId) throws ParseException {

		AdhocModuleIdSpecification spec = new AdhocModuleIdSpecification(findAdhocModuleId);
		List<AdhocModuleId> results = adhocModuleIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<AdhocModuleId> newAdhocModuleId=new ArrayList<>();
		for(AdhocModuleId dbAdhocModuleId:results) {
			if (dbAdhocModuleId.getCompanyIdAndDescription() != null&&dbAdhocModuleId.getPlantIdAndDescription()!=null&&dbAdhocModuleId.getWarehouseIdAndDescription()!=null&&dbAdhocModuleId.getModuleIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbAdhocModuleId.getCompanyCodeId(), dbAdhocModuleId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbAdhocModuleId.getPlantId(),dbAdhocModuleId.getLanguageId(), dbAdhocModuleId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbAdhocModuleId.getWarehouseId(),dbAdhocModuleId.getLanguageId(), dbAdhocModuleId.getCompanyCodeId(), dbAdhocModuleId.getPlantId());
				IKeyValuePair iKeyValuePair3=moduleIdRepository.getModuleIdAndDescription(dbAdhocModuleId.getModuleId(),dbAdhocModuleId.getLanguageId(), dbAdhocModuleId.getCompanyCodeId(), dbAdhocModuleId.getPlantId(), dbAdhocModuleId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbAdhocModuleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbAdhocModuleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbAdhocModuleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbAdhocModuleId.setModuleIdAndDescription(iKeyValuePair3.getModuleId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newAdhocModuleId.add(dbAdhocModuleId);
		}
		return newAdhocModuleId;
	}
}
