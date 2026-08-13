package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.SearchQualityHeaderV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.QualityHeaderV2Specification;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.outbound.quality.AddQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityHeader;
import com.tekclover.wms.api.transaction.repository.specification.QualityHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QualityHeaderService extends BaseService {

    @Autowired
    OrderManagementHeaderService orderManagementHeaderService;

    @Autowired
    QualityLineService qualityLineService;

    //------------------------------------------------------------------------------------------------------
    @Autowired
    QualityHeaderRepository qualityHeaderRepository;

    @Autowired
    QualityHeaderV2Repository qualityHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;
    //------------------------------------------------------------------------------------------------------

    /**
     * getQualityHeaders
     *
     * @return
     */
    public List<QualityHeader> getQualityHeaders() {
        List<QualityHeader> qualityHeaderList = qualityHeaderRepository.findAll();
        qualityHeaderList = qualityHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return qualityHeaderList;
    }

    /**
     * getQualityHeader
     *
     * @param actualHeNo
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public QualityHeader getQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber,
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
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     * @return
     */
    public long getQualityHeaderCountForDeliveryConfirmation(String warehouseId, String refDocNumber, String preOutboundNo, Long statusId) {
        long qualityHeaderCount = qualityHeaderRepository.getQualityHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
                warehouseId, refDocNumber, preOutboundNo, statusId, 0L);
        return qualityHeaderCount;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param qualityInspectionNo
     * @param actualHeNo
     * @return
     */
    public QualityHeader getQualityHeaderForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
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
        throw new BadRequestException("The given QualityHeader values : " +
                "warehouseId : " + warehouseId +
                "preOutboundNo : " + preOutboundNo +
                "refDocNumber : " + refDocNumber +
                "partnerCode : " + partnerCode +
                "pickupNumber : " + pickupNumber +
                "qualityInspectionNo : " + qualityInspectionNo +
                "actualHeNo : " + actualHeNo +
                " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<QualityHeader> getQualityHeaderForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
                                                           String qualityInspectionNo, String actualHeNo) {
        List<QualityHeader> qualityHeader =
                qualityHeaderRepository.findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo, 0L);
        if (qualityHeader != null && qualityHeader.size() != 0) {
            return qualityHeader;
        }
        log.info("Given values for QualityHeader : " + warehouseId + ":" + preOutboundNo + ":" + refDocNumber + ":" + qualityInspectionNo
                + ":" + actualHeNo + " doesn't exist.");
        return null;
    }

    public List<QualityHeader> getInitialQualityHeaderForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
                                                                  String pickupNumber, String partnerCode) {
        List<QualityHeader> qualityHeader =
                qualityHeaderRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPickupNumberAndPartnerCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, pickupNumber, partnerCode, 0L);
        if (qualityHeader != null && qualityHeader.size() != 0) {
            return qualityHeader;
        }
        log.info("Given values for QualityHeader : " + warehouseId + ":" + preOutboundNo + ":" + refDocNumber + ":" + pickupNumber
                + ":" + partnerCode + " doesn't exist.");
        return null;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<QualityHeader> getQualityHeaderCount(String companyCodeId, String plantId, String languageId, String warehouseId) {
        List<QualityHeader> qualityHeaderList =
                qualityHeaderRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, 54L, 0L);
        return qualityHeaderList;
    }

    /**
     * findQualityHeader
     *
     * @param searchQualityHeader
     * @return
     * @throws ParseException
     */
    public List<QualityHeader> findQualityHeader(SearchQualityHeader searchQualityHeader) throws ParseException {
        QualityHeaderSpecification spec = new QualityHeaderSpecification(searchQualityHeader);
        List<QualityHeader> results = qualityHeaderRepository.findAll(spec);
        return results;
    }

    //Stream
    public Stream<QualityHeader> findQualityHeaderNew(SearchQualityHeader searchQualityHeader) throws ParseException {
        QualityHeaderSpecification spec = new QualityHeaderSpecification(searchQualityHeader);
        Stream<QualityHeader> results = qualityHeaderRepository.stream(spec, QualityHeader.class).parallel();
        return results;
    }

    /**
     * @param newQualityHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public QualityHeader createQualityHeader(AddQualityHeader newQualityHeader, String loginUserID)
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
     *
     * @param qualityInspectionNo
     * @param updateQualityHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException Pass WH_ID/PRE_OB_NO/REF_DOC_NO/HE_NO/QC_NO values in QUALITYLINE table and validate STATUS_ID value.
     *                                   If STATUS_ID = 55 , pass the same value in QUALITYHEADER and update STATUS_ID as 55
     */
    public QualityHeader updateQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber,
                                             String qualityInspectionNo, String actualHeNo, String loginUserID, UpdateQualityHeader updateQualityHeader)
            throws IllegalAccessException, InvocationTargetException {
        QualityHeader dbQualityHeader = getQualityHeaderForUpdate(warehouseId, preOutboundNo,
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
        QualityHeader dbQualityHeader = getQualityHeaderForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
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
     *
     * @param loginUserID
     * @param qualityInspectionNo
     * @return
     */
    public QualityHeader deleteQualityHeader(String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo, String loginUserID) {
        QualityHeader qualityHeader = getQualityHeader(warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo);
        if (qualityHeader != null) {
            qualityHeader.setDeletionIndicator(1L);
            qualityHeader.setQualityUpdatedBy(loginUserID);
            qualityHeader.setQualityUpdatedOn(new Date());
            return qualityHeaderRepository.save(qualityHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + qualityInspectionNo);
        }
    }

    public List<QualityHeader> deleteQualityHeaderForReversal(String warehouseId, String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo, String loginUserID) {
        List<QualityHeader> qualityHeader = getQualityHeaderForReversal(warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo);
        if (qualityHeader != null && qualityHeader.size() != 0) {
            List<QualityHeader> toUpdate = new ArrayList<>();
            qualityHeader.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setQualityUpdatedBy(loginUserID);
                data.setQualityUpdatedOn(new Date());
                toUpdate.add(data);
            });
            return qualityHeaderRepository.saveAll(toUpdate);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + qualityInspectionNo);
        }
    }

    /**
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
        QualityHeader qualityHeader = getQualityHeaderForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                pickupNumber, qualityInspectionNo, actualHeNo);
        if (qualityHeader != null) {
            qualityHeader.setDeletionIndicator(1L);
            qualityHeader.setQualityUpdatedBy(loginUserID);
            qualityHeader.setQualityUpdatedOn(new Date());
            return qualityHeaderRepository.save(qualityHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + qualityInspectionNo);
        }
    }

    //======================================================V2==================================================================

    /**
     * getQualityHeaders
     *
     * @return
     */
    public List<QualityHeaderV2> getQualityHeadersV2() {
        List<QualityHeaderV2> qualityHeaderList = qualityHeaderV2Repository.findAll();
        qualityHeaderList = qualityHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return qualityHeaderList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param qualityInspectionNo
     * @param actualHeNo
     * @return
     */
    public QualityHeaderV2 getQualityHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                              String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo) {
        QualityHeaderV2 qualityHeader =
                qualityHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo, 0L);
        if (qualityHeader != null) {
            return qualityHeader;
        }
        log.info("The given QualityHeader ID : " + qualityInspectionNo + " doesn't exist.");
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<QualityHeaderV2> getQualityHeaderForPickListCancellationV2(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo) {
        List<QualityHeaderV2> qualityHeader =
                qualityHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        if (qualityHeader != null && !qualityHeader.isEmpty()) {
            log.info("Quality Header: " + qualityHeader);
            return qualityHeader;
        }
        log.info("The given picklist ID : " + refDocNumber + " doesn't exist.");
        return null;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param statusId
     * @return
     */
    public long getQualityHeaderCountForDeliveryConfirmationV2(String companyCodeId, String plantId, String languageId,
                                                               String warehouseId, String refDocNumber, String preOutboundNo, Long statusId) {
        long qualityHeaderCount = qualityHeaderV2Repository.getQualityHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicatorV2(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, statusId, 0L);
        return qualityHeaderCount;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param qualityInspectionNo
     * @param actualHeNo
     * @return
     */
    public QualityHeaderV2 getQualityHeaderForUpdateV2(String companyCodeId, String plantId, String languageId,
                                                       String warehouseId, String preOutboundNo, String refDocNumber,
                                                       String qualityInspectionNo, String actualHeNo) {
        QualityHeaderV2 qualityHeader =
                qualityHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo, 0L);
        if (qualityHeader != null) {
            return qualityHeader;
        }
        log.info("The given QualityHeader ID : " + qualityInspectionNo + " doesn't exist.");
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param pickupNumber
     * @param qualityInspectionNo
     * @param actualHeNo
     * @return
     */
    private QualityHeaderV2 getQualityHeaderForUpdateV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
                                                        String qualityInspectionNo, String actualHeNo) {
        QualityHeaderV2 qualityHeader =
                qualityHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, 0L);
        if (qualityHeader != null) {
            return qualityHeader;
        }
        throw new BadRequestException("The given QualityHeader values : " +
                "companyCodeId : " + companyCodeId +
                "plantId : " + plantId +
                "languageId : " + languageId +
                "warehouseId : " + warehouseId +
                "preOutboundNo : " + preOutboundNo +
                "refDocNumber : " + refDocNumber +
                "partnerCode : " + partnerCode +
                "pickupNumber : " + pickupNumber +
                "qualityInspectionNo : " + qualityInspectionNo +
                "actualHeNo : " + actualHeNo +
                " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param qualityInspectionNo
     * @param actualHeNo
     * @return
     */
    public List<QualityHeaderV2> getQualityHeaderForReversalV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                               String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo) {
        List<QualityHeaderV2> qualityHeader =
                qualityHeaderV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndQualityInspectionNoAndActualHeNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo, 0L);
        if (qualityHeader != null && qualityHeader.size() != 0) {
            return qualityHeader;
        }
        log.info("Given values for QualityHeader : " + warehouseId + ":" + preOutboundNo + ":" + refDocNumber + ":" + qualityInspectionNo
                + ":" + actualHeNo + " doesn't exist.");
        return null;
    }

    public List<QualityHeaderV2> getInitialQualityHeaderForReversalV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                      String preOutboundNo, String refDocNumber, String pickupNumber, String partnerCode) {
        List<QualityHeaderV2> qualityHeader =
                qualityHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPickupNumberAndPartnerCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, pickupNumber, partnerCode, 0L);
        if (qualityHeader != null && qualityHeader.size() != 0) {
            return qualityHeader;
        }
        log.info("Given values for QualityHeader : " + warehouseId + ":" + preOutboundNo + ":" + refDocNumber + ":" + pickupNumber
                + ":" + partnerCode + " doesn't exist.");
        return null;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<QualityHeaderV2> getQualityHeaderCountV2(String warehouseId) {
        List<QualityHeaderV2> qualityHeaderList =
                qualityHeaderV2Repository.findByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, 54L, 0L);
        return qualityHeaderList;
    }

    /**
     * findQualityHeader
     *
     * @param searchQualityHeader
     * @return
     * @throws ParseException
     */
    public List<QualityHeaderV2> findQualityHeaderV2(SearchQualityHeaderV2 searchQualityHeader) throws ParseException {
        QualityHeaderV2Specification spec = new QualityHeaderV2Specification(searchQualityHeader);
        List<QualityHeaderV2> results = qualityHeaderV2Repository.findAll(spec);
        return results;
    }

    //Stream
    public Stream<QualityHeaderV2> findQualityHeaderNewV2(SearchQualityHeaderV2 searchQualityHeader) throws ParseException {
        QualityHeaderV2Specification spec = new QualityHeaderV2Specification(searchQualityHeader);
        Stream<QualityHeaderV2> results = qualityHeaderV2Repository.stream(spec, QualityHeaderV2.class).parallel();
        return results;
    }

    /**
     * @param newQualityHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public QualityHeaderV2 createQualityHeaderV2(QualityHeaderV2 newQualityHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        QualityHeaderV2 dbQualityHeader = new QualityHeaderV2();
        log.info("newQualityHeader : " + newQualityHeader + "; - " + loginUserID);
        BeanUtils.copyProperties(newQualityHeader, dbQualityHeader, CommonUtils.getNullPropertyNames(newQualityHeader));

        IKeyValuePair description = stagingLineV2Repository.getDescription(dbQualityHeader.getCompanyCodeId(),
                dbQualityHeader.getLanguageId(),
                dbQualityHeader.getPlantId(),
                dbQualityHeader.getWarehouseId());

        OrderManagementHeaderV2 orderManagementHeaderV2 = orderManagementHeaderService.getOrderManagementHeaderV2(newQualityHeader.getCompanyCodeId(),
                newQualityHeader.getPlantId(), newQualityHeader.getLanguageId(), newQualityHeader.getWarehouseId(),
                newQualityHeader.getPreOutboundNo(), newQualityHeader.getRefDocNumber());
        if (orderManagementHeaderV2 != null) {
            dbQualityHeader.setMiddlewareId(orderManagementHeaderV2.getMiddlewareId());
            dbQualityHeader.setMiddlewareTable(orderManagementHeaderV2.getMiddlewareTable());
            dbQualityHeader.setReferenceDocumentType(orderManagementHeaderV2.getReferenceDocumentType());
        }

        if (dbQualityHeader.getStatusId() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(dbQualityHeader.getStatusId(), dbQualityHeader.getLanguageId());
            dbQualityHeader.setStatusDescription(statusDescription);
        }

        dbQualityHeader.setCompanyDescription(description.getCompanyDesc());
        dbQualityHeader.setPlantDescription(description.getPlantDesc());
        dbQualityHeader.setWarehouseDescription(description.getWarehouseDesc());

        dbQualityHeader.setDeletionIndicator(0L);
        dbQualityHeader.setQualityCreatedBy(loginUserID);
        dbQualityHeader.setQualityUpdatedBy(loginUserID);
        dbQualityHeader.setQualityCreatedOn(new Date());
        dbQualityHeader.setQualityUpdatedOn(new Date());
        return qualityHeaderV2Repository.save(dbQualityHeader);
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param qualityInspectionNo
     * @param actualHeNo
     * @param loginUserID
     * @param updateQualityHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException Pass WH_ID/PRE_OB_NO/REF_DOC_NO/HE_NO/QC_NO values in QUALITYLINE table and validate STATUS_ID value.
     *                                   If STATUS_ID = 55 , pass the same value in QUALITYHEADER and update STATUS_ID as 55
     */
    public QualityHeaderV2 updateQualityHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo,
                                                 String loginUserID, QualityHeaderV2 updateQualityHeader)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        QualityHeaderV2 dbQualityHeader = getQualityHeaderForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                refDocNumber, qualityInspectionNo, actualHeNo);
        if (dbQualityHeader != null) {
            BeanUtils.copyProperties(updateQualityHeader, dbQualityHeader, CommonUtils.getNullPropertyNames(updateQualityHeader));
            dbQualityHeader.setQualityUpdatedBy(loginUserID);
            dbQualityHeader.setQualityUpdatedOn(new Date());
            return qualityHeaderV2Repository.save(dbQualityHeader);
        }
        return null;
    }

    /**
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
    public QualityHeaderV2 updateQualityHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
                                                 String qualityInspectionNo, String actualHeNo, String loginUserID,
                                                 @Valid QualityHeaderV2 updateQualityHeader) throws java.text.ParseException {
        QualityHeaderV2 dbQualityHeader = getQualityHeaderForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                pickupNumber, qualityInspectionNo, actualHeNo);
        if (dbQualityHeader != null) {
            BeanUtils.copyProperties(updateQualityHeader, dbQualityHeader, CommonUtils.getNullPropertyNames(updateQualityHeader));
            dbQualityHeader.setQualityUpdatedBy(loginUserID);
            dbQualityHeader.setQualityUpdatedOn(new Date());
            return qualityHeaderV2Repository.save(dbQualityHeader);
        }
        return null;
    }

    /**
     * deleteQualityHeader
     *
     * @param loginUserID
     * @param qualityInspectionNo
     * @return
     */
    public QualityHeaderV2 deleteQualityHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String preOutboundNo, String refDocNumber, String qualityInspectionNo, String actualHeNo, String loginUserID) throws java.text.ParseException {
        QualityHeaderV2 qualityHeader = getQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo);
        if (qualityHeader != null) {
            qualityHeader.setDeletionIndicator(1L);
            qualityHeader.setQualityUpdatedBy(loginUserID);
            qualityHeader.setQualityUpdatedOn(new Date());
            return qualityHeaderV2Repository.save(qualityHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + qualityInspectionNo);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param qualityInspectionNo
     * @param actualHeNo
     * @param loginUserID
     * @return
     */
    public List<QualityHeaderV2> deleteQualityHeaderForReversalV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                  String preOutboundNo, String refDocNumber, String qualityInspectionNo,
                                                                  String actualHeNo, String loginUserID) {
        List<QualityHeaderV2> qualityHeader = getQualityHeaderForReversalV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, qualityInspectionNo, actualHeNo);
        if (qualityHeader != null && qualityHeader.size() != 0) {
            List<QualityHeaderV2> toUpdate = new ArrayList<>();
            qualityHeader.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setQualityUpdatedBy(loginUserID);
                data.setQualityUpdatedOn(new Date());
                toUpdate.add(data);
            });
            return qualityHeaderV2Repository.saveAll(toUpdate);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + qualityInspectionNo);
        }
    }

    /**
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
    public QualityHeaderV2 deleteQualityHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber,
                                                 String qualityInspectionNo, String actualHeNo, String loginUserID) throws java.text.ParseException {
        QualityHeaderV2 qualityHeader = getQualityHeaderForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                pickupNumber, qualityInspectionNo, actualHeNo);
        if (qualityHeader != null) {
            qualityHeader.setDeletionIndicator(1L);
            qualityHeader.setQualityUpdatedBy(loginUserID);
            qualityHeader.setQualityUpdatedOn(new Date());
            return qualityHeaderV2Repository.save(qualityHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + qualityInspectionNo);
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
    //Delete QualityHeaderV2
    public List<QualityHeaderV2> deleteQualityHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                       String refDocNumber, String preOutboundNo, String loginUserID)throws Exception {

        List<QualityHeaderV2> qualityHeaderList = qualityHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,0L);
        log.info("PickList Cancellation - QualityHeader : " + qualityHeaderList);
        List<QualityHeaderV2> qualityHeaders = new ArrayList<>();
        if (qualityHeaderList != null && !qualityHeaderList.isEmpty()) {
            for (QualityHeaderV2 qualityHeaderV2 : qualityHeaderList) {
                qualityHeaderV2.setDeletionIndicator(1L);
                qualityHeaderV2.setQualityUpdatedBy(loginUserID);
                qualityHeaderV2.setQualityUpdatedOn(new Date());
                qualityHeaderV2Repository.save(qualityHeaderV2);
                qualityHeaders.add(qualityHeaderV2);
            }
        }
        return qualityHeaders;
    }

    //======================================================Walkaroo===========================================================

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param dbPickupLine
     * @param loginUserID
     */
    public void createQualityHeaderV3 (String companyCodeId, String plantId, String languageId,
                                       String warehouseId, PickupLineV2 dbPickupLine,
                                       PickupHeaderV2 pickupHeader, String idMasterAuthToken, String loginUserID) throws Exception {
        try {
            QualityHeaderV2 newQualityHeader = new QualityHeaderV2();
            BeanUtils.copyProperties(dbPickupLine, newQualityHeader, CommonUtils.getNullPropertyNames(dbPickupLine));

            // QC_NO
            NUMBER_RANGE_CODE = 11L;
            String QC_NO = getNextRangeNumber(NUMBER_RANGE_CODE, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            newQualityHeader.setQualityInspectionNo(QC_NO);

            if (dbPickupLine.getPickConfirmQty() != null) {
                newQualityHeader.setQcToQty(String.valueOf(dbPickupLine.getPickConfirmQty()));
            }

            newQualityHeader.setReferenceField1(dbPickupLine.getPickedStorageBin());
            newQualityHeader.setReferenceField2(dbPickupLine.getPickedPackCode());
            newQualityHeader.setReferenceField3(dbPickupLine.getDescription());
            newQualityHeader.setReferenceField4(dbPickupLine.getItemCode());
            newQualityHeader.setReferenceField5(String.valueOf(dbPickupLine.getLineNumber()));
            newQualityHeader.setReferenceField6(dbPickupLine.getBarcodeId());
            newQualityHeader.setManufacturerPartNo(dbPickupLine.getManufacturerName());

            if (pickupHeader != null) {
                newQualityHeader.setMiddlewareId(pickupHeader.getMiddlewareId());
                newQualityHeader.setMiddlewareTable(pickupHeader.getMiddlewareTable());
                newQualityHeader.setReferenceDocumentType(pickupHeader.getReferenceDocumentType());
            }

            newQualityHeader.setDeletionIndicator(0L);
            newQualityHeader.setQualityCreatedBy(loginUserID);
            newQualityHeader.setQualityUpdatedBy(loginUserID);
            newQualityHeader.setQualityCreatedOn(new Date());
            newQualityHeader.setQualityUpdatedOn(new Date());
            QualityHeaderV2 createdQualityHeader = qualityHeaderV2Repository.save(newQualityHeader);
            log.info("createdQualityHeader : " + createdQualityHeader);
            qualityLineService.createQualityLineV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine, createdQualityHeader, idMasterAuthToken, loginUserID);
        } catch (Exception e) {
            log.error("createdQualityHeader Error :" + e.toString());
            throw e;
        }
    }
}