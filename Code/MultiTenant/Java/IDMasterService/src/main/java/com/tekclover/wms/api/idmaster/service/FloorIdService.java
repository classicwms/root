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
import com.tekclover.wms.api.idmaster.model.floorid.FindFloorId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.FloorIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.floorid.AddFloorId;
import com.tekclover.wms.api.idmaster.model.floorid.FloorId;
import com.tekclover.wms.api.idmaster.model.floorid.UpdateFloorId;
import com.tekclover.wms.api.idmaster.repository.FloorIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FloorIdService {

	@Autowired
	private FloorIdRepository floorIdRepository;

	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getFloorIds
	 * @return
	 */
	public List<FloorId> getFloorIds () {
		List<FloorId> floorIdList =  floorIdRepository.findAll();
		floorIdList = floorIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<FloorId> newFloorId=new ArrayList<>();
		for(FloorId dbFloorId:floorIdList) {
			if (dbFloorId.getCompanyIdAndDescription() != null&&dbFloorId.getPlantIdAndDescription()!=null&&dbFloorId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbFloorId.getCompanyCodeId(), dbFloorId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbFloorId.getPlantId(), dbFloorId.getLanguageId(), dbFloorId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbFloorId.getWarehouseId(), dbFloorId.getLanguageId(), dbFloorId.getCompanyCodeId(), dbFloorId.getPlantId());
				if (iKeyValuePair != null) {
					dbFloorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbFloorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbFloorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newFloorId.add(dbFloorId);
		}
		return newFloorId;
	}

	/**
	 * getFloorId
	 * @param floorId
	 * @return
	 */
	public FloorId getFloorId (String warehouseId,Long floorId,String companyCodeId,String languageId,String plantId) {
		Optional<FloorId> dbFloorId =
				floorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						floorId,
						languageId,
						0L
				);
		if (dbFloorId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"floorId - " + floorId +
					" doesn't exist.");

		}
		FloorId newFloorId = new FloorId();
		BeanUtils.copyProperties(dbFloorId.get(),newFloorId, CommonUtils.getNullPropertyNames(dbFloorId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newFloorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newFloorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newFloorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newFloorId;
	}

//	/**
//	 *
//	 * @param searchFloorId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<FloorId> findFloorId(SearchFloorId searchFloorId)
//			throws ParseException {
//		FloorIdSpecification spec = new FloorIdSpecification(searchFloorId);
//		List<FloorId> results = floorIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createFloorId
	 * @param newFloorId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public FloorId createFloorId (AddFloorId newFloorId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		FloorId dbFloorId = new FloorId();
		Optional<FloorId> duplicateFloorId=floorIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndLanguageIdAndDeletionIndicator(newFloorId.getCompanyCodeId(), newFloorId.getPlantId(), newFloorId.getWarehouseId(), newFloorId.getFloorId(), newFloorId.getLanguageId(), 0L);
		if(!duplicateFloorId.isEmpty()){
			throw new EntityNotFoundException("Record is Getting Duplicated");
		}else {
			Warehouse dbwarehouse=warehouseService.getWarehouse(newFloorId.getWarehouseId(), newFloorId.getCompanyCodeId(), newFloorId.getPlantId(), newFloorId.getLanguageId());
			log.info("newFloorId : " + newFloorId);
			BeanUtils.copyProperties(newFloorId, dbFloorId, CommonUtils.getNullPropertyNames(newFloorId));
			dbFloorId.setDeletionIndicator(0L);
			dbFloorId.setCompanyIdAndDescription(dbwarehouse.getCompanyIdAndDescription());
			dbFloorId.setPlantIdAndDescription(dbwarehouse.getPlantIdAndDescription());
			dbFloorId.setWarehouseIdAndDescription(dbwarehouse.getWarehouseId()+"-"+dbwarehouse.getWarehouseDesc());
			dbFloorId.setCreatedBy(loginUserID);
			dbFloorId.setUpdatedBy(loginUserID);
			dbFloorId.setCreatedOn(new Date());
			dbFloorId.setUpdatedOn(new Date());
			floorIdRepository.save(dbFloorId);
		}
		return dbFloorId;
	}

	/**
	 * updateFloorId
	 * @param loginUserID
	 * @param floorId
	 * @param updateFloorId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public FloorId updateFloorId (String warehouseId,Long floorId,String companyCodeId,String languageId,String plantId,String loginUserID,
								  UpdateFloorId updateFloorId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		FloorId dbFloorId = getFloorId( warehouseId, floorId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateFloorId, dbFloorId, CommonUtils.getNullPropertyNames(updateFloorId));
		dbFloorId.setUpdatedBy(loginUserID);
		dbFloorId.setUpdatedOn(new Date());
		return floorIdRepository.save(dbFloorId);
	}

	/**
	 * deleteFloorId
	 * @param loginUserID
	 * @param floorId
	 */
	public void deleteFloorId (String warehouseId,Long floorId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		FloorId dbFloorId = getFloorId(warehouseId,floorId,companyCodeId,languageId,plantId);
		if ( dbFloorId != null) {
			dbFloorId.setDeletionIndicator(1L);
			dbFloorId.setUpdatedBy(loginUserID);
			floorIdRepository.save(dbFloorId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + floorId);
		}
	}

	//Find FloorId
	public List<FloorId> findFloorId(FindFloorId findFloorId) throws ParseException {

		FloorIdSpecification spec = new FloorIdSpecification(findFloorId);
		List<FloorId> results = floorIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<FloorId> newFloorId=new ArrayList<>();
		for(FloorId dbFloorId:results) {
			if (dbFloorId.getCompanyIdAndDescription() != null&&dbFloorId.getPlantIdAndDescription()!=null&&dbFloorId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbFloorId.getCompanyCodeId(), dbFloorId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbFloorId.getPlantId(),dbFloorId.getLanguageId(), dbFloorId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbFloorId.getWarehouseId(),dbFloorId.getLanguageId(), dbFloorId.getCompanyCodeId(), dbFloorId.getPlantId());
				if (iKeyValuePair != null) {
					dbFloorId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbFloorId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbFloorId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newFloorId.add(dbFloorId);
		}
		return newFloorId;
	}
}
