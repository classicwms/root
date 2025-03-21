package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SOReturnHeaderV2 {

    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    @NotBlank(message = "Transfer Order Number is mandatory")
    private String transferOrderNumber;
    private String asnNumber;

    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;
    private String warehouseId;
    private String languageId;
    private String loginUserId;
    private Long inboundOrderTypeId;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}