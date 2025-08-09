package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.handlingequipmentid.HandlingEquipmentId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.AddHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.FindHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.HandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.UpdateHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.HandlingUnitIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.HandlingUnitIdSpecification;
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
public class HandlingUnitIdService {

	@Autowired
	private HandlingUnitIdRepository handlingUnitIdRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getHandlingUnitIds
	 * @return
	 */
	public List<HandlingUnitId> getHandlingUnitIds () {
		List<HandlingUnitId> handlingUnitIdList =  handlingUnitIdRepository.findAll();
		handlingUnitIdList = handlingUnitIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<HandlingUnitId> newHandlingUnitId=new ArrayList<>();
		for(HandlingUnitId dbHandlingUnitId:handlingUnitIdList) {

				IKeyValuePair iKeyValuePair =
						companyIdRepository.getCompanyIdAndDescription(dbHandlingUnitId.getCompanyCodeId(), dbHandlingUnitId.getLanguageId());

				IKeyValuePair iKeyValuePair1 =
						plantIdRepository.getPlantIdAndDescription(dbHandlingUnitId.getPlantId(),
								dbHandlingUnitId.getLanguageId(), dbHandlingUnitId.getCompanyCodeId());

				IKeyValuePair iKeyValuePair2 =
						warehouseRepository.getWarehouseIdAndDescription(dbHandlingUnitId.getWarehouseId(),
								dbHandlingUnitId.getLanguageId(), dbHandlingUnitId.getCompanyCodeId(), dbHandlingUnitId.getPlantId());

				if (iKeyValuePair != null) {
					dbHandlingUnitId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbHandlingUnitId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbHandlingUnitId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			newHandlingUnitId.add(dbHandlingUnitId);
		}
		return newHandlingUnitId;
	}

	/**
	 *
	 * @param warehouseId
	 * @param handlingUnitNumber
	 * @param companyCodeId
	 * @param languageId
	 * @param plantId
	 * @return
	 */
	public HandlingUnitId getHandlingUnitId (String warehouseId,String handlingUnitNumber,String companyCodeId,String languageId,String plantId) {

		Optional<HandlingUnitId> dbHandlingUnitId =
				handlingUnitIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitNumberAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						handlingUnitNumber,
						languageId,
						0L
				);
		if (dbHandlingUnitId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"companyCodeId - " + companyCodeId +
					"plantId - " + plantId +
					"handlingUnitNumber - "+ handlingUnitNumber +
					" doesn't exist.");

		}
		HandlingUnitId newHandlingUnitId = new HandlingUnitId();
		BeanUtils.copyProperties(dbHandlingUnitId.get(),newHandlingUnitId, CommonUtils.getNullPropertyNames(dbHandlingUnitId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newHandlingUnitId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newHandlingUnitId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newHandlingUnitId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newHandlingUnitId;
	}

	/**
	 * createHandlingUnitId
	 * @param newHandlingUnitId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingUnitId createHandlingUnitId (AddHandlingUnitId newHandlingUnitId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		HandlingUnitId dbHandlingUnitId = new HandlingUnitId();

		Optional<HandlingUnitId> duplicateHandlingUnitId =
				handlingUnitIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndHandlingUnitNumberAndLanguageIdAndDeletionIndicator(newHandlingUnitId.getCompanyCodeId(), newHandlingUnitId.getPlantId(),
				newHandlingUnitId.getWarehouseId(), newHandlingUnitId.getHandlingUnitNumber(), newHandlingUnitId.getLanguageId(), 0L);

		if (!duplicateHandlingUnitId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {

			Warehouse dbWarehouse=warehouseService.getWarehouse(newHandlingUnitId.getWarehouseId(),
					newHandlingUnitId.getCompanyCodeId(), newHandlingUnitId.getPlantId(), newHandlingUnitId.getLanguageId());

			log.info("newHandlingUnitId : " + newHandlingUnitId);
			BeanUtils.copyProperties(newHandlingUnitId, dbHandlingUnitId, CommonUtils.getNullPropertyNames(newHandlingUnitId));
			dbHandlingUnitId.setDeletionIndicator(0L);
			dbHandlingUnitId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbHandlingUnitId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbHandlingUnitId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbHandlingUnitId.setCreatedBy(loginUserID);
			dbHandlingUnitId.setUpdatedBy(loginUserID);
			dbHandlingUnitId.setCreatedOn(new Date());
			dbHandlingUnitId.setUpdatedOn(new Date());
			return handlingUnitIdRepository.save(dbHandlingUnitId);
		}
	}


	/**
	 *
	 * @param warehouseId
	 * @param handlingUnitNumber
	 * @param companyCodeId
	 * @param languageId
	 * @param plantId
	 * @param loginUserID
	 * @param updateHandlingUnitId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingUnitId updateHandlingUnitId (String warehouseId,String handlingUnitNumber,String companyCodeId,
												String languageId,String plantId,String loginUserID,
												UpdateHandlingUnitId updateHandlingUnitId)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		HandlingUnitId dbHandlingUnitId = getHandlingUnitId( warehouseId,handlingUnitNumber,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateHandlingUnitId, dbHandlingUnitId, CommonUtils.getNullPropertyNames(updateHandlingUnitId));
		dbHandlingUnitId.setUpdatedBy(loginUserID);
		dbHandlingUnitId.setUpdatedOn(new Date());
		return handlingUnitIdRepository.save(dbHandlingUnitId);
	}

	/**
	 *
	 * @param warehouseId
	 * @param handlingUnitNumber
	 * @param companyCodeId
	 * @param languageId
	 * @param plantId
	 * @param loginUserID
	 */
	public void deleteHandlingUnitId (String warehouseId,String handlingUnitNumber,String companyCodeId,
									  String languageId,String plantId,String loginUserID) {

		HandlingUnitId dbHandlingUnitId = getHandlingUnitId( warehouseId, handlingUnitNumber,companyCodeId,languageId,plantId);
		if ( dbHandlingUnitId != null) {
			dbHandlingUnitId.setDeletionIndicator(1L);
			dbHandlingUnitId.setUpdatedBy(loginUserID);
			handlingUnitIdRepository.save(dbHandlingUnitId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + handlingUnitNumber);
		}
	}

	//Find HandlingUnitId
	public List<HandlingUnitId> findHandlingUnitId(FindHandlingUnitId findHandlingUnitId) throws ParseException {

		HandlingUnitIdSpecification spec = new HandlingUnitIdSpecification(findHandlingUnitId);
		List<HandlingUnitId> results = handlingUnitIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<HandlingUnitId> newHandlingUnitId=new ArrayList<>();
		for(HandlingUnitId dbHandlingUnitId:results) {
				IKeyValuePair iKeyValuePair =
						companyIdRepository.getCompanyIdAndDescription(dbHandlingUnitId.getCompanyCodeId(),dbHandlingUnitId.getLanguageId());

				IKeyValuePair iKeyValuePair1 =
						plantIdRepository.getPlantIdAndDescription(dbHandlingUnitId.getPlantId(), dbHandlingUnitId.getLanguageId(),
								dbHandlingUnitId.getCompanyCodeId());

				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbHandlingUnitId.getWarehouseId(),
						dbHandlingUnitId.getLanguageId(), dbHandlingUnitId.getCompanyCodeId(), dbHandlingUnitId.getPlantId());

				if (iKeyValuePair != null) {
					dbHandlingUnitId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbHandlingUnitId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbHandlingUnitId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			newHandlingUnitId.add(dbHandlingUnitId);
		}
		return newHandlingUnitId;
	}
}
