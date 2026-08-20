package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.crossdock.CrossDockInputV9;
import com.tekclover.wms.api.inbound.transaction.model.crossdock.CrossDockResponseV9;
import com.tekclover.wms.api.inbound.transaction.model.crossdock.FindCrossDockResponseV6;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.preoutbound.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.preoutbound.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CrossDockServiceV9 extends BaseService {

    @Autowired
    OrderService orderService;

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    GrLineV2Repository grLineV2Repository;

    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;

    @Autowired
    PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    GrLineService grLineService;

    @Autowired
    StorageBinV2Repository storageBinV2Repository;

    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;

    @Autowired
    StorageBinService storageBinService;

    @Autowired
    PutAwayLineV2Repository putAwayLineV2Repository;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    InboundHeaderService inboundHeaderService;

    @Autowired
    StagingLineService stagingLineService;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    /**
     * @param itemCode
     * @return
     */
    public FindCrossDockResponseV6 findCrossDockV9(String itemCode) {
        FindCrossDockResponseV6 crossDockResponse = new FindCrossDockResponseV6();
        log.info("Find Cross Dock -----> Start");
        List<StagingLineEntityV2> stagingLines = stagingLineV2Repository.findStagingLineByItemV9(itemCode);
        log.info("Find StagingLine------->" + stagingLines);
        log.info("Find StagingLines------->" + stagingLines.size());
        List<StagingLineEntityV2> stagingLineList = new ArrayList<>();
        for (StagingLineEntityV2 stagingLine : stagingLines) {
            String grNumber = grLineV2Repository.getGrNumberV9(stagingLine.getCompanyCode(), stagingLine.getPlantId(), stagingLine.getLanguageId(),
                    stagingLine.getWarehouseId(), stagingLine.getRefDocNumber(), stagingLine.getPreInboundNo(), stagingLine.getPalletCode(),
                    stagingLine.getCaseCode(), stagingLine.getStagingNo());
            stagingLine.setReferenceField9(grNumber);
            stagingLineList.add(stagingLine);
            stagingLineV2Repository.updateStagingLineCrossDockV6(stagingLine.getCompanyCode(), stagingLine.getPlantId(), stagingLine.getLanguageId(),
                    stagingLine.getWarehouseId(), stagingLine.getRefDocNumber(), stagingLine.getPreInboundNo(), stagingLine.getLineNo(), stagingLine.getItemCode());
        }
        List<PreOutboundLineV2> preOutboundLines = preOutboundLineV2Repository.findPreOutboundLineV9(itemCode);
        log.info("Staging Line {}", stagingLineList);
        crossDockResponse.setStagingLines(stagingLineList);
        log.info("PreOutbound Line {}", preOutboundLines);
        log.info("PreOutbound Line {}", preOutboundLines.size());
        crossDockResponse.setPreOutboundLine(preOutboundLines);
        log.info("Cross Dock {}", crossDockResponse);
        return crossDockResponse;
    }


    //================================================BF===========================================

    /**
     * @param newPutAwayLines putAwayLines
     * @param loginUserID     userID
     * @return
     */
    public List<PutAwayLineV2> putAwayLineConfirmNonCBMV9(@Valid PutAwayLineV2 newPutAwayLines, String loginUserID) {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("PutAwayLineList----->" + newPutAwayLines);
//        for (PutAwayLineV2 putAwayLineV2 : newPutAwayLines) {
        log.info("PutAwayLine Created Started ---------------->");
        createdPutAwayLines.add(createPutAwayLineProcessV9(newPutAwayLines, loginUserID));
//        }

        log.info("PutAwayLine Value size is {}", createdPutAwayLines.size());
        if (!createdPutAwayLines.isEmpty()) {
            putAwayLineV2Repository.saveAll(createdPutAwayLines);
        } else {
            throw new BadRequestException("PutAwayLine List is Empty  ------------------> ");
        }

        PutAwayLineV2 putAwayLine = createdPutAwayLines.get(0);

//        log.info("Inventory Async Process Started V9-------------------> ");
//        inventoryAsyncProcessService.createInventoryAsyncProcessV9(createdPutAwayLines, loginUserID);
//        log.info("Inventory Async Process Completed V9-------------------> ");

        inboundConfirmValidationV9(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(),
                putAwayLine.getRefDocNumber(), putAwayLine.getPreInboundNo(), loginUserID);
        return createdPutAwayLines;
    }


    //================================================BF===========================================

    /**
     * @param newPutAwayLines putAwayLines
     * @param loginUserID     userID
     * @return
     */
    public List<PutAwayLineV2> putAwayLineConfirmNonCBMV9ForStatus17(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID) {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("PutAwayLineList----->" + newPutAwayLines);
        for (PutAwayLineV2 putAwayLineV2 : newPutAwayLines) {
            log.info("PutAwayLine Created Started ---------------->");
            createdPutAwayLines.add(createPutAwayLineProcessV9(putAwayLineV2, loginUserID));
        }

        log.info("PutAwayLine Value size is {}", createdPutAwayLines.size());
        if (!createdPutAwayLines.isEmpty()) {
            putAwayLineV2Repository.saveAll(createdPutAwayLines);
        } else {
            throw new BadRequestException("PutAwayLine List is Empty  ------------------> ");
        }

        PutAwayLineV2 putAwayLine = createdPutAwayLines.get(0);

//        log.info("Inventory Async Process Started V9-------------------> ");
//        inventoryAsyncProcessService.createInventoryAsyncProcessV9(createdPutAwayLines, loginUserID);
//        log.info("Inventory Async Process Completed V9-------------------> ");

        inboundConfirmValidationV9(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(),
                putAwayLine.getRefDocNumber(), putAwayLine.getPreInboundNo(), loginUserID);
        return createdPutAwayLines;
    }


    //============================================BF==================================================

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     */
    private void inboundConfirmValidationV9(String companyCode, String plantId, String languageId, String warehouseId,
                                            String refDocNumber, String preInboundNo, String loginUserID) {
        IKeyValuePair confirmedLines = inboundHeaderV2Repository.findSumOfConfirmedInboundLines(companyCode, plantId, languageId, warehouseId, preInboundNo);
        if (confirmedLines != null) {
            log.info("InboundHeader orderQty: " + confirmedLines.getOrdQty() + ", RxdQty: " + confirmedLines.getRxdQty());
            if (confirmedLines.getOrdQty().equals(confirmedLines.getRxdQty())) {
                log.info("Initiate Automatic Inbound Confirmation------> " + refDocNumber + "---> " + preInboundNo);
                inboundHeaderService.updateInboundHeaderConfirmV9(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID);
            }
        }
    }
    //================================================BF===========================================

    /**
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public PutAwayLineV2 createPutAwayLineProcessV9(@Valid PutAwayLineV2 newPutAwayLine, String loginUserID) {
        log.info("newPutAwayLines to confirm V9 -----------> : " + newPutAwayLine);


        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(), newPutAwayLine.getWarehouseId());
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

        try {
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


            StorageBinV2 dbStorageBin = null;
            try {
                log.info("Inputs----->" + companyCode);
                log.info("Inputs----->" + plantId);
                log.info("Inputs----->" + warehouseId);
                log.info("Inputs----->" + newPutAwayLine.getConfirmedStorageBin());
                if (newPutAwayLine.getInboundOrderTypeId() == 11L) {
                    dbStorageBin = storageBinService.getStorageBinEmptyCrateV9(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                }
                if (newPutAwayLine.getInboundOrderTypeId() == 1L || newPutAwayLine.getInboundOrderTypeId() == 4L) {
                    dbStorageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                }
                if (newPutAwayLine.getInboundOrderTypeId() == 2L) {
                    dbStorageBin = storageBinService.getStorageBinV9(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                }
            } catch (Exception e) {
                throw new BadRequestException("Invalid StorageBin --> " + newPutAwayLine.getConfirmedStorageBin());
            }

            log.info("PutAwayHeader Record is Queried V9 --------------> Unique No is PackBarcodeId is -------> {}", newPutAwayLine.getPackBarcodes());
            PutAwayHeaderV2 putAwayHeader = putAwayHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndPackBarcodesAndDeletionIndicator(
                    companyCode, plantId, languageId, warehouseId, newPutAwayLine.getPreInboundNo(), newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPutAwayNumber(), newPutAwayLine.getPackBarcodes(), 0L);
            log.info("putawayHeader Record is Recevied: " + putAwayHeader);

            if (dbStorageBin != null) {
                dbPutAwayLine.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
            }

            StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
                    newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getManufacturerName());
            if (dbStagingLineEntity != null) {
                newPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                newPutAwayLine.setCompanyDescription(dbStagingLineEntity.getCompanyDescription());
                newPutAwayLine.setPlantDescription(dbStagingLineEntity.getPlantDescription());
                newPutAwayLine.setWarehouseDescription(dbStagingLineEntity.getWarehouseDescription());
                newPutAwayLine.setSize(dbStagingLineEntity.getSize());
                newPutAwayLine.setBrand(dbStagingLineEntity.getBrand());
            }

            BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));
            dbPutAwayLine.setStatusId(20L);
            statusDescription = getStatusDescription(20L, languageId);
            dbPutAwayLine.setStatusDescription(statusDescription);
            dbPutAwayLine.setDeletionIndicator(0L);
            dbPutAwayLine.setCreatedBy(loginUserID);
            dbPutAwayLine.setUpdatedBy(loginUserID);
            dbPutAwayLine.setConfirmedBy(loginUserID);
            dbPutAwayLine.setPalletId(putAwayHeader.getPalletCode());

            if (putAwayHeader != null) {
                dbPutAwayLine.setBatchSerialNumber(putAwayHeader.getBatchSerialNumber());
                dbPutAwayLine.setCreatedOn(putAwayHeader.getCreatedOn());
                dbPutAwayLine.setInboundOrderTypeId(putAwayHeader.getInboundOrderTypeId());
                dbPutAwayLine.setStorageSectionId(putAwayHeader.getStorageSectionId());
                if (dbPutAwayLine.getLineNo() == null) {
                    dbPutAwayLine.setLineNo(Long.valueOf(putAwayHeader.getReferenceField9()));
                }

                dbPutAwayLine.setMaterialNo(putAwayHeader.getMaterialNo());
                dbPutAwayLine.setPriceSegment(putAwayHeader.getPriceSegment());
                dbPutAwayLine.setArticleNo(putAwayHeader.getArticleNo());
                dbPutAwayLine.setGender(putAwayHeader.getGender());
                dbPutAwayLine.setColor(putAwayHeader.getColor());
                dbPutAwayLine.setSize(putAwayHeader.getSize());
                dbPutAwayLine.setNoPairs(putAwayHeader.getNoPairs());
                dbPutAwayLine.setReferenceField6(putAwayHeader.getReferenceField6());

                if (dbPutAwayLine.getParentProductionOrderNo() == null) {
                    dbPutAwayLine.setParentProductionOrderNo(putAwayHeader.getParentProductionOrderNo());
                }

                if (newPutAwayLine.getManufacturerDate() == null) {
                    dbPutAwayLine.setManufacturerDate(putAwayHeader.getManufacturerDate());
                }
                if (newPutAwayLine.getExpiryDate() == null) {
                    dbPutAwayLine.setExpiryDate(putAwayHeader.getExpiryDate());
                }

            } else {
                dbPutAwayLine.setCreatedOn(new Date());
            }
            dbPutAwayLine.setUpdatedOn(new Date());
            dbPutAwayLine.setConfirmedOn(new Date());
            dbPutAwayLine.setVehicleNo(putAwayHeader.getVehicleNo());
            dbPutAwayLine.setVehicleUnloadingDate(putAwayHeader.getVehicleUnloadingDate());
            dbPutAwayLine.setVehicleReportingDate(putAwayHeader.getVehicleReportingDate());
            dbPutAwayLine.setReceivingVariance(putAwayHeader.getReceivingVariance());

//            boolean existingPutAwayLine = putAwayLineV2Repository.existsByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndBarcodeIdAndItemCodeAndRefDocNumberAndDeletionIndicator(
//                    companyCode, plantId, languageId, warehouseId, newPutAwayLine.getBarcodeId(), newPutAwayLine.getItemCode(), refDocNumber, 0L);

//            if (!existingPutAwayLine) {
            String leadTime = putAwayLineV2Repository.getLeadTimeV3(dbPutAwayLine.getAssignedOn(), new Date());
            dbPutAwayLine.setReferenceField1(leadTime);
            log.info("LeadTime: " + leadTime);
            if (newPutAwayLine.getInboundOrderTypeId() == 11L) {
                dbPutAwayLine.setInventoryQuantity(newPutAwayLine.getQtyInCreate());
            }
//            else {
//                dbPutAwayLine.setInventoryQuantity(newPutAwayLine.getQtyInPiece());
//            }
            log.info("---------->createdPutAwayLine created: " + dbPutAwayLine);

            if (dbPutAwayLine.getPutawayConfirmedQty() > 0L) {

                if (!putAwayHeader.getProposedStorageBin().equalsIgnoreCase(newPutAwayLine.getProposedStorageBin())) {
                    log.info("Different Bin Picking -------------->");
                    Long occ_qty = storageBinV2Repository.getOccupaidQtyV5(newPutAwayLine.getProposedStorageBin(), companyCode, plantId, warehouseId);
                    log.info("Occ_Qty {} & Bin {} ---------------> V9 ", occ_qty, newPutAwayLine.getProposedStorageBin());
                    long balanceQty = (long) (newPutAwayLine.getPutawayConfirmedQty() + occ_qty);
                    log.info("Balance Qty ------------------------> " + balanceQty);
                    if (balanceQty > 30) {
                        throw new BadRequestException("PutAway quantity exceeds bin capacity for Location and line No " + newPutAwayLine.getLineNo());
                    }
                    storageBinV2Repository.updateStorageBinV5(dbPutAwayLine.getConfirmedStorageBin(), companyCode, plantId, warehouseId, languageId, 1L, balanceQty);
                } else {
                    log.info("Same Bin Picking ------------>");
                    storageBinV2Repository.updateStorageBinV5(dbPutAwayLine.getConfirmedStorageBin(), companyCode, plantId, warehouseId, languageId, 1L);
                }

                // Updating StorageBin StatusId as '1'
                dbStorageBin.setStatusId(1L);
                if (putAwayHeader != null) {
                    log.info("putawayConfirmQty, putawayQty: " + dbPutAwayLine.getPutawayConfirmedQty()
                            + ", " + putAwayHeader.getPutAwayQuantity());

                    log.info("PutawayHeader StatusId : 20");
                    statusDescription = stagingLineV2Repository.getStatusDescription(
                            putAwayHeader.getStatusId(), dbPutAwayLine.getLanguageId());
                    putAwayHeader.setStatusId(20L);
                    putAwayHeaderV2Repository.updatePutAwayHeaderV9(putAwayHeader.getWarehouseId(), putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(), putAwayHeader.getLanguageId(),
                            putAwayHeader.getRefDocNumber(), 20L, statusDescription, putAwayHeader.getPackBarcodes());
                    log.info("putAwayHeader updated --------> V9 ----------> " + putAwayHeader);
                }


                Double addedAcceptQty = 0.0;
                Double addedDamageQty = 0.0;

                // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                    addedAcceptQty = dbPutAwayLine.getPutawayConfirmedQty();
//                        inboundLine.setVarianceQty((inboundLine.getOrderQty() != null ? inboundLine.getOrderQty() : 0.0) - addedAcceptQty);
                }

                // if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
                if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                    addedDamageQty = dbPutAwayLine.getPutawayConfirmedQty();
//                        inboundLine.setDamageQty(addedDamageQty != null ? addedDamageQty : 0.0);
//                        inboundLine.setVarianceQty((inboundLine.getOrderQty() != null ? inboundLine.getOrderQty() : 0.0) - addedDamageQty);
                }

//                    inboundLine.setHsnCode(dbPutAwayLine.getPalletId());    // PalletCode For Inboundline
//                    inboundLine.setQtyInCase(dbPutAwayLine.getQtyInCase() != null ? dbPutAwayLine.getQtyInCase() : 0.0);
//                    inboundLine.setQtyInCreate(dbPutAwayLine.getQtyInCreate() != null ? dbPutAwayLine.getQtyInCreate() : 0.0);
//                    inboundLine.setReceivingVariance(dbPutAwayLine.getReceivingVariance() != null ? dbPutAwayLine.getReceivingVariance() : "0");
                statusDescription = getStatusDescription(20L, dbPutAwayLine.getLanguageId());
//                    inboundLine.setStatusDescription(statusDescription != null ? statusDescription : "");

                log.info("InboundLine Status AND Qty Update process {}, {}, {}, {}, {}, {}, {}", dbPutAwayLine.getPutawayConfirmedQty(),
                        0.0, addedDamageQty,
                        dbPutAwayLine.getStatusId(), dbPutAwayLine.getQtyInCase(), dbPutAwayLine.getQtyInPiece(), dbPutAwayLine.getQtyInCreate());

                //InboundLine update
                inboundLineV2Repository.updateInboundLineDetailsCrossDockV9(dbPutAwayLine.getPutawayConfirmedQty(),
                        0.0,
                        addedDamageQty,
                        20L,
                        dbPutAwayLine.getQtyInCase(), dbPutAwayLine.getManufacturerDate(), dbPutAwayLine.getExpiryDate(), dbPutAwayLine.getGender(),
                        dbPutAwayLine.getGoodsReceiptNo(), statusDescription,
                        dbPutAwayLine.getCompanyCode(),
                        dbPutAwayLine.getPlantId(), dbPutAwayLine.getWarehouseId(), dbPutAwayLine.getRefDocNumber(),
                        dbPutAwayLine.getPreInboundNo(), dbPutAwayLine.getParentProductionOrderNo(), dbPutAwayLine.getItemCode());
//                        InboundLineV2 updatedInboundLine = inboundLineV2Repository.saveAndFlush(inboundLine);


//                    inboundLineV2Repository.delete(inboundLine);
            }
//            }
            log.info("Return PutAwayLine -----------> V9 <---------------" + dbPutAwayLine);
            return dbPutAwayLine;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param crossDockInputV9
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public CrossDockResponseV9 createGrLineCrossDockV9(CrossDockInputV9 crossDockInputV9, String loginUserID) throws Exception {

        log.info(".....CrossDocking Started .....");
        CrossDockResponseV9 crossDockResponse = new CrossDockResponseV9();
        List<AddGrLineV2> crossDockedGrLine = new ArrayList<>();
        List<PutAwayLineV2> crossDockedPutAwayLine = new ArrayList<>();
        log.info(".................CrossDock For Inbound................");
        for (StagingLineEntityV2 stagingLineEntityV2 : crossDockInputV9.getStagingLines()) {
//            Long statusId = stagingLineV2Repository.getStagingLineV9(stagingLineEntityV2.getCompanyCode(), stagingLineEntityV2.getPlantId(),
//                    stagingLineEntityV2.getLanguageId(), stagingLineEntityV2.getWarehouseId(), stagingLineEntityV2.getRefDocNumber(), stagingLineEntityV2.getPreInboundNo(),
//                    stagingLineEntityV2.getLineNo(), stagingLineEntityV2.getItemCode(), stagingLineEntityV2.getBarcodeId(), stagingLineEntityV2.getCaseCode(), stagingLineEntityV2.getPalletCode(),
//                    stagingLineEntityV2.getStagingNo());
            if (stagingLineEntityV2.getStatusId().equals(14L)) {
                AddGrLineV2 addGrLineV2 = new AddGrLineV2();
                BeanUtils.copyProperties(stagingLineEntityV2, addGrLineV2, CommonUtils.getNullPropertyNames(stagingLineEntityV2));
                crossDockedGrLine.add(addGrLineV2);
            } else if (stagingLineEntityV2.getStatusId().equals(17L)) {
                PutAwayLineV2 putAwayLineV2 = new PutAwayLineV2();
                List<PutAwayHeaderV2> putAwayHeaderV2 = putAwayHeaderV2Repository.getPutawayHeaderV9(stagingLineEntityV2.getCompanyCode(), stagingLineEntityV2.getLanguageId(),
                        stagingLineEntityV2.getPlantId(), stagingLineEntityV2.getWarehouseId(), stagingLineEntityV2.getPreInboundNo(), stagingLineEntityV2.getRefDocNumber());
                log.info("PutAwayHeader------------->" + putAwayHeaderV2);
//                BeanUtils.copyProperties(putAwayHeaderV2, putAwayLineV2, CommonUtils.getNullPropertyNames(putAwayHeaderV2));
                for (PutAwayHeaderV2 putAwayHeader : putAwayHeaderV2) {
                    BeanUtils.copyProperties(putAwayHeader, putAwayLineV2, CommonUtils.getNullPropertyNames(putAwayHeader));
                    putAwayLineV2.setItemCode(putAwayHeader.getReferenceField5());
                    putAwayLineV2.setPutawayConfirmedQty(putAwayHeader.getPutAwayQuantity());
                    putAwayLineV2.setConfirmedStorageBin(putAwayHeader.getProposedStorageBin());
                    putAwayLineV2.setCompanyCode(putAwayHeader.getCompanyCodeId());
                    crossDockedPutAwayLine.add(putAwayLineV2);
                }
            }
        }
        if (!crossDockedGrLine.isEmpty()) {
            List<GrLineV2> grLineV2s = grLineService.createNewGrLineV9(crossDockedGrLine, loginUserID, 1L);
            crossDockResponse.setGrLine(grLineV2s);
            log.info("CrossDock GrLine saved Line Size---->" + grLineV2s.size());
        } else if (!crossDockedPutAwayLine.isEmpty()) {
            List<PutAwayLineV2> putAwayLineV2s = putAwayLineConfirmNonCBMV9ForStatus17(crossDockedPutAwayLine, loginUserID);
            crossDockResponse.setPutAwayLineV2(putAwayLineV2s);
            log.info("CrossDock PutAwayLine Saved Line Size------>" + putAwayLineV2s);
        }

        log.info(".................CrossDock For Outbound................");

        List<PreOutboundLineV2> preOutboundLineV2List = new ArrayList<>();
        for (PreOutboundLineV2 preOutboundLineV2 : crossDockInputV9.getPreOutboundLines()) {
//            Long status = preOutboundLineV2Repository.getPreOutboundLineV9(preOutboundLineV2.getCompanyCodeId(), preOutboundLineV2.getPlantId(),
//                    preOutboundLineV2.getLanguageId(), preOutboundLineV2.getWarehouseId(), preOutboundLineV2.getRefDocNumber(), preOutboundLineV2.getPreOutboundNo(), preOutboundLineV2.getItemCode());

            if (preOutboundLineV2.getStatusId().equals(5L)) {
                PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
                BeanUtils.copyProperties(preOutboundLineV2, preOutboundLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
                preOutboundLineV2List.add(preOutboundLine);
            } else if (preOutboundLineV2.getStatusId().equals(48L)) {
                log.info("No Suitable Outbound Found" + preOutboundLineV2);
            }
        }
        if (!preOutboundLineV2List.isEmpty()) {
            List<OrderManagementLineV2> orderManagementLineV2 = createOrderManagementLineV9(preOutboundLineV2List);
            crossDockResponse.setOrderManagementLine(orderManagementLineV2);
        }

        return crossDockResponse;
    }

    //=======================================================BF=======================================================
    public List<OrderManagementLineV2> createOrderManagementLineV9(List<PreOutboundLineV2> preOutboundLineV2List) throws Exception {

        List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();
        if (preOutboundLineV2List == null || preOutboundLineV2List.isEmpty()) {
            throw new BadRequestException("PreOutboundLine List is Empty");
        }

        PreOutboundLineV2 preOutboundLineV2 = preOutboundLineV2List.get(0);
        PreOutboundHeaderV2 preOutboundHeaderV2 = preOutboundHeaderV2Repository
                .getPreOutboundHeaderV9(
                        preOutboundLineV2.getCompanyCodeId(),
                        preOutboundLineV2.getPlantId(),
                        preOutboundLineV2.getLanguageId(),
                        preOutboundLineV2.getWarehouseId(),
                        preOutboundLineV2.getPreOutboundNo(),
                        preOutboundLineV2.getRefDocNumber()
                );

        String loginUserId = MW_BFS;

        log.info("OrderManagementLineCreate and Inventory Allocation Started ----------------------------> ");

        log.info("Total PreOutboundLines received: {}", preOutboundLineV2List.size());

        List<PreOutboundLineV2> barcodeNullList = preOutboundLineV2List.stream()
                .filter(line -> line.getItemCode() != null && (line.getBarcodeId() == null || line.getBarcodeId().isBlank()))
                .collect(Collectors.toList());

        List<PreOutboundLineV2> barcodeNotNullList = preOutboundLineV2List.stream()
                .filter(line -> line.getItemCode() != null && line.getBarcodeId() != null && !line.getBarcodeId().isBlank())
                .collect(Collectors.toList());

        Map<String, Double> itemCodeToQtyMap = barcodeNullList.stream()
                .collect(Collectors.groupingBy(
                        PreOutboundLineV2::getItemCode,
                        Collectors.summingDouble(line -> Optional.ofNullable(line.getOrderQty()).orElse(0.0))
                ));

        Map<String, Double> itemCodeBarcodeToQtyMap = barcodeNotNullList.stream()
                .collect(Collectors.groupingBy(
                        line -> line.getItemCode() + "|" + line.getBarcodeId(),
                        Collectors.summingDouble(line -> Optional.ofNullable(line.getOrderQty()).orElse(0.0))
                ));

        log.info("Grouped by itemCode (barcode null): {}", itemCodeToQtyMap.keySet());
        log.info("Grouped by itemCode|barcode (barcode not null): {}", itemCodeBarcodeToQtyMap.keySet());

        for (Map.Entry<String, Double> entry : itemCodeToQtyMap.entrySet()) {
            String itemCode = entry.getKey();
            Double totalQty = entry.getValue();

            try {
                PreOutboundLineV2 referenceLine = barcodeNullList.stream()
                        .filter(p -> p.getItemCode().equals(itemCode))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("ItemCode not found: " + itemCode));

                PreOutboundLineV2 newLine = new PreOutboundLineV2();
                BeanUtils.copyProperties(referenceLine, newLine, CommonUtils.getNullPropertyNames(referenceLine));
                newLine.setOrderQty(totalQty);

                log.info("Creating OrderManagementLine (barcode null) | ItemCode: {} | TotalQty: {}", itemCode, totalQty);

                createOrderManagementProcessV9(preOutboundLineV2.getCompanyCodeId(), preOutboundLineV2.getPlantId(), preOutboundLineV2.getLanguageId(), preOutboundLineV2.getWarehouseId(), preOutboundHeaderV2, newLine, loginUserId);
                log.info("OrderManagementLineCreate and Inventory Allocation Completed ----------------------------> ");

            } catch (Exception ex) {
                log.error("Failed to create OrderManagementLine (barcode null) for ItemCode: {} | Error: {}", itemCode, ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }

        for (Map.Entry<String, Double> entry : itemCodeBarcodeToQtyMap.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            String itemCode = parts[0];
            String barcode = parts[1];
            Double totalQty = entry.getValue();

            try {
                PreOutboundLineV2 referenceLine = barcodeNotNullList.stream()
                        .filter(p -> p.getItemCode().equals(itemCode) && p.getBarcodeId().equals(barcode))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("ItemCode/Barcode not found: " + entry.getKey()));

                PreOutboundLineV2 newLine = new PreOutboundLineV2();
                BeanUtils.copyProperties(referenceLine, newLine, CommonUtils.getNullPropertyNames(referenceLine));
                newLine.setOrderQty(totalQty);

                log.info("Creating OrderManagementLine (barcode present) | ItemCode: {} | Barcode: {} | TotalQty: {}", itemCode, barcode, totalQty);

                createOrderManagementProcessV9(preOutboundLineV2.getCompanyCodeId(), preOutboundLineV2.getPlantId(), preOutboundLineV2.getLanguageId(), preOutboundLineV2.getWarehouseId(), preOutboundHeaderV2, newLine, loginUserId);
                log.info("OrderManagementLineCreate and Inventory Allocation Completed ----------------------------> ");

            } catch (Exception ex) {
                log.error("Failed to create OrderManagementLine (barcode present) for ItemCode: {} | Barcode: {} | Error: {}", itemCode, barcode, ex.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        }
        for (PreOutboundLineV2 preOutboundLine : preOutboundLineV2List) {
            log.info("OutboundLine Creation Process -------------> V9");
            List<OutboundLineV2> outboundLineV9 = createOutboundLineV9(preOutboundLine, preOutboundHeaderV2);
            outboundLineV2Repository.saveAll(outboundLineV9);
        }

        Long statusId = 41L;
        statusDescription = getStatusDescription(statusId, preOutboundLineV2.getLanguageId());
        OrderManagementHeaderV2 headerV9 = createOrderManagementHeaderV9(preOutboundHeaderV2, statusId, statusDescription, MW_AMS);
        log.info("OrderManagementHeader Creation Process ------------> V9: RefDocNo is  ----> {} ", headerV9.getRefDocNumber());

        OutboundHeaderV2 outboundHeader = createOutboundHeaderV9(preOutboundHeaderV2, preOutboundHeaderV2, statusId, statusDescription);
        log.info("outboundHeader Creation Process ----------------> V9: RefDocNo is  ----> {} ", outboundHeader.getRefDocNumber());

        createPickupHeaderV9(preOutboundHeaderV2.getCompanyCodeId(), preOutboundHeaderV2.getPlantId(), preOutboundHeaderV2.getLanguageId(),
                preOutboundHeaderV2.getWarehouseId(), preOutboundHeaderV2.getPreOutboundNo(), preOutboundHeaderV2.getRefDocNumber(), preOutboundHeaderV2);
        orderManagementHeaderV2Repository.save(headerV9);
        outboundHeaderV2Repository.save(outboundHeader);
        log.info("All OrderManagementLines created successfully for PreOutboundNo: {}", preOutboundLineV2.getPreOutboundNo());
        return orderManagementLineV2List;
    }

    //===================================================BF===============================================

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundHeaderV2
     * @param preOutboundLine
     * @param loginUserId
     * @throws Exception
     */
    private void createOrderManagementProcessV9(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                PreOutboundHeaderV2 preOutboundHeaderV2, PreOutboundLineV2 preOutboundLine, String loginUserId) throws Exception {
        try {
            OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
            log.info("orderManagementLine ------------> V9 : " + orderManagementLine);

            Long OB_ORD_TYP_ID = preOutboundHeaderV2.getOutboundOrderTypeId();
            Long BIN_CLASS_ID;

            if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 3L || OB_ORD_TYP_ID == 11L || OB_ORD_TYP_ID == 1L) {
                BIN_CLASS_ID = 1L;
                updateAllocationV9(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundLine.getItemCode(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId);
            }
            if (OB_ORD_TYP_ID == 2L) {
                BIN_CLASS_ID = 7L;
                updateAllocationV9(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundLine.getItemCode(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId);
            }
            if (OB_ORD_TYP_ID == 7L) {
                BIN_CLASS_ID = 3L;
                updateAllocationV9(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundLine.getItemCode(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId);
            }
        } catch (Exception e) {
            log.error("Exception While OrderManagementLine create: " + e);
            throw e;
        }
    }


    //=======================================================BF=======================================================

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param binClassId
     * @param orderManagementLine
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 updateAllocationV9(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String itemCode, Long binClassId, Double orderQty,
                                                    OrderManagementLineV2 orderManagementLine, String loginUserID) throws Exception {
        try {
            String manufacturerName = orderManagementLine.getManufacturerName();
            log.info("Quantity Logic started ----------> ");
            setAlternateUomQuantitiesV9(orderManagementLine);
            log.info("Quantity Logic started ----------> ");
            Double ORD_QTY = orderManagementLine.getOrderQty();
            Double INCOMING_ORD_QTY = orderManagementLine.getOrderQty();
            Double RECEIVING_ORD_QTY = 0D;
            log.info("ORD_QTY is ------------------> {} ", ORD_QTY);
            Long stockTypeId = 1L;
            String INV_STRATEGY = null;
            String obStrategy = null;
            String fifoMethod = null;
            if (orderManagementLine.getBarcodeId() != null && !orderManagementLine.getBarcodeId().isEmpty()) {
                log.info("BarcodeId is ------> " + orderManagementLine.getBarcodeId());
//                INV_STRATEGY = "SB_BEST_FIT";
                INV_STRATEGY = "FIFO";

            } else {
                log.info("Starting Warehouse table call for Strategies");
                List<Object[]> strategyList = stagingLineV2Repository.getStrategy(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), warehouseId);
                if (strategyList != null && !strategyList.isEmpty()) {
                    Object[] strategy = strategyList.get(0); // GET_FIRST_RECORD
                    if (strategy.length > 0 && strategy[0] != null) {
                        obStrategy = strategy[0].toString();
                    }
                    if (strategy.length > 0 && strategy[1] != null) {
                        fifoMethod = strategy[1].toString();
                    }
                }
                log.info("OB_STRATEGY: {}, FIFO_MD: {}", obStrategy, fifoMethod);
                INV_STRATEGY = obStrategy;
            }

            // Inventory Strategy Choices
            if (INV_STRATEGY == null) {
                INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
            }

            log.info("Allocation Strategy: " + INV_STRATEGY);
            OrderManagementLineV2 newOrderManagementLine = null;
            int invQtyByLevelIdCount = 0;
            int invQtyGroupByLevelIdCount = 0;
            // Getting Inventory GroupBy ST_BIN wise
            List<IInventoryImpl> finalInventoryList = null;
            List<InventoryV2> inventoryV2List = null;
            if (INV_STRATEGY.equalsIgnoreCase("FIFO")) {
                double balanceOrderQty = orderManagementLine.getOrderQty();
                if (orderManagementLine.getOutboundOrderTypeId().equals(3L) || orderManagementLine.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode, orderManagementLine.getBarcodeId());
                }
                if (orderManagementLine.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9Bin7(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode, orderManagementLine.getBarcodeId());
                }
                if (orderManagementLine.getOutboundOrderTypeId().equals(7L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9Bin3(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode, orderManagementLine.getBarcodeId());
                }

                log.info("Inventory List {} in Order Allocation", inventoryV2List);

                if (inventoryV2List.isEmpty()) {
                    log.warn("No inventory available for allocation for itemCode: {}", itemCode);
                }
                Long STATUS_ID = null;
                for (InventoryV2 inventory : inventoryV2List) {
                    String barcodeId = inventory.getBarcodeId();
                    Double sumOfQty = orderManagementLineV2Repository.getSumOfOrderQtyV9(
                            companyCodeId, plantId, languageId, warehouseId, itemCode, inventory.getPalletCode(), barcodeId);

                    double invQty = inventory.getInventoryQuantity();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;
//                    double availableQty = invQty - alreadyAllocated;
                    double availableQty = Math.abs(invQty - alreadyAllocated);
                    double allocatedQty = 0D;

                    log.info("BarcodeId {}, InvQty {}, AlreadyAllocated {}, Available {}",
                            barcodeId, invQty, alreadyAllocated, availableQty);

                    if (availableQty <= 0) {
                        log.info("Barcode {} has no available stock. Skipping...", barcodeId);
                        continue;
                    }

                    if (balanceOrderQty >= availableQty) {
                        balanceOrderQty = balanceOrderQty - availableQty;
                        allocatedQty = availableQty;
                    } else {
                        allocatedQty = balanceOrderQty;
                        balanceOrderQty = 0;
                    }
                    OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderManagementLine, orderLine, CommonUtils.getNullPropertyNames(orderManagementLine));
                    orderLine.setBarcodeId(barcodeId);
                    orderLine.setProposedPackBarCode(inventory.getPackBarcodes());
                    orderLine.setProposedBatchSerialNumber(inventory.getBatchSerialNumber());
                    if (orderManagementLine.getOutboundOrderTypeId() == 11) {
                        orderLine.setBarcodeId("Empty Crate");
                    }
                    orderLine.setManufacturerDate(inventory.getManufacturerDate());
                    orderLine.setReferenceField3(String.valueOf(inventory.getReferenceField4()));
                    orderLine.setExpiryDate(inventory.getExpiryDate());
                    orderLine.setInventoryQty(balanceOrderQty);
                    orderLine.setAllocatedQty(allocatedQty);
                    orderLine.setProposedStorageBin(inventory.getStorageBin());
                    orderLine.setItemCode(itemCode);
                    orderLine.setDescription(inventory.getDescription());
                    orderLine.setOrderQty(orderManagementLine.getOrderQty());
                    orderLine.setPalletId(inventory.getPalletCode());


                    /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                    if (balanceOrderQty == allocatedQty) {
                        STATUS_ID = 43L;
                    } else {
                        STATUS_ID = 42L;
                    }
                    String statusDescription = getStatusDescription(STATUS_ID, languageId);
                    orderLine.setStatusId(STATUS_ID);
                    orderLine.setStatusDescription(statusDescription);
                    orderLine.setReferenceField7(statusDescription);
                    orderLine.setPickupUpdatedBy(loginUserID);
                    orderLine.setPickupUpdatedOn(new Date());
                    orderLine.setManufacturerCode(inventory.getManufacturerCode());
                    orderLine.setManufacturerName(inventory.getManufacturerName());
                    orderLine.setManufacturerFullName(inventory.getManufacturerName());

                    orderManagementLineV2Repository.save(orderLine);
                    log.info("Fully allocated {} qty from barcode {}, Remaining: {}", allocatedQty, barcodeId, balanceOrderQty);

                    if (balanceOrderQty <= 0) {
                        return orderLine;
                    }
                }
            }
            if (INV_STRATEGY.equalsIgnoreCase("FEFO")) {
                double balanceOrderQty = orderManagementLine.getOrderQty();
                if (orderManagementLine.getOutboundOrderTypeId().equals(3L) || orderManagementLine.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationV9(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode);
                }
                if (orderManagementLine.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationBin7V9(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode);
                }
                if (orderManagementLine.getOutboundOrderTypeId().equals(7L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationBin3V9(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode);
                }
                log.info("Inventory List {} in Order Allocation", inventoryV2List);

                if (inventoryV2List.isEmpty()) {
                    log.warn("No inventory available for allocation for itemCode: {}", itemCode);
                }
                Long STATUS_ID = null;
                for (InventoryV2 inventory : inventoryV2List) {
                    String barcodeId = inventory.getBarcodeId();
                    Double sumOfQty = orderManagementLineV2Repository.getSumOfOrderQtyV9(
                            companyCodeId, plantId, languageId, warehouseId, itemCode, inventory.getPalletCode(), barcodeId);

                    log.info("Sum Of Qty----->" + sumOfQty);

                    double invQty = inventory.getInventoryQuantity();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;
                    double availableQty = Math.abs(invQty - alreadyAllocated);
                    double allocatedQty = 0D;

                    log.info("BarcodeId {}, InvQty {}, AlreadyAllocated {}, Available {}",
                            barcodeId, invQty, alreadyAllocated, availableQty);

                    if (availableQty <= 0) {
                        log.info("Barcode {} has no available stock. Skipping...", barcodeId);
                        continue;
                    }

                    if (balanceOrderQty >= availableQty) {
                        balanceOrderQty = balanceOrderQty - availableQty;
                        allocatedQty = availableQty;
                    } else {
                        allocatedQty = balanceOrderQty;
                        balanceOrderQty = 0;
                    }
                    OrderManagementLineV2 orderLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderManagementLine, orderLine, CommonUtils.getNullPropertyNames(orderManagementLine));
                    orderLine.setBarcodeId(barcodeId);
                    orderLine.setProposedPackBarCode(inventory.getPackBarcodes());
                    orderLine.setProposedBatchSerialNumber(inventory.getBatchSerialNumber());
                    if (orderManagementLine.getOutboundOrderTypeId() == 11) {
                        orderLine.setBarcodeId("Empty Crate");
                    }
                    orderLine.setManufacturerDate(inventory.getManufacturerDate());
                    orderLine.setReferenceField3(String.valueOf(inventory.getReferenceField4()));
                    orderLine.setExpiryDate(inventory.getExpiryDate());
                    orderLine.setInventoryQty(balanceOrderQty);
                    orderLine.setAllocatedQty(allocatedQty);
                    orderLine.setProposedStorageBin(inventory.getStorageBin());
                    orderLine.setItemCode(itemCode);
                    orderLine.setDescription(inventory.getDescription());
                    orderLine.setOrderQty(orderManagementLine.getOrderQty());
                    orderLine.setPalletId(inventory.getPalletCode());


                    /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                    if (balanceOrderQty == allocatedQty) {
                        STATUS_ID = 43L;
                    } else {
                        STATUS_ID = 42L;
                    }
                    String statusDescription = getStatusDescription(STATUS_ID, languageId);
                    orderLine.setStatusId(STATUS_ID);
                    orderLine.setStatusDescription(statusDescription);
                    orderLine.setReferenceField7(statusDescription);
                    orderLine.setPickupUpdatedBy(loginUserID);
                    orderLine.setPickupUpdatedOn(new Date());
                    orderLine.setManufacturerCode(inventory.getManufacturerCode());
                    orderLine.setManufacturerName(inventory.getManufacturerName());
                    orderLine.setManufacturerFullName(inventory.getManufacturerName());

                    orderManagementLineV2Repository.save(orderLine);
                    log.info("Fully allocated {} qty from barcode {}, Remaining: {}", allocatedQty, barcodeId, balanceOrderQty);

                    if (balanceOrderQty <= 0) {
                        return orderLine;
                    }
                }
            }
//            log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");

            // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
//            if (finalInventoryList == null || (finalInventoryList != null && finalInventoryList.isEmpty())) {
//                return updateOrderManagementLineV2(orderManagementLine);
//            }

            if (inventoryV2List == null || (finalInventoryList == null && finalInventoryList.isEmpty())) {
                return updateOrderManagementLineV2(orderManagementLine);
            }

            newOrderManagementLine = orderAllocationV9(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);

            log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
            return newOrderManagementLine;
        } catch (Exception e) {
            log.error("Exception while updateAllocation V3: " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param ORD_QTY
     * @param orderManagementLine
     * @param finalInventoryList
     * @param loginUserID
     * @return
     */
    public OrderManagementLineV2 orderAllocationV9(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, Double ORD_QTY,
                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
                                                   String loginUserID) throws Exception {
        try {
            if (finalInventoryList == null || finalInventoryList.isEmpty()) {
                return updateOrderManagementLineV2(orderManagementLine);
            }
            OrderManagementLineV2 newOrderManagementLine = null;
            outerloop:
            for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
                if (stBinWiseInventory == null) {
                    return updateOrderManagementLineV2(orderManagementLine);
                }

                if (stBinWiseInventory != null) {

                    Long STATUS_ID = 0L;
                    Double ALLOC_QTY = 0D;
                    Double INV_QTY = stBinWiseInventory.getInventoryQuantity();
                    orderManagementLine.setInventoryQty(INV_QTY);

                    if (ORD_QTY <= INV_QTY) {
                        ALLOC_QTY = ORD_QTY;
                    } else if (ORD_QTY > INV_QTY) {
                        ALLOC_QTY = INV_QTY;
                    } else if (INV_QTY == 0) {
                        ALLOC_QTY = 0D;
                    }
                    log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);
                    if (orderManagementLine.getStatusId() == 47L) {
                        try {
                            orderManagementLineV2Repository.delete(orderManagementLine);
                            log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
                        } catch (Exception e) {
                            log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
                            e.printStackTrace();
                        }
                    }
                    orderManagementLine.setAllocatedQty(ALLOC_QTY);
                    orderManagementLine.setReAllocatedQty(ALLOC_QTY);

                    // STATUS_ID
                    /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
                    if (ORD_QTY > ALLOC_QTY) {
                        STATUS_ID = 42L;
                    }

                    /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                    if (ORD_QTY == ALLOC_QTY) {
                        STATUS_ID = 43L;
                    }

                    statusDescription = getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
                    orderManagementLine.setStatusId(STATUS_ID);
                    orderManagementLine.setStatusDescription(statusDescription);
                    orderManagementLine.setReferenceField7(statusDescription);
                    orderManagementLine.setPickupUpdatedBy(loginUserID);
                    orderManagementLine.setPickupUpdatedOn(new Date());
                    double allocatedQtyFromOrderMgmt = 0.0;
                    newOrderManagementLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));
                    if (newOrderManagementLine.getCompanyDescription() == null) {
                        description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                        newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                        newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                        newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    newOrderManagementLine.setProposedStorageBin(stBinWiseInventory.getStorageBin());
                    newOrderManagementLine.setBarcodeId(stBinWiseInventory.getBarcodeId());
                    newOrderManagementLine.setLevelId(stBinWiseInventory.getLevelId());
                    newOrderManagementLine.setStorageSectionId(stBinWiseInventory.getStorageSectionId());
                    newOrderManagementLine.setProposedPackBarCode(stBinWiseInventory.getPackBarcodes());
                    newOrderManagementLine.setProposedBatchSerialNumber(stBinWiseInventory.getBatchSerialNumber());
                    newOrderManagementLine.setArticleNo(stBinWiseInventory.getArticleNo());
                    newOrderManagementLine.setManufacturerDate(stBinWiseInventory.getManufacturerDate());
                    newOrderManagementLine.setExpiryDate(stBinWiseInventory.getExpiryDate());
                    String totQty = String.valueOf(stBinWiseInventory.getReferenceField4());
                    newOrderManagementLine.setPalletId(stBinWiseInventory.getPalletCode());
                    newOrderManagementLine.setReferenceField3(totQty);

                    // Logic for checking ordermanagementline partner_item_barcode duplicates
                    List<Long> statusIds = Arrays.asList(42L, 43L, 48L);  //42,43,48
                    boolean existingOrderManagementLine = orderManagementLineV2Repository.existsByBarcodeIdAndStatusIdInAndDeletionIndicator(newOrderManagementLine.getBarcodeId(), statusIds, 0L);

                    OrderManagementLineV2 createdOrderManagementLine = null;
                    if (existingOrderManagementLine) {
                        log.warn("OrderManagementLine with same barcodeId is existing ---> {}", newOrderManagementLine.getBarcodeId());
                    } else {
                        createdOrderManagementLine = orderManagementLineV2Repository.saveAndFlush(newOrderManagementLine);
                        log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                        allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                        if (ORD_QTY > ALLOC_QTY) {
                            ORD_QTY = ORD_QTY - ALLOC_QTY;
                        }

                        if (ORD_QTY.equals(ALLOC_QTY)) {
                            log.info("ORD_QTY fully allocated: " + ORD_QTY);
                            break outerloop; // If the Inventory satisfied the Ord_qty
                        }
                    }
                }
            }
            return newOrderManagementLine;
        } catch (Exception e) {
            log.error("Exception while orderAllocation V3: " + e);
            throw e;
        }
    }

    //=======================================================BF=======================================================

    /**
     * @param orderManagementLineV2
     */
    private void setAlternateUomQuantitiesV9(OrderManagementLineV2 orderManagementLineV2) {
        try {
            Double qtyInPiece = null;
            Double qtyInCase = null;
            Double qtyInCreate = null;

            String orderUom = orderManagementLineV2.getOrderUom();
            String companyCodeId = orderManagementLineV2.getCompanyCodeId();
            String plantId = orderManagementLineV2.getPlantId();
            String warehouseId = orderManagementLineV2.getWarehouseId();
            String itemCode = orderManagementLineV2.getItemCode();

            if ("piece".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is PIECE");

                qtyInPiece = orderManagementLineV2.getOrderQty();
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                log.info("Piece Qty --- {}", orderManagementLineV2.getOrderQty());
                log.info("Case Qty ALT_UOM: {}", caseQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (orderManagementLineV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
                    qtyInCase = orderManagementLineV2.getOrderQty() / caseQty.getUomQty();
                }

                if (orderManagementLineV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = orderManagementLineV2.getOrderQty() / createQty.getUomQty();
                }

            } else if ("case".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CASE");

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                qtyInCase = orderManagementLineV2.getOrderQty();

                log.info("Case Qty --- {}", orderManagementLineV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (orderManagementLineV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = orderManagementLineV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (orderManagementLineV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = qtyInPiece / createQty.getUomQty();
                }
            } else if ("crate".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is Crate");
                qtyInCreate = orderManagementLineV2.getOrderQty();

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
                IKeyValuePair caseQy = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");

                log.info("Crate Qty --- {}", orderManagementLineV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", caseQy);

                if (orderManagementLineV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = orderManagementLineV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (orderManagementLineV2.getOrderQty() != null && caseQy != null && caseQy.getUomQty() != null) {
                    qtyInCase = qtyInPiece / caseQy.getUomQty();
                }
            }

            orderManagementLineV2.setQtyInPiece(qtyInPiece);
            orderManagementLineV2.setQtyInCase(qtyInCase);
            orderManagementLineV2.setQtyInCrate(qtyInCreate);
        } catch (Exception e) {
            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
        }
    }

    /**
     * @param orderManagementLine
     * @return x
     */
    private OrderManagementLineV2 updateOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {
        log.info("UnAllocated Order :" + orderManagementLine.getRefDocNumber());
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setBarcodeId("");
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param preOutboundLine
     * @param preOutboundHeaderV2
     * @return
     * @throws Exception
     */
    private List<OutboundLineV2> createOutboundLineV9(PreOutboundLineV2 preOutboundLine, PreOutboundHeaderV2 preOutboundHeaderV2) throws Exception {
        try {
            List<OutboundLineV2> outboundLines = new ArrayList<>();
//            Long lineNo = 1L;
//            for (PreOutboundLineV2 preOutboundLine : createdPreOutboundLine) {
            List<OrderManagementLineV2> orderManagementLine = orderManagementLineV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                    preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId(), preOutboundLine.getRefDocNumber(), preOutboundLine.getOutboundOrderTypeId(), 0L);
            log.info("OrderManagementLine List -----------> " + orderManagementLine.size());
            for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
                OutboundLineV2 outboundLine = new OutboundLineV2();
                BeanUtils.copyProperties(dbOrderManagementLine, outboundLine, CommonUtils.getNullPropertyNames(dbOrderManagementLine));
                outboundLine.setDeliveryQty(0D);
                outboundLine.setLineNumber(dbOrderManagementLine.getLineNumber());
                outboundLine.setStatusId(dbOrderManagementLine.getStatusId());
                outboundLine.setQtyInCrate(dbOrderManagementLine.getQtyInCrate());
                outboundLine.setQtyInPiece(dbOrderManagementLine.getQtyInPiece());
                outboundLine.setQtyInCase(dbOrderManagementLine.getQtyInCase());
                outboundLine.setDescription(dbOrderManagementLine.getDescription());
                statusDescription = getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
                outboundLine.setStatusDescription(statusDescription);
                outboundLine.setInvoiceDate(preOutboundHeaderV2.getRequiredDeliveryDate());

                if (outboundLine.getOutboundOrderTypeId() == 3L) {
                    outboundLine.setCustomerType("INVOICE");
                }
                if (outboundLine.getOutboundOrderTypeId() == 0L || outboundLine.getOutboundOrderTypeId() == 1L) {
                    outboundLine.setCustomerType("TRANSVERSE");
                }
//                    lineNo ++;
                outboundLines.add(outboundLine);
            }
//            }
//            outboundLines = outboundLineV2Repository.saveAll(outboundLines);
            log.info("outboundLines created -----2------>: " + outboundLines);
            return outboundLines;
        } catch (Exception e) {
            log.error("Exception While OutboundLine create: " + e);
            throw e;
        }
    }

    /**
     * @param createdPreOutboundHeader
     * @param loginUserId
     * @return
     */
    private OrderManagementHeaderV2 createOrderManagementHeaderV9(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, String statusDesc, String loginUserId) throws Exception {
        try {
            OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
            BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
            newOrderManagementHeader.setStatusId(statusId);
            newOrderManagementHeader.setStatusDescription(statusDesc);
            newOrderManagementHeader.setPickupCreatedBy(loginUserId);
            newOrderManagementHeader.setPickupCreatedOn(new Date());
            // Order_Text_Update
            String text = "OrderManagement Created";
            orderManagementLineV2Repository.updateOrderManagementText(newOrderManagementHeader.getOutboundOrderTypeId(), newOrderManagementHeader.getRefDocNumber(), text);
            log.info("OrderManagement Header Status Updated Successfully");
            return newOrderManagementHeader;
        } catch (Exception e) {
            log.error("Exception while creating OrderManagementHeader : " + e);
            throw e;
        }
    }

    /**
     * @param createdPreOutboundHeader
     * @param preOutboundHeaderV2
     * @param statusId
     * @param statusDesc
     * @return
     * @throws Exception
     */
    private OutboundHeaderV2 createOutboundHeaderV9(PreOutboundHeaderV2 createdPreOutboundHeader, PreOutboundHeaderV2 preOutboundHeaderV2,
                                                    Long statusId, String statusDesc) throws Exception {
        try {
            OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
            BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
            outboundHeader.setRefDocDate(new Date());
            outboundHeader.setStatusId(statusId);
            outboundHeader.setStatusDescription(statusDesc);
            outboundHeader.setInvoiceDate(preOutboundHeaderV2.getRequiredDeliveryDate());

            if (outboundHeader.getOutboundOrderTypeId() == 3L) {
                outboundHeader.setCustomerType("INVOICE");
            }
            if (outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L) {
                outboundHeader.setCustomerType("TRANSVERSE");
            }
            return outboundHeader;
        } catch (Exception e) {
            log.error("Exception While OutboundHeader create: " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param preOutboundHeaderV2
     * @throws Exception
     */
    public void createPickupHeaderV9(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                     String refDocNumber, PreOutboundHeaderV2 preOutboundHeaderV2) throws Exception {

        List<OrderManagementLineV2> orderManagementLines = orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicatorAndStatusIdNot(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L, 47L);

        log.info("OrderManagementList for PickupHeader -------------> {}", orderManagementLines.size());

        long NUM_RAN_CODE = 10;
        String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
        log.info("----------New PU_NO--------> : " + PU_NO);

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);
        if (orderManagementLines != null && !orderManagementLines.isEmpty()) {
            for (OrderManagementLineV2 orderManagementLine : orderManagementLines) {
                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                newPickupHeader.setPickupNumber(PU_NO);
                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());
                newPickupHeader.setBarcodeId(orderManagementLine.getBarcodeId());

                // STATUS_ID
                newPickupHeader.setStatusId(48L);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                newPickupHeader.setStatusDescription(statusDescription);

                // ProposedPackbarcode
                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());

                //Setting InventoryQuantity from orderManagementLine
                newPickupHeader.setInventoryQuantity(orderManagementLine.getInventoryQty());

                //Setting BagSize
                newPickupHeader.setBagSize(orderManagementLine.getInventoryQty());
                newPickupHeader.setNoBags(orderManagementLine.getNoBags());

                newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
                newPickupHeader.setBatchSerialNumber(orderManagementLine.getProposedBatchSerialNumber());
                newPickupHeader.setStorageSectionId(orderManagementLine.getStorageSectionId());
                newPickupHeader.setReferenceField2(orderManagementLine.getPalletId());
                newPickupHeader.setManufacturerDate(orderManagementLine.getManufacturerDate());
                newPickupHeader.setExpiryDate(orderManagementLine.getExpiryDate());
                PickupHeaderV2 createdPickupHeader = orderService.createOutboundOrderProcessingPickupHeaderV9(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                log.info("pickupHeader created: " + createdPickupHeader);

                orderManagementLineV2Repository.updateOrderManagementLineV9(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                        48L, statusDescription, PU_NO, new Date());
            }

            outboundHeaderV2Repository.updateOutboundHeaderStatusV9(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
            orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV9(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
        }
    }


}
