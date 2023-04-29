package com.tekclover.wms.api.transaction.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeaderStream;
import com.tekclover.wms.api.transaction.model.outbound.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Service
public class OutboundHeaderService {
	
	@Autowired
	private OutboundHeaderRepository outboundHeaderRepository;
	
	@Autowired
	OutboundLineService outboundLineService;

	@Autowired
	JdbcTemplate jdbcTemplate;

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
	 * @param refDocNumber
	 * @return
	 */
	public OutboundHeader getOutboundHeader (String refDocNumber, String warehouseId) {
		OutboundHeader outboundHeader = outboundHeaderRepository.findByRefDocNumberAndWarehouseIdAndDeletionIndicator(refDocNumber, warehouseId, 0L);
		return outboundHeader;
	}
	
	/**
	 * 
	 * @param searchOutboundHeader
	 * @return
	 * @throws ParseException
	 * @throws java.text.ParseException 
	 */
//	public List<OutboundHeader> findOutboundHeader(SearchOutboundHeader searchOutboundHeader, Integer flag)
	public List<OutboundHeader> findOutboundHeader(SearchOutboundHeader searchOutboundHeader)
			throws ParseException, java.text.ParseException {
		
		log.info("DeliveryConfirmedOn: " + searchOutboundHeader.getStartDeliveryConfirmedOn());
		
		if (searchOutboundHeader.getStartRequiredDeliveryDate() != null && searchOutboundHeader.getEndRequiredDeliveryDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate());
			searchOutboundHeader.setStartRequiredDeliveryDate(dates[0]);
			searchOutboundHeader.setEndRequiredDeliveryDate(dates[1]);
		} else {
			searchOutboundHeader.setStartRequiredDeliveryDate(null);
			searchOutboundHeader.setEndRequiredDeliveryDate(null);
		}
		
		if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {

				Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
				searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
				searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);

		} else {

			searchOutboundHeader.setStartDeliveryConfirmedOn(null);
			searchOutboundHeader.setEndDeliveryConfirmedOn(null);

		}
//		if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
//			if(flag != 1 ) {
//				Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
//				searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
//				searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);
//			}
//		} else {
//			searchOutboundHeader.setStartDeliveryConfirmedOn(null);
//			searchOutboundHeader.setEndDeliveryConfirmedOn(null);
//		}
		
		if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());
			searchOutboundHeader.setStartOrderDate(dates[0]);
			searchOutboundHeader.setEndOrderDate(dates[1]);
		} else {
			searchOutboundHeader.setStartOrderDate(null);
			searchOutboundHeader.setEndOrderDate(null);
		}

		if(searchOutboundHeader.getWarehouseId() == null || searchOutboundHeader.getWarehouseId().isEmpty()) {
			searchOutboundHeader.setWarehouseId(null);
		}
		if(searchOutboundHeader.getRefDocNumber() == null || searchOutboundHeader.getRefDocNumber().isEmpty()) {
			searchOutboundHeader.setRefDocNumber(null);
		}
		if(searchOutboundHeader.getPartnerCode() == null || searchOutboundHeader.getPartnerCode().isEmpty()) {
			searchOutboundHeader.setPartnerCode(null);
		}
		if(searchOutboundHeader.getOutboundOrderTypeId() == null || searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
			searchOutboundHeader.setOutboundOrderTypeId(null);
		}
		if(searchOutboundHeader.getSoType() == null || searchOutboundHeader.getSoType().isEmpty()) {
			searchOutboundHeader.setSoType(null);
		}
		if(searchOutboundHeader.getStatusId() == null || searchOutboundHeader.getStatusId().isEmpty()) {
			searchOutboundHeader.setStatusId(null);
		}

		List<OutboundHeader> headerSearchResults = outboundHeaderRepository.findAllOutBoundHeaderData(searchOutboundHeader.getWarehouseId(),
				searchOutboundHeader.getRefDocNumber(),searchOutboundHeader.getPartnerCode(),searchOutboundHeader.getOutboundOrderTypeId(),
				searchOutboundHeader.getStatusId(),searchOutboundHeader.getSoType(),
				searchOutboundHeader.getStartRequiredDeliveryDate(),searchOutboundHeader.getEndRequiredDeliveryDate(),
				searchOutboundHeader.getStartDeliveryConfirmedOn(),searchOutboundHeader.getEndDeliveryConfirmedOn(),
				searchOutboundHeader.getStartOrderDate(),searchOutboundHeader.getEndOrderDate());
		
		for (OutboundHeader ob : headerSearchResults) {
			log.info("Result Conf Date :" + ob.getDeliveryConfirmedOn());
		}
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
	//-------------------------------------------Streaming-------------------------------------------------------
	/**
	 *
	 * @return
	 */
	public StreamingResponseBody findStreamOutboundHeader() {
		Stream<OutboundHeaderStream> outboundHeaderStream = streamOutboundHeader();
		StreamingResponseBody responseBody = httpResponseOutputStream -> {
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(httpResponseOutputStream))) {
				JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
				jsonGenerator.writeStartArray();
				jsonGenerator.setCodec(new ObjectMapper());
				outboundHeaderStream.forEach(im -> {
					try {
						jsonGenerator.writeObject(im);
					} catch (IOException exception) {
						log.error("exception occurred while writing object to stream", exception);
					}
				});
				jsonGenerator.writeEndArray();
				jsonGenerator.close();
			} catch (Exception e) {
				log.info("Exception occurred while publishing data", e);
				e.printStackTrace();
			}
			log.info("finished streaming records");
		};
		return responseBody;
	}
	// =======================================JDBCTemplate=======================================================

//	private final Gson gson = new Gson();

	/**
	 * Outbound Header
	 * refDocNumber
	 * partnerCode
	 * referenceDocumentType
	 * statusId
	 * refDocDate
	 * requiredDeliveryDate
	 * referenceField1
	 * referenceField7
	 * referenceField8
	 * referenceField9
	 * referenceField10
	 * deliveryConfirmedOn
	 * @return
	 */
	public Stream<OutboundHeaderStream> streamOutboundHeader() {
		jdbcTemplate.setFetchSize(50);
		/**
		 * Outbound Header
		 * String refDocNumber
		 * String partnerCode
		 * String referenceDocumentType
		 * Long statusId
		 * Date refDocDate
		 * Date requiredDeliveryDate
		 * String referenceField1
		 * String referenceField7
		 * String referenceField8
		 * String referenceField9
		 * String referenceField10
		 * Date deliveryConfirmedOn
		 */
		Stream<OutboundHeaderStream> outboundHeaderStream = jdbcTemplate.queryForStream(
				"Select REF_DOC_NO, PARTNER_CODE, REF_DOC_TYP, STATUS_ID, REF_DOC_DATE, REQ_DEL_DATE, REF_FIELD_1, "
						+ "REF_FIELD_7, REF_FIELD_8, REF_FIELD_9, REF_FIELD_10, DLV_CNF_ON "
						+ "from tbloutboundheader "
						+ "where IS_DELETED = 0 ",
				(resultSet, rowNum) -> new OutboundHeaderStream (
						resultSet.getString("REF_DOC_NO"),
						resultSet.getString("PARTNER_CODE"),
						resultSet.getString("REF_DOC_TYP"),
						resultSet.getLong("STATUS_ID"),
						resultSet.getDate("REF_DOC_DATE"),
						resultSet.getDate("REQ_DEL_DATE"),
						resultSet.getString("REF_FIELD_1"),
						resultSet.getString("REF_FIELD_7"),
						resultSet.getString("REF_FIELD_8"),
						resultSet.getString("REF_FIELD_9"),
						resultSet.getString("REF_FIELD_10"),
						resultSet.getDate("DLV_CNF_ON")
				));
		return outboundHeaderStream;
	}
}
