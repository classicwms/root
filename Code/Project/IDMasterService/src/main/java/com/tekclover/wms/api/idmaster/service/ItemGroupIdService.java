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
import com.tekclover.wms.api.idmaster.model.itemgroupid.AddItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.ItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.UpdateItemGroupId;
import com.tekclover.wms.api.idmaster.repository.ItemGroupIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemGroupIdService extends BaseService {
	
	@Autowired
	private ItemGroupIdRepository itemGroupIdRepository;
	
	/**
	 * getItemGroupIds
	 * @return
	 */
	public List<ItemGroupId> getItemGroupIds () {
		List<ItemGroupId> itemGroupIdList =  itemGroupIdRepository.findAll();
		itemGroupIdList = itemGroupIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return itemGroupIdList;
	}
	
	/**
	 * getItemGroupId
	 * @param itemGroupId
	 * @return
	 */
	public ItemGroupId getItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId) {
		Optional<ItemGroupId> dbItemGroupId = 
				itemGroupIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								itemTypeId,
								itemGroupId,
								0L
								);
		if (dbItemGroupId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"itemTypeId - " + itemTypeId +
						"itemGroupId - " + itemGroupId +
						" doesn't exist.");						
			
		} 
		return dbItemGroupId.get();
	}
	
//	/**
//	 * 
//	 * @param searchItemGroupId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<ItemGroupId> findItemGroupId(SearchItemGroupId searchItemGroupId) 
//			throws ParseException {
//		ItemGroupIdSpecification spec = new ItemGroupIdSpecification(searchItemGroupId);
//		List<ItemGroupId> results = itemGroupIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createItemGroupId
	 * @param newItemGroupId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemGroupId createItemGroupId (AddItemGroupId newItemGroupId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ItemGroupId dbItemGroupId = new ItemGroupId();
		log.info("newItemGroupId : " + newItemGroupId);
		BeanUtils.copyProperties(newItemGroupId, dbItemGroupId, CommonUtils.getNullPropertyNames(newItemGroupId));
		dbItemGroupId.setCompanyCodeId(getCompanyCode());
		dbItemGroupId.setPlantId(getPlantId());
		dbItemGroupId.setDeletionIndicator(0L);
		dbItemGroupId.setCreatedBy(loginUserID);
		dbItemGroupId.setUpdatedBy(loginUserID);
		dbItemGroupId.setCreatedOn(new Date());
		dbItemGroupId.setUpdatedOn(new Date());
		return itemGroupIdRepository.save(dbItemGroupId);
	}
	
	/**
	 * updateItemGroupId
	 * @param loginUserId 
	 * @param itemGroupId
	 * @param updateItemGroupId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemGroupId updateItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, String loginUserID,
			UpdateItemGroupId updateItemGroupId) 
			throws IllegalAccessException, InvocationTargetException {
		ItemGroupId dbItemGroupId = getItemGroupId(warehouseId, itemTypeId, itemGroupId);
		BeanUtils.copyProperties(updateItemGroupId, dbItemGroupId, CommonUtils.getNullPropertyNames(updateItemGroupId));
		dbItemGroupId.setUpdatedBy(loginUserID);
		dbItemGroupId.setUpdatedOn(new Date());
		return itemGroupIdRepository.save(dbItemGroupId);
	}
	
	/**
	 * deleteItemGroupId
	 * @param loginUserID 
	 * @param itemGroupId
	 */
	public void deleteItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, String loginUserID) {
		ItemGroupId dbItemGroupId = getItemGroupId(warehouseId, itemTypeId, itemGroupId);
		if ( dbItemGroupId != null) {
			dbItemGroupId.setDeletionIndicator(1L);
			dbItemGroupId.setUpdatedBy(loginUserID);
			itemGroupIdRepository.save(dbItemGroupId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + itemGroupId);
		}
	}
}
