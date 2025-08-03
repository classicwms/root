package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class PerpetualHeaderV1 {

    @Column(nullable = false)
    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "CycleCountNo is mandatory")
    private String cycleCountNo;

    @Column(nullable = false)
    @NotBlank(message = "BranchCode is mandatory")
    private String branchCode;

    private String branchName;

    private Date cycleCountCreationDate;

    //    @NotBlank(message = "Is-New is mandatory")
    private String isNew;

    private String isCancelled;
    private String isCompleted;
    private Date updatedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}
