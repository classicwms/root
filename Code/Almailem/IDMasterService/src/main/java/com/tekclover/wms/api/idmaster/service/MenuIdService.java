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
import com.tekclover.wms.api.idmaster.model.decimalnotationid.DecimalNotationId;
import com.tekclover.wms.api.idmaster.model.menuid.FindMenuId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.MenuIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.menuid.AddMenuId;
import com.tekclover.wms.api.idmaster.model.menuid.MenuId;
import com.tekclover.wms.api.idmaster.model.menuid.UpdateMenuId;
import com.tekclover.wms.api.idmaster.repository.MenuIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MenuIdService {

	@Autowired
	private MenuIdRepository menuIdRepository;

	@Autowired
	private CompanyIdRepository companyIdRepository;

	@Autowired
	private PlantIdRepository plantIdRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getMenuIds
	 * @return
	 */
	public List<MenuId> getMenuIds () {
		List<MenuId> menuIdList =  menuIdRepository.findAll();
		menuIdList = menuIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());

		List<MenuId> newMenuIdList=new ArrayList<>();
		String companyCodeId = null;
		String plantId = null;
		String languageId = null;
		String warehouseId = null;
		IKeyValuePair description = null;

		for(MenuId dbMenuId:menuIdList) {

			if(dbMenuId.getCompanyCodeId() != companyCodeId && dbMenuId.getPlantId() != plantId &&
			dbMenuId.getLanguageId() != languageId && dbMenuId.getWarehouseId() != warehouseId) {

				companyCodeId = dbMenuId.getCompanyCodeId();
				plantId = dbMenuId.getPlantId();
				languageId = dbMenuId.getLanguageId();
				warehouseId = dbMenuId.getWarehouseId();

				description = menuIdRepository.getDescription(dbMenuId.getCompanyCodeId(),
						dbMenuId.getLanguageId(),
						dbMenuId.getPlantId(),
						dbMenuId.getWarehouseId());
			}
			if(description != null) {
				dbMenuId.setCompanyIdAndDescription(description.getCompanyCodeId() + "-" + description.getCompanyDesc());
				dbMenuId.setPlantIdAndDescription(description.getPlantId() + "-" + description.getPlantDesc());
				dbMenuId.setWarehouseIdAndDescription(description.getWarehouseId() + "-" + description.getWarehouseDesc());
			}
			newMenuIdList.add(dbMenuId);
		}

//		List<MenuId> newMenuId=new ArrayList<>();
//		for(MenuId dbMenuId:menuIdList) {
//			if (dbMenuId.getCompanyIdAndDescription() != null&&dbMenuId.getPlantIdAndDescription()!=null&&dbMenuId.getWarehouseIdAndDescription()!=null) {
//				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbMenuId.getCompanyCodeId(), dbMenuId.getLanguageId());
//				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbMenuId.getPlantId(), dbMenuId.getLanguageId(), dbMenuId.getCompanyCodeId());
//				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbMenuId.getWarehouseId(), dbMenuId.getLanguageId(), dbMenuId.getCompanyCodeId(), dbMenuId.getPlantId());
//				if (iKeyValuePair != null) {
//					dbMenuId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
//				}
//				if (iKeyValuePair1 != null) {
//					dbMenuId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
//				}
//				if (iKeyValuePair2 != null) {
//					dbMenuId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
//				}
//			}
//			newMenuId.add(dbMenuId);
//		}
		return newMenuIdList;
	}

	/**
	 *
	 * @param menuId
	 * @return
	 */


	public List<MenuId>getMenuId(Long menuId){
		List<MenuId> dbMenuId=menuIdRepository.findByMenuId(menuId);
		if(dbMenuId.isEmpty()){
			throw new BadRequestException("Menu Id -"+menuId+ " doesn't exist");
		}
		return dbMenuId;
	}

	/**
	 *
	 * @param subMenuId
	 * @return
	 */
	public List<MenuId> getSubMenuId (Long subMenuId) {
		List<MenuId> dbSubMenuId = menuIdRepository.findBySubMenuId(subMenuId);
		if(dbSubMenuId.isEmpty()){
			throw new BadRequestException("SubMenu Id -"+subMenuId+"doesn't exist");
		}
		return dbSubMenuId;
	}


	/**
	 * getMenuId
	 * @param menuId
	 * @return
	 */
	public MenuId getMenuId (String warehouseId, Long menuId, Long subMenuId, Long authorizationObjectId,
							 String companyCodeId,String languageId,String plantId) {
		Optional<MenuId> dbMenuId =
				menuIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMenuIdAndSubMenuIdAndAuthorizationObjectIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						menuId,
						subMenuId,
						authorizationObjectId,
						languageId,
						0L
				);
		if (dbMenuId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"menuId - " + menuId +
					"subMenuId - " + subMenuId +
					"authorizationObjectId - " + authorizationObjectId +
					" doesn't exist.");

		}
//		MenuId newMenuId = new MenuId();
//		BeanUtils.copyProperties(dbMenuId.get(),newMenuId, CommonUtils.getNullPropertyNames(dbMenuId));
//		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
//		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
//		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
//		if(iKeyValuePair!=null) {
//			newMenuId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
//		}
//		if(iKeyValuePair1!=null) {
//			newMenuId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
//		}
//		if(iKeyValuePair2!=null) {
//			newMenuId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
//		}
		IKeyValuePair description = menuIdRepository.getDescription(dbMenuId.get().getCompanyCodeId(),
				dbMenuId.get().getLanguageId(),
				dbMenuId.get().getPlantId(),
				dbMenuId.get().getWarehouseId());
		if(description != null) {
			dbMenuId.get().setCompanyIdAndDescription(description.getCompanyCodeId() + "-" + description.getCompanyDesc());
			dbMenuId.get().setPlantIdAndDescription(description.getPlantId() + "-" + description.getPlantDesc());
			dbMenuId.get().setWarehouseIdAndDescription(description.getWarehouseId() + "-" + description.getWarehouseDesc());
		}

		return dbMenuId.get();
	}

//	/**
//	 * 
//	 * @param searchMenuId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<MenuId> findMenuId(SearchMenuId searchMenuId) 
//			throws ParseException {
//		MenuIdSpecification spec = new MenuIdSpecification(searchMenuId);
//		List<MenuId> results = menuIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createMenuId
	 * @param newMenuId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public MenuId createMenuId (AddMenuId newMenuId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		MenuId dbMenuId = new MenuId();
		Optional<MenuId> duplicateMenuId = menuIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMenuIdAndSubMenuIdAndAuthorizationObjectIdAndLanguageIdAndDeletionIndicator(newMenuId.getCompanyCodeId(), newMenuId.getPlantId(), newMenuId.getWarehouseId(), newMenuId.getMenuId(), newMenuId.getSubMenuId(), newMenuId.getAuthorizationObjectId(), newMenuId.getLanguageId(), 0L);
		if (!duplicateMenuId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newMenuId.getWarehouseId(), newMenuId.getCompanyCodeId(), newMenuId.getPlantId(), newMenuId.getLanguageId());
			log.info("newMenuId : " + newMenuId);
			BeanUtils.copyProperties(newMenuId, dbMenuId, CommonUtils.getNullPropertyNames(newMenuId));
			dbMenuId.setDeletionIndicator(0L);
			dbMenuId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbMenuId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbMenuId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbMenuId.setCreatedBy(loginUserID);
			dbMenuId.setUpdatedBy(loginUserID);
			dbMenuId.setCreatedOn(new Date());
			dbMenuId.setUpdatedOn(new Date());
			return menuIdRepository.save(dbMenuId);
		}
	}

	//service-bulk
	public List<MenuId> creatMenuIdBulk(List<AddMenuId> newMenuId, String loginUserId)
			throws IllegalAccessException, InvocationTargetException, Exception {
		List<MenuId> savedMenuId = new ArrayList<>();

		for(AddMenuId nwMenuId: newMenuId) {
			MenuId dbMenuId = new MenuId();
			BeanUtils.copyProperties(nwMenuId, dbMenuId, CommonUtils.getNullPropertyNames(nwMenuId));

			Warehouse dbWarehouse=warehouseService.getWarehouse(nwMenuId.getWarehouseId(), nwMenuId.getCompanyCodeId(), nwMenuId.getPlantId(), nwMenuId.getLanguageId());

			dbMenuId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbMenuId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbMenuId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());

			dbMenuId.setDeletionIndicator(0L);
			dbMenuId.setCreatedBy(loginUserId);
			dbMenuId.setUpdatedBy(loginUserId);
			dbMenuId.setCreatedOn(new Date());
			dbMenuId.setUpdatedOn(new Date());
			savedMenuId.add(menuIdRepository.save(dbMenuId));
		}
		return savedMenuId;
	}

	/**
	 * updateMenuId
	 * @param loginUserID
	 * @param menuId
	 * @param updateMenuId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public MenuId updateMenuId (String warehouseId, Long menuId, Long subMenuId, Long authorizationObjectId,
								String companyCodeId,String languageId,String plantId,String loginUserID, UpdateMenuId updateMenuId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		MenuId dbMenuId = getMenuId(warehouseId, menuId, subMenuId, authorizationObjectId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateMenuId, dbMenuId, CommonUtils.getNullPropertyNames(updateMenuId));
		dbMenuId.setUpdatedBy(loginUserID);
		dbMenuId.setUpdatedOn(new Date());
		return menuIdRepository.save(dbMenuId);
	}

	/**
	 * deleteMenuId
	 * @param loginUserID
	 * @param menuId
	 */
	public void deleteMenuId (String warehouseId, Long menuId, Long subMenuId, Long authorizationObjectId,
							  String companyCodeId,String languageId,String plantId,String loginUserID) {
		MenuId dbMenuId = getMenuId(warehouseId, menuId, subMenuId, authorizationObjectId,companyCodeId,languageId,plantId);
		if ( dbMenuId != null) {
			dbMenuId.setDeletionIndicator(1L);
			dbMenuId.setUpdatedBy(loginUserID);
			menuIdRepository.save(dbMenuId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + menuId);
		}
	}

	//Find MenuId
	public List<MenuId> findMenuId(FindMenuId findMenuId) throws ParseException {

		MenuIdSpecification spec = new MenuIdSpecification(findMenuId);
		List<MenuId> results = menuIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);

		List<MenuId> newMenuIdList=new ArrayList<>();
		String companyCodeId = null;
		String plantId = null;
		String languageId = null;
		String warehouseId = null;
		IKeyValuePair description = null;

		for(MenuId dbMenuId:results) {

			if(dbMenuId.getCompanyCodeId() != companyCodeId && dbMenuId.getPlantId() != plantId &&
					dbMenuId.getLanguageId() != languageId && dbMenuId.getWarehouseId() != warehouseId) {

				companyCodeId = dbMenuId.getCompanyCodeId();
				plantId = dbMenuId.getPlantId();
				languageId = dbMenuId.getLanguageId();
				warehouseId = dbMenuId.getWarehouseId();

				description = menuIdRepository.getDescription(dbMenuId.getCompanyCodeId(),
						dbMenuId.getLanguageId(),
						dbMenuId.getPlantId(),
						dbMenuId.getWarehouseId());
			}
			if(description != null) {
				dbMenuId.setCompanyIdAndDescription(description.getCompanyCodeId() + "-" + description.getCompanyDesc());
				dbMenuId.setPlantIdAndDescription(description.getPlantId() + "-" + description.getPlantDesc());
				dbMenuId.setWarehouseIdAndDescription(description.getWarehouseId() + "-" + description.getWarehouseDesc());
			}
			newMenuIdList.add(dbMenuId);
		}
		return newMenuIdList;
	}
}
