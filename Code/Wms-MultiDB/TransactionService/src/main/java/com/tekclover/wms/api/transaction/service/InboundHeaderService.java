package com.tekclover.wms.api.transaction.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.inbound.*;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.SearchInboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.InboundHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.InboundHeaderV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class InboundHeaderService extends BaseService {

    @Autowired
    private GrLineV2Repository grLineV2Repository;
    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;
    @Autowired
    private PutAwayHeaderV2Repository putAwayHeaderV2Repository;
    @Autowired
    private PutAwayLineV2Repository putAwayLineV2Repository;
    @Autowired
    private StagingHeaderV2Repository stagingHeaderV2Repository;
    @Autowired
    private GrHeaderV2Repository grHeaderV2Repository;
    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;
    @Autowired
    private PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    private InboundHeaderRepository inboundHeaderRepository;

    @Autowired
    private InboundLineRepository inboundLineRepository;

    @Autowired
    private PreInboundHeaderRepository preInboundHeaderRepository;

    @Autowired
    private PreInboundLineRepository preInboundLineRepository;

    @Autowired
    private PreInboundHeaderService preInboundHeaderService;

    @Autowired
    private PreInboundLineService preInboundLineService;

    @Autowired
    private StagingHeaderService stagingHeaderService;

    @Autowired
    private StagingLineService stagingLineService;

    @Autowired
    private StagingHeaderRepository stagingHeaderRepository;

    @Autowired
    private GrHeaderService grHeaderService;

    @Autowired
    private GrHeaderRepository grHeaderRepository;

    @Autowired
    private GrLineService grLineService;

    @Autowired
    private PutAwayHeaderService putAwayHeaderService;

    @Autowired
    private PutAwayLineService putAwayLineService;

    @Autowired
    private InboundLineService inboundLineService;

    @Autowired
    private AuthTokenService authTokenService;

    //----------------------------------------------------------------------------------------------
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    private InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private IndusMegaFoodService indusMegaFoodService;

    @Autowired
    private StorageBinService storageBinService;

    @Autowired
    private OrderManagementLineService orderManagementLineService;

    boolean alreadyExecuted = true;
    //----------------------------------------------------------------------------------------------

    /**
     * getInboundHeaders
     * @return
     */
    public List<InboundHeader> getInboundHeaders() {
        List<InboundHeader> containerReceiptList = inboundHeaderRepository.findAll();
        containerReceiptList = containerReceiptList
                .stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return containerReceiptList;
    }

    /**
     * getInboundHeader
     * @param refDocNumber
     * @return
     */
    public InboundHeaderEntity getInboundHeader(String warehouseId, String refDocNumber, String preInboundNo) {
        Optional<InboundHeader> optInboundHeader =
                inboundHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        if (optInboundHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  " doesn't exist.");
        }
        InboundHeader inboundHeader = optInboundHeader.get();

        List<InboundHeaderEntity> listInboundHeaderEntity = new ArrayList<>();
        List<InboundLine> inboundLineList = inboundLineService.getInboundLine(inboundHeader.getWarehouseId(),
                                                                              inboundHeader.getRefDocNumber(), inboundHeader.getPreInboundNo());
        log.info("inboundLineList found: " + inboundLineList);

        List<InboundLineEntity> listInboundLineEntity = new ArrayList<>();
        for (InboundLine inboundLine : inboundLineList) {
            InboundLineEntity inboundLineEntity = new InboundLineEntity();
            BeanUtils.copyProperties(inboundLine, inboundLineEntity, CommonUtils.getNullPropertyNames(inboundLine));
            inboundLineEntity.setOrderQty(inboundLine.getOrderQty());
            inboundLineEntity.setOrderUom(inboundLine.getOrderUom());
            listInboundLineEntity.add(inboundLineEntity);
        }

        InboundHeaderEntity inboundHeaderEntity = new InboundHeaderEntity();
        BeanUtils.copyProperties(inboundHeader, inboundHeaderEntity, CommonUtils.getNullPropertyNames(inboundHeader));
        inboundHeaderEntity.setInboundLine(listInboundLineEntity);
        listInboundHeaderEntity.add(inboundHeaderEntity);

        return inboundHeaderEntity;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public InboundHeader getInboundHeaderByEntity(String warehouseId, String refDocNumber, String preInboundNo) {
        Optional<InboundHeader> optInboundHeader =
                inboundHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        if (optInboundHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  " doesn't exist.");
        }
        return optInboundHeader.get();
    }

    /**
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public List<InboundHeader> getInboundHeader(String refDocNumber, String preInboundNo) {
        List<InboundHeader> inboundHeader =
                inboundHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        refDocNumber,
                        preInboundNo,
                        0L);
        if (inboundHeader.isEmpty()) {
            throw new BadRequestException("The given values: " +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  " doesn't exist.");
        }
        return inboundHeader;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<InboundHeaderEntity> getInboundHeaderWithStatusId(String warehouseId) {
        List<InboundHeader> inboundHeaderList =
                inboundHeaderRepository.findByWarehouseIdAndDeletionIndicator(warehouseId, 0L);
        if (inboundHeaderList.isEmpty()) {
            throw new BadRequestException("The given InboundHeader :" +
                                                  " warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }

        List<InboundHeaderEntity> listInboundHeaderEntity = new ArrayList<>();
        for (InboundHeader inboundHeader : inboundHeaderList) {
            List<InboundLine> inboundLineList = inboundLineService.getInboundLine(inboundHeader.getWarehouseId(),
                                                                                  inboundHeader.getRefDocNumber(), inboundHeader.getPreInboundNo());
            log.info("inboundLineList found: " + inboundLineList);

            List<InboundLineEntity> listInboundLineEntity = new ArrayList<>();
            for (InboundLine inboundLine : inboundLineList) {
                InboundLineEntity inboundLineEntity = new InboundLineEntity();
                BeanUtils.copyProperties(inboundLine, inboundLineEntity, CommonUtils.getNullPropertyNames(inboundLine));
                listInboundLineEntity.add(inboundLineEntity);
            }

            InboundHeaderEntity inboundHeaderEntity = new InboundHeaderEntity();
            BeanUtils.copyProperties(inboundHeader, inboundHeaderEntity, CommonUtils.getNullPropertyNames(inboundHeader));
            inboundHeaderEntity.setInboundLine(listInboundLineEntity);
            listInboundHeaderEntity.add(inboundHeaderEntity);
        }
        return listInboundHeaderEntity;
    }

    /**
     * @param searchInboundHeader
     * @return
     * @throws Exception
     */
    public List<InboundHeader> findInboundHeader(SearchInboundHeader searchInboundHeader) throws Exception {
        if (searchInboundHeader.getStartCreatedOn() != null && searchInboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartCreatedOn(), searchInboundHeader.getEndCreatedOn());
            searchInboundHeader.setStartCreatedOn(dates[0]);
            searchInboundHeader.setEndCreatedOn(dates[1]);
        }

        if (searchInboundHeader.getStartConfirmedOn() != null && searchInboundHeader.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartConfirmedOn(), searchInboundHeader.getEndConfirmedOn());
            searchInboundHeader.setStartConfirmedOn(dates[0]);
            searchInboundHeader.setEndConfirmedOn(dates[1]);
        }

        InboundHeaderSpecification spec = new InboundHeaderSpecification(searchInboundHeader);
        List<InboundHeader> results = inboundHeaderRepository.findAll(spec);
//		log.info("results: " + results);
        return results;
    }

    /**
     * @param searchInboundHeader
     * @return
     * @throws Exception
     */
    //Stream
    public Stream<InboundHeader> findInboundHeaderNew(SearchInboundHeader searchInboundHeader) throws Exception {
        if (searchInboundHeader.getStartCreatedOn() != null && searchInboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartCreatedOn(), searchInboundHeader.getEndCreatedOn());
            searchInboundHeader.setStartCreatedOn(dates[0]);
            searchInboundHeader.setEndCreatedOn(dates[1]);
        }

        if (searchInboundHeader.getStartConfirmedOn() != null && searchInboundHeader.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartConfirmedOn(), searchInboundHeader.getEndConfirmedOn());
            searchInboundHeader.setStartConfirmedOn(dates[0]);
            searchInboundHeader.setEndConfirmedOn(dates[1]);
        }

        InboundHeaderSpecification spec = new InboundHeaderSpecification(searchInboundHeader);
        Stream<InboundHeader> results = inboundHeaderRepository.stream(spec, InboundHeader.class);

        return results;
    }

    /**
     * createInboundHeader
     * @param newInboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundHeader createInboundHeader(AddInboundHeader newInboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        Optional<InboundHeader> inboundheader =
                inboundHeaderRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        newInboundHeader.getWarehouseId(),
                        newInboundHeader.getRefDocNumber(),
                        newInboundHeader.getPreInboundNo(),
                        0L);
        if (!inboundheader.isEmpty()) {
            throw new BadRequestException("Record is getting duplicated with the given values");
        }
        InboundHeader dbInboundHeader = new InboundHeader();
        log.info("newInboundHeader : " + newInboundHeader);
        BeanUtils.copyProperties(newInboundHeader, dbInboundHeader, CommonUtils.getNullPropertyNames(newInboundHeader));
        dbInboundHeader.setDeletionIndicator(0L);
        dbInboundHeader.setCreatedBy(loginUserID);
        dbInboundHeader.setUpdatedBy(loginUserID);
        dbInboundHeader.setCreatedOn(new Date());
        dbInboundHeader.setUpdatedOn(new Date());
        return inboundHeaderRepository.save(dbInboundHeader);
    }

    /**
     * @param refDocNumber
     * @param preInboundNo
     * @param asnNumber
     * @return
     * @return
     */
    public Boolean replaceASN(String refDocNumber, String preInboundNo, String asnNumber) {
        List<InboundHeader> inboundHeader = getInboundHeader(refDocNumber, preInboundNo);
        if (inboundHeader != null) {
            // PREINBOUNDHEADER
            preInboundHeaderService.updateASN(asnNumber);

            // PREINBOUNDLINE
            preInboundLineService.updateASN(asnNumber);

            // STAGINGHEADER
            stagingHeaderService.updateASN(asnNumber);

            // STAGINGLINE
            stagingLineService.updateASN(asnNumber);

            // GRHEADER
            grHeaderService.updateASN(asnNumber);

            // GRLINE
            grLineService.updateASN(asnNumber);

            //PUTAWAYHEADER
            putAwayHeaderService.updateASN(asnNumber);

            //PUTAWAYLINE
            putAwayLineService.updateASN(asnNumber);

            // INBOUNDHEADER
            updateASN(asnNumber);

            // INBOUNDLINE
            inboundLineService.updateASN(asnNumber);
            return Boolean.TRUE;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     * @param updateInboundHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundHeader updateInboundHeader(String warehouseId, String refDocNumber, String preInboundNo,
                                             String loginUserID, UpdateInboundHeader updateInboundHeader)
            throws IllegalAccessException, InvocationTargetException {
        InboundHeader dbInboundHeader = getInboundHeaderByEntity(warehouseId, refDocNumber, preInboundNo);
        BeanUtils.copyProperties(updateInboundHeader, dbInboundHeader, CommonUtils.getNullPropertyNames(updateInboundHeader));
        dbInboundHeader.setUpdatedBy(loginUserID);
        dbInboundHeader.setUpdatedOn(new Date());
        return inboundHeaderRepository.save(dbInboundHeader);
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public AXApiResponse updateInboundHeaderConfirm(String warehouseId, String preInboundNo, String refDocNumber, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
//		List<InboundLine> dbInboundLines = inboundLineService.getInboundLine (warehouseId, refDocNumber, preInboundNo);
        List<Boolean> validationStatusList = new ArrayList<>();

        // PutawayLine Validation
        long putAwayLineStatusIdCount = putAwayLineService.getPutAwayLineByStatusId(warehouseId, preInboundNo, refDocNumber);
        log.info("PutAwayLine status----> : " + putAwayLineStatusIdCount);

        if (putAwayLineStatusIdCount == 0) {
            throw new BadRequestException("Error on Inbound Confirmation: PutAwayLines are NOT processed completely.");
        }
        validationStatusList.add(true);

        // PutawayHeader Validation
        long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusId(warehouseId, preInboundNo, refDocNumber);
        log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

        if (putAwayHeaderStatusIdCount == 0) {
            throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely.");
        }
        validationStatusList.add(true);

        // StagingLine Validation
        long stagingLineStatusIdCount = stagingLineService.getStagingLineByStatusId(warehouseId, preInboundNo, refDocNumber);
        log.info("stagingLineStatusIdCount status----> : " + stagingLineStatusIdCount);

        if (stagingLineStatusIdCount == 0) {
            throw new BadRequestException("Error on Inbound Confirmation: StagingLine are NOT processed completely.");
        }
        validationStatusList.add(true);

        boolean sendConfirmationToAX = false;
        for (boolean isConditionMet : validationStatusList) {
            if (isConditionMet) {
                sendConfirmationToAX = true;
            }
        }

        log.info("sendConfirmationToAX ----> : " + sendConfirmationToAX);
        if (!sendConfirmationToAX) {
            throw new BadRequestException("Order is NOT completely processed for OrderNumber : " + refDocNumber);
        }

        // Checking relevant tables for sending confirmation to AX
//		for (InboundLine dbInboundLine : dbInboundLines) {
//			long matchedCount = 0;
//			List<Boolean> validationStatusList = new ArrayList<>();

//			List<PutAwayLine> putAwayLineList = 
//				putAwayLineService.getPutAwayLine(warehouseId, preInboundNo, refDocNumber, dbInboundLine.getLineNo(), 
//						dbInboundLine.getItemCode());
//			List<Long> paStatusList = putAwayLineList.stream().map(PutAwayLine::getStatusId).collect(Collectors.toList());
//			matchedCount = paStatusList.stream().filter(a -> a == 20L || a == 22L).count();
//			boolean isConditionMet = (matchedCount == paStatusList.size());
//			log.info("PutAwayLine status condition check : " + isConditionMet);
//			
//			if (!isConditionMet) {
//				throw new BadRequestException("Error on Inbound Confirmation: PutAwayLines are NOT processed completely.");
//			}

        /*
         * Pass WH_ID/PRE_IB_NO/REF_DOC_NO values in PUTAWAYHEADER table and
         * Validate STATUS_ID of all the values = 20 or 22
         */
//			List<PutAwayHeader> putAwayHeaderList = putAwayHeaderService.getPutAwayHeader(warehouseId, preInboundNo, refDocNumber);
//			List<Long> paheaderStatusList = putAwayHeaderList.stream().map(PutAwayHeader::getStatusId).collect(Collectors.toList());
//			matchedCount = paheaderStatusList.stream().filter(a -> a == 20L || a == 22L).count();
//			boolean isConditionMet = (matchedCount == paheaderStatusList.size());
//			log.info("PutAwayHeader status condition check : " + isConditionMet);
//			
//			if (!isConditionMet) {
//				throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeaders are NOT processed completely.");
//			}
//			
//			validationStatusList.add(isConditionMet);
//			log.info("PutAwayHeader status----> : " + paheaderStatusList);

        /*
         * -----------StagingLine Validation---------------------------
         * Validate StagingLine whether statusId is 17 OR 14
         */
//			List<StagingLineEntity> stagingLineList = stagingLineService.getStagingLine(warehouseId, refDocNumber, preInboundNo, dbInboundLine.getLineNo(), dbInboundLine.getItemCode());
//			List<Long> stagingLineStatusList = stagingLineList.stream().map(StagingLineEntity::getStatusId).collect(Collectors.toList());
//			matchedCount = stagingLineStatusList.stream().filter(a -> a == 14L || a == 17L).count();
//			boolean isConditionMet = (matchedCount <= stagingLineStatusList.size());
//			log.info("StagingLine status condition check : " + isConditionMet);
//			
//			if (!isConditionMet) {
//				throw new BadRequestException("Error on Inbound Confirmation: StagingLines are NOT processed completely.");
//			}
//			
//			validationStatusList.add(isConditionMet);
//			log.info("StagingLine status----> : " + stagingLineStatusList);

//			long conditionCount = validationStatusList.stream().filter(b -> b == true).count();
//			log.info("conditionCount : " + conditionCount);
//			log.info("conditionCount ----> : " + (conditionCount == validationStatusList.size()));
//			
//			if (conditionCount == validationStatusList.size() && dbInboundLine.getStatusId() == 20) {
//				sendConfirmationToAX = true;
//			} else {
//				throw new BadRequestException("Order is NOT completely processed : " + conditionCount + "," + dbInboundLine.getStatusId());
//			}
//		}

        /*
         * -----------Send Confirmation details to MS Dynamics through API-----------------------
         * Based on IB_ORD_TYP_ID, call Corresponding End points as per API document and
         * send the confirmation details to MS Dynamics via API integration as below
         * Once the response is 200 then, we need to update inboundline and header table with StatusId = 24.
         * */
        AXApiResponse axapiResponse = null;
        if (sendConfirmationToAX) {
            try {
                InboundHeader confirmedInboundHeader = getInboundHeaderByEntity(warehouseId, refDocNumber, preInboundNo);
                List<InboundLine> confirmedInboundLines =
                        inboundLineService.getInboundLinebyRefDocNoISNULL(confirmedInboundHeader.getWarehouseId(),
                                                                          confirmedInboundHeader.getRefDocNumber(), confirmedInboundHeader.getPreInboundNo());

                log.info("Order type id: " + confirmedInboundHeader.getInboundOrderTypeId());
                // If IB_ORD_TYP_ID = 1, call ASN API
                if (confirmedInboundHeader.getInboundOrderTypeId() == 1L) {
//                    axapiResponse = postASN(confirmedInboundHeader, confirmedInboundLines);
                    log.info("AXApiResponse: " + axapiResponse);
                }

                // If IB_ORD_TYP_ID = 2, call StoreReturns API
                if (confirmedInboundHeader.getInboundOrderTypeId() == 2L) {
//                    axapiResponse = postStoreReturn(confirmedInboundHeader, confirmedInboundLines);
                    log.info("AXApiResponse: " + axapiResponse);
                }

                // If IB_ORD_TYP_ID = 3, call InterWarehouse Receipt Confirmation API
                if (confirmedInboundHeader.getInboundOrderTypeId() == 3L) {
//                    axapiResponse = postInterWarehouse(confirmedInboundHeader, confirmedInboundLines);
                    log.info("AXApiResponse: " + axapiResponse);
                }

                // If IB_ORD_TYP_ID = 4, call Sale Order Returns API
                if (confirmedInboundHeader.getInboundOrderTypeId() == 4L) {
//                    axapiResponse = postSOReturn(confirmedInboundHeader, confirmedInboundLines);
                    log.info("AXApiResponse: " + axapiResponse);
                }
            } catch (Exception e) {
                log.error("AXApiResponse error: " + e.toString());
                e.printStackTrace();
            }
        }

        // If AX throws any error then, return the Error response
        if (axapiResponse != null && axapiResponse.getStatusCode() != null && !axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
            String errorFromAXAPI = axapiResponse.getMessage();
            AXApiResponse axapiErrorResponse = new AXApiResponse();
            axapiErrorResponse.setStatusCode("400");
            axapiErrorResponse.setMessage("Error from AX: " + errorFromAXAPI);
            return axapiErrorResponse;
        }

        if (axapiResponse != null && axapiResponse.getStatusCode() != null &&
                axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
            inboundLineRepository.updateInboundLineStatus(warehouseId, refDocNumber, 24L, loginUserID, new Date());
            log.info("InboundLine updated");

            inboundHeaderRepository.updateInboundHeaderStatus(warehouseId, refDocNumber, 24L, loginUserID, new Date());
            log.info("InboundHeader updated");

            preInboundHeaderRepository.updatePreInboundHeaderEntityStatus(warehouseId, refDocNumber, 24L);
            log.info("PreInboundHeader updated");

            preInboundLineRepository.updatePreInboundLineStatus(warehouseId, refDocNumber, 24L);
            log.info("PreInboundLine updated");

            grHeaderRepository.updateGrHeaderStatus(warehouseId, refDocNumber, 24L);
            log.info("grHeader updated");

            stagingHeaderRepository.updateStagingHeaderStatus(warehouseId, refDocNumber, 24L);
            log.info("stagingHeader updated");

        }

        // Update relevant tables once AXResponse is success
//		Long statusId = 24L;
//		for (InboundLine dbInboundLine : dbInboundLines) {
//			// Checking the AX-API response
//			if (axapiResponse != null && axapiResponse.getStatusCode() != null && 
//					axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
        // Checking the status of Line record whether Status = 20
//				try {
//					BeanUtils.copyProperties(dbInboundLine, dbInboundLine, CommonUtils.getNullPropertyNames(dbInboundLine));
//					dbInboundLine.setStatusId(statusId);
//					dbInboundLine.setConfirmedBy(loginUserID);
//					dbInboundLine.setConfirmedOn(new Date());
//					dbInboundLine.setUpdatedBy(loginUserID);
//					dbInboundLine.setUpdatedOn(new Date());
//					dbInboundLine = inboundLineRepository.save(dbInboundLine);
//					log.info("dbInboundLine updated : " + dbInboundLine);
//				} catch (Exception e1) {
//					log.error("InboundLine update error: " + e1.toString());
//					e1.printStackTrace();
//				}

//				try {
//					// Inbound Header Update
//					InboundHeader dbInboundHeader = getInboundHeaderByEntity(warehouseId, refDocNumber, preInboundNo);
//					dbInboundHeader.setStatusId(statusId);
//					dbInboundHeader.setUpdatedBy(loginUserID);
//					dbInboundHeader.setUpdatedOn(new Date());
//					dbInboundHeader.setConfirmedBy(loginUserID);
//					dbInboundHeader.setConfirmedOn(new Date());
//					dbInboundHeader = inboundHeaderRepository.save(dbInboundHeader);
//					log.info("InboundHeader updated : " + dbInboundHeader);
//				} catch (Exception e1) {
//					log.info("InboundHeader update error: " + dbInboundLine);
//					e1.printStackTrace();
//				}

        // PREINBOUND table updates
//				try {
//					PreInboundHeader preInboundHeader = preInboundHeaderService.updatePreInboundHeader(preInboundNo, warehouseId, 
//							refDocNumber, statusId, loginUserID);
//					log.info("PreInboundHeader updated : " + preInboundHeader);
//					
//					PreInboundLineEntity preInboundLine = preInboundLineService.updatePreInboundLine(preInboundNo, warehouseId, 
//							refDocNumber, dbInboundLine.getLineNo(), dbInboundLine.getItemCode(), statusId, loginUserID);
//					log.info("PreInboundLine updated : " + preInboundLine);
//				} catch (Exception e1) {
//					log.error("PreInboundHeader & line update error: " + e1.toString());
//					e1.printStackTrace();
//				}	

//				try {
//					// GRHEADER table updates
//					grHeaderService.updateGrHeader(warehouseId, preInboundNo, refDocNumber, dbInboundLine.getLineNo(), 
//							dbInboundLine.getItemCode(), statusId, loginUserID);
//					log.info("grHeaderService updated : ");
//				} catch (Exception e) {
//					log.error("grHeaderService update error: " + e.getLocalizedMessage());
//					e.printStackTrace();
//				}	

//				try {
//					// STAGINGHEADER table updates
//					stagingHeaderService.updateStagingHeader(warehouseId, preInboundNo, refDocNumber, dbInboundLine.getLineNo(), 
//							dbInboundLine.getItemCode(), statusId, loginUserID);
//					log.info("stagingHeaderService updated : ");
//				} catch (Exception e) {
//					log.error("stagingHeaderService update error: " + e.getLocalizedMessage());
//					e.printStackTrace();
//				}
//			}
//		}
        return axapiResponse;
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<InboundHeader> inboundHeaders = getInboundHeaders();
        inboundHeaders.forEach(p -> p.setReferenceField1(asnNumber));
        inboundHeaderRepository.saveAll(inboundHeaders);
    }

    /**
     * deleteInboundHeader
     * @param loginUserID
     * @param refDocNumber
     */
    public void deleteInboundHeader(String warehouseId, String refDocNumber, String preInboundNo, String loginUserID) {
        InboundHeader containerReceipt = getInboundHeaderByEntity(warehouseId, refDocNumber, preInboundNo);
        if (containerReceipt != null) {
            containerReceipt.setDeletionIndicator(1L);
            containerReceipt.setUpdatedBy(loginUserID);
            inboundHeaderRepository.save(containerReceipt);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
        }
    }

    /**
     * getInboundHeader
     * @param refDocNumber
     * @return
     */
    public InboundHeaderEntityV2 getInboundHeaderV2(String companyCode, String plantId, String languageId,
                                                    String warehouseId, String refDocNumber, String preInboundNo) {
        Optional<InboundHeaderV2> optInboundHeader =
                inboundHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        if (optInboundHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  " doesn't exist.");
        }
        InboundHeaderV2 inboundHeader = optInboundHeader.get();

        List<InboundLineV2> inboundLineList = inboundLineService.getInboundLineV2(companyCode,
                                                                                  plantId,
                                                                                  languageId,
                                                                                  warehouseId,
                                                                                  refDocNumber,
                                                                                  preInboundNo);
        log.info("inboundLineList found: " + inboundLineList);

        InboundHeaderEntityV2 inboundHeaderEntity = new InboundHeaderEntityV2();
        BeanUtils.copyProperties(inboundHeader, inboundHeaderEntity, CommonUtils.getNullPropertyNames(inboundHeader));
        inboundHeaderEntity.setInboundLine(inboundLineList);

        return inboundHeaderEntity;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public InboundHeaderV2 getInboundHeaderByEntityV2(String companyCode, String plantId, String languageId,
                                                      String warehouseId, String refDocNumber, String preInboundNo) {
        Optional<InboundHeaderV2> optInboundHeader =
                inboundHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        if (optInboundHeader.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                                                  ",refDocNumber: " + refDocNumber +
                                                  ",preInboundNo: " + preInboundNo +
                                                  " doesn't exist.");
        }
        return optInboundHeader.get();
    }

    /**
     * @param searchInboundHeader
     * @return
     * @throws Exception
     */
    public List<InboundHeaderV2> findInboundHeaderV2(SearchInboundHeaderV2 searchInboundHeader) throws Exception {
        if (searchInboundHeader.getStartCreatedOn() != null && searchInboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartCreatedOn(), searchInboundHeader.getEndCreatedOn());
            searchInboundHeader.setStartCreatedOn(dates[0]);
            searchInboundHeader.setEndCreatedOn(dates[1]);
        }

        if (searchInboundHeader.getStartConfirmedOn() != null && searchInboundHeader.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartConfirmedOn(), searchInboundHeader.getEndConfirmedOn());
            searchInboundHeader.setStartConfirmedOn(dates[0]);
            searchInboundHeader.setEndConfirmedOn(dates[1]);
        }
        InboundHeaderV2Specification spec = new InboundHeaderV2Specification(searchInboundHeader);
        List<InboundHeaderV2> results = inboundHeaderV2Repository.findAll(spec);

        List<InboundHeaderV2> inboundHeaderV2List = new ArrayList<>();
        for (InboundHeaderV2 dbInboundHeaderV2 : results) {

//            Long countOfOrderLines = inboundHeaderV2Repository.getCountOfTheOrderLinesByRefDocNumber(dbInboundHeaderV2.getRefDocNumber());
            Long countOfOrderLines = inboundHeaderV2Repository.getCountOfTheOrderLinesByRefDocNumber(dbInboundHeaderV2.getRefDocNumber(),
                                                                                                     dbInboundHeaderV2.getCompanyCode(), dbInboundHeaderV2.getPreInboundNo(), dbInboundHeaderV2.getPlantId(),
                                                                                                     dbInboundHeaderV2.getLanguageId(), dbInboundHeaderV2.getWarehouseId());
            dbInboundHeaderV2.setCountOfOrderLines(countOfOrderLines);

//            Long countOfReceivedLines = inboundHeaderV2Repository.getReceivedLinesByRefDocNumber(dbInboundHeaderV2.getRefDocNumber());
            Long countOfReceivedLines = inboundHeaderV2Repository.getReceivedLinesByRefDocNumber(dbInboundHeaderV2.getRefDocNumber(),
                                                                                                 dbInboundHeaderV2.getCompanyCode(), dbInboundHeaderV2.getPreInboundNo(), dbInboundHeaderV2.getPlantId(),
                                                                                                 dbInboundHeaderV2.getLanguageId(), dbInboundHeaderV2.getWarehouseId());
            dbInboundHeaderV2.setReceivedLines(countOfReceivedLines);

            inboundHeaderV2List.add(dbInboundHeaderV2);
        }
        return inboundHeaderV2List;
    }

    /**
     * @param searchInboundHeader
     * @return
     * @throws Exception
     */
    public Stream<InboundHeaderV2> findInboundHeaderStreamV2(SearchInboundHeaderV2 searchInboundHeader) throws Exception {
        if (searchInboundHeader.getStartCreatedOn() != null && searchInboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartCreatedOn(), searchInboundHeader.getEndCreatedOn());
            searchInboundHeader.setStartCreatedOn(dates[0]);
            searchInboundHeader.setEndCreatedOn(dates[1]);
        }

        if (searchInboundHeader.getStartConfirmedOn() != null && searchInboundHeader.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchInboundHeader.getStartConfirmedOn(), searchInboundHeader.getEndConfirmedOn());
            searchInboundHeader.setStartConfirmedOn(dates[0]);
            searchInboundHeader.setEndConfirmedOn(dates[1]);
        }
        InboundHeaderV2Specification spec = new InboundHeaderV2Specification(searchInboundHeader);
        Stream<InboundHeaderV2> results = inboundHeaderV2Repository.stream(spec, InboundHeaderV2.class);
        return results;
    }


    /**
     * @return
     */
    public List<InboundHeaderV2> getInboundHeadersV2() {
        List<InboundHeaderV2> containerReceiptList = inboundHeaderV2Repository.findAll();
        containerReceiptList = containerReceiptList
                .stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return containerReceiptList;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     * @param updateInboundHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundHeaderV2 updateInboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber, String preInboundNo,
                                                 String loginUserID, InboundHeader updateInboundHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Optional<InboundHeaderV2> optInboundHeader =
                inboundHeaderV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        0L);
        InboundHeaderV2 dbInboundHeader = optInboundHeader.get();
        BeanUtils.copyProperties(updateInboundHeader, dbInboundHeader, CommonUtils.getNullPropertyNames(updateInboundHeader));
        dbInboundHeader.setUpdatedBy(loginUserID);
        dbInboundHeader.setUpdatedOn(new Date());
        return inboundHeaderV2Repository.save(dbInboundHeader);
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     */
    public void deleteInboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId,
                                      String refDocNumber, String preInboundNo, String loginUserID) throws ParseException {
        InboundHeaderV2 inboundHeader = getInboundHeaderByEntityV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
        if (inboundHeader != null) {
            inboundHeader.setDeletionIndicator(1L);
            inboundHeader.setUpdatedBy(loginUserID);
            inboundHeader.setUpdatedOn(new Date());
            inboundHeaderV2Repository.save(inboundHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + refDocNumber);
        }
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<InboundHeaderV2> inboundHeaders = getInboundHeadersV2();
        inboundHeaders.forEach(p -> p.setReferenceField1(asnNumber));
        inboundHeaderV2Repository.saveAll(inboundHeaders);
    }

    /**
     * @param refDocNumber
     * @param preInboundNo
     * @param asnNumber
     * @return
     */
    public Boolean replaceASNV2(String refDocNumber, String preInboundNo, String asnNumber) {
        List<InboundHeader> inboundHeader = getInboundHeader(refDocNumber, preInboundNo);
        if (inboundHeader != null) {
            // PREINBOUNDHEADER
            preInboundHeaderService.updateASNV2(asnNumber);

            // PREINBOUNDLINE
            preInboundLineService.updateASNV2(asnNumber);

            // STAGINGHEADER
            stagingHeaderService.updateASNV2(asnNumber);

            // STAGINGLINE
            stagingLineService.updateASNV2(asnNumber);

            // GRHEADER
            grHeaderService.updateASNV2(asnNumber);

            // GRLINE
            grLineService.updateASNV2(asnNumber);

            // PUTAWAYHEADER
            putAwayHeaderService.updateASNV2(asnNumber);

            // PUTAWAYLINE
            putAwayLineService.updateASNV2(asnNumber);

            // INBOUNDHEADER
            updateASNV2(asnNumber);

            // INBOUNDLINE
            inboundLineService.updateASNV2(asnNumber);
            return Boolean.TRUE;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Transactional
    public AXApiResponse updateInboundHeaderConfirmV2(String companyCode, String plantId, String languageId,
                                                      String warehouseId, String preInboundNo, String refDocNumber, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
//		List<InboundLine> dbInboundLines = inboundLineService.getInboundLine (warehouseId, refDocNumber, preInboundNo);
        List<Boolean> validationStatusList = new ArrayList<>();

        // PutawayLine Validation
//        long putAwayLineStatusIdCount = putAwayLineService.getPutAwayLineByStatusIdV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
//        log.info("PutAwayLine status----> : " + putAwayLineStatusIdCount);
//
//        if (putAwayLineStatusIdCount == 0) {
//            throw new BadRequestException("Error on Inbound Confirmation: PutAwayLines are NOT processed completely.");
//        }
//        validationStatusList.add(true);

        // PutawayHeader Validation
        long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
//        long putAwayHeaderConfirmCount = putAwayHeaderService.getPutawayHeaderForInboundConfirmV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
//        log.info("PutAwayHeaderConfirm status----> : " + putAwayHeaderConfirmCount);
        log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

        if (putAwayHeaderStatusIdCount != 0) {
            throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely.");
        }
        validationStatusList.add(true);

        // StagingLine Validation  ---> Validation commented since it is created automatically while inbound process so user cannot access it
//        long stagingLineStatusIdCount = stagingLineService.getStagingLineByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
//        log.info("stagingLineStatusIdCount status----> : " + stagingLineStatusIdCount);

//        if (stagingLineStatusIdCount == 0) {
//            throw new BadRequestException("Error on Inbound Confirmation: StagingLine are NOT processed completely.");
//        }
//        validationStatusList.add(true);

        boolean sendConfirmationToAX = false;
        for (boolean isConditionMet : validationStatusList) {
            if (isConditionMet) {
                sendConfirmationToAX = true;
            }
        }

        log.info("sendConfirmationToAX ----> : " + sendConfirmationToAX);
        if (!sendConfirmationToAX) {
            throw new BadRequestException("Order is NOT completely processed for OrderNumber : " + refDocNumber);
        }


//        /*
//         * -----------Send Confirmation details to MS Dynamics through API-----------------------
//         * Based on IB_ORD_TYP_ID, call Corresponding End points as per API document and
//         * send the confirmation details to MS Dynamics via API integration as below
//         * Once the response is 200 then, we need to update inboundline and header table with StatusId = 24.
//         * */
        AXApiResponse axapiResponse = new AXApiResponse();

        // Create Inventory
//        List<InventoryV2> inventoryConfirmList =
//                inventoryService.getInventoryForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber, 1L);

//        double sumOfPutAwayLineQty =
//                putAwayLineService.getSumOfPutawayLineQtyByStatusId20(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
//        log.info("PutAwayLine status----> : " + sumOfPutAwayLineQty);

//        inventoryConfirmList.stream().forEach(dbInventory -> {
//            InventoryV2 newInventory = new InventoryV2();
//            newInventory.setInventoryId(System.currentTimeMillis());
//            BeanUtils.copyProperties(dbInventory, newInventory, CommonUtils.getNullPropertyNames(dbInventory));
//
//            newInventory.setStockTypeId(1L);
//            String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
//            dbInventory.setStockTypeDescription(stockTypeDesc);
//
//            double sumOfPALineQty = dbInventory.getInventoryQuantity() + sumOfPutAwayLineQty;
//            newInventory.setInventoryQuantity(sumOfPALineQty);
//
//            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventory);
//            log.info("Inventory created: " + createdInventoryV2);
//        });

        List<InboundLineV2> inboundLineList = inboundLineService.getInboundLineForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);

        if (inboundLineList != null) {
            for (InboundLineV2 inboundLine : inboundLineList) {
                List<GrLineV2> grLineList = grLineService.getGrLineForInboundConformV2(
                        companyCode, plantId, languageId, warehouseId, refDocNumber,
                        inboundLine.getItemCode(),
                        inboundLine.getManufacturerName(),
                        inboundLine.getLineNo(),
                        inboundLine.getPreInboundNo());
                log.info("GrLine List: " + grLineList);
                for (GrLineV2 grLine : grLineList) {
                    List<PutAwayLineV2> putAwayLineList = putAwayLineService.
                            getPutAwayLineForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                                              grLine.getItemCode(),
                                                              grLine.getManufacturerName(),
                                                              grLine.getLineNo(),
                                                              grLine.getPreInboundNo(),
                                                              grLine.getPackBarcodes());
                    if (putAwayLineList != null) {
                        for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                            InventoryV2 createdInventory = createInventoryV2(putAwayLine, grLine.getQuantityType());
                            createInventoryMovementV2(putAwayLine);
                            log.info("All Inbound Line --> Inventory Created Successfully");
                        }
                    }
                }
            }
        }

        List<InboundLineV2> inboundLineV2List = inboundLineService.getInboundLineForInboundConfirmWithStatusIdV2(companyCode, plantId, languageId, warehouseId, refDocNumber);
        statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);
        inboundLineV2Repository.updateInboundLineStatus(warehouseId, companyCode, plantId, languageId, refDocNumber, 24L, statusDescription, loginUserID, new Date());
        log.info("InboundLine updated");

        inboundHeaderV2Repository.updateInboundHeaderStatus(warehouseId, companyCode, plantId, languageId, refDocNumber, 24L, statusDescription, loginUserID, new Date());
        log.info("InboundHeader updated");

        preInboundHeaderV2Repository.updatePreInboundHeaderEntityStatus(warehouseId, companyCode, plantId, languageId, refDocNumber, 24L, statusDescription);
        log.info("PreInboundHeader updated");

        if (inboundLineV2List != null && !inboundLineV2List.isEmpty()) {
            for (InboundLineV2 inboundLineV2 : inboundLineV2List) {
                preInboundLineV2Repository.updatePreInboundLineStatus(warehouseId, companyCode, plantId, languageId,
                                                                      refDocNumber, 24L, statusDescription, inboundLineV2.getItemCode(), inboundLineV2.getManufacturerName(), inboundLineV2.getLineNo());
                log.info("PreInboundLine updated");
            }
        }

        grHeaderV2Repository.updateGrHeaderStatus(warehouseId, companyCode, plantId, languageId, refDocNumber, 24L, statusDescription);
        log.info("grHeader updated");

        stagingHeaderV2Repository.updateStagingHeaderStatus(warehouseId, companyCode, plantId, languageId, refDocNumber, 24L, statusDescription);
        log.info("stagingHeader updated");

        putAwayLineV2Repository.updatePutawayLineStatus(warehouseId, companyCode, plantId, languageId, refDocNumber, 24L, statusDescription);
        log.info("putAwayLine updated");

        putAwayHeaderV2Repository.updatePutAwayHeaderStatus(warehouseId, companyCode, plantId, languageId, refDocNumber, 24L, statusDescription);
        log.info("PutAwayHeader Updated");

        axapiResponse.setStatusCode("200");                         //HardCode for Testing
        axapiResponse.setMessage("Success");                        //HardCode for Testing
        log.info("axapiResponse: " + axapiResponse);
        return axapiResponse;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     */
    @Transactional
    public AXApiResponse updateInboundHeaderPartialConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                             String preInboundNo, String refDocNumber, String loginUserID) {
        try {
            log.info("DSR/Auto--->Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
            // PutawayHeader Validation
            long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
            log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

            if (putAwayHeaderStatusIdCount != 0) {
                throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: " + refDocNumber);
            }

            AXApiResponse axapiResponse = new AXApiResponse();
            List<InboundLineV2> inboundLineList = inboundLineService.getInboundLineForInboundConfirmPartialAllocationV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);

            if (inboundLineList != null) {
                for (InboundLineV2 inboundLine : inboundLineList) {
                    List<GrLineV2> grLineList = grLineService.getGrLineForInboundConformV2(
                            companyCode, plantId, languageId, warehouseId, refDocNumber,
                            inboundLine.getItemCode(),
                            inboundLine.getManufacturerName(),
                            inboundLine.getLineNo(),
                            inboundLine.getPreInboundNo());
                    log.info("GrLine List: " + grLineList.size());
                    for (GrLineV2 grLine : grLineList) {
                        List<PutAwayLineV2> putAwayLineList = putAwayLineService.
                                getPutAwayLineForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                                                  grLine.getItemCode(),
                                                                  grLine.getManufacturerName(),
                                                                  grLine.getLineNo(),
                                                                  grLine.getPreInboundNo(),
                                                                  grLine.getPackBarcodes());
                        log.info("PutawayLine List: " + putAwayLineList.size());
                        if (putAwayLineList != null) {
                            for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                                InventoryV2 createdInventory = createInventoryNonCBMV2(putAwayLine);
                                createInventoryMovementV2(putAwayLine);
                            }
                            log.info("Inventory Created Successfully -----> for All Putaway Lines");
                        }
                    }
                }
            }

            statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);

            inboundLineV2Repository.updateInboundLineStatusUpdateInboundConfirmProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
            log.info("InboundLine updated");

            putAwayLineV2Repository.updatePutawayLineStatusUpdateInboundConfirmProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
            log.info("putAwayLine updated");

            String statusDescription17 = stagingLineV2Repository.getStatusDescription(17L, languageId);
            //Multiple Stored Procedure replaced with Single Procedure Call
            inboundHeaderV2Repository.updatePahGrlStglPiblStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                                                                                        24L, 17L, statusDescription, statusDescription17, loginUserID, new Date());
            log.info("PutawayHeader, GrLine, Stg Line, PreIbLine Status updated using stored procedure");

            Long inboundLinesV2CountForInboundConfirmWithStatusId = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirmWithStatusId(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L);
            Long inboundLinesV2CountForInboundConfirm = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirm(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            if (inboundLinesV2CountForInboundConfirmWithStatusId == null) {
                inboundLinesV2CountForInboundConfirmWithStatusId = 0L;
            }
            if (inboundLinesV2CountForInboundConfirm == null) {
                inboundLinesV2CountForInboundConfirm = 0L;
            }
            boolean isConditionMet = inboundLinesV2CountForInboundConfirmWithStatusId.equals(inboundLinesV2CountForInboundConfirm);
            log.info("Inbound Line 24_StatusCount, Line Count: " + isConditionMet + ", " + inboundLinesV2CountForInboundConfirmWithStatusId + ", " + inboundLinesV2CountForInboundConfirm);
            if (isConditionMet) {
                inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
                log.info("Header Status updated using stored procedure");
            }

            axapiResponse.setStatusCode("200");                         //HardCoded
            axapiResponse.setMessage("Success");                        //HardCoded
            log.info("axapiResponse: " + axapiResponse);
            return axapiResponse;
        } catch (Exception e) {
            throw new BadRequestException("Inbound confirmation [DSR]: Exception ----> " + e.toString());
        }
    }

    @Transactional
    public AXApiResponse updateInboundHeaderPartialConfirmNewV2(List<InboundLineV2> inboundLineList, String companyCode, String plantId, String languageId,
                                                                String warehouseId, String preInboundNo, String refDocNumber, String loginUserID) {
        try {
            log.info("Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
            // PutawayHeader Validation
            long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
            log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

            if (putAwayHeaderStatusIdCount != 0) {
                throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: " + refDocNumber);
            }

            AXApiResponse axapiResponse = new AXApiResponse();
            statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);
            log.info("InboundLine List: " + inboundLineList.size());
            List<PutAwayLineV2> putAwayLineList = null;
            if (inboundLineList != null && !inboundLineList.isEmpty()) {
                for (InboundLineV2 inboundLine : inboundLineList) {
                    log.info("Input InboundLine : " + inboundLine);
                    if (inboundLine.getStatusId() == 20L) {
                        InboundLineV2 inboundLineV2 = inboundLineService.getInboundLineForInboundConfirmPartialConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                                                                                                         preInboundNo, inboundLine.getItemCode(),
                                                                                                                         inboundLine.getManufacturerName(), inboundLine.getLineNo());
                        if (inboundLineV2 != null) {
                            putAwayLineList = putAwayLineService.
                                    getPutAwayLineForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                                                      inboundLine.getItemCode(), inboundLine.getManufacturerName(),
                                                                      inboundLine.getLineNo(), preInboundNo);
                            log.info("PutawayLine List: " + putAwayLineList.size());
                            if (putAwayLineList != null) {
                                for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                                    InventoryV2 createdInventory = createInventoryNonCBMV2(putAwayLine);
                                    createInventoryMovementV2(putAwayLine);
                                }
                                log.info("Inventory Created Successfully -----> for Inbound Line ----> " +
                                                 inboundLine.getItemCode() + ", " + inboundLine.getManufacturerName() + ", " + inboundLine.getLineNo());
                            }
                        }
                    }
                    inboundLineV2Repository.updateInboundLineStatusUpdateInboundConfirmIndividualItemProc(
                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                            inboundLine.getItemCode(), inboundLine.getManufacturerName(), inboundLine.getLineNo(), 24L, statusDescription, loginUserID, new Date());
                    log.info("InboundLine status updated: " + inboundLine.getItemCode() + ", " + inboundLine.getManufacturerName() + ", " + inboundLine.getLineNo());

                    putAwayLineV2Repository.updatePutawayLineStatusUpdateInboundConfirmProc(
                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
                    log.info("putAwayLine updated");
                }
            }

            String statusDescription17 = stagingLineV2Repository.getStatusDescription(17L, languageId);
            //Multiple Stored Procedure replaced with Single Procedure Call
            inboundHeaderV2Repository.updatePahGrlStglPiblStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                                                                                        24L, 17L, statusDescription, statusDescription17, loginUserID, new Date());
            log.info("PutawayHeader, GrLine, Stg Line, PreIbLine Status updated using stored procedure");

            Long inboundLinesV2CountForInboundConfirmWithStatusId = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirmWithStatusId(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L);
            Long inboundLinesV2CountForInboundConfirm = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirm(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            if (inboundLinesV2CountForInboundConfirmWithStatusId == null) {
                inboundLinesV2CountForInboundConfirmWithStatusId = 0L;
            }
            if (inboundLinesV2CountForInboundConfirm == null) {
                inboundLinesV2CountForInboundConfirm = 0L;
            }
            boolean isConditionMet = inboundLinesV2CountForInboundConfirmWithStatusId.equals(inboundLinesV2CountForInboundConfirm);
            log.info("Inbound Line 24_StatusCount, Line Count: " + refDocNumber + ", " + isConditionMet + ", " + inboundLinesV2CountForInboundConfirmWithStatusId + ", " + inboundLinesV2CountForInboundConfirm);
            if (isConditionMet) {
                inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
                log.info("Header Status updated using stored procedure");
            }

            axapiResponse.setStatusCode("200");                         //HardCoded
            axapiResponse.setMessage("Success");                        //HardCoded
            log.info("axapiResponse: " + axapiResponse);
            return axapiResponse;
        } catch (Exception e) {
            throw new BadRequestException("Inbound confirmation : Exception ----> " + e.toString());
        }
    }

    @Transactional
    public void updateInboundHeaderPartialConfirmFromPutAwayLineV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                   String preInboundNo, String refDocNumber, String loginUserID)
            throws Exception {

        log.info("Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
        // PutAwayHeader Validation
//        long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
//        log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);
//
//        if (putAwayHeaderStatusIdCount != 0) {
//            throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: "  + refDocNumber);
//        }
        List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLinesV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
        AXApiResponse axapiResponse = new AXApiResponse();
        statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);
        log.info("putAwayLineList List: " + putAwayLineList.size());
        Long ibOrderTypeId = null;
        String purchaseOrderNumber = null;
        String parentProductionOrderNo = null;
        if (putAwayLineList != null && !putAwayLineList.isEmpty()) {
            for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                InboundLineV2 inboundLineV2 = inboundLineService.getInboundLineForInboundConfirmPartialConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                                                                                                 putAwayLine.getPreInboundNo(), putAwayLine.getItemCode(), putAwayLine.getLineNo());
                if (inboundLineV2 != null) {

                    ibOrderTypeId = putAwayLine.getInboundOrderTypeId();
                    purchaseOrderNumber = putAwayLine.getPurchaseOrderNumber();
                    parentProductionOrderNo = putAwayLine.getParentProductionOrderNo();

//                    if (inboundLineV2.getInboundOrderTypeId().equals(IB_QH_ORD_TYP_ID)) {
                    createImfInventoryNonCBMV2(putAwayLine);
                    createInventoryMovementV2(putAwayLine);
//                    }
//                    boolean inboundOrderTypeIdPass = inboundLineV2.getInboundOrderTypeId() != null && (inboundLineV2.getInboundOrderTypeId().equals(IB_FG_ORD_TYP_ID) ||
//                            inboundLineV2.getInboundOrderTypeId().equals(IB_SFG_ORD_TYP_ID));
//                    if (inboundOrderTypeIdPass) {
//                        indusMegaFoodService.createInboundInventoryTransfer(putAwayLine, loginUserID);
//                    }
                    log.info("Inventory Created Successfully -----> for Putaway Line ----> " +
                                     putAwayLine.getItemCode() + ", " + putAwayLine.getManufacturerName() + ", " + putAwayLine.getLineNo());
                    inboundLineV2Repository.updateInboundLineStatusUpdateInboundConfirmIndividualItemProc(
                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                            putAwayLine.getItemCode(), putAwayLine.getManufacturerName(), putAwayLine.getLineNo(), 24L, statusDescription, loginUserID, new Date());
                    log.info("InboundLine status updated: " + putAwayLine.getItemCode() + ", " + putAwayLine.getManufacturerName() + ", " + putAwayLine.getLineNo());
                    putAwayLineV2Repository.updatePutawayLineStatusUpdateInboundConfirmProc(
                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
                    log.info("putAwayLine updated");
                }
            }
        }

        putAwayHeaderV2Repository.updatepaheaderStatusUpdateInboundConfirmProc(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
        log.info("PutAwayHeader Updated");

        preInboundLineV2Repository.updatePreInboundLineStatusUpdateInboundConfirmProc(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
        log.info("PreInboundLine updated");

        grLineV2Repository.updateGrLineStatusUpdateInboundConfirmProc(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
        log.info("GrLine updated");

        String statusDescription17 = stagingLineV2Repository.getStatusDescription(17L, languageId);
        stagingLineV2Repository.updateStagingLineStatusUpdateInboundConfirmProc(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 17L, statusDescription17, loginUserID, new Date());
        log.info("StagingLine updated");

        Long inboundLinesV2CountForInboundConfirmWithStatusId = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirmWithStatusId(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L);
        Long inboundLinesV2CountForInboundConfirm = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirm(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
        if (inboundLinesV2CountForInboundConfirmWithStatusId == null) {
            inboundLinesV2CountForInboundConfirmWithStatusId = 0L;
        }
        if (inboundLinesV2CountForInboundConfirm == null) {
            inboundLinesV2CountForInboundConfirm = 0L;
        }
        boolean isConditionMet = inboundLinesV2CountForInboundConfirmWithStatusId.equals(inboundLinesV2CountForInboundConfirm);
        log.info("Inbound Line 24_StatusCount, Line Count: " + refDocNumber + ", " + isConditionMet + ", " + inboundLinesV2CountForInboundConfirmWithStatusId + ", " + inboundLinesV2CountForInboundConfirm);
        if (isConditionMet) {
            inboundHeaderV2Repository.updateIbheaderStatusUpdateInboundConfirmProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
            log.info("InboundHeader updated");

            preInboundHeaderV2Repository.updatePreIbheaderStatusUpdateInboundConfirmProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
            log.info("PreInboundHeader updated");

            grHeaderV2Repository.updateGrheaderStatusUpdateInboundConfirmProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
            log.info("grHeader updated");

            stagingHeaderV2Repository.updateStagingheaderStatusUpdateInboundConfirmProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
            log.info("stagingHeader updated");

            log.info("C_Id, ib_ord_typ_id, pur_ord_no, par_prod_ord_no: " + companyCode + ", " + ibOrderTypeId + ", " + purchaseOrderNumber + ", " + parentProductionOrderNo);
            if (companyCode != null && companyCode.equalsIgnoreCase(COMPANY_CODE) &&
                    ibOrderTypeId != null && ibOrderTypeId.equals(IB_SFG_ORD_TYP_ID) &&
                    purchaseOrderNumber != null && parentProductionOrderNo != null) {
                log.info("Update FG Picklist ord Mmt Line: " + parentProductionOrderNo);
                List<OrderManagementLineV2> orderManagementLines = orderManagementLineService.
                        getOrderManagementLineForIMFV2(companyCode, plantId, languageId, warehouseId, parentProductionOrderNo);
                if (orderManagementLines != null && !orderManagementLines.isEmpty()) {
                    List<OrderManagementLineV2> filteredResult =
                            orderManagementLines.stream().filter(n -> n.getAllocatedQty() == null || !n.getAllocatedQty().equals(n.getOrderQty())).collect(Collectors.toList());
                    log.info("Filtered oml : " + filteredResult);
                    orderManagementLineService.doIMFUnAllocationV2(filteredResult, loginUserID);
                    if (filteredResult != null && !filteredResult.isEmpty()) {
                        List<OrderManagementLineV2> allocatedOml = orderManagementLineService.doAllocationV2(filteredResult, loginUserID);
//                        preOutboundHeaderService.createIMFPickUpHeaderAssignPicker(companyCode, plantId, languageId, warehouseId, allocatedOml, loginUserID);
                    }
                }
                String orderType = ibOrderTypeId.equals(IB_FG_ORD_TYP_ID) ? "FG" : "SFG";
//                mfgService.patchOperationConsumption(companyCode, plantId, languageId, warehouseId, refDocNumber, purchaseOrderNumber, parentProductionOrderNo, "updateConsumedQty");
                indusMegaFoodService.updateOperationConsumptionInventory(companyCode, plantId, languageId, warehouseId, purchaseOrderNumber, refDocNumber, parentProductionOrderNo, orderType);
            }
        }

        axapiResponse.setStatusCode("200");                         //HardCoded
        axapiResponse.setMessage("Success");                        //HardCoded
        log.info("axapiResponse: " + axapiResponse);
//        return axapiResponse;
    }

    /**
     * @param putAwayLine
     * @return
     */
    private InventoryV2 createInventoryV2(PutAwayLineV2 putAwayLine, String quantityType) {
        log.info("Create Inventory Initiated: " + new Date());
        String palletCode = null;
        String caseCode = null;
        try {
            InventoryV2 existinginventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                    putAwayLine.getCompanyCode(),
                    putAwayLine.getPlantId(),
                    putAwayLine.getLanguageId(),
                    putAwayLine.getWarehouseId(),
                    putAwayLine.getItemCode(),
                    putAwayLine.getManufacturerName(),
                    "99999", 3L, 0L);

            if (existinginventory != null) {
                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
                double INV_QTY = existinginventory.getInventoryQuantity() - putAwayLine.getPutawayConfirmedQty();
                log.info("INV_QTY : " + INV_QTY);

                if (INV_QTY >= 0) {

                    InventoryV2 inventory2 = new InventoryV2();
                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
                    String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
                                                            putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), existinginventory.getStockTypeId());
                    inventory2.setStockTypeDescription(stockTypeDesc);
                    inventory2.setInventoryQuantity(round(INV_QTY));
                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);

                    palletCode = existinginventory.getPalletCode();
                    caseCode = existinginventory.getCaseCode();
                    if (inventory2.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
                                                                    putAwayLine.getWarehouseId(), putAwayLine.getItemCode());
                        if (itemType != null) {
                            inventory2.setItemType(itemType.getItemType());
                            inventory2.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }

                    inventory2.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
                    inventory2.setUpdatedOn(new Date());
                    inventory2.setInventoryId(System.currentTimeMillis());
                    InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
                    log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Existing Inventory---Error-----> : " + e.toString());
        }

        try {
            log.info("Create Inventory bin Class Id 1 Initiated: " + new Date());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

            inventory.setCompanyCodeId(putAwayLine.getCompanyCode());

            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);                // VAR_ID
            inventory.setVariantSubCode("1");            // VAR_SUB_ID
            inventory.setStorageMethod("1");            // STR_MTD
            inventory.setBatchSerialNumber("1");        // STR_NO
            inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
            inventory.setBarcodeId(putAwayLine.getBarcodeId());
            inventory.setManufacturerName(putAwayLine.getManufacturerName());

//            Long binClassId = 0L;
//            if ((putAwayLine.getInboundOrderTypeId() == 1 ||
//                    putAwayLine.getInboundOrderTypeId() == 3 ||
//                    putAwayLine.getInboundOrderTypeId() == 4 ||
//                    putAwayLine.getInboundOrderTypeId() == 5) &&
//                    (quantityType.equalsIgnoreCase("A"))) {
//                binClassId = 1L;
//            }
//            if ((putAwayLine.getInboundOrderTypeId() == 1 ||
//                    putAwayLine.getInboundOrderTypeId() == 3 ||
//                    putAwayLine.getInboundOrderTypeId() == 4 ||
//                    putAwayLine.getInboundOrderTypeId() == 5) &&
//                    (quantityType.equalsIgnoreCase("D"))) {
//                binClassId = 7L;
//            }
//            if (putAwayLine.getInboundOrderTypeId() == 2) {
//                binClassId = 7L;
//            }

            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(putAwayLine.getCompanyCode());
            storageBinPutAway.setPlantId(putAwayLine.getPlantId());
            storageBinPutAway.setLanguageId(putAwayLine.getLanguageId());
            storageBinPutAway.setWarehouseId(putAwayLine.getWarehouseId());
            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());

            StorageBinV2 storageBin = null;
            try {
                storageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
            } catch (Exception e) {
                throw new BadRequestException("Invalid StorageBin");
            }
            log.info("storageBin: " + storageBin);

            ImBasicData imBasicData = new ImBasicData();
            imBasicData.setCompanyCodeId(putAwayLine.getCompanyCode());
            imBasicData.setPlantId(putAwayLine.getPlantId());
            imBasicData.setLanguageId(putAwayLine.getLanguageId());
            imBasicData.setWarehouseId(putAwayLine.getWarehouseId());
            imBasicData.setItemCode(putAwayLine.getItemCode());
            imBasicData.setManufacturerName(putAwayLine.getManufacturerName());
            ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
            log.info("ImbasicData1 : " + itemCodeCapacityCheck);

            if (itemCodeCapacityCheck != null) {
                inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
                inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
                inventory.setManufacturerCode(itemCodeCapacityCheck.getManufacturerPartNo());
                inventory.setDescription(itemCodeCapacityCheck.getDescription());
            }
            if (storageBin != null) {
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                inventory.setBinClassId(storageBin.getBinClassId());
            }

            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
            inventory.setPlantDescription(putAwayLine.getPlantDescription());
            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());

            inventory.setPalletCode(palletCode);
            inventory.setCaseCode(caseCode);
            log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), 1L);
            inventory.setStockTypeDescription(stockTypeDesc);
            log.info("StockTypeDescription: " + stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);

            InventoryV2 existingInventory = inventoryService.getInventoryForInhouseTransferV2(
                    putAwayLine.getCompanyCode(),
                    putAwayLine.getPlantId(),
                    putAwayLine.getLanguageId(),
                    putAwayLine.getWarehouseId(),
                    "99999",
                    putAwayLine.getItemCode(),
                    putAwayLine.getManufacturerName(),
                    putAwayLine.getConfirmedStorageBin()
            );

            Double ALLOC_QTY = 0D;
            if (existingInventory != null) {
                if (existingInventory.getAllocatedQuantity() != null) {
                    ALLOC_QTY = existingInventory.getAllocatedQuantity();
                    inventory.setAllocatedQuantity(round(ALLOC_QTY));
                }
                if (existingInventory.getAllocatedQuantity() == null) {
                    inventory.setAllocatedQuantity(round(ALLOC_QTY));
                }
                log.info("Inventory Allocated Qty: " + ALLOC_QTY);
            }
            // INV_QTY
            if (existingInventory != null) {
                inventory.setInventoryQuantity(round(existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty()));
                log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", " + putAwayLine.getPutawayConfirmedQty());
                Double totalQty = inventory.getInventoryQuantity() + inventory.getAllocatedQuantity();
                inventory.setReferenceField4(round(totalQty));
                log.info("Inventory Total Qty: " + totalQty);
            }
            if (existingInventory == null) {
                inventory.setInventoryQuantity(round(putAwayLine.getPutawayConfirmedQty()));
                log.info("Inventory Qty = pa_cnf_qty: " + putAwayLine.getPutawayConfirmedQty());
                Double totalQty = putAwayLine.getPutawayConfirmedQty() + ALLOC_QTY;
                inventory.setReferenceField4(round(totalQty));
                log.info("Inventory Total Qty: " + totalQty);
            }

            //packbarcode
            /*
             * Hardcoding Packbarcode as 99999
             */
//            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
            inventory.setPackBarcodes("99999");

            // INV_UOM
            if (putAwayLine.getPutAwayUom() != null) {
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                log.info("PA UOM: " + putAwayLine.getPutAwayUom());
            }
            inventory.setCreatedBy(putAwayLine.getCreatedBy());

            //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
            boolean capacityCheck = false;
            Double invQty = 0D;
            Double cbm = 0D;
            Double cbmPerQty = 0D;
            Double invCbm = 0D;
            if (itemCodeCapacityCheck != null) {
                if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                    log.info("CBM Check");
                    capacityCheck = itemCodeCapacityCheck.getCapacityCheck();               //Capacity Check for putaway item
                }
            }
            log.info("CapacityCheck -----------> : " + capacityCheck);

            if (capacityCheck) {
                if (putAwayLine.getCbmQuantity() != null) {
                    inventory.setCbmPerQuantity(String.valueOf(putAwayLine.getCbmQuantity()));
                }
                if (putAwayLine.getPutawayConfirmedQty() != null) {
                    invQty = putAwayLine.getPutawayConfirmedQty();
                }
                if (putAwayLine.getCbmQuantity() == null) {

                    if (putAwayLine.getCbm() != null) {
                        cbm = Double.valueOf(putAwayLine.getCbm());
                    }
                    cbmPerQty = cbm / invQty;
                    inventory.setCbmPerQuantity(String.valueOf(cbmPerQty));
                }
                if (putAwayLine.getCbm() != null) {
                    invCbm = Double.valueOf(putAwayLine.getCbm());
                }
                if (putAwayLine.getCbm() == null) {
                    invCbm = invQty * Double.valueOf(inventory.getCbmPerQuantity());
                }
                inventory.setCbm(String.valueOf(invCbm));
            }

            inventory.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setDeletionIndicator(0L);

            if (existingInventory != null) {
                inventory.setCreatedOn(existingInventory.getCreatedOn());
            }
            if (existingInventory == null) {
                inventory.setCreatedOn(new Date());
            }
            inventory.setUpdatedOn(new Date());
//            inventory.setInventoryId(System.currentTimeMillis());
            InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
            log.info("created inventory : " + createdinventory);
            return createdinventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }

    /**
     * @param putAwayLine
     */
    private void createImfInventoryNonCBMV2(PutAwayLineV2 putAwayLine) {
        log.info("Create Inventory Initiated: " + new Date());
        String palletCode = null;
        String caseCode = null;
        String packBarCode = putAwayLine.getPackBarcodes();
        InventoryV2 existinginventory = null;
        try {
            if (putAwayLine.getBatchSerialNumber() != null) {
                existinginventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndBatchSerialNumberAndDeletionIndicatorOrderByInventoryIdDesc(
                        putAwayLine.getCompanyCode(),
                        putAwayLine.getPlantId(),
                        putAwayLine.getLanguageId(),
                        putAwayLine.getWarehouseId(),
                        putAwayLine.getItemCode(),
                        putAwayLine.getManufacturerName(),
                        putAwayLine.getBatchSerialNumber(), 3L,
                        putAwayLine.getBatchSerialNumber(), 0L);
            } else {

                existinginventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        putAwayLine.getCompanyCode(),
                        putAwayLine.getPlantId(),
                        putAwayLine.getLanguageId(),
                        putAwayLine.getWarehouseId(),
                        putAwayLine.getItemCode(),
                        putAwayLine.getManufacturerName(),
                        "99999", 3L, 0L);
            }

            if (existinginventory != null) {
                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
                double INV_QTY = existinginventory.getInventoryQuantity() - putAwayLine.getPutawayConfirmedQty();
                log.info("INV_QTY : " + INV_QTY);

                if (INV_QTY >= 0) {

                    InventoryV2 inventory2 = new InventoryV2();
                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
                    String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
                                                            putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), existinginventory.getStockTypeId());
                    inventory2.setStockTypeDescription(stockTypeDesc);
                    inventory2.setInventoryQuantity(round(INV_QTY));
                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);

                    palletCode = existinginventory.getPalletCode();
                    caseCode = existinginventory.getCaseCode();

                    if (inventory2.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
                                                                    putAwayLine.getWarehouseId(), putAwayLine.getItemCode());
                        if (itemType != null) {
                            inventory2.setItemType(itemType.getItemType());
                            inventory2.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }

                    inventory2.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
                    inventory2.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
                    if (putAwayLine.getBatchSerialNumber() != null) {
                        inventory2.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                    } else {
                        inventory2.setPackBarcodes("99999");
                    }
                    inventory2.setStorageSectionId(putAwayLine.getStorageSectionId());
                    inventory2.setManufacturerDate(putAwayLine.getManufacturerDate());
                    inventory2.setExpiryDate(putAwayLine.getExpiryDate());
                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
                    inventory2.setUpdatedOn(new Date());
                    inventory2.setInventoryId(System.currentTimeMillis());
                    InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
                    log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Existing Inventory---Error-----> : " + e.toString());
        }

        try {
            log.info("Create Inventory bin Class Id 1 Initiated: " + new Date());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

            inventory.setCompanyCodeId(putAwayLine.getCompanyCode());

            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);                // VAR_ID
            inventory.setVariantSubCode("1");            // VAR_SUB_ID
            inventory.setStorageMethod("1");            // STR_MTD
//            inventory.setBatchSerialNumber("1");        // STR_NO
            inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
            inventory.setStorageSectionId(putAwayLine.getStorageSectionId());
            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
            inventory.setBarcodeId(putAwayLine.getBarcodeId());
            inventory.setManufacturerName(putAwayLine.getManufacturerName());
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceField8(putAwayLine.getDescription());
            inventory.setStorageSectionId(putAwayLine.getStorageSectionId());
            if (putAwayLine.getBatchSerialNumber() != null) {
                inventory.setPackBarcodes(putAwayLine.getBatchSerialNumber());
            } else {
                inventory.setPackBarcodes("99999");
            }
            inventory.setExpiryDate(putAwayLine.getExpiryDate());
            inventory.setManufacturerDate(putAwayLine.getManufacturerDate());


            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(putAwayLine.getCompanyCode());
            storageBinPutAway.setPlantId(putAwayLine.getPlantId());
            storageBinPutAway.setLanguageId(putAwayLine.getLanguageId());
            storageBinPutAway.setWarehouseId(putAwayLine.getWarehouseId());
            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());
            if (putAwayLine.getStorageSectionId() != null) {
                if (putAwayLine.getStorageSectionId().equalsIgnoreCase(ST_SEC_ID_PFG)) {
                    storageBinPutAway.setStorageSectionId(ST_SEC_ID_FG);
                }
                if (putAwayLine.getStorageSectionId().equalsIgnoreCase(ST_SEC_ID_PSFG)) {
                    storageBinPutAway.setStorageSectionId(ST_SEC_ID_SFG);
                }
            }

            StorageBinV2 storageBin = null;
            try {
                storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
            } catch (Exception e) {
                throw new BadRequestException("Invalid StorageBin");
            }
            log.info("storageBin: " + storageBin);


            if (storageBin != null) {
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                inventory.setBinClassId(storageBin.getBinClassId());
            }

            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
            inventory.setPlantDescription(putAwayLine.getPlantDescription());
            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());

            inventory.setPalletCode(palletCode);
            inventory.setCaseCode(caseCode);
            log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), 1L);
            inventory.setStockTypeDescription(stockTypeDesc);
            log.info("StockTypeDescription: " + stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
            InventoryV2 existingInventory = null;

            if (putAwayLine.getBatchSerialNumber() != null) {
                existingInventory = inventoryService.getInventoryForInboundConfirmV2(
                        putAwayLine.getCompanyCode(),
                        putAwayLine.getPlantId(),
                        putAwayLine.getLanguageId(),
                        putAwayLine.getWarehouseId(),
                        putAwayLine.getBatchSerialNumber(),
                        putAwayLine.getItemCode(),
                        putAwayLine.getManufacturerName(),
                        putAwayLine.getConfirmedStorageBin(),
                        putAwayLine.getBatchSerialNumber(),
                        putAwayLine.getStorageSectionId());
            } else {
                existingInventory = inventoryService.getInventoryForInhouseTransferV2(
                        putAwayLine.getCompanyCode(),
                        putAwayLine.getPlantId(),
                        putAwayLine.getLanguageId(),
                        putAwayLine.getWarehouseId(),
                        "99999",
                        putAwayLine.getItemCode(),
                        putAwayLine.getManufacturerName(),
                        putAwayLine.getConfirmedStorageBin(),
                        putAwayLine.getStorageSectionId());
            }

            Double ALLOC_QTY = 0D;
            if (existingInventory != null) {
                if (existingInventory.getAllocatedQuantity() != null) {
                    ALLOC_QTY = existingInventory.getAllocatedQuantity();
                    inventory.setAllocatedQuantity(round(ALLOC_QTY));
                }
                if (existingInventory.getAllocatedQuantity() == null) {
                    inventory.setAllocatedQuantity(round(ALLOC_QTY));
                }
                log.info("Inventory Allocated Qty: " + ALLOC_QTY);
            }
            // INV_QTY
            if (existingInventory != null) {
                inventory.setInventoryQuantity(round(existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty()));
                log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", " + putAwayLine.getPutawayConfirmedQty());
                Double totalQty = inventory.getInventoryQuantity() + inventory.getAllocatedQuantity();
                inventory.setReferenceField4(round(totalQty));
                log.info("Inventory Total Qty: " + totalQty);
            }
            if (existingInventory == null) {
                inventory.setInventoryQuantity(round(putAwayLine.getPutawayConfirmedQty()));
                log.info("Inventory Qty = pa_cnf_qty: " + putAwayLine.getPutawayConfirmedQty());
                Double totalQty = putAwayLine.getPutawayConfirmedQty() + ALLOC_QTY;
                inventory.setReferenceField4(round(totalQty));
                log.info("Inventory Total Qty: " + totalQty);
            }

            //packbarcode
            /*
             * Hardcoding Packbarcode as 99999
             */
//            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
//            inventory.setPackBarcodes("99999");

            // INV_UOM
            if (putAwayLine.getPutAwayUom() != null) {
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                log.info("PA UOM: " + putAwayLine.getPutAwayUom());
            }
            inventory.setCreatedBy(putAwayLine.getCreatedBy());

            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setDeletionIndicator(0L);

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
                                                            putAwayLine.getWarehouseId(), putAwayLine.getItemCode());
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }

            if (existingInventory != null) {
                inventory.setCreatedOn(existingInventory.getCreatedOn());
            }
            if (existingInventory == null) {
                inventory.setCreatedOn(new Date());
            }
            inventory.setUpdatedOn(new Date());
            inventory.setBatchDate(new Date());
//            inventory.setInventoryId(System.currentTimeMillis());
            InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
            log.info("created inventory : " + createdinventory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }

    /**
     * @param putAwayLine
     * @return
     */
    private InventoryV2 createInventoryNonCBMV2(PutAwayLineV2 putAwayLine) {
        log.info("Create Inventory Initiated: " + new Date());
        String palletCode = null;
        String caseCode = null;
        String packBarCode = putAwayLine.getPackBarcodes();
        InventoryV2 existinginventory = null;
        try {
            if (putAwayLine.getBatchSerialNumber() != null) {
                existinginventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndBatchSerialNumberAndDeletionIndicatorOrderByInventoryIdDesc(
                        putAwayLine.getCompanyCode(),
                        putAwayLine.getPlantId(),
                        putAwayLine.getLanguageId(),
                        putAwayLine.getWarehouseId(),
                        putAwayLine.getItemCode(),
                        putAwayLine.getManufacturerName(),
                        putAwayLine.getBatchSerialNumber(), 3L,
                        putAwayLine.getBatchSerialNumber(), 0L);
            } else {

                existinginventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                        putAwayLine.getCompanyCode(),
                        putAwayLine.getPlantId(),
                        putAwayLine.getLanguageId(),
                        putAwayLine.getWarehouseId(),
                        putAwayLine.getItemCode(),
                        putAwayLine.getManufacturerName(),
                        "99999", 3L, 0L);
            }

            if (existinginventory != null) {
                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
                double INV_QTY = existinginventory.getInventoryQuantity() - putAwayLine.getPutawayConfirmedQty();
                log.info("INV_QTY : " + INV_QTY);

                if (INV_QTY >= 0) {

                    InventoryV2 inventory2 = new InventoryV2();
                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
                    String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
                                                            putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), existinginventory.getStockTypeId());
                    inventory2.setStockTypeDescription(stockTypeDesc);
                    inventory2.setInventoryQuantity(round(INV_QTY));
                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);

                    palletCode = existinginventory.getPalletCode();
                    caseCode = existinginventory.getCaseCode();

                    inventory2.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
                    inventory2.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
                    if (putAwayLine.getBatchSerialNumber() != null) {
                        inventory2.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                    } else {
                        inventory2.setPackBarcodes("99999");
                    }
                    if (inventory2.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
                                                                    putAwayLine.getWarehouseId(), putAwayLine.getItemCode());
                        if (itemType != null) {
                            inventory2.setItemType(itemType.getItemType());
                            inventory2.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }
                    inventory2.setManufacturerDate(putAwayLine.getManufacturerDate());
                    inventory2.setExpiryDate(putAwayLine.getExpiryDate());
                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
                    inventory2.setUpdatedOn(new Date());
                    inventory2.setInventoryId(System.currentTimeMillis());
                    InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
                    log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Existing Inventory---Error-----> : " + e.toString());
        }

        try {
            log.info("Create Inventory bin Class Id 1 Initiated: " + new Date());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

            inventory.setCompanyCodeId(putAwayLine.getCompanyCode());

            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);                // VAR_ID
            inventory.setVariantSubCode("1");            // VAR_SUB_ID
            inventory.setStorageMethod("1");            // STR_MTD
            inventory.setBatchSerialNumber("1");        // STR_NO
            inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
            inventory.setBarcodeId(putAwayLine.getBarcodeId());
            inventory.setManufacturerName(putAwayLine.getManufacturerName());
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceField8(putAwayLine.getDescription());
            inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
            if (putAwayLine.getBatchSerialNumber() != null) {
                inventory.setPackBarcodes(putAwayLine.getBatchSerialNumber());
            } else {
                inventory.setPackBarcodes("99999");
            }
            inventory.setExpiryDate(putAwayLine.getExpiryDate());
            inventory.setManufacturerDate(putAwayLine.getManufacturerDate());


            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(putAwayLine.getCompanyCode());
            storageBinPutAway.setPlantId(putAwayLine.getPlantId());
            storageBinPutAway.setLanguageId(putAwayLine.getLanguageId());
            storageBinPutAway.setWarehouseId(putAwayLine.getWarehouseId());
            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());

            StorageBinV2 storageBin = null;
            try {
                storageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
            } catch (Exception e) {
                throw new BadRequestException("Invalid StorageBin");
            }
            log.info("storageBin: " + storageBin);


            if (storageBin != null) {
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                inventory.setBinClassId(storageBin.getBinClassId());
            }

            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
            inventory.setPlantDescription(putAwayLine.getPlantDescription());
            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());

            inventory.setPalletCode(palletCode);
            inventory.setCaseCode(caseCode);
            log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), 1L);
            inventory.setStockTypeDescription(stockTypeDesc);
            log.info("StockTypeDescription: " + stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
            InventoryV2 existingInventory = null;

            if (putAwayLine.getBatchSerialNumber() != null) {
                existingInventory = inventoryService.getInventoryForInboundConfirmV2(
                        putAwayLine.getCompanyCode(),
                        putAwayLine.getPlantId(),
                        putAwayLine.getLanguageId(),
                        putAwayLine.getWarehouseId(),
                        putAwayLine.getBatchSerialNumber(),
                        putAwayLine.getItemCode(),
                        putAwayLine.getBarcodeId(),
                        putAwayLine.getManufacturerName(),
                        putAwayLine.getConfirmedStorageBin(),
                        putAwayLine.getBatchSerialNumber());
            } else {
                existingInventory = inventoryService.getInventoryForInhouseTransferV2(
                        putAwayLine.getCompanyCode(),
                        putAwayLine.getPlantId(),
                        putAwayLine.getLanguageId(),
                        putAwayLine.getWarehouseId(),
                        "99999",
                        putAwayLine.getItemCode(),
                        putAwayLine.getBarcodeId(),
                        putAwayLine.getManufacturerName(),
                        putAwayLine.getConfirmedStorageBin());
            }

            Double ALLOC_QTY = 0D;
            if (existingInventory != null) {
                if (existingInventory.getAllocatedQuantity() != null) {
                    ALLOC_QTY = existingInventory.getAllocatedQuantity();
                    inventory.setAllocatedQuantity(round(ALLOC_QTY));
                }
                if (existingInventory.getAllocatedQuantity() == null) {
                    inventory.setAllocatedQuantity(round(ALLOC_QTY));
                }
                log.info("Inventory Allocated Qty: " + ALLOC_QTY);
            }
            // INV_QTY
            if (existingInventory != null) {
                inventory.setInventoryQuantity(round(existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty()));
                log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", " + putAwayLine.getPutawayConfirmedQty());
                Double totalQty = inventory.getInventoryQuantity() + inventory.getAllocatedQuantity();
                inventory.setReferenceField4(round(totalQty));
                log.info("Inventory Total Qty: " + totalQty);
            }
            if (existingInventory == null) {
                inventory.setInventoryQuantity(round(putAwayLine.getPutawayConfirmedQty()));
                log.info("Inventory Qty = pa_cnf_qty: " + putAwayLine.getPutawayConfirmedQty());
                Double totalQty = putAwayLine.getPutawayConfirmedQty() + ALLOC_QTY;
                inventory.setReferenceField4(round(totalQty));
                log.info("Inventory Total Qty: " + totalQty);
            }

            //packbarcode
            /*
             * Hardcoding Packbarcode as 99999
             */
//            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
//            inventory.setPackBarcodes("99999");

            // INV_UOM
            if (putAwayLine.getPutAwayUom() != null) {
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                log.info("PA UOM: " + putAwayLine.getPutAwayUom());
            }
            inventory.setCreatedBy(putAwayLine.getCreatedBy());
            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
                                                            putAwayLine.getWarehouseId(), putAwayLine.getItemCode());
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setDeletionIndicator(0L);

            if (existingInventory != null) {
                inventory.setCreatedOn(existingInventory.getCreatedOn());
            }
            if (existingInventory == null) {
                inventory.setCreatedOn(new Date());
            }
            inventory.setUpdatedOn(new Date());
            inventory.setBatchDate(new Date());
//            inventory.setInventoryId(System.currentTimeMillis());
            InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
            log.info("created inventory : " + createdinventory);
            return createdinventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }

    /**
     *
     * @param putAwayHeader
     */
//    private void deleteInventoryNonCBMV2(PutAwayHeaderV2 putAwayHeader) {
//        log.info("Delete Inventory bin_cl_id 3 Initiated: " + new Date());
//        try {
//            InventoryV2 existinginventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
//                    putAwayHeader.getCompanyCodeId(),
//                    putAwayHeader.getPlantId(),
//                    putAwayHeader.getLanguageId(),
//                    putAwayHeader.getWarehouseId(),
//                    putAwayHeader.getReferenceField5(),
//                    putAwayHeader.getManufacturerName(),
//                    "99999", 3L,0L);
//
//            if (existinginventory != null) {
//                log.info("Delete Inventory bin Class Id 3 Initiated: " + new Date());
//                double INV_QTY = existinginventory.getInventoryQuantity() - putAwayHeader.getPutAwayQuantity();
//                log.info("INV_QTY : " + INV_QTY);
//
//                if (INV_QTY >= 0) {
//
//                    InventoryV2 inventory2 = new InventoryV2();
//                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
//                    String stockTypeDesc = getStockTypeDesc(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
//                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), existinginventory.getStockTypeId());
//                    inventory2.setStockTypeDescription(stockTypeDesc);
//                    inventory2.setInventoryQuantity(round(INV_QTY));
//                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
//                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);
//
//                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
//                    inventory2.setUpdatedOn(new Date());
//                    inventory2.setInventoryId(System.currentTimeMillis());
//                    InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
//                    log.info("----existinginventory--created[deleted]InventoryV2--------> : " + createdInventoryV2);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("Existing Inventory---Error-----> : " + e.toString());
//        }
//    }

    /**
     * @param putAwayLineV2
     */
    private void createInventoryMovementV2(PutAwayLineV2 putAwayLineV2) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(putAwayLineV2, inventoryMovement, CommonUtils.getNullPropertyNames(putAwayLineV2));
        inventoryMovement.setCompanyCodeId(putAwayLineV2.getCompanyCode());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(1L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(1L);    //1 - Inbound/Gr Confirm

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        inventoryMovement.setReferenceNumber(putAwayLineV2.getPreInboundNo());

        // MVT_DOC_NO
//        inventoryMovement.setMovementDocumentNo(putAwayLineV2.getPutAwayNumber());
        inventoryMovement.setReferenceField10(putAwayLineV2.getPutAwayNumber());

        // ST_BIN
        inventoryMovement.setStorageBin(putAwayLineV2.getConfirmedStorageBin());

        // MVT_QTY
        inventoryMovement.setMovementQty(putAwayLineV2.getPutawayConfirmedQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue("P");

        // MVT_UOM
        inventoryMovement.setInventoryUom(putAwayLineV2.getPutAwayUom());

        // BAL_OH_QTY
        Double sumOfInvQty = inventoryService.getInventoryQtyCountForInvMmt(
                putAwayLineV2.getCompanyCode(),
                putAwayLineV2.getPlantId(),
                putAwayLineV2.getLanguageId(),
                putAwayLineV2.getWarehouseId(),
                putAwayLineV2.getManufacturerName(),
                putAwayLineV2.getItemCode());
        log.info("BalanceOhQty: " + sumOfInvQty);
        if (sumOfInvQty != null) {
            inventoryMovement.setBalanceOHQty(sumOfInvQty);
            Double openQty = sumOfInvQty - putAwayLineV2.getPutawayConfirmedQty();
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
            log.info("OH Qty, OpenQty : " + sumOfInvQty + ", " + openQty);
        }
        if (sumOfInvQty == null) {
            inventoryMovement.setBalanceOHQty(0D);
//            Double openQty = sumOfInvQty - putAwayLineV2.getPutawayConfirmedQty();
            inventoryMovement.setReferenceField2("0");          //Qty before inventory Movement occur
            log.info("OH Qty, OpenQty : 0 , 0");
        }

        inventoryMovement.setVariantCode(1L);
        inventoryMovement.setVariantSubCode("1");

        inventoryMovement.setPackBarcodes(putAwayLineV2.getPackBarcodes());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(putAwayLineV2.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(new Date());
        inventoryMovement.setMovementDocumentNo(String.valueOf(System.currentTimeMillis()));
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        log.info("inventoryMovement : " + inventoryMovement);
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    //Get InboundHeader
    public InboundHeaderV2 getInboundHeaderForInvoiceCancellationV2(String companyCode, String plantId, String languageId,
                                                                    String warehouseId, String refDocNumber) {

        InboundHeaderV2 inboundHeader = inboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                companyCode, plantId, languageId, warehouseId, refDocNumber, 0L);
        log.info("InboundHeaderV2 - cancellation : " + inboundHeader);
        return inboundHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws ParseException
     */
    //Delete InboundHeader
    public InboundHeaderV2 deleteInboundHeaderForCancelV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                          String refDocNumber, String preInboundNo, String loginUserID) throws ParseException {

        InboundHeaderV2 inboundHeader = inboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("InboundHeaderV2 - cancellation : " + inboundHeader);
        if (inboundHeader != null) {
            inboundHeader.setDeletionIndicator(1L);
            inboundHeader.setUpdatedBy(loginUserID);
            inboundHeader.setUpdatedOn(new Date());
            inboundHeaderV2Repository.save(inboundHeader);
        }
        return inboundHeader;
    }

    //===================================================================Impex-V4======================================================================
    @Transactional
    public void updateInboundHeaderConfirmV4(String companyCode, String plantId, String languageId, String warehouseId,
                                             String preInboundNo, String refDocNumber, String loginUserID) {
        try {
            log.info("DSR/Auto--->Inbound Confirmation Process Initiated Order Number -----> " + refDocNumber);
            // PutawayHeader Validation
            long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
            log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

            if (putAwayHeaderStatusIdCount != 0) {
                throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: " + refDocNumber);
            }

            List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLinesV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber);
            log.info("PutawayLine List: " + putAwayLineList.size());
            if (putAwayLineList != null && !putAwayLineList.isEmpty()) {
                for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                    createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, putAwayLine.getItemCode(),
                                            putAwayLine.getManufacturerName(), refDocNumber, putAwayLine, loginUserID);
                    createInventoryMovementV2(putAwayLine);
                }
                log.info("Inventory Created Successfully -----> for All Putaway Lines");
            }

            statusDescription = getStatusDescription(24L, languageId);
            inboundHeaderV2Repository.updateAllStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, preInboundNo,
                                                                             refDocNumber, 24L, statusDescription, loginUserID, new Date());
            log.info("Inbound Confirm Successfully---> " + refDocNumber);
        } catch (Exception e) {
            throw new BadRequestException("Inbound confirmation [DSR]: Exception ----> " + e.toString());
        }
    }

    @Transactional
    public AXApiResponse updateInboundHeaderPartialConfirmNewV4(List<InboundLineV2> inboundLineList, String companyCode, String plantId, String languageId,
                                                                String warehouseId, String preInboundNo, String refDocNumber, String loginUserID) {
        try {
            log.info("Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
            // PutawayHeader Validation
            long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
            log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

            if (putAwayHeaderStatusIdCount != 0) {
                throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: " + refDocNumber);
            }

            AXApiResponse axapiResponse = new AXApiResponse();
            statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);
            String statusDescription17 = stagingLineV2Repository.getStatusDescription(17L, languageId);
            log.info("InboundLine List: " + inboundLineList.size());
            List<PutAwayLineV2> putAwayLineList = null;
            if (inboundLineList != null && !inboundLineList.isEmpty()) {
                for (InboundLineV2 inboundLine : inboundLineList) {
                    log.info("Input InboundLine : " + inboundLine);
                    if (inboundLine.getStatusId() == 20L) {
                        boolean inboundLineStatusId20 = inboundLineV2Repository.existsByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicator(
                                languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo, inboundLine.getLineNo(), inboundLine.getItemCode(), inboundLine.getManufacturerName(), 20L, 0L);
                        if (inboundLineStatusId20) {
                            putAwayLineList = putAwayLineService.
                                    getPutAwayLineForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                                                      inboundLine.getItemCode(), inboundLine.getManufacturerName(),
                                                                      inboundLine.getLineNo(), preInboundNo);
                            log.info("PutawayLine List: " + putAwayLineList.size());
                            if (putAwayLineList != null) {
                                for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                                    createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, putAwayLine.getItemCode(),
                                                            putAwayLine.getManufacturerName(), refDocNumber, putAwayLine, loginUserID);
                                    createInventoryMovementV2(putAwayLine);
                                }
                                log.info("Inventory Created Successfully -----> for Inbound Line ----> " +
                                                 inboundLine.getItemCode() + ", " + inboundLine.getManufacturerName() + ", " + inboundLine.getLineNo());
                            }
                        }
                    }
                    //Multiple Stored Procedure replaced with Single Procedure Call
                    inboundHeaderV2Repository.updateAllLineStatusInboundConfirmProcedure(
                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                            inboundLine.getItemCode(), inboundLine.getManufacturerName(), inboundLine.getLineNo(),
                            24L, 17L, statusDescription, statusDescription17, loginUserID, new Date());
                    log.info("PutawayHeader, All Line Status updated using stored procedure");
                }
            }

            String inboundLineStatus = inboundLineV2Repository.getInboundLinesCountStatusForInboundConfirm(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            log.info("Inbound Confirm - InboundLineStatus for Header Update : " + refDocNumber + ", " + inboundLineStatus);
            boolean isConditionMet = inboundLineStatus.equalsIgnoreCase("TRUE");
            if (isConditionMet) {
                inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
                log.info("Header Status updated using stored procedure");
            }

            axapiResponse.setStatusCode("200");                         //HardCoded
            axapiResponse.setMessage("Success");                        //HardCoded
            log.info("Partial Confirmation Process Finished ---> axapiResponse: " + refDocNumber + ", " + axapiResponse);
            return axapiResponse;
        } catch (Exception e) {
            throw new BadRequestException("Inbound confirmation : Exception ----> " + e.toString());
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param refDocNumber
     * @param putAwayLine
     * @return
     */
    private InventoryV2 createInventoryNonCBMV4(String companyCode, String plantId, String languageId,
                                                String warehouseId, String itemCode, String manufacturerName,
                                                String refDocNumber, PutAwayLineV2 putAwayLine, String loginUserId) {
        alreadyExecuted = false;
        log.info("Create Inventory Initiated ---> alreadyExecuted ---> " + new Date() + ", " + alreadyExecuted);
        String palletCode = null;
        String caseCode = null;
        try {
//            InventoryV2 existinginventory = grLineService.getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, putAwayLine.getBatchSerialNumber(), putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(), putAwayLine.getBagSize());

            InventoryV2 existinginventory = grLineService.getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                                                                                    putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom());
            if (existinginventory != null) {
                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                double INV_QTY = existinginventory.getInventoryQuantity() - physicalQty;
                if (INV_QTY < 0) {
                    INV_QTY = 0;
                }
                log.info("INV_QTY : " + INV_QTY);
                Double TOT_NO_OF_BAGS = (existinginventory.getNoBags() != null ? existinginventory.getNoBags() : 0D) - (putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D);

                if (INV_QTY >= 0) {
                    InventoryV2 inventory2 = new InventoryV2();
                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
                    String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, existinginventory.getStockTypeId());
                    inventory2.setStockTypeDescription(stockTypeDesc);
                    inventory2.setInventoryQuantity(round(INV_QTY));
                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
                    inventory2.setNoBags(TOT_NO_OF_BAGS);
                    inventory2.setBagSize(putAwayLine.getBagSize());
                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY + ", " + TOT_NO_OF_BAGS);

                    palletCode = existinginventory.getPalletCode();
                    caseCode = existinginventory.getCaseCode();

                    inventory2.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
                    inventory2.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
                    if (putAwayLine.getBatchSerialNumber() != null) {
                        inventory2.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                    } else {
                        inventory2.setPackBarcodes(PACK_BARCODE);
                    }
                    if (inventory2.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventory2.setItemType(itemType.getItemType());
                            inventory2.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }
                    inventory2.setReferenceDocumentNo(refDocNumber);
                    inventory2.setReferenceOrderNo(refDocNumber);
                    inventory2.setManufacturerDate(putAwayLine.getManufacturerDate());
                    inventory2.setExpiryDate(putAwayLine.getExpiryDate());
                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
                    inventory2.setUpdatedOn(new Date());
                    inventory2.setUpdatedBy(loginUserId);
                    if (!alreadyExecuted) {
                        InventoryV2 createdinventoryV2 = inventoryV2Repository.save(inventory2);
                        log.info("----existinginventory--createdInventoryV2--------> : " + createdinventoryV2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Existing Inventory---Error-----> : " + e.toString());
        }

        try {
            log.info("Create Inventory bin Class Id 1 Initiated: " + new Date());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

            inventory.setCompanyCodeId(companyCode);
            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);                // VAR_ID
            inventory.setVariantSubCode("1");            // VAR_SUB_ID
            inventory.setStorageMethod("1");            // STR_MTD
            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceField8(putAwayLine.getDescription());
            if (putAwayLine.getBatchSerialNumber() != null) {
                inventory.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());        // STR_NO
            } else {
                inventory.setBatchSerialNumber("1");        // STR_NO
                inventory.setPackBarcodes(PACK_BARCODE);
            }

            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(companyCode);
            storageBinPutAway.setPlantId(plantId);
            storageBinPutAway.setLanguageId(languageId);
            storageBinPutAway.setWarehouseId(warehouseId);
            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());

            StorageBinV2 storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
            log.info("storageBin: " + storageBin);

            if (storageBin != null) {
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setStorageSectionId(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                inventory.setBinClassId(storageBin.getBinClassId());
            }

            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
            inventory.setPlantDescription(putAwayLine.getPlantDescription());
            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());

            inventory.setPalletCode(palletCode);
            inventory.setCaseCode(caseCode);
            log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
            inventory.setStockTypeDescription(stockTypeDesc);
            log.info("StockTypeDescription: " + stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
            InventoryV2 existingInventory = grLineService.getInventoryBinClassId1V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                                                                                    putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(),
                                                                                    putAwayLine.getConfirmedStorageBin());
//            String packBarCode = putAwayLine.getBatchSerialNumber() != null ? putAwayLine.getBatchSerialNumber() : "99999";
//            InventoryV2 existingInventory = grLineService.getInventoryBinClassId1V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, putAwayLine.getBatchSerialNumber(), putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(), putAwayLine.getBagSize(), putAwayLine.getConfirmedStorageBin(), packBarCode);
//            if (putAwayLine.getBatchSerialNumber() != null) {
//                existingInventory = inventoryService.getInventoryForInboundConfirmV3(
//                        companyCode, plantId, languageId, warehouseId,
//                        putAwayLine.getBatchSerialNumber(), itemCode,
//                        putAwayLine.getBarcodeId(), manufacturerName,
//                        putAwayLine.getConfirmedStorageBin(),
//                        putAwayLine.getBatchSerialNumber());
//            } else {
//                existingInventory = inventoryService.getInventoryV3(
//                        companyCode, plantId, languageId, warehouseId,
//                        "99999", itemCode,
//                        putAwayLine.getBarcodeId(), manufacturerName,
//                        putAwayLine.getConfirmedStorageBin());
//            }

            // INV_QTY
            if (existingInventory != null) {
                Double ALLOC_QTY = existingInventory.getAllocatedQuantity() != null ? existingInventory.getAllocatedQuantity() : 0D;
                Double INV_QTY = existingInventory.getInventoryQuantity() != null ? existingInventory.getInventoryQuantity() : 0D;
                Double NO_OF_BAGS = existingInventory.getNoBags() != null ? existingInventory.getNoBags() : 0D;
                Double NEW_NO_OF_BAGS = putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D;
                Double TOT_NO_OF_BAGS = NO_OF_BAGS + NEW_NO_OF_BAGS;
                log.info("Existing Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY, PA_CNF_QTY : "
                                 + INV_QTY + ", " + ALLOC_QTY + ", " + existingInventory.getReferenceField4() + ", " + putAwayLine.getPutawayConfirmedQty() + ", " + NO_OF_BAGS);

                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());

                INV_QTY = INV_QTY + physicalQty;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(TOT_NO_OF_BAGS);
                inventory.setBagSize(putAwayLine.getBagSize());
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                inventory.setAlternateUom(putAwayLine.getAlternateUom());
                log.info("New Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + TOT_NO_OF_BAGS);

                inventory.setUpdatedBy(loginUserId);
                inventory.setCreatedOn(existingInventory.getCreatedOn());
            }
            if (existingInventory == null) {
                Double ALLOC_QTY = 0D;
                Double INV_QTY = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                Double TOT_NO_OF_BAGS = putAwayLine.getNoBags();
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(TOT_NO_OF_BAGS);
                inventory.setBagSize(putAwayLine.getBagSize());
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                inventory.setAlternateUom(putAwayLine.getAlternateUom());
                log.info("New Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + TOT_NO_OF_BAGS);

                inventory.setCreatedBy(loginUserId);
                inventory.setCreatedOn(new Date());
            }

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, languageId, itemCode);
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setDeletionIndicator(0L);

            inventory.setUpdatedOn(new Date());
            inventory.setBatchDate(new Date());

            InventoryV2 createdinventory = null;
            if (!alreadyExecuted) {
                createdinventory = inventoryV2Repository.save(inventory);
                alreadyExecuted = true;             //to ensure method executing only once
                log.info("created inventory : executed" + createdinventory + " -----> " + alreadyExecuted);
            }

            return createdinventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }
}