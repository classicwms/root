package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.model.outbound.quality.AddQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityHeader;
import com.tekclover.wms.api.transaction.repository.QualityHeaderRepository;
import com.tekclover.wms.api.transaction.repository.specification.QualityHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QualityHeaderService {
	
	@Autowired
	private QualityHeaderRepository qualityHeaderRepository;
	
	/**
	 * getQualityHeaders
	 * @return
	 */
	public List<QualityHeader> getQualityHeaders () {
		List<QualityHeader> qualityHeaderList =  qualityHeaderRepository.findAll();
		qualityHeaderList = qualityHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return qualityHeaderList;
	}
	
	/**
	 * getQualityHeader
	 * @param actualHeNo 
	 * @param qualityInspectionNo2 
	 * @param refDocNumber 
	 * @param preOutboundNo 
	 * @return
	 */ 
	public QualityHeader getQualityHeader (String warehouseId, String preOutboundNo, String refDocNumber, 
			String qualityInspectionNo, String actualHeNo) {
		QualityHeader qualityHeader = 
				qualityHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo, 0L);
		if (qualityHeader != null) {
			return qualityHeader;
		} 
		log.info("The given QualityHeader ID : " + qualityInspectionNo + " doesn't exist.");
		return null;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param qualityInspectionNo
	 * @param actualHeNo
	 * @return
	 */
	public QualityHeader getQualityHeaderForUpdate (String warehouseId, String preOutboundNo, String refDocNumber, 
			String qualityInspectionNo, String actualHeNo) {
		QualityHeader qualityHeader = 
				qualityHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo, 0L);
		if (qualityHeader != null) {
			return qualityHeader;
		} 
		log.info("The given QualityHeader ID : " + qualityInspectionNo + " doesn't exist.");
		return null;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param pickupNumber
	 * @param qualityInspectionNo
	 * @param actualHeNo
	 * @return
	 */
	private QualityHeader getQualityHeaderForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, String qualityInspectionNo, String actualHeNo) {
		QualityHeader qualityHeader = 
				qualityHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
						warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, 0L);
		if (qualityHeader != null) {
			return qualityHeader;
		} 
		log.info("The given QualityHeader values : " + 
				"warehouseId : " + warehouseId +
				"preOutboundNo : " + preOutboundNo +
				"refDocNumber : " + refDocNumber +
				"partnerCode : " + partnerCode +
				"pickupNumber : " + pickupNumber +
				"qualityInspectionNo : " + qualityInspectionNo +
				"actualHeNo : " + actualHeNo +
				" doesn't exist.");
		return null;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param pickupNumber
	 * @param partnerCode
	 * @return
	 */
	public QualityHeader getQualityHeaderForReversal (String warehouseId, String preOutboundNo, String refDocNumber, 
			String pickupNumber, String partnerCode) {
		QualityHeader qualityHeader = 
				qualityHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPickupNumberAndPartnerCodeAndDeletionIndicator (
						warehouseId, preOutboundNo, refDocNumber, pickupNumber, partnerCode, 0L);
		if (qualityHeader != null) {
			return qualityHeader;
		} 
		log.info("Given values for QualityHeader : " + warehouseId + ":" + preOutboundNo + ":" + refDocNumber + ":" + pickupNumber
				+ ":" + partnerCode + " doesn't exist.");
		return null;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	public List<QualityHeader> getQualityHeaderCount (String warehouseId) {
		List<QualityHeader> qualityHeaderList = 
				qualityHeaderRepository.findByWarehouseIdAndStatusId(warehouseId, 54L);
		return qualityHeaderList;
	}
	
	/**
	 * findQualityHeader
	 * @param searchQualityHeader
	 * @return
	 * @throws ParseException
	 */
	public List<QualityHeader> findQualityHeader (SearchQualityHeader searchQualityHeader) throws ParseException {
		QualityHeaderSpecification spec = new QualityHeaderSpecification(searchQualityHeader);
		List<QualityHeader> results = qualityHeaderRepository.findAll(spec);
		log.info("results: " + results);
		return results;
	}
	
	/**
	 * 
	 * @param newQualityHeader
	 * @param loginUserID
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public QualityHeader createQualityHeader (AddQualityHeader newQualityHeader, String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		QualityHeader dbQualityHeader = new QualityHeader();
		log.info("newQualityHeader : " + newQualityHeader);
		BeanUtils.copyProperties(newQualityHeader, dbQualityHeader, CommonUtils.getNullPropertyNames(newQualityHeader));
		dbQualityHeader.setDeletionIndicator(0L);
		dbQualityHeader.setQualityCreatedBy(loginUserID);
		dbQualityHeader.setQualityUpdatedBy(loginUserID);
		dbQualityHeader.setQualityCreatedOn(new Date());
		dbQualityHeader.setQualityUpdatedOn(new Date());
		return qualityHeaderRepository.save(dbQualityHeader);
	}
	
	/**
	 * updateQualityHeader
	 * @param loginUserId 
	 * @param qualityInspectionNo
	 * @param updateQualityHeader
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * 
	 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/HE_NO/QC_NO values in QUALITYLINE table and validate STATUS_ID value. 
	 * If STATUS_ID = 55 , pass the same value in QUALITYHEADER and update STATUS_ID as 55
	 */
	public QualityHeader updateQualityHeader (String warehouseId, String preOutboundNo, String refDocNumber, 
			String qualityInspectionNo, String actualHeNo, String loginUserID, UpdateQualityHeader updateQualityHeader)
					throws IllegalAccessException, InvocationTargetException {
		QualityHeader dbQualityHeader = getQualityHeaderForUpdate (warehouseId, preOutboundNo, 
				refDocNumber, qualityInspectionNo, actualHeNo);
		if (dbQualityHeader != null) {
			BeanUtils.copyProperties(updateQualityHeader, dbQualityHeader, CommonUtils.getNullPropertyNames(updateQualityHeader));
			dbQualityHeader.setQualityUpdatedBy(loginUserID);
			dbQualityHeader.setQualityUpdatedOn(new Date());
			return qualityHeaderRepository.save(dbQualityHeader);
		}
		return null;
	}
	
	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param pickupNumber
	 * @param qualityInspectionNo
	 * @param actualHeNo
	 * @param loginUserID
	 * @param updateQualityHeader
	 * @return
	 */
	public QualityHeader updateQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber,
			String partnerCode, String pickupNumber, String qualityInspectionNo, String actualHeNo, String loginUserID,
			@Valid UpdateQualityHeader updateQualityHeader) {
		QualityHeader dbQualityHeader = getQualityHeaderForUpdate (warehouseId, preOutboundNo, refDocNumber, partnerCode, 
				pickupNumber, qualityInspectionNo, actualHeNo);
		if (dbQualityHeader != null) {
			BeanUtils.copyProperties(updateQualityHeader, dbQualityHeader, CommonUtils.getNullPropertyNames(updateQualityHeader));
			dbQualityHeader.setQualityUpdatedBy(loginUserID);
			dbQualityHeader.setQualityUpdatedOn(new Date());
			return qualityHeaderRepository.save(dbQualityHeader);
		}
		return null;
	}
	
	/**
	 * deleteQualityHeader
	 * @param loginUserID 
	 * @param qualityInspectionNo
	 * @return 
	 */
	public QualityHeader deleteQualityHeader (String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo, String loginUserID) {
		QualityHeader qualityHeader = getQualityHeader(warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo);
		if ( qualityHeader != null) {
			qualityHeader.setDeletionIndicator(1L);
			qualityHeader.setQualityUpdatedBy(loginUserID);
			qualityHeader.setQualityUpdatedOn(new Date());
			return qualityHeaderRepository.save(qualityHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + qualityInspectionNo);
		}
	}

	/**
	 * 
	 * @param warehouseId
	 * @param preOutboundNo
	 * @param refDocNumber
	 * @param partnerCode
	 * @param pickupNumber
	 * @param qualityInspectionNo
	 * @param actualHeNo
	 * @param loginUserID
	 * @return 
	 */
	public QualityHeader deleteQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
			String pickupNumber, String qualityInspectionNo, String actualHeNo, String loginUserID) {
		QualityHeader qualityHeader = getQualityHeaderForUpdate (warehouseId, preOutboundNo, refDocNumber, partnerCode, 
				pickupNumber, qualityInspectionNo, actualHeNo);
		if ( qualityHeader != null) {
			qualityHeader.setDeletionIndicator(1L);
			qualityHeader.setQualityUpdatedBy(loginUserID);
			qualityHeader.setQualityUpdatedOn(new Date());
			return qualityHeaderRepository.save(qualityHeader);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + qualityInspectionNo);
		}
	}
}
