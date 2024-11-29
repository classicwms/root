package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.AddOrderManagementHeader;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.OrderManagementHeader;
import com.tekclover.wms.api.enterprise.transaction.model.outbound.ordermangement.UpdateOrderManagementHeader;
import com.tekclover.wms.api.enterprise.transaction.repository.OrderManagementHeaderRepository;
import com.tekclover.wms.api.enterprise.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderManagementHeaderService {
	
	@Autowired
	private OrderManagementHeaderRepository orderManagementHeaderRepository;
	
	/**
	 * getOrderManagementHeaders
	 * @return
	 */
	public List<OrderManagementHeader> getOrderManagementHeaders () {
		List<OrderManagementHeader> orderManagementHeaderList =  orderManagementHeaderRepository.findAll();
		orderManagementHeaderList = orderManagementHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return orderManagementHeaderList;
	}
	
	/**
	 * getOrderManagementHeader
	 * @param partnerCode 
	 * @param refDocNumber2 
	 * @param preOutboundNo 
	 * @param warehouseId 
	 * @param plantId 
	 * @param companyCodeId 
	 * @return
	 */
	public OrderManagementHeader getOrderManagementHeader (String warehouseId, String preOutboundNo, 
			String refDocNumber, String partnerCode) {
		OrderManagementHeader orderManagementHeader = 
				orderManagementHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
		if (orderManagementHeader != null) {
			return orderManagementHeader;
		}
		throw new BadRequestException("The given OrderManagementHeader ID : " + 
				"warehouseId : " + warehouseId + 
				", preOutboundNo : " + preOutboundNo +
				", refDocNumber : " + refDocNumber +
				", partnerCode : " + partnerCode +
				" doesn't exist.");
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @return
	 */
	public OrderManagementHeader getOrderManagementHeaderForUpdate (String warehouseId, String preOutboundNo, 
			String refDocNumber, String partnerCode) {
		OrderManagementHeader orderManagementHeader = 
				orderManagementHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
		if (orderManagementHeader != null) {
			return orderManagementHeader;
		}
		log.info("The given OrderManagementHeader ID : " + 
				"warehouseId : " + warehouseId + 
				", preOutboundNo : " + preOutboundNo +
				", refDocNumber : " + refDocNumber +
				", partnerCode : " + partnerCode +
				" doesn't exist.");
		return null;
	}
	
	/**
	 * 
	 * @param preOutboundNo
	 * @return
	 */
	public OrderManagementHeader getOrderManagementHeader (String preOutboundNo) {
		OrderManagementHeader orderManagementHeader = orderManagementHeaderRepository.findByPreOutboundNo (preOutboundNo);
		if (orderManagementHeader != null && orderManagementHeader.getDeletionIndicator() == 0) {
			return orderManagementHeader;
		} else {
			throw new BadRequestException("The given OrderManagementHeader ID : " + preOutboundNo + " doesn't exist.");
		}
	}
	
	/**
	 * createOrderManagementHeader
	 * @param newOrderManagementHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OrderManagementHeader createOrderManagementHeader (AddOrderManagementHeader newOrderManagementHeader, String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		Optional<OrderManagementHeader> ordermangementheader = 
				orderManagementHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
						newOrderManagementHeader.getLanguageId(),
						newOrderManagementHeader.getCompanyCodeId(),
						newOrderManagementHeader.getPlantId(),
						newOrderManagementHeader.getWarehouseId(),
						newOrderManagementHeader.getPreOutboundNo(),
						newOrderManagementHeader.getRefDocNumber(),
						newOrderManagementHeader.getPartnerCode(),
						0L);
		if (!ordermangementheader.isEmpty()) {
			throw new BadRequestException("Record is getting duplicated with the given values");
		}
		OrderManagementHeader dbOrderManagementHeader = new OrderManagementHeader();
		log.info("newOrderManagementHeader : " + newOrderManagementHeader);
		BeanUtils.copyProperties(newOrderManagementHeader, dbOrderManagementHeader);
		dbOrderManagementHeader.setDeletionIndicator(0L);
		return orderManagementHeaderRepository.save(dbOrderManagementHeader);
	}
	
	/**
	 * updateOrderManagementHeader
	 * @param loginUserId 
	 * @param refDocNumber
	 * @param updateOrderManagementHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OrderManagementHeader updateOrderManagementHeader (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, 
			String loginUserID, UpdateOrderManagementHeader updateOrderManagementHeader)
			throws IllegalAccessException, InvocationTargetException {
		OrderManagementHeader dbOrderManagementHeader = getOrderManagementHeaderForUpdate (warehouseId, preOutboundNo, refDocNumber, partnerCode);
		if (dbOrderManagementHeader != null) {
			BeanUtils.copyProperties(updateOrderManagementHeader, dbOrderManagementHeader, CommonUtils.getNullPropertyNames(updateOrderManagementHeader));
			return orderManagementHeaderRepository.save(dbOrderManagementHeader);
		}
		return null;
	}
	
	/**
	 * deleteOrderManagementHeader
	 * @param loginUserID 
	 * @param refDocNumber
	 */
	public void deleteOrderManagementHeader (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String loginUserID) {
		OrderManagementHeader orderManagementHeader = 
				getOrderManagementHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
		if ( orderManagementHeader != null) {
			orderManagementHeader.setDeletionIndicator(1L);
			orderManagementHeaderRepository.save(orderManagementHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
		}
	}
}