package com.tekclover.wms.api.inbound.transaction.model.dto;

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

    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(100)")
    private String barcodeId;


    /*----------------Walkaroo changes------------------------------------------------------*/

    @Column(name = "MATERIAL_NO", columnDefinition = "nvarchar(50)")
    private String materialNo;

    @Column(name = "PRICE_SEGMENT", columnDefinition = "nvarchar(50)")
    private String priceSegment;

    @Column(name = "ARTICLE_NO", columnDefinition = "nvarchar(50)")
    private String articleNo;

    @Column(name = "GENDER", columnDefinition = "nvarchar(50)")
    private String gender;

    @Column(name = "COLOR", columnDefinition = "nvarchar(50)")
    private String color;

    @Column(name = "SIZE", columnDefinition = "nvarchar(50)")
    private String size;

    @Column(name = "NO_PAIRS", columnDefinition = "nvarchar(50)")
    private String noPairs;
}