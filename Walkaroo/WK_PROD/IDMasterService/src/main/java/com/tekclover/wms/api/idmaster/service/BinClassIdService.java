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
import com.tekclover.wms.api.idmaster.model.binclassid.FindBinClassId;
import com.tekclover.wms.api.idmaster.model.moduleid.ModuleId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.BinClassIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.binclassid.AddBinClassId;
import com.tekclover.wms.api.idmaster.model.binclassid.BinClassId;
import com.tekclover.wms.api.idmaster.model.binclassid.UpdateBinClassId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BinClassIdService {
	@Autowired
	private LanguageIdRepository languageIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private BinClassIdRepository binClassIdRepository;

	/**
	 * getBinClassIds
	 * @return
	 */
	public List<BinClassId> getBinClassIds () {
		List<BinClassId> BinClassIdList =  binClassIdRepository.findAll();
		BinClassIdList = BinClassIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<BinClassId> newBinClassId=new ArrayList<>();
		for(BinClassId dbBinClassId:BinClassIdList) {
			if (dbBinClassId.getCompanyIdAndDescription() != null && dbBinClassId.getPlantIdAndDescription() != null && dbBinClassId.getWarehouseIdAndDescription() != null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbBinClassId.getCompanyCodeId(), dbBinClassId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbBinClassId.getPlantId(), dbBinClassId.getLanguageId(), dbBinClassId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbBinClassId.getWarehouseId(), dbBinClassId.getLanguageId(), dbBinClassId.getCompanyCodeId(), dbBinClassId.getPlantId());
				if (iKeyValuePair != null) {
					dbBinClassId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbBinClassId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null){
					dbBinClassId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
			}
		}
			newBinClassId.add(dbBinClassId);
		}
		return newBinClassId;
	}

	/**
	 *
	 * @param warehouseId
	 * @param binClassId
	 * @return
	 */
	public BinClassId getBinClassId (String warehouseId,Long binClassId,String companyCodeId,String languageId,String plantId) {
		Optional<BinClassId> objBinClassId =
				binClassIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinClassIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						binClassId,
						languageId,
						0L);
		if (objBinClassId.isEmpty()) {
			throw new BadRequestException("The given values: warehouseId:" + warehouseId +
					",binClassId: " + binClassId +
					" doesn't exist.");
		}
		BinClassId newBinClassId = new BinClassId();
		BeanUtils.copyProperties(objBinClassId.get(),newBinClassId, CommonUtils.getNullPropertyNames(objBinClassId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newBinClassId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newBinClassId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newBinClassId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newBinClassId;
	}

	/**
	 * createBinClassId
	 * @param newBinClassId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BinClassId createBinClassId (AddBinClassId newBinClassId,String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Optional<BinClassId> duplicateBinClassId =
				binClassIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinClassIdAndLanguageIdAndDeletionIndicator(
						newBinClassId.getCompanyCodeId(),
						newBinClassId.getPlantId(),
						newBinClassId.getWarehouseId(),
						newBinClassId.getBinClassId(),
						newBinClassId.getLanguageId(),
						0L);
		if (!duplicateBinClassId.isEmpty()) {
			throw new BadRequestException("Record is getting duplicated with the given values");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newBinClassId.getWarehouseId(), newBinClassId.getCompanyCodeId(), newBinClassId.getPlantId(), newBinClassId.getLanguageId());
			BinClassId dbBinClassId = new BinClassId();
			log.info("newBinClassId : " + newBinClassId);
			BeanUtils.copyProperties(newBinClassId, dbBinClassId, CommonUtils.getNullPropertyNames(newBinClassId));
			dbBinClassId.setDeletionIndicator(0L);
			dbBinClassId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbBinClassId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbBinClassId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbBinClassId.setCreatedBy(loginUserID);
			dbBinClassId.setUpdatedBy(loginUserID);
			dbBinClassId.setCreatedOn(new Date());
			dbBinClassId.setUpdatedOn(new Date());
			return binClassIdRepository.save(dbBinClassId);
		}
	}

	/**
	 * updateBinClassId
	 * @param loginUserID
	 * @param binClassId
	 * @param updateBinClassId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BinClassId updateBinClassId (String warehouseId, Long binClassId,String companyCodeId,String languageId,String plantId,String loginUserID,
										UpdateBinClassId updateBinClassId) throws IllegalAccessException, InvocationTargetException, ParseException {
		BinClassId dbBinClassId = getBinClassId(warehouseId, binClassId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateBinClassId, dbBinClassId, CommonUtils.getNullPropertyNames(updateBinClassId));
		dbBinClassId.setUpdatedBy(loginUserID);
		dbBinClassId.setUpdatedOn(new Date());
		return binClassIdRepository.save(dbBinClassId);
	}

	/**
	 * deleteBinClassId
	 * @param loginUserID
	 * @param binClassId
	 */
	public void deleteBinClassId (String warehouseId, Long binClassId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		BinClassId dbBinClassId = getBinClassId(warehouseId, binClassId,companyCodeId,languageId,plantId);
		if ( dbBinClassId != null) {
			dbBinClassId.setDeletionIndicator(1L);
			dbBinClassId.setUpdatedBy(loginUserID);
			binClassIdRepository.save(dbBinClassId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + dbBinClassId);
		}
	}
	//Find BinClassId

	public List<BinClassId> findBinClassId(FindBinClassId findBinClassId) throws ParseException {

		BinClassIdSpecification spec = new BinClassIdSpecification(findBinClassId);
		List<BinClassId> results = binClassIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<BinClassId> newBinClassId=new ArrayList<>();
		for(BinClassId dbBinClassId:results) {
			if (dbBinClassId.getCompanyIdAndDescription() != null&&dbBinClassId.getPlantIdAndDescription()!=null&&dbBinClassId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbBinClassId.getCompanyCodeId(), dbBinClassId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbBinClassId.getPlantId(),dbBinClassId.getLanguageId(), dbBinClassId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbBinClassId.getWarehouseId(), dbBinClassId.getLanguageId(), dbBinClassId.getCompanyCodeId(), dbBinClassId.getPlantId());
				if (iKeyValuePair != null) {
					dbBinClassId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbBinClassId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null){
					dbBinClassId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newBinClassId.add(dbBinClassId);
		}
		return newBinClassId;
	}
}
