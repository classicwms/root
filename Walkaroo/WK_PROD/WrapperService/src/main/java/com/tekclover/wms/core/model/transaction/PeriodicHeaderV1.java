package com.tekclover.wms.core.model.transaction;


import lombok.Data;

import java.util.Date;

@Data
public class PeriodicHeaderV1 {

    private String companyCode;

    private String cycleCountNo;

    private String branchCode;

    private String branchName;
    private String warehouseId;
    private String languageId;
    private Date cycleCountCreationDate;

    private Date updatedOn;

}
