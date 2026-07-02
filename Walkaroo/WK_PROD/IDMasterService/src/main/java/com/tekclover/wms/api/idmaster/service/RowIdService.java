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
import com.tekclover.wms.api.idmaster.model.aisleid.AisleId;
import com.tekclover.wms.api.idmaster.model.rowid.FindRowId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.RowIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.rowid.AddRowId;
import com.tekclover.wms.api.idmaster.model.rowid.RowId;
import com.tekclover.wms.api.idmaster.model.rowid.UpdateRowId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RowIdService{

	@Autowired
	private RowIdRepository rowIdRepository;
	@Autowired
	private StorageSectionIdRepository storageSectionIdRepository;
	@Autowired
	private FloorIdRepository floorIdRepository;
	@Autowired
	private AisleIdRepository aisleIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private AisleIdService aisleIdService;

	/**
	 * getRowIds
	 * @return
	 */
	public List<RowId> getRowIds () {
		List<RowId> rowIdList =  rowIdRepository.findAll();
		rowIdList = rowIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<RowId> newRowId=new ArrayList<>();
		for(RowId dbRowId:rowIdList) {
			if (dbRowId.getCompanyIdAndDescription() != null&&dbRowId.getPlantIdAndDescription()!=null&&dbRowId.getWarehouseIdDescription()!=null&&dbRowId.getFloorIdAndDescription()!=null&&dbRowId.getStorageSectionIdAndDescription()!=null&&dbRowId.getAisleIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbRowId.getCompanyCodeId(), dbRowId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbRowId.getPlantId(), dbRowId.getLanguageId(), dbRowId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbRowId.getWarehouseId(), dbRowId.getLanguageId(), dbRowId.getCompanyCodeId(), dbRowId.getPlantId());
				IKeyValuePair iKeyValuePair3 = floorIdRepository.getFloorIdAndDescription(String.valueOf(dbRowId.getFloorId()), dbRowId.getLanguageId(), dbRowId.getWarehouseId(), dbRowId.getPlantId(), dbRowId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair4 = aisleIdRepository.getAisleIdAndDescription(dbRowId.getAisleId(), dbRowId.getLanguageId(), dbRowId.getWarehouseId(), dbRowId.getCompanyCodeId(), dbRowId.getStorageSectionId(), String.valueOf(dbRowId.getFloorId()), dbRowId.getPlantId());
				IKeyValuePair iKeyValuePair5 = storageSectionIdRepository.getStorageSectionIdAndDescription(dbRowId.getStorageSectionId(), dbRowId.getLanguageId(), dbRowId.getWarehouseId(), dbRowId.getCompanyCodeId(), dbRowId.getPlantId(), String.valueOf(dbRowId.getFloorId()));
				if (iKeyValuePair != null) {
					dbRowId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbRowId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbRowId.setWarehouseIdDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbRowId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbRowId.setAisleIdAndDescription(iKeyValuePair4.getAisleId() + "-" + iKeyValuePair4.getDescription());
				}
				if (iKeyValuePair5 != null) {
					dbRowId.setStorageSectionIdAndDescription(iKeyValuePair5.getStorageSectionId() + "-" + iKeyValuePair5.getDescription());
				}
			}
			newRowId.add(dbRowId);
		}
		return newRowId;
	}

	/**
	 * getRowId
	 * @param rowId
	 * @return
	 */
	public RowId getRowId (String warehouseId, Long floorId, String storageSectionId, String rowId, String companyCodeId, String languageId, String plantId) {
		Optional<RowId> dbRowId =
				rowIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndRowIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						floorId,
						storageSectionId,
						rowId,
						languageId,
						0L
				);
		if (dbRowId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"floorId - " + floorId +
					"storageSectionId - " + storageSectionId +
					"rowId - " + rowId +
					" doesn't exist.");

		}
		RowId newRowId = new RowId();
		BeanUtils.copyProperties(dbRowId.get(),newRowId, CommonUtils.getNullPropertyNames(dbRowId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(floorId),languageId,warehouseId,plantId,companyCodeId);
		IKeyValuePair iKeyValuePair4=storageSectionIdRepository.getStorageSectionIdAndDescription(storageSectionId,languageId,warehouseId,companyCodeId,plantId, String.valueOf(floorId));
		IKeyValuePair iKeyValuePair5=aisleIdRepository.getAisleIdAndDescription(newRowId.getAisleId(),languageId,warehouseId,companyCodeId,storageSectionId, String.valueOf(floorId),plantId);
		if(iKeyValuePair!=null) {
			newRowId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newRowId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newRowId.setWarehouseIdDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newRowId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
		}
		if(iKeyValuePair4!=null) {
			newRowId.setStorageSectionIdAndDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
		}
		if(iKeyValuePair5!=null) {
			newRowId.setAisleIdAndDescription(iKeyValuePair5.getAisleId() + "-" + iKeyValuePair5.getDescription());
		}
		return newRowId;
	}

	/**
	 * createRowId
	 * @param newRowId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RowId createRowId (AddRowId newRowId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		RowId dbRowId = new RowId();
		Optional<RowId> duplicateRowId=rowIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndRowIdAndLanguageIdAndDeletionIndicator(newRowId.getCompanyCodeId(), newRowId.getPlantId(), newRowId.getWarehouseId(),
				newRowId.getFloorId(), newRowId.getStorageSectionId(), newRowId.getRowId(), newRowId.getLanguageId(), 0L);
		if(!duplicateRowId.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		}else {
			AisleId dbAisleId=aisleIdService.getAisleId(newRowId.getWarehouseId(), newRowId.getAisleId(), newRowId.getFloorId(), newRowId.getStorageSectionId(), newRowId.getCompanyCodeId(), newRowId.getLanguageId(), newRowId.getPlantId());
			log.info("newRowId : " + newRowId);
			BeanUtils.copyProperties(newRowId, dbRowId, CommonUtils.getNullPropertyNames(newRowId));
			dbRowId.setDeletionIndicator(0L);
			dbRowId.setCompanyIdAndDescription(dbAisleId.getCompanyIdAndDescription());
			dbRowId.setPlantIdAndDescription(dbAisleId.getPlantIdAndDescription());
			dbRowId.setWarehouseIdDescription(dbAisleId.getWarehouseIdAndDescription());
			dbRowId.setFloorIdAndDescription(dbAisleId.getFloorIdAndDescription());
			dbRowId.setStorageSectionIdAndDescription(dbAisleId.getStorageSectionIdDescription());
			dbRowId.setAisleIdAndDescription(dbAisleId.getAisleId()+ "-" +dbAisleId.getAisleDescription());
			dbRowId.setCreatedBy(loginUserID);
			dbRowId.setUpdatedBy(loginUserID);
			dbRowId.setCreatedOn(new Date());
			dbRowId.setUpdatedOn(new Date());
			return rowIdRepository.save(dbRowId);
		}
	}

	/**
	 * updateRowId
	 * @param loginUserID
	 * @param rowId
	 * @param updateRowId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RowId updateRowId (String warehouseId, Long floorId, String storageSectionId, String rowId, String companyCodeId, String languageId, String plantId, String loginUserID,
							  UpdateRowId updateRowId) throws IllegalAccessException, InvocationTargetException, ParseException {
		RowId dbRowId = getRowId(warehouseId,floorId,storageSectionId,rowId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateRowId, dbRowId, CommonUtils.getNullPropertyNames(updateRowId));
		dbRowId.setUpdatedBy(loginUserID);
		dbRowId.setUpdatedOn(new Date());
		return rowIdRepository.save(dbRowId);
	}

	/**
	 * deleteRowId
	 * @param loginUserID
	 * @param rowId
	 */
	public void deleteRowId (String warehouseId, Long floorId, String storageSectionId, String rowId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		RowId dbRowId = getRowId(warehouseId, floorId, storageSectionId, rowId,companyCodeId,languageId,plantId);
		if ( dbRowId != null) {
			dbRowId.setDeletionIndicator(1L);
			dbRowId.setUpdatedBy(loginUserID);
			rowIdRepository.save(dbRowId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + rowId);
		}
	}
	//Find RowId

	public List<RowId> findRowId(FindRowId findRowId) throws ParseException {

		RowIdSpecification spec = new RowIdSpecification(findRowId);
		List<RowId> results = rowIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<RowId> newRowId=new ArrayList<>();
		for(RowId dbRowId:results) {
			if (dbRowId.getCompanyIdAndDescription() != null&&dbRowId.getPlantIdAndDescription()!=null&&dbRowId.getWarehouseIdDescription()!=null&&dbRowId.getFloorIdAndDescription()!=null&&dbRowId.getStorageSectionIdAndDescription()!=null&&dbRowId.getAisleIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbRowId.getCompanyCodeId(), dbRowId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbRowId.getPlantId(), dbRowId.getLanguageId(), dbRowId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbRowId.getWarehouseId(), dbRowId.getLanguageId(), dbRowId.getCompanyCodeId(), dbRowId.getPlantId());
				IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(dbRowId.getFloorId()), dbRowId.getLanguageId(), dbRowId.getWarehouseId(), dbRowId.getPlantId(), dbRowId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair4=aisleIdRepository.getAisleIdAndDescription(dbRowId.getAisleId(), dbRowId.getLanguageId(), dbRowId.getWarehouseId(), dbRowId.getCompanyCodeId(), dbRowId.getStorageSectionId(), String.valueOf(dbRowId.getFloorId()), dbRowId.getPlantId());
				IKeyValuePair iKeyValuePair5= storageSectionIdRepository.getStorageSectionIdAndDescription(dbRowId.getStorageSectionId(), dbRowId.getLanguageId(), dbRowId.getWarehouseId(), dbRowId.getCompanyCodeId(), dbRowId.getPlantId(), String.valueOf(dbRowId.getFloorId()));
				if (iKeyValuePair != null) {
					dbRowId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbRowId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbRowId.setWarehouseIdDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbRowId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbRowId.setAisleIdAndDescription(iKeyValuePair4.getAisleId() + "-" + iKeyValuePair4.getDescription());
				}
				if (iKeyValuePair5 != null) {
					dbRowId.setStorageSectionIdAndDescription(iKeyValuePair5.getStorageSectionId() + "-" + iKeyValuePair5.getDescription());
				}
			}
			newRowId.add(dbRowId);
		}
		return newRowId;
	}
}
