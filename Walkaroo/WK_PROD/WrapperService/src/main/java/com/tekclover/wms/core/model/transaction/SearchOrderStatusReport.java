package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SearchOrderStatusReport {

    private String languageId;          // LANG_ID
    private String companyCodeId;       // C_ID
    private String plantId;             // PLANT_ID
    private String warehouseId;         // WH_ID

//    @NotNull
//    @NotEmpty
    private String fromDeliveryDate;

//    @NotNull
//    @NotEmpty
    private String toDeliveryDate;

    private List<String> customerCode;
    private List<String> orderNumber;
    private List<String> orderType;
    private List<Long> statusId;

    private String itemCode;
}
