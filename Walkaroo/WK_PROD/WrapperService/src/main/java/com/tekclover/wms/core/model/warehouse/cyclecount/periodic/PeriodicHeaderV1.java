package com.tekclover.wms.core.model.warehouse.cyclecount.periodic;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class PeriodicHeaderV1 {

    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @NotBlank(message = "CycleCountNo is mandatory")
    private String cycleCountNo;

    @NotBlank(message = "Branch Code is mandatory")
    private String branchCode;

    private String branchName;

    //    @NotBlank(message = "IS-NEW is mandatory")
    private String isNew;

    private String isCancelled;
    private String isCompleted;
    private Date updatedOn;

}