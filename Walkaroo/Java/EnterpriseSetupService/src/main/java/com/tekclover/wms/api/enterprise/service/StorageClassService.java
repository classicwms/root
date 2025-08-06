package com.tekclover.wms.api.enterprise.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.warehouse.Warehouse;
import com.tekclover.wms.api.enterprise.repository.CompanyRepository;
import com.tekclover.wms.api.enterprise.repository.PlantRepository;
import com.tekclover.wms.api.enterprise.repository.WarehouseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.model.storageclass.AddStorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.SearchStorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.StorageClass;
import com.tekclover.wms.api.enterprise.model.storageclass.UpdateStorageClass;
import com.tekclover.wms.api.enterprise.repository.StorageClassRepository;
import com.tekclover.wms.api.enterprise.repository.specification.StorageClassSpecification;
import com.tekclover.wms.api.enterprise.util.CommonUtils;
import com.tekclover.wms.api.enterprise.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StorageClassService {
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantRepository plantRepository;
	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private StorageClassRepository storageclassRepository;

	/**
	 * getStorageClasss
	 * @return
	 */
	public List<StorageClass> getStorageClasss () {
		List<StorageClass> storageclassList = storageclassRepository.findAll();
		log.info("storageclassList : " + storageclassList);
		storageclassList = storageclassList.stream().filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageClass>newStorageClass=new ArrayList<>();
		for(StorageClass dbStorageClass:storageclassList) {
			if (dbStorageClass.getCompanyIdAndDescription() != null && dbStorageClass.getPlantIdAndDescription() != null&& dbStorageClass.getWarehouseIdAndDescription()!=null&&dbStorageClass.getDescription()!=null) {
				IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbStorageClass.getCompanyId(), dbStorageClass.getLanguageId());
				IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbStorageClass.getPlantId(), dbStorageClass.getLanguageId(), dbStorageClass.getCompanyId());
				IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageClass.getWarehouseId(), dbStorageClass.getLanguageId(), dbStorageClass.getCompanyId(), dbStorageClass.getPlantId());
				IkeyValuePair ikeyValuePair3 = storageclassRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageClass.getStorageClassId()), dbStorageClass.getLanguageId(), dbStorageClass.getCompanyId(), dbStorageClass.getPlantId(), dbStorageClass.getWarehouseId());
				if (iKeyValuePair != null) {
					dbStorageClass.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageClass.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (ikeyValuePair2 != null) {
					dbStorageClass.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
				}
				if (ikeyValuePair3 != null) {
					dbStorageClass.setDescription(ikeyValuePair3.getDescription());
				}
			}
			newStorageClass.add(dbStorageClass);
		}
		return newStorageClass;
	}

	/**
	 * getStorageClass
	 * @param warehouseId
	 * @param storageClassId
	 * @return
	 */
	public StorageClass getStorageClass (String warehouseId, Long storageClassId,String companyId,String languageId,String plantId) {
		Optional<StorageClass> storageclass =
				storageclassRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndDeletionIndicator(
						languageId,
						companyId,
						plantId,
						warehouseId,
						storageClassId,
						0L);
		if (storageclass.isEmpty()) {
			throw new BadRequestException("The given StorageClass Id : " + storageClassId + " doesn't exist.");
		}
		StorageClass newStorageClass = new StorageClass();
		BeanUtils.copyProperties(storageclass.get(),newStorageClass, CommonUtils.getNullPropertyNames(storageclass));
		IkeyValuePair iKeyValuePair=companyRepository.getCompanyIdAndDescription(companyId,languageId);
		IkeyValuePair iKeyValuePair1=plantRepository.getPlantIdAndDescription(plantId,languageId,companyId);
		IkeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyId,plantId);
		IkeyValuePair ikeyValuePair3=storageclassRepository.getStorageClassIdAndDescription(String.valueOf(storageClassId),languageId,companyId,plantId,warehouseId);
		if(iKeyValuePair!=null) {
			newStorageClass.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newStorageClass.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newStorageClass.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(ikeyValuePair3!=null) {
			newStorageClass.setDescription(ikeyValuePair3.getDescription());
		}
		return newStorageClass;
	}

	/**
	 * findStorageClass
	 * @param searchStorageClass
	 * @return
	 * @throws ParseException
	 */
	public List<StorageClass> findStorageClass(SearchStorageClass searchStorageClass) throws Exception {
		if (searchStorageClass.getStartCreatedOn() != null && searchStorageClass.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchStorageClass.getStartCreatedOn(), searchStorageClass.getEndCreatedOn());
			searchStorageClass.setStartCreatedOn(dates[0]);
			searchStorageClass.setEndCreatedOn(dates[1]);
		}

		StorageClassSpecification spec = new StorageClassSpecification(searchStorageClass);
		List<StorageClass> results = storageclassRepository.findAll(spec);
		log.info("results: " + results);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<StorageClass>newStorageClass=new ArrayList<>();
		for(StorageClass dbStorageClass:results) {
			if (dbStorageClass.getCompanyIdAndDescription() != null && dbStorageClass.getPlantIdAndDescription() != null&& dbStorageClass.getWarehouseIdAndDescription()!=null&&dbStorageClass.getDescription()!=null) {
				IkeyValuePair iKeyValuePair = companyRepository.getCompanyIdAndDescription(dbStorageClass.getCompanyId(), dbStorageClass.getLanguageId());
				IkeyValuePair iKeyValuePair1 = plantRepository.getPlantIdAndDescription(dbStorageClass.getPlantId(), dbStorageClass.getLanguageId(), dbStorageClass.getCompanyId());
				IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbStorageClass.getWarehouseId(), dbStorageClass.getLanguageId(), dbStorageClass.getCompanyId(), dbStorageClass.getPlantId());
				IkeyValuePair ikeyValuePair3=storageclassRepository.getStorageClassIdAndDescription(String.valueOf(dbStorageClass.getStorageClassId()), dbStorageClass.getLanguageId(), dbStorageClass.getCompanyId(), dbStorageClass.getPlantId(), dbStorageClass.getWarehouseId());
				if (iKeyValuePair != null) {
					dbStorageClass.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbStorageClass.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (ikeyValuePair2 != null) {
					dbStorageClass.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
				}
				if (ikeyValuePair3 != null) {
					dbStorageClass.setDescription(ikeyValuePair3.getDescription());
				}
			}
			newStorageClass.add(dbStorageClass);
		}
		return newStorageClass;
	}

	/**
	 * createStorageClass
	 * @param newStorageClass
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public StorageClass createStorageClass (AddStorageClass newStorageClass, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Optional<StorageClass> optStorageClass =
				storageclassRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStorageClassIdAndDeletionIndicator(
						newStorageClass.getLanguageId(),
						newStorageClass.getCompanyId(),
						newStorageClass.getPlantId(),
						newStorageClass.getWarehouseId(),
						newStorageClass.getStorageClassId(),
						0L);
		if (!optStorageClass.isEmpty()) {
			throw new BadRequestException("The given values are getting duplicated.");
		} else {
			IkeyValuePair ikeyValuePair = companyRepository.getCompanyIdAndDescription(newStorageClass.getCompanyId(), newStorageClass.getLanguageId());
			IkeyValuePair ikeyValuePair1 = plantRepository.getPlantIdAndDescription(newStorageClass.getPlantId(), newStorageClass.getLanguageId(), newStorageClass.getCompanyId());
			IkeyValuePair ikeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(newStorageClass.getWarehouseId(), newStorageClass.getLanguageId(), newStorageClass.getCompanyId(), newStorageClass.getPlantId());
			IkeyValuePair ikeyValuePair3 = storageclassRepository.getStorageClassIdAndDescription(String.valueOf(newStorageClass.getStorageClassId()), newStorageClass.getLanguageId(), newStorageClass.getCompanyId(), newStorageClass.getPlantId(), newStorageClass.getWarehouseId());
			StorageClass dbStorageClass = new StorageClass();
			BeanUtils.copyProperties(newStorageClass, dbStorageClass, CommonUtils.getNullPropertyNames(newStorageClass));

			if(ikeyValuePair != null && ikeyValuePair1 != null &&
					ikeyValuePair2 != null && ikeyValuePair3 != null) {
				dbStorageClass.setCompanyIdAndDescription(ikeyValuePair.getCompanyCodeId() + "-" + ikeyValuePair.getDescription());
				dbStorageClass.setPlantIdAndDescription(ikeyValuePair1.getPlantId() + "-" + ikeyValuePair1.getDescription());
				dbStorageClass.setWarehouseIdAndDescription(ikeyValuePair2.getWarehouseId() + "-" + ikeyValuePair2.getDescription());
				dbStorageClass.setDescription(ikeyValuePair3.getDescription());
			}
			else {
				throw new BadRequestException("The given values Company Id "
						+ newStorageClass.getCompanyId() + " Plant Id "
						+ newStorageClass.getPlantId() + " Warehouse Id "
						+ newStorageClass.getWarehouseId() + " Storage Class Id "
						+ newStorageClass.getStorageClassId() + " doesn't exist ");
			}
			dbStorageClass.setDeletionIndicator(0L);
			dbStorageClass.setCreatedBy(loginUserID);
			dbStorageClass.setUpdatedBy(loginUserID);
			dbStorageClass.setCreatedOn(new Date());
			dbStorageClass.setUpdatedOn(new Date());
			return storageclassRepository.save(dbStorageClass);
		}
	}

		/**
		 * updateStorageClass
		 * @param storageClassId
		 * @param updateStorageClass
		 * @return
		 * @throws IllegalAccessException
		 * @throws InvocationTargetException
		 */
		public StorageClass updateStorageClass (String warehouseId, Long storageClassId,String companyId,String languageId,String plantId,UpdateStorageClass updateStorageClass, String loginUserID)
				throws IllegalAccessException, InvocationTargetException, ParseException {
			StorageClass dbStorageClass = getStorageClass(warehouseId,storageClassId,companyId,languageId,plantId);
			BeanUtils.copyProperties(updateStorageClass, dbStorageClass, CommonUtils.getNullPropertyNames(updateStorageClass));
			dbStorageClass.setUpdatedBy(loginUserID);
			dbStorageClass.setUpdatedOn(new Date());
			return storageclassRepository.save(dbStorageClass);
		}

		/**
		 * deleteStorageClass
		 * @param storageClassId
		 */
		public void deleteStorageClass (String warehouseId, Long storageClassId,String companyId,String languageId,String plantId,String loginUserID) throws ParseException {
			StorageClass storageclass = getStorageClass(warehouseId, storageClassId,companyId,languageId,plantId);
			if ( storageclass != null) {
				storageclass.setDeletionIndicator (1L);
				storageclass.setUpdatedBy(loginUserID);
				storageclass.setUpdatedOn(new Date());
				storageclassRepository.save(storageclass);
			} else {
				throw new EntityNotFoundException("Error in deleting Id: " + storageClassId);
			}
		}
}
