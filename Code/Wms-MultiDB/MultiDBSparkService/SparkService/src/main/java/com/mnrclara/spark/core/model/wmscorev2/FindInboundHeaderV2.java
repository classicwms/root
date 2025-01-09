package com.mnrclara.spark.core.model.wmscorev2;

import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class FindInboundHeaderV2 {
    private List<String> warehouseId;
    private List<String> refDocNumber;
    private List<Long> inboundOrderTypeId;
    private List<String> containerNo;
    private List<Long> statusId;
    private Date startCreatedOn;
    private Date endCreatedOn;
    private Date startConfirmedOn;
    private Date endConfirmedOn;

    //v2 fields
    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
}
