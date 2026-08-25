package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.aisleid.*;
import com.tekclover.wms.api.idmaster.model.floorid.FloorId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.AisledSpecification;
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
public class AisleIdService{
	@Autowired
	private LanguageIdRepository languageIdRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private FloorIdRepository floorIdRepository;
	@Autowired
	private StorageSectionIdRepository storageSectionIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private AisleIdRepository aisleIdRepository;

	@Autowired
	private StorageSectionIdService storageSectionIdService;

	@Autowired
	private WarehouseService warehouseService;


	/**
	 * getAisleIds
	 * @return
	 */
	public List<AisleId> getAisleIds () {
		List<AisleId> aisleIdList =  aisleIdRepository.findAll();
		aisleIdList = aisleIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<AisleId> newAisleId=new ArrayList<>();
		for(AisleId dbAisleId:aisleIdList) {
			if (dbAisleId.getCompanyIdAndDescription() != null&&dbAisleId.getPlantIdAndDescription()!=null&&dbAisleId.getWarehouseIdAndDescription()!=null&&dbAisleId.getFloorIdAndDescription()!=null&&dbAisleId.getStorageSectionIdDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbAisleId.getCompanyCodeId(), dbAisleId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbAisleId.getPlantId(), dbAisleId.getLanguageId(), dbAisleId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbAisleId.getWarehouseId(), dbAisleId.getLanguageId(), dbAisleId.getCompanyCodeId(), dbAisleId.getPlantId());
				IKeyValuePair iKeyValuePair3 = floorIdRepository.getFloorIdAndDescription(String.valueOf(dbAisleId.getFloorId()), dbAisleId.getLanguageId(), dbAisleId.getWarehouseId(), dbAisleId.getPlantId(), dbAisleId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair4 = storageSectionIdRepository.getStorageSectionIdAndDescription(dbAisleId.getStorageSectionId(), dbAisleId.getLanguageId(), dbAisleId.getWarehouseId(), dbAisleId.getCompanyCodeId(), dbAisleId.getPlantId(), String.valueOf(dbAisleId.getFloorId()));
				if (iKeyValuePair != null) {
					dbAisleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbAisleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbAisleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbAisleId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbAisleId.setStorageSectionIdDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
				}
			}
			newAisleId.add(dbAisleId);
		}
		return newAisleId;
	}

	/**
	 * getAisleId
	 * @param aisleId
	 * @return
	 */
	public AisleId getAisleId (String warehouseId,String aisleId,Long floorId,String storageSectionId,String companyCodeId,String languageId,String plantId) {
		Optional<AisleId> dbAisleId =
				aisleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						aisleId,
						floorId,
						storageSectionId,
						languageId,
						0L
				);
		if (dbAisleId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"aisleId - " + aisleId +
					"floorId - " + floorId +
					"storageSectionId - " + storageSectionId +
					" doesn't exist.");

		}
		AisleId newAisleId = new AisleId();
		BeanUtils.copyProperties(dbAisleId.get(),newAisleId, CommonUtils.getNullPropertyNames(dbAisleId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(floorId),languageId,warehouseId,plantId,companyCodeId);
		IKeyValuePair iKeyValuePair4= storageSectionIdRepository.getStorageSectionIdAndDescription(storageSectionId,languageId,warehouseId,companyCodeId,plantId, String.valueOf(floorId));
		if(iKeyValuePair!=null) {
			newAisleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newAisleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newAisleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newAisleId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
		}
		if(iKeyValuePair4!=null) {
			newAisleId.setStorageSectionIdDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
		}
		return newAisleId;
	}

	/**
	 * createAisleId
	 * @param newAisleId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AisleId createAisleId (AddAisleId newAisleId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		AisleId dbAisleId = new AisleId();
		Optional<AisleId> duplicateAisledId = aisleIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(newAisleId.getCompanyCodeId(), newAisleId.getPlantId(), newAisleId.getWarehouseId(), newAisleId.getAisleId(), newAisleId.getFloorId(), newAisleId.getStorageSectionId(), newAisleId.getLanguageId(), 0L);
		if (!duplicateAisledId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			StorageSectionId dbStorageSectionId=storageSectionIdService.getStorageSectionId(newAisleId.getWarehouseId(), newAisleId.getFloorId(), newAisleId.getStorageSectionId(), newAisleId.getCompanyCodeId(),newAisleId.getLanguageId(),newAisleId.getPlantId());
			log.info("newAisleId : " + newAisleId);
			BeanUtils.copyProperties(newAisleId, dbAisleId, CommonUtils.getNullPropertyNames(newAisleId));
			dbAisleId.setDeletionIndicator(0L);
			dbAisleId.setCompanyIdAndDescription(dbStorageSectionId.getCompanyIdAndDescription());
			dbAisleId.setPlantIdAndDescription(dbStorageSectionId.getPlantIdAndDescription());
			dbAisleId.setWarehouseIdAndDescription(dbStorageSectionId.getWarehouseIdAndDescription());
			dbAisleId.setFloorIdAndDescription(dbStorageSectionId.getFloorIdAndDescription());
			dbAisleId.setStorageSectionIdDescription(dbStorageSectionId.getStorageSectionId()+"-"+dbStorageSectionId.getDescription());//storageSectionIdAndDescription Mapped By StorageSectionAndDescription
			dbAisleId.setCreatedBy(loginUserID);
			dbAisleId.setUpdatedBy(loginUserID);
			dbAisleId.setCreatedOn(new Date());
			dbAisleId.setUpdatedOn(new Date());
			return aisleIdRepository.save(dbAisleId);
		}
	}

	/**
	 * updateAisleId
	 * @param loginUserID
	 * @param aisleId
	 * @param updateAisleId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public AisleId updateAisleId (String warehouseId, String aisleId,Long floorId, String storageSectionId,String companyCodeId,String languageId,String plantId,String loginUserID,
								  UpdateAisleId updateAisleId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		AisleId dbAisleId = getAisleId(warehouseId, aisleId, floorId, storageSectionId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateAisleId, dbAisleId, CommonUtils.getNullPropertyNames(updateAisleId));
		dbAisleId.setUpdatedBy(loginUserID);
		dbAisleId.setUpdatedOn(new Date());
		return aisleIdRepository.save(dbAisleId);
	}

	/**
	 * deleteAisleId
	 * @param loginUserID
	 * @param aisleId
	 */
	public void deleteAisleId (String warehouseId, String aisleId,Long floorId, String storageSectionId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		AisleId dbAisleId = getAisleId(warehouseId, aisleId, floorId, storageSectionId,companyCodeId,languageId,plantId);
		if ( dbAisleId != null) {
			dbAisleId.setDeletionIndicator(1L);
			dbAisleId.setUpdatedBy(loginUserID);
			aisleIdRepository.save(dbAisleId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + aisleId);
		}
	}
	//Find AisleId

	public List<AisleId> findAisleId(FindAisleId findAisleId) throws ParseException {

		AisledSpecification spec = new AisledSpecification(findAisleId);
		List<AisleId> results = aisleIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<AisleId> newAisleId=new ArrayList<>();
		for(AisleId dbAisleId:results) {
			if (dbAisleId.getCompanyIdAndDescription() != null&&dbAisleId.getPlantIdAndDescription()!=null&&dbAisleId.getWarehouseIdAndDescription()!=null&&dbAisleId.getFloorIdAndDescription()!=null&&dbAisleId.getStorageSectionIdDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbAisleId.getCompanyCodeId(),dbAisleId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbAisleId.getPlantId(),dbAisleId.getLanguageId(), dbAisleId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbAisleId.getWarehouseId(),dbAisleId.getLanguageId(), dbAisleId.getCompanyCodeId(), dbAisleId.getPlantId());
				IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(dbAisleId.getFloorId()),dbAisleId.getLanguageId(), dbAisleId.getWarehouseId(), dbAisleId.getPlantId(), dbAisleId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair4= storageSectionIdRepository.getStorageSectionIdAndDescription(dbAisleId.getStorageSectionId(),dbAisleId.getLanguageId(), dbAisleId.getWarehouseId(), dbAisleId.getCompanyCodeId(), dbAisleId.getPlantId(), String.valueOf(dbAisleId.getFloorId()));
				if (iKeyValuePair != null) {
					dbAisleId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbAisleId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbAisleId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbAisleId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbAisleId.setStorageSectionIdDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
				}
			}
			newAisleId.add(dbAisleId);
		}
		return newAisleId;
	}
}
