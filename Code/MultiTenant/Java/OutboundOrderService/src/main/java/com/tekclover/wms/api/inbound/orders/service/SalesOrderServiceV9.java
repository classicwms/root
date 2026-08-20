package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.controller.OutboundOrderRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.IImbasicData1;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.*;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class SalesOrderServiceV9 extends BaseService {

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    ContainerReceiptRepository containerReceiptRepository;

    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    ImBasicData1Repository imBasicData1Repository;

    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;

    @Autowired
    OrderService orderService;

    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    /**
     * @param salesOrder
     * @return
     */
    public SalesOrderV2 postSOV9(SalesOrderV2 salesOrder) {
        log.info("SalesOrderHeader received from External: " + salesOrder);
        OutboundOrderV2 savedSoHeader = saveSalesOrderV9(salesOrder);                                // Without Nongo
        log.info("salesOrderHeader: " + savedSoHeader);
        return salesOrder;
    }

    /**
     * @param salesOrder
     * @return
     */
    public OutboundOrderV2 saveSalesOrderV9(@Valid SalesOrderV2 salesOrder) {
        try {
            SalesOrderHeaderV2 salesOrderHeader = salesOrder.getSalesOrderHeader();

            OutboundOrderV2 apiHeader = new OutboundOrderV2();
            BeanUtils.copyProperties(salesOrderHeader, apiHeader, CommonUtils.getNullPropertyNames(salesOrderHeader));

            if (salesOrderHeader.getWarehouseId() != null && !salesOrderHeader.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(salesOrderHeader.getWarehouseId());
            } else {
                Optional<Warehouse> warehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                salesOrderHeader.getCompanyCode(), salesOrderHeader.getBranchCode(),
                                salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID,
                                0L);
                apiHeader.setWarehouseID(warehouse.get().getWarehouseId());
            }

            apiHeader.setBranchCode(salesOrderHeader.getBranchCode());
            apiHeader.setCompanyCode(salesOrderHeader.getCompanyCode());
            apiHeader.setLanguageId(salesOrderHeader.getLanguageId() != null ? salesOrderHeader.getLanguageId() : LANG_ID);

            apiHeader.setOrderId(salesOrderHeader.getPickListNumber());
            apiHeader.setPickListNumber(apiHeader.getOrderId());
            apiHeader.setPickListStatus(salesOrderHeader.getStatus());
            apiHeader.setRefDocumentNo(apiHeader.getOrderId());
            salesOrderHeader.setSalesOrderNumber(apiHeader.getPickListNumber());
            if (salesOrderHeader.getOrderType() != null) {
                apiHeader.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
            }

            if (apiHeader.getOutboundOrderTypeID().equals(3L)) {
                apiHeader.setRefDocumentType("PICK LIST");
            }
            if (apiHeader.getOutboundOrderTypeID().equals(2L)) {
                apiHeader.setRefDocumentType("Purchase Return");
            }
            if (apiHeader.getOutboundOrderTypeID().equals(1L)) {
                apiHeader.setRefDocumentType("Transfer Out");
            }

            apiHeader.setCustomerType("INVOICE");                                //HardCoded
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setSalesOrderNumber(apiHeader.getOrderId());
            apiHeader.setTokenNumber(salesOrderHeader.getTokenNumber());

            apiHeader.setMiddlewareId(salesOrderHeader.getMiddlewareId());
            apiHeader.setMiddlewareTable(salesOrderHeader.getMiddlewareTable());

            try {
                Date reqDate = DateUtils.convertStringToDate2(salesOrderHeader.getRequiredDeliveryDate());
                apiHeader.setRequiredDeliveryDate(reqDate);
            } catch (Exception e) {
                throw new OutboundOrderRequestException("Date format should be MM-dd-yyyy");
            }

            List<SalesOrderLineV2> salesOrderLines = salesOrder.getSalesOrderLine();
            Set<OutboundOrderLineV2> orderLines = new HashSet<>();
            for (SalesOrderLineV2 soLine : salesOrderLines) {
                String barcodeId = null;
                OutboundOrderLineV2 apiLine = new OutboundOrderLineV2();
                BeanUtils.copyProperties(soLine, apiLine, CommonUtils.getNullPropertyNames(soLine));
                apiLine.setBrand(soLine.getBrand());
                apiLine.setOrigin(soLine.getOrigin());
                apiLine.setPackQty(soLine.getPackQty());
                apiLine.setExpectedQty(soLine.getExpectedQty());
                apiLine.setSupplierName(soLine.getSupplierName());
                apiLine.setSourceBranchCode(salesOrderHeader.getStoreID());
                apiLine.setCountryOfOrigin(soLine.getCountryOfOrigin());
                apiLine.setFromCompanyCode(salesOrderHeader.getCompanyCode());

                if(apiLine.getManufacturerCode()!= null && apiLine.getManufacturerName() != null ) {

                    apiLine.setManufacturerCode(soLine.getManufacturerCode());
                    apiLine.setManufacturerName(soLine.getManufacturerName());
                    apiLine.setManufacturerFullName(soLine.getManufacturerName());

                } else {

                    if (soLine.getUnitType() != null) {

                        IKeyValuePair mfrCode = imBasicData1V2Repository.getItemCodeUsingInventoryOwnerV9(salesOrderHeader.getCompanyCode(),
                                salesOrderHeader.getLanguageId(), salesOrderHeader.getBranchCode(), salesOrderHeader.getWarehouseId(),
                                soLine.getSku(), soLine.getUnitType());

                        if (mfrCode != null) {
                            apiLine.setManufacturerCode(mfrCode.getManufacturerCode());
                            apiLine.setManufacturerName(mfrCode.getManufacturerName());
                            apiLine.setManufacturerFullName(mfrCode.getManufacturerFullName());
                        }

                    }
                }

                if (apiLine.getManufacturerCode() == null || apiLine.getManufacturerCode().isEmpty()) {
                    if (apiHeader.getWarehouseID().equalsIgnoreCase("4100")) {
                        apiLine.setManufacturerCode(MFR_NAME_V9);
                        apiLine.setManufacturerName(MFR_NAME_V9);
                        apiLine.setManufacturerFullName(MFR_NAME_V9);
                    } else if (apiHeader.getWarehouseID().equalsIgnoreCase("4200")) {
                        apiLine.setManufacturerCode(MFR_NAME_V11);
                        apiLine.setManufacturerName(MFR_NAME_V11);
                        apiLine.setManufacturerFullName(MFR_NAME_V11);
                    } else if (apiHeader.getWarehouseID().equalsIgnoreCase("4300")) {
                        apiLine.setManufacturerCode(MFR_NAME_V12);
                        apiLine.setManufacturerName(MFR_NAME_V12);
                        apiLine.setManufacturerFullName(MFR_NAME_V12);
                    }
                }

                apiLine.setStoreID(salesOrderHeader.getStoreID());
                apiLine.setRefField1ForOrderType(soLine.getOrderType());
                apiLine.setCustomerType("INVOICE");                                //HardCoded
                if (salesOrderHeader.getOrderType() != null) {
                    apiLine.setOutboundOrderTypeID(Long.valueOf(salesOrderHeader.getOrderType()));
                } else {
                    apiLine.setOutboundOrderTypeID(OB_PL_ORD_TYP_ID);
                }
                if (soLine.getBarcodeId() != null) {
                    apiLine.setBarcodeId(soLine.getBarcodeId());
                }
                apiLine.setLineReference(soLine.getLineReference());            // IB_LINE_NO
                apiLine.setItemCode(soLine.getSku().trim());                    // ITM_CODE


                apiLine.setOrderedQty(soLine.getOrderedQty());                    // ORD_QTY
                apiLine.setUom("Case");                                // ORD_UOM
                apiLine.setRefField1ForOrderType(soLine.getOrderType());        // ORDER_TYPE
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setSalesOrderNo(soLine.getSalesOrderNo());
                apiLine.setPickListNo(apiHeader.getOrderId());

                apiLine.setMiddlewareId(soLine.getMiddlewareId());
                apiLine.setMiddlewareHeaderId(soLine.getMiddlewareHeaderId());
                apiLine.setMiddlewareTable(soLine.getMiddlewareTable());

                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (salesOrder.getSalesOrderLine() != null && !salesOrder.getSalesOrderLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);
                OutboundOrderV2 createdOrder = createOutboundOrdersV9(apiHeader);
                log.info("SalesOrder Order Success: " + createdOrder);
                return apiHeader;
            } else if (salesOrder.getSalesOrderLine() == null || salesOrder.getSalesOrderLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                OutboundOrderV2 createdOrder = createOutboundOrdersV9(apiHeader);
                log.info("SalesOrder Order Failed: " + createdOrder);
                throw new BadRequestException("SalesOrder Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw new BadRequestException("Exception while saving sales Order-PickList - " + e.toString());
        }
        return null;
    }

    public OutboundOrderV2 createOutboundOrdersV9(OutboundOrderV2 newOutboundOrder) throws ParseException {
        OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.
                findByRefDocumentNoAndOutboundOrderTypeID(newOutboundOrder.getOrderId(), newOutboundOrder.getOutboundOrderTypeID());
        if (dbOutboundOrder != null) {
            throw new BadRequestException("Order is getting Duplicated");
        }
        newOutboundOrder.setUpdatedOn(new Date());
        return outboundOrderV2Repository.save(newOutboundOrder);
    }

    //==============BF=============================
    @Async("asyncExecutor")
    public void outboundOrderV9(List<SalesOrderV2> salesOrderV2List, String preOutboundNoAndRefDocNo) throws Exception {
        for (SalesOrderV2 so : salesOrderV2List) {
            processOutboundReceivedV9(so, preOutboundNoAndRefDocNo);
        }
    }

    /**
     * @param salesOrderV2
     * @return
     * @throws Exception
     */
    public void processOutboundReceivedV9(SalesOrderV2 salesOrderV2, String preOutboundNoAndRefDocNo) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            SalesOrderHeaderV2 salesOrderHeaderV2 = salesOrderV2.getSalesOrderHeader();
            List<SalesOrderLineV2> salesOrderLineV2s = salesOrderV2.getSalesOrderLine();

            String companyCodeId = salesOrderHeaderV2.getCompanyCode();
            String plantId = salesOrderHeaderV2.getBranchCode();
            String languageId = salesOrderHeaderV2.getLanguageId() != null ? salesOrderHeaderV2.getLanguageId() : LANG_ID;
            String warehouseId = salesOrderHeaderV2.getWarehouseId();

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);

            if (salesOrderHeaderV2.getSalesOrderNumber() == null || salesOrderHeaderV2.getSalesOrderNumber().isEmpty()) {

            }
            String refDocNumber = salesOrderHeaderV2.getSalesOrderNumber();
            Long outboundOrderTypeId = Long.valueOf(salesOrderHeaderV2.getOrderType());
            log.info("Outbound Process Initiated V9------> : {}|{}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber, outboundOrderTypeId);
            MW_BFS = salesOrderHeaderV2.getLoginUserId() != null ? salesOrderHeaderV2.getLoginUserId() : MW_BFS;

            String idMasterAuthToken = getIDMasterAuthToken();
            String preOutboundNo = null;
            /*
             * Grouping PreOutboundHeader ID based on the Customer ID
             */
            if (preOutboundNoAndRefDocNo == null || preOutboundNoAndRefDocNo.isEmpty()) {
                preOutboundNo = getPreOutboundNoV9(companyCodeId, plantId, languageId, warehouseId,
                        salesOrderHeaderV2.getCustomerId(), salesOrderHeaderV2.getSalesOrderNumber(), outboundOrderTypeId, idMasterAuthToken);
            } else {
                preOutboundNo = preOutboundNoAndRefDocNo;
                log.info("PreOutboundNo : {}", preOutboundNo);
            }

            Optional<PreOutboundHeaderV2> isDuplicateOrder = preOutboundHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                    languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, 0L);
            if (!isDuplicateOrder.isEmpty()) {
                throw new BadRequestException("Order :" + salesOrderHeaderV2.getSalesOrderNumber() + " already processed. Reprocessing can't be allowed.");
            }

            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
            Long statusId = 39L;
            statusDescription = getStatusDescription(statusId, languageId);
            log.info("preOutboundHeader Creation Process ---------> V9 : RefDocNo is {}", refDocNumber);

            PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV9(companyCodeId, plantId, languageId,
                    warehouseId, preOutboundNo, salesOrderHeaderV2, statusId, statusDescription, description, MW_BFS);
            log.info("Create PreOutboundHeader------->" + createdPreOutboundHeader);


            List<PreOutboundLineV2> createdPreOutboundLineV2 = Collections.synchronizedList(new ArrayList<>());

            String finalPreOutboundNo = preOutboundNo;
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(salesOrderV2.getSalesOrderLine().stream()
                    .map(salesOrderLineV2 -> CompletableFuture.runAsync(() -> {
                        try {
                            createPreOutboundLineV9(companyCodeId, plantId, languageId, warehouseId, finalPreOutboundNo,
                                    salesOrderHeaderV2, salesOrderLineV2, createdPreOutboundLineV2, statusId, statusDescription, description, MW_BFS);
                        } catch (Exception e) {
                            log.error("Error processing SalesOrder Line for ASN: {}", salesOrderHeaderV2.getSalesOrderNumber(), e);
                            throw new RuntimeException(e);
                        }
                    }, executorService)).toArray(CompletableFuture[]::new));
            try {
                allFutures.join(); // Wait for all tasks to finish
            } catch (CompletionException e) {
                log.error("Exception during SalesOrder line processing: {}", e.getCause().getMessage());
                throw new BadRequestException("SalesOrder Order Processing failed: " + e.getCause().getMessage());
            }
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            preOutboundLineV2Repository.saveAll(createdPreOutboundLineV2);
            PreOutboundLineV2 preOutboundLineV2 = createdPreOutboundLineV2.get(0);
            createdPreOutboundHeader.setPartnerCode(preOutboundLineV2.getPartnerCode());
            preOutboundHeaderV2Repository.save(createdPreOutboundHeader);
            updateStatusId(salesOrderHeaderV2.getCompanyCode(), salesOrderHeaderV2.getBranchCode(), salesOrderHeaderV2.getWarehouseId(),
                    salesOrderHeaderV2.getSalesOrderNumber(), 10L);
            log.info("OutboundOrder Status 10 Updated");
        } catch (Exception e) {
            log.error("Error processing Outbound Order Lines", e);
            updateStatusId(salesOrderV2.getSalesOrderHeader().getCompanyCode(), salesOrderV2.getSalesOrderHeader().getBranchCode(), salesOrderV2.getSalesOrderHeader().getWarehouseId(),
                    salesOrderV2.getSalesOrderHeader().getSalesOrderNumber(), 100L);
            throw new BadRequestException("Outbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
            DataBaseContextHolder.clear();
        }
    }

    /**
     * @param companyCode
     * @param branchId
     * @param warehouseId
     * @param asnNumber
     * @param statusId
     */
    public void updateStatusId(String companyCode, String branchId, String warehouseId, String asnNumber, Long statusId) {
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCode, branchId, warehouseId);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);
        outboundOrderV2Repository.updateObOrderStatus(companyCode, branchId, warehouseId, asnNumber, statusId);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param salesOrderHeaderV2
     * @param salesOrderLineV2
     * @param statusId
     * @param statusDesc
     * @param desc
     * @param loginUserId
     * @return
     * @throws Exception
     */
    private PreOutboundLineV2 createPreOutboundLineV9(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                      SalesOrderHeaderV2 salesOrderHeaderV2, SalesOrderLineV2 salesOrderLineV2, List<PreOutboundLineV2> preOutboundLineV2List,
                                                      Long statusId, String statusDesc, IKeyValuePair desc, String loginUserId) throws Exception {
        try {
            PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
            BeanUtils.copyProperties(salesOrderLineV2, preOutboundLine, CommonUtils.getNullPropertyNames(salesOrderLineV2));
            preOutboundLine.setCompanyCodeId(companyCodeId);
            preOutboundLine.setPlantId(plantId);
            preOutboundLine.setLanguageId(languageId);
            preOutboundLine.setWarehouseId(warehouseId);
            preOutboundLine.setPreOutboundNo(preOutboundNo);
            // REF DOC Number
            preOutboundLine.setRefDocNumber(salesOrderHeaderV2.getPickListNumber());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);

            if (salesOrderLineV2.getManufacturerName() != null && salesOrderLineV2.getManufacturerCode() != null) {

                IImbasicData1 getImbasicData1 = imBasicData1V2Repository.getItemCodeV9(companyCodeId, languageId,
                        plantId, warehouseId, salesOrderLineV2.getSku(), salesOrderLineV2.getManufacturerName());

                preOutboundLine.setPartnerCode(getImbasicData1.getCustomerName());
                preOutboundLine.setCustomerName(getImbasicData1.getCustomerName());
                preOutboundLine.setDescription(getImbasicData1.getDescription());

                preOutboundLine.setManufacturerCode(salesOrderLineV2.getManufacturerCode());
                preOutboundLine.setManufacturerName(salesOrderLineV2.getManufacturerName());
                preOutboundLine.setManufacturerFullName(salesOrderLineV2.getManufacturerName());

            }else {

                IKeyValuePair getImbasicData1 =  imBasicData1V2Repository.getItemCodeUsingInventoryOwnerV9(companyCodeId, languageId,
                        plantId, warehouseId, salesOrderLineV2.getSku(), salesOrderLineV2.getUnitType());

                if (getImbasicData1 != null) {
                    preOutboundLine.setPartnerCode(getImbasicData1.getCustomerName());
                    preOutboundLine.setCustomerName(getImbasicData1.getCustomerName());
                    preOutboundLine.setDescription(getImbasicData1.getDescription());

                    preOutboundLine.setManufacturerCode(getImbasicData1.getManufacturerCode());
                    preOutboundLine.setManufacturerName(getImbasicData1.getManufacturerName());
                    preOutboundLine.setManufacturerFullName(getImbasicData1.getManufacturerFullName());
                }
            }

            if (preOutboundLine.getManufacturerCode() == null || preOutboundLine.getManufacturerCode().isEmpty()) {
                if (warehouseId.equalsIgnoreCase("4100")) {
                    preOutboundLine.setManufacturerCode(MFR_NAME_V9);
                    preOutboundLine.setManufacturerName(MFR_NAME_V9);
                    preOutboundLine.setManufacturerFullName(MFR_NAME_V9);
                    preOutboundLine.setPartnerCode(MFR_NAME_V9);
                } else if (warehouseId.equalsIgnoreCase("4200")) {
                    preOutboundLine.setManufacturerCode(MFR_NAME_V11);
                    preOutboundLine.setManufacturerName(MFR_NAME_V11);
                    preOutboundLine.setManufacturerFullName(MFR_NAME_V11);
                    preOutboundLine.setPartnerCode(MFR_NAME_V11);
                }else if (warehouseId.equalsIgnoreCase("4300")) {
                    preOutboundLine.setManufacturerCode(MFR_NAME_V12);
                    preOutboundLine.setManufacturerName(MFR_NAME_V12);
                    preOutboundLine.setManufacturerFullName(MFR_NAME_V12);
                    preOutboundLine.setPartnerCode(MFR_NAME_V12);
                }
            }

            // IB__LINE_NO
            preOutboundLine.setLineNumber(salesOrderLineV2.getLineReference());

            // ITM_CODE
            preOutboundLine.setItemCode(salesOrderLineV2.getSku());

            // OB_ORD_TYP_ID
            preOutboundLine.setOutboundOrderTypeId(Long.valueOf(salesOrderHeaderV2.getOrderType()));

            if (preOutboundLine.getOutboundOrderTypeId().equals(3L)) {
                preOutboundLine.setReferenceDocumentType("PICK LIST");
            }
            if (preOutboundLine.getOutboundOrderTypeId().equals(2L)) {
                preOutboundLine.setReferenceDocumentType("Purchase Return");
            }
            if (preOutboundLine.getOutboundOrderTypeId().equals(1L)) {
                preOutboundLine.setReferenceDocumentType("Transfer Out");
            }

            // STATUS_ID
            preOutboundLine.setStatusId(statusId);
            preOutboundLine.setStatusDescription(statusDesc);

            // STCK_TYP_ID
            preOutboundLine.setStockTypeId(1L);

            // SP_ST_IND_ID
            preOutboundLine.setSpecialStockIndicatorId(1L);

            preOutboundLine.setCompanyDescription(desc.getCompanyDesc());
            preOutboundLine.setPlantDescription(desc.getPlantDesc());
            preOutboundLine.setWarehouseDescription(desc.getWarehouseDesc());

            preOutboundLine.setSalesInvoiceNumber(salesOrderLineV2.getSalesOrderNo());
            preOutboundLine.setSalesOrderNumber(salesOrderHeaderV2.getSalesOrderNumber());
            preOutboundLine.setPickListNumber(salesOrderLineV2.getPickListNo());
            preOutboundLine.setTokenNumber(salesOrderHeaderV2.getTokenNumber());
            preOutboundLine.setTargetBranchCode(salesOrderHeaderV2.getBranchCode());

            // ORD_QTY
            preOutboundLine.setOrderQty(salesOrderLineV2.getOrderedQty());

            //InventoryOwner
            if (salesOrderLineV2.getMaterialNo() != null) {
                preOutboundLine.setMaterialNo(salesOrderLineV2.getMaterialNo());
            }
            //PriceSegment
            if(salesOrderLineV2.getPriceSegment() != null){
                preOutboundLine.setPriceSegment(salesOrderLineV2.getPriceSegment());
            }


            // ORD_UOM
            preOutboundLine.setOrderUom("Case");

            //GetCustomerPalletFromInventoryTable
            String customerPallet = inventoryV2Repository.getCustomerPalletV9(companyCodeId, plantId, languageId, warehouseId, salesOrderLineV2.getSku());
            log.info("customerPallet--->" + customerPallet);
            if(customerPallet != null) {
                preOutboundLine.setBrand(customerPallet);        //CustomerPallet
            }

            preOutboundLine.setDeletionIndicator(0L);
            preOutboundLine.setCreatedBy(loginUserId);
            preOutboundLine.setCreatedOn(new Date());
            log.info("Quantity Logic started ----------> ");
            setAlternateUomQuantitiesV9(preOutboundLine);
            log.info("Quantity Logic Completed ----------> ");
            preOutboundLineV2List.add(preOutboundLine);
            return preOutboundLine;
        } catch (Exception e) {
            log.error("Exception While PreoutboundLine create: " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param customerId
     * @param salesOrderNumber
     * @param outboundOrderTypeId
     * @param idMasterAuthToken
     * @return
     */
    private String getPreOutboundNoV9(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String customerId, String salesOrderNumber, Long outboundOrderTypeId, String idMasterAuthToken) {
        Optional<PreOutboundHeaderV2> orderProcessedStatus =
                preOutboundHeaderV2Repository.findTopBySalesOrderNumberAndOutboundOrderTypeIdAndCustomerIdAndDeletionIndicator(salesOrderNumber, outboundOrderTypeId, customerId, 0L);
        if (orderProcessedStatus.isPresent()) {
            log.info("------preOutboundNo---------existing----one---> " + orderProcessedStatus.get().getPreOutboundNo());
            return orderProcessedStatus.get().getPreOutboundNo();
        } else {
            // Getting PreOutboundNo from NumberRangeTable
            return getNextRangeNumber(9L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param salesOrderHeaderV2
     * @param statusId
     * @param statusDesc
     * @param desc
     * @param loginUserId
     * @return
     * @throws Exception
     */
    private PreOutboundHeaderV2 createPreOutboundHeaderV9(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                          SalesOrderHeaderV2 salesOrderHeaderV2,
                                                          Long statusId, String statusDesc, IKeyValuePair desc, String loginUserId) throws Exception {
        try {
            PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
            BeanUtils.copyProperties(salesOrderHeaderV2, preOutboundHeader, CommonUtils.getNullPropertyNames(salesOrderHeaderV2));
            preOutboundHeader.setCompanyCodeId(companyCodeId);
            preOutboundHeader.setPlantId(plantId);
            preOutboundHeader.setLanguageId(languageId);
            preOutboundHeader.setWarehouseId(warehouseId);
            preOutboundHeader.setRefDocNumber(salesOrderHeaderV2.getPickListNumber());

            preOutboundHeader.setPreOutboundNo(preOutboundNo);
            preOutboundHeader.setOutboundOrderTypeId(Long.valueOf(salesOrderHeaderV2.getOrderType()));
            preOutboundHeader.setRefDocDate(new Date());
            // REF_FIELD_1
            preOutboundHeader.setStatusId(statusId);
            preOutboundHeader.setStatusDescription(statusDesc);
            preOutboundHeader.setCompanyDescription(desc.getCompanyDesc());
            preOutboundHeader.setPlantDescription(desc.getPlantDesc());
            preOutboundHeader.setWarehouseDescription(desc.getWarehouseDesc());

            preOutboundHeader.setCustomerName(salesOrderHeaderV2.getCustomerName());
            if (preOutboundHeader.getOutboundOrderTypeId().equals(3L)) {
                preOutboundHeader.setReferenceDocumentType("PICK LIST");
            }
            if (preOutboundHeader.getOutboundOrderTypeId().equals(2L)) {
                preOutboundHeader.setReferenceDocumentType("Purchase Return");
            }
            if (preOutboundHeader.getOutboundOrderTypeId().equals(1L)) {
                preOutboundHeader.setReferenceDocumentType("Transfer Out");
            }

            preOutboundHeader.setTokenNumber(salesOrderHeaderV2.getTokenNumber());
            preOutboundHeader.setDeletionIndicator(0L);
            preOutboundHeader.setCreatedBy(loginUserId);
            preOutboundHeader.setCreatedOn(new Date());
            // Order_Text_Update
            String text = "PreOutboundHeader Created";
            outboundOrderV2Repository.updatePreOutBoundOrderText(preOutboundHeader.getOutboundOrderTypeId(), preOutboundHeader.getRefDocNumber(), text);
            log.info("PreOutbound Header Status Updated Successfully");
            return preOutboundHeader;
        } catch (Exception e) {
            log.error("Exception While PreoutboundHeader create: " + e);
            throw e;
        }
    }

    /**
     * @param preOutboundLineV2
     */
    private void setAlternateUomQuantitiesV9(PreOutboundLineV2 preOutboundLineV2) {
        try {
            Double qtyInPiece = null;
            Double qtyInCase = null;
            Double qtyInCreate = null;

            String orderUom = preOutboundLineV2.getOrderUom();
            String companyCodeId = preOutboundLineV2.getCompanyCodeId();
            String plantId = preOutboundLineV2.getPlantId();
            String warehouseId = preOutboundLineV2.getWarehouseId();
            String itemCode = preOutboundLineV2.getItemCode();

            if ("piece".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is PIECE");

                qtyInPiece = preOutboundLineV2.getOrderQty();
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                log.info("Piece Qty --- {}", preOutboundLineV2.getOrderQty());
                log.info("Case Qty ALT_UOM: {}", caseQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (preOutboundLineV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
                    qtyInCase = preOutboundLineV2.getOrderQty() / caseQty.getUomQty();
                }

                if (preOutboundLineV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = preOutboundLineV2.getOrderQty() / createQty.getUomQty();
                }

            } else if ("case".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CASE");

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                qtyInCase = preOutboundLineV2.getOrderQty();

                log.info("Case Qty --- {}", preOutboundLineV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (preOutboundLineV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = preOutboundLineV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (preOutboundLineV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = qtyInPiece / createQty.getUomQty();
                }
            } else if ("crate".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is Crate");
                qtyInCreate = preOutboundLineV2.getOrderQty();

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
                IKeyValuePair caseQy = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");

                log.info("Crate Qty --- {}", preOutboundLineV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", caseQy);

                if (preOutboundLineV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = preOutboundLineV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (preOutboundLineV2.getOrderQty() != null && caseQy != null && caseQy.getUomQty() != null) {
                    qtyInCase = qtyInPiece / caseQy.getUomQty();
                }
            }

            preOutboundLineV2.setQtyInPiece(qtyInPiece);
            preOutboundLineV2.setQtyInCase(qtyInCase);
            preOutboundLineV2.setQtyInCrate(qtyInCreate);
        } catch (Exception e) {
            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
        }
    }

    public void createOrderManagementLineV9(List<PreOutboundLineV2> preOutboundLineV2List) throws Exception {


        log.info("Inputs For OrderProcess----->" + preOutboundLineV2List);
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

        ImBasicData1 imBasicData1 = imBasicData1Repository.getImBasicData1WeightV9(preOutboundLineV2.getItemCode(),
                preOutboundLineV2.getCompanyCodeId(), preOutboundLineV2.getPlantId(),
                preOutboundLineV2.getLanguageId(), preOutboundLineV2.getWarehouseId(), preOutboundLineV2.getManufacturerName());

        Double weight = null;
        if (imBasicData1 != null && imBasicData1.getReferenceField1() != null) {
            weight = Double.valueOf(imBasicData1.getReferenceField1());
        }

        String loginUserId = MW_BFS;
        for(PreOutboundLineV2 line : preOutboundLineV2List) {
            createOrderManagementProcessV9(preOutboundLineV2.getCompanyCodeId(), preOutboundLineV2.getPlantId(),
                    preOutboundLineV2.getLanguageId(), preOutboundLineV2.getWarehouseId(), preOutboundHeaderV2, line, loginUserId,weight);
        }

        log.info("OutboundLine Creation Process Started -------------> V9 --> PreOutboundLine Size is {} ", preOutboundLineV2List.size());
        List<OutboundLineV2> outboundLineV9 = createOutboundLineV9(preOutboundLineV2List, preOutboundHeaderV2);
        outboundLineV2Repository.saveAll(outboundLineV9);
        log.info("OutboundLine Creation Process Completed -------------> V9");

        Long statusId = 41L;
        statusDescription = getStatusDescription(statusId, preOutboundLineV2.getLanguageId());
        OrderManagementHeaderV2 headerV9 = createOrderManagementHeaderV9(preOutboundHeaderV2, statusId, statusDescription, MW_AMS);
        log.info("OrderManagementHeader Creation Process ------------> V9: RefDocNo is  ----> {} ", headerV9.getRefDocNumber());

        OutboundHeaderV2 outboundHeader = createOutboundHeaderV9(preOutboundHeaderV2, preOutboundHeaderV2, statusId, statusDescription);
        log.info("outboundHeader Creation Process ----------------> V9: RefDocNo is  ----> {} ", outboundHeader.getRefDocNumber());

        createPickupHeaderV9(preOutboundHeaderV2.getCompanyCodeId(), preOutboundHeaderV2.getPlantId(), preOutboundHeaderV2.getLanguageId(),
                preOutboundHeaderV2.getWarehouseId(), preOutboundHeaderV2.getPreOutboundNo(), preOutboundHeaderV2.getRefDocNumber(), preOutboundHeaderV2, preOutboundLineV2List);
        OrderManagementHeaderV2 duplicateOrderManagementHeader = orderManagementHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                headerV9.getCompanyCodeId(), headerV9.getPlantId(), headerV9.getLanguageId(), headerV9.getWarehouseId(), headerV9.getRefDocNumber(), 0L);

        if (duplicateOrderManagementHeader == null) {
            orderManagementHeaderV2Repository.save(headerV9);
        }

        OutboundHeaderV2 duplicateOutboundHeader = outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndRefDocNumberAndWarehouseIdAndDeletionIndicator(
                outboundHeader.getCompanyCodeId(), outboundHeader.getPlantId(), outboundHeader.getLanguageId(), outboundHeader.getRefDocNumber(), outboundHeader.getWarehouseId(), 0L);
        if (duplicateOutboundHeader == null) {
            outboundHeaderV2Repository.save(outboundHeader);
        }
        log.info("All OrderManagementLines created successfully for PreOutboundNo: {}", preOutboundLineV2.getPreOutboundNo());
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
                                                PreOutboundHeaderV2 preOutboundHeaderV2, PreOutboundLineV2 preOutboundLine, String loginUserId,Double weight) throws Exception {
        try {
            OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));
            log.info("orderManagementLine ------------> V9 : " + orderManagementLine);

            Long OB_ORD_TYP_ID = preOutboundHeaderV2.getOutboundOrderTypeId();
            Long BIN_CLASS_ID;

            if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 3L || OB_ORD_TYP_ID == 11L || OB_ORD_TYP_ID == 7L || OB_ORD_TYP_ID == 1L) {
                BIN_CLASS_ID = 1L;
                updateAllocationV9(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundLine.getItemCode(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId,weight);
            }
            if (OB_ORD_TYP_ID == 2L) {
                BIN_CLASS_ID = 7L;
                updateAllocationV9(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundLine.getItemCode(), BIN_CLASS_ID, preOutboundLine.getOrderQty(), orderManagementLine, loginUserId,weight);
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
                                                    OrderManagementLineV2 orderManagementLine, String loginUserID,Double weight) throws Exception {
        try {
            log.info("OrderManagementLineV2--------->" + orderManagementLine);
            String manufacturerName = orderManagementLine.getManufacturerName();
            log.info("Quantity Logic started ----------> ");
            setAlternateUomQuantitiesV9(orderManagementLine);
            log.info("Quantity Logic completed ----------> ");
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
                log.info("PalletId is -------->" + orderManagementLine.getReferenceField1());
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
            double balanceOrderQty = orderManagementLine.getOrderQty();
            if (INV_STRATEGY.equalsIgnoreCase("FIFO")) {
                if (orderManagementLine.getOutboundOrderTypeId().equals(3L) || orderManagementLine.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, orderManagementLine.getBarcodeId(), orderManagementLine.getReferenceField1(), orderManagementLine.getManufacturerName());
                }
                if (orderManagementLine.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationWithBarcodeV9Bin7(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, orderManagementLine.getBarcodeId(), orderManagementLine.getReferenceField1(), orderManagementLine.getManufacturerName());
                }
                if (inventoryV2List == null || inventoryV2List.isEmpty()) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationBin2(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, orderManagementLine.getBarcodeId(), orderManagementLine.getReferenceField1(), orderManagementLine.getManufacturerName());
                }
                log.info("Inventory List {} in Order Allocation", inventoryV2List);

                if (inventoryV2List.isEmpty()) {
                    log.warn("No inventory available for allocation for itemCode: {}", itemCode);
                }
                Long STATUS_ID = null;
                for (InventoryV2 inventory : inventoryV2List) {
                    String barcodeId = inventory.getBarcodeId();
                    log.info("BarcodeId----->" + barcodeId);
                    Double sumOfQty = orderManagementLineV2Repository.getSumOfOrderQtyV9(
                            companyCodeId, plantId, languageId, warehouseId, itemCode, inventory.getPalletCode(), barcodeId);

                    double invAllocQty = inventory.getReferenceField4();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;
//                    double availableQty = invQty - alreadyAllocated;
                    double availableQty = invAllocQty - alreadyAllocated;
                    double allocatedQty = 0D;
//                    if(availableQty <= 0) {
//                        availableQty = 0;
//                    }
//
//
//                    log.info("BarcodeId {}, InvAllocQty {}, AlreadyAllocated {}, Available {}",
//                            barcodeId, invAllocQty, alreadyAllocated, availableQty);
//
//                    if (availableQty <= 0) {
//                        log.info("Barcode {} has no available stock. Skipping...", barcodeId);
//                        continue;
//                    }
//
//                    if (balanceOrderQty >= availableQty) {
//                        balanceOrderQty = balanceOrderQty - availableQty;
//                        allocatedQty = availableQty;
//                    } else {
//                        allocatedQty = balanceOrderQty;
//                        balanceOrderQty = 0;
//                    }

                    if (alreadyAllocated > inventory.getInventoryQuantity()) {
                        log.info("AlreadyAllocated {} InventoryQuantity {} ", alreadyAllocated, inventory.getInventoryQuantity());
                        alreadyAllocated = inventory.getInventoryQuantity();
                    }

                    if (availableQty <= 0) {
                        availableQty = 0;
                    }

                    log.info("BarcodeId {}, InvAllocQty {}, AlreadyAllocated {}, Available {}",
                            barcodeId, invAllocQty, alreadyAllocated, availableQty);

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
                    orderLine.setManufacturerCode(orderManagementLine.getManufacturerCode());
                    orderLine.setManufacturerName(orderManagementLine.getManufacturerName());
                    orderLine.setManufacturerFullName(orderManagementLine.getManufacturerName());
                    orderLine.setReferenceField10(inventory.getPriceSegment());               //NetWeight
                    orderLine.setMrp(inventory.getMrp());                                    //MRP
                    orderLine.setReferenceField6(inventory.getThreePLPartnerId());             //GrossWeight
                    orderLine.setReferenceField10(inventory.getBrand());                        //totalWeight
                    orderLine.setOrigin(inventory.getOrigin());                                 //Origin

                    String inventoryOwner = containerReceiptRepository.getInventoryOwnerV9(orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                    log.info("Fetched inventoryOwner from DB----> {}", inventoryOwner);
                    if (inventoryOwner != null) {
                        orderLine.setMaterialNo(inventoryOwner);
                        log.info("MaterialNo --------> {}", inventoryOwner);
                        log.info("MaterialNo in orderLine-----> {}", orderLine.getMaterialNo());
                    }

                    // Net Weight ---> Price Segment
                    log.info("Weight -------> {}", weight);
                    Double roundWeight = weight != null ? weight : 0D;
                    Double allocQty = orderLine.getAllocatedQty() != null ? orderLine.getAllocatedQty() : 0D;

                    log.info("roundWeight  -------> {}", roundWeight);
                    log.info("allocQty -------> {}", allocQty);

                    Double priceSegment = roundWeight * allocQty;
                    orderLine.setPriceSegment(String.valueOf(priceSegment));


                    Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderLine.getCompanyCodeId(), orderLine.getPlantId(),
                            orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getRefDocNumber(),
                            orderLine.getPreOutboundNo(), orderLine.getItemCode());
                    orderLine.setLineNumber(lineNumber);

                    OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(orderLine);
                    log.info("CreatedOrderManagementLine----------> {}",createdOrderManagementLine);
                    log.info("MaterialNo -----> {}", createdOrderManagementLine.getMaterialNo());

                    log.info("Fully allocated {} qty from barcode {}, Remaining: {}", allocatedQty, barcodeId, balanceOrderQty);

                    if (balanceOrderQty <= 0) {
                        return orderLine;
                    }
                }
            }
            if (INV_STRATEGY.equalsIgnoreCase("FEFO")) {

                if (orderManagementLine.getOutboundOrderTypeId().equals(3L) || orderManagementLine.getOutboundOrderTypeId().equals(1L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationV9(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, orderManagementLine.getReferenceField1(), orderManagementLine.getManufacturerName());
                }
                if (orderManagementLine.getOutboundOrderTypeId().equals(2L)) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationBin7V9(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, orderManagementLine.getReferenceField1(), orderManagementLine.getManufacturerName());
                }
                if (inventoryV2List == null || inventoryV2List.isEmpty()) {
                    inventoryV2List = inventoryV2Repository.getInventoryAllocationBin2V9(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, orderManagementLine.getReferenceField1(), orderManagementLine.getManufacturerName());
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

                    double invAllocQty = inventory.getReferenceField4();
                    double alreadyAllocated = (sumOfQty != null) ? sumOfQty : 0.0;
                    double availableQty = invAllocQty - alreadyAllocated;
                    double allocatedQty = 0D;

                    if(availableQty <= 0) {
                        availableQty = 0;
                    }
//
//                    log.info("BarcodeId {}, InvAllocQty {}, AlreadyAllocated {}, Available {}",
//                            barcodeId, invAllocQty, alreadyAllocated, availableQty);
//
//                    if (availableQty <= 0) {
//                        log.info("Barcode {} has no available stock. Skipping...", barcodeId);
//                        continue;
//                    }
//                    log.info("BalanceOrderQty---->" + balanceOrderQty);
//
//                    if (balanceOrderQty >= availableQty) {
//                        balanceOrderQty = balanceOrderQty - availableQty;
//                        allocatedQty = availableQty;
//                    } else {
//                        allocatedQty = balanceOrderQty;
//                        balanceOrderQty = 0;
//                    }

                    if (alreadyAllocated > inventory.getInventoryQuantity()) {
                        log.info("AlreadyAllocated {} InventoryQuantity {} ", alreadyAllocated, inventory.getInventoryQuantity());
                        alreadyAllocated = inventory.getInventoryQuantity();
                    }

                    if (availableQty <= 0) {
                        availableQty = 0;
                    }

                    log.info("BarcodeId {}, InvAllocQty {}, AlreadyAllocated {}, Available {}",
                            barcodeId, invAllocQty, alreadyAllocated, availableQty);

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
                    log.info("BalanceOrderQty---->" + balanceOrderQty);
                    log.info("AllocatedQty---->" + allocatedQty);


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
                    orderLine.setManufacturerCode(orderManagementLine.getManufacturerCode());
                    orderLine.setManufacturerName(orderManagementLine.getManufacturerName());
                    orderLine.setManufacturerFullName(orderManagementLine.getManufacturerName());
                    orderLine.setReferenceField10(inventory.getPriceSegment());               //NetWeight
                    orderLine.setMrp(inventory.getMrp());                                    //MRP
                    orderLine.setReferenceField6(inventory.getThreePLPartnerId());             //GrossWeight
                    orderLine.setReferenceField5(inventory.getBrand());                        //totalWeight

                    orderLine.setOrigin(inventory.getOrigin());                                 //Origin

                    String inventoryOwner = containerReceiptRepository.getInventoryOwnerV9(orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                    log.info("Fetched inventoryOwner DB----> {}", inventoryOwner);
                    if (inventoryOwner != null) {
                        orderLine.setMaterialNo(inventoryOwner);
                        log.info("MaterialNo --------> {}", inventoryOwner);
                        log.info("MaterialNo in orderLine-----> {}", orderLine.getMaterialNo());
                    }

                    // Net Weight ---> Price Segment
                    log.info("Weight -------> {}", weight);
                    Double roundWeight = weight != null ? weight : 0D;
                    Double allocQty = orderLine.getAllocatedQty() != null ? orderLine.getAllocatedQty() : 0D;

                    log.info("roundWeight  -------> {}", roundWeight);
                    log.info("allocQty -------> {}", allocQty);

                    Double priceSegment = roundWeight * allocQty;
                    orderLine.setPriceSegment(String.valueOf(priceSegment));

                    Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderLine.getCompanyCodeId(), orderLine.getPlantId(),
                            orderLine.getLanguageId(), orderLine.getWarehouseId(), orderLine.getRefDocNumber(),
                            orderLine.getPreOutboundNo(), orderLine.getItemCode());
                    orderLine.setLineNumber(lineNumber);

                    log.info("OrderManagementLine------>" + orderLine);
                    log.info("BalanceOrderQty---->" + balanceOrderQty);

                    OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(orderLine);
                    log.info("CreatedOrderManagementLine----------> {}",createdOrderManagementLine);
                    log.info("MaterialNo -----> {}", createdOrderManagementLine.getMaterialNo());

                    log.info("Fully allocated {} qty from barcode {}, Remaining: {}", allocatedQty, barcodeId, balanceOrderQty);

                    if (balanceOrderQty <= 0) {
                        return orderLine;
                    }
                }
            }

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);

            if (inventoryV2List == null || inventoryV2List.isEmpty() || (finalInventoryList == null || finalInventoryList.isEmpty()) || balanceOrderQty >= 0) {
                return updateOrderManagementLineV9(orderManagementLine, balanceOrderQty);
            }

            log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
            return newOrderManagementLine;
        } catch (Exception e) {
            log.error("Exception while updateAllocation V3: " + e);
            throw e;
        }
    }

    //=================BF===================================

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
    private OrderManagementLineV2 updateOrderManagementLineV9(OrderManagementLineV2 orderManagementLine, double balanceOrderQty) {
        log.info("UnAllocated Order :" + orderManagementLine.getRefDocNumber());
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setBarcodeId("");
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(balanceOrderQty);
        orderManagementLine.setAllocatedQty(0D);
        Long lineNumber = orderManagementLineV2Repository.getLineNumberV9(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(),
                orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId(), orderManagementLine.getRefDocNumber(),
                orderManagementLine.getPreOutboundNo(), orderManagementLine.getItemCode());
        orderManagementLine.setLineNumber(lineNumber);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param createdPreOutboundLine
     * @param preOutboundHeaderV2
     * @return
     * @throws Exception
     */
    private List<OutboundLineV2> createOutboundLineV9(List<PreOutboundLineV2> createdPreOutboundLine, PreOutboundHeaderV2 preOutboundHeaderV2) throws Exception {
        try {
            List<OutboundLineV2> outboundLines = new ArrayList<>();
            Long lineNo = 1L;
            for (PreOutboundLineV2 preOutboundLine : createdPreOutboundLine) {

                List<OrderManagementLineV2> orderManagementLine = orderManagementLineV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndDeletionIndicator(
                        preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(), preOutboundLine.getWarehouseId(), preOutboundLine.getRefDocNumber(), preOutboundLine.getItemCode(), 0L);
                log.info("Updated PreOutboundLine------>{} For this RefDocNo", preOutboundLine.getRefDocNumber());
                preOutboundLineV2Repository.updatePreOutboundLineV9(preOutboundLine.getCompanyCodeId(), preOutboundLine.getPlantId(), preOutboundLine.getLanguageId(),
                        preOutboundLine.getWarehouseId(), preOutboundLine.getRefDocNumber(), preOutboundLine.getPreOutboundNo(), orderManagementLine.get(0).getStatusId(), orderManagementLine.get(0).getStatusDescription());
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
                    outboundLine.setReferenceField1(dbOrderManagementLine.getPalletId());
                    outboundLine.setReferenceField6(dbOrderManagementLine.getReferenceField6());     //GrossWeight
                    outboundLine.setReferenceField10(dbOrderManagementLine.getReferenceField10());  //NetWeight
                    outboundLine.setMrp(dbOrderManagementLine.getMrp());                              //MRP
                    outboundLine.setReferenceField5(dbOrderManagementLine.getReferenceField5());       //totalWeight
                    outboundLine.setTracking(dbOrderManagementLine.getOrigin());                       //customerPallet

                    outboundLine.setReferenceField2(String.valueOf(dbOrderManagementLine.getManufacturerDate()));
                    outboundLine.setReferenceField8(String.valueOf(dbOrderManagementLine.getExpiryDate()));
                    outboundLine.setReferenceField4(dbOrderManagementLine.getPalletId());
//                    outboundLine.setLineNumber(lineNo);
                    outboundLine.setBrand(dbOrderManagementLine.getOrigin());                         //Inventory Origin

                    //InventoryOwner
                    if (dbOrderManagementLine.getMaterialNo() != null) {
                        outboundLine.setMaterialNo(dbOrderManagementLine.getMaterialNo());
                    }
                    //PriceSegment
                    if(dbOrderManagementLine.getPriceSegment() != null){
                        outboundLine.setPriceSegment(dbOrderManagementLine.getPriceSegment());
                    }

                    if (outboundLine.getOutboundOrderTypeId() == 3L) {
                        outboundLine.setCustomerType("INVOICE");
                    }
                    if (outboundLine.getOutboundOrderTypeId() == 1L) {
                        outboundLine.setCustomerType("Transfer Out");
                    }
                    if (outboundLine.getOutboundOrderTypeId() == 0L) {
                        outboundLine.setCustomerType("TRANSVERSE");
                    }

                    outboundLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                    outboundLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerName());

//                    lineNo++;
                    outboundLines.add(outboundLine);

                }
            }
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
            outboundOrderV2Repository.updateOrderManagementText(newOrderManagementHeader.getOutboundOrderTypeId(), newOrderManagementHeader.getRefDocNumber(), text);
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
            if (outboundHeader.getOutboundOrderTypeId() == 1L) {
                outboundHeader.setCustomerType("Transfer Out");
            }
            if (outboundHeader.getOutboundOrderTypeId() == 0L) {
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
                                     String refDocNumber, PreOutboundHeaderV2 preOutboundHeaderV2, List<PreOutboundLineV2> lines) throws Exception {

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
                newPickupHeader.setOrigin(orderManagementLine.getOrigin());                       //customerPallet
                newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerCode());
                newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());

                // Setting inventoryOwner and Weight(pricesegment)
                newPickupHeader.setMaterialNo(orderManagementLine.getMaterialNo());
                newPickupHeader.setPriceSegment(orderManagementLine.getPriceSegment());

                PickupHeaderV2 createdPickupHeader = createOutboundOrderProcessingPickupHeaderV9(newPickupHeader, orderManagementLine.getPickupCreatedBy());
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

    /**
     * @param newPickupHeader
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public PickupHeaderV2 createOutboundOrderProcessingPickupHeaderV9(PickupHeaderV2 newPickupHeader, String loginUserID) throws Exception {
        try {
            PickupHeaderV2 dbPickupHeader = new PickupHeaderV2();
            log.info("newPickupHeader : " + newPickupHeader);
            BeanUtils.copyProperties(newPickupHeader, dbPickupHeader, CommonUtils.getNullPropertyNames(newPickupHeader));

            IKeyValuePair description = stagingLineV2Repository.getDescription(dbPickupHeader.getCompanyCodeId(),
                    dbPickupHeader.getLanguageId(),
                    dbPickupHeader.getPlantId(),
                    dbPickupHeader.getWarehouseId());

            if (dbPickupHeader.getStatusId() != null) {
                statusDescription = stagingLineV2Repository.getStatusDescription(dbPickupHeader.getStatusId(), dbPickupHeader.getLanguageId());
                dbPickupHeader.setStatusDescription(statusDescription);
            }

            dbPickupHeader.setCompanyDescription(description.getCompanyDesc());
            dbPickupHeader.setPlantDescription(description.getPlantDesc());
            dbPickupHeader.setWarehouseDescription(description.getWarehouseDesc());

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

            String customerName = getCustomerName(dbPickupHeader.getCompanyCodeId(), dbPickupHeader.getPlantId(),
                    dbPickupHeader.getLanguageId(), dbPickupHeader.getWarehouseId(),
                    dbPickupHeader.getCustomerCode());
            if (customerName != null) {
                dbPickupHeader.setCustomerName(customerName);
            }

            dbPickupHeader.setManufacturerCode(newPickupHeader.getManufacturerCode());
            dbPickupHeader.setManufacturerName(newPickupHeader.getManufacturerName());

            dbPickupHeader.setDeletionIndicator(0L);
            dbPickupHeader.setPickupCreatedBy(loginUserID);
            dbPickupHeader.setPickupCreatedOn(new Date());
            PickupHeaderV2 pickupHeaderV2 = pickupHeaderV2Repository.save(dbPickupHeader);

            return pickupHeaderV2;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param customerCode
     * @return
     */
    public String getCustomerName(String companyCodeId, String plantId, String languageId, String warehouseId, String customerCode) {
        return stagingLineV2Repository.getCustomerName(companyCodeId, plantId, languageId, warehouseId, customerCode);
    }
}
