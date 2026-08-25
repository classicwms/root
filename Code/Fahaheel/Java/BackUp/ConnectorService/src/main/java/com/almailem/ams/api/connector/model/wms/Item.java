package com.almailem.ams.api.connector.model.wms;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class Item {

    @Column(nullable = false)
    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @Column(nullable = false)
    @NotBlank(message = "BranchCode is mandatory")
    private String branchCode;

    @Column(nullable = false)
    @NotBlank(message = "SKU is mandatory")
    private String sku;

    @Column(nullable = false)
    @NotBlank(message = "SKU Description is mandatory")
    private String skuDescription;

    @Column(nullable = false)
    @NotBlank(message = "UOM is mandatory")
    private String uom;

    private Long itemGroupId;

    private Long subItemGroupId;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @Column(nullable = false)
    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private String manufacturerFullName;

    private String brand;

    private String supplierPartNumber;

    @Column(nullable = false)
    @NotBlank(message = "Created By is mandatory")
    private String createdBy;

    @Column(nullable = false)
    @NotBlank(message = "CreatedOn Date is mandatory")
    private String createdOn;

    private String isNew;
    private String isUpdate;
    private String isCompleted;
    private Date updatedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}