package com.tekclover.wms.core.model.warehouse.inbound;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class ASNV9Header {
    @NotBlank(message = "Warehouse ID is mandatory")
    private String wareHouseId;

    @NotBlank(message = "ASN Number is mandatory")
    private String asnNumber;

//    @Column(nullable = false)
//    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    //    @Column(nullable = false)
//    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;
    private String languageId;
    private Long inboundOrderTypeId;
    private String customerId;
    private  String customerName;
}
