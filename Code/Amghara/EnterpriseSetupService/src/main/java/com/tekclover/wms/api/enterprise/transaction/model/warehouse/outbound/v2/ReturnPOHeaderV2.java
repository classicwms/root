package com.tekclover.wms.api.enterprise.transaction.model.warehouse.outbound.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class ReturnPOHeaderV2 {

    @Column(nullable = false)
    @NotBlank(message = "PO Number is mandatory")
    private String poNumber;                            // REF_DOC_NO;

    @Column(nullable = false)
    @NotBlank(message = "StoreId is mandatory")
    private String storeID;                            // PARTNER_CODE;

    private String storeName;                            // PARTNER_NM;

    @Column(nullable = false)
    @NotBlank(message = "Required Delivery Date is mandatory")
    private String requiredDeliveryDate;                //REQ_DEL_DATE

    @Column(nullable = false)
    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    private String wareHouseId;                            // WH_ID
    private String branchCode;
    private String languageId;

    private String isCompleted;
    private String isCancelled;
    private Date updatedOn;

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