package com.tekclover.wms.core.model.Connector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SupplierInvoiceLine {

    private Long supplierInvoiceLineId;

    private Long supplierInvoiceHeaderId;

    private String companyCode;

    private String branchCode;

    private String supplierInvoiceNo;

    private Long lineNoForEachItem;

    private String itemCode;

    private String itemDescription;

    private String containerNo;

    private String supplierCode;

    private String supplierPartNo;

    private String manufacturerShortName;

    private String manufacturerCode;

    private String purchaseOrderNo;

    private Date invoiceDate;

    private Double invoiceQty;

    private String unitOfMeasure;

    private String supplierName;

    private String manufacturerFullName;

    private Date receivedDate;

    private Double receivedQty;

    private String receivedBy;

    private String isCompleted;

    private String isCancelled;
}