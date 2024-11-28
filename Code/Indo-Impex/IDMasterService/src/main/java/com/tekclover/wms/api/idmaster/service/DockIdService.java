package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.dockid.AddDockId;
import com.tekclover.wms.api.idmaster.model.dockid.DockId;
import com.tekclover.wms.api.idmaster.model.dockid.FindDockId;
import com.tekclover.wms.api.idmaster.model.dockid.UpdateDockId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.DockIdSpecification;
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
public class DockIdService {

	@Autowired
	private DockIdRepository dockIdRepository;
    @Autowired
	private CompanyIdRepository companyIdRepository;
    @Autowired
    private PlantIdRepository plantIdRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getDockIds
	 * @return
	 */
	public List<DockId> getDockIds () {
		List<DockId> dockIdList =  dockIdRepository.findAll();
		dockIdList = dockIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<DockId> newDockId=new ArrayList<>();
		for(DockId dbDockId:dockIdList) {
			if (dbDockId.getCompanyIdAndDescription() != null&&dbDockId.getPlantIdAndDescription()!=null&&dbDockId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbDockId.getCompanyCodeId(), dbDockId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbDockId.getPlantId(), dbDockId.getLanguageId(), dbDockId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbDockId.getWarehouseId(), dbDockId.getLanguageId(), dbDockId.getCompanyCodeId(), dbDockId.getPlantId());
				if (iKeyValuePair != null) {
					dbDockId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbDockId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbDockId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newDockId.add(dbDockId);
		}
		return newDockId;
	}

	/**
	 * getDockId
	 * @param dockId
	 * @return
	 */
	public DockId getDockId (String warehouseId, String dockId,String companyCodeId,String languageId,String plantId) {
		Optional<DockId> dbDockId =
				dockIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDockIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						dockId,
						languageId,
						0L
				);
		if (dbDockId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"dockId - " + dockId +
					" doesn't exist.");

		}
		DockId newDockId = new DockId();
		BeanUtils.copyProperties(dbDockId.get(),newDockId, CommonUtils.getNullPropertyNames(dbDockId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newDockId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newDockId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newDockId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newDockId;
	}

	/**
	 * createDockId
	 * @param newDockId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DockId createDockId (AddDockId newDockId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		DockId dbDockId = new DockId();
		Optional<DockId>duplicateDockId=dockIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDockIdAndLanguageIdAndDeletionIndicator(newDockId.getCompanyCodeId(), newDockId.getPlantId(), newDockId.getWarehouseId(), newDockId.getDockId(), newDockId.getLanguageId(), 0L);
		if(!duplicateDockId.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newDockId.getWarehouseId(), newDockId.getCompanyCodeId(), newDockId.getPlantId(), newDockId.getLanguageId());
			log.info("newDockId : " + newDockId);
			BeanUtils.copyProperties(newDockId, dbDockId, CommonUtils.getNullPropertyNames(newDockId));
			dbDockId.setDeletionIndicator(0L);
			dbDockId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbDockId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbDockId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbDockId.setCreatedBy(loginUserID);
			dbDockId.setUpdatedBy(loginUserID);
			dbDockId.setCreatedOn(new Date());
			dbDockId.setUpdatedOn(new Date());
			return dockIdRepository.save(dbDockId);
		}
	}

	/**
	 * updateDockId
	 * @param loginUserID
	 * @param dockId
	 * @param updateDockId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DockId updateDockId (String warehouseId,String dockId,String companyCodeId,String languageId,String plantId,String loginUserID,
								UpdateDockId updateDockId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		DockId dbDockId = getDockId(warehouseId,dockId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateDockId, dbDockId, CommonUtils.getNullPropertyNames(updateDockId));
		dbDockId.setUpdatedBy(loginUserID);
		dbDockId.setUpdatedOn(new Date());
		return dockIdRepository.save(dbDockId);
	}

	/**
	 * deleteDockId
	 * @param loginUserID
	 * @param dockId
	 */
	public void deleteDockId (String warehouseId, String dockId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		DockId dbDockId = getDockId( warehouseId, dockId,companyCodeId,languageId,plantId);
		if ( dbDockId != null) {
			dbDockId.setDeletionIndicator(1L);
			dbDockId.setUpdatedBy(loginUserID);
			dockIdRepository.save(dbDockId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + dockId);
		}
	}
	//Find DockId

	public List<DockId> findDockId(FindDockId findDockId) throws ParseException {

		DockIdSpecification spec = new DockIdSpecification(findDockId);
		List<DockId> results = dockIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<DockId> newDockId=new ArrayList<>();
		for(DockId dbDockId:results) {
			if (dbDockId.getCompanyIdAndDescription() != null&&dbDockId.getPlantIdAndDescription()!=null&&dbDockId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbDockId.getCompanyCodeId(), dbDockId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbDockId.getPlantId(), dbDockId.getLanguageId(), dbDockId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbDockId.getWarehouseId(), dbDockId.getLanguageId(), dbDockId.getCompanyCodeId(), dbDockId.getPlantId());
				if (iKeyValuePair != null) {
					dbDockId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbDockId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbDockId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newDockId.add(dbDockId);
		}
		return newDockId;
	}
}
