package com.tekclover.wms.api.masters.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.handlingequipment.AddHandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.HandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.SearchHandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.UpdateHandlingEquipment;
import com.tekclover.wms.api.masters.repository.HandlingEquipmentRepository;
import com.tekclover.wms.api.masters.repository.specification.HandlingEquipmentSpecification;
import com.tekclover.wms.api.masters.util.CommonUtils;
import com.tekclover.wms.api.masters.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HandlingEquipmentService {
	
	@Autowired
	private HandlingEquipmentRepository handlingequipmentRepository;
	
	/**
	 * getHandlingEquipments
	 * @return
	 */
	public List<HandlingEquipment> getHandlingEquipments () {
		List<HandlingEquipment> handlingequipmentList = handlingequipmentRepository.findAll();
		log.info("handlingequipmentList : " + handlingequipmentList);
		handlingequipmentList = handlingequipmentList.stream()
				.filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
				.collect(Collectors.toList());
		return handlingequipmentList;
	}
	
	/**
	 * getHandlingEquipment
	 * @param handlingEquipmentId
	 * @return
	 */
	public HandlingEquipment getHandlingEquipment (String handlingEquipmentId) {
		HandlingEquipment handlingequipment = handlingequipmentRepository.findByHandlingEquipmentId(handlingEquipmentId).orElse(null);
		if (handlingequipment != null && handlingequipment.getDeletionIndicator() != null && handlingequipment.getDeletionIndicator() == 0) {
			return handlingequipment;
		} else {
			throw new BadRequestException("The given HandlingEquipment ID : " + handlingEquipmentId + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param heBarcode
	 * @return
	 */
	public HandlingEquipment getHandlingEquipment(String warehouseId, String heBarcode) {
		Optional<HandlingEquipment> handlingequipment = 
				handlingequipmentRepository.findByHeBarcodeAndWarehouseIdAndDeletionIndicator(heBarcode, warehouseId, 0L);
		if (!handlingequipment.isEmpty()) {
			return handlingequipment.get();
		} else {
			throw new BadRequestException("The given values: warehouseId-" + warehouseId + ", heBarcode - " + heBarcode + " doesn't exist.");
		}
	}
	
	/**
	 * 
	 * @param searchHandlingEquipment
	 * @return
	 * @throws Exception
	 */
	public List<HandlingEquipment> findHandlingEquipment(SearchHandlingEquipment searchHandlingEquipment)
			throws Exception {
		if (searchHandlingEquipment.getStartCreatedOn() != null && searchHandlingEquipment.getEndCreatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchHandlingEquipment.getStartCreatedOn(), searchHandlingEquipment.getEndCreatedOn());
			searchHandlingEquipment.setStartCreatedOn(dates[0]);
			searchHandlingEquipment.setEndCreatedOn(dates[1]);
		}
		
		if (searchHandlingEquipment.getStartUpdatedOn() != null && searchHandlingEquipment.getEndUpdatedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchHandlingEquipment.getStartUpdatedOn(), searchHandlingEquipment.getEndUpdatedOn());
			searchHandlingEquipment.setStartUpdatedOn(dates[0]);
			searchHandlingEquipment.setEndUpdatedOn(dates[1]);
		}
		
		HandlingEquipmentSpecification spec = new HandlingEquipmentSpecification(searchHandlingEquipment);
		List<HandlingEquipment> results = handlingequipmentRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * createHandlingEquipment
	 * @param newHandlingEquipment
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingEquipment createHandlingEquipment (AddHandlingEquipment newHandlingEquipment, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingEquipment dbHandlingEquipment = new HandlingEquipment();
		BeanUtils.copyProperties(newHandlingEquipment, dbHandlingEquipment, CommonUtils.getNullPropertyNames(newHandlingEquipment));
		dbHandlingEquipment.setDeletionIndicator(0L);
		dbHandlingEquipment.setCreatedBy(loginUserID);
		dbHandlingEquipment.setUpdatedBy(loginUserID);
		dbHandlingEquipment.setCreatedOn(new Date());
		dbHandlingEquipment.setUpdatedOn(new Date());
		return handlingequipmentRepository.save(dbHandlingEquipment);
	}
	
	/**
	 * updateHandlingEquipment
	 * @param handlingequipment
	 * @param updateHandlingEquipment
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public HandlingEquipment updateHandlingEquipment (String handlingEquipmentId, UpdateHandlingEquipment updateHandlingEquipment, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		HandlingEquipment dbHandlingEquipment = getHandlingEquipment(handlingEquipmentId);
		BeanUtils.copyProperties(updateHandlingEquipment, dbHandlingEquipment, CommonUtils.getNullPropertyNames(updateHandlingEquipment));
		dbHandlingEquipment.setUpdatedBy(loginUserID);
		dbHandlingEquipment.setUpdatedOn(new Date());
		return handlingequipmentRepository.save(dbHandlingEquipment);
	}
	
	/**
	 * deleteHandlingEquipment
	 * @param handlingequipment
	 */
	public void deleteHandlingEquipment (String handlingEquipmentId, String loginUserID) {
		HandlingEquipment handlingequipment = getHandlingEquipment(handlingEquipmentId);
		if ( handlingequipment != null) {
			handlingequipment.setDeletionIndicator (1L);
			handlingequipment.setUpdatedBy(loginUserID);
			handlingequipment.setUpdatedOn(new Date());
			handlingequipmentRepository.save(handlingequipment);
		} else {
			throw new EntityNotFoundException("Error in deleting Id:" + handlingEquipmentId);
		}
	}
}
