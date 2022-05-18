package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.UpdatePickupHeader;
import com.tekclover.wms.api.transaction.repository.PickupHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.PickupHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PickupHeaderService {
	
	@Autowired
	private PickupHeaderRepository pickupHeaderRepository;
	
	/**
	 * getPickupHeaders
	 * @return
	 */
	public List<PickupHeader> getPickupHeaders () {
		List<PickupHeader> pickupHeaderList =  pickupHeaderRepository.findAll();
		pickupHeaderList = pickupHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return pickupHeaderList;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param pickupNumber
	 * @param lineNumber
	 * @param itemCode
	 * @param proposedStorageBin
	 * @param proposedPackCode
	 * @return
	 */
	public PickupHeader getPickupHeader (String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, Long lineNumber, String itemCode) {
		PickupHeader pickupHeader = 
				pickupHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator (
						warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, 
    					lineNumber, itemCode, 0L);
		if (pickupHeader != null && pickupHeader.getDeletionIndicator() == 0) {
			return pickupHeader;
		} 
		throw new BadRequestException("The given PickupHeader ID : " + 
					"warehouseId : " + warehouseId + 
					",preOutboundNo : " + preOutboundNo +
					",refDocNumber : " + refDocNumber +
					",partnerCode : " + partnerCode +
					",pickupNumber : " + pickupNumber +
					",lineNumber : " + lineNumber +
					",itemCode : " + itemCode +
					" doesn't exist.");
	}
	
	/**
	 * getPickupHeader
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param pickupNumber
	 * @return
	 */
	public PickupHeader getPickupHeader (String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber) {
		PickupHeader pickupHeader = 
				pickupHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndDeletionIndicator (
						warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, 0L);
		if (pickupHeader != null && pickupHeader.getDeletionIndicator() == 0) {
			return pickupHeader;
		} 
		throw new BadRequestException("The given PickupHeader ID : " + 
					"warehouseId : " + warehouseId + 
					",preOutboundNo : " + preOutboundNo +
					",refDocNumber : " + refDocNumber +
					",partnerCode : " + partnerCode +
					",pickupNumber : " + pickupNumber +
					" doesn't exist.");
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param pickupNumber
	 * @param lineNumber
	 * @param itemCode
	 * @return
	 */
	public PickupHeader getPickupHeaderForUpdate (String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, Long lineNumber, String itemCode) {
		PickupHeader pickupHeader = 
				pickupHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator (
						warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, 
    					lineNumber, itemCode, 0L);
		if (pickupHeader != null && pickupHeader.getDeletionIndicator() == 0) {
			return pickupHeader;
		} 
		log.info("The given PickupHeader ID : " + 
					"warehouseId : " + warehouseId + 
					",preOutboundNo : " + preOutboundNo +
					",refDocNumber : " + refDocNumber +
					",partnerCode : " + partnerCode +
					",pickupNumber : " + pickupNumber +
					",lineNumber : " + lineNumber +
					",itemCode : " + itemCode +
					" doesn't exist.");
		return null;
	}
	
	/**
	 * 
	 * @param searchPickupHeader
	 * @return
	 * @throws ParseException
	 */
	public List<PickupHeader> findPickupHeader(SearchPickupHeader searchPickupHeader) 
			throws ParseException {
		PickupHeaderSpecification spec = new PickupHeaderSpecification(searchPickupHeader);
		List<PickupHeader> results = pickupHeaderRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param orderTypeId
	 * @return
	 */
	public List<PickupHeader> getPickupHeaderCount (String warehouseId, List<Long> orderTypeId) {
		List<PickupHeader> header = 
				pickupHeaderRepository.findByWarehouseIdAndStatusIdAndOutboundOrderTypeIdIn (
						warehouseId, 48L, orderTypeId);
		return header;
	}
	
	/**
	 * createPickupHeader
	 * @param newPickupHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PickupHeader createPickupHeader (AddPickupHeader newPickupHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PickupHeader dbPickupHeader = new PickupHeader();
		log.info("newPickupHeader : " + newPickupHeader);
		BeanUtils.copyProperties(newPickupHeader, dbPickupHeader);
		dbPickupHeader.setDeletionIndicator(0L);
		return pickupHeaderRepository.save(dbPickupHeader);
	}
	
	/**
	 * updatePickupHeader
	 * @param loginUserId 
	 * @param pickupNumber
	 * @param loginUserID2 
	 * @param proposedPackCode 
	 * @param updatePickupHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public PickupHeader updatePickupHeader (String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String loginUserID, 
			UpdatePickupHeader updatePickupHeader) throws IllegalAccessException, InvocationTargetException {
		PickupHeader dbPickupHeader = getPickupHeaderForUpdate (warehouseId, preOutboundNo, refDocNumber, partnerCode, 
				pickupNumber, lineNumber, itemCode);
		if (dbPickupHeader != null) {
			BeanUtils.copyProperties(updatePickupHeader, dbPickupHeader, CommonUtils.getNullPropertyNames(updatePickupHeader));
			dbPickupHeader.setPickUpdatedBy(loginUserID);
			dbPickupHeader.setPickUpdatedOn(new Date());
			return pickupHeaderRepository.save(dbPickupHeader);
		}
		return null;
	}
	
	/**
	 * deletePickupHeader
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param pickupNumber
	 * @param lineNumber
	 * @param itemCode
	 * @param loginUserID
	 * @return
	 */
	public PickupHeader deletePickupHeader (String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		PickupHeader dbPickupHeader = getPickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
				pickupNumber, lineNumber, itemCode);
		if (dbPickupHeader != null) {
			dbPickupHeader.setDeletionIndicator(1L);
			dbPickupHeader.setPickupReversedBy(loginUserID);
			dbPickupHeader.setPickupReversedOn(new Date());
			return pickupHeaderRepository.save(dbPickupHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting PickupHeader : -> Id: " + lineNumber);
		}
	}
}
