package com.tekclover.wms.api.transaction.model.threepl.dashboard;

import lombok.Data;

import java.util.Date;

@Data
public class FindDashBoard {

    private String partnerId;
    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private Date fromDate;
    private Date toDate;


}
