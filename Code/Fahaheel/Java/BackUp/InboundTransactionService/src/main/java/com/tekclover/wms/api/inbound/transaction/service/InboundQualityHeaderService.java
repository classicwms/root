package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inboundquality.InboundQualityHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inboundquality.InboundQualityLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inboundquality.SearchInboundQualityHeader;
import com.tekclover.wms.api.inbound.transaction.repository.InboundQualityHeaderRepository;
import com.tekclover.wms.api.inbound.transaction.repository.InboundQualityLineRepository;
import com.tekclover.wms.api.inbound.transaction.repository.specification.InboundQualityHeaderSpecification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class InboundQualityHeaderService extends BaseService {

    @Autowired
    InboundQualityHeaderRepository inboundQualityHeaderRepository;

    @Autowired
    InboundQualityLineRepository inboundQualityLineRepository;
    //--------------------------------------------------------------------------

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param inboundQualityNumber
     * @param itemCode
     * @return
     */
    public InboundQualityHeader getInboundQualityHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                                        String preInboundNo, String inboundQualityNumber, String itemCode) {
        Optional<InboundQualityHeader> inboundQualityHeader =
                inboundQualityHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundQualityNumberAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        inboundQualityNumber,
                        itemCode,
                        0L);
        if (inboundQualityHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",companyCodeId: " + companyCodeId +
                    ",languageId: " + languageId +
                    ",plantId: " + plantId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",inboundQualityNumber: " + inboundQualityNumber +
                    ",itemCode: " + itemCode +
                    " doesn't exist.");
        }
        return inboundQualityHeader.get();
    }
//
//    /**
//     * @param searchInboundQualityHeader
//     * @return
//     * @throws Exception
//     */
//    public Stream<InboundQualityHeader> findInboundQualityHeader(SearchInboundQualityHeader searchInboundQualityHeader) throws Exception {
//        if (searchInboundQualityHeader.getStartCreatedOn() != null && searchInboundQualityHeader.getEndCreatedOn() != null) {
//            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundQualityHeader.getStartCreatedOn(), searchInboundQualityHeader.getEndCreatedOn());
//            searchInboundQualityHeader.setStartCreatedOn(dates[0]);
//            searchInboundQualityHeader.setEndCreatedOn(dates[1]);
//        }
//        log.info("searchInboundQualityHeader Input: {}", searchInboundQualityHeader);
//        InboundQualityHeaderSpecification spec = new InboundQualityHeaderSpecification(searchInboundQualityHeader);
//        Stream<InboundQualityHeader> results = inboundQualityHeaderRepository.stream(spec, InboundQualityHeader.class);
//        return results;
//    }

    /**
     * @param newInboundQualityHeader
     * @param loginUserID
     * @return
     */
    public InboundQualityHeader createInboundQualityHeader(InboundQualityHeader newInboundQualityHeader, String loginUserID) {
        try {
            Optional<InboundQualityHeader> inboundQualityHeader =
                    inboundQualityHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundQualityNumberAndItemCodeAndDeletionIndicator(
                            newInboundQualityHeader.getLanguageId(),
                            newInboundQualityHeader.getCompanyCodeId(),
                            newInboundQualityHeader.getPlantId(),
                            newInboundQualityHeader.getWarehouseId(),
                            newInboundQualityHeader.getRefDocNumber(),
                            newInboundQualityHeader.getPreInboundNo(),
                            newInboundQualityHeader.getInboundQualityNumber(),
                            newInboundQualityHeader.getItemCode(),
                            0L);
            if (inboundQualityHeader.isPresent()) {
                throw new BadRequestException("Record is getting duplicated with the given values");
            }
            log.info("create InboundQualityHeader Initiated: {}", newInboundQualityHeader);
            InboundQualityHeader dbInboundQualityHeader = new InboundQualityHeader();
            BeanUtils.copyProperties(newInboundQualityHeader, dbInboundQualityHeader, CommonUtils.getNullPropertyNames(newInboundQualityHeader));
            if (dbInboundQualityHeader.getCompanyCodeId() != null && dbInboundQualityHeader.getPlantId() != null &&
                    dbInboundQualityHeader.getLanguageId() != null && dbInboundQualityHeader.getWarehouseId() != null) {

                NUMBER_RANGE_CODE = 23L;
                numberRangeId = getNextRangeNumber(NUMBER_RANGE_CODE, dbInboundQualityHeader.getCompanyCodeId(), dbInboundQualityHeader.getPlantId(),
                        dbInboundQualityHeader.getLanguageId(), dbInboundQualityHeader.getWarehouseId());

                if (numberRangeId != null) {
                    dbInboundQualityHeader.setInboundQualityNumber(numberRangeId);
                }

                description = getDescription(dbInboundQualityHeader.getCompanyCodeId(), dbInboundQualityHeader.getPlantId(),
                        dbInboundQualityHeader.getLanguageId(), dbInboundQualityHeader.getWarehouseId());
                if (description != null) {
                    dbInboundQualityHeader.setCompanyDescription(description.getCompanyDesc());
                    dbInboundQualityHeader.setPlantDescription(description.getPlantDesc());
                    dbInboundQualityHeader.setWarehouseDescription(description.getWarehouseDesc());
                }
            } else {
                throw new BadRequestException("Inbound Quality Number cannot be Null. provide company, plant, language & warehouse");
            }
            if (dbInboundQualityHeader.getStatusId() != null && dbInboundQualityHeader.getLanguageId() != null) {
                statusDescription = getStatusDescription(dbInboundQualityHeader.getStatusId(), dbInboundQualityHeader.getLanguageId());
                if (statusDescription != null) {
                    dbInboundQualityHeader.setStatusDescription(statusDescription);
                }
            }
            dbInboundQualityHeader.setDeletionIndicator(0L);
            dbInboundQualityHeader.setCreatedBy(loginUserID);
            dbInboundQualityHeader.setCreatedOn(new Date());
            return inboundQualityHeaderRepository.save(dbInboundQualityHeader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

//    /**
//     * @param newInboundQualityHeaders
//     * @param loginUserID
//     * @return
//     */
//    public List<InboundQualityHeader> createInboundQualityHeaderBatch(List<InboundQualityHeader> newInboundQualityHeaders, String loginUserID) {
//        List<InboundQualityHeader> createdIbQualityHeader = new ArrayList<>();
//        for (InboundQualityHeader dbInboundQualityHeader : newInboundQualityHeaders) {
//            InboundQualityHeader newInboundQualityHeader = createInboundQualityHeader(dbInboundQualityHeader, loginUserID);
//            createdIbQualityHeader.add(newInboundQualityHeader);
//        }
//        return createdIbQualityHeader;
//    }

//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param refDocNumber
//     * @param preInboundNo
//     * @param inboundQualityNumber
//     * @param itemCode
//     * @param loginUserID
//     * @param modifyInboundQualityHeader
//     * @return
//     */
//    public InboundQualityHeader updateInboundQualityHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                           String refDocNumber, String preInboundNo, String inboundQualityNumber,
//                                                           String itemCode, String loginUserID, InboundQualityHeader modifyInboundQualityHeader) {
//        try {
//            InboundQualityHeader dbInboundQualityHeader = getInboundQualityHeader(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber, itemCode);
//            log.info("Update InboundQualityHeader Initiated : {}", dbInboundQualityHeader);
//            BeanUtils.copyProperties(modifyInboundQualityHeader, dbInboundQualityHeader, CommonUtils.getNullPropertyNames(modifyInboundQualityHeader));
//            if (dbInboundQualityHeader.getStatusId() != null && dbInboundQualityHeader.getLanguageId() != null) {
//                statusDescription = getStatusDescription(dbInboundQualityHeader.getStatusId(), dbInboundQualityHeader.getLanguageId());
//                if (statusDescription != null) {
//                    dbInboundQualityHeader.setStatusDescription(statusDescription);
//                }
//            }
//            dbInboundQualityHeader.setUpdatedBy(loginUserID);
//            dbInboundQualityHeader.setUpdatedOn(new Date());
//            return inboundQualityHeaderRepository.save(dbInboundQualityHeader);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Exception : " + e);
//        }
//    }

//    /**
//     * update Status
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param refDocNumber
//     * @param preInboundNo
//     * @param inboundQualityNumber
//     * @param itemCode
//     * @param loginUserID
//     * @return
//     */
//    public void updateInboundQualityHeaderStatus(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                 String refDocNumber, String preInboundNo, String inboundQualityNumber,
//                                                 String itemCode, Long lineNumber, String loginUserID) {
//        try {
//
//            statusDescription = getStatusDescription(19L, languageId);
//            inboundQualityHeaderRepository.updateQualityHeaderStatusProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
//                    preInboundNo, itemCode, inboundQualityNumber, String.valueOf(lineNumber), 19L, statusDescription, loginUserID, new Date());
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Exception : " + e);
//        }
//    }

//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param refDocNumber
//     * @param preInboundNo
//     * @param inboundQualityNumber
//     * @param loginUserID
//     * @param modifyInboundQualityHeaders
//     * @return
//     */
//    public List<InboundQualityHeader> updateInboundQualityHeaderBatch(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                                      String refDocNumber, String preInboundNo, String inboundQualityNumber,
//                                                                      String loginUserID, List<InboundQualityHeader> modifyInboundQualityHeaders) {
//        try {
//            List<InboundQualityHeader> updatedInboundQualityHeaderList = new ArrayList<>();
//            for (InboundQualityHeader modifyInboundQualityHeader : modifyInboundQualityHeaders) {
//                InboundQualityHeader dbInboundQualityHeader = getInboundQualityHeader(companyCodeId, plantId, languageId, warehouseId,
//                        refDocNumber, preInboundNo, inboundQualityNumber, modifyInboundQualityHeader.getItemCode());
//                log.info("Update InboundQualityHeader Initiated : {}", dbInboundQualityHeader);
//                BeanUtils.copyProperties(modifyInboundQualityHeader, dbInboundQualityHeader, CommonUtils.getNullPropertyNames(modifyInboundQualityHeader));
//
//                if (dbInboundQualityHeader.getStatusId() != null && dbInboundQualityHeader.getLanguageId() != null) {
//                    statusDescription = getStatusDescription(dbInboundQualityHeader.getStatusId(), dbInboundQualityHeader.getLanguageId());
//                    if (statusDescription != null) {
//                        dbInboundQualityHeader.setStatusDescription(statusDescription);
//                    }
//                }
//
//                dbInboundQualityHeader.setUpdatedBy(loginUserID);
//                dbInboundQualityHeader.setUpdatedOn(new Date());
//                InboundQualityHeader updateInboundQualityHeader = inboundQualityHeaderRepository.save(dbInboundQualityHeader);
//                updatedInboundQualityHeaderList.add(updateInboundQualityHeader);
//            }
//            return updatedInboundQualityHeaderList;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Exception : " + e);
//        }
//    }

//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param refDocNumber
//     * @param preInboundNo
//     * @param inboundQualityNumber
//     * @param itemCode
//     * @param loginUserID
//     */
//    public void deleteInboundQualityHeader(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
//                                           String preInboundNo, String inboundQualityNumber, String itemCode, String loginUserID) {
//        try {
//            InboundQualityHeader dbInboundQualityHeader = getInboundQualityHeader(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber, itemCode);
//            log.info("Delete InboundQualityHeader Initiated : {}", dbInboundQualityHeader);
//            dbInboundQualityHeader.setDeletionIndicator(1L);
//            dbInboundQualityHeader.setUpdatedBy(loginUserID);
//            dbInboundQualityHeader.setUpdatedOn(new Date());
//            inboundQualityHeaderRepository.save(dbInboundQualityHeader);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Exception : " + e);
//        }
//    }

//    //=============================================Impex-V4================================================================//
//
//    /**
//     *
//     * @param createdGRLine
//     * @param statusId
//     * @param statusDesc
//     * @param inboundQualityNumber
//     */
//    public void createInboundQualityHeaderV4(GrLineV2 createdGRLine, Long statusId, String statusDesc, String inboundQualityNumber) {
//        try {
//
//            log.info("create InboundQualityHeader,Line Initiated");
//            InboundQualityHeader dbInboundQualityHeader = new InboundQualityHeader();
//            InboundQualityLine dbInboundQualityLine = new InboundQualityLine();
//            BeanUtils.copyProperties(createdGRLine, dbInboundQualityHeader, CommonUtils.getNullPropertyNames(createdGRLine));
//            BeanUtils.copyProperties(createdGRLine, dbInboundQualityLine, CommonUtils.getNullPropertyNames(createdGRLine));
//
//            dbInboundQualityHeader.setCompanyCodeId(createdGRLine.getCompanyCode());
//            dbInboundQualityLine.setCompanyCodeId(createdGRLine.getCompanyCode());
//            dbInboundQualityHeader.setReceivedQuantity(createdGRLine.getGoodReceiptQty());
//            dbInboundQualityLine.setReceivedQuantity(createdGRLine.getGoodReceiptQty());
//            dbInboundQualityHeader.setReferenceField6(createdGRLine.getManufacturerName());
//            dbInboundQualityLine.setReferenceField6(createdGRLine.getManufacturerName());
//            dbInboundQualityHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
//
//            dbInboundQualityHeader.setStatusId(statusId);
//            dbInboundQualityLine.setStatusId(statusId);
//            dbInboundQualityHeader.setStatusDescription(statusDesc);
//            dbInboundQualityLine.setStatusDescription(statusDesc);
//
//            dbInboundQualityHeader.setInboundQualityNumber(inboundQualityNumber);
//            dbInboundQualityLine.setInboundQualityNumber(inboundQualityNumber);
//
//            inboundQualityHeaderRepository.save(dbInboundQualityHeader);
//            log.info("created InboundQualityHeader,Line");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Exception : " + e);
//        }
//    }
}