package com.mnrclara.spark.core.model.wmscorev2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOrderStatusReport {
    private List<String> languageId;          // LANG_ID
    private List<String> companyCodeId;       // C_ID
    private List<String> plantId;             // PLANT_ID
    private List<String> warehouseId;         // WH_ID
    private Date fromDeliveryDate;      // DLV_CNF_ON
    private Date toDeliveryDate;        // DLV_CNF_ON
    private List<String> partnerCode;   // PARTNER_CODE
    private List<String> refDocNumber;  // REF_DOC_NO
    private List<String> orderType;     // REF_FIELD_1
    private List<Long> statusId;        // STATUS_ID

    private List<String> customerCode;
    private List<String> orderNumber;

    private List<String> itemCode;


}
