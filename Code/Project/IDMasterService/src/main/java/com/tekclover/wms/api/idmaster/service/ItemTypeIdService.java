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
import com.tekclover.wms.api.idmaster.model.itemtypeid.AddItemTypeId;
import com.tekclover.wms.api.idmaster.model.itemtypeid.ItemTypeId;
import com.tekclover.wms.api.idmaster.model.itemtypeid.UpdateItemTypeId;
import com.tekclover.wms.api.idmaster.repository.ItemTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemTypeIdService extends BaseService {
	
	@Autowired
	private ItemTypeIdRepository itemTypeIdRepository;
	
	/**
	 * getItemTypeIds
	 * @return
	 */
	public List<ItemTypeId> getItemTypeIds () {
		List<ItemTypeId> itemTypeIdList =  itemTypeIdRepository.findAll();
		itemTypeIdList = itemTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return itemTypeIdList;
	}
	
	/**
	 * getItemTypeId
	 * @param itemTypeId
	 * @return
	 */
	public ItemTypeId getItemTypeId (String warehouseId, Long itemTypeId) {
		Optional<ItemTypeId> dbItemTypeId = 
				itemTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								itemTypeId,
								0L
								);
		if (dbItemTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"itemTypeId - " + itemTypeId +
						" doesn't exist.");
			
		} 
		return dbItemTypeId.get();
	}
	
//	/**
//	 * 
//	 * @param searchItemTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<ItemTypeId> findItemTypeId(SearchItemTypeId searchItemTypeId) 
//			throws ParseException {
//		ItemTypeIdSpecification spec = new ItemTypeIdSpecification(searchItemTypeId);
//		List<ItemTypeId> results = itemTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}
	
	/**
	 * createItemTypeId
	 * @param newItemTypeId
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemTypeId createItemTypeId (AddItemTypeId newItemTypeId, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ItemTypeId dbItemTypeId = new ItemTypeId();
		log.info("newItemTypeId : " + newItemTypeId);
		BeanUtils.copyProperties(newItemTypeId, dbItemTypeId, CommonUtils.getNullPropertyNames(newItemTypeId));
		dbItemTypeId.setCompanyCodeId(getCompanyCode());
		dbItemTypeId.setPlantId(getPlantId());
		dbItemTypeId.setDeletionIndicator(0L);
		dbItemTypeId.setCreatedBy(loginUserID);
		dbItemTypeId.setUpdatedBy(loginUserID);
		dbItemTypeId.setCreatedOn(new Date());
		dbItemTypeId.setUpdatedOn(new Date());
		return itemTypeIdRepository.save(dbItemTypeId);
	}
	
	/**
	 * updateItemTypeId
	 * @param loginUserId 
	 * @param itemTypeId
	 * @param updateItemTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemTypeId updateItemTypeId (String warehouseId, Long itemTypeId, String loginUserID,
			UpdateItemTypeId updateItemTypeId) 
			throws IllegalAccessException, InvocationTargetException {
		ItemTypeId dbItemTypeId = getItemTypeId(warehouseId, itemTypeId);
		BeanUtils.copyProperties(updateItemTypeId, dbItemTypeId, CommonUtils.getNullPropertyNames(updateItemTypeId));
		dbItemTypeId.setUpdatedBy(loginUserID);
		dbItemTypeId.setUpdatedOn(new Date());
		return itemTypeIdRepository.save(dbItemTypeId);
	}
	
	/**
	 * deleteItemTypeId
	 * @param loginUserID 
	 * @param itemTypeId
	 */
	public void deleteItemTypeId (String warehouseId, Long itemTypeId,  String loginUserID) {
		ItemTypeId dbItemTypeId = getItemTypeId(warehouseId, itemTypeId);
		if ( dbItemTypeId != null) {
			dbItemTypeId.setDeletionIndicator(1L);
			dbItemTypeId.setUpdatedBy(loginUserID);
			itemTypeIdRepository.save(dbItemTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + itemTypeId);
		}
	}
}
