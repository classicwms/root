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
import com.tekclover.wms.api.enterprise.model.itemgroup.AddItemGroup;
import com.tekclover.wms.api.enterprise.model.itemgroup.ItemGroup;
import com.tekclover.wms.api.enterprise.model.itemgroup.SearchItemGroup;
import com.tekclover.wms.api.enterprise.model.itemgroup.UpdateItemGroup;
import com.tekclover.wms.api.enterprise.repository.ItemGroupRepository;
import com.tekclover.wms.api.enterprise.repository.specification.ItemGroupSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemGroupService extends BaseService {
	
	@Autowired
	private ItemGroupRepository itemgroupRepository;
	
	/**
	 * getItemGroups
	 * @return
	 */
	public List<ItemGroup> getItemGroups () {
		List<ItemGroup> itemgroupList = itemgroupRepository.findAll();
		log.info("itemgroupList : " + itemgroupList);
		itemgroupList = itemgroupList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return itemgroupList;
	}
	
	/**
	 * getItemGroup
	 * @param warehouseId
	 * @param itemGroupId
	 * @param itemTypeId
	 * @param subItemGroup
	 * @return
	 */
	public ItemGroup getItemGroup (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroup) {
		Optional<ItemGroup> itemgroup = 
				itemgroupRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndDeletionIndicator(
						getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, itemTypeId, itemGroupId, subItemGroup, 0L);
		if (itemgroup.isEmpty()) {
			throw new BadRequestException("The given ItemGroup Id : " + itemGroupId + " doesn't exist.");
		} 
		return itemgroup.get();
	}
	
	/**
	 * findItemGroup
	 * @param searchItemGroup
	 * @return
	 * @throws ParseException
	 */
	public List<ItemGroup> findItemGroup(SearchItemGroup searchItemGroup) throws Exception {
		if (searchItemGroup.getStartCreatedOn() != null && searchItemGroup.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchItemGroup.getStartCreatedOn(), searchItemGroup.getEndCreatedOn());
			searchItemGroup.setStartCreatedOn(dates[0]);
			searchItemGroup.setEndCreatedOn(dates[1]);
		}
		
		ItemGroupSpecification spec = new ItemGroupSpecification(searchItemGroup);
		List<ItemGroup> results = itemgroupRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createItemGroup
	 * @param newItemGroup
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemGroup createItemGroup (AddItemGroup newItemGroup, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Optional<ItemGroup> optItemGroup = 
				itemgroupRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndDeletionIndicator(
						getLanguageId(), 
						getCompanyCode(), 
						getPlantId(), 
						newItemGroup.getWarehouseId(),
						newItemGroup.getItemTypeId(),
						newItemGroup.getItemGroupId(),
						newItemGroup.getSubItemGroupId(),
						0L);
		if (!optItemGroup.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}
		
		ItemGroup dbItemGroup = new ItemGroup();
		BeanUtils.copyProperties(newItemGroup, dbItemGroup, CommonUtils.getNullPropertyNames(newItemGroup));
		
		dbItemGroup.setLanguageId(getLanguageId());
		dbItemGroup.setCompanyId(getCompanyCode());
		dbItemGroup.setPlantId(getPlantId());
		
		dbItemGroup.setDeletionIndicator(0L);
		dbItemGroup.setCompanyId(getCompanyCode());
		dbItemGroup.setCreatedBy(loginUserID);
		dbItemGroup.setUpdatedBy(loginUserID);
		dbItemGroup.setCreatedOn(new Date());
		dbItemGroup.setUpdatedOn(new Date());
		return itemgroupRepository.save(dbItemGroup);
	}
	
	/**
	 * updateItemGroup
	 * @param itemgroupCode
	 * @param updateItemGroup
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemGroup updateItemGroup (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroup, UpdateItemGroup updateItemGroup, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ItemGroup dbItemGroup = getItemGroup(warehouseId, itemTypeId, itemGroupId, subItemGroup);
		BeanUtils.copyProperties(updateItemGroup, dbItemGroup, CommonUtils.getNullPropertyNames(updateItemGroup));
		dbItemGroup.setUpdatedBy(loginUserID);
		dbItemGroup.setUpdatedOn(new Date());
		return itemgroupRepository.save(dbItemGroup);
	}
	
	/**
	 * deleteItemGroup
	 * @param itemgroupCode
	 */
	public void deleteItemGroup (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroup, String loginUserID) {
		ItemGroup itemgroup = getItemGroup(warehouseId, itemTypeId, itemGroupId, subItemGroup);
		if ( itemgroup != null) {
			itemgroup.setDeletionIndicator (1L);
			itemgroup.setUpdatedBy(loginUserID);
			itemgroup.setUpdatedOn(new Date());
			itemgroupRepository.save(itemgroup);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + itemGroupId);
		}
	}
}
