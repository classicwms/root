package com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
public class StockReceiptHeader {

    @Column(nullable = false)
    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    @Column(nullable = false)
    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "Receipt No is mandatory")
    private String receiptNo;

    private String isCompleted;
    private Date updatedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
    private List<StockReceiptLine> stockReceiptLines;
}
