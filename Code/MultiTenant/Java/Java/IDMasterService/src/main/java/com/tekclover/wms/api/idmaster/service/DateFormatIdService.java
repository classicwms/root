package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.dateformatid.AddDateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.DateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.FindDateFormatId;
import com.tekclover.wms.api.idmaster.model.dateformatid.UpdateDateFormatId;
import com.tekclover.wms.api.idmaster.model.doorid.DoorId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.DateFormatIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.DateFormatIdSpecification;
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
public class DateFormatIdService{

	@Autowired
	private DateFormatIdRepository dateFormatIdRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getDateFormatIds
	 * @return
	 */
	public List<DateFormatId> getDateFormatIds () {
		List<DateFormatId> dateFormatIdList =  dateFormatIdRepository.findAll();
		dateFormatIdList = dateFormatIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<DateFormatId> newDateFormatId=new ArrayList<>();
		for(DateFormatId dbDateFormatId:dateFormatIdList) {
			if (dbDateFormatId.getCompanyIdAndDescription() != null&&dbDateFormatId.getPlantIdAndDescription()!=null&&dbDateFormatId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbDateFormatId.getCompanyCodeId(), dbDateFormatId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbDateFormatId.getPlantId(), dbDateFormatId.getLanguageId(), dbDateFormatId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbDateFormatId.getWarehouseId(), dbDateFormatId.getLanguageId(), dbDateFormatId.getCompanyCodeId(), dbDateFormatId.getPlantId());
				if (iKeyValuePair != null) {
					dbDateFormatId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbDateFormatId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbDateFormatId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newDateFormatId.add(dbDateFormatId);
		}
		return newDateFormatId;
	}

	/**
	 * getDateFormatId
	 * @param dateFormatId
	 * @return
	 */
	public DateFormatId getDateFormatId (String warehouseId,String dateFormatId,String companyCodeId,String languageId,String plantId) {
		Optional<DateFormatId> dbDateFormatId =
				dateFormatIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDateFormatIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						dateFormatId,
						languageId,
						0L
				);
		if (dbDateFormatId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"dateFormatId - " + dateFormatId +
					" doesn't exist.");

		}
		DateFormatId newDateFormatId = new DateFormatId();
		BeanUtils.copyProperties(dbDateFormatId.get(),newDateFormatId, CommonUtils.getNullPropertyNames(dbDateFormatId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newDateFormatId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newDateFormatId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newDateFormatId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newDateFormatId;
	}

	/**
	 * createDateFormatId
	 * @param newDateFormatId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DateFormatId createDateFormatId (AddDateFormatId newDateFormatId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		DateFormatId dbDateFormatId = new DateFormatId();
		Optional<DateFormatId> duplicateDateFormatId = dateFormatIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDateFormatIdAndLanguageIdAndDeletionIndicator(newDateFormatId.getCompanyCodeId(), newDateFormatId.getPlantId(), newDateFormatId.getWarehouseId(), newDateFormatId.getDateFormatId(), newDateFormatId.getLanguageId(), 0L);
		if (!duplicateDateFormatId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newDateFormatId.getWarehouseId(), newDateFormatId.getCompanyCodeId(), newDateFormatId.getPlantId(), newDateFormatId.getLanguageId());
			log.info("newDateFormatId : " + newDateFormatId);
			BeanUtils.copyProperties(newDateFormatId, dbDateFormatId, CommonUtils.getNullPropertyNames(newDateFormatId));
			dbDateFormatId.setDeletionIndicator(0L);
			dbDateFormatId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbDateFormatId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbDateFormatId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbDateFormatId.setCreatedBy(loginUserID);
			dbDateFormatId.setUpdatedBy(loginUserID);
			dbDateFormatId.setCreatedOn(new Date());
			dbDateFormatId.setUpdatedOn(new Date());
			return dateFormatIdRepository.save(dbDateFormatId);
		}
	}

	/**
	 * updateDateFormatId
	 * @param loginUserID
	 * @param dateFormatId
	 * @param updateDateFormatId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public DateFormatId updateDateFormatId (String warehouseId, String dateFormatId,String companyCodeId,String languageId,String plantId,String loginUserID,
											UpdateDateFormatId updateDateFormatId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		DateFormatId dbDateFormatId = getDateFormatId( warehouseId,dateFormatId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateDateFormatId, dbDateFormatId, CommonUtils.getNullPropertyNames(updateDateFormatId));
		dbDateFormatId.setUpdatedBy(loginUserID);
		dbDateFormatId.setUpdatedOn(new Date());
		return dateFormatIdRepository.save(dbDateFormatId);
	}

	/**
	 * deleteDateFormatId
	 * @param loginUserID
	 * @param dateFormatId
	 */
	public void deleteDateFormatId (String warehouseId, String dateFormatId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		DateFormatId dbDateFormatId = getDateFormatId( warehouseId, dateFormatId,companyCodeId,languageId,plantId);
		if ( dbDateFormatId != null) {
			dbDateFormatId.setDeletionIndicator(1L);
			dbDateFormatId.setUpdatedBy(loginUserID);
			dateFormatIdRepository.save(dbDateFormatId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + dateFormatId);
		}
	}
	//Find DateFormatId

	public List<DateFormatId> findDateFormatId(FindDateFormatId findDateFormatId) throws ParseException {

		DateFormatIdSpecification spec = new DateFormatIdSpecification(findDateFormatId);
		List<DateFormatId> results = dateFormatIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<DateFormatId> newDateFormatId=new ArrayList<>();
		for(DateFormatId dbDateFormatId:results) {
			if (dbDateFormatId.getCompanyIdAndDescription() != null&&dbDateFormatId.getPlantIdAndDescription()!=null&&dbDateFormatId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbDateFormatId.getCompanyCodeId(), dbDateFormatId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbDateFormatId.getPlantId(),dbDateFormatId.getLanguageId(), dbDateFormatId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbDateFormatId.getWarehouseId(),dbDateFormatId.getLanguageId(), dbDateFormatId.getCompanyCodeId(), dbDateFormatId.getPlantId());
				if (iKeyValuePair != null) {
					dbDateFormatId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbDateFormatId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbDateFormatId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newDateFormatId.add(dbDateFormatId);
		}
		return newDateFormatId;
	}
}
