package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class InterWarehouseTransferOutHeaderV2 {

    @NotBlank(message = "From Company Code is mandatory")
    private String fromCompanyCode;

    @NotBlank(message = "To Company Code is mandatory")
    private String toCompanyCode;

    @NotBlank(message = "From Branch Code is mandatory")
    private String fromBranchCode;

    @NotBlank(message = "To Branch Code is mandatory")
    private String toBranchCode;

    @NotBlank(message = "Transfer Order Number is mandatory")
    private String transferOrderNumber;

    @NotBlank(message = "Required Delivery Date is mandatory")
    private String requiredDeliveryDate;
}
