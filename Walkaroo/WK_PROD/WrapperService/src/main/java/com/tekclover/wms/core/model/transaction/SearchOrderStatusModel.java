package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOrderStatusModel {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Date fromDeliveryDate;
    private Date toDeliveryDate;
    private List<String> customerCode;
    private List<String> orderNumber;
    private List<String> orderType;
    private List<Long> statusId;

    private String itemCode;
}
