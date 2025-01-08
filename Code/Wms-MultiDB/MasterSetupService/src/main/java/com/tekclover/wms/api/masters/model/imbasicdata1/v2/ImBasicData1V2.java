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

    @Column(name = "manufacturer_name")
    private String manufacturerName;
    @Column(name = "manufacturer_full_name")
    private String manufacturerFullName;
    @Column(name = "manufacturer_code")
    private String manufacturerCode;
    @Column(name = "brand")
    private String brand;
    @Column(name = "supplier_part_number")
    private String supplierPartNumber;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "is_new")
    private String isNew;
    @Column(name = "is_update")
    private String isUpdate;
    @Column(name = "is_completed")
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
    @Column(name = "middleware_id")
    private Long middlewareId;
    @Column(name = "middleware_table")
    private String middlewareTable;

    @Column(name = "ITM_TYP_TEXT", columnDefinition = "nvarchar(100)")
    private String itemTypeDescription;

    @Column(name = "ITM_GRP_TEXT", columnDefinition = "nvarchar(100)")
    private String itemGroupDescription;
}