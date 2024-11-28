package com.tekclover.wms.api.transaction.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.*;
import com.tekclover.wms.api.transaction.model.errorlog.ErrorLog;
import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.UpdateInboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.*;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.SearchPreInboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.StagingHeader;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.PreInboundHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PreInboundHeaderV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PreInboundHeaderService extends BaseService {

    @Autowired
    private ImBasicData1V2Repository imBasicData1V2Repository;

    private static String WAREHOUSEID_NUMBERRANGE = "110";

    @Autowired
    private PreInboundHeaderRepository preInboundHeaderRepository;

    @Autowired
    private PreInboundLineRepository preInboundLineRepository;

    @Autowired
    private InboundHeaderRepository inboundHeaderRepository;

    @Autowired
    private InboundLineRepository inboundLineRepository;

    @Autowired
    private StagingHeaderRepository stagingHeaderRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private IDMasterService idmasterService;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private PreInboundLineService preInboundLineService;

    @Autowired
    private InboundHeaderService inboundHeaderService;

    @Autowired
    private InboundLineService inboundLineService;

    @Autowired
    InboundIntegrationLogRepository inboundIntegrationLogRepository;

    @Autowired
    OrderService orderService;

    //--------------------------------------------------------------------------------------------------------------
    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StagingLineService stagingLineService;

    @Autowired
    private StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    private InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    private PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    private ErrorLogRepository exceptionLogRepo;

    String statusDescription = null;
    //--------------------------------------------------------------------------------------------------------------

    /**
     * getPreInboundHeaders
     * @return
     */
    public List<PreInboundHeader> getPreInboundHeaders() {
        List<PreInboundHeaderEntity> preInboundHeaderEntityList = preInboundHeaderRepository.findAll();
        preInboundHeaderEntityList = preInboundHeaderEntityList.stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());

        List<PreInboundHeader> headerList = new ArrayList<PreInboundHeader>();
        for (PreInboundHeaderEntity preInboundHeaderEntity : preInboundHeaderEntityList) {
            List<PreInboundLineEntity> lineEntityList = preInboundLineRepository.findByPreInboundNoAndDeletionIndicator(preInboundHeaderEntity.getPreInboundNo(), 0L);

            List<PreInboundLine> preInboundLineList = new ArrayList<PreInboundLine>();
            for (PreInboundLineEntity lineEntity : lineEntityList) {
                PreInboundLine line = new PreInboundLine();
                BeanUtils.copyProperties(lineEntity, line, CommonUtils.getNullPropertyNames(lineEntity));
                preInboundLineList.add(line);
            }

            PreInboundHeader header = new PreInboundHeader();
            BeanUtils.copyProperties(preInboundHeaderEntity, header, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
            header.setPreInboundLine(preInboundLineList);
            headerList.add(header);
        }

        return headerList;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumner
     * @return
     */
    public PreInboundHeader getPreInboundHeader(String warehouseId, String preInboundNo, String refDocNumner) {
        Optional<PreInboundHeaderEntity> preInboundHeaderEntity =
                preInboundHeaderRepository.findByWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(warehouseId, preInboundNo, refDocNumner, 0L);

        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  ", warehouseId: " + warehouseId + " doesn't exist.");
        }
        PreInboundHeader preInboundHeader = getPreInboundLineItems(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    public String getReferenceDocumentTypeFromPreInboundHeader(String warehouseId, String preInboundNo, String refDocNumner) {
        return preInboundHeaderRepository.getReferenceDocumentTypeFromPreInboundHeader(warehouseId, preInboundNo, refDocNumner, 0L);
    }

    /**
     * @param preInboundNo
     * @param warehouseId
     * @return
     */
    public PreInboundHeader getPreInboundHeader(String preInboundNo, String warehouseId) {
        Optional<PreInboundHeaderEntity> preInboundHeaderEntity =
                preInboundHeaderRepository.findByPreInboundNoAndWarehouseIdAndDeletionIndicator(preInboundNo, warehouseId, 0L);
        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  ", warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }
        PreInboundHeader preInboundHeader = getPreInboundLineItems(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param preInboundNo
     * @return
     */
    public PreInboundHeader getPreInboundHeaderByPreInboundNo(String preInboundNo) {
        Optional<PreInboundHeaderEntity> preInboundHeaderEntity =
                preInboundHeaderRepository.findByPreInboundNoAndDeletionIndicator(preInboundNo, 0L);
        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  " doesn't exist.");
        }
        PreInboundHeader preInboundHeader = getPreInboundLineItems(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<PreInboundHeader> getPreInboundHeaderWithStatusId(String warehouseId) {
        List<PreInboundHeaderEntity> preInboundHeaderEntity =
                preInboundHeaderRepository.findByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, 24L, 0L);
        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader with StatusID=24 & " +
                                                  " warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }
        List<PreInboundHeader> preInboundHeaderList = new ArrayList<PreInboundHeader>();
        for (PreInboundHeaderEntity dbPreInboundHeaderEntity : preInboundHeaderEntity) {
            PreInboundHeader preInboundHeader = getPreInboundLineItems(dbPreInboundHeaderEntity);
            preInboundHeaderList.add(preInboundHeader);
        }

        return preInboundHeaderList;
    }

    /**
     * @param warehouseId
     * @return
     */
    public PreInboundHeader getPreInboundHeaderByWarehouseId(String warehouseId) {
        PreInboundHeaderEntity preInboundHeaderEntity = preInboundHeaderRepository.findByWarehouseId(warehouseId);
        if (preInboundHeaderEntity == null) {
            throw new BadRequestException("The given PreInboundHeader ID : " +
                                                  ", warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }

        if (preInboundHeaderEntity != null && preInboundHeaderEntity.getDeletionIndicator() != null
                && preInboundHeaderEntity.getDeletionIndicator() == 0) {
            PreInboundHeader preInboundHeader = getPreInboundLineItems(preInboundHeaderEntity);
            return preInboundHeader;
        }
        return null;
    }

    /**
     * @param preInboundHeaderEntity
     * @return
     */
    private PreInboundHeader getPreInboundLineItems(PreInboundHeaderEntity preInboundHeaderEntity) {
        PreInboundHeader header = new PreInboundHeader();
        BeanUtils.copyProperties(preInboundHeaderEntity, header, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
        List<PreInboundLineEntity> lineEntityList = preInboundLineRepository.findByWarehouseIdAndPreInboundNoAndDeletionIndicator(
                preInboundHeaderEntity.getWarehouseId(), preInboundHeaderEntity.getPreInboundNo(), 0L);
        log.info("lineEntityList : " + lineEntityList);

        if (!lineEntityList.isEmpty()) {
            List<PreInboundLine> preInboundLineList = new ArrayList<PreInboundLine>();
            for (PreInboundLineEntity lineEntity : lineEntityList) {
                PreInboundLine line = new PreInboundLine();
                BeanUtils.copyProperties(lineEntity, line, CommonUtils.getNullPropertyNames(lineEntity));
                preInboundLineList.add(line);
            }

            header.setPreInboundLine(preInboundLineList);
        }
        return header;
    }

    /**
     * @param searchPreInboundHeader
     * @return
     * @throws ParseException
     */
    public List<PreInboundHeaderEntity> findPreInboundHeader(SearchPreInboundHeader searchPreInboundHeader) throws ParseException {
        if (searchPreInboundHeader.getStartCreatedOn() != null && searchPreInboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundHeader.getStartCreatedOn(), searchPreInboundHeader.getEndCreatedOn());
            searchPreInboundHeader.setStartCreatedOn(dates[0]);
            searchPreInboundHeader.setEndCreatedOn(dates[1]);
        }

        if (searchPreInboundHeader.getStartRefDocDate() != null && searchPreInboundHeader.getEndRefDocDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundHeader.getStartRefDocDate(), searchPreInboundHeader.getEndRefDocDate());
            searchPreInboundHeader.setStartRefDocDate(dates[0]);
            searchPreInboundHeader.setEndRefDocDate(dates[1]);
        }

        PreInboundHeaderSpecification spec = new PreInboundHeaderSpecification(searchPreInboundHeader);
        List<PreInboundHeaderEntity> results = preInboundHeaderRepository.findAll(spec);
//		log.info("results: " + results);
        return results;
    }

    /**
     * @param searchPreInboundHeader
     * @return
     * @throws ParseException
     */
    //Streaming
    public Stream<PreInboundHeaderEntity> findPreInboundHeaderNew(SearchPreInboundHeader searchPreInboundHeader) throws ParseException {
        if (searchPreInboundHeader.getStartCreatedOn() != null && searchPreInboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundHeader.getStartCreatedOn(), searchPreInboundHeader.getEndCreatedOn());
            searchPreInboundHeader.setStartCreatedOn(dates[0]);
            searchPreInboundHeader.setEndCreatedOn(dates[1]);
        }

        if (searchPreInboundHeader.getStartRefDocDate() != null && searchPreInboundHeader.getEndRefDocDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundHeader.getStartRefDocDate(), searchPreInboundHeader.getEndRefDocDate());
            searchPreInboundHeader.setStartRefDocDate(dates[0]);
            searchPreInboundHeader.setEndRefDocDate(dates[1]);
        }

        PreInboundHeaderSpecification spec = new PreInboundHeaderSpecification(searchPreInboundHeader);
        Stream<PreInboundHeaderEntity> results = preInboundHeaderRepository.stream(spec, PreInboundHeaderEntity.class);
//		log.info("results: " + results);
        return results;
    }

    /**
     * createPreInboundHeader
     * @param newPreInboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundHeaderEntity createPreInboundHeader(AddPreInboundHeader newPreInboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        PreInboundHeaderEntity dbPreInboundHeader = new PreInboundHeaderEntity();
        BeanUtils.copyProperties(newPreInboundHeader, dbPreInboundHeader, CommonUtils.getNullPropertyNames(newPreInboundHeader));

        // Fetch WH_ID value from INTEGRATIONINBOUND table and insert
        //	1. If WH_ID value is null in INTEGRATIONINBOUND table , insert a Harcoded value""110"""
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        Warehouse dbWarehouse = idmasterService.getWarehouse(newPreInboundHeader.getWarehouseId(), authTokenForIDMasterService.getAccess_token());

        dbPreInboundHeader.setWarehouseId(dbWarehouse.getWarehouseId());
        dbPreInboundHeader.setLanguageId(dbWarehouse.getLanguageId());        // LANG_ID
        dbPreInboundHeader.setCompanyCode(dbWarehouse.getCompanyCode());    // C_ID
        dbPreInboundHeader.setPlantId(dbWarehouse.getPlantId());            // PLANT_ID
        dbPreInboundHeader.setDeletionIndicator(0L);
        dbPreInboundHeader.setCreatedBy(loginUserID);
        dbPreInboundHeader.setUpdatedBy(loginUserID);
        dbPreInboundHeader.setCreatedOn(new Date());
        dbPreInboundHeader.setUpdatedOn(new Date());
        return preInboundHeaderRepository.save(dbPreInboundHeader);
    }

    /**
     * @param preInboundNo
     * @param warehouseId
     * @param updatePreInboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundHeader updatePreInboundHeader(String preInboundNo, String warehouseId,
                                                   UpdatePreInboundHeader updatePreInboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundHeader dbPreInboundHeader = getPreInboundHeader(preInboundNo, warehouseId);
        PreInboundHeaderEntity dbEntity = copyBeanToHeaderEntity(dbPreInboundHeader);
        BeanUtils.copyProperties(updatePreInboundHeader, dbEntity, CommonUtils.getNullPropertyNames(updatePreInboundHeader));

        if (updatePreInboundHeader.getStatusId() == null) {
            dbEntity.setStatusId(7L); // Hardcoded as 7 during update
        }
        dbEntity.setUpdatedBy(loginUserID);
        dbEntity.setUpdatedOn(new Date());
        dbEntity = preInboundHeaderRepository.save(dbEntity);

        List<PreInboundLine> updatedPreInboundLineList = new ArrayList<>();
        for (PreInboundLine lineItem : updatePreInboundHeader.getPreInboundLine()) {
            // Get Lines items from DB
            UpdatePreInboundLine updatePreInboundLine = new UpdatePreInboundLine();
            BeanUtils.copyProperties(lineItem, updatePreInboundLine, CommonUtils.getNullPropertyNames(lineItem));

            // CONT_NO value entered in PREINBOUNDHEADER
            updatePreInboundLine.setContainerNo(dbEntity.getContainerNo());
            if (updatePreInboundHeader.getStatusId() == null) {
                updatePreInboundLine.setStatusId(7L); // Hardcoded as 7 during update
            }

            PreInboundLineEntity updatedLineEntity = preInboundLineService.updatePreInboundLine(preInboundNo, warehouseId,
                                                                                                lineItem.getRefDocNumber(), lineItem.getLineNo(), lineItem.getItemCode(),
                                                                                                updatePreInboundLine, loginUserID);
            lineItem = copyLineEntityToBean(updatedLineEntity);
            updatedPreInboundLineList.add(lineItem);
        }

        dbPreInboundHeader = copyHeaderEntityToBean(dbEntity);
        dbPreInboundHeader.setPreInboundLine(updatedPreInboundLineList);
        return dbPreInboundHeader;
    }

    /**
     * @param preInboundNo
     * @param warehousId
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundHeader updatePreInboundHeader(String preInboundNo, String warehousId, String refDocNumner, Long statusId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundHeader dbPreInboundHeader = getPreInboundHeader(warehousId, preInboundNo, refDocNumner);
        PreInboundHeaderEntity dbEntity = copyBeanToHeaderEntity(dbPreInboundHeader);
        dbEntity.setStatusId(statusId);
        dbEntity.setUpdatedBy(loginUserID);
        dbEntity.setUpdatedOn(new Date());
        dbEntity = preInboundHeaderRepository.save(dbEntity);
        dbPreInboundHeader = copyHeaderEntityToBean(dbEntity);
        return dbPreInboundHeader;
    }

    /**
     * deletePreInboundHeader
     * @param loginUserID
     * @param preInboundNo
     */
    public void deletePreInboundHeader(String preInboundNo, String warehousId, String loginUserID) {
        PreInboundHeader preInboundHeader = getPreInboundHeader(preInboundNo, warehousId);
        PreInboundHeaderEntity dbEntity = copyBeanToHeaderEntity(preInboundHeader);
        if (dbEntity != null) {
            dbEntity.setDeletionIndicator(1L);
            dbEntity.setUpdatedBy(loginUserID);
            preInboundHeaderRepository.save(dbEntity);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + preInboundNo);
        }
    }

    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Transactional
    public InboundHeader processInboundReceived(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader)
            throws IllegalAccessException, InvocationTargetException, BadRequestException, Exception {
        /*
         * Checking whether received refDocNumber processed already.
         */
        Optional<PreInboundHeaderEntity> orderProcessedStatus = preInboundHeaderRepository.findByRefDocNumberAndDeletionIndicator(refDocNumber, 0);
        if (!orderProcessedStatus.isEmpty()) {
            orderService.updateProcessedInboundOrder(refDocNumber);
            throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
        }

        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        log.info("warehouseId : " + warehouseId);

        // Fetch ITM_CODE inserted in INBOUNDINTEGRATION table and pass the ITM_CODE in IMBASICDATA1 table and
        // validate the ITM_CODE result is Not Null
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        log.info("authTokenForMastersService : " + authTokenForMastersService);
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();

        Warehouse warehouse = idmasterService.getWarehouse(warehouseId, authTokenForIDMasterService.getAccess_token());
        log.info("warehouse : " + warehouse);
        if (warehouse == null) {
            log.info("warehouse not found.");
            throw new BadRequestException("Warehouse cannot be null.");
        }

        // Getting PreInboundNo from NumberRangeTable
        String preInboundNo = getPreInboundNo(warehouseId);

        List<PreInboundLineEntity> overallCreatedPreInboundLineList = new ArrayList<>();
        for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
            log.info("inboundIntegrationLine : " + inboundIntegrationLine);
            ImBasicData1 imBasicData1 =
                    mastersService.getImBasicData1ByItemCode(inboundIntegrationLine.getItemCode(), warehouseId,
                                                             authTokenForMastersService.getAccess_token());
            log.info("imBasicData1 exists: " + imBasicData1);

            // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
            if (imBasicData1 == null) {
                imBasicData1 = new ImBasicData1();
                imBasicData1.setLanguageId(LANG_ID);                                            // LANG_ID
                imBasicData1.setWarehouseId(warehouseId);                                    // WH_ID
                imBasicData1.setCompanyCodeId(warehouse.getCompanyCode());                    // C_ID
                imBasicData1.setPlantId(warehouse.getPlantId());                            // PLANT_ID
                imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                // ITM_CODE
                imBasicData1.setUomId(inboundIntegrationLine.getUom());                    // UOM_ID
                imBasicData1.setDescription(inboundIntegrationLine.getItemText());            // ITEM_TEXT
                imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());        // MFR_PART
                imBasicData1.setStatusId(1L);                                                // STATUS_ID
                ImBasicData1 createdImBasicData1 =
                        mastersService.createImBasicData1(imBasicData1, MW_AMS, authTokenForMastersService.getAccess_token());
                log.info("ImBasicData1 created: " + createdImBasicData1);
            }

            /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
            /*
             * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
             * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
             */
            BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId, authTokenForMastersService.getAccess_token());
            log.info("bomHeader [BOM] : " + bomHeader);
            if (bomHeader != null) {
                BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(),
                                                              authTokenForMastersService.getAccess_token());
                List<PreInboundLineEntity> toBeCreatedPreInboundLineList = new ArrayList<>();
                for (BomLine dbBomLine : bomLine) {
                    PreInboundLineEntity preInboundLineEntity = createPreInboundLineBOMBased(warehouse.getCompanyCode(),
                                                                                             warehouse.getPlantId(), preInboundNo, inboundIntegrationHeader, dbBomLine, inboundIntegrationLine);
                    log.info("preInboundLineEntity [BOM] : " + preInboundLineEntity);
                    toBeCreatedPreInboundLineList.add(preInboundLineEntity);
                }

                // Batch Insert - PreInboundLines
                if (!toBeCreatedPreInboundLineList.isEmpty()) {
                    List<PreInboundLineEntity> createdPreInboundLine = preInboundLineRepository.saveAll(toBeCreatedPreInboundLineList);
                    log.info("createdPreInboundLine [BOM] : " + createdPreInboundLine);
                    overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
                }
            }
        }

        /*
         * Append PREINBOUNDLINE table through below logic
         */
        List<PreInboundLineEntity> toBeCreatedPreInboundLineList = new ArrayList<>();
        for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
            toBeCreatedPreInboundLineList.add(createPreInboundLine(warehouse.getCompanyCode(),
                                                                   warehouse.getPlantId(), preInboundNo, inboundIntegrationHeader, inboundIntegrationLine));
        }

        log.info("toBeCreatedPreInboundLineList [API] : " + toBeCreatedPreInboundLineList);

        // Batch Insert - PreInboundLines
        if (!toBeCreatedPreInboundLineList.isEmpty()) {
            List<PreInboundLineEntity> createdPreInboundLine = preInboundLineRepository.saveAll(toBeCreatedPreInboundLineList);
            log.info("createdPreInboundLine [API] : " + createdPreInboundLine);
            overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
        }

        /*------------------Insert into PreInboundHeader table-----------------------------*/
        PreInboundHeaderEntity createdPreInboundHeader = createPreInboundHeader(warehouse.getCompanyCode(), warehouse.getPlantId(), preInboundNo,
                                                                                inboundIntegrationHeader);
        log.info("preInboundHeader Created : " + createdPreInboundHeader);

        /*------------------Insert into Inbound Header And Line----------------------------*/
        InboundHeader createdInboundHeader = createInbounHeaderAndLine(createdPreInboundHeader, overallCreatedPreInboundLineList);

        // Inserting into InboundLog Table.
        InboundIntegrationLog createdInboundIntegrationLog = createInboundIntegrationLog(createdPreInboundHeader);

//		if (createdInboundIntegrationLog != null) {
//			inboundIntegrationHeader.setProcessedStatusId(1L);
//			inboundIntegrationHeader.setOrderProcessedOn(new Date());
//			mongoRepository.save(inboundIntegrationHeader);
//		}

        return createdInboundHeader;
    }

    /**
     * @param companyCode
     * @param plantID
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param bomLine
     * @param inboundIntegrationLine
     * @return
     * @throws ParseException
     */
    private PreInboundLineEntity createPreInboundLineBOMBased(String companyCode, String plantID, String preInboundNo,
                                                              InboundIntegrationHeader inboundIntegrationHeader, BomLine bomLine, InboundIntegrationLine inboundIntegrationLine) throws ParseException {
        PreInboundLineEntity preInboundLine = new PreInboundLineEntity();
        Warehouse warehouse = getWarehouse(inboundIntegrationHeader.getWarehouseID());

        preInboundLine.setLanguageId(warehouse.getLanguageId());
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantID);
        preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
        preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(Long.valueOf(inboundIntegrationLine.getLineReference()));

        // ITM_CODE
        preInboundLine.setItemCode(bomLine.getChildItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 =
                mastersService.getImBasicData1ByItemCode(inboundIntegrationLine.getItemCode(),
                                                         inboundIntegrationHeader.getWarehouseID(),
                                                         authTokenForMastersService.getAccess_token());
        preInboundLine.setItemDescription(imBasicData1.getDescription());

        // MFR
        preInboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());

        // ORD_QTY
        double orderQuantity = inboundIntegrationLine.getOrderedQty() * bomLine.getChildItemQuantity();
        preInboundLine.setOrderQty(orderQuantity);

        // ORD_UOM
        preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

        // EA_DATE
        preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // STATUS_ID
        preInboundLine.setStatusId(6L);

        // REF_FIELD_1
        preInboundLine.setReferenceField1("CHILD ITEM");

        // REF_FIELD_2
        preInboundLine.setReferenceField2("BOM ITEM");

        // REF_FIELD_4
        preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy(MW_AMS);
        preInboundLine.setCreatedOn(new Date());
        return preInboundLine;
    }

    /**
     * PreInboundLine Insert
     * @param companyCode
     * @param plantID
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @return
     * @throws ParseException
     */
    private PreInboundLineEntity createPreInboundLine(String companyCode, String plantID, String preInboundNo,
                                                      InboundIntegrationHeader inboundIntegrationHeader, InboundIntegrationLine inboundIntegrationLine) throws ParseException {
        PreInboundLineEntity preInboundLine = new PreInboundLineEntity();
        Warehouse warehouse = getWarehouse(inboundIntegrationHeader.getWarehouseID());

        preInboundLine.setLanguageId(warehouse.getLanguageId());
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantID);
        preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
        preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(Long.valueOf(inboundIntegrationLine.getLineReference()));

        // ITM_CODE
        preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 =
                mastersService.getImBasicData1ByItemCode(inboundIntegrationLine.getItemCode(),
                                                         inboundIntegrationHeader.getWarehouseID(),
                                                         authTokenForMastersService.getAccess_token());
        preInboundLine.setItemDescription(imBasicData1.getDescription());

        // MFR_PART
        preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());

        // PARTNER_CODE
        preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

        // ORD_QTY
        preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());

        // ORD_UOM
        preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // EA_DATE
        log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
        preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

        // ITM_CASE_QTY
        preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());

        // REF_FIELD_4
        preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

        // STATUS_ID
        preInboundLine.setStatusId(6L);
        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy(MW_AMS);
        preInboundLine.setCreatedOn(new Date());

        log.info("preInboundLine : " + preInboundLine);
        return preInboundLine;
    }

    /**
     * @param companyCode
     * @param plantID
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @return
     */
    private PreInboundHeaderEntity createPreInboundHeader(String companyCode, String plantID, String preInboundNo,
                                                          InboundIntegrationHeader inboundIntegrationHeader) {
        PreInboundHeaderEntity preInboundHeader = new PreInboundHeaderEntity();
        Warehouse warehouse = getWarehouse(inboundIntegrationHeader.getWarehouseID());

        preInboundHeader.setLanguageId(warehouse.getLanguageId());                                    // LANG_ID
        preInboundHeader.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
        preInboundHeader.setCompanyCode(companyCode);
        preInboundHeader.setPlantId(plantID);
        preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
        preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
        preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
        preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
        preInboundHeader.setStatusId(6L);
        preInboundHeader.setDeletionIndicator(0L);
        preInboundHeader.setCreatedBy(MW_AMS);
        preInboundHeader.setCreatedOn(new Date());
        PreInboundHeaderEntity createdPreInboundHeader = preInboundHeaderRepository.save(preInboundHeader);
        log.info("createdPreInboundHeader : " + createdPreInboundHeader);
        return createdPreInboundHeader;
    }

    /**
     * @param preInboundHeader
     * @param preInboundLine
     * @return
     */
    private InboundHeader createInbounHeaderAndLine(PreInboundHeaderEntity preInboundHeader, List<PreInboundLineEntity> preInboundLine) {
        InboundHeader inboundHeader = new InboundHeader();
        BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

        // Status ID
        inboundHeader.setStatusId(6L);
        inboundHeader.setDeletionIndicator(0L);
        inboundHeader.setCreatedBy(preInboundHeader.getCreatedBy());
        inboundHeader.setCreatedOn(preInboundHeader.getCreatedOn());
        InboundHeader createdInboundHeader = inboundHeaderRepository.save(inboundHeader);
        log.info("createdInboundHeader : " + createdInboundHeader);

        /*
         * Inbound Line Table Insert
         */
        List<InboundLine> toBeCreatedInboundLineList = new ArrayList<>();
        for (PreInboundLineEntity createdPreInboundLine : preInboundLine) {
            InboundLine inboundLine = new InboundLine();
            BeanUtils.copyProperties(createdPreInboundLine, inboundLine, CommonUtils.getNullPropertyNames(createdPreInboundLine));

            inboundLine.setOrderQty(createdPreInboundLine.getOrderQty());
            inboundLine.setOrderUom(createdPreInboundLine.getOrderUom());
            inboundLine.setDescription(createdPreInboundLine.getItemDescription());
            inboundLine.setBusinessPartnerCode(createdPreInboundLine.getBusinessPartnerCode());
            inboundLine.setReferenceField4(createdPreInboundLine.getReferenceField4());
            inboundLine.setDeletionIndicator(0L);
            inboundLine.setCreatedBy(preInboundHeader.getCreatedBy());
            inboundLine.setCreatedOn(preInboundHeader.getCreatedOn());
            toBeCreatedInboundLineList.add(inboundLine);
        }

        List<InboundLine> createdInboundLine = inboundLineRepository.saveAll(toBeCreatedInboundLineList);
        log.info("createdInboundLine : " + createdInboundLine);

        return createdInboundHeader;
    }

    /**
     * @param inputPreInboundLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StagingHeader processASN(List<AddPreInboundLine> inputPreInboundLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        boolean isPreInboundHeaderUpdated = false;
        boolean isPreInboundLineUpdated = false;

        // Update PREINBOUNDHEADER and PREINBOUNDLINE table with STATUS_ID = 05 and update the other fields from UI
        // PREINBOUNDLINE Update
        String preInboundNo = null;
        String containerNo = null;
        String warehouseId = null;
        for (AddPreInboundLine preInboundLine : inputPreInboundLines) {
            PreInboundLineEntity objUpdatePreInboundLine = new PreInboundLineEntity();
            BeanUtils.copyProperties(preInboundLine, objUpdatePreInboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
            objUpdatePreInboundLine.setCompanyCode(getCompanyCode());
            objUpdatePreInboundLine.setStatusId(5L);
            objUpdatePreInboundLine.setUpdatedBy(loginUserID);
            objUpdatePreInboundLine.setUpdatedOn(new Date());
            PreInboundLineEntity updatedPreInboundLine = preInboundLineRepository.save(objUpdatePreInboundLine);
            log.info("preInboundLine updated: " + updatedPreInboundLine);
            if (updatedPreInboundLine != null) {
                isPreInboundLineUpdated = true;
                preInboundNo = updatedPreInboundLine.getPreInboundNo();
                containerNo = updatedPreInboundLine.getContainerNo();
                warehouseId = updatedPreInboundLine.getWarehouseId();
            }
        }

        // PREINBOUNDHEADER Update
//		PreInboundHeader preInboundHeader = getPreInboundHeaderByPreInboundNo (preInboundNo);
        PreInboundHeader preInboundHeader = getPreInboundHeader(preInboundNo, warehouseId);
        log.info("preInboundHeader---found-------> : " + preInboundHeader);

        PreInboundHeaderEntity preInboundHeaderEntity = copyBeanToHeaderEntity(preInboundHeader);
        preInboundHeaderEntity.setContainerNo(containerNo);
        preInboundHeaderEntity.setStatusId(5L);
        PreInboundHeaderEntity updatedPreInboundHeaderEntity = preInboundHeaderRepository.save(preInboundHeaderEntity);
        log.info("updatedPreInboundHeaderEntity---@------> : " + updatedPreInboundHeaderEntity);
        if (updatedPreInboundHeaderEntity != null) {
            isPreInboundHeaderUpdated = true;
        }

        // Update INBOUNDHEADER and INBOUNDLINE table
        if (isPreInboundHeaderUpdated && isPreInboundLineUpdated) {
            UpdateInboundHeader updateInboundHeader = new UpdateInboundHeader();
            updateInboundHeader.setContainerNo(containerNo);
            updateInboundHeader.setStatusId(5L);

            // warehouseId, refDocNumber, preInboundNo, loginUserID, updateInboundHeader
            InboundHeader updatedInboundHeader =
                    inboundHeaderService.updateInboundHeader(preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber(),
                                                             preInboundNo, loginUserID, updateInboundHeader);
            log.info("updatedInboundHeader : " + updatedInboundHeader);

            // INBOUNDLINE table update
            for (AddPreInboundLine dbPreInboundLine : inputPreInboundLines) {
                InboundLine inboundLine = inboundLineService.getInboundLine(preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber(),
                                                                            preInboundNo, dbPreInboundLine.getLineNo(), dbPreInboundLine.getItemCode());
                inboundLine.setContainerNo(dbPreInboundLine.getContainerNo());
                inboundLine.setInvoiceNo(dbPreInboundLine.getInvoiceNo());
                inboundLine.setStatusId(5L);

                // warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine
                InboundLine updatedInboundLine = inboundLineRepository.save(inboundLine);
                log.info("updatedInboundLine updated: " + updatedInboundLine);
            }
        }

        StagingHeader stagingHeader = new StagingHeader();
        BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
        stagingHeader.setCompanyCode(getCompanyCode());

        // STG_NO
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        UserManagement userManagement = getUserManagement(loginUserID, preInboundHeader.getWarehouseId());

        long NUMBER_RANGE_CODE = 3;
        WAREHOUSEID_NUMBERRANGE = userManagement.getWarehouseId();
        String nextRangeNumber = getNextRangeNumber(NUMBER_RANGE_CODE, WAREHOUSEID_NUMBERRANGE, authTokenForIDMasterService.getAccess_token());
        stagingHeader.setStagingNo(nextRangeNumber);

        // GR_MTD
        stagingHeader.setGrMtd("INTEGRATION");

        // STATUS_ID
        stagingHeader.setStatusId(12L);
        stagingHeader.setCreatedBy(preInboundHeader.getCreatedBy());
        stagingHeader.setCreatedOn(preInboundHeader.getCreatedOn());
        return stagingHeaderRepository.save(stagingHeader);
    }

    /**
     * @param refDocNumber
     */
    public void updateASN(String refDocNumber) {
        List<PreInboundHeader> preInboundHeaders = getPreInboundHeaders();
        List<PreInboundHeaderEntity> preInboundHeadersEntityList = createEntityList(preInboundHeaders);
        preInboundHeaders.forEach(preibheaders -> preibheaders.setReferenceField1(refDocNumber));
        preInboundHeaderRepository.saveAll(preInboundHeadersEntityList);
    }

    /**
     * @return
     */
    private String getPreInboundNo(String warehouseId) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 2 values in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then
         * update in PREINBOUNDHEADER table
         */
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            String nextRangeNumber = getNextRangeNumber(2, warehouseId, authTokenForIDMasterService.getAccess_token());
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }

    /**
     * @param createdPreInboundHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundIntegrationLog createInboundIntegrationLog(PreInboundHeaderEntity createdPreInboundHeader)
            throws IllegalAccessException, InvocationTargetException {
        InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
        dbInboundIntegrationLog.setLanguageId(LANG_ID);
        dbInboundIntegrationLog.setCompanyCodeId(createdPreInboundHeader.getCompanyCode());
        dbInboundIntegrationLog.setPlantId(createdPreInboundHeader.getPlantId());
        dbInboundIntegrationLog.setWarehouseId(createdPreInboundHeader.getWarehouseId());
        dbInboundIntegrationLog.setIntegrationLogNumber(createdPreInboundHeader.getPreInboundNo());
        dbInboundIntegrationLog.setRefDocNumber(createdPreInboundHeader.getRefDocNumber());
        dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
        dbInboundIntegrationLog.setIntegrationStatus("SUCCESS");
        dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
        dbInboundIntegrationLog.setDeletionIndicator(0L);
        dbInboundIntegrationLog.setCreatedBy(createdPreInboundHeader.getCreatedBy());
        dbInboundIntegrationLog.setCreatedOn(new Date());
        dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
        log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
        return dbInboundIntegrationLog;
    }

    /**
     * @param inbound
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundIntegrationLog createInboundIntegrationLog(InboundIntegrationHeader inbound)
            throws IllegalAccessException, InvocationTargetException {
        com.tekclover.wms.api.transaction.model.warehouse.Warehouse warehouse =
                getWarehouse(inbound.getCompanyCode(), inbound.getBranchCode());
        if (warehouse == null) {
            throw new BadRequestException("Warehouse not found : " + inbound.getWarehouseID());
        }
        InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
        dbInboundIntegrationLog.setLanguageId(LANG_ID);
        dbInboundIntegrationLog.setCompanyCodeId(warehouse.getCompanyCodeId());
        dbInboundIntegrationLog.setPlantId(warehouse.getPlantId());
        dbInboundIntegrationLog.setWarehouseId(warehouse.getWarehouseId());
        dbInboundIntegrationLog.setIntegrationLogNumber(inbound.getRefDocumentNo());
        dbInboundIntegrationLog.setRefDocNumber(inbound.getRefDocumentNo());
        dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
        dbInboundIntegrationLog.setIntegrationStatus("FAILED");
        dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
        dbInboundIntegrationLog.setDeletionIndicator(0L);
        dbInboundIntegrationLog.setCreatedBy("MSD_API");
        dbInboundIntegrationLog.setCreatedOn(new Date());
        dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
        log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
        return dbInboundIntegrationLog;
    }

    /**
     * @param preInboundHeaderList
     * @return
     */
    private List<PreInboundHeader> createBeanList(List<PreInboundHeaderEntity> preInboundHeaderList) {
        List<PreInboundHeader> listPreInboundHeader = new ArrayList<>();
        for (PreInboundHeaderEntity preInboundHeaderEntity : preInboundHeaderList) {
            PreInboundHeader preInboundHeader = copyHeaderEntityToBean(preInboundHeaderEntity);
            listPreInboundHeader.add(preInboundHeader);
        }
        return listPreInboundHeader;
    }

    private List<PreInboundLine> createBeanListAsLine(List<PreInboundLineEntity> preInboundLineEntityList) {
        List<PreInboundLine> listPreInboundLine = new ArrayList<>();
        for (PreInboundLineEntity preInboundLineEntity : preInboundLineEntityList) {
            PreInboundLine preInboundLine = copyLineEntityToBean(preInboundLineEntity);
            listPreInboundLine.add(preInboundLine);
        }
        return listPreInboundLine;
    }

    private List<PreInboundHeaderEntity> createEntityList(List<PreInboundHeader> preInboundHeaderList) {
        List<PreInboundHeaderEntity> listPreInboundHeaderEntity = new ArrayList<>();
        for (PreInboundHeader preInboundHeader : preInboundHeaderList) {
            PreInboundHeaderEntity newPreInboundHeaderEntity = copyBeanToHeaderEntity(preInboundHeader);
            listPreInboundHeaderEntity.add(newPreInboundHeaderEntity);
        }
        return listPreInboundHeaderEntity;
    }

    private PreInboundHeader copyHeaderEntityToBean(PreInboundHeaderEntity preInboundHeaderEntity) {
        PreInboundHeader preInboundHeader = new PreInboundHeader();
        BeanUtils.copyProperties(preInboundHeaderEntity, preInboundHeader, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
        return preInboundHeader;
    }

    private PreInboundLine copyLineEntityToBean(PreInboundLineEntity preInboundLineEntity) {
        PreInboundLine preInboundLine = new PreInboundLine();
        BeanUtils.copyProperties(preInboundLineEntity, preInboundLine, CommonUtils.getNullPropertyNames(preInboundLineEntity));
        return preInboundLine;
    }

    private PreInboundHeaderEntity copyBeanToHeaderEntity(PreInboundHeader preInboundHeader) {
        PreInboundHeaderEntity preInboundHeaderEntity = new PreInboundHeaderEntity();
        BeanUtils.copyProperties(preInboundHeader, preInboundHeaderEntity, CommonUtils.getNullPropertyNames(preInboundHeader));
        return preInboundHeaderEntity;
    }

    private PreInboundLineEntity copyBeanToLineEntity(PreInboundLine preInboundLine) {
        PreInboundLineEntity preInboundLineEntity = new PreInboundLineEntity();
        BeanUtils.copyProperties(preInboundLine, preInboundLineEntity, CommonUtils.getNullPropertyNames(preInboundLine));
        return preInboundLineEntity;
    }

    //========================================================================V2====================================================================

    /**
     * getPreInboundHeaders
     * @return
     */
    public List<PreInboundHeaderEntityV2> getPreInboundHeadersV2() {
        List<PreInboundHeaderEntityV2> preInboundHeaderEntityList = preInboundHeaderV2Repository.findAll();
        preInboundHeaderEntityList = preInboundHeaderEntityList.stream()
                .filter(n -> n.getDeletionIndicator() != null && n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());

        return preInboundHeaderEntityList;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumner
     * @return
     */
    public PreInboundHeaderV2 getPreInboundHeaderV2(String warehouseId, String preInboundNo, String refDocNumner) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(warehouseId, preInboundNo, refDocNumner, 0L);

        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  ", warehouseId: " + warehouseId + " doesn't exist.");
        }
        PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public PreInboundHeaderV2 getPreInboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumber) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, 0L);

        if (preInboundHeaderEntity.isEmpty()) {
            // Exception log
            createPreInboundHeaderLog1(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo,
                                       "PreInboundHeaderV2 with given values doesn't exists - " + refDocNumber);
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  ", warehouseId: " + warehouseId + " doesn't exist.");
        }
        PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public PreInboundHeaderV2 getPreInboundHeaderV2ForPutAwayCreate(String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumber) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, 0L);

        if (preInboundHeaderEntity.isEmpty()) {
            // Exception log
            createPreInboundHeaderLog1(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo,
                                       "PreInboundHeaderV2 with given values doesn't exists - " + refDocNumber);
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  ", warehouseId: " + warehouseId + " doesn't exist.");
        }
        PreInboundHeaderV2 preInboundHeader = new PreInboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeaderEntity.get(), preInboundHeader, CommonUtils.getNullPropertyNames(preInboundHeaderEntity.get()));
//        PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public PreInboundHeaderEntityV2 getPreInboundHeaderForReversalV2(String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumber) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, 0L);

        if (preInboundHeaderEntity.isEmpty()) {
            return null;
        }

        return preInboundHeaderEntity.get();
    }

    /**
     * @param preInboundNo
     * @param warehouseId
     * @return
     */
    public PreInboundHeaderV2 getPreInboundHeaderV2(String preInboundNo, String warehouseId,
                                                    String companyCode, String plantId, String languageId) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByPreInboundNoAndWarehouseIdAndCompanyCodeAndPlantIdAndLanguageIdAndDeletionIndicator(
                        preInboundNo, warehouseId, companyCode, plantId, languageId, 0L);
        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  ", warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }
        PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param preInboundNo
     * @param warehouseId
     * @return
     */
    public PreInboundHeaderV2 getPreInboundHeaderV2(String preInboundNo, String warehouseId) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByPreInboundNoAndWarehouseIdAndDeletionIndicator(preInboundNo, warehouseId, 0L);
        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  ", warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }
        PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param preInboundNo
     * @return
     */
    public PreInboundHeaderV2 getPreInboundHeaderByPreInboundNoV2(String preInboundNo) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByPreInboundNoAndDeletionIndicator(preInboundNo, 0L);
        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
                                                  " doesn't exist.");
        }
        PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity.get());
        return preInboundHeader;
    }

    /**
     * @param warehouseId
     * @return
     */
    public List<PreInboundHeaderV2> getPreInboundHeaderWithStatusIdV2(String warehouseId) {
        List<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByWarehouseIdAndStatusIdAndDeletionIndicator(warehouseId, 24L, 0L);
        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader with StatusID=24 & " +
                                                  " warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }
        List<PreInboundHeaderV2> preInboundHeaderList = new ArrayList<PreInboundHeaderV2>();
        for (PreInboundHeaderEntityV2 dbPreInboundHeaderEntity : preInboundHeaderEntity) {
            PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(dbPreInboundHeaderEntity);
            preInboundHeaderList.add(preInboundHeader);
        }

        return preInboundHeaderList;
    }

    /**
     * @param warehouseId
     * @param companyCode
     * @param plantId
     * @param languageId
     * @return
     */
    public List<PreInboundHeaderV2> getPreInboundHeaderWithStatusIdV2(String warehouseId, String companyCode, String plantId, String languageId) {
        List<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByWarehouseIdAndCompanyCodeAndPlantIdAndLanguageIdAndStatusIdAndDeletionIndicator(
                        warehouseId, companyCode, plantId, languageId, 24L, 0L);
        if (preInboundHeaderEntity.isEmpty()) {
            throw new BadRequestException("The given PreInboundHeader with StatusID=24 & " +
                                                  " warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }
        List<PreInboundHeaderV2> preInboundHeaderList = new ArrayList<>();
        for (PreInboundHeaderEntityV2 dbPreInboundHeaderEntity : preInboundHeaderEntity) {
            PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(dbPreInboundHeaderEntity);
            preInboundHeaderList.add(preInboundHeader);
        }
        return preInboundHeaderList;
    }

    /**
     * @param warehouseId
     * @return
     */
    public PreInboundHeaderV2 getPreInboundHeaderByWarehouseIdV2(String warehouseId) {
        PreInboundHeaderEntityV2 preInboundHeaderEntity = preInboundHeaderV2Repository.findByWarehouseId(warehouseId);
        if (preInboundHeaderEntity == null) {
            throw new BadRequestException("The given PreInboundHeader ID : " +
                                                  ", warehouseId: " + warehouseId +
                                                  " doesn't exist.");
        }

        if (preInboundHeaderEntity != null && preInboundHeaderEntity.getDeletionIndicator() != null
                && preInboundHeaderEntity.getDeletionIndicator() == 0) {
            PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity);
            return preInboundHeader;
        }
        return null;
    }

    /**
     * @param preInboundHeaderEntity
     * @return
     */
    private PreInboundHeaderV2 getPreInboundLineItemsV2(PreInboundHeaderEntityV2 preInboundHeaderEntity) {
        PreInboundHeaderV2 header = new PreInboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeaderEntity, header, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
        List<PreInboundLineEntityV2> lineEntityList = preInboundLineV2Repository.findByWarehouseIdAndPreInboundNoAndDeletionIndicator(
                preInboundHeaderEntity.getWarehouseId(), preInboundHeaderEntity.getPreInboundNo(), 0L);
        log.info("lineEntityList : " + lineEntityList);

        if (!lineEntityList.isEmpty()) {
            header.setPreInboundLineV2(lineEntityList);
        }
        return header;
    }

    /**
     * @param searchPreInboundHeader
     * @return
     * @throws ParseException
     */
    //Streaming
    public Stream<PreInboundHeaderEntityV2> findPreInboundHeaderV2(SearchPreInboundHeaderV2 searchPreInboundHeader) throws ParseException {
        if (searchPreInboundHeader.getStartCreatedOn() != null && searchPreInboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundHeader.getStartCreatedOn(), searchPreInboundHeader.getEndCreatedOn());
            searchPreInboundHeader.setStartCreatedOn(dates[0]);
            searchPreInboundHeader.setEndCreatedOn(dates[1]);
        }

        if (searchPreInboundHeader.getStartRefDocDate() != null && searchPreInboundHeader.getEndRefDocDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreInboundHeader.getStartRefDocDate(), searchPreInboundHeader.getEndRefDocDate());
            searchPreInboundHeader.setStartRefDocDate(dates[0]);
            searchPreInboundHeader.setEndRefDocDate(dates[1]);
        }

        PreInboundHeaderV2Specification spec = new PreInboundHeaderV2Specification(searchPreInboundHeader);
        Stream<PreInboundHeaderEntityV2> results = preInboundHeaderV2Repository.stream(spec, PreInboundHeaderEntityV2.class);
//		log.info("results: " + results);
        return results;
    }

    /**
     * createPreInboundHeader
     * @param newPreInboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundHeaderEntityV2 createPreInboundHeaderV2(PreInboundHeaderV2 newPreInboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        PreInboundHeaderEntityV2 dbPreInboundHeader = new PreInboundHeaderEntityV2();
        BeanUtils.copyProperties(newPreInboundHeader, dbPreInboundHeader, CommonUtils.getNullPropertyNames(newPreInboundHeader));

        // Fetch WH_ID value from INTEGRATIONINBOUND table and insert
        //	1. If WH_ID value is null in INTEGRATIONINBOUND table , insert a Harcoded value""110"""
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        Warehouse dbWarehouse = idmasterService.getWarehouse(newPreInboundHeader.getWarehouseId(),
                                                             newPreInboundHeader.getCompanyCode(),
                                                             newPreInboundHeader.getPlantId(),
                                                             newPreInboundHeader.getLanguageId(),
                                                             authTokenForIDMasterService.getAccess_token());

        dbPreInboundHeader.setWarehouseId(dbWarehouse.getWarehouseId());
        dbPreInboundHeader.setLanguageId(dbWarehouse.getLanguageId());        // LANG_ID
        dbPreInboundHeader.setCompanyCode(dbWarehouse.getCompanyCode());    // C_ID
        dbPreInboundHeader.setPlantId(dbWarehouse.getPlantId());            // PLANT_ID
        dbPreInboundHeader.setDeletionIndicator(0L);
        dbPreInboundHeader.setCreatedBy(loginUserID);
        dbPreInboundHeader.setUpdatedBy(loginUserID);
        dbPreInboundHeader.setCreatedOn(new Date());
        dbPreInboundHeader.setUpdatedOn(new Date());
        return preInboundHeaderV2Repository.save(dbPreInboundHeader);
    }

    /**
     * @param preInboundNo
     * @param warehouseId
     * @param updatePreInboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundHeaderV2 updatePreInboundHeaderV2(String companyCode, String plantId, String languageId,
                                                       String preInboundNo, String warehouseId,
                                                       PreInboundHeaderV2 updatePreInboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndPreInboundNoAndWarehouseIdAndDeletionIndicator(
                        companyCode, plantId, languageId, preInboundNo, warehouseId, 0L);
        PreInboundHeaderEntityV2 dbEntity = null;
        if (!preInboundHeaderEntity.isEmpty()) {
            dbEntity = preInboundHeaderEntity.get();
            if (updatePreInboundHeader.getStatusId() == null) {
                dbEntity.setStatusId(7L); // Hardcoded as 7 during update
            }
            dbEntity.setUpdatedBy(loginUserID);
            dbEntity.setUpdatedOn(new Date());
            dbEntity = preInboundHeaderV2Repository.save(dbEntity);
        }
        List<PreInboundLineEntityV2> updatedPreInboundLineList = new ArrayList<>();
        for (PreInboundLineEntityV2 updatePreInboundLine : updatePreInboundHeader.getPreInboundLineV2()) {
            // Get Lines items from DB
//            PreInboundLineEntityV2 updatePreInboundLine = new PreInboundLineEntityV2();
//            BeanUtils.copyProperties(lineItem, updatePreInboundLine, CommonUtils.getNullPropertyNames(lineItem));

            // CONT_NO value entered in PREINBOUNDHEADER
            updatePreInboundLine.setContainerNo(dbEntity.getContainerNo());
            if (updatePreInboundHeader.getStatusId() == null) {
                updatePreInboundLine.setStatusId(7L); // Hardcoded as 7 during update
                statusDescription = stagingLineV2Repository.getStatusDescription(7L, languageId);
                updatePreInboundLine.setStatusDescription(statusDescription);
            }

            PreInboundLineEntityV2 updatedLineEntity = preInboundLineService.updatePreInboundLineV2(companyCode, plantId, languageId, preInboundNo, warehouseId,
                                                                                                    updatePreInboundLine.getRefDocNumber(), updatePreInboundLine.getLineNo(), updatePreInboundLine.getItemCode(),
                                                                                                    updatePreInboundLine, loginUserID);
//            lineItem = copyLineEntityToBean(updatedLineEntity);
            updatedPreInboundLineList.add(updatedLineEntity);
        }

        PreInboundHeaderV2 dbPreInboundHeader = copyHeaderEntityToBean(dbEntity);
        dbPreInboundHeader.setPreInboundLineV2(updatedPreInboundLineList);
        return dbPreInboundHeader;
    }

    /**
     * @param preInboundNo
     * @param warehousId
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreInboundHeaderV2 updatePreInboundHeaderV2(String companyCode, String plantId, String languageId,
                                                       String preInboundNo, String warehousId, String refDocNumner, Long statusId, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PreInboundHeaderV2 dbPreInboundHeader = getPreInboundHeaderV2(companyCode, plantId, languageId, warehousId, preInboundNo, refDocNumner);
        PreInboundHeaderEntityV2 dbEntity = copyBeanToHeaderEntity(dbPreInboundHeader);
        dbEntity.setStatusId(statusId);
        statusDescription = stagingLineV2Repository.getStatusDescription(statusId, languageId);
        dbEntity.setStatusDescription(statusDescription);
        dbEntity.setUpdatedBy(loginUserID);
        dbEntity.setUpdatedOn(new Date());
        dbEntity = preInboundHeaderV2Repository.save(dbEntity);
        dbPreInboundHeader = copyHeaderEntityToBean(dbEntity);
        return dbPreInboundHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param preInboundNo
     * @param warehousId
     * @param loginUserID
     */
    public void deletePreInboundHeaderV2(String companyCode, String plantId, String languageId,
                                         String preInboundNo, String warehousId, String loginUserID) throws ParseException {
        PreInboundHeaderV2 preInboundHeader = getPreInboundHeaderV2(preInboundNo, warehousId, companyCode, plantId, languageId);
        PreInboundHeaderEntityV2 dbEntity = copyBeanToHeaderEntity(preInboundHeader);
        if (dbEntity != null) {
            dbEntity.setDeletionIndicator(1L);
            dbEntity.setUpdatedBy(loginUserID);
            dbEntity.setUpdatedOn(new Date());
            preInboundHeaderV2Repository.save(dbEntity);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + preInboundNo);
        }
    }

    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public InboundHeaderV2 processInboundReceivedV2(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader)
            throws IllegalAccessException, InvocationTargetException, BadRequestException,
            SQLException, SQLServerException, CannotAcquireLockException, LockAcquisitionException, Exception {
        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundIntegrationHeader.getInboundOrderTypeId());
            if (inboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = inboundIntegrationHeader.getLoginUserId();
            }
            /*
             * Checking whether received refDocNumber processed already.
             */
//        Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.findByRefDocNumberAndDeletionIndicator(refDocNumber, 0L);
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
//            orderService.updateProcessedInboundOrderV2(refDocNumber, 100L);
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

            String warehouseId = inboundIntegrationHeader.getWarehouseID();
            log.info("warehouseId : " + warehouseId);

            // Fetch ITM_CODE inserted in INBOUNDINTEGRATION table and pass the ITM_CODE in IMBASICDATA1 table and
            // validate the ITM_CODE result is Not Null
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            log.info("authTokenForMastersService : " + authTokenForMastersService);
            InboundOrderV2 inboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndInboundOrderTypeId(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId());
            log.info("inboundOrder : " + inboundOrder);

            com.tekclover.wms.api.transaction.model.warehouse.Warehouse warehouse = null;
            try {
                Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> optWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                inboundOrder.getCompanyCode(),
                                inboundOrder.getBranchCode(),
                                LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + optWarehouse);

                if (optWarehouse != null && optWarehouse.isEmpty()) {
                    log.info("warehouse not found.");
                    throw new BadRequestException("Warehouse cannot be null.");
                }

                warehouse = optWarehouse.get();

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            // Getting PreInboundNo from NumberRangeTable
            String preInboundNo = getPreInboundNo(warehouseId, inboundOrder.getCompanyCode(), inboundOrder.getBranchCode(), warehouse.getLanguageId());

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                log.info("inboundIntegrationLine : " + inboundIntegrationLine);

                //===========================================Handle MfrName==============================================================//
                if (inboundOrder.getCompanyCode() != null && inboundIntegrationLine.getManufacturerName() == null) {
                    inboundIntegrationLine.setManufacturerName(getMfrName(inboundOrder.getCompanyCode()));
                    if (inboundIntegrationLine.getManufacturerPartNo() == null) {
                        inboundIntegrationLine.setManufacturerPartNo(getMfrName(inboundOrder.getCompanyCode()));
                    }
                    if (inboundIntegrationLine.getManufacturerCode() == null) {
                        inboundIntegrationLine.setManufacturerCode(getMfrName(inboundOrder.getCompanyCode()));
                    }
                }
                //=========================================================================================================//

                ImBasicData1V2 imBasicData1 =
                        imBasicData1V2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                                warehouse.getLanguageId(),
                                warehouse.getCompanyCodeId(),
                                warehouse.getPlantId(),
                                warehouse.getWarehouseId(),
                                inboundIntegrationLine.getItemCode().trim(),
//                            inboundIntegrationLine.getManufacturerName(),
                                0L);
                log.info("imBasicData1 exists: " + imBasicData1);
                if (inboundIntegrationLine.getItemText() == null) {
                    inboundIntegrationLine.setItemText(imBasicData1.getDescription());
                }

                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
                if (imBasicData1 == null) {
                    imBasicData1 = new ImBasicData1V2();
                    imBasicData1.setLanguageId(LANG_ID);                                            // LANG_ID
                    imBasicData1.setWarehouseId(warehouseId);                                    // WH_ID
                    imBasicData1.setCompanyCodeId(warehouse.getCompanyCodeId());                    // C_ID
                    imBasicData1.setPlantId(warehouse.getPlantId());                            // PLANT_ID
                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                // ITM_CODE
                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                    // UOM_ID
                    imBasicData1.setDescription(inboundIntegrationLine.getItemText());            // ITEM_TEXT
//                imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());        // MFR_PART
//                if (inboundIntegrationLine.getManufacturerPartNo() == null && inboundIntegrationLine.getManufacturerCode() != null) {
//                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerCode());
//                }
//                if (inboundIntegrationLine.getManufacturerPartNo() == null && inboundIntegrationLine.getManufacturerCode() == null && inboundIntegrationLine.getManufacturerName() != null) {
                    if (inboundIntegrationLine.getManufacturerName() != null) {
                        imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                        imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                    } else {
                        imBasicData1.setManufacturerPartNo(COMPANY_CODE);
                        imBasicData1.setManufacturerName(COMPANY_CODE);
                    }
                    imBasicData1.setCapacityCheck(false);
                    imBasicData1.setDeletionIndicator(0L);

//                } else {
//                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());        // MFR_PART
//                }
                    imBasicData1.setStatusId(1L);                                                // STATUS_ID
                    ImBasicData1 createdImBasicData1 =
                            mastersService.createImBasicData1V2(imBasicData1, MW_AMS, authTokenForMastersService.getAccess_token());
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
                 */
//            BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId,
//                    warehouse.getCompanyCodeId(),
//                    warehouse.getPlantId(),
//                    warehouse.getLanguageId(),
//                    authTokenForMastersService.getAccess_token());
//            log.info("bomHeader [BOM] : " + bomHeader);
//            if (bomHeader != null) {
//                BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(),
//                        bomHeader.getCompanyCodeId(),
//                        bomHeader.getPlantId(),
//                        bomHeader.getLanguageId(),
//                        bomHeader.getWarehouseId(),
//                        authTokenForMastersService.getAccess_token());
//                List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
//                for (BomLine dbBomLine : bomLine) {
//                    PreInboundLineEntityV2 preInboundLineEntity = createPreInboundLineBOMBasedV2(warehouse, preInboundNo, inboundIntegrationHeader, dbBomLine, inboundIntegrationLine);
//                    log.info("preInboundLineEntity [BOM] : " + preInboundLineEntity);
//                    toBeCreatedPreInboundLineList.add(preInboundLineEntity);
//                }
//
//                // Batch Insert - PreInboundLines
//                if (!toBeCreatedPreInboundLineList.isEmpty()) {
//                    List<PreInboundLineEntityV2> createdPreInboundLine = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
//                    log.info("createdPreInboundLine [BOM] : " + createdPreInboundLine);
//                    overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
//                }
//            }
            }

            /*
             * Append PREINBOUNDLINE table through below logic
             */
            List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                toBeCreatedPreInboundLineList.add(createPreInboundLineV2(warehouse, preInboundNo, inboundIntegrationHeader, inboundIntegrationLine));
            }

            log.info("toBeCreatedPreInboundLineList [API] : " + toBeCreatedPreInboundLineList);

            // Batch Insert - PreInboundLines
            List<PreInboundLineEntityV2> createdPreInboundLine = new ArrayList<>();
            if (!toBeCreatedPreInboundLineList.isEmpty()) {
                createdPreInboundLine = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
                log.info("createdPreInboundLine [API] : " + createdPreInboundLine);
                overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
            }

            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createdPreInboundHeader = createPreInboundHeaderV2(warehouse, preInboundNo, inboundIntegrationHeader);
            log.info("preInboundHeader Created : " + createdPreInboundHeader);

            /*------------------Insert into Inbound Header And Line----------------------------*/
            InboundHeaderV2 createdInboundHeader = createInboundHeaderAndLineV2(createdPreInboundHeader, overallCreatedPreInboundLineList);

            // Inserting into InboundLog Table.
            InboundIntegrationLog createdInboundIntegrationLog = createInboundIntegrationLogV2(createdPreInboundHeader);

//		if (createdInboundIntegrationLog != null) {
//			inboundIntegrationHeader.setProcessedStatusId(1L);
//			inboundIntegrationHeader.setOrderProcessedOn(new Date());
//			mongoRepository.save(inboundIntegrationHeader);
//		}

            // process ASN
//        StagingHeaderV2 stagingHeader = processASNV2(createdPreInboundLine, createdInboundHeader.getCreatedBy());
            StagingHeaderV2 stagingHeader = processNewASNV2(createdPreInboundHeader, createdInboundHeader.getCreatedBy());
            log.info("StagingHeader Created : " + stagingHeader);

            List<StagingLineEntityV2> stagingLines =
                    stagingLineService.createStagingLineV2(createdPreInboundLine, stagingHeader.getStagingNo(), stagingHeader.getWarehouseId(),
                                                           stagingHeader.getCompanyCode(), stagingHeader.getPlantId(), stagingHeader.getLanguageId(),
                                                           createdInboundHeader.getCreatedBy());
            log.info("StagingLines Created : " + stagingLines);

            return createdInboundHeader;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Inbound Order Processing Bad Request Exception : " + e);
        }
    }

    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws BadRequestException
     * @throws SQLException
     * @throws SQLServerException
     * @throws CannotAcquireLockException
     * @throws LockAcquisitionException
     * @throws Exception
     */
    public InboundHeaderV2 processInboundReceivedV3(String refDocNumber,
                                                    InboundIntegrationHeader inboundIntegrationHeader,
                                                    com.tekclover.wms.api.transaction.model.warehouse.Warehouse warehouse)
            throws IllegalAccessException, InvocationTargetException, BadRequestException, SQLException,
            SQLServerException, CannotAcquireLockException, LockAcquisitionException, Exception {
        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundIntegrationHeader.getInboundOrderTypeId());
            if (inboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = inboundIntegrationHeader.getLoginUserId();
            }

            /*
             * Checking whether received refDocNumber processed already.
             */
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository
                    .findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber,
                                                                                 inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException(
                        "Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

            String warehouseId = inboundIntegrationHeader.getWarehouseID();
            String preInboundNo = inboundIntegrationHeader.getPreInboundNo();
            log.info("warehouseId : " + warehouseId);

            // Fetch ITM_CODE inserted in INBOUNDINTEGRATION table and pass the ITM_CODE in
            // IMBASICDATA1 table and
            // validate the ITM_CODE result is Not Null
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            InboundOrderV2 inboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndInboundOrderTypeId(
                    refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId());
            log.info("inboundOrder : " + inboundOrder);

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                log.info("inboundIntegrationLine : " + inboundIntegrationLine);
                ImBasicData1V2 imBasicData1 = imBasicData1V2Repository
                        .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                                warehouse.getLanguageId(), warehouse.getCompanyCodeId(), warehouse.getPlantId(),
                                warehouse.getWarehouseId(), inboundIntegrationLine.getItemCode().trim(),
                                inboundIntegrationLine.getManufacturerName(), 0L);
                log.info("imBasicData1 exists: " + imBasicData1);
                if (inboundIntegrationLine.getItemText() == null && imBasicData1 != null) {
                    inboundIntegrationLine.setItemText(imBasicData1.getDescription());
                }

                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as
                // below
                if (imBasicData1 == null) {
                    imBasicData1 = new ImBasicData1V2();
                    imBasicData1.setLanguageId(LANG_ID); // LANG_ID
                    imBasicData1.setWarehouseId(warehouseId); // WH_ID
                    imBasicData1.setCompanyCodeId(warehouse.getCompanyCodeId()); // C_ID
                    imBasicData1.setPlantId(warehouse.getPlantId()); // PLANT_ID
                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode()); // ITM_CODE
                    imBasicData1.setUomId(inboundIntegrationLine.getUom()); // UOM_ID
                    imBasicData1.setDescription(inboundIntegrationLine.getItemText()); // ITEM_TEXT
                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setCapacityCheck(false);
                    imBasicData1.setDeletionIndicator(0L);
                    imBasicData1.setStatusId(1L); // STATUS_ID
                    ImBasicData1 createdImBasicData1 = mastersService.createImBasicData1V2(imBasicData1, MW_AMS,
                                                                                           authTokenForMastersService.getAccess_token());
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * Before inserting the record into Preinbound table, fetch ITM_CODE from
                 * InboundIntegrationHeader (MONGO) table and pass into BOMHEADER table as
                 * PAR_ITM_CODE and validate record is Not Null
                 */
                BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId,
                                                                  warehouse.getCompanyCodeId(), warehouse.getPlantId(), warehouse.getLanguageId(),
                                                                  authTokenForMastersService.getAccess_token());
                log.info("bomHeader [BOM] : " + bomHeader);
                if (bomHeader != null) {
                    BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(),
                                                                  authTokenForMastersService.getAccess_token());
                    List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
                    for (BomLine dbBomLine : bomLine) {
                        PreInboundLineEntityV2 preInboundLineEntity = createPreInboundLineBOMBasedV2(warehouse,
                                                                                                     preInboundNo, inboundIntegrationHeader, dbBomLine, inboundIntegrationLine);
                        log.info("preInboundLineEntity [BOM] : " + preInboundLineEntity);
                        toBeCreatedPreInboundLineList.add(preInboundLineEntity);
                    }

                    // Batch Insert - PreInboundLines
                    if (!toBeCreatedPreInboundLineList.isEmpty()) {
                        List<PreInboundLineEntityV2> createdPreInboundLine = preInboundLineV2Repository
                                .saveAll(toBeCreatedPreInboundLineList);
                        log.info("createdPreInboundLine [BOM] : " + createdPreInboundLine);
                        overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
                    }
                }
            }

            /*
             * Append PREINBOUNDLINE table through below logic
             */
            List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                toBeCreatedPreInboundLineList.add(createPreInboundLineV2(warehouse, preInboundNo,
                                                                         inboundIntegrationHeader, inboundIntegrationLine));
            }

            log.info("toBeCreatedPreInboundLineList [API] : " + toBeCreatedPreInboundLineList);

            // Batch Insert - PreInboundLines
            List<PreInboundLineEntityV2> createdPreInboundLine = new ArrayList<>();
            if (!toBeCreatedPreInboundLineList.isEmpty()) {
                createdPreInboundLine = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
                log.info("createdPreInboundLine [API] : " + createdPreInboundLine);
                overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
            }

            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createdPreInboundHeader = createPreInboundHeaderV2(warehouse, preInboundNo,
                                                                                        inboundIntegrationHeader);
            log.info("preInboundHeader Created : " + createdPreInboundHeader);

            /*------------------Insert into Inbound Header And Line----------------------------*/
            InboundHeaderV2 createdInboundHeader = createInboundHeaderAndLineV2(createdPreInboundHeader,
                                                                                overallCreatedPreInboundLineList);

            // Inserting into InboundLog Table.
            InboundIntegrationLog createdInboundIntegrationLog = createInboundIntegrationLogV2(createdPreInboundHeader);

            // process ASN
            StagingHeaderV2 stagingHeader = processNewASNV2(createdPreInboundHeader,
                                                            createdInboundHeader.getCreatedBy());
            log.info("StagingHeader Created : " + stagingHeader);

            List<StagingLineEntityV2> stagingLines = stagingLineService.createStagingLineV2(createdPreInboundLine,
                                                                                            stagingHeader.getStagingNo(), stagingHeader.getWarehouseId(), stagingHeader.getCompanyCode(),
                                                                                            stagingHeader.getPlantId(), stagingHeader.getLanguageId(), createdInboundHeader.getCreatedBy());
            log.info("StagingLines Created : " + stagingLines);

            return createdInboundHeader;
        } catch (Exception e) {
            log.error("Inbound Order Processing - Exception ");
            e.printStackTrace();

            // Updating the Processed Status
            log.info("Inbound Rollback Initiated...!" + refDocNumber);

            initiateInboundRollBack(inboundIntegrationHeader.getCompanyCode(), inboundIntegrationHeader.getBranchCode(),
                                    LANG_ID, inboundIntegrationHeader.getWarehouseID(), refDocNumber,
                                    inboundIntegrationHeader.getInboundOrderTypeId());
            orderService.updateProcessedIbOrderV2(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId());
            log.info("Inbound Rollback Finished...!" + refDocNumber);
            throw e;
        }
    }

    /**
     * @param warehouse
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param bomLine
     * @param inboundIntegrationLine
     * @return
     * @throws ParseException
     */
    public PreInboundLineEntityV2 createPreInboundLineBOMBasedV2(com.tekclover.wms.api.transaction.model.warehouse.Warehouse warehouse,
                                                                 String preInboundNo,
                                                                 InboundIntegrationHeader inboundIntegrationHeader,
                                                                 BomLine bomLine,
                                                                 InboundIntegrationLine inboundIntegrationLine) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(warehouse.getLanguageId());
            preInboundLine.setCompanyCode(warehouse.getCompanyCodeId());
            preInboundLine.setPlantId(warehouse.getPlantId());
            preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(inboundIntegrationHeader.getParentProductionOrderNo());

            // PRE_IB_NO
            preInboundLine.setPreInboundNo(preInboundNo);

            // IB__LINE_NO
            preInboundLine.setLineNo(Long.valueOf(inboundIntegrationLine.getLineReference()));

            // ITM_CODE
            preInboundLine.setItemCode(bomLine.getChildItemCode());

            // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            if (inboundIntegrationLine.getItemText() == null) {
                ImBasicData1 imBasicData1 =
                        imBasicData1V2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                                warehouse.getLanguageId(),
                                warehouse.getCompanyCodeId(),
                                warehouse.getPlantId(),
                                warehouse.getWarehouseId(),
                                inboundIntegrationLine.getItemCode(),
//                            inboundIntegrationLine.getManufacturerName(),
                                0L);
                preInboundLine.setItemDescription(imBasicData1.getDescription());
                preInboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
            } else {
                preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
            }

            // MFR

            // ORD_QTY
            double orderQuantity = inboundIntegrationLine.getOrderedQty() * bomLine.getChildItemQuantity();
            preInboundLine.setOrderQty(orderQuantity);

            // ORD_UOM
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

            // EA_DATE
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

            // STCK_TYP_ID
            preInboundLine.setStockTypeId(1L);

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            // STATUS_ID
            preInboundLine.setStatusId(6L);

            statusDescription = stagingLineV2Repository.getStatusDescription(6L, warehouse.getLanguageId());
            preInboundLine.setStatusDescription(statusDescription);

            IKeyValuePair description = stagingLineV2Repository.getDescription(warehouse.getCompanyCodeId(),
                                                                               warehouse.getLanguageId(),
                                                                               warehouse.getPlantId(),
                                                                               warehouse.getWarehouseId());

            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());

            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
            preInboundLine.setBrand(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

            preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
            preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
            preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
            preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
            preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());

            preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
            preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
            preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

            // REF_FIELD_1
            preInboundLine.setReferenceField1("CHILD ITEM");

            // REF_FIELD_2
            preInboundLine.setReferenceField2("BOM ITEM");

            // REF_FIELD_4
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(MW_AMS);
            preInboundLine.setCreatedOn(new Date());
            return preInboundLine;
        } catch (Exception e) {
            log.error("Exception while PreInbound Line create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param warehouse
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @return
     * @throws ParseException
     */
    public PreInboundLineEntityV2 createPreInboundLineV2(com.tekclover.wms.api.transaction.model.warehouse.Warehouse warehouse,
                                                         String preInboundNo,
                                                         InboundIntegrationHeader inboundIntegrationHeader,
                                                         InboundIntegrationLine inboundIntegrationLine) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(warehouse.getLanguageId());
            preInboundLine.setCompanyCode(warehouse.getCompanyCodeId());
            preInboundLine.setPlantId(warehouse.getPlantId());
            preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(inboundIntegrationHeader.getParentProductionOrderNo());

            // PRE_IB_NO
            preInboundLine.setPreInboundNo(preInboundNo);

            // IB__LINE_NO
            preInboundLine.setLineNo(Long.valueOf(inboundIntegrationLine.getLineReference()));

            // ITM_CODE
            preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());

            // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            if (inboundIntegrationLine.getItemText() == null) {
                ImBasicData1 imBasicData1 =
                        imBasicData1V2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                                warehouse.getLanguageId(),
                                warehouse.getCompanyCodeId(),
                                warehouse.getPlantId(),
                                warehouse.getWarehouseId(),
                                inboundIntegrationLine.getItemCode(),
//                            inboundIntegrationLine.getManufacturerName(),
                                0L);
                preInboundLine.setItemDescription(imBasicData1.getDescription());
            } else {
                preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
            }

            // MFR_PART
            preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());

            // PARTNER_CODE
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

            // ORD_QTY
            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());

            // ORD_UOM
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

            // STCK_TYP_ID
            preInboundLine.setStockTypeId(1L);

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            // EA_DATE
            log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

            // ITM_CASE_QTY
            preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());

            // REF_FIELD_4
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            // STATUS_ID
//        preInboundLine.setStatusId(6L);
            preInboundLine.setStatusId(5L);
            statusDescription = stagingLineV2Repository.getStatusDescription(5L, warehouse.getLanguageId());
            preInboundLine.setStatusDescription(statusDescription);

            IKeyValuePair description = stagingLineV2Repository.getDescription(warehouse.getCompanyCodeId(),
                                                                               warehouse.getLanguageId(),
                                                                               warehouse.getPlantId(),
                                                                               warehouse.getWarehouseId());

            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());
            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
            preInboundLine.setBrand(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

            preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
            preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
            preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
            preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
            preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());

            preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
            preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
            preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(MW_AMS);
            preInboundLine.setCreatedOn(new Date());

            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("Exception while PreInbound Line create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param warehouse
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @return
     */
    public PreInboundHeaderEntityV2 createPreInboundHeaderV2(com.tekclover.wms.api.transaction.model.warehouse.Warehouse warehouse,
                                                             String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {
        try {
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(inboundIntegrationHeader, preInboundHeader, CommonUtils.getNullPropertyNames(inboundIntegrationHeader));
            preInboundHeader.setLanguageId(warehouse.getLanguageId());                                    // LANG_ID
            preInboundHeader.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
            preInboundHeader.setCompanyCode(warehouse.getCompanyCodeId());
            preInboundHeader.setPlantId(warehouse.getPlantId());
            preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
            preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
            preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
            preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
//        preInboundHeader.setStatusId(6L);
            preInboundHeader.setStatusId(5L);
            statusDescription = stagingLineV2Repository.getStatusDescription(5L, warehouse.getLanguageId());
            preInboundHeader.setStatusDescription(statusDescription);

            IKeyValuePair description = stagingLineV2Repository.getDescription(warehouse.getCompanyCodeId(),
                                                                               warehouse.getLanguageId(),
                                                                               warehouse.getPlantId(),
                                                                               warehouse.getWarehouseId());

            preInboundHeader.setCompanyDescription(description.getCompanyDesc());
            preInboundHeader.setPlantDescription(description.getPlantDesc());
            preInboundHeader.setWarehouseDescription(description.getWarehouseDesc());

            preInboundHeader.setMiddlewareId(inboundIntegrationHeader.getMiddlewareId());
            preInboundHeader.setMiddlewareTable(inboundIntegrationHeader.getMiddlewareTable());
//        preInboundHeader.setManufacturerFullName(inboundIntegrationHeader.getManufacturerFullName());
            preInboundHeader.setContainerNo(inboundIntegrationHeader.getContainerNo());

            preInboundHeader.setTransferOrderDate(inboundIntegrationHeader.getTransferOrderDate());
            preInboundHeader.setSourceBranchCode(inboundIntegrationHeader.getSourceBranchCode());
            preInboundHeader.setSourceCompanyCode(inboundIntegrationHeader.getSourceCompanyCode());
            preInboundHeader.setIsCompleted(inboundIntegrationHeader.getIsCompleted());
            preInboundHeader.setIsCancelled(inboundIntegrationHeader.getIsCancelled());
            preInboundHeader.setMUpdatedOn(inboundIntegrationHeader.getUpdatedOn());

            preInboundHeader.setDeletionIndicator(0L);
            preInboundHeader.setCreatedBy(MW_AMS);
            preInboundHeader.setCreatedOn(new Date());
            PreInboundHeaderEntityV2 createdPreInboundHeader = preInboundHeaderV2Repository.save(preInboundHeader);
            log.info("createdPreInboundHeader : " + createdPreInboundHeader);
            return createdPreInboundHeader;
        } catch (Exception e) {
            log.error("Exception while PreInbound Header create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param preInboundHeader
     * @param preInboundLine
     * @return
     */
    public InboundHeaderV2 createInboundHeaderAndLineV2(PreInboundHeaderEntityV2 preInboundHeader, List<PreInboundLineEntityV2> preInboundLine) throws Exception {
        try {
            InboundHeaderV2 inboundHeader = new InboundHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
//        inboundHeader.setStatusId(6L);
            inboundHeader.setStatusId(5L);
            statusDescription = stagingLineV2Repository.getStatusDescription(5L, preInboundHeader.getLanguageId());
            inboundHeader.setStatusDescription(statusDescription);

            IKeyValuePair description = stagingLineV2Repository.getDescription(preInboundHeader.getCompanyCode(),
                                                                               preInboundHeader.getLanguageId(),
                                                                               preInboundHeader.getPlantId(),
                                                                               preInboundHeader.getWarehouseId());

            inboundHeader.setCompanyDescription(description.getCompanyDesc());
            inboundHeader.setPlantDescription(description.getPlantDesc());
            inboundHeader.setWarehouseDescription(description.getWarehouseDesc());

            inboundHeader.setMiddlewareId(preInboundHeader.getMiddlewareId());
            inboundHeader.setMiddlewareTable(preInboundHeader.getMiddlewareTable());
            inboundHeader.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());
            inboundHeader.setManufacturerFullName(preInboundHeader.getManufacturerFullName());
            inboundHeader.setContainerNo(preInboundHeader.getContainerNo());

            inboundHeader.setTransferOrderDate(preInboundHeader.getTransferOrderDate());
            inboundHeader.setSourceBranchCode(preInboundHeader.getSourceBranchCode());
            inboundHeader.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
            inboundHeader.setIsCompleted(preInboundHeader.getIsCompleted());
            inboundHeader.setIsCancelled(preInboundHeader.getIsCancelled());
            inboundHeader.setMUpdatedOn(preInboundHeader.getMUpdatedOn());
            inboundHeader.setCountOfOrderLines((long) preInboundLine.size());       //count of lines

            inboundHeader.setDeletionIndicator(0L);
            inboundHeader.setCreatedBy(preInboundHeader.getCreatedBy());
            inboundHeader.setCreatedOn(preInboundHeader.getCreatedOn());
            InboundHeaderV2 createdInboundHeader = inboundHeaderV2Repository.save(inboundHeader);
            log.info("createdInboundHeader : " + createdInboundHeader);

            /*
             * Inbound Line Table Insert
             */
            List<InboundLineV2> toBeCreatedInboundLineList = new ArrayList<>();
            for (PreInboundLineEntityV2 createdPreInboundLine : preInboundLine) {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(createdPreInboundLine, inboundLine, CommonUtils.getNullPropertyNames(createdPreInboundLine));

                inboundLine.setOrderQty(createdPreInboundLine.getOrderQty());
                inboundLine.setOrderUom(createdPreInboundLine.getOrderUom());
                inboundLine.setDescription(createdPreInboundLine.getItemDescription());
                inboundLine.setBusinessPartnerCode(createdPreInboundLine.getBusinessPartnerCode());
                inboundLine.setReferenceField4(createdPreInboundLine.getReferenceField4());

                inboundLine.setCompanyDescription(description.getCompanyDesc());
                inboundLine.setPlantDescription(description.getPlantDesc());
                inboundLine.setWarehouseDescription(description.getWarehouseDesc());
                inboundLine.setStatusDescription(statusDescription);
                inboundLine.setContainerNo(createdPreInboundLine.getContainerNo());
                inboundLine.setSupplierName(createdPreInboundLine.getSupplierName());

                inboundLine.setMiddlewareId(createdPreInboundLine.getMiddlewareId());
                inboundLine.setMiddlewareHeaderId(createdPreInboundLine.getMiddlewareHeaderId());
                inboundLine.setMiddlewareTable(createdPreInboundLine.getMiddlewareTable());
                inboundLine.setReferenceDocumentType(createdInboundHeader.getReferenceDocumentType());
                inboundLine.setManufacturerFullName(createdPreInboundLine.getManufacturerFullName());
                inboundLine.setPurchaseOrderNumber(createdPreInboundLine.getPurchaseOrderNumber());

                inboundLine.setManufacturerCode(createdPreInboundLine.getManufacturerName());
                inboundLine.setManufacturerName(createdPreInboundLine.getManufacturerName());
                inboundLine.setExpectedArrivalDate(createdPreInboundLine.getExpectedArrivalDate());
                inboundLine.setOrderQty(createdPreInboundLine.getOrderQty());
                inboundLine.setOrderUom(createdPreInboundLine.getOrderUom());

                inboundLine.setBusinessPartnerCode(createdPreInboundLine.getBusinessPartnerCode());
                inboundLine.setManufacturerPartNo(createdPreInboundLine.getManufacturerPartNo());

                inboundLine.setBranchCode(createdPreInboundLine.getBranchCode());
                inboundLine.setTransferOrderNo(createdPreInboundLine.getTransferOrderNo());
                inboundLine.setIsCompleted(createdPreInboundLine.getIsCompleted());

                inboundLine.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
                inboundLine.setSourceBranchCode(preInboundHeader.getSourceBranchCode());

                inboundLine.setDeletionIndicator(0L);
                inboundLine.setCreatedBy(preInboundHeader.getCreatedBy());
                inboundLine.setCreatedOn(preInboundHeader.getCreatedOn());
                toBeCreatedInboundLineList.add(inboundLine);
            }

            List<InboundLineV2> createdInboundLine = inboundLineV2Repository.saveAll(toBeCreatedInboundLineList);
            log.info("createdInboundLine : " + createdInboundLine);

            return createdInboundHeader;
        } catch (Exception e) {
            log.error("Exception while Inbound Header & Line create : " + e.toString());
            throw e;
        }
    }


    /**
     * @param inputPreInboundLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public StagingHeaderV2 processASNV2(List<PreInboundLineEntityV2> inputPreInboundLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        boolean isPreInboundHeaderUpdated = false;
        boolean isPreInboundLineUpdated = false;

        // Update PREINBOUNDHEADER and PREINBOUNDLINE table with STATUS_ID = 05 and update the other fields from UI
        // PREINBOUNDLINE Update
        String preInboundNo = null;
        String containerNo = null;
        String warehouseId = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        for (PreInboundLineEntityV2 objUpdatePreInboundLine : inputPreInboundLines) {
            objUpdatePreInboundLine.setStatusId(5L);
            statusDescription = stagingLineV2Repository.getStatusDescription(5L, objUpdatePreInboundLine.getLanguageId());
            objUpdatePreInboundLine.setStatusDescription(statusDescription);
            objUpdatePreInboundLine.setUpdatedBy(loginUserID);
            objUpdatePreInboundLine.setUpdatedOn(new Date());
            PreInboundLineEntityV2 updatedPreInboundLine = preInboundLineV2Repository.save(objUpdatePreInboundLine);
            log.info("preInboundLine updated: " + updatedPreInboundLine);
            if (updatedPreInboundLine != null) {
                isPreInboundLineUpdated = true;
                preInboundNo = updatedPreInboundLine.getPreInboundNo();
                containerNo = updatedPreInboundLine.getContainerNo();
                warehouseId = updatedPreInboundLine.getWarehouseId();
                companyCode = updatedPreInboundLine.getCompanyCode();
                plantId = updatedPreInboundLine.getPlantId();
                languageId = updatedPreInboundLine.getLanguageId();
            }
        }

        // PREINBOUNDHEADER Update
        // PreInboundHeaderV2 preInboundHeader = getPreInboundHeaderV2(preInboundNo, warehouseId);
        PreInboundHeaderV2 preInboundHeader = getPreInboundHeaderV2(preInboundNo, warehouseId, companyCode, plantId, languageId);
        log.info("preInboundHeader---found-------> : " + preInboundHeader);

        PreInboundHeaderEntityV2 preInboundHeaderEntity = copyBeanToHeaderEntity(preInboundHeader);
        preInboundHeaderEntity.setContainerNo(containerNo);
        preInboundHeaderEntity.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, preInboundHeader.getLanguageId());
        preInboundHeaderEntity.setStatusDescription(statusDescription);
        PreInboundHeaderEntity updatedPreInboundHeaderEntity = preInboundHeaderV2Repository.save(preInboundHeaderEntity);
        log.info("updatedPreInboundHeaderEntity---@------> : " + updatedPreInboundHeaderEntity);
        if (updatedPreInboundHeaderEntity != null) {
            isPreInboundHeaderUpdated = true;
        }

        // Update INBOUNDHEADER and INBOUNDLINE table
        if (isPreInboundHeaderUpdated && isPreInboundLineUpdated) {

            Optional<InboundHeaderV2> optInboundHeader =
                    inboundHeaderV2Repository.findByWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                            warehouseId, preInboundHeader.getRefDocNumber(), preInboundHeader.getPreInboundNo(), 0L);
            if (optInboundHeader.isPresent()) {
                log.info("optInboundHeader : " + optInboundHeader.get());
                InboundHeaderV2 updateInboundHeader = optInboundHeader.get();
                updateInboundHeader.setContainerNo(containerNo);
                updateInboundHeader.setStatusId(5L);
                statusDescription = stagingLineV2Repository.getStatusDescription(5L, updateInboundHeader.getLanguageId());
                updateInboundHeader.setStatusDescription(statusDescription);
                inboundHeaderV2Repository.saveAndFlush(updateInboundHeader);
            }
//            InboundHeaderV2 updateInboundHeader = new InboundHeaderV2();
//            updateInboundHeader.setContainerNo(containerNo);
//            updateInboundHeader.setStatusId(5L);
//            statusDescription = stagingLineV2Repository.getStatusDescription(5L, preInboundHeader.getLanguageId());
//            updateInboundHeader.setStatusDescription(statusDescription);
//            // warehouseId, refDocNumber, preInboundNo, loginUserID, updateInboundHeader
//            InboundHeaderV2 updatedInboundHeader =
//                    inboundHeaderService.updateInboundHeaderV2(preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber(),
//                            preInboundNo, loginUserID, updateInboundHeader);
//            log.info("updatedInboundHeader : " + updatedInboundHeader);

            // INBOUNDLINE table update
            for (PreInboundLineEntityV2 dbPreInboundLine : inputPreInboundLines) {
                InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(preInboundHeader.getCompanyCode(),
                                                                                preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(),
                                                                                preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber(),
                                                                                preInboundNo, dbPreInboundLine.getLineNo(), dbPreInboundLine.getItemCode());
                inboundLine.setContainerNo(dbPreInboundLine.getContainerNo());
                inboundLine.setInvoiceNo(dbPreInboundLine.getInvoiceNo());
                inboundLine.setStatusId(5L);
                statusDescription = stagingLineV2Repository.getStatusDescription(5L, preInboundHeader.getLanguageId());
                inboundLine.setStatusDescription(statusDescription);
                // warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine
                InboundLineV2 updatedInboundLine = inboundLineV2Repository.save(inboundLine);
                log.info("updatedInboundLine updated: " + updatedInboundLine);
            }
        }

        StagingHeaderV2 stagingHeader = new StagingHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

        // STG_NO
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();

        long NUMBER_RANGE_CODE = 3;
        WAREHOUSEID_NUMBERRANGE = preInboundHeader.getWarehouseId();
        String nextRangeNumber = getNextRangeNumber(NUMBER_RANGE_CODE,
                                                    preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(), WAREHOUSEID_NUMBERRANGE,
                                                    authTokenForIDMasterService.getAccess_token());
        stagingHeader.setStagingNo(nextRangeNumber);

        // GR_MTD
        stagingHeader.setGrMtd("INTEGRATION");

        // STATUS_ID
        stagingHeader.setStatusId(12L);
        statusDescription = stagingLineV2Repository.getStatusDescription(12L, preInboundHeader.getLanguageId());
        stagingHeader.setStatusDescription(statusDescription);
        IKeyValuePair description = stagingLineV2Repository.getDescription(preInboundHeader.getCompanyCode(),
                                                                           preInboundHeader.getLanguageId(),
                                                                           preInboundHeader.getPlantId(),
                                                                           warehouseId);

        stagingHeader.setCompanyDescription(description.getCompanyDesc());
        stagingHeader.setPlantDescription(description.getPlantDesc());
        stagingHeader.setWarehouseDescription(description.getWarehouseDesc());

        stagingHeader.setContainerNo(preInboundHeader.getContainerNo());
        stagingHeader.setMiddlewareId(preInboundHeader.getMiddlewareId());
        stagingHeader.setMiddlewareTable(preInboundHeader.getMiddlewareTable());
        stagingHeader.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());
        stagingHeader.setManufacturerFullName(preInboundHeader.getManufacturerFullName());

        stagingHeader.setTransferOrderDate(preInboundHeader.getTransferOrderDate());
        stagingHeader.setSourceBranchCode(preInboundHeader.getSourceBranchCode());
        stagingHeader.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
        stagingHeader.setIsCompleted(preInboundHeader.getIsCompleted());
        stagingHeader.setIsCancelled(preInboundHeader.getIsCancelled());
        stagingHeader.setMUpdatedOn(preInboundHeader.getMUpdatedOn());

        stagingHeader.setCreatedBy(preInboundHeader.getCreatedBy());
        stagingHeader.setCreatedOn(preInboundHeader.getCreatedOn());
        return stagingHeaderV2Repository.save(stagingHeader);
    }

    /**
     * To avoid Deadlock
     * @param preInboundHeader
     * @param loginUserID
     * @return
     */
    public StagingHeaderV2 processNewASNV2(PreInboundHeaderEntityV2 preInboundHeader, String loginUserID) throws Exception {

        try {
            StagingHeaderV2 stagingHeader = new StagingHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

            // STG_NO
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();

            long NUMBER_RANGE_CODE = 3;
            WAREHOUSEID_NUMBERRANGE = preInboundHeader.getWarehouseId();
            String nextRangeNumber = getNextRangeNumber(NUMBER_RANGE_CODE,
                                                        preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(), WAREHOUSEID_NUMBERRANGE,
                                                        authTokenForIDMasterService.getAccess_token());
            stagingHeader.setStagingNo(nextRangeNumber);

            // GR_MTD
            stagingHeader.setGrMtd("INTEGRATION");

            // STATUS_ID
            stagingHeader.setStatusId(12L);
            statusDescription = stagingLineV2Repository.getStatusDescription(12L, preInboundHeader.getLanguageId());
            stagingHeader.setStatusDescription(statusDescription);
            IKeyValuePair description = stagingLineV2Repository.getDescription(preInboundHeader.getCompanyCode(),
                                                                               preInboundHeader.getLanguageId(),
                                                                               preInboundHeader.getPlantId(),
                                                                               preInboundHeader.getWarehouseId());

            stagingHeader.setCompanyDescription(description.getCompanyDesc());
            stagingHeader.setPlantDescription(description.getPlantDesc());
            stagingHeader.setWarehouseDescription(description.getWarehouseDesc());

            stagingHeader.setContainerNo(preInboundHeader.getContainerNo());
            stagingHeader.setMiddlewareId(preInboundHeader.getMiddlewareId());
            stagingHeader.setMiddlewareTable(preInboundHeader.getMiddlewareTable());
            stagingHeader.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());
            stagingHeader.setManufacturerFullName(preInboundHeader.getManufacturerFullName());

            stagingHeader.setTransferOrderDate(preInboundHeader.getTransferOrderDate());
            stagingHeader.setSourceBranchCode(preInboundHeader.getSourceBranchCode());
            stagingHeader.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
            stagingHeader.setIsCompleted(preInboundHeader.getIsCompleted());
            stagingHeader.setIsCancelled(preInboundHeader.getIsCancelled());
            stagingHeader.setMUpdatedOn(preInboundHeader.getMUpdatedOn());

            stagingHeader.setCreatedBy(preInboundHeader.getCreatedBy());
            stagingHeader.setCreatedOn(preInboundHeader.getCreatedOn());
            return stagingHeaderV2Repository.save(stagingHeader);
        } catch (Exception e) {
            log.error("Exception while Staging Header create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param refDocNumber
     */
    public void updateASNV2(String refDocNumber) {
        List<PreInboundHeader> preInboundHeaders = getPreInboundHeaders();
        List<PreInboundHeaderEntity> preInboundHeadersEntityList = createEntityList(preInboundHeaders);
        preInboundHeaders.forEach(preibheaders -> preibheaders.setReferenceField1(refDocNumber));
        preInboundHeaderRepository.saveAll(preInboundHeadersEntityList);
    }

    /**
     * @return
     */
    public String getPreInboundNo(String warehouseId, String companyCodeId, String plantId, String languageId) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 2 values in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then
         * update in PREINBOUNDHEADER table
         */
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            String nextRangeNumber = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }

    /**
     * @param createdPreInboundHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundIntegrationLog createInboundIntegrationLogV2(PreInboundHeaderEntityV2 createdPreInboundHeader)
            throws IllegalAccessException, InvocationTargetException, Exception {
        try {
            InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
            dbInboundIntegrationLog.setLanguageId(LANG_ID);
            dbInboundIntegrationLog.setCompanyCodeId(createdPreInboundHeader.getCompanyCode());
            dbInboundIntegrationLog.setPlantId(createdPreInboundHeader.getPlantId());
            dbInboundIntegrationLog.setWarehouseId(createdPreInboundHeader.getWarehouseId());
            dbInboundIntegrationLog.setIntegrationLogNumber(createdPreInboundHeader.getPreInboundNo());
            dbInboundIntegrationLog.setRefDocNumber(createdPreInboundHeader.getRefDocNumber());
            dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
            dbInboundIntegrationLog.setIntegrationStatus("SUCCESS");
            dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
            dbInboundIntegrationLog.setDeletionIndicator(0L);
            dbInboundIntegrationLog.setCreatedBy(createdPreInboundHeader.getCreatedBy());
            dbInboundIntegrationLog.setCreatedOn(new Date());
            dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
            log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
            return dbInboundIntegrationLog;
        } catch (Exception e) {
            log.error("Exception while InboundIntegration Log create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param preInboundHeaderEntity
     * @return
     */
    private PreInboundHeaderV2 copyHeaderEntityToBean(PreInboundHeaderEntityV2 preInboundHeaderEntity) {
        PreInboundHeaderV2 preInboundHeader = new PreInboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeaderEntity, preInboundHeader, CommonUtils.getNullPropertyNames(preInboundHeaderEntity));
        return preInboundHeader;
    }

    /**
     * @param preInboundHeader
     * @return
     */
    private PreInboundHeaderEntityV2 copyBeanToHeaderEntity(PreInboundHeaderV2 preInboundHeader) {
        PreInboundHeaderEntityV2 preInboundHeaderEntity = new PreInboundHeaderEntityV2();
        BeanUtils.copyProperties(preInboundHeader, preInboundHeaderEntity, CommonUtils.getNullPropertyNames(preInboundHeader));
        return preInboundHeaderEntity;
    }

    /**
     * @param companyCode
     * @param branchCode
     * @return
     */
    private com.tekclover.wms.api.transaction.model.warehouse.Warehouse getWarehouse(String companyCode, String branchCode) {
        Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> optWarehouse =
                warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                        companyCode, branchCode, LANG_ID, 0L);
        if (optWarehouse.isEmpty()) {
            log.info("dbWarehouse not found for companyCode & branchCode: " + companyCode + "," + branchCode);
            return null;
        }

        return optWarehouse.get();
    }

    /**
     * @param inbound
     * @param errorDesc
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public InboundIntegrationLog createInboundIntegrationLogV2(InboundIntegrationHeader inbound, String errorDesc)
            throws IllegalAccessException, InvocationTargetException {
        com.tekclover.wms.api.transaction.model.warehouse.Warehouse warehouse =
                getWarehouse(inbound.getCompanyCode(), inbound.getBranchCode());
        if (warehouse == null) {
            throw new BadRequestException("Warehouse not found : " + inbound.getWarehouseID());
        }
        InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
        dbInboundIntegrationLog.setLanguageId(LANG_ID);
        dbInboundIntegrationLog.setCompanyCodeId(warehouse.getCompanyCodeId());
        dbInboundIntegrationLog.setPlantId(warehouse.getPlantId());
        dbInboundIntegrationLog.setWarehouseId(warehouse.getWarehouseId());
        dbInboundIntegrationLog.setIntegrationLogNumber(inbound.getRefDocumentNo());
        dbInboundIntegrationLog.setRefDocNumber(inbound.getRefDocumentNo());
        dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
        dbInboundIntegrationLog.setIntegrationStatus("FAILED");
        dbInboundIntegrationLog.setOrderReceiptDate(inbound.getOrderProcessedOn());
        dbInboundIntegrationLog.setDeletionIndicator(0L);
        dbInboundIntegrationLog.setCreatedBy("MSD_API");

        dbInboundIntegrationLog.setCreatedOn(new Date());
        dbInboundIntegrationLog = inboundIntegrationLogRepository.save(dbInboundIntegrationLog);
        log.info("dbInboundIntegrationLog : " + dbInboundIntegrationLog);
        return dbInboundIntegrationLog;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserId
     * @return
     */
    // Delete PreInboundHeader
    public PreInboundHeaderEntityV2 deletePreInboundHeader(String companyCode, String plantId, String languageId,
                                                           String warehouseId, String refDocNumber, String preInboundNo, String loginUserId) {
        PreInboundHeaderEntityV2 preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("preInboundHeaderEntity - Cancellation: " + preInboundHeaderEntity);
        if (preInboundHeaderEntity != null) {
            preInboundHeaderEntity.setDeletionIndicator(1L);
            preInboundHeaderEntity.setUpdatedBy(loginUserId);
            preInboundHeaderV2Repository.save(preInboundHeaderEntity);
        }
        return preInboundHeaderEntity;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserId
     * @return
     */
    public PreInboundHeaderEntityV2 cancelPreInboundHeader(String companyCode, String plantId, String languageId, String warehouseId,
                                                           String refDocNumber, String preInboundNo, String loginUserId, String remarks) {
        PreInboundHeaderEntityV2 preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("preInboundHeaderEntity - Order Cancellation: " + preInboundHeaderEntity);
        if (preInboundHeaderEntity != null) {
            preInboundHeaderEntity.setStatusId(96L);
            statusDescription = stagingLineV2Repository.getStatusDescription(96L, languageId);
            preInboundHeaderEntity.setStatusDescription(statusDescription);
            if (remarks != null) {
                preInboundHeaderEntity.setReferenceField1(remarks);
            }
            preInboundHeaderEntity.setUpdatedBy(loginUserId);
            preInboundHeaderEntity.setUpdatedOn(new Date());
            preInboundHeaderV2Repository.save(preInboundHeaderEntity);
        }
        return preInboundHeaderEntity;
    }

    //=========================================PreInboundHeader_ExceptionLog===========================================
    private void createPreInboundHeaderLog1(String languageId, String companyCode, String plantId, String warehouseId,
                                            String refDocNumber, String preInboundNo, String error) {

        ErrorLog dbErrorLog = new ErrorLog();
        dbErrorLog.setOrderTypeId(refDocNumber);
        dbErrorLog.setOrderDate(new Date());
        dbErrorLog.setLanguageId(languageId);
        dbErrorLog.setCompanyCodeId(companyCode);
        dbErrorLog.setPlantId(plantId);
        dbErrorLog.setWarehouseId(warehouseId);
        dbErrorLog.setRefDocNumber(refDocNumber);
        dbErrorLog.setReferenceField1(preInboundNo);
        dbErrorLog.setErrorMessage(error);
        dbErrorLog.setCreatedBy("MSD_API");
        dbErrorLog.setCreatedOn(new Date());
        exceptionLogRepo.save(dbErrorLog);
    }

    //=======================================Roll Back Code=============================================================//
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = Exception.class, maxAttempts = 1, backoff = @Backoff(delay = 10000))
    public void initiateInboundRollBack(String companyCodeId, String plantId, String languageId, String warehouseId,
                                        String refDocNo, Long inboundOrderTypeId) throws Exception {
        try {
            //delete all records from respective tables
            log.info("Rollback---> delete all record initiated ----> " + refDocNo + ", " + inboundOrderTypeId);
            preInboundHeaderRepository.deleteInboundProcessingProc(companyCodeId, plantId, languageId, warehouseId, refDocNo, inboundOrderTypeId);
            log.info("Rollback---> delete all record finished ----> " + refDocNo + ", " + inboundOrderTypeId);
//            throw new BadRequestException("Exception roll back testing");
        } catch (Exception e) {
            log.error("Exception While Inbound Rollback");
            e.printStackTrace();
            throw e;
        }
    }

    //=======================================================Base=============================================

    /**
     *
     * @param refDocNumber
     * @param orderTypeId
     * @return
     */
    public PreInboundHeaderEntityV2 getPreInboundHeaderForDuplicateCheckV2(String companyCodeId, String plantId, String languageId,
                                                                           String warehouseId, String refDocNumber, Long orderTypeId) {
        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, orderTypeId, 0L);
        if (preInboundHeaderEntity.isPresent()) {
            return preInboundHeaderEntity.get();
        }
        return null;
    }
}