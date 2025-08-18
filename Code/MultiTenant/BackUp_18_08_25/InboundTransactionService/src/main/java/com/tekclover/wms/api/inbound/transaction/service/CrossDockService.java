package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.crossdock.*;
import com.tekclover.wms.api.inbound.transaction.model.dto.*;
import com.tekclover.wms.api.inbound.transaction.model.impl.GrLineImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.notification.NotificationSave;
import com.tekclover.wms.api.inbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
@Slf4j
public class CrossDockService extends BaseService {

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    MastersService mastersService;

    @Autowired
    StrategiesService strategiesService;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    GrLineV2Repository grLineV2Repository;

    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    CrossDockGrLineRepository crossDockGrLineRepository;

    @Autowired
    CrossDockUnallocatedRepository crossDockUnallocatedRepository;

    @Autowired
    StagingLineService stagingLineService;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    InboundQualityHeaderService inboundQualityHeaderService;

    @Autowired
    PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    PutAwayHeaderService putAwayHeaderService;

    @Autowired
    PutAwayLineService putAwayLineService;

    @Autowired
    StorageBinService storageBinService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InboundLineService inboundLineService;

    @Autowired
    PutAwayLineV2Repository putAwayLineV2Repository;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    GrLineService grLineService;

    boolean alreadyExecuted = true;

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param alternateUom
     * @param bagSize
     * @return
     */
    public PickupLineV2 getPickupLineForLastBinCheckV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                       String itemCode, String manufacturerName, String alternateUom, Double bagSize) {
        String directReceiptStorageBin = "REC-AL-B2";   //storage-bin excluding direct stock receipt bin
//        PickupLineV2 pickupLine = pickupLineV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndAlternateUomAndBagSizeAndDeletionIndicatorAndPickedStorageBinNotOrderByPickupConfirmedOnDesc(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, alternateUom, bagSize, 0L, directReceiptStorageBin);
        PickupLineV2 pickupLine = pickupLineV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorAndPickedStorageBinNotOrderByPickupConfirmedOnDesc(
                companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L, directReceiptStorageBin);
        if (pickupLine != null) {
            return pickupLine;
        }
        return null;
    }

    /**
     *
     * @return
     */
    public CrossDock findCrossDockDetails() {

        CrossDock crossDock = new CrossDock();
        log.info("...........CrossDock Find Started..........");
        List<StagingLineEntityV2> stagingLineEntityV2List = stagingLineV2Repository.getStagingLineEntityV2List();

        List<StagingLineEntityV2> finalStagingLineList = new ArrayList<>();

        for (StagingLineEntityV2 stline : stagingLineEntityV2List) {

            String goodsReceiptNo = grLineV2Repository.getGrNumber(stline.getCompanyCode(),stline.getPlantId(),stline.getLanguageId(),stline.getWarehouseId(),stline.getRefDocNumber(),stline.getPreInboundNo(), stline.getPalletCode(), stline.getCaseCode(), stline.getStagingNo());
            stline.setReferenceField10(goodsReceiptNo);

            finalStagingLineList.add(stline);
        }

        log.info("StagingLineList ------------> {}", stagingLineEntityV2List);
        crossDock.setStagingLineEntityV2List(finalStagingLineList);
        List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineV2Repository.getOrderManagementLineV2();
        log.info("OrderManagementLineList --------------> {}", orderManagementLineV2List);
        crossDock.setOrderManagementLineV2List(orderManagementLineV2List);
        log.info("CrossDock Object ----------> {}", crossDock);
        return crossDock;
    }

    /**
     *
     * @param crossDock
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public CrossDockEntity createGrLineCrossDock(CrossDockGrLineOrderManagementLine crossDock, String loginUserID) throws Exception {

        log.info(".....CrossDocking Started .....");

        CrossDockEntity crossDockEntity = new CrossDockEntity();

        List<CrossDockGrLine> crossDockGrLines = new ArrayList<>();

        List<CrossDockUnallocated> crossDockUnallocateds = new ArrayList<>();

        List<GrLineV2> crossDockedGrLine = createGrLineNonCBMV4(crossDock.getGrLineV2List(), crossDock.getOrderManagementLineV2List(), loginUserID);

        log.info("CrossDock Finished and PutawayLine Confirmation Done.........");

        if (!crossDockedGrLine.isEmpty()) {
            for (AddGrLineV2 grLine : crossDock.getGrLineV2List()) {

                CrossDockGrLine crossDockGrLine = new CrossDockGrLine();

                // Generate Cross Dock Number
                String CROSS_DOCK_GR_NO = getNextRangeNumber(26L, grLine.getCompanyCode(), grLine.getPlantId(),
                        grLine.getLanguageId(), grLine.getWarehouseId());

                crossDockGrLine.setCrossDockGrNumber(CROSS_DOCK_GR_NO);

                // Populate from GrLine
                crossDockGrLine.setLanguageId(grLine.getLanguageId());
                crossDockGrLine.setCompanyCode(grLine.getCompanyCode());
                crossDockGrLine.setPlantId(grLine.getPlantId());
                crossDockGrLine.setWarehouseId(grLine.getWarehouseId());
                crossDockGrLine.setPreInboundNo(grLine.getPreInboundNo());
                crossDockGrLine.setRefDocNumber(grLine.getRefDocNumber());
                crossDockGrLine.setGoodsReceiptNo(grLine.getGoodsReceiptNo());
                crossDockGrLine.setPalletCode(grLine.getPalletCode());
                crossDockGrLine.setCaseCode(grLine.getCaseCode());
                crossDockGrLine.setLineNo(grLine.getLineNo());
                crossDockGrLine.setItemCode(grLine.getItemCode());
                crossDockGrLine.setOrderQty(grLine.getOrderQty());
                crossDockGrLine.setGoodReceiptQty(grLine.getGoodReceiptQty());
                crossDockGrLine.setAcceptedQty(grLine.getAcceptedQty());
                crossDockGrLine.setDamageQty(grLine.getDamageQty());
                crossDockGrLine.setQuantityType(grLine.getQuantityType());
                crossDockGrLine.setRemainingQty(grLine.getRemainingQty());
                crossDockGrLine.setReferenceOrderQty(grLine.getReferenceOrderQty());
                crossDockGrLine.setCrossDockAllocationQty(grLine.getCrossDockAllocationQty());
                crossDockGrLine.setStorageQty(grLine.getStorageQty());
                crossDockGrLine.setInventoryQuantity(grLine.getInventoryQuantity());
                crossDockGrLine.setPutAwayNumber(grLine.getPutAwayNumber());
                crossDockGrLine.setDeletionIndicator(0L);
                crossDockGrLine.setNoBags(grLine.getNoBags());

                crossDockGrLines.add(crossDockGrLine);

                crossDockGrLineRepository.save(crossDockGrLine);
            }

            for (OrderManagementLineV2 orderManagementLineV2 : crossDock.getOrderManagementLineV2List()) {

                CrossDockUnallocated crossDockUnallocated = new CrossDockUnallocated();

                // Generate Cross Dock Number
                String CROSS_DOCK_OM_NO = getNextRangeNumber(27L, orderManagementLineV2.getCompanyCodeId(), orderManagementLineV2.getPlantId(),
                        orderManagementLineV2.getLanguageId(), orderManagementLineV2.getWarehouseId());

                crossDockUnallocated.setCrossDockOrderManagementNo(CROSS_DOCK_OM_NO);
                crossDockUnallocated.setLanguageId(orderManagementLineV2.getLanguageId());
                crossDockUnallocated.setCompanyCodeId(orderManagementLineV2.getCompanyCodeId());
                crossDockUnallocated.setPlantId(orderManagementLineV2.getPlantId());
                crossDockUnallocated.setWarehouseId(orderManagementLineV2.getWarehouseId());
                crossDockUnallocated.setPreOutboundNo(orderManagementLineV2.getPreOutboundNo());
                crossDockUnallocated.setRefDocNumber(orderManagementLineV2.getRefDocNumber());
                crossDockUnallocated.setPartnerCode(orderManagementLineV2.getPartnerCode());
                crossDockUnallocated.setLineNumber(orderManagementLineV2.getLineNumber());
                crossDockUnallocated.setItemCode(orderManagementLineV2.getItemCode());
                crossDockUnallocated.setProposedStorageBin(orderManagementLineV2.getProposedStorageBin());
                crossDockUnallocated.setProposedPackBarCode(orderManagementLineV2.getProposedPackBarCode());
                crossDockUnallocated.setBarcodeId(orderManagementLineV2.getBarcodeId());
                crossDockUnallocated.setOrderQty(orderManagementLineV2.getOrderQty());
                crossDockUnallocated.setOrderUom(orderManagementLineV2.getOrderUom());
//                crossDockUnallocated.setInventoryQty(orderManagementLineV2.getInventoryQty());
                crossDockUnallocated.setAllocatedQty(orderManagementLineV2.getAllocatedQty());
                crossDockUnallocated.setReAllocatedQty(orderManagementLineV2.getReAllocatedQty());
                crossDockUnallocated.setDeletionIndicator(0L); // Assuming 0 means active
                crossDockUnallocated.setPickupNumber(orderManagementLineV2.getPickupNumber());
                crossDockUnallocated.setNoBags(orderManagementLineV2.getNoBags());

                crossDockUnallocateds.add(crossDockUnallocated);

                // Save to repository
                crossDockUnallocatedRepository.save(crossDockUnallocated);
            }

            crossDockEntity.setCrossDockGrLines(crossDockGrLines);
            crossDockEntity.setCrossDockOrderManagementLines(crossDockUnallocateds);
        }

        return crossDockEntity;
    }

    /**
     *
     * @param newGrLines
     * @param loginUserID
     * @return
     * @throws Exception
     */
    @Transactional
    public List<GrLineV2> createGrLineNonCBMV4(@Valid List<AddGrLineV2> newGrLines, List<OrderManagementLineV2> orderManagementLineV2List, String loginUserID) throws Exception {

        List<GrLineV2> createdGRLines = new ArrayList<>();
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String goodsReceiptNo = null;
        String itemCode = null;
        String manufacturerName = null;
        String packBarcodes = "99999";

        log.info("Incoming GrLines for Cross Dock ---------------> {}", newGrLines);

        try {
            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {

                if (newGrLine.getPackBarcodes() == null || newGrLine.getPackBarcodes().isEmpty()) {
                    throw new BadRequestException("Enter either Accept Qty or Damage Qty");
                }

                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLineV2 dbGrLine = new GrLineV2();
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setAcceptedQty(grQty);
                        dbGrLine.setDamageQty(0D);
                        log.info("Accept (A)-------->: " + dbGrLine);
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
                        Double grQty = newGrLine.getDamageQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(grQty);
                        dbGrLine.setOrderQty(grQty);
                        dbGrLine.setAcceptedQty(0D);
                        log.info("Damage (D)-------->: " + dbGrLine);
                    }

                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setPackBarcodes(packBarcodes);
                    dbGrLine.setGrUom(newGrLine.getOrderUom());
                    dbGrLine.setStatusId(14L);

                    if (dbGrLine.getGoodReceiptQty() != null && dbGrLine.getGoodReceiptQty() < 0) {
                        throw new BadRequestException("Gr Quantity Cannot be Negative");
                    }
                    if (dbGrLine.getOrderUom() == null) {
                        throw new BadRequestException("Uom is mandatory");
                    }
                    log.info("StatusId: " + newGrLine.getStatusId());
                    if (newGrLine.getStatusId() != null && newGrLine.getStatusId() == 24L) {
                        throw new BadRequestException("GrLine is already Confirmed");
                    }

                    companyCode = dbGrLine.getCompanyCode();
                    plantId = dbGrLine.getPlantId();
                    languageId = dbGrLine.getLanguageId();
                    warehouseId = dbGrLine.getWarehouseId();
                    refDocNumber = dbGrLine.getRefDocNumber();
                    preInboundNo = dbGrLine.getPreInboundNo();
//                    goodsReceiptNo = grLineV2Repository.getGrNumber(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, dbGrLine.getPalletCode(), dbGrLine.getCaseCode(), dbGrLine.getStagingNo());
                    goodsReceiptNo = dbGrLine.getGoodsReceiptNo();
                    itemCode = dbGrLine.getItemCode();
                    manufacturerName = dbGrLine.getManufacturerName();

                    //GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty > OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(
                            companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, newGrLine.getPalletCode(),
                            newGrLine.getCaseCode(), itemCode, manufacturerName, newGrLine.getLineNo(), dbGrLine.getGoodReceiptQty());
                    log.info("dbGrQty+newGrQty, OrdQty: " + dbGrQty + ", " + newGrLine.getOrderQty());
                    if (dbGrQty != null) {
//                        if (newGrLine.getOrderQty() < dbGrQty) {
//                            throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
//                        }
                    }

                    log.info("getDescription started");
                    description = getDescription(companyCode, plantId, languageId, warehouseId);
                    log.info("getDescription ----> {}", description);
                    if (description != null) {
                        dbGrLine.setCompanyDescription(description.getCompanyDesc());
                        dbGrLine.setPlantDescription(description.getPlantDesc());
                        dbGrLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    Double recAcceptQty = 0D;
                    Double recDamageQty = 0D;
                    Double variance = 0D;
                    Double invoiceQty = newGrLine.getOrderQty() != null ? newGrLine.getOrderQty() : 0D;
                    Double acceptQty = newGrLine.getAcceptedQty() != null ? newGrLine.getAcceptedQty() : 0D;
                    Double damageQty = newGrLine.getDamageQty() != null ? newGrLine.getDamageQty() : 0D;

                    log.info("dbStagingLineEntity started");
                    StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber, newGrLine.getLineNo(),
                            itemCode, manufacturerName);
                    log.info("StagingLine: " + dbStagingLineEntity);

                    if(dbStagingLineEntity == null) {
                        dbStagingLineEntity = createLines(dbGrLine, loginUserID);
                    }

                    if (dbStagingLineEntity != null) {
                        if (dbStagingLineEntity.getRec_accept_qty() != null) {
                            recAcceptQty = dbStagingLineEntity.getRec_accept_qty();
                        }
                        if (dbStagingLineEntity.getRec_damage_qty() != null) {
                            recDamageQty = dbStagingLineEntity.getRec_damage_qty();
                        }
                        if(newGrLine.getAlternateUom() == null || newGrLine.getBagSize() == null || newGrLine.getAlternateUom().isBlank()) {
                            dbGrLine.setAlternateUom(dbStagingLineEntity.getAlternateUom());
                            dbGrLine.setNoBags(dbStagingLineEntity.getNoBags());
                            dbGrLine.setBagSize(dbStagingLineEntity.getBagSize());
                            dbGrLine.setMrp(dbStagingLineEntity.getMrp());
                            dbGrLine.setItemType(dbStagingLineEntity.getItemType());
                            dbGrLine.setItemGroup(dbStagingLineEntity.getItemGroup());
                            dbGrLine.setSize(dbStagingLineEntity.getSize());
                            dbGrLine.setBrand(dbStagingLineEntity.getBrand());
                        }
                    }
                    //Calculate No of Bags for Damage Qty
                    if(dbGrLine.getQuantityType().equalsIgnoreCase("D")) {
//                        double actualQty = getQuantity(dbGrLine.getGoodReceiptQty(), dbGrLine.getBagSize());
                        double actualQty = dbGrLine.getGoodReceiptQty();
                        double NO_OF_BAGS = actualQty / dbGrLine.getBagSize();
                        dbGrLine.setNoBags(roundUp(NO_OF_BAGS));
                    }

                    variance = invoiceQty - (acceptQty + damageQty + recAcceptQty + recDamageQty);
                    log.info("Variance: " + variance);

                    if (variance == 0D) {
                        dbGrLine.setStatusId(17L);
                    }
                    statusDescription = getStatusDescription(dbGrLine.getStatusId(), languageId);
                    dbGrLine.setStatusDescription(statusDescription);

                    if (variance < 0D) {
                        throw new BadRequestException("Variance Qty cannot be Less than 0");
                    }
                    dbGrLine.setConfirmedQty(dbGrLine.getGoodReceiptQty());
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(loginUserID);
                    dbGrLine.setUpdatedBy(loginUserID);
                    dbGrLine.setConfirmedBy(loginUserID);
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
                    dbGrLine.setConfirmedOn(new Date());

                    List<GrLineV2> oldGrLine = grLineV2Repository.findByGoodsReceiptNoAndItemCodeAndLineNoAndLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndWarehouseIdAndPreInboundNoAndCaseCodeAndCreatedOnAndDeletionIndicator(
                            goodsReceiptNo, itemCode, dbGrLine.getLineNo(), languageId, companyCode, plantId,
                            refDocNumber, dbGrLine.getPackBarcodes(), warehouseId, preInboundNo,
                            dbGrLine.getCaseCode(), dbGrLine.getCreatedOn(), 0L);
                    GrLineV2 createdGRLine = null;
                    boolean createGrLineError = false;
                    //validate to check if grline is already exists
                    if (oldGrLine == null || oldGrLine.isEmpty()) {
                        // Lead Time
                        GrLineImpl implForLeadTime = grLineV2Repository.getLeadTime(languageId, companyCode, plantId, warehouseId, goodsReceiptNo, new Date());
                        if (implForLeadTime != null) {
                            if (!implForLeadTime.getDiffDays().equals("00")) {
                                String leadTime = implForLeadTime.getDiffDays() + "Days: " + implForLeadTime.getDiffHours() + "Hours: "
                                        + implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else if (!implForLeadTime.getDiffHours().equals("00")) {
                                String leadTime = implForLeadTime.getDiffHours() + "Hours: " + implForLeadTime.getDiffMinutes()
                                        + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else if (!implForLeadTime.getDiffMinutes().equals("00")) {
                                String leadTime = implForLeadTime.getDiffMinutes() + "Minutes: " + implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            } else {
                                String leadTime = implForLeadTime.getDiffSeconds() + "Seconds";
                                dbGrLine.setReferenceField10(leadTime);
                            }
                        }

                        try {
                            createdGRLine = grLineV2Repository.save(dbGrLine);
                        } catch (Exception e) {
                            throw e;
                        }
                        log.info("createdGRLine : " + createdGRLine);
                        createdGRLines.add(createdGRLine);
                    }
                }

                log.info("Records were inserted successfully...");
            }

            if (createdGRLines != null) {
                createPutAwayHeaderNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, preInboundNo, refDocNumber, createdGRLines, orderManagementLineV2List, loginUserID);
            }

            //GrHeader Status 17 Updating Using Stored Procedure when condition met - multiple procedure combined to single procedure
            statusDescription = stagingLineV2Repository.getStatusDescription(17L, languageId);
            grHeaderV2Repository.updateStatusProc(
                    companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, goodsReceiptNo, 17L, statusDescription, new Date());
            log.info("Status Update Using Stored Procedure ---> GrHeader, StagingLine, InboundLine");

            return createdGRLines;
        } catch (Exception e) {
            throw new BadRequestException("Error while Creating CrossDock GrLines");
        }
    }

    /**
     *
     * @param createdGRLines
     * @param loginUserID
     * @throws Exception
     */
    private void createPutAwayHeaderNonCBMV4(String company, String plant, String language,
                                             String warehouse, String item, String manufactureName,
                                             String preInbound, String refDocNo,
                                             List<GrLineV2> createdGRLines, List<OrderManagementLineV2> orderManagementLineV2List, String loginUserID) throws Exception {

        try {

            String idMasterToken = getIDMasterAuthToken();
            //PA_NO
            NUMBER_RANGE_CODE = 7L;
            String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);

            log.info("PA number ----------------> {}", nextPANumber);

            for (GrLineV2 createdGRLine : createdGRLines) {

                // Setting params
                String languageId = createdGRLine.getLanguageId();
                String companyCode = createdGRLine.getCompanyCode();
                String plantId = createdGRLine.getPlantId();
                String warehouseId = createdGRLine.getWarehouseId();
                String itemCode = createdGRLine.getItemCode();
                String manufacturerName = createdGRLine.getManufacturerName();
                String preInboundNo = createdGRLine.getPreInboundNo();
                String refDocNumber = createdGRLine.getRefDocNumber();


                String proposedStorageBin = createdGRLine.getInterimStorageBin();
                String alternateUom = createdGRLine.getAlternateUom();
                Double bagSize = createdGRLine.getBagSize();

                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                storageBinPutAway.setCompanyCodeId(companyCode);
                storageBinPutAway.setPlantId(plantId);
                storageBinPutAway.setLanguageId(languageId);
                storageBinPutAway.setWarehouseId(warehouseId);

                //  ASS_HE_NO
                if (createdGRLine != null) {
                    // Insert record into PutAwayHeader
                    PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                    BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                    putAwayHeader.setCompanyCodeId(companyCode);
                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                    //Inbound Quality Number
                    NUMBER_RANGE_CODE = 23L;
                    String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
                    log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);

                    //-----------------PROP_ST_BIN---------------------------------------------

                    //V2 Code
                    Long binClassId = 0L;                   //actual code follows
                    if (createdGRLine.getInboundOrderTypeId() == null) {
                        throw new BadRequestException("inbound order type id cannot be null");
                    }
                    if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 ||
                            createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 ||
                            createdGRLine.getInboundOrderTypeId() == 6 || createdGRLine.getInboundOrderTypeId() == 7) {
                        binClassId = 1L;
                    }
                    if (createdGRLine.getInboundOrderTypeId() == 2) {
                        binClassId = 7L;
                    }
                    log.info("BinClassId : " + binClassId);

//                String packBarCode = createdGRLine.getBatchSerialNumber() != null ? createdGRLine.getBatchSerialNumber() : "99999";
//                List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, createdGRLine.getBarcodeId(), createdGRLine.getBatchSerialNumber(), packBarCode, alternateUom, bagSize, binClassId);
                    List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, alternateUom, bagSize, binClassId);
//                    log.info("Inventory StorageBin List: " + inventoryStorageBinList.size() + " | ----> " + inventoryStorageBinList);

                    if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
                        storageBinPutAway.setBinClassId(binClassId);
                        storageBinPutAway.setBin(proposedStorageBin);
                        StorageBinV2 storageBin = null;
                        try {
                            log.info("getStorageBin Input: " + storageBinPutAway);
                            storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
                        } catch (Exception e) {
                            throw new BadRequestException("Invalid StorageBin");
                        }
                        log.info("InterimStorageBin: " + storageBin);
                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                        if (storageBin != null) {
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                        }
                        if (storageBin == null) {
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        }
                    }
                    //BinClassId - 7 - Return Order(Sale Return)
                    if (createdGRLine.getInboundOrderTypeId() == 2) {
                        storageBinPutAway.setBinClassId(binClassId);
                        log.info("BinClassId : " + binClassId);

                        StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
                        if (proposedBin != null) {
                            putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                            log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
                        }
                    }

                    if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                        log.info("BinClassId : " + binClassId);
                        if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                            if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                                storageBinPutAway.setBinClassId(binClassId);
                                storageBinPutAway.setStorageBin(inventoryStorageBinList);

                                StorageBinV2 proposedExistingBin = storageBinService.getExistingProposedStorageBinNonCBM(storageBinPutAway);
                                if (proposedExistingBin != null) {
                                    proposedStorageBin = proposedExistingBin.getStorageBin();
                                    log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);

                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
                                }
                                log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
                            }
                            if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
                                storageBinPutAway.setBinClassId(7L);
                                StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
                                if (proposedBin != null) {
                                    putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                                    putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                                    log.info("Damage Qty --> Proposed Bin: " + proposedBin.getStorageBin());
                                }
                            }
                        }
                    }

                    //Last Picked Bin as Proposed Bin If it is empty
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if (putAwayHeader.getProposedStorageBin() == null && (inventoryStorageBinList == null || inventoryStorageBinList.isEmpty())) {

                        PickupLineV2 pickupLineList = getPickupLineForLastBinCheckV4(companyCode, plantId, languageId, warehouseId,
                                itemCode, manufacturerName, alternateUom, bagSize);
                        log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
                        if (pickupLineList != null) {
                            putAwayHeader.setProposedStorageBin(pickupLineList.getPickedStorageBin());
                            putAwayHeader.setLevelId(pickupLineList.getLevelId());
                            log.info("LastPick NonCBM Bin: " + pickupLineList.getPickedStorageBin());
                            log.info("LastPick NonCBM PutawayQty: " + createdGRLine.getGoodReceiptQty());
                        }
                    }

                    //Propose Empty Bin if Last picked bin is unavailable
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if (putAwayHeader.getProposedStorageBin() == null && (inventoryStorageBinList == null || inventoryStorageBinList.isEmpty())) {
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
                            PutAwayLineV2 existingBinPutAwayLineItemCheck = putAwayLineService.getPutAwayLineExistingItemCheckV4(companyCode, plantId, languageId, warehouseId,
                                    itemCode, manufacturerName, alternateUom, bagSize);
                            log.info("existingBinPutAwayLineItemCheck: " + existingBinPutAwayLineItemCheck);
                            if (existingBinPutAwayLineItemCheck != null) {
                                proposedStorageBin = existingBinPutAwayLineItemCheck.getConfirmedStorageBin();
                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                if (existingBinPutAwayLineItemCheck.getLevelId() != null) {
                                    putAwayHeader.setLevelId(String.valueOf(existingBinPutAwayLineItemCheck.getLevelId()));
                                } else {
                                    storageBinPutAway.setBin(proposedStorageBin);
                                    StorageBinV2 getLevelIdForProposedBin = storageBinService.getaStorageBinV2(storageBinPutAway);
                                    if (getLevelIdForProposedBin != null) {
                                        putAwayHeader.setLevelId(String.valueOf(getLevelIdForProposedBin.getFloorId()));
                                    }
                                }
                                log.info("Existing PutAwayCreate ProposedStorageBin from putAway line-->A : " + proposedStorageBin);
                            }
                            //Checking proposed bin in putAway header for that item
                            if (putAwayHeader.getProposedStorageBin() == null) {
                                PutAwayHeaderV2 existingBinItemCheck = putAwayHeaderService.getPutawayHeaderExistingBinItemCheckV4(companyCode, plantId, languageId, warehouseId,
                                        itemCode, manufacturerName, alternateUom, bagSize);
                                log.info("existingBinItemCheck: " + existingBinItemCheck);
                                if (existingBinItemCheck != null) {
                                    proposedStorageBin = existingBinItemCheck.getProposedStorageBin();
                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(existingBinItemCheck.getLevelId()));
                                    log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                                }
                            }
                            List<String> existingBinCheck = putAwayHeaderService.getPutawayHeaderExistingBinCheckV4(companyCode, plantId, languageId, warehouseId);
                            log.info("existingBinCheck: " + existingBinCheck);
                            if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck != null && !existingBinCheck.isEmpty())) {
                                storageBinPutAway.setStorageBin(existingBinCheck);
                                proposedNonCbmStorageBin = storageBinService.getProposedStorageBinNonCBM(storageBinPutAway);
                                if (proposedNonCbmStorageBin != null) {
                                    proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                    log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
                                    log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
                                }
                            }
                            if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck == null || existingBinCheck.isEmpty() || existingBinCheck.size() == 0)) {
                                List<String> existingProposedPutawayStorageBin = putAwayHeaderService.getPutawayHeaderExistingBinCheckV4(companyCode, plantId, languageId, warehouseId);
                                log.info("existingProposedPutawayStorageBin: " + existingProposedPutawayStorageBin);
                                log.info("BinClassId: " + binClassId);
                                storageBinPutAway.setStorageBin(existingProposedPutawayStorageBin);
                                proposedNonCbmStorageBin = storageBinService.getProposedStorageBinNonCBM(storageBinPutAway);
                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin);
                                if (proposedNonCbmStorageBin != null) {
                                    proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
                                    log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
                                }
                                if (proposedNonCbmStorageBin == null) {
                                    StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
                                    log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
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
                            StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
                            if (proposedBin != null) {
                                putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                                log.info("D --> Proposed Bin: " + proposedBin.getStorageBin());
                            }
                        }
                    }
                    if (putAwayHeader.getProposedStorageBin() == null) {
                        StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
                        log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                    log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                    log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber);
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

                    //PROP_HE_NO	<- PAWAY_HE_NO
                    putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                    putAwayHeader.setReferenceField5(itemCode);
                    putAwayHeader.setReferenceField6(manufacturerName);
                    putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                    putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                    putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                    Long statusId = 19L;
                    putAwayHeader.setStatusId(statusId);
                    statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
                    putAwayHeader.setStatusDescription(statusDescription);

                    putAwayHeader.setDeletionIndicator(0L);
                    putAwayHeader.setPackBarcodes(createdGRLine.getBarcodeId());
                    putAwayHeader.setCreatedBy(loginUserID);
                    putAwayHeader.setUpdatedBy(loginUserID);
                    putAwayHeader.setCreatedOn(new Date());
                    putAwayHeader.setUpdatedOn(new Date());
                    putAwayHeader.setConfirmedOn(new Date());
                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                    log.info("putAwayHeader : " + putAwayHeader);

                    // Updating Grline field -------------> PutAwayNumber
                    log.info("Updation of PutAwayNumber on GrLine Started");
                    putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
                            putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
                            putAwayHeader.getPutAwayNumber());

                    log.info("Updation of PutAwayNumber on GrLine Completed");

                    List<PutAwayLineV2> createdPutawayLine = null;

                    log.info("Putaway line Confirm Initiated");
                    List<PutAwayLineV2> createPutawayLine = new ArrayList<>();

                    PutAwayLineV2 putAwayLine = new PutAwayLineV2();

                    BeanUtils.copyProperties(createdGRLine, putAwayLine, CommonUtils.getNullPropertyNames(createdGRLine));
                    putAwayLine.setProposedStorageBin(putAwayHeader.getProposedStorageBin());
                    putAwayLine.setConfirmedStorageBin(putAwayHeader.getProposedStorageBin());
                    putAwayLine.setPutawayConfirmedQty(putAwayHeader.getPutAwayQuantity());
                    putAwayLine.setPutAwayNumber(putAwayHeader.getPutAwayNumber());
                    createPutawayLine.add(putAwayLine);

//                    createdPutawayLine = putAwayLineService.putAwayLineConfirmNonCBMV4(createPutawayLine, loginUserID);

                    createdPutawayLine = putAwayLineConfirmForNoStock(createPutawayLine, orderManagementLineV2List, createdGRLine,
                            companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, loginUserID);
                    log.info("PutawayLine Confirmed: " + createdPutawayLine);

                    /*----------------Inventory tables Create---------------------------------------------*/
//                    createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

                    //bypass quality header and line
                    inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
                }
            }
        } catch (Exception e) {
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws Exception
     */
    public List<PutAwayLineV2> putAwayLineConfirmForNoStock(@Valid List<PutAwayLineV2> newPutAwayLines, List<OrderManagementLineV2> orderManagementLineV2List,
                                                            GrLineV2 createdGRLine, String companyCode, String plantId, String languageId, String warehouseId,
                                                            String itemCode, String manufacturerName, String refDocNumber, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, Exception {

        log.info("PutAwayLine confirm started ------------------------------------> " + newPutAwayLines);
        List<PutAwayLineV2> putAwayLineV2 = putAwayLineConfirmNonCBMV4(newPutAwayLines, loginUserID, manufacturerName, createdGRLine);

        crossDockOrderManagementLine(orderManagementLineV2List, loginUserID);

        return  putAwayLineV2;
    }

    /**
     *
     * @param orderManagementLineV2List
     * @param loginUserID
     */
    public void crossDockOrderManagementLine(List<OrderManagementLineV2> orderManagementLineV2List, String loginUserID) {
        Long BIN_CLASS_ID;
        BIN_CLASS_ID = 1L;

//            List<OrderManagementLineV2> orderManagementLineList = orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndStatusIdAndDeletionIndicator(
//                    pa.getCompanyCode(), pa.getPlantId(), pa.getLanguageId(), pa.getWarehouseId(), pa.getItemCode(), 47L, 0L);
        log.info("OrderManagement Line Records --------> {}", orderManagementLineV2List);
        if (!orderManagementLineV2List.isEmpty()) {
            orderManagementLineV2List.stream().forEach(order -> {
                try {
                    log.info("OrderManagement Allocation is Starting ----------------------------> ");
                    OrderManagementLineV2 orderManagementLine = updateAllocationV4(order.getCompanyCodeId(), order.getPlantId(), order.getLanguageId(), order.getWarehouseId(), order.getItemCode(),
                            order.getManufacturerName(), BIN_CLASS_ID, order.getOrderQty(), order, loginUserID);
//                        OrderManagementLineV2 orderManagementLine = doAllocationV2(order.getCompanyCodeId(), order.getPlantId(), order.getLanguageId(), order.getWarehouseId(), order.getPreOutboundNo(),
//                                order.getRefDocNumber(), order.getPartnerCode(), order.getLineNumber(), order.getItemCode(), loginUserID, order);
                    log.info("OrderManagementLine is confirmed ------------->  " + orderManagementLine);

                    log.info("PickupHeader Created Started ---------------------------->" + orderManagementLine);
                    createPickupHeaderNo(order.getCompanyCodeId(), order.getPlantId(), order.getLanguageId(), order.getWarehouseId(), order.getPreOutboundNo(),
                            order.getRefDocNumber(), orderManagementLine);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Creating PickupHeader Number for Incoming Orders (ie., Request)
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public void createPickupHeaderNo(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                     String refDocNumber, OrderManagementLineV2 orderManagementLineV2) throws Exception {

//        List<OrderManagementLineV2> orderManagementLines = orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicatorAndStatusIdNot(
//                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L, 47L);

//        log.info("OrderManagementList for PickupHeader -------------> {}", orderManagementLines.size());

        long NUM_RAN_CODE = 10;
        String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
        log.info("----------New PU_NO--------> : " + PU_NO);

//        if (orderManagementLines != null && !orderManagementLines.isEmpty()) {
//            for (OrderManagementLineV2 orderManagementLine : orderManagementLines) {
        PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
        log.info("OrderMangementLine----->" +orderManagementLineV2);
        BeanUtils.copyProperties(orderManagementLineV2, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLineV2));
        newPickupHeader.setPickupNumber(PU_NO);
        newPickupHeader.setPickToQty(orderManagementLineV2.getAllocatedQty() != null ? orderManagementLineV2.getAllocatedQty() : 0);
        newPickupHeader.setPickUom(orderManagementLineV2.getOrderUom() != null ? orderManagementLineV2.getOrderUom() : "");
        newPickupHeader.setBarcodeId(orderManagementLineV2.getBarcodeId() != null ? orderManagementLineV2.getBarcodeId() : "");

        // STATUS_ID
        newPickupHeader.setStatusId(48L);
        statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
        newPickupHeader.setStatusDescription(statusDescription);

        // ProposedPackbarcode
        newPickupHeader.setProposedPackBarCode(orderManagementLineV2.getProposedPackBarCode() != null ? orderManagementLineV2.getProposedPackBarCode() : "");

        //Setting InventoryQuantity from orderManagementLineV2
        newPickupHeader.setInventoryQuantity(orderManagementLineV2.getInventoryQty());

        // REF_FIELD_1
        newPickupHeader.setReferenceField1(orderManagementLineV2.getReferenceField1());
        newPickupHeader.setReferenceField5(orderManagementLineV2.getDescription());
        newPickupHeader.setBatchSerialNumber(orderManagementLineV2.getProposedBatchSerialNumber());
        newPickupHeader.setManufacturerCode(orderManagementLineV2.getManufacturerCode());
        newPickupHeader.setManufacturerName(orderManagementLineV2.getManufacturerName());
        newPickupHeader.setManufacturerPartNo(orderManagementLineV2.getManufacturerPartNo());
        newPickupHeader.setSalesOrderNumber(orderManagementLineV2.getRefDocNumber());
        newPickupHeader.setPickListNumber(orderManagementLineV2.getPickListNumber() != null ? orderManagementLineV2.getPickListNumber() : "");
        newPickupHeader.setSalesInvoiceNumber(orderManagementLineV2.getSalesInvoiceNumber());
        newPickupHeader.setOutboundOrderTypeId(orderManagementLineV2.getOutboundOrderTypeId());
        newPickupHeader.setReferenceDocumentType(orderManagementLineV2.getReferenceDocumentType() != null ? orderManagementLineV2.getReferenceDocumentType() : "");
        newPickupHeader.setSupplierInvoiceNo(orderManagementLineV2.getSupplierInvoiceNo());
        newPickupHeader.setTokenNumber(orderManagementLineV2.getTokenNumber() != null ? orderManagementLineV2.getTokenNumber() : "");
        newPickupHeader.setLevelId(orderManagementLineV2.getLevelId());
        newPickupHeader.setTargetBranchCode(orderManagementLineV2.getTargetBranchCode() != null ? orderManagementLineV2.getTargetBranchCode() : "");
        newPickupHeader.setLineNumber(orderManagementLineV2.getLineNumber());
//                newPickupHeader.setFromBranchCode(orderManagementLineV2.getFromBranchCode());
        newPickupHeader.setIsCompleted(orderManagementLineV2.getIsCompleted() != null ? orderManagementLineV2.getIsCompleted() : "");
        newPickupHeader.setIsCancelled(orderManagementLineV2.getIsCancelled() != null ? orderManagementLineV2.getIsCancelled() : "");
        newPickupHeader.setMUpdatedOn(orderManagementLineV2.getPickupUpdatedOn() != null ? orderManagementLineV2.getPickupUpdatedOn() : new Date());
        newPickupHeader.setStorageSectionId(orderManagementLineV2.getStorageSectionId());

        log.info("Outbound order processing for pickup header started");

        PickupHeaderV2 createdPickupHeader = createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLineV2.getPickupCreatedBy());
        log.info("pickupHeader created: " + createdPickupHeader);

        orderManagementLineV2Repository.updateOrderManagementLineV6(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                orderManagementLineV2.getLineNumber(), orderManagementLineV2.getItemCode(),
                48L, statusDescription, PU_NO, new Date());
//            }

        log.info("Outbound header status update to Status 48 Started... ");
        log.info("The Input values are : companyCodeId: " + companyCodeId + " and plantId: " + plantId + " and languageId: " +
                languageId + " and warehouseId: " + warehouseId + " and preOutboundNo: " + preOutboundNo + " and statusDescription: " + statusDescription);
        outboundHeaderV2Repository.updateOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
        log.info("Outbound header status update to Status 48 completed... ");
        log.info("OrderManagement header status update to Status 48 Started... ");
        orderManagementHeaderV2Repository.updateOrderManagementHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, 48L, statusDescription);
        log.info("OrderManagement header status update to Status 48 completed... ");
//        }
    }

    /**
     * @param newPickupHeader
     * @param loginUserID
     * @return
     */
    public PickupHeaderV2 createOutboundOrderProcessingPickupHeaderV2(PickupHeaderV2 newPickupHeader, String loginUserID) throws Exception {
        try {
            Optional<PickupHeaderV2> duplicateCheck = pickupHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndBarcodeIdAndDeletionIndicator(
                    newPickupHeader.getCompanyCodeId(), newPickupHeader.getPlantId(), newPickupHeader.getLanguageId(),
                    newPickupHeader.getWarehouseId(), newPickupHeader.getPreOutboundNo(), newPickupHeader.getRefDocNumber(),
                    newPickupHeader.getPartnerCode(), newPickupHeader.getPickupNumber(), newPickupHeader.getLineNumber(),
                    newPickupHeader.getItemCode(), newPickupHeader.getProposedStorageBin(), newPickupHeader.getProposedPackBarCode(),
                    newPickupHeader.getBarcodeId(), 0L);
            log.info("duplicate pickup header : " + duplicateCheck);
            if (duplicateCheck.isEmpty()) {
                PickupHeaderV2 dbPickupHeader = new PickupHeaderV2();
                log.info("newPickupHeader : " + newPickupHeader);
                BeanUtils.copyProperties(newPickupHeader, dbPickupHeader, CommonUtils.getNullPropertyNames(newPickupHeader));

                if(dbPickupHeader.getCompanyDescription() == null || dbPickupHeader.getPlantDescription() == null || dbPickupHeader.getWarehouseDescription() == null) {
                    description = getDescription(dbPickupHeader.getCompanyCodeId(), dbPickupHeader.getPlantId(), dbPickupHeader.getLanguageId(), dbPickupHeader.getWarehouseId());
                    dbPickupHeader.setCompanyDescription(description.getCompanyDesc());
                    dbPickupHeader.setPlantDescription(description.getPlantDesc());
                    dbPickupHeader.setWarehouseDescription(description.getWarehouseDesc());
                }

                if (dbPickupHeader.getStatusId() != null) {
                    statusDescription = stagingLineV2Repository.getStatusDescription(dbPickupHeader.getStatusId(), dbPickupHeader.getLanguageId());
                    dbPickupHeader.setStatusDescription(statusDescription);
                }


                statusDescription = stagingLineV2Repository.getStatusDescription(48L, dbPickupHeader.getLanguageId());
                outboundLineV2Repository.updateOutboundLineV2(dbPickupHeader.getCompanyCodeId(),
                        dbPickupHeader.getPlantId(),
                        dbPickupHeader.getLanguageId(),
                        dbPickupHeader.getWarehouseId(),
                        dbPickupHeader.getPreOutboundNo(),
                        dbPickupHeader.getRefDocNumber(),
                        dbPickupHeader.getPartnerCode(),
                        dbPickupHeader.getLineNumber(),
                        dbPickupHeader.getItemCode(),
                        48L,
                        statusDescription,
                        dbPickupHeader.getAssignedPickerId(),
                        dbPickupHeader.getManufacturerName(),
                        loginUserID,
                        new Date());

                dbPickupHeader.setDeletionIndicator(0L);
                dbPickupHeader.setPickupCreatedBy(loginUserID);
                dbPickupHeader.setPickupCreatedOn(new Date());
                PickupHeaderV2 pickupHeaderV2 = pickupHeaderV2Repository.save(dbPickupHeader);

                return pickupHeaderV2;
            }
            return newPickupHeader;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 updateAllocationV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                    OrderManagementLineV2 orderManagementLine, String loginUserID) throws Exception {

        if (orderManagementLine.getStatusId() == 42L || orderManagementLine.getStatusId() == 43L || orderManagementLine.getStatusId() == 48L) {

            log.info("CrossDock Unallocation for incoming orderManagementLine started.....");

            orderManagementLine = doUnAllocationV4(companyCodeId, plantId, languageId, warehouseId, orderManagementLine.getPreOutboundNo(),
                    orderManagementLine.getRefDocNumber(), orderManagementLine.getPartnerCode(), orderManagementLine.getLineNumber(), orderManagementLine.getItemCode(),
                    orderManagementLine.getProposedStorageBin(), orderManagementLine.getProposedPackBarCode(), loginUserID);

        }

        String masterToken = getMasterAuthToken();
        String alternateUom = orderManagementLine.getAlternateUom();
        Long stockTypeId = 1L;
        String orderBy = null;
        String INV_STRATEGY = null;

        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(orderManagementLine.getCompanyCodeId());
        imBasicData.setPlantId(orderManagementLine.getPlantId());
        imBasicData.setLanguageId(orderManagementLine.getLanguageId());
        imBasicData.setWarehouseId(orderManagementLine.getWarehouseId());
        imBasicData.setItemCode(itemCode);
        ImBatchSerial imBatchSerial = mastersService.getImBatchSerialV2(imBasicData, masterToken);

        if (imBatchSerial != null) {
            Strategies strategies = strategiesService.getStrategies(companyCodeId, languageId, plantId, warehouseId, 2L, imBatchSerial.getSequenceIndicator());           //Outbound - Strategy type - 2; Inbound - Strategy type - 1
            if (strategies != null && strategies.getPriority1() != null) {
                INV_STRATEGY = String.valueOf(strategies.getPriority1());
            }
        }

        // Inventory Strategy Choices
        if (INV_STRATEGY == null) {
            INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
        }

        boolean shelfLifeIndicator = false;
        imBasicData.setManufacturerName(manufacturerName);
        ImBasicData1 dbImBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, masterToken);
        log.info("ImBasicData1: " + dbImBasicData1);
        if (dbImBasicData1 != null) {
            if (dbImBasicData1.getShelfLifeIndicator() != null) {
                shelfLifeIndicator = dbImBasicData1.getShelfLifeIndicator();
            }
        }
        log.info("Allocation Strategy: " + INV_STRATEGY);
        log.info("shelfLifeIndicator: " + shelfLifeIndicator);

        OrderManagementLineV2 newOrderManagementLine = null;
        int invQtyByLevelIdCount = 0;
        int invQtyGroupByLevelIdCount = 0;
        List<IInventoryImpl> stockType1InventoryList =
                inventoryService.getInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList.size());

        if ((stockType1InventoryList == null || stockType1InventoryList.isEmpty()) && orderManagementLine.getStatusId() != 47L) {
            return updateOrderManagementLineV2(orderManagementLine);
        }

        // Getting Inventory GroupBy ST_BIN wise
        List<IInventoryImpl> finalInventoryList = null;
//        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
//            log.info("INV_STRATEGY: " + INV_STRATEGY + shelfLifeIndicator);
//            if(!shelfLifeIndicator) {
//                orderBy = "iv.UTD_ON";
//                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByCreatedOnV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            } else {
//                orderBy = "iv.EXP_DATE";
//                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByExpiryDateV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            }
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID")) { // SB_LEVEL_ID
//            log.info("INV_STRATEGY: " + INV_STRATEGY);
//            orderBy = "iv.LEVEL_ID";
//            finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stockTypeId, binClassId, alternateUom);
//        }
//        if (INV_STRATEGY.equalsIgnoreCase("1")) { // FIFO
//            log.info("FIFO");
//            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementByBatchV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                    manufacturerName, stockTypeId, binClassId, alternateUom);
//            log.info("Group By Batch: " + levelIdList.size());
//            List<String> invQtyByLevelIdList = new ArrayList<>();
//            boolean toBeIncluded = true;
//            for (IInventory iInventory : levelIdList) {
//                log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());
//                if (ORD_QTY <= iInventory.getInventoryQty()) {
//                    orderBy = "iv.STR_NO";
//                    finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByBatchV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                            manufacturerName, stockTypeId, binClassId, alternateUom);
//                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
//                    newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
//                            binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
//                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
//                    return newOrderManagementLine;
//                }
//                if (ORD_QTY > iInventory.getInventoryQty()) {
//                    toBeIncluded = false;
//                }
//                if (!toBeIncluded) {
//                    invQtyByLevelIdList.add("True");
//                }
//            }
//            invQtyByLevelIdCount = levelIdList.size();
//            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
//            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
//            if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
//                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
//                return newOrderManagementLine;
//            }
//            if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
//                orderBy = "iv.LEVEL_ID";
//                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stockTypeId, binClassId, alternateUom);
//            }
//        }
        if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
            log.info("INV_STRATEGY: " + INV_STRATEGY);
            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    stockTypeId, binClassId, manufacturerName, alternateUom);

            log.info("The Given Values for getting InventoryQty : companyCodeId ---> " + companyCodeId + " plantId ----> " + plantId + " languageId ----> " + languageId +
                    ", warehouseId -----> " + warehouseId + "itemCode -----> " + itemCode + " refDocumentNo -----> " + orderManagementLine.getRefDocNumber() + " barcodeId -------> " + orderManagementLine.getBarcodeId());

//            Double INV_QTY = inventoryV2Repository.getCurrentCaseQty(companyCodeId, plantId, languageId, warehouseId, itemCode, orderManagementLine.getRefDocNumber(), orderManagementLine.getBarcodeId());

            Double INV_QTY = inventoryV2Repository.getInvCaseQty2(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, ORD_QTY);
            log.info("Queried invQty2 ----------> {}", INV_QTY);

            log.info("Group By LeveId: " + levelIdList.size());
            List<String> invQtyByLevelIdList = new ArrayList<>();
            boolean toBeIncluded = true;
            for (IInventory iInventory : levelIdList) {
                log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());

                log.info("Order Qty --------> {}", ORD_QTY);
                log.info("BagSize ------------> {}", orderManagementLine.getBagSize());
                if (ORD_QTY.equals(INV_QTY)) {
                    log.info("Closed Case Allocation started !!");
                    newOrderManagementLine = fullInvQtyAllocation(iInventory, companyCodeId, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, stockTypeId, binClassId, alternateUom, loginUserID, ORD_QTY, orderManagementLine);
                    return newOrderManagementLine;
                } else if (ORD_QTY < iInventory.getInventoryQty()) {
                    orderBy = "iv.LEVEL_ID";
                    finalInventoryList = inventoryService.getInventoryForOrderManagementLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, stockTypeId, binClassId, alternateUom,
                            iInventory.getLevelId());
                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
                    newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                            binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                    return newOrderManagementLine;
                }
                if (ORD_QTY > iInventory.getInventoryQty()) {
                    toBeIncluded = false;
                }
                if (!toBeIncluded) {
                    invQtyByLevelIdList.add("True");
                }
            }
            invQtyByLevelIdCount = levelIdList.size();
            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
            if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                return newOrderManagementLine;
            }
            if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
                orderBy = "iv.LEVEL_ID";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
            }
        }
        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");

        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
        if ((finalInventoryList != null && finalInventoryList.isEmpty()) && orderManagementLine.getStatusId() != 47L) {
            return updateOrderManagementLineV2(orderManagementLine);
        }

        newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);

        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     *
     * @param iInventory
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @param loginUserID
     * @param ORD_QTY
     * @param orderManagementLine
     */
    public OrderManagementLineV2 fullInvQtyAllocation(IInventory iInventory, String companyCodeId, String plantId, String languageId, String warehouseId,
                                     String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom, String loginUserID,
                                     Double ORD_QTY, OrderManagementLineV2 orderManagementLine){

        List<IInventoryImpl> finalInventoryList = null;
        OrderManagementLineV2 newOrderManagementLine = null;

        log.info("Logic according to Closed Case Full ---------------> INV_QTY == ORD_QTY Started");
        finalInventoryList = inventoryService.getInventoryForOrderManagementLevelAsscIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
                manufacturerName, stockTypeId, binClassId, alternateUom,
                iInventory.getLevelId());

        log.info("Group By LeveId Inventory Closed Case: " + finalInventoryList.size());
        newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
        log.info("newOrderManagementLine fullInvQty Allocaiton updated Closed Case ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param finalInventoryList
     * @param loginUserID
     * @return
     */
    public OrderManagementLineV2 orderAllocationV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
                                                   String loginUserID) {
        OrderManagementLineV2 newOrderManagementLine = null;
        String alternateUom = orderManagementLine.getAlternateUom();
        outerloop:
        for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
            InventoryV2 stBinInventory = inventoryService.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stBinWiseInventory.getBarcodeId(),
                    stBinWiseInventory.getStorageBin(), alternateUom);
            log.info("Inventory for Allocation Bin wise ---->: " + stBinInventory);

            // If the queried Inventory is empty then EMPTY orderManagementLine is created.
            if (stBinInventory == null && orderManagementLine.getStatusId() != 47L) {
                return updateOrderManagementLineV2(orderManagementLine);
            }

            if (stBinInventory != null) {

                Long STATUS_ID = 0L;
                Double ALLOC_QTY = 0D;
                String inventoryAlternateUom = stBinInventory.getAlternateUom();

                /*
                 * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. If
                 * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
                 * ALLOC_QTY=0
                 */
                Double INV_QTY = stBinInventory.getInventoryQuantity();

                // INV_QTY
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

                /*
                 * Deleting current record and inserting new record (since UK is not allowing to
                 * update prop_st_bin and Pack_bar_codes columns
                 */
                newOrderManagementLine = new OrderManagementLineV2();
                BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));

                if(newOrderManagementLine.getCompanyDescription() == null) {
                    description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                    newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                    newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                    newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
                }

                newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
                if (stBinInventory.getBarcodeId() != null) {
                    newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
                }
                if (stBinInventory.getLevelId() != null) {
                    newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
                }
                newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
                newOrderManagementLine.setProposedBatchSerialNumber(stBinInventory.getBatchSerialNumber());
                newOrderManagementLine.setMrp(stBinInventory.getMrp());
//                OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
                log.info("--else---createdOrderManagementLine newly created------: " + newOrderManagementLine);
                allocatedQtyFromOrderMgmt = newOrderManagementLine.getAllocatedQty();

                if (ORD_QTY > ALLOC_QTY) {
                    ORD_QTY = ORD_QTY - ALLOC_QTY;
                }

                if (allocatedQtyFromOrderMgmt > 0) {

                    double[] inventoryQty = allocateInventory(allocatedQtyFromOrderMgmt, orderManagementLine.getNoBags(), stBinInventory.getInventoryQuantity(), stBinInventory.getAllocatedQuantity());

                    log.info("The inventory array values are -------> {}", inventoryQty);

                    // Create new Inventory Record
                    InventoryV2 inventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(stBinInventory, inventoryV2, CommonUtils.getNullPropertyNames(stBinInventory));

                    if (inventoryQty != null && inventoryQty.length > 2) {
                        inventoryV2.setInventoryQuantity(inventoryQty[0]);
                        inventoryV2.setAllocatedQuantity(inventoryQty[1]);
                        inventoryV2.setReferenceField4(inventoryQty[2]);
                    }

                    if (inventoryV2.getBagSize() > inventoryV2.getInventoryQuantity()) {
                        log.info("Loose Pack True");
                        inventoryV2.setLoosePack(true);
                    }

                    inventoryV2.setReferenceDocumentNo(orderManagementLine.getRefDocNumber());
                    inventoryV2.setReferenceOrderNo(orderManagementLine.getRefDocNumber());
                    inventoryV2.setUpdatedOn(new Date());
                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
                    log.info("-----Inventory2 updated-------: " + inventoryV2);
                }

                if (ORD_QTY == ALLOC_QTY) {
                    log.info("ORD_QTY fully allocated: " + ORD_QTY);
                    break outerloop; // If the Inventory satisfied the Ord_qty
                }
            }
        }
        return newOrderManagementLine;
    }

    /**
     * orderManagementLine inventory calculation (Allocate)
     * @param allocatedQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] allocateInventory (Double allocatedQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, ALLOC_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + allocatedQty);
        bagSize = bagSize != null ? bagSize : 0;

        double actualAllocatedQty = getQuantity((allocatedQty != null ? allocatedQty : 0), bagSize);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) - actualAllocatedQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) + actualAllocatedQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY};
    }

    /**
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmNonCBMV4(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID, String manufacturerName, GrLineV2 createdGRLine) {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String manufactureName = null;
        String lineReferenceNo = null;

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
                manufactureName = newPutAwayLine.getManufacturerName();
                lineReferenceNo = String.valueOf(newPutAwayLine.getLineNo());

                log.info("proposedBin, confirmedBin: " + newPutAwayLine.getProposedStorageBin() + ", " + newPutAwayLine.getConfirmedStorageBin());

                StorageBinV2 dbStorageBin = null;
                try {
                    dbStorageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                } catch (Exception e) {
                    throw new BadRequestException("Invalid StorageBin --> " + newPutAwayLine.getConfirmedStorageBin());
                }

                PutAwayHeaderV2 putAwayHeader = putAwayHeaderService.fetchPutawayHeaderByItemV2(companyCode, plantId, warehouseId, itemCode, manufactureName, lineReferenceNo, languageId, newPutAwayLine.getPutAwayNumber());
                log.info("putawayHeader: " + putAwayHeader);

                if (dbStorageBin != null) {
                    dbPutAwayLine.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
                }

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
                        newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getManufacturerName());
                log.info("StagingLine: " + dbStagingLineEntity);
                if (dbStagingLineEntity != null) {
                    newPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                    newPutAwayLine.setMiddlewareId(dbStagingLineEntity.getMiddlewareId());
                    newPutAwayLine.setMiddlewareHeaderId(dbStagingLineEntity.getMiddlewareHeaderId());
                    newPutAwayLine.setMiddlewareTable(dbStagingLineEntity.getMiddlewareTable());
                    newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                    newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                    newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                    newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                    newPutAwayLine.setCompanyDescription(dbStagingLineEntity.getCompanyDescription());
                    newPutAwayLine.setPlantDescription(dbStagingLineEntity.getPlantDescription());
                    newPutAwayLine.setWarehouseDescription(dbStagingLineEntity.getWarehouseDescription());
                    newPutAwayLine.setMrp(dbStagingLineEntity.getMrp());
                    newPutAwayLine.setItemType(dbStagingLineEntity.getItemType());
                    newPutAwayLine.setItemGroup(dbStagingLineEntity.getItemGroup());
                    newPutAwayLine.setSize(dbStagingLineEntity.getSize());
                    newPutAwayLine.setBrand(dbStagingLineEntity.getBrand());
                    newPutAwayLine.setBagSize(dbStagingLineEntity.getBagSize());
                }

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));

                dbPutAwayLine.setStatusId(20L);
                statusDescription = getStatusDescription(20L, languageId);
                dbPutAwayLine.setStatusDescription(statusDescription);
                dbPutAwayLine.setDeletionIndicator(0L);
                dbPutAwayLine.setCreatedBy(loginUserID);
                dbPutAwayLine.setUpdatedBy(loginUserID);
                dbPutAwayLine.setConfirmedBy(loginUserID);

                if (putAwayHeader != null) {
                    dbPutAwayLine.setCreatedOn(putAwayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(putAwayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(putAwayHeader.getInboundOrderTypeId());
                    if (dbPutAwayLine.getBagSize() == null || dbPutAwayLine.getNoBags() == null) {
                        dbPutAwayLine.setNoBags(putAwayHeader.getNoBags());
                        dbPutAwayLine.setBagSize(putAwayHeader.getBagSize());
                        dbPutAwayLine.setAlternateUom(putAwayHeader.getAlternateUom());
                    }
                } else {
                    dbPutAwayLine.setCreatedOn(new Date());
                }
                dbPutAwayLine.setUpdatedOn(new Date());
                dbPutAwayLine.setConfirmedOn(new Date());

                Optional<PutAwayLineV2> existingPutAwayLine = putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, newPutAwayLine.getGoodsReceiptNo(), preInboundNo, refDocNumber,
                        newPutAwayLine.getPutAwayNumber(), newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getProposedStorageBin(),
                        Arrays.asList(newPutAwayLine.getConfirmedStorageBin()), 0L);
                log.info("Existing putawayline already created : " + existingPutAwayLine);

//                double actualInventoryQty = getQuantity(dbPutAwayLine.getPutawayConfirmedQty(), dbPutAwayLine.getBagSize());
                double actualInventoryQty = dbPutAwayLine.getPutawayConfirmedQty();
                dbPutAwayLine.setActualInventoryQty(actualInventoryQty);

                if (existingPutAwayLine.isEmpty()) {
                    //Lead Time calculation
                    String leadTime = putAwayLineV2Repository.getleadtimeV2(new Date(), putAwayHeader.getCreatedOn());
                    dbPutAwayLine.setReferenceField1(leadTime);
                    log.info("LeadTime: " + leadTime);

                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                    log.info("---------->NewPutAwayLine created: " + createdPutAwayLine);
                    fireBaseNotification(createdPutAwayLine, loginUserID);

                    createdPutAwayLines.add(createdPutAwayLine);

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {

                        dbStorageBin.setStatusId(1L);
                        storageBinService.updateStorageBinV6(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin, companyCode, plantId, languageId, warehouseId, loginUserID);

                        if (putAwayHeader != null) {
                            String confirmedStorageBin = createdPutAwayLine.getConfirmedStorageBin();
                            String proposedStorageBin = putAwayHeader.getProposedStorageBin();
                            log.info("putawayConfirmQty, putawayQty: " + createdPutAwayLine.getPutawayConfirmedQty() + ", " + putAwayHeader.getPutAwayQuantity());

                            putAwayHeader.setStatusId(20L);
                            putAwayHeader.setStatusDescription(statusDescription);
                            putAwayHeaderV2Repository.delete(putAwayHeader);
                            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                            log.info("putAwayHeader updated----> StatusId : " + putAwayHeader + " ---->----> " + putAwayHeader.getStatusId());

                            if (createdPutAwayLine.getPutawayConfirmedQty() < putAwayHeader.getPutAwayQuantity()) {
                                Double dbAssignedPutawayQty = 0D;
                                if (putAwayHeader.getReferenceField2() != null) {
                                    dbAssignedPutawayQty = Double.valueOf(putAwayHeader.getReferenceField2());
                                }
                                if (putAwayHeader.getReferenceField2() == null) {
                                    dbAssignedPutawayQty = putAwayHeader.getPutAwayQuantity();
                                }
                                Double dbPutawayQty = putAwayLineV2Repository.getPutawayCnfQuantity(companyCode, plantId, languageId, warehouseId,
                                        refDocNumber, preInboundNo, itemCode,
                                        createdPutAwayLine.getManufacturerName(),
                                        createdPutAwayLine.getLineNo());
                                if (dbPutawayQty == null) {
                                    dbPutawayQty = 0D;
                                }

                                log.info("NON CBM ---> tot_pa_cnf_qty,created_pa_line_cnf_qty,partial_pa_header_pa_qty,pa_header_pa_qty,RF2 : "
                                        + dbPutawayQty + ", " + createdPutAwayLine.getPutawayConfirmedQty()
                                        + ", " + putAwayHeader.getPutAwayQuantity() + ", " + putAwayHeader.getReferenceField2());
                                if (dbPutawayQty > dbAssignedPutawayQty) {
                                    throw new BadRequestException("sum of confirm Putaway line qty is greater than assigned putaway header qty");
                                }
                                if (dbPutawayQty <= dbAssignedPutawayQty) {
//                                    if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                    log.info("New PutawayHeader Create Initiated---> ");
                                    PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                    BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader, CommonUtils.getNullPropertyNames(putAwayHeader));

                                    // PA_NO
                                    long NUM_RAN_CODE = 7;
                                    String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId);
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

//                                    Double PUTAWAY_QTY = (putAwayHeader.getPutAwayQuantity() != null ? putAwayHeader.getPutAwayQuantity() : 0) - (createdPutAwayLine.getPutawayConfirmedQty() != null ? createdPutAwayLine.getPutawayConfirmedQty() : 0);
                                    Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                    if (PUTAWAY_QTY < 0) {
                                        throw new BadRequestException("total confirm qty greater than putaway qty");
                                    }
                                    newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                    log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                    newPutAwayHeader.setStatusId(19L);
                                    log.info("PutawayHeader StatusId : 19");
                                    statusDescription = getStatusDescription(newPutAwayHeader.getStatusId(), languageId);
                                    newPutAwayHeader.setStatusDescription(statusDescription);
                                    newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                    log.info("putAwayHeader created: " + newPutAwayHeader);

                                }
                            }
                        }

                        /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                        // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE table and
                        // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
                        double addedAcceptQty = 0.0;
                        double addedDamageQty = 0.0;

                        InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(companyCode, plantId, languageId, warehouseId,
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
//                            double actualAcceptQty = getQuantity(addedAcceptQty, createdPutAwayLine.getBagSize());
                            double actualAcceptQty = addedAcceptQty;
                            inboundLine.setActualAcceptedQty(actualAcceptQty);
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
//                            double actualDamageQty = getQuantity(addedDamageQty, createdPutAwayLine.getBagSize());
                            double actualDamageQty = addedDamageQty;
                            inboundLine.setActualDamageQty(actualDamageQty);
                            inboundLine.setDamageQty(addedDamageQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedDamageQty);
                        }

                        if (inboundLine.getInboundOrderTypeId() == 5L) {          //condition added for final Inbound confirm
                            inboundLine.setReferenceField2("true");
                        }

                        inboundLine.setStatusId(20L);
                        statusDescription = getStatusDescription(20L, languageId);
                        inboundLine.setStatusDescription(statusDescription);
                        inboundLineV2Repository.delete(inboundLine);
                        inboundLine = inboundLineV2Repository.save(inboundLine);
                        log.info("inboundLine updated : " + inboundLine);
                    }
                } else {
                    log.info("Putaway Line already exist : " + existingPutAwayLine);
                }
            }
            putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            log.info("InboundHeader received lines count updated: " + refDocNumber);

            if (createdPutAwayLines != null && !createdPutAwayLines.isEmpty()) {
                for (PutAwayLineV2 putAwayLine : createdPutAwayLines) {
                    createInventoryNonCBMCrossDockV6(companyCode, plantId, languageId, warehouseId, putAwayLine.getItemCode(),
                            putAwayLine.getManufacturerName(), refDocNumber, putAwayLine, loginUserID);
                }
                log.info("Inventory Created Successfully -----> for All Putaway Lines");
            }

//            inboundConfirmValidation(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, loginUserID);

//            /*----------------Inventory tables Create---------------------------------------------*/
//            createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

            return createdPutAwayLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("PutawayLine Create Exception");
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLineV2> getListOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                                    String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementLine = orderManagementLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
            return orderManagementLine;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param proposedStorageBin
     * @param proposedPackBarCode
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 doUnAllocationV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                  String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                  String itemCode, String proposedStorageBin, String proposedPackBarCode, String loginUserID) throws Exception {

        log.info("Unallocation for incoming orderManagementLine initaited.........");

        List<OrderManagementLineV2> orderManagementLineList =
                getListOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                        refDocNumber, partnerCode, lineNumber, itemCode);
        log.info("Processing Order management Line : " + orderManagementLineList);

        /*
         * Update Inventory table -------------------------- Pass the
         * WH_ID/ITM_CODE/PACK_BARCODE(PROP_PACK_BARCODE)/ST_BIN(PROP_ST_BIN) values in
         * INVENTORY table update INV_QTY as (INV_QTY+ALLOC_QTY) and change ALLOC_QTY as
         * 0
         */
        int i = 0;

        statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);

        for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineList) {
            String storageBin = dbOrderManagementLine.getProposedStorageBin();
            InventoryV2 inventory = inventoryService.getOutboundInventoryV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    dbOrderManagementLine.getManufacturerName(), dbOrderManagementLine.getBarcodeId(),
                    storageBin, dbOrderManagementLine.getAlternateUom());

            double[] inventoryQty = calculateInventoryUnAllocate(dbOrderManagementLine.getAllocatedQty(), dbOrderManagementLine.getNoBags(),
                    inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
            if (inventoryQty != null && inventoryQty.length > 3) {
                inventory.setInventoryQuantity(inventoryQty[0]);
                inventory.setAllocatedQuantity(inventoryQty[1]);
                inventory.setReferenceField4(inventoryQty[2]);
//                inventory.setNoBags(inventoryQty[3]);
            }
            inventory.setNoBags(dbOrderManagementLine.getNoBags() != null ? dbOrderManagementLine.getNoBags() : 1.0);

            // Create new Inventory Record
            InventoryV2 inventoryV2 = new InventoryV2();
            BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
            inventoryV2 = inventoryV2Repository.save(inventoryV2);
            log.info("-----InventoryV2 created-------: " + inventoryV2);

            /*
             * 1. Update ALLOC_QTY value as 0 2. Update STATUS_ID = 47
             */
            dbOrderManagementLine.setAllocatedQty(0D);
            dbOrderManagementLine.setStatusId(47L);
            dbOrderManagementLine.setReferenceField7(statusDescription);
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            if (i != 0) {
                dbOrderManagementLine.setDeletionIndicator(1L);
            }
            OrderManagementLineV2 updatedOrderManagementLine = orderManagementLineV2Repository.save(dbOrderManagementLine);
            log.info("OrderManagementLine updated: " + updatedOrderManagementLine);
            i++;
        }
        return !orderManagementLineList.isEmpty() ? orderManagementLineList.get(0) : null;
    }

    /**
     *
     * @param orderQty
     * @param uomQty
     * @return
     */
    public Double getQuantity(Double orderQty, Double uomQty) {
        if(orderQty != null && uomQty != null) {
            return orderQty * uomQty;
        }
        throw new BadRequestException("Quantity cannot be null");
    }

    /**
     * pickupline inventory calculation (unAllocate)
     * @param pickCnfQty/allocatedQty
     * @param bagSize
     * @param inventoryQty
     * @param invAllocatedQty
     * @return
     */
    public double[] calculateInventoryUnAllocate (Double pickCnfQty, Double bagSize, Double inventoryQty, Double invAllocatedQty) {
        log.info("INV_QTY, ALLOC_QTY, PICK_CNF_QTY : " + inventoryQty + ", " + invAllocatedQty + ", " + pickCnfQty);
        bagSize = bagSize != null ? bagSize : 0;

        double actualPickConfirmQty = getQuantity((pickCnfQty != null ? pickCnfQty : 0), bagSize);

        double INV_QTY = (inventoryQty != null ? inventoryQty : 0) + actualPickConfirmQty;
        double ALLOC_QTY = (invAllocatedQty != null ? invAllocatedQty : 0) - actualPickConfirmQty;

        INV_QTY = INV_QTY < 0 ? 0 : round(INV_QTY);
        ALLOC_QTY = ALLOC_QTY < 0 ? 0 : round(ALLOC_QTY);

        double TOT_QTY = INV_QTY + ALLOC_QTY;
        double NO_OF_BAGS = TOT_QTY != 0 ? roundUp(TOT_QTY / bagSize) : 0;

        log.info("INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + "|" + ALLOC_QTY + "|" + TOT_QTY + "|" + NO_OF_BAGS);
        return new double[] {INV_QTY, ALLOC_QTY, TOT_QTY, NO_OF_BAGS};
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
    private InventoryV2 createInventoryNonCBMCrossDockV6(String companyCode, String plantId, String languageId,
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
//                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                double physicalQty = putAwayLine.getPutawayConfirmedQty();
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

            // INV_QTY
            if (existingInventory != null) {
                Double ALLOC_QTY = existingInventory.getAllocatedQuantity() != null ? existingInventory.getAllocatedQuantity() : 0D;
                Double INV_QTY = existingInventory.getInventoryQuantity() != null ? existingInventory.getInventoryQuantity() : 0D;
                Double NO_OF_BAGS = existingInventory.getNoBags() != null ? existingInventory.getNoBags() : 0D;
                Double NEW_NO_OF_BAGS = putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D;
                Double TOT_NO_OF_BAGS = NO_OF_BAGS + NEW_NO_OF_BAGS;
                log.info("Existing Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY, PA_CNF_QTY : "
                        + INV_QTY + ", " + ALLOC_QTY + ", " + existingInventory.getReferenceField4() + ", " + putAwayLine.getPutawayConfirmedQty() + ", " + NO_OF_BAGS);

//                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                double physicalQty = putAwayLine.getPutawayConfirmedQty();

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
//                Double INV_QTY = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                Double INV_QTY = putAwayLine.getPutawayConfirmedQty();
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


    /**
     *
     * @param grLine
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public StagingLineEntityV2 createLines(GrLineV2 grLine, String loginUserId) throws Exception {
        try {
            StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
            InboundLineV2 inboundLine = new InboundLineV2();
            PreInboundLineEntityV2 preInboundLine = createPreInboundLineV2(grLine, loginUserId);
            BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
            BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
            stagingLine.setStagingNo(grLine.getStagingNo());
            stagingLine.setPalletCode(grLine.getPalletCode());
            stagingLine.setCaseCode(grLine.getCaseCode());
            stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
            stagingLine.setRemarks("test");                     //Handled for UI - hardcoded

            preInboundLineV2Repository.save(preInboundLine);
            inboundLineV2Repository.save(inboundLine);
            StagingLineEntityV2 createdStagingLine = stagingLineV2Repository.save(stagingLine);

            return createdStagingLine;
        } catch (Exception e) {
            log.error("IB - Line Create Exception: " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param grLine
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public PreInboundLineEntityV2 createPreInboundLineV2(GrLineV2 grLine, String loginUserId) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(grLine, preInboundLine, CommonUtils.getNullPropertyNames(grLine));

            preInboundLine.setStockTypeId(1L);
            preInboundLine.setSpecialStockIndicatorId(1L);
            preInboundLine.setManufacturerCode(MFR_NAME);
            preInboundLine.setManufacturerName(MFR_NAME);
            preInboundLine.setManufacturerPartNo(MFR_NAME);
            if(grLine.getReferenceDocumentType() == null) {
                preInboundLine.setReferenceDocumentType(getInboundOrderTypeDesc(
                        grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(), grLine.getWarehouseId(), grLine.getInboundOrderTypeId()));
            }

            if(preInboundLine.getOrderUom() != null) {
                AlternateUomImpl alternateUom = getUom(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(),
                        grLine.getWarehouseId(), grLine.getItemCode(), grLine.getOrderUom());
                if(alternateUom == null) {
                    throw new BadRequestException("AlternateUom is not available for this item : " + grLine.getItemCode());
                }
                if (alternateUom != null) {
                    preInboundLine.setOrderUom(alternateUom.getUom());
                    preInboundLine.setAlternateUom(alternateUom.getAlternateUom());
                    preInboundLine.setBagSize(alternateUom.getAlternateUomQty());
                    preInboundLine.setNoBags(grLine.getGoodReceiptQty());
                }
            }

            String barcodeId = generateBarCodeIdV4(grLine.getItemCode(), grLine.getRefDocNumber());
            preInboundLine.setBarcodeId(barcodeId);

            preInboundLine.setOrderQty(grLine.getGoodReceiptQty());
            preInboundLine.setStatusDescription(getStatusDescription(13L, grLine.getLanguageId()));
            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(loginUserId);
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param itemCode
     * @param refDocNumber
     * @return
     */
    public String generateBarCodeIdV4 (String itemCode, String refDocNumber) {
        itemCode = itemCode.trim().toUpperCase().replaceAll("\\s+", "");
        String orderNoLastThreeDigit = refDocNumber;
        if (refDocNumber != null && refDocNumber.length() >= 3) {
            orderNoLastThreeDigit = refDocNumber.substring(refDocNumber.length() - 3);
        } else {
            throw new IllegalArgumentException("refDocNumber must be at least 3 characters long");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(itemCode);
        stringBuilder.append(orderNoLastThreeDigit);
        stringBuilder.append(DateUtils.getDateMonth());
        return stringBuilder.toString();
    }

    /**
     *
     * @param warehouseId
     * @param binClassId
     * @param companyCode
     * @param plantId
     * @param languageId
     * @return
     */
    private StorageBinV2 getReserveBin (String warehouseId, Long binClassId, String companyCode, String plantId, String languageId) {
        log.info("BinClassId : " + binClassId);
        return storageBinService.getStorageBinByBinClassIdV2(warehouseId, binClassId, companyCode, plantId, languageId);
    }

    /**
     * @param putAwayLine
     */
    private void fireBaseNotification(PutAwayLineV2 putAwayLine, String loginUserID) {
        try {
            List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
                    putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(),loginUserID);
            if (deviceToken != null && !deviceToken.isEmpty()) {
                String title = "Inbound Create";
                String message = "A new Inbound- " + putAwayLine.getConfirmedStorageBin() + " has been created on ";

                NotificationSave notificationInput = new NotificationSave();
                notificationInput.setUserId(Collections.singletonList(loginUserID));
                notificationInput.setUserType(null);
                notificationInput.setMessage(message);
                notificationInput.setTopic(title);
                notificationInput.setReferenceNumber(putAwayLine.getRefDocNumber());
                notificationInput.setDocumentNumber(putAwayLine.getPreInboundNo());
                notificationInput.setCompanyCodeId(putAwayLine.getCompanyCode());
                notificationInput.setPlantId(putAwayLine.getPlantId());
                notificationInput.setLanguageId(putAwayLine.getLanguageId());
                notificationInput.setWarehouseId(putAwayLine.getWarehouseId());
                notificationInput.setCreatedOn(putAwayLine.getCreatedOn());
                notificationInput.setCreatedBy(loginUserID);
                notificationInput.setStorageBin(putAwayLine.getConfirmedStorageBin());

                pushNotificationService.sendPushNotification(deviceToken, notificationInput);
            }
        } catch (Exception e) {
            log.error("Inbound firebase notification error " + e.toString());
        }
    }

    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLineV2 updateOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }
}
