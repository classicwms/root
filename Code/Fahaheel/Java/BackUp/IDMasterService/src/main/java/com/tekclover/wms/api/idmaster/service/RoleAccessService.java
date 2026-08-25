package com.tekclover.wms.api.idmaster.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.roleaccess.FindRoleAccess;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.RoleAccessIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.menuid.MenuId;
import com.tekclover.wms.api.idmaster.model.roleaccess.AddRoleAccess;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import com.tekclover.wms.api.idmaster.model.roleaccess.UpdateRoleAccess;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.RoleAccessRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleAccessService {
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;

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
	 *
	 * @return
	 */
	public List<RoleAccess> getRoleAccesss() {
		List<RoleAccess> roleAccessList = roleAccessRepository.findAll();
		roleAccessList = roleAccessList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return roleAccessList;
	}

	/**
	 * getRoleAccess
	 *
	 * @param roleId
	 * @return
	 */
	public List<RoleAccess> getRoleAccess(String warehouseId, Long roleId,
										  String companyCodeId, String languageId, String plantId) {
		List<RoleAccess> roleAccess =
				roleAccessRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRoleIdAndDeletionIndicator(
						languageId,
						companyCodeId,
						plantId,
						warehouseId,
						roleId, 0L);

		if (roleAccess.isEmpty()) {
			throw new BadRequestException("The given RoleAccess ID : " + roleId + " doesn't exist.");
		}

		return roleAccess;

	}


	/**
	 * createRoleAccess
	 *
	 * @param addRoleAccessList
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<RoleAccess> createRoleAccess(List<AddRoleAccess> addRoleAccessList, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		Long roleId = roleAccessRepository.getRoleId();

		if(roleId==null){
			roleId=1L;
		}

		List<RoleAccess> roleAccessList = new ArrayList<>();

		for (AddRoleAccess newRoleAccess : addRoleAccessList) {

			RoleAccess dbRoleAccess = new RoleAccess();

			Warehouse dbWarehouse = warehouseService.getWarehouse(newRoleAccess.getWarehouseId(), newRoleAccess.getCompanyCodeId(), newRoleAccess.getPlantId(), newRoleAccess.getLanguageId());

			BeanUtils.copyProperties(newRoleAccess, dbRoleAccess, CommonUtils.getNullPropertyNames(newRoleAccess));

			dbRoleAccess.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbRoleAccess.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbRoleAccess.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());

			dbRoleAccess.setStatusId(1L); // ACTIVE
			dbRoleAccess.setRoleId(roleId);
			dbRoleAccess.setDeletionIndicator(0L);
			dbRoleAccess.setCreatedBy(loginUserID);
			dbRoleAccess.setUpdatedBy(loginUserID);
			dbRoleAccess.setCreatedOn(new Date());
			dbRoleAccess.setUpdatedOn(new Date());

			List<MenuId> menuId = menuIdService.getMenuId(newRoleAccess.getMenuId());			//Check Menu Exist or not
			List<MenuId> subMenuId = menuIdService.getSubMenuId(newRoleAccess.getSubMenuId());	//Check Sub Menu Exist or Not

			// Insert Record
			RoleAccess createdRoleAccess = roleAccessRepository.save(dbRoleAccess);
			log.info("createdRoleAccess : " + createdRoleAccess);
			roleAccessList.add(createdRoleAccess);
		}
		return roleAccessList;
	}

	/**
	 * updateRoleAccess
	 *
	 * @param loginUserID
	 * @param roleId
	 * @param updateRoleAccessList
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<RoleAccess> updateRoleAccess(String warehouseId, Long roleId, String companyCodeId,
											 String languageId, String plantId, String loginUserID,
											 List<AddRoleAccess> updateRoleAccessList)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<RoleAccess> dbRoleAccessList = roleAccessRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRoleIdAndDeletionIndicator(
																languageId,
																companyCodeId,
																plantId,
																warehouseId,
																roleId, 0L);
		for (RoleAccess dbRoleAccess : dbRoleAccessList) {
					//delete existing records
					roleAccessRepository.delete(dbRoleAccess);

		}

		//Creating New instead of updating Role Access

		List<RoleAccess> roleAccessList = new ArrayList<>();

		for (AddRoleAccess newRoleAccess : updateRoleAccessList) {

			RoleAccess dbRoleAccess = new RoleAccess();

			Warehouse dbWarehouse = warehouseService.getWarehouse(newRoleAccess.getWarehouseId(), newRoleAccess.getCompanyCodeId(),
																	newRoleAccess.getPlantId(), newRoleAccess.getLanguageId());

			BeanUtils.copyProperties(newRoleAccess, dbRoleAccess, CommonUtils.getNullPropertyNames(newRoleAccess));

			dbRoleAccess.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbRoleAccess.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbRoleAccess.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());

			dbRoleAccess.setStatusId(1L); 														// ACTIVE
			dbRoleAccess.setRoleId(roleId);	
			dbRoleAccess.setDeletionIndicator(0L);
			dbRoleAccess.setCreatedBy(loginUserID);
			dbRoleAccess.setUpdatedBy(loginUserID);
			dbRoleAccess.setCreatedOn(new Date());
			dbRoleAccess.setUpdatedOn(new Date());

			List<MenuId> menuId = menuIdService.getMenuId(newRoleAccess.getMenuId());			//Check Menu Exist or not
			List<MenuId> subMenuId = menuIdService.getSubMenuId(newRoleAccess.getSubMenuId());	//Check Sub Menu Exist or Not

			// Insert Record
			RoleAccess createdRoleAccess = roleAccessRepository.save(dbRoleAccess);

			roleAccessList.add(createdRoleAccess);
		}
		return roleAccessList;
	}

	/**
	 * deleteRoleAccess
	 *
	 * @param loginUserID
	 * @param roleId
	 */
	public void deleteRoleAccess(String warehouseId, Long roleId, String companyCodeId,
								 String languageId, String plantId, String loginUserID) {

		List<RoleAccess> roleAccess = getRoleAccess(warehouseId, roleId, companyCodeId, languageId, plantId);

		for (RoleAccess deleteRoleAccess : roleAccess) {
			if (deleteRoleAccess != null) {
				deleteRoleAccess.setDeletionIndicator(1L);
				deleteRoleAccess.setUpdatedBy(loginUserID);
				roleAccessRepository.save(deleteRoleAccess);
			} else {
				throw new EntityNotFoundException("Error in deleting Id: " + roleId);
			}
		}
	}
	//Find RoleAccess

	public List<RoleAccess> findRoleAccess(FindRoleAccess findRoleAccess) throws ParseException {

		RoleAccessIdSpecification spec = new RoleAccessIdSpecification(findRoleAccess);
		List<RoleAccess> results = roleAccessRepository.findAll(spec);
//		log.info("results: " + results);
		return results;
	}
}
