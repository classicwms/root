package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SOHeaderV2 {

    private String wareHouseId;                                      // WH_ID

    @Column(nullable = false)
    @NotBlank(message = "Transfer Order Number is mandatory")
    private String transferOrderNumber;                              // REF_DOC_NO

    @Column(nullable = false)
    @NotBlank(message = "StoreId is mandatory")
    private String storeID;                                          // PARTNER_CODE

    private String storeName;                                        // PARTNER_NM

    @Column(nullable = false)
    @NotBlank(message = "Required Delivery Date is mandatory")
    private String requiredDeliveryDate;                             //REQ_DEL_DATE

    @Column(nullable = false)
    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "BranchCode is mandatory")
    private String branchCode;

    private String languageId;

    private String targetCompanyCode;

    private String targetBranchCode;

    @JsonIgnore
    private String id;

    @JsonIgnore
    private Date orderReceivedOn;

    @JsonIgnore
    private Long statusId;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}