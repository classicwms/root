package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.inboundorderstatusid.*;
import com.tekclover.wms.api.idmaster.model.levelid.LevelId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.*;
import com.tekclover.wms.api.idmaster.repository.Specification.InboundOrderStatusIdSpecification;
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
public class InboundOrderStatusIdService{

	@Autowired
	private InboundOrderStatusIdRepository inboundOrderStatusIdRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private WarehouseService warehouseService;

	/**
	 * getInboundOrderStatusIds
	 * @return
	 */
	public List<InboundOrderStatusId> getInboundOrderStatusIds () {
		List<InboundOrderStatusId> inboundOrderStatusIdList =  inboundOrderStatusIdRepository.findAll();
		inboundOrderStatusIdList = inboundOrderStatusIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<InboundOrderStatusId> newInboundOrderStatusId=new ArrayList<>();
		for(InboundOrderStatusId dbInboundOrderStatusId:inboundOrderStatusIdList) {
			if (dbInboundOrderStatusId.getCompanyIdAndDescription() != null&&dbInboundOrderStatusId.getPlantIdAndDescription()!=null&&dbInboundOrderStatusId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbInboundOrderStatusId.getCompanyCodeId(), dbInboundOrderStatusId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbInboundOrderStatusId.getPlantId(), dbInboundOrderStatusId.getLanguageId(), dbInboundOrderStatusId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbInboundOrderStatusId.getWarehouseId(), dbInboundOrderStatusId.getLanguageId(), dbInboundOrderStatusId.getCompanyCodeId(), dbInboundOrderStatusId.getPlantId());
				if (iKeyValuePair != null) {
					dbInboundOrderStatusId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbInboundOrderStatusId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbInboundOrderStatusId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newInboundOrderStatusId.add(dbInboundOrderStatusId);
		}
		return newInboundOrderStatusId;
	}

	/**
	 * getInboundOrderStatusId
	 * @param inboundOrderStatusId
	 * @return
	 */
	public InboundOrderStatusId getInboundOrderStatusId (String warehouseId, String inboundOrderStatusId,String companyCodeId,String languageId,String plantId) {

		Optional<InboundOrderStatusId> dbInboundOrderStatusId =
				inboundOrderStatusIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInboundOrderStatusIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						inboundOrderStatusId,
						languageId,
						0L
				);
		if (dbInboundOrderStatusId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"inboundOrderStatusId - " + inboundOrderStatusId +
					" doesn't exist.");

		}
		InboundOrderStatusId newInboundOrderStatusId = new InboundOrderStatusId();
		BeanUtils.copyProperties(dbInboundOrderStatusId.get(),newInboundOrderStatusId, CommonUtils.getNullPropertyNames(dbInboundOrderStatusId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newInboundOrderStatusId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newInboundOrderStatusId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newInboundOrderStatusId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newInboundOrderStatusId;
	}

	/**
	 * createInboundOrderStatusId
	 * @param newInboundOrderStatusId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InboundOrderStatusId createInboundOrderStatusId (AddInboundOrderStatusId newInboundOrderStatusId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		InboundOrderStatusId dbInboundOrderStatusId = new InboundOrderStatusId();
		Optional<InboundOrderStatusId> duplicateInboundOrderStatusId = inboundOrderStatusIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndInboundOrderStatusIdAndLanguageIdAndDeletionIndicator(newInboundOrderStatusId.getCompanyCodeId(), newInboundOrderStatusId.getPlantId(), newInboundOrderStatusId.getWarehouseId(), newInboundOrderStatusId.getInboundOrderStatusId(), newInboundOrderStatusId.getLanguageId(), 0L);
		if (!duplicateInboundOrderStatusId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newInboundOrderStatusId.getWarehouseId(), newInboundOrderStatusId.getCompanyCodeId(), newInboundOrderStatusId.getPlantId(), newInboundOrderStatusId.getLanguageId());
			log.info("newInboundOrderStatusId : " + newInboundOrderStatusId);
			BeanUtils.copyProperties(newInboundOrderStatusId, dbInboundOrderStatusId, CommonUtils.getNullPropertyNames(newInboundOrderStatusId));
			dbInboundOrderStatusId.setDeletionIndicator(0L);
			dbInboundOrderStatusId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbInboundOrderStatusId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbInboundOrderStatusId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbInboundOrderStatusId.setCreatedBy(loginUserID);
			dbInboundOrderStatusId.setUpdatedBy(loginUserID);
			dbInboundOrderStatusId.setCreatedOn(new Date());
			dbInboundOrderStatusId.setUpdatedOn(new Date());
			return inboundOrderStatusIdRepository.save(dbInboundOrderStatusId);
		}
	}

	/**
	 * updateInboundOrderStatusId
	 * @param loginUserID
	 * @param inboundOrderStatusId
	 * @param updateInboundOrderStatusId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public InboundOrderStatusId updateInboundOrderStatusId (String warehouseId, String inboundOrderStatusId,String companyCodeId,String languageId,String plantId,String loginUserID,
															UpdateInboundOrderStatusId updateInboundOrderStatusId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		InboundOrderStatusId dbInboundOrderStatusId = getInboundOrderStatusId( warehouseId,inboundOrderStatusId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateInboundOrderStatusId, dbInboundOrderStatusId, CommonUtils.getNullPropertyNames(updateInboundOrderStatusId));
		dbInboundOrderStatusId.setUpdatedBy(loginUserID);
		dbInboundOrderStatusId.setUpdatedOn(new Date());
		return inboundOrderStatusIdRepository.save(dbInboundOrderStatusId);
	}

	/**
	 * deleteInboundOrderStatusId
	 * @param loginUserID
	 * @param inboundOrderStatusId
	 */
	public void deleteInboundOrderStatusId (String warehouseId, String inboundOrderStatusId,String companyCodeId,String languageId,String plantId, String loginUserID) {
		InboundOrderStatusId dbInboundOrderStatusId = getInboundOrderStatusId( warehouseId, inboundOrderStatusId,companyCodeId,languageId,plantId);
		if ( dbInboundOrderStatusId != null) {
			dbInboundOrderStatusId.setDeletionIndicator(1L);
			dbInboundOrderStatusId.setUpdatedBy(loginUserID);
			inboundOrderStatusIdRepository.save(dbInboundOrderStatusId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + inboundOrderStatusId);
		}
	}

	//Find InboundOrderStatusId
	public List<InboundOrderStatusId> findInboundOrderStatusId(FindInboundOrderStatusId findInboundOrderStatusId) throws ParseException {

		InboundOrderStatusIdSpecification spec = new InboundOrderStatusIdSpecification(findInboundOrderStatusId);
		List<InboundOrderStatusId> results = inboundOrderStatusIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<InboundOrderStatusId> newInboundOrderStatusId=new ArrayList<>();
		for(InboundOrderStatusId dbInboundOrderStatusId:results) {
			if (dbInboundOrderStatusId.getCompanyIdAndDescription() != null&&dbInboundOrderStatusId.getPlantIdAndDescription()!=null&&dbInboundOrderStatusId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbInboundOrderStatusId.getCompanyCodeId(), dbInboundOrderStatusId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbInboundOrderStatusId.getPlantId(), dbInboundOrderStatusId.getLanguageId(), dbInboundOrderStatusId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbInboundOrderStatusId.getWarehouseId(), dbInboundOrderStatusId.getLanguageId(), dbInboundOrderStatusId.getCompanyCodeId(), dbInboundOrderStatusId.getPlantId());
				if (iKeyValuePair != null) {
					dbInboundOrderStatusId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbInboundOrderStatusId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbInboundOrderStatusId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newInboundOrderStatusId.add(dbInboundOrderStatusId);
		}
		return newInboundOrderStatusId;
	}
}
