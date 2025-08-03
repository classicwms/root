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
import com.tekclover.wms.api.idmaster.model.usertypeid.FindUserTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.model.workcenterid.WorkCenterId;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.UserTypeIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.usertypeid.AddUserTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.UpdateUserTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.UserTypeId;
import com.tekclover.wms.api.idmaster.repository.UserTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserTypeIdService {

	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private UserTypeIdRepository userTypeIdRepository;

	/**
	 * getUserTypeIds
	 * @return
	 */
	public List<UserTypeId> getUserTypeIds () {
		List<UserTypeId> userTypeIdList =  userTypeIdRepository.findAll();
		userTypeIdList = userTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<UserTypeId> newUserTypeId=new ArrayList<>();
		for(UserTypeId dbUserTypeId:userTypeIdList) {
			if (dbUserTypeId.getCompanyIdAndDescription() != null&&dbUserTypeId.getPlantIdAndDescription()!=null&&dbUserTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbUserTypeId.getCompanyCodeId(), dbUserTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbUserTypeId.getPlantId(), dbUserTypeId.getLanguageId(), dbUserTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbUserTypeId.getWarehouseId(), dbUserTypeId.getLanguageId(), dbUserTypeId.getCompanyCodeId(), dbUserTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbUserTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbUserTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbUserTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newUserTypeId.add(dbUserTypeId);
		}
		return newUserTypeId;
	}

	/**
	 * getUserTypeId
	 * @param userTypeId
	 * @return
	 */
	public UserTypeId getUserTypeId ( String warehouseId,Long userTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<UserTypeId> dbUserTypeId =
				userTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndUserTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						userTypeId,
						languageId,
						0L
				);
		if (dbUserTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"userTypeId - " + userTypeId +
					" doesn't exist.");

		}
		UserTypeId newUserTypeId = new UserTypeId();
		BeanUtils.copyProperties(dbUserTypeId.get(),newUserTypeId, CommonUtils.getNullPropertyNames(dbUserTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newUserTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newUserTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newUserTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newUserTypeId;
	}

//	/**
//	 * 
//	 * @param searchUserTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<UserTypeId> findUserTypeId(SearchUserTypeId searchUserTypeId) 
//			throws ParseException {
//		UserTypeIdSpecification spec = new UserTypeIdSpecification(searchUserTypeId);
//		List<UserTypeId> results = userTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createUserTypeId
	 * @param newUserTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UserTypeId createUserTypeId (AddUserTypeId newUserTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		UserTypeId dbUserTypeId = new UserTypeId();
		Optional<UserTypeId> duplicateUsertypeId = userTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndUserTypeIdAndLanguageIdAndDeletionIndicator(newUserTypeId.getCompanyCodeId(), newUserTypeId.getPlantId(), newUserTypeId.getWarehouseId(), newUserTypeId.getUserTypeId(), newUserTypeId.getLanguageId(), 0L);
		if (!duplicateUsertypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newUserTypeId.getWarehouseId(), newUserTypeId.getCompanyCodeId(), newUserTypeId.getPlantId(), newUserTypeId.getLanguageId());
			log.info("newUserTypeId : " + newUserTypeId);
			BeanUtils.copyProperties(newUserTypeId, dbUserTypeId, CommonUtils.getNullPropertyNames(newUserTypeId));
			dbUserTypeId.setDeletionIndicator(0L);
			dbUserTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbUserTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbUserTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbUserTypeId.setCreatedBy(loginUserID);
			dbUserTypeId.setUpdatedBy(loginUserID);
			dbUserTypeId.setCreatedOn(new Date());
			dbUserTypeId.setUpdatedOn(new Date());
			return userTypeIdRepository.save(dbUserTypeId);
		}
	}

	/**
	 * updateUserTypeId
	 * @param loginUserID
	 * @param userTypeId
	 * @param updateUserTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public UserTypeId updateUserTypeId ( String warehouseId,Long userTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
										 UpdateUserTypeId updateUserTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		UserTypeId dbUserTypeId = getUserTypeId(warehouseId,userTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateUserTypeId, dbUserTypeId, CommonUtils.getNullPropertyNames(updateUserTypeId));
		dbUserTypeId.setUpdatedBy(loginUserID);
		dbUserTypeId.setUpdatedOn(new Date());
		return userTypeIdRepository.save(dbUserTypeId);
	}

	/**
	 * deleteUserTypeId
	 * @param loginUserID
	 * @param userTypeId
	 */
	public void deleteUserTypeId ( String warehouseId, Long userTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		UserTypeId dbUserTypeId = getUserTypeId(warehouseId,userTypeId,companyCodeId,languageId,plantId);
		if ( dbUserTypeId != null) {
			dbUserTypeId.setDeletionIndicator(1L);
			dbUserTypeId.setUpdatedBy(loginUserID);
			userTypeIdRepository.save(dbUserTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + userTypeId);
		}
	}

	//Find UserTypeId
	public List<UserTypeId> findUserTypeId(FindUserTypeId findUserTypeId) throws ParseException {

		UserTypeIdSpecification spec = new UserTypeIdSpecification(findUserTypeId);
		List<UserTypeId> results = userTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<UserTypeId> newUserTypeId=new ArrayList<>();
		for(UserTypeId dbUserTypeId:results) {
			if (dbUserTypeId.getCompanyIdAndDescription() != null&&dbUserTypeId.getPlantIdAndDescription()!=null&&dbUserTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbUserTypeId.getCompanyCodeId(), dbUserTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbUserTypeId.getPlantId(), dbUserTypeId.getLanguageId(), dbUserTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbUserTypeId.getWarehouseId(), dbUserTypeId.getLanguageId(), dbUserTypeId.getCompanyCodeId(), dbUserTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbUserTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbUserTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbUserTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newUserTypeId.add(dbUserTypeId);
		}
		return newUserTypeId;
	}
}
