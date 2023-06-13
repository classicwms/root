package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.dto.Inventory;
import com.tekclover.wms.api.masters.model.impl.ItemListImpl;
import com.tekclover.wms.api.masters.repository.InventoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.model.dto.ItemCodeDesc;
import com.tekclover.wms.api.masters.model.imbasicdata1.AddImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.SearchImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.UpdateImBasicData1;
import com.tekclover.wms.api.masters.repository.ImBasicData1Repository;
import com.tekclover.wms.api.masters.repository.specification.ImBasicData1Specification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImBasicData1Service {
	
	@Autowired
	private ImBasicData1Repository imbasicdata1Repository;

	@Autowired
	private InventoryRepository inventoryRepository;
	
	/**
	 * getImBasicData1s
	 * @return
	 */
	public Iterable<ImBasicData1> getImBasicData1s () {
		Iterable<ImBasicData1> imbasicdata1List = imbasicdata1Repository.findAll();
//		log.info("imbasicdata1List : " + imbasicdata1List);
		
//		imbasicdata1List = Arrays.asimbasicdata1List.stream()
//				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
//				.collect(Collectors.toList());
		return imbasicdata1List;
	}
	
	/**
	 * 
	 * @param itemCode
	 * @return
	 */
	public ImBasicData1 getImBasicData1(String itemCode, String warehouseId) {
		Optional<ImBasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCodeAndWarehouseIdAndDeletionIndicator(
				itemCode, warehouseId, 0L);
		if (!imbasicdata1.isEmpty()) {
			return imbasicdata1.get();
		} 
		return null;
	}
	
	/**
	 * 
	 * @param searchImBasicData1
	 * @param sortBy 
	 * @param pageSize 
	 * @param pageNo 
	 * @return
	 * @throws Exception
	 */
	public Page<ImBasicData1> findImBasicData1(SearchImBasicData1 searchImBasicData1, 
			Integer pageNo, Integer pageSize, String sortBy) 
			throws Exception {
		if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());
			searchImBasicData1.setStartCreatedOn(dates[0]);
			searchImBasicData1.setEndCreatedOn(dates[1]);
		}
		
		if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
			searchImBasicData1.setStartUpdatedOn(dates[0]);
			searchImBasicData1.setEndUpdatedOn(dates[1]);
		}
		
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
		ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
		Page<ImBasicData1> results = imbasicdata1Repository.findAll(spec, paging);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * 
	 * @param searchImBasicData1
	 * @return
	 * @throws Exception
	 */
	public List<ImBasicData1> findImBasicData1(SearchImBasicData1 searchImBasicData1) 
			throws Exception {
		if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());
			searchImBasicData1.setStartCreatedOn(dates[0]);
			searchImBasicData1.setEndCreatedOn(dates[1]);
		}
		
		if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
			searchImBasicData1.setStartUpdatedOn(dates[0]);
			searchImBasicData1.setEndUpdatedOn(dates[1]);
		}
		
		ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
		List<ImBasicData1> results = imbasicdata1Repository.findAll(spec);
		log.info("results: " + results);
		return results;
	}

	//Streaming
	public Stream<ImBasicData1> findImBasicData1Stream(SearchImBasicData1 searchImBasicData1)
			throws Exception {
		if (searchImBasicData1.getStartCreatedOn() != null && searchImBasicData1.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartCreatedOn(), searchImBasicData1.getEndCreatedOn());
			searchImBasicData1.setStartCreatedOn(dates[0]);
			searchImBasicData1.setEndCreatedOn(dates[1]);
		}

		if (searchImBasicData1.getStartUpdatedOn() != null && searchImBasicData1.getEndUpdatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchImBasicData1.getStartUpdatedOn(), searchImBasicData1.getEndUpdatedOn());
			searchImBasicData1.setStartUpdatedOn(dates[0]);
			searchImBasicData1.setEndUpdatedOn(dates[1]);
		}

		ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
		Stream<ImBasicData1> results = imbasicdata1Repository.stream(spec, ImBasicData1.class);
//		log.info("results: " + results);
		return results;
	}
	
	/**
	 * 
	 * @param likeSearchByItemCodeNDesc
	 * @return
	 */
	public List<ItemListImpl> findImBasicData1LikeSearch(String likeSearchByItemCodeNDesc) {
		if(likeSearchByItemCodeNDesc != null && !likeSearchByItemCodeNDesc.trim().isEmpty()) {
			List<ItemListImpl> data = imbasicdata1Repository.getItemListBySearch(likeSearchByItemCodeNDesc.trim(),
					likeSearchByItemCodeNDesc.trim(),likeSearchByItemCodeNDesc.trim());
			return data;
		} else {
			throw new BadRequestException("Search string must not be empty");
		}
	}
	
	/**
	 * createImBasicData1
	 * @param newImBasicData1
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImBasicData1 createImBasicData1 (AddImBasicData1 newImBasicData1, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImBasicData1 dbImBasicData1 = new ImBasicData1();
		BeanUtils.copyProperties(newImBasicData1, dbImBasicData1, CommonUtils.getNullPropertyNames(newImBasicData1));
		dbImBasicData1.setDeletionIndicator(0L);
		dbImBasicData1.setCreatedBy(loginUserID);
		dbImBasicData1.setUpdatedBy(loginUserID);
		dbImBasicData1.setCreatedOn(new Date());
		dbImBasicData1.setUpdatedOn(new Date());
		return imbasicdata1Repository.save(dbImBasicData1);
	}
	
	/**
	 * updateImBasicData1
	 * @param itemCode
	 * @param updateImBasicData1
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ImBasicData1 updateImBasicData1 (String itemCode, String warehouseId, UpdateImBasicData1 updateImBasicData1, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImBasicData1 dbImBasicData1 = getImBasicData1(itemCode, warehouseId);
		BeanUtils.copyProperties(updateImBasicData1, dbImBasicData1, CommonUtils.getNullPropertyNames(updateImBasicData1));
		dbImBasicData1.setUpdatedBy(loginUserID);
		dbImBasicData1.setUpdatedOn(new Date());

		updateInventoryFields(itemCode, warehouseId, updateImBasicData1);					//Update Inventory

		return imbasicdata1Repository.save(dbImBasicData1);
	}
	
	/**
	 * deleteImBasicData1
	 * @param itemCode
	 */
	public void deleteImBasicData1 (String itemCode, String warehouseId, String loginUserID) {
		ImBasicData1 imbasicdata1 = getImBasicData1(itemCode, warehouseId);
		if ( imbasicdata1 != null) {
			imbasicdata1.setDeletionIndicator (1L);
			imbasicdata1.setUpdatedBy(loginUserID);
			imbasicdata1.setUpdatedOn(new Date());
			imbasicdata1Repository.save(imbasicdata1);
		} else {
			throw new EntityNotFoundException("Error in deleting itemCode Id:" + itemCode);
		}
	}

	//Update Inventory while ImBasicData1 Got Updated
	public void updateInventoryFields(String itemCode, String warehouseId, UpdateImBasicData1 updateImBasicData1) {
		List<Inventory> updateInventoryList = inventoryRepository.updateInventory(
				itemCode,
				warehouseId,
				updateImBasicData1.getLanguageId(),
				updateImBasicData1.getCompanyCodeId(),
				updateImBasicData1.getPlantId());
		if(updateInventoryList != null) {
			for (Inventory updateInventory : updateInventoryList) {
				updateInventory.setReferenceField8(updateImBasicData1.getDescription());
				updateInventory.setReferenceField9(updateImBasicData1.getManufacturerPartNo());
				inventoryRepository.save(updateInventory);
			}
		}
	}
}
