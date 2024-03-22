package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.inbound.InboundHeader;
import com.tekclover.wms.api.transaction.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.*;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.repository.InboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.SupplierInvoiceHeaderRepository;
import com.tekclover.wms.api.transaction.repository.SupplierInvoiceLineRepository;
import com.tekclover.wms.api.transaction.repository.specification.SupplierInvoiceHeaderSpecification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class InvoiceCancellationService extends BaseService{
    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private MastersService mastersService;

    @Autowired
    InboundHeaderRepository inboundHeaderRepository;

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    PreInboundLineService preInboundLineService;

    @Autowired
    StagingHeaderService stagingHeaderService;

    @Autowired
    StagingLineService stagingLineService;

    @Autowired
    GrHeaderService grHeaderService;

    @Autowired
    GrLineService grLineService;

    @Autowired
    PutAwayHeaderService putAwayHeaderService;

    @Autowired
    PutAwayLineService putAwayLineService;

    @Autowired
    InboundHeaderService inboundHeaderService;

    @Autowired
    InboundLineService inboundLineService;

    @Autowired
    InventoryMovementService inventoryMovementService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    SupplierInvoiceLineRepository supplierInvoiceLineRepository;

    @Autowired
    SupplierInvoiceHeaderRepository supplierInvoiceHeaderRepository;

//===================================================================================================================================

    @Transactional
//    public WarehouseApiResponse replaceSupplierInvoice(String companyCode, String languageId, String plantId, String warehouseId, String oldInvoiceNo, String newInvoiceNo, String loginUserId) throws ParseException {
//
//        log.info("Supplier Invoice Cancellation Initiated ---> oldInvoiceNumber ----> : " + oldInvoiceNo + ", " + newInvoiceNo);
//
//        Optional<InboundHeader> dbInvoiceNumber = inboundHeaderRepository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndStatusIdAndDeletionIndicator(
//                companyCode, plantId, languageId, warehouseId, oldInvoiceNo, 24L, 0L);
//
//        if (dbInvoiceNumber.isPresent()) {
//            throw new BadRequestException("Invoice cannot be processed it has been already completed " + oldInvoiceNo);
//        }
//
//        //InboundHeader
//        InboundHeaderV2 inboundHeaderV2 = inboundHeaderService.deleteInboundHeaderV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("InboundHeader Deleted Successfully" + inboundHeaderV2);
//
//        //InboundLine
//        List<InboundLineV2> inboundLineV2 = inboundLineService.deleteInboundLineV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("InboundLine Deleted Successfully" + inboundLineV2);
//
//        //PreInboundHeader
//        PreInboundHeaderEntityV2 preInboundHeaderV2 = preInboundHeaderService.deletePreInboundHeader(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("PreInboundHeader Deleted SuccessFully" + preInboundHeaderV2);
//
//        //Delete PreInboundLine
//        List<PreInboundLineEntityV2> preInboundLineEntityV2 = preInboundLineService.deletePreInboundLine(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("PreInboundLine Deleted Successfully " + preInboundLineEntityV2);
//
//        //Delete StagingHeader
//        StagingHeaderV2 stagingHeaderV2 = stagingHeaderService.deleteStagingHeaderV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("StagingHeader Deleted Successfully" + stagingHeaderV2);
//
//        //Delete StagingLine
//        List<StagingLineEntityV2> stagingLineEntityV2 = stagingLineService.deleteStagingLineV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("StagingLine Deleted Successfully " + stagingLineEntityV2);
//
//        //Delete GrHeaderService
//        GrHeaderV2 grHeaderV2 = grHeaderService.deleteGrHeaderV2(companyCode, languageId, plantId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("GrHeader Deleted Successfully " + grHeaderV2);
//
//        //Delete GrLine
//        List<GrLineV2> grLineList = grLineService.deleteGrLineV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("GrLine Deleted Successfully " + grLineList);
//
//        if (grLineList != null && !grLineList.isEmpty()) {
//            List<InventoryV2> inventoryList = new ArrayList<>();
//            for (GrLineV2 grLine : grLineList) {
//                InventoryV2 dbInventory = inventoryService.deleteInventoryInvoiceCancellation(companyCode, plantId, languageId, warehouseId, grLine);
//                inventoryList.add(dbInventory);
//            }
//            log.info("Inventory List - after delete(insert) : " + inventoryList);
//        }
//
//        //Delete PutAwayHeader
//        List<PutAwayHeaderV2> putAwayHeaderV2 = putAwayHeaderService.deletePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("PutAwayHeader Deleted Successfully " + putAwayHeaderV2);
//
//        //Delete PutAwayLine
//        List<PutAwayLineV2> putAwayLineV2List = putAwayLineService.deletePutAwayLineV2(languageId, companyCode, plantId, warehouseId, oldInvoiceNo, loginUserId);
//        log.info("PutAwayLine Deleted Successfully" + putAwayLineV2List);
//
//        //InventoryMovement
//        List<InventoryMovement> inventoryMovement = inventoryMovementService.deleteInventoryMovement(warehouseId, companyCode, plantId, languageId, oldInvoiceNo, loginUserId);
//        log.info("InventoryMovement Deleted Successfully" + inventoryMovement);
//
//        //Get GrHeader NewInvoiceNo
//        GrHeaderV2 grHeader = grHeaderService.getGrHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, newInvoiceNo);
//        log.info("New Supplier Invoice - GrHeader : " + grHeader);
//        List<GrLineV2> createGrLine = null;
//        List<PutAwayLineV2> createdPutawayLine = null;
//        if (grHeader != null) {
//
//            List<StagingLineEntityV2> stagingLineEntityList = stagingLineService.getStagingLineForGrLine(
//                    companyCode, plantId, languageId, warehouseId, grHeader.getStagingNo(), grHeader.getCaseCode());
//            log.info("New Supplier Invoice StagingLine : " + stagingLineEntityList);
//
//            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//
//            Double itemLength = 0D;
//            Double itemWidth = 0D;
//            Double itemHeight = 0D;
//            Double orderQty = 0D;
//            Double cbm = 0D;
//            Double cbmPerQty = 0D;
//
//            List<AddGrLineV2> newGrLineList = new ArrayList<>();
//
//            if (stagingLineEntityList != null && !stagingLineEntityList.isEmpty()) {
//                for (StagingLineEntityV2 dbStagingLine : stagingLineEntityList) {
//                    List<GrLineV2> grLinePresent = null;
//                    if (grLineList != null && !grLineList.isEmpty()) {
//                        grLinePresent = grLineList.stream().filter(n -> n.getItemCode().equalsIgnoreCase(dbStagingLine.getItemCode()) && n.getManufacturerName().equalsIgnoreCase(dbStagingLine.getManufacturerName())).collect(Collectors.toList());
//                    }
//                    log.info("GrLine Present in cancelled SupplierInvoice : " + grLinePresent);
//                    if (grLinePresent != null && !grLinePresent.isEmpty()) {
//                        List<PackBarcode> packBarcodeList = new ArrayList<>();
//                        AddGrLineV2 newGrLine = new AddGrLineV2();
//                        PackBarcode newPackBarcode = new PackBarcode();
//
//                        BeanUtils.copyProperties(dbStagingLine, newGrLine, CommonUtils.getNullPropertyNames(dbStagingLine));
//
//                        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
//                        long NUM_RAN_ID = 6;
//                        String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, dbStagingLine.getCompanyCode(),
//                                dbStagingLine.getPlantId(), dbStagingLine.getLanguageId(), dbStagingLine.getWarehouseId(), authTokenForIDMasterService.getAccess_token());
//
////                        boolean capacityCheck = false;
////                        boolean storageBinCapacityCheck = false;
//
////                        ImBasicData imBasicData = new ImBasicData();
////                        imBasicData.setCompanyCodeId(dbStagingLine.getCompanyCode());
////                        imBasicData.setPlantId(dbStagingLine.getPlantId());
////                        imBasicData.setLanguageId(dbStagingLine.getLanguageId());
////                        imBasicData.setWarehouseId(dbStagingLine.getWarehouseId());
////                        imBasicData.setItemCode(dbStagingLine.getItemCode());
////                        imBasicData.setManufacturerName(dbStagingLine.getManufacturerName());
////                        ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
////                        log.info("ImbasicData1 : " + itemCodeCapacityCheck);
////                        if (itemCodeCapacityCheck.getCapacityCheck() != null) {
////                            capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
////                            log.info("capacity Check: " + capacityCheck);
////                        }
//
////                        newPackBarcode.setQuantityType("A");
//                        newPackBarcode.setQuantityType(grLinePresent.get(0).getQuantityType());
//                        newPackBarcode.setBarcode(nextRangeNumber);
//
////                        if (capacityCheck) {
////
////                            if (dbStagingLine.getOrderQty() != null) {
////                                orderQty = dbStagingLine.getOrderQty();
////                            }
////                            if (itemCodeCapacityCheck.getLength() != null) {
////                                itemLength = itemCodeCapacityCheck.getLength();
////                            }
////                            if (itemCodeCapacityCheck.getWidth() != null) {
////                                itemWidth = itemCodeCapacityCheck.getWidth();
////                            }
////                            if (itemCodeCapacityCheck.getHeight() != null) {
////                                itemHeight = itemCodeCapacityCheck.getHeight();
////                            }
////
////                            cbmPerQty = itemLength * itemWidth * itemHeight;
////                            cbm = orderQty * cbmPerQty;
////
////                            newPackBarcode.setCbmQuantity(cbmPerQty);
////                            newPackBarcode.setCbm(cbm);
////
////                            log.info("item Length, Width, Height, Volume[CbmPerQty], CBM: " + itemLength + ", " + itemWidth + "," + itemHeight + ", " + cbmPerQty + ", " + cbm);
////                        }
////                        if (!capacityCheck) {
//
//                            newPackBarcode.setCbmQuantity(0D);
//                            newPackBarcode.setCbm(0D);
////                        }
//
//                        packBarcodeList.add(newPackBarcode);
//
//                        newGrLine.setOrderQty(dbStagingLine.getOrderQty());
//                        newGrLine.setGoodReceiptQty(grLinePresent.get(0).getGoodReceiptQty());
//                        newGrLine.setAcceptedQty(grLinePresent.get(0).getAcceptedQty());
//                        newGrLine.setDamageQty(grLinePresent.get(0).getDamageQty());
//                        newGrLine.setGoodsReceiptNo(grHeader.getGoodsReceiptNo());
//                        newGrLine.setManufacturerFullName(dbStagingLine.getManufacturerFullName());
//                        newGrLine.setReferenceDocumentType(dbStagingLine.getReferenceDocumentType());
//                        newGrLine.setPurchaseOrderNumber(dbStagingLine.getPurchaseOrderNumber());
//                        if (dbStagingLine.getPartner_item_barcode() != null) {
//                            newGrLine.setBarcodeId(dbStagingLine.getPartner_item_barcode());
//                        }
//                        if (dbStagingLine.getPartner_item_barcode() == null) {
//                            newGrLine.setBarcodeId(dbStagingLine.getManufacturerName() + dbStagingLine.getItemCode());
//                        }
//
//                        newGrLine.setAssignedUserId(grLinePresent.get(0).getAssignedUserId());
//
//                        newGrLine.setPackBarcodes(packBarcodeList);
//
//                        newGrLineList.add(newGrLine);
//                    }
//                }
//            }
//
//            try {
//                if (newGrLineList != null && !newGrLineList.isEmpty()) {
////                    createGrLine = grLineService.createGrLineV2(newGrLineList, grHeader.getCreatedBy());
//                    createGrLine = grLineService.createGrLineNonCBMV2(newGrLineList, grHeader.getCreatedBy());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException(e);
//            }
//            log.info("GrLine Created Successfully: " + createGrLine);
//
//            createdPutawayLine = null;
//            //putaway Confirm
//            if (createGrLine != null && !createGrLine.isEmpty()) {
//                log.info("Putaway line Confirm Initiated");
//                List<PutAwayLineV2> createPutawayLine = new ArrayList<>();
//
//                List<PutAwayHeaderV2> dbPutawayHeaderList = putAwayHeaderService.getPutAwayHeaderV2(
//                        companyCode,
//                        plantId,
//                        languageId,
//                        warehouseId,
//                        newInvoiceNo);
//                log.info("Putaway header: " + dbPutawayHeaderList);
//
//                if (dbPutawayHeaderList != null && !dbPutawayHeaderList.isEmpty()) {
//                    for (PutAwayHeaderV2 dbPutawayHeader : dbPutawayHeaderList) {
//                        List<PutAwayLineV2> putAwayLinePresent = putAwayLineV2List.stream().filter(n -> n.getItemCode().equalsIgnoreCase(dbPutawayHeader.getReferenceField5()) && n.getManufacturerName().equalsIgnoreCase(dbPutawayHeader.getManufacturerName())).collect(Collectors.toList());
//                        log.info("PutawayLine Present : " + putAwayLinePresent);
//                        if (putAwayLinePresent != null && !putAwayLinePresent.isEmpty()) {
//
//                            PutAwayLineV2 putAwayLine = new PutAwayLineV2();
//
//                            List<GrLineV2> grLine = createGrLine.stream().filter(n -> n.getPackBarcodes().equalsIgnoreCase(dbPutawayHeader.getPackBarcodes())).collect(Collectors.toList());
//
//                            BeanUtils.copyProperties(grLine.get(0), putAwayLine, CommonUtils.getNullPropertyNames(grLine.get(0)));
//                            putAwayLine.setProposedStorageBin(dbPutawayHeader.getProposedStorageBin());
//                            putAwayLine.setConfirmedStorageBin(dbPutawayHeader.getProposedStorageBin());
//                            putAwayLine.setPutawayConfirmedQty(dbPutawayHeader.getPutAwayQuantity());
//                            putAwayLine.setPutAwayNumber(dbPutawayHeader.getPutAwayNumber());
//                            createPutawayLine.add(putAwayLine);
//                        }
//                    }
//                }
//                try {
//                    createdPutawayLine = putAwayLineService.putAwayLineConfirmV2(createPutawayLine, grHeader.getCreatedBy());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    throw new RuntimeException(e);
//                }
//                log.info("PutawayLine Confirmed: " + createdPutawayLine);
//            }
//        }
//
//        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
//        if (createdPutawayLine != null && !createdPutawayLine.isEmpty()){
//            warehouseApiResponse.setStatusCode("200");
//            warehouseApiResponse.setMessage("SupplierInvoice Cancellation Success");
//        }
//        if(grHeader != null) {
//            //Insert Record in SupplierInvoiceCancellation Table
//            createSupplierInvoiceHeader(inboundHeaderV2, inboundLineV2, grHeader, grLineList, createGrLine, putAwayLineV2List, createdPutawayLine, loginUserId);
//        }
//        return warehouseApiResponse;
//    }
    public WarehouseApiResponse replaceSupplierInvoice(String companyCode, String languageId, String plantId, String warehouseId,
                                                       String oldPreInboundNo, String oldInvoiceNo, String newInvoiceNo, String newPreInboundNo, String loginUserId) throws ParseException {

        log.info("Supplier Invoice Cancellation Initiated ---> oldInvoiceNumber ----> : " + oldInvoiceNo + ", " + newInvoiceNo);

        Optional<InboundHeader> dbInvoiceNumber = inboundHeaderRepository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndStatusIdAndDeletionIndicator(
                companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, 24L, 0L);

        if (dbInvoiceNumber.isPresent()) {
            throw new BadRequestException("Invoice cannot be processed it has been already completed " + oldInvoiceNo);
        }

        //InboundHeader
        InboundHeaderV2 inboundHeaderV2 = inboundHeaderService.deleteInboundHeaderForCancelV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("InboundHeader Deleted Successfully" + inboundHeaderV2);

        //InboundLine
        List<InboundLineV2> inboundLineV2 = inboundLineService.deleteInboundLineV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("InboundLine Deleted Successfully" + inboundLineV2);

        //PreInboundHeader
        PreInboundHeaderEntityV2 preInboundHeaderV2 = preInboundHeaderService.deletePreInboundHeader(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("PreInboundHeader Deleted SuccessFully" + preInboundHeaderV2);

        //Delete PreInboundLine
        List<PreInboundLineEntityV2> preInboundLineEntityV2 = preInboundLineService.deletePreInboundLine(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("PreInboundLine Deleted Successfully " + preInboundLineEntityV2);

        //Delete StagingHeader
        StagingHeaderV2 stagingHeaderV2 = stagingHeaderService.deleteStagingHeaderV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("StagingHeader Deleted Successfully" + stagingHeaderV2);

        //Delete StagingLine
        List<StagingLineEntityV2> stagingLineEntityV2 = stagingLineService.deleteStagingLineV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("StagingLine Deleted Successfully " + stagingLineEntityV2);

        //Delete GrHeaderService
        GrHeaderV2 grHeaderV2 = grHeaderService.deleteGrHeaderV2(companyCode, languageId, plantId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("GrHeader Deleted Successfully " + grHeaderV2);

        //Delete GrLine
        List<GrLineV2> grLineList = grLineService.deleteGrLineV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("GrLine Deleted Successfully " + grLineList);

        if (grLineList != null && !grLineList.isEmpty()) {
            List<InventoryV2> inventoryList = new ArrayList<>();
            for (GrLineV2 grLine : grLineList) {
                InventoryV2 dbInventory = inventoryService.deleteInventoryInvoiceCancellation(companyCode, plantId, languageId, warehouseId, grLine);
                inventoryList.add(dbInventory);
            }
            log.info("Inventory List - after delete(insert) : " + inventoryList);
        }

        //Delete PutAwayHeader
        List<PutAwayHeaderV2> putAwayHeaderV2 = putAwayHeaderService.deletePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("PutAwayHeader Deleted Successfully " + putAwayHeaderV2);

        //Delete PutAwayLine
        List<PutAwayLineV2> putAwayLineV2List = putAwayLineService.deletePutAwayLineV2(languageId, companyCode, plantId, warehouseId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("PutAwayLine Deleted Successfully" + putAwayLineV2List);

        //InventoryMovement
        List<InventoryMovement> inventoryMovement = inventoryMovementService.deleteInventoryMovement(warehouseId, companyCode, plantId, languageId, oldInvoiceNo, oldPreInboundNo, loginUserId);
        log.info("InventoryMovement Deleted Successfully" + inventoryMovement);

        //Get GrHeader NewInvoiceNo
        GrHeaderV2 grHeader = grHeaderService.getGrHeaderForReversalV2(companyCode, plantId, languageId, warehouseId, newInvoiceNo, newPreInboundNo);
        log.info("New Supplier Invoice - GrHeader : " + grHeader);
        List<GrLineV2> createGrLine = null;
        List<PutAwayLineV2> createdPutawayLine = null;
        if (grHeader != null) {

            List<StagingLineEntityV2> stagingLineEntityList = stagingLineService.getStagingLineForGrLine(
                    companyCode, plantId, languageId, warehouseId, grHeader.getStagingNo(), grHeader.getRefDocNumber(), grHeader.getPreInboundNo());
            log.info("New Supplier Invoice StagingLine : " + stagingLineEntityList);

//            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

//            Double itemLength = 0D;
//            Double itemWidth = 0D;
//            Double itemHeight = 0D;
//            Double orderQty = 0D;
//            Double cbm = 0D;
//            Double cbmPerQty = 0D;

            List<AddGrLineV2> newGrLineList = new ArrayList<>();

            if (stagingLineEntityList != null && !stagingLineEntityList.isEmpty()) {
                for (StagingLineEntityV2 dbStagingLine : stagingLineEntityList) {
                    List<GrLineV2> grLinePresent = null;
                    if (grLineList != null && !grLineList.isEmpty()) {
                        grLinePresent = grLineList.stream().filter(n -> n.getItemCode().equalsIgnoreCase(dbStagingLine.getItemCode()) && n.getManufacturerName().equalsIgnoreCase(dbStagingLine.getManufacturerName())).collect(Collectors.toList());
                    }
                    log.info("GrLine Present in cancelled SupplierInvoice : " + grLinePresent);
                    if (grLinePresent != null && !grLinePresent.isEmpty()) {
                        List<PackBarcode> packBarcodeList = new ArrayList<>();
                        AddGrLineV2 newGrLine = new AddGrLineV2();
                        PackBarcode newPackBarcode = new PackBarcode();

                        BeanUtils.copyProperties(dbStagingLine, newGrLine, CommonUtils.getNullPropertyNames(dbStagingLine));

                        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
                        long NUM_RAN_ID = 6;
                        String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, dbStagingLine.getCompanyCode(),
                                dbStagingLine.getPlantId(), dbStagingLine.getLanguageId(), dbStagingLine.getWarehouseId(), authTokenForIDMasterService.getAccess_token());

//                        boolean capacityCheck = false;
//                        boolean storageBinCapacityCheck = false;

//                        ImBasicData imBasicData = new ImBasicData();
//                        imBasicData.setCompanyCodeId(dbStagingLine.getCompanyCode());
//                        imBasicData.setPlantId(dbStagingLine.getPlantId());
//                        imBasicData.setLanguageId(dbStagingLine.getLanguageId());
//                        imBasicData.setWarehouseId(dbStagingLine.getWarehouseId());
//                        imBasicData.setItemCode(dbStagingLine.getItemCode());
//                        imBasicData.setManufacturerName(dbStagingLine.getManufacturerName());
//                        ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
//                        log.info("ImbasicData1 : " + itemCodeCapacityCheck);
//                        if (itemCodeCapacityCheck.getCapacityCheck() != null) {
//                            capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
//                            log.info("capacity Check: " + capacityCheck);
//                        }

//                        newPackBarcode.setQuantityType("A");
                        newPackBarcode.setQuantityType(grLinePresent.get(0).getQuantityType());
                        newPackBarcode.setBarcode(nextRangeNumber);

//                        if (capacityCheck) {
//
//                            if (dbStagingLine.getOrderQty() != null) {
//                                orderQty = dbStagingLine.getOrderQty();
//                            }
//                            if (itemCodeCapacityCheck.getLength() != null) {
//                                itemLength = itemCodeCapacityCheck.getLength();
//                            }
//                            if (itemCodeCapacityCheck.getWidth() != null) {
//                                itemWidth = itemCodeCapacityCheck.getWidth();
//                            }
//                            if (itemCodeCapacityCheck.getHeight() != null) {
//                                itemHeight = itemCodeCapacityCheck.getHeight();
//                            }
//
//                            cbmPerQty = itemLength * itemWidth * itemHeight;
//                            cbm = orderQty * cbmPerQty;
//
//                            newPackBarcode.setCbmQuantity(cbmPerQty);
//                            newPackBarcode.setCbm(cbm);
//
//                            log.info("item Length, Width, Height, Volume[CbmPerQty], CBM: " + itemLength + ", " + itemWidth + "," + itemHeight + ", " + cbmPerQty + ", " + cbm);
//                        }
//                        if (!capacityCheck) {

                        newPackBarcode.setCbmQuantity(0D);
                        newPackBarcode.setCbm(0D);
//                        }

                        packBarcodeList.add(newPackBarcode);

                        newGrLine.setOrderQty(dbStagingLine.getOrderQty());
                        newGrLine.setGoodReceiptQty(grLinePresent.get(0).getGoodReceiptQty());
                        newGrLine.setAcceptedQty(grLinePresent.get(0).getAcceptedQty());
                        newGrLine.setDamageQty(grLinePresent.get(0).getDamageQty());
                        newGrLine.setGoodsReceiptNo(grHeader.getGoodsReceiptNo());
                        newGrLine.setManufacturerFullName(dbStagingLine.getManufacturerFullName());
                        newGrLine.setReferenceDocumentType(dbStagingLine.getReferenceDocumentType());
                        newGrLine.setPurchaseOrderNumber(dbStagingLine.getPurchaseOrderNumber());
                        if (dbStagingLine.getPartner_item_barcode() != null) {
                            newGrLine.setBarcodeId(dbStagingLine.getPartner_item_barcode());
                        }
                        if (dbStagingLine.getPartner_item_barcode() == null) {
                            newGrLine.setBarcodeId(dbStagingLine.getManufacturerName() + dbStagingLine.getItemCode());
                        }

                        newGrLine.setAssignedUserId(grLinePresent.get(0).getAssignedUserId());

                        newGrLine.setPackBarcodes(packBarcodeList);

                        newGrLineList.add(newGrLine);
                    }
                }
            }

            try {
                if (newGrLineList != null && !newGrLineList.isEmpty()) {
//                    createGrLine = grLineService.createGrLineV2(newGrLineList, grHeader.getCreatedBy());
                    createGrLine = grLineService.createGrLineNonCBMV2(newGrLineList, grHeader.getCreatedBy());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            log.info("GrLine Created Successfully: " + createGrLine);

            createdPutawayLine = null;
            //putaway Confirm
            if (createGrLine != null && !createGrLine.isEmpty()) {
                log.info("Putaway line Confirm Initiated");
                List<PutAwayLineV2> createPutawayLine = new ArrayList<>();

                List<PutAwayHeaderV2> dbPutawayHeaderList = putAwayHeaderService.getPutAwayHeaderForCancellationV2(
                        companyCode, plantId, languageId, warehouseId, newInvoiceNo, newPreInboundNo);
                log.info("Putaway header: " + dbPutawayHeaderList);

                if (dbPutawayHeaderList != null && !dbPutawayHeaderList.isEmpty()) {
                    for (PutAwayHeaderV2 dbPutawayHeader : dbPutawayHeaderList) {
                        List<PutAwayLineV2> putAwayLinePresent = putAwayLineV2List.stream().filter(n -> n.getItemCode().equalsIgnoreCase(dbPutawayHeader.getReferenceField5()) && n.getManufacturerName().equalsIgnoreCase(dbPutawayHeader.getManufacturerName())).collect(Collectors.toList());
                        log.info("PutawayLine Present : " + putAwayLinePresent);
                        if (putAwayLinePresent != null && !putAwayLinePresent.isEmpty()) {

                            PutAwayLineV2 putAwayLine = new PutAwayLineV2();

                            List<GrLineV2> grLine = createGrLine.stream().filter(n -> n.getPackBarcodes().equalsIgnoreCase(dbPutawayHeader.getPackBarcodes())).collect(Collectors.toList());

                            BeanUtils.copyProperties(grLine.get(0), putAwayLine, CommonUtils.getNullPropertyNames(grLine.get(0)));
                            putAwayLine.setProposedStorageBin(dbPutawayHeader.getProposedStorageBin());
                            putAwayLine.setConfirmedStorageBin(dbPutawayHeader.getProposedStorageBin());
                            putAwayLine.setPutawayConfirmedQty(dbPutawayHeader.getPutAwayQuantity());
                            putAwayLine.setPutAwayNumber(dbPutawayHeader.getPutAwayNumber());
                            createPutawayLine.add(putAwayLine);
                        }
                    }
                }
                try {
                    createdPutawayLine = putAwayLineService.putAwayLineConfirmV2(createPutawayLine, grHeader.getCreatedBy());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                log.info("PutawayLine Confirmed: " + createdPutawayLine);
            }
        }

        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        if (createdPutawayLine != null && !createdPutawayLine.isEmpty()){
            warehouseApiResponse.setStatusCode("200");
            warehouseApiResponse.setMessage("SupplierInvoice Cancellation Success");
        }
        if(grHeader != null) {
            //Insert Record in SupplierInvoiceCancellation Table
            createSupplierInvoiceHeader(inboundHeaderV2, inboundLineV2, grHeader, grLineList, createGrLine, putAwayLineV2List, createdPutawayLine, loginUserId);
        }
        return warehouseApiResponse;
    }

    //Streaming
    public Stream<SupplierInvoiceHeader> findSupplierInvoiceHeader(SearchSupplierInvoiceHeader searchSupplierInvoiceHeader) throws ParseException {

        if (searchSupplierInvoiceHeader.getStartCreatedOn() != null
                && searchSupplierInvoiceHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchSupplierInvoiceHeader.getStartCreatedOn(),
                    searchSupplierInvoiceHeader.getEndCreatedOn());
            searchSupplierInvoiceHeader.setStartCreatedOn(dates[0]);
            searchSupplierInvoiceHeader.setEndCreatedOn(dates[1]);
        }

        if (searchSupplierInvoiceHeader.getStartConfirmedOn() != null
                && searchSupplierInvoiceHeader.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchSupplierInvoiceHeader.getStartConfirmedOn(),
                    searchSupplierInvoiceHeader.getEndConfirmedOn());
            searchSupplierInvoiceHeader.setStartConfirmedOn(dates[0]);
            searchSupplierInvoiceHeader.setEndConfirmedOn(dates[1]);
        }
        SupplierInvoiceHeaderSpecification spec = new SupplierInvoiceHeaderSpecification(searchSupplierInvoiceHeader);
        Stream<SupplierInvoiceHeader> searchResults = supplierInvoiceHeaderRepository.stream(spec, SupplierInvoiceHeader.class);

        return searchResults;
    }

    /**
     *
     * @param oldInboundHeader
     * @param oldInboundLineList
     * @param newGrHeader
     * @param oldGrLineList
     * @param newGrLineList
     * @param oldPutAwayLineList
     * @param newPutAwayLineList
     * @param loginUserId
     * @return
     */
    private SupplierInvoiceHeader createSupplierInvoiceHeader(InboundHeaderV2 oldInboundHeader, List<InboundLineV2> oldInboundLineList,
                                                              GrHeaderV2 newGrHeader, List<GrLineV2> oldGrLineList, List<GrLineV2> newGrLineList,
                                                              List<PutAwayLineV2> oldPutAwayLineList, List<PutAwayLineV2> newPutAwayLineList, String loginUserId) {
        String companyCodeId = newGrHeader.getCompanyCode();
        String plantId = newGrHeader.getPlantId();
        String languageId = newGrHeader.getLanguageId();
        String warehouseId = newGrHeader.getWarehouseId();
        String oldSupplierInvoiceNo = oldInboundHeader.getRefDocNumber();
        String newSupplierInvoiceNo = newGrHeader.getRefDocNumber();

        Long oldCountOfOrderedLines = 0L;
        Long oldCountOfReceivedLines = 0L;
        Long newCountOfOrderedLines = 0L;
        Long newCountOfReceivedLines = 0L;
        InboundHeaderV2 newInboundHeaderV2 = inboundHeaderService.getInboundHeaderForInvoiceCancellationV2(companyCodeId, plantId, languageId, warehouseId, newSupplierInvoiceNo);

        if (oldInboundLineList != null && !oldInboundLineList.isEmpty()) {
            oldCountOfOrderedLines = oldInboundLineList.stream().count();
        }
        List<InboundLineV2> oldInboundLineConfirmedList = inboundLineService.getInboundLineForInvoiceCancellationV2(companyCodeId, plantId, languageId, warehouseId, oldSupplierInvoiceNo, 24L);
        if (oldInboundLineConfirmedList != null && !oldInboundLineConfirmedList.isEmpty()) {
            oldCountOfReceivedLines = oldInboundLineConfirmedList.stream().count();
        }
        List<InboundLineV2> newInboundLineList = inboundLineService.getInboundLineForInvoiceCancellationV2(companyCodeId, plantId, languageId, warehouseId, newSupplierInvoiceNo, 14L);
        if (newInboundLineList != null && !newInboundLineList.isEmpty()) {
            newCountOfOrderedLines = newInboundLineList.stream().count();
        }
        List<InboundLineV2> newInboundLineConfirmedList = inboundLineService.getInboundLineForInvoiceCancellationV2(companyCodeId, plantId, languageId, warehouseId, newSupplierInvoiceNo, 24L);
        if (newInboundLineConfirmedList != null && !newInboundLineConfirmedList.isEmpty()) {
            newCountOfReceivedLines = newInboundLineConfirmedList.stream().count();
        }
        SupplierInvoiceHeader newSupplierInvoiceHeader = new SupplierInvoiceHeader();
        BeanUtils.copyProperties(oldInboundHeader, newSupplierInvoiceHeader, CommonUtils.getNullPropertyNames(oldInboundHeader));

        newSupplierInvoiceHeader.setOldPreInboundNo(oldInboundHeader.getPreInboundNo());
        newSupplierInvoiceHeader.setOldRefDocNumber(oldInboundHeader.getRefDocNumber());
        newSupplierInvoiceHeader.setOldContainerNo(oldInboundHeader.getContainerNo());
        newSupplierInvoiceHeader.setOldVechicleNo(oldInboundHeader.getVechicleNo());
        newSupplierInvoiceHeader.setOldCountOfOrderLines(oldCountOfOrderedLines);
        newSupplierInvoiceHeader.setOldReceivedLines(oldCountOfReceivedLines);
        newSupplierInvoiceHeader.setOldStatusId(oldInboundHeader.getStatusId());
        newSupplierInvoiceHeader.setOldStatusDescription(oldInboundHeader.getStatusDescription());

        newSupplierInvoiceHeader.setNewPreInboundNo(newGrHeader.getPreInboundNo());
        newSupplierInvoiceHeader.setNewRefDocNumber(newGrHeader.getRefDocNumber());
        newSupplierInvoiceHeader.setNewContainerNo(newGrHeader.getContainerNo());
        newSupplierInvoiceHeader.setNewVechicleNo(newGrHeader.getVechicleNo());
        newSupplierInvoiceHeader.setNewCountOfOrderLines(newCountOfOrderedLines);
        newSupplierInvoiceHeader.setNewReceivedLines(newCountOfReceivedLines);
        if(newInboundHeaderV2 != null) {
            newSupplierInvoiceHeader.setNewStatusId(newInboundHeaderV2.getStatusId());
            newSupplierInvoiceHeader.setNewStatusDescription(newInboundHeaderV2.getStatusDescription());
        }

        newSupplierInvoiceHeader.setDeletionIndicator(0L);
        newSupplierInvoiceHeader.setCreatedBy(loginUserId);
        newSupplierInvoiceHeader.setCreatedOn(new Date());
        newSupplierInvoiceHeader.setSupplierInvoiceCancelHeaderId(System.currentTimeMillis());

        /*
         * newSupplierInvoice Line Table Insert
         */
        List<SupplierInvoiceLine> toBeCreatedSupplierInvoiceLineList = new ArrayList<>();
        List<InboundLineV2> filteredInboundList = null;
        List<String> createdItmMfrNameList = new ArrayList<>();
        List<String> oldItmMfrNameList = new ArrayList<>();
        List<String> newItmMfrNameList = new ArrayList<>();
        List<String> filterOldList = new ArrayList<>();
        List<String> filterNewList = new ArrayList<>();
        List<String> createdItmMfrNameList2 = new ArrayList<>();
        List<String> oldItmMfrNameList2 = new ArrayList<>();
        List<String> newItmMfrNameList2 = new ArrayList<>();
        List<String> filterOldList2 = new ArrayList<>();
        List<String> filterNewList2 = new ArrayList<>();
        if (oldPutAwayLineList != null && !oldPutAwayLineList.isEmpty()) {
            if (newPutAwayLineList != null && !newPutAwayLineList.isEmpty()) {
                for (PutAwayLineV2 putAwayLine : oldPutAwayLineList) {
                    oldItmMfrNameList.add(putAwayLine.getItemCode() + putAwayLine.getManufacturerName());
                    for (PutAwayLineV2 newPutAwayLine : newPutAwayLineList) {
//                        newItmMfrNameList.add(newPutAwayLine.getItemCode() + newPutAwayLine.getManufacturerName());
                        if (putAwayLine.getItemCode().equalsIgnoreCase(newPutAwayLine.getItemCode()) &&
                                putAwayLine.getManufacturerName().equalsIgnoreCase(newPutAwayLine.getManufacturerName())) {

                            SupplierInvoiceLine supplierInvoiceLine = new SupplierInvoiceLine();
                            BeanUtils.copyProperties(putAwayLine, supplierInvoiceLine, CommonUtils.getNullPropertyNames(putAwayLine));

                            if (oldInboundLineList != null && !oldInboundLineList.isEmpty()) {
                                filteredInboundList = oldInboundLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(putAwayLine.getItemCode()) &&
                                        a.getManufacturerName().equalsIgnoreCase(putAwayLine.getManufacturerName())).collect(Collectors.toList());
                                if (filteredInboundList != null && !filteredInboundList.isEmpty()) {
                                    supplierInvoiceLine.setOldInvoiceNo(filteredInboundList.get(0).getInvoiceNo());
                                }
                            }
                            if (newInboundLineList != null && !newInboundLineList.isEmpty()) {
                                filteredInboundList = newInboundLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(newPutAwayLine.getItemCode()) &&
                                        a.getManufacturerName().equalsIgnoreCase(newPutAwayLine.getManufacturerName())).collect(Collectors.toList());
                                if (filteredInboundList != null && !filteredInboundList.isEmpty()) {
                                    supplierInvoiceLine.setOldInvoiceNo(filteredInboundList.get(0).getInvoiceNo());
                                }
                            }

                            supplierInvoiceLine.setOldRefDocNumber(oldInboundHeader.getRefDocNumber());
                            supplierInvoiceLine.setOldPreInboundNo(oldInboundHeader.getPreInboundNo());
                            supplierInvoiceLine.setOldReferenceOrderNo(oldInboundHeader.getRefDocNumber());
                            supplierInvoiceLine.setOldContainerNo(oldInboundHeader.getContainerNo());
                            supplierInvoiceLine.setOldLineNo(putAwayLine.getLineNo());
                            supplierInvoiceLine.setOldOrderQty(putAwayLine.getOrderQty());
                            supplierInvoiceLine.setOldConfirmedStorageBin(putAwayLine.getConfirmedStorageBin());
                            supplierInvoiceLine.setOldProposedStorageBin(putAwayLine.getProposedStorageBin());
                            supplierInvoiceLine.setOldPutAwayQuantity(putAwayLine.getPutAwayQuantity());
                            supplierInvoiceLine.setOldPutawayConfirmedQty(putAwayLine.getPutawayConfirmedQty());
                            supplierInvoiceLine.setOldPutAwayHandlingEquipment(putAwayLine.getPutAwayHandlingEquipment());
                            supplierInvoiceLine.setOldStatusId(putAwayLine.getStatusId());
                            supplierInvoiceLine.setOldStatusDescription(putAwayLine.getStatusDescription());

                            supplierInvoiceLine.setNewRefDocNumber(newGrHeader.getRefDocNumber());
                            supplierInvoiceLine.setNewPreInboundNo(newGrHeader.getPreInboundNo());
                            supplierInvoiceLine.setNewReferenceOrderNo(newGrHeader.getRefDocNumber());
                            supplierInvoiceLine.setNewContainerNo(newGrHeader.getContainerNo());
                            supplierInvoiceLine.setNewLineNo(newPutAwayLine.getLineNo());
                            supplierInvoiceLine.setNewOrderQty(newPutAwayLine.getOrderQty());
                            supplierInvoiceLine.setNewConfirmedStorageBin(newPutAwayLine.getConfirmedStorageBin());
                            supplierInvoiceLine.setNewProposedStorageBin(newPutAwayLine.getProposedStorageBin());
                            supplierInvoiceLine.setNewPutAwayQuantity(newPutAwayLine.getPutAwayQuantity());
                            supplierInvoiceLine.setNewPutawayConfirmedQty(newPutAwayLine.getPutawayConfirmedQty());
                            supplierInvoiceLine.setNewPutAwayHandlingEquipment(newPutAwayLine.getPutAwayHandlingEquipment());
                            supplierInvoiceLine.setNewStatusId(newPutAwayLine.getStatusId());
                            supplierInvoiceLine.setNewStatusDescription(newPutAwayLine.getStatusDescription());

                            supplierInvoiceLine.setDeletionIndicator(0L);
                            supplierInvoiceLine.setCreatedBy(loginUserId);
                            supplierInvoiceLine.setUpdatedBy(loginUserId);
                            supplierInvoiceLine.setCreatedOn(new Date());
                            supplierInvoiceLine.setUpdatedOn(new Date());
                            supplierInvoiceLine.setSupplierInvoiceCancelHeaderId(newSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId());
                            supplierInvoiceLine.setSupplierInvoiceCancelLineId(System.currentTimeMillis());
                            toBeCreatedSupplierInvoiceLineList.add(supplierInvoiceLine);
                            createdItmMfrNameList.add(newPutAwayLine.getItemCode() + newPutAwayLine.getManufacturerName());
                        }
                    }
                }
                for (PutAwayLineV2 newPutAwayLine : newPutAwayLineList) {
                    newItmMfrNameList.add(newPutAwayLine.getItemCode() + newPutAwayLine.getManufacturerName());
                }
                log.info("OldSupplierInvoiceLineList : " + oldItmMfrNameList);
                log.info("NewSupplierInvoiceLineList : " + newItmMfrNameList);
                log.info("CreatedSupplierInvoiceLineList : " + createdItmMfrNameList);
                if(createdItmMfrNameList != null && !createdItmMfrNameList.isEmpty()){
                    for(String itmMfrName : createdItmMfrNameList){
                        boolean oldItmPresent = oldItmMfrNameList.stream().anyMatch(a->a.equalsIgnoreCase(itmMfrName));
                        boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a->a.equalsIgnoreCase(itmMfrName));
                        if(!oldItmPresent) {
                            filterOldList.add(itmMfrName);
                        }
                        if(!newItmPresent){
                            filterNewList.add(itmMfrName);
                        }
                    }
                }
                if(createdItmMfrNameList == null || createdItmMfrNameList.isEmpty()){
                    filterOldList = oldItmMfrNameList;
                    filterNewList = newItmMfrNameList;
                }
                log.info("Filtered OldSupplierInvoiceLineList : " + oldItmMfrNameList);
                log.info("Filtered NewSupplierInvoiceLineList : " + newItmMfrNameList);
                if (filterNewList != null && !filterNewList.isEmpty()) {
                    for (PutAwayLineV2 newPutAwayLine : newPutAwayLineList) {
                        String itmMfrName = newPutAwayLine.getItemCode() + newPutAwayLine.getManufacturerName();
                        boolean itmPresent = filterNewList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                        if (!itmPresent) {
                            SupplierInvoiceLine supplierInvoiceLine = new SupplierInvoiceLine();
                            BeanUtils.copyProperties(newPutAwayLine, supplierInvoiceLine, CommonUtils.getNullPropertyNames(newPutAwayLine));

                            if (newInboundLineList != null && !newInboundLineList.isEmpty()) {
                                filteredInboundList = newInboundLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(newPutAwayLine.getItemCode()) &&
                                        a.getManufacturerName().equalsIgnoreCase(newPutAwayLine.getManufacturerName())).collect(Collectors.toList());
                                if (filteredInboundList != null && !filteredInboundList.isEmpty()) {
                                    supplierInvoiceLine.setOldInvoiceNo(filteredInboundList.get(0).getInvoiceNo());
                                }
                            }

                            supplierInvoiceLine.setNewRefDocNumber(newGrHeader.getRefDocNumber());
                            supplierInvoiceLine.setNewPreInboundNo(newGrHeader.getPreInboundNo());
                            supplierInvoiceLine.setNewReferenceOrderNo(newGrHeader.getRefDocNumber());
                            supplierInvoiceLine.setNewContainerNo(newGrHeader.getContainerNo());
                            supplierInvoiceLine.setNewLineNo(newPutAwayLine.getLineNo());
                            supplierInvoiceLine.setNewOrderQty(newPutAwayLine.getOrderQty());
                            supplierInvoiceLine.setNewConfirmedStorageBin(newPutAwayLine.getConfirmedStorageBin());
                            supplierInvoiceLine.setNewProposedStorageBin(newPutAwayLine.getProposedStorageBin());
                            supplierInvoiceLine.setNewPutAwayQuantity(newPutAwayLine.getPutAwayQuantity());
                            supplierInvoiceLine.setNewPutawayConfirmedQty(newPutAwayLine.getPutawayConfirmedQty());
                            supplierInvoiceLine.setNewPutAwayHandlingEquipment(newPutAwayLine.getPutAwayHandlingEquipment());
                            supplierInvoiceLine.setNewStatusId(newPutAwayLine.getStatusId());
                            supplierInvoiceLine.setNewStatusDescription(newPutAwayLine.getStatusDescription());

                            supplierInvoiceLine.setDeletionIndicator(0L);
                            supplierInvoiceLine.setCreatedBy(loginUserId);
                            supplierInvoiceLine.setUpdatedBy(loginUserId);
                            supplierInvoiceLine.setCreatedOn(new Date());
                            supplierInvoiceLine.setUpdatedOn(new Date());
                            supplierInvoiceLine.setSupplierInvoiceCancelHeaderId(newSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId());
                            supplierInvoiceLine.setSupplierInvoiceCancelLineId(System.currentTimeMillis());
                            toBeCreatedSupplierInvoiceLineList.add(supplierInvoiceLine);
                        }
                    }
                }
            }
            if (filterOldList != null && !filterOldList.isEmpty()) {
                for (PutAwayLineV2 putAwayLine : oldPutAwayLineList) {
                    String itmMfrName = putAwayLine.getItemCode() + putAwayLine.getManufacturerName();
                    boolean itmPresent = filterOldList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                    if (!itmPresent) {
                        SupplierInvoiceLine supplierInvoiceLine = new SupplierInvoiceLine();
                        BeanUtils.copyProperties(putAwayLine, supplierInvoiceLine, CommonUtils.getNullPropertyNames(putAwayLine));

                        if (oldInboundLineList != null && !oldInboundLineList.isEmpty()) {
                            filteredInboundList = oldInboundLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(putAwayLine.getItemCode()) &&
                                    a.getManufacturerName().equalsIgnoreCase(putAwayLine.getManufacturerName())).collect(Collectors.toList());
                            if (filteredInboundList != null && !filteredInboundList.isEmpty()) {
                                supplierInvoiceLine.setOldInvoiceNo(filteredInboundList.get(0).getInvoiceNo());
                            }
                        }

                        supplierInvoiceLine.setOldRefDocNumber(oldInboundHeader.getRefDocNumber());
                        supplierInvoiceLine.setOldPreInboundNo(oldInboundHeader.getPreInboundNo());
                        supplierInvoiceLine.setOldReferenceOrderNo(oldInboundHeader.getRefDocNumber());
                        supplierInvoiceLine.setOldContainerNo(oldInboundHeader.getContainerNo());
                        supplierInvoiceLine.setOldLineNo(putAwayLine.getLineNo());
                        supplierInvoiceLine.setOldOrderQty(putAwayLine.getOrderQty());
                        supplierInvoiceLine.setOldConfirmedStorageBin(putAwayLine.getConfirmedStorageBin());
                        supplierInvoiceLine.setOldProposedStorageBin(putAwayLine.getProposedStorageBin());
                        supplierInvoiceLine.setOldPutAwayQuantity(putAwayLine.getPutAwayQuantity());
                        supplierInvoiceLine.setOldPutawayConfirmedQty(putAwayLine.getPutawayConfirmedQty());
                        supplierInvoiceLine.setOldPutAwayHandlingEquipment(putAwayLine.getPutAwayHandlingEquipment());
                        supplierInvoiceLine.setOldStatusId(putAwayLine.getStatusId());
                        supplierInvoiceLine.setOldStatusDescription(putAwayLine.getStatusDescription());

                        supplierInvoiceLine.setDeletionIndicator(0L);
                        supplierInvoiceLine.setCreatedBy(loginUserId);
                        supplierInvoiceLine.setUpdatedBy(loginUserId);
                        supplierInvoiceLine.setCreatedOn(new Date());
                        supplierInvoiceLine.setUpdatedOn(new Date());
                        supplierInvoiceLine.setSupplierInvoiceCancelHeaderId(newSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId());
                        supplierInvoiceLine.setSupplierInvoiceCancelLineId(System.currentTimeMillis());
                        toBeCreatedSupplierInvoiceLineList.add(supplierInvoiceLine);
                    }
                }
            }
        }
        //If PutawayLine is Empty fill the line details from GrLine
//        if(newPutAwayLineList == null || !newPutAwayLineList.isEmpty()) {
            if(oldGrLineList != null && !oldGrLineList.isEmpty()) {
            if(newGrLineList != null && !newGrLineList.isEmpty()) {
                for (GrLineV2 oldGrLine : oldGrLineList) {
                    String oldItmMfrName = oldGrLine.getItemCode() + oldGrLine.getManufacturerName();
                    log.info("ItmMfrName: " + oldItmMfrName);
                    boolean oldItmPresent = oldItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(oldItmMfrName));
                    if(!oldItmPresent){
                    oldItmMfrNameList2.add(oldGrLine.getItemCode() + oldGrLine.getManufacturerName());
                    for (GrLineV2 newGrLine : newGrLineList) {
                        String newItmMfrName = newGrLine.getItemCode() + newGrLine.getManufacturerName();
                        log.info("ItmMfrName: " + newItmMfrName);
                        boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(newItmMfrName));
                        if(!newItmPresent){
//                        newItmMfrNameList2.add(newGrLine.getItemCode() + newGrLine.getManufacturerName());
                        if (oldGrLine.getItemCode().equalsIgnoreCase(newGrLine.getItemCode()) &&
                                oldGrLine.getManufacturerName().equalsIgnoreCase(newGrLine.getManufacturerName())) {

                            SupplierInvoiceLine supplierInvoiceLine = new SupplierInvoiceLine();
                            BeanUtils.copyProperties(oldGrLine, supplierInvoiceLine, CommonUtils.getNullPropertyNames(oldGrLine));

                            if (oldInboundLineList != null && !oldInboundLineList.isEmpty()) {
                                filteredInboundList = oldInboundLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(oldGrLine.getItemCode()) &&
                                        a.getManufacturerName().equalsIgnoreCase(oldGrLine.getManufacturerName())).collect(Collectors.toList());
                                if (filteredInboundList != null && !filteredInboundList.isEmpty()) {
                                    supplierInvoiceLine.setOldInvoiceNo(filteredInboundList.get(0).getInvoiceNo());
                                }
                            }
                            if (newInboundLineList != null && !newInboundLineList.isEmpty()) {
                                filteredInboundList = newInboundLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(newGrLine.getItemCode()) &&
                                        a.getManufacturerName().equalsIgnoreCase(newGrLine.getManufacturerName())).collect(Collectors.toList());
                                if (filteredInboundList != null && !filteredInboundList.isEmpty()) {
                                    supplierInvoiceLine.setOldInvoiceNo(filteredInboundList.get(0).getInvoiceNo());
                                }
                            }

                            supplierInvoiceLine.setOldRefDocNumber(oldInboundHeader.getRefDocNumber());
                            supplierInvoiceLine.setOldPreInboundNo(oldInboundHeader.getPreInboundNo());
                            supplierInvoiceLine.setOldReferenceOrderNo(oldInboundHeader.getRefDocNumber());
                            supplierInvoiceLine.setOldContainerNo(oldInboundHeader.getContainerNo());
                            supplierInvoiceLine.setOldLineNo(oldGrLine.getLineNo());
                            supplierInvoiceLine.setOldOrderQty(oldGrLine.getOrderQty());
                            supplierInvoiceLine.setOldPutAwayQuantity(oldGrLine.getGoodReceiptQty());
                            supplierInvoiceLine.setOldPutawayConfirmedQty(oldGrLine.getGoodReceiptQty());
                            supplierInvoiceLine.setOldStatusId(oldGrLine.getStatusId());
                            supplierInvoiceLine.setOldStatusDescription(oldGrLine.getStatusDescription());

                            supplierInvoiceLine.setNewRefDocNumber(newGrHeader.getRefDocNumber());
                            supplierInvoiceLine.setNewPreInboundNo(newGrHeader.getPreInboundNo());
                            supplierInvoiceLine.setNewReferenceOrderNo(newGrHeader.getRefDocNumber());
                            supplierInvoiceLine.setNewContainerNo(newGrHeader.getContainerNo());
                            supplierInvoiceLine.setNewLineNo(newGrLine.getLineNo());
                            supplierInvoiceLine.setNewOrderQty(newGrLine.getOrderQty());
                            supplierInvoiceLine.setNewPutAwayQuantity(newGrLine.getGoodReceiptQty());
                            supplierInvoiceLine.setNewPutawayConfirmedQty(newGrLine.getGoodReceiptQty());
                            supplierInvoiceLine.setNewStatusId(newGrLine.getStatusId());
                            supplierInvoiceLine.setNewStatusDescription(newGrLine.getStatusDescription());

                            supplierInvoiceLine.setDeletionIndicator(0L);
                            supplierInvoiceLine.setCreatedBy(loginUserId);
                            supplierInvoiceLine.setUpdatedBy(loginUserId);
                            supplierInvoiceLine.setCreatedOn(new Date());
                            supplierInvoiceLine.setUpdatedOn(new Date());
                            supplierInvoiceLine.setSupplierInvoiceCancelHeaderId(newSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId());
                            supplierInvoiceLine.setSupplierInvoiceCancelLineId(System.currentTimeMillis());
                            toBeCreatedSupplierInvoiceLineList.add(supplierInvoiceLine);
                            createdItmMfrNameList2.add(newGrLine.getItemCode() + newGrLine.getManufacturerName());
                        }
                    }
                    }
                    }
                }
                for (GrLineV2 newGrLine : newGrLineList) {
                        String newItmMfrName = newGrLine.getItemCode() + newGrLine.getManufacturerName();
                        log.info("ItmMfrName: " + newItmMfrName);
                        boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(newItmMfrName));
                        if(!newItmPresent) {
                            newItmMfrNameList2.add(newGrLine.getItemCode() + newGrLine.getManufacturerName());
                        }
                }
                log.info("OldSupplierInvoiceLineList : " + oldItmMfrNameList2);
                log.info("NewSupplierInvoiceLineList : " + newItmMfrNameList2);
                log.info("CreatedSupplierInvoiceLineList : " + createdItmMfrNameList2);
                if(createdItmMfrNameList2 != null && !createdItmMfrNameList2.isEmpty()){
                    for(String itmMfrName : createdItmMfrNameList2){
                        boolean oldItmPresent = oldItmMfrNameList2.stream().anyMatch(a->a.equalsIgnoreCase(itmMfrName));
                        boolean newItmPresent = newItmMfrNameList2.stream().anyMatch(a->a.equalsIgnoreCase(itmMfrName));
                        if(!oldItmPresent) {
                            filterOldList2.add(itmMfrName);
                        }
                        if(!newItmPresent){
                            filterNewList2.add(itmMfrName);
                        }
                    }
                }
                if(createdItmMfrNameList2 == null || createdItmMfrNameList2.isEmpty()){
                    filterOldList2 = oldItmMfrNameList2;
                    filterNewList2 = newItmMfrNameList2;
                }
                log.info("Filtered OldSupplierInvoiceLineList : " + oldItmMfrNameList2);
                log.info("Filtered NewSupplierInvoiceLineList : " + newItmMfrNameList2);
                if (filterNewList2 != null && !filterNewList2.isEmpty()) {
                    for (GrLineV2 newGrLine : newGrLineList) {
                        String itmMfrName = newGrLine.getItemCode() + newGrLine.getManufacturerName();
                        boolean itmPresent = filterNewList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                        if (!itmPresent) {
                            SupplierInvoiceLine supplierInvoiceLine = new SupplierInvoiceLine();
                            BeanUtils.copyProperties(newGrLine, supplierInvoiceLine, CommonUtils.getNullPropertyNames(newGrLine));

                            if (newInboundLineList != null && !newInboundLineList.isEmpty()) {
                                filteredInboundList = newInboundLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(newGrLine.getItemCode()) &&
                                        a.getManufacturerName().equalsIgnoreCase(newGrLine.getManufacturerName())).collect(Collectors.toList());
                                if (filteredInboundList != null && !filteredInboundList.isEmpty()) {
                                    supplierInvoiceLine.setOldInvoiceNo(filteredInboundList.get(0).getInvoiceNo());
                                }
                            }

                            supplierInvoiceLine.setNewRefDocNumber(newGrHeader.getRefDocNumber());
                            supplierInvoiceLine.setNewPreInboundNo(newGrHeader.getPreInboundNo());
                            supplierInvoiceLine.setNewReferenceOrderNo(newGrHeader.getRefDocNumber());
                            supplierInvoiceLine.setNewContainerNo(newGrHeader.getContainerNo());
                            supplierInvoiceLine.setNewLineNo(newGrLine.getLineNo());
                            supplierInvoiceLine.setNewOrderQty(newGrLine.getOrderQty());
                            supplierInvoiceLine.setNewPutAwayQuantity(newGrLine.getGoodReceiptQty());
                            supplierInvoiceLine.setNewPutawayConfirmedQty(newGrLine.getGoodReceiptQty());
                            supplierInvoiceLine.setNewStatusId(newGrLine.getStatusId());
                            supplierInvoiceLine.setNewStatusDescription(newGrLine.getStatusDescription());

                            supplierInvoiceLine.setDeletionIndicator(0L);
                            supplierInvoiceLine.setCreatedBy(loginUserId);
                            supplierInvoiceLine.setUpdatedBy(loginUserId);
                            supplierInvoiceLine.setCreatedOn(new Date());
                            supplierInvoiceLine.setUpdatedOn(new Date());
                            supplierInvoiceLine.setSupplierInvoiceCancelHeaderId(newSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId());
                            supplierInvoiceLine.setSupplierInvoiceCancelLineId(System.currentTimeMillis());
                            toBeCreatedSupplierInvoiceLineList.add(supplierInvoiceLine);
                        }
                    }
                }
            }
            if (filterOldList2 != null && !filterOldList2.isEmpty()) {
                for (GrLineV2 oldGrLine : oldGrLineList) {
                    String itmMfrName = oldGrLine.getItemCode() + oldGrLine.getManufacturerName();
                    boolean itmPresent = filterOldList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                    if (!itmPresent) {
                        SupplierInvoiceLine supplierInvoiceLine = new SupplierInvoiceLine();
                        BeanUtils.copyProperties(oldGrLine, supplierInvoiceLine, CommonUtils.getNullPropertyNames(oldGrLine));

                        if (oldInboundLineList != null && !oldInboundLineList.isEmpty()) {
                            filteredInboundList = oldInboundLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(oldGrLine.getItemCode()) &&
                                    a.getManufacturerName().equalsIgnoreCase(oldGrLine.getManufacturerName())).collect(Collectors.toList());
                            if (filteredInboundList != null && !filteredInboundList.isEmpty()) {
                                supplierInvoiceLine.setOldInvoiceNo(filteredInboundList.get(0).getInvoiceNo());
                            }
                        }

                        supplierInvoiceLine.setOldRefDocNumber(oldInboundHeader.getRefDocNumber());
                        supplierInvoiceLine.setOldPreInboundNo(oldInboundHeader.getPreInboundNo());
                        supplierInvoiceLine.setOldReferenceOrderNo(oldInboundHeader.getRefDocNumber());
                        supplierInvoiceLine.setOldContainerNo(oldInboundHeader.getContainerNo());
                        supplierInvoiceLine.setOldLineNo(oldGrLine.getLineNo());
                        supplierInvoiceLine.setOldOrderQty(oldGrLine.getOrderQty());
                        supplierInvoiceLine.setOldPutAwayQuantity(oldGrLine.getGoodReceiptQty());
                        supplierInvoiceLine.setOldPutawayConfirmedQty(oldGrLine.getGoodReceiptQty());
                        supplierInvoiceLine.setOldStatusId(oldGrLine.getStatusId());
                        supplierInvoiceLine.setOldStatusDescription(oldGrLine.getStatusDescription());

                        supplierInvoiceLine.setDeletionIndicator(0L);
                        supplierInvoiceLine.setCreatedBy(loginUserId);
                        supplierInvoiceLine.setUpdatedBy(loginUserId);
                        supplierInvoiceLine.setCreatedOn(new Date());
                        supplierInvoiceLine.setUpdatedOn(new Date());
                        supplierInvoiceLine.setSupplierInvoiceCancelHeaderId(newSupplierInvoiceHeader.getSupplierInvoiceCancelHeaderId());
                        supplierInvoiceLine.setSupplierInvoiceCancelLineId(System.currentTimeMillis());
                        toBeCreatedSupplierInvoiceLineList.add(supplierInvoiceLine);
                    }
                }
            }
        }
//    }
        newSupplierInvoiceHeader.setLine(toBeCreatedSupplierInvoiceLineList);
        SupplierInvoiceHeader creatednewSupplierInvoiceHeader = supplierInvoiceHeaderRepository.save(newSupplierInvoiceHeader);
        log.info("newSupplierInvoiceHeader : " + creatednewSupplierInvoiceHeader);
        log.info("createdSupplierInvoiceLine : " + toBeCreatedSupplierInvoiceLineList);

        return creatednewSupplierInvoiceHeader;
    }

}