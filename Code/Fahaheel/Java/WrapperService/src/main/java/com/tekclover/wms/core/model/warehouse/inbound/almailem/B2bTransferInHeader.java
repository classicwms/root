package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class B2bTransferInHeader {

    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    @NotBlank(message = "Transfer Order Number is mandatory")
    private String transferOrderNumber;

    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    private String sourceCompanyCode;
    private String sourceBranchCode;
    private Date transferOrderDate;
    private String isCompleted;
    private Date updatedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}
