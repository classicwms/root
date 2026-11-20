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
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.FindBarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.BarcodeSubTypeIdSpecification;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.AddBarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.BarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.model.barcodesubtypeid.UpdateBarcodeSubTypeId;
import com.tekclover.wms.api.idmaster.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BarcodeSubTypeIdService {

	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private BarcodeTypeIdService barcodeTypeIdService;
	@Autowired
	private BarcodeTypeIdRepository barcodeTypeIdRepository;
	@Autowired
	private BarcodeSubTypeIdRepository barcodeSubTypeIdRepository;

	/**
	 * getBarcodeSubTypeIds
	 * @return
	 */
	public List<BarcodeSubTypeId> getBarcodeSubTypeIds () {
		List<BarcodeSubTypeId> barcodeSubTypeIdList =  barcodeSubTypeIdRepository.findAll();
		barcodeSubTypeIdList = barcodeSubTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<BarcodeSubTypeId> newBarcodeSubTypeId=new ArrayList<>();
		for(BarcodeSubTypeId dbBarcodeSubTypeId:barcodeSubTypeIdList) {
			if (dbBarcodeSubTypeId.getCompanyIdAndDescription() != null&&dbBarcodeSubTypeId.getPlantIdAndDescription()!=null&&dbBarcodeSubTypeId.getWarehouseIdAndDescription()!=null&&dbBarcodeSubTypeId.getBarcodeTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbBarcodeSubTypeId.getCompanyCodeId(), dbBarcodeSubTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbBarcodeSubTypeId.getPlantId(), dbBarcodeSubTypeId.getLanguageId(), dbBarcodeSubTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbBarcodeSubTypeId.getWarehouseId(), dbBarcodeSubTypeId.getLanguageId(), dbBarcodeSubTypeId.getCompanyCodeId(), dbBarcodeSubTypeId.getPlantId());
				IKeyValuePair iKeyValuePair3 = barcodeTypeIdRepository.getBarcodeTypeIdAndDescription(String.valueOf(dbBarcodeSubTypeId.getBarcodeTypeId()), dbBarcodeSubTypeId.getLanguageId(), dbBarcodeSubTypeId.getCompanyCodeId(), dbBarcodeSubTypeId.getPlantId(), dbBarcodeSubTypeId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbBarcodeSubTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbBarcodeSubTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbBarcodeSubTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbBarcodeSubTypeId.setBarcodeTypeIdAndDescription(iKeyValuePair3.getBarcodeTypeId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newBarcodeSubTypeId.add(dbBarcodeSubTypeId);
		}
		return newBarcodeSubTypeId;
	}

	/**
	 * getBarcodeSubTypeId
	 * @param barcodeSubTypeId
	 * @return
	 */
	public BarcodeSubTypeId getBarcodeSubTypeId (String warehouseId, Long barcodeTypeId,Long barcodeSubTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<BarcodeSubTypeId> dbBarcodeSubTypeId =
				barcodeSubTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeTypeIdAndBarcodeSubTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						barcodeTypeId,
						barcodeSubTypeId,
						languageId,
						0L
				);
		if (dbBarcodeSubTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"barcodeTypeId - " + barcodeTypeId +
					"barcodeSubTypeId - " + barcodeSubTypeId +
					" doesn't exist.");

		}
		BarcodeSubTypeId newBarcodeSubTypeId = new BarcodeSubTypeId();
		BeanUtils.copyProperties(dbBarcodeSubTypeId.get(),newBarcodeSubTypeId, CommonUtils.getNullPropertyNames(dbBarcodeSubTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= barcodeTypeIdRepository.getBarcodeTypeIdAndDescription(String.valueOf(barcodeTypeId),languageId,companyCodeId,plantId,warehouseId);
		if(iKeyValuePair!=null) {
			newBarcodeSubTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newBarcodeSubTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newBarcodeSubTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newBarcodeSubTypeId.setBarcodeTypeIdAndDescription(iKeyValuePair3.getBarcodeTypeId() + "-" + iKeyValuePair3.getDescription());
		}
		return newBarcodeSubTypeId;
	}

//	/**
//	 * 
//	 * @param searchBarcodeSubTypeId
//	 * @return
//	 * @throws ParseException
//	 */
//	public List<BarcodeSubTypeId> findBarcodeSubTypeId(SearchBarcodeSubTypeId searchBarcodeSubTypeId) 
//			throws ParseException {
//		BarcodeSubTypeIdSpecification spec = new BarcodeSubTypeIdSpecification(searchBarcodeSubTypeId);
//		List<BarcodeSubTypeId> results = barcodeSubTypeIdRepository.findAll(spec);
//		log.info("results: " + results);
//		return results;
//	}

	/**
	 * createBarcodeSubTypeId
	 * @param newBarcodeSubTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BarcodeSubTypeId createBarcodeSubTypeId (AddBarcodeSubTypeId newBarcodeSubTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		BarcodeSubTypeId dbBarcodeSubTypeId = new BarcodeSubTypeId();
		Optional<BarcodeSubTypeId> duplicateBarcodeSubTypeId = barcodeSubTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBarcodeTypeIdAndBarcodeSubTypeIdAndLanguageIdAndDeletionIndicator(newBarcodeSubTypeId.getCompanyCodeId(), newBarcodeSubTypeId.getPlantId(), newBarcodeSubTypeId.getWarehouseId(), newBarcodeSubTypeId.getBarcodeTypeId(), newBarcodeSubTypeId.getBarcodeSubTypeId(), newBarcodeSubTypeId.getLanguageId(), 0L);
		if (!duplicateBarcodeSubTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			BarcodeTypeId dbBarcodeTypeId=barcodeTypeIdService.getBarcodeTypeId(newBarcodeSubTypeId.getWarehouseId(), newBarcodeSubTypeId.getBarcodeTypeId(), newBarcodeSubTypeId.getCompanyCodeId(), newBarcodeSubTypeId.getLanguageId(), newBarcodeSubTypeId.getPlantId());
			log.info("newBarcodeSubTypeId : " + newBarcodeSubTypeId);
			BeanUtils.copyProperties(newBarcodeSubTypeId, dbBarcodeSubTypeId, CommonUtils.getNullPropertyNames(newBarcodeSubTypeId));
			dbBarcodeSubTypeId.setDeletionIndicator(0L);
			dbBarcodeSubTypeId.setCompanyIdAndDescription(dbBarcodeTypeId.getCompanyIdAndDescription());
			dbBarcodeSubTypeId.setPlantIdAndDescription(dbBarcodeTypeId.getPlantIdAndDescription());
			dbBarcodeSubTypeId.setWarehouseIdAndDescription(dbBarcodeTypeId.getWarehouseIdAndDescription());
			dbBarcodeSubTypeId.setBarcodeTypeIdAndDescription(dbBarcodeTypeId.getBarcodeTypeId()+"-"+dbBarcodeTypeId.getBarcodeType());
			dbBarcodeSubTypeId.setCreatedBy(loginUserID);
			dbBarcodeSubTypeId.setUpdatedBy(loginUserID);
			dbBarcodeSubTypeId.setCreatedOn(new Date());
			dbBarcodeSubTypeId.setUpdatedOn(new Date());
			return barcodeSubTypeIdRepository.save(dbBarcodeSubTypeId);
		}
	}

	/**
	 * updateBarcodeSubTypeId
	 * @param loginUserID
	 * @param barcodeSubTypeId
	 * @param updateBarcodeSubTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public BarcodeSubTypeId updateBarcodeSubTypeId (String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId,String companyCodeId,String languageId,String plantId, String loginUserID,
													UpdateBarcodeSubTypeId updateBarcodeSubTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		BarcodeSubTypeId dbBarcodeSubTypeId = getBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateBarcodeSubTypeId, dbBarcodeSubTypeId, CommonUtils.getNullPropertyNames(updateBarcodeSubTypeId));
		dbBarcodeSubTypeId.setUpdatedBy(loginUserID);
		dbBarcodeSubTypeId.setUpdatedOn(new Date());
		return barcodeSubTypeIdRepository.save(dbBarcodeSubTypeId);
	}

	/**
	 * deleteBarcodeSubTypeId
	 * @param loginUserID
	 * @param barcodeSubTypeId
	 */
	public void deleteBarcodeSubTypeId (String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		BarcodeSubTypeId dbBarcodeSubTypeId = getBarcodeSubTypeId(warehouseId, barcodeTypeId,barcodeSubTypeId,companyCodeId,languageId,plantId);
		if ( dbBarcodeSubTypeId != null) {
			dbBarcodeSubTypeId.setDeletionIndicator(1L);
			dbBarcodeSubTypeId.setUpdatedBy(loginUserID);
			barcodeSubTypeIdRepository.save(dbBarcodeSubTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + barcodeSubTypeId);
		}
	}

	//Find BarcodeSubTypeId
	public List<BarcodeSubTypeId> findBarcodeSubTypeId(FindBarcodeSubTypeId findBarcodeSubTypeId) throws ParseException {

		BarcodeSubTypeIdSpecification spec = new BarcodeSubTypeIdSpecification(findBarcodeSubTypeId);
		List<BarcodeSubTypeId> results = barcodeSubTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<BarcodeSubTypeId> newBarcodeSubTypeId=new ArrayList<>();
		for(BarcodeSubTypeId dbBarcodeSubTypeId:results) {
			if (dbBarcodeSubTypeId.getCompanyIdAndDescription() != null&&dbBarcodeSubTypeId.getPlantIdAndDescription()!=null&&dbBarcodeSubTypeId.getWarehouseIdAndDescription()!=null&&dbBarcodeSubTypeId.getBarcodeTypeIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbBarcodeSubTypeId.getCompanyCodeId(),dbBarcodeSubTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbBarcodeSubTypeId.getPlantId(), dbBarcodeSubTypeId.getLanguageId(), dbBarcodeSubTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbBarcodeSubTypeId.getWarehouseId(), dbBarcodeSubTypeId.getLanguageId(), dbBarcodeSubTypeId.getCompanyCodeId(), dbBarcodeSubTypeId.getPlantId());
				IKeyValuePair iKeyValuePair3= barcodeTypeIdRepository.getBarcodeTypeIdAndDescription(String.valueOf(dbBarcodeSubTypeId.getBarcodeTypeId()), dbBarcodeSubTypeId.getLanguageId(), dbBarcodeSubTypeId.getCompanyCodeId(), dbBarcodeSubTypeId.getPlantId(), dbBarcodeSubTypeId.getWarehouseId());
				if (iKeyValuePair != null) {
					dbBarcodeSubTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbBarcodeSubTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbBarcodeSubTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbBarcodeSubTypeId.setBarcodeTypeIdAndDescription(iKeyValuePair3.getBarcodeTypeId() + "-" + iKeyValuePair3.getDescription());
				}
			}
			newBarcodeSubTypeId.add(dbBarcodeSubTypeId);
		}
		return newBarcodeSubTypeId;

	}
}
