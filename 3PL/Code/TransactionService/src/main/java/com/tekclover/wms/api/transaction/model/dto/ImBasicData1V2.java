package com.tekclover.wms.api.transaction.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class ImBasicData1V2 extends ImBasicData1 {

    private String manufacturerName;
    private String manufacturerFullName;
    private String manufacturerCode;
    private String brand;
    private String supplierPartNumber;
    private String remarks;

    private String isNew;
    private String isUpdate;
    private String isCompleted;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;
}