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

import com.ustorage.api.master.model.itemgroup.AddItemGroup;
import com.ustorage.api.master.model.itemgroup.ItemGroup;
import com.ustorage.api.master.model.itemgroup.UpdateItemGroup;
import com.ustorage.api.master.repository.ItemGroupRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemGroupService {
	
	@Autowired
	private ItemGroupRepository itemGroupRepository;
	
	public List<ItemGroup> getItemGroup () {
		List<ItemGroup> itemGroupList =  itemGroupRepository.findAll();
		itemGroupList = itemGroupList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return itemGroupList;
	}
	
	/**
	 * getItemGroup
	 * @param itemGroupId
	 * @return
	 */
	public ItemGroup getItemGroup (String itemGroupId) {
		Optional<ItemGroup> itemGroup = itemGroupRepository.findByCodeAndDeletionIndicator(itemGroupId, 0L);
		if (itemGroup.isEmpty()) {
			return null;
		}
		return itemGroup.get();
	}
	
	/**
	 * createItemGroup
	 * @param newItemGroup
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemGroup createItemGroup (AddItemGroup newItemGroup, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		ItemGroup dbItemGroup = new ItemGroup();
		BeanUtils.copyProperties(newItemGroup, dbItemGroup, CommonUtils.getNullPropertyNames(newItemGroup));
		dbItemGroup.setDeletionIndicator(0L);
		dbItemGroup.setCreatedBy(loginUserId);
		dbItemGroup.setUpdatedBy(loginUserId);
		dbItemGroup.setCreatedOn(new Date());
		dbItemGroup.setUpdatedOn(new Date());
		return itemGroupRepository.save(dbItemGroup);
	}
	
	/**
	 * updateItemGroup
	 * @param itemGroupId
	 * @param loginUserId 
	 * @param updateItemGroup
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemGroup updateItemGroup (String code, String loginUserId, UpdateItemGroup updateItemGroup)
			throws IllegalAccessException, InvocationTargetException {
		ItemGroup dbItemGroup = getItemGroup(code);
		BeanUtils.copyProperties(updateItemGroup, dbItemGroup, CommonUtils.getNullPropertyNames(updateItemGroup));
		dbItemGroup.setUpdatedBy(loginUserId);
		dbItemGroup.setUpdatedOn(new Date());
		return itemGroupRepository.save(dbItemGroup);
	}
	
	/**
	 * deleteItemGroup
	 * @param loginUserID 
	 * @param itemgroupCode
	 */
	public void deleteItemGroup (String itemgroupModuleId, String loginUserID) {
		ItemGroup itemgroup = getItemGroup(itemgroupModuleId);
		if (itemgroup != null) {
			itemgroup.setDeletionIndicator(1L);
			itemgroup.setUpdatedBy(loginUserID);
			itemgroup.setUpdatedOn(new Date());
			itemGroupRepository.save(itemgroup);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + itemgroupModuleId);
		}
	}
}
