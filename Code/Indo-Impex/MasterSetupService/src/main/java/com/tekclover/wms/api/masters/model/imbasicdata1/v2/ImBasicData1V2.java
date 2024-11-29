package com.tekclover.wms.api.masters.model.imbasicdata1.v2;

import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

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

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "SIZE", columnDefinition = "nvarchar(50)")
    private String size;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;

    @Column(name = "ITM_TYP_TEXT", columnDefinition = "nvarchar(100)")
    private String itemTypeDescription;

    @Column(name = "ITM_GRP_TEXT", columnDefinition = "nvarchar(100)")
    private String itemGroupDescription;
}