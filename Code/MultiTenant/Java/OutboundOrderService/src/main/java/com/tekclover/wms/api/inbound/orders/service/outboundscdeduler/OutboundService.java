package com.tekclover.wms.api.inbound.orders.service.outboundscdeduler;


import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.service.BaseService;
import com.tekclover.wms.api.inbound.orders.service.InventoryService;
import com.tekclover.wms.api.inbound.orders.service.SalesOrderService;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OutboundService extends BaseService {

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    SalesOrderService salesOrderService;
    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;
    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    String statusDescription = null;

    /**
     * @param outboundIntegrationHeader
     * @param warehouseId
     * @return
     * @throws Exception
     */
    public OutboundHeaderV2 updateOutboundHeaderForSalesInvoiceV2(OutboundIntegrationHeaderV2 outboundIntegrationHeader, String warehouseId) throws Exception {

        try {
            OutboundHeaderV2 dbOutboundHeader = getOutboundHeaderForSalesInvoiceUpdateV2(
                    outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), "EN",
                    warehouseId, outboundIntegrationHeader.getPickListNumber());
            log.info("OutboundHeader: " + dbOutboundHeader);

            outboundHeaderV2Repository.updateSalesInvoiceOutboundHeaderV2(
                    outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), "EN", warehouseId,
                    outboundIntegrationHeader.getPickListNumber(), outboundIntegrationHeader.getSalesOrderNumber(),
                    outboundIntegrationHeader.getSalesInvoiceNumber(), outboundIntegrationHeader.getRequiredDeliveryDate(),
                    outboundIntegrationHeader.getDeliveryType(), outboundIntegrationHeader.getCustomerId(),
                    outboundIntegrationHeader.getCustomerName(), outboundIntegrationHeader.getAddress(),
                    outboundIntegrationHeader.getPhoneNumber(), outboundIntegrationHeader.getAlternateNo(),
                    outboundIntegrationHeader.getStatus(), new Date());

            outboundHeaderV2Repository.updateSalesInvoiceOutboundLineV2(
                    outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), "EN", warehouseId,
                    outboundIntegrationHeader.getPickListNumber(), outboundIntegrationHeader.getSalesOrderNumber(),
                    outboundIntegrationHeader.getSalesInvoiceNumber(), outboundIntegrationHeader.getRequiredDeliveryDate(),
                    outboundIntegrationHeader.getDeliveryType(), outboundIntegrationHeader.getCustomerId(),
                    outboundIntegrationHeader.getCustomerName(), outboundIntegrationHeader.getAddress(),
                    outboundIntegrationHeader.getPhoneNumber(), outboundIntegrationHeader.getAlternateNo(),
                    outboundIntegrationHeader.getStatus(), new Date());

            return dbOutboundHeader;
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
     * @param pickListNumber
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderForSalesInvoiceUpdateV2(String companyCodeId, String plantId, String languageId,
                                                                     String warehouseId, String pickListNumber) {
        OutboundHeaderV2 outboundHeader =
                outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPickListNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, pickListNumber, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        } else {
            return null;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param binClassId
     * @param orderManagementLine
     * @param warehouseId
     * @param itemCode
     * @param ORD_QTY
     * @return
     * @throws ParseException
     */
    public OrderManagementLineV2 createOrderManagementV2(String companyCodeId, String plantId, String languageId,
                                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
                                                         String warehouseId, String itemCode, Double ORD_QTY) throws ParseException {
        List<IInventoryImpl> stockType1InventoryList = inventoryService.
                getInventoryForOrderManagementV2(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
        log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
        if (stockType1InventoryList.isEmpty()) {
            return createEMPTYOrderManagementLineV2(orderManagementLine);
        }
        return salesOrderService.updateAllocationV2(orderManagementLine, binClassId, ORD_QTY, warehouseId, itemCode, "ORDER_PLACED");
    }


    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLineV2 createEMPTYOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {

        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);

        IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
                orderManagementLine.getLanguageId(),
                orderManagementLine.getPlantId(),
                orderManagementLine.getWarehouseId());

        orderManagementLine.setCompanyDescription(description.getCompanyDesc());
        orderManagementLine.setPlantDescription(description.getPlantDesc());
        orderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

}
