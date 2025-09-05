package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AXAuthToken;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.inbound.AddInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.InboundHeaderEntity;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.InboundLineEntity;
import com.tekclover.wms.api.transaction.model.inbound.SearchInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.UpdateInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLinePartialConfirm;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.transaction.model.inbound.v2.SearchInboundHeaderV2;
import com.tekclover.wms.api.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.ASN;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.ASNHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.ASNLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransfer;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransferHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.InterWarehouseTransferLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.SOReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.SOReturnHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.SOReturnLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.StoreReturn;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.StoreReturnHeader;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.StoreReturnLine;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.ASNHeaderV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.ASNLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInHeaderV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InterWarehouseTransferInV2;
import com.tekclover.wms.api.transaction.repository.GrHeaderRepository;
import com.tekclover.wms.api.transaction.repository.GrHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.GrLineV2Repository;
import com.tekclover.wms.api.transaction.repository.InboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.InboundHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.InboundLinePartialConfirmRepository;
import com.tekclover.wms.api.transaction.repository.InboundLineRepository;
import com.tekclover.wms.api.transaction.repository.InboundLineV2Repository;
import com.tekclover.wms.api.transaction.repository.InboundOrderLinesV2Repository;
import com.tekclover.wms.api.transaction.repository.IntegrationApiResponseRepository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryTransRepository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.repository.PreInboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PreInboundHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.PreInboundLineRepository;
import com.tekclover.wms.api.transaction.repository.PreInboundLineV2Repository;
import com.tekclover.wms.api.transaction.repository.PutAwayHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.PutAwayLineRepository;
import com.tekclover.wms.api.transaction.repository.PutAwayLineV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingHeaderRepository;
import com.tekclover.wms.api.transaction.repository.StagingHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.specification.InboundHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.InboundHeaderV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

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
    private InboundLinePartialConfirmRepository inboundLinePartialConfirmRepository;

    @Autowired
    private PreInboundHeaderRepository preInboundHeaderRepository;

    @Autowired
    private PreInboundLineRepository preInboundLineRepository;

    @Autowired
    private PutAwayLineRepository putAwayLineRepository;

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

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private PropertiesConfig propertiesConfig;

    @Autowired
    private IntegrationApiResponseRepository integrationApiResponseRepository;

    //----------------------------------------------------------------------------------------------
    @Autowired
    private InboundOrderLinesV2Repository inboundOrderLinesV2Repository;

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

    // Inventory Prod Issue Fix
  	@Autowired
  	private InventoryTransRepository inventoryTransRepository;

    String statusDescription = null;
    //----------------------------------------------------------------------------------------------

    /**
     * getInboundHeaders
     *
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
     *
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
     *
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
                    axapiResponse = postASN(confirmedInboundHeader, confirmedInboundLines);
                    log.info("AXApiResponse: " + axapiResponse);
                }

                // If IB_ORD_TYP_ID = 2, call StoreReturns API
                if (confirmedInboundHeader.getInboundOrderTypeId() == 2L) {
                    axapiResponse = postStoreReturn(confirmedInboundHeader, confirmedInboundLines);
                    log.info("AXApiResponse: " + axapiResponse);
                }

                // If IB_ORD_TYP_ID = 3, call InterWarehouse Receipt Confirmation API
                if (confirmedInboundHeader.getInboundOrderTypeId() == 3L) {
                    axapiResponse = postInterWarehouse(confirmedInboundHeader, confirmedInboundLines);
                    log.info("AXApiResponse: " + axapiResponse);
                }

                // If IB_ORD_TYP_ID = 4, call Sale Order Returns API
                if (confirmedInboundHeader.getInboundOrderTypeId() == 4L) {
                    axapiResponse = postSOReturn(confirmedInboundHeader, confirmedInboundLines);
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
     *
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

    //----------------------------------------------------------------------------------------------------

    /**
     * POST ASN
     *
     * @param confirmedInboundHeader
     * @param confirmedInboundLines
     * @return
     */
    private AXApiResponse postASN(InboundHeader confirmedInboundHeader, List<InboundLine> confirmedInboundLines) {
        ASNHeader asnHeader = new ASNHeader();
        asnHeader.setAsnNumber(confirmedInboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<ASNLine> asnLines = new ArrayList<>();
        for (InboundLine inboundLine : confirmedInboundLines) {
            asnHeader.setSupplierInvoice(inboundLine.getInvoiceNo());

            /*
             * AcceptQty not equal to null and greater than 0
             * OR
             * DamageQty not equal to null and greater than 0
             */
            if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
                    || (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
                inboundLine.setConfirmedOn(new Date());

                ASNLine asnLine = new ASNLine();

                // SKU	<-	ITM_CODE
                asnLine.setSku(inboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                asnLine.setSkuDescription(inboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                asnLine.setLineReference(inboundLine.getLineNo());

                // Expected Qty	<- ORD_QTY
                asnLine.setExpectedQty(inboundLine.getOrderQty());

                // UOM	<-	ORD_UOM
                asnLine.setUom(inboundLine.getOrderUom());

                // Received Qty	<-	ACCEPT_QTY
                if (inboundLine.getAcceptedQty() == null) {
                    asnLine.setReceivedQty(0D);
                } else {
                    asnLine.setReceivedQty(inboundLine.getAcceptedQty());
                }

                // Damage Qty <-	DAMAGE_QTY
                if (inboundLine.getDamageQty() == null) {
                    asnLine.setDamagedQty(0D);
                } else {
                    asnLine.setDamagedQty(inboundLine.getDamageQty());
                }

                // Pack Qty	<-	ITM_CASE_QTY
                asnLine.setPackQty(inboundLine.getItemCaseQty());

                // Actual Receipt Date	<-	IB_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
                asnLine.setActualReceiptDate(date);

                // Warehouse ID	<-	WH_ID
                asnLine.setWareHouseId(inboundLine.getWarehouseId());
                asnLines.add(asnLine);
            }
        }

        if (asnLines.isEmpty()) {
            throw new BadRequestException("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
        }

        ASN asn = new ASN();
        asn.setAsnHeader(asnHeader);
        asn.setAsnLines(asnLines);
        log.info("Sending ASN : " + asn);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postASNConfirmation(asn, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(asnHeader.getAsnNumber());
        response.setOrderType("INBOUND");
        response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceAsnUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     * StoreReturn API
     *
     * @param confirmedInboundHeader
     * @param confirmedInboundLines
     * @return
     */
    private AXApiResponse postStoreReturn(InboundHeader confirmedInboundHeader,
                                          List<InboundLine> confirmedInboundLines) {
        StoreReturnHeader storeReturnHeader = new StoreReturnHeader();
        storeReturnHeader.setTransferOrderNumber(confirmedInboundHeader.getRefDocNumber());    // REF_DOC_NO
        List<StoreReturnLine> storeReturnLines = new ArrayList<>();
        for (InboundLine inboundLine : confirmedInboundLines) {
            /*
             * AcceptQty not equal to null and greater than 0
             * OR
             * DamageQty not equal to null and greater than 0
             */
            if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
                    || (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
                inboundLine.setConfirmedOn(new Date());
                StoreReturnLine storeReturnLine = new StoreReturnLine();

                // SKU	<-	ITM_CODE
                storeReturnLine.setSku(inboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                storeReturnLine.setSkuDescription(inboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                storeReturnLine.setLineReference(inboundLine.getLineNo());

                // Expected Qty	<- ORD_QTY
                storeReturnLine.setExpectedQty(inboundLine.getOrderQty());

                // UOM	<-	ORD_UOM
                storeReturnLine.setUom(inboundLine.getOrderUom());

                // Received Qty	<-	ACCEPT_QTY
                if (inboundLine.getAcceptedQty() == null) {
                    storeReturnLine.setReceivedQty(0D);
                } else {
                    storeReturnLine.setReceivedQty(inboundLine.getAcceptedQty());
                }

                // Damage Qty <-	DAMAGE_QTY
                if (inboundLine.getDamageQty() == null) {
                    storeReturnLine.setDamagedQty(0D);
                } else {
                    storeReturnLine.setDamagedQty(inboundLine.getDamageQty());
                }

                // Pack Qty	<-	ITM_CASE_QTY
                storeReturnLine.setPackQty(inboundLine.getItemCaseQty());

                // Actual Receipt Date	<-	IB_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
                storeReturnLine.setActualReceiptDate(date);

                // Warehouse ID	<-	WH_ID
                storeReturnLine.setWareHouseId(inboundLine.getWarehouseId());

                // Store ID <- PARTNER_CODE
                storeReturnLine.setStoreId(inboundLine.getVendorCode());

                storeReturnLines.add(storeReturnLine);
            }
        }

        if (storeReturnLines.isEmpty()) {
            throw new BadRequestException("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
        }

        StoreReturn storeReturn = new StoreReturn();
        storeReturn.setToHeader(storeReturnHeader);
        storeReturn.setToLines(storeReturnLines);
        log.info("Sending StoreReturn : " + storeReturn);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postStoreReturnConfirmation(storeReturn, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(storeReturnHeader.getTransferOrderNumber());
        response.setOrderType("INBOUND");
        response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceStoreReturnUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     * SO Return
     *
     * @param confirmedInboundHeader
     * @param confirmedInboundLines
     * @return
     */
    private AXApiResponse postSOReturn(InboundHeader confirmedInboundHeader, List<InboundLine> confirmedInboundLines) {
        SOReturnHeader soReturnHeader = new SOReturnHeader();
        soReturnHeader.setReturnOrderReference(confirmedInboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<SOReturnLine> soReturnLines = new ArrayList<>();
        for (InboundLine inboundLine : confirmedInboundLines) {
            /*
             * AcceptQty not equal to null and greater than 0
             * OR
             * DamageQty not equal to null and greater than 0
             */
            if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
                    || (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
                inboundLine.setConfirmedOn(new Date());
                SOReturnLine soReturnLine = new SOReturnLine();

                // Salesroderreference <-	REF_FIELD_4
                soReturnLine.setSalesOrderReference(inboundLine.getReferenceField4());

                // SKU	<-	ITM_CODE
                soReturnLine.setSku(inboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                soReturnLine.setSkuDescription(inboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                soReturnLine.setLineReference(inboundLine.getLineNo());

                // Expected Qty	<- ORD_QTY
                soReturnLine.setExpectedQty(inboundLine.getOrderQty());

                // UOM	<-	ORD_UOM
                soReturnLine.setUom(inboundLine.getOrderUom());

                // Received Qty	<-	ACCEPT_QTY
                if (inboundLine.getAcceptedQty() == null) {
                    soReturnLine.setReturnQty(0D);
                } else {
                    soReturnLine.setReturnQty(inboundLine.getAcceptedQty());
                }

                // Actual Receipt Date	<-	IB_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
                soReturnLine.setActualReceiptDate(date);

                // Warehouse ID	<-	WH_ID
                soReturnLine.setWareHouseId(inboundLine.getWarehouseId());

                soReturnLines.add(soReturnLine);
            }
        }

        if (soReturnLines.isEmpty()) {
            throw new BadRequestException("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
        }

        SOReturn soReturn = new SOReturn();
        soReturn.setReturnOrderHeader(soReturnHeader);
        soReturn.setReturnOrderLines(soReturnLines);
        log.info("Sending SOReturn : " + soReturn);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postSOReturnConfirmation(soReturn, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(soReturnHeader.getReturnOrderReference());
        response.setOrderType("INBOUND");
        response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceSOReturnUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     * InterWarehouse API
     *
     * @param confirmedInboundHeader
     * @param confirmedInboundLines
     * @return
     */
    private AXApiResponse postInterWarehouse(InboundHeader confirmedInboundHeader,
                                             List<InboundLine> confirmedInboundLines) {
        InterWarehouseTransferHeader toHeader = new InterWarehouseTransferHeader();
        toHeader.setTransferOrderNumber(confirmedInboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<InterWarehouseTransferLine> toLines = new ArrayList<>();
        for (InboundLine inboundLine : confirmedInboundLines) {
            /*
             * AcceptQty not equal to null and greater than 0
             * OR
             * DamageQty not equal to null and greater than 0
             */
            if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
                    || (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
                inboundLine.setConfirmedOn(new Date());
                InterWarehouseTransferLine iwhTransferLine = new InterWarehouseTransferLine();

                // SKU	<-	ITM_CODE
                iwhTransferLine.setSku(inboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                iwhTransferLine.setSkuDescription(inboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                iwhTransferLine.setLineReference(inboundLine.getLineNo());

                // Expected Qty	<- ORD_QTY
                iwhTransferLine.setExpectedQty(inboundLine.getOrderQty());

                // UOM	<-	ORD_UOM
                iwhTransferLine.setUom(inboundLine.getOrderUom());

                // Received Qty	<-	ACCEPT_QTY
                if (inboundLine.getAcceptedQty() == null) {
                    iwhTransferLine.setReceivedQty(0D);
                } else {
                    iwhTransferLine.setReceivedQty(inboundLine.getAcceptedQty());
                }

                // Damage Qty <-	DAMAGE_QTY
                if (inboundLine.getDamageQty() == null) {
                    iwhTransferLine.setDamageQty(0D);
                } else {
                    iwhTransferLine.setDamageQty(inboundLine.getDamageQty());
                }

                // Pack Qty	<-	ITM_CASE_QTY
                iwhTransferLine.setPackQty(inboundLine.getItemCaseQty());

                // Actual Receipt Date	<-	IB_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
                iwhTransferLine.setActualReceiptDate(date);

                // Warehouse ID	<-	WH_ID
                iwhTransferLine.setToWhsId(inboundLine.getWarehouseId());

                // FromWhsID <-	PARTNER_CODE
                iwhTransferLine.setFromWhsId(inboundLine.getVendorCode());

                toLines.add(iwhTransferLine);
            }
        }

        if (toLines.isEmpty()) {
            throw new BadRequestException("confirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
        }

        InterWarehouseTransfer interWarehouseTransfer = new InterWarehouseTransfer();
        interWarehouseTransfer.setToHeader(toHeader);
        interWarehouseTransfer.setToLines(toLines);
        log.info("Sending InterWarehouseTransfer : " + interWarehouseTransfer);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postInterWarehouseTransferConfirmation(interWarehouseTransfer, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(toHeader.getTransferOrderNumber());
        response.setOrderType("INBOUND");
        response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceInterwareHouseUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    //=================================================================V2===========================================================================

    /**
     * getInboundHeader
     *
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
     *
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
                for(GrLineV2 grLine : grLineList) {
                    List<PutAwayLineV2> putAwayLineList = putAwayLineService.
                            getPutAwayLineForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                    grLine.getItemCode(),
                                    grLine.getManufacturerName(),
                                    grLine.getLineNo(),
                                    grLine.getPreInboundNo(),
                                    grLine.getPackBarcodes());
                    if (putAwayLineList != null) {
                        for(PutAwayLineV2 putAwayLine : putAwayLineList) {
                            InventoryV2 createdInventory = createInventoryV2(putAwayLine, grLine.getQuantityType());
//                            createInventoryMovementV2(putAwayLine);
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

        if(inboundLineV2List != null && !inboundLineV2List.isEmpty()) {
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
//    @Transactional
    public AXApiResponse updateInboundHeaderPartialConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                             String preInboundNo, String refDocNumber, String loginUserID) {
        try {
            log.info("DSR--->Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
        // PutawayHeader Validation
			long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId,
					warehouseId, preInboundNo, refDocNumber);
        log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

        if (putAwayHeaderStatusIdCount != 0) {
				throw new BadRequestException(
						"Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: "
								+ refDocNumber);
        }

        AXApiResponse axapiResponse = new AXApiResponse();

			List<InboundLineV2> inboundLineList = inboundLineService.getInboundLineForInboundConfirmPartialAllocationV2(
					companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);

        if (inboundLineList != null) {
            for (InboundLineV2 inboundLine : inboundLineList) {
					List<GrLineV2> grLineList = grLineService.getGrLineForInboundConformV2(companyCode, plantId,
							languageId, warehouseId, refDocNumber, inboundLine.getItemCode(),
							inboundLine.getManufacturerName(), inboundLine.getLineNo(), inboundLine.getPreInboundNo());
                log.info("GrLine List: " + grLineList.size());
                    for (GrLineV2 grLine : grLineList) {
						List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLineForInboundConfirmV2(
								companyCode, plantId, languageId, warehouseId, refDocNumber, grLine.getItemCode(),
								grLine.getManufacturerName(), grLine.getLineNo(), grLine.getPreInboundNo(),
                                    grLine.getPackBarcodes());
                    log.info("PutawayLine List: " + putAwayLineList.size());
                    if (putAwayLineList != null) {
                            for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                                boolean createdInventory = createInventoryNonCBMV2(putAwayLine);
//                            createInventoryMovementV2(putAwayLine);
                        }
                        log.info("Inventory Created Successfully -----> for All Putaway Lines");
                    }
                }
            }
        }
        statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);

			inboundLineV2Repository.updateInboundLineStatusUpdateInboundConfirmProc(companyCode, plantId, languageId,
					warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
        log.info("InboundLine updated");

			putAwayLineV2Repository.updatePutAwayLineStatusUpdateInboundConfirm(companyCode, plantId, languageId,
					warehouseId, refDocNumber, preInboundNo, 24L, statusDescription);
        log.info("putAwayLine updated");

	//        putAwayHeaderV2Repository.updatepaheaderStatusUpdateInboundConfirmProc(
			// companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
			// 24L, statusDescription, loginUserID, new Date());
	//        log.info("PutAwayHeader Updated");
	
	//        preInboundLineV2Repository.updatePreInboundLineStatusUpdateInboundConfirmProc(
			// companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
			// 24L, statusDescription, loginUserID, new Date());
	//                log.info("PreInboundLine updated");

            String statusDescription17 = stagingLineV2Repository.getStatusDescription(17L, languageId);
	//        stagingLineV2Repository.updateStagingLineStatusUpdateInboundConfirmProc(
			// companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
			// 17L, statusDescription17, loginUserID, new Date());
	//                log.info("StagingLine updated");
	
	//        grLineV2Repository.updateGrLineStatusUpdateInboundConfirmProc(
			// companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
			// 24L, statusDescription, loginUserID, new Date());
	//                log.info("GrLine updated");

			// Multiple Stored Procedure replaced with Single Procedure Call
			inboundHeaderV2Repository.updatePahGrlStglPiblStatusInboundConfirmProcedure(companyCode, plantId,
					languageId, warehouseId, refDocNumber, preInboundNo, 24L, 17L, statusDescription,
					statusDescription17, loginUserID, new Date());
            log.info("PutawayHeader, GrLine, Stg Line, PreIbLine Status updated using stored procedure");

			Long inboundLinesV2CountForInboundConfirmWithStatusId = inboundLineV2Repository
					.getInboundLinesV2CountForInboundConfirmWithStatusId(companyCode, plantId, languageId, warehouseId,
							refDocNumber, preInboundNo, 24L);
        Long inboundLinesV2CountForInboundConfirm = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirm(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            if (inboundLinesV2CountForInboundConfirmWithStatusId == null) {
            inboundLinesV2CountForInboundConfirmWithStatusId = 0L;
        }
            if (inboundLinesV2CountForInboundConfirm == null) {
            inboundLinesV2CountForInboundConfirm = 0L;
        }
			boolean isConditionMet = inboundLinesV2CountForInboundConfirmWithStatusId
					.equals(inboundLinesV2CountForInboundConfirm);
			log.info("Inbound Line 24_StatusCount, Line Count: " + isConditionMet + ", "
					+ inboundLinesV2CountForInboundConfirmWithStatusId + ", " + inboundLinesV2CountForInboundConfirm);
            if (isConditionMet) {
				log.info("Inbound Line 24-------< :  " + isConditionMet);
				// Multiple Stored Procedure replaced with Single Procedure Call
				inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId,
						warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
                log.info("Header Status updated using stored procedure");
        }
			axapiResponse.setStatusCode("200"); // HardCoded
			axapiResponse.setMessage("Success"); // HardCoded
        log.info("axapiResponse: " + axapiResponse);
        return axapiResponse;
        } catch (Exception e) {
            throw new BadRequestException("Inbound confirmation [DSR]: Exception ----> " + e.toString());
        }
    }

    /**
     * 
     * @param inboundLineList
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     */
    public void updateInboundHeaderPartialConfirmNewV2(List<InboundLineV2> inboundLineList, String companyCode,
			String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumber,
			String loginUserID) {
		try {
			log.info("Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
			// PutawayHeader Validation
			long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId,
					warehouseId, preInboundNo, refDocNumber);
			log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

			if (putAwayHeaderStatusIdCount != 0) {
				throw new BadRequestException(
						"Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: "
								+ refDocNumber);
			}

			statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);
			List<InboundLinePartialConfirm> newInboundLinePartialConfirmList = new ArrayList<>();
			inboundLineList.stream().forEach(inboundLine -> {
				try {
//					inboundLineV2Repository.updateInboundLineStatusUpdateInboundConfirmIndividualItemProc(companyCode,
//							plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundLine.getItemCode(),
//							inboundLine.getManufacturerName(), inboundLine.getLineNo(), 24L, statusDescription,
//							loginUserID, new Date());

                    inboundLineV2Repository.updateInboundLineStatusUpdateInboundConfirmIndividualItem(24L,
                            statusDescription, loginUserID, new Date(), inboundLine.getItemCode(), inboundLine.getManufacturerName(), companyCode, plantId, languageId, warehouseId,
                            refDocNumber, preInboundNo, inboundLine.getLineNo());
					log.info("-----updateInboundHeaderPartialConfirmNewV2----InboundLine-status-updated: "
							+ inboundLine.getItemCode() + ", " + inboundLine.getManufacturerName() + ", "
							+ inboundLine.getLineNo());

					InboundLinePartialConfirm inboundLinePartExistingRecord = inboundLinePartialConfirmRepository
							.findByCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCode(
									companyCode, plantId, warehouseId, refDocNumber, preInboundNo,
									inboundLine.getLineNo(), inboundLine.getItemCode());
					log.info("----inboundLinePartExistingRecord--###---> : " + inboundLinePartExistingRecord);
					if (inboundLinePartExistingRecord == null) {
						InboundLinePartialConfirm newInboundLinePartialConfirm = new InboundLinePartialConfirm();
						BeanUtils.copyProperties(inboundLine, newInboundLinePartialConfirm,
								CommonUtils.getNullPropertyNames(inboundLine));
						newInboundLinePartialConfirm.setStatusId(24L);
						newInboundLinePartialConfirm.setIsExecuted(0L);
						newInboundLinePartialConfirmList.add(newInboundLinePartialConfirm);
					}
				} catch (Exception e) {
					log.error("Exception while InboundLine status update: " + e.toString());
					e.printStackTrace();
				}
			});

			inboundLinePartialConfirmRepository.saveAll(newInboundLinePartialConfirmList);
			log.info("----newInboundLinePartialConfirm--created---> : " + newInboundLinePartialConfirmList);

			// Inbound Header 24 Update
			inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId,
					warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
			log.info("Header Status updated using stored procedure");

			// PutawayHeader, GrLine, Stg Line, PreIbLine Status updated
			String statusDescription17 = stagingLineV2Repository.getStatusDescription(17L, languageId);
			inboundHeaderV2Repository.updatePahGrlStglPiblStatusInboundConfirmProcedure(companyCode, plantId,
					languageId, warehouseId, refDocNumber, preInboundNo, 24L, 17L, statusDescription,
					statusDescription17, loginUserID, new Date());
			log.info("PutawayHeader, GrLine, Stg Line, PreIbLine Status updated using stored procedure");

//			Long inboundLinesV2CountForInboundConfirmWithStatusId = inboundLineV2Repository
//					.getInboundLinesV2CountForInboundConfirmWithStatusId(companyCode, plantId, languageId, warehouseId,
//							refDocNumber, preInboundNo, 24L);
//
//			Long inboundLinesV2CountForInboundConfirm = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirm(
//					companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
//
//			if (inboundLinesV2CountForInboundConfirmWithStatusId == null) {
//				inboundLinesV2CountForInboundConfirmWithStatusId = 0L;
//			}
//
//			if (inboundLinesV2CountForInboundConfirm == null) {
//				inboundLinesV2CountForInboundConfirm = 0L;
//			}
//
//			boolean isConditionMet = inboundLinesV2CountForInboundConfirmWithStatusId
//					.equals(inboundLinesV2CountForInboundConfirm);
//			log.info("Inbound Line 24_StatusCount, Line Count: " + refDocNumber + ", " + isConditionMet + ", "
//					+ inboundLinesV2CountForInboundConfirmWithStatusId + ", " + inboundLinesV2CountForInboundConfirm);
//			if (isConditionMet) {
//				inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId,
//						warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
//				log.info("Header Status updated using stored procedure");
//			}
		} catch (Exception e) {
			throw new BadRequestException("Inbound confirmation : Exception ----> " + e.toString());
		}
	}
    
	/**
	 * 
	 */
	@Scheduled(fixedDelay = 15000)
	private void scheduleInboundLinePartialConfirmation() {
		log.info("-----scheduleInboundLinePartialConfirmation--1-->: ");
        List<InboundLinePartialConfirm> inboundLinePartialConfirmList = inboundLinePartialConfirmRepository
                .findByStatusIdAndIsExecutedAndAcceptedQtyNot(24L, 0L, 0D);
        log.info("-----scheduleInboundLinePartialConfirmation--2-->: " + inboundLinePartialConfirmList);

		inboundLinePartialConfirmList.stream().forEach(inboundLine -> {
			log.info("-----scheduleInboundLinePartialConfirmation---->: " + inboundLine);
			String updatedBy = inboundLine.getUpdatedBy();
			if (inboundLine.getUpdatedBy() == null) {
				updatedBy = inboundLine.getCreatedBy();
			}

			List<PutAwayLineV2> putAwayLineList = putAwayLineService.getPutAwayLineForInboundConfirmV2(
					inboundLine.getCompanyCode(), inboundLine.getPlantId(), inboundLine.getLanguageId(),
					inboundLine.getWarehouseId(), inboundLine.getRefDocNumber(), inboundLine.getItemCode(),
					inboundLine.getManufacturerName(), inboundLine.getLineNo(), inboundLine.getPreInboundNo());
			log.info("PutawayLine List: " + putAwayLineList.size());

			if (putAwayLineList != null) {
				putAwayLineList.stream().forEach(putAwayLine -> {
					try {
						boolean createdInventory = createInventoryNonCBMV2(putAwayLine);
						log.info("----createdInventory-------flag---> : " + createdInventory);
						if (createdInventory) {
							inboundLinePartialConfirmRepository.updateInboundLinePartialConfirmExecutedStatus(
									inboundLine.getLanguageId(), inboundLine.getPlantId(), inboundLine.getCompanyCode(),
									inboundLine.getWarehouseId(), inboundLine.getPreInboundNo(),
									inboundLine.getRefDocNumber(), inboundLine.getLineNo(), 1L);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
			
			statusDescription = stagingLineV2Repository.getStatusDescription(24L, inboundLine.getLanguageId());
			putAwayLineV2Repository.updatePutAwayLineStatusUpdateInboundConfirm(inboundLine.getCompanyCode(),
					inboundLine.getPlantId(), inboundLine.getLanguageId(), inboundLine.getWarehouseId(),
					inboundLine.getRefDocNumber(), inboundLine.getPreInboundNo(), 24L, statusDescription);
			log.info("-----updateInboundHeaderPartialConfirmNewV2----putAwayLine-updated----");
		});
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
			InventoryV2 existinginventory = inventoryV2Repository
					.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
							putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
							putAwayLine.getWarehouseId(), putAwayLine.getItemCode(), putAwayLine.getManufacturerName(),
							"99999", 3L, 0L);

			if (existinginventory != null) {
				log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
				double INV_QTY = existinginventory.getInventoryQuantity() - putAwayLine.getPutawayConfirmedQty();
				log.info("INV_QTY : " + INV_QTY);

				if (INV_QTY >= 0) {

					InventoryV2 inventory2 = new InventoryV2();
					BeanUtils.copyProperties(existinginventory, inventory2,
							CommonUtils.getNullPropertyNames(existinginventory));
					String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
							putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(),
							existinginventory.getStockTypeId());
					inventory2.setStockTypeDescription(stockTypeDesc);
					inventory2.setInventoryQuantity(INV_QTY);
					inventory2.setReferenceField4(INV_QTY); // Allocated Qty is always 0 for BinClassId 3
					log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);

					palletCode = existinginventory.getPalletCode();
					caseCode = existinginventory.getCaseCode();

					inventory2.setCreatedOn(existinginventory.getCreatedOn());
					inventory2.setUpdatedOn(new Date());
					inventory2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
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
			inventory.setVariantCode(1L); // VAR_ID
			inventory.setVariantSubCode("1"); // VAR_SUB_ID
			inventory.setStorageMethod("1"); // STR_MTD
			inventory.setBatchSerialNumber("1"); // STR_NO
			inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
			inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
			inventory.setBarcodeId(putAwayLine.getBarcodeId());
			inventory.setManufacturerName(putAwayLine.getManufacturerName());

			// ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value
			// and update
			AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
			StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
			storageBinPutAway.setCompanyCodeId(putAwayLine.getCompanyCode());
			storageBinPutAway.setPlantId(putAwayLine.getPlantId());
			storageBinPutAway.setLanguageId(putAwayLine.getLanguageId());
			storageBinPutAway.setWarehouseId(putAwayLine.getWarehouseId());
			storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());

			StorageBinV2 storageBin = null;
			try {
				storageBin = mastersService.getaStorageBinV2(storageBinPutAway,
						authTokenForMastersService.getAccess_token());
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
			ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData,
					authTokenForMastersService.getAccess_token());
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
			String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
					putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), 1L);
			inventory.setStockTypeDescription(stockTypeDesc);
			log.info("StockTypeDescription: " + stockTypeDesc);

// SP_ST_IND_ID
			inventory.setSpecialStockIndicatorId(1L);

			InventoryV2 existingInventory = inventoryService.getInventoryForInhouseTransferV2(
					putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
					putAwayLine.getWarehouseId(), "99999", putAwayLine.getItemCode(), putAwayLine.getManufacturerName(),
					putAwayLine.getConfirmedStorageBin());

			Double ALLOC_QTY = 0D;
			if (existingInventory != null) {
				if (existingInventory.getAllocatedQuantity() != null) {
					ALLOC_QTY = existingInventory.getAllocatedQuantity();
					inventory.setAllocatedQuantity(ALLOC_QTY);
				}
				if (existingInventory.getAllocatedQuantity() == null) {
					inventory.setAllocatedQuantity(ALLOC_QTY);
				}
				log.info("Inventory Allocated Qty: " + ALLOC_QTY);
			}
// INV_QTY
			if (existingInventory != null) {
				inventory.setInventoryQuantity(
						existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty());
				log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", "
						+ putAwayLine.getPutawayConfirmedQty());
				Double totalQty = inventory.getInventoryQuantity() + inventory.getAllocatedQuantity();
				inventory.setReferenceField4(totalQty);
				log.info("Inventory Total Qty: " + totalQty);
			}
			if (existingInventory == null) {
				inventory.setInventoryQuantity(putAwayLine.getPutawayConfirmedQty());
				log.info("Inventory Qty = pa_cnf_qty: " + putAwayLine.getPutawayConfirmedQty());
				Double totalQty = putAwayLine.getPutawayConfirmedQty() + ALLOC_QTY;
				inventory.setReferenceField4(totalQty);
				log.info("Inventory Total Qty: " + totalQty);
			}

			inventory.setPackBarcodes("99999");

			if (putAwayLine.getPutAwayUom() != null) {
				inventory.setInventoryUom(putAwayLine.getPutAwayUom());
				log.info("PA UOM: " + putAwayLine.getPutAwayUom());
			}
			inventory.setCreatedBy(putAwayLine.getCreatedBy());
			// V2 Code (remaining all fields copied already using beanUtils.copyProperties)
			boolean capacityCheck = false;
			Double invQty = 0D;
			Double cbm = 0D;
			Double cbmPerQty = 0D;
			Double invCbm = 0D;
			if (itemCodeCapacityCheck != null) {
				if (itemCodeCapacityCheck.getCapacityCheck() != null) {
					log.info("CBM Check");
					capacityCheck = itemCodeCapacityCheck.getCapacityCheck(); // Capacity Check for putaway item
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
			inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 1));
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
     * @param putAwayLine
     * @return
	 * @throws Exception
     */
	private boolean createInventoryNonCBMV2(PutAwayLineV2 putAwayLine) throws Exception {
		log.info("-------createInventoryNonCBMV2---called-----");

		boolean isInventoryCreated = false;
		String palletCode = null;
		String caseCode = null;
		try {
			InventoryV2 existinginventory = inventoryService.getInventoryForStockAdjustmentDamageV2(
					putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
					putAwayLine.getWarehouseId(), putAwayLine.getItemCode(), "99999", 3L,
					putAwayLine.getManufacturerName());
			log.info("----existinginventory------CLASS-3----> " + existinginventory);

			if (existinginventory != null) {
				double INV_QTY = existinginventory.getInventoryQuantity() - putAwayLine.getPutawayConfirmedQty();
				double INV_QTY_ERR = INV_QTY;
				log.info("INV_QTY : " + INV_QTY);

				if (INV_QTY >= 0) {

					InventoryV2 inventory2 = new InventoryV2();
					try {
						BeanUtils.copyProperties(existinginventory, inventory2,
								CommonUtils.getNullPropertyNames(existinginventory));
						String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
								putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(),
								existinginventory.getStockTypeId());
						inventory2.setStockTypeDescription(stockTypeDesc);
						inventory2.setInventoryQuantity(INV_QTY);
						inventory2.setReferenceField4(INV_QTY); // Allocated Qty is always 0 for BinClassId 3
						log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);

						palletCode = existinginventory.getPalletCode();
						caseCode = existinginventory.getCaseCode();

						inventory2.setCreatedOn(existinginventory.getCreatedOn());
						inventory2.setUpdatedOn(new Date());
						inventory2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
						InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
						log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
						isInventoryCreated = true;
					} catch (Exception e) {
						log.error(
								"--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
						e.printStackTrace();

						// Inventory Error Handling
						InventoryTrans newInventoryTrans = new InventoryTrans();
						BeanUtils.copyProperties(inventory2, newInventoryTrans,
								CommonUtils.getNullPropertyNames(inventory2));
						newInventoryTrans.setInventoryQuantity(INV_QTY_ERR);
						newInventoryTrans.setReRun(0L);
						InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
						log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
						isInventoryCreated = false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Existing Inventory---Error-----> : " + e.toString());
			isInventoryCreated = false;
		}

		try {
			log.info("-------Create Inventory bin Class Id 1 Initiated-----");
			InventoryV2 inventory = new InventoryV2();
			BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

			inventory.setCompanyCodeId(putAwayLine.getCompanyCode());
			inventory.setVariantCode(1L); // VAR_ID
			inventory.setVariantSubCode("1"); // VAR_SUB_ID
			inventory.setStorageMethod("1"); // STR_MTD
			inventory.setBatchSerialNumber("1"); // STR_NO
			inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
			inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
			inventory.setBarcodeId(putAwayLine.getBarcodeId());
			inventory.setManufacturerName(putAwayLine.getManufacturerName());
			inventory.setManufacturerCode(putAwayLine.getManufacturerName());
			inventory.setReferenceField9(putAwayLine.getManufacturerName());
			inventory.setDescription(putAwayLine.getDescription());
			inventory.setReferenceField8(putAwayLine.getDescription());

			// ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value
			// and update
			AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
			StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
			storageBinPutAway.setCompanyCodeId(putAwayLine.getCompanyCode());
			storageBinPutAway.setPlantId(putAwayLine.getPlantId());
			storageBinPutAway.setLanguageId(putAwayLine.getLanguageId());
			storageBinPutAway.setWarehouseId(putAwayLine.getWarehouseId());
			storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());

			StorageBinV2 storageBin = null;
			try {
				storageBin = mastersService.getaStorageBinV2(storageBinPutAway,
						authTokenForMastersService.getAccess_token());
				log.info("storageBin: " + storageBin);
			} catch (Exception e) {
				throw new BadRequestException("Invalid StorageBin");
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
			String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
					putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), 1L);
			inventory.setStockTypeDescription(stockTypeDesc);
			log.info("StockTypeDescription: " + stockTypeDesc);

			// SP_ST_IND_ID
			inventory.setSpecialStockIndicatorId(1L);

			InventoryV2 existingInventory = inventoryService.getInventoryForInhouseTransferV2(
					putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
					putAwayLine.getWarehouseId(), "99999", putAwayLine.getItemCode(), putAwayLine.getManufacturerName(),
					putAwayLine.getConfirmedStorageBin());

			Double ALLOC_QTY = 0D;
			if (existingInventory != null) {
				if (existingInventory.getAllocatedQuantity() != null) {
					ALLOC_QTY = existingInventory.getAllocatedQuantity();
					inventory.setAllocatedQuantity(ALLOC_QTY);
				}
				if (existingInventory.getAllocatedQuantity() == null) {
					inventory.setAllocatedQuantity(ALLOC_QTY);
				}
				log.info("Inventory Allocated Qty: " + ALLOC_QTY);
			}
			// INV_QTY
			if (existingInventory != null) {
				inventory.setInventoryQuantity(
						existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty());
				log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", "
						+ putAwayLine.getPutawayConfirmedQty());
				Double totalQty = inventory.getInventoryQuantity() + inventory.getAllocatedQuantity();
				inventory.setReferenceField4(totalQty);
				log.info("Inventory Total Qty: " + totalQty);
			}
			if (existingInventory == null) {
				inventory.setInventoryQuantity(putAwayLine.getPutawayConfirmedQty());
				log.info("Inventory Qty = pa_cnf_qty: " + putAwayLine.getPutawayConfirmedQty());
				Double totalQty = putAwayLine.getPutawayConfirmedQty() + ALLOC_QTY;
				inventory.setReferenceField4(totalQty);
				log.info("Inventory Total Qty: " + totalQty);
			}

			/*
			 * Hardcoding Packbarcode as 99999
			 */
			inventory.setPackBarcodes("99999");

			// INV_UOM
			if (putAwayLine.getPutAwayUom() != null) {
				inventory.setInventoryUom(putAwayLine.getPutAwayUom());
				log.info("PA UOM: " + putAwayLine.getPutAwayUom());
			}
			inventory.setCreatedBy(putAwayLine.getCreatedBy());

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
			InventoryV2 createdinventory = null;
			inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 1));
			try {
				createdinventory = inventoryV2Repository.save(inventory);
				log.error("----createdinventory--------- :" + createdinventory);
				isInventoryCreated = true;
			} catch (Exception e) {
				log.error("--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
				e.printStackTrace();

				// Inventory Error Handling
				InventoryTrans newInventoryTrans = new InventoryTrans();
				BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
				newInventoryTrans.setReRun(0L);
				InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
				log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
				isInventoryCreated = false;
			}

			log.info("created inventory : executed" + createdinventory + " -----> ");
		} catch (Exception e) {
			log.error("Error While Creating Inventory ---> " + e.toString());
			e.printStackTrace();
			isInventoryCreated = false;
			sendMail(putAwayLine, e.getLocalizedMessage());
			throw e;
		}
		return isInventoryCreated;
	}

	@Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT2M")
	private void updateErroredOutInventory() {
		List<InventoryTrans> inventoryTransList = inventoryTransRepository.findByReRun(0L);
		inventoryTransList.stream().forEach(it -> {
			log.info("----updateErroredOutInventory-->: " + it);
			inventoryV2Repository.updateInventory(it.getWarehouseId(), it.getPackBarcodes(), it.getItemCode(),
					it.getStorageBin(), it.getInventoryQuantity(), it.getAllocatedQuantity());
			it.setReRun(1L);
			inventoryTransRepository.save(it);
			log.info("----updateInventoryTrans-is-done-->: " + it);
		});
    }

	/**
	 *
	 * @param putAwayLine
	 * @return
	 */
//	@Transactional(rollbackFor = { Exception.class, Throwable.class })
////@Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
//	private boolean createInventoryNonCBMV2(PutAwayLineV2 putAwayLine) throws Exception {
//		inventoryError = false;
//		alreadyExecuted = false;
//		log.info("Create Inventory Initiated ---> alreadyExecuted ---> " + new Date() + ", " + alreadyExecuted);
//		String palletCode = null;
//		String caseCode = null;
//		try {
//			InventoryV2 existinginventory = inventoryService.getInventoryForStockAdjustmentDamageV2(
//					putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
//					putAwayLine.getWarehouseId(), putAwayLine.getItemCode(), "99999", 3L,
//					putAwayLine.getManufacturerName());
//
//			if (existinginventory != null) {
//				log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
//				double INV_QTY = existinginventory.getInventoryQuantity() - putAwayLine.getPutawayConfirmedQty();
//				double INV_QTY_ERR = INV_QTY;
//				log.info("INV_QTY : " + INV_QTY);
//
//				if (INV_QTY >= 0) {
//
//					InventoryV2 inventory2 = new InventoryV2();
//					try {
//						BeanUtils.copyProperties(existinginventory, inventory2,
//								CommonUtils.getNullPropertyNames(existinginventory));
//						String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
//								putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(),
//								existinginventory.getStockTypeId());
//						inventory2.setStockTypeDescription(stockTypeDesc);
//						inventory2.setInventoryQuantity(INV_QTY);
//						inventory2.setReferenceField4(INV_QTY); // Allocated Qty is always 0 for BinClassId 3
//						log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);
//
//						palletCode = existinginventory.getPalletCode();
//						caseCode = existinginventory.getCaseCode();
//
//						inventory2.setCreatedOn(existinginventory.getCreatedOn());
//						inventory2.setUpdatedOn(new Date());
//						if (!alreadyExecuted) {
//							inventory2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
//							InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
//							log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
//						}
//					} catch (Exception e) {
//						log.error(
//								"--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
//						e.printStackTrace();
//
//						// Inventory Error Handling
//						InventoryTrans newInventoryTrans = new InventoryTrans();
//						BeanUtils.copyProperties(inventory2, newInventoryTrans,
//								CommonUtils.getNullPropertyNames(inventory2));
//						newInventoryTrans.setInventoryQuantity(INV_QTY_ERR);
//						newInventoryTrans.setReRun(0L);
//						InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
//						log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.info("Existing Inventory---Error-----> : " + e.toString());
//		}
//
//		try {
//			log.info("Create Inventory bin Class Id 1 Initiated---->alreadyExecuted---> " + new Date() + ", "
//					+ alreadyExecuted);
//			InventoryV2 inventory = new InventoryV2();
//			BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));
//
//			inventory.setCompanyCodeId(putAwayLine.getCompanyCode());
//
//// VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
//			inventory.setVariantCode(1L); // VAR_ID
//			inventory.setVariantSubCode("1"); // VAR_SUB_ID
//			inventory.setStorageMethod("1"); // STR_MTD
//			inventory.setBatchSerialNumber("1"); // STR_NO
//			inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
//			inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
//			inventory.setBarcodeId(putAwayLine.getBarcodeId());
//			inventory.setManufacturerName(putAwayLine.getManufacturerName());
//			inventory.setManufacturerCode(putAwayLine.getManufacturerName());
//			inventory.setReferenceField9(putAwayLine.getManufacturerName());
//			inventory.setDescription(putAwayLine.getDescription());
//			inventory.setReferenceField8(putAwayLine.getDescription());
//
//// ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value
//// and update
//			AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//			StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//			storageBinPutAway.setCompanyCodeId(putAwayLine.getCompanyCode());
//			storageBinPutAway.setPlantId(putAwayLine.getPlantId());
//			storageBinPutAway.setLanguageId(putAwayLine.getLanguageId());
//			storageBinPutAway.setWarehouseId(putAwayLine.getWarehouseId());
//			storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());
//
//			StorageBinV2 storageBin = null;
//			try {
//				storageBin = mastersService.getaStorageBinV2(storageBinPutAway,
//						authTokenForMastersService.getAccess_token());
//			} catch (Exception e) {
//				throw new BadRequestException("Invalid StorageBin");
//			}
//			log.info("storageBin: " + storageBin);
//
//			if (storageBin != null) {
//				inventory.setReferenceField10(storageBin.getStorageSectionId());
//				inventory.setReferenceField5(storageBin.getAisleNumber());
//				inventory.setReferenceField6(storageBin.getShelfId());
//				inventory.setReferenceField7(storageBin.getRowId());
//				inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
//				inventory.setBinClassId(storageBin.getBinClassId());
//			}
//
//			inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
//			inventory.setPlantDescription(putAwayLine.getPlantDescription());
//			inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());
//
//			inventory.setPalletCode(palletCode);
//			inventory.setCaseCode(caseCode);
//			log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);
//
//// STCK_TYP_ID
//			inventory.setStockTypeId(1L);
//			String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
//					putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), 1L);
//			inventory.setStockTypeDescription(stockTypeDesc);
//			log.info("StockTypeDescription: " + stockTypeDesc);
//
//// SP_ST_IND_ID
//			inventory.setSpecialStockIndicatorId(1L);
//
//			InventoryV2 existingInventory = inventoryService.getInventoryForInhouseTransferV2(
//					putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
//					putAwayLine.getWarehouseId(), "99999", putAwayLine.getItemCode(), putAwayLine.getManufacturerName(),
//					putAwayLine.getConfirmedStorageBin());
//
//			Double ALLOC_QTY = 0D;
//			if (existingInventory != null) {
//				if (existingInventory.getAllocatedQuantity() != null) {
//					ALLOC_QTY = existingInventory.getAllocatedQuantity();
//					inventory.setAllocatedQuantity(ALLOC_QTY);
//				}
//				if (existingInventory.getAllocatedQuantity() == null) {
//					inventory.setAllocatedQuantity(ALLOC_QTY);
//				}
//				log.info("Inventory Allocated Qty: " + ALLOC_QTY);
//			}
//// INV_QTY
//			if (existingInventory != null) {
//				inventory.setInventoryQuantity(
//						existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty());
//				log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", "
//						+ putAwayLine.getPutawayConfirmedQty());
//				Double totalQty = inventory.getInventoryQuantity() + inventory.getAllocatedQuantity();
//				inventory.setReferenceField4(totalQty);
//				log.info("Inventory Total Qty: " + totalQty);
//			}
//			if (existingInventory == null) {
//				inventory.setInventoryQuantity(putAwayLine.getPutawayConfirmedQty());
//				log.info("Inventory Qty = pa_cnf_qty: " + putAwayLine.getPutawayConfirmedQty());
//				Double totalQty = putAwayLine.getPutawayConfirmedQty() + ALLOC_QTY;
//				inventory.setReferenceField4(totalQty);
//				log.info("Inventory Total Qty: " + totalQty);
//			}
//
//			/*
//			 * Hardcoding Packbarcode as 99999
//			 */
//			inventory.setPackBarcodes("99999");
//
//// INV_UOM
//			if (putAwayLine.getPutAwayUom() != null) {
//				inventory.setInventoryUom(putAwayLine.getPutAwayUom());
//				log.info("PA UOM: " + putAwayLine.getPutAwayUom());
//			}
//			inventory.setCreatedBy(putAwayLine.getCreatedBy());
//
//			inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
//			inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
//			inventory.setDeletionIndicator(0L);
//
//			if (existingInventory != null) {
//				inventory.setCreatedOn(existingInventory.getCreatedOn());
//			}
//
//			if (existingInventory == null) {
//				inventory.setCreatedOn(new Date());
//			}
//			inventory.setUpdatedOn(new Date());
//			InventoryV2 createdinventory = null;
//			if (!alreadyExecuted) {
//				inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 1));
//				try {
//					createdinventory = inventoryV2Repository.save(inventory);
//				} catch (Exception e) {
//					log.error("--ERROR--createInventoryNonCBMV2 ----level1--inventory--error----> :" + e.toString());
//					e.printStackTrace();
//
//// Inventory Error Handling
//					InventoryTrans newInventoryTrans = new InventoryTrans();
//					BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
//					newInventoryTrans.setReRun(0L);
//					InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
//					log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
//				}
//				alreadyExecuted = true; // to ensure method executing only once
//				log.info("created inventory : executed" + createdinventory + " -----> " + alreadyExecuted);
//			}
//			return false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			inventoryError = true;
//			inventoryErrorCount++;
//			log.error("Error While Creating Inventory --> errorCount ---> " + inventoryErrorCount);
//			if (inventoryErrorCount == 3) {
//				sendMail(putAwayLine, e.getLocalizedMessage());
//			}
//			throw e;
//		}
//	}
//
//    @Transactional
//    public AXApiResponse updateInboundHeaderPartialConfirmNewV2(List<InboundLineV2> inboundLineList, String companyCode, String plantId, String languageId,
//                                                                String warehouseId, String preInboundNo, String refDocNumber, String loginUserID) {
//
//        try {
//        log.info("Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
//        // PutawayHeader Validation
//        long putAwayHeaderStatusIdCount = putAwayHeaderService.getPutawayHeaderByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
//        log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);
//
//        if (putAwayHeaderStatusIdCount != 0) {
//            throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: "  + refDocNumber);
//        }
//
//        AXApiResponse axapiResponse = new AXApiResponse();
//        statusDescription = stagingLineV2Repository.getStatusDescription(24L, languageId);
//        log.info("InboundLine List: " + inboundLineList.size());
//        List<PutAwayLineV2> putAwayLineList = null;
//        if (inboundLineList != null && !inboundLineList.isEmpty()) {
//            for (InboundLineV2 inboundLine : inboundLineList) {
//                log.info("Input InboundLine : " + inboundLine);
//                if(inboundLine.getStatusId() == 20L) {
//                    InboundLineV2 inboundLineV2 = inboundLineService.getInboundLineForInboundConfirmPartialConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
//                            inboundLine.getPreInboundNo(),
//                            inboundLine.getItemCode(),
//                            inboundLine.getManufacturerName(),
//                            inboundLine.getLineNo());
//                    if(inboundLineV2 != null) {
//                        putAwayLineList = putAwayLineService.
//                                getPutAwayLineForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
//                                        inboundLine.getItemCode(),
//                                        inboundLine.getManufacturerName(),
//                                        inboundLine.getLineNo(),
//                                        inboundLine.getPreInboundNo());
//                        log.info("PutawayLine List: " + putAwayLineList.size());
//                        if (putAwayLineList != null) {
//                            for (PutAwayLineV2 putAwayLine : putAwayLineList) {
//                                boolean createdInventory = createInventoryNonCBMV2(putAwayLine);
//                                createInventoryMovementV2(putAwayLine);
//                            }
//                            log.info("Inventory Created Successfully -----> for Inbound Line ----> " +
//                                    inboundLine.getItemCode() + ", " + inboundLine.getManufacturerName() + ", " + inboundLine.getLineNo());
//                        }
//                    }
//                }
//                inboundLineV2Repository.updateInboundLineStatusUpdateInboundConfirmIndividualItemProc(
//                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
//                            inboundLine.getItemCode(), inboundLine.getManufacturerName(), inboundLine.getLineNo(),   24L, statusDescription, loginUserID, new Date());
//                log.info("InboundLine status updated: " + inboundLine.getItemCode() + ", " + inboundLine.getManufacturerName() + ", " + inboundLine.getLineNo());
//
//		        putAwayLineV2Repository.updatePutawayLineStatusUpdateInboundConfirmProc(
//		                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
//		        log.info("putAwayLine updated");
//            }
//        }
//
////        putAwayHeaderV2Repository.updatepaheaderStatusUpdateInboundConfirmProc(
////                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
////        log.info("PutAwayHeader Updated");
////
////        preInboundLineV2Repository.updatePreInboundLineStatusUpdateInboundConfirmProc(
////                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
////                log.info("PreInboundLine updated");
////
////        grLineV2Repository.updateGrLineStatusUpdateInboundConfirmProc(
////                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
////        log.info("GrLine updated");
//        log.info("inventoryError: " + inventoryError + "|" + refDocNumber);
//        if(!inventoryError) {
//        String statusDescription17 = stagingLineV2Repository.getStatusDescription(17L,languageId);
////        stagingLineV2Repository.updateStagingLineStatusUpdateInboundConfirmProc(
////                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 17L, statusDescription17, loginUserID, new Date());
////                log.info("StagingLine updated");
//            //Multiple Stored Procedure replaced with Single Procedure Call
//            inboundHeaderV2Repository.updatePahGrlStglPiblStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
//                                                                                        24L, 17L, statusDescription, statusDescription17, loginUserID, new Date());
//            log.info("PutawayHeader, GrLine, Stg Line, PreIbLine Status updated using stored procedure");
//
//        Long inboundLinesV2CountForInboundConfirmWithStatusId = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirmWithStatusId(
//                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L);
//        Long inboundLinesV2CountForInboundConfirm = inboundLineV2Repository.getInboundLinesV2CountForInboundConfirm(
//                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
//        if(inboundLinesV2CountForInboundConfirmWithStatusId == null) {
//            inboundLinesV2CountForInboundConfirmWithStatusId = 0L;
//        }
//        if(inboundLinesV2CountForInboundConfirm == null) {
//            inboundLinesV2CountForInboundConfirm = 0L;
//        }
//        boolean isConditionMet = inboundLinesV2CountForInboundConfirmWithStatusId.equals(inboundLinesV2CountForInboundConfirm);
//        log.info("Inbound Line 24_StatusCount, Line Count: " + refDocNumber + ", " + isConditionMet + ", " + inboundLinesV2CountForInboundConfirmWithStatusId + ", " + inboundLinesV2CountForInboundConfirm);
//        if(isConditionMet) {
//    //            inboundHeaderV2Repository.updateIbheaderStatusUpdateInboundConfirmProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
//    //            log.info("InboundHeader updated");
//
//    //            preInboundHeaderV2Repository.updatePreIbheaderStatusUpdateInboundConfirmProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
//    //            log.info("PreInboundHeader updated");
//
//    //            grHeaderV2Repository.updateGrheaderStatusUpdateInboundConfirmProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
//    //            log.info("grHeader updated");
//
//    //            stagingHeaderV2Repository.updateStagingheaderStatusUpdateInboundConfirmProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
//    //            log.info("stagingHeader updated");
//
//                //Multiple Stored Procedure replaced with Single Procedure Call
//                inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
//                log.info("Header Status updated using stored procedure");
//        }
//        }
//        axapiResponse.setStatusCode("200");                         //HardCoded
//        axapiResponse.setMessage("Success");                        //HardCoded
//        log.info("axapiResponse: " + axapiResponse);
//        return axapiResponse;
//        } catch (Exception e) {
//            throw new BadRequestException("Inbound confirmation : Exception ----> " + e.toString());
//        }
//    }
//
//    /**
//     * @param putAwayLine
//     * @return
//     */
//    private InventoryV2 createInventoryV2(PutAwayLineV2 putAwayLine, String quantityType) {
//        log.info("Create Inventory Initiated: " + new Date());
//        String palletCode = null;
//        String caseCode = null;
//        try {
//            InventoryV2 existinginventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
//                    putAwayLine.getCompanyCode(),
//                    putAwayLine.getPlantId(),
//                    putAwayLine.getLanguageId(),
//                    putAwayLine.getWarehouseId(),
//                    putAwayLine.getItemCode(),
//                    putAwayLine.getManufacturerName(),
//                    "99999", 3L,0L);
//
//            if (existinginventory != null) {
//                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
//                double INV_QTY = existinginventory.getInventoryQuantity() - putAwayLine.getPutawayConfirmedQty();
//                log.info("INV_QTY : " + INV_QTY);
//
//                if (INV_QTY >= 0) {
//
//                    InventoryV2 inventory2 = new InventoryV2();
//                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
//                    String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
//                            putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), existinginventory.getStockTypeId());
//                    inventory2.setStockTypeDescription(stockTypeDesc);
//                    inventory2.setInventoryQuantity(INV_QTY);
//                    inventory2.setReferenceField4(INV_QTY);         //Allocated Qty is always 0 for BinClassId 3
//                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);
//
//                    palletCode = existinginventory.getPalletCode();
//                    caseCode = existinginventory.getCaseCode();
//
//                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
//                    inventory2.setUpdatedOn(new Date());
//                    inventory2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
//                    InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
//                    log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("Existing Inventory---Error-----> : " + e.toString());
//        }
//
//        try {
//            log.info("Create Inventory bin Class Id 1 Initiated: " + new Date());
//            InventoryV2 inventory = new InventoryV2();
//            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));
//
//            inventory.setCompanyCodeId(putAwayLine.getCompanyCode());
//
//            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
//            inventory.setVariantCode(1L);                // VAR_ID
//            inventory.setVariantSubCode("1");            // VAR_SUB_ID
//            inventory.setStorageMethod("1");            // STR_MTD
//            inventory.setBatchSerialNumber("1");        // STR_NO
//            inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
//            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
//            inventory.setBarcodeId(putAwayLine.getBarcodeId());
//            inventory.setManufacturerName(putAwayLine.getManufacturerName());
//
////            Long binClassId = 0L;
////            if ((putAwayLine.getInboundOrderTypeId() == 1 ||
////                    putAwayLine.getInboundOrderTypeId() == 3 ||
////                    putAwayLine.getInboundOrderTypeId() == 4 ||
////                    putAwayLine.getInboundOrderTypeId() == 5) &&
////                    (quantityType.equalsIgnoreCase("A"))) {
////                binClassId = 1L;
////            }
////            if ((putAwayLine.getInboundOrderTypeId() == 1 ||
////                    putAwayLine.getInboundOrderTypeId() == 3 ||
////                    putAwayLine.getInboundOrderTypeId() == 4 ||
////                    putAwayLine.getInboundOrderTypeId() == 5) &&
////                    (quantityType.equalsIgnoreCase("D"))) {
////                binClassId = 7L;
////            }
////            if (putAwayLine.getInboundOrderTypeId() == 2) {
////                binClassId = 7L;
////            }
//
//            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
//            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//            storageBinPutAway.setCompanyCodeId(putAwayLine.getCompanyCode());
//            storageBinPutAway.setPlantId(putAwayLine.getPlantId());
//            storageBinPutAway.setLanguageId(putAwayLine.getLanguageId());
//            storageBinPutAway.setWarehouseId(putAwayLine.getWarehouseId());
//            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());
//
//            StorageBinV2 storageBin = null;
//            try {
//                storageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
//            } catch (Exception e) {
//                throw new BadRequestException("Invalid StorageBin");
//            }
//            log.info("storageBin: " + storageBin);
//
//            ImBasicData imBasicData = new ImBasicData();
//            imBasicData.setCompanyCodeId(putAwayLine.getCompanyCode());
//            imBasicData.setPlantId(putAwayLine.getPlantId());
//            imBasicData.setLanguageId(putAwayLine.getLanguageId());
//            imBasicData.setWarehouseId(putAwayLine.getWarehouseId());
//            imBasicData.setItemCode(putAwayLine.getItemCode());
//            imBasicData.setManufacturerName(putAwayLine.getManufacturerName());
//            ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
//            log.info("ImbasicData1 : " + itemCodeCapacityCheck);
//
//            if (itemCodeCapacityCheck != null) {
//                inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
//                inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
//                inventory.setManufacturerCode(itemCodeCapacityCheck.getManufacturerPartNo());
//                inventory.setDescription(itemCodeCapacityCheck.getDescription());
//            }
//            if (storageBin != null) {
//                inventory.setReferenceField10(storageBin.getStorageSectionId());
//                inventory.setReferenceField5(storageBin.getAisleNumber());
//                inventory.setReferenceField6(storageBin.getShelfId());
//                inventory.setReferenceField7(storageBin.getRowId());
//                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
//                inventory.setBinClassId(storageBin.getBinClassId());
//            }
//
//            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
//            inventory.setPlantDescription(putAwayLine.getPlantDescription());
//            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());
//
//                inventory.setPalletCode(palletCode);
//                inventory.setCaseCode(caseCode);
//                log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);
//
//            // STCK_TYP_ID
//            inventory.setStockTypeId(1L);
//            String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), 1L);
//            inventory.setStockTypeDescription(stockTypeDesc);
//            log.info("StockTypeDescription: " + stockTypeDesc);
//
//            // SP_ST_IND_ID
//            inventory.setSpecialStockIndicatorId(1L);
//
//            InventoryV2 existingInventory = inventoryService.getInventoryForInhouseTransferV2(
//                    putAwayLine.getCompanyCode(),
//                    putAwayLine.getPlantId(),
//                    putAwayLine.getLanguageId(),
//                    putAwayLine.getWarehouseId(),
//                    "99999",
//                    putAwayLine.getItemCode(),
//                    putAwayLine.getManufacturerName(),
//                    putAwayLine.getConfirmedStorageBin()
//            );
//
//            Double ALLOC_QTY = 0D;
//            if(existingInventory != null) {
//                if (existingInventory.getAllocatedQuantity() != null) {
//                    ALLOC_QTY = existingInventory.getAllocatedQuantity();
//                    inventory.setAllocatedQuantity(ALLOC_QTY);
//                }
//                if (existingInventory.getAllocatedQuantity() == null) {
//                    inventory.setAllocatedQuantity(ALLOC_QTY);
//                }
//                log.info("Inventory Allocated Qty: " + ALLOC_QTY);
//            }
//            // INV_QTY
//            if (existingInventory != null) {
//                inventory.setInventoryQuantity(existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty());
//                log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", " + putAwayLine.getPutawayConfirmedQty());
//                Double totalQty = inventory.getInventoryQuantity() + inventory.getAllocatedQuantity();
//                inventory.setReferenceField4(totalQty);
//                log.info("Inventory Total Qty: " + totalQty);
//            }
//            if (existingInventory == null) {
//                inventory.setInventoryQuantity(putAwayLine.getPutawayConfirmedQty());
//                log.info("Inventory Qty = pa_cnf_qty: " + putAwayLine.getPutawayConfirmedQty());
//                Double totalQty = putAwayLine.getPutawayConfirmedQty() + ALLOC_QTY;
//                inventory.setReferenceField4(totalQty);
//                log.info("Inventory Total Qty: " + totalQty);
//            }
//
//            //packbarcode
//            /*
//             * Hardcoding Packbarcode as 99999
//             */
////            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
//            inventory.setPackBarcodes("99999");
//
//            // INV_UOM
//            if (putAwayLine.getPutAwayUom() != null) {
//                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
//                log.info("PA UOM: " + putAwayLine.getPutAwayUom());
//            }
//            inventory.setCreatedBy(putAwayLine.getCreatedBy());
//
//            //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
//            boolean capacityCheck = false;
//            Double invQty = 0D;
//            Double cbm = 0D;
//            Double cbmPerQty = 0D;
//            Double invCbm = 0D;
//            if(itemCodeCapacityCheck != null) {
//                if (itemCodeCapacityCheck.getCapacityCheck() != null) {
//                    log.info("CBM Check");
//                    capacityCheck = itemCodeCapacityCheck.getCapacityCheck();               //Capacity Check for putaway item
//                }
//            }
//            log.info("CapacityCheck -----------> : " + capacityCheck);
//
//            if (capacityCheck) {
//                if (putAwayLine.getCbmQuantity() != null) {
//                    inventory.setCbmPerQuantity(String.valueOf(putAwayLine.getCbmQuantity()));
//                }
//                if (putAwayLine.getPutawayConfirmedQty() != null) {
//                    invQty = putAwayLine.getPutawayConfirmedQty();
//                }
//                if (putAwayLine.getCbmQuantity() == null) {
//
//                    if (putAwayLine.getCbm() != null) {
//                        cbm = Double.valueOf(putAwayLine.getCbm());
//                    }
//                    cbmPerQty = cbm / invQty;
//                    inventory.setCbmPerQuantity(String.valueOf(cbmPerQty));
//                }
//                if (putAwayLine.getCbm() != null) {
//                    invCbm = Double.valueOf(putAwayLine.getCbm());
//                }
//                if (putAwayLine.getCbm() == null) {
//                    invCbm = invQty * Double.valueOf(inventory.getCbmPerQuantity());
//                }
//                inventory.setCbm(String.valueOf(invCbm));
//            }
//
//            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
//            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
//            inventory.setDeletionIndicator(0L);
//
//            if(existingInventory != null) {
//                inventory.setCreatedOn(existingInventory.getCreatedOn());
//            }
//            if(existingInventory == null) {
//                inventory.setCreatedOn(new Date());
//            }
//            inventory.setUpdatedOn(new Date());
//            inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 1));
//            InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
//            log.info("created inventory : " + createdinventory);
//            return createdinventory;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new BadRequestException("Error While Creating Inventory");
//        }
//    }
//
//    /**
//     *
//     * @param putAwayLine
//     * @return
//     */
//    @Transactional(rollbackFor = {Exception.class, Throwable.class})
//    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
//    private boolean createInventoryNonCBMV2(PutAwayLineV2 putAwayLine) throws Exception {
//        inventoryError = false;
//        alreadyExecuted = false;
//        log.info("Create Inventory Initiated ---> alreadyExecuted ---> " + new Date() + ", " + alreadyExecuted);
//        String palletCode = null;
//        String caseCode = null;
//        try {
////            InventoryV2 existinginventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
////                    putAwayLine.getCompanyCode(),
////                    putAwayLine.getPlantId(),
////                    putAwayLine.getLanguageId(),
////                    putAwayLine.getWarehouseId(),
////                    putAwayLine.getItemCode(),
////                    putAwayLine.getManufacturerName(),
////                    "99999", 3L,0L);
//            InventoryV2 existinginventory = inventoryService.getInventoryForStockAdjustmentDamageV2(
//                    putAwayLine.getCompanyCode(),
//                    putAwayLine.getPlantId(),
//                    putAwayLine.getLanguageId(),
//                    putAwayLine.getWarehouseId(),
//                    putAwayLine.getItemCode(),
//                    "99999", 3L,
//                    putAwayLine.getManufacturerName());
//
//            if (existinginventory != null) {
//                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
//                double INV_QTY = existinginventory.getInventoryQuantity() - putAwayLine.getPutawayConfirmedQty();
//                log.info("INV_QTY : " + INV_QTY);
//
//                if (INV_QTY >= 0) {
//
//                    InventoryV2 inventory2 = new InventoryV2();
//                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
//                    String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(),
//                            putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), existinginventory.getStockTypeId());
//                    inventory2.setStockTypeDescription(stockTypeDesc);
//                    inventory2.setInventoryQuantity(INV_QTY);
//                    inventory2.setReferenceField4(INV_QTY);         //Allocated Qty is always 0 for BinClassId 3
//                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);
//
//                    palletCode = existinginventory.getPalletCode();
//                    caseCode = existinginventory.getCaseCode();
//
//                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
//                    inventory2.setUpdatedOn(new Date());
//                    if(!alreadyExecuted) {
//                    inventory2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
//                    InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
//                    log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
//                }
//            }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("Existing Inventory---Error-----> : " + e.toString());
//        }
//
//        try {
//
//            log.info("Create Inventory bin Class Id 1 Initiated---->alreadyExecuted---> " + new Date() + ", " + alreadyExecuted);
//            InventoryV2 inventory = new InventoryV2();
//            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));
//
//            inventory.setCompanyCodeId(putAwayLine.getCompanyCode());
//
//            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
//            inventory.setVariantCode(1L);                // VAR_ID
//            inventory.setVariantSubCode("1");            // VAR_SUB_ID
//            inventory.setStorageMethod("1");            // STR_MTD
//            inventory.setBatchSerialNumber("1");        // STR_NO
//            inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
//            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
//            inventory.setBarcodeId(putAwayLine.getBarcodeId());
//            inventory.setManufacturerName(putAwayLine.getManufacturerName());
//            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
//            inventory.setReferenceField9(putAwayLine.getManufacturerName());
//            inventory.setDescription(putAwayLine.getDescription());
//            inventory.setReferenceField8(putAwayLine.getDescription());
//
//
//            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
//            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//            storageBinPutAway.setCompanyCodeId(putAwayLine.getCompanyCode());
//            storageBinPutAway.setPlantId(putAwayLine.getPlantId());
//            storageBinPutAway.setLanguageId(putAwayLine.getLanguageId());
//            storageBinPutAway.setWarehouseId(putAwayLine.getWarehouseId());
//            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());
//
//            StorageBinV2 storageBin = null;
//            try {
//                storageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
//            } catch (Exception e) {
//                throw new BadRequestException("Invalid StorageBin");
//            }
//            log.info("storageBin: " + storageBin);
//
//
//            if (storageBin != null) {
//                inventory.setReferenceField10(storageBin.getStorageSectionId());
//                inventory.setReferenceField5(storageBin.getAisleNumber());
//                inventory.setReferenceField6(storageBin.getShelfId());
//                inventory.setReferenceField7(storageBin.getRowId());
//                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
//                inventory.setBinClassId(storageBin.getBinClassId());
//            }
//
//            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
//            inventory.setPlantDescription(putAwayLine.getPlantDescription());
//            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());
//
//                inventory.setPalletCode(palletCode);
//                inventory.setCaseCode(caseCode);
//                log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);
//
//            // STCK_TYP_ID
//            inventory.setStockTypeId(1L);
//            String stockTypeDesc = getStockTypeDesc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), 1L);
//            inventory.setStockTypeDescription(stockTypeDesc);
//            log.info("StockTypeDescription: " + stockTypeDesc);
//
//            // SP_ST_IND_ID
//            inventory.setSpecialStockIndicatorId(1L);
//
//            InventoryV2 existingInventory = inventoryService.getInventoryForInhouseTransferV2(
//                    putAwayLine.getCompanyCode(),
//                    putAwayLine.getPlantId(),
//                    putAwayLine.getLanguageId(),
//                    putAwayLine.getWarehouseId(),
//                    "99999",
//                    putAwayLine.getItemCode(),
//                    putAwayLine.getManufacturerName(),
//                    putAwayLine.getConfirmedStorageBin()
//            );
//
//            Double ALLOC_QTY = 0D;
//            if(existingInventory != null) {
//                if (existingInventory.getAllocatedQuantity() != null) {
//                    ALLOC_QTY = existingInventory.getAllocatedQuantity();
//                    inventory.setAllocatedQuantity(ALLOC_QTY);
//                }
//                if (existingInventory.getAllocatedQuantity() == null) {
//                    inventory.setAllocatedQuantity(ALLOC_QTY);
//                }
//                log.info("Inventory Allocated Qty: " + ALLOC_QTY);
//            }
//            // INV_QTY
//            if (existingInventory != null) {
//                inventory.setInventoryQuantity(existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty());
//                log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", " + putAwayLine.getPutawayConfirmedQty());
//                Double totalQty = inventory.getInventoryQuantity() + inventory.getAllocatedQuantity();
//                inventory.setReferenceField4(totalQty);
//                log.info("Inventory Total Qty: " + totalQty);
//            }
//            if (existingInventory == null) {
//                inventory.setInventoryQuantity(putAwayLine.getPutawayConfirmedQty());
//                log.info("Inventory Qty = pa_cnf_qty: " + putAwayLine.getPutawayConfirmedQty());
//                Double totalQty = putAwayLine.getPutawayConfirmedQty() + ALLOC_QTY;
//                inventory.setReferenceField4(totalQty);
//                log.info("Inventory Total Qty: " + totalQty);
//            }
//
//            //packbarcode
//            /*
//             * Hardcoding Packbarcode as 99999
//             */
////            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
//            inventory.setPackBarcodes("99999");
//
//            // INV_UOM
//            if (putAwayLine.getPutAwayUom() != null) {
//                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
//                log.info("PA UOM: " + putAwayLine.getPutAwayUom());
//            }
//            inventory.setCreatedBy(putAwayLine.getCreatedBy());
//
//            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
//            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
//            inventory.setDeletionIndicator(0L);
//
//            if(existingInventory != null) {
//                inventory.setCreatedOn(existingInventory.getCreatedOn());
//            }
//            if(existingInventory == null) {
//                inventory.setCreatedOn(new Date());
//            }
//            inventory.setUpdatedOn(new Date());
//            InventoryV2 createdinventory = null;
//            if (!alreadyExecuted) {
//            inventory.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 1));
//            createdinventory = inventoryV2Repository.save(inventory);
//            alreadyExecuted = true;             //to ensure method executing only once
//                log.info("created inventory : executed" + createdinventory + " -----> " + alreadyExecuted);
//            }
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//            inventoryError = true;
//            inventoryErrorCount++;
//            log.error("Error While Creating Inventory --> errorCount ---> " + inventoryErrorCount);
//            if(inventoryErrorCount == 3) {
//                sendMail(putAwayLine, e.getLocalizedMessage());
//            }
//            throw e;
//        }
//    }

    /**
     *
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

        inventoryMovement.setManufacturerName(putAwayLineV2.getManufacturerName());
        inventoryMovement.setRefDocNumber(putAwayLineV2.getRefDocNumber());
        inventoryMovement.setCompanyDescription(putAwayLineV2.getCompanyDescription());
        inventoryMovement.setPlantDescription(putAwayLineV2.getPlantDescription());
        inventoryMovement.setWarehouseDescription(putAwayLineV2.getWarehouseDescription());
        inventoryMovement.setBarcodeId(putAwayLineV2.getBarcodeId());
        inventoryMovement.setDescription(putAwayLineV2.getDescription());
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
        if(sumOfInvQty != null) {
        inventoryMovement.setBalanceOHQty(sumOfInvQty);
            Double openQty = sumOfInvQty - putAwayLineV2.getPutawayConfirmedQty();
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
            log.info("OH Qty, OpenQty : " + sumOfInvQty + ", " + openQty);
        }
        if(sumOfInvQty == null) {
            inventoryMovement.setBalanceOHQty(0D);
//            Double openQty = sumOfInvQty - putAwayLineV2.getPutawayConfirmedQty();
            inventoryMovement.setReferenceField2("0");          //Qty before inventory Movement occur
            log.info("OH Qty, OpenQty : 0 , 0" );
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
     * POST ASN
     *
     * @param confirmedInboundHeader
     * @param confirmedInboundLines
     * @return
     */
    private AXApiResponse postASNV2(InboundHeaderV2 confirmedInboundHeader, List<InboundLineV2> confirmedInboundLines) throws ParseException {
        ASNHeaderV2 asnHeader = new ASNHeaderV2();
        asnHeader.setAsnNumber(confirmedInboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<ASNLineV2> asnLines = new ArrayList<>();
        for (InboundLineV2 inboundLine : confirmedInboundLines) {
//            asnHeader.setSupplierInvoice(inboundLine.getInvoiceNo());

            /*
             * AcceptQty not equal to null and greater than 0
             * OR
             * DamageQty not equal to null and greater than 0
             */
            if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
                    || (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
                inboundLine.setConfirmedOn(new Date());

                ASNLineV2 asnLine = new ASNLineV2();

                // SKU	<-	ITM_CODE
                asnLine.setSku(inboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                asnLine.setSkuDescription(inboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                asnLine.setLineReference(inboundLine.getLineNo());

                // Expected Qty	<- ORD_QTY
                asnLine.setExpectedQty(inboundLine.getOrderQty());

                // UOM	<-	ORD_UOM
                asnLine.setUom(inboundLine.getOrderUom());

                // Received Qty	<-	ACCEPT_QTY
//                if (inboundLine.getAcceptedQty() == null) {
//                    asnLine.setReceivedQty(0D);
//                } else {
//                    asnLine.setReceivedQty(inboundLine.getAcceptedQty());
//                }
//
//                // Damage Qty <-	DAMAGE_QTY
//                if (inboundLine.getDamageQty() == null) {
//                    asnLine.setDamagedQty(0D);
//                } else {
//                    asnLine.setDamagedQty(inboundLine.getDamageQty());
//                }

                // Pack Qty	<-	ITM_CASE_QTY
                asnLine.setPackQty(inboundLine.getItemCaseQty());

                // Actual Receipt Date	<-	IB_CNF_ON
//                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
//                asnLine.setActualReceiptDate(date);

                // Warehouse ID	<-	WH_ID
//                asnLine.setWareHouseId(inboundLine.getWarehouseId());

                asnLine.setManufacturerName(inboundLine.getManufacturerName());
                asnLine.setManufacturerCode(inboundLine.getManufacturerCode());
                asnLine.setUom(inboundLine.getOrderUom());
                if (inboundLine.getOrderQty() == null) {
                    asnLine.setExpectedQty(0D);
                } else {
                    asnLine.setExpectedQty(inboundLine.getOrderQty());
                }
                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getExpectedArrivalDate());
                asnLine.setExpectedDate(date);

                asnLines.add(asnLine);
            }
        }

        if (asnLines.isEmpty()) {
            throw new BadRequestException("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
        }

        ASNV2 asn = new ASNV2();
        asn.setAsnHeader(asnHeader);
        asn.setAsnLine(asnLines);
        log.info("Sending ASN : " + asn);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postASNConfirmationV2(asn, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(asnHeader.getAsnNumber());
        response.setOrderType("INBOUND");
        response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceAsnUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     * StoreReturn API
     *
     * @param confirmedInboundHeader
     * @param confirmedInboundLines
     * @return
     */
    private AXApiResponse postStoreReturnV2(InboundHeaderV2 confirmedInboundHeader,
                                            List<InboundLineV2> confirmedInboundLines) throws ParseException {
        StoreReturnHeader storeReturnHeader = new StoreReturnHeader();
        storeReturnHeader.setTransferOrderNumber(confirmedInboundHeader.getRefDocNumber());    // REF_DOC_NO
        List<StoreReturnLine> storeReturnLines = new ArrayList<>();
        for (InboundLine inboundLine : confirmedInboundLines) {
            /*
             * AcceptQty not equal to null and greater than 0
             * OR
             * DamageQty not equal to null and greater than 0
             */
            if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
                    || (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
                inboundLine.setConfirmedOn(new Date());
                StoreReturnLine storeReturnLine = new StoreReturnLine();

                // SKU	<-	ITM_CODE
                storeReturnLine.setSku(inboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                storeReturnLine.setSkuDescription(inboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                storeReturnLine.setLineReference(inboundLine.getLineNo());

                // Expected Qty	<- ORD_QTY
                storeReturnLine.setExpectedQty(inboundLine.getOrderQty());

                // UOM	<-	ORD_UOM
                storeReturnLine.setUom(inboundLine.getOrderUom());

                // Received Qty	<-	ACCEPT_QTY
                if (inboundLine.getAcceptedQty() == null) {
                    storeReturnLine.setReceivedQty(0D);
                } else {
                    storeReturnLine.setReceivedQty(inboundLine.getAcceptedQty());
                }

                // Damage Qty <-	DAMAGE_QTY
                if (inboundLine.getDamageQty() == null) {
                    storeReturnLine.setDamagedQty(0D);
                } else {
                    storeReturnLine.setDamagedQty(inboundLine.getDamageQty());
                }

                // Pack Qty	<-	ITM_CASE_QTY
                storeReturnLine.setPackQty(inboundLine.getItemCaseQty());

                // Actual Receipt Date	<-	IB_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
                storeReturnLine.setActualReceiptDate(date);

                // Warehouse ID	<-	WH_ID
                storeReturnLine.setWareHouseId(inboundLine.getWarehouseId());

                // Store ID <- PARTNER_CODE
                storeReturnLine.setStoreId(inboundLine.getVendorCode());

                storeReturnLines.add(storeReturnLine);
            }
        }

        if (storeReturnLines.isEmpty()) {
            throw new BadRequestException("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
        }

        StoreReturn storeReturn = new StoreReturn();
        storeReturn.setToHeader(storeReturnHeader);
        storeReturn.setToLines(storeReturnLines);
        log.info("Sending StoreReturn : " + storeReturn);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postStoreReturnConfirmation(storeReturn, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(storeReturnHeader.getTransferOrderNumber());
        response.setOrderType("INBOUND");
        response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceStoreReturnUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     * SO Return
     *
     * @param confirmedInboundHeader
     * @param confirmedInboundLines
     * @return
     */
    private AXApiResponse postSOReturnV2(InboundHeaderV2 confirmedInboundHeader, List<InboundLineV2> confirmedInboundLines) throws ParseException {
        SOReturnHeader soReturnHeader = new SOReturnHeader();
        soReturnHeader.setReturnOrderReference(confirmedInboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<SOReturnLine> soReturnLines = new ArrayList<>();
        for (InboundLine inboundLine : confirmedInboundLines) {
            /*
             * AcceptQty not equal to null and greater than 0
             * OR
             * DamageQty not equal to null and greater than 0
             */
            if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
                    || (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
                inboundLine.setConfirmedOn(new Date());
                SOReturnLine soReturnLine = new SOReturnLine();

                // Salesroderreference <-	REF_FIELD_4
                soReturnLine.setSalesOrderReference(inboundLine.getReferenceField4());

                // SKU	<-	ITM_CODE
                soReturnLine.setSku(inboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                soReturnLine.setSkuDescription(inboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                soReturnLine.setLineReference(inboundLine.getLineNo());

                // Expected Qty	<- ORD_QTY
                soReturnLine.setExpectedQty(inboundLine.getOrderQty());

                // UOM	<-	ORD_UOM
                soReturnLine.setUom(inboundLine.getOrderUom());

                // Received Qty	<-	ACCEPT_QTY
                if (inboundLine.getAcceptedQty() == null) {
                    soReturnLine.setReturnQty(0D);
                } else {
                    soReturnLine.setReturnQty(inboundLine.getAcceptedQty());
                }

                // Actual Receipt Date	<-	IB_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
                soReturnLine.setActualReceiptDate(date);

                // Warehouse ID	<-	WH_ID
                soReturnLine.setWareHouseId(inboundLine.getWarehouseId());

                soReturnLines.add(soReturnLine);
            }
        }

        if (soReturnLines.isEmpty()) {
            throw new BadRequestException("ConfirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
        }

        SOReturn soReturn = new SOReturn();
        soReturn.setReturnOrderHeader(soReturnHeader);
        soReturn.setReturnOrderLines(soReturnLines);
        log.info("Sending SOReturn : " + soReturn);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postSOReturnConfirmation(soReturn, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(soReturnHeader.getReturnOrderReference());
        response.setOrderType("INBOUND");
        response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceSOReturnUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     * InterWarehouse API
     *
     * @param confirmedInboundHeader
     * @param confirmedInboundLines
     * @return
     */
    private AXApiResponse postInterWarehouseV2(InboundHeaderV2 confirmedInboundHeader,
                                               List<InboundLineV2> confirmedInboundLines) throws ParseException {
        InterWarehouseTransferInHeaderV2 toHeader = new InterWarehouseTransferInHeaderV2();
        toHeader.setTransferOrderNumber(confirmedInboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<InterWarehouseTransferInLineV2> toLines = new ArrayList<>();
        for (InboundLineV2 inboundLine : confirmedInboundLines) {
            /*
             * AcceptQty not equal to null and greater than 0
             * OR
             * DamageQty not equal to null and greater than 0
             */
            if ((inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() > 0)
                    || (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() > 0)) {
                inboundLine.setConfirmedOn(new Date());
                InterWarehouseTransferInLineV2 iwhTransferLine = new InterWarehouseTransferInLineV2();

                // SKU	<-	ITM_CODE
                iwhTransferLine.setSku(inboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                iwhTransferLine.setSkuDescription(inboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                iwhTransferLine.setLineReference(inboundLine.getLineNo());

                // Expected Qty	<- ORD_QTY
                iwhTransferLine.setExpectedQty(inboundLine.getOrderQty());

                // UOM	<-	ORD_UOM
                iwhTransferLine.setUom(inboundLine.getOrderUom());

                // Received Qty	<-	ACCEPT_QTY
//                if (inboundLine.getAcceptedQty() == null) {
//                    iwhTransferLine.setReceivedQty(0D);
//                } else {
//                    iwhTransferLine.setReceivedQty(inboundLine.getAcceptedQty());
//                }

                // Damage Qty <-	DAMAGE_QTY
//                if (inboundLine.getDamageQty() == null) {
//                    iwhTransferLine.setDamageQty(0D);
//                } else {
//                    iwhTransferLine.setDamageQty(inboundLine.getDamageQty());
//                }
                InboundOrderLinesV2 dbInboundOrderLine = inboundOrderLinesV2Repository.findTopByOrderIdAndItemCode(inboundLine.getRefDocNumber(), inboundLine.getItemCode());
                // Pack Qty	<-	ITM_CASE_QTY
                iwhTransferLine.setPackQty(inboundLine.getItemCaseQty());

                // Actual Receipt Date	<-	IB_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(inboundLine.getConfirmedOn());
//                iwhTransferLine.setActualReceiptDate(date);

                // Warehouse ID	<-	WH_ID
//                iwhTransferLine.setToWhsId(inboundLine.getWarehouseId());

                // FromWhsID <-	PARTNER_CODE
//                iwhTransferLine.setFromWhsId(inboundLine.getVendorCode());

                iwhTransferLine.setManufacturerCode(inboundLine.getManufacturerCode());
                iwhTransferLine.setManufacturerName(inboundLine.getManufacturerName());
                iwhTransferLine.setFromBranchCode(dbInboundOrderLine.getSourceBranchCode());
                iwhTransferLine.setOrigin(dbInboundOrderLine.getOrigin());
                iwhTransferLine.setFromCompanyCode(dbInboundOrderLine.getFromCompanyCode());

                toLines.add(iwhTransferLine);
            }
        }

        if (toLines.isEmpty()) {
            throw new BadRequestException("confirmedInboundLines had neither AcceptQty nor DamageQty. Please check the data.");
        }

        InterWarehouseTransferInV2 interWarehouseTransfer = new InterWarehouseTransferInV2();
        interWarehouseTransfer.setInterWarehouseTransferInHeader(toHeader);
        interWarehouseTransfer.setInterWarehouseTransferInLine(toLines);
        log.info("Sending InterWarehouseTransfer : " + interWarehouseTransfer);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postInterWarehouseTransferConfirmationV2(interWarehouseTransfer, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(toHeader.getTransferOrderNumber());
        response.setOrderType("INBOUND");
        response.setOrderTypeId(confirmedInboundHeader.getInboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceInterwareHouseUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     *
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
     *
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
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,0L);
        log.info("InboundHeaderV2 - cancellation : " + inboundHeader);
        if (inboundHeader != null) {
            inboundHeader.setDeletionIndicator(1L);
            inboundHeader.setUpdatedBy(loginUserID);
            inboundHeader.setUpdatedOn(new Date());
            inboundHeaderV2Repository.save(inboundHeader);
        }
        return inboundHeader;
    }

    /**
     *
     * @param putAwayLine
     */
    private void sendMail(PutAwayLineV2 putAwayLine, String errorMessage) throws Exception {
        try {
            //Sending Failed Details through Mail
            InboundOrderCancelInput inboundOrderCancelInput = new InboundOrderCancelInput();
            inboundOrderCancelInput.setCompanyCodeId(putAwayLine.getCompanyCode());
            inboundOrderCancelInput.setPlantId(putAwayLine.getPlantId());
            inboundOrderCancelInput.setLanguageId(putAwayLine.getLanguageId());
            inboundOrderCancelInput.setWarehouseId(putAwayLine.getWarehouseId());
            inboundOrderCancelInput.setRefDocNumber(putAwayLine.getRefDocNumber());
            inboundOrderCancelInput.setReferenceField2(putAwayLine.getManufacturerName());
            inboundOrderCancelInput.setReferenceField1("Inbound Confirm - Inventory Exception");
            inboundOrderCancelInput.setRemarks(errorMessage);
            mastersService.sendMail(inboundOrderCancelInput);
        } catch (Exception e) {
            log.error("Exception while sending mail..!");
            throw e;
        }
    }

}