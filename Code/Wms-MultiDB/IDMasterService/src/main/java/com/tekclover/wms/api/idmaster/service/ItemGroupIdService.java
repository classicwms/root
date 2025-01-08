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
import com.tekclover.wms.api.idmaster.model.itemgroupid.FindItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemtypeid.ItemTypeId;
import com.tekclover.wms.api.idmaster.model.levelid.LevelId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.ItemGroupIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.itemgroupid.AddItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.ItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.UpdateItemGroupId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemGroupIdService{

	@Autowired
	private ItemTypeIdService itemTypeIdService;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private ItemTypeIdRepository itemTypeIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private ItemGroupIdRepository itemGroupIdRepository;

	/**
	 * getItemGroupIds
	 * @return
	 */
	public List<ItemGroupId> getItemGroupIds () {
		List<ItemGroupId> itemGroupIdList =  itemGroupIdRepository.findAll();
		itemGroupIdList = itemGroupIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ItemGroupId> newItemGroupId=new ArrayList<>();
		for(ItemGroupId dbItemGroupId:itemGroupIdList) {
			if (dbItemGroupId.getCompanyIdAndDescription() != null&&dbItemGroupId.getPlantIdAndDescription()!=null&&dbItemGroupId.getWarehouseIdAndDescription()!=null&&dbItemGroupId.getItemTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbItemGroupId.getCompanyCodeId(), dbItemGroupId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbItemGroupId.getPlantId(), dbItemGroupId.getLanguageId(), dbItemGroupId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbItemGroupId.getWarehouseId(), dbItemGroupId.getLanguageId(), dbItemGroupId.getCompanyCodeId(), dbItemGroupId.getPlantId());
				IKeyValuePair iKeyValuePair3 = itemTypeIdRepository.getItemTypeIdAndDescription(dbItemGroupId.getItemTypeId(), dbItemGroupId.getLanguageId(), dbItemGroupId.getCompanyCodeId(), dbItemGroupId.getPlantId(), dbItemGroupId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbItemGroupId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbItemGroupId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbItemGroupId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbItemGroupId.setItemTypeIdAndDescription(iKeyValuePair3.getItemTypeId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newItemGroupId.add(dbItemGroupId);
		}
		return newItemGroupId;
	}

	/**
	 * getItemGroupId
	 * @param itemGroupId
	 * @return
	 */
	public ItemGroupId getItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId,String companyCodeId,String plantId,String languageId) {
		Optional<ItemGroupId> dbItemGroupId =
				itemGroupIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						itemTypeId,
						itemGroupId,
						languageId,
						0L
				);
		if (dbItemGroupId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"itemTypeId - " + itemTypeId +
					"itemGroupId - " + itemGroupId +
					" doesn't exist.");

		}
		ItemGroupId newItemGroupId = new ItemGroupId();
		BeanUtils.copyProperties(dbItemGroupId.get(),newItemGroupId, CommonUtils.getNullPropertyNames(dbItemGroupId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= itemTypeIdRepository.getItemTypeIdAndDescription(itemTypeId,languageId,companyCodeId,plantId,warehouseId);
		if (iKeyValuePair!=null) {
			newItemGroupId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newItemGroupId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newItemGroupId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newItemGroupId.setItemTypeIdAndDescription(iKeyValuePair3.getItemTypeId() + "-" + iKeyValuePair3.getDescription());
		}
		return newItemGroupId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ItemGroupId dbItemGroupId = new ItemGroupId();
		Optional<ItemGroupId> duplicateItemGroupId = itemGroupIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndLanguageIdAndDeletionIndicator(newItemGroupId.getCompanyCodeId(), newItemGroupId.getPlantId(), newItemGroupId.getWarehouseId(), newItemGroupId.getItemTypeId(), newItemGroupId.getItemGroupId(), newItemGroupId.getLanguageId(), 0L);
		if (!duplicateItemGroupId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			ItemTypeId dbItemTypeId=itemTypeIdService.getItemTypeId(newItemGroupId.getWarehouseId(), newItemGroupId.getItemTypeId(),newItemGroupId.getCompanyCodeId(), newItemGroupId.getPlantId(),newItemGroupId.getLanguageId());
			log.info("newItemGroupId : " + newItemGroupId);
			BeanUtils.copyProperties(newItemGroupId, dbItemGroupId, CommonUtils.getNullPropertyNames(newItemGroupId));
			dbItemGroupId.setDeletionIndicator(0L);
			dbItemGroupId.setCompanyIdAndDescription(dbItemTypeId.getCompanyIdAndDescription());
			dbItemGroupId.setPlantIdAndDescription(dbItemTypeId.getPlantIdAndDescription());
			dbItemGroupId.setWarehouseIdAndDescription(dbItemTypeId.getWarehouseIdAndDescription());
			dbItemGroupId.setItemTypeIdAndDescription(dbItemTypeId.getItemTypeId()+"-"+dbItemTypeId.getItemType());
			dbItemGroupId.setCreatedBy(loginUserID);
			dbItemGroupId.setUpdatedBy(loginUserID);
			dbItemGroupId.setCreatedOn(new Date());
			dbItemGroupId.setUpdatedOn(new Date());
			return itemGroupIdRepository.save(dbItemGroupId);
		}
	}

	/**
	 * updateItemGroupId
	 * @param loginUserID
	 * @param itemGroupId
	 * @param updateItemGroupId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemGroupId updateItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId,String companyCodeId,String plantId,String languageId,String loginUserID,
										  UpdateItemGroupId updateItemGroupId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ItemGroupId dbItemGroupId = getItemGroupId(warehouseId, itemTypeId, itemGroupId,companyCodeId,plantId,languageId);
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
	public void deleteItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, String companyCodeId, String plantId,String languageId, String loginUserID) {
		ItemGroupId dbItemGroupId = getItemGroupId(warehouseId, itemTypeId, itemGroupId,companyCodeId,plantId,languageId);
		if ( dbItemGroupId != null) {
			dbItemGroupId.setDeletionIndicator(1L);
			dbItemGroupId.setUpdatedBy(loginUserID);
			itemGroupIdRepository.save(dbItemGroupId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + itemGroupId);
		}
	}
	//Find ItemGroupId

	public List<ItemGroupId> findItemGroupId(FindItemGroupId findItemGroupId) throws ParseException {

		ItemGroupIdSpecification spec = new ItemGroupIdSpecification(findItemGroupId);
		List<ItemGroupId> results = itemGroupIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ItemGroupId> newItemGroupId=new ArrayList<>();
		for(ItemGroupId dbItemGroupId:results) {
			if (dbItemGroupId.getCompanyIdAndDescription() != null&&dbItemGroupId.getPlantIdAndDescription()!=null&&dbItemGroupId.getWarehouseIdAndDescription()!=null&&dbItemGroupId.getItemTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbItemGroupId.getCompanyCodeId(), dbItemGroupId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbItemGroupId.getPlantId(), dbItemGroupId.getLanguageId(), dbItemGroupId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbItemGroupId.getWarehouseId(), dbItemGroupId.getLanguageId(), dbItemGroupId.getCompanyCodeId(), dbItemGroupId.getPlantId());
				IKeyValuePair iKeyValuePair3 = itemTypeIdRepository.getItemTypeIdAndDescription(dbItemGroupId.getItemTypeId(), dbItemGroupId.getLanguageId(), dbItemGroupId.getCompanyCodeId(), dbItemGroupId.getPlantId(), dbItemGroupId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbItemGroupId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbItemGroupId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbItemGroupId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbItemGroupId.setItemTypeIdAndDescription(iKeyValuePair3.getItemTypeId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newItemGroupId.add(dbItemGroupId);
		}
		return newItemGroupId;
	}
}
