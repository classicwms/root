package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.AddSubItemGroupId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.SubItemGroupId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.UpdateSubItemGroupId;
import com.tekclover.wms.api.idmaster.repository.SubItemGroupIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubItemGroupIdService extends BaseService {
	
	@Autowired
	private SubItemGroupIdRepository subItemGroupIdRepository;
	
	/**
	 * getSubItemGroupIds
	 * @return
	 */
	public List<SubItemGroupId> getSubItemGroupIds () {
		List<SubItemGroupId> subItemGroupIdList =  subItemGroupIdRepository.findAll();
		subItemGroupIdList = subItemGroupIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return subItemGroupIdList;
	}
	
	/**
	 * getSubItemGroupId
	 * @param subItemGroupId
	 * @param subItemGroup 
	 * @param subItemGroupId2 
	 * @return
	 */
	public SubItemGroupId getSubItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroupId, String subItemGroup) {
		Optional<SubItemGroupId> dbSubItemGroupId = 
				subItemGroupIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndSubItemGroupAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								itemTypeId,
								itemGroupId,
								subItemGroupId,
								subItemGroup,
								getLanguageId(),
								0L
								);
		if (dbSubItemGroupId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"itemTypeId - " + itemTypeId +
						"itemGroupId - " + itemGroupId +
						"subItemGroupId - " + subItemGroupId +
						"subItemGroup - " + subItemGroup +
						" doesn't exist.");
			
		} 
		return dbSubItemGroupId.get();
	}
	
//	/**
//	 * 
//	 * @param searchSubItemGroupId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<SubItemGroupId> findSubItemGroupId(SearchSubItemGroupId searchSubItemGroupId) 
//			throws ParseException {
//		SubItemGroupIdSpecification spec = new SubItemGroupIdSpecification(searchSubItemGroupId);
//		List<SubItemGroupId> results = subItemGroupIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createSubItemGroupId
	 * @param newSubItemGroupId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubItemGroupId createSubItemGroupId (AddSubItemGroupId newSubItemGroupId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		SubItemGroupId dbSubItemGroupId = new SubItemGroupId();
		log.info("newSubItemGroupId : " + newSubItemGroupId);
		BeanUtils.copyProperties(newSubItemGroupId, dbSubItemGroupId, CommonUtils.getNullPropertyNames(newSubItemGroupId));
		dbSubItemGroupId.setCompanyCodeId(getCompanyCode());
		dbSubItemGroupId.setPlantId(getPlantId());
		dbSubItemGroupId.setDeletionIndicator(0L);
		dbSubItemGroupId.setCreatedBy(loginUserID);
		dbSubItemGroupId.setUpdatedBy(loginUserID);
		dbSubItemGroupId.setCreatedOn(new Date());
		dbSubItemGroupId.setUpdatedOn(new Date());
		return subItemGroupIdRepository.save(dbSubItemGroupId);
	}
	
	/**
	 * updateSubItemGroupId
	 * @param loginUserId 
	 * @param subItemGroupId
	 * @param updateSubItemGroupId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubItemGroupId updateSubItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroupId, 
			String subItemGroup, String loginUserID, UpdateSubItemGroupId updateSubItemGroupId) 
			throws IllegalAccessException, InvocationTargetException {
		SubItemGroupId dbSubItemGroupId = 
				getSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId, subItemGroup);
		BeanUtils.copyProperties(updateSubItemGroupId, dbSubItemGroupId, CommonUtils.getNullPropertyNames(updateSubItemGroupId));
		dbSubItemGroupId.setUpdatedBy(loginUserID);
		dbSubItemGroupId.setUpdatedOn(new Date());
		return subItemGroupIdRepository.save(dbSubItemGroupId);
	}
	
	/**
	 * deleteSubItemGroupId
	 * @param loginUserID 
	 * @param subItemGroupId
	 */
	public void deleteSubItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroupId, 
			String subItemGroup, String loginUserID) {
		SubItemGroupId dbSubItemGroupId = getSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId, subItemGroup);
		if ( dbSubItemGroupId != null) {
			dbSubItemGroupId.setDeletionIndicator(1L);
			dbSubItemGroupId.setUpdatedBy(loginUserID);
			subItemGroupIdRepository.save(dbSubItemGroupId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + subItemGroupId);
		}
	}
}
