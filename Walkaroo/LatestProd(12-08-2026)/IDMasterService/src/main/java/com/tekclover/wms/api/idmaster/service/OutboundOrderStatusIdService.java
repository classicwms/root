package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.outboundorderstatusid.*;
import com.tekclover.wms.api.idmaster.model.outboundordertypeid.OutboundOrderTypeId;
import com.tekclover.wms.api.idmaster.model.warehouseid.Warehouse;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.OutboundOrderStatusIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.repository.Specification.OutboundOrderStatusIdSpecification;
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
public class OutboundOrderStatusIdService {

	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	private OutboundOrderStatusIdRepository outboundOrderStatusIdRepository;

	/**
	 * getOutboundOrderStatusIds
	 * @return
	 */
	public List<OutboundOrderStatusId> getOutboundOrderStatusIds () {
		List<OutboundOrderStatusId> outboundOrderStatusIdList =  outboundOrderStatusIdRepository.findAll();
		outboundOrderStatusIdList = outboundOrderStatusIdList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		List<OutboundOrderStatusId> newOutboundOrderStatusId=new ArrayList<>();
		for(OutboundOrderStatusId dbOutboundOrderStatusId:outboundOrderStatusIdList) {
			if (dbOutboundOrderStatusId.getCompanyIdAndDescription() != null&&dbOutboundOrderStatusId.getPlantIdAndDescription()!=null&&dbOutboundOrderStatusId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair = companyIdRepository.getCompanyIdAndDescription(dbOutboundOrderStatusId.getCompanyCodeId(), dbOutboundOrderStatusId.getLanguageId());
				IKeyValuePair iKeyValuePair1 = plantIdRepository.getPlantIdAndDescription(dbOutboundOrderStatusId.getPlantId(), dbOutboundOrderStatusId.getLanguageId(), dbOutboundOrderStatusId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2 = warehouseRepository.getWarehouseIdAndDescription(dbOutboundOrderStatusId.getWarehouseId(), dbOutboundOrderStatusId.getLanguageId(), dbOutboundOrderStatusId.getCompanyCodeId(), dbOutboundOrderStatusId.getPlantId());
				if (iKeyValuePair != null) {
					dbOutboundOrderStatusId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbOutboundOrderStatusId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbOutboundOrderStatusId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newOutboundOrderStatusId.add(dbOutboundOrderStatusId);
		}
		return newOutboundOrderStatusId;
	}

	/**
	 * getOutboundOrderStatusId
	 * @param outboundOrderStatusId
	 * @return
	 */
	public OutboundOrderStatusId getOutboundOrderStatusId (String warehouseId, String outboundOrderStatusId,String companyCodeId,String languageId,String plantId) {
		Optional<OutboundOrderStatusId> dbOutboundOrderStatusId =
				outboundOrderStatusIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndOutboundOrderStatusIdAndLanguageIdAndDeletionIndicator(
						companyCodeId,
						plantId,
						warehouseId,
						outboundOrderStatusId,
						languageId,
						0L
				);
		if (dbOutboundOrderStatusId.isEmpty()) {
			throw new BadRequestException("The given values : " +
					"warehouseId - " + warehouseId +
					"outboundOrderStatusId - " + outboundOrderStatusId +
					" doesn't exist.");

		}
		OutboundOrderStatusId newOutboundOrderStatusId = new OutboundOrderStatusId();
		BeanUtils.copyProperties(dbOutboundOrderStatusId.get(),newOutboundOrderStatusId, CommonUtils.getNullPropertyNames(dbOutboundOrderStatusId));
		IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(companyCodeId,languageId);
		IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(plantId,languageId,companyCodeId);
		IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(warehouseId,languageId,companyCodeId,plantId);
		if(iKeyValuePair!=null) {
			newOutboundOrderStatusId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
		}
		if(iKeyValuePair1!=null) {
			newOutboundOrderStatusId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
		}
		if(iKeyValuePair2!=null) {
			newOutboundOrderStatusId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
		}
		return newOutboundOrderStatusId;
	}

	/**
	 * createOutboundOrderStatusId
	 * @param newOutboundOrderStatusId
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundOrderStatusId createOutboundOrderStatusId (AddOutboundOrderStatusId newOutboundOrderStatusId, String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		OutboundOrderStatusId dbOutboundOrderStatusId = new OutboundOrderStatusId();
		Optional<OutboundOrderStatusId> duplicateOutboundOrderStatusId = outboundOrderStatusIdRepository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndOutboundOrderStatusIdAndLanguageIdAndDeletionIndicator(newOutboundOrderStatusId.getCompanyCodeId(), newOutboundOrderStatusId.getPlantId(), newOutboundOrderStatusId.getWarehouseId(), newOutboundOrderStatusId.getOutboundOrderStatusId(), newOutboundOrderStatusId.getLanguageId(), 0L);
		if (!duplicateOutboundOrderStatusId.isEmpty()) {
			throw new EntityNotFoundException("Record is Getting Duplicated");
		} else {
			Warehouse dbWarehouse=warehouseService.getWarehouse(newOutboundOrderStatusId.getWarehouseId(), newOutboundOrderStatusId.getCompanyCodeId(), newOutboundOrderStatusId.getPlantId(), newOutboundOrderStatusId.getLanguageId());
			log.info("newOutboundOrderStatusId : " + newOutboundOrderStatusId);
			BeanUtils.copyProperties(newOutboundOrderStatusId, dbOutboundOrderStatusId, CommonUtils.getNullPropertyNames(newOutboundOrderStatusId));
			dbOutboundOrderStatusId.setDeletionIndicator(0L);
			dbOutboundOrderStatusId.setCompanyIdAndDescription(dbWarehouse.getCompanyIdAndDescription());
			dbOutboundOrderStatusId.setPlantIdAndDescription(dbWarehouse.getPlantIdAndDescription());
			dbOutboundOrderStatusId.setWarehouseIdAndDescription(dbWarehouse.getWarehouseId()+"-"+dbWarehouse.getWarehouseDesc());
			dbOutboundOrderStatusId.setCreatedBy(loginUserID);
			dbOutboundOrderStatusId.setUpdatedBy(loginUserID);
			dbOutboundOrderStatusId.setCreatedOn(new Date());
			dbOutboundOrderStatusId.setUpdatedOn(new Date());
			return outboundOrderStatusIdRepository.save(dbOutboundOrderStatusId);
		}
	}

	/**
	 * updateOutboundOrderStatusId
	 * @param loginUserID
	 * @param outboundOrderStatusId
	 * @param updateOutboundOrderStatusId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundOrderStatusId updateOutboundOrderStatusId (String warehouseId, String outboundOrderStatusId, String companyCodeId, String languageId, String plantId,String loginUserID,
															  UpdateOutboundOrderStatusId updateOutboundOrderStatusId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		OutboundOrderStatusId dbOutboundOrderStatusId = getOutboundOrderStatusId( warehouseId,outboundOrderStatusId,companyCodeId,languageId,plantId);
		BeanUtils.copyProperties(updateOutboundOrderStatusId, dbOutboundOrderStatusId, CommonUtils.getNullPropertyNames(updateOutboundOrderStatusId));
		dbOutboundOrderStatusId.setUpdatedBy(loginUserID);
		dbOutboundOrderStatusId.setUpdatedOn(DateUtils.getCurrentKWTDateTime());
		return outboundOrderStatusIdRepository.save(dbOutboundOrderStatusId);
	}

	/**
	 * deleteOutboundOrderStatusId
	 * @param loginUserID
	 * @param outboundOrderStatusId
	 */
	public void deleteOutboundOrderStatusId (String warehouseId, String outboundOrderStatusId,String companyCodeId,String languageId,String plantId,String loginUserID) {
		OutboundOrderStatusId dbOutboundOrderStatusId = getOutboundOrderStatusId( warehouseId,outboundOrderStatusId,companyCodeId,languageId,plantId);
		if ( dbOutboundOrderStatusId != null) {
			dbOutboundOrderStatusId.setDeletionIndicator(1L);
			dbOutboundOrderStatusId.setUpdatedBy(loginUserID);
			outboundOrderStatusIdRepository.save(dbOutboundOrderStatusId);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + outboundOrderStatusId);
		}
	}

	//Find OutboundOrderStatusId
	public List<OutboundOrderStatusId> findOutboundOrderStatusId(FindOutboundOrderStatusId findOutboundOrderStatusId) throws ParseException {

		OutboundOrderStatusIdSpecification spec = new OutboundOrderStatusIdSpecification(findOutboundOrderStatusId);
		List<OutboundOrderStatusId> results = outboundOrderStatusIdRepository.findAll(spec);
		results = results.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		log.info("results: " + results);
		List<OutboundOrderStatusId> newOutboundOrderStatusId=new ArrayList<>();
		for(OutboundOrderStatusId dbOutboundOrderStatusId:results) {
			if (dbOutboundOrderStatusId.getCompanyIdAndDescription() != null&&dbOutboundOrderStatusId.getPlantIdAndDescription()!=null&&dbOutboundOrderStatusId.getWarehouseIdAndDescription()!=null) {
				IKeyValuePair iKeyValuePair=companyIdRepository.getCompanyIdAndDescription(dbOutboundOrderStatusId.getCompanyCodeId(), dbOutboundOrderStatusId.getLanguageId());
				IKeyValuePair iKeyValuePair1=plantIdRepository.getPlantIdAndDescription(dbOutboundOrderStatusId.getPlantId(), dbOutboundOrderStatusId.getLanguageId(), dbOutboundOrderStatusId.getCompanyCodeId());
				IKeyValuePair iKeyValuePair2=warehouseRepository.getWarehouseIdAndDescription(dbOutboundOrderStatusId.getWarehouseId(), dbOutboundOrderStatusId.getLanguageId(), dbOutboundOrderStatusId.getCompanyCodeId(), dbOutboundOrderStatusId.getPlantId());
				if (iKeyValuePair != null) {
					dbOutboundOrderStatusId.setCompanyIdAndDescription(iKeyValuePair.getCompanyCodeId() + "-" + iKeyValuePair.getDescription());
				}
				if (iKeyValuePair1 != null) {
					dbOutboundOrderStatusId.setPlantIdAndDescription(iKeyValuePair1.getPlantId() + "-" + iKeyValuePair1.getDescription());
				}
				if (iKeyValuePair2 != null) {
					dbOutboundOrderStatusId.setWarehouseIdAndDescription(iKeyValuePair2.getWarehouseId() + "-" + iKeyValuePair2.getDescription());
				}
			}
			newOutboundOrderStatusId.add(dbOutboundOrderStatusId);
		}
		return newOutboundOrderStatusId;
	}

}
