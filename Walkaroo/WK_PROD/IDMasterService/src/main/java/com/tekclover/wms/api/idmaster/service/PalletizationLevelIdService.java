package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.handlingunitid.HandlingUnitId;
import com.tekclover.wms.api.idmaster.model.palletizationlevelid.*;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PalletizationLevelIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.PalletizationLevelIdSpecification;
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
public class PalletizationLevelIdService {

	@Autowired
	private PalletizationLevelIdRepository palletizationLevelIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getPalletizationLevelIds
	 * @return
	 */
	public List<PalletizationLevelId> getPalletizationLevelIds () {
		List<PalletizationLevelId> palletizationLevelIdList =  palletizationLevelIdRepository.findAll();
		palletizationLevelIdList = palletizationLevelIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<PalletizationLevelId> newPalletizationLevelId=new ArrayList<>();
		for(PalletizationLevelId dbPalletizationLevelId:palletizationLevelIdList) {
			if (dbPalletizationLevelId.getCompanyIdAndDescription() != null&&dbPalletizationLevelId.getPlantIdAndDescription()!=null&&dbPalletizationLevelId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbPalletizationLevelId.getCompanyCodeId(), dbPalletizationLevelId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbPalletizationLevelId.getPlantId(), dbPalletizationLevelId.getLanguageId(), dbPalletizationLevelId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbPalletizationLevelId.getWarehouseId(), dbPalletizationLevelId.getLanguageId(), dbPalletizationLevelId.getCompanyCodeId(), dbPalletizationLevelId.getPlantId());
				if (iKeyValuePair != null) {
					dbPalletizationLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbPalletizationLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbPalletizationLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newPalletizationLevelId.add(dbPalletizationLevelId);
		}
		return newPalletizationLevelId;
	}

	/**
	 * getPalletizationLevelId
	 * @param palletizationLevelId
	 * @return
	 */
	public PalletizationLevelId getPalletizationLevelId (String warehouseId, String palletizationLevelId, String palletizationLevel,
														 String companyCodeId,String languageId,String plantId) {
		Optional<PalletizationLevelId> dbPalletizationLevelId =
				palletizationLevelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPalletizationLevelIdAndPalletizationLevelAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						palletizationLevelId,
						palletizationLevel,
						languageId,
						0L
				);
		if (dbPalletizationLevelId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"palletizationLevelId - " + palletizationLevelId +
					"palletizationLevel - " + palletizationLevel +
					" doesn't exist.");

		}
		PalletizationLevelId newPalletizationLevelId = new PalletizationLevelId();
		BeanUtils.copyProperties(dbPalletizationLevelId.get(),newPalletizationLevelId, CommonUtils.getNullPropertyNames(dbPalletizationLevelId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newPalletizationLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newPalletizationLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newPalletizationLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
			return newPalletizationLevelId;
	}

	/**
	 * createPalletizationLevelId
	 * @param newPalletizationLevelId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PalletizationLevelId createPalletizationLevelId (AddPalletizationLevelId newPalletizationLevelId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		PalletizationLevelId dbPalletizationLevelId = new PalletizationLevelId();
		Optional<PalletizationLevelId> duplicatePalletizationLevelId = palletizationLevelIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPalletizationLevelIdAndPalletizationLevelAndLanguageIdAndDeletionIndicator(newPalletizationLevelId.getCompanyCodeId(), newPalletizationLevelId.getPlantId(), newPalletizationLevelId.getWarehouseId(), newPalletizationLevelId.getPalletizationLevelId(), newPalletizationLevelId.getPalletizationLevel(), newPalletizationLevelId.getLanguageId(), 0L);
		if (!duplicatePalletizationLevelId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newPalletizationLevelId.getWarehouseId(), newPalletizationLevelId.getCompanyCodeId(), newPalletizationLevelId.getPlantId(), newPalletizationLevelId.getLanguageId());
			log.info("newPalletizationLevelId : " + newPalletizationLevelId);
			BeanUtils.copyProperties(newPalletizationLevelId, dbPalletizationLevelId, CommonUtils.getNullPropertyNames(newPalletizationLevelId));
			dbPalletizationLevelId.setDeletionIndicator(0L);
			dbPalletizationLevelId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbPalletizationLevelId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbPalletizationLevelId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbPalletizationLevelId.setCreatedBy(loginUserID);
			dbPalletizationLevelId.setUpdatedBy(loginUserID);
			dbPalletizationLevelId.setCreatedOn(new Date());
			dbPalletizationLevelId.setUpdatedOn(new Date());
			return palletizationLevelIdRepository.save(dbPalletizationLevelId);
		}
	}

	/**
	 * updatePalletizationLevelId
	 * @param loginUserID
	 * @param palletizationLevelId
	 * @param updatePalletizationLevelId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PalletizationLevelId updatePalletizationLevelId (String warehouseId, String palletizationLevelId, String palletizationLevel,
															String companyCodeId,String languageId,String plantId, String loginUserID,
															UpdatePalletizationLevelId updatePalletizationLevelId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		PalletizationLevelId dbPalletizationLevelId = getPalletizationLevelId( warehouseId, palletizationLevelId, palletizationLevel,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updatePalletizationLevelId, dbPalletizationLevelId, CommonUtils.getNullPropertyNames(updatePalletizationLevelId));
		dbPalletizationLevelId.setUpdatedBy(loginUserID);
		dbPalletizationLevelId.setUpdatedOn(new Date());
		return palletizationLevelIdRepository.save(dbPalletizationLevelId);
	}

	/**
	 * deletePalletizationLevelId
	 * @param loginUserID
	 * @param palletizationLevelId
	 */
	public void deletePalletizationLevelId (String warehouseId, String palletizationLevelId, String palletizationLevel,String companyCodeId,String languageId,String plantId,String loginUserID) {
		PalletizationLevelId dbPalletizationLevelId = getPalletizationLevelId( warehouseId, palletizationLevelId, palletizationLevel,companyCodeId,languageId,plantId);
		if ( dbPalletizationLevelId != null) {
			dbPalletizationLevelId.setDeletionIndicator(1L);
			dbPalletizationLevelId.setUpdatedBy(loginUserID);
			palletizationLevelIdRepository.save(dbPalletizationLevelId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + palletizationLevelId);
		}
	}

	//Find PalletizationLevelId
	public List<PalletizationLevelId> findPalletizationLevelId (FindPalletizationLevelId findPalletizationLevelId) throws ParseException {

		PalletizationLevelIdSpecification spec = new PalletizationLevelIdSpecification(findPalletizationLevelId);
		List<PalletizationLevelId> results = palletizationLevelIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<PalletizationLevelId> newPalletizationLevelId=new ArrayList<>();
		for(PalletizationLevelId dbPalletizationLevelId:results) {
			if (dbPalletizationLevelId.getCompanyIdAndDescription() != null&&dbPalletizationLevelId.getPlantIdAndDescription()!=null&&dbPalletizationLevelId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbPalletizationLevelId.getCompanyCodeId(), dbPalletizationLevelId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbPalletizationLevelId.getPlantId(), dbPalletizationLevelId.getLanguageId(), dbPalletizationLevelId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbPalletizationLevelId.getWarehouseId(), dbPalletizationLevelId.getLanguageId(), dbPalletizationLevelId.getCompanyCodeId(), dbPalletizationLevelId.getPlantId());
				if (iKeyValuePair != null) {
					dbPalletizationLevelId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbPalletizationLevelId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbPalletizationLevelId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newPalletizationLevelId.add(dbPalletizationLevelId);
		}
		return newPalletizationLevelId;
	}

}
