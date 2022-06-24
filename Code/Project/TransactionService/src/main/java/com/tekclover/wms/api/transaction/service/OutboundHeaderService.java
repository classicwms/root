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
import com.tekclover.wms.api.transaction.model.outbound.AddOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.SearchOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundHeader;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.OutboundHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutboundHeaderService {
	
	@Autowired
	private OutboundHeaderRepository outboundHeaderRepository;
	
	@Autowired
	OutboundLineService outboundLineService;
	
	/**
	 * getOutboundHeaders
	 * @return
	 */
	public List<OutboundHeader> getOutboundHeaders () {
		List<OutboundHeader> outboundHeaderList =  outboundHeaderRepository.findAll();
		outboundHeaderList = outboundHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return outboundHeaderList;
	}
	
	/**
	 * getOutboundHeader
	 * @param partnerCode 
	 * @param refDocNumber 
	 * @param preOutboundNo2 
	 * @param warehouseId 
	 * @param plantId 
	 * @param companyCodeId 
	 * @return
	 *  pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE 
	 */
	public OutboundHeader getOutboundHeader (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode) {
		OutboundHeader outboundHeader = 
				outboundHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
		if (outboundHeader != null) {
			return outboundHeader;
		} 
		throw new BadRequestException ("The given OutboundHeader ID : " + 
					"warehouseId : " + warehouseId +
					",preOutboundNo : " + preOutboundNo +
					",refDocNumber : " + refDocNumber +
					",partnerCode : " + partnerCode +
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
	public OutboundHeader getOutboundHeaderForUpdate (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode) {
		OutboundHeader outboundHeader = 
				outboundHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
		if (outboundHeader != null) {
			return outboundHeader;
		} 
		throw new BadRequestException ("The given OutboundHeader ID : " + 
					"warehouseId : " + warehouseId +
					",preOutboundNo : " + preOutboundNo +
					",refDocNumber : " + refDocNumber +
					",partnerCode : " + partnerCode +
					" doesn't exist.");
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @return
	 */
	public OutboundHeader getOutboundHeader (String warehouseId, String preOutboundNo, String refDocNumber) {
		OutboundHeader outboundHeader = 
				outboundHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, null, 0L);
		if (outboundHeader != null) {
			return outboundHeader;
		} 
		throw new BadRequestException("The given OutboundHeader ID : " + 
					"warehouseId : " + warehouseId +
					",preOutboundNo : " + preOutboundNo +
					",refDocNumber : " + refDocNumber +
					" doesn't exist.");
	}
	
	/**
	 * 
	 * @param refDocNumber
	 * @return
	 */
	public OutboundHeader getOutboundHeader (String refDocNumber) {
		OutboundHeader outboundHeader = outboundHeaderRepository.findByRefDocNumberAndDeletionIndicator(refDocNumber, 0L);
		return outboundHeader;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param partnerCode
	 * @return
	 */
	public List<Long> getTotalOrder_N (String warehouseId, String partnerCode, 
			String refField1, Date startDate, Date endDate) {
		List<Long> totalOrderCount = outboundHeaderRepository.findTotalOrder_NByWarehouseIdAndPartnerCode(warehouseId, 
				partnerCode, refField1, startDate, endDate);
		return totalOrderCount;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param partnerCode
	 * @return
	 */
	public List<String> getRefDocListByWarehouseIdAndPartnerCode (String warehouseId, String partnerCode,
			Date startDate, Date endDate) {
		List<String> totalOrderList = 
				outboundHeaderRepository.findRefDocNoByWarehouseIdAndPartnerCode(warehouseId, partnerCode, startDate, endDate);
		return totalOrderList;
	}
	
	/**
	 * 
	 * @param searchOutboundHeader
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException 
	 */
	public List<OutboundHeader> findOutboundHeader(SearchOutboundHeader searchOutboundHeader) 
			throws ParseException, java.text.ParseException {
		
		if (searchOutboundHeader.getStartRequiredDeliveryDate() != null && searchOutboundHeader.getStartRequiredDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate());
			searchOutboundHeader.setStartRequiredDeliveryDate(dates[0]);
			searchOutboundHeader.setEndRequiredDeliveryDate(dates[1]);
		}
		
		if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getStartDeliveryConfirmedOn() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
			searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
			searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);
		}
		
		if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getStartOrderDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());
			searchOutboundHeader.setStartOrderDate(dates[0]);
			searchOutboundHeader.setEndOrderDate(dates[1]);
		}
		
		OutboundHeaderSpecification spec = new OutboundHeaderSpecification(searchOutboundHeader);
		List<OutboundHeader> headerSearchResults = outboundHeaderRepository.findAll(spec);
		
		if (headerSearchResults != null) {
			for (OutboundHeader outboundHeader : headerSearchResults) {
				List<Long> deliveryLines = outboundLineService.getDeliveryLines (outboundHeader.getWarehouseId(), 
						outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());
				List<Long> sumOfOrderedQty = outboundLineService.getSumOfOrderedQty (outboundHeader.getWarehouseId(), 
						outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());
				List<Long> countOfOrderedLines = outboundLineService.getCountofOrderedLines (outboundHeader.getWarehouseId(), 
						outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());
				List<Long> sumOfDeliveryQtyList = outboundLineService.getDeliveryQty (outboundHeader.getWarehouseId(), 
						outboundHeader.getPreOutboundNo(), outboundHeader.getRefDocNumber());
				
				double deliveryLinesCount = deliveryLines.stream().mapToLong(Long::longValue).sum();
				double sumOfOrderedQtyValue = sumOfOrderedQty.stream().mapToLong(Long::longValue).sum();
				double countOfOrderedLinesvalue = countOfOrderedLines.stream().mapToLong(Long::longValue).sum();
				double sumOfDeliveryQty = sumOfDeliveryQtyList.stream().mapToLong(Long::longValue).sum();
				
				outboundHeader.setReferenceField7 (String.valueOf(sumOfDeliveryQty));
				outboundHeader.setReferenceField8 (String.valueOf(deliveryLinesCount));
				outboundHeader.setReferenceField9 (String.valueOf(sumOfOrderedQtyValue));
				outboundHeader.setReferenceField10 (String.valueOf(countOfOrderedLinesvalue));
			}
		}
		
//		log.info("headerSearchResults: " + headerSearchResults);
		return headerSearchResults;
	}
	
	/**
	 * createOutboundHeader
	 * @param newOutboundHeader
	 * @param loginUserID 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundHeader createOutboundHeader (AddOutboundHeader newOutboundHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		
		OutboundHeader dbOutboundHeader = new OutboundHeader();
		log.info("newOutboundHeader : " + newOutboundHeader);
		BeanUtils.copyProperties(newOutboundHeader, dbOutboundHeader);
		dbOutboundHeader.setDeletionIndicator(0L);
		dbOutboundHeader.setCreatedBy(loginUserID);
		dbOutboundHeader.setUpdatedBy(loginUserID);
		dbOutboundHeader.setCreatedOn(new Date());
		dbOutboundHeader.setUpdatedOn(new Date());
		return outboundHeaderRepository.save(dbOutboundHeader);
	}
	
	/**
	 * updateOutboundHeader
	 * @param loginUserId 
	 * @param preOutboundNo
	 * @param updateOutboundHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public OutboundHeader updateOutboundHeader (String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, 
			UpdateOutboundHeader updateOutboundHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		OutboundHeader dbOutboundHeader = getOutboundHeaderForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode);
		if (dbOutboundHeader != null) {
			BeanUtils.copyProperties(updateOutboundHeader, dbOutboundHeader, CommonUtils.getNullPropertyNames(updateOutboundHeader));
			dbOutboundHeader.setUpdatedBy(loginUserID);
			dbOutboundHeader.setUpdatedOn(new Date());
			return outboundHeaderRepository.save(dbOutboundHeader);
		}
		return null;
	}
	
	/**
	 * deleteOutboundHeader
	 * @param loginUserID 
	 * @param preOutboundNo
	 */
	public void deleteOutboundHeader (String warehouseId, String preOutboundNo, String refDocNumber, 
			String partnerCode, String loginUserID) {
		OutboundHeader outboundHeader = getOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
		if ( outboundHeader != null) {
			outboundHeader.setDeletionIndicator(1L);
			outboundHeader.setUpdatedBy(loginUserID);
			outboundHeaderRepository.save(outboundHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + preOutboundNo);
		}
	}
}
