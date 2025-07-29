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
import com.tekclover.wms.api.idmaster.model.itemtypeid.FindItemTypeId;
import com.tekclover.wms.api.idmaster.model.outboundorderstatusid.OutboundOrderStatusId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.ItemTypeIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
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
public class ItemTypeIdService {

	@Autowired
	private ItemTypeIdRepository itemTypeIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getItemTypeIds
	 * @return
	 */
	public List<ItemTypeId> getItemTypeIds () {
		List<ItemTypeId> itemTypeIdList =  itemTypeIdRepository.findAll();
		itemTypeIdList = itemTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ItemTypeId> newItemTypeId=new ArrayList<>();
		for(ItemTypeId dbItemTypeId:itemTypeIdList) {
			if (dbItemTypeId.getCompanyIdAndDescription() != null&&dbItemTypeId.getPlantIdAndDescription()!=null&&dbItemTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbItemTypeId.getCompanyCodeId(), dbItemTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbItemTypeId.getPlantId(), dbItemTypeId.getLanguageId(), dbItemTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbItemTypeId.getWarehouseId(), dbItemTypeId.getLanguageId(), dbItemTypeId.getCompanyCodeId(), dbItemTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbItemTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbItemTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbItemTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newItemTypeId.add(dbItemTypeId);
		}
		return newItemTypeId;
	}

	/**
	 * getItemTypeId
	 * @param itemTypeId
	 * @return
	 */
	public ItemTypeId getItemTypeId (String warehouseId, Long itemTypeId,String companyCodeId,String plantId,String languageId) {
		Optional<ItemTypeId> dbItemTypeId =
				itemTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						itemTypeId,
						languageId,
						0L
				);
		if (dbItemTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"itemTypeId - " + itemTypeId +
					" doesn't exist.");

		}
		ItemTypeId newItemTypeId = new ItemTypeId();
		BeanUtils.copyProperties(dbItemTypeId.get(),newItemTypeId, CommonUtils.getNullPropertyNames(dbItemTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newItemTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newItemTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newItemTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newItemTypeId;
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
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ItemTypeId dbItemTypeId = new ItemTypeId();
		Optional<ItemTypeId> duplicateItemTypeId = itemTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndLanguageIdAndDeletionIndicator(newItemTypeId.getCompanyCodeId(), newItemTypeId.getPlantId(), newItemTypeId.getWarehouseId(), newItemTypeId.getItemTypeId(), newItemTypeId.getLanguageId(), 0l);
		if (!duplicateItemTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newItemTypeId.getWarehouseId(), newItemTypeId.getCompanyCodeId(), newItemTypeId.getPlantId(),newItemTypeId.getLanguageId());
			log.info("newItemTypeId : " + newItemTypeId);
			BeanUtils.copyProperties(newItemTypeId, dbItemTypeId, CommonUtils.getNullPropertyNames(newItemTypeId));
			dbItemTypeId.setDeletionIndicator(0L);
			dbItemTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbItemTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbItemTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbItemTypeId.setCreatedBy(loginUserID);
			dbItemTypeId.setUpdatedBy(loginUserID);
			dbItemTypeId.setCreatedOn(new Date());
			dbItemTypeId.setUpdatedOn(new Date());
			return itemTypeIdRepository.save(dbItemTypeId);
		}
	}

	/**
	 * updateItemTypeId
	 * @param loginUserID
	 * @param itemTypeId
	 * @param updateItemTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemTypeId updateItemTypeId (String warehouseId, Long itemTypeId,String companyCodeId,String plantId,String languageId,String loginUserID,
										UpdateItemTypeId updateItemTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ItemTypeId dbItemTypeId = getItemTypeId(warehouseId,itemTypeId,companyCodeId,plantId,languageId);
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
	public void deleteItemTypeId (String warehouseId, Long itemTypeId,String companyCodeId,String plantId,String languageId,String loginUserID) {
		ItemTypeId dbItemTypeId = getItemTypeId(warehouseId,itemTypeId,companyCodeId,plantId,languageId);
		if ( dbItemTypeId != null) {
			dbItemTypeId.setDeletionIndicator(1L);
			dbItemTypeId.setUpdatedBy(loginUserID);
			itemTypeIdRepository.save(dbItemTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + itemTypeId);
		}
	}
	//Find ItemTypeId

	public List<ItemTypeId> findItemTypeId(FindItemTypeId findItemTypeId) throws ParseException {

		ItemTypeIdSpecification spec = new ItemTypeIdSpecification(findItemTypeId);
		List<ItemTypeId> results = itemTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ItemTypeId> newItemTypeId=new ArrayList<>();
		for(ItemTypeId dbItemTypeId:results) {
			if (dbItemTypeId.getCompanyIdAndDescription() != null&&dbItemTypeId.getPlantIdAndDescription()!=null&&dbItemTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbItemTypeId.getCompanyCodeId(), dbItemTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbItemTypeId.getPlantId(), dbItemTypeId.getLanguageId(), dbItemTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbItemTypeId.getWarehouseId(), dbItemTypeId.getLanguageId(), dbItemTypeId.getCompanyCodeId(), dbItemTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbItemTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbItemTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbItemTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newItemTypeId.add(dbItemTypeId);
		}
		return newItemTypeId;
	}
}
