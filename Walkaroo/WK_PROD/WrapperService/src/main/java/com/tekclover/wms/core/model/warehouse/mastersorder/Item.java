package com.tekclover.wms.core.model.warehouse.mastersorder;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Item {

    @NotBlank(message = "CompanyCode is mandatory")
    private String companyCode;

    @NotBlank(message = "BranchCode is mandatory")
    private String branchCode;

    @NotBlank(message = "SKU is mandatory")
    private String sku;

    @NotBlank(message = "SKU Description is mandatory")
    private String skuDescription;

    @NotBlank(message = "UOM is mandatory")
    private String uom;

    private Long itemGroupId;

    private Long subItemGroupId;

    @NotBlank(message = "Manufacturer Code is mandatory")
    private String manufacturerCode;

    @NotBlank(message = "Manufacturer Name is mandatory")
    private String manufacturerName;

    private String brand;

    private String supplierPartNumber;

    @NotBlank(message = "Created By is mandatory")
    private String createdBy;

    @NotBlank(message = "CreatedOn Date is mandatory")
    private String createdOn;
}
