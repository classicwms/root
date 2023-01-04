package com.ustorage.api.master.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.master.model.itemtype.AddItemType;
import com.ustorage.api.master.model.itemtype.ItemType;
import com.ustorage.api.master.model.itemtype.UpdateItemType;
import com.ustorage.api.master.repository.ItemTypeRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemTypeService {
	
	@Autowired
	private ItemTypeRepository itemTypeRepository;
	
	public List<ItemType> getItemType () {
		List<ItemType> itemTypeList =  itemTypeRepository.findAll();
		itemTypeList = itemTypeList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return itemTypeList;
	}
	
	/**
	 * getItemType
	 * @param itemTypeId
	 * @return
	 */
	public ItemType getItemType (String itemTypeId) {
		Optional<ItemType> itemType = itemTypeRepository.findByCodeAndDeletionIndicator(itemTypeId, 0L);
		if (itemType.isEmpty()) {
			return null;
		}
		return itemType.get();
	}
	
	/**
	 * createItemType
	 * @param newItemType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemType createItemType (AddItemType newItemType, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		ItemType dbItemType = new ItemType();
		BeanUtils.copyProperties(newItemType, dbItemType, CommonUtils.getNullPropertyNames(newItemType));
		dbItemType.setDeletionIndicator(0L);
		dbItemType.setCreatedBy(loginUserId);
		dbItemType.setUpdatedBy(loginUserId);
		dbItemType.setCreatedOn(new Date());
		dbItemType.setUpdatedOn(new Date());
		return itemTypeRepository.save(dbItemType);
	}
	
	/**
	 * updateItemType
	 * @param itemTypeId
	 * @param loginUserId 
	 * @param updateItemType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemType updateItemType (String code, String loginUserId, UpdateItemType updateItemType)
			throws IllegalAccessException, InvocationTargetException {
		ItemType dbItemType = getItemType(code);
		BeanUtils.copyProperties(updateItemType, dbItemType, CommonUtils.getNullPropertyNames(updateItemType));
		dbItemType.setUpdatedBy(loginUserId);
		dbItemType.setUpdatedOn(new Date());
		return itemTypeRepository.save(dbItemType);
	}
	
	/**
	 * deleteItemType
	 * @param loginUserID 
	 * @param itemtypeCode
	 */
	public void deleteItemType (String itemtypeModuleId, String loginUserID) {
		ItemType itemtype = getItemType(itemtypeModuleId);
		if (itemtype != null) {
			itemtype.setDeletionIndicator(1L);
			itemtype.setUpdatedBy(loginUserID);
			itemtype.setUpdatedOn(new Date());
			itemTypeRepository.save(itemtype);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + itemtypeModuleId);
		}
	}
}
