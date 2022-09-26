package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

	public PickupHeader getPickupHeaderForReversal (String warehouseId, String preOutboundNo, String refDocNumber,
										 String partnerCode, String pickupNumber, Long lineNumber, String itemCode) {
		PickupHeader pickupHeader =
				pickupHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator (
						warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
						lineNumber, itemCode, 0L);
		if (pickupHeader != null && pickupHeader.getDeletionIndicator() == 0) {
			return pickupHeader;
		} else {
			return null;
		}
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
		if (pickupHeader != null) {
			return pickupHeader;
		} 
		throw new BadRequestException ("The given PickupHeader ID : " + 
					"warehouseId : " + warehouseId + 
					",preOutboundNo : " + preOutboundNo +
					",refDocNumber : " + refDocNumber +
					",partnerCode : " + partnerCode +
					",pickupNumber : " + pickupNumber +
					",lineNumber : " + lineNumber +
					",itemCode : " + itemCode +
					" doesn't exist.");
	}

	public List<PickupHeader> getPickupHeaderForUpdateConfirmation (String warehouseId, String preOutboundNo, String refDocNumber,
																	String partnerCode, String pickupNumber, Long lineNumber, String itemCode) {
		List<PickupHeader> pickupHeader =
				pickupHeaderRepository.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndDeletionIndicator (
						warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
						lineNumber, itemCode, 0L);
		if (pickupHeader != null && !pickupHeader.isEmpty()) {
			return pickupHeader;
		}
		throw new BadRequestException ("The given PickupHeader ID : " +
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
	 * 
	 * @param searchPickupHeader
	 * @return
	 * @throws ParseException
	 */
	public List<PickupHeader> findPickupHeader(SearchPickupHeader searchPickupHeader) 
			throws ParseException {
		PickupHeaderSpecification spec = new PickupHeaderSpecification(searchPickupHeader);
		List<PickupHeader> results = pickupHeaderRepository.findAll(spec);
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

	public List<PickupHeader> updatePickupHeaderForConfirmation (String warehouseId, String preOutboundNo, String refDocNumber,
											String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String loginUserID,
											UpdatePickupHeader updatePickupHeader) throws IllegalAccessException, InvocationTargetException {
		List<PickupHeader> dbPickupHeader = getPickupHeaderForUpdateConfirmation (warehouseId, preOutboundNo, refDocNumber, partnerCode,
				pickupNumber, lineNumber, itemCode);
		if (dbPickupHeader != null && !dbPickupHeader.isEmpty()) {
			List<PickupHeader> toSave = new ArrayList<>();
			for(PickupHeader data : dbPickupHeader) {
				BeanUtils.copyProperties(updatePickupHeader, data, CommonUtils.getNullPropertyNames(updatePickupHeader));
				data.setPickUpdatedBy(loginUserID);
				data.setPickUpdatedOn(new Date());
				toSave.add(data);
			}
			return pickupHeaderRepository.saveAll(toSave);
		}
		return null;
	}

	/**
	 * updateAssignedPickerInPickupHeader
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public List<PickupHeader> patchAssignedPickerIdInPickupHeader (String loginUserID,
											List<UpdatePickupHeader> updatePickupHeaderList) throws IllegalAccessException, InvocationTargetException {
		List<PickupHeader> pickupHeaderList = new ArrayList<>();
		try{
			log.info("Process start to update Assigned Picker Id in PickupHeader: " + updatePickupHeaderList);
			for(UpdatePickupHeader data : updatePickupHeaderList){
				log.info("PickupHeader object to update : " + data);
				PickupHeader dbPickupHeader = getPickupHeaderForUpdate (data.getWarehouseId(), data.getPreOutboundNo(), data.getRefDocNumber(), data.getPartnerCode(),
						data.getPickupNumber(), data.getLineNumber(), data.getItemCode());
				log.info("Old PickupHeader object from db : " + data);
				if (dbPickupHeader != null) {
					dbPickupHeader.setAssignedPickerId(data.getAssignedPickerId());
					dbPickupHeader.setPickUpdatedBy(loginUserID);
					dbPickupHeader.setPickUpdatedOn(new Date());
					PickupHeader pickupHeader = pickupHeaderRepository.save(dbPickupHeader);
					pickupHeaderList.add(pickupHeader);
				} else {
					log.info("No record for PickupHeader object from db for data : " + data);
					throw new BadRequestException("Error in data");
				}
			}
			return pickupHeaderList;
		} catch (Exception e){
			log.error("Update Assigned Picker Id in PickupHeader failed for : " + updatePickupHeaderList);
			throw new BadRequestException("Error in data");
		}
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
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
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

	public PickupHeader deletePickupHeaderForReversal (String warehouseId, String preOutboundNo, String refDocNumber,
											String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		PickupHeader dbPickupHeader = getPickupHeaderForReversal(warehouseId, preOutboundNo, refDocNumber, partnerCode,
				pickupNumber, lineNumber, itemCode);
		if (dbPickupHeader != null) {
			dbPickupHeader.setDeletionIndicator(1L);
			dbPickupHeader.setPickupReversedBy(loginUserID);
			dbPickupHeader.setPickupReversedOn(new Date());
			return pickupHeaderRepository.save(dbPickupHeader);
		} else {
			return null;
		}
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
	 * @param loginUserID2 
	 * @param proposedPackCode 
	 * @return
	 */
	public PickupHeader deletePickupHeader (String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, Long lineNumber, String itemCode, String proposedStorageBin,
			String proposedPackCode, String loginUserID) throws IllegalAccessException, InvocationTargetException {
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
