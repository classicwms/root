package com.tekclover.wms.api.transaction.model.warehouse.inbound.v2;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SOReturnHeaderV2 {

    @Column(nullable = false)
    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    @Column(nullable = false)
    @NotBlank(message = "Transfer Order Number is mandatory")
    private String transferOrderNumber;

    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}
