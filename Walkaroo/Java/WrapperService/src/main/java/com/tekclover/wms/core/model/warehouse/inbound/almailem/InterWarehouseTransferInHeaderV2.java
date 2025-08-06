package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class InterWarehouseTransferInHeaderV2 {

    @NotBlank(message = "To CompanyCode is mandatory")
    private String toCompanyCode;

    @NotBlank(message = "To Branch Code is mandatory")
    private String toBranchCode;

    @NotBlank(message = "Transfer Order Number is mandatory")
    private String transferOrderNumber;

    private String sourceCompanyCode;
    private String sourceBranchCode;

    private Date transferOrderDate;
    private String isCompleted;
    private Date updatedOn;
    private String warehouseId;
    private String languageId;
    private String loginUserId;
    private Long inboundOrderTypeId;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}
