package com.tekclover.wms.core.model.Connector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
public class SupplierInvoiceHeader {

    private Long supplierInvoiceHeaderId;

    private String companyCode;

    private String branchCode;

    private String supplierInvoiceNo;

    private String isCompleted;

    private Date updatedOn;

    private String isCancelled;

    private Long processedStatusId = 0L;

    private Date orderReceivedOn;

    private Date orderProcessedOn;


    private List<SupplierInvoiceLine> supplierInvoiceLines;
}
