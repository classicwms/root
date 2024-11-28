package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.AddCycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.CycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.FindCycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.UpdateCycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.uomid.UomId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.CycleCountTypeIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.CycleCountTypeIdSpecification;
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
public class CycleCountTypeIdService{
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private CycleCountTypeIdRepository cycleCountTypeIdRepository;

	/**
	 * getCycleCountTypeIds
	 * @return
	 */
	public List<CycleCountTypeId> getCycleCountTypeIds () {
		List<CycleCountTypeId> cycleCountTypeIdList =  cycleCountTypeIdRepository.findAll();
		cycleCountTypeIdList = cycleCountTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<CycleCountTypeId> newCycleCountTypeId=new ArrayList<>();
		for(CycleCountTypeId dbCycleCountTypeId:cycleCountTypeIdList) {
			if (dbCycleCountTypeId.getCompanyIdAndDescription() != null&&dbCycleCountTypeId.getPlantIdAndDescription()!=null&&dbCycleCountTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbCycleCountTypeId.getCompanyCodeId(), dbCycleCountTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbCycleCountTypeId.getPlantId(), dbCycleCountTypeId.getLanguageId(), dbCycleCountTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbCycleCountTypeId.getWarehouseId(), dbCycleCountTypeId.getLanguageId(), dbCycleCountTypeId.getCompanyCodeId(), dbCycleCountTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbCycleCountTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbCycleCountTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbCycleCountTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newCycleCountTypeId.add(dbCycleCountTypeId);
		}
		return newCycleCountTypeId;
	}

	/**
	 * getCycleCountTypeId
	 * @param cycleCountTypeId
	 * @return
	 */
	public CycleCountTypeId getCycleCountTypeId (String warehouseId,String cycleCountTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<CycleCountTypeId> dbCycleCountTypeId =
				cycleCountTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						cycleCountTypeId,
						languageId,
						0L
				);
		if (dbCycleCountTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"cycleCountTypeId - " + cycleCountTypeId +
					" doesn't exist.");

		}
		CycleCountTypeId newCycleCountTypeId = new CycleCountTypeId();
		BeanUtils.copyProperties(dbCycleCountTypeId.get(),newCycleCountTypeId, CommonUtils.getNullPropertyNames(dbCycleCountTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newCycleCountTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newCycleCountTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newCycleCountTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newCycleCountTypeId;
	}

	/**
	 * createCycleCountTypeId
	 * @param newCycleCountTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CycleCountTypeId createCycleCountTypeId (AddCycleCountTypeId newCycleCountTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		CycleCountTypeId dbCycleCountTypeId = new CycleCountTypeId();
		Optional<CycleCountTypeId> duplicateCycleCountTypeId = cycleCountTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndCycleCountTypeIdAndLanguageIdAndDeletionIndicator(newCycleCountTypeId.getCompanyCodeId(), newCycleCountTypeId.getPlantId(), newCycleCountTypeId.getWarehouseId(), newCycleCountTypeId.getCycleCountTypeId(), newCycleCountTypeId.getLanguageId(), 0L);
		if (!duplicateCycleCountTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newCycleCountTypeId.getWarehouseId(), newCycleCountTypeId.getCompanyCodeId(), newCycleCountTypeId.getPlantId(), newCycleCountTypeId.getLanguageId());
			log.info("newCycleCountTypeId : " + newCycleCountTypeId);
			BeanUtils.copyProperties(newCycleCountTypeId, dbCycleCountTypeId, CommonUtils.getNullPropertyNames(newCycleCountTypeId));
			dbCycleCountTypeId.setDeletionIndicator(0L);
			dbCycleCountTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbCycleCountTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbCycleCountTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbCycleCountTypeId.setCreatedBy(loginUserID);
			dbCycleCountTypeId.setUpdatedBy(loginUserID);
			dbCycleCountTypeId.setCreatedOn(new Date());
			dbCycleCountTypeId.setUpdatedOn(new Date());
			return cycleCountTypeIdRepository.save(dbCycleCountTypeId);
		}
	}

	/**
	 * updateCycleCountTypeId
	 * @param loginUserID
	 * @param cycleCountTypeId
	 * @param updateCycleCountTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public CycleCountTypeId updateCycleCountTypeId (String warehouseId, String cycleCountTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
													UpdateCycleCountTypeId updateCycleCountTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		CycleCountTypeId dbCycleCountTypeId = getCycleCountTypeId( warehouseId, cycleCountTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateCycleCountTypeId, dbCycleCountTypeId, CommonUtils.getNullPropertyNames(updateCycleCountTypeId));
		dbCycleCountTypeId.setUpdatedBy(loginUserID);
		dbCycleCountTypeId.setUpdatedOn(new Date());
		return cycleCountTypeIdRepository.save(dbCycleCountTypeId);
	}

	/**
	 * deleteCycleCountTypeId
	 * @param loginUserID
	 * @param cycleCountTypeId
	 */
	public void deleteCycleCountTypeId (String warehouseId, String cycleCountTypeId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		CycleCountTypeId dbCycleCountTypeId = getCycleCountTypeId( warehouseId,cycleCountTypeId,companyCodeId,languageId,plantId);
		if ( dbCycleCountTypeId != null) {
			dbCycleCountTypeId.setDeletionIndicator(1L);
			dbCycleCountTypeId.setUpdatedBy(loginUserID);
			cycleCountTypeIdRepository.save(dbCycleCountTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + cycleCountTypeId);
		}
	}
	//Find CycleCountTypeId

	public List<CycleCountTypeId> findCycleCountTypeId(FindCycleCountTypeId findCycleCountTypeId) throws ParseException {

		CycleCountTypeIdSpecification spec = new CycleCountTypeIdSpecification(findCycleCountTypeId);
		List<CycleCountTypeId> results = cycleCountTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<CycleCountTypeId> newCycleCountTypeId=new ArrayList<>();
		for(CycleCountTypeId dbCycleCountTypeId:results) {
			if (dbCycleCountTypeId.getCompanyIdAndDescription() != null&&dbCycleCountTypeId.getPlantIdAndDescription()!=null&&dbCycleCountTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbCycleCountTypeId.getCompanyCodeId(), dbCycleCountTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbCycleCountTypeId.getPlantId(), dbCycleCountTypeId.getLanguageId(), dbCycleCountTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbCycleCountTypeId.getWarehouseId(), dbCycleCountTypeId.getLanguageId(), dbCycleCountTypeId.getCompanyCodeId(), dbCycleCountTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbCycleCountTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbCycleCountTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbCycleCountTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}	}
			newCycleCountTypeId.add(dbCycleCountTypeId);
		}
		return newCycleCountTypeId;
	}
}
