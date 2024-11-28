package com.tekclover.wms.api.masters.model.masters;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Entity
@Table(name = "tblorderitemmaster")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long item_id;

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

    private String manufacturerFullName;
    private String isNew;
    private String isUpdate;
    private String isCompleted;
    private Date updatedOn;
    private String remarks;

    //ProcessedStatusIdOrderByOrderReceivedOn

    private Long processedStatusId = 0L;
    private Date orderReceivedOn;
    private Date orderProcessedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}