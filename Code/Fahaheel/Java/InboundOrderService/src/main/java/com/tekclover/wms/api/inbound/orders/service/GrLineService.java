package com.tekclover.wms.api.inbound.orders.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.*;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.orders.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
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

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class GrLineService extends BaseService {
    @Autowired
    private StorageBinRepository storageBinRepository;
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
    private PutAwayLineRepository putAwayLineRepository;

    @Autowired
    private StagingLineService stagingLineService;

    @Autowired
    private StagingHeaderRepository stagingHeaderRepository;

    @Autowired
    private GrHeaderRepository grHeaderRepository;

    @Autowired
    private GrLineService grLineService;

    @Autowired
    private AuthTokenService authTokenService;

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
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private MastersService mastersService;

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    StockReceiptServiceV2 stockReceiptServiceV2;

    @Autowired
    ImBasicData1Repository imBasicData1Repository;

    @Autowired
    DbConfigRepository dbConfigRepository;
    @Autowired
    InventoryTransRepository inventoryTransRepository;

    String statusDescription = null;
    boolean alreadyExecuted = true;
    boolean inventoryError = false;
    long inventoryErrorCount = 0;


    //    @Transactional
    public List<GrLineV2> createGrLineNonCBMV2(@Valid List<AddGrLineV2> newGrLines, String loginUserID) throws java.text.ParseException {
        List<GrLineV2> createdGRLines = new ArrayList<>();
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String goodsReceiptNo = null;
        try {

            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {
                if (newGrLine.getPackBarcodes() == null || newGrLine.getPackBarcodes().isEmpty()) {
                    throw new BadRequestException("Enter either Accept Qty or Damage Qty");
                }
                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLineV2 dbGrLine = new GrLineV2();
                    log.info("newGrLine : " + newGrLine);
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));
                    dbGrLine.setCompanyCode(newGrLine.getCompanyCode());

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setAcceptedQty(grQty);
                        dbGrLine.setDamageQty(0D);
                        log.info("A-------->: " + dbGrLine);
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
                        Double grQty = newGrLine.getDamageQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(newGrLine.getDamageQty());
                        dbGrLine.setAcceptedQty(0D);
                        log.info("D-------->: " + dbGrLine);
                    }

                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setPackBarcodes(packBarcode.getBarcode());
                    dbGrLine.setStatusId(14L);

                    //12-03-2024 - Ticket No. ALM/2024/006
                    if (dbGrLine.getGoodReceiptQty() < 0) {
                        throw new BadRequestException("Gr Quantity Cannot be Negative");
                    }
                    log.info("StatusId: " + newGrLine.getStatusId());
                    if (newGrLine.getStatusId() == 24L) {
                        throw new BadRequestException("GrLine is already Confirmed");
                    }

                    //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                            newGrLine.getCompanyCode(), newGrLine.getPlantId(), newGrLine.getLanguageId(), newGrLine.getWarehouseId(),
                            newGrLine.getRefDocNumber(), newGrLine.getPreInboundNo(), newGrLine.getGoodsReceiptNo(), newGrLine.getPalletCode(),
                            newGrLine.getCaseCode(), newGrLine.getItemCode(), newGrLine.getManufacturerName(), newGrLine.getLineNo());
                    log.info("dbGrQty, newGrQty, OrdQty: " + dbGrQty + ", " + dbGrLine.getGoodReceiptQty() + ", " + newGrLine.getOrderQty());
                    if (dbGrQty != null) {
                        Double totalGrQty = dbGrQty + dbGrLine.getGoodReceiptQty();
//                        if (newGrLine.getOrderQty() < totalGrQty) {
//                            throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
//                        }
                    }

                    //V2 Code
                    IKeyValuePair description = stagingLineV2Repository.getDescription(newGrLine.getCompanyCode(),
                            newGrLine.getLanguageId(),
                            newGrLine.getPlantId(),
                            newGrLine.getWarehouseId());

                    statusDescription = stagingLineV2Repository.getStatusDescription(dbGrLine.getStatusId(), newGrLine.getLanguageId());
                    dbGrLine.setStatusDescription(statusDescription);

                    if (description != null) {
                        dbGrLine.setCompanyDescription(description.getCompanyDesc());
                        dbGrLine.setPlantDescription(description.getPlantDesc());
                        dbGrLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    dbGrLine.setMiddlewareId(newGrLine.getMiddlewareId());
                    dbGrLine.setMiddlewareHeaderId(newGrLine.getMiddlewareHeaderId());
                    dbGrLine.setMiddlewareTable(newGrLine.getMiddlewareTable());
                    dbGrLine.setManufacturerFullName(newGrLine.getManufacturerFullName());
                    dbGrLine.setReferenceDocumentType(newGrLine.getReferenceDocumentType());
                    dbGrLine.setPurchaseOrderNumber(newGrLine.getPurchaseOrderNumber());

                    Double recAcceptQty = 0D;
                    Double recDamageQty = 0D;
                    Double variance = 0D;
                    Double invoiceQty = 0D;
                    Double acceptQty = 0D;
                    Double damageQty = 0D;

                    if (newGrLine.getOrderQty() != null) {
                        invoiceQty = newGrLine.getOrderQty();
                    }
                    if (newGrLine.getAcceptedQty() != null) {
                        acceptQty = newGrLine.getAcceptedQty();
                    }
                    if (newGrLine.getDamageQty() != null) {
                        damageQty = newGrLine.getDamageQty();
                    }

                    StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(newGrLine.getCompanyCode(),
                            newGrLine.getPlantId(),
                            newGrLine.getLanguageId(),
                            newGrLine.getWarehouseId(),
                            newGrLine.getPreInboundNo(),
                            newGrLine.getRefDocNumber(),
                            newGrLine.getLineNo(),
                            newGrLine.getItemCode(),
                            newGrLine.getManufacturerName());
                    log.info("StagingLine: " + dbStagingLineEntity);

                    if (dbStagingLineEntity != null) {
                        if (dbStagingLineEntity.getRec_accept_qty() != null) {
                            recAcceptQty = dbStagingLineEntity.getRec_accept_qty();
                        }
                        if (dbStagingLineEntity.getRec_damage_qty() != null) {
                            recDamageQty = dbStagingLineEntity.getRec_damage_qty();
                        }
                        dbGrLine.setOrderUom(dbStagingLineEntity.getOrderUom());
                        dbGrLine.setGrUom(dbStagingLineEntity.getOrderUom());
                    }

                    variance = invoiceQty - (acceptQty + damageQty + recAcceptQty + recDamageQty);
                    log.info("Variance: " + variance);

                    if (variance == 0D) {
                        dbGrLine.setStatusId(17L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(17L, newGrLine.getLanguageId());
                        dbGrLine.setStatusDescription(statusDescription);
                    }

                    if (variance < 0D) {
                        throw new BadRequestException("Variance Qty cannot be Less than 0");
                    }
                    dbGrLine.setConfirmedQty(dbGrLine.getGoodReceiptQty());
                    dbGrLine.setBranchCode(newGrLine.getBranchCode());
                    dbGrLine.setTransferOrderNo(newGrLine.getTransferOrderNo());
                    dbGrLine.setIsCompleted(newGrLine.getIsCompleted());

                    dbGrLine.setIsPutAwayHeaderCreated(0L);
                    dbGrLine.setBarcodeId(newGrLine.getBarcodeId());
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(loginUserID);
                    dbGrLine.setUpdatedBy(loginUserID);
                    dbGrLine.setConfirmedBy(loginUserID);
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
                    dbGrLine.setConfirmedOn(new Date());

                    companyCode = dbGrLine.getCompanyCode();
                    plantId = dbGrLine.getPlantId();
                    languageId = dbGrLine.getLanguageId();
                    warehouseId = dbGrLine.getWarehouseId();
                    refDocNumber = dbGrLine.getRefDocNumber();
                    preInboundNo = dbGrLine.getPreInboundNo();
                    goodsReceiptNo = dbGrLine.getGoodsReceiptNo();

                    GrLineV2 createdGRLine = null;
                    createdGRLine = grLineV2Repository.saveAndFlush(dbGrLine);
                    log.info("createdGRLine : " + createdGRLine);
                    createdGRLines.add(createdGRLine);
                    //Update staging Line using stored Procedure
                    log.info(companyCode + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + refDocNumber + "|" + preInboundNo + "|" + createdGRLine.getLineNo() + "|" + createdGRLine.getItemCode() + "|" + createdGRLine.getManufacturerName());
                    stagingLineV2Repository.updateStagingLineUpdateNewProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                            createdGRLine.getLineNo(), createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), new Date());
                    log.info("stagingLine Status updated using Stored Procedure ");

                    //Update InboundLine using Stored Procedure
                    inboundLineV2Repository.updateInboundLineStatusUpdateNewProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                            createdGRLine.getLineNo(), createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), 17L, statusDescription, new Date());
                    log.info("inboundLine Status updated using Stored Procedure ");
                }
                log.info("Records were inserted successfully...");
            }

            //Update GrHeader using stored Procedure
            statusDescription = stagingLineV2Repository.getStatusDescription(17L, createdGRLines.get(0).getLanguageId());
            grHeaderV2Repository.updateGrheaderStatusUpdateProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, 17L, statusDescription, new Date());
            log.info("GrHeader Status 17 Updating Using Stored Procedure when condition met");
            return createdGRLines;
        } catch (Exception e) {
            //Exception Log
//            createGrLineLog10(newGrLines, e.toString());

            e.printStackTrace();
            throw e;
        }
    }


    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmV2(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        try {
            for (PutAwayLineV2 newPutAwayLine : newPutAwayLines) {

                if (newPutAwayLine.getPutawayConfirmedQty() <= 0) {
                    throw new BadRequestException("Putaway Confirm Qty cannot be zero or negative");
                }
                PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();

                itemCode = newPutAwayLine.getItemCode();
                companyCode = newPutAwayLine.getCompanyCode();
                plantId = newPutAwayLine.getPlantId();
                languageId = newPutAwayLine.getLanguageId();
                warehouseId = newPutAwayLine.getWarehouseId();
                refDocNumber = newPutAwayLine.getRefDocNumber();
                preInboundNo = newPutAwayLine.getPreInboundNo();

                Double cbmPerQuantity = 0D;
                Double cbm = 0D;
                Double allocatedVolume = 0D;
                Double occupiedVolume = 0D;
                Double remainingVolume = 0D;
                Double totalVolume = 0D;

                Double allocateQty = 0D;
                Double orderedQty = 0D;
                Double differenceQty = 0D;
                Double assignedProposedBinVolume = 0D;

                Long statusId = 0L;

                boolean capacityCheck = false;
                boolean storageBinCapacityCheck = false;

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(companyCode);
                imBasicData.setPlantId(plantId);
                imBasicData.setLanguageId(languageId);
                imBasicData.setWarehouseId(warehouseId);
                imBasicData.setItemCode(itemCode);
                imBasicData.setManufacturerName(newPutAwayLine.getManufacturerName());
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + itemCodeCapacityCheck);

                if (itemCodeCapacityCheck != null) {
                    if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                        capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                        log.info("capacity Check: " + capacityCheck);
                    }
                }

                String confirmedStorageBin = newPutAwayLine.getConfirmedStorageBin();
                String proposedStorageBin = newPutAwayLine.getProposedStorageBin();
                log.info("proposedBin, confirmedBin: " + newPutAwayLine.getProposedStorageBin() + ", " + newPutAwayLine.getConfirmedStorageBin());

                StorageBinV2 storageBin = storageBinRepository.getStorageBin(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                StorageBinV2 proposedBin = storageBinRepository.getStorageBin(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getProposedStorageBin());

                PutAwayHeaderV2 findPutawayHeader = getPutawayHeaderV2(companyCode, plantId, warehouseId, languageId, newPutAwayLine.getPutAwayNumber());
                List<PutAwayLineV2> findPutawayLine = getPutAwayLineV2ForPutawayConfirm(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPutAwayNumber());

                if (storageBin != null) {
                    dbPutAwayLine.setLevelId(String.valueOf(storageBin.getFloorId()));
                    if (storageBin.isCapacityCheck()) {
                        storageBinCapacityCheck = storageBin.isCapacityCheck();
                        log.info("confirmed storageBinCapacityCheck: " + storageBinCapacityCheck);
                    }
                }

                if (capacityCheck && !storageBinCapacityCheck) {
                    throw new BadRequestException("Selected Bin is not under Capacity Check. Kindly Select a Capacity Enabled Bin!");
                }
                if (!capacityCheck && storageBinCapacityCheck) {
                    throw new BadRequestException("Selected ItemCode is not under Capacity Check. Kindly Select a Capacity Enabled Item!");
                }

                if (capacityCheck && storageBinCapacityCheck) {

                    if (!confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
                        log.info("confirmedStorageBin != proposedBin: " + confirmedStorageBin + ", " + proposedStorageBin);

                        if (newPutAwayLine.getCbmQuantity() != null) {
                            cbmPerQuantity = newPutAwayLine.getCbmQuantity();
                        }
                        if (newPutAwayLine.getCbm() != null && newPutAwayLine.getCbm() != "") {
                            cbm = Double.valueOf(newPutAwayLine.getCbm());
                        }
                        if (storageBin.getTotalVolume() != null && storageBin.getTotalVolume() != "") {
                            totalVolume = Double.valueOf(storageBin.getTotalVolume());
                        }
                        if (storageBin.getAllocatedVolume() != null) {
                            allocatedVolume = Double.valueOf(storageBin.getAllocatedVolume());
                        }
                        if (storageBin.getOccupiedVolume() != null && storageBin.getOccupiedVolume() != "") {
                            occupiedVolume = Double.valueOf(storageBin.getOccupiedVolume());
                        }
                        if (storageBin.getRemainingVolume() != null && storageBin.getRemainingVolume() != "") {
                            remainingVolume = Double.valueOf(storageBin.getRemainingVolume());
                        }

                        if (remainingVolume <= 0) {
                            throw new BadRequestException("Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }

                        allocateQty = newPutAwayLine.getPutawayConfirmedQty();

                        if (remainingVolume < cbmPerQuantity) {
                            throw new BadRequestException("Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }

                        allocatedVolume = allocateQty * cbmPerQuantity;
                        if (allocatedVolume <= remainingVolume) {
                            allocatedVolume = allocateQty * cbmPerQuantity;
                        } else {
                            throw new BadRequestException("Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }
                        if (totalVolume >= remainingVolume) {
                            remainingVolume = totalVolume - (allocatedVolume + occupiedVolume);
                        } else {
                            remainingVolume = remainingVolume - allocatedVolume;
                        }
                        occupiedVolume = occupiedVolume + allocatedVolume;

                        log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                        if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0) && remainingVolume.equals(totalVolume)) {
                            log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                            statusId = 0L;
                            log.info("StorageBin Emptied");
                        } else {
                            log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                            statusId = 1L;
                            log.info("StorageBin Occupied");
                        }

                        //confirmed Bin volume update
                        updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, newPutAwayLine.getConfirmedStorageBin(),
                                companyCode, plantId, languageId, warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());

                        if (findPutawayLine == null) {
                            //proposed Bin revert volume update done during putaway header create
                            remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
//                            allocatedVolume = proposedBin.getAllocatedVolume();
                            occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                            totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                            log.info("proposed Bin before confirm remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            remainingVolume = remainingVolume + allocatedVolume;
                            occupiedVolume = occupiedVolume - allocatedVolume;

                            log.info("proposed bin after confirm remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0) && remainingVolume.equals(totalVolume)) {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                                statusId = 0L;
                                log.info("StorageBin Emptied");
                            } else {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                                statusId = 1L;
                                log.info("StorageBin Occupied");
                            }

                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, newPutAwayLine.getProposedStorageBin(),
                                    companyCode, plantId, languageId, warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());
                        }

                        log.info("Storage Bin occupied volume got updated");

                    }
                    if (confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
                        log.info("confirmedStorageBin == proposedBin" + confirmedStorageBin + ", " + proposedStorageBin);

                        if (findPutawayHeader.getPutAwayQuantity() > newPutAwayLine.getPutawayConfirmedQty()) {
                            log.info("putAwayQty > confirmQty" + findPutawayHeader.getPutAwayQuantity() + ", " + newPutAwayLine.getPutawayConfirmedQty());

                            if (newPutAwayLine.getCbmQuantity() != null) {
                                cbmPerQuantity = newPutAwayLine.getCbmQuantity();
                            }
                            if (newPutAwayLine.getCbm() != null && newPutAwayLine.getCbm() != "") {
                                cbm = Double.valueOf(newPutAwayLine.getCbm());
                            }
                            if (proposedBin.getTotalVolume() != null && proposedBin.getTotalVolume() != "") {
                                totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                            }
                            if (proposedBin.getAllocatedVolume() != null) {
                                allocatedVolume = Double.valueOf(proposedBin.getAllocatedVolume());
                            }
                            if (proposedBin.getOccupiedVolume() != null && proposedBin.getOccupiedVolume() != "") {
                                occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                            }
                            if (proposedBin.getRemainingVolume() != null && proposedBin.getRemainingVolume() != "") {
                                remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
                            }

                            allocateQty = newPutAwayLine.getPutawayConfirmedQty();
                            if (newPutAwayLine.getOrderQty() != null) {
                                orderedQty = newPutAwayLine.getOrderQty();
                            }
                            log.info("allocateQty(confirmed PutawayQty), putawayQty, orderQty: " + allocateQty + ", " + findPutawayHeader.getPutAwayQuantity() + ", " + orderedQty);

                            assignedProposedBinVolume = findPutawayHeader.getPutAwayQuantity() * cbmPerQuantity;
                            allocatedVolume = allocateQty * cbmPerQuantity;

                            log.info("assignedProposedBinVolume, allocatedVolume: " + assignedProposedBinVolume + ", " + allocatedVolume);

                            remainingVolume = remainingVolume + assignedProposedBinVolume - allocatedVolume;
                            occupiedVolume = occupiedVolume - assignedProposedBinVolume + allocatedVolume;

                            log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0) && remainingVolume.equals(totalVolume)) {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                                statusId = 0L;
                                log.info("StorageBin Emptied");
                            } else {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                                statusId = 1L;
                                log.info("StorageBin Occupied");
                            }

                            //confirmed Bin volume update
                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, newPutAwayLine.getConfirmedStorageBin(),
                                    companyCode, plantId, languageId, warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());

                            log.info("Storage Bin occupied volume got updated");

                        }
                    }
                }
                //V2 Code
                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                        languageId,
                        plantId,
                        warehouseId);

                newPutAwayLine.setCompanyDescription(description.getCompanyDesc());
                newPutAwayLine.setPlantDescription(description.getPlantDesc());
                newPutAwayLine.setWarehouseDescription(description.getWarehouseDesc());

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                        newPutAwayLine.getPreInboundNo(), newPutAwayLine.getRefDocNumber(), newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getManufacturerName());
                log.info("StagingLine: " + dbStagingLineEntity);
                if (dbStagingLineEntity != null) {
                    if (newPutAwayLine.getManufacturerFullName() != null) {
                        newPutAwayLine.setManufacturerFullName(newPutAwayLine.getManufacturerFullName());
                    } else {
                        newPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                    }
                    if (newPutAwayLine.getMiddlewareId() != null) {
                        newPutAwayLine.setMiddlewareId(newPutAwayLine.getMiddlewareId());
                    } else {
                        newPutAwayLine.setMiddlewareId(dbStagingLineEntity.getMiddlewareId());
                    }
                    if (newPutAwayLine.getMiddlewareHeaderId() != null) {
                        newPutAwayLine.setMiddlewareHeaderId(newPutAwayLine.getMiddlewareHeaderId());
                    } else {
                        newPutAwayLine.setMiddlewareHeaderId(dbStagingLineEntity.getMiddlewareHeaderId());
                    }
                    if (newPutAwayLine.getMiddlewareTable() != null) {
                        newPutAwayLine.setMiddlewareTable(newPutAwayLine.getMiddlewareTable());
                    } else {
                        newPutAwayLine.setMiddlewareTable(dbStagingLineEntity.getMiddlewareTable());
                    }
                    if (newPutAwayLine.getPurchaseOrderNumber() != null) {
                        newPutAwayLine.setPurchaseOrderNumber(newPutAwayLine.getPurchaseOrderNumber());
                    } else {
                        newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                    }
                    newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                    newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                    newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                }

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));
                dbPutAwayLine.setCompanyCode(newPutAwayLine.getCompanyCode());
                dbPutAwayLine.setBranchCode(newPutAwayLine.getBranchCode());
                dbPutAwayLine.setTransferOrderNo(newPutAwayLine.getTransferOrderNo());
                dbPutAwayLine.setIsCompleted(newPutAwayLine.getIsCompleted());
                dbPutAwayLine.setPutawayConfirmedQty(newPutAwayLine.getPutawayConfirmedQty());
                dbPutAwayLine.setConfirmedStorageBin(newPutAwayLine.getConfirmedStorageBin());
                dbPutAwayLine.setStatusId(20L);
                String statusDescription = stagingLineV2Repository.getStatusDescription(20L, newPutAwayLine.getLanguageId());
                dbPutAwayLine.setStatusDescription(statusDescription);
                dbPutAwayLine.setPackBarcodes(newPutAwayLine.getPackBarcodes());
                dbPutAwayLine.setBarcodeId(newPutAwayLine.getBarcodeId());
                dbPutAwayLine.setDeletionIndicator(0L);
                dbPutAwayLine.setCreatedBy(loginUserID);
                dbPutAwayLine.setUpdatedBy(loginUserID);
                dbPutAwayLine.setConfirmedBy(loginUserID);

                log.info("putawayHeader: " + findPutawayHeader);
                if (findPutawayHeader != null) {
                    dbPutAwayLine.setCreatedOn(findPutawayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(findPutawayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(findPutawayHeader.getInboundOrderTypeId());
                } else {
                    dbPutAwayLine.setCreatedOn(new Date());
                }
                dbPutAwayLine.setUpdatedOn(new Date());
                dbPutAwayLine.setConfirmedOn(new Date());

                Optional<PutAwayLineV2> existingPutAwayLine = putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                        newPutAwayLine.getLanguageId(), newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(),
                        newPutAwayLine.getWarehouseId(), newPutAwayLine.getGoodsReceiptNo(),
                        newPutAwayLine.getPreInboundNo(), newPutAwayLine.getRefDocNumber(),
                        newPutAwayLine.getPutAwayNumber(), newPutAwayLine.getLineNo(),
                        newPutAwayLine.getItemCode(), newPutAwayLine.getProposedStorageBin(),
                        Arrays.asList(newPutAwayLine.getConfirmedStorageBin()),
                        newPutAwayLine.getDeletionIndicator());

                log.info("Existing putawayline already created : " + existingPutAwayLine);

                if (existingPutAwayLine.isEmpty()) {

                    try {
                        String leadTime = putAwayLineV2Repository.getleadtime(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getPutAwayNumber(), new Date());
                        dbPutAwayLine.setReferenceField1(leadTime);
                        log.info("LeadTime: " + leadTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);

                    log.info("---------->createdPutAwayLine created: " + createdPutAwayLine);

                    createdPutAwayLines.add(createdPutAwayLine);

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {
                        // Insert a record into INVENTORY table as below
                        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                        storageBinPutAway.setCompanyCodeId(dbPutAwayLine.getCompanyCode());
                        storageBinPutAway.setPlantId(dbPutAwayLine.getPlantId());
                        storageBinPutAway.setLanguageId(dbPutAwayLine.getLanguageId());
                        storageBinPutAway.setWarehouseId(dbPutAwayLine.getWarehouseId());
                        storageBinPutAway.setBin(dbPutAwayLine.getConfirmedStorageBin());

                        StorageBinV2 dbStorageBin = null;
                        try {
                            dbStorageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        } catch (Exception e) {
                            throw new BadRequestException("Invalid StorageBin");
                        }
                        // Updating StorageBin StatusId as '1'
                        dbStorageBin.setStatusId(1L);
                        mastersService.updateStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin,
                                dbPutAwayLine.getCompanyCode(), dbPutAwayLine.getPlantId(), dbPutAwayLine.getLanguageId(), dbPutAwayLine.getWarehouseId(),
                                loginUserID, authTokenForMastersService.getAccess_token());

                        PutAwayHeaderV2 putAwayHeader = getPutAwayHeaderV2ForPutAwayLine(createdPutAwayLine.getWarehouseId(),
                                createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getRefDocNumber(),
                                createdPutAwayLine.getPutAwayNumber(),
                                createdPutAwayLine.getCompanyCode(),
                                createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId());

                        confirmedStorageBin = createdPutAwayLine.getConfirmedStorageBin();
                        proposedStorageBin = putAwayHeader.getProposedStorageBin();
                        if (putAwayHeader != null) {
                            log.info("putawayConfirmQty, putawayQty: " + createdPutAwayLine.getPutawayConfirmedQty() + ", " + putAwayHeader.getPutAwayQuantity());

                            putAwayHeader.setStatusId(20L);
                            log.info("PutawayHeader StatusId : 20");
                            statusDescription = stagingLineV2Repository.getStatusDescription(putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                            putAwayHeader.setStatusDescription(statusDescription);
                            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                            log.info("putAwayHeader updated: " + putAwayHeader);

                            if (createdPutAwayLine.getPutawayConfirmedQty() < putAwayHeader.getPutAwayQuantity()) {
                                Double dbAssignedPutawayQty = 0D;
                                if (putAwayHeader.getReferenceField2() != null) {
                                    dbAssignedPutawayQty = Double.valueOf(putAwayHeader.getReferenceField2());
                                }
                                if (putAwayHeader.getReferenceField2() == null) {
                                    dbAssignedPutawayQty = putAwayHeader.getPutAwayQuantity();
                                }
                                Double dbPutawayQty = putAwayLineV2Repository.getPutawayCnfQuantity(createdPutAwayLine.getCompanyCode(),
                                        createdPutAwayLine.getPlantId(),
                                        createdPutAwayLine.getLanguageId(),
                                        createdPutAwayLine.getWarehouseId(),
                                        createdPutAwayLine.getRefDocNumber(),
                                        createdPutAwayLine.getPreInboundNo(),
                                        createdPutAwayLine.getItemCode(),
                                        createdPutAwayLine.getManufacturerName(),
                                        createdPutAwayLine.getLineNo());
                                if (dbPutawayQty == null) {
                                    dbPutawayQty = 0D;
                                }

                                log.info("tot_pa_cnf_qty,created_pa_line_cnf_qty,partial_pa_header_pa_qty,pa_header_pa_qty,RF2 : "
                                        + dbPutawayQty + ", " + createdPutAwayLine.getPutawayConfirmedQty()
                                        + ", " + putAwayHeader.getPutAwayQuantity() + ", " + putAwayHeader.getReferenceField2());
                                if (dbPutawayQty > dbAssignedPutawayQty) {
                                    throw new BadRequestException("sum of confirm Putaway line qty is greater than assigned putaway header qty");
                                }
                                if (dbPutawayQty <= dbAssignedPutawayQty) {
                                    if ((putAwayHeader.getWarehouseId().equalsIgnoreCase("200") || putAwayHeader.getWarehouseId().equalsIgnoreCase("100")) && proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
//                                        if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                        log.info("New PutawayHeader Creation: ");
                                        PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                        BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader, CommonUtils.getNullPropertyNames(putAwayHeader));

                                        // PA_NO
                                        long NUM_RAN_CODE = 7;
                                        String nextPANumber = mastersService.getNextNumberRange(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                                        newPutAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number

                                        newPutAwayHeader.setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            newPutAwayHeader.setReferenceField2(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            newPutAwayHeader.setReferenceField4("1");
                                        }
                                        Double putawaycnfQty = 0D;
                                        if (newPutAwayHeader.getReferenceField3() != null) {
                                            putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                        }
                                        putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                        newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));
                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        newPutAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");
                                        statusDescription = stagingLineV2Repository.getStatusDescription(newPutAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        newPutAwayHeader.setStatusDescription(statusDescription);
                                        newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                        log.info("1.putAwayHeader created: " + newPutAwayHeader);
                                    }
                                    if ((putAwayHeader.getWarehouseId().equalsIgnoreCase("200") || putAwayHeader.getWarehouseId().equalsIgnoreCase("100")) && !proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                        PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                        BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader, CommonUtils.getNullPropertyNames(putAwayHeader));

                                        // PA_NO
                                        long NUM_RAN_CODE = 7;
                                        String nextPANumber = mastersService.getNextNumberRange(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                                        newPutAwayHeader.setPutAwayNumber(nextPANumber);
                                        newPutAwayHeader.setProposedStorageBin(confirmedStorageBin);

                                        newPutAwayHeader.setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            newPutAwayHeader.setReferenceField2(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            newPutAwayHeader.setReferenceField4("1");
                                        }

                                        Double putawaycnfQty = 0D;
                                        if (newPutAwayHeader.getReferenceField3() != null) {
                                            putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                        }
                                        putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                        newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));

                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        newPutAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");
                                        statusDescription = stagingLineV2Repository.getStatusDescription(putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        newPutAwayHeader.setStatusDescription(statusDescription);

                                        newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                        log.info("2.putAwayHeader created: " + newPutAwayHeader);
                                    }
                                }
//                                }
                            }
                        }

                        /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                        // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE table and
                        // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
                        double addedAcceptQty = 0.0;
                        double addedDamageQty = 0.0;

                        InboundLineV2 inboundLine = getInboundLineV2(createdPutAwayLine.getCompanyCode(),
                                createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId(),
                                createdPutAwayLine.getWarehouseId(),
                                createdPutAwayLine.getRefDocNumber(),
                                createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getLineNo(),
                                createdPutAwayLine.getItemCode());
                        log.info("inboundLine----from--DB---------> " + inboundLine);

                        // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                            if (inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() < inboundLine.getOrderQty()) {
                                addedAcceptQty = inboundLine.getAcceptedQty() + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedAcceptQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Accept qty cannot be greater than order qty");
                            }
                            inboundLine.setAcceptedQty(addedAcceptQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedAcceptQty);
                        }

                        // if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                            if (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() < inboundLine.getOrderQty()) {
                                addedDamageQty = inboundLine.getDamageQty() + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedDamageQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedDamageQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Damage qty cannot be greater than order qty");
                            }
                            inboundLine.setDamageQty(addedDamageQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedDamageQty);
                        }

                        if (inboundLine.getInboundOrderTypeId() == 5L) {          //condition added for final Inbound confirm
                            inboundLine.setReferenceField2("true");
                        }

                        inboundLine.setStatusId(20L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(20L, createdPutAwayLine.getLanguageId());
                        inboundLine.setStatusDescription(statusDescription);
                        inboundLine = inboundLineV2Repository.save(inboundLine);
                        log.info("inboundLine updated : " + inboundLine);
//                        }
                    }
                } else {
                    log.info("Putaway Line already exist : " + existingPutAwayLine);
                }
            }
            putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            log.info("InboundHeader received lines count updated: " + refDocNumber);
            return createdPutAwayLines;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param loginUserID
     * @return
     */
    public AXApiResponse updateInboundHeaderPartialConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                             String preInboundNo, String refDocNumber, String loginUserID) {
        try {
            log.info("DSR--->Partial Confirmation Process Initiated Order Number -----> " + refDocNumber);
            // PutawayHeader Validation
            long putAwayHeaderStatusIdCount = getPutawayHeaderByStatusIdV2(companyCode, plantId, warehouseId, preInboundNo, refDocNumber);
            log.info("PutAwayHeader status----> : " + putAwayHeaderStatusIdCount);

            if (putAwayHeaderStatusIdCount != 0) {
                throw new BadRequestException("Error on Inbound Confirmation: PutAwayHeader are NOT processed completely ---> OrderNumber: " + refDocNumber);
            }

            AXApiResponse axapiResponse = new AXApiResponse();

            List<InboundLineV2> inboundLineList = getInboundLineForInboundConfirmPartialAllocationV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);

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
                        List<PutAwayLineV2> putAwayLineList =
                                getPutAwayLineForInboundConfirmV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                                        grLine.getItemCode(),
                                        grLine.getManufacturerName(),
                                        grLine.getLineNo(),
                                        grLine.getPreInboundNo(),
                                        grLine.getPackBarcodes());
                        log.info("PutawayLine List: " + putAwayLineList.size());
                        if (putAwayLineList != null) {
                            for (PutAwayLineV2 putAwayLine : putAwayLineList) {
                                boolean createdInventory = createInventoryNonCBMV2(putAwayLine);
                            }
                            log.info("Inventory Created Successfully -----> for All Putaway Lines");
                        }
                    }
                }
            }
            log.info("inventoryError: " + inventoryError + "|" + refDocNumber);
            if (!inventoryError) {
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
                    //Multiple Stored Procedure replaced with Single Procedure Call
                    inboundHeaderV2Repository.updateHeaderStatusInboundConfirmProcedure(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 24L, statusDescription, loginUserID, new Date());
                    log.info("Header Status updated using stored procedure");
                }
            }
            axapiResponse.setStatusCode("200");                         //HardCoded
            axapiResponse.setMessage("Success");                        //HardCoded
            log.info("axapiResponse: " + axapiResponse);
            return axapiResponse;
        } catch (Exception e) {
            throw new BadRequestException("Inbound confirmation [DSR]: Exception ----> " + e.toString());
        }
    }

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getPutawayHeaderByStatusIdV2(String companyId, String plantId, String warehouseId,
                                             String preInboundNo, String refDocNumber) {
        long putAwayHeaderStatusIdCount = putAwayHeaderV2Repository.getPutawayHeaderCountByStatusId(companyId, plantId, warehouseId, preInboundNo, refDocNumber);
        return putAwayHeaderStatusIdCount;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public StagingLineEntityV2 getStagingLineForPutAwayLineV2(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String preInboundNo,
                                                              String refDocNumber, Long lineNo, String itemCode, String manufacturerName) {
        Optional<StagingLineEntityV2> stagingLineV2 = stagingLineV2Repository
                .findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        manufacturerName,
                        0L);
        if (stagingLineV2 == null || stagingLineV2.isEmpty()) {
            return null;
        }
        log.info("dbStagingLine: " + stagingLineV2.get());
        return stagingLineV2.get();
    }

    /**
     * @param companyId
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param putAwayNumber
     * @return
     */
    public PutAwayHeaderV2 getPutawayHeaderV2(String companyId, String plantId, String warehouseId, String languageId, String putAwayNumber) {
        PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPutAwayNumberAndDeletionIndicator(
                companyId, plantId, warehouseId, languageId, putAwayNumber, 0L);
        return dbPutAwayHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param putAwayNumber
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineV2ForPutawayConfirm(String companyCode, String plantId, String languageId,
                                                                 String warehouseId, String refDocNumber, String putAwayNumber) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        putAwayNumber,
                        0L);
        if (putAwayLine.isEmpty()) {
            return null;
        }

        return putAwayLine;
    }

    /**
     * @param remainingVolume
     * @param occupiedVolume
     * @param allocatedVolume
     * @param storageBin
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param statusId
     * @param loginUserId
     * @param authToken
     */
    public void updateStorageBin(Double remainingVolume, Double occupiedVolume, Double allocatedVolume,
                                 String storageBin, String companyCode, String plantId, String languageId,
                                 String warehouseId, Long statusId, String loginUserId, String authToken) {

        StorageBinV2 modifiedStorageBin = new StorageBinV2();
        modifiedStorageBin.setRemainingVolume(String.valueOf(remainingVolume));
        modifiedStorageBin.setAllocatedVolume(allocatedVolume);
        modifiedStorageBin.setOccupiedVolume(String.valueOf(occupiedVolume));
        modifiedStorageBin.setCapacityCheck(true);
        modifiedStorageBin.setStatusId(statusId);

        StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(storageBin,
                modifiedStorageBin,
                companyCode,
                plantId,
                languageId,
                warehouseId,
                loginUserId,
                authToken);

        if (updateStorageBinV2 != null) {
            log.info("Storage Bin Volume Updated successfully ");
        }
    }


    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    public PutAwayHeaderV2 getPutAwayHeaderV2ForPutAwayLine(String warehouseId, String preInboundNo, String refDocNumber, String putAwayNumber,
                                                            String companyCodeId, String plantId, String languageId) {
        PutAwayHeaderV2 putAwayHeader =
                putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        companyCodeId,
                        plantId,
                        languageId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        putAwayNumber,
                        0L
                );
        if (putAwayHeader == null) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",putAwayNumber: " + putAwayNumber +
                    " doesn't exist.");
        }
        return putAwayHeader;
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public InboundLineV2 getInboundLineV2(String companyCode, String plantId,
                                          String languageId, String warehouseId, String refDocNumber,
                                          String preInboundNo, Long lineNo, String itemCode) {
        Optional<InboundLineV2> inboundLine =
                inboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        0L);
        log.info("inboundLine : " + inboundLine);
        if (inboundLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",temCode: " + itemCode + " doesn't exist.");
        }

        return inboundLine.get();
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @return
     */
    public List<InboundLineV2> getInboundLineForInboundConfirmPartialAllocationV2(String companyCode, String plantId, String languageId,
                                                                                  String warehouseId, String refDocNumber, String preInboundNo) {
        List<InboundLineV2> inboundLines = inboundLineV2Repository.getInboundLinesV2ForInboundConfirm(
                companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 20L);
        log.info("inboundLine : " + inboundLines.size());
        return inboundLines;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param preInboundNo
     * @return
     */
    public List<GrLineV2> getGrLineForInboundConformV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                       String refDocNumber, String itemCode, String manufacturerName, Long lineNumber, String preInboundNo) {
        List<GrLineV2> grLine =
                grLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndWarehouseIdAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        refDocNumber,
                        warehouseId,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        0L);
        if (grLine == null) {
            return null;
        }
        return grLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param preInboundNo
     * @param packBarcodes
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                 String refDocNumber, String itemCode, String manufacturerName,
                                                                 Long lineNumber, String preInboundNo, String packBarcodes) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        20L,
                        packBarcodes,
                        0L);
        if (putAwayLine == null) {
            return null;
        }
        return putAwayLine;
    }

    /**
     * @param createdGRLine
     * @return
     */
    @Transactional
    private InventoryV2 createInventoryNonCBMV2(GrLineV2 createdGRLine) {

        try {
            InventoryV2 dbInventory = inventoryV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPackBarcodesAndBinClassIdAndDeletionIndicatorOrderByInventoryIdDesc(
                    createdGRLine.getCompanyCode(),
                    createdGRLine.getPlantId(),
                    createdGRLine.getLanguageId(),
                    createdGRLine.getWarehouseId(),
                    createdGRLine.getItemCode(),
                    createdGRLine.getManufacturerName(),
                    "99999", 3L, 0L);

            InventoryV2 createdinventory = null;

            if (dbInventory != null) {
                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
                inventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty());
                log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                Double totalQty = 0D;
                if (inventory.getReferenceField4() != null) {
                    totalQty = inventory.getReferenceField4() + createdGRLine.getGoodReceiptQty();
                }
                if (inventory.getReferenceField4() == null) {
                    totalQty = createdGRLine.getGoodReceiptQty();
                }
                inventory.setReferenceField4(totalQty);
                log.info("Total Inventory Qty : " + totalQty);
                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());
                inventory.setCreatedOn(dbInventory.getCreatedOn());
                inventory.setUpdatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory[Existing] : " + createdinventory);
                } catch (Exception e) {
                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                    e.printStackTrace();
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }

            if (dbInventory == null) {

                InventoryV2 inventory = new InventoryV2();
                BeanUtils.copyProperties(createdGRLine, inventory, CommonUtils.getNullPropertyNames(createdGRLine));

                inventory.setCompanyCodeId(createdGRLine.getCompanyCode());

                // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
                inventory.setVariantCode(1L);
                inventory.setVariantSubCode("1");
                inventory.setStorageMethod("1");
                inventory.setBatchSerialNumber("1");
                inventory.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
                inventory.setBinClassId(3L);
                inventory.setDeletionIndicator(0L);
                inventory.setManufacturerCode(createdGRLine.getManufacturerName());
                inventory.setManufacturerName(createdGRLine.getManufacturerName());

                if (createdGRLine.getBarcodeId() != null) {
                    inventory.setBarcodeId(createdGRLine.getBarcodeId());
                }

                // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                StorageBin storageBin = mastersService.getStorageBin(
                        createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(),
                        createdGRLine.getLanguageId(),
                        createdGRLine.getWarehouseId(), 3L, authTokenForMastersService.getAccess_token());
                log.info("storageBin: " + storageBin);
                inventory.setStorageBin(storageBin.getStorageBin());

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(createdGRLine.getCompanyCode());
                imBasicData.setPlantId(createdGRLine.getPlantId());
                imBasicData.setLanguageId(createdGRLine.getLanguageId());
                imBasicData.setWarehouseId(createdGRLine.getWarehouseId());
                imBasicData.setItemCode(createdGRLine.getItemCode());
                imBasicData.setManufacturerName(createdGRLine.getManufacturerName());

//                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
//                log.info("ImbasicData1 : " + itemCodeCapacityCheck);

                ImBasicData1 itemCodeCapacityCheck = imBasicData1Repository.getImBasicData1CapacityCheck(createdGRLine.getItemCode(), createdGRLine.getCompanyCode(),
                        createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), createdGRLine.getManufacturerName());

                if (itemCodeCapacityCheck != null) {
                    inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
                    inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
                    inventory.setDescription(itemCodeCapacityCheck.getDescription());
                }
                if (storageBin != null) {
                    inventory.setReferenceField10(storageBin.getStorageSectionId());
                    inventory.setReferenceField5(storageBin.getAisleNumber());
                    inventory.setReferenceField6(storageBin.getShelfId());
                    inventory.setReferenceField7(storageBin.getRowId());
                    inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                }

                // STCK_TYP_ID
                inventory.setStockTypeId(1L);
                String stockTypeDesc = getStockTypeDesc(createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getLanguageId(), createdGRLine.getWarehouseId(), 1L);
                inventory.setStockTypeDescription(stockTypeDesc);

                // SP_ST_IND_ID
                inventory.setSpecialStockIndicatorId(1L);

                // INV_QTY
                if (dbInventory != null) {
                    inventory.setInventoryQuantity(dbInventory.getInventoryQuantity() + createdGRLine.getGoodReceiptQty());
                    log.info("Inventory Qty = inv_qty + gr_qty: " + dbInventory.getInventoryQuantity() + ", " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(inventory.getInventoryQuantity());
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                if (dbInventory == null) {
                    inventory.setInventoryQuantity(createdGRLine.getGoodReceiptQty());
                    log.info("Inventory Qty = gr_qty: " + createdGRLine.getGoodReceiptQty());
                    inventory.setReferenceField4(inventory.getInventoryQuantity());
                    log.info("Inventory Total Qty: " + inventory.getInventoryQuantity());   //Allocated Qty is always 0 for BinClassId 3
                }
                //packbarcode
                /*
                 * Hardcoding Packbarcode as 99999
                 */
//            inventory.setPackBarcodes(createdGRLine.getPackBarcodes());
                inventory.setPackBarcodes("99999");
                inventory.setReferenceField1(createdGRLine.getPackBarcodes());

                // INV_UOM
                inventory.setInventoryUom(createdGRLine.getOrderUom());
                inventory.setCreatedBy(createdGRLine.getCreatedBy());

                //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
                inventory.setReferenceDocumentNo(createdGRLine.getRefDocNumber());
                inventory.setReferenceOrderNo(createdGRLine.getRefDocNumber());

                inventory.setCreatedOn(new Date());
                inventory.setUpdatedOn(new Date());
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    log.info("created inventory : " + createdinventory);
                } catch (Exception e) {
                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                    e.printStackTrace();
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }

            return createdinventory;
        } catch (Exception e) {
            // Exception Log
//            createGrLineLog7(createdGRLine, e.toString());

            e.printStackTrace();
            throw e;
        }
    }


    /**
     *
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
//    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
    public boolean createInventoryNonCBMV2(PutAwayLineV2 putAwayLine) throws Exception {
        inventoryError = false;
        alreadyExecuted = false;
        log.info("Create Inventory Initiated ---> alreadyExecuted ---> " + new Date() + ", " + alreadyExecuted);
        String palletCode = null;
        String caseCode = null;
        try {
            InventoryV2 existinginventory = getInventoryForStockAdjustmentDamageV2(
                    putAwayLine.getCompanyCode(),
                    putAwayLine.getPlantId(),
                    putAwayLine.getLanguageId(),
                    putAwayLine.getWarehouseId(),
                    putAwayLine.getItemCode(),
                    "99999", 3L,
                    putAwayLine.getManufacturerName());

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
                    inventory2.setInventoryQuantity(INV_QTY);
                    inventory2.setReferenceField4(INV_QTY);         //Allocated Qty is always 0 for BinClassId 3
                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);

                    palletCode = existinginventory.getPalletCode();
                    caseCode = existinginventory.getCaseCode();

                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
                    inventory2.setUpdatedOn(new Date());
                    if (!alreadyExecuted) {
                        try {
                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
                            log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
                        } catch (Exception e) {
                            log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                            e.printStackTrace();
                            InventoryTrans newInventoryTrans = new InventoryTrans();
                            BeanUtils.copyProperties(inventory2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory2));
                            newInventoryTrans.setReRun(0L);
                            InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                            log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Existing Inventory---Error-----> : " + e.toString());
        }

        try {
            log.info("Create Inventory bin Class Id 1 Initiated---->alreadyExecuted---> " + new Date() + ", " + alreadyExecuted);
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

            InventoryV2 existingInventory = getInventoryForInhouseTransferV2(
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
                    inventory.setAllocatedQuantity(ALLOC_QTY);
                }
                if (existingInventory.getAllocatedQuantity() == null) {
                    inventory.setAllocatedQuantity(ALLOC_QTY);
                }
                log.info("Inventory Allocated Qty: " + ALLOC_QTY);
            }
            // INV_QTY
            if (existingInventory != null) {
                inventory.setInventoryQuantity(existingInventory.getInventoryQuantity() + putAwayLine.getPutawayConfirmedQty());
                log.info("Inventory Qty = inv_qty + pa_cnf_qty: " + existingInventory.getInventoryQuantity() + ", " + putAwayLine.getPutawayConfirmedQty());
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
            if (!alreadyExecuted) {
                try {
                    createdinventory = inventoryV2Repository.save(inventory);
                    alreadyExecuted = true;             //to ensure method executing only once
                    log.info("created inventory : executed" + createdinventory + " -----> " + alreadyExecuted);
                } catch (Exception e) {
                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                    e.printStackTrace();
                    InventoryTrans newInventoryTrans = new InventoryTrans();
                    BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
                    newInventoryTrans.setReRun(0L);
                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            inventoryError = true;
            inventoryErrorCount++;
            log.error("Error While Creating Inventory --> errorCount ---> " + inventoryErrorCount);
            if (inventoryErrorCount == 3) {
                sendMail(putAwayLine, e.getLocalizedMessage());
            }
            throw e;
        }
    }

//    /**
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param storageBin
//     * @return
//     */
//    public StorageBin getStorageBinV2(String companyCode, String plantId, String languageId, String warehouseId, String storageBin) {
//        return storageBinRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStorageBinAndDeletionIndicator(
//                companyCode, plantId, languageId, warehouseId, storageBin, 0L);
//    }
//
//    /**
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param preInboundNo
//     * @param refDocNumber
//     * @return
//     */
//    public PreInboundHeaderV2 getPreInboundHeaderV2(String companyCode, String plantId, String languageId, String warehouseId, String preInboundNo, String refDocNumber) {
//        Optional<PreInboundHeaderEntityV2> preInboundHeaderEntity =
//                preInboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
//                        companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, 0L);
//
//        if (preInboundHeaderEntity.isEmpty()) {
//            // Exception log
////            createPreInboundHeaderLog1(languageId, companyCode, plantId, warehouseId, refDocNumber, preInboundNo,
////                    "PreInboundHeaderV2 with given values doesn't exists - " + refDocNumber);
//            throw new BadRequestException("The given PreInboundHeader ID : preInboundNo : " + preInboundNo +
//                    ", warehouseId: " + warehouseId + " doesn't exist.");
//        }
////        PreInboundHeaderV2 preInboundHeader = getPreInboundLineItemsV2(preInboundHeaderEntity.get());
//        PreInboundHeaderV2 preInboundHeader = new PreInboundHeaderV2();
//        BeanUtils.copyProperties(preInboundHeaderEntity.get(), preInboundHeader, CommonUtils.getNullPropertyNames(preInboundHeaderEntity.get()));
//        return preInboundHeader;
//    }


    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getInboundOrderTypeDesc(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 1) {
            referenceDocumentType = "SupplierInvoice";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "SalesReturn"; //sale return -7(Bin Class Id)
        }
        if (referenceDocumentTypeId == 3) {
            referenceDocumentType = "Non-WMS to WMS"; //b2b
        }
        if (referenceDocumentTypeId == 4) {
            referenceDocumentType = "WMS to WMS"; //iwt
        }
        if (referenceDocumentTypeId == 5) {
            referenceDocumentType = "DirectReceipt";
        }

        return referenceDocumentType;
    }

//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @return
//     */
//    public PickupLineV2 getPickupLineForLastBinCheck(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                     String itemCode, String manufacturerName) {
//        String directReceiptStorageBin = "REC-AL-B2";   //storage-bin excluding direct stock receipt bin
//        PickupLineV2 pickupLine = pickupLineV2Repository
//                .findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorAndPickedStorageBinNotOrderByPickupConfirmedOnDesc(
//                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L, directReceiptStorageBin);
//        if (pickupLine != null) {
//            return pickupLine;
//        }
//        return null;
//    }

//    /**
//     * @param companyCodeId
//     * @param plantId
//     * @param languageId
//     * @param warehouseId
//     * @param itemCode
//     * @param manufacturerName
//     * @return
//     */
//    public PutAwayLineV2 getPutAwayLineExistingItemCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                           String itemCode, String manufacturerName) {
//        PutAwayLineV2 dbPutawayLine = putAwayLineV2Repository.
//                findTopByCompanyCodeAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
//                        companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 20L, 0L);
//        if (dbPutawayLine != null) {
//            return dbPutawayLine;
//        }
//        return null;
//    }

    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryForInhouseTransferV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                        String packBarcodes, String itemCode, String manufacturerName, String storageBin) {
        try {
            log.info("getInventory----------> : " + warehouseId + "," + packBarcodes + "," + itemCode + "," + storageBin);
            List<InventoryV2> inventory =
                    inventoryV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPackBarcodesAndItemCodeAndManufacturerCodeAndStorageBinAndDeletionIndicatorOrderByInventoryIdDesc(
                            languageId,
                            companyCode,
                            plantId,
                            warehouseId,
                            packBarcodes,
                            itemCode,
                            manufacturerName,
                            storageBin,
                            0L
                    );
            if (inventory.isEmpty()) {
                log.error("---------Inventory is null-----------");
                return null;
            }
            log.info("getInventory record----------> : " + inventory.get(0));
            return inventory.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Inventory Get : " + e);
        }
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param stockTypeId
     * @return
     */
    public String getStockTypeDesc(String companyCodeId, String plantId, String languageId,
                                   String warehouseId, Long stockTypeId) {

        String stockTypeDesc = stagingLineV2Repository.getStockTypeDescription(companyCodeId, plantId, languageId, warehouseId, stockTypeId);
        return stockTypeDesc;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param packBarcodes
     * @param binClassId
     * @param manufacturerName
     * @return
     */
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public InventoryV2 getInventoryForStockAdjustmentDamageV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                              String itemCode, String packBarcodes, Long binClassId, String manufacturerName) {
        try {
            InventoryV2 inventory =
                    inventoryV2Repository.findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndPackBarcodesAndBinClassIdAndManufacturerNameAndDeletionIndicatorOrderByInventoryIdDesc(
                            languageId, companyCodeId, plantId, warehouseId, itemCode, packBarcodes, binClassId, manufacturerName, 0L
                    );
            if (inventory != null) {
                log.info("InventoryForStockAdjustmentDamageV2: " + inventory);
                return inventory;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception while Inventory Get : " + e);
        }
    }

    /**
     * @param createdGRLine
     * @param loginUserID
     */
    public void createPutAwayHeaderNonCBMV2(GrLineV2 createdGRLine, String loginUserID) throws Exception {
        String itemCode = createdGRLine.getItemCode();
        String companyCode = createdGRLine.getCompanyCode();
        String plantId = createdGRLine.getPlantId();
        String languageId = createdGRLine.getLanguageId();
        String warehouseId = createdGRLine.getWarehouseId();
        String proposedStorageBin = createdGRLine.getInterimStorageBin();

        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
        storageBinPutAway.setCompanyCodeId(companyCode);
        storageBinPutAway.setPlantId(plantId);
        storageBinPutAway.setLanguageId(languageId);
        storageBinPutAway.setWarehouseId(warehouseId);

        Double cbm = 0D;

        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

        if (createdGRLine.getCbm() != null) {
            cbm = createdGRLine.getCbm();
            log.info("cbm, createdGrLine.getCbm: " + cbm + ", " + createdGRLine.getCbm());
        }
        outerloop:
//        while (true) {
        //  ASS_HE_NO
        if (createdGRLine != null) {
            // Insert record into PutAwayHeader
            PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
            BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
            putAwayHeader.setCompanyCodeId(companyCode);
            putAwayHeader.setReferenceField5(itemCode);

            // PA_NO
            long NUM_RAN_CODE = 7;
            String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
            log.info("PutAwayNumber Generated: " + nextPANumber);

            putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());

            //set bar code id for packbarcode
            putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());

            //set pack bar code for actual packbarcode
            putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());

            putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

            //-----------------PROP_ST_BIN---------------------------------------------

            //V2 Code
            Long binClassId = 0L;                   //actual code follows
            if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 || createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5) {
                binClassId = 1L;
            }
            if (createdGRLine.getInboundOrderTypeId() == 2) {
                binClassId = 7L;
            }
            log.info("BinClassId : " + binClassId);

//            List<IInventoryImpl> stBinInventoryList = inventoryService.getInventoryForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
//                    itemCode, createdGRLine.getManufacturerName(), binClassId);

            List<IInventoryImpl> stBinInventoryList = inventoryV2Repository.inventoryForPutAwaytemp(companyCode, plantId, languageId, warehouseId,
                    itemCode, createdGRLine.getManufacturerName(), binClassId);

            log.info("stBinInventoryList -----------> : " + stBinInventoryList.size());

            List<String> inventoryStorageBinList = null;
            if (stBinInventoryList != null && !stBinInventoryList.isEmpty()) {
                inventoryStorageBinList = stBinInventoryList.stream().map(IInventoryImpl::getStorageBin).collect(Collectors.toList());
            }
            log.info("Inventory StorageBin List: " + inventoryStorageBinList);

            if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
                storageBinPutAway.setBinClassId(binClassId);
                storageBinPutAway.setBin(proposedStorageBin);
                StorageBinV2 storageBin = null;
                try {
                    storageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                } catch (Exception e) {
                    throw new BadRequestException("Invalid StorageBin");
                }
                log.info("InterimStorageBin: " + storageBin);
                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                if (storageBin != null) {
                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                    putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                    cbm = 0D;               //to break the loop
                }
                if (storageBin == null) {
                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                    cbm = 0D;               //to break the loop
                }
            }
            //BinClassId - 7 - Return Order(Sale Return)
            if (createdGRLine.getInboundOrderTypeId() == 2) {

                storageBinPutAway.setBinClassId(binClassId);
                log.info("BinClassId : " + binClassId);

                StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                if (proposedBinClass7Bin != null) {
                    String proposedStBin = proposedBinClass7Bin.getStorageBin();
                    putAwayHeader.setProposedStorageBin(proposedStBin);
                    putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                    log.info("Return Order --> BinClassId7 Proposed Bin: " + proposedStBin);
                    cbm = 0D;   //break the loop
                }
                if (proposedBinClass7Bin == null) {
                    binClassId = 2L;
                    log.info("BinClassId : " + binClassId);
                    StorageBinV2 stBin = mastersService.getStorageBin(
                            companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                    log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                    cbm = 0D;   //break the loop
                }
            }

            if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                if (stBinInventoryList != null) {
                    log.info("BinClassId : " + binClassId);
                    if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                        if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                            storageBinPutAway.setBinClassId(binClassId);
                            storageBinPutAway.setStorageBin(inventoryStorageBinList);

                            StorageBinV2 proposedExistingBin = mastersService.getExistingStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (proposedExistingBin != null) {
                                proposedStorageBin = proposedExistingBin.getStorageBin();
                                log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);

                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
                            }
                            log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
                            cbm = 0D;   //break the loop
                        }
                        if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                            storageBinPutAway.setBinClassId(7L);
                            StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (proposedBinClass7Bin != null) {
                                String proposedStBin = proposedBinClass7Bin.getStorageBin();
                                putAwayHeader.setProposedStorageBin(proposedStBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                                log.info("Damage Qty --> BinClassId7 Proposed Bin: " + proposedStBin);
                                cbm = 0D;   //break the loop
                            }
                            if (proposedBinClass7Bin == null) {
                                binClassId = 2L;
                                log.info("BinClassId : " + binClassId);
                                StorageBinV2 stBin = mastersService.getStorageBin(
                                        companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                                log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                                cbm = 0D;   //break the loop
                            }
                        }
                    }
                }
            }

            //Last Picked Bin as Proposed Bin If it is empty
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || stBinInventoryList.isEmpty())) {
                PickupLineV2 pickupLineList = stockReceiptServiceV2.getPickupLineForLastBinCheck(companyCode, plantId, languageId, warehouseId, itemCode, createdGRLine.getManufacturerName());
                log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
//                    String lastPickedStorageBinList = null;
                if (pickupLineList != null) {
                    putAwayHeader.setProposedStorageBin(pickupLineList.getPickedStorageBin());
                    putAwayHeader.setLevelId(pickupLineList.getLevelId());
                    log.info("LastPick NonCBM Bin: " + pickupLineList.getPickedStorageBin());
                    log.info("LastPick NonCBM PutawayQty: " + createdGRLine.getGoodReceiptQty());
                    cbm = 0D;   //break the loop
//                        }
                }
            }

            //Propose Empty Bin if Last picked bin is unavailable
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if (putAwayHeader.getProposedStorageBin() == null && (stBinInventoryList == null || stBinInventoryList.isEmpty())) {
                // If ST_BIN value is null
                // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
                // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
                log.info("QuantityType : " + createdGRLine.getQuantityType());
                log.info("BinClassId : " + binClassId);

                storageBinPutAway.setStatusId(0L);
                StorageBinV2 proposedNonCbmStorageBin = null;

                if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                    storageBinPutAway.setBinClassId(binClassId);

                    //Checking confirmed bin in putAway line for that item
                    PutAwayLineV2 existingBinPutAwayLineItemCheck = stockReceiptServiceV2.getPutAwayLineExistingItemCheckV2(companyCode, plantId, languageId, warehouseId,
                            itemCode, createdGRLine.getManufacturerName());
                    log.info("existingBinPutAwayLineItemCheck: " + existingBinPutAwayLineItemCheck);
                    if (existingBinPutAwayLineItemCheck != null) {
                        proposedStorageBin = existingBinPutAwayLineItemCheck.getConfirmedStorageBin();
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        if (existingBinPutAwayLineItemCheck.getLevelId() != null) {
                            putAwayHeader.setLevelId(String.valueOf(existingBinPutAwayLineItemCheck.getLevelId()));
                        } else {
                            storageBinPutAway.setBin(proposedStorageBin);
                            StorageBinV2 getLevelIdForProposedBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                            if (getLevelIdForProposedBin != null) {
                                putAwayHeader.setLevelId(String.valueOf(getLevelIdForProposedBin.getFloorId()));
                            }
                        }
                        log.info("Existing PutAwayCreate ProposedStorageBin from putAway line-->A : " + proposedStorageBin);
                    }
                    List<PutAwayHeaderV2> existingBinItemCheck = stockReceiptServiceV2.getPutawayHeaderExistingBinItemCheckV2(companyCode, plantId, languageId, warehouseId,
                            itemCode, createdGRLine.getManufacturerName());
                    log.info("existingBinItemCheck: " + existingBinItemCheck);
                    if (existingBinItemCheck != null && !existingBinItemCheck.isEmpty()) {
                        proposedStorageBin = existingBinItemCheck.get(0).getProposedStorageBin();
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        putAwayHeader.setLevelId(String.valueOf(existingBinItemCheck.get(0).getLevelId()));
                        log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                        cbm = 0D;   //break the loop
                    }
                    List<String> existingBinCheck = stockReceiptServiceV2.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
                    log.info("existingBinCheck: " + existingBinCheck);
                    if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck != null && !existingBinCheck.isEmpty())) {
                        storageBinPutAway.setStorageBin(existingBinCheck);
                        proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        if (proposedNonCbmStorageBin != null) {
                            proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                            log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
                            log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                            cbm = 0D;   //break the loop
                        }
                    }
                    if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck == null || existingBinCheck.isEmpty() || existingBinCheck.size() == 0)) {
                        List<String> existingProposedPutawayStorageBin = stockReceiptServiceV2.getPutawayHeaderExistingBinCheckV2(companyCode, plantId, languageId, warehouseId);
                        log.info("existingProposedPutawayStorageBin: " + existingProposedPutawayStorageBin);
                        log.info("BinClassId: " + binClassId);
                        storageBinPutAway.setStorageBin(existingProposedPutawayStorageBin);
                        proposedNonCbmStorageBin = mastersService.getStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin);
                        if (proposedNonCbmStorageBin != null) {
                            proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                            log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));

                            cbm = 0D;   //break the loop
                        }
                        if (proposedNonCbmStorageBin == null) {
                            binClassId = 2L;
                            log.info("BinClassId : " + binClassId);
                            StorageBinV2 stBin = mastersService.getStorageBin(
                                    companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                            log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                            cbm = 0D;   //break the loop
                        }
                    }
                }

                /*
                 * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
                 * fetch ST_BIN values for STATUS_ID=EMPTY.
                 */
                if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                    binClassId = 7L;
                    storageBinPutAway.setBinClassId(binClassId);
                    log.info("BinClassId : " + binClassId);
                    StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
                    if (proposedBinClass7Bin != null) {
                        proposedStorageBin = proposedBinClass7Bin.getStorageBin();
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
                        log.info("D --> BinClassId7 Proposed Bin: " + proposedStorageBin);
                        cbm = 0D;   //break the loop
                    }
                    if (proposedBinClass7Bin == null) {
                        binClassId = 2L;
                        log.info("BinClassId : " + binClassId);
                        StorageBinV2 stBin = mastersService.getStorageBin(
                                companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
                        log.info("D --> reserveBin: " + stBin.getStorageBin());
                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                        cbm = 0D;   //break the loop
                    }
                }
            }
            /////////////////////////////////////////////////////////////////////////////////////////////////////
            log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
            log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
            //PROP_HE_NO	<- PAWAY_HE_NO
            if (createdGRLine.getReferenceDocumentType() != null) {
                putAwayHeader.setReferenceDocumentType(createdGRLine.getReferenceDocumentType());
            } else {
                putAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(createdGRLine.getInboundOrderTypeId()));
            }
            putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
            putAwayHeader.setCbmQuantity(createdGRLine.getCbmQuantity());

            IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                    languageId,
                    plantId,
                    warehouseId);

            putAwayHeader.setCompanyDescription(description.getCompanyDesc());
            putAwayHeader.setPlantDescription(description.getPlantDesc());
            putAwayHeader.setWarehouseDescription(description.getWarehouseDesc());

            PreInboundHeaderV2 dbPreInboundHeader = stockReceiptServiceV2.getPreInboundHeaderV2(companyCode, plantId, languageId, warehouseId,
                    createdGRLine.getPreInboundNo(), createdGRLine.getRefDocNumber());

            putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
            putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
            putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
            putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());

            putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
            putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
            putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
            putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
            putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
            putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());

            putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
            putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
            putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
            putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
            putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

            putAwayHeader.setStatusId(19L);
            statusDescription = stagingLineV2Repository.getStatusDescription(19L, createdGRLine.getLanguageId());
            putAwayHeader.setStatusDescription(statusDescription);

            putAwayHeader.setDeletionIndicator(0L);
            putAwayHeader.setCreatedBy(loginUserID);
            putAwayHeader.setUpdatedBy(loginUserID);
            putAwayHeader.setCreatedOn(new Date());
            putAwayHeader.setUpdatedOn(new Date());
            putAwayHeader.setConfirmedOn(new Date());
            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
            log.info("putAwayHeader : " + putAwayHeader);

            /*----------------Inventory tables Create---------------------------------------------*/

//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("FAHAHEEL");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

            InventoryV2 createdinventory = createInventoryNonCBMV2(createdGRLine);

            /*----------------INVENTORYMOVEMENT table Update---------------------------------------------*/
//                createInventoryMovementV2(createdGRLine, createdinventory.getStorageBin());
        }
//            if (cbm == 0D) {
//                break outerloop;
//            }
//        }
    }


    /**
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
