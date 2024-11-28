package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.palletizationlevelid.PalletizationLevelId;
import com.tekclover.wms.api.idmaster.model.refdoctypeid.*;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.RefDocTypeIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.RefDocTypeIdSpecification;
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
public class RefDocTypeIdService{

	@Autowired
	private WarehouseService warehouseService;

	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private RefDocTypeIdRepository refDocTypeIdRepository;

	/**
	 * getRefDocTypeIds
	 * @return
	 */
	public List<RefDocTypeId> getRefDocTypeIds () {
		List<RefDocTypeId> refDocTypeIdList =  refDocTypeIdRepository.findAll();
		refDocTypeIdList = refDocTypeIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<RefDocTypeId> newRefDocTypeId=new ArrayList<>();
		for(RefDocTypeId dbRefDocTypeId:refDocTypeIdList) {
			if (dbRefDocTypeId.getCompanyIdAndDescription() != null&&dbRefDocTypeId.getPlantIdAndDescription()!=null&&dbRefDocTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbRefDocTypeId.getCompanyCodeId(), dbRefDocTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbRefDocTypeId.getPlantId(), dbRefDocTypeId.getLanguageId(), dbRefDocTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbRefDocTypeId.getWarehouseId(), dbRefDocTypeId.getLanguageId(), dbRefDocTypeId.getCompanyCodeId(), dbRefDocTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbRefDocTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbRefDocTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbRefDocTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newRefDocTypeId.add(dbRefDocTypeId);
		}
		return newRefDocTypeId;
	}

	/**
	 * getRefDocTypeId
	 * @param referenceDocumentTypeId
	 * @return
	 */
	public RefDocTypeId getRefDocTypeId (String warehouseId, String referenceDocumentTypeId,String companyCodeId,String languageId,String plantId) {
		Optional<RefDocTypeId> dbRefDocTypeId =
				refDocTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentTypeIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						referenceDocumentTypeId,
						languageId,
						0L
				);
		if (dbRefDocTypeId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"refDocTypeId - " + referenceDocumentTypeId +
					"doesn't exist.");

		}
		RefDocTypeId newRefDocTypeId = new RefDocTypeId();
		BeanUtils.copyProperties(dbRefDocTypeId.get(),newRefDocTypeId, CommonUtils.getNullPropertyNames(dbRefDocTypeId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newRefDocTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newRefDocTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newRefDocTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newRefDocTypeId;
	}

	/**
	 * createRefDocTypeId
	 * @param newRefDocTypeId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RefDocTypeId createRefDocTypeId (AddRefDocTypeId newRefDocTypeId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		RefDocTypeId dbRefDocTypeId = new RefDocTypeId();
		Optional<RefDocTypeId> duplicateRefDocTypeId = refDocTypeIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndReferenceDocumentTypeIdAndLanguageIdAndDeletionIndicator(newRefDocTypeId.getCompanyCodeId(), newRefDocTypeId.getPlantId(), newRefDocTypeId.getWarehouseId(), newRefDocTypeId.getReferenceDocumentTypeId(), newRefDocTypeId.getLanguageId(), 0L);
		if (!duplicateRefDocTypeId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newRefDocTypeId.getWarehouseId(), newRefDocTypeId.getCompanyCodeId(), newRefDocTypeId.getPlantId(), newRefDocTypeId.getLanguageId());
			log.info("newRefDocTypeId : " + newRefDocTypeId);
			BeanUtils.copyProperties(newRefDocTypeId, dbRefDocTypeId, CommonUtils.getNullPropertyNames(newRefDocTypeId));
			dbRefDocTypeId.setDeletionIndicator(0L);
			dbRefDocTypeId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbRefDocTypeId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbRefDocTypeId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbRefDocTypeId.setCreatedBy(loginUserID);
			dbRefDocTypeId.setUpdatedBy(loginUserID);
			dbRefDocTypeId.setCreatedOn(new Date());
			dbRefDocTypeId.setUpdatedOn(new Date());
			return refDocTypeIdRepository.save(dbRefDocTypeId);
		}
	}

	/**
	 * updateRefDocTypeId
	 * @param loginUserID
	 * @param referenceDocumentTypeId
	 * @param updateRefDocTypeId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public RefDocTypeId updateRefDocTypeId (String warehouseId, String referenceDocumentTypeId,String companyCodeId,String languageId,String plantId,String loginUserID,
											UpdateRefDocTypeId updateRefDocTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		RefDocTypeId dbRefDocTypeId = getRefDocTypeId( warehouseId, referenceDocumentTypeId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateRefDocTypeId, dbRefDocTypeId, CommonUtils.getNullPropertyNames(updateRefDocTypeId));
		dbRefDocTypeId.setUpdatedBy(loginUserID);
		dbRefDocTypeId.setUpdatedOn(new Date());
		return refDocTypeIdRepository.save(dbRefDocTypeId);
	}

	/**
	 * deleteRefDocTypeId
	 * @param loginUserID
	 * @param referenceDocumentTypeId
	 */
	public void deleteRefDocTypeId (String warehouseId, String referenceDocumentTypeId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		RefDocTypeId dbRefDocTypeId = getRefDocTypeId( warehouseId, referenceDocumentTypeId,companyCodeId,languageId,plantId);
		if ( dbRefDocTypeId != null) {
			dbRefDocTypeId.setDeletionIndicator(1L);
			dbRefDocTypeId.setUpdatedBy(loginUserID);
			refDocTypeIdRepository.save(dbRefDocTypeId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + referenceDocumentTypeId);
		}
	}

	//Find RefDocTypeId
	public List<RefDocTypeId> findRefDocTypeId(FindRefDocTypeId findRefDocTypeId) throws ParseException {

		RefDocTypeIdSpecification spec = new RefDocTypeIdSpecification(findRefDocTypeId);
		List<RefDocTypeId> results = refDocTypeIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<RefDocTypeId> newRefDocTypeId=new ArrayList<>();
		for(RefDocTypeId dbRefDocTypeId:results) {
			if (dbRefDocTypeId.getCompanyIdAndDescription() != null&&dbRefDocTypeId.getPlantIdAndDescription()!=null&&dbRefDocTypeId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbRefDocTypeId.getCompanyCodeId(), dbRefDocTypeId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbRefDocTypeId.getPlantId(), dbRefDocTypeId.getLanguageId(), dbRefDocTypeId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbRefDocTypeId.getWarehouseId(), dbRefDocTypeId.getLanguageId(), dbRefDocTypeId.getCompanyCodeId(), dbRefDocTypeId.getPlantId());
				if (iKeyValuePair != null) {
					dbRefDocTypeId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbRefDocTypeId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbRefDocTypeId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newRefDocTypeId.add(dbRefDocTypeId);
		}
		return newRefDocTypeId;
	}
}
