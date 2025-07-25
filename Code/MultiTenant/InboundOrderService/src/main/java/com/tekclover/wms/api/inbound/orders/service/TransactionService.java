package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundOrderCancelInput;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    OrderService orderService;
    @Autowired
    MastersService mastersService;
    @Autowired
    DbConfigRepository dbConfigRepository;
    //-------------------------------------------------------------------------------------------
    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

    //-------------------------------------------------------------------Inbound---------------------------------------------------------------
    public WarehouseApiResponse processInboundOrder() throws IllegalAccessException, InvocationTargetException, ParseException {
        WarehouseApiResponse warehouseApiResponse = new WarehouseApiResponse();
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        List<InboundOrderV2> sqlInboundList = inboundOrderV2Repository.findTopByProcessedStatusIdOrderByOrderReceivedOn(0L);
        log.info("ib sql header list: " + sqlInboundList);


        // Inbound_Order_Update Process_status_Id = 1
        sqlInboundList.stream().forEach(ibOrder -> {
            log.info("Inbound Order Processing Records" + sqlInboundList.size());
            inboundOrderV2Repository.updateProcessStatusId(ibOrder.getInboundOrderHeaderId());
        });

        List<InboundIntegrationHeader> inboundList = new ArrayList<>();
        for (InboundOrderV2 dbOBOrder : sqlInboundList) {
            InboundIntegrationHeader inboundIntegrationHeader = new InboundIntegrationHeader();
            BeanUtils.copyProperties(dbOBOrder, inboundIntegrationHeader, CommonUtils.getNullPropertyNames(dbOBOrder));
            inboundIntegrationHeader.setId(dbOBOrder.getOrderId());
            inboundIntegrationHeader.setMiddlewareId(String.valueOf(dbOBOrder.getMiddlewareId()));
//            inboundIntegrationHeader.setMiddlewareTable(dbOBOrder.getMiddlewareTable());
//            inboundIntegrationHeader.setSourceBranchCode(dbOBOrder.getSourceBranchCode());
//            inboundIntegrationHeader.setSourceCompanyCode(dbOBOrder.getSourceCompanyCode());
//            inboundIntegrationHeader.setCustomerCode(dbOBOrder.getCustomerCode());
//            inboundIntegrationHeader.setTransferRequestType(dbOBOrder.getTransferRequestType());
            if (dbOBOrder.getCustomerId() != null) {
                inboundIntegrationHeader.setCustomerId(dbOBOrder.getCustomerId());
            }
            if (dbOBOrder.getCustomerName() != null) {
                inboundIntegrationHeader.setCustomerName(dbOBOrder.getCustomerName());
            }

            log.info("ib line list: " + dbOBOrder.getLine().size());
            List<InboundIntegrationLine> inboundIntegrationLineList = new ArrayList<>();
            for (InboundOrderLinesV2 line : dbOBOrder.getLine()) {
                InboundIntegrationLine inboundIntegrationLine = new InboundIntegrationLine();
                BeanUtils.copyProperties(line, inboundIntegrationLine, CommonUtils.getNullPropertyNames(line));
                inboundIntegrationLine.setLineReference(line.getLineReference());
                //inboundIntegrationLine.setItemCode(line.getItemCode().replaceAll("[^a-zA-Z0-9-_]", ""));    //For Knowell
//                inboundIntegrationLine.setItemCode(line.getItemCode());
//                inboundIntegrationLine.setItemText(line.getItemText());
//                inboundIntegrationLine.setInvoiceNumber(line.getInvoiceNumber());
//                inboundIntegrationLine.setContainerNumber(line.getContainerNumber());
//                inboundIntegrationLine.setSupplierCode(line.getSupplierCode());
//                inboundIntegrationLine.setSupplierPartNumber(line.getSupplierPartNumber());
//                inboundIntegrationLine.setManufacturerName(line.getManufacturerName());
//                inboundIntegrationLine.setManufacturerPartNo(line.getManufacturerPartNo());
//                inboundIntegrationLine.setExpectedDate(line.getExpectedDate());
                inboundIntegrationLine.setOrderedQty(line.getExpectedQty());
//                inboundIntegrationLine.setUom(line.getUom());
//                inboundIntegrationLine.setItemCaseQty(line.getItemCaseQty());
//                inboundIntegrationLine.setSalesOrderReference(line.getSalesOrderReference());
//                inboundIntegrationLine.setManufacturerCode(line.getManufacturerCode());
//                inboundIntegrationLine.setOrigin(line.getOrigin());
//                inboundIntegrationLine.setBrand(line.getBrand());
//                inboundIntegrationLine.setSourceCompanyCode(dbOBOrder.getSourceCompanyCode());
//                inboundIntegrationLine.setSourceBranchCode(dbOBOrder.getSourceBranchCode());
//                inboundIntegrationLine.setSupplierName(line.getSupplierName());
                inboundIntegrationLine.setMiddlewareId(String.valueOf(line.getMiddlewareId()));
                inboundIntegrationLine.setMiddlewareHeaderId(String.valueOf(line.getMiddlewareHeaderId()));
//                inboundIntegrationLine.setMiddlewareTable(line.getMiddlewareTable());
//                inboundIntegrationLine.setManufacturerFullName(line.getManufacturerFullName());
//                inboundIntegrationLine.setPurchaseOrderNumber(line.getPurchaseOrderNumber());
//                inboundIntegrationLine.setContainerNumber(line.getContainerNumber());
                inboundIntegrationLine.setManufacturerDate(line.getReceivedDate());
                inboundIntegrationHeader.setContainerNo(line.getContainerNumber());
//                inboundIntegrationLine.setVehicleNo(line.getVehicleNo());
//                inboundIntegrationLine.setVehicleUnloadingDate(line.getVehicleUnloadingDate());
//                inboundIntegrationLine.setVehicleReportingDate(line.getVehicleReportingDate());
                if (inboundIntegrationHeader.getCustomerId() != null) {
                    inboundIntegrationLine.setCustomerId(inboundIntegrationHeader.getCustomerId());
                }
                if (inboundIntegrationHeader.getCustomerName() != null) {
                    inboundIntegrationLine.setCustomerName(inboundIntegrationHeader.getCustomerName());
                }
                inboundIntegrationLineList.add(inboundIntegrationLine);
            }
            inboundIntegrationHeader.setInboundIntegrationLine(inboundIntegrationLineList);
            inboundList.add(inboundIntegrationHeader);
        }

        if (!inboundList.isEmpty()) {
            log.info("Latest InboundOrder found: " + inboundList);
            Iterator<InboundIntegrationHeader> iterator = inboundList.iterator();
            while (iterator.hasNext()) {
                InboundIntegrationHeader inbound = iterator.next();
                try {
                    log.info("InboundOrder ID : {}", inbound.getRefDocumentNo());
                    String profile = dbConfigRepository.getDbName(inbound.getCompanyCode(), inbound.getBranchCode(), inbound.getWarehouseID());
                    log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);

                    DataBaseContextHolder.clear();
                    DataBaseContextHolder.setCurrentDb(profile);
                    InboundHeaderV2 inboundHeader = null;

                    if (profile != null) {
                        switch (profile) {
                            case "FAHAHEEL":
                            case "AUTO_LAP":
                                inboundHeader = orderService.processInboundReceivedV2(inbound.getRefDocumentNo(), inbound);
                                break;
                            case "NAMRATHA":
                                inboundHeader = orderService.processInboundReceivedV3(inbound.getRefDocumentNo(), inbound);
                                break;
                            case "REEFERON":
                                inboundHeader = orderService.processInboundReceivedV5(inbound.getRefDocumentNo(), inbound);
                                break;
                            case "KNOWELL":
                                inboundHeader = orderService.processInboundReceivedV7(inbound.getRefDocumentNo(), inbound);
                                break;
                        }
                    }

                    if (inboundHeader != null) {
                        orderService.updateProcessedInboundOrderV2(inbound.getRefDocumentNo(), inbound.getInboundOrderTypeId(), 10L);
                        iterator.remove(); // Safe removal
                        warehouseApiResponse.setStatusCode("200");
                        warehouseApiResponse.setMessage("Success");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error on inbound processing: {}", e);

                    boolean isLockOrDeadlock = e.toString().contains("SQLState: 40001") ||
                            e.toString().contains("SQL Error: 1205") ||
                            e.toString().contains("was deadlocked on lock") ||
                            e.toString().contains("CannotAcquireLockException") ||
                            e.toString().contains("LockAcquisitionException") ||
                            e.toString().contains("UnexpectedRollbackException");

                    Long status = isLockOrDeadlock ? 900L : 100L;
                    orderService.updateProcessedInboundOrderV2(inbound.getRefDocumentNo(), inbound.getInboundOrderTypeId(), status);

                    InboundOrderCancelInput cancelInput = new InboundOrderCancelInput();
                    cancelInput.setCompanyCodeId(inbound.getCompanyCode());
                    cancelInput.setPlantId(inbound.getBranchCode());
                    cancelInput.setRefDocNumber(inbound.getRefDocumentNo());
                    cancelInput.setReferenceField1(getInboundOrderTypeTable(inbound.getInboundOrderTypeId()));

                    String errorDesc = "Unknown Error";
                    try {
                        if (e.toString().contains("message")) {
                            errorDesc = e.toString().substring(e.toString().indexOf("message") + 9).replaceAll("}]", "");
                        }
                        if (e.toString().contains("DataIntegrityViolationException") || e.toString().contains("ConstraintViolationException")) {
                            errorDesc = "Null Pointer Exception";
                        } else if (e.toString().contains("CannotAcquireLockException") ||
                                e.toString().contains("LockAcquisitionException") ||
                                e.toString().contains("SQLServerException") ||
                                e.toString().contains("UnexpectedRollbackException")) {
                            errorDesc = "SQLServerException";
                        } else if (e.toString().contains("BadRequestException")) {
                            errorDesc = e.toString().substring(e.toString().indexOf("BadRequestException:") + 20);
                        }
                    } catch (Exception ex) {
                        throw new BadRequestException("ErrorDesc Extract Error: " + ex);
                    }

                    cancelInput.setRemarks(errorDesc);
                    mastersService.sendMail(cancelInput);

                    try {
                        // preinboundheaderService.createInboundIntegrationLogV2(inbound, e.toString());
                        iterator.remove(); // Safe removal
                    } catch (Exception ex) {
                        iterator.remove();
                        throw new RuntimeException(ex);
                    }

                    warehouseApiResponse.setStatusCode("1400");
                    warehouseApiResponse.setMessage("Failure");
                }
            }
        }
        return warehouseApiResponse;
    }


    /**
     * @param referenceDocumentTypeId
     * @return
     */
    public String getInboundOrderTypeTable(Long referenceDocumentTypeId) {
        String referenceDocumentType = null;

        if (referenceDocumentTypeId == 1) {
            referenceDocumentType = "SUPPLIERINVOICEHEADER";
        }
        if (referenceDocumentTypeId == 2) {
            referenceDocumentType = "SALESRETURNHEADER"; //sale return -7(Bin Class Id)
        }
        if (referenceDocumentTypeId == 3 || referenceDocumentTypeId == 4) {
            referenceDocumentType = "TRANSFERINHEADER"; //b2b
        }
        if (referenceDocumentTypeId == 5) {
            referenceDocumentType = "STOCKRECEIPTHEADER";
        }

        return referenceDocumentType;
    }
}