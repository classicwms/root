package com.almailem.ams.api.connector.service;


import com.almailem.ams.api.connector.model.IntegrationLog.IntegrationLog;
import com.almailem.ams.api.connector.model.wms.*;
import com.almailem.ams.api.connector.repository.IntegrationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IntegrationLogService {

    @Autowired
    IntegrationLogRepository integrationLogRepository;


    //GET ALL
    public List<IntegrationLog> getAllIntegrationLog() {
        List<IntegrationLog> integrationLogList = integrationLogRepository.findAll();
        return integrationLogList;
    }

    //Asn
    public void createAsnLog(ASN asn, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(asn.getAsnHeader().getAsnNumber());
        integrationLog.setMiddlewareTable(asn.getAsnHeader().getMiddlewareTable());
        integrationLog.setMiddlewareId(asn.getAsnHeader().getMiddlewareId());
        integrationLog.setCompanyCode(asn.getAsnHeader().getCompanyCode());
        integrationLog.setBranchCode(asn.getAsnHeader().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //StockReceiptHeader
    public void createStockReceiptHeaderLog(StockReceiptHeader stockReceiptHeader, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(stockReceiptHeader.getReceiptNo());
        integrationLog.setMiddlewareId(stockReceiptHeader.getMiddlewareId());
        integrationLog.setMiddlewareTable(stockReceiptHeader.getMiddlewareTable());
        integrationLog.setCompanyCode(stockReceiptHeader.getCompanyCode());
        integrationLog.setBranchCode(stockReceiptHeader.getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //SalesOrderReturn LOG
    public void createSalesOrderReturnLog(SaleOrderReturn saleOrderReturn, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(saleOrderReturn.getSoReturnHeader().getTransferOrderNumber());
        integrationLog.setMiddlewareId(saleOrderReturn.getSoReturnHeader().getMiddlewareId());
        integrationLog.setMiddlewareTable(saleOrderReturn.getSoReturnHeader().getMiddlewareTable());
        integrationLog.setCompanyCode(saleOrderReturn.getSoReturnHeader().getCompanyCode());
        integrationLog.setBranchCode(saleOrderReturn.getSoReturnHeader().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);

    }

    //B2bTransferIn
    public void createB2bTransferLog(B2bTransferIn b2bTransferIn, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(b2bTransferIn.getB2bTransferInHeader().getTransferOrderNumber());
        integrationLog.setMiddlewareId(b2bTransferIn.getB2bTransferInHeader().getMiddlewareId());
        integrationLog.setMiddlewareTable(b2bTransferIn.getB2bTransferInHeader().getMiddlewareTable());
        integrationLog.setCompanyCode(b2bTransferIn.getB2bTransferInHeader().getCompanyCode());
        integrationLog.setBranchCode(b2bTransferIn.getB2bTransferInHeader().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //InterWarehouseTransferIn
    public void createInterWarehouseTransferInLog(InterWarehouseTransferIn interWarehouseTransferIn, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(interWarehouseTransferIn.getInterWarehouseTransferInHeader().getTransferOrderNumber());
        integrationLog.setMiddlewareTable(interWarehouseTransferIn.getInterWarehouseTransferInHeader().getMiddlewareTable());
        integrationLog.setMiddlewareId(interWarehouseTransferIn.getInterWarehouseTransferInHeader().getMiddlewareId());
        integrationLog.setCompanyCode(interWarehouseTransferIn.getInterWarehouseTransferInHeader().getSourceCompanyCode());
        integrationLog.setBranchCode(interWarehouseTransferIn.getInterWarehouseTransferInHeader().getToBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //ReturnPO
    public void createReturnPoLog(ReturnPO returnPO, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(returnPO.getReturnPOHeader().getPoNumber());
        integrationLog.setMiddlewareId(returnPO.getReturnPOHeader().getMiddlewareId());
        integrationLog.setMiddlewareTable(returnPO.getReturnPOHeader().getMiddlewareTable());
        integrationLog.setCompanyCode(returnPO.getReturnPOHeader().getCompanyCode());
        integrationLog.setBranchCode(returnPO.getReturnPOHeader().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //ShipmentOrder
    public void createShipmentOrderLog(ShipmentOrder shipmentOrder, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(shipmentOrder.getSoHeader().getTransferOrderNumber());
        integrationLog.setMiddlewareId(shipmentOrder.getSoHeader().getMiddlewareId());
        integrationLog.setMiddlewareTable(shipmentOrder.getSoHeader().getMiddlewareTable());
        integrationLog.setCompanyCode(shipmentOrder.getSoHeader().getCompanyCode());
        integrationLog.setBranchCode(shipmentOrder.getSoHeader().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //InterWarehouseTransferOut
    public void createInterWarehouseTransferOutLog(InterWarehouseTransferOut interWarehouseTransferOut, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getTransferOrderNumber());
        integrationLog.setMiddlewareId(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getMiddlewareId());
        integrationLog.setMiddlewareTable(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getMiddlewareTable());
        integrationLog.setCompanyCode(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getCompanyCode());
        integrationLog.setBranchCode(interWarehouseTransferOut.getInterWarehouseTransferOutHeader().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //SalesOrder
    public void createSalesOrderLog(SalesOrder salesOrder, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(salesOrder.getSalesOrderHeader().getSalesOrderNumber());
        integrationLog.setMiddlewareId(salesOrder.getSalesOrderHeader().getMiddlewareId());
        integrationLog.setMiddlewareTable(salesOrder.getSalesOrderHeader().getMiddlewareTable());
        integrationLog.setCompanyCode(salesOrder.getSalesOrderHeader().getCompanyCode());
        integrationLog.setBranchCode(salesOrder.getSalesOrderHeader().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //SalesInvoice
    public void createSalesInvoice(SalesInvoice salesInvoice, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(salesInvoice.getSalesInvoiceNumber());
        integrationLog.setMiddlewareTable(salesInvoice.getMiddlewareTable());
        integrationLog.setMiddlewareId(salesInvoice.getMiddlewareId());
        integrationLog.setCompanyCode(salesInvoice.getCompanyCode());
        integrationLog.setBranchCode(salesInvoice.getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //Perpetual
    public void createPerpetualLog(Perpetual perpetual, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(perpetual.getPerpetualHeaderV1().getCycleCountNo());
        integrationLog.setMiddlewareId(perpetual.getPerpetualHeaderV1().getMiddlewareId());
        integrationLog.setMiddlewareTable(perpetual.getPerpetualHeaderV1().getMiddlewareTable());
        integrationLog.setCompanyCode(perpetual.getPerpetualHeaderV1().getCompanyCode());
        integrationLog.setBranchCode(perpetual.getPerpetualHeaderV1().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //Periodic
    public void createPeriodicLog(Periodic periodic, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(periodic.getPeriodicHeaderV1().getCycleCountNo());
        integrationLog.setMiddlewareId(periodic.getPeriodicHeaderV1().getMiddlewareId());
        integrationLog.setMiddlewareTable(periodic.getPeriodicHeaderV1().getMiddlewareTable());
        integrationLog.setCompanyCode(periodic.getPeriodicHeaderV1().getCompanyCode());
        integrationLog.setBranchCode(periodic.getPeriodicHeaderV1().getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }

    //StockAdjustment
    public void createStockAdjustment(StockAdjustment stockAdjustment, String error) {

        IntegrationLog integrationLog = new IntegrationLog();
        integrationLog.setOrderTypeId(String.valueOf(stockAdjustment.getStockAdjustmentId()));
        integrationLog.setMiddlewareTable(stockAdjustment.getMiddlewareTable());
        integrationLog.setMiddlewareId(stockAdjustment.getMiddlewareId());
        integrationLog.setCompanyCode(stockAdjustment.getCompanyCode());
        integrationLog.setBranchCode(stockAdjustment.getBranchCode());
        integrationLog.setErrorMessage(error);
        integrationLog.setCreatedBy("MSD_INT");
        integrationLog.setOrderDate(new Date());
        integrationLog.setCreatedOn(new Date());
        integrationLogRepository.save(integrationLog);
    }


}
