package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.menuid.MenuId;
import com.tekclover.wms.api.idmaster.model.moduleid.*;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.ModuleIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.ModuleIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ModuleIdService {

	@Autowired
	private ModuleIdRepository moduleIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getModuleIds
	 * @return
	 */
	public List<ModuleId> getModuleIds () {
		List<ModuleId> moduleIdList =  moduleIdRepository.findAll();
		moduleIdList = moduleIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return moduleIdList;
//		List<ModuleId> newModuleId=new ArrayList<>();
//		for(ModuleId dbModuleId:moduleIdList) {
//			if (dbModuleId.getCompanyIdAndDescription() != null&&dbModuleId.getPlantIdAndDescription()!=null&&dbModuleId.getWarehouseIdAndDescription()!=null) {
//				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbModuleId.getCompanyCodeId(), dbModuleId.getLanguageId());
//				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbModuleId.getPlantId(), dbModuleId.getLanguageId(), dbModuleId.getCompanyCodeId());
//				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbModuleId.getWarehouseId(), dbModuleId.getLanguageId(), dbModuleId.getCompanyCodeId(), dbModuleId.getPlantId());
//				if (iKeyValuePair != null) {
//					dbModuleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
//				}
//				if (iKeyValuePair1 != null) {
//					dbModuleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
//				}
//				if (iKeyValuePair2 != null) {
//					dbModuleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
//				}
//			}
//			newModuleId.add(dbModuleId);
//		}
//		return newModuleId;
	}

	/**
	 * getModuleId
	 * @param moduleId
	 * @return
	 */
	/*public ModuleId getModuleId (String warehouseId, String moduleId,String companyCodeId,String languageId,String plantId) {
		Optional<ModuleId> dbModuleId =
				moduleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						moduleId,
						languageId,
						0L
				);
		if (dbModuleId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"moduleId - " + moduleId +
					" doesn't exist.");

		}
		ModuleId newModuleId = new ModuleId();
		BeanUtils.copyProperties(dbModuleId.get(),newModuleId, CommonUtils.getNullPropertyNames(dbModuleId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newModuleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newModuleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newModuleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newModuleId;
	}*/

	public List<ModuleId> getModuleIdList (String warehouseId, String moduleId,String companyCodeId,String languageId,String plantId) {
		List<ModuleId> moduleIdList =
				moduleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						moduleId,
						languageId,
						0L
				);
		if (moduleIdList.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"moduleId - " + moduleId +
					" doesn't exist.");

		}
		return moduleIdList;
//		List<ModuleId> moduleIds = new ArrayList<>();
//		for(ModuleId dbModuleId : moduleIdList) {
//			ModuleId newModuleId = new ModuleId();
//			BeanUtils.copyProperties(dbModuleId, newModuleId, CommonUtils.getNullPropertyNames(dbModuleId));
//			IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(companyCodeId, languageId);
//			IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(plantId, languageId, companyCodeId);
//			IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(warehouseId, languageId, companyCodeId, plantId);
//			if (iKeyValuePair != null) {
//				newModuleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
//			}
//			if (iKeyValuePair1 != null) {
//				newModuleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
//			}
//			if (iKeyValuePair2 != null) {
//				newModuleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
//			}
//			moduleIds.add(newModuleId);
//		}
//		return moduleIds;
	}

	/**
	 * createModuleId
	 * @param newModuleId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
//	public ModuleId createModuleId (AddModuleId newModuleId, String loginUserID)
//			throws IllegalAccessException, InvocationTargetException {
//		ModuleId dbModuleId = new ModuleId();
//		Optional<ModuleId> duplicateModuleId = moduleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndDeletionIndicator(newModuleId.getCompanyCodeId(), newModuleId.getPlantId(), newModuleId.getWarehouseId(), newModuleId.getModuleId(), newModuleId.getLanguageId(), 0L);
//		if (!duplicateModuleId.isEmpty()) {
//			throw new EntityNotFoundException("Record is Getting Duplicated");
//		} else {
//			Warehouse dbWarehouse=warehouseService.getWarehouse(newModuleId.getWarehouseId(), newModuleId.getCompanyCodeId(), newModuleId.getPlantId(), newModuleId.getLanguageId());
//			log.info("newModuleId : " + newModuleId);
//			BeanUtils.copyProperties(newModuleId, dbModuleId, CommonUtils.getNullPropertyNames(newModuleId));
//			dbModuleId.setDeletionIndicator(0L);
//			dbModuleId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
//			dbModuleId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
//			dbModuleId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
//			dbModuleId.setCreatedBy(loginUserID);
//			dbModuleId.setUpdatedBy(loginUserID);
//			dbModuleId.setCreatedOn(DateUtils.getCurrentKWTDateTime());
//			dbModuleId.setUpdatedOn(DateUtils.getCurrentKWTDateTime());
//			return moduleIdRepository.save(dbModuleId);
//		}
//	}

	public List<ModuleId> createModuleId (List<AddModuleId> newModuleId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<ModuleId> moduleIdList = new ArrayList<>();
//		String moduleId = moduleIdRepository.getModuleId();
		for(AddModuleId newAddModuleId : newModuleId) {

			ModuleId dbModuleId = new ModuleId();

//			Optional<ModuleId> duplicateModuleId = moduleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndDeletionIndicator(
//													newAddModuleId.getCompanyCodeId(),
//													newAddModuleId.getPlantId(),
//													newAddModuleId.getWarehouseId(),
//													newAddModuleId.getModuleId(),
//													newAddModuleId.getLanguageId(), 0L);

			ModuleId duplicateMenuSubMenu = moduleIdRepository.
					findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndMenuIdAndSubMenuIdAndDeletionIndicator(
							newAddModuleId.getCompanyCodeId(),
							newAddModuleId.getPlantId(),
							newAddModuleId.getWarehouseId(),
							newAddModuleId.getModuleId(),
							newAddModuleId.getLanguageId(),
							newAddModuleId.getMenuId(),
							newAddModuleId.getSubMenuId(),0L);

			if (duplicateMenuSubMenu != null) {

				throw new EntityNotFoundException("Record is Getting Duplicated");

			} else {

//				if (duplicateMenuSubMenu != null) {

					/*Optional<ModuleId> duplicateMenuSubMenuId = moduleIdRepository.
							findByModuleIdAndMenuIdAndSubMenuIdAndDeletionIndicator(
									newAddModuleId.getModuleId(),
									newAddModuleId.getMenuId(),
									newAddModuleId.getSubMenuId(),0L);*/
					Optional<ModuleId> duplicateMenuSubMenuId = moduleIdRepository.
							findByMenuIdAndSubMenuIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndDeletionIndicator(
									newAddModuleId.getMenuId(),
									newAddModuleId.getSubMenuId(),
									newAddModuleId.getCompanyCodeId(),
									newAddModuleId.getPlantId(),
									newAddModuleId.getWarehouseId(),
									newAddModuleId.getLanguageId(),0L);

					if (!duplicateMenuSubMenuId.isEmpty()) {

						throw new IllegalAccessException("MenuId: " + newAddModuleId.getMenuId() +
														" and SubMenuId: " + newAddModuleId.getSubMenuId() + " is Getting Duplicated");
					}

//				}
			else {

					Warehouse dbWarehouse = warehouseService.getWarehouse(newAddModuleId.getWarehouseId(),
							newAddModuleId.getCompanyCodeId(),
							newAddModuleId.getPlantId(),
							newAddModuleId.getLanguageId());
					log.info("newModuleId : " + newAddModuleId);

					BeanUtils.copyProperties(newAddModuleId, dbModuleId, CommonUtils.getNullPropertyNames(newAddModuleId));

//						if (moduleId != null) {
//
//							dbModuleId.setModuleId(moduleId);
//
//						} else {
//
//							dbModuleId.setModuleId("1");
//
//						}

					dbModuleId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
					dbModuleId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
					dbModuleId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId() + "-" + dbWarehouse.getWarehouseDesc());

					dbModuleId.setDeletionIndicator(0L);
					dbModuleId.setCreatedBy(loginUserID);
					dbModuleId.setUpdatedBy(loginUserID);
					dbModuleId.setCreatedOn(new Date());
					dbModuleId.setUpdatedOn(new Date());
					moduleIdList.add(moduleIdRepository.save(dbModuleId));
				}
			}
		}

		return moduleIdList;
	}

	/**
	 * updateModuleId
	 * @param loginUserID
	 * @param moduleId
	 * @param updateModuleId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	/*public ModuleId updateModuleId (String warehouseId,String moduleId,String companyCodeId,String languageId,String plantId, String loginUserID,
									UpdateModuleId updateModuleId)
			throws IllegalAccessException, InvocationTargetException {
		ModuleId dbModuleId = getModuleId( warehouseId, moduleId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateModuleId, dbModuleId, CommonUtils.getNullPropertyNames(updateModuleId));
		dbModuleId.setUpdatedBy(loginUserID);
		dbModuleId.setUpdatedOn(DateUtils.getCurrentKWTDateTime());
		return moduleIdRepository.save(dbModuleId);
	}*/

	public List<ModuleId> updateModuleId (String warehouseId,String moduleId,String companyCodeId,
										  String languageId,String plantId, String loginUserID,
										  List<UpdateModuleId> updateModuleId)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<ModuleId> moduleIdList = new ArrayList<>();

		for(UpdateModuleId newUpdateModuleId : updateModuleId) {

			ModuleId dbModuleId = moduleIdRepository.
					findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndMenuIdAndSubMenuIdAndDeletionIndicator(
							companyCodeId,
							plantId,
							warehouseId,
							moduleId,
							languageId,
							newUpdateModuleId.getMenuId(),
							newUpdateModuleId.getSubMenuId(), 0L);

			if (dbModuleId != null) {
				if(newUpdateModuleId.getDeletionIndicator() == 1){
					moduleIdRepository.delete(dbModuleId);
				} else {
					BeanUtils.copyProperties(newUpdateModuleId, dbModuleId, CommonUtils.getNullPropertyNames(newUpdateModuleId));
					dbModuleId.setUpdatedBy(loginUserID);
					dbModuleId.setUpdatedOn(new Date());
					moduleIdList.add(moduleIdRepository.save(dbModuleId));
				}
			} else {

				if(newUpdateModuleId.getDeletionIndicator() != 1) {

					ModuleId newModuleId = new ModuleId();

					Warehouse dbWarehouse = warehouseService.getWarehouse(warehouseId,
							companyCodeId,
							plantId,
							languageId);
					log.info("newModuleId : " + newUpdateModuleId);

					BeanUtils.copyProperties(newUpdateModuleId, newModuleId, CommonUtils.getNullPropertyNames(newUpdateModuleId));

					newModuleId.setModuleId(moduleId);
					newModuleId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
					newModuleId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
					newModuleId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId() + "-" + dbWarehouse.getWarehouseDesc());

					newModuleId.setCompanyCodeId(companyCodeId);
					newModuleId.setPlantId(plantId);
					newModuleId.setWarehouseId(warehouseId);
					newModuleId.setModuleId(moduleId);
					newModuleId.setLanguageId(languageId);

					newModuleId.setDeletionIndicator(0L);
					newModuleId.setCreatedBy(loginUserID);
					newModuleId.setUpdatedBy(loginUserID);
					newModuleId.setCreatedOn(new Date());
					newModuleId.setUpdatedOn(new Date());
					moduleIdList.add(moduleIdRepository.save(newModuleId));
					}
				}
			}
		return moduleIdList;
	}

	/**
	 * deleteModuleId
	 * @param loginUserID
	 * @param moduleId
	 */
	public void deleteModuleId (String warehouseId, String moduleId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		List<ModuleId> dbModuleId = moduleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndLanguageIdAndDeletionIndicator(
				companyCodeId,
				plantId,
				warehouseId,
				moduleId,
				languageId,
				0L);
		if (dbModuleId != null) {
			for (ModuleId newModuleId : dbModuleId) {
				if (newModuleId != null) {
					newModuleId.setDeletionIndicator(1L);
					newModuleId.setUpdatedBy(loginUserID);
					moduleIdRepository.save(newModuleId);
				} else {
					throw new EntityNotFoundException("Error in deleting Id: " + newModuleId.getModuleId());
				}
			}
		}
	}

	//Find ModuleId
	public List<ModuleId> findModuleId(FindModuleId findModuleId) throws ParseException {

		ModuleIdSpecification spec = new ModuleIdSpecification(findModuleId);
		List<ModuleId> results = moduleIdRepository.findAll(spec);
		log.info("results: " + results);
		return results;
//		List<ModuleId> newModuleId=new ArrayList<>();
//		for(ModuleId dbModuleId:results) {
//			if (dbModuleId.getCompanyIdAndDescription() != null&&dbModuleId.getPlantIdAndDescription()!=null&&dbModuleId.getWarehouseIdAndDescription()!=null) {
//				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbModuleId.getCompanyCodeId(),dbModuleId.getLanguageId());
//				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbModuleId.getPlantId(), dbModuleId.getLanguageId(), dbModuleId.getCompanyCodeId());
//				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbModuleId.getWarehouseId(), dbModuleId.getLanguageId(), dbModuleId.getCompanyCodeId(), dbModuleId.getPlantId());
//				if (iKeyValuePair != null) {
//					dbModuleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
//				}
//				if (iKeyValuePair1 != null) {
//					dbModuleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
//				}
//				if (iKeyValuePair2 != null) {
//					dbModuleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
//				}
//			}
//			newModuleId.add(dbModuleId);
//		}
//		return newModuleId;
	}
}
