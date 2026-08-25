package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SalesOrderHeaderV2 {

    @NotBlank(message = "SalesOrder Number is mandatory")
    private String salesOrderNumber;                        // REF_DOC_NO;

    @NotBlank(message = "StoreId is mandatory")
    private String storeID;                                // PARTNER_CODE;

    @NotBlank(message = "Store Name is mandatory")
    private String storeName;                                // PARTNER_NM;

    @NotBlank(message = "Required Delivery Date is mandatory")
    private String requiredDeliveryDate;                    //REQ_DEL_DATE

    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @NotBlank(message = "PickList Number is mandatory")
    private String pickListNumber;

    @NotBlank(message = "Status is mandatory")
    private String status;
}
