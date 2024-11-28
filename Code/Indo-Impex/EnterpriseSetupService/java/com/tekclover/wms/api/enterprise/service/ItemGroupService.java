package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.itemgroup.AddItemGroup;
import com.tekclover.wms.api.enterprise.model.itemgroup.ItemGroup;
import com.tekclover.wms.api.enterprise.model.itemgroup.SearchItemGroup;
import com.tekclover.wms.api.enterprise.repository.specification.ItemGroupSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemGroupService {
	@Autowired
	private ItemTypeRepository itemTypeRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantRepository plantRepository;
	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private ItemGroupRepository itemgroupRepository;

	/**
	 * getItemGroups
	 * @return
	 */
	public List<ItemGroup> getItemGroups () {
		List<ItemGroup> itemgroupList = itemgroupRepository.findAll();
		log.info("itemgroupList : " + itemgroupList);
		itemgroupList = itemgroupList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ItemGroup> newItemGroup=new ArrayList<>();
		for(ItemGroup dbItemGroup:itemgroupList) {
			if (dbItemGroup.getCompanyIdAndDescription() != null &&
					dbItemGroup.getPlantIdAndDescription() != null &&
					dbItemGroup.getWarehouseIdAndDescription() != null &&
					dbItemGroup.getItemTypeIdAndDescription() != null &&
					dbItemGroup.getSubItemGroupIdAndDescription() != null) {
				IkeyValuePair iKeyValuePair =
						companyRepository.getCompanyIdAndDescription(dbItemGroup.getCompanyId(), dbItemGroup.getLanguageId());

				IkeyValuePair iKeyValuePair1 =
						plantRepository.getPlantIdAndDescription(dbItemGroup.getPlantId(), dbItemGroup.getLanguageId(),
								dbItemGroup.getCompanyId());

				IkeyValuePair iKeyValuePair2 =
						warehouseRepository.getWarehouseIdAndDescription(dbItemGroup.getWarehouseId(),
								dbItemGroup.getLanguageId(), dbItemGroup.getCompanyId(), dbItemGroup.getPlantId());

				IkeyValuePair ikeyValuePair3 =
						itemTypeRepository.getItemTypeIdAndDescription(dbItemGroup.getItemTypeId(), dbItemGroup.getLanguageId(),
								dbItemGroup.getCompanyId(), dbItemGroup.getPlantId(), dbItemGroup.getWarehouseId());

				IkeyValuePair ikeyValuePair4 =
						itemgroupRepository.getItemGroupIdAndDescription(dbItemGroup.getItemGroupId(), dbItemGroup.getLanguageId(),
								dbItemGroup.getCompanyId(), dbItemGroup.getPlantId(), dbItemGroup.getWarehouseId(), dbItemGroup.getItemTypeId());

				IkeyValuePair ikeyValuePair5 =
						itemgroupRepository.getSubItemGroupIdAndDescription(dbItemGroup.getSubItemGroupId(), dbItemGroup.getCompanyId(),
								dbItemGroup.getPlantId(), dbItemGroup.getWarehouseId(), dbItemGroup.getItemTypeId(),
								dbItemGroup.getItemGroupId(), dbItemGroup.getLanguageId());

				if (iKeyValuePair != null) {
					dbItemGroup.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbItemGroup.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbItemGroup.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (ikeyValuePair3 != null) {
					dbItemGroup.setItemTypeIdAndDescription(ikeyValuePair3.getItemTypeId() + "-" + ikeyValuePair3.getDescription());
				}
				if (ikeyValuePair4 != null) {
					dbItemGroup.setDescription(ikeyValuePair4.getDescription());
				}
				if(ikeyValuePair5 != null){
					dbItemGroup.setSubItemGroupIdAndDescription(ikeyValuePair5.getSubItemGroupId() + " - " + ikeyValuePair5.getDescription());
				}
			}
			newItemGroup.add(dbItemGroup);
		}
		return newItemGroup;

	}

	/**
	 *
	 * @param companyId
	 * @param languageId
	 * @param plantId
	 * @param warehouseId
	 * @param itemTypeId
	 * @return
	 */
	public List<ItemGroup> getItemGroup (String companyId,String languageId,String plantId,String warehouseId,
										 Long itemTypeId) {

		List<ItemGroup> itemGroupList = new ArrayList<>();

		List<ItemGroup> itemgroup =
				itemgroupRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndAndDeletionIndicator(
						languageId, companyId, plantId, warehouseId, itemTypeId, 0L);

		if (itemgroup.isEmpty()) {
			throw new BadRequestException("The given Company Id : " + companyId + " LanguageId " + languageId +
					" plantId " + plantId + " Warehouse Id " + warehouseId + "ItemType Id " + itemTypeId + " doesn't exist.");
		}

		for (ItemGroup itemGroup : itemgroup) {
			ItemGroup newItemGroup = new ItemGroup();
			BeanUtils.copyProperties(itemGroup, newItemGroup, CommonUtils.getNullPropertyNames(itemGroup));
			IkeyValuePair iKeyValuePair =
					companyRepository.getCompanyIdAndDescription(companyId, languageId);

			IkeyValuePair iKeyValuePair1 =
					plantRepository.getPlantIdAndDescription(plantId, languageId, companyId);

			IkeyValuePair iKeyValuePair2 =
					warehouseRepository.getWarehouseIdAndDescription(warehouseId, languageId, companyId, plantId);

			IkeyValuePair ikeyValuePair3 =
					itemTypeRepository.getItemTypeIdAndDescription(itemTypeId, languageId, companyId,
							plantId, warehouseId);

			IkeyValuePair ikeyValuePair4 =
					itemgroupRepository.getItemGroupIdAndDescription(itemGroup.getItemGroupId(), languageId, companyId,
							plantId, warehouseId, itemTypeId);

			IkeyValuePair ikeyValuePair5 =
					itemgroupRepository.getSubItemGroupIdAndDescription(itemGroup.getSubItemGroupId(),companyId,plantId,
							warehouseId,itemTypeId,itemGroup.getItemGroupId(),languageId);

			if (iKeyValuePair != null) {
				newItemGroup.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
			}
			if (iKeyValuePair1 != null) {
				newItemGroup.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
			}
			if (iKeyValuePair2 != null) {
				newItemGroup.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
			}
			if (ikeyValuePair3 != null) {
				newItemGroup.setItemTypeIdAndDescription(ikeyValuePair3.getItemTypeId() + "-" + ikeyValuePair3.getDescription());
			}
			if (ikeyValuePair4 != null) {
				newItemGroup.setDescription(ikeyValuePair4.getDescription());
			}
			if (ikeyValuePair5 != null){
				newItemGroup.setSubItemGroupIdAndDescription(ikeyValuePair5.getSubItemGroupId() + "-" + ikeyValuePair5.getDescription());
			}
			itemGroupList.add(newItemGroup);
		}
		return itemGroupList;
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
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ItemGroup> newItemGroup=new ArrayList<>();
		for(ItemGroup dbItemGroup:results) {
			if (dbItemGroup.getCompanyIdAndDescription() != null &&
					dbItemGroup.getPlantIdAndDescription()!= null &&
					dbItemGroup.getWarehouseIdAndDescription() != null &&
					dbItemGroup.getItemTypeIdAndDescription()!= null &&
					dbItemGroup.getSubItemGroupIdAndDescription()!= null) {
				IkeyValuePair iKeyValuePair =
						companyRepository.getCompanyIdAndDescription(dbItemGroup.getCompanyId(), dbItemGroup.getLanguageId());

				IkeyValuePair iKeyValuePair1 =
						plantRepository.getPlantIdAndDescription(dbItemGroup.getPlantId(), dbItemGroup.getLanguageId(),
								dbItemGroup.getCompanyId());

				IkeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbItemGroup.getWarehouseId(),
						dbItemGroup.getLanguageId(), dbItemGroup.getCompanyId(), dbItemGroup.getPlantId());

				IkeyValuePair ikeyValuePair3 =
						itemTypeRepository.getItemTypeIdAndDescription(dbItemGroup.getItemTypeId(), dbItemGroup.getLanguageId(),
								dbItemGroup.getCompanyId(), dbItemGroup.getPlantId(), dbItemGroup.getWarehouseId());

				IkeyValuePair ikeyValuePair4 = itemgroupRepository.getItemGroupIdAndDescription(dbItemGroup.getItemGroupId(),
						dbItemGroup.getLanguageId(), dbItemGroup.getCompanyId(), dbItemGroup.getPlantId(), dbItemGroup.getWarehouseId(),
						dbItemGroup.getItemTypeId());

				IkeyValuePair ikeyValuePair5 = itemgroupRepository.getSubItemGroupIdAndDescription(dbItemGroup.getSubItemGroupId(),
						dbItemGroup.getCompanyId(), dbItemGroup.getPlantId(), dbItemGroup.getWarehouseId(),
						dbItemGroup.getItemTypeId(), dbItemGroup.getItemGroupId(), dbItemGroup.getLanguageId());

				if (iKeyValuePair != null) {
					dbItemGroup.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbItemGroup.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbItemGroup.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (ikeyValuePair3 != null) {
					dbItemGroup.setItemTypeIdAndDescription(ikeyValuePair3.getItemTypeId() + "-" + ikeyValuePair3.getDescription());
				}
				if (ikeyValuePair4 != null) {
					dbItemGroup.setDescription(ikeyValuePair4.getDescription());
				}
				if (ikeyValuePair5 != null){
					dbItemGroup.setSubItemGroupIdAndDescription(ikeyValuePair5.getSubItemGroupId() + "-" + ikeyValuePair5.getDescription());
				}
			}
			newItemGroup.add(dbItemGroup);
		}
		return newItemGroup;
	}

	/**
	 * createItemGroup
	 * @param newItemGroup
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ItemGroup> createItemGroup (List<AddItemGroup> newItemGroup, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<ItemGroup>dbItemGroupList=new ArrayList<>();

		for(AddItemGroup newItemGroupId : newItemGroup) {
			List<ItemGroup> optItemGroup =
					itemgroupRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndDeletionIndicator(
							newItemGroupId.getLanguageId(),
							newItemGroupId.getCompanyId(),
							newItemGroupId.getPlantId(),
							newItemGroupId.getWarehouseId(),
							newItemGroupId.getItemTypeId(),
							newItemGroupId.getItemGroupId(),
							newItemGroupId.getSubItemGroupId(),
							0L);
			if (!optItemGroup.isEmpty()) {
				throw new BadRequestException("The given values are getting duplicated.");
			} else {

				IkeyValuePair iKeyValuePair =
						companyRepository.getCompanyIdAndDescription(newItemGroupId.getCompanyId(), newItemGroupId.getLanguageId());

				IkeyValuePair iKeyValuePair1 =
						plantRepository.getPlantIdAndDescription(newItemGroupId.getPlantId(), newItemGroupId.getLanguageId(),
								newItemGroupId.getCompanyId());

				IkeyValuePair iKeyValuePair2 =
						warehouseRepository.getWarehouseIdAndDescription(newItemGroupId.getWarehouseId(), newItemGroupId.getLanguageId(),
								newItemGroupId.getCompanyId(), newItemGroupId.getPlantId());

				IkeyValuePair ikeyValuePair3 =
						itemTypeRepository.getItemTypeIdAndDescription(newItemGroupId.getItemTypeId(), newItemGroupId.getLanguageId(),
								newItemGroupId.getCompanyId(), newItemGroupId.getPlantId(), newItemGroupId.getWarehouseId());

				IkeyValuePair ikeyValuePair4 = itemgroupRepository.getItemGroupIdAndDescription(newItemGroupId.getItemGroupId(),
						newItemGroupId.getLanguageId(), newItemGroupId.getCompanyId(), newItemGroupId.getPlantId(),
						newItemGroupId.getWarehouseId(), newItemGroupId.getItemTypeId());

				IkeyValuePair ikeyValuePair5 = itemgroupRepository.getSubItemGroupIdAndDescription(newItemGroupId.getSubItemGroupId(),
						newItemGroupId.getCompanyId(), newItemGroupId.getPlantId(), newItemGroupId.getWarehouseId(),
						newItemGroupId.getItemTypeId(), newItemGroupId.getItemGroupId(), newItemGroupId.getLanguageId());


				ItemGroup dbItemGroupId = new ItemGroup();
				BeanUtils.copyProperties(newItemGroupId, dbItemGroupId, CommonUtils.getNullPropertyNames(newItemGroupId));

				if (iKeyValuePair != null && iKeyValuePair1 != null &&
						iKeyValuePair2 != null && ikeyValuePair3 != null &&
						ikeyValuePair4 != null && ikeyValuePair5 != null) {
					dbItemGroupId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
					dbItemGroupId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
					dbItemGroupId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
					dbItemGroupId.setItemTypeIdAndDescription(ikeyValuePair3.getItemTypeId() + "-" + ikeyValuePair3.getDescription());
					dbItemGroupId.setDescription(ikeyValuePair4.getDescription());
					dbItemGroupId.setSubItemGroupIdAndDescription(ikeyValuePair5.getSubItemGroupId() + "-" + ikeyValuePair5.getDescription());
				} else {
					throw new BadRequestException("The given values of Company Id "
							+ newItemGroupId.getCompanyId() + " Plant Id "
							+ newItemGroupId.getPlantId() + " Warehouse Id "
							+ newItemGroupId.getWarehouseId() + " Item Type Id "
							+ newItemGroupId.getItemTypeId() + " Item Group Id "
							+ newItemGroupId.getItemGroupId() + " doesn't exist");
				}
				dbItemGroupId.setDeletionIndicator(0L);
				dbItemGroupId.setCreatedBy(loginUserID);
				dbItemGroupId.setUpdatedBy(loginUserID);
				dbItemGroupId.setCreatedOn(new Date());
				dbItemGroupId.setUpdatedOn(new Date());
				ItemGroup savedItemGroup = itemgroupRepository.save(dbItemGroupId);
				dbItemGroupList.add(savedItemGroup);
			}
		}
		return dbItemGroupList;
	}

	/**
	 *
	 * @param companyId
	 * @param languageId
	 * @param plantId
	 * @param warehouseId
	 * @param itemTypeId
	 * @param updateItemGroup
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<ItemGroup> updateItemGroup (String companyId,String languageId,String plantId,String warehouseId,
											Long itemTypeId, List<AddItemGroup> updateItemGroup, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

			List<ItemGroup> dbItemGroup =
					itemgroupRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndAndDeletionIndicator(
							languageId, companyId, plantId, warehouseId, itemTypeId, 0L);

			if (dbItemGroup != null) {
				for (ItemGroup newItemGroup : dbItemGroup) {
					newItemGroup.setUpdatedBy(loginUserID);
					newItemGroup.setUpdatedOn(new Date());
					newItemGroup.setDeletionIndicator(1L);
					itemgroupRepository.save(newItemGroup);
				}
			} else {
				throw new EntityNotFoundException("The Given Values of companyId " + companyId +
						" plantId " + plantId +
						" warehouseId " + warehouseId +
						" languageId " + languageId +
						" itemTypeId " + itemTypeId + "doesn't exists");
			}

			List<ItemGroup> createItemGroup =createItemGroup(updateItemGroup,loginUserID);

		return createItemGroup;
	}

		/**
		 *
		 * @param companyId
		 * @param languageId
		 * @param plantId
		 * @param warehouseId
		 * @param itemTypeId
		 * @param loginUserID
		 */
		public void deleteItemGroup (String companyId,String languageId,String plantId,String warehouseId,
				Long itemTypeId, String loginUserID) throws ParseException {
			List<ItemGroup> itemgroup = getItemGroup(companyId,languageId,plantId,warehouseId, itemTypeId);

			if ( itemgroup != null) {
				for (ItemGroup itemGroup : itemgroup) {
					itemGroup.setDeletionIndicator(1L);
					itemGroup.setUpdatedBy(loginUserID);
					itemGroup.setUpdatedOn(new Date());
					itemgroupRepository.save(itemGroup);
				}
			}else {
				throw new EntityNotFoundException("Error in deleting Id: " + itemTypeId);
			}
		}

}
