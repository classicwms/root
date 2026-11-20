package com.tekclover.wms.core.model.Connector;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchSupplierInvoiceLine {

    private List<Long> supplierInvoiceLineId;

    private List<String> companyCode;
    private List<String> branchCode;
    private List<String> supplierInvoiceNo;
    private List<Long> lineNoForEachItem;
    private List<String> itemCode;
    private List<String> itemDescription;
    private List<String> containerNo;
    private List<String> supplierCode;
    private List<String> supplierPartNo;
    private List<String> manufacturerShortName;
    private List<String> manufacturerCode;
    private List<String> purchaseOrderNo;
    private Date fromInvoiceDate;
    private Date toInvoiceDate;
    private List<Double> invoiceQty;
    private List<String> unitOfMeasure;



}
