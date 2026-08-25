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
//    @Column(name = "company_code")
    private String companyCode;

    @NotBlank(message = "BranchCode is mandatory")
//    @Column(name = "branch_code")
    private String branchCode;

    @NotBlank(message = "SKU is mandatory")
//    @Column(name = "sku")
    private String sku;

    @NotBlank(message = "SKU Description is mandatory")
//    @Column(name = "sku_description")
    private String skuDescription;

    @NotBlank(message = "UOM is mandatory")
//    @Column(name = "uom")
    private String uom;

//    @Column(name = "item_group_id")
    private Long itemGroupId;

//    @Column(name = "sub_item_group_id")
    private Long subItemGroupId;

    @NotBlank(message = "Manufacturer Code is mandatory")
//    @Column(name = "manufacturer_code")
    private String manufacturerCode;

    @NotBlank(message = "Manufacturer Name is mandatory")
//    @Column(name = "manufacturer_name")
    private String manufacturerName;

//    @Column(name = "brand")
    private String brand;

//    @Column(name = "supplier_part_number")
    private String supplierPartNumber;

    @NotBlank(message = "Created By is mandatory")
//    @Column(name = "created_by")
    private String createdBy;

    @NotBlank(message = "CreatedOn Date is mandatory")
//    @Column(name = "created_on")
    private String createdOn;

//    @Column(name = "manufacturer_full_name")
    private String manufacturerFullName;

//    @Column(name = "is_new")
    private String isNew;

//    @Column(name = "is_update")
    private String isUpdate;

//    @Column(name = "is_completed")
    private String isCompleted;

//    @Column(name = "updated_on")
    private Date updatedOn;

//    @Column(name = "remarks")
    private String remarks;

    //ProcessedStatusIdOrderByOrderReceivedOn

//    @Column(name = "processed_status_id")
    private Long processedStatusId = 0L;

//    @Column(name = "order_received_on")
    private Date orderReceivedOn;

//    @Column(name = "order_processed_on")
    private Date orderProcessedOn;

    //MiddleWare Fields
//    @Column(name = "middleware_id")
    private Long middlewareId;

//    @Column(name = "middleware_table")
    private String middlewareTable;
}