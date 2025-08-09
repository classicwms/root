package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.rowid.RowId;
import com.tekclover.wms.api.idmaster.model.spanid.*;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.SpanIdSpecification;
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
public class SpanIdService {
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private LanguageIdRepository languageIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private StorageSectionIdRepository storageSectionIdRepository;
	@Autowired
	private FloorIdRepository floorIdRepository;
	@Autowired
	private RowIdRepository rowIdRepository;
	@Autowired
	private AisleIdRepository aisleIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private RowIdService rowIdService;
	@Autowired
	private SpanIdRepository spanIdRepository;

	/**
	 * getSpanIds
	 * @return
	 */
	public List<SpanId> getSpanIds () {
		List<SpanId> spanIdList =  spanIdRepository.findAll();
		spanIdList = spanIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<SpanId> newSpanId=new ArrayList<>();
		for(SpanId dbSpanId:spanIdList) {
			if (dbSpanId.getCompanyIdAndDescription() != null && dbSpanId.getPlantIdAndDescription() != null && dbSpanId.getWarehouseIdAndDescription() != null && dbSpanId.getFloorIdAndDescription() != null && dbSpanId.getStorageSectionIdAndDescription() != null && dbSpanId.getRowIdAndDescription() != null && dbSpanId.getAisleIdAndDescription() != null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbSpanId.getCompanyCodeId(), dbSpanId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbSpanId.getPlantId(), dbSpanId.getLanguageId(), dbSpanId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbSpanId.getWarehouseId(), dbSpanId.getLanguageId(), dbSpanId.getCompanyCodeId(), dbSpanId.getPlantId());
				IKeyValuePair iKeyValuePair3 = floorIdRepository.getFloorIdAndDescription(String.valueOf(dbSpanId.getFloorId()), dbSpanId.getLanguageId(), dbSpanId.getWarehouseId(), dbSpanId.getPlantId(), dbSpanId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair4 = storageSectionIdRepository.getStorageSectionIdAndDescription(dbSpanId.getStorageSectionId(), dbSpanId.getLanguageId(), dbSpanId.getWarehouseId(), dbSpanId.getCompanyCodeId(), dbSpanId.getPlantId(), String.valueOf(dbSpanId.getFloorId()));
				IKeyValuePair iKeyValuePair5 = rowIdRepository.getRowIdAndDescription(dbSpanId.getRowId(), dbSpanId.getLanguageId(), dbSpanId.getWarehouseId(), dbSpanId.getCompanyCodeId(), dbSpanId.getPlantId(), dbSpanId.getStorageSectionId(), String.valueOf(dbSpanId.getFloorId()));
				IKeyValuePair iKeyValuePair6 = aisleIdRepository.getAisleIdAndDescription(dbSpanId.getAisleId(), dbSpanId.getLanguageId(), dbSpanId.getWarehouseId(), dbSpanId.getCompanyCodeId(), dbSpanId.getStorageSectionId(), String.valueOf(dbSpanId.getFloorId()), dbSpanId.getPlantId());
				if (iKeyValuePair != null) {
					dbSpanId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbSpanId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbSpanId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbSpanId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbSpanId.setStorageSectionIdAndDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
				}
				if (iKeyValuePair5 != null) {
					dbSpanId.setRowIdAndDescription(iKeyValuePair5.getRowId() + "-" + iKeyValuePair5.getDescription());
				}
				if (iKeyValuePair6 != null) {
					dbSpanId.setAisleIdAndDescription(iKeyValuePair6.getAisleId() + "-" + iKeyValuePair6.getDescription());
				}
			}
			newSpanId.add(dbSpanId);
		}
		return newSpanId;
	}

	/**
	 * getSpanId
	 * @param spanId
	 * @return
	 */
	public SpanId getSpanId (String warehouseId,String aisleId,String spanId,Long floorId,String storageSectionId,String companyCodeId,String languageId,String plantId) {
		Optional<SpanId> dbSpanId =
				spanIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						aisleId,
						spanId,
						floorId,
						storageSectionId,
						languageId,
						0L
				);
		if (dbSpanId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"aisleId - " + aisleId +
					"spanId - " + spanId +
					"floorId - " + floorId +
					"storageSectionId - " + storageSectionId +
					" doesn't exist.");

		}
		SpanId newSpanId = new SpanId();
		BeanUtils.copyProperties(dbSpanId.get(),newSpanId, CommonUtils.getNullPropertyNames(dbSpanId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(floorId),languageId,warehouseId,plantId,companyCodeId);
		IKeyValuePair iKeyValuePair4=storageSectionIdRepository.getStorageSectionIdAndDescription(storageSectionId,languageId,warehouseId,companyCodeId,plantId, String.valueOf(floorId));
		IKeyValuePair iKeyValuePair5=aisleIdRepository.getAisleIdAndDescription(aisleId,languageId,warehouseId,companyCodeId,storageSectionId, String.valueOf(floorId),plantId);
		IKeyValuePair iKeyValuePair6= rowIdRepository.getRowIdAndDescription(newSpanId.getRowId(),languageId,warehouseId,companyCodeId,plantId,storageSectionId, String.valueOf(floorId));
		if(iKeyValuePair!=null) {
			newSpanId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newSpanId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newSpanId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newSpanId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
		}
		if(iKeyValuePair4!=null) {
			newSpanId.setStorageSectionIdAndDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
		}
		if(iKeyValuePair5!=null) {
			newSpanId.setAisleIdAndDescription(iKeyValuePair5.getAisleId() + "-" + iKeyValuePair5.getDescription());
		}
		if(iKeyValuePair6!=null) {
			newSpanId.setRowIdAndDescription(iKeyValuePair6.getRowId() + "-" + iKeyValuePair6.getDescription());
		}
		return newSpanId;
	}

	/**
	 * createSpanId
	 * @param newSpanId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SpanId createSpanId (AddSpanId newSpanId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SpanId dbSpanId = new SpanId();
		Optional<SpanId> duplicateSpanId = spanIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(newSpanId.getCompanyCodeId(), newSpanId.getPlantId(), newSpanId.getWarehouseId(), newSpanId.getAisleId(), newSpanId.getSpanId(), newSpanId.getFloorId(), newSpanId.getStorageSectionId(), newSpanId.getLanguageId(), 0L);
		if (!duplicateSpanId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			RowId dbRowId =rowIdService.getRowId(newSpanId.getWarehouseId(),newSpanId.getFloorId(), newSpanId.getStorageSectionId(), newSpanId.getRowId(), newSpanId.getCompanyCodeId(), newSpanId.getLanguageId(), newSpanId.getPlantId());
			log.info("newSpanId : " + newSpanId);
			BeanUtils.copyProperties(newSpanId, dbSpanId, CommonUtils.getNullPropertyNames(newSpanId));
			dbSpanId.setDeletionIndicator(0L);
			dbSpanId.setCompanyIdAndDescription(dbRowId.getCompanyIdAndDescription());
			dbSpanId.setPlantIdAndDescription(dbRowId.getPlantIdAndDescription());
			dbSpanId.setWarehouseIdAndDescription(dbRowId.getWarehouseIdDescription());
			dbSpanId.setFloorIdAndDescription(dbRowId.getFloorIdAndDescription());
			dbSpanId.setStorageSectionIdAndDescription(dbRowId.getStorageSectionIdAndDescription());
			dbSpanId.setAisleIdAndDescription(dbRowId.getAisleIdAndDescription());
			dbSpanId.setRowIdAndDescription(dbRowId.getRowId()+"-"+ dbRowId.getRowNumber());
			dbSpanId.setCreatedBy(loginUserID);
			dbSpanId.setUpdatedBy(loginUserID);
			dbSpanId.setCreatedOn(new Date());
			dbSpanId.setUpdatedOn(new Date());
			return spanIdRepository.save(dbSpanId);
		}
	}

	/**
	 * updateSpanId
	 * @param loginUserID
	 * @param spanId
	 * @param updateSpanId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public SpanId updateSpanId (String warehouseId,String aisleId,String spanId,Long floorId,String storageSectionId,
								String companyCodeId,String languageId,String plantId, String loginUserID,
								UpdateSpanId updateSpanId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SpanId dbSpanId = getSpanId(warehouseId, aisleId, spanId, floorId, storageSectionId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateSpanId, dbSpanId, CommonUtils.getNullPropertyNames(updateSpanId));
		dbSpanId.setUpdatedBy(loginUserID);
		dbSpanId.setUpdatedOn(new Date());
		return spanIdRepository.save(dbSpanId);
	}

	/**
	 * deleteSpanId
	 * @param loginUserID
	 * @param spanId
	 */
	public void deleteSpanId (String warehouseId,String aisleId,String spanId,Long floorId,String storageSectionId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		SpanId dbSpanId = getSpanId(warehouseId, aisleId, spanId, floorId, storageSectionId,companyCodeId,languageId,plantId);
		if ( dbSpanId != null) {
			dbSpanId.setDeletionIndicator(1L);
			dbSpanId.setUpdatedBy(loginUserID);
			spanIdRepository.save(dbSpanId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + spanId);
		}
	}

	//Find SpanId
	public List<SpanId> findSpanId(FindSpanId findSpanId) throws ParseException {

		SpanIdSpecification spec = new SpanIdSpecification(findSpanId);
		List<SpanId> results = spanIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<SpanId> newSpanId=new ArrayList<>();
		for(SpanId dbSpanId:results) {
			if (dbSpanId.getCompanyIdAndDescription() != null&&dbSpanId.getPlantIdAndDescription()!=null&&dbSpanId.getWarehouseIdAndDescription()!=null&&dbSpanId.getFloorIdAndDescription()!=null&&dbSpanId.getStorageSectionIdAndDescription()!=null&&dbSpanId.getRowIdAndDescription()!=null&&dbSpanId.getAisleIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbSpanId.getCompanyCodeId(), dbSpanId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbSpanId.getPlantId(), dbSpanId.getLanguageId(), dbSpanId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbSpanId.getWarehouseId(), dbSpanId.getLanguageId(), dbSpanId.getCompanyCodeId(), dbSpanId.getPlantId());
				IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(dbSpanId.getFloorId()), dbSpanId.getLanguageId(),dbSpanId.getWarehouseId(), dbSpanId.getPlantId(), dbSpanId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair4=storageSectionIdRepository.getStorageSectionIdAndDescription(dbSpanId.getStorageSectionId(), dbSpanId.getLanguageId(), dbSpanId.getWarehouseId(), dbSpanId.getCompanyCodeId(), dbSpanId.getPlantId(), String.valueOf(dbSpanId.getFloorId()));
				IKeyValuePair iKeyValuePair5=rowIdRepository.getRowIdAndDescription(dbSpanId.getRowId(), dbSpanId.getLanguageId(), dbSpanId.getWarehouseId(), dbSpanId.getCompanyCodeId(), dbSpanId.getPlantId(), dbSpanId.getStorageSectionId(), String.valueOf(dbSpanId.getFloorId()));
				IKeyValuePair iKeyValuePair6=aisleIdRepository.getAisleIdAndDescription(dbSpanId.getAisleId(), dbSpanId.getLanguageId(), dbSpanId.getWarehouseId(), dbSpanId.getCompanyCodeId(), dbSpanId.getStorageSectionId(), String.valueOf(dbSpanId.getFloorId()), dbSpanId.getPlantId());
				if (iKeyValuePair != null) {
					dbSpanId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbSpanId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbSpanId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbSpanId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbSpanId.setStorageSectionIdAndDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
				}
				if (iKeyValuePair5 != null) {
					dbSpanId.setRowIdAndDescription(iKeyValuePair5.getRowId() + "-" + iKeyValuePair5.getDescription());
				}
				if (iKeyValuePair6 != null)
					dbSpanId.setAisleIdAndDescription(iKeyValuePair6.getAisleId() + "-" + iKeyValuePair6.getDescription());
			}
			newSpanId.add(dbSpanId);
		}
		return newSpanId;
	}
}
