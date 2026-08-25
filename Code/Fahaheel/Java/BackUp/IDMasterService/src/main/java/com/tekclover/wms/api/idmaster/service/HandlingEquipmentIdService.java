package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.AddHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.FindHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.HandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.UpdateHandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.HandlingEquipmentIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.HandlingEquipmentIdSpecification;
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
public class HandlingEquipmentIdService{

	@Autowired
	private HandlingEquipmentIdRepository handlingEquipmentIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getHandlingEquipmentIds
	 * @return
	 */
	public List<HandlingEquipmentId> getHandlingEquipmentIds () {
		List<HandlingEquipmentId> handlingEquipmentIdList =  handlingEquipmentIdRepository.findAll();
		handlingEquipmentIdList = handlingEquipmentIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<HandlingEquipmentId> newHandlingEquipmentId=new ArrayList<>();
		for(HandlingEquipmentId dbHandlingEquipmentId:handlingEquipmentIdList) {
			if (dbHandlingEquipmentId.getCompanyIdAndDescription() != null&&dbHandlingEquipmentId.getPlantIdAndDescription()!=null&&dbHandlingEquipmentId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbHandlingEquipmentId.getCompanyCodeId(), dbHandlingEquipmentId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbHandlingEquipmentId.getPlantId(), dbHandlingEquipmentId.getLanguageId(), dbHandlingEquipmentId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbHandlingEquipmentId.getWarehouseId(), dbHandlingEquipmentId.getLanguageId(), dbHandlingEquipmentId.getCompanyCodeId(), dbHandlingEquipmentId.getPlantId());
				if (iKeyValuePair != null) {
					dbHandlingEquipmentId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbHandlingEquipmentId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbHandlingEquipmentId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newHandlingEquipmentId.add(dbHandlingEquipmentId);
		}
		return newHandlingEquipmentId;
	}

	/**
	 * getHandlingEquipmentId
	 * @param handlingEquipmentId
	 * @return
	 */
	public HandlingEquipmentId getHandlingEquipmentId (String warehouseId,Long handlingEquipmentNumber,String companyCodeId,
													   String languageId,String plantId) {
		Optional<HandlingEquipmentId> dbHandlingEquipmentId =
				handlingEquipmentIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingEquipmentNumberAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						handlingEquipmentNumber,
						languageId,
						0L
				);
		if (dbHandlingEquipmentId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"handlingEquipmentNumber - "+ handlingEquipmentNumber +
					" doesn't exist.");

		}
		HandlingEquipmentId newHandlingEquipmentId = new HandlingEquipmentId();
		BeanUtils.copyProperties(dbHandlingEquipmentId.get(),newHandlingEquipmentId, CommonUtils.getNullPropertyNames(dbHandlingEquipmentId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newHandlingEquipmentId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newHandlingEquipmentId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newHandlingEquipmentId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newHandlingEquipmentId;
	}

	/**
	 * createHandlingEquipmentId
	 * @param newHandlingEquipmentId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingEquipmentId createHandlingEquipmentId (AddHandlingEquipmentId newHandlingEquipmentId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		HandlingEquipmentId dbHandlingEquipmentId = new HandlingEquipmentId();

		Optional<HandlingEquipmentId> duplicateHandlingEquipmentId =
				handlingEquipmentIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingEquipmentNumberAndLanguageIdAndDeletionIndicator(newHandlingEquipmentId.getCompanyCodeId(), newHandlingEquipmentId.getPlantId(),
				newHandlingEquipmentId.getWarehouseId(),newHandlingEquipmentId.getHandlingEquipmentNumber(), newHandlingEquipmentId.getLanguageId(), 0l);

		if (!duplicateHandlingEquipmentId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {

			Warehouse dbWarehouse=warehouseService.getWarehouse(newHandlingEquipmentId.getWarehouseId(), newHandlingEquipmentId.getCompanyCodeId(), newHandlingEquipmentId.getPlantId(), newHandlingEquipmentId.getLanguageId());
			log.info("newHandlingEquipmentId : " + newHandlingEquipmentId);
			BeanUtils.copyProperties(newHandlingEquipmentId, dbHandlingEquipmentId, CommonUtils.getNullPropertyNames(newHandlingEquipmentId));
			dbHandlingEquipmentId.setDeletionIndicator(0L);
			dbHandlingEquipmentId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbHandlingEquipmentId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbHandlingEquipmentId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbHandlingEquipmentId.setCreatedBy(loginUserID);
			dbHandlingEquipmentId.setUpdatedBy(loginUserID);
			dbHandlingEquipmentId.setCreatedOn(new Date());
			dbHandlingEquipmentId.setUpdatedOn(new Date());
			return handlingEquipmentIdRepository.save(dbHandlingEquipmentId);
		}
	}

	/**
	 * updateHandlingEquipmentId
	 * @param loginUserID
	 * @param handlingEquipmentId
	 * @param updateHandlingEquipmentId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingEquipmentId updateHandlingEquipmentId (String warehouseId,Long handlingEquipmentNumber,
														  String companyCodeId,String languageId,String plantId,String loginUserID,
														  UpdateHandlingEquipmentId updateHandlingEquipmentId)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		HandlingEquipmentId dbHandlingEquipmentId = getHandlingEquipmentId( warehouseId,handlingEquipmentNumber,
				companyCodeId,languageId,plantId);

		BeanUtils.copyProperties(updateHandlingEquipmentId, dbHandlingEquipmentId, CommonUtils.getNullPropertyNames(updateHandlingEquipmentId));
		dbHandlingEquipmentId.setUpdatedBy(loginUserID);
		dbHandlingEquipmentId.setUpdatedOn(new Date());
		return handlingEquipmentIdRepository.save(dbHandlingEquipmentId);
	}

	/**
	 *
	 * @param warehouseId
	 * @param handlingEquipmentNumber
	 * @param companyCodeId
	 * @param languageId
	 * @param plantId
	 * @param loginUserID
	 */
	public void deleteHandlingEquipmentId (String warehouseId,Long handlingEquipmentNumber,String companyCodeId,
										   String languageId,String plantId,String loginUserID) {
		HandlingEquipmentId dbHandlingEquipmentId = getHandlingEquipmentId( warehouseId, handlingEquipmentNumber,companyCodeId,languageId,plantId);
		if ( dbHandlingEquipmentId != null) {
			dbHandlingEquipmentId.setDeletionIndicator(1L);
			dbHandlingEquipmentId.setUpdatedBy(loginUserID);
			handlingEquipmentIdRepository.save(dbHandlingEquipmentId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + handlingEquipmentNumber);
		}
	}

	//Find HandlingEquipmentId
	public List<HandlingEquipmentId> findHandlingEquipmentId(FindHandlingEquipmentId findHandlingEquipmentId) throws ParseException {

		HandlingEquipmentIdSpecification spec = new HandlingEquipmentIdSpecification(findHandlingEquipmentId);
		List<HandlingEquipmentId> results = handlingEquipmentIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<HandlingEquipmentId> newHandlingEquipmentId=new ArrayList<>();
		for(HandlingEquipmentId dbHandlingEquipmentId:results) {
			if (dbHandlingEquipmentId.getCompanyIdAndDescription() != null&&dbHandlingEquipmentId.getPlantIdAndDescription()!=null&&dbHandlingEquipmentId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbHandlingEquipmentId.getCompanyCodeId(), dbHandlingEquipmentId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbHandlingEquipmentId.getPlantId(), dbHandlingEquipmentId.getLanguageId(), dbHandlingEquipmentId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbHandlingEquipmentId.getWarehouseId(), dbHandlingEquipmentId.getLanguageId(), dbHandlingEquipmentId.getCompanyCodeId(), dbHandlingEquipmentId.getPlantId());
				if (iKeyValuePair != null) {
					dbHandlingEquipmentId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbHandlingEquipmentId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbHandlingEquipmentId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}	}
			newHandlingEquipmentId.add(dbHandlingEquipmentId);
		}
		return newHandlingEquipmentId;
	}
}
