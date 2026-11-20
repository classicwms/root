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
import com.tekclover.wms.api.idmaster.model.barcodetypeid.FindBarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.usertypeid.UserTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.BarcodeTypeIdSpecification;
import com.tekclover.wms.api.idmaster.repository.WarehouseRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.AddBarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.UpdateBarcodeTypeId;
import com.tekclover.wms.api.idmaster.repository.BarcodeTypeIdRepository;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BarcodeTypeIdService{

	@Autowired
	private BarcodeTypeIdRepository barcodeTypeIdRepository;

	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getBarcodeTypeIds
	 * @return
	 */
	public List<BarcodeTypeId> getBarcodeTypeIds () {
		List<BarcodeTypeId> barcodeTypeIdList =  barcodeTypeIdRepository.findAll();
		barcodeTypeIdList = barcodeTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<BarcodeTypeId> newBarcodeTypeId=new ArrayList<>();
		for(BarcodeTypeId dbBarcodeTypeId:barcodeTypeIdList) {
			if (dbBarcodeTypeId.getCompanyIdAndDescription() != null&&dbBarcodeTypeId.getPlantIdAndDescription()!=null&&dbBarcodeTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbBarcodeTypeId.getCompanyCodeId(), dbBarcodeTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbBarcodeTypeId.getPlantId(), dbBarcodeTypeId.getLanguageId(), dbBarcodeTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbBarcodeTypeId.getWarehouseId(), dbBarcodeTypeId.getLanguageId(), dbBarcodeTypeId.getCompanyCodeId(), dbBarcodeTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbBarcodeTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbBarcodeTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbBarcodeTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newBarcodeTypeId.add(dbBarcodeTypeId);
		}
		return newBarcodeTypeId;
	}

	/**
	 * getBarcodeTypeId
	 * @param barcodeTypeId
	 * @return
	 */
	public BarcodeTypeId getBarcodeTypeId (String warehouseId,Long barcodeTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<BarcodeTypeId> dbBarcodeTypeId =
				barcodeTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						barcodeTypeId,
						languageId,
						0L
				);
		if (dbBarcodeTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"barcodeTypeId - " + barcodeTypeId +
					" doesn't exist.");

		}
		BarcodeTypeId newBarCodeTypeId = new BarcodeTypeId();
		BeanUtils.copyProperties(dbBarcodeTypeId.get(),newBarCodeTypeId, CommonUtils.getNullPropertyNames(dbBarcodeTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newBarCodeTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newBarCodeTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newBarCodeTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newBarCodeTypeId;
	}

//	/**
//	 * 
//	 * @param searchBarcodeTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<BarcodeTypeId> findBarcodeTypeId(SearchBarcodeTypeId searchBarcodeTypeId) 
//			throws ParseException {
//		BarcodeTypeIdSpecification spec = new BarcodeTypeIdSpecification(searchBarcodeTypeId);
//		List<BarcodeTypeId> results = barcodeTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createBarcodeTypeId
	 * @param newBarcodeTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BarcodeTypeId createBarcodeTypeId (AddBarcodeTypeId newBarcodeTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		BarcodeTypeId dbBarcodeTypeId = new BarcodeTypeId();
		Optional<BarcodeTypeId> duplicateBarcodeTypeId = barcodeTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeTypeIdAndLanguageIdAndDeletionIndicator(newBarcodeTypeId.getCompanyCodeId(), newBarcodeTypeId.getPlantId(), newBarcodeTypeId.getWarehouseId(), newBarcodeTypeId.getBarcodeTypeId(), newBarcodeTypeId.getLanguageId(), 0L);
		if (!duplicateBarcodeTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newBarcodeTypeId.getWarehouseId(), newBarcodeTypeId.getCompanyCodeId(), newBarcodeTypeId.getPlantId(), newBarcodeTypeId.getLanguageId());
			log.info("newBarcodeTypeId : " + newBarcodeTypeId);
			BeanUtils.copyProperties(newBarcodeTypeId, dbBarcodeTypeId, CommonUtils.getNullPropertyNames(newBarcodeTypeId));
			dbBarcodeTypeId.setDeletionIndicator(0L);
			dbBarcodeTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbBarcodeTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbBarcodeTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbBarcodeTypeId.setCreatedBy(loginUserID);
			dbBarcodeTypeId.setUpdatedBy(loginUserID);
			dbBarcodeTypeId.setCreatedOn(new Date());
			dbBarcodeTypeId.setUpdatedOn(new Date());
			return barcodeTypeIdRepository.save(dbBarcodeTypeId);
		}
	}

	/**
	 * updateBarcodeTypeId
	 * @param loginUserID
	 * @param barcodeTypeId
	 * @param updateBarcodeTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BarcodeTypeId updateBarcodeTypeId (String warehouseId, Long barcodeTypeId,
											  String companyCodeId,String languageId,String plantId,String loginUserID,
											  UpdateBarcodeTypeId updateBarcodeTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		BarcodeTypeId dbBarcodeTypeId = getBarcodeTypeId(warehouseId,barcodeTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateBarcodeTypeId, dbBarcodeTypeId, CommonUtils.getNullPropertyNames(updateBarcodeTypeId));
		dbBarcodeTypeId.setUpdatedBy(loginUserID);
		dbBarcodeTypeId.setUpdatedOn(new Date());
		return barcodeTypeIdRepository.save(dbBarcodeTypeId);
	}

	/**
	 * deleteBarcodeTypeId
	 * @param loginUserID
	 * @param barcodeTypeId
	 */
	public void deleteBarcodeTypeId (String warehouseId, Long barcodeTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		BarcodeTypeId dbBarcodeTypeId = getBarcodeTypeId(warehouseId,barcodeTypeId,companyCodeId,languageId,plantId);
		if ( dbBarcodeTypeId != null) {
			dbBarcodeTypeId.setDeletionIndicator(1L);
			dbBarcodeTypeId.setUpdatedBy(loginUserID);
			barcodeTypeIdRepository.save(dbBarcodeTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + barcodeTypeId);
		}
	}

	//Find BarcodeTypeId
	public List<BarcodeTypeId> findBarcodeTypeId(FindBarcodeTypeId findBarcodeTypeId) throws ParseException {

		BarcodeTypeIdSpecification spec = new BarcodeTypeIdSpecification(findBarcodeTypeId);
		List<BarcodeTypeId> results = barcodeTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<BarcodeTypeId> newBarcodeTypeId=new ArrayList<>();
		for(BarcodeTypeId dbBarcodeTypeId:results) {
			if (dbBarcodeTypeId.getCompanyIdAndDescription() != null&&dbBarcodeTypeId.getPlantIdAndDescription()!=null&&dbBarcodeTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbBarcodeTypeId.getCompanyCodeId(), dbBarcodeTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbBarcodeTypeId.getPlantId(),dbBarcodeTypeId.getLanguageId(), dbBarcodeTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbBarcodeTypeId.getWarehouseId(), dbBarcodeTypeId.getLanguageId(), dbBarcodeTypeId.getCompanyCodeId(), dbBarcodeTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbBarcodeTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbBarcodeTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbBarcodeTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}	}
			newBarcodeTypeId.add(dbBarcodeTypeId);
		}
		return newBarcodeTypeId;
	}
}
