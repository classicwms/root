package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SalesOrderHeaderV2 {

    //    @NotBlank(message = "SalesOrder Number is mandatory")
    private String salesOrderNumber;                        // REF_DOC_NO;

//    @NotBlank(message = "StoreId is mandatory")
    private String storeID;                                // PARTNER_CODE;

    //    @NotBlank(message = "Store Name is mandatory")
    private String storeName;                                // PARTNER_NM;

//    @NotBlank(message = "Required Delivery Date is mandatory")
    private String requiredDeliveryDate;                    //REQ_DEL_DATE

//    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

//    @NotBlank(message = "PickList Number is mandatory")
    private String pickListNumber;

    //    @NotBlank(message = "Status is mandatory")
    private String status;
    private String tokenNumber;
    private String orderType;                                // REF_FIELD_1

    private String branchCode;
    private String warehouseId;
    private String languageId;

    private String customerId;
    private String customerName;
    private String loginUserId;
    private String address;
    private String invoice;
}