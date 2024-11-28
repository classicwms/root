package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import com.tekclover.wms.api.idmaster.model.levelid.LevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.AddSubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.FindSubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.SubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.UpdateSubLevelId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.SubLevelIdSpecification;
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
public class SubLevelIdService{
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private LevelIdRepository levelIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private LevelIdService levelIdService;
	@Autowired
	private SubLevelIdRepository subLevelIdRepository;

	/**
	 * getSubLevelIds
	 * @return
	 */
	public List<SubLevelId> getSubLevelIds () {
		List<SubLevelId> subLevelIdList =  subLevelIdRepository.findAll();
		subLevelIdList = subLevelIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<SubLevelId> newSubLevelId=new ArrayList<>();
		for(SubLevelId dbSubLevelId:subLevelIdList) {
			if (dbSubLevelId.getCompanyIdAndDescription() != null&&dbSubLevelId.getPlantIdAndDescription()!=null&&dbSubLevelId.getWarehouseIdAndDescription()!=null&&dbSubLevelId.getLevelIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbSubLevelId.getCompanyCodeId(), dbSubLevelId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbSubLevelId.getPlantId(), dbSubLevelId.getLanguageId(), dbSubLevelId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbSubLevelId.getWarehouseId(), dbSubLevelId.getLanguageId(), dbSubLevelId.getCompanyCodeId(), dbSubLevelId.getPlantId());
				IKeyValuePair iKeyValuePair3 = levelIdRepository.getLevelIdAndDescription(dbSubLevelId.getLevelId(), dbSubLevelId.getLanguageId(), dbSubLevelId.getCompanyCodeId(), dbSubLevelId.getPlantId(), dbSubLevelId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbSubLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbSubLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbSubLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbSubLevelId.setLevelIdAndDescription(iKeyValuePair3.getLevelId() + "-" + iKeyValuePair3.getDescription());//LevelIdAndDescription Mapped By LevelAndDescription
				}
			}
			newSubLevelId.add(dbSubLevelId);
		}
		return newSubLevelId;
	}

	/**
	 * getSubLevelId
	 * @param subLevelId
	 * @return
	 */
	public SubLevelId getSubLevelId (String warehouseId, String subLevelId,Long levelId,String companyCodeId,String languageId,String plantId) {
		Optional<SubLevelId> dbSubLevelId =
				subLevelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubLevelIdAndLevelIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						subLevelId,
						levelId,
						languageId,
						0L
				);
		if (dbSubLevelId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"subLevelId - " + subLevelId +
					"levelId - " + levelId +
					" doesn't exist.");

		}
		SubLevelId newSubLevelId = new SubLevelId();
		BeanUtils.copyProperties(dbSubLevelId.get(),newSubLevelId, CommonUtils.getNullPropertyNames(dbSubLevelId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= levelIdRepository.getLevelIdAndDescription(levelId,languageId,companyCodeId,plantId,warehouseId);
		if(iKeyValuePair!=null) {
			newSubLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newSubLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newSubLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newSubLevelId.setLevelIdAndDescription(iKeyValuePair3.getLevelId() + "-" + iKeyValuePair3.getDescription());//LevelIdAndDescription mapped by levelAndLevelReference
		}
			return newSubLevelId;
	}

	/**
	 * createSubLevelId
	 * @param newSubLevelId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubLevelId createSubLevelId (AddSubLevelId newSubLevelId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SubLevelId dbSubLevelId = new SubLevelId();
		Optional<SubLevelId> duplicateSubLevelId = subLevelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubLevelIdAndLevelIdAndLanguageIdAndDeletionIndicator(newSubLevelId.getCompanyCodeId(), newSubLevelId.getPlantId(), newSubLevelId.getWarehouseId(), newSubLevelId.getSubLevelId(), newSubLevelId.getLevelId(), newSubLevelId.getLanguageId(), 0L);
		if (!duplicateSubLevelId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			LevelId dbLevelId=levelIdService.getLevelId(newSubLevelId.getWarehouseId(), newSubLevelId.getLevelId(), newSubLevelId.getCompanyCodeId(), newSubLevelId.getLanguageId(), newSubLevelId.getPlantId());
			log.info("newSubLevelId : " + newSubLevelId);
			BeanUtils.copyProperties(newSubLevelId, dbSubLevelId, CommonUtils.getNullPropertyNames(newSubLevelId));
			dbSubLevelId.setDeletionIndicator(0L);
			dbSubLevelId.setCompanyIdAndDescription(dbLevelId.getCompanyIdAndDescription());
			dbSubLevelId.setPlantIdAndDescription(dbLevelId.getPlantIdAndDescription());
			dbSubLevelId.setWarehouseIdAndDescription(dbLevelId.getWarehouseIdAndDescription());
			dbSubLevelId.setLevelIdAndDescription(dbLevelId.getLevel()+"-"+dbLevelId.getLevelReference());//levelIdAndDescription mapped by levelAndLevelReference
			dbSubLevelId.setCreatedBy(loginUserID);
			dbSubLevelId.setUpdatedBy(loginUserID);
			dbSubLevelId.setCreatedOn(new Date());
			dbSubLevelId.setUpdatedOn(new Date());
			return subLevelIdRepository.save(dbSubLevelId);
		}
	}

	/**
	 * updateSubLevelId
	 * @param loginUserID
	 * @param subLevelId
	 * @param updateSubLevelId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubLevelId updateSubLevelId (String warehouseId, String subLevelId,Long levelId,String companyCodeId,String languageId,String plantId,String loginUserID,
										UpdateSubLevelId updateSubLevelId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SubLevelId dbSubLevelId = getSubLevelId(warehouseId, subLevelId,levelId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateSubLevelId, dbSubLevelId, CommonUtils.getNullPropertyNames(updateSubLevelId));
		dbSubLevelId.setUpdatedBy(loginUserID);
		dbSubLevelId.setUpdatedOn(new Date());
		return subLevelIdRepository.save(dbSubLevelId);
	}

	/**
	 * deleteSubLevelId
	 * @param loginUserID
	 * @param subLevelId
	 */
	public void deleteSubLevelId (String warehouseId, String subLevelId,Long levelId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		SubLevelId dbSubLevelId = getSubLevelId(warehouseId, subLevelId,levelId,companyCodeId,languageId,plantId);
		if ( dbSubLevelId != null) {
			dbSubLevelId.setDeletionIndicator(1L);
			dbSubLevelId.setUpdatedBy(loginUserID);
			subLevelIdRepository.save(dbSubLevelId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + subLevelId);
		}
	}

	//Find SubLevelId
	public List<SubLevelId> findSubLevelId(FindSubLevelId findSubLevelId) throws ParseException {

		SubLevelIdSpecification spec = new SubLevelIdSpecification(findSubLevelId);
		List<SubLevelId> results = subLevelIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<SubLevelId> newSubLevelId=new ArrayList<>();
		for(SubLevelId dbSubLevelId:results) {
			if (dbSubLevelId.getCompanyIdAndDescription() != null&&dbSubLevelId.getPlantIdAndDescription()!=null&&dbSubLevelId.getWarehouseIdAndDescription()!=null&&dbSubLevelId.getLevelIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbSubLevelId.getCompanyCodeId(), dbSubLevelId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbSubLevelId.getPlantId(), dbSubLevelId.getLanguageId(), dbSubLevelId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbSubLevelId.getWarehouseId(), dbSubLevelId.getLanguageId(), dbSubLevelId.getCompanyCodeId(), dbSubLevelId.getPlantId());
				IKeyValuePair iKeyValuePair3=levelIdRepository.getLevelIdAndDescription(dbSubLevelId.getLevelId(), dbSubLevelId.getLanguageId(), dbSubLevelId.getCompanyCodeId(), dbSubLevelId.getPlantId(), dbSubLevelId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbSubLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbSubLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbSubLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbSubLevelId.setLevelIdAndDescription(iKeyValuePair3.getLevelId() + "-" + iKeyValuePair3.getDescription());//LevelIdAndDescription Mapped By LevelAndDescription
				}
			}
			newSubLevelId.add(dbSubLevelId);
		}
		return newSubLevelId;
	}
}
