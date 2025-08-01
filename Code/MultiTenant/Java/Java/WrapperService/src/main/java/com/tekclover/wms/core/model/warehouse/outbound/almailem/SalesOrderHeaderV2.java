package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;

@Data
public class SalesOrderHeaderV2 {

//    @NotBlank(message = "SalesOrder Number is mandatory")
    private String salesOrderNumber;                        // REF_DOC_NO;

//    @NotBlank(message = "StoreId is mandatory")
    private String storeID;                                // PARTNER_CODE;

//    @NotBlank(message = "Store Name is mandatory")
    private String storeName;                                // PARTNER_NM;

    //    @Column(nullable = false)
//    @NotBlank(message = "Required Delivery Date is mandatory")
    private String requiredDeliveryDate;                    //REQ_DEL_DATE

    //    @Column(nullable = false)
//    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    //    @Column(nullable = false)
//    @NotBlank(message = "PickList Number is mandatory")
    private String pickListNumber;

    //    @Column(nullable = false)
//    @NotBlank(message = "Status is mandatory")
    private String status;
    private String tokenNumber;
    private String orderType;                                // REF_FIELD_1

    private String branchCode;
    private String warehouseId;
    private String languageId;
    private Integer imsSaleTypeCode;
    private String customerCode;

    @JsonIgnore
    private String id;

    @JsonIgnore
    private Date orderReceivedOn;

    @JsonIgnore
    private Long statusId;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
    private String customerId;
    private String customerName;
    private String loginUserId;
    private String address;
    private String invoice;
}