package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

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
public class MenuIdService extends BaseService {
	
	@Autowired
	private MenuIdRepository menuIdRepository;
	
	/**
	 * getMenuIds
	 * @return
	 */
	public List<MenuId> getMenuIds () {
		List<MenuId> menuIdList =  menuIdRepository.findAll();
		menuIdList = menuIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return menuIdList;
	}
	
	/**
	 * 
	 * @param menuId
	 * @return
	 */
	public MenuId getMenuId (Long menuId) {
		MenuId dbMenuId = menuIdRepository.findByMenuId(menuId);
		return dbMenuId;
	}
	
	/**
	 * 
	 * @param subMenuId
	 * @return
	 */
	public MenuId getSubMenuId (Long subMenuId) {
		MenuId dbSubMenuId = menuIdRepository.findBySubMenuId(subMenuId);
		return dbSubMenuId;
	}
	
	/**
	 * getMenuId
	 * @param menuId
	 * @return
	 */
	public MenuId getMenuId (String warehouseId, Long menuId, Long subMenuId, Long authorizationObjectId, 
			String authorizationObjectValue) {
		Optional<MenuId> dbMenuId = 
				menuIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndMenuIdAndSubMenuIdAndAuthorizationObjectIdAndAuthorizationObjectValueAndLanguageIdAndDeletionIndicator(
								getCompanyCode(),
								getPlantId(),
								warehouseId,
								menuId,
								subMenuId,
								authorizationObjectId,
								authorizationObjectValue,
								getLanguageId(),
								0L
								);
		if (dbMenuId.isEmpty()) {
			throw new BadRequestException("The given values : " + 
						"warehouseId - " + warehouseId +
						"menuId - " + menuId +
						"subMenuId - " + subMenuId +
						"authorizationObjectId - " + authorizationObjectId +
						"authorizationObjectValue - " + authorizationObjectValue +
						" doesn't exist.");
			
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
			throws IllegalAccessException, InvocationTargetException {
		MenuId dbMenuId = new MenuId();
		log.info("newMenuId : " + newMenuId);
		BeanUtils.copyProperties(newMenuId, dbMenuId, CommonUtils.getNullPropertyNames(newMenuId));
		dbMenuId.setCompanyCodeId(getCompanyCode());
		dbMenuId.setPlantId(getPlantId());
		dbMenuId.setDeletionIndicator(0L);
		dbMenuId.setCreatedBy(loginUserID);
		dbMenuId.setUpdatedBy(loginUserID);
		dbMenuId.setCreatedOn(new Date());
		dbMenuId.setUpdatedOn(new Date());
		return menuIdRepository.save(dbMenuId);
	}
	
	/**
	 * updateMenuId
	 * @param loginUserId 
	 * @param menuId
	 * @param updateMenuId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public MenuId updateMenuId (String warehouseId, Long menuId, Long subMenuId, Long authorizationObjectId, 
			String authorizationObjectValue, String loginUserID, UpdateMenuId updateMenuId) 
			throws IllegalAccessException, InvocationTargetException {
		MenuId dbMenuId = getMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, authorizationObjectValue);
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
			String authorizationObjectValue, String loginUserID) {
		MenuId dbMenuId = getMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, authorizationObjectValue);
		if ( dbMenuId != null) {
			dbMenuId.setDeletionIndicator(1L);
			dbMenuId.setUpdatedBy(loginUserID);
			menuIdRepository.save(dbMenuId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + menuId);
		}
	}
}
