package com.mnrclara.spark.core.model.wmscorev2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindContainerReceiptV2 {

    private List<String> warehouseId;
    private List<String> containerReceiptNo;
    private List<String> containerNo;
    private List<String> partnerCode;
    private List<Long> statusId;

    private Date startContainerReceivedDate;
    private Date endContainerReceivedDate;

    // CNT_UL_BY
    private List<String> unloadedBy;


    private Date fromCreatedOn;
    private Date toCreatedOn;
//New added
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> refDocNumber;
}
