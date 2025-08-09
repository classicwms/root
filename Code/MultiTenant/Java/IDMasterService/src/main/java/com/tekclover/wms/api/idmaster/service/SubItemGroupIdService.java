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
import com.tekclover.wms.api.idmaster.model.itemgroupid.ItemGroupId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.FindSubItemGroupId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.SubItemGroupIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.AddSubItemGroupId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.SubItemGroupId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.UpdateSubItemGroupId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubItemGroupIdService{

	@Autowired
	private SubItemGroupIdRepository subItemGroupIdRepository;
	@Autowired
	private ItemGroupIdRepository itemGroupIdRepository;
	@Autowired
	private ItemTypeIdRepository itemTypeIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private ItemGroupIdService itemGroupIdService;


	/**
	 * getSubItemGroupIds
	 * @return
	 */
	public List<SubItemGroupId> getSubItemGroupIds () {
		List<SubItemGroupId> subItemGroupIdList =  subItemGroupIdRepository.findAll();
		subItemGroupIdList = subItemGroupIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<SubItemGroupId> newItemGroupId=new ArrayList<>();
		for(SubItemGroupId dbSubItemGroupId:subItemGroupIdList) {
			if (dbSubItemGroupId.getCompanyIdAndDescription() != null&&dbSubItemGroupId.getPlantIdAndDescription()!=null&&dbSubItemGroupId.getWarehouseIdAndDescription()!=null&&dbSubItemGroupId.getItemTypeIdAndDescription()!=null&&dbSubItemGroupId.getItemGroupIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbSubItemGroupId.getCompanyCodeId(), dbSubItemGroupId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbSubItemGroupId.getPlantId(), dbSubItemGroupId.getLanguageId(), dbSubItemGroupId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbSubItemGroupId.getWarehouseId(), dbSubItemGroupId.getLanguageId(), dbSubItemGroupId.getCompanyCodeId(), dbSubItemGroupId.getPlantId());
				IKeyValuePair iKeyValuePair3= itemTypeIdRepository.getItemTypeIdAndDescription(dbSubItemGroupId.getItemTypeId(), dbSubItemGroupId.getLanguageId(), dbSubItemGroupId.getCompanyCodeId(), dbSubItemGroupId.getPlantId(), dbSubItemGroupId.getWarehouseId());
				IKeyValuePair iKeyValuePair4= itemGroupIdRepository.getItemGroupIdAndDescription(String.valueOf(dbSubItemGroupId.getItemGroupId()), dbSubItemGroupId.getLanguageId(), String.valueOf(dbSubItemGroupId.getItemTypeId()), dbSubItemGroupId.getCompanyCodeId(), dbSubItemGroupId.getWarehouseId(), dbSubItemGroupId.getPlantId());
				if(iKeyValuePair != null) {
					dbSubItemGroupId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if(iKeyValuePair1 != null) {
					dbSubItemGroupId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if(iKeyValuePair2 != null) {
					dbSubItemGroupId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if(iKeyValuePair3 != null) {
					dbSubItemGroupId.setItemTypeIdAndDescription(iKeyValuePair3.getItemTypeId() + "-" + iKeyValuePair3.getDescription());
				}
				if(iKeyValuePair4 != null) {
					dbSubItemGroupId.setItemGroupIdAndDescription(iKeyValuePair4.getItemGroupId() + "-" + iKeyValuePair4.getDescription());
				}
			}
			newItemGroupId.add(dbSubItemGroupId);
		}
		return newItemGroupId;
	}


	/**
	 *
	 * @param warehouseId
	 * @param itemTypeId
	 * @param itemGroupId
	 * @param subItemGroupId
	 * @param companyCodeId
	 * @param languageId
	 * @param plantId
	 * @return
	 */
	public SubItemGroupId getSubItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroupId,
											 String companyCodeId,String languageId,String plantId) {
		Optional<SubItemGroupId> dbSubItemGroupId =
				subItemGroupIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						itemTypeId,
						itemGroupId,
						subItemGroupId,
						languageId,
						0L
				);
		if (dbSubItemGroupId.isEmpty()) {
			throw new BadRequestException("The given values : Company Code Id "
					+ companyCodeId + " plant Id "
					+ plantId + " warehouse Id "
					+ warehouseId + " language Id "
					+ languageId + " item group id "
					+ itemGroupId + " item type id "
					+ itemTypeId + " Sub Item Group Id "
					+ subItemGroupId + " doesn't exist.");

		}
		SubItemGroupId newSubItemGroupId = new SubItemGroupId();
		BeanUtils.copyProperties(dbSubItemGroupId.get(),newSubItemGroupId, CommonUtils.getNullPropertyNames(dbSubItemGroupId));

		IKeyValuePair iKeyValuePair =
				companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);

		IKeyValuePair iKeyValuePair1 =
				plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);

		IKeyValuePair iKeyValuePair2 =
				warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);

		IKeyValuePair iKeyValuePair3 =
				itemTypeIdRepository.getItemTypeIdAndDescription(itemTypeId,languageId,companyCodeId,plantId,warehouseId);

		IKeyValuePair iKeyValuePair4 =
				itemGroupIdRepository.getItemGroupIdAndDescription(String.valueOf(itemGroupId),
						languageId, String.valueOf(itemTypeId),companyCodeId,warehouseId,plantId);

		if(iKeyValuePair!=null) {
			newSubItemGroupId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newSubItemGroupId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newSubItemGroupId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newSubItemGroupId.setItemTypeIdAndDescription(iKeyValuePair3.getItemTypeId() + "-" + iKeyValuePair3.getDescription());
		}
		if(iKeyValuePair4!=null) {
			newSubItemGroupId.setItemGroupIdAndDescription(iKeyValuePair4.getItemGroupId() + "-" + iKeyValuePair4.getDescription());
		}
		return newSubItemGroupId;

	}


	/**
	 *
	 * @param newSubItemGroupId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubItemGroupId createSubItemGroupId (AddSubItemGroupId newSubItemGroupId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SubItemGroupId dbSubItemGroupId = new SubItemGroupId();

		Optional<SubItemGroupId> duplicateSubItemGroupId
				= subItemGroupIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndLanguageIdAndDeletionIndicator(
						newSubItemGroupId.getCompanyCodeId(),newSubItemGroupId.getPlantId(), newSubItemGroupId.getWarehouseId(),
						newSubItemGroupId.getItemTypeId(),newSubItemGroupId.getItemGroupId(), newSubItemGroupId.getSubItemGroupId(),
						newSubItemGroupId.getLanguageId(), 0L);

		if (!duplicateSubItemGroupId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			ItemGroupId dbItemGroupId = itemGroupIdService.getItemGroupId(newSubItemGroupId.getWarehouseId(),
					newSubItemGroupId.getItemTypeId(), newSubItemGroupId.getItemGroupId(), newSubItemGroupId.getCompanyCodeId(),
					newSubItemGroupId.getPlantId(), newSubItemGroupId.getLanguageId());

			log.info("newSubItemGroupId : " + newSubItemGroupId);
			BeanUtils.copyProperties(newSubItemGroupId, dbSubItemGroupId, CommonUtils.getNullPropertyNames(newSubItemGroupId));
			dbSubItemGroupId.setCompanyIdAndDescription(dbItemGroupId.getCompanyIdAndDescription());
			dbSubItemGroupId.setPlantIdAndDescription(dbItemGroupId.getPlantIdAndDescription());
			dbSubItemGroupId.setWarehouseIdAndDescription(dbItemGroupId.getWarehouseIdAndDescription());
			dbSubItemGroupId.setItemGroupIdAndDescription(dbItemGroupId.getItemGroupId()+"-"+dbItemGroupId.getItemGroup());
			dbSubItemGroupId.setItemTypeIdAndDescription(dbItemGroupId.getItemTypeIdAndDescription());
			dbSubItemGroupId.setDeletionIndicator(0L);
			dbSubItemGroupId.setCreatedBy(loginUserID);
			dbSubItemGroupId.setUpdatedBy(loginUserID);
			dbSubItemGroupId.setCreatedOn(new Date());
			dbSubItemGroupId.setUpdatedOn(new Date());
			return subItemGroupIdRepository.save(dbSubItemGroupId);
		}
	}


	/**
	 *
	 * @param warehouseId
	 * @param itemTypeId
	 * @param itemGroupId
	 * @param subItemGroupId
	 * @param companyCodeId
	 * @param languageId
	 * @param plantId
	 * @param loginUserID
	 * @param updateSubItemGroupId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SubItemGroupId updateSubItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroupId,
												String companyCodeId,String languageId,
												String plantId,String loginUserID, UpdateSubItemGroupId updateSubItemGroupId)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		SubItemGroupId dbSubItemGroupId =
				getSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId,companyCodeId,languageId,plantId);

		BeanUtils.copyProperties(updateSubItemGroupId, dbSubItemGroupId, CommonUtils.getNullPropertyNames(updateSubItemGroupId));
		dbSubItemGroupId.setUpdatedBy(loginUserID);
		dbSubItemGroupId.setUpdatedOn(new Date());
		return subItemGroupIdRepository.save(dbSubItemGroupId);
	}

	/**
	 *
	 * @param warehouseId
	 * @param itemTypeId
	 * @param itemGroupId
	 * @param subItemGroupId
	 * @param companyCodeId
	 * @param languageId
	 * @param plantId
	 * @param loginUserID
	 */
	public void deleteSubItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroupId,
									  String companyCodeId,String languageId,String plantId,String loginUserID) {
		SubItemGroupId dbSubItemGroupId
				= getSubItemGroupId(warehouseId, itemTypeId, itemGroupId,subItemGroupId,companyCodeId,languageId,plantId);

		if ( dbSubItemGroupId != null) {
			dbSubItemGroupId.setDeletionIndicator(1L);
			dbSubItemGroupId.setUpdatedBy(loginUserID);
			subItemGroupIdRepository.save(dbSubItemGroupId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + subItemGroupId);
		}
	}

	/**
	 *
	 * @param findSubItemGroupId
	 * @return
	 * @throws ParseException
	 */
	public List<SubItemGroupId> findSubItemGroupId(FindSubItemGroupId findSubItemGroupId) throws ParseException {

		SubItemGroupIdSpecification spec = new SubItemGroupIdSpecification(findSubItemGroupId);
		List<SubItemGroupId> results = subItemGroupIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<SubItemGroupId> newItemGroupId=new ArrayList<>();
		for(SubItemGroupId dbSubItemGroupId:results) {
			if (dbSubItemGroupId.getCompanyIdAndDescription() != null && dbSubItemGroupId.getPlantIdAndDescription() != null
					&& dbSubItemGroupId.getWarehouseIdAndDescription() != null && dbSubItemGroupId.getItemTypeIdAndDescription() != null
					&& dbSubItemGroupId.getItemGroupIdAndDescription()!=null) {

				IKeyValuePair iKeyValuePair =
						companyIdRepository.getCompanyIdAndDescription(dbSubItemGroupId.getCompanyCodeId(), dbSubItemGroupId.getLanguageId());

				IKeyValuePair iKeyValuePair1 =
						plantIdRepository.getPlantIdAndDescription(dbSubItemGroupId.getPlantId(), dbSubItemGroupId.getLanguageId(),
								dbSubItemGroupId.getCompanyCodeId());

				IKeyValuePair iKeyValuePair2 =
						warehouseRepository.getWarehouseIdAndDescription(dbSubItemGroupId.getWarehouseId(),
								dbSubItemGroupId.getLanguageId(), dbSubItemGroupId.getCompanyCodeId(), dbSubItemGroupId.getPlantId());

				IKeyValuePair iKeyValuePair3 =
						itemTypeIdRepository.getItemTypeIdAndDescription(dbSubItemGroupId.getItemTypeId(),
								dbSubItemGroupId.getLanguageId(), dbSubItemGroupId.getCompanyCodeId(), dbSubItemGroupId.getPlantId(),
								dbSubItemGroupId.getWarehouseId());

				IKeyValuePair iKeyValuePair4 =
						itemGroupIdRepository.getItemGroupIdAndDescription(String.valueOf(dbSubItemGroupId.getItemGroupId()),
								dbSubItemGroupId.getLanguageId(), String.valueOf(dbSubItemGroupId.getItemTypeId()),
								dbSubItemGroupId.getCompanyCodeId(), dbSubItemGroupId.getWarehouseId(), dbSubItemGroupId.getPlantId());

				if(iKeyValuePair != null) {
					dbSubItemGroupId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if(iKeyValuePair1 != null) {
					dbSubItemGroupId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if(iKeyValuePair2 != null) {
					dbSubItemGroupId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if(iKeyValuePair3 != null) {
					dbSubItemGroupId.setItemTypeIdAndDescription(iKeyValuePair3.getItemTypeId() + "-" + iKeyValuePair3.getDescription());
				}
				if(iKeyValuePair4 != null) {
					dbSubItemGroupId.setItemGroupIdAndDescription(iKeyValuePair4.getItemGroupId() + "-" + iKeyValuePair4.getDescription());
				}
			}
			newItemGroupId.add(dbSubItemGroupId);
		}
		return newItemGroupId;
	}
}
