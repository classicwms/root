package com.tekclover.wms.api.transaction.model.warehouse.outbound.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SalesOrderHeaderV2 {

    @Column(nullable = false)
    @NotBlank(message = "SalesOrder Number is mandatory")
    private String salesOrderNumber;                        // REF_DOC_NO;

    @Column(nullable = false)
    @NotBlank(message = "StoreId is mandatory")
    private String storeID;                                // PARTNER_CODE;

    @Column(nullable = false)
    @NotBlank(message = "Store Name is mandatory")
    private String storeName;                                // PARTNER_NM;

    @Column(nullable = false)
    @NotBlank(message = "Required Delivery Date is mandatory")
    private String requiredDeliveryDate;                    //REQ_DEL_DATE

    @Column(nullable = false)
    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "PickList Number is mandatory")
    private String pickListNumber;

    @Column(nullable = false)
    @NotBlank(message = "Status is mandatory")
    private String status;

    private String wareHouseId;                                // WH_ID
    private String languageId;
    private String branchCode;
    private String tokenNumber;

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
