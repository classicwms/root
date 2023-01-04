package com.ustorage.api.trans.service;

import com.ustorage.api.trans.model.itemservice.*;

import com.ustorage.api.trans.repository.*;

import com.ustorage.api.trans.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceService {

	@Autowired
	private WorkOrderRepository workOrderRepository;

	@Autowired
	private ItemServiceRepository itemServiceRepository;


	public List<ItemService> getItemService () {
		List<ItemService> itemServiceList =  itemServiceRepository.findAll();
		itemServiceList = itemServiceList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return itemServiceList;
	}

	/**
	 * getItemService
	 * @param workOrderId
	 * @return
	 */
	public List<ItemService> getItemService (String workOrderId) {
		List<ItemService> itemService = itemServiceRepository.findByWorkOrderIdAndDeletionIndicator(workOrderId, 0L);
		if (itemService.isEmpty()) {
			return null;
		}
		itemService = itemService.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return itemService;
	}

	
	/**
	 * deleteItemService
	 */
	public void deleteItemService (String workOrderId, String loginUserID) {
		List<ItemService> itemservice = getItemService(workOrderId);
		if (itemservice != null) {
			for(ItemService newItemService: itemservice){
				if(newItemService.getDeletionIndicator()==0){
					newItemService.setDeletionIndicator(1L);
					newItemService.setUpdatedBy(loginUserID);
					newItemService.setUpdatedOn(new Date());
					itemServiceRepository.save(newItemService);
				}
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + workOrderId);
		}
	}
	

}
