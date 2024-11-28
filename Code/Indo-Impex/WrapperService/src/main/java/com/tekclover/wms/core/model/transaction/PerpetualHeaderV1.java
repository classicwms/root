package com.tekclover.wms.core.model.transaction;


import lombok.Data;

import java.util.Date;

@Data
public class PerpetualHeaderV1 {

    private String companyCode;

    private String cycleCountNo;

    private String branchCode;
    private String warehouseId;
    private String languageId;

    private String branchName;

    private Date cycleCountCreationDate;

    private Date updatedOn;
}
