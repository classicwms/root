package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class InterWarehouseTransferInHeader {

    @Column(nullable = false)
    @NotBlank(message = "To CompanyCode is mandatory")
    private String toCompanyCode;

    @Column(nullable = false)
    @NotBlank(message = "To Branch Code is mandatory")
    private String toBranchCode;

    @Column(nullable = false)
    @NotBlank(message = "Transfer Order Number is mandatory")
    private String transferOrderNumber;

    private String sourceCompanyCode;
    private String sourceBranchCode;

    private Date transferOrderDate;
    private String isCompleted;
    private Date updatedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}
