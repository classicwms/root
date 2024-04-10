package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.outbound.*;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2Stream;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.SearchOutboundHeaderV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.OutboundHeaderV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class OutboundHeaderService {
    @Autowired
    private PreOutboundLineV2Repository preOutboundLineV2Repository;

    @Autowired
    private OutboundHeaderRepository outboundHeaderRepository;

    @Autowired
    OutboundLineService outboundLineService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    //------------------------------------------------------------------------------------------------------
    @Autowired
    private OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;

    String statusDescription = null;
    //------------------------------------------------------------------------------------------------------

    /**
     * getOutboundHeaders
     *
     * @return
     */
    public List<OutboundHeader> getOutboundHeaders() {
        List<OutboundHeader> outboundHeaderList = outboundHeaderRepository.findAll();
        outboundHeaderList = outboundHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return outboundHeaderList;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE
     */
    public OutboundHeader getOutboundHeader(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode) {
        OutboundHeader outboundHeader =
                outboundHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        }
        throw new BadRequestException("The given OutboundHeader ID : " +
                "warehouseId : " + warehouseId +
                ",preOutboundNo : " + preOutboundNo +
                ",refDocNumber : " + refDocNumber +
                ",partnerCode : " + partnerCode +
                " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public OutboundHeader getOutboundHeaderForUpdate(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode) {
        OutboundHeader outboundHeader =
                outboundHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        }
        throw new BadRequestException("The given OutboundHeader ID : " +
                "warehouseId : " + warehouseId +
                ",preOutboundNo : " + preOutboundNo +
                ",refDocNumber : " + refDocNumber +
                ",partnerCode : " + partnerCode +
                " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public OutboundHeader getOutboundHeader(String warehouseId, String preOutboundNo, String refDocNumber) {
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
     * @param refDocNumber
     * @return
     */
    public OutboundHeader getOutboundHeader(String refDocNumber) {
        OutboundHeader outboundHeader = outboundHeaderRepository.findByRefDocNumberAndDeletionIndicator(refDocNumber, 0L);
        return outboundHeader;
    }

    /**
     * @param refDocNumber
     * @return
     */
    public OutboundHeader getOutboundHeader(String refDocNumber, String warehouseId) {
        OutboundHeader outboundHeader = outboundHeaderRepository.findByRefDocNumberAndWarehouseIdAndDeletionIndicator(refDocNumber, warehouseId, 0L);
        return outboundHeader;
    }

    /**
     * @param searchOutboundHeader
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OutboundHeader> findOutboundHeader(SearchOutboundHeader searchOutboundHeader, Integer flag)
//	public List<OutboundHeader> findOutboundHeader(SearchOutboundHeader searchOutboundHeader)
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

//		if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
//
//				Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
//				searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
//				searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);
//
//		} else {
//
//			searchOutboundHeader.setStartDeliveryConfirmedOn(null);
//			searchOutboundHeader.setEndDeliveryConfirmedOn(null);
//
//		}
        if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
            if (flag != 1) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
                searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
                searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);
            }
        } else {
            searchOutboundHeader.setStartDeliveryConfirmedOn(null);
            searchOutboundHeader.setEndDeliveryConfirmedOn(null);
        }

        if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());
            searchOutboundHeader.setStartOrderDate(dates[0]);
            searchOutboundHeader.setEndOrderDate(dates[1]);
        } else {
            searchOutboundHeader.setStartOrderDate(null);
            searchOutboundHeader.setEndOrderDate(null);
        }

        if (searchOutboundHeader.getWarehouseId() == null || searchOutboundHeader.getWarehouseId().isEmpty()) {
            searchOutboundHeader.setWarehouseId(null);
        }
        if (searchOutboundHeader.getRefDocNumber() == null || searchOutboundHeader.getRefDocNumber().isEmpty()) {
            searchOutboundHeader.setRefDocNumber(null);
        }
        if (searchOutboundHeader.getPartnerCode() == null || searchOutboundHeader.getPartnerCode().isEmpty()) {
            searchOutboundHeader.setPartnerCode(null);
        }
        if (searchOutboundHeader.getOutboundOrderTypeId() == null || searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
            searchOutboundHeader.setOutboundOrderTypeId(null);
        }
        if (searchOutboundHeader.getSoType() == null || searchOutboundHeader.getSoType().isEmpty()) {
            searchOutboundHeader.setSoType(null);
        }
        if (searchOutboundHeader.getStatusId() == null || searchOutboundHeader.getStatusId().isEmpty()) {
            searchOutboundHeader.setStatusId(null);
        }

        List<OutboundHeader> headerSearchResults = outboundHeaderRepository.findAllOutBoundHeaderData(searchOutboundHeader.getWarehouseId(),
                searchOutboundHeader.getRefDocNumber(), searchOutboundHeader.getPartnerCode(), searchOutboundHeader.getOutboundOrderTypeId(),
                searchOutboundHeader.getStatusId(), searchOutboundHeader.getSoType(),
                searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate(),
                searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn(),
                searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());

//		for (OutboundHeader ob : headerSearchResults) {
//			log.info("Result Conf Date :" + ob.getDeliveryConfirmedOn());
//		}
        return headerSearchResults;
    }

    @Transactional(readOnly = true)
    public List<OutboundHeaderStream> findOutboundHeadernew(SearchOutboundHeader searchOutboundHeader, Integer flag)        //Streaming
            throws ParseException, java.text.ParseException {

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
        if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
            if (flag != 1) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
                searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
                searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);
            }
        } else {
            searchOutboundHeader.setStartDeliveryConfirmedOn(null);
            searchOutboundHeader.setEndDeliveryConfirmedOn(null);
        }

        if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());
            searchOutboundHeader.setStartOrderDate(dates[0]);
            searchOutboundHeader.setEndOrderDate(dates[1]);
        } else {
            searchOutboundHeader.setStartOrderDate(null);
            searchOutboundHeader.setEndOrderDate(null);
        }

        if (searchOutboundHeader.getWarehouseId() == null || searchOutboundHeader.getWarehouseId().isEmpty()) {
            searchOutboundHeader.setWarehouseId(null);
        }
        if (searchOutboundHeader.getRefDocNumber() == null || searchOutboundHeader.getRefDocNumber().isEmpty()) {
            searchOutboundHeader.setRefDocNumber(null);
        }
        if (searchOutboundHeader.getPartnerCode() == null || searchOutboundHeader.getPartnerCode().isEmpty()) {
            searchOutboundHeader.setPartnerCode(null);
        }
        if (searchOutboundHeader.getOutboundOrderTypeId() == null || searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
            searchOutboundHeader.setOutboundOrderTypeId(null);
        }
        if (searchOutboundHeader.getSoType() == null || searchOutboundHeader.getSoType().isEmpty()) {
            searchOutboundHeader.setSoType(null);
        }
        if (searchOutboundHeader.getStatusId() == null || searchOutboundHeader.getStatusId().isEmpty()) {
            searchOutboundHeader.setStatusId(null);
        }

        Stream<OutboundHeaderStream> spec = outboundHeaderRepository.findAllOutBoundHeader(searchOutboundHeader.getWarehouseId(),
                searchOutboundHeader.getRefDocNumber(), searchOutboundHeader.getPartnerCode(), searchOutboundHeader.getOutboundOrderTypeId(),
                searchOutboundHeader.getStatusId(), searchOutboundHeader.getSoType(),
                searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate(),
                searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn(),
                searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());

        List<OutboundHeaderStream> outboundHeaderList = spec.collect(Collectors.toList());

        return outboundHeaderList;
    }

    /**
     * createOutboundHeader
     *
     * @param newOutboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundHeader createOutboundHeader(AddOutboundHeader newOutboundHeader, String loginUserID)
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
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param updateOutboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundHeader updateOutboundHeader(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
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
     *
     * @param loginUserID
     * @param preOutboundNo
     */
    public void deleteOutboundHeader(String warehouseId, String preOutboundNo, String refDocNumber,
                                     String partnerCode, String loginUserID) {
        OutboundHeader outboundHeader = getOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        if (outboundHeader != null) {
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
	/*public StreamingResponseBody findStreamOutboundHeader() {
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
	}*/
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
//	public Stream<OutboundHeaderStream> streamOutboundHeader() {
//		jdbcTemplate.setFetchSize(50);
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
	/*	Stream<OutboundHeaderStream> outboundHeaderStream = jdbcTemplate.queryForStream(
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
	}*/

//=======================================================================V2===========================================================================================

    /**
     * getOutboundHeaders
     *
     * @return
     */
    public List<OutboundHeaderV2> getOutboundHeadersV2() {
        List<OutboundHeaderV2> outboundHeaderList = outboundHeaderV2Repository.findAll();
        outboundHeaderList = outboundHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return outboundHeaderList;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE
     */
    public OutboundHeaderV2 getOutboundHeaderV2(String companyCodeId, String plantId, String languageId,
                                                String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode) {
        OutboundHeaderV2 outboundHeader =
                outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        }
        throw new BadRequestException("The given OutboundHeader ID : " +
                "companyCodeId : " + companyCodeId +
                "plantId : " + plantId +
                "languageId : " + languageId +
                "warehouseId : " + warehouseId +
                ",preOutboundNo : " + preOutboundNo +
                ",refDocNumber : " + refDocNumber +
                ",partnerCode : " + partnerCode +
                " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderForUpdateV2(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode) {
        OutboundHeaderV2 outboundHeader =
                outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        }
        throw new BadRequestException("The given OutboundHeader ID : " +
                "companyCodeId : " + companyCodeId +
                "plantId : " + plantId +
                "languageId : " + languageId +
                "warehouseId : " + warehouseId +
                ",preOutboundNo : " + preOutboundNo +
                ",refDocNumber : " + refDocNumber +
                ",partnerCode : " + partnerCode +
                " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param pickListNumber
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderForSalesInvoiceUpdateV2(String companyCodeId, String plantId, String languageId,
                                                                     String warehouseId, String pickListNumber) {
        OutboundHeaderV2 outboundHeader =
                outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPickListNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, pickListNumber, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        } else {
            return null;
        }
    }

    /**
     *
     * @param outboundIntegrationHeader
     * @param warehouseId
     * @return
     */
    public OutboundHeaderV2 updateOutboundHeaderForSalesInvoice(OutboundIntegrationHeaderV2 outboundIntegrationHeader, String warehouseId) throws java.text.ParseException {
        OutboundHeaderV2 dbOutboundHeader = getOutboundHeaderForSalesInvoiceUpdateV2(
                outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), "EN",
                warehouseId, outboundIntegrationHeader.getPickListNumber());
        log.info("OutboundHeader: " + dbOutboundHeader);

        //11-03-2024 validation removed as per business requirement - Ticket No. ALM/2024/003
//        if(dbOutboundHeader.getStatusId() != 59) {
//            throw new RuntimeException("OutboundOrder is not Delivered Yet to process Sales Invoice. Aborting...!");
//        }

        if (dbOutboundHeader != null) {
            dbOutboundHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
            dbOutboundHeader.setSalesInvoiceNumber(outboundIntegrationHeader.getSalesInvoiceNumber());
            dbOutboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
            dbOutboundHeader.setDeliveryType(outboundIntegrationHeader.getDeliveryType());
            dbOutboundHeader.setCustomerId(outboundIntegrationHeader.getCustomerId());
            dbOutboundHeader.setCustomerName(outboundIntegrationHeader.getCustomerName());
            dbOutboundHeader.setAddress(outboundIntegrationHeader.getAddress());
            dbOutboundHeader.setPhoneNumber(outboundIntegrationHeader.getPhoneNumber());
            dbOutboundHeader.setAlternateNo(outboundIntegrationHeader.getAlternateNo());
            dbOutboundHeader.setStatus(outboundIntegrationHeader.getStatus());
            dbOutboundHeader.setUpdatedOn(new Date());

            outboundHeaderV2Repository.save(dbOutboundHeader);
            log.info("OutboundHeader updated with salesInvoice: " + outboundIntegrationHeader.getSalesInvoiceNumber());

            List<OutboundLineV2> dbOutboundLineList = outboundLineService.updateOutboundLineForSalesInvoice(outboundIntegrationHeader,
                    dbOutboundHeader.getPreOutboundNo(), dbOutboundHeader.getRefDocNumber(), warehouseId);
        }
        return dbOutboundHeader;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderV2(String companyCodeId, String plantId, String languageId,
                                                String warehouseId, String preOutboundNo, String refDocNumber) {
        OutboundHeaderV2 outboundHeader =
                outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, null, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        }
        throw new BadRequestException("The given OutboundHeader ID : " +
                "companyCodeId : " + companyCodeId +
                "plantId : " + plantId +
                "languageId : " + languageId +
                "warehouseId : " + warehouseId +
                ",preOutboundNo : " + preOutboundNo +
                ",refDocNumber : " + refDocNumber +
                " doesn't exist.");
    }

    /**
     * @param refDocNumber
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String refDocNumber) {
        OutboundHeaderV2 outboundHeader = outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndRefDocNumberAndDeletionIndicator(
                companyCodeId, plantId, languageId, refDocNumber, 0L);
        return outboundHeader;
    }

    /**
     * @param refDocNumber
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String refDocNumber, String warehouseId) {
        OutboundHeaderV2 outboundHeader = outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndRefDocNumberAndWarehouseIdAndDeletionIndicator(
                companyCodeId, plantId, languageId, refDocNumber, warehouseId, 0L);
        return outboundHeader;
    }

    /**
     * @param searchOutboundHeader
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public Stream<OutboundHeaderV2> findOutboundHeaderV2(SearchOutboundHeaderV2 searchOutboundHeader)
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

        if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());
            searchOutboundHeader.setStartOrderDate(dates[0]);
            searchOutboundHeader.setEndOrderDate(dates[1]);
        } else {
            searchOutboundHeader.setStartOrderDate(null);
            searchOutboundHeader.setEndOrderDate(null);
        }

        if (searchOutboundHeader.getWarehouseId() == null || searchOutboundHeader.getWarehouseId().isEmpty()) {
            searchOutboundHeader.setWarehouseId(null);
        }
        if (searchOutboundHeader.getRefDocNumber() == null || searchOutboundHeader.getRefDocNumber().isEmpty()) {
            searchOutboundHeader.setRefDocNumber(null);
        }
        if (searchOutboundHeader.getPartnerCode() == null || searchOutboundHeader.getPartnerCode().isEmpty()) {
            searchOutboundHeader.setPartnerCode(null);
        }
        if (searchOutboundHeader.getOutboundOrderTypeId() == null || searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
            searchOutboundHeader.setOutboundOrderTypeId(null);
        }
        if (searchOutboundHeader.getSoType() == null || searchOutboundHeader.getSoType().isEmpty()) {
            searchOutboundHeader.setSoType(null);
        }
        if (searchOutboundHeader.getStatusId() == null || searchOutboundHeader.getStatusId().isEmpty()) {
            searchOutboundHeader.setStatusId(null);
        }

        OutboundHeaderV2Specification spec = new OutboundHeaderV2Specification(searchOutboundHeader);
        Stream<OutboundHeaderV2> headerSearchResults = outboundHeaderV2Repository.stream(spec, OutboundHeaderV2.class);

        return headerSearchResults;
    }

    @Transactional(readOnly = true)
    public List<OutboundHeaderV2Stream> findOutboundHeadernewV2(SearchOutboundHeaderV2 searchOutboundHeader)        //Streaming
            throws ParseException, java.text.ParseException {

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
        if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
//            if (flag != 1) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
                searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
                searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);
//            }
        } else {
            searchOutboundHeader.setStartDeliveryConfirmedOn(null);
            searchOutboundHeader.setEndDeliveryConfirmedOn(null);
        }

        if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());
            searchOutboundHeader.setStartOrderDate(dates[0]);
            searchOutboundHeader.setEndOrderDate(dates[1]);
        } else {
            searchOutboundHeader.setStartOrderDate(null);
            searchOutboundHeader.setEndOrderDate(null);
        }

        if (searchOutboundHeader.getWarehouseId() == null || searchOutboundHeader.getWarehouseId().isEmpty()) {
            searchOutboundHeader.setWarehouseId(null);
        }
        if (searchOutboundHeader.getRefDocNumber() == null || searchOutboundHeader.getRefDocNumber().isEmpty()) {
            searchOutboundHeader.setRefDocNumber(null);
        }
        if (searchOutboundHeader.getPartnerCode() == null || searchOutboundHeader.getPartnerCode().isEmpty()) {
            searchOutboundHeader.setPartnerCode(null);
        }
        if (searchOutboundHeader.getOutboundOrderTypeId() == null || searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
            searchOutboundHeader.setOutboundOrderTypeId(null);
        }
        if (searchOutboundHeader.getSoType() == null || searchOutboundHeader.getSoType().isEmpty()) {
            searchOutboundHeader.setSoType(null);
        }
        if (searchOutboundHeader.getStatusId() == null || searchOutboundHeader.getStatusId().isEmpty()) {
            searchOutboundHeader.setStatusId(null);
        }

        Stream<OutboundHeaderV2Stream> spec = outboundHeaderV2Repository.findAllOutBoundHeader(
                searchOutboundHeader.getCompanyCodeId(),
                searchOutboundHeader.getPlantId(),
                searchOutboundHeader.getLanguageId(),
                searchOutboundHeader.getWarehouseId(),
                searchOutboundHeader.getRefDocNumber(),
                searchOutboundHeader.getPreOutboundNo(),
                searchOutboundHeader.getPartnerCode(),
                searchOutboundHeader.getTargetBranchCode(), searchOutboundHeader.getOutboundOrderTypeId(),
                searchOutboundHeader.getStatusId(), searchOutboundHeader.getSoType(),
                searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate(),
                searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn(),
                searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());

        List<OutboundHeaderV2Stream> outboundHeaderList = spec.collect(Collectors.toList());

        return outboundHeaderList;
    }

    /**
     *
     * @param searchOutboundHeader
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OutboundHeaderV2Stream> findOutboundHeaderRfdV2(SearchOutboundHeaderV2 searchOutboundHeader)        //Streaming
            throws ParseException, java.text.ParseException {

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
        if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
//            if (flag != 1) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
                searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
                searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);
//            }
        } else {
            searchOutboundHeader.setStartDeliveryConfirmedOn(null);
            searchOutboundHeader.setEndDeliveryConfirmedOn(null);
        }

        if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());
            searchOutboundHeader.setStartOrderDate(dates[0]);
            searchOutboundHeader.setEndOrderDate(dates[1]);
        } else {
            searchOutboundHeader.setStartOrderDate(null);
            searchOutboundHeader.setEndOrderDate(null);
        }

        if (searchOutboundHeader.getCompanyCodeId() == null || searchOutboundHeader.getCompanyCodeId().isEmpty()) {
            searchOutboundHeader.setCompanyCodeId(null);
        }
        if (searchOutboundHeader.getPlantId() == null || searchOutboundHeader.getPlantId().isEmpty()) {
            searchOutboundHeader.setPlantId(null);
        }
        if (searchOutboundHeader.getLanguageId() == null || searchOutboundHeader.getLanguageId().isEmpty()) {
            searchOutboundHeader.setLanguageId(null);
        }
        if (searchOutboundHeader.getWarehouseId() == null || searchOutboundHeader.getWarehouseId().isEmpty()) {
            searchOutboundHeader.setWarehouseId(null);
        }
        if (searchOutboundHeader.getRefDocNumber() == null || searchOutboundHeader.getRefDocNumber().isEmpty()) {
            searchOutboundHeader.setRefDocNumber(null);
        }
        if (searchOutboundHeader.getPartnerCode() == null || searchOutboundHeader.getPartnerCode().isEmpty()) {
            searchOutboundHeader.setPartnerCode(null);
        }
        if (searchOutboundHeader.getOutboundOrderTypeId() == null || searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
            searchOutboundHeader.setOutboundOrderTypeId(null);
        }
        if (searchOutboundHeader.getSoType() == null || searchOutboundHeader.getSoType().isEmpty()) {
            searchOutboundHeader.setSoType(null);
        }
        if (searchOutboundHeader.getStatusId() == null || searchOutboundHeader.getStatusId().isEmpty()) {
            searchOutboundHeader.setStatusId(null);
        }



        List<OutboundHeaderV2Stream> headerSearchResults = outboundHeaderV2Repository.findAllOutBoundHeaderForRFD(
                searchOutboundHeader.getCompanyCodeId(),
                searchOutboundHeader.getPlantId(),
                searchOutboundHeader.getLanguageId(),
                searchOutboundHeader.getWarehouseId(),
                searchOutboundHeader.getRefDocNumber(),
                searchOutboundHeader.getPreOutboundNo(),
                searchOutboundHeader.getPartnerCode(),
                searchOutboundHeader.getTargetBranchCode(), searchOutboundHeader.getOutboundOrderTypeId(),
                searchOutboundHeader.getStatusId(), searchOutboundHeader.getSoType(),
                searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate(),
                searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn(),
                searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());


//        List<OutboundHeaderV2> headerSearchResults = outboundHeaderV2Repository.findOutboundHeaderV2(
//                searchOutboundHeader.getCompanyCodeId(),
//                searchOutboundHeader.getPlantId(),
//                searchOutboundHeader.getLanguageId(),
//                searchOutboundHeader.getWarehouseId(),
//                searchOutboundHeader.getRefDocNumber(),
//                searchOutboundHeader.getPartnerCode(),
//                searchOutboundHeader.getOutboundOrderTypeId(),
//                searchOutboundHeader.getStatusId(), searchOutboundHeader.getSoType(),
//                searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate(),
//                searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn(),
//                searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());

//        headerSearchResults.stream().forEach(oh -> {
//            Long sumOfOrderedQty = preOutboundLineV2Repository.getSumofOrderedQty(oh.getRefDocNumber());
//            Long countOfOrderedQty = preOutboundLineV2Repository.getCountOfOrderedQty(oh.getRefDocNumber());
//            Long lineOfCount = preOutboundLineV2Repository.getCountOfLine(oh.getRefDocNumber());
//            Long sumOfPickedQty = preOutboundLineV2Repository.getCountOfPickedQty(oh.getRefDocNumber());

//            log.info("sumOfOrderedQty------------> : " + sumOfOrderedQty + "," + countOfOrderedQty);
//            log.info("LineOfCount------------> : " + lineOfCount + "," + sumOfPickedQty );

//            oh.setReferenceField9((sumOfOrderedQty != null) ? String.valueOf(sumOfOrderedQty) : "0");
//            oh.setReferenceField10((countOfOrderedQty != null) ? String.valueOf(countOfOrderedQty) : "0");
//            oh.setCountOfPickedLine((lineOfCount != null) ? String.valueOf(lineOfCount) : "0");
//            oh.setSumOfPickedQty((sumOfPickedQty != null) ? String.valueOf(sumOfPickedQty) : "0");
//
//        });

        return headerSearchResults;
    }


    /**
     * createOutboundHeader
     *
     * @param newOutboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundHeaderV2 createOutboundHeaderV2(OutboundHeaderV2 newOutboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {

        OutboundHeaderV2 dbOutboundHeader = new OutboundHeaderV2();
        log.info("newOutboundHeader : " + newOutboundHeader);
        BeanUtils.copyProperties(newOutboundHeader, dbOutboundHeader, CommonUtils.getNullPropertyNames(newOutboundHeader));

        IKeyValuePair description = stagingLineV2Repository.getDescription(dbOutboundHeader.getCompanyCodeId(),
                dbOutboundHeader.getLanguageId(),
                dbOutboundHeader.getPlantId(),
                dbOutboundHeader.getWarehouseId());

        if (dbOutboundHeader.getStatusId() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(dbOutboundHeader.getStatusId(), dbOutboundHeader.getLanguageId());
            dbOutboundHeader.setStatusDescription(statusDescription);
        }

        dbOutboundHeader.setCompanyDescription(description.getCompanyDesc());
        dbOutboundHeader.setPlantDescription(description.getPlantDesc());
        dbOutboundHeader.setWarehouseDescription(description.getWarehouseDesc());

        dbOutboundHeader.setDeletionIndicator(0L);
        dbOutboundHeader.setCreatedBy(loginUserID);
        dbOutboundHeader.setUpdatedBy(loginUserID);
        dbOutboundHeader.setCreatedOn(new Date());
        dbOutboundHeader.setUpdatedOn(new Date());
        return outboundHeaderV2Repository.save(dbOutboundHeader);
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param updateOutboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundHeaderV2 updateOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String preOutboundNo, String refDocNumber, String partnerCode,
                                                   OutboundHeaderV2 updateOutboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        OutboundHeaderV2 dbOutboundHeader = getOutboundHeaderForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        Date ctdOn = dbOutboundHeader.getCreatedOn();
        Date refDocDate = dbOutboundHeader.getRefDocDate();
//        log.info("dbOutboundHeader for updating status 57: " + dbOutboundHeader);
        if (dbOutboundHeader != null) {
            BeanUtils.copyProperties(updateOutboundHeader, dbOutboundHeader, CommonUtils.getNullPropertyNames(updateOutboundHeader));
            dbOutboundHeader.setUpdatedBy(loginUserID);
            dbOutboundHeader.setUpdatedOn(new Date());
            if (dbOutboundHeader.getStatusId() != null) {
                statusDescription = stagingLineV2Repository.getStatusDescription(dbOutboundHeader.getStatusId(), dbOutboundHeader.getLanguageId());
                dbOutboundHeader.setStatusDescription(statusDescription);
            }
            log.info("dbOutboundHeader.getCreatedOn(), ref_doc_date :--->" + ctdOn + ", " + refDocDate);
            dbOutboundHeader.setCreatedOn(ctdOn);
            dbOutboundHeader.setRefDocDate(refDocDate);
            return outboundHeaderV2Repository.save(dbOutboundHeader);
        }
        return null;
    }

    /**
     * deleteOutboundHeader
     *
     * @param loginUserID
     * @param preOutboundNo
     */
    public void deleteOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                       String preOutboundNo, String refDocNumber, String partnerCode, String loginUserID) throws java.text.ParseException {
        OutboundHeaderV2 outboundHeader = getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        if (outboundHeader != null) {
            outboundHeader.setDeletionIndicator(1L);
            outboundHeader.setUpdatedBy(loginUserID);
            outboundHeader.setUpdatedOn(new Date());
            outboundHeaderV2Repository.save(outboundHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + preOutboundNo);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    //Delete OutBoundHeaderV2
    public OutboundHeaderV2 deleteOutBoundHeader(String companyCodeId, String plantId, String languageId,
                                                 String warehouseId, String refDocNumber, String preOutboundNo,String loginUserID) throws Exception{

        OutboundHeaderV2 outboundHeaderV2 = outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - OutboundHeader : " + outboundHeaderV2);
        if (outboundHeaderV2 != null) {
            outboundHeaderV2.setDeletionIndicator(1L);
            outboundHeaderV2.setUpdatedBy(loginUserID);
            outboundHeaderV2.setUpdatedOn(new Date());
            outboundHeaderV2Repository.save(outboundHeaderV2);
        }
        return outboundHeaderV2;
    }

}
