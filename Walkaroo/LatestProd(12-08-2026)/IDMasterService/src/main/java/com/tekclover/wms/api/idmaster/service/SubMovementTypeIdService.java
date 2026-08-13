package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.movementtypeid.MovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.AddSubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.FindSubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.SubMovementTypeId;
import com.tekclover.wms.api.idmaster.model.submovementtypeid.UpdateSubMovementTypeId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.SubMovementTypeIdSpecification;
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
public class SubMovementTypeIdService {

	@Autowired
	private MovementTypeIdService movementTypeIdService;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private MovementTypeIdRepository movementTypeIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private SubMovementTypeIdRepository subMovementTypeIdRepository;

	/**
	 * getSubMovementTypeIds
	 * @return
	 */
	public List<SubMovementTypeId> getSubMovementTypeIds () {
		List<SubMovementTypeId> subMovementTypeIdList =  subMovementTypeIdRepository.findAll();
		subMovementTypeIdList = subMovementTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<SubMovementTypeId> newSubMovementTypeId=new ArrayList<>();
		for(SubMovementTypeId dbSubMovementTypeId:subMovementTypeIdList) {
			if (dbSubMovementTypeId.getCompanyIdAndDescription() != null&&dbSubMovementTypeId.getPlantIdAndDescription()!=null&&dbSubMovementTypeId.getWarehouseIdAndDescription()!=null&&dbSubMovementTypeId.getMovementTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbSubMovementTypeId.getCompanyCodeId(), dbSubMovementTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbSubMovementTypeId.getPlantId(), dbSubMovementTypeId.getLanguageId(), dbSubMovementTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbSubMovementTypeId.getWarehouseId(), dbSubMovementTypeId.getLanguageId(), dbSubMovementTypeId.getCompanyCodeId(), dbSubMovementTypeId.getPlantId());
				IKeyValuePair iKeyValuePair3 = movementTypeIdRepository.getMovementTypeIdAndDescription(dbSubMovementTypeId.getMovementTypeId(), dbSubMovementTypeId.getLanguageId(), dbSubMovementTypeId.getWarehouseId(), dbSubMovementTypeId.getCompanyCodeId(), dbSubMovementTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbSubMovementTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbSubMovementTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbSubMovementTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbSubMovementTypeId.setMovementTypeIdAndDescription(iKeyValuePair3.getMovementTypeId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newSubMovementTypeId.add(dbSubMovementTypeId);
		}
		return newSubMovementTypeId;
	}

	/**
	 * getSubMovementTypeId
	 * @param subMovementTypeId
	 * @return
	 */
	public SubMovementTypeId getSubMovementTypeId (String warehouseId, String movementTypeId, String subMovementTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<SubMovementTypeId> dbSubMovementTypeId =
				subMovementTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubMovementTypeIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						subMovementTypeId,
						movementTypeId,
						languageId,
						0L
				);
		if (dbSubMovementTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"movementTypeId - " + movementTypeId +
					"subMovementTypeId - " + subMovementTypeId +
					" doesn't exist.");

		}
		SubMovementTypeId newSubMovementTypeId = new SubMovementTypeId();
		BeanUtils.copyProperties(dbSubMovementTypeId.get(),newSubMovementTypeId, CommonUtils.getNullPropertyNames(dbSubMovementTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= movementTypeIdRepository.getMovementTypeIdAndDescription(movementTypeId,languageId,warehouseId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newSubMovementTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newSubMovementTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newSubMovementTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newSubMovementTypeId.setMovementTypeIdAndDescription(iKeyValuePair3.getMovementTypeId() + "-" + iKeyValuePair3.getDescription());
		}
		return newSubMovementTypeId;

	}

	/**
	 * createSubMovementTypeId
	 * @param newSubMovementTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubMovementTypeId createSubMovementTypeId (AddSubMovementTypeId newSubMovementTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SubMovementTypeId dbSubMovementTypeId = new SubMovementTypeId();
		Optional<SubMovementTypeId> duplicateSubMovementTypeId = subMovementTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndSubMovementTypeIdAndMovementTypeIdAndLanguageIdAndDeletionIndicator(newSubMovementTypeId.getCompanyCodeId(), newSubMovementTypeId.getPlantId(), newSubMovementTypeId.getWarehouseId(), newSubMovementTypeId.getSubMovementTypeId(), newSubMovementTypeId.getMovementTypeId(), newSubMovementTypeId.getLanguageId(), 0l);
		if (!duplicateSubMovementTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			MovementTypeId dbMovementTypeId=movementTypeIdService.getMovementTypeId(newSubMovementTypeId.getWarehouseId(), newSubMovementTypeId.getMovementTypeId(), newSubMovementTypeId.getCompanyCodeId(), newSubMovementTypeId.getLanguageId(), newSubMovementTypeId.getPlantId());
			log.info("newSubMovementTypeId : " + newSubMovementTypeId);
			BeanUtils.copyProperties(newSubMovementTypeId, dbSubMovementTypeId, CommonUtils.getNullPropertyNames(newSubMovementTypeId));
			dbSubMovementTypeId.setDeletionIndicator(0L);
			dbSubMovementTypeId.setCompanyIdAndDescription(dbMovementTypeId.getCompanyIdAndDescription());
			dbSubMovementTypeId.setPlantIdAndDescription(dbMovementTypeId.getPlantIdAndDescription());
			dbSubMovementTypeId.setWarehouseIdAndDescription(dbMovementTypeId.getWarehouseIdAndDescription());
			dbSubMovementTypeId.setMovementTypeIdAndDescription(dbMovementTypeId.getMovementTypeId()+"-"+dbMovementTypeId.getMovementTypeText());
			dbSubMovementTypeId.setCreatedBy(loginUserID);
			dbSubMovementTypeId.setUpdatedBy(loginUserID);
			dbSubMovementTypeId.setCreatedOn(new Date());
			dbSubMovementTypeId.setUpdatedOn(new Date());
			return subMovementTypeIdRepository.save(dbSubMovementTypeId);
		}
	}

	/**
	 * updateSubMovementTypeId
	 * @param loginUserID
	 * @param subMovementTypeId
	 * @param updateSubMovementTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubMovementTypeId updateSubMovementTypeId (String warehouseId,String movementTypeId, String subMovementTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
													  UpdateSubMovementTypeId updateSubMovementTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SubMovementTypeId dbSubMovementTypeId = getSubMovementTypeId( warehouseId,movementTypeId,subMovementTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateSubMovementTypeId, dbSubMovementTypeId, CommonUtils.getNullPropertyNames(updateSubMovementTypeId));
		dbSubMovementTypeId.setUpdatedBy(loginUserID);
		dbSubMovementTypeId.setUpdatedOn(new Date());
		return subMovementTypeIdRepository.save(dbSubMovementTypeId);
	}

	/**
	 * deleteSubMovementTypeId
	 * @param loginUserID
	 * @param subMovementTypeId
	 */
	public void deleteSubMovementTypeId (String warehouseId,String movementTypeId, String subMovementTypeId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		SubMovementTypeId dbSubMovementTypeId = getSubMovementTypeId( warehouseId, movementTypeId,subMovementTypeId,companyCodeId,languageId,plantId);
		if ( dbSubMovementTypeId != null) {
			dbSubMovementTypeId.setDeletionIndicator(1L);
			dbSubMovementTypeId.setUpdatedBy(loginUserID);
			subMovementTypeIdRepository.save(dbSubMovementTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + subMovementTypeId);
		}
	}

	//Find SubMovementTypeId
	public List<SubMovementTypeId> findSubMovementTypeId(FindSubMovementTypeId findSubMovementTypeId) throws ParseException {

		SubMovementTypeIdSpecification spec = new SubMovementTypeIdSpecification(findSubMovementTypeId);
		List<SubMovementTypeId> results = subMovementTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<SubMovementTypeId> newSubMovementTypeId=new ArrayList<>();
		for(SubMovementTypeId dbSubMovementTypeId:results) {
			if (dbSubMovementTypeId.getCompanyIdAndDescription() != null&&dbSubMovementTypeId.getPlantIdAndDescription()!=null&&dbSubMovementTypeId.getWarehouseIdAndDescription()!=null&&dbSubMovementTypeId.getMovementTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbSubMovementTypeId.getCompanyCodeId(), dbSubMovementTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbSubMovementTypeId.getPlantId(), dbSubMovementTypeId.getLanguageId(), dbSubMovementTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbSubMovementTypeId.getWarehouseId(), dbSubMovementTypeId.getLanguageId(), dbSubMovementTypeId.getCompanyCodeId(), dbSubMovementTypeId.getPlantId());
				IKeyValuePair iKeyValuePair3=movementTypeIdRepository.getMovementTypeIdAndDescription(dbSubMovementTypeId.getMovementTypeId(), dbSubMovementTypeId.getLanguageId(), dbSubMovementTypeId.getWarehouseId(), dbSubMovementTypeId.getCompanyCodeId(), dbSubMovementTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbSubMovementTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbSubMovementTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbSubMovementTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbSubMovementTypeId.setMovementTypeIdAndDescription(iKeyValuePair3.getMovementTypeId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newSubMovementTypeId.add(dbSubMovementTypeId);
		}
		return newSubMovementTypeId;
	}
}
