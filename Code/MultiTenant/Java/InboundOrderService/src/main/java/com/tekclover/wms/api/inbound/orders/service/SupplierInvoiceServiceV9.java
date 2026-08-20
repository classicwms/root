package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.controller.InboundOrderRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.inbound.containerreceipt.v2.ContainerReceiptV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.ASNV9;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.ASNV9Header;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.ASNV9Line;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupplierInvoiceServiceV9 extends BaseService {

    private final RepositoryProvider repo;
    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;
    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;
    @Autowired
    private ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    ContainerReceiptV2Repository containerReceiptV2Repository;

    private int counter = 1; // starts at M00001


    @Autowired
    ErrorLogService errorLogService;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveASNV9(ASNV9 asnv2) {
        try {
            ASNV9Header asnV2Header = asnv2.getAsnHeader();
            List<ASNV9Line> asnLineV2s = asnv2.getAsnLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(asnV2Header, apiHeader, CommonUtils.getNullPropertyNames(asnV2Header));
            apiHeader.setOrderId(asnV2Header.getAsnNumber());
            apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
            apiHeader.setBranchCode(asnV2Header.getBranchCode());
            apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());

            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
            apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());

            apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
            apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
            apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());
            apiHeader.setCustomerId(asnV2Header.getCustomerId());
            apiHeader.setCustomerName(asnV2Header.getCustomerName());

            if (asnV2Header.getWareHouseId() != null && !asnV2Header.getWareHouseId().isBlank()) {
                apiHeader.setWarehouseID(asnV2Header.getWareHouseId());
            } else {
                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                asnV2Header.getCompanyCode(),
                                asnV2Header.getBranchCode(),
                                asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + dbWarehouse);
                apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            }

            if (asnV2Header.getInboundOrderTypeId() != null) {
                apiHeader.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
            } else {
                apiHeader.setInboundOrderTypeId(1L);                                            //Default
            }
            if (asnV2Header.getInboundOrderTypeId().equals(1L)) {
                apiHeader.setRefDocumentType("Supplier Invoice");
            }
            if (asnV2Header.getInboundOrderTypeId().equals(2L)) {
                apiHeader.setRefDocumentType("Customer Returns");
            }
            if (asnV2Header.getInboundOrderTypeId().equals(4L)) {
                apiHeader.setRefDocumentType("Transfer In");
            }

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNV9Line asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));
                apiLine.setLineReference(Long.valueOf(asnLineV2.getLineReference()));            // IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku().trim());                            // ITM_CODE
                apiLine.setBarcodeId(asnLineV2.getBarcodeId());
                apiLine.setItemText(asnLineV2.getSkuText());                // ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());            // CONT_NO
                apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE

                if(asnLineV2.getManufacturerCode() != null && asnLineV2.getManufacturerName() != null){
                    if(asnv2.getAsnHeader().getWareHouseId().equalsIgnoreCase("4100")) {
                        asnLineV2.setManufacturerCode(asnLineV2.getManufacturerCode());
                        asnLineV2.setManufacturerName(asnLineV2.getManufacturerName());
                        asnLineV2.setManufacturerFullName(asnLineV2.getManufacturerName());
                        asnLineV2.setExpectedQty(asnLineV2.getExpectedQty());
                    } else if(asnv2.getAsnHeader().getWareHouseId().equalsIgnoreCase("4200")) {
                        asnLineV2.setManufacturerCode(asnLineV2.getManufacturerCode());
                        asnLineV2.setManufacturerName(asnLineV2.getManufacturerName());
                        asnLineV2.setManufacturerFullName(asnLineV2.getManufacturerName());
                        asnLineV2.setExpectedQty(asnLineV2.getExpectedQty());
                     }else if(asnv2.getAsnHeader().getWareHouseId().equalsIgnoreCase("4300")) {
                        asnLineV2.setManufacturerCode(asnLineV2.getManufacturerCode());
                        asnLineV2.setManufacturerName(asnLineV2.getManufacturerName());
                        asnLineV2.setManufacturerFullName(asnLineV2.getManufacturerName());
                        asnLineV2.setExpectedQty(asnLineV2.getExpectedQty());
                    }
                }else {
                    if (asnv2.getAsnHeader().getWareHouseId().equalsIgnoreCase("4100")) {
                        asnLineV2.setManufacturerCode(MFR_NAME_V9);
                        asnLineV2.setManufacturerName(MFR_NAME_V9);
                        asnLineV2.setManufacturerFullName(MFR_NAME_V9);
                    } else if (asnv2.getAsnHeader().getWareHouseId().equalsIgnoreCase("4200")) {
                        asnLineV2.setManufacturerCode(MFR_NAME_V11);
                        asnLineV2.setManufacturerName(MFR_NAME_V11);
                        asnLineV2.setManufacturerFullName(MFR_NAME_V11);
                    } else if (asnv2.getAsnHeader().getWareHouseId().equalsIgnoreCase("4300")) {
                        asnLineV2.setManufacturerCode(MFR_NAME_V12);
                        asnLineV2.setManufacturerName(MFR_NAME_V12);
                        asnLineV2.setManufacturerFullName(MFR_NAME_V12);
                    }
                }
//                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
//                apiLine.setSupplierName(asnLineV2.getSupplierName());
//                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());

                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
//                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
//                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(1L);                                            //Default
                }

//                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setIsCancelled(asnLineV2.getIsCancelled());
                apiLine.setIsCompleted(asnLineV2.getIsCompleted());

                apiLine.setMiddlewareHeaderId(asnLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(asnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());


                apiLine.setStoreID(asnLineV2.getNetWeight());                  //NetWeight
                apiLine.setMiddlewareTable(asnLineV2.getGrossWeight());        //GrossWeight
                apiLine.setBrand(asnLineV2.getTotalWeight());                  //TotalWeight
                apiLine.setMrp(asnLineV2.getMrp());                         //MRP

                if (asnLineV2.getExpectedDate() != null) {
                    if (asnLineV2.getExpectedDate().contains("-")) {
                        // EA_DATE
                        try {
                            Date reqDelDate = new Date();
                            if (asnLineV2.getExpectedDate().length() > 10) {
                                reqDelDate = DateUtils.convertStringToDateWithTime(asnLineV2.getExpectedDate());
                            }
                            if (asnLineV2.getExpectedDate().length() == 10) {
                                reqDelDate = DateUtils.convertStringToDate2(asnLineV2.getExpectedDate());
                            }
                            apiLine.setExpectedDate(reqDelDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BadRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                    if (asnLineV2.getExpectedDate().contains("/")) {
                        // EA_DATE
                        try {
                            ZoneId defaultZoneId = ZoneId.systemDefault();
                            String sdate = asnLineV2.getExpectedDate();
                            String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
                            String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
                            secondHalf = "/20" + secondHalf;
                            sdate = firstHalf + secondHalf;
                            log.info("sdate--------> : " + sdate);

                            LocalDate localDate = DateUtils.dateConv2(sdate);
                            log.info("localDate--------> : " + localDate);
                            Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
                            apiLine.setExpectedDate(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                        }
                    }
                }
                apiLine.setOrderedQty(asnLineV2.getExpectedQty());                // ORD_QTY
                apiLine.setUom(asnLineV2.getUom());
                apiLine.setPackQty(asnLineV2.getPackQty());                    // ITM_CASE_QTY
//                apiLine.setNoPairs(asnLineV2.getNoPairs());
                apiLine.setVehicleNo(asnLineV2.getVehicleNo());
                apiLine.setVehicleUnloadingDate(asnLineV2.getVehicleUnloadingDate());
                apiLine.setVehicleReportingDate(asnLineV2.getVehicleReportingDate());
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());
            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = createInboundOrdersV9(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = createInboundOrdersV9(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    //For Upload
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveASNUploadV9(ASNV2 asnv2) {
        try {
            ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
            List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(asnV2Header, apiHeader, CommonUtils.getNullPropertyNames(asnV2Header));
            apiHeader.setOrderId(asnV2Header.getAsnNumber());
            apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
            apiHeader.setBranchCode(asnV2Header.getBranchCode());
            apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());

            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
            apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());

            apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
            apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
            apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());
            apiHeader.setCustomerId(asnV2Header.getCustomerId());
            apiHeader.setCustomerName(asnV2Header.getCustomerName());

            if (asnV2Header.getWarehouseId() != null && !asnV2Header.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(asnV2Header.getWarehouseId());
            } else {
                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                asnV2Header.getCompanyCode(),
                                asnV2Header.getBranchCode(),
                                asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + dbWarehouse);
                apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            }

            if (asnV2Header.getInboundOrderTypeId() != null) {
                apiHeader.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
            } else {
                apiHeader.setInboundOrderTypeId(1L);                                            //Default
            }

            if (apiHeader.getInboundOrderTypeId().equals(1L)) {
                apiHeader.setRefDocumentType("Supplier Invoice");
            }
            if (apiHeader.getInboundOrderTypeId().equals(4L)) {
                apiHeader.setRefDocumentType("Transfer In");
            }

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNLineV2 asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));
                apiLine.setLineReference(asnLineV2.getLineReference());            // IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku().trim());                            // ITM_CODE
                apiLine.setBarcodeId(asnLineV2.getBarcodeId().trim());
                apiLine.setItemText(asnLineV2.getSkuDescription());                // ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());            // CONT_NO
                apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
                if(apiHeader.getWarehouseID().equalsIgnoreCase("4100")) {
                    apiLine.setManufacturerName(MFR_NAME_V9);        // BRAND_NM
                    apiLine.setManufacturerCode(MFR_NAME_V9);
                    apiLine.setManufacturerFullName(MFR_NAME_V9);
                }else if(apiHeader.getWarehouseID().equalsIgnoreCase("4200")){
                    apiLine.setManufacturerName(MFR_NAME_V11);        // BRAND_NM
                    apiLine.setManufacturerCode(MFR_NAME_V11);
                    apiLine.setManufacturerFullName(MFR_NAME_V11);
                }else if(apiHeader.getWarehouseID().equalsIgnoreCase("4300")){
                    apiLine.setManufacturerName(MFR_NAME_V12);        // BRAND_NM
                    apiLine.setManufacturerCode(MFR_NAME_V12);
                    apiLine.setManufacturerFullName(MFR_NAME_V12);
                }
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());


                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(1L);                                            //Default
                }

                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setIsCancelled(asnLineV2.getIsCancelled());
                apiLine.setIsCompleted(asnLineV2.getIsCompleted());

                apiLine.setMiddlewareHeaderId(asnLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(asnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());

                if (asnLineV2.getExpectedDate() != null) {
                    if (asnLineV2.getExpectedDate().contains("-")) {
                        // EA_DATE
                        try {
                            Date reqDelDate = new Date();
                            if (asnLineV2.getExpectedDate().length() > 10) {
                                reqDelDate = DateUtils.convertStringToDateWithTime(asnLineV2.getExpectedDate());
                            }
                            if (asnLineV2.getExpectedDate().length() == 10) {
                                reqDelDate = DateUtils.convertStringToDate2(asnLineV2.getExpectedDate());
                            }
                            apiLine.setExpectedDate(reqDelDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BadRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                    if (asnLineV2.getExpectedDate().contains("/")) {
                        // EA_DATE
                        try {
                            ZoneId defaultZoneId = ZoneId.systemDefault();
                            String sdate = asnLineV2.getExpectedDate();
                            String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
                            String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
                            secondHalf = "/20" + secondHalf;
                            sdate = firstHalf + secondHalf;
                            log.info("sdate--------> : " + sdate);

                            LocalDate localDate = DateUtils.dateConv2(sdate);
                            log.info("localDate--------> : " + localDate);
                            Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
                            apiLine.setExpectedDate(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
                        }
                    }
                }
                apiLine.setOrderedQty(asnLineV2.getExpectedQty());                // ORD_QTY
                apiLine.setUom(asnLineV2.getUom());
                apiLine.setPackQty(asnLineV2.getPackQty());                    // ITM_CASE_QTY
                apiLine.setNoPairs(asnLineV2.getNoPairs());
                apiLine.setVehicleNo(asnLineV2.getVehicleNo());
                apiLine.setVehicleUnloadingDate(asnLineV2.getVehicleUnloadingDate());
                apiLine.setVehicleReportingDate(asnLineV2.getVehicleReportingDate());
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());
            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                apiHeader.setExecuted(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = createInboundOrders(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = createInboundOrders(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param orderV2
     * @return
     */
    public InboundOrderV2 createInboundOrders(InboundOrderV2 orderV2) {
        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.
                findByCompanyCodeAndBranchCodeAndWarehouseIDAndRefDocumentNoAndInboundOrderTypeId(
                        orderV2.getCompanyCode(), orderV2.getBranchCode(), orderV2.getWarehouseID(),
                        orderV2.getOrderId(), orderV2.getInboundOrderTypeId());
        if (dbInboundOrder != null) {
            throw new BadRequestException("Order is getting Duplicated");
        }
        return inboundOrderV2Repository.save(orderV2);
    }

    /**
     * @param orderV2
     * @return
     */
    public InboundOrderV2 createInboundOrdersV9(InboundOrderV2 orderV2) {

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(orderV2.getCompanyCode(), orderV2.getBranchCode(), orderV2.getWarehouseID());
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);

        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.
                findByCompanyCodeAndBranchCodeAndWarehouseIDAndRefDocumentNoAndInboundOrderTypeId(
                        orderV2.getCompanyCode(), orderV2.getBranchCode(), orderV2.getWarehouseID(),
                        orderV2.getOrderId(), orderV2.getInboundOrderTypeId());
        if (dbInboundOrder != null) {
            throw new BadRequestException("Order is getting Duplicated");
        }
        return inboundOrderV2Repository.save(orderV2);
    }

    @Async("asyncExecutor")
    public void inboundOrderV9(List<ASNV9> asnv2List) throws Exception {
        for (ASNV9 asn : asnv2List) {
            processInboundOrder(asn);
        }
    }

    @Async("asyncExecutor")
    public void inboundOrderUploadV9(List<ASNV2> asnv2List) throws Exception {
        for (ASNV2 asn : asnv2List) {
            processInboundOrderUploadV9(asn);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processInboundOrder(ASNV9 asnv2) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            ASNV9Header headerV2 = asnv2.getAsnHeader();
            List<ASNV9Line> lineV2List = asnv2.getAsnLine();
            String companyCode = headerV2.getCompanyCode();
            String plantId = headerV2.getBranchCode();
            String warehouseId = headerV2.getWareHouseId();
            String languageId = headerV2.getLanguageId();
//            String weight = lineV2List.get(0).getPriceSegment();

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);
            // Description_Set
            IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
            String companyText = description.getCompanyDesc();
            String plantText = description.getPlantDesc();
            String warehouseText = description.getWarehouseDesc();

            String idMasterAuthToken = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            Long statusId = 13L;

            // Getting PreInboundNo from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo : " + preInboundNo);
            statusDescription = getStatusDescription(statusId, languageId);

            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndCustomerIdAndDeletionIndicator(
                            companyCode, plantId, languageId, warehouseId, headerV2.getAsnNumber(), preInboundNo, headerV2.getCustomerId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + headerV2.getAsnNumber() + " already processed. Reprocessing can't be allowed.");
            }
            Date poDate = lineV2List.get(0).getReceivedDate();
            // Step 1: Create headers before line processing
            PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV9(
                    companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, MFR_NAME_V9);
            log.info("PreInboundHeader created: {}", preInboundHeader);

            repo.preInboundHeaderV2Repository.save(preInboundHeader);
            // Collections for batch saving
            List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<ImBasicData1V2> imBasicData1V2List = Collections.synchronizedList(new ArrayList<>());

            // Process lines in parallel
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(asnv2.getAsnLine().stream()
                    .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                        try {
                            createPreInboundLineV9(asnv2, asnLineV2, preInboundHeader, preInboundLineList, imBasicData1V2List);
                        } catch (Exception e) {
                            log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
                            throw new RuntimeException(e);
                        }
                    }, executorService)).toArray(CompletableFuture[]::new));
            try {
                allFutures.join(); // Wait for all tasks to finish
            } catch (CompletionException e) {
                log.error("Exception during ASN line processing: {}", e.getCause().getMessage());
                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
            }

            // Batch Save All Records
            if (!imBasicData1V2List.isEmpty()) {
                repo.imBasicData1V2Repository.saveAll(imBasicData1V2List);
            }
            repo.preInboundLineV2Repository.saveAll(preInboundLineList);
            updateStatusId(headerV2.getCompanyCode(), headerV2.getBranchCode(), headerV2.getWareHouseId(),
                    headerV2.getAsnNumber(), 10L);
            log.info("InboundOrder Status 10 Updated");

        } catch (Exception e) {
            log.error("Error processing inbound ASN Lines", e);

            errorLogService.createProcessInboundReceivedV9(asnv2, e.getMessage());
            updateStatusId(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWareHouseId(),
                    asnv2.getAsnHeader().getAsnNumber(), 100L);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
            DataBaseContextHolder.clear();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processInboundOrderUploadV9(ASNV2 asnv2) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
            List<ASNLineV2> lineV2List = asnv2.getAsnLine();
            String companyCode = headerV2.getCompanyCode();
            String plantId = headerV2.getBranchCode();
            String warehouseId = headerV2.getWarehouseId();
            String languageId = headerV2.getLanguageId();
//            String weight = lineV2List.get(0).getPriceSegment();

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);


            // Description_Set
            IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
            String companyText = description.getCompanyDesc();
            String plantText = description.getPlantDesc();
            String warehouseText = description.getWarehouseDesc();

            String idMasterAuthToken = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            Long statusId = 13L;

            // Getting PreInboundNo from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo : " + preInboundNo);
            statusDescription = getStatusDescription(statusId, languageId);

            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndCustomerCodeAndDeletionIndicator(
                            companyCode, plantId, languageId, warehouseId, headerV2.getAsnNumber(), preInboundNo, headerV2.getSupplierCode(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + headerV2.getAsnNumber() + " already processed. Reprocessing can't be allowed.");
            }
            Date poDate = lineV2List.get(0).getReceivedDate();
            // Step 1: Create headers before line processing
            PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderUploadV9(
                    companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, MFR_NAME_V9);
            log.info("PreInboundHeader created: {}", preInboundHeader);

            repo.preInboundHeaderV2Repository.save(preInboundHeader);
            // Collections for batch saving
            List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<ImBasicData1V2> imBasicData1V2List = Collections.synchronizedList(new ArrayList<>());

            // Process lines in parallel
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(asnv2.getAsnLine().stream()
                    .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                        try {
                            createPreInboundLineUploadV9(asnv2, asnLineV2, preInboundHeader, preInboundLineList, imBasicData1V2List);
                        } catch (Exception e) {
                            log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
                            throw new RuntimeException(e);
                        }
                    }, executorService)).toArray(CompletableFuture[]::new));
            try {
                allFutures.join(); // Wait for all tasks to finish
            } catch (CompletionException e) {
                log.error("Exception during ASN line processing: {}", e.getCause().getMessage());
                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
            }

            // Batch Save All Records
            if (!imBasicData1V2List.isEmpty()) {
                repo.imBasicData1V2Repository.saveAll(imBasicData1V2List);
            }
            repo.preInboundLineV2Repository.saveAll(preInboundLineList);
            updateStatusId(headerV2.getCompanyCode(), headerV2.getBranchCode(), headerV2.getWarehouseId(),
                    headerV2.getAsnNumber(), 10L);
            log.info("InboundOrder Status 10 Updated");

        } catch (Exception e) {
            log.error("Error processing inbound ASN Lines", e);

            errorLogService.createProcessInboundReceivedV2(asnv2, e.getMessage());
            updateStatusId(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId(),
                    asnv2.getAsnHeader().getAsnNumber(), 100L);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
            DataBaseContextHolder.clear();
        }
    }

    /**
     * @param companyId
     * @param languageId
     * @param plantId
     * @param preInboundNo
     * @param asnHeaderV2
     * @param warehouseId
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @param mfrName
     * @return
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV9(String companyId, String languageId, String plantId, String preInboundNo, ASNV9Header asnHeaderV2,
                                                              String warehouseId, String companyText, String plantText, String warehouseText, String mfrName) {
        try {
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(asnHeaderV2, preInboundHeader, CommonUtils.getNullPropertyNames(asnHeaderV2));
            preInboundHeader.setLanguageId(languageId);
            preInboundHeader.setWarehouseId(warehouseId);
            preInboundHeader.setCompanyCode(companyId);
            preInboundHeader.setPlantId(plantId);
            preInboundHeader.setRefDocNumber(asnHeaderV2.getAsnNumber());
            preInboundHeader.setPreInboundNo(preInboundNo);                  // PRE_IB_NO
            if (asnHeaderV2.getInboundOrderTypeId() != null) {
                preInboundHeader.setInboundOrderTypeId(asnHeaderV2.getInboundOrderTypeId());                      // IB_ORD_TYP_ID
            } else {
                preInboundHeader.setInboundOrderTypeId(1L);
            }
            if (asnHeaderV2.getInboundOrderTypeId().equals(1L)) {
                preInboundHeader.setReferenceDocumentType("Supplier Invoice");   // REF_DOC_TYP - Hard Coded Value "ASN"
                preInboundHeader.setTransferRequestType("Supplier Invoice");
            }
            if (asnHeaderV2.getInboundOrderTypeId().equals(2L)) {
                preInboundHeader.setReferenceDocumentType("Customer Returns");
                preInboundHeader.setTransferRequestType("Customer Returns");
            }

            if (asnHeaderV2.getInboundOrderTypeId().equals(4L)) {
                preInboundHeader.setReferenceDocumentType("Transfer In");
                preInboundHeader.setTransferRequestType("Transfer In");
            }

            preInboundHeader.setRefDocDate(new Date());                      // REF_DOC_DATE
            preInboundHeader.setStatusId(5L);
            statusDescription = repo.stagingLineV2Repository.getStatusDescription(5L, languageId);
            preInboundHeader.setStatusDescription(statusDescription);
            preInboundHeader.setCompanyDescription(companyText);
            preInboundHeader.setPlantDescription(plantText);
            preInboundHeader.setWarehouseDescription(warehouseText);
            preInboundHeader.setMiddlewareId(String.valueOf(asnHeaderV2.getMiddlewareId()));
            preInboundHeader.setMiddlewareTable(asnHeaderV2.getMiddlewareTable());
//            preInboundHeader.setManufacturerFullName(mfrName);
            if (asnHeaderV2.getCustomerId() != null) {
                preInboundHeader.setCustomerId(asnHeaderV2.getCustomerId());
            }
            if (asnHeaderV2.getCustomerName() != null) {
                preInboundHeader.setCustomerName(asnHeaderV2.getCustomerName());
            }

            preInboundHeader.setTransferOrderDate(new Date());
            preInboundHeader.setSourceBranchCode(asnHeaderV2.getBranchCode());
            preInboundHeader.setSourceCompanyCode(asnHeaderV2.getCompanyCode());
            preInboundHeader.setMUpdatedOn(asnHeaderV2.getUpdatedOn());

            //Get container Receipt table

            ContainerReceiptV2 containerReceipt = containerReceiptV2Repository.getContainerReceipt(preInboundHeader.getCompanyCode(), preInboundHeader.getLanguageId(),
                    preInboundHeader.getPlantId(), preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber());
            log.info("Container Receipt Values --------------> {} ", containerReceipt);
            preInboundHeader.setReferenceField1(containerReceipt.getReferenceField1());
            preInboundHeader.setReferenceField2(containerReceipt.getReferenceField2());
            preInboundHeader.setReferenceField3(containerReceipt.getReferenceField3());
            preInboundHeader.setReferenceField4(containerReceipt.getReferenceField4());
            preInboundHeader.setReferenceField5(containerReceipt.getReferenceField5());
            preInboundHeader.setReferenceField7(containerReceipt.getReferenceField7());
            preInboundHeader.setReferenceField8(containerReceipt.getReferenceField8());
            preInboundHeader.setReferenceField9(containerReceipt.getReferenceField9());
            preInboundHeader.setReferenceField10(containerReceipt.getReferenceField10());
            preInboundHeader.setReferenceField6(containerReceipt.getReferenceField30());


            preInboundHeader.setDeletionIndicator(0L);
            preInboundHeader.setCreatedBy("MW_AMS");
            preInboundHeader.setCreatedOn(new Date());


            // IB_Order
            String preInbound = "PreInbound Created";
            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
            log.info("Update Inbound Order Update Successfully");
            return preInboundHeader;
        } catch (Exception e) {
            log.info("PreInboundHeader Creation Failed -----------> " + e.getMessage());
            throw new BadRequestException("PreInboundHeader Failed -----------------> " + e);
        }
    }

    /**
     * @param asnv2
     * @param asnLineV2
     * @param preInboundHeader
     * @param preInboundLineList
     * @param imBasicDataList
     */
    public void createPreInboundLineV9(ASNV9 asnv2, ASNV9Line asnLineV2, PreInboundHeaderEntityV2 preInboundHeader,
                                       List<PreInboundLineEntityV2> preInboundLineList, List<ImBasicData1V2> imBasicDataList) {

        log.info("ASNV9 ----->" + asnv2);
        log.info("asnLineV2 ----->" + asnLineV2);

        ASNV9Header headerV2 = asnv2.getAsnHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();

        if(asnLineV2.getManufacturerCode() != null && asnLineV2.getManufacturerName() != null){
            if(warehouseId.equalsIgnoreCase("4100")) {
                asnLineV2.setManufacturerCode(asnLineV2.getManufacturerCode());
                asnLineV2.setManufacturerName(asnLineV2.getManufacturerName());
                asnLineV2.setManufacturerFullName(asnLineV2.getManufacturerName());
            } else if(warehouseId.equalsIgnoreCase("4200")) {
                asnLineV2.setManufacturerCode(asnLineV2.getManufacturerCode());
                asnLineV2.setManufacturerName(asnLineV2.getManufacturerName());
                asnLineV2.setManufacturerFullName(asnLineV2.getManufacturerName());
            } else if(warehouseId.equalsIgnoreCase("4300")) {
                asnLineV2.setManufacturerCode(asnLineV2.getManufacturerCode());
                asnLineV2.setManufacturerName(asnLineV2.getManufacturerName());
                asnLineV2.setManufacturerFullName(asnLineV2.getManufacturerName());
            }
        }else {
            if (warehouseId.equalsIgnoreCase("4100")) {
                asnLineV2.setManufacturerCode(MFR_NAME_V9);
                asnLineV2.setManufacturerName(MFR_NAME_V9);
                asnLineV2.setManufacturerFullName(MFR_NAME_V9);
            } else if (warehouseId.equalsIgnoreCase("4200")) {
                asnLineV2.setManufacturerCode(MFR_NAME_V11);
                asnLineV2.setManufacturerName(MFR_NAME_V11);
                asnLineV2.setManufacturerFullName(MFR_NAME_V11);
            } else if (warehouseId.equalsIgnoreCase("4300")) {
                asnLineV2.setManufacturerCode(MFR_NAME_V12);
                asnLineV2.setManufacturerName(MFR_NAME_V12);
                asnLineV2.setManufacturerFullName(MFR_NAME_V12);
            }
        }
        asnLineV2.setExpectedQty(asnLineV2.getExpectedQty());
//        asnLineV2.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);

        log.info("Input for ImbasicData1 : Lang_id = {} and c_id = {} and plant_id = {} and wh_id = {} and ITM_CODE = {} and MFR_PART = {} ", languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName());

        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);

        log.info("imBasicData1 -----> {}", imBasicData1);

//        if (imBasicData1 == null) {
//            imBasicData1 = new ImBasicData1V2();
//            imBasicData1.setLanguageId(languageId);
//            imBasicData1.setWarehouseId(warehouseId);
//            imBasicData1.setCompanyCodeId(companyCode);
//            imBasicData1.setPlantId(plantId);
//            imBasicData1.setItemCode(asnLineV2.getSku());
//            imBasicData1.setUomId(asnLineV2.getUom());
//            imBasicData1.setDescription(asnLineV2.getSkuText());
//            imBasicData1.setManufacturerPartNo(asnLineV2.getManufacturerName());
//            imBasicData1.setManufacturerName(asnLineV2.getManufacturerName());
//            imBasicData1.setCapacityCheck(false);
//            imBasicData1.setDeletionIndicator(0L);
//            imBasicData1.setStatusId(1L);
////            imBasicData1.setReferenceField3(asnLineV2.getPriceSegment());
//            imBasicData1.setCompanyDescription(preInboundHeader.getCompanyDescription());
//            imBasicData1.setPlantDescription(preInboundHeader.getPlantDescription());
//            imBasicData1.setWarehouseDescription(preInboundHeader.getWarehouseDescription());
//            imBasicDataList.add(imBasicData1); // Collect for batch save
//        }

        String inventoryOwner = null;
        String priceSegment = null;
        if (imBasicData1.getReferenceField2() != null) {
            inventoryOwner = imBasicData1.getReferenceField2();
        }
        if(imBasicData1.getReferenceField1() != null) {
            priceSegment = imBasicData1.getReferenceField1();
        }

        PreInboundLineEntityV2 preInboundLine = createPreInboundLine(companyCode, plantId, languageId,
                preInboundHeader.getPreInboundNo(), headerV2, asnLineV2, warehouseId, preInboundHeader.getCompanyDescription(),
                preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription(), inventoryOwner, priceSegment);
        preInboundLineList.add(preInboundLine);

    }

    public void createPreInboundLineUploadV9(ASNV2 asnv2, ASNLineV2 asnLineV2, PreInboundHeaderEntityV2 preInboundHeader,
                                             List<PreInboundLineEntityV2> preInboundLineList, List<ImBasicData1V2> imBasicDataList) {

        ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();

        if(warehouseId.equalsIgnoreCase("4100")) {
            asnLineV2.setManufacturerCode(MFR_NAME_V9);
            asnLineV2.setManufacturerName(MFR_NAME_V9);
            asnLineV2.setManufacturerFullName(MFR_NAME_V9);
        }else if(warehouseId.equalsIgnoreCase("4200")){
            asnLineV2.setManufacturerCode(MFR_NAME_V11);
            asnLineV2.setManufacturerName(MFR_NAME_V11);
            asnLineV2.setManufacturerFullName(MFR_NAME_V11);
        } else if(warehouseId.equalsIgnoreCase("4300")){
            asnLineV2.setManufacturerCode(MFR_NAME_V12);
            asnLineV2.setManufacturerName(MFR_NAME_V12);
            asnLineV2.setManufacturerFullName(MFR_NAME_V12);
        }
        asnLineV2.setExpectedQty(asnLineV2.getExpectedQty());
        asnLineV2.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);

        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);

        if (imBasicData1 == null) {
            imBasicData1 = new ImBasicData1V2();
            imBasicData1.setLanguageId(languageId);
            imBasicData1.setWarehouseId(warehouseId);
            imBasicData1.setCompanyCodeId(companyCode);
            imBasicData1.setPlantId(plantId);
            imBasicData1.setItemCode(asnLineV2.getSku());
            imBasicData1.setUomId(asnLineV2.getUom());
            imBasicData1.setDescription(asnLineV2.getSkuDescription());
            imBasicData1.setManufacturerPartNo(asnLineV2.getManufacturerName());
            imBasicData1.setManufacturerName(asnLineV2.getManufacturerName());
            imBasicData1.setCapacityCheck(false);
            imBasicData1.setDeletionIndicator(0L);
            imBasicData1.setStatusId(1L);
            imBasicData1.setReferenceField3(asnLineV2.getPriceSegment());
            imBasicData1.setCompanyDescription(preInboundHeader.getCompanyDescription());
            imBasicData1.setPlantDescription(preInboundHeader.getPlantDescription());
            imBasicData1.setWarehouseDescription(preInboundHeader.getWarehouseDescription());
            imBasicDataList.add(imBasicData1); // Collect for batch save
        }

        PreInboundLineEntityV2 preInboundLine = createPreInboundLineV9(companyCode, plantId, languageId,
                preInboundHeader.getPreInboundNo(), headerV2, asnLineV2, warehouseId, preInboundHeader.getCompanyDescription(),
                preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription());
        preInboundLineList.add(preInboundLine);

    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param preInboundNo
     * @param asnHeaderV2
     * @param asnLineV2
     * @param warehouseId
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @return
     */
    private PreInboundLineEntityV2 createPreInboundLine(String companyCode, String plantId, String languageId, String preInboundNo, ASNV9Header asnHeaderV2,
                                                        ASNV9Line asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText,
                                                        String inventoryOwner, String referenceField1) {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(asnLineV2, preInboundLine, CommonUtils.getNullPropertyNames(asnLineV2));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCode);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(asnHeaderV2.getAsnNumber());
            preInboundLine.setInboundOrderTypeId(asnHeaderV2.getInboundOrderTypeId());
//            preInboundLine.setParentProductionOrderNo(asnHeaderV2.getParentProductionOrderNo());

            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(Long.valueOf(asnLineV2.getLineReference()));
            preInboundLine.setItemCode(asnLineV2.getSku());
            preInboundLine.setItemDescription(asnLineV2.getSkuText());
            preInboundLine.setManufacturerPartNo(asnLineV2.getManufacturerName());
            preInboundLine.setBusinessPartnerCode(asnLineV2.getSupplierCode());
            preInboundLine.setOrderQty(asnLineV2.getExpectedQty());

            //Net Weight--->PriceSegment
            if (referenceField1 != null && preInboundLine.getOrderQty() != null) {
                Double netWeight = Double.valueOf((referenceField1));
                Double priceSegment = netWeight * preInboundLine.getOrderQty();
                preInboundLine.setPriceSegment(String.valueOf((priceSegment)));
            }
            preInboundLine.setOrderUom(asnLineV2.getUom());
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setSpecialStockIndicatorId(1L);
            preInboundLine.setExpectedArrivalDate(asnLineV2.getReceivedDate());
//            preInboundLine.setItemCaseQty(asnLineV2.getExpectedQtyInCases());
            preInboundLine.setCompanyDescription(companyText);
            preInboundLine.setPlantDescription(plantText);
            preInboundLine.setWarehouseDescription(warehouseText);

//            preInboundLine.setBrandName(asnLineV2.getBrand());
            preInboundLine.setManufacturerCode(asnLineV2.getManufacturerName());
            preInboundLine.setManufacturerName(asnLineV2.getManufacturerName());
            preInboundLine.setPartnerItemNo(asnLineV2.getSupplierCode());
            preInboundLine.setContainerNo(asnLineV2.getContainerNumber());
//            preInboundLine.setSupplierName(asnLineV2.getSupplierName());

            preInboundLine.setMiddlewareId(String.valueOf(asnLineV2.getMiddlewareId()));
            preInboundLine.setMiddlewareHeaderId(String.valueOf(asnLineV2.getMiddlewareHeaderId()));
            preInboundLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());
//            preInboundLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
            if (preInboundLine.getInboundOrderTypeId().equals(1L)) {
                preInboundLine.setReferenceDocumentType("SUPPLIER INVOICE");
            }
            if (preInboundLine.getInboundOrderTypeId().equals(2L)) {
                preInboundLine.setReferenceDocumentType("Customer Returns");
            }
            if (preInboundLine.getInboundOrderTypeId().equals(4L)) {
                preInboundLine.setReferenceDocumentType("Transfer In");
            }
            preInboundLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());

            preInboundLine.setBranchCode(asnLineV2.getBranchCode());
            preInboundLine.setIsCompleted(asnLineV2.getIsCompleted());
            preInboundLine.setManufacturerDate(asnLineV2.getReceivedDate());
            preInboundLine.setBarcodeId(asnLineV2.getBarcodeId());

            preInboundLine.setReferenceField11(asnLineV2.getNetWeight());                  //NetWeight
            preInboundLine.setReferenceField12(asnLineV2.getGrossWeight());                //GrossWeight
            preInboundLine.setReferenceField13(asnLineV2.getTotalWeight());                //TotalWeight
            preInboundLine.setMrp(asnLineV2.getMrp());                        //MRP
//            preInboundLine.setSize(asnLineV2.getTotalNetWeight());                  //TotalNetWeight

            //InventoryOwner--->MaterialNo
            if (inventoryOwner != null) {
                preInboundLine.setMaterialNo(inventoryOwner);
            }

            preInboundLine.setStatusId(13L);
            statusDescription = stagingLineV2Repository.getStatusDescription(preInboundLine.getStatusId(), preInboundLine.getLanguageId());
            preInboundLine.setStatusDescription(statusDescription);

            if (asnHeaderV2.getCustomerId() != null) {
                preInboundLine.setReferenceField6(asnHeaderV2.getCustomerId());
            }
            if (asnHeaderV2.getCustomerName() != null) {
                preInboundLine.setReferenceField7(asnHeaderV2.getCustomerName());
            }

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy("MW_AMS");
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e);
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param preInboundNo
     * @param asnHeaderV2
     * @param asnLineV2
     * @param warehouseId
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @return
     */
    private PreInboundLineEntityV2 createPreInboundLineV9(String companyCode, String plantId, String languageId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                          ASNLineV2 asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText) {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(asnLineV2, preInboundLine, CommonUtils.getNullPropertyNames(asnLineV2));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCode);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(asnHeaderV2.getAsnNumber());
            preInboundLine.setInboundOrderTypeId(asnHeaderV2.getInboundOrderTypeId());
//            preInboundLine.setParentProductionOrderNo(asnHeaderV2.getParentProductionOrderNo());

            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(Long.valueOf(asnLineV2.getLineReference()));
            preInboundLine.setItemCode(asnLineV2.getSku());
            preInboundLine.setItemDescription(asnLineV2.getSkuDescription());
            preInboundLine.setManufacturerPartNo(asnLineV2.getManufacturerName());
            preInboundLine.setBusinessPartnerCode(asnLineV2.getSupplierCode());
            preInboundLine.setOrderQty(asnLineV2.getExpectedQty());
            preInboundLine.setOrderUom(asnLineV2.getUom());
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setSpecialStockIndicatorId(1L);
            preInboundLine.setExpectedArrivalDate(asnLineV2.getReceivedDate());
//            preInboundLine.setItemCaseQty(asnLineV2.getExpectedQtyInCases());
            preInboundLine.setCompanyDescription(companyText);
            preInboundLine.setPlantDescription(plantText);
            preInboundLine.setWarehouseDescription(warehouseText);

//            preInboundLine.setOrigin(asnLineV2.getOrigin());
//            preInboundLine.setBrandName(asnLineV2.getBrand());
            preInboundLine.setManufacturerCode(asnLineV2.getManufacturerName());
            preInboundLine.setManufacturerName(asnLineV2.getManufacturerName());
            preInboundLine.setPartnerItemNo(asnLineV2.getSupplierCode());
            preInboundLine.setContainerNo(asnLineV2.getContainerNumber());
//            preInboundLine.setSupplierName(asnLineV2.getSupplierName());

            preInboundLine.setMiddlewareId(String.valueOf(asnLineV2.getMiddlewareId()));
            preInboundLine.setMiddlewareHeaderId(String.valueOf(asnLineV2.getMiddlewareHeaderId()));
            preInboundLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());
//            preInboundLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
            if (preInboundLine.getInboundOrderTypeId().equals(1L)) {
                preInboundLine.setReferenceDocumentType("SUPPLIER INVOICE");
            }
            if (preInboundLine.getInboundOrderTypeId().equals(4L)) {
                preInboundLine.setReferenceDocumentType("Transfer In");
            }
            preInboundLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());

            preInboundLine.setBranchCode(asnLineV2.getBranchCode());
            preInboundLine.setIsCompleted(asnLineV2.getIsCompleted());
            preInboundLine.setManufacturerDate(asnLineV2.getReceivedDate());
            preInboundLine.setBarcodeId(asnLineV2.getBarcodeId());
            preInboundLine.setStatusId(13L);
            statusDescription = stagingLineV2Repository.getStatusDescription(preInboundLine.getStatusId(), preInboundLine.getLanguageId());
            preInboundLine.setStatusDescription(statusDescription);

            if (asnHeaderV2.getCustomerId() != null) {
                preInboundLine.setReferenceField6(asnHeaderV2.getCustomerId());
            }
            if (asnHeaderV2.getCustomerName() != null) {
                preInboundLine.setReferenceField7(asnHeaderV2.getCustomerName());
            }

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy("MW_AMS");
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e);
            throw e;
        }
    }

    private PreInboundLineEntityV2 createPreInboundLineUploadV9(String companyCode, String plantId, String languageId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                                ASNLineV2 asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText) {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(asnLineV2, preInboundLine, CommonUtils.getNullPropertyNames(asnLineV2));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCode);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(asnHeaderV2.getAsnNumber());
            preInboundLine.setInboundOrderTypeId(asnHeaderV2.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(asnHeaderV2.getParentProductionOrderNo());

            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(asnLineV2.getLineReference());
            preInboundLine.setItemCode(asnLineV2.getSku());
            preInboundLine.setItemDescription(asnLineV2.getSkuDescription());
            preInboundLine.setManufacturerPartNo(asnLineV2.getManufacturerName());
            preInboundLine.setBusinessPartnerCode(asnLineV2.getSupplierCode());
            preInboundLine.setOrderQty(asnLineV2.getExpectedQty());
            preInboundLine.setOrderUom(asnLineV2.getUom());
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setSpecialStockIndicatorId(1L);
            preInboundLine.setExpectedArrivalDate(asnLineV2.getReceivedDate());
            preInboundLine.setItemCaseQty(asnLineV2.getExpectedQtyInCases());
            preInboundLine.setCompanyDescription(companyText);
            preInboundLine.setPlantDescription(plantText);
            preInboundLine.setWarehouseDescription(warehouseText);

            preInboundLine.setOrigin(asnLineV2.getOrigin());
            preInboundLine.setBrandName(asnLineV2.getBrand());
            preInboundLine.setManufacturerCode(asnLineV2.getManufacturerName());
            preInboundLine.setManufacturerName(asnLineV2.getManufacturerName());
            preInboundLine.setPartnerItemNo(asnLineV2.getSupplierCode());
            preInboundLine.setContainerNo(asnLineV2.getContainerNumber());
            preInboundLine.setSupplierName(asnLineV2.getSupplierName());

            preInboundLine.setMiddlewareId(String.valueOf(asnLineV2.getMiddlewareId()));
            preInboundLine.setMiddlewareHeaderId(String.valueOf(asnLineV2.getMiddlewareHeaderId()));
            preInboundLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());
            preInboundLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
            preInboundLine.setReferenceDocumentType("SUPPLIER INVOICE");
            preInboundLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());

            preInboundLine.setBranchCode(asnLineV2.getBranchCode());
            preInboundLine.setIsCompleted(asnLineV2.getIsCompleted());
            preInboundLine.setManufacturerDate(asnLineV2.getReceivedDate());
            preInboundLine.setBarcodeId(asnLineV2.getBarcodeId());
            preInboundLine.setStatusId(13L);
            statusDescription = stagingLineV2Repository.getStatusDescription(preInboundLine.getStatusId(), preInboundLine.getLanguageId());
            preInboundLine.setStatusDescription(statusDescription);

            if (asnHeaderV2.getCustomerId() != null) {
                preInboundLine.setReferenceField6(asnHeaderV2.getCustomerId());
            }
            if (asnHeaderV2.getCustomerName() != null) {
                preInboundLine.setReferenceField7(asnHeaderV2.getCustomerName());
            }

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy("MW_AMS");
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e);
            throw e;
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
        inboundOrderV2Repository.updateIbOrderStatus(companyCode, branchId, warehouseId, asnNumber, statusId);
    }

    //    @Transactional

    /**
     * @param preInboundLineEntityV2s
     * @throws Exception
     */
    public void orderProcessV9(List<PreInboundLineEntityV2> preInboundLineEntityV2s) throws Exception {

        if (preInboundLineEntityV2s == null || preInboundLineEntityV2s.isEmpty()) {
            throw new BadRequestException("PreInboundLine list is empty");
        }

        PreInboundLineEntityV2 preInbounLine = preInboundLineEntityV2s.get(0);
        PreInboundHeaderEntityV2 preInboundHeader = preInboundHeaderV2Repository
                .getPreInboundHeaderV6(
                        preInbounLine.getCompanyCode(),
                        preInbounLine.getPlantId(),
                        preInbounLine.getLanguageId(),
                        preInbounLine.getWarehouseId(),
                        preInbounLine.getPreInboundNo(),
                        preInbounLine.getRefDocNumber()
                );
        try {

            String token = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();

            // getNumberRange
            String stagingNo = getNextRangeNumber(3L, preInboundHeader.getCompanyCode(),
                    preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(),
                    preInboundHeader.getWarehouseId(), token);

            String caseCode = getNextRangeNumber(4L, preInboundHeader.getCompanyCode(),
                    preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(),
                    preInboundHeader.getWarehouseId(), token);

            String grNumber = getNextRangeNumber(5L, preInboundHeader.getCompanyCode(),
                    preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(),
                    preInboundHeader.getWarehouseId(), token);

            //Create InboundHeader and GrHeader and StagingHeader
            InboundHeaderV2 inboundHeader = createInboundHeaderV9(preInboundHeader, (long) preInboundLineEntityV2s.size());
            StagingHeaderV2 stagingHeader = createStagingHeader(preInboundHeader, stagingNo);
            GrHeaderV2 grHeader = createGrHeader(stagingHeader, caseCode, grNumber);

            repo.inboundHeaderV2Repository.save(inboundHeader);
            repo.stagingHeaderV2Repository.save(stagingHeader);
            repo.grHeaderV2Repository.save(grHeader);

            List<InboundLineV2> inboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<StagingLineEntityV2> stagingLineList = Collections.synchronizedList(new ArrayList<>());

            // Staging Line Creation
            processInboundLinesV9(preInboundLineEntityV2s, stagingHeader, grHeader, inboundLineList, stagingLineList);

            //create staging lines
//            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
//                    preInboundLineEntityV2s.stream()
//                            .map(preInboundLine -> CompletableFuture.runAsync(() -> {
//                                try {
//            processInboundLinesV9(preInboundLineEntityV2s, stagingHeader, grHeader, inboundLineList, stagingLineList);
//                                } catch (Exception e) {
//                                    log.error("Error processing Line: {}", preInboundLine.getItemCode(), e);
//                                    throw new RuntimeException(e);
//                                }
//                            }))
//                            .toArray(CompletableFuture[]::new)
//            );
//            try {
//                allFutures.join();
//            } catch (CompletionException e) {
//                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
//            }

            // Batch Save All Records
            repo.inboundLineV2Repository.saveAll(inboundLineList);
            repo.stagingLineV2Repository.saveAll(stagingLineList);
        } catch (Exception e) {
            log.error("Error processing inbound Lines", e);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        }
    }

    /**
     * @param preInboundHeader
     * @param stagingNo
     * @return
     */
    public StagingHeaderV2 createStagingHeader(PreInboundHeaderEntityV2 preInboundHeader, String stagingNo) throws Exception {
        try {
            StagingHeaderV2 stagingHeader = new StagingHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            stagingHeader.setStagingNo(stagingNo);
            stagingHeader.setGrMtd("INTEGRATION");
            // Staging_Header
            String orderText = "StagingHeader Created";
            inboundOrderV2Repository.updateStagingHeader(stagingHeader.getInboundOrderTypeId(), stagingHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");
            return stagingHeader;
        } catch (Exception e) {
            log.error("Exception while StagingHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param stagingHeader
     * @param caseCode
     * @param grNumber
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeader(StagingHeaderV2 stagingHeader, String caseCode, String grNumber) throws Exception {
        try {
            GrHeaderV2 grHeader = new GrHeaderV2();
            BeanUtils.copyProperties(stagingHeader, grHeader, CommonUtils.getNullPropertyNames(stagingHeader));
            grHeader.setCaseCode(caseCode);
            grHeader.setPalletCode(caseCode);
            grHeader.setGoodsReceiptNo(grNumber);
            grHeader.setStatusId(16L);
            grHeader.setStatusDescription(getStatusDescription(16L, grHeader.getLanguageId()));

            // Staging_Header
            String orderText = "GrHeader Created";
            inboundOrderV2Repository.updateGrHeader(grHeader.getInboundOrderTypeId(), grHeader.getRefDocNumber(), orderText);
            log.info("Update GR Header Update Successfully");

            return grHeader;
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param preInboundLine
     * @param stagingHeader
     * @param grHeaderV2
     * @param inboundLineList
     * @param stagingLineList
     * @throws Exception
     */
    private void processInboundLinesV9(List<PreInboundLineEntityV2> preInboundLine, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
                                       List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList) throws Exception {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(stagingHeader.getCompanyCode(), stagingHeader.getPlantId(), stagingHeader.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);
            statusDescription = getStatusDescription(14L, stagingHeader.getLanguageId());
            List<StagingLineEntityV2> stagingLines = createStagingLineV9(preInboundLine, grHeaderV2, stagingHeader, statusDescription);
            stagingLineList.addAll(stagingLines);
            List<InboundLineV2> inboundLines = createInboundLines(16L, statusDescription, stagingLines);
            inboundLineList.addAll(inboundLines);
            log.info("Create InboundLines ----->" + inboundLineList);

            log.info("Create StagingLine ----->" + stagingLineList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param preInboundLineEntityV2s
     * @param grHeaderV2
     * @param stagingHeader
     * @return
     */
    public List<StagingLineEntityV2> createStagingLineV9(List<PreInboundLineEntityV2> preInboundLineEntityV2s, GrHeaderV2 grHeaderV2, StagingHeaderV2 stagingHeader, String statusDesc) {

        List<StagingLineEntityV2> stagingLineEntityV2s = new ArrayList<>();

        Double uomQty = null;

        long lineNo = 1L;
        for (PreInboundLineEntityV2 preInboundLine : preInboundLineEntityV2s) {
            if (preInboundLine.getInboundOrderTypeId().equals(1L) || preInboundLine.getInboundOrderTypeId().equals(4L)) {
                uomQty = stagingLineV2Repository.getQty(preInboundLine.getCompanyCode(), preInboundLine.getPlantId(),
                        preInboundLine.getWarehouseId(), preInboundLine.getLanguageId(), preInboundLine.getItemCode(), "2");
                log.info("Uom Qty {} Based On That ItemCode --{} --> ", uomQty, preInboundLine.getItemCode());
            }
            if (preInboundLine.getInboundOrderTypeId().equals(2L)) {
                uomQty = 60.0;
            }
            if (uomQty == null || uomQty == 0.0) {
                throw new BadRequestException("Uom Qty Not Found in ImAlternateUom Table ------> ItemCode " + preInboundLine.getItemCode());
            }
            String token = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            if (preInboundLine.getOrderUom().equalsIgnoreCase("CASE")) {
                Double orderQty = preInboundLine.getOrderQty();
                double qty = orderQty / uomQty;
                int rounded = (int) qty;
                double calQty = uomQty * rounded;
                double balQty = orderQty - calQty;
                log.info("BalanceQty ---->" + balQty);
                int roundedQty = (int) Math.ceil(qty);
                log.info("RoundedQty ---------------------> {} ", roundedQty);

                for (int i = 1; i <= roundedQty; i++) {
                    StagingLineEntityV2 stagingLineEntityV2 = new StagingLineEntityV2();
                    BeanUtils.copyProperties(preInboundLine, stagingLineEntityV2, CommonUtils.getNullPropertyNames(preInboundLine));
                    stagingLineEntityV2.setStagingNo(stagingHeader.getStagingNo());
                    stagingLineEntityV2.setCaseCode(grHeaderV2.getCaseCode());
                    stagingLineEntityV2.setGoodsReceiptNo(grHeaderV2.getGoodsReceiptNo());
                    stagingLineEntityV2.setStatusId(14L);
                    if (i == roundedQty && balQty != 0) {
                        stagingLineEntityV2.setOrderQty(balQty);
                    } else {
                        stagingLineEntityV2.setOrderQty(uomQty);
                    }
                    // getNumberRange
                    String palletCode = getNextRangeNumber(30L, preInboundLine.getCompanyCode(),
                            preInboundLine.getPlantId(), preInboundLine.getLanguageId(),
                            preInboundLine.getWarehouseId(), token);

                    stagingLineEntityV2.setPalletCode("M" + palletCode);
                    log.info("PalletCode in Staging Line Creation {} ", stagingLineEntityV2.getPalletCode());
                    stagingLineEntityV2.setParentProductionOrderNo(stagingLineEntityV2.getPalletCode());
                    stagingLineEntityV2.setLineNo(lineNo);
                    stagingLineEntityV2.setBarcodeId(preInboundLine.getBarcodeId());
                    stagingLineEntityV2.setRec_accept_qty(preInboundLine.getOrderQty());
                    stagingLineEntityV2.setRec_damage_qty(0D);
                    stagingLineEntityV2.setVehicleNo(preInboundLine.getVehicleNo());
                    stagingLineEntityV2.setVehicleUnloadingDate(preInboundLine.getVehicleUnloadingDate());
                    stagingLineEntityV2.setVehicleReportingDate(preInboundLine.getVehicleReportingDate());

                    stagingLineEntityV2.setHsnCode(preInboundLine.getReferenceField11());                   //NetWeight
                    stagingLineEntityV2.setVariantType(preInboundLine.getReferenceField12());                //GrossWeight
                    stagingLineEntityV2.setSpecificationActual(preInboundLine.getReferenceField13());         //TotalWeight
                    stagingLineEntityV2.setMrp(preInboundLine.getMrp());                                      //MRP
                    //MFR_NAME
                    stagingLineEntityV2.setManufacturerCode(preInboundLine.getManufacturerCode());
                    stagingLineEntityV2.setManufacturerName(preInboundLine.getManufacturerName());
                    stagingLineEntityV2.setManufacturerFullName(preInboundLine.getManufacturerName());
                    //InventoryOwner
                    if (preInboundLine.getMaterialNo() != null) {
                        stagingLineEntityV2.setMaterialNo(preInboundLine.getMaterialNo());
                    }
                    //PriceSegment
                    if(preInboundLine.getPriceSegment() != null){
                        stagingLineEntityV2.setPriceSegment(preInboundLine.getPriceSegment());
                    }


                    if (preInboundLine.getReferenceField6() != null) {
                        stagingLineEntityV2.setReferenceField6(preInboundLine.getReferenceField6());
                    }
                    if (preInboundLine.getReferenceField7() != null) {
                        stagingLineEntityV2.setReferenceField7(preInboundLine.getReferenceField7());
                    }
                    if (preInboundLine.getOrderUom().equalsIgnoreCase("Pallet") && preInboundLine.getInboundOrderTypeId() == 11L) {
                        stagingLineEntityV2.setQtyInCreate(preInboundLine.getOrderQty());
                    } else {
                        log.info("Quantity Logic started-------------->");
                        setAlternateUomQuantities(stagingLineEntityV2);
                        log.info("Quantity Logic Completed-------------->");
                    }
                    stagingLineEntityV2.setStatusDescription(statusDesc);
                    stagingLineEntityV2s.add(stagingLineEntityV2);
                    lineNo++;
                }
            } else {
                int roundedQty = (int) Math.ceil(preInboundLine.getOrderQty());
                for (int i = 1; i <= roundedQty; i++) {
                    StagingLineEntityV2 stagingLineEntityV2 = new StagingLineEntityV2();
                    BeanUtils.copyProperties(preInboundLine, stagingLineEntityV2, CommonUtils.getNullPropertyNames(preInboundLine));
                    stagingLineEntityV2.setStagingNo(stagingHeader.getStagingNo());
                    stagingLineEntityV2.setCaseCode(grHeaderV2.getCaseCode());
                    stagingLineEntityV2.setGoodsReceiptNo(grHeaderV2.getGoodsReceiptNo());
                    stagingLineEntityV2.setStatusId(14L);
                    stagingLineEntityV2.setLineNo(lineNo);
                    stagingLineEntityV2.setOrderQty(uomQty);
                    // getNumberRange
                    String palletCode = getNextRangeNumber(30L, preInboundLine.getCompanyCode(),
                            preInboundLine.getPlantId(), preInboundLine.getLanguageId(),
                            preInboundLine.getWarehouseId(), token);

                    stagingLineEntityV2.setPalletCode("M" + (palletCode));
                    stagingLineEntityV2.setParentProductionOrderNo(stagingLineEntityV2.getPalletCode());
                    stagingLineEntityV2.setBarcodeId(preInboundLine.getBarcodeId());
                    stagingLineEntityV2.setRec_accept_qty(preInboundLine.getOrderQty());
                    stagingLineEntityV2.setRec_damage_qty(0D);
                    stagingLineEntityV2.setVehicleNo(preInboundLine.getVehicleNo());
                    stagingLineEntityV2.setVehicleUnloadingDate(preInboundLine.getVehicleUnloadingDate());
                    stagingLineEntityV2.setVehicleReportingDate(preInboundLine.getVehicleReportingDate());

                    stagingLineEntityV2.setHsnCode(preInboundLine.getReferenceField11());                  //NetWeight
                    stagingLineEntityV2.setVariantType(preInboundLine.getReferenceField12());                //GrossWeight
                    stagingLineEntityV2.setSpecificationActual(preInboundLine.getReferenceField13());          //TotalWeight
                    stagingLineEntityV2.setMrp(preInboundLine.getMrp());                                       //MRP
                    //MFR_NAME
                    stagingLineEntityV2.setManufacturerCode(preInboundLine.getManufacturerCode());
                    stagingLineEntityV2.setManufacturerName(preInboundLine.getManufacturerName());
                    stagingLineEntityV2.setManufacturerFullName(preInboundLine.getManufacturerName());

                    if (preInboundLine.getPriceSegment() != null) {
                        stagingLineEntityV2.setPriceSegment(preInboundLine.getPriceSegment());               //InventoryOwner
                    }


                    if (preInboundLine.getReferenceField6() != null) {
                        stagingLineEntityV2.setReferenceField6(preInboundLine.getReferenceField6());
                    }
                    if (preInboundLine.getReferenceField7() != null) {
                        stagingLineEntityV2.setReferenceField7(preInboundLine.getReferenceField7());
                    }
                    if (preInboundLine.getOrderUom().equalsIgnoreCase("PALLET") && preInboundLine.getInboundOrderTypeId() == 11L) {
                        stagingLineEntityV2.setQtyInCreate(preInboundLine.getOrderQty());
                    } else {
                        log.info("Quantity Logic started-------------->");
                        setAlternateUomQuantities(stagingLineEntityV2);
                        log.info("Quantity Logic Completed-------------->");
                    }
                    stagingLineEntityV2.setStatusDescription(statusDesc);
                    stagingLineEntityV2s.add(stagingLineEntityV2);
                    lineNo++;
                }
            }
        }

        //update PreInboundLine
        String text = "GR Release";
        inboundOrderV2Repository.updatePreInboundLineV9(stagingLineEntityV2s.get(0).getRefDocNumber(), text);
        log.info("Update PreInboundLine Update Successfully");
        // Update GrHeader
        String orderText = "GrHeader Created";
        inboundOrderV2Repository.updateGrHeaderV9(stagingLineEntityV2s.get(0).getInboundOrderTypeId(), stagingLineEntityV2s.get(0).getRefDocNumber(), orderText);
        log.info("Update Staging Header Update Successfully");
        return stagingLineEntityV2s;
    }

    /**
     * @param statusId
     * @param statusDesc
     * @param stagingLineEntityList
     * @return
     * @throws Exception
     */
    public List<InboundLineV2> createInboundLines(Long statusId, String statusDesc, List<StagingLineEntityV2> stagingLineEntityList) throws Exception {
        try {

            List<InboundLineV2> inboundLineV2List = new ArrayList<>();
            stagingLineEntityList.stream().forEach(staging -> {

                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(staging, inboundLine, CommonUtils.getNullPropertyNames(staging));
                inboundLine.setStatusId(statusId);
                inboundLine.setStatusDescription(statusDesc);
                inboundLine.setQtyInPiece(staging.getQtyInPiece());
                inboundLine.setQtyInCreate(staging.getQtyInCreate());
                inboundLine.setQtyInCase(staging.getQtyInCase());
                inboundLine.setManufacturerDate(staging.getManufacturerDate());
                inboundLine.setBarcodeId(staging.getBarcodeId());
                inboundLine.setDescription(staging.getItemDescription());
                inboundLine.setParentProductionOrderNo(staging.getPalletCode());

                inboundLine.setHsnCode(staging.getHsnCode());                                    //NetWeight
                inboundLine.setReferenceField1(staging.getVariantType());                        //GrossWeight
                inboundLine.setReferenceField8(staging.getSpecificationActual());                //TotalWeight
                inboundLine.setMrp(staging.getMrp());                                            //MRP
                //MFR_NAME
                inboundLine.setManufacturerCode(staging.getManufacturerCode());
                inboundLine.setManufacturerName(staging.getManufacturerName());
                inboundLine.setManufacturerFullName(staging.getManufacturerName());
//                inboundLine.setSize(staging.getSize());                                //TotalNetWeight

                //InventoryOwner
                if (staging.getMaterialNo() != null) {
                    inboundLine.setMaterialNo(staging.getMaterialNo());
                }
                //PriceSegment
                if(staging.getPriceSegment() != null){
                    inboundLine.setPriceSegment(staging.getPriceSegment());
                }

                if (staging.getReferenceField6() != null) {
                    inboundLine.setReferenceField6(staging.getReferenceField6());
                }
                if (staging.getReferenceField7() != null) {
                    inboundLine.setReferenceField7(staging.getReferenceField7());
                }
                log.info("InboundLines ----->" + inboundLine);
                inboundLineV2List.add(inboundLine);
            });

            return inboundLineV2List;
        } catch (Exception e) {
            log.error("Exception while InboundLines Create : " + e);
            throw e;
        }
    }

    //Generate Barcode
    public String getNextBarcode() {
        String barcode = String.format("M%05d", counter);
        counter++;
        return barcode;
    }


    /**
     * @param stagingLineEntityV2
     */
    private void setAlternateUomQuantities(StagingLineEntityV2 stagingLineEntityV2) {
        try {
            Double qtyInCase = null;
            Double qtyInCreate = null;

            String orderUom = stagingLineEntityV2.getOrderUom();
            String companyCodeId = stagingLineEntityV2.getCompanyCode();
            String plantId = stagingLineEntityV2.getPlantId();
            String warehouseId = stagingLineEntityV2.getWarehouseId();
            String itemCode = stagingLineEntityV2.getItemCode();

            if ("case".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CASE");

                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "2", "3");

                qtyInCase = stagingLineEntityV2.getOrderQty();

                log.info("Case Qty --- {}", stagingLineEntityV2.getOrderQty());
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (stagingLineEntityV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = qtyInCase / createQty.getUomQty();
                }
            } else if ("crate".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CRATE");
//                qtyInCreate = stagingLineEntityV2.getOrderQty();
                qtyInCreate = 1.0;
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "2", "3");

                log.info("CRATE Qty --- {}", stagingLineEntityV2.getOrderQty());
                log.info("CASE Qty ALT_UOM: {}", caseQty);

                if (stagingLineEntityV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
                    qtyInCase = qtyInCreate * caseQty.getUomQty();
                    log.info("Case Qty ----->" + qtyInCase);
                }
            }

            stagingLineEntityV2.setQtyInCase(qtyInCase);
            stagingLineEntityV2.setQtyInCreate(qtyInCreate);
        } catch (Exception e) {
            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
        }
    }

    /**
     * @param preInboundHeader
     * @param orderLinesCount
     * @return
     * @throws Exception
     */
    private InboundHeaderV2 createInboundHeaderV9(PreInboundHeaderEntityV2 preInboundHeader, Long orderLinesCount) throws Exception {
        try {
            InboundHeaderV2 inboundHeader = new InboundHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            inboundHeader.setCountOfOrderLines(orderLinesCount);       //count of lines
            if (preInboundHeader.getCustomerId() != null) {
                inboundHeader.setCustomerId(preInboundHeader.getCustomerId());
            }
            if (preInboundHeader.getCustomerName() != null) {
                inboundHeader.setCustomerName(preInboundHeader.getCustomerName());
            }
            // Inbound_Header
            String orderText = "Inbound Header Created";
            inboundOrderV2Repository.updateIbHeader(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), orderText);
            log.info("Update Inbound Header Update Successfully");
            return inboundHeader;
        } catch (Exception e) {
            log.error("Exception while InboundHeader Create : " + e);
            throw e;
        }
    }

    private PreInboundHeaderEntityV2 createPreInboundHeaderUploadV9(String companyId, String languageId, String plantId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                                    String warehouseId, String companyText, String plantText, String warehouseText, String mfrName) {
        try {
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(asnHeaderV2, preInboundHeader, CommonUtils.getNullPropertyNames(asnHeaderV2));
            preInboundHeader.setLanguageId(languageId);
            preInboundHeader.setWarehouseId(warehouseId);
            preInboundHeader.setCompanyCode(companyId);
            preInboundHeader.setPlantId(plantId);
            preInboundHeader.setRefDocNumber(asnHeaderV2.getAsnNumber());
            preInboundHeader.setPreInboundNo(preInboundNo);                  // PRE_IB_NO

            preInboundHeader.setInboundOrderTypeId(asnHeaderV2.getInboundOrderTypeId());                      // IB_ORD_TYP_ID
            if (preInboundHeader.getInboundOrderTypeId().equals(1L)) {
                preInboundHeader.setReferenceDocumentType("Supplier Invoice");
                preInboundHeader.setTransferRequestType("Supplier Invoice");// REF_DOC_TYP - Hard Coded Value "ASN"
            }
            if (preInboundHeader.getInboundOrderTypeId().equals(4L)) {
                preInboundHeader.setReferenceDocumentType("Transfer In");
                preInboundHeader.setTransferRequestType("Transfer In");// REF_DOC_TYP - Hard Coded Value "ASN"
            }

            preInboundHeader.setRefDocDate(new Date());                      // REF_DOC_DATE
            preInboundHeader.setStatusId(5L);
            statusDescription = repo.stagingLineV2Repository.getStatusDescription(5L, languageId);
            preInboundHeader.setStatusDescription(statusDescription);
            preInboundHeader.setCompanyDescription(companyText);
            preInboundHeader.setPlantDescription(plantText);
            preInboundHeader.setWarehouseDescription(warehouseText);
            preInboundHeader.setMiddlewareId(String.valueOf(asnHeaderV2.getMiddlewareId()));
            preInboundHeader.setMiddlewareTable(asnHeaderV2.getMiddlewareTable());
            preInboundHeader.setManufacturerFullName(mfrName);
            if (asnHeaderV2.getCustomerId() != null) {
                preInboundHeader.setCustomerId(asnHeaderV2.getCustomerId());
            }
            if (asnHeaderV2.getCustomerName() != null) {
                preInboundHeader.setCustomerName(asnHeaderV2.getCustomerName());
            }

            preInboundHeader.setTransferOrderDate(new Date());
            preInboundHeader.setSourceBranchCode(asnHeaderV2.getBranchCode());
            preInboundHeader.setSourceCompanyCode(asnHeaderV2.getCompanyCode());
            preInboundHeader.setMUpdatedOn(asnHeaderV2.getUpdatedOn());

            preInboundHeader.setDeletionIndicator(0L);
            preInboundHeader.setCreatedBy("MW_AMS");
            preInboundHeader.setCreatedOn(new Date());


            // IB_Order
            String preInbound = "PreInbound Created";
            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
            log.info("Update Inbound Order Update Successfully");
            return preInboundHeader;
        } catch (Exception e) {
            log.info("PreInboundHeader Creation Failed -----------> " + e.getMessage());
            throw new BadRequestException("PreInboundHeader Failed -----------------> " + e);
        }
    }

    private PreInboundLineEntityV2 createPreInboundLineUpload(String companyCode, String plantId, String languageId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                              ASNLineV2 asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText) {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(asnLineV2, preInboundLine, CommonUtils.getNullPropertyNames(asnLineV2));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCode);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(asnHeaderV2.getAsnNumber());
            preInboundLine.setInboundOrderTypeId(asnHeaderV2.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(asnHeaderV2.getParentProductionOrderNo());

            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(asnLineV2.getLineReference());
            preInboundLine.setItemCode(asnLineV2.getSku());
            preInboundLine.setItemDescription(asnLineV2.getSkuDescription());
            preInboundLine.setManufacturerPartNo(asnLineV2.getManufacturerName());
            preInboundLine.setBusinessPartnerCode(asnLineV2.getSupplierCode());
            preInboundLine.setOrderQty(asnLineV2.getExpectedQty());
            preInboundLine.setOrderUom(asnLineV2.getUom());
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setSpecialStockIndicatorId(1L);
            preInboundLine.setExpectedArrivalDate(asnLineV2.getReceivedDate());
            preInboundLine.setItemCaseQty(asnLineV2.getExpectedQtyInCases());
            preInboundLine.setCompanyDescription(companyText);
            preInboundLine.setPlantDescription(plantText);
            preInboundLine.setWarehouseDescription(warehouseText);

            preInboundLine.setOrigin(asnLineV2.getOrigin());
            preInboundLine.setBrandName(asnLineV2.getBrand());
            preInboundLine.setManufacturerCode(asnLineV2.getManufacturerName());
            preInboundLine.setManufacturerName(asnLineV2.getManufacturerName());
            preInboundLine.setPartnerItemNo(asnLineV2.getSupplierCode());
            preInboundLine.setContainerNo(asnLineV2.getContainerNumber());
            preInboundLine.setSupplierName(asnLineV2.getSupplierName());

            preInboundLine.setMiddlewareId(String.valueOf(asnLineV2.getMiddlewareId()));
            preInboundLine.setMiddlewareHeaderId(String.valueOf(asnLineV2.getMiddlewareHeaderId()));
            preInboundLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());
            preInboundLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
            preInboundLine.setReferenceDocumentType("SUPPLIER INVOICE");
            preInboundLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());

            preInboundLine.setBranchCode(asnLineV2.getBranchCode());
            preInboundLine.setIsCompleted(asnLineV2.getIsCompleted());
            preInboundLine.setManufacturerDate(asnLineV2.getReceivedDate());
            preInboundLine.setBarcodeId(asnLineV2.getBarcodeId());
            preInboundLine.setStatusId(13L);
            statusDescription = stagingLineV2Repository.getStatusDescription(preInboundLine.getStatusId(), preInboundLine.getLanguageId());
            preInboundLine.setStatusDescription(statusDescription);

            if (asnHeaderV2.getCustomerId() != null) {
                preInboundLine.setReferenceField6(asnHeaderV2.getCustomerId());
            }
            if (asnHeaderV2.getCustomerName() != null) {
                preInboundLine.setReferenceField7(asnHeaderV2.getCustomerName());
            }

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy("MW_AMS");
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e);
            throw e;
        }
    }
}
