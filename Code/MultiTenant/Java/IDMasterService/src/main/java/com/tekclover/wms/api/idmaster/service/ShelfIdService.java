package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.shelfid.*;
import com.tekclover.wms.api.idmaster.model.spanid.SpanId;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.ShelfIdSpecification;
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
public class ShelfIdService {

	@Autowired
	private ShelfIdRepository shelfIdRepository;

	@Autowired
	private StorageSectionIdRepository storageSectionIdRepository;
	@Autowired
	private RowIdRepository rowIdRepository;
	@Autowired
	private SpanIdRepository spanIdRepository;
	@Autowired
	private FloorIdRepository floorIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private AisleIdRepository aisleIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private SpanIdService spanIdService;

	/**
	 * getShelfIds
	 * @return
	 */
	public List<ShelfId> getShelfIds () {
		List<ShelfId> shelfIdList =  shelfIdRepository.findAll();
		shelfIdList = shelfIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<ShelfId> newShelfId=new ArrayList<>();
		for(ShelfId dbShelfId:shelfIdList) {
			if (dbShelfId.getCompanyIdAndDescription() != null&&dbShelfId.getPlantIdAndDescription()!=null&&dbShelfId.getWarehouseIdAndDescription()!=null&&dbShelfId.getFloorIdAndDescription()!=null&&dbShelfId.getStorageSectionIdAndDescription()!=null&&dbShelfId.getRowIdAndDescription()!=null&&dbShelfId.getAisledIdAndDescription()!=null&&dbShelfId.getSpanIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbShelfId.getCompanyCodeId(), dbShelfId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbShelfId.getPlantId(), dbShelfId.getLanguageId(), dbShelfId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbShelfId.getWarehouseId(), dbShelfId.getLanguageId(), dbShelfId.getCompanyCodeId(), dbShelfId.getPlantId());
				IKeyValuePair iKeyValuePair3 = floorIdRepository.getFloorIdAndDescription(String.valueOf(dbShelfId.getFloorId()), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getPlantId(), dbShelfId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair4 = storageSectionIdRepository.getStorageSectionIdAndDescription(dbShelfId.getStorageSectionId(), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getCompanyCodeId(), dbShelfId.getPlantId(), String.valueOf(dbShelfId.getFloorId()));
				IKeyValuePair iKeyValuePair5 = rowIdRepository.getRowIdAndDescription(dbShelfId.getRowId(), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getCompanyCodeId(), dbShelfId.getPlantId(), dbShelfId.getStorageSectionId(), String.valueOf(dbShelfId.getFloorId()));
				IKeyValuePair iKeyValuePair6 = aisleIdRepository.getAisleIdAndDescription(dbShelfId.getAisleId(), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getCompanyCodeId(), dbShelfId.getStorageSectionId(), String.valueOf(dbShelfId.getFloorId()), dbShelfId.getPlantId());
				IKeyValuePair iKeyValuePair7 = spanIdRepository.getSpanIdAndDescription(dbShelfId.getSpanId(), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getStorageSectionId(), dbShelfId.getAisleId(), dbShelfId.getCompanyCodeId(), dbShelfId.getPlantId(), String.valueOf(dbShelfId.getFloorId()), dbShelfId.getRowId());
				if (iKeyValuePair != null) {
					dbShelfId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbShelfId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbShelfId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbShelfId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbShelfId.setStorageSectionIdAndDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
				}
				if (iKeyValuePair5 != null) {
					dbShelfId.setRowIdAndDescription(iKeyValuePair5.getRowId() + "-" + iKeyValuePair5.getDescription());
				}
				if (iKeyValuePair6 != null) {
					dbShelfId.setAisledIdAndDescription(iKeyValuePair6.getAisleId() + "-" + iKeyValuePair6.getDescription());
				}
				if (iKeyValuePair7 != null) {
					dbShelfId.setSpanIdAndDescription(iKeyValuePair7.getSpanId() + "-" + iKeyValuePair7.getDescription());
				}
			}
			newShelfId.add(dbShelfId);
		}
		return newShelfId;
	}

	/**
	 * getShelfId
	 * @param shelfId
	 * @return
	 */
	public ShelfId getShelfId (String warehouseId,String aisleId,String shelfId,String spanId,Long floorId,String storageSectionId,String companyCodeId,String languageId,String plantId) {
		Optional<ShelfId> dbShelfId =
				shelfIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndShelfIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						aisleId,
						shelfId,
						spanId,
						floorId,
						storageSectionId,
						languageId,
						0L
				);
		if (dbShelfId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"aisleId - " + aisleId +
					"shelfId - " + shelfId +
					"spanId - " + spanId +
					"floorId - " + floorId +
					"storageSectionId - " + storageSectionId +
					" doesn't exist.");

		}
		ShelfId newShelfId = new ShelfId();
		BeanUtils.copyProperties(dbShelfId.get(),newShelfId, CommonUtils.getNullPropertyNames(dbShelfId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(floorId),languageId,warehouseId,plantId,companyCodeId);
		IKeyValuePair iKeyValuePair4=storageSectionIdRepository.getStorageSectionIdAndDescription(storageSectionId,languageId,warehouseId,companyCodeId,plantId, String.valueOf(floorId));
		IKeyValuePair iKeyValuePair5=aisleIdRepository.getAisleIdAndDescription(aisleId,languageId,warehouseId,companyCodeId,storageSectionId, String.valueOf(floorId),plantId);
		IKeyValuePair iKeyValuePair6= rowIdRepository.getRowIdAndDescription(newShelfId.getRowId(),languageId,warehouseId,companyCodeId,plantId,storageSectionId, String.valueOf(floorId));
		IKeyValuePair iKeyValuePair7= spanIdRepository.getSpanIdAndDescription(spanId,languageId,warehouseId,storageSectionId,aisleId,companyCodeId,plantId, String.valueOf(floorId), newShelfId.getRowId());
		if(iKeyValuePair!=null) {
			newShelfId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newShelfId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newShelfId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		if(iKeyValuePair3!=null) {
			newShelfId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
		}
		if(iKeyValuePair4!=null) {
			newShelfId.setStorageSectionIdAndDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
		}
		if(iKeyValuePair5!=null) {
			newShelfId.setAisledIdAndDescription(iKeyValuePair5.getAisleId() + "-" + iKeyValuePair5.getDescription());
		}
		if(iKeyValuePair6!=null) {
			newShelfId.setRowIdAndDescription(iKeyValuePair6.getRowId() + "-" + iKeyValuePair6.getDescription());
		}
		if(iKeyValuePair7!=null) {
			newShelfId.setSpanIdAndDescription(iKeyValuePair7.getSpanId() + "-" + iKeyValuePair7.getDescription());
		}
		return newShelfId;
	}

	/**
	 * createShelfId
	 * @param newShelfId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ShelfId createShelfId (AddShelfId newShelfId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ShelfId dbShelfId = new ShelfId();
		Optional<ShelfId> duplicateShelfId = shelfIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndShelfIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(newShelfId.getCompanyCodeId(), newShelfId.getPlantId(), newShelfId.getWarehouseId(), newShelfId.getAisleId(), newShelfId.getShelfId(), newShelfId.getSpanId(),
				newShelfId.getFloorId(), newShelfId.getStorageSectionId(), newShelfId.getLanguageId(), 0L);

		if (!duplicateShelfId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			SpanId dbSpanId=spanIdService.getSpanId(newShelfId.getWarehouseId(), newShelfId.getAisleId(), newShelfId.getSpanId(), newShelfId.getFloorId(), newShelfId.getStorageSectionId(), newShelfId.getCompanyCodeId(), newShelfId.getLanguageId(), newShelfId.getPlantId());
			log.info("newShelfId : " + newShelfId);
			BeanUtils.copyProperties(newShelfId, dbShelfId, CommonUtils.getNullPropertyNames(newShelfId));
			dbShelfId.setCompanyIdAndDescription(dbSpanId.getCompanyIdAndDescription());
			dbShelfId.setPlantIdAndDescription(dbSpanId.getPlantIdAndDescription());
			dbShelfId.setWarehouseIdAndDescription(dbSpanId.getWarehouseIdAndDescription());
			dbShelfId.setAisledIdAndDescription(dbSpanId.getAisleIdAndDescription());
			dbShelfId.setFloorIdAndDescription(dbSpanId.getFloorIdAndDescription());
			dbShelfId.setStorageSectionIdAndDescription(dbSpanId.getStorageSectionIdAndDescription());
			dbShelfId.setRowIdAndDescription(dbSpanId.getRowIdAndDescription());
			dbShelfId.setSpanIdAndDescription(dbSpanId.getSpanId()+"-"+dbSpanId.getSpanDescription());
			dbShelfId.setDeletionIndicator(0L);
			dbShelfId.setCreatedBy(loginUserID);
			dbShelfId.setUpdatedBy(loginUserID);
			dbShelfId.setCreatedOn(new Date());
			dbShelfId.setUpdatedOn(new Date());
			return shelfIdRepository.save(dbShelfId);
		}
	}

	/**
	 * updateShelfId
	 * @param loginUserID
	 * @param shelfId
	 * @param updateShelfId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public ShelfId updateShelfId (String warehouseId,String aisleId,String shelfId,String spanId,Long floorId,String storageSectionId,String companyCodeId,String languageId,String plantId, String loginUserID,
								  UpdateShelfId updateShelfId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ShelfId dbShelfId = getShelfId(warehouseId, aisleId, shelfId,spanId, floorId, storageSectionId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateShelfId, dbShelfId, CommonUtils.getNullPropertyNames(updateShelfId));
		dbShelfId.setUpdatedBy(loginUserID);
		dbShelfId.setUpdatedOn(new Date());
		return shelfIdRepository.save(dbShelfId);
	}

	/**
	 * deleteShelfId
	 * @param loginUserID
	 * @param shelfId
	 */
	public void deleteShelfId (String warehouseId,String aisleId,String shelfId,String spanId,Long floorId,String storageSectionId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		ShelfId dbShelfId = getShelfId(warehouseId, aisleId, shelfId,spanId, floorId, storageSectionId,companyCodeId,languageId,plantId);
		if ( dbShelfId != null) {
			dbShelfId.setDeletionIndicator(1L);
			dbShelfId.setUpdatedBy(loginUserID);
			shelfIdRepository.save(dbShelfId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + shelfId);
		}
	}

	//Find ShelfId
	public List<ShelfId> findShelfId(FindShelfId findShelfId) throws ParseException {

		ShelfIdSpecification spec = new ShelfIdSpecification(findShelfId);
		List<ShelfId> results = shelfIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<ShelfId> newShelfId=new ArrayList<>();
		for(ShelfId dbShelfId:results) {
			if (dbShelfId.getCompanyIdAndDescription() != null&&dbShelfId.getPlantIdAndDescription()!=null&&dbShelfId.getWarehouseIdAndDescription()!=null&&dbShelfId.getFloorIdAndDescription()!=null&&dbShelfId.getStorageSectionIdAndDescription()!=null&&dbShelfId.getRowIdAndDescription()!=null&&dbShelfId.getAisledIdAndDescription()!=null&&dbShelfId.getSpanIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbShelfId.getCompanyCodeId(), dbShelfId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbShelfId.getPlantId(), dbShelfId.getLanguageId(), dbShelfId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbShelfId.getWarehouseId(), dbShelfId.getLanguageId(), dbShelfId.getCompanyCodeId(), dbShelfId.getPlantId());
				IKeyValuePair iKeyValuePair3= floorIdRepository.getFloorIdAndDescription(String.valueOf(dbShelfId.getFloorId()), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getPlantId(), dbShelfId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair4=storageSectionIdRepository.getStorageSectionIdAndDescription(dbShelfId.getStorageSectionId(), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getCompanyCodeId(), dbShelfId.getPlantId(), String.valueOf(dbShelfId.getFloorId()));
				IKeyValuePair iKeyValuePair5=rowIdRepository.getRowIdAndDescription(dbShelfId.getRowId(), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getCompanyCodeId(), dbShelfId.getPlantId(), dbShelfId.getStorageSectionId(), String.valueOf(dbShelfId.getFloorId()));
				IKeyValuePair iKeyValuePair6=aisleIdRepository.getAisleIdAndDescription(dbShelfId.getAisleId(), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getCompanyCodeId(), dbShelfId.getStorageSectionId(), String.valueOf(dbShelfId.getFloorId()), dbShelfId.getPlantId());
				IKeyValuePair iKeyValuePair7= spanIdRepository.getSpanIdAndDescription(dbShelfId.getSpanId(), dbShelfId.getLanguageId(), dbShelfId.getWarehouseId(), dbShelfId.getStorageSectionId(), dbShelfId.getAisleId(), dbShelfId.getCompanyCodeId(), dbShelfId.getPlantId(), String.valueOf(dbShelfId.getFloorId()), dbShelfId.getRowId());
				if (iKeyValuePair != null) {
					dbShelfId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbShelfId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbShelfId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
				if (iKeyValuePair3 != null) {
					dbShelfId.setFloorIdAndDescription(iKeyValuePair3.getFloorId() + "-" + iKeyValuePair3.getDescription());
				}
				if (iKeyValuePair4 != null) {
					dbShelfId.setStorageSectionIdAndDescription(iKeyValuePair4.getStorageSectionId() + "-" + iKeyValuePair4.getDescription());
				}
				if (iKeyValuePair5 != null) {
					dbShelfId.setRowIdAndDescription(iKeyValuePair5.getRowId() + "-" + iKeyValuePair5.getDescription());
				}
				if (iKeyValuePair6 != null) {
					dbShelfId.setAisledIdAndDescription(iKeyValuePair6.getAisleId() + "-" + iKeyValuePair6.getDescription());
				}
				if (iKeyValuePair7 != null) {
					dbShelfId.setSpanIdAndDescription(iKeyValuePair7.getSpanId() + "-" + iKeyValuePair7.getDescription());
				}
			}
			newShelfId.add(dbShelfId);
		}
		return newShelfId;
	}
}
