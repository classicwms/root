package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.menuid.MenuId;
import com.tekclover.wms.api.idmaster.model.roleaccess.AddRoleAccess;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import com.tekclover.wms.api.idmaster.model.roleaccess.UpdateRoleAccess;
import com.tekclover.wms.api.idmaster.model.user.UserManagement;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.RoleAccessRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleAccessService extends BaseService {
	
	@Autowired
	private RoleAccessRepository roleAccessRepository;
	
	@Autowired
	WarehouseService warehouseService;
	
	@Autowired
	UserManagementService userManagementService;
	
	@Autowired
	MenuIdService menuIdService;
	
	/**
	 * getRoleAccesss
	 * @return
	 */
	public List<RoleAccess> getRoleAccesss () {
		List<RoleAccess> roleAccessList =  roleAccessRepository.findAll();
		roleAccessList = roleAccessList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return roleAccessList;
	}
	
	/**
	 * getRoleAccess
	 * @param userRoleId
	 * @return
	 */
	public RoleAccess getRoleAccess (String warehouseId, Long userRoleId, Long menuId, Long subMenuId) {
		Warehouse warehouse = warehouseService.getWarehouse (warehouseId);
		Optional<RoleAccess> roleAccess =
			roleAccessRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndUserRoleIdAndMenuIdAndSubMenuIdAndDeletionIndicator(
					warehouse.getLanguageId(), warehouse.getCompanyCode(), warehouse.getPlantId(), warehouse.getWarehouseId(),
					userRoleId, menuId, subMenuId, 0L);
		if (!roleAccess.isEmpty()) {
			return roleAccess.get();
		} else {
			throw new BadRequestException("The given RoleAccess ID : " + userRoleId + " doesn't exist.");
		}
	}
	
	/**
	 * getRoleAccess
	 * @param userRoleId
	 * @return
	 */
	public List<RoleAccess> getRoleAccess (String warehouseId, Long userRoleId) {
		Warehouse warehouse = warehouseService.getWarehouse (warehouseId);
		List<RoleAccess> roleAccess =
			roleAccessRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndUserRoleIdAndDeletionIndicator(
					warehouse.getLanguageId(), warehouse.getCompanyCode(), warehouse.getPlantId(), warehouse.getWarehouseId(),
					userRoleId, 0L);
		if (!roleAccess.isEmpty()) {
			return roleAccess;
		} else {
			throw new BadRequestException("The given RoleAccess ID : " + userRoleId + " doesn't exist.");
		}
	}
	
	/**
	 * createRoleAccess
	 * @param newRoleAccess
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<RoleAccess> createRoleAccess (List<AddRoleAccess> addRoleAccessList, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		List<RoleAccess> roleAccessList = new ArrayList<>();
		for (AddRoleAccess newRoleAccess : addRoleAccessList) {
			UserManagement userManagement = 
					userManagementService.getUserManagement(newRoleAccess.getWarehouseId(), loginUserID );
			
				RoleAccess dbRoleAccess = new RoleAccess();
				dbRoleAccess.setLanguageId(userManagement.getLanguageId());
				dbRoleAccess.setCompanyCode(userManagement.getCompanyCode());
				dbRoleAccess.setPlantId(userManagement.getPlantId());
				dbRoleAccess.setWarehouseId(userManagement.getWarehouseId());
				dbRoleAccess.setUserRoleName(newRoleAccess.getUserRoleName());
				dbRoleAccess.setDescription(newRoleAccess.getDescription());
				dbRoleAccess.setStatusId(1L); // ACTIVE
				dbRoleAccess.setDeletionIndicator(0L);
				dbRoleAccess.setCreatedBy(loginUserID);
				dbRoleAccess.setUpdatedBy(loginUserID);
				dbRoleAccess.setCreatedOn(new Date());
				dbRoleAccess.setUpdatedOn(new Date());
				
				MenuId menuId = menuIdService.getMenuId(newRoleAccess.getMenuId());
				dbRoleAccess.setMenuId(newRoleAccess.getMenuId());
				dbRoleAccess.setReferenceField1(menuId.getMenuName());
				
				MenuId subMenuId = menuIdService.getSubMenuId(newRoleAccess.getSubMenuId());
				dbRoleAccess.setSubMenuId(newRoleAccess.getSubMenuId());
				dbRoleAccess.setReferenceField2(subMenuId.getSubMenuName());
				
				if (newRoleAccess.getCreate()) {
					dbRoleAccess.setCreate(1);
				} else {
					dbRoleAccess.setCreate(2);
				}
				
				if (newRoleAccess.getEdit()) {
					dbRoleAccess.setEdit(1);
				} else {
					dbRoleAccess.setEdit(0);
				}
				
				if (newRoleAccess.getView()) {
					dbRoleAccess.setView(1);
				} else {
					dbRoleAccess.setView(0);
				}
				
				if (newRoleAccess.getDelete()) {
					dbRoleAccess.setDelete(1);
				} else {
					dbRoleAccess.setDelete(0);
				}
				
				// Insert Record
				RoleAccess createdRoleAccess = roleAccessRepository.save(dbRoleAccess);
				log.info("createdRoleAccess : " + createdRoleAccess);
				roleAccessList.add(createdRoleAccess);
			}
		return roleAccessList;
	}
	
	/**
	 * updateRoleAccess
	 * @param loginUserId 
	 * @param userRoleId
	 * @param updateRoleAccess
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<RoleAccess> updateRoleAccess (String warehouseId, Long userRoleId, String loginUserID, 
			List<UpdateRoleAccess> updateRoleAccessList) 
			throws IllegalAccessException, InvocationTargetException {
		List<RoleAccess> updatedRoleAccessList = new ArrayList<>();
		for (UpdateRoleAccess updateRoleAccess : updateRoleAccessList) {
			// Getting unique record for each Menu and Submenu
			RoleAccess dbRoleAccess = getRoleAccess(warehouseId, userRoleId, updateRoleAccess.getMenuId(), 
					updateRoleAccess.getSubMenuId());
			BeanUtils.copyProperties(updateRoleAccess, dbRoleAccess, CommonUtils.getNullPropertyNames(updateRoleAccess));
			dbRoleAccess.setUpdatedBy(loginUserID);
			dbRoleAccess.setUpdatedOn(new Date());
			RoleAccess updatedRoleAccess = roleAccessRepository.save(dbRoleAccess);
			log.info("updatedRoleAccess----------> : " + updatedRoleAccess);
			updatedRoleAccessList.add(updatedRoleAccess);
		}
		return updatedRoleAccessList;
	}
	
	/**
	 * deleteRoleAccess
	 * @param loginUserID 
	 * @param userRoleId
	 */
	public void deleteRoleAccess (String warehouseId, Long userRoleId, Long menuId, Long subMenuId, String loginUserID) {
		RoleAccess roleAccess = getRoleAccess(warehouseId, userRoleId, menuId, subMenuId);
		if ( roleAccess != null) {
			roleAccess.setDeletionIndicator(1L);
			roleAccess.setUpdatedBy(loginUserID);
			roleAccessRepository.save(roleAccess);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + userRoleId);
		}
	}
}
