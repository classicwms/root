package com.tekclover.wms.api.inbound.orders.service;


import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.controller.InboundOrderRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class WarehouseService extends BaseService {


    protected static final String LANG_ID = "EN";

    @Autowired
    OrderService orderService;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    InboundOrderLinesV2Repository inboundOrderLinesV2Repository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;


    /**
     * @param asnv2
     * @return
     */
    public InboundOrderV2 postWarehouseASNV2(ASNV2 asnv2) {
        log.info("ASNV2Header received from External: " + asnv2);
        InboundOrderV2 savedAsnV2Header = saveASNV2(asnv2);
        log.info("savedAsnV2Header: " + savedAsnV2Header);
        return savedAsnV2Header;
    }

    /**
     * @param asnv2
     * @return
     */
    public InboundOrderV2 postWarehouseEmptyCrateV5(ASNV2 asnv2) {
        log.info("ASNV2Header received from External: " + asnv2);
        InboundOrderV2 savedAsnV2Header = saveEmptyCrate5(asnv2);
        log.info("savedAsnV2Header: " + savedAsnV2Header);
        return savedAsnV2Header;
    }

    // POST ASNV2Header
    private InboundOrderV2 saveEmptyCrate5(ASNV2 asnv2) {
        try {
            ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
            List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();

            //validateBarcodeIds
            huSerialValidation(asnLineV2s, asnV2Header.getAsnNumber());

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
                apiHeader.setInboundOrderTypeId(11L);                                            //Default
            }
            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getInboundOrderTypeId()));
            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNLineV2 asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));
                apiLine.setLineReference(asnLineV2.getLineReference());            // IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku().trim());                            // ITM_CODE
                if (asnLineV2.getBarcodeId() != null) {
                    apiLine.setBarcodeId(asnLineV2.getBarcodeId().trim());
                }
                apiLine.setItemText(asnLineV2.getSkuDescription());                // ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());            // CONT_NO
                apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
                apiLine.setManufacturerName(MFR_NAME_V5);        // BRAND_NM
                apiLine.setManufacturerCode(MFR_NAME_V5);
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());

                Date parsedDate = DateUtils.parseCustomDate(asnLineV2.getBarcodeId());
                if (parsedDate != null) {
                    apiLine.setReceivedDate(parsedDate);
                } else {
                    System.out.println("Failed to parse date from barcode: " + asnLineV2.getBarcodeId());
                }
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(11L);                                            //Default
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
                apiLine.setUom("Crate");                                // ORD_UOM
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
                InboundOrderV2 createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param asnv2
     * @return
     */
    public InboundOrderV2 postWarehouseASNV5(ASNV2 asnv2) {
        log.info("ASNV2Header received from External: " + asnv2);
        InboundOrderV2 savedAsnV2Header = saveASNV5(asnv2);
        log.info("savedAsnV2Header: " + savedAsnV2Header);
        return savedAsnV2Header;
    }


    // POST ASNV2Header
    private InboundOrderV2 saveASNV5(ASNV2 asnv2) {
        try {
            ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
            List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();

            //validateBarcodeIds
//            huSerialValidation(asnLineV2s, asnV2Header.getAsnNumber());

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
            apiHeader.setRefDocumentType("Supplier Invoice");

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
                apiLine.setManufacturerName(MFR_NAME_V5);        // BRAND_NM
                apiLine.setManufacturerCode(MFR_NAME_V5);
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());


                Date parsedDate = DateUtils.parseCustomDate(asnLineV2.getBarcodeId());
                if (parsedDate != null) {
                    apiLine.setReceivedDate(parsedDate);
                } else {
                    System.out.println("Failed to parse date from barcode: " + asnLineV2.getBarcodeId());
                }
//				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
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
                apiLine.setUom(asnLineV2.getUom());                                // ORD_UOM
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
                InboundOrderV2 createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    // POST ASNV2Header
    private InboundOrderV2 saveASNV2(ASNV2 asnv2) {
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
            apiHeader.setRefDocumentType("SUPPLIER INVOICE");

            apiHeader.setLanguageId(asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID);
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
//            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
//                                                                 LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNLineV2 asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));

                //validation for bagSize performing BagUom
//                checkUom(asnLineV2.getUom(), asnLineV2.getAlternateUom(), asnLineV2.getBagSize());

                apiLine.setLineReference(asnLineV2.getLineReference());            // IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku());                            // ITM_CODE
                apiLine.setItemText(asnLineV2.getSkuDescription());                // ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());            // CONT_NO
                if (asnLineV2.getSupplierCode() != null) {
                    apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
                } else {
                    apiLine.setSupplierCode(asnV2Header.getSupplierCode());
                }
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
                apiLine.setManufacturerName("NAMRATHA");
                apiLine.setManufacturerCode("NAMRATHA");
                apiLine.setManufacturerPartNo("NAMRATHA");
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
//                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());

                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(1L);                                            //Default
                }

//                if(asnLineV2.getUom() != null) {
//                    AlternateUomImpl alternateUom = getUom(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
//                                                           apiHeader.getWarehouseID(), apiLine.getItemCode(), asnLineV2.getUom());
//                    if(alternateUom == null) {
//                        throw new BadRequestException("AlternateUom is not available for this item : " + apiLine.getItemCode());
//                    }
//                    if (alternateUom != null) {
//                        apiLine.setUom(alternateUom.getUom());
//                        apiLine.setAlternateUom(alternateUom.getAlternateUom());
//                        apiLine.setBagSize(alternateUom.getAlternateUomQty());
//                    apiLine.setNoBags(asnLineV2.getExpectedQty());
////                        double orderQty = getQuantity(asnLineV2.getExpectedQty(), alternateUom.getAlternateUomQty());
////                        apiLine.setExpectedQty(orderQty);
////                        apiLine.setOrderedQty(orderQty);
//                }
//                }

                if (asnLineV2.getExpectedQtyInCases() != null && asnLineV2.getExpectedQtyInPieces() != null) {
                    Double ordQty = asnLineV2.getExpectedQtyInPieces() / asnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
                    apiLine.setExpectedQty(ordQty);     // 25
                    apiLine.setOrderedQty(ordQty);      // 25
                    apiLine.setBagSize(ordQty);         // 25
                } else {
                    Double ordQty = asnLineV2.getExpectedQty() / asnLineV2.getNoBags();  // 50 / 2 => 25
                    apiLine.setExpectedQty(ordQty);     // 25
                    apiLine.setOrderedQty(ordQty);      // 25
                    apiLine.setBagSize(ordQty);         // 25
                }

                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setReceivedDate(asnLineV2.getReceivedDate());
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
                            throw new InboundOrderRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                }

                apiLine.setPackQty(asnLineV2.getPackQty());                    // ITM_CASE_QTY
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param asnLines
     */
    public void huSerialValidation(List<ASNLineV2> asnLines, String refDocNumber) {
        if (asnLines != null && !asnLines.isEmpty()) {
            List<String> barcodeIds = asnLines.stream().filter(n -> n.getBarcodeId() != null).map(ASNLineV2::getBarcodeId).collect(Collectors.toList());
            log.info("BarcodeId: " + barcodeIds.size());
            barcodeIdValidation(barcodeIds, refDocNumber);
        }
    }

    /**
     * @param barcodeIdList
     * @param refDocNumber
     */
    public void barcodeIdValidation(List<String> barcodeIdList, String refDocNumber) {
        List<String> result = inboundOrderLinesV2Repository.findAllByBarcodeIdIn(barcodeIdList);
        log.info("Duplicate HU Serial No: " + result);
        if (result.size() > 0) {
            List<String> duplicates = new ArrayList<>(result.size());
            for (String barcode : result) {
                boolean pass = inboundOrderLinesV2Repository.existsByBarcodeIdAndOrderId(barcode, refDocNumber);
                if (pass) {
                    duplicates.add(barcode);
                }
            }
            if (duplicates.size() > 0) {
                throw new BadRequestException("HU-SerialNo/BarcodeId already exists for this order number..! " + result + "|" + refDocNumber);
            }
        }
    }

    //============================================KnowellInboundOrder=============================================//


    /**
     * Knowell
     *
     * @param asnv2
     * @return
     */
    public InboundOrderV2 postWarehouseASNV7(ASNV2 asnv2) {
        log.info("ASNV2Header received from External: " + asnv2);
        InboundOrderV2 savedAsnV2Header = saveASNV7(asnv2);
        log.info("savedAsnV2Header: " + savedAsnV2Header);
        return savedAsnV2Header;
    }

    // POST ASNV2Header
    private InboundOrderV2 saveASNV7(ASNV2 asnv2) {
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
            apiHeader.setRefDocumentType("SUPPLIER INVOICE");

            apiHeader.setLanguageId(asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID);
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
//            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
//                                                                 LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNLineV2 asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));

                //validation for bagSize performing BagUom
//                checkUom(asnLineV2.getUom(), asnLineV2.getAlternateUom(), asnLineV2.getBagSize());

                apiLine.setLineReference(asnLineV2.getLineReference());            // IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku());                            // ITM_CODE
                apiLine.setItemText(asnLineV2.getSkuDescription());                // ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());            // CONT_NO
                if (asnLineV2.getSupplierCode() != null) {
                    apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
                } else {
                    apiLine.setSupplierCode(asnV2Header.getSupplierCode());
                }
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
                apiLine.setManufacturerCode(asnLineV2.getManufacturerName());
                apiLine.setManufacturerName(asnLineV2.getManufacturerName());
                apiLine.setManufacturerPartNo(asnLineV2.getManufacturerName());
//                apiLine.setManufacturerName(MRF_NAME_V7);
//                apiLine.setManufacturerCode(MRF_NAME_V7);
//                apiLine.setManufacturerPartNo(MRF_NAME_V7);
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
//                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());

                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(1L);                                            //Default
                }


                if (asnLineV2.getExpectedQtyInCases() > 0.0 && asnLineV2.getExpectedQtyInPieces() > 0.0) {
                    Double ordQty = asnLineV2.getExpectedQtyInPieces() / asnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
                    apiLine.setExpectedQty(ordQty);     // 25
                    apiLine.setOrderedQty(ordQty);      // 25
                    apiLine.setBagSize(ordQty);         // 25
                    apiLine.setNoBags(asnLineV2.getExpectedQtyInCases());
                } else {
                    apiLine.setExpectedQty(asnLineV2.getExpectedQtyInPieces());
                    apiLine.setOrderedQty(asnLineV2.getExpectedQtyInPieces());
                    apiLine.setBagSize(asnLineV2.getExpectedQtyInPieces());
                }

                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setReceivedDate(asnLineV2.getReceivedDate());
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
                            throw new InboundOrderRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                }

                apiLine.setPackQty(asnLineV2.getPackQty());                    // ITM_CASE_QTY
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param soReturnV2
     * @return
     */
    public InboundOrderV2 postSOReturnV5(SaleOrderReturnV2 soReturnV2) {
        log.info("StoreReturnHeader received from External: " + soReturnV2);
        InboundOrderV2 savedSOReturn = saveSOReturnV5(soReturnV2);
        log.info("soReturnHeader: " + savedSOReturn);
        return savedSOReturn;
    }


    // SOReturnV2
    private InboundOrderV2 saveSOReturnV5(SaleOrderReturnV2 soReturnV2) {
        try {
            SOReturnHeaderV2 soReturnHeaderV2 = soReturnV2.getSoReturnHeader();
            List<SOReturnLineV2> salesOrderReturnLinesV2 = soReturnV2.getSoReturnLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(soReturnHeaderV2, apiHeader, CommonUtils.getNullPropertyNames(soReturnHeaderV2));
//            apiHeader.setTransferOrderNumber(soReturnHeaderV2.getAsnNumber());
            apiHeader.setReturnOrderReference(soReturnHeaderV2.getAsnNumber());
            apiHeader.setRefDocumentNo(soReturnHeaderV2.getAsnNumber());
            apiHeader.setBranchCode(soReturnHeaderV2.getBranchCode());
            apiHeader.setOrderId(soReturnHeaderV2.getAsnNumber());
            apiHeader.setCompanyCode(soReturnHeaderV2.getCompanyCode());
            apiHeader.setRefDocumentType("SalesReturn");
            apiHeader.setCustomerId(soReturnHeaderV2.getCustomerId());
            if (soReturnHeaderV2.getInboundOrderTypeId() != null) {
                apiHeader.setInboundOrderTypeId(soReturnHeaderV2.getInboundOrderTypeId());
            } else {
                apiHeader.setInboundOrderTypeId(3L);                                        // Hardcoded Value 2
            }
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(soReturnHeaderV2.getMiddlewareId());
            apiHeader.setMiddlewareTable(soReturnHeaderV2.getMiddlewareTable());
            apiHeader.setIsCompleted(soReturnHeaderV2.getIsCompleted());
            apiHeader.setIsCancelled(soReturnHeaderV2.getIsCancelled());
            apiHeader.setUpdatedOn(soReturnHeaderV2.getUpdatedOn());
            apiHeader.setRefDocumentType("SALES RETURN");
            apiHeader.setCustomerId(soReturnHeaderV2.getCustomerId());
            apiHeader.setCustomerName(soReturnHeaderV2.getCustomerName());

            if (soReturnHeaderV2.getWarehouseId() != null && !soReturnHeaderV2.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(soReturnHeaderV2.getWarehouseId());
            } else {
                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                soReturnHeaderV2.getCompanyCode(),
                                soReturnHeaderV2.getBranchCode(),
                                LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + dbWarehouse);
                apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            }
//            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
//                    LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

            Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
            for (SOReturnLineV2 soReturnLineV2 : salesOrderReturnLinesV2) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(soReturnLineV2, apiLine, CommonUtils.getNullPropertyNames(soReturnLineV2));

                //validation for bagSize performing BagUom
//                checkUom(soReturnLineV2.getUom(), soReturnLineV2.getAlternateUom(), soReturnLineV2.getBagSize());

                Date parsedDate = DateUtils.parseCustomDate(soReturnLineV2.getBarcodeId());
                if (parsedDate != null) {
                    apiLine.setReceivedDate(parsedDate);
                } else {
                    System.out.println("Failed to parse date from barcode: " + soReturnLineV2.getBarcodeId());
                }
                apiLine.setExpectedQty(soReturnLineV2.getExpectedQty());
                apiLine.setInvoiceNumber(soReturnLineV2.getInvoiceNumber());                // INV_NO
                apiLine.setSalesOrderReference(soReturnLineV2.getSalesOrderReference());
                apiLine.setLineReference(soReturnLineV2.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(soReturnLineV2.getSku());                                // ITM_CODE
                apiLine.setItemText(soReturnLineV2.getSkuDescription());                     // ITEM_TEXT
                apiLine.setStoreID(soReturnLineV2.getStoreID());
                apiLine.setSupplierPartNumber(soReturnLineV2.getSupplierPartNumber());
                apiLine.setUom(soReturnLineV2.getUom());
                apiLine.setPackQty(soReturnLineV2.getPackQty());
                apiLine.setOrigin(soReturnLineV2.getOrigin());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setManufacturerFullName(soReturnLineV2.getManufacturerFullName());
                apiLine.setMiddlewareId(soReturnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(soReturnLineV2.getMiddlewareTable());
                apiLine.setMiddlewareHeaderId(soReturnLineV2.getMiddlewareHeaderId());
                if (soReturnHeaderV2.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(soReturnHeaderV2.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(3L);
                }
                // EA_DATE
                try {
                    Date reqDelDate = new Date();
//					if (soReturnLineV2.getExpectedDate().length() > 10) {
//						reqDelDate = DateUtils.convertStringToDateWithTime(soReturnLineV2.getExpectedDate());
//					}
                    if (soReturnLineV2.getExpectedDate() != null) {
                        reqDelDate = DateUtils.convertStringToDate2(soReturnLineV2.getExpectedDate());
                    }
                    apiLine.setExpectedDate(reqDelDate);

                } catch (Exception e) {
                    throw new BadRequestException("Date format should be yyyy-MM-dd");
                }

//                if(soReturnLineV2.getUom() != null) {
//                    AlternateUomImpl alternateUom = getUom(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
//                                                           apiHeader.getWarehouseID(), apiLine.getItemCode(), soReturnLineV2.getUom());
//                    if(alternateUom == null) {
//                        throw new BadRequestException("AlternateUom is not available for this item : " + apiLine.getItemCode());
//                    }
//                    if (alternateUom != null) {
//                        apiLine.setUom(alternateUom.getUom());
//                        apiLine.setAlternateUom(alternateUom.getAlternateUom());
//                        apiLine.setBagSize(alternateUom.getAlternateUomQty());
//                    apiLine.setNoBags(soReturnLineV2.getExpectedQty());
////                        double orderQty = getQuantity(soReturnLineV2.getExpectedQty(), alternateUom.getAlternateUomQty());
////                        apiLine.setExpectedQty(orderQty);
////                        apiLine.setOrderedQty(orderQty);
//                }
//                }
//
//                log.info("SOReturn ExpectedQtyInCases -----------> {}", soReturnLineV2.getExpectedQtyInCases());
//                log.info("SOReturn ExpectedQtyInPieces ----------> {}", soReturnLineV2.getExpectedQtyInPieces());
//                if (soReturnLineV2.getExpectedQtyInCases() != null && soReturnLineV2.getExpectedQtyInPieces() != null) {
//                    Double ordQty = soReturnLineV2.getExpectedQtyInPieces() / soReturnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
//                    apiLine.setExpectedQty(ordQty);     // 25
//                    apiLine.setOrderedQty(ordQty);      // 25
//                    apiLine.setBagSize(ordQty);         // 25
//                } else {
//                    Double ordQty = soReturnLineV2.getExpectedQty() / soReturnLineV2.getNoBags();  // 50 / 2 => 25
//                    apiLine.setExpectedQty(ordQty);     // 25
//                    apiLine.setOrderedQty(ordQty);      // 25
//                    apiLine.setBagSize(ordQty);         // 25
//                }

                apiLine.setNoBags(soReturnLineV2.getExpectedQtyInCases());
                apiLine.setManufacturerCode(MFR_NAME_V5);
                apiLine.setManufacturerName(MFR_NAME_V5);
                apiLine.setManufacturerPartNo(MFR_NAME_V5);
                apiLine.setBrand(soReturnLineV2.getBrand());
                apiLine.setVehicleNo(soReturnLineV2.getVehicleNo());
                apiLine.setVehicleUnloadingDate(soReturnLineV2.getVehicleUnloadingDate());
                apiLine.setVehicleReportingDate(soReturnLineV2.getVehicleReportingDate());
                orderLinesV2.add(apiLine);
            }
            apiHeader.setLine(orderLinesV2);
            apiHeader.setOrderProcessedOn(new Date());

            if (soReturnV2.getSoReturnLine() != null && !soReturnV2.getSoReturnLine().isEmpty()) {

                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrders(apiHeader);
                log.info("Return Order Reference Order Success: " + createdOrderV2);
                return createdOrderV2;
            } else if (soReturnV2.getSoReturnLine() == null || soReturnV2.getSoReturnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrders(apiHeader);
                log.info("Return Order Reference Order Failed : " + createdOrderV2);
                throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param soReturnV2
     * @return
     */
    public InboundOrderV2 postSOReturnV2(SaleOrderReturnV2 soReturnV2) {
        log.info("StoreReturnHeader received from External: " + soReturnV2);
        InboundOrderV2 savedSOReturn = saveSOReturnV2(soReturnV2);
        log.info("soReturnHeader: " + savedSOReturn);
        return savedSOReturn;
    }

    // SOReturnV2
    private InboundOrderV2 saveSOReturnV2(SaleOrderReturnV2 soReturnV2) {
        try {
            SOReturnHeaderV2 soReturnHeaderV2 = soReturnV2.getSoReturnHeader();
            List<SOReturnLineV2> salesOrderReturnLinesV2 = soReturnV2.getSoReturnLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(soReturnHeaderV2, apiHeader, CommonUtils.getNullPropertyNames(soReturnHeaderV2));
//            apiHeader.setTransferOrderNumber(soReturnHeaderV2.getAsnNumber());
            apiHeader.setReturnOrderReference(soReturnHeaderV2.getAsnNumber());
            apiHeader.setRefDocumentNo(soReturnHeaderV2.getAsnNumber());
            apiHeader.setBranchCode(soReturnHeaderV2.getBranchCode());
            apiHeader.setOrderId(soReturnHeaderV2.getAsnNumber());
            apiHeader.setCompanyCode(soReturnHeaderV2.getCompanyCode());
            apiHeader.setRefDocumentType("SALES RETURN");
            if(soReturnHeaderV2.getInboundOrderTypeId() != null) {
                apiHeader.setInboundOrderTypeId(soReturnHeaderV2.getInboundOrderTypeId());
            } else {
                apiHeader.setInboundOrderTypeId(3L);                                        // Hardcoded Value 2
            }
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(soReturnHeaderV2.getMiddlewareId());
            apiHeader.setMiddlewareTable(soReturnHeaderV2.getMiddlewareTable());
            apiHeader.setIsCompleted(soReturnHeaderV2.getIsCompleted());
            apiHeader.setIsCancelled(soReturnHeaderV2.getIsCancelled());
            apiHeader.setUpdatedOn(soReturnHeaderV2.getUpdatedOn());
            apiHeader.setRefDocumentType("SALES RETURN");

            if (soReturnHeaderV2.getWarehouseId() != null && !soReturnHeaderV2.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(soReturnHeaderV2.getWarehouseId());
            } else {
                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                soReturnHeaderV2.getCompanyCode(),
                                soReturnHeaderV2.getBranchCode(),
                                LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + dbWarehouse);
                apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            }
//            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
//                    LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

            Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
            for (SOReturnLineV2 soReturnLineV2 : salesOrderReturnLinesV2) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(soReturnLineV2, apiLine, CommonUtils.getNullPropertyNames(soReturnLineV2));

                //validation for bagSize performing BagUom
//                checkUom(soReturnLineV2.getUom(), soReturnLineV2.getAlternateUom(), soReturnLineV2.getBagSize());

                apiLine.setExpectedQty(soReturnLineV2.getExpectedQty());
                apiLine.setInvoiceNumber(soReturnLineV2.getInvoiceNumber());                // INV_NO
                apiLine.setSalesOrderReference(soReturnLineV2.getSalesOrderReference());
                apiLine.setLineReference(soReturnLineV2.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(soReturnLineV2.getSku());                                // ITM_CODE
                apiLine.setItemText(soReturnLineV2.getSkuDescription());                     // ITEM_TEXT
                apiLine.setStoreID(soReturnLineV2.getStoreID());
                apiLine.setSupplierPartNumber(soReturnLineV2.getSupplierPartNumber());
                apiLine.setUom("PIECE");
                apiLine.setPackQty(soReturnLineV2.getPackQty());
                apiLine.setOrigin(soReturnLineV2.getOrigin());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setManufacturerFullName(soReturnLineV2.getManufacturerFullName());
                apiLine.setMiddlewareId(soReturnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(soReturnLineV2.getMiddlewareTable());
                apiLine.setMiddlewareHeaderId(soReturnLineV2.getMiddlewareHeaderId());
                if(soReturnHeaderV2.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(soReturnHeaderV2.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(3L);
                }
                // EA_DATE
                try {
                    Date reqDelDate = new Date();
                    if (soReturnLineV2.getExpectedDate() != null) {
                        reqDelDate = DateUtils.convertStringToDate2(soReturnLineV2.getExpectedDate());
                    }
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new BadRequestException("Date format should be yyyy-MM-dd");
                }

                log.info("SOReturn ExpectedQtyInCases -----------> {}", soReturnLineV2.getExpectedQtyInCases());
                log.info("SOReturn ExpectedQtyInPieces ----------> {}", soReturnLineV2.getExpectedQtyInPieces());

                if (soReturnLineV2.getExpectedQtyInCases() > 0.0 && soReturnLineV2.getExpectedQtyInPieces() > 0.0) {
                    Double ordQty = soReturnLineV2.getExpectedQtyInPieces() / soReturnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
                    apiLine.setExpectedQty(ordQty);     // 25
                    apiLine.setOrderedQty(ordQty);      // 25
                    apiLine.setBagSize(ordQty);         // 25
                    apiLine.setNoBags(soReturnLineV2.getExpectedQtyInCases());
                } else {
                    //Double ordQty = soReturnLineV2.getExpectedQty();  // 50 / 2 => 25
                    apiLine.setExpectedQty(soReturnLineV2.getExpectedQty());     // 25
                    apiLine.setOrderedQty(soReturnLineV2.getExpectedQty());      // 25
                    apiLine.setBagSize(soReturnLineV2.getExpectedQty());         // 25
                }

                apiLine.setManufacturerCode(soReturnLineV2.getManufacturerName());
                apiLine.setManufacturerName(soReturnLineV2.getManufacturerName());
                apiLine.setManufacturerPartNo(soReturnLineV2.getManufacturerName());

//                apiLine.setManufacturerCode(MFR_NAME);
//                apiLine.setManufacturerName(MFR_NAME);
//                apiLine.setManufacturerPartNo(MFR_NAME);
                apiLine.setBrand(soReturnLineV2.getBrand());
                orderLinesV2.add(apiLine);
            }
            apiHeader.setLine(orderLinesV2);
            apiHeader.setOrderProcessedOn(new Date());

            if (soReturnV2.getSoReturnLine() != null && !soReturnV2.getSoReturnLine().isEmpty()) {

                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrders(apiHeader);
                log.info("Return Order Reference Order Success: " + createdOrderV2);
                return createdOrderV2;
            } else if (soReturnV2.getSoReturnLine() == null || soReturnV2.getSoReturnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrders(apiHeader);
                log.info("Return Order Reference Order Failed : " + createdOrderV2);
                throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param interWarehouseTransferInV2
     * @return
     */
    public InboundOrderV2 postInterWarehouseTransferInV2Upload(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
        log.info("InterWarehouseTransferHeaderV2 received from External: " + interWarehouseTransferInV2);
        InboundOrderV2 savedIWHReturnV2 = saveInterWarehouseTransferInV2Upload(interWarehouseTransferInV2);
        log.info("interWarehouseTransferHeaderV2: " + savedIWHReturnV2);
        return savedIWHReturnV2;
    }

    // InterWarehouseTransferInV2
    private InboundOrderV2 saveInterWarehouseTransferInV2Upload(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
        try {
            InterWarehouseTransferInHeaderV2 interWarehouseTransferInHeaderV2 = interWarehouseTransferInV2.getInterWarehouseTransferInHeader();
            List<InterWarehouseTransferInLineV2> interWarehouseTransferInLinesV2 = interWarehouseTransferInV2.getInterWarehouseTransferInLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(interWarehouseTransferInHeaderV2, apiHeader, CommonUtils.getNullPropertyNames(interWarehouseTransferInHeaderV2));
            apiHeader.setRefDocumentNo(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setOrderId(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setCompanyCode(interWarehouseTransferInHeaderV2.getToCompanyCode());
            apiHeader.setTransferOrderNumber(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setBranchCode(interWarehouseTransferInHeaderV2.getToBranchCode());
            apiHeader.setInboundOrderTypeId(2L);                // Hardcoded Value 3
            apiHeader.setRefDocumentType("STOCK TRANSFER");
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(interWarehouseTransferInHeaderV2.getMiddlewareId());
            apiHeader.setMiddlewareTable(interWarehouseTransferInHeaderV2.getMiddlewareTable());
            apiHeader.setTransferOrderDate(interWarehouseTransferInHeaderV2.getTransferOrderDate());
            apiHeader.setIsCompleted(interWarehouseTransferInHeaderV2.getIsCompleted());
            apiHeader.setUpdatedOn(interWarehouseTransferInHeaderV2.getUpdatedOn());
            apiHeader.setSourceCompanyCode(interWarehouseTransferInHeaderV2.getSourceCompanyCode());
            apiHeader.setSourceBranchCode(interWarehouseTransferInHeaderV2.getSourceBranchCode());

            // Get Warehouse
            Optional<Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            interWarehouseTransferInHeaderV2.getToCompanyCode(),
                            interWarehouseTransferInHeaderV2.getToBranchCode(),
                            LANG_ID,
                            0L
                    );
            log.info("dbWarehouse : " + dbWarehouse);
            apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
                    LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

            Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
            for (InterWarehouseTransferInLineV2 iwhTransferLineV2 : interWarehouseTransferInLinesV2) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(iwhTransferLineV2, apiLine, CommonUtils.getNullPropertyNames(iwhTransferLineV2));
                apiLine.setLineReference(iwhTransferLineV2.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(iwhTransferLineV2.getSku());                                // ITM_CODE
                apiLine.setItemText(iwhTransferLineV2.getSkuDescription());                     // ITEM_TEXT
                apiLine.setFromCompanyCode(iwhTransferLineV2.getFromCompanyCode());
                apiLine.setSourceBranchCode(iwhTransferLineV2.getFromBranchCode());
                apiLine.setSupplierPartNumber(iwhTransferLineV2.getSupplierPartNumber());        // PARTNER_ITM_CODE
                apiLine.setExpectedQty(iwhTransferLineV2.getExpectedQty());
                apiLine.setUom(iwhTransferLineV2.getUom());
                apiLine.setPackQty(iwhTransferLineV2.getPackQty());
                apiLine.setOrigin(iwhTransferLineV2.getOrigin());
                apiLine.setSupplierName(iwhTransferLineV2.getSupplierName());
                if (iwhTransferLineV2.getManufacturerCode() != null) {
                    apiLine.setManufacturerCode(iwhTransferLineV2.getManufacturerCode());
                } else {
                    if (apiHeader.getCompanyCode().equalsIgnoreCase(KW_COMPANY_CODE)) {
                        apiLine.setManufacturerCode(getMfrName(KW_COMPANY_CODE));
                    }
                }
                if (iwhTransferLineV2.getManufacturerName() != null) {
                    apiLine.setManufacturerName(iwhTransferLineV2.getManufacturerName());
                } else {
                    if (apiHeader.getCompanyCode().equalsIgnoreCase(KW_COMPANY_CODE)) {
                        apiLine.setManufacturerName(getMfrName(KW_COMPANY_CODE));
                    }
                }
                apiLine.setManufacturerCode(iwhTransferLineV2.getManufacturerName());
                apiLine.setManufacturerName(iwhTransferLineV2.getManufacturerName());
                apiLine.setManufacturerPartNo(iwhTransferLineV2.getManufacturerName());
                apiLine.setBrand(iwhTransferLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setInboundOrderTypeId(2L);

                apiLine.setTransferOrderNumber(iwhTransferLineV2.getTransferOrderNo());
                apiLine.setMiddlewareHeaderId(iwhTransferLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(iwhTransferLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(iwhTransferLineV2.getMiddlewareTable());

                orderLinesV2.add(apiLine);

                // EA_DATE
                try {
                    ZoneId defaultZoneId = ZoneId.systemDefault();
                    String sdate = iwhTransferLineV2.getExpectedDate();
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
                    throw new InboundOrderRequestException("Date format should be yyyy-MM-dd");
                }
            }
            apiHeader.setLine(orderLinesV2);
            apiHeader.setOrderProcessedOn(new Date());
            if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() != null &&
                    !interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrders(apiHeader);
                log.info("InterWarehouseTransferV2 Order Success: " + createdOrderV2);
                return createdOrderV2;
            } else if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() == null ||
                    interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrders(apiHeader);
                log.info("InterWarehouseTransferV2 Order Failed : " + createdOrderV2);
                throw new BadRequestException("InterWarehouseTransferInV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    //=======================SPAREX=============================================================
    /**
     * @param asnv2
     * @return
     */
    public InboundOrderV2 postWarehouseASNV10(ASNV2 asnv2) {
        log.info("ASNV2Header received from External: " + asnv2);
        InboundOrderV2 savedAsnV2Header = saveASNV10(asnv2);
        log.info("savedAsnV2Header: " + savedAsnV2Header);
        return savedAsnV2Header;
    }
    //===========================SPAREX===========================================================
    private InboundOrderV2 saveASNV10(ASNV2 asnv2) {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("SPAREX");

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

            if(apiHeader.getInboundOrderTypeId().equals(5L)) {
                apiHeader.setRefDocumentType("SUPPLIER INVOICE");
            }
            if(apiHeader.getInboundOrderTypeId().equals(2L)) {
                apiHeader.setRefDocumentType("Reprocessed Order");
            }


            apiHeader.setLanguageId(asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID);
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
                apiHeader.setInboundOrderTypeId(5L);                                            //Default
            }


            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNLineV2 asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));

                apiLine.setLineReference(asnLineV2.getLineReference());            // IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku());                            // ITM_CODE
                apiLine.setItemText(asnLineV2.getSkuDescription());                // ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());            // CONT_NO

                ImBasicData1V2 imBasicData1V2 =
                        imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(apiHeader.getLanguageId(),
                                apiHeader.getCompanyCode(), apiHeader.getBranchCode(),apiHeader.getWarehouseID(),apiLine.getItemCode(),0L);

                if (imBasicData1V2 != null) {
                    if(imBasicData1V2.getItemCode() == null) {
                        throw new BadRequestException("ItemCode is Mandatory");
                    }
                    apiLine.setStoreID(imBasicData1V2.getStorageSectionId());
                } else {
                    throw new BadRequestException("ImbasicData not found");
                }


                if (asnLineV2.getSupplierCode() != null) {
                    apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
                } else {
                    apiLine.setSupplierCode(asnV2Header.getSupplierCode());
                }
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
                apiLine.setManufacturerName(MFR_NAME_V10);
                apiLine.setManufacturerCode(MFR_NAME_V10);
                apiLine.setManufacturerPartNo(MFR_NAME_V10);
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());

                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(5L);                                            //Default
                }

                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setReceivedDate(asnLineV2.getReceivedDate());
                apiLine.setIsCancelled(asnLineV2.getIsCancelled());
                apiLine.setIsCompleted(asnLineV2.getIsCompleted());

                apiLine.setMiddlewareHeaderId(asnLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(asnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());
                apiLine.setOrderedQty(asnLineV2.getPackQty());
                apiLine.setExpectedQty(asnLineV2.getPackQty());

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
                            throw new InboundOrderRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                }

                apiLine.setPackQty(asnLineV2.getPackQty());                    // ITM_CASE_QTY
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {

                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb("SPAREX");
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param asnv2
     * @return
     */
    public InboundOrderV2 postWarehouseASNV9(ASNV2 asnv2) {
        log.info("ASNV2Header received from External: " + asnv2);
        InboundOrderV2 savedAsnV2Header = saveASNV9(asnv2);
        log.info("savedAsnV2Header: " + savedAsnV2Header);
        return savedAsnV2Header;
    }

    // POST ASNV2Header
    private InboundOrderV2 saveASNV9(ASNV2 asnv2) {
        try {
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("BF");
            ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
            List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();

            //validateBarcodeIds
//            huSerialValidation(asnLineV2s, asnV2Header.getAsnNumber());

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
            apiHeader.setRefDocumentType("Supplier Invoice");

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

                if(asnv2.getAsnHeader().getWarehouseId().equalsIgnoreCase("4100")) {
                    apiLine.setManufacturerName(MFR_NAME_V9);        // BRAND_NM
                    apiLine.setManufacturerCode(MFR_NAME_V9);
                    apiLine.setManufacturerFullName(MFR_NAME_V9);
                } else if(asnv2.getAsnHeader().getWarehouseId().equalsIgnoreCase("4200")) {
                    apiLine.setManufacturerName(MFR_NAME_V11);        // BRAND_NM for 2nd WH
                    apiLine.setManufacturerCode(MFR_NAME_V11);
                    apiLine.setManufacturerFullName(MFR_NAME_V11);
                } else if(asnv2.getAsnHeader().getWarehouseId().equalsIgnoreCase("4300")) {
                apiLine.setManufacturerName(MFR_NAME_V12);        // BRAND_NM for 2nd WH
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


//                Date parsedDate = DateUtils.parseCustomDate(asnLineV2.getBarcodeId());
//                if (parsedDate != null) {
//                    apiLine.setReceivedDate(parsedDate);
//                } else {
//                    System.out.println("Failed to parse date from barcode: " + asnLineV2.getBarcodeId());
//                }

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

                String routingDb = dbConfigRepository.getDbName(asnv2.getAsnHeader().getCompanyCode(),
                        asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId());
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
                log.info("Current Routing Db " + routingDb);

//                Long caseQty = stagingLineV2Repository.getAlternateUomQtyUpload(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getWarehouseID(), apiLine.getItemCode(), "1", "2");
//                Long createQty = stagingLineV2Repository.getAlternateUomQtyUpload(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getWarehouseID(), apiLine.getItemCode(), "1", "3");
//
//                if (caseQty == 0 || createQty == 0) {
//                    throw new BadRequestException("Uom Qty is Not Found This ItemCode ---> " + apiLine.getItemCode());
//                }

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
                InboundOrderV2 createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = orderService.createInboundOrders(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    //====================================================BF=========================================================
    /**
     * @param soReturnV2
     * @return
     */
    public InboundOrderV2 postSOReturnV9(SaleOrderReturnV2 soReturnV2) {
        log.info("StoreReturnHeader received from External: " + soReturnV2);
        InboundOrderV2 savedSOReturn = saveSOReturnV9(soReturnV2);
        log.info("soReturnHeader: " + savedSOReturn);
        return savedSOReturn;
    }

    //====================================================BF=========================================================
    private InboundOrderV2 saveSOReturnV9(SaleOrderReturnV2 soReturnV2) {
        try {
            SOReturnHeaderV2 soReturnHeaderV2 = soReturnV2.getSoReturnHeader();
            List<SOReturnLineV2> salesOrderReturnLinesV2 = soReturnV2.getSoReturnLine();

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(soReturnHeaderV2, apiHeader, CommonUtils.getNullPropertyNames(soReturnHeaderV2));
//            apiHeader.setTransferOrderNumber(soReturnHeaderV2.getAsnNumber());
            apiHeader.setReturnOrderReference(soReturnHeaderV2.getAsnNumber());
            apiHeader.setRefDocumentNo(soReturnHeaderV2.getAsnNumber());
            apiHeader.setBranchCode(soReturnHeaderV2.getBranchCode());
            apiHeader.setOrderId(soReturnHeaderV2.getAsnNumber());
            apiHeader.setCompanyCode(soReturnHeaderV2.getCompanyCode());
            apiHeader.setRefDocumentType("SalesReturn");
            apiHeader.setCustomerId(soReturnHeaderV2.getCustomerId());
            if (soReturnHeaderV2.getInboundOrderTypeId() != null) {
                apiHeader.setInboundOrderTypeId(soReturnHeaderV2.getInboundOrderTypeId());
            } else {
                apiHeader.setInboundOrderTypeId(3L);                                        // Hardcoded Value 2
            }
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setMiddlewareId(soReturnHeaderV2.getMiddlewareId());
            apiHeader.setMiddlewareTable(soReturnHeaderV2.getMiddlewareTable());
            apiHeader.setIsCompleted(soReturnHeaderV2.getIsCompleted());
            apiHeader.setIsCancelled(soReturnHeaderV2.getIsCancelled());
            apiHeader.setUpdatedOn(soReturnHeaderV2.getUpdatedOn());
            apiHeader.setRefDocumentType("SALES RETURN");
            apiHeader.setCustomerId(soReturnHeaderV2.getCustomerId());
            apiHeader.setCustomerName(soReturnHeaderV2.getCustomerName());

            if (soReturnHeaderV2.getWarehouseId() != null && !soReturnHeaderV2.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(soReturnHeaderV2.getWarehouseId());
            } else {
                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                soReturnHeaderV2.getCompanyCode(),
                                soReturnHeaderV2.getBranchCode(),
                                LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + dbWarehouse);
                apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            }
//            apiHeader.setRefDocumentType(getInboundOrderTypeDesc(apiHeader.getCompanyCode(), apiHeader.getBranchCode(),
//                    LANG_ID, apiHeader.getWarehouseID(), apiHeader.getInboundOrderTypeId()));

            Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
            for (SOReturnLineV2 soReturnLineV2 : salesOrderReturnLinesV2) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(soReturnLineV2, apiLine, CommonUtils.getNullPropertyNames(soReturnLineV2));

                //validation for bagSize performing BagUom
//                checkUom(soReturnLineV2.getUom(), soReturnLineV2.getAlternateUom(), soReturnLineV2.getBagSize());

                Date parsedDate = DateUtils.parseCustomDate(soReturnLineV2.getBarcodeId());
                if (parsedDate != null) {
                    apiLine.setReceivedDate(parsedDate);
                } else {
                    System.out.println("Failed to parse date from barcode: " + soReturnLineV2.getBarcodeId());
                }
                apiLine.setExpectedQty(soReturnLineV2.getExpectedQty());
                apiLine.setInvoiceNumber(soReturnLineV2.getInvoiceNumber());                // INV_NO
                apiLine.setSalesOrderReference(soReturnLineV2.getSalesOrderReference());
                apiLine.setLineReference(soReturnLineV2.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(soReturnLineV2.getSku());                                // ITM_CODE
                apiLine.setItemText(soReturnLineV2.getSkuDescription());                     // ITEM_TEXT
                apiLine.setStoreID(soReturnLineV2.getStoreID());
                apiLine.setSupplierPartNumber(soReturnLineV2.getSupplierPartNumber());
                apiLine.setUom(soReturnLineV2.getUom());
                apiLine.setPackQty(soReturnLineV2.getPackQty());
                apiLine.setOrigin(soReturnLineV2.getOrigin());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setManufacturerFullName(soReturnLineV2.getManufacturerFullName());
                apiLine.setMiddlewareId(soReturnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(soReturnLineV2.getMiddlewareTable());
                apiLine.setMiddlewareHeaderId(soReturnLineV2.getMiddlewareHeaderId());
                if (soReturnHeaderV2.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(soReturnHeaderV2.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(3L);
                }
                // EA_DATE
                try {
                    Date reqDelDate = new Date();
//					if (soReturnLineV2.getExpectedDate().length() > 10) {
//						reqDelDate = DateUtils.convertStringToDateWithTime(soReturnLineV2.getExpectedDate());
//					}
                    if (soReturnLineV2.getExpectedDate() != null) {
                        reqDelDate = DateUtils.convertStringToDate2(soReturnLineV2.getExpectedDate());
                    }
                    apiLine.setExpectedDate(reqDelDate);

                } catch (Exception e) {
                    throw new BadRequestException("Date format should be yyyy-MM-dd");
                }

//                if(soReturnLineV2.getUom() != null) {
//                    AlternateUomImpl alternateUom = getUom(apiHeader.getCompanyCode(), apiHeader.getBranchCode(), apiHeader.getLanguageId(),
//                                                           apiHeader.getWarehouseID(), apiLine.getItemCode(), soReturnLineV2.getUom());
//                    if(alternateUom == null) {
//                        throw new BadRequestException("AlternateUom is not available for this item : " + apiLine.getItemCode());
//                    }
//                    if (alternateUom != null) {
//                        apiLine.setUom(alternateUom.getUom());
//                        apiLine.setAlternateUom(alternateUom.getAlternateUom());
//                        apiLine.setBagSize(alternateUom.getAlternateUomQty());
//                    apiLine.setNoBags(soReturnLineV2.getExpectedQty());
////                        double orderQty = getQuantity(soReturnLineV2.getExpectedQty(), alternateUom.getAlternateUomQty());
////                        apiLine.setExpectedQty(orderQty);
////                        apiLine.setOrderedQty(orderQty);
//                }
//                }
//
//                log.info("SOReturn ExpectedQtyInCases -----------> {}", soReturnLineV2.getExpectedQtyInCases());
//                log.info("SOReturn ExpectedQtyInPieces ----------> {}", soReturnLineV2.getExpectedQtyInPieces());
//                if (soReturnLineV2.getExpectedQtyInCases() != null && soReturnLineV2.getExpectedQtyInPieces() != null) {
//                    Double ordQty = soReturnLineV2.getExpectedQtyInPieces() / soReturnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
//                    apiLine.setExpectedQty(ordQty);     // 25
//                    apiLine.setOrderedQty(ordQty);      // 25
//                    apiLine.setBagSize(ordQty);         // 25
//                } else {
//                    Double ordQty = soReturnLineV2.getExpectedQty() / soReturnLineV2.getNoBags();  // 50 / 2 => 25
//                    apiLine.setExpectedQty(ordQty);     // 25
//                    apiLine.setOrderedQty(ordQty);      // 25
//                    apiLine.setBagSize(ordQty);         // 25
//                }

                apiLine.setNoBags(soReturnLineV2.getExpectedQtyInCases());

                if(soReturnHeaderV2.getWarehouseId().equalsIgnoreCase("4100")) {
                    apiLine.setManufacturerCode(MFR_NAME_V9);
                    apiLine.setManufacturerName(MFR_NAME_V9);
                    apiLine.setManufacturerPartNo(MFR_NAME_V9);
                }else if(soReturnHeaderV2.getWarehouseId().equalsIgnoreCase("4200")){
                    apiLine.setManufacturerCode(MFR_NAME_V11);
                    apiLine.setManufacturerName(MFR_NAME_V11);
                    apiLine.setManufacturerPartNo(MFR_NAME_V11);
                }else if(soReturnHeaderV2.getWarehouseId().equalsIgnoreCase("4300")){
                    apiLine.setManufacturerCode(MFR_NAME_V12);
                    apiLine.setManufacturerName(MFR_NAME_V12);
                    apiLine.setManufacturerPartNo(MFR_NAME_V12);
                }
                apiLine.setBrand(soReturnLineV2.getBrand());
                apiLine.setVehicleNo(soReturnLineV2.getVehicleNo());
                apiLine.setVehicleUnloadingDate(soReturnLineV2.getVehicleUnloadingDate());
                apiLine.setVehicleReportingDate(soReturnLineV2.getVehicleReportingDate());
                orderLinesV2.add(apiLine);
            }
            apiHeader.setLine(orderLinesV2);
            apiHeader.setOrderProcessedOn(new Date());

            if (soReturnV2.getSoReturnLine() != null && !soReturnV2.getSoReturnLine().isEmpty()) {

                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrders(apiHeader);
                log.info("Return Order Reference Order Success: " + createdOrderV2);
                return createdOrderV2;
            } else if (soReturnV2.getSoReturnLine() == null || soReturnV2.getSoReturnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrders(apiHeader);
                log.info("Return Order Reference Order Failed : " + createdOrderV2);
                throw new BadRequestException("Return Order Reference Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    /**
     * @param interWarehouseTransferInV2
     * @return
     */
    public void postInterWarehouseTransferInV9(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
        InboundOrderV2 inboundOrder = saveInterWarehouseTransferInV9(interWarehouseTransferInV2);
        log.info("interWarehouseTransferHeaderV2: " + inboundOrder);
        InterWarehouseTransferInHeaderV2 interWhTransferInHeader = interWarehouseTransferInV2.getInterWarehouseTransferInHeader();

        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(interWhTransferInHeader.getSourceCompanyCode(),
                interWhTransferInHeader.getSourceBranchCode(), interWhTransferInHeader.getWarehouseId());
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);

        String idMasterAuthToken = getIDMasterAuthToken();
        String preInboundNo = getNextRangeNumber(2L, interWhTransferInHeader.getToCompanyCode(), interWhTransferInHeader.getToBranchCode(),
                interWhTransferInHeader.getLanguageId(), inboundOrder.getWarehouseID(), idMasterAuthToken);

        PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
        BeanUtils.copyProperties(interWhTransferInHeader, preInboundHeader, CommonUtils.getNullPropertyNames(interWhTransferInHeader));
        preInboundHeader.setCompanyCode(interWhTransferInHeader.getToCompanyCode());
        preInboundHeader.setPlantId(interWhTransferInHeader.getToBranchCode());
        preInboundHeader.setWarehouseId(inboundOrder.getWarehouseID());
        preInboundHeader.setLanguageId(interWhTransferInHeader.getLanguageId());

//        preInboundHeader.setPartnerCode(interWhTransferInHeader.getToBranchCode());
//        preInboundHeader.setFromBranchCode(interWhTransferInHeader.getSourceBranchCode());
//        preInboundHeader.setTargetBranchCode(interWhTransferInHeader.getToBranchCode());
        //To Wh-Id
        preInboundHeader.setReferenceField1(inboundOrder.getWarehouseID());
        preInboundHeader.setRefDocNumber(interWhTransferInHeader.getTransferOrderNumber());

        preInboundHeader.setInboundOrderTypeId(4L);
        preInboundHeader.setStatusId(5L);
        preInboundHeader.setStatusDescription("RECEIPT IN PROGRESS");
        preInboundHeader.setReferenceDocumentType("TRANSVERSE IN");
//        preInboundHeader.setCustomerName("TRANSVERSE OUT");                                //HardCoded
        preInboundHeader.setMiddlewareId(String.valueOf(interWhTransferInHeader.getMiddlewareId()));
        preInboundHeader.setMiddlewareTable(interWhTransferInHeader.getMiddlewareTable());
        preInboundHeader.setDeletionIndicator(0L);

        IKeyValuePair iKeyValuePair = inboundOrderLinesV2Repository.getV2Description(
                interWhTransferInHeader.getToCompanyCode(),
                interWhTransferInHeader.getToBranchCode(),
                inboundOrder.getWarehouseID());
        if (iKeyValuePair != null) {
            preInboundHeader.setCompanyDescription(iKeyValuePair.getCompanyDesc());
            preInboundHeader.setPlantDescription(iKeyValuePair.getPlantDesc());
            preInboundHeader.setWarehouseDescription(iKeyValuePair.getWarehouseDesc());
        }

        preInboundHeader.setTransferRequestType("TRANSVERSE IN");
//        if(interWhTransferInHeader.getWarehouseId().equalsIgnoreCase("4100")) {
//            preInboundHeader.setManufacturerFullName(MFR_NAME_V9);
//        }else{
//            preInboundHeader.setManufacturerFullName(MFR_NAME_V11);
//        }

        preInboundHeader.setTransferOrderDate(interWhTransferInHeader.getTransferOrderDate());
        preInboundHeader.setPreInboundNo(preInboundNo);

        preInboundHeader.setCreatedOn(new Date());
        preInboundHeader.setCreatedBy(MW_AMS);
        preInboundHeaderV2Repository.save(preInboundHeader);
        log.info("preInboundHeader Saved successfully");

        List<InterWarehouseTransferInLineV2> interWarehouseTransferInLines = interWarehouseTransferInV2.getInterWarehouseTransferInLine();
        List<PreInboundLineEntityV2> preInboundLines = new ArrayList<>();
        for (InterWarehouseTransferInLineV2 iwhTransferLine : interWarehouseTransferInLines) {

            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(iwhTransferLine, preInboundLine, CommonUtils.getNullPropertyNames(iwhTransferLine));
            preInboundLine.setCompanyCode(preInboundHeader.getCompanyCode());
            preInboundLine.setPlantId(preInboundHeader.getPlantId());
            preInboundLine.setWarehouseId(preInboundHeader.getWarehouseId());
            preInboundLine.setLanguageId(preInboundHeader.getLanguageId());

            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setBarcodeId(iwhTransferLine.getBarcodeId());

            // REF DOC Number
            preInboundLine.setRefDocNumber(preInboundHeader.getRefDocNumber());

            // PARTNER_CODE
//            preInboundLine.setPartnerCode(interWhTransferInHeader.getToBranchCode());

            // IB__LINE_NO
            preInboundLine.setLineNo(iwhTransferLine.getLineReference());

            // ITM_CODE
            preInboundLine.setItemCode(iwhTransferLine.getSku());

            // OB_ORD_TYP_ID
            preInboundLine.setInboundOrderTypeId(1L);

            // STATUS_ID
            preInboundLine.setStatusId(5L);
            preInboundLine.setStatusDescription(preInboundHeader.getStatusDescription());

            // STCK_TYP_ID
            preInboundLine.setStockTypeId(1L);

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            //To Wh-Id
            preInboundLine.setReferenceField1(interWhTransferInHeader.getWarehouseId());

            preInboundLine.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());


            preInboundLine.setCompanyDescription(preInboundHeader.getCompanyDescription());
            preInboundLine.setPlantDescription(preInboundHeader.getPlantDescription());
            preInboundLine.setWarehouseDescription(preInboundHeader.getWarehouseDescription());

            // ORD_QTY
            preInboundLine.setOrderQty(iwhTransferLine.getExpectedQty());

            // ORD_UOM
            preInboundLine.setOrderUom(iwhTransferLine.getUom());

//             REQ_DEL_DATE
//            preInboundLine.set(preInboundHeader.getRequiredDeliveryDate());

            //MFR_NAME
            if(iwhTransferLine.getManufacturerCode() != null && iwhTransferLine.getManufacturerName() != null){
                if(interWhTransferInHeader.getWarehouseId().equalsIgnoreCase("4100")) {
                    preInboundLine.setManufacturerCode(iwhTransferLine.getManufacturerCode());
                    preInboundLine.setManufacturerName(iwhTransferLine.getManufacturerName());
                    preInboundLine.setManufacturerFullName(iwhTransferLine.getManufacturerName());
                } else if(interWhTransferInHeader.getWarehouseId().equalsIgnoreCase("4200")){
                    preInboundLine.setManufacturerCode(iwhTransferLine.getManufacturerCode());
                    preInboundLine.setManufacturerName(iwhTransferLine.getManufacturerName());
                    preInboundLine.setManufacturerFullName(iwhTransferLine.getManufacturerName());
                } else if(interWhTransferInHeader.getWarehouseId().equalsIgnoreCase("4300")){
                    preInboundLine.setManufacturerCode(iwhTransferLine.getManufacturerCode());
                    preInboundLine.setManufacturerName(iwhTransferLine.getManufacturerName());
                    preInboundLine.setManufacturerFullName(iwhTransferLine.getManufacturerName());
                }
            }else {
                if(interWhTransferInHeader.getWarehouseId().equalsIgnoreCase("4100")) {
                    preInboundLine.setManufacturerCode(MFR_NAME_V9);
                    preInboundLine.setManufacturerName(MFR_NAME_V9);
                    preInboundLine.setManufacturerFullName(MFR_NAME_V9);
                } else if(interWhTransferInHeader.getWarehouseId().equalsIgnoreCase("4200")) {
                    preInboundLine.setManufacturerCode(MFR_NAME_V11);
                    preInboundLine.setManufacturerName(MFR_NAME_V11);
                    preInboundLine.setManufacturerFullName(MFR_NAME_V11);
                } else if(interWhTransferInHeader.getWarehouseId().equalsIgnoreCase("4300")) {
                    preInboundLine.setManufacturerCode(MFR_NAME_V12);
                    preInboundLine.setManufacturerName(MFR_NAME_V12);
                    preInboundLine.setManufacturerFullName(MFR_NAME_V12);
                }
            }


            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(preInboundHeader.getCreatedBy());
            preInboundLine.setCreatedOn(new Date());
            preInboundLines.add(preInboundLine);
        }
        preInboundLineV2Repository.saveAll(preInboundLines);
        log.info("preInboundLine Saved Successfully");
    }

    // InterWarehouseTransferInV2
    private InboundOrderV2 saveInterWarehouseTransferInV9(InterWarehouseTransferInV2 interWarehouseTransferInV2) {
        try {
            InterWarehouseTransferInHeaderV2 interWarehouseTransferInHeaderV2 = interWarehouseTransferInV2.getInterWarehouseTransferInHeader();
            List<InterWarehouseTransferInLineV2> interWarehouseTransferInLinesV2 = interWarehouseTransferInV2.getInterWarehouseTransferInLine();

            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(interWarehouseTransferInHeaderV2.getSourceCompanyCode(),
                    interWarehouseTransferInHeaderV2.getSourceBranchCode(), interWarehouseTransferInHeaderV2.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);

            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(interWarehouseTransferInHeaderV2, apiHeader, CommonUtils.getNullPropertyNames(interWarehouseTransferInHeaderV2));
            apiHeader.setRefDocumentNo(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setOrderId(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setCompanyCode(interWarehouseTransferInHeaderV2.getToCompanyCode());
            apiHeader.setTransferOrderNumber(interWarehouseTransferInHeaderV2.getTransferOrderNumber());
            apiHeader.setBranchCode(interWarehouseTransferInHeaderV2.getToBranchCode());
            // Get Warehouse
            Optional<Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                            interWarehouseTransferInHeaderV2.getToCompanyCode(),
                            interWarehouseTransferInHeaderV2.getToBranchCode(),
                            "EN",
                            0L
                    );
            log.info("dbWarehouse : " + dbWarehouse);
            apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());

            apiHeader.setInboundOrderTypeId(4L);                // Hardcoded Value 3
            apiHeader.setRefDocumentType("WMS to WMS");
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setSourceCompanyCode(interWarehouseTransferInHeaderV2.getSourceCompanyCode());
            apiHeader.setSourceBranchCode(interWarehouseTransferInHeaderV2.getSourceBranchCode());
            apiHeader.setMiddlewareId(interWarehouseTransferInHeaderV2.getMiddlewareId());
            apiHeader.setMiddlewareTable(interWarehouseTransferInHeaderV2.getMiddlewareTable());
            apiHeader.setIsCompleted(interWarehouseTransferInHeaderV2.getIsCompleted());
            apiHeader.setUpdatedOn(interWarehouseTransferInHeaderV2.getUpdatedOn());


            apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());
            Set<InboundOrderLinesV2> orderLinesV2 = new HashSet<>();
            for (InterWarehouseTransferInLineV2 iwhTransferLineV2 : interWarehouseTransferInLinesV2) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                apiLine.setLineReference(iwhTransferLineV2.getLineReference());                 // IB_LINE_NO
                apiLine.setItemCode(iwhTransferLineV2.getSku());                                // ITM_CODE
                apiLine.setItemText(iwhTransferLineV2.getSkuDescription());                     // ITEM_TEXT
                apiLine.setFromCompanyCode(apiHeader.getCompanyCode());
                apiLine.setSourceBranchCode(apiHeader.getBranchCode());
                apiLine.setIsCompleted(dbWarehouse.get().getWarehouseId());
                apiLine.setSupplierPartNumber(iwhTransferLineV2.getSupplierPartNumber());        // PARTNER_ITM_CODE
                apiLine.setManufacturerName(iwhTransferLineV2.getManufacturerName());            // BRND_NM
                apiLine.setExpectedQty(iwhTransferLineV2.getExpectedQty());
                apiLine.setUom(iwhTransferLineV2.getUom());
                apiLine.setPackQty(iwhTransferLineV2.getPackQty());
                apiLine.setOrigin(iwhTransferLineV2.getOrigin());
                apiLine.setSupplierName(iwhTransferLineV2.getSupplierName());
                apiLine.setManufacturerCode(iwhTransferLineV2.getManufacturerCode());
                apiLine.setInboundOrderTypeId(4L);

                apiLine.setTransferOrderNumber(iwhTransferLineV2.getTransferOrderNo());
                apiLine.setMiddlewareId(iwhTransferLineV2.getMiddlewareId());
                apiLine.setMiddlewareHeaderId(iwhTransferLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareTable(iwhTransferLineV2.getMiddlewareTable());

                // EA_DATE
                try {
                    Date reqDelDate = new Date();
                    if (iwhTransferLineV2.getExpectedDate() != null) {
                        reqDelDate = DateUtils.convertStringToDate2(iwhTransferLineV2.getExpectedDate());
                    }
                    apiLine.setExpectedDate(reqDelDate);
                } catch (Exception e) {
                    throw new BadRequestException("Date format should be MM-dd-yyyy");
                }
                apiLine.setBrand(iwhTransferLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
                orderLinesV2.add(apiLine);
            }
            apiHeader.setLine(orderLinesV2);
            apiHeader.setOrderProcessedOn(new Date());
            if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() != null &&
                    !interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
                apiHeader.setProcessedStatusId(0L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
                log.info("InterWarehouseTransferV2 Order Success: " + createdOrderV2);
                return createdOrderV2;
            } else if (interWarehouseTransferInV2.getInterWarehouseTransferInLine() == null ||
                    interWarehouseTransferInV2.getInterWarehouseTransferInLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrderV2 = orderService.createInboundOrdersV2(apiHeader);
                log.info("InterWarehouseTransferV2 Order Failed : " + createdOrderV2);
                throw new BadRequestException("InterWarehouseTransferInV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}
