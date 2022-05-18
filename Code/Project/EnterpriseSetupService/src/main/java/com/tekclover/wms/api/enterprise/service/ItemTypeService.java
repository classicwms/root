package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.itemtype.AddItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.ItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.SearchItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.UpdateItemType;
import com.tekclover.wms.api.enterprise.repository.ItemTypeRepository;
import com.tekclover.wms.api.enterprise.repository.specification.ItemTypeSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemTypeService extends BaseService {
	
	@Autowired
	private ItemTypeRepository itemtypeRepository;
	
	/**
	 * getItemTypes
	 * @return
	 */
	public List<ItemType> getItemTypes () {
		List<ItemType> itemtypeList = itemtypeRepository.findAll();
		log.info("itemtypeList : " + itemtypeList);
		itemtypeList = itemtypeList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return itemtypeList;
	}
	
	/**
	 * getItemType
	 * @param warehouseId
	 * @param itemTypeId	 
	 * @return
	 */
	public ItemType getItemType (String warehouseId, Long itemTypeId) {
		Optional<ItemType> itemtype = 
				itemtypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, itemTypeId, 0L);
		if (itemtype.isEmpty()) {
			throw new BadRequestException("The given ItemType Id : " + itemTypeId + " doesn't exist.");
		} 
		return itemtype.get();
	}
	
	/**
	 * findItemType
	 * @param searchItemType
	 * @return
	 * @throws ParseException
	 */
	public List<ItemType> findItemType(SearchItemType searchItemType) throws Exception {
		if (searchItemType.getStartCreatedOn() != null && searchItemType.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchItemType.getStartCreatedOn(), searchItemType.getEndCreatedOn());
			searchItemType.setStartCreatedOn(dates[0]);
			searchItemType.setEndCreatedOn(dates[1]);
		}
		
		ItemTypeSpecification spec = new ItemTypeSpecification(searchItemType);
		List<ItemType> results = itemtypeRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createItemType
	 * @param newItemType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemType createItemType (AddItemType newItemType, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<ItemType> optItemType = 
				itemtypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newItemType.getWarehouseId(),
						newItemType.getItemTypeId(),
						0L);
		if (!optItemType.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		ItemType dbItemType = new ItemType();
		BeanUtils.copyProperties(newItemType, dbItemType, CommonUtils.getNullPropertyNames(newItemType));
		
		dbItemType.setLanguageId(getLanguageId());
		dbItemType.setCompanyId(getCompanyCode());
		dbItemType.setPlantId(getPlantId());
		
		dbItemType.setDeletionIndicator(0L);
		dbItemType.setCreatedBy(loginUserID);
		dbItemType.setUpdatedBy(loginUserID);
		dbItemType.setCreatedOn(new Date());
		dbItemType.setUpdatedOn(new Date());
		return itemtypeRepository.save(dbItemType);
	}
	
	/**
	 * updateItemType
	 * @param itemtypeCode
	 * @param updateItemType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemType updateItemType (String warehouseId, Long itemTypeId, UpdateItemType updateItemType, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ItemType dbItemType = getItemType(warehouseId, itemTypeId);
		BeanUtils.copyProperties(updateItemType, dbItemType, CommonUtils.getNullPropertyNames(updateItemType));
		dbItemType.setUpdatedBy(loginUserID);
		dbItemType.setUpdatedOn(new Date());
		return itemtypeRepository.save(dbItemType);
	}
	
	/**
	 * deleteItemType
	 * @param itemtypeCode
	 */
	public void deleteItemType (String warehouseId, Long itemTypeId, String loginUserID) {
		ItemType itemtype = getItemType(warehouseId, itemTypeId);
		if ( itemtype != null) {
			itemtype.setDeletionIndicator (1L);
			itemtype.setUpdatedBy(loginUserID);
			itemtype.setUpdatedOn(new Date());
			itemtypeRepository.save(itemtype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + itemTypeId);
		}
	}
}
