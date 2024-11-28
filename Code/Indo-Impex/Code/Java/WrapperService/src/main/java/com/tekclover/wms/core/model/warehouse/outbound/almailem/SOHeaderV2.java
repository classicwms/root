package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class SOHeaderV2 {

    @NotBlank(message = "Transfer Order Number is mandatory")
    private String transferOrderNumber;                              // REF_DOC_NO

    @NotBlank(message = "StoreId is mandatory")
    private String storeID;                                          // PARTNER_CODE

    private String storeName;                                        // PARTNER_NM

    @NotBlank(message = "Required Delivery Date is mandatory")
    private String requiredDeliveryDate;                             //REQ_DEL_DATE

    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @NotBlank(message = "BranchCode is mandatory")
    private String branchCode;

    private String warehouseId;
    private String languageId;
}
