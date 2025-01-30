package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.repository.CompanyRepository;
import com.tekclover.wms.api.enterprise.repository.PlantRepository;
import com.tekclover.wms.api.enterprise.repository.WarehouseRepository;
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
public class ItemTypeService{
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantRepository plantRepository;
	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private ItemTypeRepository itemtypeRepository;

	/**
	 * getItemTypes
	 * @return
	 */
	public List<ItemType> getItemTypes () {
		List<ItemType> itemtypeList = itemtypeRepository.findAll();
		log.info("itemtypeList : " + itemtypeList);
		itemtypeList = itemtypeList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ItemType> newItemType=new ArrayList<>();
		for(ItemType dbItemType:itemtypeList) {
			if (dbItemType.getCompanyIdAndDescription() != null&&dbItemType.getPlantIdAndDescription()!=null&&dbItemType.getWarehouseIdAndDescription()!=null) {
				IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbItemType.getCompanyId(), dbItemType.getLanguageId());
				IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbItemType.getPlantId(), dbItemType.getLanguageId(), dbItemType.getCompanyId());
				IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbItemType.getWarehouseId(), dbItemType.getLanguageId(), dbItemType.getCompanyId(), dbItemType.getPlantId());
				IkeyValuePair ikeyValuePair3 = itemtypeRepository.getItemTypeIdAndDescription(dbItemType.getItemTypeId(), dbItemType.getLanguageId(), dbItemType.getCompanyId(), dbItemType.getPlantId(), dbItemType.getWarehouseId());
				if (iKeyValuePair != null) {
					dbItemType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbItemType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbItemType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (ikeyValuePair3 != null) {
					dbItemType.setDescription(ikeyValuePair3.getDescription());
				}
			}
			newItemType.add(dbItemType);
		}
		return newItemType;
	}

	/**
	 * getItemType
	 * @param warehouseId
	 * @param itemTypeId
	 * @return
	 */
	public ItemType getItemType (String warehouseId, Long itemTypeId,String companyId,String languageId,String plantId) {
		Optional<ItemType> itemtype =
				itemtypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndDeletionIndicator(
						languageId,
						companyId,
						plantId,
						warehouseId,
						itemTypeId,
						0L);
		if (itemtype.isEmpty()) {
			throw new BadRequestException("The given ItemType Id : " + itemTypeId + " doesn't exist.");
		}
		ItemType newItemType = new ItemType();
		BeanUtils.copyProperties(itemtype.get(),newItemType, CommonUtils.getNullPropertyNames(itemtype));
		IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(companyId,languageId);
		IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(plantId,languageId,companyId);
		IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyId,plantId);
		IkeyValuePair ikeyValuePair3= itemtypeRepository.getItemTypeIdAndDescription(itemTypeId,languageId,companyId,plantId,warehouseId);
		if(iKeyValuePair!=null) {
			newItemType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newItemType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newItemType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(ikeyValuePair3!=null) {
			newItemType.setDescription(ikeyValuePair3.getDescription());
		}
		return newItemType;

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
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ItemType> newItemType=new ArrayList<>();
		for(ItemType dbItemType:results) {
			if (dbItemType.getCompanyIdAndDescription() != null&&dbItemType.getPlantIdAndDescription()!=null&&dbItemType.getWarehouseIdAndDescription()!=null) {
				IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(dbItemType.getCompanyId(), dbItemType.getLanguageId());
				IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(dbItemType.getPlantId(), dbItemType.getLanguageId(), dbItemType.getCompanyId());
				IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbItemType.getWarehouseId(), dbItemType.getLanguageId(), dbItemType.getCompanyId(), dbItemType.getPlantId());
				IkeyValuePair ikeyValuePair3= itemtypeRepository.getItemTypeIdAndDescription(dbItemType.getItemTypeId(), dbItemType.getLanguageId(), dbItemType.getCompanyId(), dbItemType.getPlantId(), dbItemType.getWarehouseId());
				if (iKeyValuePair != null) {
					dbItemType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbItemType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbItemType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (ikeyValuePair3 != null) {
					dbItemType.setDescription(ikeyValuePair3.getDescription());
				}
			}
			newItemType.add(dbItemType);
		}
		return newItemType;
	}

	/**
	 * createItemType
	 * @param newItemType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemType createItemType (AddItemType newItemType, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Optional<ItemType> optItemType =
				itemtypeRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndDeletionIndicator(
						newItemType.getLanguageId(),
						newItemType.getCompanyId(),
						newItemType.getPlantId(),
						newItemType.getWarehouseId(),
						newItemType.getItemTypeId(),
						0L);
		if (!optItemType.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		}

		ItemType dbItemType = new ItemType();
		BeanUtils.copyProperties(newItemType, dbItemType, CommonUtils.getNullPropertyNames(newItemType));
		IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(dbItemType.getCompanyId(), dbItemType.getLanguageId());
		IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(dbItemType.getPlantId(), dbItemType.getLanguageId(), dbItemType.getCompanyId());
		IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbItemType.getWarehouseId(), dbItemType.getLanguageId(), dbItemType.getCompanyId(), dbItemType.getPlantId());
		IkeyValuePair ikeyValuePair3= itemtypeRepository.getItemTypeIdAndDescription(dbItemType.getItemTypeId(), dbItemType.getLanguageId(), dbItemType.getCompanyId(), dbItemType.getPlantId(), dbItemType.getWarehouseId());

		if(iKeyValuePair != null && iKeyValuePair1 != null &&
				iKeyValuePair2 != null && ikeyValuePair3 != null) {
			dbItemType.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
			dbItemType.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
			dbItemType.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
			dbItemType.setDescription(ikeyValuePair3.getDescription());
		}
		else {
			throw new BadRequestException("The given values of Company Id "
					+ newItemType.getCompanyId() + " Plant Id "
					+ newItemType.getPlantId() + " Warehouse Id "
					+ newItemType.getWarehouseId() + " ItemType Id "
					+ newItemType.getItemTypeId() + " doesn't exist ");
		}
		dbItemType.setDeletionIndicator(0L);
		dbItemType.setCreatedBy(loginUserID);
		dbItemType.setUpdatedBy(loginUserID);
		dbItemType.setCreatedOn(new Date());
		dbItemType.setUpdatedOn(new Date());
		return itemtypeRepository.save(dbItemType);
	}

	/**
	 * updateItemType
	 * @param itemTypeId
	 * @param updateItemType
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ItemType updateItemType (String warehouseId, Long itemTypeId,String companyId,String languageId,String plantId,UpdateItemType updateItemType, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ItemType dbItemType = getItemType(warehouseId, itemTypeId,companyId,languageId,plantId);
		BeanUtils.copyProperties(updateItemType, dbItemType, CommonUtils.getNullPropertyNames(updateItemType));
		dbItemType.setUpdatedBy(loginUserID);
		dbItemType.setUpdatedOn(new Date());
		return itemtypeRepository.save(dbItemType);
	}

	/**
	 * deleteItemType
	 * @param itemTypeId
	 */
	public void deleteItemType (String warehouseId, Long itemTypeId,String companyId,String languageId,String plantId, String loginUserID) throws ParseException {
		ItemType itemtype = getItemType(warehouseId, itemTypeId,companyId,languageId,plantId);
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
