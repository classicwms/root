package com.tekclover.wms.api.inbound.orders.service;


import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.PickListCancellation;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.SalesInvoice;
import com.tekclover.wms.api.inbound.orders.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SalesInvoiceService {

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    protected AuthTokenService authTokenService;
    @Autowired
    StagingLineV2Repository stagingLineV2Repository;
    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;
//    @Autowired
//    OrderManagementLineService orderManagementLineService;
    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;
    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    protected String statusDescription = null;
    @Autowired
    MastersService mastersService;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    InventoryV2Repository inventoryV2Repository;
    @Autowired
    OrderService orderService;


    public List<OutboundOrderV2> createSalesInvoiceList(List<SalesInvoice> salesInvoice) {
        List<OutboundOrderV2> salesInvoices = Collections.synchronizedList(new ArrayList<>());
        log.info("Outbound Process Start {} sales invoice", salesInvoice);

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            for (SalesInvoice salesInvoice1 : salesInvoice) {
//                OutboundHeaderV2 header = ou.getou();
//                List<SalesOrderLineV2> lineV2List = salesOrder.getSalesOrderLine();
                String companyCode = salesInvoice1.getCompanyCode();
                String plantId = salesInvoice1.getBranchCode();
                String newPickListNo = salesInvoice1.getPickListNumber();
                String orderType = "4L";

                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
                Warehouse WH = dbWarehouse.get();
                String warehouseId = WH.getWarehouseId();
                String languageId = WH.getLanguageId();
                log.info("Warehouse ID: {}", warehouseId);

                PickListCancellation createPickListCancellation = null;
                String preOutboundNo = getPreOutboundNo(warehouseId, companyCode, plantId, languageId);

                // Description_Set
                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
                String companyText = description.getCompanyDesc();
                String plantText = description.getPlantDesc();
                String warehouseText = description.getWarehouseDesc();

                //PickList Cancellation
                log.info("Executing PickList cancellation scenario pre - checkup process");
                String salesOrderNumber = salesInvoice1.getSalesOrderNumber();

                //Check WMS order table
                List<OutboundHeaderV2> outbound = outboundHeaderV2Repository.findBySalesOrderNumberAndOutboundOrderTypeIdAndDeletionIndicator(salesOrderNumber, 3L, 0L);
                log.info("SalesOrderNumber already Exist: ---> PickList Cancellation to be executed " + salesOrderNumber);

                if (outbound != null && !outbound.isEmpty()) {
                    List<OutboundHeaderV2> oldPickListNo = outbound.stream().filter(n -> !n.getPickListNumber().equalsIgnoreCase(newPickListNo)).collect(Collectors.toList());
                    log.info("Old PickList Number, New PickList Number: " + oldPickListNo + ", " + newPickListNo);

                    if (oldPickListNo != null && !oldPickListNo.isEmpty()) {
                        for (OutboundHeaderV2 oldPickListNumber : oldPickListNo) {
                            OutboundHeaderV2 outboundOrderV2 =
                                    outboundHeaderV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                                            companyCode, languageId, plantId, warehouseId, oldPickListNumber.getPickListNumber(), oldPickListNumber.getPreOutboundNo(), 0L);
                            log.info("Outbound Order status ---> Delivered for old Picklist Number: " + outboundOrderV2 + ", " + oldPickListNumber);

                            if (outboundOrderV2 != null && outboundOrderV2.getInvoiceNumber() != null) {
                                // Update error message for the new PicklistNo
                                throw new BadRequestException("Picklist cannot be cancelled as Sales order associated with picklist - Invoice has been raised");
                            }

                            log.info("Old PickList Number: " + oldPickListNumber.getPickListNumber() + ", " +
                                    oldPickListNumber.getPreOutboundNo() + " Cancellation Initiated and followed by New PickList " + newPickListNo + " creation started");

                            //Delete old PickListData
                            createPickListCancellation = orderService.pickListCancellationNew(companyCode, plantId, languageId, warehouseId, oldPickListNumber.getPickListNumber(), newPickListNo, oldPickListNumber.getPreOutboundNo(), "MW_AMS");
                        }
                    }
                }
            }
        } finally {

        }
        return null;
    }

    /**
     *
     * @param warehouseId
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @return
     */
    private String getPreOutboundNo(String warehouseId, String companyCodeId, String plantId, String languageId) {
        try {
            String nextRangeNumber = mastersService.getNextNumberRange(9L, warehouseId, companyCodeId, plantId, languageId);
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }
}
