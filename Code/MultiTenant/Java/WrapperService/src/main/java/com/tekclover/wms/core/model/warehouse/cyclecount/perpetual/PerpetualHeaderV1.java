package com.tekclover.wms.core.model.warehouse.cyclecount.perpetual;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class PerpetualHeaderV1 {

    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @NotBlank(message = "CycleCountNo is mandatory")
    private String cycleCountNo;

    @NotBlank(message = "BranchCode is mandatory")
    private String branchCode;

    private String branchName;

    //    @NotBlank(message = "Is-New is mandatory")
    private String isNew;
    private String isCompleted;
    private String isCancelled;
    private Date updatedOn;
}