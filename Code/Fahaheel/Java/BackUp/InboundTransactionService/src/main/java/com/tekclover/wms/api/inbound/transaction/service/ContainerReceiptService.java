package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.AddContainerReceipt;
import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.ContainerReceipt;
import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.SearchContainerReceipt;
import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.UpdateContainerReceipt;
import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.v2.ContainerReceiptV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.v2.SearchContainerReceiptV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.repository.specification.ContainerReceiptSpecification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.ContainerReceiptSpecificationV2;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContainerReceiptService extends BaseService {

    @Autowired
    private ContainerReceiptRepository containerReceiptRepository;

    @Autowired
    private IDMasterService idmasterService;

    @Autowired
    private InboundHeaderRepository inboundHeaderRepository;

    @Autowired
    AuthTokenService authTokenService;

    private final String tableName = "CONTAINERRECEIPT";

    //----------------------------------------------------------------------------------------------------
    @Autowired
    private ContainerReceiptV2Repository containerReceiptV2Repository;

    @Autowired
    private InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    String statusDescription = null;
    //----------------------------------------------------------------------------------------------------

//    /**
//     * getContainerReceipts
//     *
//     * @return
//     */
//    public List<ContainerReceipt> getContainerReceipts() {
//        List<ContainerReceipt> containerReceiptList = containerReceiptRepository.findAll();
//        containerReceiptList =
//                containerReceiptList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
//        return containerReceiptList;
//    }

    /**
     * @param containerReceiptNo
     * @return
     */
    public ContainerReceipt getContainerReceipt(String containerReceiptNo) {
        Optional<ContainerReceipt> containerReceipt =
                containerReceiptRepository.findByContainerReceiptNoAndDeletionIndicator(containerReceiptNo, 0L);
        if (!containerReceipt.isEmpty()) {
            return containerReceipt.get();
        } else {
            throw new BadRequestException("The given ContainerReceipt ID : " + containerReceiptNo + " doesn't exist.");
        }
    }

    /**
     * @param preInboundNo
     * @param refDocNumber
     * @param containerReceiptNo
     * @param loginUserID
     * @return
     */
    public ContainerReceipt getContainerReceipt(String preInboundNo,
                                                String refDocNumber, String containerReceiptNo, String warehouseId, String loginUserID) {
        Optional<ContainerReceipt> containerReceipt =
                containerReceiptRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndContainerReceiptNoAndDeletionIndicator(
                        getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, preInboundNo, refDocNumber,
                        containerReceiptNo, 0L);
        if (containerReceipt.isEmpty()) {
            throw new BadRequestException("The given ContainerReceipt ID : " + containerReceiptNo + " doesn't exist.");
        }
        return containerReceipt.get();
    }

    /**
     * @param searchContainerReceipt
     * @return
     * @throws ParseException
     */
    public List<ContainerReceipt> findContainerReceipt(SearchContainerReceipt searchContainerReceipt) throws ParseException {

        if (searchContainerReceipt.getStartContainerReceivedDate() != null && searchContainerReceipt.getEndContainerReceivedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchContainerReceipt.getStartContainerReceivedDate(), searchContainerReceipt.getEndContainerReceivedDate());
            searchContainerReceipt.setStartContainerReceivedDate(dates[0]);
            searchContainerReceipt.setEndContainerReceivedDate(dates[1]);
        }

        ContainerReceiptSpecification spec = new ContainerReceiptSpecification(searchContainerReceipt);
        List<ContainerReceipt> results = containerReceiptRepository.findAll(spec);

        for (ContainerReceipt containerReceipt : results) {
            List<InboundHeader> inboundHeaderData = this.inboundHeaderRepository.findByRefDocNumberAndDeletionIndicator(containerReceipt.getRefDocNumber(), 0L);
            if (inboundHeaderData != null && !inboundHeaderData.isEmpty() && inboundHeaderData.get(0).getConfirmedOn() != null) {
                containerReceipt.setReferenceField5(inboundHeaderData.get(0).getConfirmedOn().toString());
            } else {
                containerReceipt.setReferenceField5(null);
            }
        }
        return results;
    }

    /**
     * @param searchContainerReceipt
     * @return
     * @throws ParseException
     */
    //Streaming
    public List<ContainerReceipt> findContainerReceiptNew(SearchContainerReceipt searchContainerReceipt) throws ParseException {

        if (searchContainerReceipt.getStartContainerReceivedDate() != null && searchContainerReceipt.getEndContainerReceivedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchContainerReceipt.getStartContainerReceivedDate(), searchContainerReceipt.getEndContainerReceivedDate());
            searchContainerReceipt.setStartContainerReceivedDate(dates[0]);
            searchContainerReceipt.setEndContainerReceivedDate(dates[1]);
        }

        ContainerReceiptSpecification spec = new ContainerReceiptSpecification(searchContainerReceipt);
        List<ContainerReceipt> results = containerReceiptRepository.stream(spec, ContainerReceipt.class).collect(Collectors.toList());

//		List<ContainerReceipt> containerReceiptList = results.collect(Collectors.toList());

        for (ContainerReceipt containerReceipt : results) {
            List<InboundHeader> inboundHeaderData = this.inboundHeaderRepository.findByRefDocNumberAndDeletionIndicator(containerReceipt.getRefDocNumber(), 0L);
            if (inboundHeaderData != null && !inboundHeaderData.isEmpty() && inboundHeaderData.get(0).getConfirmedOn() != null) {
                containerReceipt.setReferenceField5(inboundHeaderData.get(0).getConfirmedOn().toString());
            } else {
                containerReceipt.setReferenceField5(null);
            }
        }
        return results;
    }

    /**
     * createContainerReceipt
     *
     * @param newContainerReceipt
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ContainerReceipt createContainerReceipt(AddContainerReceipt newContainerReceipt, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        log.info("newContainerReceipt : " + newContainerReceipt);

        ContainerReceipt dbContainerReceipt = new ContainerReceipt();
        BeanUtils.copyProperties(newContainerReceipt, dbContainerReceipt, CommonUtils.getNullPropertyNames(newContainerReceipt));
        dbContainerReceipt.setLanguageId(getLanguageId());
        dbContainerReceipt.setCompanyCodeId(getCompanyCode());
        dbContainerReceipt.setPlantId(getPlantId());

        // WH_ID
        dbContainerReceipt.setWarehouseId(newContainerReceipt.getWarehouseId());

        // REF_DOC_NO
        dbContainerReceipt.setRefDocNumber(newContainerReceipt.getRefDocNumber());

        // PRE_IB_NO
        dbContainerReceipt.setPreInboundNo(newContainerReceipt.getPreInboundNo());

        // CONT_REC_NO
        AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();
        String containerRecNo = getNextRangeNumber(newContainerReceipt.getWarehouseId(), authTokenForIdmasterService.getAccess_token());
        log.info("containerRecNo-------> : " + containerRecNo);

        dbContainerReceipt.setContainerReceiptNo(containerRecNo);
        dbContainerReceipt.setContainerReceivedDate(new Date());
        dbContainerReceipt.setReferenceField2(newContainerReceipt.getReferenceField2());
        dbContainerReceipt.setStatusId(10L);
        dbContainerReceipt.setDeletionIndicator(0L);
        dbContainerReceipt.setCreatedBy(loginUserID);
        dbContainerReceipt.setUpdatedBy(loginUserID);
        dbContainerReceipt.setCreatedOn(new Date());
        dbContainerReceipt.setUpdatedOn(new Date());
        return containerReceiptRepository.save(dbContainerReceipt);
    }

    /**
     * updateContainerReceipt
     *
     * @param loginUserID
     * @param containerReceiptNo
     * @param updateContainerReceipt
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ContainerReceipt updateContainerReceipt(String containerReceiptNo,
                                                   UpdateContainerReceipt updateContainerReceipt, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        log.info("update container receipt no : " + containerReceiptNo);
        log.info("update container receipt data : " + updateContainerReceipt);
        ContainerReceipt dbContainerReceipt = getContainerReceipt(containerReceiptNo);
        BeanUtils.copyProperties(updateContainerReceipt, dbContainerReceipt, CommonUtils.getNullPropertyNames(updateContainerReceipt));

        // REF_DOC_NO
        if (updateContainerReceipt.getRefDocNumber() != null && !updateContainerReceipt.getRefDocNumber()
                .equalsIgnoreCase(dbContainerReceipt.getRefDocNumber())) {
            log.info("Inserting Audit log for REF_DOC_NO");
            idmasterService.createAuditLogRecord(dbContainerReceipt.getWarehouseId(), tableName, 0, 0, "REF_DOC_NO",
                    dbContainerReceipt.getRefDocNumber(), updateContainerReceipt.getRefDocNumber(),
                    loginUserID);
        }

        // CONT_REC_DATE
        if (updateContainerReceipt.getContainerReceivedDate() != null &&
                updateContainerReceipt.getContainerReceivedDate() != dbContainerReceipt.getContainerReceivedDate()) {
            log.info("Inserting Audit log for CONT_REC_DATE");
            idmasterService.createAuditLogRecord(dbContainerReceipt.getWarehouseId(), tableName, 0, 0, "CONT_REC_DATE",
                    String.valueOf(dbContainerReceipt.getContainerReceivedDate()),
                    String.valueOf(updateContainerReceipt.getContainerReceivedDate()),
                    loginUserID);
        }

        // CONT_NO
        if (updateContainerReceipt.getContainerNo() != null && !updateContainerReceipt.getContainerNo()
                .equalsIgnoreCase(dbContainerReceipt.getContainerNo())) {
            log.info("Inserting Audit log for CONT_NO");
            idmasterService.createAuditLogRecord(dbContainerReceipt.getWarehouseId(), tableName, 0, 0, "CONT_NO",
                    dbContainerReceipt.getContainerNo(), updateContainerReceipt.getContainerNo(),
                    loginUserID);
        }

        dbContainerReceipt.setUpdatedBy(loginUserID);
        dbContainerReceipt.setUpdatedOn(new Date());
        return containerReceiptRepository.save(dbContainerReceipt);
    }

    /**
     * deleteContainerReceipt
     *
     * @param loginUserID
     * @param containerReceiptNo
     */
    public void deleteContainerReceipt(String preInboundNo, String refDocNumber, String containerReceiptNo,
                                       String warehouseId, String loginUserID) {
        ContainerReceipt containerReceipt = getContainerReceipt(preInboundNo, refDocNumber, containerReceiptNo,
                warehouseId, loginUserID);
        if (containerReceipt != null) {
            containerReceipt.setDeletionIndicator(1L);
            containerReceipt.setUpdatedBy(loginUserID);
            containerReceiptRepository.save(containerReceipt);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + containerReceiptNo);
        }
    }

    /**
     * @param warehouseId
     * @param accessToken
     * @return
     */
    private String getNextRangeNumber(String warehouseId, String accessToken) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_ID = 01 values in NumberRANGE table
         * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then update in
         * CONTAINERRECEIPT table during save
         */
        long NUM_RAN_CODE = 1;
        int FISCALYEAR = Year.now().getValue();
        String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, FISCALYEAR, warehouseId, accessToken);
        return nextNumberRange;
    }

    //=====================================================V2====================================================

    /**
     * getContainerReceipts
     *
     * @return
     */
    public List<ContainerReceiptV2> getContainerReceiptsV2() {
        List<ContainerReceiptV2> containerReceiptList = containerReceiptV2Repository.findAll();
        containerReceiptList =
                containerReceiptList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return containerReceiptList;
    }

    /**
     * @param containerReceiptNo
     * @return
     */
    public ContainerReceiptV2 getContainerReceiptV2(String containerReceiptNo) {
        Optional<ContainerReceiptV2> containerReceipt =
                containerReceiptV2Repository.findByContainerReceiptNoAndDeletionIndicator(containerReceiptNo, 0L);
        if (!containerReceipt.isEmpty()) {
            return containerReceipt.get();
        } else {
            throw new BadRequestException("The given ContainerReceipt ID : " + containerReceiptNo + " doesn't exist.");
        }
    }

    /**
     * @param containerReceiptNo
     * @return
     */
    public ContainerReceiptV2 getContainerReceiptV2(String companyCode, String plantId, String languageId,
                                                    String warehouseId, String containerReceiptNo) {
        Optional<ContainerReceiptV2> containerReceipt =
                containerReceiptV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndContainerReceiptNoAndDeletionIndicator(
                        companyCode, plantId, languageId,
                        warehouseId, containerReceiptNo, 0L);
        if (containerReceipt.isPresent()) {
            return containerReceipt.get();
        } else {
            throw new BadRequestException("The given ContainerReceipt ID : " + containerReceiptNo + " doesn't exist.");
        }
    }

    /**
     * @param preInboundNo
     * @param refDocNumber
     * @param containerReceiptNo
     * @param loginUserID
     * @return
     */
    public ContainerReceiptV2 getContainerReceiptV2(String companyCode, String plantId, String languageId,
                                                    String warehouseId, String preInboundNo, String refDocNumber,
                                                    String containerReceiptNo, String loginUserID) {
        Optional<ContainerReceiptV2> containerReceipt =
                containerReceiptV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndContainerReceiptNoAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, preInboundNo, refDocNumber,
                        containerReceiptNo, 0L);
        if (containerReceipt.isEmpty()) {
            throw new BadRequestException("The given ContainerReceipt ID : " + containerReceiptNo + " doesn't exist.");
        }
        return containerReceipt.get();
    }

    /**
     * @param preInboundNo
     * @param refDocNumber
     * @param containerReceiptNo
     * @param warehouseId
     * @return
     */
    public ContainerReceiptV2 getContainerReceiptV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                    String containerReceiptNo, String preInboundNo, String refDocNumber) {
        Optional<ContainerReceiptV2> containerReceipt =
                containerReceiptV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndContainerReceiptNoAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, preInboundNo, refDocNumber,
                        containerReceiptNo, 0L);
        if (containerReceipt.isEmpty()) {
            throw new BadRequestException("The given ContainerReceipt ID : " + containerReceiptNo + " doesn't exist.");
        }
        return containerReceipt.get();
    }

    /**
     * @param searchContainerReceipt
     * @return
     * @throws ParseException
     */
    //Streaming
    public List<ContainerReceiptV2> findContainerReceiptV2(SearchContainerReceiptV2 searchContainerReceipt) throws ParseException {

        if (searchContainerReceipt.getStartContainerReceivedDate() != null && searchContainerReceipt.getEndContainerReceivedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchContainerReceipt.getStartContainerReceivedDate(), searchContainerReceipt.getEndContainerReceivedDate());
            searchContainerReceipt.setStartContainerReceivedDate(dates[0]);
            searchContainerReceipt.setEndContainerReceivedDate(dates[1]);
        }

        ContainerReceiptSpecificationV2 spec = new ContainerReceiptSpecificationV2(searchContainerReceipt);
        List<ContainerReceiptV2> results = containerReceiptV2Repository.stream(spec, ContainerReceiptV2.class).collect(Collectors.toList());

        for (ContainerReceiptV2 containerReceipt : results) {
            List<InboundHeaderV2> inboundHeaderData = this.inboundHeaderV2Repository.findByRefDocNumberAndDeletionIndicator(containerReceipt.getRefDocNumber(), 0L);
            if (inboundHeaderData != null && !inboundHeaderData.isEmpty() && inboundHeaderData.get(0).getConfirmedOn() != null) {
                containerReceipt.setReferenceField5(inboundHeaderData.get(0).getConfirmedOn().toString());
            } else {
                containerReceipt.setReferenceField5(null);
            }
        }
        return results;
    }

    /**
     * createContainerReceipt
     *
     * @param newContainerReceipt
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ContainerReceiptV2 createContainerReceiptV2(ContainerReceiptV2 newContainerReceipt, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        log.info("newContainerReceipt : " + newContainerReceipt);

        IKeyValuePair description = stagingLineV2Repository.getDescription(newContainerReceipt.getCompanyCodeId(),
                newContainerReceipt.getLanguageId(),
                newContainerReceipt.getPlantId(),
                newContainerReceipt.getWarehouseId());

        newContainerReceipt.setCompanyDescription(description.getCompanyDesc());
        newContainerReceipt.setPlantDescription(description.getPlantDesc());
        newContainerReceipt.setWarehouseDescription(description.getWarehouseDesc());

        // CONT_REC_NO
        AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();
        String containerRecNo = getNextRangeNumber(newContainerReceipt.getWarehouseId(),
                newContainerReceipt.getCompanyCodeId(),
                newContainerReceipt.getPlantId(),
                newContainerReceipt.getLanguageId(),
                authTokenForIdmasterService.getAccess_token());
        log.info("containerRecNo-------> : " + containerRecNo);

        newContainerReceipt.setContainerReceiptNo(containerRecNo);
        newContainerReceipt.setContainerReceivedDate(new Date());
        newContainerReceipt.setStatusId(10L);

        statusDescription = stagingLineV2Repository.getStatusDescription(10L, newContainerReceipt.getLanguageId());
        newContainerReceipt.setStatusDescription(statusDescription);

        newContainerReceipt.setDeletionIndicator(0L);
        newContainerReceipt.setCreatedBy(loginUserID);
        newContainerReceipt.setUpdatedBy(loginUserID);
        newContainerReceipt.setCreatedOn(new Date());
        newContainerReceipt.setUpdatedOn(new Date());
        return containerReceiptV2Repository.save(newContainerReceipt);
    }

    /**
     * updateContainerReceipt
     *
     * @param loginUserId
     * @param containerReceiptNo
     * @param updateContainerReceipt
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public ContainerReceiptV2 updateContainerReceiptV2(String companyCode, String plantId, String languageId,
                                                       String warehouseId, String containerReceiptNo,
                                                       ContainerReceiptV2 updateContainerReceipt, String loginUserId)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        log.info("update container receipt no : " + containerReceiptNo);
        log.info("update container receipt data : " + updateContainerReceipt);

        ContainerReceiptV2 dbContainerReceipt = getContainerReceiptV2(companyCode, plantId, languageId, warehouseId, containerReceiptNo);
        BeanUtils.copyProperties(updateContainerReceipt, dbContainerReceipt, CommonUtils.getNullPropertyNames(updateContainerReceipt));

        // REF_DOC_NO
        if (updateContainerReceipt.getRefDocNumber() != null && !updateContainerReceipt.getRefDocNumber()
                .equalsIgnoreCase(dbContainerReceipt.getRefDocNumber())) {
            log.info("Inserting Audit log for REF_DOC_NO");
            idmasterService.createAuditLogRecord(dbContainerReceipt.getWarehouseId(), tableName, 0, 0, "REF_DOC_NO",
                    dbContainerReceipt.getRefDocNumber(), updateContainerReceipt.getRefDocNumber(),
                    loginUserId);
        }

        // CONT_REC_DATE
        if (updateContainerReceipt.getContainerReceivedDate() != null &&
                updateContainerReceipt.getContainerReceivedDate() != dbContainerReceipt.getContainerReceivedDate()) {
            log.info("Inserting Audit log for CONT_REC_DATE");
            idmasterService.createAuditLogRecord(dbContainerReceipt.getWarehouseId(), tableName, 0, 0, "CONT_REC_DATE",
                    String.valueOf(dbContainerReceipt.getContainerReceivedDate()),
                    String.valueOf(updateContainerReceipt.getContainerReceivedDate()),
                    loginUserId);
        }

        // CONT_NO
        if (updateContainerReceipt.getContainerNo() != null && !updateContainerReceipt.getContainerNo()
                .equalsIgnoreCase(dbContainerReceipt.getContainerNo())) {
            log.info("Inserting Audit log for CONT_NO");
            idmasterService.createAuditLogRecord(dbContainerReceipt.getWarehouseId(), tableName, 0, 0, "CONT_NO",
                    dbContainerReceipt.getContainerNo(), updateContainerReceipt.getContainerNo(),
                    loginUserId);
        }

        dbContainerReceipt.setUpdatedBy(loginUserId);
        dbContainerReceipt.setUpdatedOn(new Date());
        return containerReceiptV2Repository.save(dbContainerReceipt);
    }

    /**
     * deleteContainerReceipt
     *
     * @param loginUserID
     * @param containerReceiptNo
     */
    public void deleteContainerReceiptV2(String companyCode, String plantId, String languageId, String warehouseId,
                                         String preInboundNo, String refDocNumber, String containerReceiptNo, String loginUserID) throws ParseException {
        ContainerReceiptV2 containerReceipt = getContainerReceiptV2(companyCode, plantId, languageId, warehouseId,
                													preInboundNo, refDocNumber, containerReceiptNo);
        if (containerReceipt != null) {
            containerReceipt.setDeletionIndicator(1L);
            containerReceipt.setUpdatedBy(loginUserID);
            containerReceipt.setUpdatedOn(new Date());
            containerReceiptV2Repository.save(containerReceipt);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + containerReceiptNo);
        }
    }

    /**
     * @param warehouseId
     * @param accessToken
     * @return
     */
    private String getNextRangeNumber(String warehouseId,
                                      String companyCodeId, String plantId,
                                      String languageId, String accessToken) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_ID = 01 values in NumberRANGE table
         * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then update in
         * CONTAINERRECEIPT table during save
         */
        long NUM_RAN_CODE = 1;
        int FISCALYEAR = Year.now().getValue();
        String nextNumberRange = idmasterService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId, accessToken);
        return nextNumberRange;
    }
}
